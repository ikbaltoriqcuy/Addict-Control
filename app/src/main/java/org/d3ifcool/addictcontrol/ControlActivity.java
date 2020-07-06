package org.d3ifcool.addictcontrol;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.addictcontrol.Account.Account;
import org.d3ifcool.addictcontrol.Database.DatabaseAdapter;
import org.d3ifcool.addictcontrol.Database.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ControlActivity extends AppCompatActivity {
    private RecyclerViewAdapterControl mAdapter;
    private ArrayList<User> mData;
    RecyclerView recyclerView;
    DatabaseReference mDatabase;

    private ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mData.clear();
            getUser();
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mData = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view_controls);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);

        mAdapter = new RecyclerViewAdapterControl(this,
                R.layout.list_controls,mData, new RecyclerViewAdapterControl.ClickHandler() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public boolean onItemLongClick(int position) {

                return false;
            }
        });

        recyclerView.setAdapter(mAdapter);

        DatabaseAdapter innerDatabase = new DatabaseAdapter(this);
        Account account = innerDatabase.getAccount();
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("connected")
                .child("connect_"+account.getmUsername());
        mDatabase.addChildEventListener(mListener);

        getUser();
    }

    public ArrayList<Map<String,String>> queryDataConnected(String data){
        ArrayList<Map<String,String>> allData = new ArrayList<>();


        String temp[]  = data.split(",");

        for(int n=0; n <temp.length ; n++) {
            String getQuery[] = temp[n].split(":") ;
            Map<String,String> dataConnected= new HashMap<String, String>();
            dataConnected.put("user" ,  getQuery[0]) ;
            dataConnected.put("status" , getQuery[1]) ;
            Log.e("Info" , getQuery[0] + " "+ getQuery[1]);


            allData.add(dataConnected);
        }

        return allData;

    }


    void getUser(){

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                Map<String,Object> map =  dataSnapshot.getValue(genericTypeIndicator);

                String connect = (String) map.get("users");

                if(!connect.equals("")) {
                    final ArrayList<Map<String,String>> allDataConnected = queryDataConnected(connect) ;

                    for (int n = 0; n <allDataConnected.size() ; n++ ) {
                        final int position = n;

                        DatabaseReference mDatabaseUser = FirebaseDatabase.getInstance().
                                getReference().child("users").child(
                                (String) allDataConnected.get(n).get("user"));
                        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                                Map<String,Object> map =  dataSnapshot.getValue(genericTypeIndicator);

                                String username = (String) map.get("username");
                                String UUIDImage = (String) map.get("UUIDImage");
                                String smartphoneOn = (String) map.get("smartphoneOn");

                                if( ((String)allDataConnected.get(position).get("status") ).equals("accept") ){

                                    mData.add(new User(username,
                                            "",
                                            UUIDImage,
                                            "",
                                            "accept",
                                            false));
                                    mAdapter.notifyDataSetChanged();


                                }else{

                                    mData.add(new User(username,
                                            "",
                                            UUIDImage,
                                            "",
                                            "notaccept",
                                            false));

                                    mAdapter.notifyDataSetChanged();

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
