package NewGame;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * 客户端
 */
public class Client extends JFrame implements Runnable {

    private Socket socket;//连接服务器
    private DataInputStream dis;//数据接收
    private DataOutputStream dos;//数据传送

    private GamePanel mePanel;//自己的面板
    private GamePanel enemyPanel;//对方的面板

    Client() {

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
        new Client();
    }

    public void init() {
        Dimension ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int JFrame_X = (int) (ScreenSize.getWidth() - Constant.JFrame_Width) / 2;
        int JFrame_Y = (int) (ScreenSize.getHeight() - Constant.JFrame_Height) / 2;
        this.setTitle("我是客户端");
        this.setLocation(JFrame_X, JFrame_Y);
        this.setLayout(null);
        this.setSize(Constant.JFrame_Width, Constant.JFrame_Height);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mePanel = new GamePanel(dos, true, Constant.ClientId);
        mePanel.setLocation(Constant.x1, Constant.y1);
        this.add(InfoPanel.me);
        enemyPanel = new GamePanel(null, false, Constant.ServerId);
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