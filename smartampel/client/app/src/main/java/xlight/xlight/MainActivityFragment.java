package xlight.xlight;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import xlight.xlight.download.LightRequestDownloader;

public class MainActivityFragment extends Fragment implements BeaconConsumer {
    
    TextView tfInfo;
    TextView tfBeaconInfo;
    private BeaconManager beaconManager;
    private static final NotificationState NOTIFY_STATE = new NotificationState();
    protected static final String TAG = "BluetoothBLEListener";
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        
        tfInfo = v.findViewById(R.id.ibeacon_name_textview);
        tfInfo.setText("+++ XLights found:");
        tfBeaconInfo = v.findViewById(R.id.ibeacon_range_textview);
        
        getBeaconManager();
        
        return v;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        if (beaconManager != null) {
            beaconManager.unbind(this);
        }
        
    }
    
    public void getBeaconManager() {
        
        try {
            beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
            beaconManager.getBeaconParsers().add(new BeaconParser()
                    .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
            beaconManager.setForegroundScanPeriod(250);
            beaconManager.bind(this);
            Log.i(TAG, "-- betw-scan-period=" + beaconManager.getForegroundBetweenScanPeriod());
            Log.i(TAG, "-- scan-period=" + beaconManager.getForegroundScanPeriod());
            Toast.makeText(getApplicationContext(), "Started listening for iBeacons", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e.getCause());
        }
    }
    
    private static class NotificationState {
        boolean stateChanged;
        
    }
    
    
    @Override
    public void onBeaconServiceConnect() {
        final MainActivityFragment thisObj = this;
        
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            
            @Override
            public void didEnterRegion(Region region) {
                Toast.makeText(getApplicationContext(), "new region found", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void didExitRegion(Region region) {
                Toast.makeText(getApplicationContext(), "region exited", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                Toast.makeText(getApplicationContext(), "I have just switched from seeing : " + region.getUniqueId(),
                        Toast.LENGTH_SHORT).show();
            }
            
        });
        
        RangeNotifier rn = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
    
                if (beacons == null || beacons.size() < 1) {
                    return;
                }
                final Collection<Beacon> r = beacons;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (r.iterator().next().getId1() == null) {
                            return;
                        }
//                        Log.i(TAG, "--- pass: " + System.currentTimeMillis());
                        List<Beacon> list = new ArrayList<>(r);
                        List<Structs.XLightState> states = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getId1() == null) {
                                continue;
                            }
                            if (list.get(i).getId1().toUuid() == null) {
                                continue;
                            }
                            Structs.XLightState state = Structs.newXLightState();
                            state.uuid = list.get(i).getId1().toUuid().toString();
                            state.distance = list.get(i).getDistance();
//                            state.distance = 77.0;
                            state.txPower = list.get(i).getTxPower();
                            state.rssi = list.get(i).getRssi();
                            state.remoteKnown = false;
                            state.remoteState = -1;
                            // check if we're in range
                            state.lightNearby = state.distance <=
                                    Structs.XLightState.NEARBY_THRESHOLD;
                            
                            LightRequestDownloader downloader = new LightRequestDownloader();
                            JSONArray a;
                            try {
                                a = downloader.execute(
                                        "https://labs.basti.site/?beaconid=" + state.uuid).get();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                continue;
                            }
                            try {
                                JSONObject payload = (JSONObject)
                                        ((JSONObject) a.get(0)).get("fields");
                                
                                if (payload.get("beaconid") == null ||
                                        ((String) payload.get("beaconid")).isEmpty()) {
                                    continue;
                                    
                                }
                                state.remoteKnown = true;
                                state.remoteState = Integer.parseInt(
                                        payload.get("current_status").toString());
                                state.location = payload.get("location_street") + " " +
                                        payload.get("location_streetno") + ", " +
                                        payload.get("location_postcode") + " " +
                                        payload.get("location_city");
                                
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                continue;
                            }
                            states.add(state);
                        }
                        
                        if (states.isEmpty()) {
                            return;
                        }
                        
                        StringBuilder sb = new StringBuilder();
                        for (Structs.XLightState state : states) {
                            sb.append(state.toString()).append("\n-----------\n");
                        }
                        tfBeaconInfo.setText(sb.toString());
                        
                        NotificationHandler.getInstance(thisObj)
                                .handleNotifications(states);
                    }
                });
            }
        };
        
        beaconManager.addRangeNotifier(rn);
        
        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("1337", null, null, null));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("1337", null, null, null));
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
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
