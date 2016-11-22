package com.cnam.reconnaissancevocale;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.cengalabs.flatui.FlatUI;

/**
 * Created by Victor Bauer on 19/11/2016.
 * ecran d'affichage de l'aide de l'application, expliquant les differents buts des differents layouts
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
        tvHelp.setText("Aide :\n•\tOuvrir une application installée (lancer, lancement, ouvrir) +*nom application*\n" +
                "•\tOuvrir PDF les PDF doivent se trouver dans le dossier*Dossier PDF* (ouvrir, PDF) +*nom PDF*\n" +
                "•\tTéléphoner à un numéro (appeler, téléphoner, contacter) +*numéro*\n" +
                "•\tEnvoyer un SMS à un numéro (sms, envoyer) +*numéro*+*message*\n" +
                "•\tOuvrir la boite vocale (boite vocale, boite message vocaux, messages vocaux, messagerie vocale)\n" +
                "•\tMonter le son (monter volume)\n" +
                "•\tBaisser le son (baisser volume)\n" +
                "•\tActiver le mode silencieux (couper son)\n" +
                "•\tActiver le mode vibreur(vibreur)\n Reconnaissance vocale : \n" +
                "Appuyer simplement sur le bouton \"speak\" et énoncer la commande désirée\n" +
                "\n" +
                "Editeur de commande :\n" +
                "Une commande peut être exécutée avec des mots différents. (Par exemple la commande téléphoner peut être utilisée avec les mots « appeler », « téléphoner », « contacter »).\n\n" +
                "Ajout d’un mot\n" +
                "Pour ajouter un mot lié à une commande, choisir la commande dans la liste déroulante, puis dans la barre de texte écrire le mot désiré puis sur « Nouveau »\n\n" +
                "Suppression d’une commande\n" +
                "Pour supprimer une commande , cliquer sur la commande à supprimer plus cliquez sur  le bouton « supprimer »\n\n" +
                "Modification d’un mot\n" +
                "Pour modifier un mot , sélectionner la commande désirée puis choisir le mot à modifier, effectuer la correction puis cliquer sur « sauvegarder » \n\n ");
    }


}
