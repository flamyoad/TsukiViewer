# Proguard rules for consumers of this library

# ONNX Runtime resolves Java classes and methods through JNI.
-keep class ai.onnxruntime.** { *; }
