package com.rakesh.voicerecognition;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
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

import com.cengalabs.flatui.FlatUI;
import com.rakesh.voicerecognition.R;

public class

VoiceRecognitionActivity extends Activity {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;

    //private EditText metTextHint;
    //private ListView mlvTextMatches;
    private Spinner msTextMatches;
    private Button mbtSpeak;
    private Button mbtReglage;
    private PhoneStateListener listener = new PhoneStateListener();
    private ListView mlvTextMatches;
    private EditText metTextHint;
    private List pkgAppsList;
    private List<ApplicationInfo> installedApps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_voice_recognition);
        metTextHint = (EditText) findViewById(R.id.etTextHint);
        mlvTextMatches = (ListView) findViewById(R.id.lvTextMatches);
        msTextMatches = (Spinner) findViewById(R.id.sNoOfMatches);
        mbtSpeak = (Button) findViewById(R.id.btSpeak);
        mbtReglage = (Button) findViewById(R.id.btnReglage);


        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        installedApps = new ArrayList<ApplicationInfo>();

        for (ApplicationInfo app : apps) {
            if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                installedApps.add(app);
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            }
            else {
                installedApps.add(app);
            }

            Log.d("info", (String) pm.getApplicationLabel(app));
        }
        pkgAppsList = installedApps;

        mbtReglage.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent nextScreen = new Intent(getApplicationContext(), settings.class);
                startActivity(nextScreen);
            }
        });


    }


    public void speak(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //Identifie le package appelant pour spécifier l'application
        //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
       //         .getPackage().getName());


        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)  //Vérification de l'activitée appelée

            //If Voice recognition is successful then it returns RESULT_OK
            if (resultCode == RESULT_OK) {
                ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                boolean found = false;
                Command comFound ;
                if (!pkgAppsList.isEmpty())
                    Log.d("info", Locale.getDefault().getDisplayLanguage().toString());

                for (int i = 0; i < textMatchList.toArray().length; i++) {
                    String[] commande = textMatchList.toArray()[i].toString().split(" ");
                    if(found)
                        break;
    /*                for (Command command : xpph.cmds) {
                        if(found)
                            break;
                        if (textMatchList.toArray()[i].toString().toLowerCase().compareTo(command.getFunction().toString().toLowerCase())==0) {
                            showToastMessage(command.getFunction().toString());
                            comFound=command;

                            break;
                        }
                    }*/
                }

                if (textMatchList.get(0).contains("search")) {

                    String searchQuery = textMatchList.get(0).replace("search",
                            " ");
                    Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                    search.putExtra(SearchManager.QUERY, searchQuery);
                    startActivity(search);    //Lancement de l'activité de recherche sur internet du terme
                }
//                Log.i("info",ResultCode.formInt(resultCode).toSring());
                //Affichage des différentes erreurs
            } else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                showToastMessage("Audio Error");
            } else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
                showToastMessage("Client Error");
            } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
                showToastMessage("Network Error");
            } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                showToastMessage("No Match");
            } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
                showToastMessage("Server Error");
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
