package com.example.stevensu.asdasdada;

import android.widget.Toast;

/**
 * Created by Steven Su on 2016/8/11.
 * 自定义Toast类，使得Toast可以在static类中使用
 */
public class MyToast {
    public static void MyToast(int i) {
        switch (i) {
            case(0):
                Toast.makeText(MyApplication.getContext(), "无法连接到PC！！", Toast.LENGTH_LONG).show();
                break;
            case(1):
                Toast.makeText(MyApplication.getContext(), "连接成功！！", Toast.LENGTH_LONG).show();
                break;
            case(2):
                Toast.makeText(MyApplication.getContext(), "未设定IP地址或端口号！！", Toast.LENGTH_LONG).show();
                break;
            case(3):
                Toast.makeText(MyApplication.getContext(), "设定成功！！", Toast.LENGTH_LONG).show();
                break;
            case (4):
                Toast.makeText(MyApplication.getContext(), "未连接到PC！！", Toast.LENGTH_LONG).show();
                break;
            case(5):
                Toast.makeText(MyApplication.getContext(), "IP地址或端口号为空！！", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
