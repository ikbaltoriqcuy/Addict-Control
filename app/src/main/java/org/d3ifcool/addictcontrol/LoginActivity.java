package org.d3ifcool.addictcontrol;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    public void registrasi(View view) {
        startActivity(new Intent(this, CreateProfileActivity.class));
    }

    public void login(View view) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Tunggu sebentar...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final EditText user,password;
        user = findViewById(R.id.username_edit_text);
        password = findViewById(R.id.password_edit_text);

        DetectInternetConnectiom detectInternetConnectiom = new DetectInternetConnectiom(this);

        if(detectInternetConnectiom.checkInternetConnection()) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("usr_" + user.getText().toString());
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                        };
                        Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);

                        String getUser = (String) map.get("username");
                        String getPassword = (String) map.get("password");
                        String getImage = (String) map.get("UUIDImage");
                        String getTypeAccount = (String) map.get("typeAccount");


                        if (user.getText().toString().equals(getUser) &&
                                password.getText().toString().equals(getPassword)
                                ) {

                            DatabaseAdapter databaseAdapter = new DatabaseAdapter(LoginActivity.this);
                            databaseAdapter.addAccount(
                                    new Account(
                                            getUser,
                                            getImage,
                                            getTypeAccount,
                                            getPassword
                                    ));
                            progressDialog.dismiss();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            messageDialog("Password salah ");
                            progressDialog.dismiss();

                        }
                    } else {
                        messageDialog("Akun Tidak Ditemukan");
                        progressDialog.dismiss();

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }else{
            messageDialog ( "Gagal Login, Coba Cek Internet Anda");
            progressDialog.dismiss();

        }


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
