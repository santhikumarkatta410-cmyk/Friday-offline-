package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.model.AppLanguage
import com.example.model.SpeechEngineType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class VoiceManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val onSpeechRecognized: (String) -> Unit,
    private val onError: (String) -> Unit = {}
) : RecognitionListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var speechRecognizer: SpeechRecognizer? = null

    // Speech Engine Selection
    private val _engineType = MutableStateFlow(SpeechEngineType.VOSK_OFFLINE)
    val engineType: StateFlow<SpeechEngineType> = _engineType.asStateFlow()

    val voskEngine: VoskSpeechEngine by lazy {
        VoskSpeechEngine(
            context = context,
            coroutineScope = coroutineScope,
            onResultText = { recognizedText ->
                _isListening.value = false
                stopWaveAnimation()
                _partialText.value = recognizedText
                onSpeechRecognized(recognizedText)
            },
            onPartialText = { partial ->
                _partialText.value = partial
            },
            onAmplitudeChanged = { amp ->
                _amplitude.value = amp
            },
            onErrorOccurred = { err ->
                _isListening.value = false
                stopWaveAnimation()
                onError("Vosk Engine: $err")
            }
        )
    }

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _amplitude = MutableStateFlow(0.15f)
    val amplitude: StateFlow<Float> = _amplitude.asStateFlow()

    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText.asStateFlow()

    private var waveSimJob: Job? = null

    init {
        initTts()
    }

    fun setEngineType(type: SpeechEngineType) {
        if (_isListening.value) {
            stopListening()
        }
        _engineType.value = type
    }

    private fun initTts() {
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    isTtsReady = true
                    tts?.language = Locale.US
                    tts?.setPitch(1.05f) // crisp futuristic assistant tone
                    tts?.setSpeechRate(1.0f)
                    setupUtteranceListener()
                }
            }
        } catch (e: Exception) {
            isTtsReady = false
        }
    }

    private fun setupUtteranceListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
                startWaveAnimation(isSpeaking = true)
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
                stopWaveAnimation()
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
                stopWaveAnimation()
            }
        })
    }

    fun speak(text: String, language: AppLanguage) {
        if (!isTtsReady || tts == null) {
            // Fallback simulation if TTS engine is pending on device
            simulateSpeaking(text)
            return
        }

        try {
            val locale = when (language) {
                AppLanguage.ENGLISH -> Locale.US
                AppLanguage.TELUGU -> Locale("te", "IN")
                AppLanguage.HINDI -> Locale("hi", "IN")
                AppLanguage.SPANISH -> Locale("es", "ES")
            }
            tts?.language = locale
            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "FRIDAY_MSG_${System.currentTimeMillis()}")
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "FRIDAY_SPEECH")
        } catch (e: Exception) {
            simulateSpeaking(text)
        }
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
        stopWaveAnimation()
    }

    fun startListening(language: AppLanguage) {
        if (_isListening.value) return
        stopSpeaking()

        if (_engineType.value == SpeechEngineType.VOSK_OFFLINE) {
            // Try starting Vosk offline engine
            if (voskEngine.modelStatus.value == VoskModelStatus.READY) {
                val started = voskEngine.startListening()
                if (started) {
                    _isListening.value = true
                    _partialText.value = "Vosk Offline Listening..."
                    startWaveAnimation(isSpeaking = false)
                    return
                }
            }
            // If Vosk model is not ready or failed, fallback to native Android offline recognizer
        }

        // System On-Device Recognizer with EXTRA_PREFER_OFFLINE
        coroutineScope.launch(Dispatchers.Main) {
            try {
                if (speechRecognizer == null) {
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                        setRecognitionListener(this@VoiceManager)
                    }
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra("android.speech.extra.PREFER_OFFLINE", true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

                    val localeStr = when (language) {
                        AppLanguage.ENGLISH -> "en-US"
                        AppLanguage.TELUGU -> "te-IN"
                        AppLanguage.HINDI -> "hi-IN"
                        AppLanguage.SPANISH -> "es-ES"
                    }
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeStr)
                }

                _isListening.value = true
                _partialText.value = "Listening offline..."
                startWaveAnimation(isSpeaking = false)
                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                _isListening.value = false
                stopWaveAnimation()
                onError("Voice recognizer initialization failed: ${e.message}")
            }
        }
    }

    fun stopListening() {
        coroutineScope.launch(Dispatchers.Main) {
            try {
                if (_engineType.value == SpeechEngineType.VOSK_OFFLINE) {
                    voskEngine.stopListening()
                }
                speechRecognizer?.stopListening()
            } catch (_: Exception) {
            } finally {
                _isListening.value = false
                stopWaveAnimation()
            }
        }
    }

    fun simulateVoiceInput(text: String) {
        _isListening.value = false
        stopWaveAnimation()
        _partialText.value = text
        onSpeechRecognized(text)
    }

    private fun startWaveAnimation(isSpeaking: Boolean) {
        waveSimJob?.cancel()
        waveSimJob = coroutineScope.launch {
            while (_isListening.value || _isSpeaking.value) {
                val base = if (isSpeaking) 0.5f else 0.4f
                val delta = (Math.random().toFloat() * 0.5f)
                _amplitude.value = (base + delta).coerceIn(0.1f, 1.0f)
                delay(90)
            }
            _amplitude.value = 0.15f
        }
    }

    private fun stopWaveAnimation() {
        waveSimJob?.cancel()
        _amplitude.value = 0.15f
    }

    private fun simulateSpeaking(text: String) {
        _isSpeaking.value = true
        startWaveAnimation(isSpeaking = true)
        coroutineScope.launch {
            val estimatedDurationMs = (text.length * 50L).coerceIn(1200L, 4000L)
            delay(estimatedDurationMs)
            _isSpeaking.value = false
            stopWaveAnimation()
        }
    }

    fun cleanup() {
        stopSpeaking()
        stopListening()
        voskEngine.release()
        speechRecognizer?.destroy()
        speechRecognizer = null
        tts?.shutdown()
        tts = null
    }

    // Android SpeechRecognizer Callbacks
    override fun onReadyForSpeech(params: Bundle?) {
        _partialText.value = "Listening..."
    }

    override fun onBeginningOfSpeech() {
        _partialText.value = "Hearing voice..."
    }

    override fun onRmsChanged(rmsdB: Float) {
        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.1f, 1.0f)
        _amplitude.value = normalized
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        _isListening.value = false
    }

    override fun onError(error: Int) {
        _isListening.value = false
        stopWaveAnimation()
        _partialText.value = ""
    }

    override fun onResults(results: Bundle?) {
        _isListening.value = false
        stopWaveAnimation()
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            val text = matches[0]
            _partialText.value = text
            onSpeechRecognized(text)
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            _partialText.value = matches[0]
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}
}
