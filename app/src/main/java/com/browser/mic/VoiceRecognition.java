package com.browser.mic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ozgur on 2/16/16 at 1:11 PM.
 */
public class VoiceRecognition implements RecognitionListener {

    private SpeechRecognizer speechRecognizer = null;
    private Intent recognizerIntent;
    private VoiceRecogListener voiceRecogListener;
    private Context context;

    public interface VoiceRecogListener {
        void onVoiceRecogError(int errNo);
        void onVoiceRecogStarted();
        void onVoiceRecogFinished(String txt);
        void onVoiceRecogPartial(String partial);
        void onVoiceRecogReady();
    }

    public VoiceRecognition(Context context, VoiceRecogListener voiceRecogListener) {
        init(context, voiceRecogListener);
    }

    private void init(Context context, VoiceRecogListener voiceRecogListener) {

        if(context == null) {
            return;
        }

        /**/
        this.voiceRecogListener = voiceRecogListener;
        this.context = context;

        /**/
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.asistan");//TODO
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log("onReadyForSpeech");
        voiceRecogListener.onVoiceRecogReady();
    }

    @Override
    public void onBeginningOfSpeech() {
        Log("onBeginningOfSpeech");
        voiceRecogListener.onVoiceRecogStarted();
    }

    @Override
    public void onRmsChanged(float v) {
        //Log("onRmsChanged");
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        Log("onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Log("onEndOfSpeech");
    }

    @Override
    public void onError(int i) {
        Log("onError errNo:" + i);
        voiceRecogListener.onVoiceRecogError(i);

        if(i == SpeechRecognizer.ERROR_CLIENT) {
            //init again?
            destroy();
            init(context, voiceRecogListener);
        }
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = null;

        if (matches != null)
            text = matches.get(0);

        if(!TextUtils.isEmpty(text)) {
            voiceRecogListener.onVoiceRecogFinished(text);
        }
    }

    private void Log(String e) {
        Log.d("VoiceRecog", e);
    }

    @Override
    public void onPartialResults(Bundle results) {

        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = null;

        if (matches != null)
            text = matches.get(0);

        if(!TextUtils.isEmpty(text)) {
            voiceRecogListener.onVoiceRecogPartial(text);
        }
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log("onPartialResults");
    }

    public void startListen() {

        if(speechRecognizer == null)
            return;

        //speechRecognizer.stopListening(); ???
        speechRecognizer.startListening(recognizerIntent);
    }

    public void destroy(){
        if(speechRecognizer != null){
            speechRecognizer.destroy();
        }
    }

    public void stop() {
        if(speechRecognizer != null){
            speechRecognizer.stopListening();
        }
    }

}
