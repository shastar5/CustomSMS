package com.humanplus.readallsms;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;

import static com.humanplus.readallsms.R.id.textView;

public class PopUpActivity extends Activity {
    private String output;
    private String input = "";
    private EditText editText;
    private String address = "";

    ListView listView;
    CustomAdapter customAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pop_up);
        editText = (EditText)findViewById(R.id.SMS_Input);

        // Construct adapter and listview
        customAdapter = new CustomAdapter();
        listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(customAdapter);

        // Get msg content
        Intent intent = getIntent();
        address = intent.getExtras().getString("addr");

        readMessage(address);
    }

    private void readMessage(String digit) {
        Uri allMessage = Uri.parse("content://sms");
        ContentResolver cr = getContentResolver();
        // _id - 메세지 id
        // address - 수신자 전화번호
        // date - 수/발신 날짜(long 형태로 나옴)
        // body - 내용
        // protocol - 0이면 내가 보낸 것 null이면 내가 받은 것
        Cursor c = cr.query(allMessage,
                new String[] {"_id", "address", "date", "body", "protocol"}
                , "address='" + digit +"'", null, "date ASC");


        while(c.moveToNext()) {
            int id = c.getInt(0);
            String address = c.getString(1);
            long timestamp = c.getLong(2);
            String body = c.getString(3);
            String protocol = c.getString(4);

            // Case: 발신
            if(protocol == null) {
                customAdapter.add(body, 1);
            }
            // Case: 수신
            else {
                customAdapter.add(body, 0);
            }

        }
        c.close();
    }


    protected void onClickSend(View v) {
        switch(v.getId()) {
            case R.id.SMS_SEND_BUTTON:
                String input = editText.getText().toString();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(address, null, input, null, null);
                finish();
                break;
        }
    }
}
