package com.simutech.landry.stechphone;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

public class teste extends Activity implements LocationListener {
    TextView v;
    List<SubscriptionInfo> sublis;
    Button but;
    TelephonyManager TM, TM2;
    SubscriptionManager Sm;
    SubscriptionInfo operateur;
    TelephonyInfo t;
    MultiSimListener muti;
    MultiSimListener muti2;
    SubscriptionInfo si;
    int signalLTE = 0;
    int signalgsm = 0;
    Object subid;
    Long l = 2L;
    Writter w;
    LocationManager Lm;
    MyLocation Ml;
    Location loc;
    private LocationManager lManager;
    private Location[] locationT = new Location[3];
    private Location location;
    public String[] sources;
    int idsim1=1,idsim2=3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        v = (TextView) findViewById(R.id.View);
        but = (Button) findViewById(R.id.but);
        w = new Writter();
        TM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        TM2 = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        t = TelephonyInfo.getInstance(getApplicationContext());
        Sm = SubscriptionManager.from(getApplicationContext());
        Class<? extends SubscriptionManager> y = Sm.getClass();
        final Method[] methods2 = y.getMethods();
        lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<SubscriptionInfo> list = Sm.getActiveSubscriptionInfoList();
        idsim1 = list.get(0).getSubscriptionId();
        idsim2 = list.get(1).getSubscriptionId();
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenirPosition();
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                muti = new MultiSimListener(idsim1);
                muti2 = new MultiSimListener(idsim2);
                TM.listen(muti,
                        PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                                | PhoneStateListener.LISTEN_SERVICE_STATE);
                TM2.listen(muti2,
                        PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                                | PhoneStateListener.LISTEN_SERVICE_STATE);
            }

        });


    }


    public class MultiSimListener extends PhoneStateListener {

        private Field subIdField;
        private int subId = -1;
        private String service = " ";

        public MultiSimListener(int subId) {
            super();
            try {

                final Method[] methods2 = TM.getClass().getSuperclass().getMethods();
                Field[] listchamp = TM.getClass().getSuperclass().getDeclaredFields();
                subIdField = this.getClass().getSuperclass().getDeclaredField("mSubId");
                subIdField.setAccessible(true);
                subIdField.set(this, subId);
                this.subId = subId;
            } catch (NoSuchFieldException e) {

            } catch (IllegalAccessException e) {

            } catch (IllegalArgumentException e) {
                e.toString();
            }
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            double lat;
            double lon;

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
            for (int i = 0; i < sources.length; i++) {
                locationT[i] = lManager.getLastKnownLocation(sources[i]);
            }
            for (int i = 0; i<= 2; i++) {
                if (locationT[i] != null) {
                    location = locationT[i];
                }
            }
            Date d = new Date();
            DateFormat df = new DateFormat();
            String heure = (String) DateFormat.format("hh:mm:ss",d);
                    lat=location.getLatitude();
                    lon = location.getLongitude();
                String ssignal = signalStrength.toString();
                String[] parts = ssignal.split(" ");
                signalLTE =  Integer.parseInt(parts[8])*2 - 113;
                signalgsm = -113 + 2*signalStrength.getGsmSignalStrength();

                if (this.subId == 1) {
                    v.setText("LTE "+signalLTE+" \n"+v.getText());
                    w.WriteSettings(service + " " + subId + "  " + signalLTE + "," + heure + "," + lat + " " + lon + "\n" , "sim1", "Non1", "" + subId, service);
                } else {
                    v.setText("gsm "+signalgsm+" \n"+v.getText());
                    w.WriteSettings(service + " " + subId + "  "+ signalgsm +"," + heure + "," + lat + " " + lon + "\n" , "sim2", "Non2", "" + subId, service);
                }

            }

        public void onServiceStateChanged(ServiceState serviceState) {
            this.service = serviceState.getOperatorAlphaLong();
            super.onServiceStateChanged(serviceState);
            v.setText(this.service+" \n");

        }

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
        ToastMaker to = new ToastMaker(this,"La source "+s+" a été désactivé, données compromise veillez verifier vos paramètre", Color.RED);
        to.createone();
    }
    public void obtenirPosition() {
        List<String> providers = lManager.getProviders(true);
        sources = new String[providers.size()];
        int j = 0;
        for (String provider : providers)
            sources[j++] = provider;
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        for (int i=0 ;i<providers.size();i++)
            lManager.requestLocationUpdates (sources[i], 200, 0, this);

    }



}
