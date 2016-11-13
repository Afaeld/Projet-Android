package com.rakesh.voicerecognition;

/**
 * Created by Vikes on 29/09/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cengalabs.flatui.FlatUI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class settings extends Activity {


    private Spinner allCommands;
    private ListView listWord;
    private XmlPullParserHandler xpph;
    private ArrayAdapter<String> arrayAdapter;
    private EditText editWords;
    private Context thisContext;
    private String selectedWord;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(FlatUI.DEEP);
        setContentView(R.layout.settings1);
        Intent i = getIntent();
        thisContext = this;
        String name = i.getStringExtra("name");
        String email = i.getStringExtra("email");
        Log.e("Second Screen", name + "." + email);
        allCommands = (Spinner) findViewById(R.id.commandSpinner);
        editWords = (EditText) findViewById(R.id.wordsEdit);
        listWord = (ListView) findViewById(R.id.listeWord);
        getXpph();
        fillData();
        addEventListener();
        String[] widgetModes = {"Mode 1", "Mode2"};
        ArrayAdapter<String> widgetModeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, widgetModes);
        widgetModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void addEventListener() {
        Button btnSauvegarder = (Button) findViewById(R.id.btnSauvegarder);
        btnSauvegarder.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {


                for (Command commande : xpph.cmds) {
                    if (commande.getFunction().toString().toLowerCase().equals(allCommands.getSelectedItem().toString().toLowerCase()))
                        if (VoiceRecognitionActivity.Language.equals("français")) {
                            for (int i = 0; i < commande.getFR().size(); i++) {
                                if (commande.getFR().get(i).toString().toLowerCase().equals(selectedWord)) {
                                    commande.getFR().set(i, editWords.getText().toString().toLowerCase());
                                    break;
                                }
                            }
                        } else if (VoiceRecognitionActivity.Language.equals("english")) {
                            for (int i = 0; i < commande.getFR().size(); i++) {
                                if (commande.getEN().get(i).toString().toLowerCase().equals(selectedWord)) {
                                    commande.getEN().set(i, editWords.getText().toString().toLowerCase());
                                    break;
                                }
                            }
                        } else if (VoiceRecognitionActivity.Language.equals("german")) {
                            for (int i = 0; i < commande.getFR().size(); i++) {
                                if (commande.getDE().get(i).toString().toLowerCase().equals(selectedWord)) {
                                    commande.getDE().set(i, editWords.getText().toString().toLowerCase());
                                    break;
                                }
                            }
                        } else {
                            for (int i = 0; i < commande.getFR().size(); i++) {
                                if (commande.getES().get(i).toString().toLowerCase().equals(selectedWord)) {
                                    commande.getES().set(i, editWords.getText().toString().toLowerCase());
                                    break;
                                }
                            }
                        }
                }
                xpph.save();
                fillData();
            }
        });

        allCommands.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected = allCommands.getSelectedItem().toString();
                List<String> words = new ArrayList<String>();
                boolean commandeFounded = false;
                for (Command commande : xpph.cmds) {
                    if (VoiceRecognitionActivity.Language.equals("français")) {
                        for (String str : commande.getFR()) {
                            if (commande.getFunction().toLowerCase().equals(selected.toLowerCase())) {
                                words.add(str);
                                commandeFounded = true;
                            }
                        }
                    } else if (VoiceRecognitionActivity.Language.equals("english")) {
                        for (String str : commande.getEN()) {
                            if (commande.getFunction().toLowerCase().equals(selected.toLowerCase())) {
                                words.add(str);
                                commandeFounded = true;
                            }
                        }
                    } else if (VoiceRecognitionActivity.Language.equals("german")) {
                        for (String str : commande.getDE()) {
                            if (commande.getFunction().toLowerCase().equals(selected.toLowerCase())) {
                                words.add(str);
                                commandeFounded = true;
                            }
                        }
                    } else {
                        for (String str : commande.getES()) {
                            if (commande.getFunction().toLowerCase().equals(selected.toLowerCase())) {
                                words.add(str);
                                commandeFounded = true;
                            }
                        }
                    }

                    if (commandeFounded)
                        break;
                }
                arrayAdapter = new ArrayAdapter<String>(thisContext, android.R.layout.simple_list_item_1, words);
                listWord.setAdapter(new ArrayAdapter<String>(thisContext, android.R.layout.simple_list_item_1, new ArrayList<String>()));
                listWord.setAdapter(arrayAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        listWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Object o = listWord.getItemAtPosition(position);
                editWords.setText((String) o.toString());
                selectedWord = (String) o.toString();
            }
        });

        Button btnSupprimer = (Button) findViewById(R.id.btnSupprimer);
        btnSupprimer.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String selected = allCommands.getSelectedItem().toString();
                for (Command commande : xpph.cmds) {
                    if (commande.getFunction().toLowerCase().equals(selected.toLowerCase())) {
                        xpph.removeCmd(
                                xpph.cmds.indexOf(commande));
                        xpph.save();
                        fillData();
                        return;
                    }
                }
            }
        });
        Button btnNouveau = (Button) findViewById(R.id.btnNouveau);
        btnNouveau.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String selected = allCommands.getSelectedItem().toString();
                for (Command commande : xpph.cmds) {
                    if (commande.getFunction().toLowerCase().equals(selected.toLowerCase())) {
                        if (VoiceRecognitionActivity.Language.equals("français")) {
                            commande.addFR(editWords.getText().toString().toLowerCase());
                            xpph.save();
                            fillData();
                            return;
                        } else if (VoiceRecognitionActivity.Language.equals("english")) {
                            commande.addEN(editWords.getText().toString().toLowerCase());
                            xpph.save();
                            fillData();
                            return;
                        } else if (VoiceRecognitionActivity.Language.equals("german")) {
                            commande.addDE(editWords.getText().toString().toLowerCase());
                            xpph.save();
                            fillData();
                            return;
                        } else {
                            commande.addES(editWords.getText().toString().toLowerCase());
                            xpph.save();
                            fillData();
                            return;
                        }

                    }
                }
            }
        });
    }

    public void getXpph() {
        try {
            xpph = new XmlPullParserHandler(thisContext.getApplicationContext(), VoiceRecognitionActivity.XmlFileName);
            initXML();

        } catch (Exception e) {
            Log.e("Exception", e.getMessage() + e.getCause());
        }
    }

    public void initXML() {
        Log.v("XML", "Starting getXML ...");
        Boolean isAnXML = false;
        Boolean fileExist = false;
        FileInputStream fileIn = null;
        StringBuilder fileString = new StringBuilder();
        //We check if the file on the storage was created
        File file = this.getBaseContext().getFileStreamPath(VoiceRecognitionActivity.XmlFileName);
        if (file == null || !file.exists()) {
            Log.v("getFile", "false");
        } else {
            fileExist = true;
            Log.v("getFile", "true");
        }
        if (fileExist) {
            Log.v("XML", "Starting fileExist ...");
            try {

                if (VoiceRecognitionActivity.newDico) {
                    Log.v("XML", "Starting !isAnXML || !fileExist ...");
                    xpph.parse(this.getResources().openRawResource(R.raw.dico));
                    String XML = xpph.XmlToString();
                    try {
                        FileOutputStream fileout;
                        fileout = openFileOutput(VoiceRecognitionActivity.XmlFileName, MODE_PRIVATE);
                        PrintWriter writer = new PrintWriter(fileout);
                        writer.print("");
                        writer.close();
                        fileout = openFileOutput(VoiceRecognitionActivity.XmlFileName, MODE_PRIVATE);
                        OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                        outputWriter.write(XML);
                        outputWriter.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                fileIn = openFileInput(VoiceRecognitionActivity.XmlFileName);
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
                fileout = openFileOutput(VoiceRecognitionActivity.XmlFileName, MODE_PRIVATE);
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
                fileIn = openFileInput(VoiceRecognitionActivity.XmlFileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            xpph.parse(fileIn);
        }
    }

    public void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void fillData() {
        List<String> spinnerArray = new ArrayList<String>();

        for (Command commande : xpph.cmds) {
            spinnerArray.add(commande.getFunction().toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                thisContext, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        allCommands.setAdapter(new ArrayAdapter<String>(
                thisContext, android.R.layout.simple_spinner_item, new ArrayList<String>()));
        allCommands.setAdapter(adapter);
    }
}
