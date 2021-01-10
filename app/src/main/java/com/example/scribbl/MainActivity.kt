package com.example.scribbl

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scribbl.AppValues.Companion.DEFAULT_BRUSH_WIDTH
import java.util.ArrayList


class MainActivity : AppCompatActivity(), View.OnClickListener, SeekBar.OnSeekBarChangeListener, DrawingViewEventListener,
    GameScreen.GameScreenActionListener {

    private var fragManager: FragmentManager? = null
    private lateinit var gameScreen: GameScreen
    private lateinit var iv_button_1: ImageView
    private lateinit var iv_button_2: ImageView
    private lateinit var iv_button_3: ImageView
    private lateinit var iv_button_4: ImageView

    private lateinit var sizeSlider: AppCompatSeekBar

    private var colorPalette:ColorPalette? = null
    private var IS_PLAYER_DRAWING = true

    private lateinit var bottomBar: View
    private lateinit var userInputLayout: View
    private lateinit var changeModeButton: View
    private lateinit var sttButton: View
    private lateinit var speechRecognizer : SpeechRecognizer

    private lateinit var textInputUser : EditText

    private var backToExitPressedOnce = false
    private final val RECORD_REQUEST_CODE = 100

    private lateinit var rvUserMessage : RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        linearLayoutManager = LinearLayoutManager(this)
        initUI()

        rvUserMessage.layoutManager = linearLayoutManager
        rvUserMessage.itemAnimator = DefaultItemAnimator()
        rvUserMessage.adapter = UserMessageAdapter(getUserMessages())

        fragManager = supportFragmentManager
        gameScreen = GameScreen(this, this)
        fragManager?.beginTransaction()?.add(R.id.container, gameScreen)?.addToBackStack("gameScreen")?.commit()
    }

    private fun getUserMessages(): ArrayList<UserMessageModel> {
        val list = ArrayList<UserMessageModel>()
        list.add(UserMessageModel(message = "castiel has guessed it right!!!"))
        for(i in 0..50){
            list.add(UserMessageModel("castiel", "burger"))
        }
        return list
    }

    private fun initUI() {
        bottomBar = findViewById(R.id.bottom_bar)
        rvUserMessage = findViewById(R.id.rv_user_message)
        userInputLayout = findViewById(R.id.user_input_layout)
        sttButton = findViewById(R.id.stt_button)
        sttButton.setOnClickListener(this)
        userInputLayout.visibility = View.GONE

        changeModeButton = findViewById(R.id.test_changeMode)
        textInputUser = findViewById(R.id.et_user_input)
        changeModeButton.setOnClickListener(this)

        iv_button_1 = findViewById(R.id.iv_bottom1)
        iv_button_2 = findViewById(R.id.iv_bottom2)
        iv_button_3 = findViewById(R.id.iv_bottom3)
        iv_button_4 = findViewById(R.id.iv_bottom4)

        iv_button_1.setOnClickListener(this)
        iv_button_2.setOnClickListener(this)
        iv_button_3.setOnClickListener(this)
        iv_button_4.setOnClickListener(this)

        sizeSlider = findViewById(R.id.size_slider);
        sizeSlider.max = 100
        sizeSlider.incrementProgressBy(5)

        sizeSlider.setOnSeekBarChangeListener(this)

    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.iv_bottom1 -> passMessageToCanvas(CanvasCommand.BRUSH)
            R.id.iv_bottom2 -> passMessageToCanvas(CanvasCommand.PALETTE)
            R.id.iv_bottom3 -> passMessageToCanvas(CanvasCommand.RESET)
            R.id.iv_bottom4 -> passMessageToCanvas(CanvasCommand.ERASER)
            R.id.test_changeMode -> changeMode(!IS_PLAYER_DRAWING)
            R.id.stt_button -> sttLaunch()
        }
    }

    private fun sttLaunch() {
        checkVoicePermissionAndRequest()
    }

    private fun createSpeechRecogniser() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_CALLING_PACKAGE,
            "com.example.scribbl"
        )

        speechRecognizer = SpeechRecognizer
            .createSpeechRecognizer(this.applicationContext)


        speechRecognizer.setRecognitionListener(listener);
        speechRecognizer.startListening(intent);
    }

    private fun checkVoicePermissionAndRequest() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.RECORD_AUDIO)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }else{
            createSpeechRecogniser()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORD_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == RECORD_REQUEST_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED){
                createSpeechRecogniser()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showBottomSheet() {
        colorPalette = ColorPalette(gameScreen)
        colorPalette?.show(
            supportFragmentManager,
            ColorPalette.TAG
        )
    }

    private fun passMessageToCanvas(command: CanvasCommand) {
        if(command == CanvasCommand.PALETTE){
            showBottomSheet()
        }else {
            gameScreen.passCommand(command)
        }
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        val currentValue = p0?.progress
        currentValue?.let {
            gameScreen.setSeekValue(it)
        }
    }

    override fun onEraserSelected(seekValue: Int) {
        setSliderProgress(seekValue)
    }

    override fun onBrushSelected(seekValue: Int) {
        setSliderProgress(seekValue)
    }

    override fun resetDrawing() {
        sizeSlider.progress = DEFAULT_BRUSH_WIDTH.toInt()
    }

    private fun setSliderProgress(value : Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sizeSlider.setProgress(value, true)
        }else{
            sizeSlider.progress = value
        }
    }

    override fun colorSelected(color: Int) {
        colorPalette?.dismiss()
    }

    fun changeMode(isPlayerDrawing: Boolean){
        IS_PLAYER_DRAWING = isPlayerDrawing
        if(IS_PLAYER_DRAWING){
            sizeSlider.visibility = View.VISIBLE
            bottomBar.visibility = View.VISIBLE
            userInputLayout.visibility = View.GONE
        }else{
            sizeSlider.visibility = View.GONE
            bottomBar.visibility = View.GONE
            userInputLayout.visibility = View.VISIBLE
        }
        if(IS_PLAYER_DRAWING)
            passMessageToCanvas(CanvasCommand.ENABLE_DRAWING)
        else
            passMessageToCanvas(CanvasCommand.DISABLE_DRAWING)
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 1){
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()
            if (backToExitPressedOnce) {
                finish()
            }
            this.backToExitPressedOnce = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler().postDelayed(Runnable { backToExitPressedOnce = false }, 2000)
        }else{
            super.onBackPressed()
        }
    }

    override fun onPause() {
        backToExitPressedOnce = false
        super.onPause()
    }

    var listener: RecognitionListener = object : RecognitionListener {
        override fun onResults(results: Bundle) {
            val voiceResults = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (voiceResults == null) {

            } else {
                for (match in voiceResults) {
                    setTextInUserInput(match)
                }
            }
        }

        override fun onReadyForSpeech(params: Bundle) {
            println("Ready for speech")
        }

        /**
         * ERROR_NETWORK_TIMEOUT = 1;
         * ERROR_NETWORK = 2;
         * ERROR_AUDIO = 3;
         * ERROR_SERVER = 4;
         * ERROR_CLIENT = 5;
         * ERROR_SPEECH_TIMEOUT = 6;
         * ERROR_NO_MATCH = 7;
         * ERROR_RECOGNIZER_BUSY = 8;
         * ERROR_INSUFFICIENT_PERMISSIONS = 9;
         *
         * @param error code is defined in SpeechRecognizer
         */
        override fun onError(error: Int) {
            System.err.println("Error listening for speech: $error")
        }

        override fun onBeginningOfSpeech() {
            println("Speech starting")
        }

        override fun onBufferReceived(buffer: ByteArray) {
            // TODO Auto-generated method stub
        }

        override fun onEndOfSpeech() {
            // TODO Auto-generated method stub
        }

        override fun onEvent(eventType: Int, params: Bundle) {
            // TODO Auto-generated method stub
        }

        override fun onPartialResults(partialResults: Bundle) {
            // TODO Auto-generated method stub
        }

        override fun onRmsChanged(rmsdB: Float) {
            // TODO Auto-generated method stub
        }
    }

    private fun setTextInUserInput(match: String?) {
        textInputUser.setText(match)
    }
}