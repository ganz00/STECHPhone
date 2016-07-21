package com.simutech.landry.stechphone;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements LocationListener {
    public TextView statut;
    public TextView dbm1;
    public TextView dbm2;
    public TextView info;
    public ListView Select;
    public NumberPicker source;
    public Button btnstart;
    public Button btnStop;
    public Button change;
    public Button btnpause;
    public Button btnsend;
    public RadioButton r4g;
    public RadioButton r3g;
    public RadioButton r2g;
    public RelativeLayout rela;
    Calendar c;
    int bg = 1;
    private LocationManager lManager;
    private Location location;
    private Location location2;
    private static Context mContext;
    int i = 0;
    int demarer = 0;
    TelephonyManager TelephonManager;
    String[] operateur = new String[2];
    String[] text = new String[2];
    String[] mode = new String[2];
    int signalcdma = 0;
    int signalgsm = 0;
    int signallte = 0;
    int a = 0;
    String nu = "";
    int num = 0;
    long duree;
    long debut;
    String Ldate="";
    String Heure ="";
    boolean pause = false;
    public String[] sources;
    Thread start;
    Thread majheure;
    boolean continu = false;
    Writter w = new Writter();
    boolean dual = true;
    SubscriptionManager Sm;
    String Tdbm1;
    String Tdbm2;
    int idsim1;
    int idsim2;
    double[] lat = new double[2];
    double[] lon = new double[2];
    String type = "";

    MultiSimListener muti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        Sm = SubscriptionManager.from(mContext);

        mode[1] = "2G";
        mode[0] = "";
        rela = (RelativeLayout) findViewById(R.id.relay);
        statut = (TextView) findViewById(R.id.Ttime);
        dbm1 = (TextView) findViewById(R.id.dbm1);
        dbm2 = (TextView) findViewById(R.id.dbm2);
        info = (TextView) findViewById(R.id.info) ;
        r4g = (RadioButton) findViewById(R.id.radio2G);
        r3g = (RadioButton) findViewById(R.id.radio3G);
        r2g = (RadioButton) findViewById(R.id.radio4G);
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
        List<SubscriptionInfo> list = Sm.getActiveSubscriptionInfoList();
        if (list.size() == 2) {
            operateur[0] = (String) Sm.getActiveSubscriptionInfoForSimSlotIndex(0).getCarrierName();
            operateur[1] = (String) Sm.getActiveSubscriptionInfoForSimSlotIndex(1).getCarrierName();
            idsim1 = list.get(0).getSubscriptionId();
            idsim2 = list.get(1).getSubscriptionId();
            muti = new MultiSimListener(idsim2);
        } else {
            operateur[0] = (String) Sm.getActiveSubscriptionInfoForSimSlotIndex(0).getCarrierName();
            dual = false;
        }
    }


    View.OnClickListener startClickListener = new View.OnClickListener() {
        public void onClick(final View v) {

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
            r3g.setClickable(false);
            r4g.setClickable(false);
            r2g.setClickable(false);
            obtenirPosition();
            pause = false;
            if (!continu) {
                continu = true;
                majheure = new Heurethread();
                majheure.start();
                start = new Monthread();
                start.start();

            }
            btnstart.setEnabled(false);
            btnStop.setEnabled(true);
            btnpause.setEnabled(true);
            ToastMaker to = new ToastMaker(MainActivity.this, "debut enregistrement", Color.GREEN);
            to.createone();
            statut.setBackgroundColor(Color.GREEN);
        }
    };
    View.OnClickListener stopClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            a = 2;
            continu = false;
            try {
                Thread.sleep(900);
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
            statut.setBackgroundColor(Color.WHITE);
            info.setText("dernier enregistrement a "+Heure);
            bg=3;
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
            statut.setBackgroundColor(Color.GRAY);
            bg = 2;

        }
    };
    View.OnClickListener sendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private String format(int signal, int idsim) {
        boolean valid=false;
        //Time today = new Time(Time.getCurrentTimezone());
        //today.setToNow();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return "TODO";
        }

        if (location != null) {
            lat[0] = location.getLatitude();
            lon[0] = location.getLongitude();
            lat[1] = location2.getLatitude();
            lon[1] = location2.getLongitude();
        } else {
            lat[idsim] = 0;
            lon[idsim] = 0;
        }
        return signal + ";" + Heure + ";" + lat[0] + " ; " + lon[0] + "\n";

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void ReadcellInfo() {
        if (TelephonManager.getAllCellInfo() != null) {
            List<CellInfo> list = TelephonManager.getAllCellInfo();
            String dat = getNetworkClass(mContext);
            for (CellInfo cell : list) {
                int dbmValue;
                if (cell.isRegistered()) {
                    if (cell instanceof CellInfoLte) {
                        type = "4G";
                        if (mode[0].equals("4G")) {
                            CellInfoLte cellLTE = (CellInfoLte) cell;
                            dbmValue = cellLTE.getCellSignalStrength().getDbm();
                            signallte = dbmValue;
                            return;
                        }
                    }
                    if (cell instanceof CellInfoWcdma) {
                        type = "3G";
                        if (mode[0].equals("3G")) {
                            CellInfoWcdma cellWCDMA = (CellInfoWcdma) cell;
                            dbmValue = cellWCDMA.getCellSignalStrength().getDbm();
                            signalcdma = dbmValue;
                            return;

                        }
                    }
                    if (cell instanceof CellInfoGsm) {
                        type = "2G";
                        if (mode[0].equals("2G")) {
                            CellInfoGsm cellGSM = (CellInfoGsm) cell;
                            dbmValue = cellGSM.getCellSignalStrength().getDbm();
                            signalgsm = dbmValue;
                            return;
                        }
                        if (mode[0].equals("3G")) {
                            CellInfoGsm cellGSM = (CellInfoGsm) cell;
                            dbmValue = cellGSM.getCellSignalStrength().getDbm();
                            signalcdma = dbmValue;
                            return;
                        }

                    }

                }
            }
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
                e.printStackTrace();

            } catch (IllegalAccessException e) {
                e.printStackTrace();

            } catch (IllegalArgumentException e) {
                e.printStackTrace();

            }
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {

            if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                    (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                // TODO: Consider calling
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
            return;
        }
        for (int k = 1; k < 3; k++)
            lManager.requestLocationUpdates(sources[k], 100, 0, this);
        location = lManager.getLastKnownLocation("gps");
        location2 = lManager.getLastKnownLocation("network");
    }

    public void onLocationChanged(Location llocation) {
        if (llocation.getProvider().equals("gps")) {
            this.location = llocation;
            //dbm1.setText(llocation.getProvider().charAt(0)+" "+llocation.getLatitude()+"\n "+llocation.getLongitude());
        }
        if (llocation.getProvider().equals("network")) {
            this.location2 = llocation;
            //dbm2.setText(llocation.getProvider().charAt(0)+" "+llocation.getLatitude()+"\n "+llocation.getLongitude());
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
                    w.WriteSettings(" mesure " + operateur[0] + " " + mode[0] + " " + Ldate + " " + Heure + "\n", mode[0], Ldate, nu, operateur[0], 0,Heure);
                    if (dual)
                        w.WriteSettings(" mesure " + operateur[1] + " " + mode[1] + " " + Ldate + " " + Heure + "\n", mode[1], Ldate, nu, operateur[1], 1,Heure);
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
                        Thread t1 = new savethread(signallte,0);
                        t1.start();
                        Tdbm1 = operateur[0] + " " + signallte + " dbm";
                        if (dual) {
                            Thread t2 = new savethread(signalgsm,1);
                            t2.start();
                            Tdbm2 = operateur[1] + " " + signalgsm + " dbm";
                        }
                    } else {
                        if (mode[0].equals("3G") ) {
                                Thread t3 = new savethread(signalcdma, 0);
                                t3.start();
                                Tdbm1 = operateur[0] + " " + signalcdma + " dbm";
                            if (dual) {
                                Thread t4 = new savethread(signalgsm,1);
                                t4.start();
                                Tdbm2 = operateur[1] + " " + signalgsm + " dbm";
                            }
                        } else {
                            if (mode[0].equals("2G") && type.equals("2G")) {
                                Thread t5 = new savethread(signalgsm,0);
                                t5.start();
                                Tdbm1 = operateur[0] + " " + signalgsm + " dbm";
                                if (dual) {
                                    Thread t6 = new savethread(signalgsm,1);
                                    t6.start();
                                    Tdbm2 = operateur[1] + " " + signalgsm + " dbm";
                                }
                            } else {
                                Thread t7 = new savethread(-150,0);
                                t7.start();
                                Tdbm1 = operateur[0] + " " + -150 + " dbm";
                                if (dual) {
                                    Thread t8 = new savethread(signalgsm,1);
                                    t8.start();
                                    Tdbm2 = operateur[1] + " " + signalgsm + " dbm";
                                }
                            }
                        }
                    }
                    i++;
                    c = Calendar.getInstance();
                    duree = c.getTimeInMillis() - debut;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (i == 0)
                                statut.setText("" + i);
                            else {
                                //statut.setText(i + "  en " + duree + "ms\n");
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
            text[idsim] = signal + " " + mode[idsim] + Heure + " erreur geolocalisation\n ";
            if(bg!=0)
            runOnUiThread(new Runnable() {
                public void run() {
                    ToastMaker to = new ToastMaker(MainActivity.this, "Attention erreur geolocalisation veillez relancer" +
                            " l'application ou les paramètres", Color.RED);
                    to.createone();
                    statut.setBackgroundColor(Color.RED);
                    bg = 0;
                }
            });
        } else {
            if (bg == 0)
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (continu  && pause)
                            statut.setBackgroundColor(Color.GREEN);
                        bg = 1;
                    }
                });
        }
        w.WriteSettings(text[idsim], idsim);
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
                return "Unknown";
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
        public savethread(int signal,int id) {
            this.signal = signal;
            this.a = id;
        }
        public void run() {
            prewrite(this.signal, this.a);
        }
    }
    public class Heurethread extends Thread {

        public Heurethread(){
        }
        public void run() {
            while(continu) {
                Date d = new Date();
                DateFormat df = new DateFormat();
                Heure = (String) DateFormat.format("HH:mm:ss", d);
                Ldate = (String) DateFormat.format("dd-MM-yyyy", d);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


