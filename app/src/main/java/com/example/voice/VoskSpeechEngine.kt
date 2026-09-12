package com.example.voice

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import java.io.File
import java.io.IOException

enum class VoskModelStatus {
    UNINITIALIZED,
    LOADING,
    READY,
    NO_MODEL_FOUND,
    ERROR
}

class VoskSpeechEngine(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val onResultText: (String) -> Unit,
    private val onPartialText: (String) -> Unit,
    private val onAmplitudeChanged: (Float) -> Unit,
    private val onErrorOccurred: (String) -> Unit
) : RecognitionListener {

    private val TAG = "VoskSpeechEngine"

    private var model: Model? = null
    private var speechService: SpeechService? = null

    private val _modelStatus = MutableStateFlow(VoskModelStatus.UNINITIALIZED)
    val modelStatus: StateFlow<VoskModelStatus> = _modelStatus.asStateFlow()

    private val _statusMessage = MutableStateFlow("Vosk ASR v0.3.47 initialized")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _currentModelPath = MutableStateFlow<String>("")
    val currentModelPath: StateFlow<String> = _currentModelPath.asStateFlow()

    init {
        detectAndLoadModel()
    }

    /**
     * Inspect local storage locations for a Vosk acoustic model directory.
     * Looks in app internal filesDir, externalFilesDir, and cache.
     */
    fun detectAndLoadModel() {
        coroutineScope.launch(Dispatchers.IO) {
            _modelStatus.value = VoskModelStatus.LOADING
            _statusMessage.value = "Checking local offline model files..."

            val possibleDirs = listOf(
                File(context.filesDir, "vosk-model"),
                File(context.filesDir, "model"),
                File(context.getExternalFilesDir(null), "vosk-model"),
                File(context.getExternalFilesDir(null), "model")
            )

            val existingDir = possibleDirs.firstOrNull { it.exists() && it.isDirectory && (File(it, "am").exists() || File(it, "conf").exists() || it.list()?.isNotEmpty() == true) }

            if (existingDir != null) {
                try {
                    _statusMessage.value = "Loading offline model from ${existingDir.name}..."
                    model = Model(existingDir.absolutePath)
                    _currentModelPath.value = existingDir.absolutePath
                    _modelStatus.value = VoskModelStatus.READY
                    _statusMessage.value = "Vosk Model Ready (Offline: ${existingDir.name})"
                    Log.d(TAG, "Vosk Model loaded successfully from: ${existingDir.absolutePath}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading Vosk model from dir", e)
                    _modelStatus.value = VoskModelStatus.ERROR
                    _statusMessage.value = "Model load failed: ${e.message}"
                }
            } else {
                _modelStatus.value = VoskModelStatus.NO_MODEL_FOUND
                _currentModelPath.value = possibleDirs.first().absolutePath
                _statusMessage.value = "No local model folder found in filesDir/vosk-model. Vosk engine is standing by; built-in offline engine active."
            }
        }
    }

    /**
     * Start continuous offline listening using Vosk SpeechService
     */
    fun startListening(): Boolean {
        if (_isListening.value) return true

        val currentModel = model
        if (currentModel == null) {
            _statusMessage.value = "Vosk model not loaded. Please ensure model files are placed in ${_currentModelPath.value} or use system offline engine."
            onErrorOccurred("Vosk model not loaded yet.")
            return false
        }

        return try {
            val recognizer = Recognizer(currentModel, 16000.0f)
            speechService = SpeechService(recognizer, 16000.0f).apply {
                startListening(this@VoskSpeechEngine)
            }
            _isListening.value = true
            _statusMessage.value = "Vosk listening offline on mic (16kHz PCM)..."
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start Vosk SpeechService", e)
            _isListening.value = false
            _statusMessage.value = "Vosk start error: ${e.message}"
            onErrorOccurred("Vosk listening failed: ${e.message}")
            false
        }
    }

    /**
     * Stop listening
     */
    fun stopListening() {
        try {
            speechService?.stop()
            speechService?.shutdown()
            speechService = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping Vosk", e)
        } finally {
            _isListening.value = false
        }
    }

    /**
     * Direct test recognition simulation for verification
     */
    fun simulateVoskRecognition(phrase: String) {
        onPartialText("Vosk: $phrase")
        onAmplitudeChanged(0.8f)
        onResultText(phrase)
    }

    fun release() {
        stopListening()
        model?.close()
        model = null
    }

    // --- Vosk RecognitionListener callbacks ---

    override fun onPartialResult(hypothesis: String?) {
        if (hypothesis.isNullOrBlank()) return
        try {
            val json = JSONObject(hypothesis)
            val partial = json.optString("partial", "").trim()
            if (partial.isNotBlank()) {
                onPartialText(partial)
                onAmplitudeChanged(0.7f)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing partial Vosk result: $hypothesis", e)
        }
    }

    override fun onResult(hypothesis: String?) {
        if (hypothesis.isNullOrBlank()) return
        try {
            val json = JSONObject(hypothesis)
            val text = json.optString("text", "").trim()
            if (text.isNotBlank()) {
                Log.d(TAG, "Vosk onResult: $text")
                onResultText(text)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Vosk onResult: $hypothesis", e)
        }
    }

    override fun onFinalResult(hypothesis: String?) {
        _isListening.value = false
        if (hypothesis.isNullOrBlank()) return
        try {
            val json = JSONObject(hypothesis)
            val text = json.optString("text", "").trim()
            if (text.isNotBlank()) {
                Log.d(TAG, "Vosk onFinalResult: $text")
                onResultText(text)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Vosk onFinalResult: $hypothesis", e)
        }
    }

    override fun onError(exception: java.lang.Exception?) {
        Log.e(TAG, "Vosk error: ${exception?.message}", exception)
        _isListening.value = false
        _statusMessage.value = "Vosk Error: ${exception?.message}"
        onErrorOccurred(exception?.message ?: "Vosk speech error")
    }

    override fun onTimeout() {
        Log.d(TAG, "Vosk onTimeout")
        _isListening.value = false
    }
}
