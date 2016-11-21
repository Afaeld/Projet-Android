package com.cnam.reconnaissancevocale;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.cengalabs.flatui.FlatUI;

/**
 * Created by Victor Bauer on 19/11/2016.
 * Écran d'affichage de l'aide de l'application, expliquant les différents buts des différents layouts
 */

public class Aide extends Activity {

    TextView tvHelp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(FlatUI.DEEP);
        setContentView(R.layout.help);
        tvHelp= (TextView) findViewById(R.id.tvAide);
        tvHelp.setText("Aide :   L'écran principale permet de lancer la reconnaissance vocale puis d'exécuter des commmandes. Le bouton settings permet de configurer les commandes, ajouter des mots clé aux commandes et enfin de supprimer des mots clé.");

    }


}