package com.simutech.landry.stechphone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.widget.Button;


/**
 * Created by Landry Kateu on 21/07/2016.
 */
public class Erreur {
    public final static  String ERREUR_GPS = "ERREUR_GPS";
    public final static String ERREUR_MESURE = "ERREUR_MESURE";
    public final static String ERREUR_AUTRE = "ERREUR_AUTRE";
    public static int[] vind;
    private String type;
    private String heure;
    private String libelle;
    private int duree=0;
    Handler handler;
    Button[] b;
    public int[] color;
    int[] Dureec;
    int id;
    int changed;
    boolean cont ;
    boolean play = true;
    Calendar c;
    long[] debut;
    Thread T;
    boolean started = false;

    public static List<Erreur> list;

    public String getType() {
        return type;
    }
    public Erreur(Button[] b){
        this.b = b;
        vind = new int[]{0,0,0};
        Erreur.list = new ArrayList<Erreur>();
        color = new int[]{1,1,1,1};
        Dureec = new int[]{0,0,0};
        debut = new long[3];
        cont = false;
        T=new refresh();
    }
    public void NewErreur(String heure, String type, String libelle, int id, Handler handler) {
        this.heure = heure;
        this.type = type;
        this.libelle = libelle;
        this.id =id;
        this.handler = handler;
        this.add(id);

    }

    public void setType(String type) {
        this.type = type;
    }
    public void add(int id) {
        boolean pres = false;
        int pos = 0;
        for (int i = 0; i < Erreur.list.size(); i++) {
            if (Erreur.list.get(i).id == id) {
                pres = true;
                Erreur.list.get(pos).setDuree(list.get(pos).getDuree() + 1);
            }
            pos++;
        }
        if (!pres)
            Erreur.list.add(this);
        c = Calendar.getInstance();
        switch (this.getType()) {

            case ERREUR_AUTRE:
                vind[2]++;
                debut[2] = c.getTimeInMillis();
                tooglecolor(2,-1);
                break;
            case ERREUR_GPS:
                vind[0]++;
                tooglecolor(0,-1);
                debut[0] = c.getTimeInMillis();
                break;
            case ERREUR_MESURE:
                vind[1]++;
                tooglecolor(1,-1);
                debut[1] = c.getTimeInMillis();
                break;
        }
        if(!started) {
            T.start();
            started = true;
        }

    }
        public void tooglecolor(int i,int val){
            if(color[i] != val){
                color[i] = val;
                Message myMessage = handler.obtainMessage();
                Bundle messageBundle=new Bundle();
                messageBundle.putInt("KEY", i);
                messageBundle.putInt("value", val);
                myMessage.setData(messageBundle);
                handler.sendMessage(myMessage);
            }
        }



    @Override
    public String toString() {
        return "Erreur[" +
                ", heure='" + heure + '\'' +
                ", libelle='" + libelle + '\'' +
                ", duree=" + duree +
                ']'+"\n";
    }

    public String getHeure() {
        return heure;
    }
    public static void save(Writter w,String heure,String date){
        if(Erreur.list != null)
        for (Erreur e : Erreur.list) {
            w.WriteSettings(e.toString(),heure,date);
        }
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

    public void setB(Button[] b) {
        this.b = b;
    }

    public class refresh extends Thread {

        public refresh(){
        }
        public void run() {
            while(play) {
                if (cont) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    c = Calendar.getInstance();
                    if ((c.getTimeInMillis() - debut[0]) > 1500) {
                        tooglecolor(0, 1);
                        Dureec[0] = 0;
                    } else
                        Dureec[0]++;

                    if ((c.getTimeInMillis() - debut[1]) > 1500) {
                        tooglecolor(1, 1);
                        Dureec[1] = 0;
                    } else
                        Dureec[1]++;

                    if ((c.getTimeInMillis() - debut[2]) > 1500) {
                        tooglecolor(2, 1);
                        Dureec[2] = 0;
                    } else
                        Dureec[2]++;

                    if (Dureec[0] > 20 || Dureec[1] > 20 ||Dureec[2] > 20)
                        tooglecolor(3, -1);
                    else
                        tooglecolor(3, 1);
                }else{
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

}
