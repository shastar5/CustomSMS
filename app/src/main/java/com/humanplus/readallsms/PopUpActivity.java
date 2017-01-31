package com.humanplus.readallsms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class PopUpActivity extends Activity {
    private String output = "";
    private String input = "";
    private EditText editText;
    private String address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pop_up);
        editText = (EditText)findViewById(R.id.SMS_Input);

        // Get msg content
        Intent intent = getIntent();
        output = intent.getExtras().getString("msg");
        address = intent.getExtras().getString("addr");
    }

    protected void onClickSend(View v) {
        switch(v.getId()) {
            case R.id.SMS_SEND_BUTTON:
                input = editText.getText().toString();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(address, null, input, null, null);
                finish();
                break;
        }
    }
}
