package com.example.mathias.tankineedanoperator;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String CONNECT = "CONNECTIVITY";

    private static final String WEATHER_API_KEY = "a111d736151b7b355e24269ad611a112";

    private static final long CITY_ID_AARHUS = 2624652;

    private static final String WEATHER_API_CALL = "http://api.openweathermap.org/data/2.5/forecast/city?id=" + CITY_ID_AARHUS + "&APPID=" + WEATHER_API_KEY;

    private TextView txtResponse;
    private Button btnJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtResponse = (TextView) findViewById(R.id.textView);
        txtResponse.setMovementMethod(new ScrollingMovementMethod());

        btnJson = (Button) findViewById(R.id.btnJson);
        btnJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //try to convert JSON
                if(txtResponse.getText().toString()!=null){
                    //try to interpret JSON
                    interpretWeatherJSON(txtResponse.getText().toString());
                }
            }
        });
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return callURL(urls[0]);

        }

        @Override
        protected void onPostExecute(String result) {
            if(result!=null) {
                txtResponse.setText(result);
            }
        }
    }

    private String callURL(String callUrl) {

        InputStream is = null;

        try {
            //create URL
            URL url = new URL(callUrl);

            //configure HttpURLConnetion object
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);


            // Starts the request
            conn.connect();
            int response = conn.getResponseCode();

            //probably check check on response code here!

            //give user feedback in case of error

            Log.d(CONNECT, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string

            String contentAsString = convertStreamToStringBuffered(is);
            return contentAsString;


        } catch (ProtocolException pe) {
            Log.d(CONNECT, "oh noes....ProtocolException");
        } catch (UnsupportedEncodingException uee) {
            Log.d(CONNECT, "oh noes....UnsuportedEncodingException");
        } catch (IOException ioe) {
            Log.d(CONNECT, "oh noes....IOException");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                    Log.d(CONNECT, "oh noes....could not close stream, IOException");
                }
            }
        }
        return null;
    }
    protected void onGetWeatherBtnClick(View view){
        DownloadTask d = new DownloadTask();
        d.execute(WEATHER_API_CALL);
    }

    private String convertStreamToStringBuffered(InputStream is) {
        String s = "";
        String line = "";

        BufferedReader rd = new BufferedReader(new InputStreamReader(is));


        try {
            while ((line = rd.readLine()) != null) { s += line; }
        } catch (IOException ex) {
            Log.e(CONNECT, "ERROR reading HTTP response", ex);
            //ex.printStackTrace();
        }

        // Return full string
        return s;
    }

    //attempt to decode the json response from weather server
    public void interpretWeatherJSON(String jsonResonse){

        try {
            JSONObject cityWeatherJson = new JSONObject(jsonResonse);

            JSONObject city = cityWeatherJson.getJSONObject("city");
            String name = city.getString("country");
            JSONArray measurements = cityWeatherJson.getJSONArray("list");

/*
            ArrayList<String> weatherStrings = new ArrayList<String>();
            for(int i=0; i<measurements.length(); i++){
                weatherStrings.add( measurements.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("main") + " : " + measurements.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description"));
            }
*/

            String weatherString = measurements.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("main") + " : " + measurements.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description");
            Toast.makeText(MainActivity.this, name + "\n" + weatherString, Toast.LENGTH_SHORT).show();

            /*
            weatherString = "";
            for(String s : weatherStrings){
                weatherString += s + "\n";
            }
            Toast.makeText(MainActivity.this, name + "\n" + weatherString, Toast.LENGTH_SHORT).show();
*/
            //Gson gson = new Gson();
            //

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
