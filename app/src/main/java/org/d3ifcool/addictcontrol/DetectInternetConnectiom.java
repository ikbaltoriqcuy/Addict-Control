package org.d3ifcool.addictcontrol;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by cool on 11/8/2018.
 */

public class DetectInternetConnectiom {

    Context mContext;
    boolean connected;

    public DetectInternetConnectiom(Context mContext) {
        this.mContext = mContext;
    }

    public boolean checkInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        }
        else
            connected = false;

        return connected;
    }

    public String message(){
        return connected ? "Internet Tersedia" : "Maaf, Tidak ada koneksi internet" ;
    }
}
