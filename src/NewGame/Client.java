package NewGame;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.*;

/**
 * 客户端
 */
public class Client extends JFrame implements Runnable {

    private Socket socket;//连接服务器
    private DataInputStream dis;//数据接收
    private DataOutputStream dos;//数据传送

    private GamePanel mePanel;//自己的面板
    private GamePanel enemyPanel;//对方的面板

    private Client() {

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
     */
    public static void main(String[] args) {
		if (args.length > 0) {
			Constant.ServerIp = args[0];
			System.out.printf("您输入的ip地址为:%s\n", args[0]);
		} else {
			System.out.println("您默认了本地服务器连接\n");
		}

        new Client();
    }

    private void init() {
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
                info = dis.readUTF();
                String[] split = info.split("_");
                switch (split.length) {
                    case 1:
                        panelClass.getMethod(split[0]).invoke(enemyPanel);
                        break;
                    case 2:
                        Integer num1 = Integer.valueOf(split[1]);
                        panelClass.getMethod(split[0], int.class).invoke(enemyPanel, num1);
                        break;
                    case 3:
                        Integer num2 = Integer.valueOf(split[1]);
                        Integer num3 = Integer.valueOf(split[2]);
                        panelClass.getMethod(split[0], int.class, int.class).invoke(enemyPanel, num2, num3);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("对方断开了连接");
                destroy();
                break;
            }
        }
    }

    private void destroy() {
        this.dispose();//窗体销毁
        try {
            dis.close();
            dos.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}