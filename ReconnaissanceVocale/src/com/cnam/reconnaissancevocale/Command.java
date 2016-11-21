package com.cnam.reconnaissancevocale;


import android.util.Log;

import java.util.ArrayList;

/**
 * Created by jerome on 16/09/2016.
 * Représente une commande, qui peut être sérializée en XML, avec des mots clés associés dans différentes langues.
 */
public class Command {

    private String category;
    private String function;
    private ArrayList<String> EN;
    private ArrayList<String> FR;
    private ArrayList<String> DE;
    private ArrayList<String> ES;

    /**
     * Initialize a new command with the 4 languages arrays
     */
    public Command(){
        this.EN = new ArrayList<String>();
        this.FR = new ArrayList<String>();
        this.ES = new ArrayList<String>();
        this.DE = new ArrayList<String>();
    }

    /**
     *
     * @param category Category of the command
     * @param function Function of the command, this is what we use for the switch case in our Voice Recognition
     * @param EN       Array of English matching words
     * @param FR       Array of French matching words
     * @param DE       Array of German matching words
     * @param ES       Array of Spanish matching words
     */
    public Command(String category, String function, ArrayList<String> EN, ArrayList<String> FR, ArrayList<String> DE, ArrayList<String> ES) {
        this.function = function;
        this.EN = EN;
        this.FR = FR;
        this.DE = DE;
        this.ES = ES;
    }

    /**
     *
     * @return Command's category
     */
    public String getCategory() {
        return this.category;
    }

    /**
     *
     * @return Command's function
     */
    public String getFunction() {
        return this.function;
    }

    /**
     *
     * @return Command's Spanish matching words
     */
    public ArrayList<String> getES() {
        return ES;
    }

    /**
     *
     * @return Command's German matching words
     */
    public ArrayList<String> getDE() {
        return DE;
    }

    /**
     *
     * @return Command's French matching words
     */
    public ArrayList<String> getFR() {
        return FR;
    }

    /**
     *
     * @return Command's English matching words
     */
    public ArrayList<String> getEN() {
        return EN;
    }

    /**
     *
     * @param en The word we want to add to the english matching words ArrayList
     */
    public void addEN(String en) {
        this.EN.add(en);
    }

    /**
     *
     * @param es The word we want to add to the spanish matching words ArrayList
     */
    public void addES(String es) {
        this.ES.add(es);
    }

    /**
     *
     * @param de The word we want to add to the german matching words ArrayList
     */
    public void addDE(String de) {
       this.DE.add(de);
    }

    /**
     *
     * @param fr The word we want to add to the french matching words ArrayList
     */
    public void addFR(String fr) {
        this.FR.add(fr);
    }

    /**
     *
     * @param category The category we want to have for this command
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     *
     * @param function The function we want to have for this command
     */
    public void setFunction(String function) {
        this.function = function;
    }

    /**
     *
     * @param ES The arraylist of spanish matching words we want to have ( replace the current one )
     */
    public void setES(ArrayList<String> ES) {
        this.ES = ES;
    }

    /**
     *
     * @param DE The arraylist of german matching words we want to have ( replace the current one )
     */
    public void setDE(ArrayList<String> DE) {
        this.DE = DE;
    }

    /**
     *
     * @param FR The arraylist of french matching words we want to have ( replace the current one )
     */
    public void setFR(ArrayList<String> FR) {
        this.FR = FR;
    }

    /**
     *
     * @param EN The arraylist of english matching words we want to have ( replace the current one )
     */
    public void setEN(ArrayList<String> EN) {
        this.EN = EN;
    }

    /**
     * Display all the command informations on logcat, usefull for debugging
     */
    public void display(){
        Log.v("XPPH Display","====Starting Display====");
        Log.v("XPPH Display","Category : " +this.getCategory());
        Log.v("XPPH Display","Function : " +this.getFunction());
        Log.v("XPPH Display","========FR=========");
        for(int i = 0; i < this.FR.size(); i++){
            Log.v("XPPH Display",this.getFR().get(i));
        }
        Log.v("XPPH Display","========EN=========");
        for(int i = 0; i < this.EN.size(); i++){
            Log.v("XPPH Display",this.getEN().get(i));
        }
        Log.v("XPPH Display","========DE=========");
        for(int i = 0; i < this.DE.size(); i++){
            Log.v("XPPH Display",this.getDE().get(i));
        }
        Log.v("XPPH Display","========ES=========");
        for(int i = 0; i < this.ES.size(); i++){
            Log.v("XPPH Display",this.getES().get(i));
        }
        Log.v("XPPH Display","====Ending Display====");
    }
}
