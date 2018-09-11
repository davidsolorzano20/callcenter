package com.thunderlabs.callcenter;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final int PERMISSION_REQUEST_CONTACT = 1;
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 123;
    private static final int PERMISSIONS_REQUEST_CALL = 112;
    private Context mContext = MainActivity.this;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_call:
                    if (Build.VERSION.SDK_INT >= 23) {
                        String[] PERMISSIONS = {android.Manifest.permission.CALL_PHONE};
                        if (!hasPermissions(mContext, PERMISSIONS)) {
                            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSIONS_REQUEST_CALL);
                        } else {
                            makeCall();
                        }
                    } else {
                        makeCall();
                    }
                    return true;

                case R.id.navigation_sms:
                    if (Build.VERSION.SDK_INT >= 23) {
                        String[] PERMISSIONS = {Manifest.permission.SEND_SMS};
                        if (!hasPermissions(mContext, PERMISSIONS)) {
                            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSIONS_REQUEST_SEND_SMS);
                        } else {
                            sendSMS();
                        }
                    } else {
                        sendSMS();
                    }
                    return true;
                case R.id.navigation_contact:

                    if (Build.VERSION.SDK_INT >= 23) {
                        String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
                        if (!hasPermissions(mContext, PERMISSIONS)) {
                            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_REQUEST_CONTACT);
                        } else {
                            addContacts();
                        }
                    } else {
                        addContacts();
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void makeCall() {
        String number = "+50555-25936";
        Intent call = new Intent(Intent.ACTION_CALL);
        call.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(call);
    }


    private void addContacts() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        //Por si quieres ingresar el nombre
        contactIntent.putExtra(ContactsContract.Intents.Insert.NAME, "Luis Solorzano");

        //Add number
        ArrayList<ContentValues> contactData = new ArrayList<ContentValues>();
        ContentValues phoneRow = new ContentValues();
        phoneRow.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        phoneRow.put(ContactsContract.CommonDataKinds.Phone.NUMBER, "55025936");
        contactData.add(phoneRow);
        contactIntent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, contactData);
        startActivity(contactIntent);
    }

    private void sendSMS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String smsNumber = "55025936";
        String sms = "que paso";

        Uri uri = Uri.parse("smsto:" + smsNumber);
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
        smsIntent.putExtra("sms_body", sms);
        startActivity(smsIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CONTACT) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addContacts();
            } else {
                Toast.makeText(mContext, "La aplicación no pudo guardar el contacto.", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS();
            } else {
                Toast.makeText(mContext, "La aplicación no pudo guardar el contacto.", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PERMISSIONS_REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall();
            } else {
                Toast.makeText(mContext, "La aplicación no pudo llamar.", Toast.LENGTH_LONG).show();
            }
        }

    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
