package com.example.stevensu.asdasdada;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ConnService extends Service {
    private Connect connect=null;
    private SendMsg sendMsg = null;
    private ReceiveMsg receiveMsg = null;
    public ConnService() {
    }
    public int onStartCommand(Intent intent, int flags, int startId){
        //Client.SetAddress("192.168.56.1", 4900);
        //Client.instance();
        connect.start();
        //sendMsg.start();
        return super.onStartCommand(intent, flags, startId);
    }
    public void onCreate(){
        super.onCreate();
        connect=new Connect();
        //sendMsg=new SendMsg();

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public void CatchMsg(String s){
        System.out.println("catch");
        sendMsg.UpdateMsg(s);

    }
    public void showup(String s){
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, MainActivity.class);

                /*add the followed two lines to resume the app same with previous statues*/
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                /**/
        PendingIntent intent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        Notification notify3 = new Notification.Builder(this)
                .setSmallIcon(R.drawable.s233)
                .setTicker("您有新短消息，请注意查收！")
                .setContentTitle("新消息:")
                .setContentText(s)
                .setContentIntent(intent).setNumber(1).build();
        notify3.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(1, notify3);

    }
}
