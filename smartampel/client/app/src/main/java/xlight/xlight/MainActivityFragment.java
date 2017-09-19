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

import java.util.Collection;

import xlight.xlight.interfaces.UIUpdater;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements BeaconConsumer {

    public MainActivityFragment() {
    }

    TextView name;
    TextView distance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_main, container, false);

        name = (TextView) v.findViewById(R.id.ibeacon_name_textview);
        distance = (TextView) v.findViewById(R.id.ibeacon_range_textview);

        getBeaconManager();

        return v;
    }

    private BeaconManager beaconManager;
    protected static final String TAG = "BluetoothBLEListener";
    private void getBeaconManager(){
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
                                name.setText((r.iterator().next().getId1().toUuid().toString()));
                            } else {

                            }
                            distance.setText(String.valueOf(r.iterator().next().getDistance()));
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
