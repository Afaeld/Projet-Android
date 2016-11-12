package com.rakesh.voicerecognition;

/**
 * Created by Vikes on 29/09/2016.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cengalabs.flatui.FlatUI;

import java.util.ArrayList;
import java.util.List;

public class settings  extends Activity {



    private Spinner allCommands;
    private ListView listWord;
    private XmlPullParserHandler xpph;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlatUI.initDefaultValues(this);



// Setting default theme to avoid to add the attribute "theme" to XML
// and to be able to change the whole theme at once
        FlatUI.setDefaultTheme(FlatUI.DEEP);
        setContentView(R.layout.settings1);


       // FlatUI.SetActivityTheme(FlatTheme.Sky());
        Intent i = getIntent();
        // Receiving the Data
        String name = i.getStringExtra("name");
        String email = i.getStringExtra("email");
        Log.e("Second Screen", name + "." + email);
        allCommands = (Spinner) findViewById(R.id.commandSpinner);
        //xpph = new XmlPullParserHandler(this.getApplicationContext(),"C:\\Users\\Vikes\\Documents\\GitHub\\Projet-Android\\VoiceRecognitionRakesh\\dico.xml");
        xpph.parse(this.getResources().openRawResource(R.raw.dico));
        List<String> spinnerArray =  new ArrayList<String>();

        for (Command commande: xpph.cmds) {
            spinnerArray.add(commande.getFunction().toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        allCommands.setAdapter(adapter);

        Button buttonOne = (Button) findViewById(R.id.btnSupprimer);
        buttonOne.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String selected = allCommands.getSelectedItem().toString();
                int i=0;
                boolean test = false;
                for (Command commande: xpph.cmds) {

                    for(String str:commande.getFR())
                    {
                        if(str.toLowerCase().equals(selected.toLowerCase())) {
                            xpph.removeCmd(i);
                            test=true;
                            break;
                        }
                    }
                    if(test)
                        break;
                    i++;

                }

            }
        });
        listWord= (ListView) findViewById(R.id.listeWord);
        allCommands.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

              String selected = allCommands.getSelectedItem().toString();
              ArrayAdapter<String> arrayAdapter;
              String[] wordsFr= {};

              boolean test = false;
              for (Command commande: xpph.cmds) {
                  for(String str:commande.getFR())
                  {
                      //if(str.toLowerCase().equals(selected.toLowerCase()))
                  }
              }
             // arrayAdapter = new ArrayAdapter<String>(,android.R.layout.simple_list_item_1, wordsFr);

              //listWord.setAdapter(arrayAdapter);


          }

          @Override
          public void onNothingSelected(AdapterView<?> parentView) {
              // your code here
          }

      });

        String[] widgetModes = {"Mode 1", "Mode2"};
        ArrayAdapter<String> widgetModeAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, widgetModes);
        widgetModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ListView words = (ListView) findViewById(R.id.listeWord);



    }


    public void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
