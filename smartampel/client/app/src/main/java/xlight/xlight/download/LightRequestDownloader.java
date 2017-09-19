package xlight.xlight.download;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by paul on 19.09.17.
 */

public class LightRequestDownloader extends AsyncTask<String, Void, JSONObject > {
    protected static final String TAG = "BluetoothBLEListener";


    public static String get(String urlRequested) {

        try {
            URL url = new URL(urlRequested);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            JSONObject jsonObject = (JSONObject) new JSONObject(in.toString());


            urlConnection.disconnect();

        } catch(Exception e){
            Log.e(TAG, e.getMessage(), e.getCause());
        }


        return  null;
    }

    @Override
    protected JSONObject doInBackground(String... urls) {

        try {
            URL url = new URL(urls[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            JSONObject jsonObject = (JSONObject) new JSONObject(in.toString());
            urlConnection.disconnect();

            return jsonObject;

        } catch(Exception e){
            Log.e(TAG, e.getMessage(), e.getCause());
        }

        return null;
    }
}
