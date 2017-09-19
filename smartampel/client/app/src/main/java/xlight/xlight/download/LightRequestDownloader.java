package xlight.xlight.download;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LightRequestDownloader extends AsyncTask<String, Void, JSONArray> {
    private static final String TAG = "LightRequestDownloader";
    
    @Override
    protected JSONArray doInBackground(String... urls) {
        
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            
            String res = convertStreamToString(in);
            
            JSONArray result = new JSONArray(res);
            urlConnection.disconnect();
            
            return result;
            
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e.getCause());
        }
        
        return null;
    }
    
    
    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
