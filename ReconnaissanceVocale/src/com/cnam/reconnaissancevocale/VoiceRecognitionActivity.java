package com.cnam.reconnaissancevocale;

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

import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class

VoiceRecognitionActivity extends Activity {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;

    //private EditText metTextHint;
    //private ListView mlvTextMatches;
    private Button mbtReglage;
    private Button mbtAide;
    private PhoneStateListener listener = new PhoneStateListener();
    private List pkgAppsList;
    private List<ApplicationInfo> installedApps;
    private XmlPullParserHandler xpph;
    private PackageManager pm;
    static final int READ_BLOCK_SIZE = 100;
    static boolean newDico = false;
    static final String XmlFileName = "dico.xml";
    static final String Language = Locale.getDefault().getDisplayLanguage();

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_voice_recognition);
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
        for (int i = 0; i < installedApps.size(); i++) {
            Log.d("info", (String) pm.getApplicationLabel(installedApps.get(i)));
        }


        mbtAide = (Button) findViewById(R.id.btnAide);
        mbtAide.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent helpScreen = new Intent(getApplicationContext(), Aide.class);
                startActivity(helpScreen);
            }
        });
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
                String allwordsoncommand = "";
                String matchingword = null;//The word who match with the commande
                if (!pkgAppsList.isEmpty())
                    Log.d("info", Locale.getDefault().getDisplayLanguage().toString());

                for (int i = 0; i < textMatchList.toArray().length; i++) {
                    commande = textMatchList.toArray()[i].toString().split(" ");
                    if (found)
                        break;
                    for (Command command : xpph.cmds) {
                        if (found)
                            break;
                        for (String leword : command.getFR()) {
                            for (String cmd : commande) {
                                if (found)
                                    break;
                                if (cmd.toLowerCase().compareTo(leword.toLowerCase()) == 0) {
                                    //showToastMessage(command.getFunction().toString());
                                    matchingword = leword;
                                    comFound = command;
                                    found = true;
                                    break;
                                } else {
                                    allwordsoncommand += cmd.toLowerCase();
                                }
                            }
                            //on regarde si la commande match si on a plusieurs mots
                            if (allwordsoncommand.replaceAll("\\s+", "").contains(leword.toLowerCase().replaceAll("\\s+", ""))) {
                                matchingword = leword;
                                comFound = command;
                                found = true;
                                break;
                            }
                        }

                    }
                }
                if (found) {
                    String number;
                    String text;
                    switch (comFound.getFunction()) {
                        /**
                         * Case Ouverture when you want to open an app from your phone
                         */
                        case ("Ouverture"):
                            String app = ""; //Nom du package qui va etre lancé
                            String appnamefromcommande = ""; //Nom de l'application récuperer par la reconnaissance vocale
                            String installapp = "";   //Nom des applications installés
                            for (int i = 0; i < commande.length; i++) {
                                if (i != 0)
                                    appnamefromcommande += commande[i];//On récupère le nom de l'application depuis le résultat de la commande vocale
                            }
                            appnamefromcommande = appnamefromcommande.toLowerCase().replaceAll("\\s+", ""); //On formalise notre String

                            for (int i = 0; i < installedApps.size(); i++) {
                                installapp = (String) pm.getApplicationLabel(installedApps.get(i));//On formalise notre String
                                installapp = installapp.toLowerCase().replaceAll("\\s+", "");
                                Log.d("Info 2.0", installapp);
                                if (installapp.contains(appnamefromcommande)) {  //Si on trouve une application correspondante on récupère le nom du package afin de lancer l'appli
                                    app = installedApps.get(i).packageName;
                                    //showToastMessage(app);
                                    break;
                                }
                            }
                            //On lance l'application
                            if (app.length() > 0) {
                                Intent mIntent = getPackageManager().getLaunchIntentForPackage(app);
                                if (mIntent != null) {
                                    try {
                                        startActivity(mIntent);
                                        showToastMessage("Starting " + installapp);
                                    } catch (ActivityNotFoundException err) {
                                        Toast t = Toast.makeText(getApplicationContext(), R.string.app_not_found, Toast.LENGTH_SHORT);
                                        t.show();
                                    }
                                }
                            }
                            break;
                        /**
                         * Case Recherche when you want to start a Web Research
                         */
                        case ("Recherche"):
                            String query="";
                            for (int i =1; i<commande.length; i++){
                                query +=commande[i]+" ";
                            }
                            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                            intent.putExtra(SearchManager.QUERY, query); // query contains search string
                            startActivity(intent);
                            break;

                        /**
                         * Case Téléphone when you want to make a call with a number/ contact name
                         */
                        case ("Téléphoner"):
                            number = "";
                            for (int i = 0; i < commande.length; i++) {
                                if (i != 0)
                                    number += commande[i];
                            }
                            number = number.replaceAll("[^0-9]", "");
                            Log.d("Info 2.0", number);
                            if(number.length() < 2){
                                String name="";
                                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
                                while (phones.moveToNext())
                                {
                                    if(allwordsoncommand.replaceAll("\\s+", "").toLowerCase().contains(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).replaceAll("\\s+", "").toLowerCase())){
                                        number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));;
                                        Toast.makeText(getApplicationContext(),name, Toast.LENGTH_LONG).show();
                                    }
                                }
                                phones.close();
                            }
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + number));
                            startActivity(callIntent);
                            break;
                        /**
                         * When you want to send an sms with a number/contact name
                         */
                        case ("SMS"):
                            number = "";
                            text = "";
                            for (int i = 0; i < commande.length; i++) {
                                if (i != 0) {
                                    number += commande[i];
                                    text += commande[i].toLowerCase() + " ";
                                }
                            }
                            number = number.replaceAll(matchingword, "").replaceAll("[^0-9]", "");
                            if(number.length() < 2){
                                String name="";
                                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
                                while (phones.moveToNext())
                                {
                                    if(allwordsoncommand.replaceAll("\\s+", "").toLowerCase().contains(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).replaceAll("\\s+", "").toLowerCase())){
                                        number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                        Toast.makeText(getApplicationContext(),name, Toast.LENGTH_LONG).show();
                                        text = text.toLowerCase().replaceAll(name,"");
                                    }
                                }
                                phones.close();
                            }
                            text = text.replaceAll("\\d", "");
                            text = text.replaceAll(matchingword, "");
                            text = text.trim();
                            Log.d("Info 2.0", number);
                            Log.d("Info 2.0", text);
                            SmsManager sm = SmsManager.getDefault();
                            sm.sendTextMessage(number, null, text, null, null);
                            break;
                        /**
                         * Case Magnetophone when you want to start the audio recorder
                         */
                        case "Magnetophone":
                            break;
                        /*
                         * Case BoiteVocale when you want to start the voice mail
                         */
                        case "BoiteVocale":
                            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                            String voicemailnumber = tm.getVoiceMailNumber();
                            Intent voicemailIntent = new Intent(Intent.ACTION_CALL);
                            voicemailIntent.setData(Uri.parse("tel:" + voicemailnumber));
                            startActivity(voicemailIntent);
                            break;
                        /*
                         * Case PDF when you want to open a PDF
                         */
                        case "PDF": //WIP
                            File file = null;
                            file = new File(Environment.getExternalStorageDirectory() + "/PDF.pdf");
                            Toast.makeText(getApplicationContext(), file.toString(), Toast.LENGTH_LONG).show();
                            if (file.exists()) {
                                Intent target = new Intent(Intent.ACTION_VIEW);
                                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                Intent intentpdf = Intent.createChooser(target, "Open File");
                                try {
                                    startActivity(intentpdf);
                                } catch (ActivityNotFoundException e) {
                                    //Toast.makeText(getApplicationContext(),"Please install a pdf viewer",Toast.LENGTH_LONG).show();
                                }
                            } else
                                Toast.makeText(getApplicationContext(), "File path is incorrect.", Toast.LENGTH_LONG).show();
                            break;
                        /**
                         * Case Monter Volume when you want to up the audio level of your phone
                         */
                        case "Monter Volume":
                            AudioManager audioup;
                            audioup = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                            //int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
                            audioup.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
                            audioup.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
                            audioup.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
                            audioup.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                            break;
                        /**
                         * Case Baisser Volume when you want to down the audio level of your phone
                         */
                        case "Baisser Volume":
                            AudioManager audiodown;
                            audiodown = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                            audiodown.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
                            audiodown.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
                            audiodown.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
                            audiodown.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                            break;
                        /**
                         * Case Silencieux when you want to set your phone state to mute
                         */
                        case "Silencieux":
                            AudioManager audiomute;
                            audiomute = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                            audiomute.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            int currentStreamRingM = audiomute.getStreamVolume(AudioManager.STREAM_RING); // Permet d'activer le flag ( Barre du haut de volume) pour silencieux
                            audiomute.setStreamVolume(AudioManager.STREAM_RING, currentStreamRingM, AudioManager.FLAG_VIBRATE + AudioManager.FLAG_SHOW_UI);
                            break;
                        /**
                         * Case Vibreur when you want to set your phone state to vibrate
                         */
                        case "Vibreur":
                            AudioManager audiovibrate;
                            audiovibrate = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                            audiovibrate.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            int currentStreamRingV = audiovibrate.getStreamVolume(AudioManager.STREAM_RING); // Permet d'activer le flag ( Barre du haut de volume) pour vibreur
                            audiovibrate.setStreamVolume(AudioManager.STREAM_RING, currentStreamRingV, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE + AudioManager.FLAG_SHOW_UI);
                            break;
                        /**
                         * When no implementation are found, show a toast
                         */
                        default:
                            showToastMessage("Nothing implemented for this order");
                    }
                } else {
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

    /**
     *
     * @param message The message we want to show to the user
     */
    public void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     *  This function was used to debug and see what was inside the file stored on the phone
     */
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

    /*
    @Override
    public void onReceive(Context mContext, Intent intent) //Intercept les signaux du téléphone
    {
        saveContext=mContext;
        // Get the current Phone State


        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if(state==null){
            return;
        }

        Bundle bundle = intent.getExtras();
        String number= bundle.getString("incoming_number");
        Calendar calendar=Calendar.getInstance();
        // If phone state "Rininging"
        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
        {
            ring =true;
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            //Toast.makeText(mContext, "Detected call : " + incomingNumber.toString(), Toast.LENGTH_SHORT).show(); // Récupération et affichage du numéro entrant

            /*Intent intentCall = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); //Création d'un "intent" ou action
            intentCall.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
            intentCall.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

            intentCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentCall.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
            mContext.startActivity(intentCall);                                  //Tentative de lancement de l'application de détection de la reconnaissance vocale
            Toast.makeText(mContext,"Activité lancée ",Toast.LENGTH_LONG).show();
        }



        // If incoming call is received
        if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
        {
            callReceived=true;
        }


        // If phone is Idle
        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
        {
            // If phone was ringing(ring=true) and not received(callReceived=false) , then it is a missed call
            if(ring==true&&callReceived==false)
            {
                Toast.makeText(mContext, "Appel manqué : " + number.toString(), Toast.LENGTH_SHORT).show();
                Intent voiceintent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
                mContext.startActivity(voiceintent);
                ring=false;
            }
            callReceived=false;
        }
    }*/

    /**
     * Init the XML File, if there is no XML file stores on the phone, create a new one based on dico.xml, else load the file
     */
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
        } else {
            fileExist = true;
            Log.v("getFile", "true");
        }
        if (fileExist) {
            Log.v("XML", "Starting fileExist ...");
            try {

                if (newDico) {
                    Log.v("XML", "Starting !isAnXML || !fileExist ...");
                    xpph.parse(this.getResources().openRawResource(R.raw.dico));
                    String XML = xpph.XmlToString();
                    try {
                        FileOutputStream fileout;
                        fileout = openFileOutput(XmlFileName, MODE_PRIVATE);
                        PrintWriter writer = new PrintWriter(fileout);
                        writer.print("");
                        writer.close();
                        fileout = openFileOutput(XmlFileName, MODE_PRIVATE);
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
}
