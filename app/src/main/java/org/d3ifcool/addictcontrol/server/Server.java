package org.d3ifcool.addictcontrol.server;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cool on 4/14/2018.
 */

public abstract class Server {

    private Context mContext;

    public Server(Context mContext) {
        this.mContext = mContext;
    }

    public void getJSON () {
        getDataJSON("https://app-1523625833.000webhostapp.com/showdatabase.php");
    }

    public void getDataJSON(final String urlWeb) {

         class GetJSON extends AsyncTask<Void,Void,String> {
            private String mUrlWeb;
            private ArrayList<HashMap<String,String>> mDataQuotes;

            public  GetJSON(String urlWeb){
                mUrlWeb = urlWeb;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                getDefaultQuotes(s);
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(mUrlWeb);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String json;
                    while ((json = bf.readLine()) !=null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }

        }

        GetJSON getJson = new GetJSON(urlWeb);
        getJson.execute();
    }

    public abstract void getDefaultQuotes(String json);

}
