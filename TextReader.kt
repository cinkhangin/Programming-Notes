object TextReader {
    private var textToSpeech: TextToSpeech? = null
    private var isReady : Boolean = false

    private var utteranceInProgress = MutableStateFlow(SpeechState.INITIALIZE)
    val utteranceInProgressFlow = utteranceInProgress.asStateFlow()

    fun read(text : String){
      textToSpeech?.speak(
          text ,
          TextToSpeech.QUEUE_FLUSH ,
          null ,
          "quote")
    }

    fun initialize(context: Context){
        textToSpeech?.let {
            return
        }


        val speechListener = TextToSpeech.OnInitListener {
           if(it == TextToSpeech.SUCCESS) {
               textToSpeech?.language = Locale.US
               textToSpeech?.setSpeechRate(1.1f)

               val utteranceListener = object : UtteranceProgressListener(){
                   override fun onStart(utteranceId: String?) {
                       utteranceInProgress.value = SpeechState.SPEAKING
                   }
                   override fun onDone(utteranceId: String?) {
                       utteranceInProgress.value = SpeechState.COMPLETE
                   }
                   @Deprecated("Deprecated in Java")
                   override fun onError(utteranceId: String?) {}
               }

               textToSpeech?.setOnUtteranceProgressListener(utteranceListener)
               isReady = true
           }
        }
        textToSpeech = TextToSpeech(context , speechListener)
    }

    fun check(context: Context){
        if(isReady) return

        initialize(context)
    }

    fun destroy(){
        textToSpeech?.shutdown()
        textToSpeech = null
        isReady = false
    }

    fun stop(){
        textToSpeech?.stop()
    }

    fun reset(){
        utteranceInProgress.value = SpeechState.INITIALIZE
    }
}
