package NewGame;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * 单个玩家的panel 游戏组成由三个panel（两个gamepanel和一个infopanel）组成一个frame
 */
public class GamePanel extends JPanel {//客户端画面

	private boolean stop;//当前游戏是否暂停的标记
	private long stopTime = 0L;//游戏暂停的时间

	private final DataOutputStream dos;//数据发送，当玩家产生操作后,通过此流输出给对方
	private final boolean canDos;//队友发来的操作后禁止再发送
	private int blockType, lastBlockType;//方块类型
	private int turnState, lastTurnState;//旋转状态
	private int x = 4;//当前方块的坐标
	private int y = 0;//当前方块的坐标
	private final int[][][] map = new int[2][13][23];//地图：12列22行。为防止越界，数组开成：13列23行
	private int delay;//游戏方块下落速度

	private final Random random;//随机数的产生

	GamePanel(DataOutputStream dos, boolean canDos) {
		this.dos = dos;
		this.canDos = canDos;
		this.setSize(Constant.Panel_Width, Constant.Panel_Height);//设置此panel的大小
		stop = false;
		random = new Random();
		if (canDos) {
			blockType = random.nextInt(7);//随机产生一个第一个下落的方块类型
			turnState = random.nextInt(4);//随机产生一个第一个下落的方块旋转方向
			lastBlockType = random.nextInt(7);//随机产生一个第二个下落的方块类型
			lastTurnState = random.nextInt(4);//随机产生一个第二个下落的方块旋转方向

			newGame(blockType, turnState);//初始化游戏

			new Thread(() -> {
				while (true) {
					if (stop) {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						down();//游戏运行中自动执行下落操作
						try {
							Thread.sleep(delay);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}

	/**
	 * 信息发送的入口
	 */
	private void sendMsg(int... parm) {
		if (!canDos || dos == null) {
			return;
		}
		StringBuilder methodName = new StringBuilder(Thread.currentThread().getStackTrace()[2].getMethodName());//写入方法名
		for (int i : parm) {
			methodName.append("_").append(i);//写入参数，并下划线分割
		}
		try {
			dos.writeUTF(methodName.toString());//输出给对方
		} catch (IOException e) {
			System.out.println("断开连接");
			System.exit(0);
		}
	}

	//开始游戏
	public void newGame(int a, int b) {
		sendMsg(a, b);
		for (int i = 0; i < 12; i++) {//走列
			for (int j = 0; j < 21; j++) {//走行
				if (i == 0 || i == 11) {//3为界面边框的格
					map[0][i][j] = -1;//设置边界
				} else {
					map[0][i][j] = 0;//设置空白区域
				}
			}
			map[0][i][21] = -1;//设置边界
		}
		blockType = a;
		turnState = b;
		delay = Constant.BlockDelay;
	}

	public void endGame(int result) {
		stop();
		if (result == 1) {
			sendMsg(0);
			switch (JOptionPane.showConfirmDialog(this, "恭喜恭喜！！！,游戏即将退出")) {
				case JOptionPane.OK_OPTION:
					break;
				case JOptionPane.NO_OPTION:
					break;
				case JOptionPane.CANCEL_OPTION:
					break;
			}
		} else {
			sendMsg(1);
			switch (JOptionPane.showConfirmDialog(this, "再接再厉吧！！！,游戏即将退出")) {
				case JOptionPane.OK_OPTION:
					break;
				case JOptionPane.NO_OPTION:
					break;
				case JOptionPane.CANCEL_OPTION:
					break;
			}
		}
		System.exit(0);
	}

	//下落中方块添加到地图中
	private void add(int x, int y, int blockType, int turnState) {
		sendMsg(x, y, blockType, turnState);
		for (int a = 0; a < 4; a++) {
			for (int b = 0; b < 4; b++) {
				if (Constant.shapes[blockType][turnState][a * 4 + b] == 1) {
					map[0][x + b + 1][y + a] = 1;
					map[1][x + b + 1][y + a] = blockType;
				}
			}
		}
		tryDelLine();
	}

	//下落
	public void down() {
		sendMsg();
		if (crash(x, y + 1, blockType, turnState) == 0) {//尝试进行消除
			add(x, y, blockType, turnState);
			nextBlock(lastBlockType, lastTurnState);//生成新的方块
		} else {
			y++;//下落中方块y坐标加1
		}
		repaint();//重新刷新界面
	}

	//决定下一方块
	public void nextBlock(int a, int b) {
		blockType = a;
		turnState = b;
		if (canDos) {
			lastBlockType = random.nextInt(7);//随机产生新的方块类型
			lastTurnState = random.nextInt(4);//随机产生新的方块旋转方向
			InfoPanel.lastBlockType = lastBlockType;
			InfoPanel.lastTurnState = lastTurnState;
			InfoPanel.me.repaint();
			sendMsg(a, b);
		}
		x = 4;
		y = 0;
		if (canDos) {
			if (crash(x, y, blockType, turnState) == 0) {//产生新的方块时，如果发生了碰撞，则游戏结束
				endGame(0);
			}
		}
	}

	//向左移动
	public void left() {
		if (x >= 0) {
			x -= crash(x - 1, y, blockType, turnState);
		}
		sendMsg();
		repaint();
	}

	//向右移动
	public void right() {
		if (x < 8) {
			x += crash(x + 1, y, blockType, turnState);
		}
		sendMsg();
		repaint();
	}

	//旋转方向
	public void turn() {
		if (crash(x, y, blockType, (turnState + 1) % 4) == 1) {
			turnState = (turnState + 1) % 4;
		}
		sendMsg();
		repaint();
	}

	/**
	 * 2019年11月28日21:34:54
	 * bug:本地暂停但是对方没有暂停
	 * 原因:对方调用了敌人的暂停,,,没有动自己的暂停
	 * 修改:stop方法特殊处理
	 */
	public void stop() {
		long now = System.currentTimeMillis();
		if (stopTime + 100 < now) {
			stopTime = now;
		} else {
			return;
		}
		sendMsg();
		stop = !stop;
	}

	//消行
	private void tryDelLine() {
		int c;
		for (int b = 0; b < 21; b++) {
			c = 0;
			for (int a = 1; a <= 10; a++) {
				c += map[0][a][b];
			}
			if (c == 10) {
				delLine(b);
				addMeScore(100);
				delay -= 30;//每消除一行增加难度
			}
		}
	}

	//增加我的分数
	public void addMeScore(int score) {
		if (canDos) {
			InfoPanel.meScore += score;
		} else {
			InfoPanel.enemyScore += score;
		}
		InfoPanel.me.repaint();
		if (InfoPanel.meScore == 3000) {
			endGame(1);
		}
	}

	//删除一行
	public void delLine(int line) {
		for (int i = line; i > 0; i--) {
			for (int j = 0; j < 11; j++) {
				map[0][j][i] = map[0][j][i - 1];
				map[1][j][i] = map[1][j][i - 1];
			}
		}
	}

	/**
	 * 判断是否发生碰撞
	 */
	private int crash(int x, int y, int blockType, int turnState) {
		for (int a = 0; a < 4; a++) {
			for (int b = 0; b < 4; b++) {
				if ((Constant.shapes[blockType][turnState][a * 4 + b] & map[0][x + b + 1][y + a]) == 1) {//如果都为1则发生碰撞
					return 0;//碰撞
				}
			}
		}
		return 1;//没有碰撞
	}

	//画图
	public void paint(Graphics g) {
		super.paint(g);

		//绘画正在下落的方块
		for (int j = 0; j < 16; j++) {
			if (Constant.shapes[blockType][turnState][j] == 1) {
				g.setColor(Constant.BlockBack);
				g.drawRect((j % 4 + x + 1) * 30, (j / 4 + y) * 30, 30, 30);
				g.setColor(Constant.NextBlock);
				g.fillRect((j % 4 + x + 1) * 30 + 1, (j / 4 + y) * 30 + 1, 29, 29);
			}
		}

		//绘画地图
		for (int i = 0; i < 12; i++) {//走列
			for (int j = 0; j < 22; j++) {//走行
				if (map[0][i][j] == -1) {
					//边界
					g.setColor(Constant.MapLine);
					g.fillRect(i * 30, j * 30, 30, 30);
				} else if (map[0][i][j] == 1) {
					paintBlock(i, j, map[1][i][j], g);
				}
			}
		}
	}

	/**
	 * 绘制每个方块
	 */
	private void paintBlock(int x, int y, int index, Graphics g) {
		g.setColor(Constant.BlockBack);
		g.drawRect(x * Constant.BlockSize, y * Constant.BlockSize, Constant.BlockSize, Constant.BlockSize);
		g.setColor(Constant.BlockColors[index]);
		g.fillRect(x * Constant.BlockSize + 1, y * Constant.BlockSize + 1, Constant.BlockSize - 1, Constant.BlockSize - 1);
	}
}