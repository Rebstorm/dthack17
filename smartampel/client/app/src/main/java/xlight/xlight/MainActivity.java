package xlight.xlight;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;


public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getBeaconManager();
    }


    private BeaconManager beaconManager;
    protected static final String TAG = "BluetoothBLEListener";
    private void getBeaconManager(){
        try {
            beaconManager = BeaconManager.getInstanceForApplication(getApplication());
            beaconManager.getBeaconParsers().add(new BeaconParser()
                    .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
            beaconManager.bind(this);
            Toast.makeText(getApplicationContext(), "Started listening for iBeacons", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Log.e(TAG, e.getMessage(), e.getCause());
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                Toast.makeText(getApplicationContext(), "I have just switched from seeing/not seeing beacons: "+ i, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
