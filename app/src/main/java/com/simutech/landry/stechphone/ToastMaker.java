package com.simutech.landry.stechphone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by User on 12/07/2016.
 */
public class ToastMaker {
    String text;
    int color;
    Activity a;
    public static boolean save=false;
    public static boolean over=true;
    Handler h;
    public int voiture=0; // 1 clio 2 scenic

    public ToastMaker(Activity a,String t, int color){
        this.color = color;
        this.text = t;
        this.a = a;

    }
    public ToastMaker(Activity a, String t, int color, Handler h){
        this.color = color;
        this.text = t;
        this.a = a;
        over = false;
        this.h = h;
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
        alert.show();
    }
    public void showDialogone(){
            AlertDialog.Builder builder = new AlertDialog.Builder(a);

           builder.setTitle("confirmation");

            builder.setMessage(text);
            builder.setPositiveButton("oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            save = true;
                            Message myMessage = h.obtainMessage();
                            Bundle messageBundle = new Bundle();
                            messageBundle.putInt("KEY", 7);
                            myMessage.setData(messageBundle);
                            h.sendMessage(myMessage);
                        }
                    }
            );
            builder.setNegativeButton("annuler", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    save = false;
                    Message myMessage = h.obtainMessage();
                    Bundle messageBundle = new Bundle();
                    messageBundle.putInt("KEY", 7);
                    myMessage.setData(messageBundle);
                    h.sendMessage(myMessage);
                }
            });
            builder.show();

    }
    public void showDialogtwo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(a);

        builder.setTitle("confirmation");

        builder.setMessage(text);
        builder.setPositiveButton("clio", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        voiture = 1;

                    }
                }
        );
        builder.setNegativeButton("scenic", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                        voiture = 2;
            }
        });
        builder.show();

    }
}
