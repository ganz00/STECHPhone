package com.simutech.landry.stechphone;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by User on 11/07/2016.
 */
public class Writter {
File myFile;
    File myDir;

    public Writter() {

    }
    public void WriteSettings(String valeurs, String mode, String date,String nu,String operateur ) {

        this.myFile = new File(Environment.getExternalStorageDirectory() +
                File.separator + "donneesMesures"+File.separator +nu+" "+operateur+" "+mode + "_" + date + ".txt"); //on déclare notre futur fichier
        this.myDir = new File(Environment.getExternalStorageDirectory() + File.separator + "donneesMesures"); //pour créer le repertoire dans lequel on va mettre notre fichier
        Boolean success = true;
        if (!myDir.exists()) {
            success = myDir.mkdir(); //On crée le répertoire (s'il n'existe pas!!)
        }
        if (success) {
            try {
                FileOutputStream output = new FileOutputStream(myFile, true);
                output.write(valeurs.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("TEST1", "ERROR DE CREATION DE DOSSIER");
        }

    }
    public void WriteSettings(String valeurs) {

        Boolean success = true;
        if (!this.myDir.exists()) {
            success = this.myDir.mkdir(); //On crée le répertoire (s'il n'existe pas!!)
        }
        if (success) {
            try {
                FileOutputStream output = new FileOutputStream(myFile, true);
                output.write(valeurs.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("TEST1", "ERROR DE CREATION DE DOSSIER");
        }

    }

}
