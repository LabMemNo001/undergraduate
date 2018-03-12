package com.example.stevensu.asdasdada;

import android.os.Message;

/**
 * Created by Steven Su on 2016/8/5.
 * 连接线程，用于与服务器连接，并定义了一个心跳方法，维持连接
 */
public class Connect extends Thread {
    boolean Conn=false;

    public Connect()
    {
        System.out.println("ConnectCheckStart!");
        // 连接服务器
        Conn=true;

    }
    @Override
    public void run() {
        Message message=new Message();
        Client.instance();
        while (Conn){
            boolean canConnectToServer = Client.instance().ConnectTest();
            if(canConnectToServer){
                System.out.println("connect");
                //Data.CONN=true;
                //message.what=1;
                //MainActivity.mHandler.sendMessage(message);
            }
            else{
                System.out.println("disconnect");
                Data.CONN=false;
                message.what=0;
                MainActivity.mHandler.sendMessage(message);
                Conn=false;
            }
            try
            {
                Thread.sleep(10000);

            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }


    }
    public void SetStart(){
        Conn=true;
    }
    public void SetStop(){
        Conn=false;
        Data.CONN=false;
        //Client.uninstance();
        System.out.println("---ConnectStop---");
    }

}
