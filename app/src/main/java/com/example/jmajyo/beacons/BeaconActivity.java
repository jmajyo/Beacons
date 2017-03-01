package com.example.jmajyo.beacons;

import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.jmajyo.beacons.model.Bacon;
import com.example.jmajyo.beacons.utils.Notifications;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class BeaconActivity extends AppCompatActivity implements BeaconConsumer {

    public static final String TAG = "BeaconsEverywhere";
    private static final long MILLISECONDS_IN_A_DAY = 86400000;
    private BeaconManager beaconManager;
    private TextView text;
    private Bacon bacon = new Bacon();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);
        Realm.init(this);                       //--> REALM

        text = (TextView) findViewById(R.id.activity_beacon___text);

        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser()
                 .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        beaconManager.bind(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
        /*Realm realm = Realm.getDefaultInstance();
        RealmResults<Bacon> listOfBaconInRealm = realm.where(Bacon.class).findAll();
        Log.d(TAG, "Todos los beacon de la memoria");
        for (Bacon b :listOfBaconInRealm) {
            Log.d(TAG, "minor: "+ b.getMinor() + " - major: " + b.getMajor());
        }
        realm.beginTransaction();
        realm.delete(Bacon.class);
        realm.commitTransaction();*/
    }

    @Override
    public void onBeaconServiceConnect() {
        final Region region = new Region("myBeacons", Identifier.parse("699ebc80-e1f3-11e3-9a0f-0cf3ee3bc012"), null, null);

        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    Log.d(TAG, "didEnterRegion");
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    Log.d(TAG, "didExitRegion");
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for(final Beacon oneBeacon : beacons) {
                    Log.d(TAG, "distance: " + oneBeacon.getDistance() + " id:" + oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3());
                    bacon = new Bacon();
                    bacon.setDate(new Date());
                    bacon.setUUID(oneBeacon.getId1().toString());
                    bacon.setMajor(oneBeacon.getId2().toInt());
                    bacon.setMinor(oneBeacon.getId3().toInt());

                    //runOnUiThread(new Runnable() {
                        //@Override
                        //public void run() {
                            boolean isInRealm= false;
                            Date date = null;
                            Realm realm = Realm.getDefaultInstance();
                            RealmResults<Bacon> listOfBaconInRealm = realm.where(Bacon.class).findAllSorted("minor");
                            if(listOfBaconInRealm.size()>0) {//Si la base de datos tiene algo
                                for (Bacon b : listOfBaconInRealm) {//busco todos los bacon de la base de datos
                                    //dentro de este for solo hay que compararlos y poner una variable a true o 1 si son iguales, luego ya se harán
                                    //el resto de comprobaciones.
                                    if(bacon.getMinor() == b.getMinor()){
                                        isInRealm=true;
                                        date=b.getDate();
                                    }
                                }
                                    if (isInRealm) {//Si el bacon ya esta en la base de datos
                                        //comprobar fecha, no se como...
                                        Log.d(TAG, "Si minor = minor base de datos ");
                                        long time = date.getTime();

                                        long millisecondsPassed = new Date().getTime() - time;
                                        if (millisecondsPassed > MILLISECONDS_IN_A_DAY) {//Sí ha pasado más de un día desde la última notificación
                                            // si la fecha es más vieja de un día mando notificación y guardo la nueva notificación en la BD(la fecha se ha cambiado)
                                            Notifications.postNotification(getBaseContext(), BeaconActivity.class, "Nuevo beacon", "Si ha pasado mas de un día" + bacon.getMinor(), R.drawable.btn_check_buttonless_on, 0xFF00FF00, 889988);
                                            realm.beginTransaction();
                                            realm.copyToRealmOrUpdate(bacon);
                                            realm.commitTransaction();
                                            Log.d(TAG, "si la fecha es más vieja.");
                                        } else {//si no ha pasado más de un día no hay que hacer nada
                                            //no hay que hacer nada
                                            Log.d(TAG, "No hacer nada");
                                        }
                                    } else {//Sí el bacon que he visto no esta en la base de datos problema!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                        //mando notificación y añado a realm ese bacon.
                                        Notifications.postNotification(getBaseContext(), BeaconActivity.class, "Nuevo beacon", "Si hay algo en la base de datos, pero el bacon no" + bacon.getMinor() , R.drawable.btn_check_buttonless_on, 0xFF00FF00, 889988);
                                        realm.beginTransaction();
                                        realm.copyToRealm(bacon);
                                        realm.commitTransaction();
                                        Log.d(TAG, "Sí no esta en la base de datos el minor");
                                    }

                            }else{//sí la base de datos no tiene nada
                                Notifications.postNotification(getBaseContext(), BeaconActivity.class, "Nuevo beacon", "No esta en la base de datos" + bacon.getMinor(), R.drawable.btn_check_buttonless_on, 0xFF00FF00, 889988);
                                realm.beginTransaction();
                                realm.copyToRealm(bacon);
                                realm.commitTransaction();
                                Log.d(TAG, "Sí no hay nada en la base de datos.");
                            }
                        //}
                    //});
                }
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
