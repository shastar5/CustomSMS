package com.humanplus.readallsms;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    private boolean check = false;
    private int requestCode = 1;
    private final int SMS_MESSAGE = 2;
    private TextView textView;
    private EditText editText;
    private String str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);

        requestPermission(this);
    }

    protected void onClickButton(View v) {
        switch(v.getId()) {
            case R.id.button:
                // Put digit to find message
                // If protocol == 0, Sent message
                // otherwise(null) received message.
                readMessage(editText.getText().toString());
                break;
        }
    }

    private void readMessage(String digit) {
        Uri allMessage = Uri.parse("content://sms");
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(allMessage,
                new String[] {"address", "date", "body", "protocol"}
        , "address='" + digit +"'", null, "date ASC");

        String output = "";

        while(c.moveToNext()) {
            String address = c.getString(0);
            long timestamp = c.getLong(1);
            String body = c.getString(2);
            String protocol = c.getString(3);

            if(protocol == null) {
                Log.d("발신", body);
                str += "발신: "+body + "\n";
            } else {
                Log.d("수신", body);
                str += "수신: " +body + "\n";
            }

        }
        c.close();

        textView.setText(null);
        textView.setText(str);

        Intent intent = new Intent(this, PopUpActivity.class);

        // Give intent content of msg and destination number
        intent.putExtra("msg", str);
        intent.putExtra("addr", digit);
        startActivity(intent);
    }

    private void requestPermission(Activity activity) {
        final Activity currentActivity = activity;
        int permissionCheck = ContextCompat.checkSelfPermission(currentActivity, Manifest.permission.READ_SMS);

        // The case we do not need to check permissions.
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M || permissionCheck == PackageManager.PERMISSION_GRANTED) {
            check = true;
        }

        //  When denied
        if(permissionCheck == PackageManager.PERMISSION_DENIED) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
            ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

            // If we need additional explanation for using permission.
            if(ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, Manifest.permission.READ_SMS)) {

                // Pop up dialog to grant permission from user.
                AlertDialog.Builder dialog = new AlertDialog.Builder(currentActivity);
                dialog.setTitle("권한 요청")
                        .setMessage("메세지를 읽고 씁니다.")
                        .setPositiveButton("수락하기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(currentActivity,
                                        new String[]{Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS}, requestCode);
                                check = true;
                            }
                        })
                        .setNegativeButton("거절", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(currentActivity, "요청이 거절되었습니다.", LENGTH_SHORT).show();
                                return;
                            }
                        }).create().show();
                return;
            } else {
                // Request READ_CONTACT to android system
                // 최초 실행시 권한 요청
                ActivityCompat.requestPermissions(currentActivity,
                        new String[]{Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS}, requestCode);
            }
        }
    }
}
