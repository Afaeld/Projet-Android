package com.rakesh.voicerecognition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
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
    private XmlPullParserHandler xpph;
    private PackageManager pm;
    static final int READ_BLOCK_SIZE = 100;
    static boolean newDico = false;
    static final String XmlFileName = "dico.xml";
    static final String Language =  Locale.getDefault().getDisplayLanguage();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_voice_recognition);
        metTextHint = (EditText) findViewById(R.id.etTextHint);
        mlvTextMatches = (ListView) findViewById(R.id.lvTextMatches);
        msTextMatches = (Spinner) findViewById(R.id.sNoOfMatches);
        mbtSpeak = (Button) findViewById(R.id.btSpeak);
        mbtReglage = (Button) findViewById(R.id.btnReglage);
        xpph = new XmlPullParserHandler(this.getApplicationContext(), XmlFileName);
        initXML();

        pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        installedApps = new ArrayList<ApplicationInfo>();

        for (ApplicationInfo app : apps) {
            if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                installedApps.add(app);
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            } else {
                installedApps.add(app);
            }

            Log.d("info", (String) pm.getApplicationLabel(app));
            Log.d("info", (String) app.packageName);
        }
        pkgAppsList = installedApps;
        for (int i =0; i< installedApps.size();i++){
            Log.d("info", (String) pm.getApplicationLabel(installedApps.get(i)));
        }
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
                Command comFound = null;//the command found
                String[] commande = null;//the list of subcommand
                String allwordsoncommand ="";
                String matchingword =null;//The word who match with the commande
                if (!pkgAppsList.isEmpty())
                    Log.d("info", Locale.getDefault().getDisplayLanguage().toString());

                for (int i = 0; i < textMatchList.toArray().length; i++) {
                    commande = textMatchList.toArray()[i].toString().split(" ");
                    if (found)
                        break;
                    for (Command command : xpph.cmds) {
                        if (found)
                            break;
                        for(String leword: command.getFR())
                        {
                            for(String cmd : commande) {
                                if(found)
                                    break;
                                if (cmd.toLowerCase().compareTo(leword.toLowerCase()) == 0) {
                                    //showToastMessage(command.getFunction().toString());
                                    matchingword = leword;
                                    comFound = command;
                                    found = true;
                                    break;
                                }else{
                                    allwordsoncommand+=cmd.toLowerCase();
                                }
                            }
                            //on regarde si la commande match si on a plusieurs mots
                            if(allwordsoncommand.replaceAll("\\s+","").contains(leword.toLowerCase().replaceAll("\\s+",""))){
                                matchingword = leword;
                                comFound = command;
                                found = true;
                                break;
                            }
                        }

                    }
                }
                if(found) {
                    String number;
                    String text;
                    switch (comFound.getFunction()) {
                        //Quand on veut lancer une application
                        case ("Ouverture"):
                            String app = ""; //Nom du package qui va etre lancé
                            String appnamefromcommande=""; //Nom de l'application récuperer par la reconnaissance vocale
                            String installapp="";   //Nom des applications installés
                            for(int i =0; i< commande.length ;i++){
                                if(i!=0)
                                    appnamefromcommande+= commande[i];//On récupère le nom de l'application depuis le résultat de la commande vocale
                            }
                            appnamefromcommande= appnamefromcommande.toLowerCase().replaceAll("\\s+",""); //On formalise notre String

                            for(int i=0; i<installedApps.size();i++){
                                installapp = (String) pm.getApplicationLabel(installedApps.get(i));//On formalise notre String
                                installapp = installapp.toLowerCase().replaceAll("\\s+","");
                                Log.d("Info 2.0",installapp);
                                if( installapp.contains(appnamefromcommande)){  //Si on trouve une application correspondante on récupère le nom du package afin de lancer l'appli
                                    app = installedApps.get(i).packageName;
                                    //showToastMessage(app);
                                    break;
                                }
                            }
                            //On lance l'application
                            if(app.length() > 0){
                                Intent mIntent = getPackageManager().getLaunchIntentForPackage(app);
                                if (mIntent != null) {
                                    try {
                                        startActivity(mIntent);
                                        showToastMessage("Starting "+ installapp);
                                    } catch (ActivityNotFoundException err) {
                                        Toast t = Toast.makeText(getApplicationContext(), R.string.app_not_found, Toast.LENGTH_SHORT);
                                        t.show();
                                    }
                                }
                            }
                            break;
                        //Quand on veut téléphoner
                        case ("Téléphoner"):
                            number= "";
                            for(int i =0; i< commande.length;i++){
                                if(i!=0)
                                    number+=commande[i];
                            }
                            number = number.replaceAll("[^0-9]", "");
                            Log.d("Info 2.0",number);
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + number));
                            startActivity(callIntent);
                            break;
                        //Quand on veut envoyer un sms
                        case ("SMS"):
                            number="";
                            text="";
                            for(int i=0;i<commande.length;i++){
                                if(i!=0){
                                    number+=commande[i];
                                    text+=commande[i]+" ";
                                }
                            }
                            number= number.replaceAll(matchingword,"").replaceAll("[^0-9]", "");
                            text = text.replaceAll("\\d", "");
                            text= text.replaceAll(matchingword,"");
                            text= text.trim();
                            Log.d("Info 2.0", number);
                            Log.d("Info 2.0",text);
                            SmsManager sm = SmsManager.getDefault();
                            sm.sendTextMessage(number, null, text, null, null);
                            break;
                        //Quand on veut lancer un magnétophone
                        case "Magnetophone":
                            break;
                        //Quand on veut ouvrir la boite vocale
                        case "BoiteVocale":
                            TelephonyManager  tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                            String voicemailnumber = tm.getVoiceMailNumber();
                            Intent voicemailIntent = new Intent(Intent.ACTION_CALL);
                            voicemailIntent.setData(Uri.parse("tel:" + voicemailnumber));
                            startActivity(voicemailIntent);
                            break;
                        case "PDF": //WIP
                            File file = null;
                            file = new File(Environment.getExternalStorageDirectory()+"/PDF.pdf");
                            Toast.makeText(getApplicationContext(), file.toString() , Toast.LENGTH_LONG).show();
                            if(file.exists()) {
                                Intent target = new Intent(Intent.ACTION_VIEW);
                                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                Intent intent = Intent.createChooser(target, "Open File");
                                try {
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    //Toast.makeText(getApplicationContext(),"Please install a pdf viewer",Toast.LENGTH_LONG).show();
                                }
                            }
                            else
                                Toast.makeText(getApplicationContext(), "File path is incorrect." , Toast.LENGTH_LONG).show();
                            break;
                        case"Monter Volume":
                            AudioManager audioup;
                            audioup = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                            //int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
                            audioup.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,0);
                            audioup.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,0);
                            audioup.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,0);
                            audioup.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,AudioManager.FLAG_SHOW_UI);
                            break;
                        case"Baisser Volume":
                            AudioManager audiodown;
                            audiodown = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                            audiodown.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
                            audiodown.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
                            audiodown.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
                            audiodown.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                            break;
                        case"Silencieux":
                            AudioManager audiomute;
                            audiomute = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                            audiomute.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            int currentStreamRingM = audiomute.getStreamVolume(AudioManager.STREAM_RING); // Permet d'activer le flag ( Barre du haut de volume) pour silencieux
                            audiomute.setStreamVolume(AudioManager.STREAM_RING,currentStreamRingM,AudioManager.FLAG_VIBRATE+AudioManager.FLAG_SHOW_UI);
                            break;
                        case "Vibreur":
                            AudioManager audiovibrate;
                            audiovibrate = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                            audiovibrate.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            int currentStreamRingV = audiovibrate.getStreamVolume(AudioManager.STREAM_RING); // Permet d'activer le flag ( Barre du haut de volume) pour vibreur
                            audiovibrate.setStreamVolume(AudioManager.STREAM_RING,currentStreamRingV,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE+AudioManager.FLAG_SHOW_UI);
                            break;
                        default:
                            showToastMessage("Nothing implemented for this order");
                    }
                }else{
                    showToastMessage("No command found for :" + textMatchList.toString());
                }
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

    public void initXML() {
        Log.v("XML", "Starting getXML ...");
        Boolean isAnXML = false;
        Boolean fileExist = false;
        FileInputStream fileIn = null;
        StringBuilder fileString = new StringBuilder();
        //We check if the file on the storage was created
        File file = this.getBaseContext().getFileStreamPath(XmlFileName);
        if (file == null || !file.exists()) {
            Log.v("getFile", "false");
        }
        else {
            fileExist = true;
            Log.v("getFile", "true");
        }
        if (fileExist) {
            Log.v("XML", "Starting fileExist ...");
            try {

                if(newDico)
                {
                    Log.v("XML", "Starting !isAnXML || !fileExist ...");
                    xpph.parse(this.getResources().openRawResource(R.raw.dico));
                    String XML = xpph.XmlToString();
                    try {
                        FileOutputStream fileout;
                        fileout = openFileOutput(XmlFileName, MODE_PRIVATE);
                        PrintWriter writer = new PrintWriter(fileout);
                        writer.print("");
                        writer.close();
                        fileout= openFileOutput(XmlFileName, MODE_PRIVATE);
                        OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                        outputWriter.write(XML);
                        outputWriter.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                    fileIn = openFileInput(XmlFileName);
                    fileString = new StringBuilder();
                    int ch;
                    try {
                        while ((ch = fileIn.read()) != -1) {
                            fileString.append((char) ch);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.v("XML file :", fileString.toString());
                    isAnXML = fileString.toString().startsWith("<Dictionnaire>");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //If there is no file or it's not an XML, we get the Dictionnary from the ressources and create the file rigght away
        if (!isAnXML || !fileExist) {
            Log.v("XML", "Starting !isAnXML || !fileExist ...");
            xpph.parse(this.getResources().openRawResource(R.raw.dico));
            String XML = xpph.XmlToString();
            try {
                FileOutputStream fileout;
                fileout = openFileOutput(XmlFileName, MODE_PRIVATE);
                OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                outputWriter.write(XML);
                outputWriter.close();

                //display file saved message
                Toast.makeText(getBaseContext(), "File saved successfully!",
                        Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.v("XML", "Parsing Xml ...");
            try {
                fileIn = openFileInput(XmlFileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            xpph.parse(fileIn);
        }
    }

    public void displayFileContent() {
        try {
            FileInputStream fileIn = openFileInput(XmlFileName);
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
            }
            InputRead.close();
            Log.v("XML", s);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
