package com.humanplus.readallsms;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST = 10;
    private boolean check = false;
    private int requestCode = 1;
    private final int SMS_MESSAGE = 2;
    private TextView textView;

    private EditText editText;
    private EditText inputText;
    private String addr;

    private BroadcastReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);
        inputText = (EditText)findViewById(R.id.SMS_Input);

        // 권한 요청
        requestPermission();

        // Register receiver for SMS_RECEIVE
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(myReceiver, intentFilter);
    }

    protected void onClickButton(View v) {
        switch(v.getId()) {
            case R.id.button:
                // Put digit to find message
                // If protocol == 0, Sent message
                // otherwise(null) received message.
                addr = editText.getText().toString();

                Intent intent = new Intent(this, PopUpActivity.class);

                // Give intent content of msg and destination number
                intent.putExtra("addr", addr);
                startActivity(intent);
                break;
        }
    }

    // 권한 요청 부분
    private void requestPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS))) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS,
                                Manifest.permission.SEND_SMS,
                                Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }
}
