package org.d3ifcool.addictcontrol.Quotes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import org.d3ifcool.addictcontrol.server.Server;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class Quotes {
    private ArrayList<String> mAllQuotes ;

    public Quotes() {

        mAllQuotes = new ArrayList<>();
//        getDataJSON("https://app-1523625833.000webhostapp.com/showdatabase.php");


//        for (String data:defaultQuotes)
//            mAllQuotes.add(data);

    }




    //set quote
    public void setQuote(String quote){
        mAllQuotes.add(quote);
    }

    //get all data quote
    public ArrayList<String> getmAllQuotes() {
        return mAllQuotes;
    }
}
