package xlight.xlight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import xlight.xlight.download.LightRequestDownloader;
import xlight.xlight.interfaces.UIUpdater;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements BeaconConsumer {

    public MainActivityFragment() {
    }

    TextView name;
    TextView distance;
    private BeaconManager beaconManager;
    protected static final String TAG = "BluetoothBLEListener";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_main, container, false);

        name = (TextView) v.findViewById(R.id.ibeacon_name_textview);
        distance = (TextView) v.findViewById(R.id.ibeacon_range_textview);

        getBeaconManager();

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(beaconManager != null){
            beaconManager.unbind(this);
        }

    }

    public void getBeaconManager(){

        /*
        if(beaconManager.isBound(this)){
            beaconManager.unbind(this);
        }
        */

        try {
            beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
            beaconManager.getBeaconParsers().add(new BeaconParser()
                    .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
            beaconManager.bind(this);
            Toast.makeText(getApplicationContext(), "Started listening for iBeacons", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Log.e(TAG, e.getMessage(), e.getCause());
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addMonitorNotifier(new MonitorNotifier() {

            @Override
            public void didEnterRegion(Region region) {
                Toast.makeText(getApplicationContext(),"new region found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void didExitRegion(Region region) {
                Toast.makeText(getApplicationContext(),"region exited", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                Toast.makeText(getApplicationContext(), "I have just switched from seeing : "+ region.getUniqueId(), Toast.LENGTH_SHORT).show();
            }

        });

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if(beacons.size() > 0){
                    final Collection<Beacon> r = beacons;
                    final Region re = region;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(r.iterator().next().getId1() != null){

                                List<Beacon> list = new ArrayList<Beacon>(r);
                                for(int i = 0; i < list.size(); i++){
                                    if(list.get(i).getId1().toUuid().toString().equals("8644d8ef-b649-4a86-b40f-382f89d0bcd0")){
                                        name.setText(list.get(i).getId1().toUuid().toString());
                                        distance.setText("DIST: " + list.get(i).getDistance() + "\nTXPower: "
                                                + list.get(i).getTxPower() + "\nRSSI: " + list.get(i).getRssi());

                                        LightRequestDownloader downloader = new LightRequestDownloader();
                                        downloader.execute("https://labs.basti.site/?beaconid=" + list.get(i).getId1().toUuid().toString());

                                        //LightRequestDownloader.execute("https://labs.basti.site/?beaconid=" + list.get(i).getId1().toUuid().toString());
                                        //Toast.makeText(getApplicationContext(), a, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } else {

                            }

                        }
                    });

                }
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("1337", null, null, null));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("1337", null, null, null));
        } catch (RemoteException e) {

        }
    }

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return getActivity().bindService(intent, serviceConnection, i);
    }
}
