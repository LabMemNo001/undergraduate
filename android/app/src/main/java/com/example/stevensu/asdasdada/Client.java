package com.example.stevensu.asdasdada;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by Steven Su on 2016/8/5.
 * 客户端类，定义了socketChannel的各类使用方法，主要为收发信息
 */
public class Client {
    // 信道选择器
    private Selector selector=null;

    // 与服务器通信的信道
    SocketChannel socketChannel=null;

    // 要连接的服务器Ip地址
    private String hostIp;

    // 要连接的远程服务器在监听的端口
    private int hostListenningPort;
    public static Client client=null;
    public boolean done=false;

    public Client(String s,int n){
        this.hostIp=s;
        this.hostListenningPort=n;
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//传输地址端口

    public static synchronized Client instance(){
        if(client==null||!client.ConnectTest()){
            client=new Client(Data.IP,Data.PORT);
        }
        return client;
    }
    public static Client uninstance(){
        if(client!=null){
            client.Destroy();
        }
        return null;
    }
    public static void SetAddress(String s,int n){
        Data.IP=s;
        Data.PORT=n;
    }
    public void init() throws IOException {
        try
        {

            // 打开监听信道并设置为非阻塞模式
            socketChannel = SocketChannel.open(new InetSocketAddress(Data.IP, Data.PORT));
            socketChannel.configureBlocking(false);
            System.out.println("tryinit");
            if (socketChannel != null)
            {
                if(socketChannel.isConnected()){System.out.println("init");}
                else System.out.println("noinit");
                socketChannel.socket().setTcpNoDelay(false);
                socketChannel.socket().setKeepAlive(true);

                // 打开并注册选择器到信道
                selector = Selector.open();
                Data.CONN=true;
                if (selector != null)
                {
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    done = true;
                }
            }
        } finally
        {
            if (!done && selector != null)
            {
                selector.close();
            }
            if (!done) {
                if (socketChannel != null) socketChannel.close();
                Data.CONN = false;
                //Toast.makeText(context.getApplicationContext(), "无法连接！
                System.out.println("无法连接");
            }
        }

    }
    public void Destroy(){
        try
        {
            if (socketChannel != null)
            {
                socketChannel.close();
                Data.CONN=false;
            }

        } catch (IOException e)
        {

        }
        try
        {
            if (selector != null)
            {
                selector.close();
            }
        } catch (IOException e)
        {
        }
    }
    public void CSend(String s){
        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
        System.out.println("CSend");
        try {
            writeBuffer=ByteBuffer.wrap(s.getBytes("UTF-8"));
            socketChannel.write(writeBuffer);
            writeBuffer.flip();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String CReceive(){
        String result = null;
        ByteBuffer buf = ByteBuffer.allocate(1024);
        try{
            socketChannel.read(buf);
            buf.flip();
            Charset charset=Charset.forName("UTF-8");
            CharsetDecoder decoder=charset.newDecoder();
            CharBuffer charbuffer;
            charbuffer=decoder.decode(buf);
            result=charbuffer.toString();
        }catch(CharacterCodingException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    public boolean ConnectTest(){
        if(socketChannel!=null){

                return socketChannel.isConnected();
        }
        else {
            return false;
        }
    }
}