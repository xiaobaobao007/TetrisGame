package NewGame;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

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

		while (true) {//初始化连接，一直循环直到彼此建立通讯
			try {
				socket = new Socket(Constant.ServerIp, Constant.ServerPort);//根据服务器ip和端口号与对方建立连接
				dis = new DataInputStream(socket.getInputStream());//拿到服务器通信给本地的数据传输流，即收件箱
				dos = new DataOutputStream(socket.getOutputStream());//输出流，向此流写入信息，即发件箱
				break;
			} catch (Exception e) {
				System.out.println("等待服务器启动中............");
				try {
					TimeUnit.SECONDS.sleep(5);//建立失败后等待5秒钟，再次向指定ip和端口建立连接
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}

		init();//初始化

		new Thread(this).start();//创建线程，因为此类继承runnable所以重写run方法，此线程主要用来网络通信

		this.addKeyListener(new KeyListen(mePanel));//键盘监听器，将键盘监听器注册在此frame中

		this.setVisible(true);//使frame显现出来
	}

	/**
	 * 我是主方法
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			Constant.ServerIp = args[0];
			System.out.printf("您输入的ip地址为:%s\n", args[0]);
		} else {
			System.out.println("您默认了本地服务器连接");
		}

		new Client();
	}

	private void init() {
		Dimension ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();//获取当前屏幕的长和宽
		int JFrame_X = (int) (ScreenSize.getWidth() - Constant.JFrame_Width) / 2;
		int JFrame_Y = (int) (ScreenSize.getHeight() - Constant.JFrame_Height) / 2;
		this.setTitle("我是客户端");
		if ("localhost".equals(Constant.ServerIp)) {
			this.setLocation(40 + Constant.JFrame_Width, JFrame_Y);//设置运行窗体在屏幕上的位置
		} else {
			this.setLocation(JFrame_X, JFrame_Y);
		}
		this.setLayout(null);//置空布局，使得自定义的panel坐标生效
		this.setSize(Constant.JFrame_Width, Constant.JFrame_Height);//设置窗体大小
		this.setResizable(false);//禁止放大缩小修改尺寸
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//窗体关闭后，应用程序也同时关闭
		mePanel = new GamePanel(dos, true);//创建客户端运行类
		mePanel.setLocation(Constant.x1, Constant.y1);//设置客户端panel在frame中的位置
		this.add(InfoPanel.me);//添加信息面板到主frame
		enemyPanel = new GamePanel(null, false);//创建对方运行类
		enemyPanel.setLocation(Constant.x3, Constant.y3);//设置对方panel在frame中的位置
		this.add(mePanel);//添加我的panel到主frame
		this.add(enemyPanel);//添加对方panel到主frame
	}

	/**
	 * 被重写的方法
	 */
	@Override
	public void run() {
		String info;
		Class<? extends GamePanel> panelClass = enemyPanel.getClass();
		while (true) {//一直循环，保证程序一直运行
			try {
				info = dis.readUTF();//从输入流获取对方发送的信息
				String[] split = info.split("_");//通过下划线进行分割
				switch (split.length) {//根据分割后不同的数量执行不同的操作
					case 1:
						if ("stop".equals(split[0])) {
							mePanel.getClass().getMethod(split[0]).invoke(mePanel);//反射
						} else {
							panelClass.getMethod(split[0]).invoke(enemyPanel);//反射
						}
						break;
					case 2:
						Integer num1 = Integer.valueOf(split[1]);
						panelClass.getMethod(split[0], int.class).invoke(enemyPanel, num1);//反射
						break;
					case 3:
						Integer num2 = Integer.valueOf(split[1]);
						Integer num3 = Integer.valueOf(split[2]);
						panelClass.getMethod(split[0], int.class, int.class).invoke(enemyPanel, num2, num3);//反射
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

	/**
	 * 游戏结束，资源进行回收
	 */
	private void destroy() {
		this.dispose();//窗体销毁
		try {
			dis.close();//关闭输入流
			dos.close();//关闭输出流
			socket.close();//连接关闭
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}