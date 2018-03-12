package com.example.stevensu.text;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private EditText contextEt = null;
    private EditText phoneEt = null;
    private Button send = null;
    public TextView TextMsg = null;
    public TextView Name = null;
    public TextView Number = null;
    public TextView Time = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        contextEt = (EditText) findViewById(R.id.context);
        phoneEt = (EditText) findViewById(R.id.phone);
        send = (Button) findViewById(R.id.send);
        TextMsg = (TextView) findViewById(R.id.TextMsg);
        Name = (TextView) findViewById(R.id.name);
        Number = (TextView) findViewById(R.id.number);
        Time = (TextView) findViewById(R.id.time);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendTextMsg();
            }
        });


    }
    protected void onResume() {
        super.onResume();
        registerReceiver(SMSBroadcastReceiver, new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void SendTextMsg() {
        String phone = phoneEt.getText().toString();
        String context = contextEt.getText().toString();
        SmsManager manager = SmsManager.getDefault();
        ArrayList<String> list = manager.divideMessage(context);  //因为一条短信有字数限制，因此要将长短信拆分
        for (String text : list) {
            manager.sendTextMessage(phone, null, text, null, null);
        }
        Toast.makeText(getApplicationContext(), "发送完毕", Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver SMSBroadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("SimpleDateFormat")
        @Override
        public void onReceive(Context context, Intent intent) {
            SmsMessage msg = null;
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object p : pdusObj) {
                    msg = SmsMessage.createFromPdu((byte[]) p);
                    String msgTxt = msg.getMessageBody();
                    Date date = new Date(msg.getTimestampMillis());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String receiveTime = format.format(date);
                    String senderNumber = msg.getOriginatingAddress();
                    Toast.makeText(context, "收到短信", Toast.LENGTH_LONG).show();
                    Time.setText("接收时间" + receiveTime);
                    Number.setText("发送人" + senderNumber);
                    TextMsg.setText("短信内容" + msgTxt);
                }
                return;
            }
        }
    };
}
