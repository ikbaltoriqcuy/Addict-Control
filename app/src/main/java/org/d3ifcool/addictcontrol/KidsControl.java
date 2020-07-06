package org.d3ifcool.addictcontrol;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
//import com.squareup.picasso.Picasso;

import org.d3ifcool.addictcontrol.Account.Account;
import org.d3ifcool.addictcontrol.Alarm.TempDataAlarm;
import org.d3ifcool.addictcontrol.Database.Connect;
import org.d3ifcool.addictcontrol.Database.DatabaseAdapter;
import org.d3ifcool.addictcontrol.Database.DatabaseFirebase;
import org.d3ifcool.addictcontrol.Database.User;
import org.d3ifcool.addictcontrol.SmartphoneActive.CountTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class KidsControl extends AppCompatActivity {

    private RecyclerViewAdapter mAdapter;
    private ArrayList<User> mData;
    RecyclerView recyclerView;
    DatabaseAdapter databaseAdapter;
    Account mAccount;
    private Handler handler;
    DatabaseReference mDatabase;

    public static boolean isRunnable = false;
    private boolean isAddChild;
    private DetectInternetConnectiom detectInternetConnectiom;


    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getUser(true);
            handler.postDelayed(this,10000);
        }
    };

    public void updateItem(int index,User item) {
        try {
            mData.set(index, item);
            mAdapter.notifyItemChanged(index);
        }catch (IndexOutOfBoundsException e) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kids_control);


        detectInternetConnectiom = new DetectInternetConnectiom(this);


        databaseAdapter = new DatabaseAdapter(this);
        mAccount = databaseAdapter.getAccount();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("connected").child("connect_"+mAccount.getmUsername());
        //mDatabase.addChildEventListener(mListener);

        mData = new ArrayList<>();
         recyclerView = findViewById(R.id.recycler_view_controls);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);

        mAdapter = new RecyclerViewAdapter(this,
                R.layout.list_kids,mData, new RecyclerViewAdapter.ClickHandler() {
            @Override
            public void onItemClick(int position) {


            }

            @Override
            public boolean onItemLongClick(final int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        KidsControl.this);

                mData.get(position);

                if(mData.get(position).smartphoneOn.equals("Menunggu Di Terima")){

                    builder.setItems(new CharSequence[]{"Hapus"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch(which) {
                                case 0 :

                                    if(!detectInternetConnectiom.checkInternetConnection()) {
                                        messageDialog("Tidak Ada Internet Connection");
                                        return;
                                    }

                                    deleteConnectedUser(mAccount.getmUsername(),mData.get(position).username);
                                    mData.clear();
                                    getUser(false);
                                    mAdapter.notifyDataSetChanged();
                                    break;

                            }
                        }
                    });

                    builder.show();



                }else{

                    builder.setItems(new CharSequence[]{"Hapus","Kunci"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch(which) {
                                case 0 :

                                    if(!detectInternetConnectiom.checkInternetConnection()) {
                                        messageDialog("Tidak Ada Internet Connection");
                                        return;
                                    }

                                    deleteConnectedUser(mAccount.getmUsername(),mData.get(position).username);

                                    mData.clear();
                                    getUser(false);
                                    mAdapter.notifyDataSetChanged();
                                    break;

                                case 1 :

                                    if(!detectInternetConnectiom.checkInternetConnection()) {
                                        messageDialog("Tidak Ada Internet Connection");
                                        return;
                                    }

                                    lock(mData.get(position));

                                    break;
                            }
                        }
                    });
                    builder.show();

                }
                return false;
            }
        });

        recyclerView.setAdapter(mAdapter);
        getUser(false);

        handler = new Handler();
        handler.postDelayed(runnable,1000);

    }



    public void lock(User user){
        user.isLock = true;
        DatabaseFirebase databaseFirebase = new DatabaseFirebase(this);
        databaseFirebase.updateUserLock(user.username,true);
        mData.clear();
        getUser(false);
        mAdapter.notifyDataSetChanged();
    }


    public ArrayList<Map<String,String>> queryDataConnected(String data){
        ArrayList<Map<String,String>> allData = new ArrayList<>();


        String temp[]  = data.split(",");
        Log.e("Info" , data);

        for(int n=0; n <temp.length ; n++) {
            String getQuery[] = temp[n].split(":") ;
            Map<String,String> dataConnected= new HashMap<String, String>();
            dataConnected.put("user" ,  getQuery[0]) ;
            dataConnected.put("status" , getQuery[1]) ;

            allData.add(dataConnected);
        }

        return allData;
    }


    void getUser(final boolean isUpdate){

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                Map<String,Object> map =  dataSnapshot.getValue(genericTypeIndicator);

                String connect = (String) map.get("users");

                if(!connect.equals("") ) {
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
                                String typeAccount = (String) map.get("typeAccount");
                                boolean isLock = (Boolean) map.get("isLock");


                                if( ((String)allDataConnected.get(position).get("status") ).equals("accept") ){
                                    if(isUpdate) {
                                        updateItem(position,
                                                new User(username,
                                                        "",
                                                        UUIDImage,
                                                        "",
                                                        smartphoneOn,
                                                        isLock));
                                    }else{

                                        mData.add(new User(username,
                                                "",
                                                UUIDImage,
                                                "",
                                                smartphoneOn,
                                                isLock));
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }else{
                                    if(isUpdate) {

                                        updateItem(position,
                                                new User(username,
                                                        "",
                                                        UUIDImage,
                                                        "",
                                                        "Menunggu Di Terima",
                                                        false));
                                    }else{
                                        mData.add(new User(username,
                                                "",
                                                UUIDImage,
                                                "",
                                                "Menunggu Di Terima",
                                                false));

                                        mAdapter.notifyDataSetChanged();
                                    }

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

    private void messageDialog(String text) {

        final android.app.AlertDialog.Builder message = new android.app.AlertDialog.Builder(this);
        message.setTitle("Info");
        message.setMessage(text);
        message.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        message.create().show();


    }


    public void addChild(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customeView = getLayoutInflater().inflate(R.layout.dialog_add_child,null);

        if(!detectInternetConnectiom.checkInternetConnection()) {
            messageDialog("Tidak Ada Internet Connection");
            return;
        }
        builder.setView(customeView)
                .setTitle("Tambahkan Akun Anak ")
                .setPositiveButton("Tambah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final EditText username = customeView.findViewById(R.id.edittext_username);
                        sanityCheck(username.getText().toString());
                        isAddChild = true;
                        mData.clear();
                        getUser(false);
                    }
                })
                .setNegativeButton("batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.show();

    }

    private void delete(final String username , final String userDelete, final boolean isDeleteEnd){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("connected").child("connect_"+userDelete);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                Map<String,Object> map =  dataSnapshot.getValue(genericTypeIndicator);

                String connect = (String) map.get("users");


                String tempData = "";
                ArrayList<Map<String,String>> allData = new ArrayList<>();

                String temp[]  = connect.split(",");
                Log.e("Info" , connect);

                for(int n=0; n <temp.length ; n++) {
                    String getQuery[] = temp[n].split(":") ;

                    if(getQuery[0].equals("usr_"+username)) {
                        continue;
                    }else {
                        tempData += tempData.equals("") ? temp[n] : "," + temp[n] ;
                    }
                }

                DatabaseFirebase databaseFirebase = new DatabaseFirebase(KidsControl.this);
                databaseFirebase.insertConnectedUser( userDelete, new Connect(tempData) );

                if(isDeleteEnd){
                    mData.clear();
                    getUser(false);
                    mAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void deleteConnectedUser(final String username , final String userDelete){

        AlertDialog.Builder builder = new AlertDialog.Builder(
                KidsControl.this);
        builder.setMessage("Apkah anda yakin menghapus user " + userDelete +  "?");
        builder.setPositiveButton("iya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete(username,userDelete,false);
                delete(userDelete,username,true);
                TempDataAlarm.isLock = false;
                DatabaseFirebase databaseFirebase = new DatabaseFirebase(KidsControl.this);
                databaseFirebase.updateUserLock(userDelete,false);

            }
        });

        builder.setNegativeButton("tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();


    }

    public void sanityCheck(String username) {

        for (int i=0 ; i<mData.size() ; i++) {
            if(mData.get(i).username.equals(username)) {
                messageDialog("Akun Telah Ditambahkan Di List");
                return;
            }
        }

        DatabaseAdapter innerDatabase = new DatabaseAdapter(KidsControl.this);
        final Account account = innerDatabase.getAccount();


        final String user= username;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("usr_"+ user);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                Map<String,Object> map =  dataSnapshot.getValue(genericTypeIndicator);

                String getUser = "";
                String typeAccount="";

                try{
                    getUser = (String) map.get("username");
                    typeAccount = (String) map.get("typeAccount");
                }catch (NullPointerException e){
                   messageDialog(" Username tidak ditemukan ");
                    return;
                }


                if(user.equals(getUser) && typeAccount.equals("Anak")) {


                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("connected").child("connect_" + account.getmUsername());
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                            Map<String,Object> map =  dataSnapshot.getValue(genericTypeIndicator);

                            String connectedUser = (String) map.get("users");
                            String UUIDImage = (String) map.get("UUIDImage");

                            DatabaseFirebase databaseFirebase = new DatabaseFirebase(KidsControl.this);
                            databaseFirebase.insertConnectedUser(account.getmUsername(), new Connect(
                                    connectedUser + (connectedUser.equals("") ? "" : ",") + "usr_"+user+":notaccept"));


                            mData.add(new User(user,
                                    "",
                                    UUIDImage,
                                    "",
                                    "Menunggu Di Terima",
                                    false));

                            mAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else if(user.equals(getUser) && !typeAccount.equals("Anak")){
                  messageDialog("Tipe Accoount Harus Anak");
                } else{
                    messageDialog("Username Tidak Ditemukan");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("connected").child("connect_" + user);
        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                Map<String,Object> map =  dataSnapshot.getValue(genericTypeIndicator);

                String connectedUser = "";

                try{
                    connectedUser = (String) map.get("users");
                }catch (NullPointerException e){
                    return;
                }
                DatabaseFirebase databaseFirebase = new DatabaseFirebase(KidsControl.this);
                databaseFirebase.insertConnectedUser(user, new Connect(
                        connectedUser + (connectedUser.equals("") ? "" : ",") + "usr_"+account.getmUsername()+":notaccept"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
