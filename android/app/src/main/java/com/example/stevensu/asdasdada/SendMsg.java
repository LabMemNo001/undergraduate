package com.example.stevensu.asdasdada;

/**
 * Created by Steven Su on 2016/8/5.
 * 发送线程，发送消息用
 */
public class SendMsg extends Thread {
    private boolean isStart = true;
    public String toSend=null;
    public void UpdateMsg(String s){
        System.out.println("UpdateMsg");
        toSend=s;
    }
    public void SSend(String s){
        System.out.println("SSend");
        Client.instance().CSend(s);
    }
    @Override
    public void run() {
        while(isStart){
            if(toSend!=null){
                SSend(toSend);
                toSend=null;
            }
            synchronized (this)
            {
                try
                {
                    Thread.sleep(100);

                } catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }// 发送完消息后，线程进入循环监控状态
            }
        }
    }
    public void SetStart(){
        isStart=true;
    }
    public void SetStop(){
        isStart=false;
        System.out.println("---SendStop---");
    }
}
