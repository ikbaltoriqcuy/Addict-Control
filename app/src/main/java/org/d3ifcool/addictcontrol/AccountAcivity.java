package org.d3ifcool.addictcontrol;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.content.CursorLoader;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.d3ifcool.addictcontrol.Account.Account;
import org.d3ifcool.addictcontrol.Database.DatabaseAdapter;
import org.d3ifcool.addictcontrol.Database.DatabaseFirebase;
import org.d3ifcool.addictcontrol.Database.TimeWorkContract;
import org.d3ifcool.addictcontrol.Database.User;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.PendingIntent.getActivity;

public class AccountAcivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    private TextView mUsername;
    private  ImageView mAvatar ;
    private TextView myQuote;
    private TextView mTypeAccount;
    private  Account mAccount;

    private String username;

    private static final int PICK_IMAGE = 100;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_acivity);

        //call class account and get data from database in showAcccount
        getLoaderManager().initLoader(1,null ,  this);

        //initial all component in layout account_activiy
        mAvatar = (ImageView) findViewById(R.id.avatar_image_view) ; //is an avatar image
        mUsername = (TextView) findViewById(R.id.username_text_view) ;// is a username account
        myQuote = (TextView) findViewById(R.id.quote_text_view); // is a quote from user
        mTypeAccount = (TextView) findViewById(R.id.type_text_view);
        ////
        FirebaseStorage firebaseStorage;
        StorageReference storageReference;
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        StorageReference storageRef = firebaseStorage.getReference();


        DatabaseFirebase databaseFirebase = new DatabaseFirebase(this);
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
        Account account = databaseAdapter.getAccount();
        viewUser(account.getmUsername());
        mTypeAccount.setText(account.getmTypeAccount());

    }
    public  void  viewUser(String username) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("usr_"+username);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                Map<String,Object> map =  dataSnapshot.getValue(genericTypeIndicator);
                try {
                    String url = (String) map.get("UUIDImage");
                    //Picasso.get().load(url).into(mAvatar);
                    Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(url)
                            .into(mAvatar);
                }catch (NullPointerException e) {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, SettingsActivity.class));
        return super.onOptionsItemSelected(item);
    }

    //event on Click when user want to change the avatar
    public void changeAvatar(View view) {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

        // start cropping activity for pre-acquired image saved on the device
        // CropImage.activity(imageUri)
        //       .start(this);


        //galery();


        /*
        //make imageview array to save all image avatar
        ImageView avatar[] = new ImageView[4];
        final int avatarImage[] = {R.drawable.avatar_satu,R.drawable.avatar_dua,R.drawable.avatar_tiga,R.drawable.avatar_empat};
        ///

        //make alert dialog
        AlertDialog.Builder showListAvatar = new AlertDialog.Builder(this);
        //get layout from avatar_list
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.avatar_list,null);
        //set content alert dialog
        showListAvatar.setView(dialogView);
        //set title alert dialog
        showListAvatar.setTitle(R.string.choose_avatar);
        //show alert dialog
        final AlertDialog alertDialog = showListAvatar.create();
        alertDialog.show();

        //set ImageView array for each component in avatar list
        avatar[0] = (ImageView) dialogView.findViewById(R.id.avatar1_image_view);
        avatar[1] = (ImageView) dialogView.findViewById(R.id.avatar2_image_view);
        avatar[2] = (ImageView) dialogView.findViewById(R.id.avatar3_image_view);
        avatar[3] = (ImageView) dialogView.findViewById(R.id.avatar4_image_view);

        //set image for variabel avatar
        for(int n=0;n<avatar.length;n++){
            final int finalN = n;//to save variabel n
            //set on click listener for each image view
            avatar[n].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //hide alert dialog
                    alertDialog.hide();
                    //make variabel account to save data account from database
                    Account account = mAccount;

                    //set image from avatarImageView
                    CircleImageView avatarImageView = (CircleImageView) findViewById(R.id.avatar_image_view);
                    avatarImageView.setImageResource(avatarImage[finalN]);
                    //send data update account  in database
                    account.setmImage(avatarImage[finalN]);
                    sendToDatabase(account,account.getmUsername());
                }
            });
        }
        */

    }

    //this method is to save data Account from Database
    public void sendToDatabase(Account account,String currUsername){
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
        databaseAdapter.updateAccount(account,currUsername);
    }

    //this method is to get data account from database
    public Account showAccount(){
        Account account=null ;

        //DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
        //account = databaseAdapter.getAccount();
        return account;
    }

    //method to edit username
    public void editUsername(View view) {
        //create an alert dialog
        AlertDialog.Builder editUsername = new AlertDialog.Builder(this);
        //get layout edit_username
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_username,null);
        //set editText with mUsername
        EditText username  = (EditText) dialogView.findViewById(R.id.username_edit_text);
        username.setText(mUsername.getText());
        //set content alert dialog
        editUsername.setView(dialogView);
        //set title alert dialog
        editUsername.setTitle("edit nama user");

        //set positive button in alert dialog
        editUsername.setPositiveButton("edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    //set Text View mUsername from variabel edit
                    EditText edit = (EditText) dialogView.findViewById(R.id.username_edit_text);
                    //get data account from database
                    Account account = mAccount;


                    DatabaseFirebase databaseFirebase = new DatabaseFirebase();
                    databaseFirebase.updateUser(account.getmUsername(),
                            new User(
                                    edit.getText().toString(),
                                    null,
                                    null,
                                    null,
                                    null,
                                    false
                            ));


                    //save data account to database
                    account.setmUsername(edit.getText().toString());
                    DatabaseAdapter databaseAdapter = new DatabaseAdapter(AccountAcivity.this);
                    databaseAdapter.updateAccount(account, edit.getText().toString());


                    mUsername.setText(edit.getText().toString());
                }catch (IllegalArgumentException e) {
                    messageDialog("Nama User Tidak Boleh Kosong");
                }

            }
        });

        //set Negative Button in alert dialog
        editUsername.setNegativeButton("urung", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //back to AccountActivity
            }
        });

        //show alert dialog
        AlertDialog alertDialog = editUsername.create();
        alertDialog.show();
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                TimeWorkContract.AccountEntry.KEY_USERNAME,
                TimeWorkContract.AccountEntry.KEY_IMAGE,
                TimeWorkContract.AccountEntry.KEY_TYPE_ACCOUNT,
                TimeWorkContract.AccountEntry.KEY_MY_PASSWORD
        };

        return new CursorLoader(
                this,
                TimeWorkContract.AccountEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        Account account = null;

        int indexColumnUsername = cursor.getColumnIndex(TimeWorkContract.AccountEntry.KEY_USERNAME);
        int indexColumnImage = cursor.getColumnIndex(TimeWorkContract.AccountEntry.KEY_IMAGE);
        int indexColumnTypeAccount = cursor.getColumnIndex(TimeWorkContract.AccountEntry.KEY_TYPE_ACCOUNT);
        int indexColumnMyqUOTE = cursor.getColumnIndex(TimeWorkContract.AccountEntry.KEY_MY_PASSWORD);

        if (cursor.moveToFirst()) {
            do {
                account = new Account(
                        cursor.getString(indexColumnUsername),
                        cursor.getString(indexColumnImage),
                        cursor.getString(indexColumnTypeAccount),
                        cursor.getString(indexColumnMyqUOTE));
            } while (cursor.moveToNext());
        }


        //show data account in component layout account_activity
        mUsername.setText(account.getmUsername().toString());
        username = account.getmUsername().toString();

        try{
            mAvatar.setImageURI(Uri.parse(account.getmImage()));
        }catch (NullPointerException e) {

        }
        myQuote.setText("****");
        ///

        mAccount =account;

        cursor.close();

    }

    public void galery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE)
        {
            imageUri = data.getData();
            mAvatar.setImageURI(imageUri);
        }
        */
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                mAvatar.setImageURI(resultUri);

                Account account = mAccount;
                account.setmImage(resultUri.toString());
                sendToDatabase(account,account.getmUsername());

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        //mCursor =null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //startActivity(new Intent(this, SettingsActivity.class));
    }

    public void LogOut(View view) {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
        databaseAdapter.deleteAccount(databaseAdapter.getAccount().getmUsername());
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

    public void editPassword(View view) {
        //create an alert dialog
        AlertDialog.Builder editPassword = new AlertDialog.Builder(this);
        //get layout edit_username
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_password,null);
        //set editText with mUsername
        final EditText newPassword  = (EditText) dialogView.findViewById(R.id.new_password);
        final EditText oldPassword  = (EditText) dialogView.findViewById(R.id.Current_password);

        //set content alert dialog
        editPassword.setView(dialogView);
        //set title alert dialog
        editPassword.setTitle("edit password");

        //set positive button in alert dialog
        editPassword.setPositiveButton("edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    //set Text View mUsername from variabel edit
                    EditText edit = (EditText) dialogView.findViewById(R.id.username_edit_text);
                    //get data account from database
                    Account account = mAccount;


                    if(newPassword.getText().toString().trim().equals("") ||
                            oldPassword.getText().toString().trim().equals("")){
                        messageDialog("Password Baru Atau Password Lama Kosong  ");
                        return;
                    }else if (account.getQuote().equals(oldPassword.getText().toString())) {

                        DetectInternetConnectiom detectInternetConnectiom = new DetectInternetConnectiom(AccountAcivity.this);

                        if(detectInternetConnectiom.checkInternetConnection()) {
                            DatabaseFirebase databaseFirebase = new DatabaseFirebase();
                            databaseFirebase.updateUser(account.getmUsername(),
                                    new User(
                                            null,
                                            newPassword.getText().toString(),
                                            null,
                                            null,
                                            null,
                                            false
                                    ));

                            //save data account to database
                            account.setQuote(newPassword.getText().toString());
                            DatabaseAdapter databaseAdapter = new DatabaseAdapter(AccountAcivity.this);
                            databaseAdapter.updateAccount(account, account.getmUsername());

                            messageDialog("Password Berhasil Diubah");
                        }else{
                            messageDialog("Gagal Mengganti Password, Periksa Internet Anda");
                        }

                    }else{
                        messageDialog("Password Lama Tidak Sesuai");
                    }


                }catch(Exception  e) {
                }

            }
        });

        //set Negative Button in alert dialog
        editPassword.setNegativeButton("urung", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //back to AccountActivity
            }
        });

        //show alert dialog
        AlertDialog alertDialog = editPassword.create();
        alertDialog.show();

    }


    private void messageDialog(String text) {

        final AlertDialog.Builder message = new AlertDialog.Builder(this);
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
