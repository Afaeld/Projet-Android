package com.rakesh.voicerecognition;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.rakesh.voicerecognition.R;

public class VoiceRecognitionActivity extends Activity {

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;

	//private EditText metTextHint;
	//private ListView mlvTextMatches;
	private Spinner msTextMatches;
	private Button mbtSpeak;
	private PhoneStateListener listener=new PhoneStateListener();
	private ListView mlvTextMatches;
	private EditText metTextHint;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice_recognition);
		metTextHint = (EditText) findViewById(R.id.etTextHint);
		mlvTextMatches = (ListView) findViewById(R.id.lvTextMatches);
		msTextMatches = (Spinner) findViewById(R.id.sNoOfMatches);
		mbtSpeak = (Button) findViewById(R.id.btSpeak);
	}


	public void checkVoiceRecognition()
	{
		// Check if voice recognition is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			mbtSpeak.setEnabled(false);
			Toast.makeText(this, "Voice recognizer not present",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void speak(View view) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		//Identifie le package appelant pour spécifier l'application
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
				.getPackage().getName());


		// Donner une direction à la reconnaissance vocale pour que ce soit plus rapide
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);




		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)  //Vérification de l'activitée appelée

			//If Voice recognition is successful then it returns RESULT_OK
			if(resultCode == RESULT_OK) {

				ArrayList<String> textMatchList = data
				.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				if (!textMatchList.isEmpty()) {
					// Si le premier mot contient "Search"
					// Lance une recherche internet
					if (textMatchList.get(0).contains("search")) {

						String searchQuery = textMatchList.get(0).replace("search",
						" ");
						Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
						search.putExtra(SearchManager.QUERY, searchQuery);
						startActivity(search);	//Lancement de l'activité de recherche sur internet du terme
					}

				}
			//Affichage des différentes erreurs
			}else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
				showToastMessage("Audio Error");
			}else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
				showToastMessage("Client Error");
			}else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
				showToastMessage("Network Error");
			}else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
				showToastMessage("No Match");
			}else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
				showToastMessage("Server Error");
			}
		super.onActivityResult(requestCode, resultCode, data);
	}

	void showToastMessage(String message){
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

}
