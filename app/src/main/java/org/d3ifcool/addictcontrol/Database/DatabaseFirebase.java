package org.d3ifcool.addictcontrol.Database;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.d3ifcool.addictcontrol.Account.Account;
import org.d3ifcool.addictcontrol.CreateProfileActivity;
import org.d3ifcool.addictcontrol.MainActivity;
import org.d3ifcool.addictcontrol.SmartphoneActive.CountTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class DatabaseFirebase implements DatabaseAction.user , DatabaseAction.appInfo {
    public String UUIDImage;
    private FirebaseDatabase databaseFirebase;
    private DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Context mContext;
    ProgressDialog progressDialog;
    Uri resultUri;
    String username  = "";
    public int nApp= 0;
    public ArrayList<App> appInfo = new ArrayList<>();


    public DatabaseFirebase(Context context) {
        this.mContext = context;
        databaseFirebase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(mContext);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    public DatabaseFirebase() {
    }


    public DatabaseReference getmDatabase() {
        return mDatabase;
    }

    @Override
    public void insertUser(User user, Uri imageUri) {
        progressDialog.setMessage("Mohon Tunggu Sebentar...");
        progressDialog.show();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        String pushId = mDatabase.push().getKey();
        mDatabase.child("usr_"+user.username).setValue(user);

        username = user.username;
        uploadImage(imageUri, user.UUIDImage);

    }

    public void insertConnectedUser(String username, Connect connect) {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("connected");
        mDatabase.child("connect_"+username).setValue(connect);

    }

    public void insertApps() {
        App app = appInfo.get(nApp);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("app_info");
        mDatabase.child("app_"+username).setValue(app);
    }

    @Override
    public void deleteUser(String username) {
        mDatabase.child("usr_"+username).removeValue();
    }

    @Override
    public void updateUser(String username,User user) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        if(user.username!=null) {
            mDatabase.child("usr_"+username).setValue("usr_"+user.username);
            mDatabase.child("usr_"+user.username).child("username").setValue(user.username);
        }
        if(user.password!=null)mDatabase.child("usr_"+username).child("password").setValue(user.password);
        if(user.UUIDImage!=null)mDatabase.child("usr_"+username).child("UUIDImage").setValue(user.UUIDImage);
        if(user.typeAccount!=null)mDatabase.child("usr_"+username).child("typeAccount").setValue(user.typeAccount);
        if(user.smartphoneOn!=null) mDatabase.child("usr_"+username).child("smartphoneOn").setValue(user.smartphoneOn);
    }


    public void updateUserLock(String username,boolean isLock) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.child("usr_"+username).child("isLock").setValue(isLock);
    }

    @Override
    public void viewAllUser() {
    }
    /*
    public  Map<String,Object>  viewUser(String username) {
        final User user;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("usr_"+username);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> map = dataSnapshot.getValue(Map.class);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return  user;
    }
    */


    @Override
    public void signOut() {

    }

    @Override
    public void insertAppInfo() {

    }

    @Override
    public void deleteAppInfo() {

    }

    @Override
    public void updateAppInfo() {

    }

    @Override
    public void viewAllAppInfo() {

    }

    public void readChild(String document){


    }


    public void deleteChild(String document){

    }
    //upload image
    public void setImageUri(Uri imageUri){
        this.resultUri = imageUri;
    }

    private void uploadImage(final Uri imageUri, String UUIDid ){
        final Uri uri = imageUri;

        StorageReference ref =storageReference.child("images/"+ UUIDid);
        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        updateUser(username,
                                new User(null,
                                null,
                                taskSnapshot.getDownloadUrl().toString(),
                                null,
                                null,
                                false
                        ));

                        insertConnectedUser(username, new Connect(""));

                        //insertConnectedUser(username, new Connect(""));


                        Intent createProfile = new Intent(mContext,MainActivity.class) ;
                        mContext.startActivity(createProfile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //uploadImage(uri, UUIDImage);
                //Toast.makeText(mContext, "Gagal Upload" , Toast.LENGTH_LONG).show();
            }
        });

    }

    private void uploadImageForApp(final Uri imageUri, String UUIDid ){
        final Uri uri = imageUri;

        StorageReference ref =storageReference.child("images/"+ UUIDid);
        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        nApp++;
                        insertApps();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadImage(uri, UUIDImage);
                Toast.makeText(mContext, "Gagal Upload" , Toast.LENGTH_LONG).show();
            }
        });

    }

    public Uri getImage(String username){
        final Uri[] getUri = {null};
        //getImageUser(username, "UUIDImage");
        storageReference.child("images/" + UUIDImage ).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                getUri[0] = uri;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        return getUri[0];
    }
}

interface DatabaseAction{
    interface user {
        public void insertUser(User user,Uri imageUri);

        public void deleteUser(String username);

        public void updateUser(String username,User user);

        public void viewAllUser();

        public void signOut();
    }


    interface appInfo {
        public void insertAppInfo();

        public void deleteAppInfo();

        public void updateAppInfo();

        public void viewAllAppInfo();
    }
}
