package com.flamyoad.tsukiviewer.ui.reader

import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder

// Bound Glide’s source-resolution byte buffer separately from its downsampled bitmaps.
private const val MAX_CANVAS_PIXELS = 2_500_000L
private const val MAX_FRAMES = 2_048

// Glide uses source-resolution scratch space for the canvas and individual frames.
internal fun canAnimateGif(data: ByteBuffer): Boolean {
    data.order(ByteOrder.LITTLE_ENDIAN)
    try {
        val signature = ByteArray(6).also { data.get(it) }.decodeToString()
        if (signature != "GIF87a" && signature != "GIF89a") return false
        val width = data.readUnsignedShort()
        val height = data.readUnsignedShort()
        if (width > 4096 || height > 4096 || width.toLong() * height !in 1L..MAX_CANVAS_PIXELS) return false

        val screenFlags = data.readUnsignedByte()
        data.skipBytes(2) // Background index and pixel aspect ratio.
        data.skipColorTable(screenFlags)
        var frames = 0
        while (data.hasRemaining()) {
            when (data.readUnsignedByte()) {
                0x21 -> {
                    data.get() // Extension label.
                    data.skipSubBlocks()
                }
                0x2c -> {
                    val left = data.readUnsignedShort()
                    val top = data.readUnsignedShort()
                    val frameWidth = data.readUnsignedShort()
                    val frameHeight = data.readUnsignedShort()
                    if (frameWidth == 0 || frameHeight == 0 ||
                        left + frameWidth > width || top + frameHeight > height
                    ) return false
                    if (++frames > MAX_FRAMES) return false

                    data.skipColorTable(data.readUnsignedByte())
                    if (data.readUnsignedByte() !in 2..8) return false // LZW minimum code size.
                    data.skipSubBlocks()
                }
                0x3b -> return frames >= 2
                else -> return false
            }
        }
    } catch (_: BufferUnderflowException) {
        return false
    }
    return false
}

private fun ByteBuffer.readUnsignedByte() = get().toInt() and 0xff
private fun ByteBuffer.readUnsignedShort() = short.toInt() and 0xffff

private fun ByteBuffer.skipBytes(count: Int) {
    if (count > remaining()) throw BufferUnderflowException()
    position(position() + count)
}

private fun ByteBuffer.skipColorTable(flags: Int) {
    if (flags and 0x80 != 0) skipBytes(3 * (2 shl (flags and 7)))
}

private fun ByteBuffer.skipSubBlocks() {
    var size = readUnsignedByte()
    while (size != 0) {
        skipBytes(size)
        size = readUnsignedByte()
    }
}
