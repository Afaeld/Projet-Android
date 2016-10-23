package com.rakesh.voicerecognition;


/**
 * Created by jerome on 02/06/2016.
 */
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;



public class XmlPullParserHandler {
    private String text;
    public ArrayList<Command> cmds = new ArrayList<Command>();
    public Command cmd;
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

    public String XmlToString() {

        String chaine="<Dictionnaire>";

        for (Command commande: cmds) {
            chaine = chaine + "<Command>";
            chaine = chaine + "<Function>" + commande.getFunction() + "</Function>";
            chaine = chaine + "<Category>" + commande.getCategory()+ "</Category>" ;

            chaine = chaine + "<Words>";

            for (String command:commande.getFR()) {
                chaine = chaine + "<Fr>" + command + "</fr>";
            }

            for (String command:commande.getEN()) {
                chaine = chaine + "<En>" + command + "</En>";
            }

            for (String command:commande.getDE()) {
                chaine = chaine + "<De>" + command + "</De>";
            }

            for (String command:commande.getES()) {
                chaine = chaine + "<ES>" + command + "</Es>";
            }

            chaine = chaine + "</Words>";
            chaine = chaine + "</Command>";
        }

        chaine = chaine+"</Dictionnaire>";

        return chaine;
    }
}