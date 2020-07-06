package org.d3ifcool.addictcontrol;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.addictcontrol.Account.Account;
import org.d3ifcool.addictcontrol.Alarm.TempDataAlarm;
import org.d3ifcool.addictcontrol.Database.DatabaseAdapter;
import org.d3ifcool.addictcontrol.Database.DatabaseFirebase;
import org.d3ifcool.addictcontrol.Database.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Roki on 9/22/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private Context mContext;
    private ClickHandler mClickHandler;
    private int listItemLayout;
    private ArrayList<User> itemList;
    public RecyclerViewAdapter(Context context,int layoutId, ArrayList<User> itemList, ClickHandler handler) {
        mContext =context;
        listItemLayout = layoutId;
        this.itemList = itemList;
        this.mClickHandler = handler;
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

        if(! itemList.get(listPosition).smartphoneOn.equals("Menunggu Di Terima") ){
            //Picasso.get().load(itemList.get(listPosition).UUIDImage).into(holder.imageAccount);
            Glide.with(mContext)
                    .asBitmap()
                    .load(itemList.get(listPosition).UUIDImage)
                    .into(holder.imageAccount);
            holder.username.setText(itemList.get(listPosition).username);
            holder.smartphoneOn.setText(itemList.get(listPosition).smartphoneOn);
            holder.imageEmot.setVisibility(View.VISIBLE);


            if(itemList.get(listPosition).isLock) {
                holder.imageEmot.setImageResource(R.drawable.ic_vpn_key_yellow_24dp);
                holder.imageEmot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                mContext);
                        builder.setMessage("Apakah anda yakin akan membuka kunci ? ");
                        builder.setPositiveButton("iya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                unLock(itemList.get(listPosition));
                                TempDataAlarm.isLock = false;
                                holder.imageEmot.setImageResource(R.drawable.ic_sentiment_satisfied_green_24dp);
                                holder.imageEmot.setOnClickListener(null);
                            }
                        }).setNegativeButton("tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
            }else {
                holder.imageEmot.setImageResource(R.drawable.ic_sentiment_satisfied_green_24dp);
            }

        }else{
            //Picasso.get().load(itemList.get(listPosition).UUIDImage).into(holder.imageAccount);
            Glide.with(mContext)
                    .asBitmap()
                    .load(itemList.get(listPosition).UUIDImage)
                    .into(holder.imageAccount);
            holder.username.setText(itemList.get(listPosition).username);
            holder.smartphoneOn.setText(itemList.get(listPosition).smartphoneOn);
            holder.imageEmot.setVisibility(View.GONE);
        }

    }


    public void unLock(User user){
        DatabaseFirebase databaseFirebase = new DatabaseFirebase(mContext);
        databaseFirebase.updateUserLock(user.username,false);
    }




    class ViewHolder extends RecyclerView.ViewHolder
            implements
            View.OnClickListener,
            View.OnLongClickListener{
        public ImageView imageAccount, imageEmot;
        public TextView username,smartphoneOn;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageAccount =  (ImageView) itemView.findViewById(R.id.imageAccount);
            imageEmot =  (ImageView) itemView.findViewById(R.id.emot);
            username = (TextView) itemView.findViewById(R.id.username);
            smartphoneOn = (TextView) itemView.findViewById(R.id.smartphoneOn);

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