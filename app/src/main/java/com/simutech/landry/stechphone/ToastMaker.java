package com.simutech.landry.stechphone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.widget.Toast;

/**
 * Created by User on 12/07/2016.
 */
public class ToastMaker {
    String text;
    int color;
    Activity a;

    public ToastMaker(Activity a,String t, int color){
        this.color = color;
        this.text = t;
        this.a = a;
    }
    public void createone(){
        Toast toast = Toast.makeText(a, String.format("  "+text+"  "), Toast.LENGTH_SHORT);
        toast.getView().setBackgroundColor(color);
        toast.show();
    }
    public void createtwo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setMessage(text)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).setTitle("Erreur");
        AlertDialog alert = builder.create();
        //
        alert.show();
    }
}
