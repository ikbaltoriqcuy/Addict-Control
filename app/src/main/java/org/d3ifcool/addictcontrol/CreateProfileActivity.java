package org.d3ifcool.addictcontrol;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.d3ifcool.addictcontrol.Account.Account;
import org.d3ifcool.addictcontrol.Database.DatabaseAdapter;
import org.d3ifcool.addictcontrol.Database.DatabaseFirebase;
import org.d3ifcool.addictcontrol.Database.User;
import org.d3ifcool.addictcontrol.Quotes.Quotes;
import org.d3ifcool.addictcontrol.SmartphoneActive.CountTime;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateProfileActivity extends AppCompatActivity {
    private RadioGroup radioTypeGroup;
    private RadioButton radioTypeButton;

    FirebaseFirestore db;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Uri resultUri;
    private boolean isRegister;

    private final int mAvatarImage[] = {R.drawable.avatar_satu,R.drawable.avatar_dua,
            R.drawable.avatar_tiga, R.drawable.avatar_empat}; //is a all image avatar
    private String mCurrentAvatar ; //is a default or avatar image


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);


        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

    }



    public void createProfile(View view) {
        isRegister = true;
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Tunggu sebentar...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);

        try{
            Account account = databaseAdapter.getAccount();
            if(account.getmUsername() != null) {
                Intent gotoMain = new Intent(this,MainActivity.class) ;
                startActivity(gotoMain);
            }else{

            }
        }catch (Exception e) {
            final EditText username = (EditText) findViewById(R.id.username_edit_text);
            final EditText password = (EditText) findViewById(R.id.password_edit_text);
            Quotes quotes = new Quotes();

            try {

                final DatabaseFirebase databaseFirebase = new DatabaseFirebase(this);


                DetectInternetConnectiom detectInternetConnectiom = new DetectInternetConnectiom(this);

                if(detectInternetConnectiom.checkInternetConnection()) {

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot children : dataSnapshot.getChildren()) {
                                if (children.getKey().equals("usr_" + username.getText().toString())) {
                                    messageDialog( "nama akun telah dipakai coba yang lain");
                                    //databaseAdapter.addAccount(null);
                                    progressDialog.dismiss();

                                    return;
                                }
                            }

                            if (resultUri == null) {
                                messageDialog("Anda harus memilih gambar account terlebih dahulu");
                                progressDialog.dismiss();
                                return;
                            } else {
                                databaseFirebase.insertUser(
                                        new User(username.getText().toString(),
                                                password.getText().toString(),
                                                UUID.randomUUID().toString(),
                                                addListenerOnButton(),
                                                CountTime.mHour + ":" + CountTime.mMinute + ":" + CountTime.mSecond,
                                                false
                                        ),
                                        resultUri
                                );


                                databaseAdapter.addAccount(
                                        new Account(
                                                username.getText().toString(),
                                                mCurrentAvatar,
                                                addListenerOnButton(),
                                                password.getText().toString()));

                            }

                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }else{
                    messageDialog("Gagal Daftar, Coba Cek Internet Anda");
                    progressDialog.dismiss();

                }
                // /DatabaseFirebase databaseFirebase = new DatabaseFirebase(this);
                //databaseFirebase.insertUser(username.getText().toString(), password.getText().toString() );

            } catch (IllegalArgumentException err) {
                messageDialog("Nama User Tidak Boleh Kosong");
                progressDialog.dismiss();
                return;
            }


        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isRegister)
            finish();
    }

    //event onClick to change Avatar
    public void changeAvatar(View view) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);


        /*
        //create ImageView Array
        ImageView avatar[] = new ImageView[4];

        //create alert dialog
        AlertDialog.Builder showListAvatar = new AlertDialog.Builder(this);

        //get layput avatar_list
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.avatar_list,null);

        //set content alert dialog
        showListAvatar.setView(dialogView);
        //set title alert dialog
        showListAvatar.setTitle(R.string.text_choose_avatar);
        final AlertDialog alertDialog = showListAvatar.create();
        alertDialog.show();

        //set all avatar image in variabel avatar
        avatar[0] = (ImageView) dialogView.findViewById(R.id.avatar1_image_view);
        avatar[1] = (ImageView) dialogView.findViewById(R.id.avatar2_image_view);
        avatar[2] = (ImageView) dialogView.findViewById(R.id.avatar3_image_view);
        avatar[3] = (ImageView) dialogView.findViewById(R.id.avatar4_image_view);

        //set onClickListener for each avatar
        for(int n=0;n<avatar.length;n++){
            final int finalN = n;
            avatar[n].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //hide alert dialog
                    alertDialog.hide();

                    mCurrentAvatar = mAvatarImage[finalN];
                }
            });
        }
        */

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                CircleImageView avatarImageView = (CircleImageView) findViewById(R.id.avatar_image_view);
                avatarImageView.setImageURI(resultUri);
                mCurrentAvatar = resultUri.toString();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public boolean checkAccount(String username) {
        final  String user =  username;
        final boolean[] isDuplicat = new boolean[1];
        isDuplicat[0] = false;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    if(children.getKey().equals("usr_"+user)) {
                        isDuplicat[0] = true;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return  isDuplicat[0];

    }

    public String addListenerOnButton() {

        radioTypeGroup = (RadioGroup) findViewById(R.id.type_account);

        int selectedId = radioTypeGroup.getCheckedRadioButtonId();

        radioTypeButton = (RadioButton) findViewById(selectedId);

        if(radioTypeButton.getText().equals("Orang Tua")) {
            return "Orang Tua";
        }

        if(radioTypeButton.getText().equals("Anak")) {
            return "Anak";
        }
        return "";

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
}
