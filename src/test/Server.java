package test;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("ALL")
public class Server extends JFrame implements Runnable {

    private ServerSocket serverSocket;//建立服务器连接
    private Socket socket;//连接客户端
    private DataInputStream dis;//数据接收
    private DataOutputStream dos;//数据传送

    private GamePanel mePanel;//自己的面板
    private GamePanel enemyPanel;//对方的面板

    Server(){

        init();//初始化

        try {//初始化连接
            serverSocket = new ServerSocket(Constant.ServerPort);
            socket = serverSocket.accept();
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println(Constant.ServerPort + "被占用，请设置新的端口号");
            e.printStackTrace();
        }

        this.setVisible(true);
    }

    public static void main(String[] args) {
        new Server();
    }

    public void init() {
        Dimension ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int JFrame_X = (int) (ScreenSize.getWidth() - Constant.JFrame_Width) / 2;
        int JFrame_Y = (int) (ScreenSize.getHeight() - Constant.JFrame_Height) / 2;
        this.setLocation(JFrame_X, JFrame_Y);
        this.setSize(Constant.JFrame_Width, Constant.JFrame_Height);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        while (true) {
            String info;
            Class<GamePanel> panelClass = GamePanel.class;
            try {
                if ((info = dis.readUTF()) != null) {
                    String[] split = info.split("_");
                    switch (split.length) {
                        case 1:
                            panelClass.getMethod(split[0]).invoke(new Calculator(), new Class[]{});//方法映射
                            break;
                        case 2:
                            Integer num1 = Integer.valueOf(split[1]);
                            panelClass.getMethod(split[0], new Class[]{int.class}).invoke(new Calculator(), new Object[]{num1});
                            break;
                        case 3:
                            Integer num2 = Integer.valueOf(split[1]);
                            Integer num3 = Integer.valueOf(split[2]);
                            panelClass.getMethod(split[0], new Class[]{int.class, int.class}).invoke(new Calculator(), new Object[]{num2, num3});
                            break;
                    }
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