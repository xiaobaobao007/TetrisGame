package test;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

@SuppressWarnings("ALL")
public class Client extends JFrame implements Runnable {

    private Socket socket;//连接服务器
    private DataInputStream dis;//数据接收
    private DataOutputStream dos;//数据传送

    private GamePanel mePanel;//自己的面板
    private GamePanel enemyPanel;//对方的面板

    Client(){
        while (true) {//初始化连接
            try {
                socket = new Socket(Constant.ServerIp, Constant.ServerPort);
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                break;
            } catch (Exception e) {
                System.out.println("等待服务器启动中............");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }

    @Override
    public void run() {
        while (true) {
            String info;
            Class<? extends GamePanel> panelClass = enemyPanel.getClass();
            try {
                if ((info = dis.readUTF()) != null) {
                    panelClass.getMethod(info).invoke(new Calculator(), new Class[]{});//方法映射
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}