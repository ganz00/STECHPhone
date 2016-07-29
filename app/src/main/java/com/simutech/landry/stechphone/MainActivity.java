package com.simutech.landry.stechphone;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends Activity implements LocationListener {
    public static final String VOITURE_CLE = "voiture";
    public TextView statut;
    public TextView dbm1;
    public TextView dbm2;
    public TextView info;
    public ListView Select;
    public NumberPicker source;
    public Button btnstart;
    public Button btnStop;
    public Button change;
    public Button btnEgps;
    public Button btnEmes;
    public Button btnEautre;
    public Button btnpause;
    public Button btnsend;
    public RadioButton r4g;
    public RadioButton r3g;
    public RadioButton r2g;
    public RelativeLayout rela;
    public Calendar c;
    public int voiture=0;
    static List<File> listM = new ArrayList<File>();
    static List<File> listE = new ArrayList<File>();
    private LocationManager lManager;
    private Location location;
    private Location location2;
    private static Context mContext;
    public TelephonyManager TelephonManager;
    public String[] operateur = new String[2];
    public  String[] text = new String[2];
    public String[] mode = new String[2];
    public String nu = "";
    public int num = 0;
    public long duree,debut;
    public String Ldate = "";
    public String Heure = "";
    boolean pause = false;
    public String[] sources;
    Thread start;
    Thread majheure;
    Thread coulerT;
    boolean continu = false;
    boolean colorchanged = false;
    Writter wm;
    Writter wE;
    boolean dual = true;
    SubscriptionManager Sm;
    String Tdbm1;
    String Tdbm2;
    int idsim1;
    int idsim2;
    double[] lat = new double[2];
    double[] lon = new double[2];
    String type = "";
    boolean valid = true,canstart = true;
    int couleurfond = 1; // 1 vert -1 rouge 2 gris 0 blanc
    int i = 0;
    int nbpos = 0;
    int demarer = 0;
    int signalcdma = 0;
    int signalgsm = 0;
    int signallte = 0;
    int a = 0;
    Handler handler;
    Long timemaj;
    Calendar cal;
    int[] color;
    boolean[] colorb;
    MultiSimListener muti;
    Erreur er;
    Button[] but;
    public SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        Sm = SubscriptionManager.from(mContext);

        mode[1] = "2G";
        mode[0] = "";
        color = new int[]{1, 1, 1};

        colorb = new boolean[]{false, false, false};
        rela = (RelativeLayout) findViewById(R.id.relay);
        statut = (TextView) findViewById(R.id.Ttime);
        dbm1 = (TextView) findViewById(R.id.dbm1);
        dbm2 = (TextView) findViewById(R.id.dbm2);
        info = (TextView) findViewById(R.id.info);
        r4g = (RadioButton) findViewById(R.id.radio2G);
        r3g = (RadioButton) findViewById(R.id.radio3G);
        r2g = (RadioButton) findViewById(R.id.radio4G);
        btnEgps = (Button) findViewById(R.id.Gps);
        btnEmes = (Button) findViewById(R.id.mesure);
        btnEautre = (Button) findViewById(R.id.autre);
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
        but = new Button[]{btnEgps, btnEmes, btnEautre};
        btnpause.setEnabled(false);
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        btnStop.setEnabled(false);
        Date d = new Date();
        DateFormat df = new DateFormat();
        Heure = (String) DateFormat.format("HH-mm-ss", d);
        Ldate = (String) DateFormat.format("dd-MM-yyyy", d);
        er = new Erreur(but);
        final Button[] but = new Button[]{btnEgps, btnEmes, btnEautre};
        wE = new Writter("ErreurAppli", 1, Heure, er, handler, mContext,"");
        try {
            TelephonManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            List<SubscriptionInfo> list = Sm.getActiveSubscriptionInfoList();
            if (list.size() == 2) {
                wm = new Writter("Fichiersmesure", 2, Heure, er, handler, mContext, Ldate);
                operateur[0] = (String) Sm.getActiveSubscriptionInfoForSimSlotIndex(0).getCarrierName();
                operateur[1] = (String) Sm.getActiveSubscriptionInfoForSimSlotIndex(1).getCarrierName();
                idsim1 = list.get(0).getSubscriptionId();
                idsim2 = list.get(1).getSubscriptionId();
                muti = new MultiSimListener(idsim2);
            } else {
                wm = new Writter("Fichiersmesure", 1, Heure, er, handler, mContext, Ldate);
                operateur[0] = (String) Sm.getActiveSubscriptionInfoForSimSlotIndex(0).getCarrierName();
                dual = false;
            }
        }catch (Exception e){
            ToastMaker to = new ToastMaker(MainActivity.this, "Redemarer l'application et Verifier que le telephone est bien connecté a un reseau", Color.RED);
            to.createtwo();
            canstart = false;
        }
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle b = msg.getData();
                Integer key = b.getInt("KEY");
                Integer value = b.getInt("value");
                if (key == 3) {
                    switch (value) {
                        case 1:
                            statut.setBackgroundColor(Color.GREEN);

                            break;
                        case -1:
                            statut.setBackgroundColor(Color.RED);
                            break;
                    }
                } else {
                    switch (value) {
                        case 1:
                            but[key].setEnabled(true);
                            break;
                        case -1:
                            but[key].setEnabled(false);
                            break;
                    }

                }
                if(key == 7) sauvegarde();
                if(key == 8) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(VOITURE_CLE, value);
                    editor.commit();
                }
            }

        };
         if( sharedPref.getInt(VOITURE_CLE , 0) == 0){
             ToastMaker to = new ToastMaker(MainActivity.this, "vous avez qu'elle voiture ?", Color.GRAY,handler);
             to.showDialogtwo();
         }else{
             voiture = sharedPref.getInt(VOITURE_CLE , 0);
         }


    }



    private void sauvegarde() {
        if(!ToastMaker.save){
            return;
        }
        new Thread()
        {
            public void run() {
                final  boolean  reussi = sendFTP(voiture);
                runOnUiThread(new Runnable() {
                    public void run() {
                        String message;
                        int colori;
                        if(reussi) {
                            message = "Envoie reussi";
                            colori = Color.GREEN;
                        }else{
                            message = "Echec envoie ";
                            colori = Color.RED;
                        }
                        ToastMaker to = new ToastMaker(MainActivity.this ,message, colori);
                        to.createone();
                    }
                });

            }
        }.start();
    }


    View.OnClickListener startClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            if(!canstart)
                return;
            if (!(wm.success && wE.success)) {
                ToastMaker to = new ToastMaker(MainActivity.this, "Impossible d'enregistrer verifier la mémoire du télephone", Color.RED);
                to.createtwo();
                return;
            }
            if (dual) {
                TelephonManager.listen(muti,
                        PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                                | PhoneStateListener.LISTEN_SERVICE_STATE);
            }
            demarer = 1;
            List<String> providers = lManager.getProviders(true);
            if (providers.size() < 2) {
                ToastMaker to = new ToastMaker(MainActivity.this, "verifier la disponibilité de la localisation", Color.RED);
                to.createtwo();
                return;
            }
            if (mode[0].length() < 2) {
                ToastMaker to = new ToastMaker(MainActivity.this, "choisir la technologie avant de commencer", Color.RED);
                to.createtwo();
                return;
            }
            for (Button b : but) {
                b.setEnabled(true);
            }
            r3g.setClickable(false);
            r4g.setClickable(false);
            r2g.setClickable(false);
            pause = false;
            if (!continu) {
                continu = true;
                er = new Erreur(but);
                obtenirPosition();
                majheure = new Heurethread();
                majheure.start();
                start = new Monthread();
                start.start();
                er.started = false;
            }
            er.cont = true;
            btnstart.setEnabled(false);
            btnStop.setEnabled(true);
            btnpause.setEnabled(true);
            ToastMaker to = new ToastMaker(MainActivity.this, "debut enregistrement", Color.GREEN);
            to.createone();
            couleurfond = 1;
            statut.setBackgroundColor(Color.GREEN);

        }
    };
    View.OnClickListener stopClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            a = 2;
            continu = false;
            er.cont = false;
            er.play = false;
            er.color[3] = 3;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i = 0;
            num++;
            nu = num + "_";
            btnstart.setEnabled(true);
            btnpause.setEnabled(false);
            btnsend.setEnabled(true);
            btnStop.setEnabled(false);
            ToastMaker to = new ToastMaker(MainActivity.this, "Fin enregistrement", Color.RED);
            to.createone();
            couleurfond = 0;
            info.setText("dernier enregistrement a " + Heure);
            Erreur.save(wE, Heure, Ldate);
            scan();
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
            a = 1;
            try {
                Thread.sleep(900);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pause = true;
            btnstart.setEnabled(true);
            btnpause.setEnabled(false);
            ToastMaker to = new ToastMaker(MainActivity.this, "interuption enregistrement", Color.BLUE);
            to.createone();
            er.cont = false;
            er.color[3] = 2;
            statut.setBackgroundColor(Color.GRAY);


        }
    };
    View.OnClickListener sendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ToastMaker to = new ToastMaker(MainActivity.this, "confirmer l'envoie", Color.GRAY,handler);
            to.showDialogone();
        }
    };

    private String format(int signal, int idsim) {
        if (location != null) {
            lat[0] = location.getLatitude();
            lon[0] = location.getLongitude();
        } else {
            lat[idsim] = 0;
            lon[idsim] = 0;
        }
        if (signal >= -150 && signal < 0)
            return signal + ";" + Heure + ";" + lat[0] + " ; " + lon[0] + "\n";
        else
            return -150 + ";" + Heure + ";" + lat[0] + " ; " + lon[0] + "\n";

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void ReadcellInfo() {
        boolean valid = false;
        if (TelephonManager.getAllCellInfo() != null) {
            List<CellInfo> list = TelephonManager.getAllCellInfo();
            for (CellInfo cell : list) {
                int dbmValue;
                if (cell.isRegistered()) {
                    if (cell instanceof CellInfoLte) {
                        type = "4G";
                        if (mode[0].equals("4G")) {
                            CellInfoLte cellLTE = (CellInfoLte) cell;
                            dbmValue = cellLTE.getCellSignalStrength().getDbm();
                            signallte = dbmValue;
                            valid = true;
                            return;
                        }
                    }
                    if (cell instanceof CellInfoWcdma) {
                        type = "3G";
                        if (mode[0].equals("3G")) {
                            CellInfoWcdma cellWCDMA = (CellInfoWcdma) cell;
                            dbmValue = cellWCDMA.getCellSignalStrength().getDbm();
                            signalcdma = dbmValue;
                            valid = true;
                            return;

                        }
                    }
                    if (cell instanceof CellInfoGsm) {
                        type = "2G";
                        if (mode[0].equals("2G")) {
                            CellInfoGsm cellGSM = (CellInfoGsm) cell;
                            dbmValue = cellGSM.getCellSignalStrength().getDbm();
                            signalgsm = dbmValue;
                            valid = true;
                            return;
                        }
                    }

                }
            }
        }
        if (valid) {
            er.NewErreur(Heure, Erreur.ERREUR_MESURE, "Sim 1" + mode[0] + " " + operateur[0] + " impossible de recuperer les données des antennes ", 1, handler);
        }
    }


    public class MultiSimListener extends PhoneStateListener {

        private Field subIdField;
        private int subId = -1;
        String service = "";

        public MultiSimListener(int subId) {
            super();
            try {
                // Get the protected field mSubId of PhoneStateListener and set it
                subIdField = this.getClass().getSuperclass().getDeclaredField("mSubId");
                subIdField.setAccessible(true);
                subIdField.set(this, subId);
                this.subId = subId;
            } catch (NoSuchFieldException e) {
                er.NewErreur(Heure, Erreur.ERREUR_AUTRE, "Sim 2" + mode[1] + " " + operateur[1] + " acces dual sim impossible ", 2, handler);
                btnpause.callOnClick();
                e.printStackTrace();

            } catch (IllegalAccessException e) {
                er.NewErreur(Heure, Erreur.ERREUR_AUTRE, "Sim 2" + mode[1] + " " + operateur[1] + "access dual sim impossible", 3, handler);
                btnpause.callOnClick();
                e.printStackTrace();

            } catch (IllegalArgumentException e) {
                er.NewErreur(Heure, Erreur.ERREUR_AUTRE, "Sim 2" + mode[1] + " " + operateur[1] + "acces dual sim impossible ", 4, handler);
                btnpause.callOnClick();
                e.printStackTrace();

            }
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {

            if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                    (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                // TODO: Consider calling
                er.NewErreur(Heure, Erreur.ERREUR_MESURE, "Sim 2" + mode[1] + " " + operateur[1] + "problème  autorisation signal ", 5, handler);
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            int pre = -113 + 2 * signalStrength.getGsmSignalStrength();
            if (pre == 85) {
                pre = -150;
            }
            signalgsm = pre;
        }

        public void onServiceStateChanged(ServiceState serviceState) {
            this.service = serviceState.getOperatorAlphaLong();
            super.onServiceStateChanged(serviceState);

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
            er.NewErreur(Heure, Erreur.ERREUR_GPS, "autorisation gps refusée", 6, handler);
            return;
        }

        lManager.requestLocationUpdates("gps", 100, 0, this);
        location = lManager.getLastKnownLocation("gps");
        if (location == null) {
            er.NewErreur(Heure, Erreur.ERREUR_GPS, "dernière position gps inconnu ", 7, handler);
        }
    }

    public void onLocationChanged(Location llocation) {
        if (llocation.getProvider().equals("gps")) {
            this.location = llocation;
            cal = new GregorianCalendar();
            timemaj = cal.getTimeInMillis();
            nbpos++;
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
        ToastMaker to = new ToastMaker(MainActivity.this, "La source " + s + " a été désactivé, données compromise veillez verifier vos paramètre", Color.RED);
        to.createone();
        btnpause.callOnClick();
    }

    public class Monthread extends Thread {
        public Monthread() {
        }

        @Override
        public void run() {
            while (continu) {
                if (a == 0 || a == 2) {
                    c = Calendar.getInstance();
                    wm.WriteSettings(" mesure " + operateur[0] + " " + mode[0] + " " + Ldate + " " + Heure + "\n", mode[0], Ldate, nu, operateur[0], 0, Heure);
                    if (dual)
                        wm.WriteSettings(" mesure " + operateur[1] + " " + mode[1] + " " + Ldate + " " + Heure + "\n", mode[1], Ldate, nu, operateur[1], 1, Heure);
                    a = 0;
                } else {
                    if (a == 1 && pause) {
                        while (pause) {
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
                    if (mode[0].equals("4G") && type.equals("4G")) {
                        Thread t1 = new savethread(signallte, 0);
                        t1.start();
                        Tdbm1 = operateur[0] + " " + signallte + " dbm";
                        if (dual) {
                            Thread t2 = new savethread(signalgsm, 1);
                            t2.start();
                            Tdbm2 = operateur[1] + " " + signalgsm + " dbm";
                        }
                    } else {
                        if (mode[0].equals("3G") && type.equals("3G")) {
                            Thread t3 = new savethread(signalcdma, 0);
                            t3.start();
                            Tdbm1 = operateur[0] + " " + signalcdma + " dbm";
                            if (dual) {
                                Thread t4 = new savethread(signalgsm, 1);
                                t4.start();
                                Tdbm2 = operateur[1] + " " + signalgsm + " dbm";
                            }
                        } else {
                            if (mode[0].equals("2G") && type.equals("2G")) {
                                Thread t5 = new savethread(signalgsm, 0);
                                t5.start();
                                Tdbm1 = operateur[0] + " " + signalgsm + " dbm";
                                if (dual) {
                                    Thread t6 = new savethread(signalgsm, 1);
                                    t6.start();
                                    Tdbm2 = operateur[1] + " " + signalgsm + " dbm";
                                }
                            } else {
                                Thread t7 = new savethread(-150, 0);
                                t7.start();
                                Tdbm1 = operateur[0] + " " + -150 + " dbm";
                                er.NewErreur(Heure, Erreur.ERREUR_MESURE, "Type Reseau inconnu ", 9, handler);
                                if (dual) {
                                    Thread t8 = new savethread(signalgsm, 1);
                                    t8.start();
                                    Tdbm2 = operateur[1] + " " + signalgsm + " dbm";

                                }
                            }
                        }
                    }
                    i++;
                    cal = Calendar.getInstance();
                    if (nbpos > 2 && cal.getTimeInMillis() - timemaj > 1000) {
                        er.NewErreur(Heure, Erreur.ERREUR_GPS, "problème actualisation corrdonées gps ", 8, handler);
                        nbpos = i;
                    }
                    c = Calendar.getInstance();
                    duree = c.getTimeInMillis() - debut;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (i == 0)
                                statut.setText("" + i);
                            else {
                                statut.setText(i + "");
                                dbm1.setText(Tdbm1);
                                dbm2.setText(Tdbm2);
                            }
                        }
                    });
                }
            }

        }
    }

    public void prewrite(int signal, int idsim) {
        if (idsim == 0) {
            text[idsim] = format(signal, idsim);
        } else {
            text[idsim] = format(signal, idsim);
        }
        if (lat[0] == 0) {
            text[idsim] = signal + " " + mode[idsim] +" "+ Heure + " erreur geolocalisation\n ";
            runOnUiThread(new Runnable() {
                public void run() {
                    ToastMaker to = new ToastMaker(MainActivity.this, "Attention erreur geolocalisation veillez relancer" +
                            " l'application ou les paramètres", Color.RED);
                    to.createone();
                }
            });
        }
        wm.WriteSettings(text[idsim], idsim, Heure);
    }

    public String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                er.NewErreur(Heure, Erreur.ERREUR_MESURE, "Type de reseau inconnu", 9, handler);
                return "reseau sim 1 inconnu";
        }
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio4G:
                if (checked)
                    mode[0] = "4G";
                break;
            case R.id.radio3G:
                if (checked)
                    mode[0] = "3G";
                break;
            default:
                mode[0] = "2G";
                break;
        }

    }

    public class savethread extends Thread {
        int signal;
        int a;

        public savethread(int signal, int id) {
            this.signal = signal;
            this.a = id;
        }

        public void run() {
            prewrite(this.signal, this.a);
        }
    }

    public class Heurethread extends Thread {

        public Heurethread() {
        }

        public void run() {
            while (continu) {
                Date d = new Date();
                DateFormat df = new DateFormat();
                Heure = (String) DateFormat.format("HH-mm-ss", d);
                Ldate = (String) DateFormat.format("dd-MM-yyyy", d);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    er.NewErreur(Heure, Erreur.ERREUR_AUTRE, "Erreur Systeme THREAD ZOMBI", 10, handler);
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean  sendFTP(int v) {
        String USER;
        try {
            Listfile();
            SimpleFTP ftp = new SimpleFTP();
            if(v == 1){
                 USER = "clio";
            } else {
                USER = "scenic";
            }
            // Connect to an FTP server on port 21.
            ftp.connect("ftp.simutech-uae.fr", 21,"simutech", "simutech92");
            // Set binary mode.
            ftp.bin();
            ftp.cwd("Cartolille");
            // Change to a new working directory on the FTP server.
            String nom =USER;
            String DIR = nom.toUpperCase()+" "+wm.dir;
            ftp.mkd(DIR);
            ftp.cwd(DIR);
            Boolean state3 = false;
                for(File f : listM){
                   state3 = ftp.stor(f);
            }
            // Quit from the FTP server.
            ftp.disconnect();
            return true && state3;
        }
        catch (IOException e) {
            String eror = e.toString();
            return false;
        }

    }
    public void Listfile(){
        File f2 = wm.myDir;
        File f1 = wE.myDir;

        File[] fichiers = f2.listFiles();
        File[] fichiers1 = f1.listFiles();
        int k= 0;
        listM.clear();
        listE.clear();
        // Si le répertoire n'est pas vide...
        if(fichiers != null)
            // On les ajoute à  l'adaptateur
            for(File f : fichiers) {
                listM.add(k, f);
                k++;
            }
        k=0;
        for(File f : fichiers1) {
            listE.add(k, f);
            k++;
        }
    }
    public void scan(){
        Listfile();
        MediaScannerConnection.scanFile(mContext, new String[]{wm.cartolille.getAbsolutePath()}, null, null);
        for(File f : listM){
            MediaScannerConnection.scanFile(mContext, new String[]{wm.myDir.toString(), f.toString()}, null, null);
        }
        for(File f : listE){
            MediaScannerConnection.scanFile(mContext, new String[]{wE.myDir.toString(), f.toString()}, null, null);
        }
    }
}


