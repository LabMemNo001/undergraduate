package com.example.stevensu.asdasdada;


import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;


/**
 * Created by Steven Su on 2016/8/5.
 * 接收消息、指令、响应指令
 */
public class ReceiveMsg extends Service{
    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;
    private PowerManager pm;
    private PowerManager.WakeLock wl;
    private InetAddress ia=null;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("ReceiveStart2");
                Client.instance();
                StartReceiver();
            }
        });
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }
    public void onCreate(){
        System.out.println("ReceiveStart1");
        super.onCreate();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onDestroy()
    {

        System.out.println("---onDestory---");
        Client.instance().Destroy();
        super.onDestroy();

    }
    public class Rreceiver extends Thread
    {
        public void run()
        {
            if(!Data.CONN) onDestroy();
            while(Data.CONN)
            {
                String result=Client.instance().CReceive();
                if(result!=null&&result.length()>0)
                {

                    if(result.contains( "%Call@")){
                        String phone=result.substring(result.indexOf("@")+1);
                        showup("新指令！拨打电话："+phone);
                        Tel(phone);
                    }
                    else if(result.equals("%Photo")){
                        showup("新指令！调用相机！");
                        Camera();
                    }
                    else if(result.contains("%SentTextMsg%")){
                        String phone=result.substring(result.indexOf("%号")+2,result.indexOf("码%"));
                        String txt=result.substring(result.indexOf("%内")+2,result.indexOf("容%"));
                        showup("新指令！向此号码："+phone+"发送短信！");
                        SendTextMsg(phone, txt);
                    }
                    else if(result.contains( "@has@left!@") ) {
                        showup("断开连接！");
                        Data.CONN=false;
                        //Client.uninstance();
                    }
                    else {showup("来自PC控制端："+result);}
                }

            }
            //onDestroy();
        }
    }



    private void StartReceiver() {
        // TODO Auto-generated method stub
        Rreceiver a=new Rreceiver();
        a.start();
        System.out.println("start listen");
    }
    public void showup(String s){
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent intent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        Notification notify3 = new Notification.Builder(this)
                .setSmallIcon(R.drawable.s233)
                .setTicker("您有新消息！")
                .setContentTitle("新消息：")
                .setContentText(s)
                .setContentIntent(intent).setNumber(1).build();
        notify3.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(1, notify3);

    }
    private void Tel(String number){
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        wakeAndUnlock(true);
    }

    private void Camera() {
        Intent intent = new Intent();
        intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        wakeAndUnlock(true);
    }
    private void SendTextMsg(String phone,String context){
        SmsManager manager = SmsManager.getDefault();
        ArrayList<String> list = manager.divideMessage(context);  //因为一条短信有字数限制，因此要将长短信拆分
        for(String text:list){
            manager.sendTextMessage(phone, null, text, null, null);
        }
        showup("向" + phone + "发送短信成功！");
        wakeAndUnlock(true);
    }
    private void wakeAndUnlock(boolean b)
    {
        if(b)
        {
            //获取电源管理器对象
            pm=(PowerManager) getSystemService(Context.POWER_SERVICE);

            //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
            wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");

            //点亮屏幕
            wl.acquire();

            //得到键盘锁管理器对象
            km= (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
            kl = km.newKeyguardLock("unLock");

            //解锁
            kl.disableKeyguard();
        }
        else
        {
            //锁屏
            kl.reenableKeyguard();

            //释放wakeLock，关灯
            wl.release();
        }

    }
}
