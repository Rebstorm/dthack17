package xlight.xlight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import xlight.xlight.download.LightRequestDownloader;

public class MainActivityFragment extends Fragment implements BeaconConsumer {
    
    TextView tfInfo;
    TextView tfBeaconInfo;
    private BeaconManager beaconManager;
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
            beaconManager.bind(this);
            Toast.makeText(getApplicationContext(), "Started listening for iBeacons", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e.getCause());
        }
    }
    
    private enum LIGHT_STATES {
        IDLE,
        GREEN,
        RED,
        YELLOW,
        ALARM
    }
    
    private class XLightState {
        
        private static final double NEARBY_THRESHOLD = 2.0;
        
        String uuid;
        double distance;
        int txPower;
        int rssi;
        int remoteState;
        boolean remoteKnown;
        boolean lightNearby;
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("UUID=").append(uuid).append("\n");
            sb.append("DIST=").append(distance).append("\n");
            sb.append("TXPO=").append(txPower).append("\n");
            sb.append("RSSI=").append(rssi).append("\n");
            sb.append("NEARBY   =").append(lightNearby);
            if (lightNearby) {
                sb.append("\n");
                sb.append("REM-KNOWN=").append(remoteKnown).append("\n");
                sb.append("REM-STATE=").append(remoteState).append("\n");
                sb.append("LIGHT @ ").append(LIGHT_STATES.values()[remoteState]);
            }
            return sb.toString();
        }
    }
    
    @Override
    public void onBeaconServiceConnect() {
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
                        Log.i(TAG, "--- pass: " + System.currentTimeMillis());
                        List<Beacon> list = new ArrayList<>(r);
                        List<XLightState> states = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getId1() == null) {
                                continue;
                            }
                            if (list.get(i).getId1().toUuid() == null) {
                                continue;
                            }
                            XLightState state = new XLightState();
                            state.uuid = list.get(i).getId1().toUuid().toString();
                            state.distance = list.get(i).getDistance();
                            state.txPower = list.get(i).getTxPower();
                            state.rssi = list.get(i).getRssi();
                            state.remoteKnown = false;
                            state.remoteState = -1;
                            // check if we're in range
                            state.lightNearby = state.distance <= XLightState.NEARBY_THRESHOLD;
                            
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
                                    state.remoteKnown = false;
                                    state.remoteState = -1;
                                    continue;

                                }
                                state.remoteKnown = true;
                                state.remoteState = Integer.parseInt(
                                        payload.get("current_status").toString());
                                
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
                        for (XLightState state : states) {
                            sb.append(state.toString()).append("\n-----------\n");
                        }
                        tfBeaconInfo.setText(sb.toString());
                        
                        // Here I'd call some method if I found new valid information..
                        // call(states)
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
