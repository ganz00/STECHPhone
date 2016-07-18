package com.simutech.landry.stechphone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends Activity implements LocationListener {
    public TextView statut;
    public TextView position;
    public NumberPicker source;
    public Button btnstart;
    public Button btnStop;
    public Button change;
    public Button btnpause;
    public Button btnsend;
    public RelativeLayout rela;
    Calendar c;
    private LocationManager lManager;
    private Location[] locationT = new Location[3];
    private Location location;
    private static Context mContext;
    int i = 0;
    int demarer = 0;
    TelephonyManager TelephonManager;
    String text1 = "";
    String text = "";
    String text2 = "";
    String text3 = "";
    String mode;
    int signalcdma = 0;
    int signalgsm = 0;
    int Fsignalcdma = 0;
    int Fsignalgsm = 0;
    int Fsignallte = 0;
    int signallte = 0;
    int a = 0;
    String nu = "";
    int num =0;
    long duree;
    long debut;
    boolean pause = false;
    public String[] sources;
    Thread start ;
    boolean continu = false;
    Writter w = new Writter();
    boolean lancer = false;
    boolean valid = true;
    boolean dual = true;
    SubscriptionManager Sm;
    SubscriptionInfo operateur;
    TelephonyInfo t;
    MyLocation Ml;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        t = TelephonyInfo.getInstance(mContext);
        Sm = SubscriptionManager.from(mContext);
        operateur = Sm.getActiveSubscriptionInfoForSimSlotIndex(0);
        rela = (RelativeLayout) findViewById(R.id.relay);
        statut = (TextView) findViewById(R.id.Ttime);
        position = (TextView)findViewById(R.id.pos);
        btnstart = (Button) findViewById(R.id.btnstart);
        btnstart.setOnClickListener(startClickListener);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(stopClickListener);
        change = (Button) findViewById(R.id.btnChange);
        change.setOnClickListener(changeClickListener);
        btnpause = (Button) findViewById(R.id.btnpause);
        btnpause.setOnClickListener(pauseClickListener);
        btnsend = (Button) findViewById(R.id.btnsend);
        btnsend.setOnClickListener(sendClickListener);
        btnpause.setEnabled(false);
        btnsend.setEnabled(false);
        TelephonManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        btnStop.setEnabled(false);
        Ml = new MyLocation(lManager,MainActivity.this,btnpause);




        position.setText("");

    }


    View.OnClickListener startClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            mode = t.getNetworkClass(mContext);
            demarer = 1;
            List<String> providers = lManager.getProviders(true);
            if(providers.size()< 2){
                ToastMaker to = new ToastMaker(MainActivity.this,"verifier la disponibilité de la localisation",Color.RED);
                to.createtwo();
                return;
            }

            sources = Ml.obtenirPosition();
            pause = false;
            if(continu == false) {
                continu = true;
                start = new Monthread();
                start.start();
            }
            btnstart.setEnabled(false);
            btnStop.setEnabled(true);
            btnpause.setEnabled(true);
            ToastMaker to = new ToastMaker(MainActivity.this,"debut enregistrement",Color.GREEN);
            to.createone();
            statut.setBackgroundColor(Color.GREEN);
        }
    };
    View.OnClickListener stopClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            a = 2;
            continu = false;
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i=0;
            num++;
            nu = num+"_";
            btnstart.setEnabled(true);
            btnpause.setEnabled(false);
            btnsend.setEnabled(true);
            btnStop.setEnabled(false);
            ToastMaker to = new ToastMaker(MainActivity.this,"Fin enregistrement",Color.RED);
            to.createone();
            statut.setBackgroundColor(Color.WHITE);
        }
    };
    View.OnClickListener changeClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            Intent in = new Intent(Intent.ACTION_MAIN);
            in.setClassName("com.android.settings", "com.android.settings.TestingSettings");
            startActivity(in);
        }
    };
    View.OnClickListener pauseClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            a=1 ;
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pause = true;
            btnstart.setEnabled(true);
            btnpause.setEnabled(false);
            ToastMaker to = new ToastMaker(MainActivity.this,"intéruption enregistrement",Color.BLUE);
            to.createone();
            statut.setBackgroundColor(Color.GRAY);

        }
    };
    View.OnClickListener sendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    class StateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            c = Calendar.getInstance();
            long debut = c.getTimeInMillis();
            super.onSignalStrengthsChanged(signalStrength);
        }

    }

    private String format(int signal) {
        //Time today = new Time(Time.getCurrentTimezone());
        //today.setToNow();
        Date d = new Date();
        DateFormat df = new DateFormat();
       String heure = (String) DateFormat.format("hh:mm:ss",d);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return "TODO";
        }
        for(int i=0;i<sources.length;i++)
            locationT[i] =  lManager.getLastKnownLocation(sources[i]);
       for(int i = 2;i >= 0; i--){
           if(locationT[i]!=null) {
               location = locationT[i];
                break;
           }

       }
        if(location != null)
        return    signal + "," + heure + "," +location.getLatitude()+ "," +location.getLongitude()+ "\n";
        else
            return  null;
    }


    private int add(int N, int a) {
        if (a == 0) {
            a = N;
        } else {
            if (N > a && N < 50)
                a = N;
        }
        return a;
    }

    private void renew() {
        signalcdma = 0;
        signalgsm = 0;
        Fsignalcdma = 0;
        Fsignalgsm = 0;
        Fsignallte = 0;
        signallte = 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void ReadcellInfo() {
        if (TelephonManager.getAllCellInfo() != null) {
            List<CellInfo> list = TelephonManager.getAllCellInfo();
            for (CellInfo cell : list) {

                String name = "";
                String type = "";
                String signal = "";
                String registered = "";
                int level = 0;
                String id = "";
                int dbmValue = 0;

                if (cell instanceof CellInfoLte) {

                    CellInfoLte cellLTE = (CellInfoLte) cell;
                    type = "LTE";
                    name = cellLTE.getCellIdentity().getMcc() + " "
                            + cellLTE.getCellIdentity().getMnc();
                    signal = cellLTE.getCellSignalStrength().getDbm() + " dbm";
                    level = cellLTE.getCellSignalStrength().getLevel();
                    id = "id: " + cellLTE.getCellIdentity().getPci();
                    dbmValue = cellLTE.getCellSignalStrength().getDbm();
                    signallte = add(dbmValue, signallte);


                } else if (cell instanceof CellInfoWcdma) {
                    CellInfoWcdma cellWCDMA = (CellInfoWcdma) cell;
                    if (cellWCDMA.getCellSignalStrength().getAsuLevel() < 32) {
                        type = "WCDMA";
                        name = cellWCDMA.getCellIdentity().getMcc() + " "
                                + cellWCDMA.getCellIdentity().getMnc();
                        signal = cellWCDMA.getCellSignalStrength().getDbm()
                                + " dbm";
                        level = cellWCDMA.getCellSignalStrength().getLevel();
                        id = "id: " + cellWCDMA.getCellIdentity().getCid();
                        dbmValue = cellWCDMA.getCellSignalStrength().getDbm();
                        signalcdma = add(dbmValue, signalcdma);
                    }else
                        valid = false;


                } else if (cell instanceof CellInfoGsm) {
                    CellInfoGsm cellGSM = (CellInfoGsm) cell;
                    if (cellGSM.getCellSignalStrength().getAsuLevel() < 32) {
                        type = "GSM";
                        name = cellGSM.getCellIdentity().getMcc() + " "
                                + cellGSM.getCellIdentity().getMnc();
                        signal = cellGSM.getCellSignalStrength().getDbm() + " dbm";
                        level = cellGSM.getCellSignalStrength().getLevel();
                        id = "id: " + cellGSM.getCellIdentity().getCid();
                        dbmValue = cellGSM.getCellSignalStrength().getDbm();
                        String text3 = type + " " + dbmValue;
                        signalgsm = add(dbmValue, signalgsm);
                    }else
                        valid = false;
                } else{
                    valid = false;
                }

            }


        }

    }


    public class MultiSimListener extends PhoneStateListener {

        private Field subIdField;
        private long subId = -1;

        public MultiSimListener (long subId) {
            super();
            try {
                // Get the protected field mSubId of PhoneStateListener and set it
                subIdField = this.getClass().getSuperclass().getDeclaredField("mSubId");
                subIdField.setAccessible(true);
                subIdField.set(this, subId);
                this.subId = subId;
            } catch (NoSuchFieldException e) {

            } catch (IllegalAccessException e) {

            } catch (IllegalArgumentException e) {

            }
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            // Handle the event here, subId indicates the subscription id if > 0
        }

    }

    private void obtenirPosition() {
        List<String> providers = lManager.getProviders(true);
         sources = new String[providers.size()];
        int j = 0;
        for (String provider : providers)
            sources[j++] = provider;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        for (int i=1 ;i<providers.size();i++)
            lManager.requestLocationUpdates(sources[i], 200, 0, this);
    }

    public void onLocationChanged(Location location) {
        if(location.getProvider().toLowerCase().equals("passive")) {
            this.locationT[0] = location;
        }
        if(location.getProvider().toLowerCase().equals("gps")) {
            this.locationT[1] = location;
        }
        if(location.getProvider().toLowerCase().equals("network")) {
            this.locationT[2] = location;

        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        ToastMaker to = new ToastMaker(MainActivity.this,"La source "+s+" a été désactivé, données compromise veillez verifier vos paramètre",Color.RED);
        to.createone();
        btnpause.callOnClick();
    }

    public class Monthread extends Thread {
        public Monthread() {
        }
        @Override
        public void run() {
            while (continu == true){
                if (a == 0 || a == 2) {
                    c = Calendar.getInstance();
                    String date = c.get(Calendar.DATE) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.YEAR);
                    w.WriteSettings(" mesure "+operateur.getCarrierName()+" "+ mode + " " + date + "\n", mode, date, nu,(String)operateur.getCarrierName());
                    a=0;
                } else {
                    if (a == 1 && pause == true) {
                        while (pause == true){
                            try {
                                Thread.sleep(400);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        a = 0;
                    }
                }

            while (a == 0) {
                c = Calendar.getInstance();
                debut = c.getTimeInMillis();
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ReadcellInfo();
                if (mode == "4G") {
                    prewrite(signallte);
                }else {
                    if (mode == "3G") {
                      prewrite(signalcdma);
                    }else {
                        if (mode == "2G") {
                           prewrite(signalgsm);
                        }
                    }
                }
                i++;
                c = Calendar.getInstance();
                duree = c.getTimeInMillis() - debut;
                renew();
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (i==0)
                            statut.setText( ""+i );
                        else
                            statut.setText(  i + "  en " + duree + "ms\n");
                    }
                });
            }
        }

    }
    }
    public void prewrite(int signal){

        if(valid){
            text = format(signal);
        }

        else{
            text = format(0);
        }
        if (text == null) {
            Date d = new Date();
            DateFormat df = new DateFormat();
            final String heure = (String) DateFormat.format("hh:mm:ss",d);
            try {
                Thread.currentThread().sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            text = signal+" "+ mode + " erreur geolocalisation "+heure;
            runOnUiThread(new Runnable() {
                public void run() {
                    ToastMaker to = new ToastMaker(MainActivity.this,"Attention erreur geolocalisation veillez relancer" +
                            " l'application et verifier les paramètres",Color.RED);
                    to.createone();
                    statut.setBackgroundColor(Color.RED);
                }
            });
        }
        else {
            runOnUiThread(new Runnable() {
                public void run() {
                    if(continu==true && pause ==false)
                        statut.setBackgroundColor(Color.GREEN);
                }
            });
        }

        w.WriteSettings(text);
    }

}


