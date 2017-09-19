package xlight.xlight.download;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by paul on 19.09.17.
 */

public class LightRequestDownloader extends AsyncTask<String, Void, JSONArray > {
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
    protected JSONArray doInBackground(String... urls) {

        try {
            URL url = new URL(urls[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            String res = convertStreamToString(in);

            JSONArray result= new JSONArray(res);

            urlConnection.disconnect();

            return result;

        } catch(Exception e){
            Log.e(TAG, e.getMessage(), e.getCause());
        }

        return null;
    }


    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
