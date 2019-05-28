package test;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame implements Runnable {

    private ServerSocket serverSocket;//建立服务器连接
    private Socket socket;//连接客户端
    private DataInputStream dis;//数据接收
    private DataOutputStream dos;//数据传送

    private GamePanel mePanel;//自己的面板
    private GamePanel enemyPanel;//对方的面板

    Server() {

        try {//初始化连接
            serverSocket = new ServerSocket(Constant.ServerPort);
            socket = serverSocket.accept();
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println(Constant.ServerPort + "被占用，请设置新的端口号");
            e.printStackTrace();
        }

        init();//初始化
        new Thread(this).start();

        this.addKeyListener(new KeyListen(mePanel));
        this.setVisible(true);
    }

    /**
     * 我是主方法
     *
     * @param args
     */
    public static void main(String[] args) {
        new Server();
    }

    public void init() {
        Dimension ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int JFrame_X = (int) (ScreenSize.getWidth() - Constant.JFrame_Width) / 2;
        int JFrame_Y = (int) (ScreenSize.getHeight() - Constant.JFrame_Height) / 2;
        this.setTitle("我是服务器");
        this.setLocation(JFrame_X, JFrame_Y);
        this.setLayout(null);
        this.setSize(Constant.JFrame_Width, Constant.JFrame_Height);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mePanel = new GamePanel(dos, true, Constant.ServerId);
        mePanel.setLocation(Constant.x1, Constant.y1);
        this.add(InfoPanel.me);
        enemyPanel = new GamePanel(null, false, Constant.ClientId);
        enemyPanel.setLocation(Constant.x3, Constant.y3);
        this.add(mePanel);
        this.add(enemyPanel);
    }

    @Override
    public void run() {
        String info;
        Class<? extends GamePanel> panelClass = enemyPanel.getClass();
        while (true) {
            try {
                if ((info = dis.readUTF()) != null) {
                    String[] split = info.split("_");
                    switch (split.length) {
                        case 1:
                            panelClass.getMethod(split[0]).invoke(enemyPanel, new Class[]{});//方法映射
                            break;
                        case 2:
                            Integer num1 = Integer.valueOf(split[1]);
                            panelClass.getMethod(split[0], new Class[]{int.class}).invoke(enemyPanel, new Object[]{num1});
                            break;
                        case 3:
                            Integer num2 = Integer.valueOf(split[1]);
                            Integer num3 = Integer.valueOf(split[2]);
                            panelClass.getMethod(split[0], new Class[]{int.class, int.class}).invoke(enemyPanel, new Object[]{num2, num3});
                            break;
                    }
                }
            } catch (Exception e) {
                System.out.println("对方断开了连接");
                break;
            }
        }
    }
}