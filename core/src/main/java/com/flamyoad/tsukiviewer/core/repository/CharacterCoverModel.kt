package com.flamyoad.tsukiviewer.core.repository

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtException
import ai.onnxruntime.OrtSession
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import java.io.Closeable
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

internal const val CHARACTER_INPUT_SIZE = 160
internal const val MAX_CHARACTER_DISTANCE = 0.16

internal class CharacterCoverModel private constructor(
    private val environment: OrtEnvironment,
    private val session: OrtSession
) : Closeable {
    private val thumbnail = Bitmap.createBitmap(CHARACTER_INPUT_SIZE, CHARACTER_INPUT_SIZE, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(thumbnail)
    private val bounds = Rect(0, 0, CHARACTER_INPUT_SIZE, CHARACTER_INPUT_SIZE)
    private val paint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val pixels = IntArray(CHARACTER_INPUT_SIZE * CHARACTER_INPUT_SIZE)
    private val input = ByteBuffer.allocateDirect(3 * pixels.size * Float.SIZE_BYTES)
        .order(ByteOrder.nativeOrder()).asFloatBuffer()

    fun fingerprint(bitmap: Bitmap): ByteArray? {
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, null, bounds, paint)
        thumbnail.getPixels(pixels, 0, CHARACTER_INPUT_SIZE, 0, 0, CHARACTER_INPUT_SIZE, CHARACTER_INPUT_SIZE)
        for (index in pixels.indices) {
            val pixel = pixels[index]
            input.put(index, ((pixel ushr 16 and 0xff) / 255f - 0.48145466f) / 0.26862954f)
            input.put(index + pixels.size, ((pixel ushr 8 and 0xff) / 255f - 0.4578275f) / 0.26130258f)
            input.put(index + 2 * pixels.size, ((pixel and 0xff) / 255f - 0.40821073f) / 0.27577711f)
        }
        return try {
            OnnxTensor.createTensor(environment, input, INPUT_SHAPE).use { tensor ->
                session.run(mapOf("input" to tensor)).use { result ->
                    val output = (result[0] as OnnxTensor).floatBuffer
                    val values = FloatArray(output.remaining()).also { output.get(it) }
                    quantizeCharacterFingerprint(values)
                }
            }
        } catch (error: OrtException) {
            Log.w(TAG, "Could not extract a character fingerprint", error)
            null
        }
    }

    override fun close() {
        session.close()
        thumbnail.recycle()
    }

    companion object {
        private const val TAG = "CharacterCoverModel"
        private const val MODEL_ASSET = "ccip-caformer-160-int4.onnx"
        private val INPUT_SHAPE = longArrayOf(1, 3, CHARACTER_INPUT_SIZE.toLong(), CHARACTER_INPUT_SIZE.toLong())
        fun open(assets: AssetManager): CharacterCoverModel? =
            try {
                val descriptor = assets.openFd(MODEL_ASSET)
                val model = descriptor.createInputStream().use { stream ->
                    stream.channel.map(FileChannel.MapMode.READ_ONLY, descriptor.startOffset, descriptor.length)
                }
                val environment = OrtEnvironment.getEnvironment()
                OrtSession.SessionOptions().use { options ->
                    options.setIntraOpNumThreads(1)
                    options.setInterOpNumThreads(1)
                    CharacterCoverModel(environment, environment.createSession(model, options))
                }
            } catch (error: IOException) {
                Log.w(TAG, "Could not read the character model", error)
                null
            } catch (error: OrtException) {
                Log.w(TAG, "Could not start the character model", error)
                null
            } catch (error: LinkageError) {
                Log.w(TAG, "Character inference is unavailable on this device", error)
                null
            }
    }
}

internal fun quantizeCharacterFingerprint(values: FloatArray): ByteArray? {
    if (values.size != 768 || values.any { !it.isFinite() }) return null
    val peak = values.maxOf { abs(it) }
    if (peak == 0f) return null
    return ByteArray(values.size) { (values[it] / peak * 127).roundToInt().toByte() }
}

// CCIP's metric is half the cosine distance. Renormalizing also removes the byte scale.
internal fun characterDistance(a: ByteArray, b: ByteArray): Double? {
    if (a.size != 768 || b.size != a.size) return null
    var dot = 0
    var squaredA = 0
    var squaredB = 0
    for (index in a.indices) {
        val x = a[index].toInt()
        val y = b[index].toInt()
        dot += x * y
        squaredA += x * x
        squaredB += y * y
    }
    if (squaredA == 0 || squaredB == 0) return null
    val cosine = dot / sqrt(squaredA.toDouble() * squaredB)
    return ((1 - cosine) / 2).coerceIn(0.0, 1.0)
}
