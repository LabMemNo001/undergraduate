import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.*;
/**
 * Created by Steven Su on 2016/8/2.
 */
public class Servertest {
    private JTextArea textArea1;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JButton 发送短信Button;
    private JButton 设置号码Button;
    private JButton 发送消息Button;
    private JButton 相机Button;
    private JButton 拨打电话Button;
    private JButton 初始化Button;
    private JPanel Servertest;
    public Selector sel = null;
    public ServerSocketChannel server = null;
    public SocketChannel socket = null;
    public int thisport = 4900;
    private String result = null;
    private Hashtable<String, SocketChannel> userlists;
    private SocketChannel readingsocket = null;
    public String socketname=null;
    private String PhoneNumber=null;
    public Boolean connect=false;
    public InetAddress ia=null;
    /*public Servertest(int port,final JTextArea txt){

        thisport=port;
        textArea1=txt;
        textArea1.append("Inside startserver "+"\n");
    };*/
    public Servertest() {
        初始化Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea1.setText("");
                textField3.setText("");
                textField2.setText("");
                textField1.setText("");
                textArea1.append("Local Ip Address is "+ia.getHostAddress()+"\n");
            }
        });

        发送消息Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event){

                String msg=textField3.getText().toString();
                try {
                    sendMessage(msg);
                    if(connect){
                        textArea1.append("消息已发送：" + msg + "\n");
                        textField3.setText("");
                    }
                }
                catch (Exception ex){
                    textArea1.append(ex.toString()+'\n');
                }
            }
        });
        设置号码Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PhoneNumber=textField2.getText().toString();
                if(PhoneNumber!=null){
                    textArea1.append("号码已设置：" + PhoneNumber + "\n");
                }
            }
        });
        发送短信Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg=textField1.getText().toString();
                if(PhoneNumber!=null){
                    String TextMsg="%SentTextMsg%"+"%号"+PhoneNumber+"码"+"%内"+msg+"容%";
                    try {
                        sendMessage(TextMsg);
                        if(connect){
                            textArea1.append("短信已发送！" + "\n");
                            textField1.setText("");
                        }
                    }
                    catch (Exception ex){
                        textArea1.append(ex.toString()+'\n');
                    }
                }
                else{
                    textArea1.append("未设置号码！！" + "\n");
                }
            }
        });
        相机Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendMessage("%Photo");
                    if (connect) {
                        textArea1.append("命令已发送！" + "\n");
                        textField1.setText("");
                    }
                } catch (Exception ex) {
                    textArea1.append(ex.toString() + '\n');
                }
            }
        });
        拨打电话Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(PhoneNumber!=null){
                    String PhoneMsg="%Call@"+PhoneNumber;
                    try {
                        sendMessage(PhoneMsg);
                        if(connect){
                            textArea1.append("号码已拨出！" + "\n");
                        }
                    }
                    catch (Exception ex){
                        textArea1.append(ex.toString()+'\n');
                    }
                }
                else{
                    textArea1.append("未设置号码！！" + "\n");
                }
            }
        });

    }


    public void init() throws IOException, UnknownHostException {

        textArea1.append("Inside initialization" + "\n");
        System.out.println("Inside initialization");
        sel = Selector.open();
        server = ServerSocketChannel.open();
        server.configureBlocking(false);
        ia=ia.getLocalHost();
        String li=ia.getHostAddress();
        textArea1.append("本地IP地址为"+li+",端口号为4900"+"\n");
        System.out.println("本地IP地址为"+li+",端口号为4900");
        InetSocketAddress isa = new InetSocketAddress(li,
                thisport);
        server.socket().bind(isa);
        userlists = new Hashtable<String, SocketChannel>();
    }

    public void startServer() throws IOException {
        init();
        server.register(sel, SelectionKey.OP_ACCEPT);
        while (sel.select() > 0) {

            Set<?> readyKeys = sel.selectedKeys();
            Iterator<?> it = readyKeys.iterator();
            while (it.hasNext()) {
                SelectionKey sk = (SelectionKey) it.next();
                it.remove();
                if (sk.isAcceptable()) {

                    ServerSocketChannel ssc = (ServerSocketChannel) sk.channel();
                    socket = (SocketChannel) ssc.accept();
                    System.out.println(socket.toString());
                    /*设置为非阻塞模式*/
                    socket.configureBlocking(false);
                    socketname = socket.socket().getRemoteSocketAddress().toString();
                    socket.register(sel, SelectionKey.OP_READ);
                    sk.interestOps(SelectionKey.OP_ACCEPT);
                    userlists.put(socketname, socket);
                    System.out.println(socketname + " 已经连接了！！");
                    textArea1.append(socketname + " 已经连接了！！" + "\n");
                    connect = true;
                }
                if (sk.isReadable()) {
                    System.out.println("start write...");
                    readingsocket = (SocketChannel) sk.channel();
                    String ret = readMessage(readingsocket);
                    if (ret.equalsIgnoreCase("%phone@left!")) {
                        System.out.println("233s...");
                        sendMessage("phone@has@left!@");
                        userlists.remove(readingsocket.socket().getRemoteSocketAddress().toString());
                        System.out.println("left message:" + readingsocket.socket().getRemoteSocketAddress().toString() + " has left!");
                        textArea1.append("left message:" + readingsocket.socket().getRemoteSocketAddress().toString() + " has left!"+"\n");
                        readingsocket.close();
                        sk.cancel();

                    } else if (ret.length() > 0) {
                        //sk.cancel();
                        System.out.println("Receive msg:" + ret);//记录
                        if (ret.equals("%Connecttest%")) {
                            sendMessage("连接成功!");
                        } else {
                            textArea1.append("Receive Message:" + result + "\n");
                        }
                        //返回测试连接

                    }
                }
            }

        }
    }



    public void sendMessage(String msg) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer = ByteBuffer.wrap(msg.getBytes("UTF-8"));

        if(connect){
            Collection<SocketChannel> channels = userlists.values();
            SocketChannel sc;
            for (Object o : channels) {
                sc = (SocketChannel) o;
                sc.write(buffer);
                buffer.flip();
                System.out.println("send server msg:"+msg);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            textArea1.append("未连接到设备！无法发送指令！"+"\n");
        }

    }

    public String readMessage(SocketChannel sc) {
        ByteBuffer buf = ByteBuffer.allocate(1024);
        try {
            sc.read(buf);
            buf.flip();
            Charset charset = Charset.forName("UTF-8");
            CharsetDecoder decoder = charset.newDecoder();
            CharBuffer charBuffer = decoder.decode(buf);
            result = charBuffer.toString();
            System.out.println(result);
        } catch (IOException e) {
            result = "phone@%has@left!";
        }
        return result;
    }

    public static void main(String[] args) {

        Servertest St= new Servertest();

        JFrame frame = new JFrame("Servertest");
        frame.setContentPane(St.Servertest);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        try {
            St.startServer();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }


}
