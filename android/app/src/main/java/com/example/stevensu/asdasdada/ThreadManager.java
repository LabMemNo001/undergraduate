package com.example.stevensu.asdasdada;

import android.os.Handler;

/**
 * Created by Steven Su on 2016/8/5.
 * 线程管理类，发送、连接两线程的启动、结束接口
 */
public class ThreadManager {
    private static ThreadManager Manager=null;
    private String ip=null;
    private int port=0;
    private int n=0;
    private static ThreadManager threadManager=null;
    private SendMsg sendMsg = null;
    //private ReceiveMsg receiveMsg = null;
    private Connect connect=null;
    public static ThreadManager instance(){
        if(threadManager==null){
            threadManager=new ThreadManager(Data.IP,Data.PORT);
        }
        return threadManager;
    }
    public ThreadManager(String ip, int port) {
        this.ip=ip;
        this.port=port;
        Client.SetAddress(ip, port);
        connect=new Connect();
        sendMsg=new SendMsg();
    }

    public void StartAct() {
        connect.start();
        connect.SetStart();
        sendMsg.start();
        sendMsg.SetStart();
    }
    public void StopAct(){

       /* String msg = "phone@%has@left!";
        SendMsg stop=new SendMsg();
        stop.start();
        stop.SetStart();
        stop.UpdateMsg(msg);*/

        sendMsg.SetStop();
        connect.SetStop();
        //stop.SetStop();
    }
    public void CatchMsg(String s){
        System.out.println("catch");
        sendMsg.UpdateMsg(s);

    }
}
