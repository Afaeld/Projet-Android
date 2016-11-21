package com.cnam.reconnaissancevocale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
/**
 * Created by Vic on 24/05/2016.
 */


/**
 * Differentes fonctionnalites associees a la reception des appels
 */
public class CallReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent) //Intercept les signaux du telephone
    {
        Log.d("Call", intent.getStringExtra(TelephonyManager.EXTRA_STATE).toString());
        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) //Si le message est "Sonnerie"
        {
            // Ce code sera execute lorsque le telephone reçoit un appel entrant

            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Toast.makeText(context, "Detected call : " + incomingNumber.toString(), Toast.LENGTH_SHORT).show(); // Recuperation et affichage du numero entrant


            Intent intentCall = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); //Creation d'un "intent" ou action
            intentCall.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
            intentCall.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

            intentCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentCall.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
            context.startActivity(intentCall);                                  //Tentative de lancement de l'application de detection de la reconnaissance vocale
            Toast.makeText(context,"Activite lancee ",Toast.LENGTH_LONG).show();
        }
    }
}