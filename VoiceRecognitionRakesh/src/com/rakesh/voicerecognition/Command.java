package com.rakesh.voicerecognition;


import android.util.Log;

import java.util.ArrayList;

/**
 * Created by jerome on 16/09/2016.
 */
public class Command {

    private String category;
    private String function;
    private ArrayList<String> EN;
    private ArrayList<String> FR;
    private ArrayList<String> DE;
    private ArrayList<String> ES;

    public Command(){
        this.EN = new ArrayList<String>();
        this.FR = new ArrayList<String>();
        this.ES = new ArrayList<String>();
        this.DE = new ArrayList<String>();
    }

    public Command(String category, String function, ArrayList<String> EN, ArrayList<String> FR, ArrayList<String> DE, ArrayList<String> ES) {
        this.function = function;
        this.EN = EN;
        this.FR = FR;
        this.DE = DE;
        this.ES = ES;
    }

    public String getCategory() {
        return this.category;
    }

    public String getFunction() {
        return this.function;
    }

    public ArrayList<String> getES() {
        return ES;
    }

    public ArrayList<String> getDE() {
        return DE;
    }

    public ArrayList<String> getFR() {
        return FR;
    }

    public ArrayList<String> getEN() {
        return EN;
    }

    public void addEN(String en) {
        this.EN.add(en);
    }

    public void addES(String es) {
        this.ES.add(es);
    }

    public void addDE(String de) {
       this.DE.add(de);
    }

    public void addFR(String fr) {
        this.FR.add(fr);
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public void setES(ArrayList<String> ES) {
        this.ES = ES;
    }

    public void setDE(ArrayList<String> DE) {
        this.DE = DE;
    }

    public void setFR(ArrayList<String> FR) {
        this.FR = FR;
    }

    public void setEN(ArrayList<String> EN) {
        this.EN = EN;
    }
    
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
