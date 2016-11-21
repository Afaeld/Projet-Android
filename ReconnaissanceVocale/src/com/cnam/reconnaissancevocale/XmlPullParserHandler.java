package com.cnam.reconnaissancevocale;


/**
 * Created by jerome on 02/06/2016.
 */
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * Class de gestion du fichier XML
 * Liste les commandes, les mots associés aux commandes, le context et le chemin du fichier.
 *
 */
public class XmlPullParserHandler {
    private String text;
    public ArrayList<Command> cmds = new ArrayList<Command>();
    public Command cmd;
    private String XmlFileName;
    private Context context;

    public XmlPullParserHandler(Context contextt, String XmlFileName){
        this.XmlFileName = XmlFileName;
        this.context = contextt;
    }
    public void parse(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser  parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("Command")) {
                            // create a new instance of employee
                            cmd = new Command();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("Command")) {
                            // add employee object to list
                            cmds.add(cmd);
                            Log.v("Xpph Command", "fin");
                        }else if(tagname.equalsIgnoreCase("Function")){
                            Log.v("Xpph Function",text);
                            cmd.setFunction(text);
                        }
                        else if(tagname.equalsIgnoreCase("Category")){
                            Log.v("Xpph Category",text);
                            cmd.setCategory(text);
                        }
                        else if(tagname.equalsIgnoreCase("ES")){
                            Log.v("Xpph ES",text);
                            cmd.addES(text);
                        }
                        else if(tagname.equalsIgnoreCase("FR")){
                            Log.v("Xpph FR",text);
                            cmd.addFR(text);
                        }
                        else if(tagname.equalsIgnoreCase("EN")){
                            Log.v("Xpph EN",text);
                            cmd.addEN(text);
                        }
                        else if(tagname.equalsIgnoreCase("DE")){
                            Log.v("Xpph DE",text);
                            cmd.addDE(text);
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }
            for(int i =0;i< cmds.size(); i++){
                cmds.get(i).display();
            }
        } catch (XmlPullParserException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
    }

    public void addCmd(String category, String function, ArrayList<String> EN, ArrayList<String> FR, ArrayList<String> DE, ArrayList<String> ES){
        cmds.add(new Command(category,function,EN,FR,DE,ES));
        save();
    }

    public void removeCmd(int index){
        if(cmds.size() > index)
            cmds.remove(index);
        save();
    }
    public void addTranslate(int index,String lang, String traduction){
        switch (lang){
            case "EN":
                cmds.get(index).addEN(traduction);
                save();
                break;
            case "ES":
                cmds.get(index).addES(traduction);
                save();
                break;
            case "FR":
                cmds.get(index).addFR(traduction);
                save();
                break;
            case "DE":
                cmds.get(index).addDE(traduction);
                save();
                break;
        }
    }

    /**
     * Sauvegarde le contenu du XPPH dans le fichier XML
     * Vide le fichier XML pour le remplir avec le XPPH
     */
    public void save(){
        try {
            Log.v("XPPHSaved","Saved");
            FileOutputStream fileout;
            fileout= this.context.openFileOutput(XmlFileName, context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(fileout);
            writer.print("");
            writer.close();
            fileout= this.context.openFileOutput(XmlFileName, context.MODE_PRIVATE);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write(this.XmlToString());
            outputWriter.close();

            //display file saved message
            Toast.makeText(context, "File saved successfully!",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affichage complet du contenu du fichier xml
     * @return String contenu du fichier xml formaté
     */
    public String XmlToString() {

        String chaine="<Dictionnaire>";

        for (Command commande: cmds) {
            chaine = chaine + "<Command>";
            chaine = chaine + "<Function>" + commande.getFunction() + "</Function>";
            chaine = chaine + "<Category>" + commande.getCategory()+ "</Category>" ;

            chaine = chaine + "<Words>";

            for (String command:commande.getFR()) {
                chaine = chaine + "<Fr>" + command + "</Fr>";
            }

            for (String command:commande.getEN()) {
                chaine = chaine + "<En>" + command + "</En>";
            }

            for (String command:commande.getDE()) {
                chaine = chaine + "<De>" + command + "</De>";
            }

            for (String command:commande.getES()) {
                chaine = chaine + "<Es>" + command + "</Es>";
            }

            chaine = chaine + "</Words>";
            chaine = chaine + "</Command>";
        }

        chaine = chaine+"</Dictionnaire>";

        return chaine;
    }
}