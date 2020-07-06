package org.d3ifcool.addictcontrol.server;

/**
 * Created by cool on 4/21/2018.
 */

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.d3ifcool.addictcontrol.Quotes.Quotes;

import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class ServerLoader extends AsyncTaskLoader<Quotes>{

    /** Tag for log messages */

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link ServerLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public ServerLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public Quotes loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        Quotes quotes = QueryUtils.fetchEarthquakeData(mUrl);
        return quotes;
    }
}

