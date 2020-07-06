package org.d3ifcool.addictcontrol;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.addictcontrol.Account.Account;
import org.d3ifcool.addictcontrol.Database.Connect;
import org.d3ifcool.addictcontrol.Database.DatabaseAdapter;
import org.d3ifcool.addictcontrol.Database.DatabaseFirebase;
import org.d3ifcool.addictcontrol.Database.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Roki on 9/22/2018.
 */

public class RecyclerViewAdapterControl extends RecyclerView.Adapter<RecyclerViewAdapterControl.ViewHolder>{

    private Context mContext;
    private ClickHandler mClickHandler;
    private int listItemLayout;
    private ArrayList<User> itemList;

    DatabaseAdapter innerDatabase ;
    Account account ;

    public RecyclerViewAdapterControl(Context context, int layoutId, ArrayList<User> itemList, ClickHandler handler) {
        mContext =context;
        listItemLayout = layoutId;
        this.itemList = itemList;
        this.mClickHandler = handler;
        innerDatabase = new DatabaseAdapter(mContext);
        account = innerDatabase.getAccount();
    }


    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {

        if(itemList.get(listPosition).smartphoneOn.equals("notaccept") ){
            //Picasso.get().load(itemList.get(listPosition).UUIDImage).into(holder.imageAccount);
            Glide.with(mContext)
                    .asBitmap()
                    .load(itemList.get(listPosition).UUIDImage)
                    .into(holder.imageAccount);
            holder.username.setText(itemList.get(listPosition).username);
            holder.linearLayoutButton.setVisibility(View.VISIBLE);
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("connected").child("connect_"+account.getmUsername());
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                            Map<String,Object> map =  dataSnapshot.getValue(genericTypeIndicator);

                            String connect = (String) map.get("users");
                            updateConnectedUser(connect , itemList.get(listPosition).username);
                            sendAccept(itemList.get(listPosition).username);
                            holder.linearLayoutButton.setVisibility(View.VISIBLE);
                            holder.accept.setVisibility(View.GONE);
                            holder.noAccept.setVisibility(View.GONE);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

            holder.noAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String targetUser = itemList.get(listPosition).username;
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("connected").child("connect_"+account.getmUsername());
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                            Map<String,Object> map =  dataSnapshot.getValue(genericTypeIndicator);
                            String connect = (String) map.get("users");
                            Log.e("Info" , connect);
                            deleteConnectedUser(connect ,account.getmUsername(), targetUser);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference().child("connected")
                            .child("connect_"+ itemList.get(listPosition).username);
                    mDatabase1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                            Map<String,Object> map =  dataSnapshot.getValue(genericTypeIndicator);
                            String connect = (String) map.get("users");
                            Log.e("Info" , connect);
                            deleteConnectedUser(connect ,targetUser, account.getmUsername());
                            notifyDataSetChanged();
                            /// /holder.accept.setVisibility(View.GONE);
                            //holder.noAccept.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });


        }else{
            //Picasso.get().load(itemList.get(listPosition).UUIDImage).into(holder.imageAccount);
            Glide.with(mContext)
                    .asBitmap()
                    .load(itemList.get(listPosition).UUIDImage)
                    .into(holder.imageAccount);
            holder.username.setText(itemList.get(listPosition).username);
            holder.linearLayoutButton.setVisibility(View.GONE);

        }

    }


    public void updateConnectedUser(String data , String user){
        String tempData = "";

        String temp[]  = data.split(",");


        for(int n=0; n <temp.length ; n++) {
            String getQuery[] = temp[n].split(":") ;

            if(getQuery[0].equals("usr_"+user)) {
               tempData += tempData.equals("") ? getQuery[0]+":accept" : "," + getQuery[0]+":accept" ;
            }else {
                tempData += tempData.equals("") ? temp[n] : "," + temp[n] ;
            }
        }

        DatabaseFirebase databaseFirebase = new DatabaseFirebase(mContext);
        databaseFirebase.insertConnectedUser( account.getmUsername(), new Connect(tempData) );
     }


    public void deleteConnectedUser(String data ,String targetUser , String deleteUser){
        String tempData = "";
        ArrayList<Map<String,String>> allData = new ArrayList<>();

        String temp[]  = data.split(",");
        Log.e("Info" , data);

        for(int n=0; n <temp.length ; n++) {
            String getQuery[] = temp[n].split(":");
            try {

                if(getQuery[0].equals("usr_"+deleteUser)) {
                    continue;
                }else {
                    tempData += tempData.equals("") ? temp[n] : "," + temp[n] ;
                }

            }catch (IndexOutOfBoundsException e) {

            }
        }
        DatabaseFirebase databaseFirebase = new DatabaseFirebase(mContext);
        databaseFirebase.insertConnectedUser( targetUser, new Connect(tempData) );
    }

    public void sendAccept(String username){
        final String user = username;
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("connected").child("connect_" + user);
        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                Map<String,Object> map =  dataSnapshot.getValue(genericTypeIndicator);

                String connectedUser = (String) map.get("users");


                String tempData = "";

                String temp[]  = connectedUser.split(",");
                Log.e("Info" , temp[0]);

                for(int n=0; n <temp.length ; n++) {
                    String getQuery[] = temp[n].split(":") ;

                    if(getQuery[0].equals("usr_"+account.getmUsername())) {
                        tempData += tempData.equals("") ? getQuery[0]+":accept" : "," + getQuery[0]+":accept" ;
                    }else {
                        tempData += tempData.equals("") ? temp[n] : "," + temp[n] ;
                    }
                }

                DatabaseFirebase databaseFirebase = new DatabaseFirebase(mContext);
                databaseFirebase.insertConnectedUser( user, new Connect(tempData) );


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    class ViewHolder extends RecyclerView.ViewHolder
            implements
            View.OnClickListener,
            View.OnLongClickListener{
        public ImageView imageAccount;
        public TextView username;
        public LinearLayout linearLayoutButton;
        public Button accept,noAccept;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageAccount =  (ImageView) itemView.findViewById(R.id.imageAccount);
            username = (TextView) itemView.findViewById(R.id.username);
            linearLayoutButton = (LinearLayout) itemView.findViewById(R.id.linear_button);
            accept = (Button) itemView.findViewById(R.id.accept);
            noAccept = (Button) itemView.findViewById(R.id.no_accept);


            itemView.setFocusable(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);


        }
        @Override
        public void onClick(View view) {
            mClickHandler.onItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return mClickHandler.onItemLongClick(getAdapterPosition());
        }
    }

    interface ClickHandler{
        void onItemClick(int position);
        boolean onItemLongClick(int position);

    }
}