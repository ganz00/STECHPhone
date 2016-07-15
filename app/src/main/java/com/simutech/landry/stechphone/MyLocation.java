package com.simutech.landry.stechphone;

/**
 * Created by User on 05/07/2016.
 */
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Button;

public class MyLocation implements LocationListener{
    Timer timer1;
    LocationManager lManager;
    public String[] sources;
    Context context;
    Activity activite;
    Button btnpause;
    boolean gps_enabled=false;
    boolean network_enabled=false;
    public Location[] locationT = new Location[3];




    public MyLocation(LocationManager Lm,Activity ac,Button b){
        this.lManager = Lm;
        this.activite = ac;
        this.btnpause = b;
    }

    public String[] obtenirPosition() {
        List<String> providers = lManager.getProviders(true);
        sources = new String[providers.size()];
        int j = 0;
        for (String provider : providers)
            sources[j++] = provider;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return null;
        }
        for (int i=1 ;i<providers.size();i++)
            lManager.requestLocationUpdates (sources[i], 200, 0, this);
        return sources;
    }

    @Override
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
        ToastMaker to = new ToastMaker(activite,"La source "+s+" a été désactivé, données compromise veillez verifier vos paramètre", Color.RED);
        to.createone();
        btnpause.callOnClick();
    }
}
