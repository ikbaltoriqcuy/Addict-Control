package org.d3ifcool.addictcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.d3ifcool.addictcontrol.Account.Account;
import org.d3ifcool.addictcontrol.Database.DatabaseAdapter;
import org.d3ifcool.addictcontrol.Quotes.Quotes;
import org.d3ifcool.addictcontrol.server.ServerLoader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class QuotesActivity extends Activity
        implements LoaderManager.LoaderCallbacks<Quotes>{

    private  ListView listView;
    private final String URL = "http://www.solset.esy.es/showdatabase.php";
    ArrayList<String> dat;
    private int mNServer = 0;
    private ArrayAdapter<String> mAdapter;
    private boolean mIsConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);

        mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        dat = new ArrayList<>();



        //set list data quote from listView
        listView = (ListView) findViewById(R.id.list_quote);
//        listView.setAdapter(new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1,getQuotes()));

        //getDataJSON("https://app-1523625833.000webhostapp.com/showdatabase.php");

        listView.setAdapter(mAdapter);

        //create alert dialog
        AlertDialog.Builder showContentOption = new AlertDialog.Builder(this);
        //get layout quote_content_option
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.quote_content_option,null);
        //set content alert builder
        showContentOption.setView(dialogView);

        //to shoe aler dialog
        final AlertDialog alertDialog = showContentOption.create();

        //set setOnItemLongClickListener for each list item
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                //get all component
                Button myQuote = (Button) dialogView.findViewById(R.id.my_quote_button);
                Button edit = (Button) dialogView.findViewById(R.id.edit_button);
                Button delete = (Button) dialogView.findViewById(R.id.hapus_button);
                //

                //show alert dialog
                alertDialog.show();

                //when item is not default quote item
                if(position > (mNServer-1)){

                    edit.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.VISIBLE);

                    //when myQuote is click
                    myQuote.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //get data account from database
                            DatabaseAdapter databaseAdapter = new DatabaseAdapter(QuotesActivity.this);

                            Account account = databaseAdapter.getAccount();
                            account.setQuote(dat.get(position));

                            databaseAdapter.updateAccount(account,account.getmUsername());
                            ////

                            //refresh data
                            Intent intent = new Intent(QuotesActivity.this,MainActivity.class);
                            intent.putExtra("current_tab",2);
                            startActivity(intent);
                            //destroy this activity
                            QuotesActivity.this.finish();

                        }
                    });

                    //when edit is click
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //go to AddQuoActivity
                            Intent showAddQuoActivity = new Intent(QuotesActivity.this,AddQuoActivity.class);
                            showAddQuoActivity.putExtra("quote",dat.get(position));
                            startActivity(showAddQuoActivity);
                            //destroy this activity
                            QuotesActivity.this.finish();
                        }
                    });

                    //when delete is click
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.hide();
                            final AlertDialog.Builder messageAlert = new AlertDialog.Builder(QuotesActivity.this);
                            View viewDeleteMessage = inflater.inflate(R.layout.delete_message,null);
                            messageAlert.setView(viewDeleteMessage);
                            messageAlert.setNegativeButton("urung", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //nothing
                                }
                            });
                            messageAlert.setPositiveButton("hapus", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //delete from database
                                    DatabaseAdapter databaseAdapter = new DatabaseAdapter(QuotesActivity.this);
                                    databaseAdapter.deleteQuote(dat.get(position));

                                    //refresh data
                                    Intent intent = new Intent(QuotesActivity.this,MainActivity.class);
                                    intent.putExtra(getString(R.string.current_tab),1);
                                    startActivity(intent);
                                    QuotesActivity.this.finish();

                                }
                            });
                            //show alert dialog
                            AlertDialog alert = messageAlert.create();
                            alert.show();


                        }
                    });
                    //when default quoote item is click then disable edit and delete button
                }else {

                    //deactive button
                    edit.setVisibility(View.GONE);
                    delete.setVisibility(View.GONE);

                    //when myQuote is click then change quote from table account in database
                    myQuote.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //update quote from table account database
                            DatabaseAdapter databaseAdapter = new DatabaseAdapter(QuotesActivity.this);

                            Account account = databaseAdapter.getAccount();
                            account.setQuote(dat.get(position));

                            databaseAdapter.updateAccount(account,account.getmUsername());


                            //refresh data
                            Intent intent = new Intent(QuotesActivity.this,MainActivity.class);
                            intent.putExtra("current_tab",2);
                            startActivity(intent);
                            QuotesActivity.this.finish();

                        }
                    });

                }

                return false;
            }
        });


        if(checkConnection()) {
            //we are connected to a network

            getLoaderManager().initLoader(1, null , this);
            LinearLayout errorMessage = (LinearLayout) findViewById(R.id.no_connection_textview) ;
            errorMessage.setVisibility(View.GONE);
            mIsConnected = true;

        } else{
            mAdapter.addAll(getQuotes());
            dat = getQuotes();
            LinearLayout errorMessage = (LinearLayout) findViewById(R.id.no_connection_textview) ;
            errorMessage.setVisibility(View.VISIBLE);
            mIsConnected=false;
        }


    }

    private boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }else{
            return false;
        }
    }

    //event click listener to start AddQuoActivity
    public void addQuotes(View view){
        Intent quotesActivity = new Intent(this,AddQuoActivity.class);
        startActivity(quotesActivity);
        this.finish();
    }
    //this method to get all quotes in table quotes database
    private ArrayList<String> getQuotes(){
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
        Quotes quotes = null;
        try{
            quotes = databaseAdapter.getAllQuote();;
        }catch (Exception e) {
        }

        return quotes.getmAllQuotes();
    }

    private void getDataJSON (final String urlWeb) {

        class GetJSON extends AsyncTask<Void,Void,String> {

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
                    URL url = new URL(urlWeb);
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

        GetJSON getJson = new GetJSON();
        getJson.execute();
    }

    public void getDefaultQuotes(String json) {

        try {
            JSONObject data = new JSONObject(json);
            JSONArray quotes = data.getJSONArray("quotes");
            dat = new ArrayList<>();

            for (int n=0; n<quotes.length() ;n++) {
                JSONObject quote =quotes.getJSONObject(n);
                String image = quote.getString("image");
                String textQuote = quote.getString("quote");
                Toast.makeText(this,textQuote , Toast.LENGTH_LONG).show();
//                ImageView imageView = (ImageView) findViewById(R.id.test);
//                imageView.setImageBitmap(loadImageURL("https://app-1523625833.000webhostapp.com/image/parallax.jpg" ));
                dat.add(textQuote);
            }
            ArrayList<String> allQuotes = getQuotes();
            for (int n=0; n< allQuotes.size() ;n++)
                dat.add(allQuotes.get(n));

            listView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,dat));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Bitmap loadImageURL (String url) {
        try {
            URL urlImage =  new URL(url);
            Bitmap bitmap = BitmapFactory.decodeStream(urlImage.openConnection().getInputStream());
              return bitmap;
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public Loader<Quotes> onCreateLoader(int id, Bundle args) {
        return new ServerLoader (this,URL );
    }

    @Override
    public void onLoadFinished(Loader<Quotes> loader, Quotes data) {


        if(checkConnection()) {
            mAdapter.clear();
            mNServer = data.getmAllQuotes().size();

            ArrayList<String> allQuotes = getQuotes();
            for (int n = 0; n < allQuotes.size(); n++)
                data.setQuote(allQuotes.get(n));

            dat = data.getmAllQuotes();

            mAdapter.addAll(data.getmAllQuotes());

        }else{
            mNServer = 0;
        }

    }


    @Override
    public void onLoaderReset(Loader<Quotes> loader) {
        mAdapter.clear();
    }

    public void refresh(View view) {
        Intent intent = new Intent(QuotesActivity.this,MainActivity.class);
        intent.putExtra(getString(R.string.current_tab),1);
        startActivity(intent);
    }
}
