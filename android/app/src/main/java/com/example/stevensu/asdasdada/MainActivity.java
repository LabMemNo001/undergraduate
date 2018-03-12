package com.example.stevensu.asdasdada;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
/**
 * Created by Steven Su on 2016/8/5.
 * UI线程，按钮、文本接口，启动，设定IP地址与端口号，输入文本
 */

public class MainActivity extends AppCompatActivity {

    public Button Conn = null;
    public Button Send = null;
    public Button Test1 = null;
    public Button Test2 = null;
    public Button Set = null;
    public EditText editText = null;
    public EditText IPedt = null;
    public EditText PORTedt = null;
    public boolean SET = false;
    public TextView textView = null;
    private String msg = null;
    private ThreadManager threadManager = null;
    private Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FindID();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "新手练习程序", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IPedt.getText().toString() != null && PORTedt.getText().toString() != null) {
                    Data.IP = IPedt.getText().toString();
                    Data.PORT = Integer.parseInt(PORTedt.getText().toString());
                }
                if (Data.PORT != 0 && Data.IP != null) {
                    SET = true;
                    MyToast.MyToast(3);
                } else MyToast.MyToast(5);
            }
        });
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Sendclick");
                msg = editText.getText().toString();
                if (Data.CONN) {
                    threadManager.CatchMsg(msg);
                } else
                    MyToast.MyToast(4);

            }
        });
        Conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Connectclick");
                if (SET) {
                    if (!Data.CONN) {
                        threadManager = new ThreadManager(Data.IP, Data.PORT);
                        threadManager.StartAct();
                        intent = new Intent(MainActivity.this, ReceiveMsg.class);
                        MainActivity.this.startService(intent);
                        threadManager.CatchMsg("%Connecttest%");

                    }
                    else {
                        Thread thread=new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Client.instance();
                                Client.instance().CSend("%phone@left!");
                            }
                        });
                        thread.start();//不知道为什么调用sendMsg的方法不行，是延时的问题吗？模拟器完成不了
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                //延时等待接收反馈
                            }
                        }, 500000);
                        MainActivity.this.stopService(intent);
                        threadManager.StopAct();

                    }
                } else {
                    MyToast.MyToast(2);
                }

            }
        });
        Test1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data.IP="192.168.100.100";
                Data.PORT=4900;
                SET=true;

            }
        });
        Test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Test2");
                try {
                    Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot "});  //关机功能测试，此功能目前在真机上无法使用。。
                    proc.waitFor();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public boolean FindID() {
        //控件对应ID
        Send = (Button) findViewById(R.id.send);
        Conn = (Button) findViewById(R.id.conn);
        Test1 = (Button) findViewById(R.id.test1);
        Test2 = (Button) findViewById(R.id.test2);
        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView);
        Set = (Button) findViewById(R.id.set);
        IPedt = (EditText) findViewById(R.id.IP);
        PORTedt = (EditText) findViewById(R.id.PORT);
        return true;
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


    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            MyToast.MyToast(msg.what);
        }
    };
}
