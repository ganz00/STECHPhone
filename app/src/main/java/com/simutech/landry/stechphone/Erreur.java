package com.simutech.landry.stechphone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Landry Kateu on 21/07/2016.
 */
public class Erreur {
    public static int ERREUR_GPS = 1;
    public static int ERREUR_MESURE = 2;
    public static int ERREUR_AUTRE = 3;
    private String type;
    private String heure;
    private String libelle;
    private int duree;
    public static List<String> list;

    public String getType() {
        return type;
    }
    public Erreur(String heure, String type, String libelle) {
       list = new ArrayList<String>();
        this.heure = heure;
        this.type = type;
        this.libelle = libelle;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }





}
