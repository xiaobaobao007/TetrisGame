package NewGame;

import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.swing.*;

/**
 * 游戏的主面板
 */
public class GamePanel extends JPanel {//客户端画面

    private int id;

    private boolean stop;
    private long stopTime = 0L;

    private DataOutputStream dos;//数据发送
    private boolean canDos;//队友发来的操作后禁止再发送
    private int blockType, lastBlockType;//方块类型
    private int turnState, lastTurnState;//旋转状态
    private int x = 4;//当前方块的坐标
    private int y = 0;//当前方块的坐标
    private int[][][] map = new int[2][13][23];//地图：12列22行。为防止越界，数组开成：13列23行
    private int delay;//游戏方块下落速度

    private Random random;

    GamePanel(DataOutputStream dos, boolean canDos, int id) {
        this.dos = dos;
        this.canDos = canDos;
        this.id = id;
        this.setSize(Constant.Panel_Width, Constant.Panel_Height);
        stop = false;
        random = new Random();
        if (canDos) {
            blockType = random.nextInt(7);
            turnState = random.nextInt(4);
            lastBlockType = random.nextInt(7);
            lastTurnState = random.nextInt(4);

            newGame(blockType, turnState);
            new Thread(() -> {
                while (true) {
                    if (stop) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        down();
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

    private void sendMsg(int... parm) {
        if (!canDos || dos == null) {
            return;
        }
        StringBuilder methodName = new StringBuilder(Thread.currentThread().getStackTrace()[2].getMethodName());
        for (int i : parm) {
            methodName.append("_").append(i);
        }
        try {
            dos.writeUTF(methodName.toString());
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
                    map[0][i][j] = -1;//
                } else {
                    map[0][i][j] = 0;
                }
            }
            map[0][i][21] = -1;
        }
        blockType = a;
        turnState = b;
        delay = Constant.BlockDelay;
    }


    public void endGame(boolean iswin) {
        stop();
        if (iswin) {
            switch (JOptionPane.showConfirmDialog(null, "恭喜恭喜！！！")) {
                case JOptionPane.OK_OPTION:
                    break;
                case JOptionPane.NO_OPTION:
                    break;
                case JOptionPane.CANCEL_OPTION:
                    break;
            }
        } else {
            switch (JOptionPane.showConfirmDialog(null, "再接再厉吧！！！")) {
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

    public void down() {
        if (crash(x, y + 1, blockType, turnState) == 0) {
            add(x, y, blockType, turnState);
            nextBlock(lastBlockType, lastTurnState);
        } else {
            sendMsg();
            y++;
        }
        repaint();
    }

    //决定下一方块
    public void nextBlock(int a, int b) {
        //2019年11月28日21:30:11   调试bug,莫名其妙的好了!!!!
        //if (!canDos) add(x, y, blockType, turnState);//这个解决bug,不知道为什么队友的面板没有进入123行循环,这里给加上
        blockType = a;
        turnState = b;
        if (canDos) {
            lastBlockType = random.nextInt(7);
            lastTurnState = random.nextInt(4);
            InfoPanel.lastBlockType = lastBlockType;
            InfoPanel.lastTurnState = lastTurnState;
            InfoPanel.me.repaint();
            sendMsg(a, b);
        }
        x = 4;
        y = 0;
        if (crash(x, y, blockType, turnState) == 0) {
            endGame(false);
        }
    }

    public void left() {
        if (x >= 0) {
            x -= crash(x - 1, y, blockType, turnState);
        }
        sendMsg();
        repaint();
    }

    public void right() {
        if (x < 8) {
            x += crash(x + 1, y, blockType, turnState);
        }
        sendMsg();
        repaint();
    }

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
                addMeScore(id, 100);
                delay -= 30;//每消除一行增加难度
            }
        }

    }

    public void addMeScore(int id, int score) {
        if (this.id == id) {
            InfoPanel.meScore += score;
        } else {
            InfoPanel.enemyScore += score;
        }
        InfoPanel.me.repaint();
        if (InfoPanel.meScore == 3000) {
            endGame(true);
        }
    }

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
                if ((Constant.shapes[blockType][turnState][a * 4 + b] & map[0][x + b + 1][y + a]) == 1) {
                    return 0;//碰撞
                }
            }
        }
        return 1;//没有碰撞
    }

    //画图
    public void paint(Graphics g) {
        super.paint(g);
        for (int j = 0; j < 16; j++) {
            if (Constant.shapes[blockType][turnState][j] == 1) {
                g.setColor(Constant.BlockBack);
                g.drawRect((j % 4 + x + 1) * 30, (j / 4 + y) * 30, 30, 30);
                g.setColor(Constant.NextBlock);
                g.fillRect((j % 4 + x + 1) * 30 + 1, (j / 4 + y) * 30 + 1, 29, 29);
            }
        }
        for (int i = 0; i < 12; i++) {//走列
            for (int j = 0; j < 22; j++) {//走行
                if (map[0][i][j] == -1) {
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