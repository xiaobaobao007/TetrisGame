import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client extends JFrame {

    int[][][] map1 = new int[2][22][12];//地图
    private int message, i, j, xx = 4, yy = 0, score1, STOP = 1;//数据、坐标、分数、暂停
    private Timer timer;//时间间隔
    private Socket socket, socket1;//连接客户端
    private DataInputStream dis;//数据接收
    private DataOutputStream dos;//数据传送
    private int blockType1;//方块类型
    private int turnState1;//旋转状态
    public Client() {//客户端
        new bmy().start();//运行客户端，连接服务器，必须先运行服务器。多线程启动
        this.setLayout(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(1100, 750);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.requestFocus(true);
        this.setVisible(true);
        TetrisPanel tp = new TetrisPanel();//添加操控本地游戏面板
        add(tp);
        tp.setBounds(50, 0, 800, 800);
        TetrisPanel1 tq = new TetrisPanel1();//添加还原对方界面面板
        tq.setBounds(690, 0, 800, 800);
        add(tq);
        this.addKeyListener(tp.listener);//键盘监听
        while (true) {
            tq.repaint();
        }//多线程绘图
    }

    public static void main(String[] args) {
        new Client().setVisible(true);
    }

    class bmy extends Thread {//数据接收

        public void run() {
            try {
                newgame();
                socket = new Socket(Constant.ServerIp, Constant.ServerPort1);
                dis = new DataInputStream(socket.getInputStream());
                while (true) {
                    message = dis.readInt();
                    if (message == 1) {
                        yy++;
                    } else if (message == 2) {
                        xx--;
                    } else if (message == 3) {
                        xx++;
                    } else if (message == 4) {
                        turnState1 = (turnState1 + 1) % 4;
                    } else if (message == 5) {
                        for (int a = 0; a < 4; a++) {
                            for (int b = 0; b < 4; b++) {
                                if (Constant.shapes[blockType1][turnState1][a * 4 + b] == 1) {
                                    map1[0][yy + a][xx + b + 1] = 1;
                                    map1[1][yy + a][xx + b + 1] = blockType1;
                                }
                            }
                        }
                    } else if (9 < message & message < 80) {
                        xx = 4;
                        yy = 0;
                        blockType1 = message / 10 - 1;
                        turnState1 = message % 10;
                    } else if (150 > message && message > 100) {
                        message = message - 100;
                        for (int i = message; i > 0; i--) {
                            for (int j = 1; j < 11; j++) {

                                map1[0][i][j] = map1[0][i - 1][j];
                                map1[1][i][j] = map1[1][i - 1][j];
                            }
                        }
                    } else if (message == 190) {
                        timer.stop();
                        int option = JOptionPane.showConfirmDialog(null, "恭喜您赢啦");
                        if (option == JOptionPane.OK_OPTION) {
                            System.exit(0);
                        } else if (option == JOptionPane.NO_OPTION) {
                            System.exit(0);
                        } else if (option == JOptionPane.CANCEL_OPTION) {
                            System.exit(0);
                        }
                    } else if (message == 195) {
                        timer.stop();
                        int option = JOptionPane.showConfirmDialog(null, "对手比你先赢一步");
                        if (option == JOptionPane.OK_OPTION) {
                            System.exit(0);
                        } else if (option == JOptionPane.NO_OPTION) {
                            System.exit(0);
                        } else if (option == JOptionPane.CANCEL_OPTION) {
                            System.exit(0);
                        }
                    } else if (message == 196) {
                        if (STOP == 1) {
                            STOP = 0;
                            timer.stop();
                        } else {
                            STOP = 1;
                            timer.start();
                        }
                    } else if (message > 200) {
                        score1 = message - 200;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void newgame() {//游戏初始化
            for (i = 0; i < 22; i++) {
                for (j = 0; j < 12; j++) {
                    if (j == 0 || j == 11) {//-1为界面边框的格
                        map1[0][i][j] = -1;
                    } else {
                        map1[0][i][j] = 0;
                    }
                    map1[0][21][j] = -1;
                }

            }
        }
    }


    class TetrisPanel1 extends JPanel {//服务器画面


        public void paint(Graphics g) {//绘图
            super.paint(g);
            for (j = 0; j < 16; j++) {
                if (Constant.shapes[blockType1][turnState1][j] == 1) {
                    g.setColor(Color.pink);
                    g.drawRect((j % 4 + xx + 1) * 30, (j / 4 + yy) * 30, 30, 30);
                    g.setColor(Color.blue);
                    g.fillRect((j % 4 + xx + 1) * 30 + 1, (j / 4 + yy) * 30 + 1, 29, 29);
                }
            }
            for (i = 0; i < 22; i++) {
                for (j = 0; j < 12; j++) {
                    if (map1[0][i][j] == -1) {
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillRect(j * 30, i * 30, 30, 30);
                    }
                    if (map1[0][i][j] == 1) {
                        if (map1[1][i][j] == 0) {
                            g.setColor(Color.pink);
                            g.drawRect(j * 30, i * 30, 30, 30);
                            g.setColor(Color.red);
                            g.fillRect(j * 30 + 1, i * 30 + 1, 29, 29);
                        }
                        if (map1[1][i][j] == 1) {
                            g.setColor(Color.pink);
                            g.drawRect(j * 30, i * 30, 30, 30);
                            g.setColor(Color.orange);
                            g.fillRect(j * 30 + 1, i * 30 + 1, 29, 29);
                        }
                        if (map1[1][i][j] == 2) {
                            g.setColor(Color.pink);
                            g.drawRect(j * 30, i * 30, 30, 30);
                            g.setColor(Color.yellow);
                            g.fillRect(j * 30 + 1, i * 30 + 1, 29, 29);
                        }
                        if (map1[1][i][j] == 3) {
                            g.setColor(Color.pink);
                            g.drawRect(j * 30, i * 30, 30, 30);
                            g.setColor(Color.green);
                            g.fillRect(j * 30 + 1, i * 30 + 1, 29, 29);
                        }
                        if (map1[1][i][j] == 4) {
                            g.setColor(Color.pink);
                            g.drawRect(j * 30, i * 30, 30, 30);
                            g.setColor(Color.blue);
                            g.fillRect(j * 30 + 1, i * 30 + 1, 29, 29);
                        }
                        if (map1[1][i][j] == 5) {
                            g.setColor(Color.pink);
                            g.drawRect(j * 30, i * 30, 30, 30);
                            g.setColor(Color.magenta);
                            g.fillRect(j * 30 + 1, i * 30 + 1, 29, 29);
                        }
                        if (map1[1][i][j] == 6) {
                            g.setColor(Color.pink);
                            g.drawRect(j * 30, i * 30, 30, 30);
                            g.setColor(Color.gray);
                            g.fillRect(j * 30 + 1, i * 30 + 1, 29, 29);
                        }
                    }
                }
            }
        }
    }


    class TetrisPanel extends JPanel {
        public TimerKeyLister listener = new TimerKeyLister(); //键盘监听
        private int blockType, lastblockType = 0;//方块类型
        private int turnState, lastturnState = 0;//旋转状态
        private int x;
        private int y;
        private int map[][][] = new int[2][13][23];//地图：12列22行。为防止越界，数组开成：13列23行
        private int delay = 1000;
        private int score = 0;//分数


        //游戏开始包
        public TetrisPanel() {
            try {
                socket1 = new Socket(Constant.ServerIp, Constant.ServerPort2);
                dos = new DataOutputStream(socket1.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            newGame();
            nextBlock();
        }

        //开始游戏
        public void newGame() {
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
            delay = 1000;
            timer = new Timer(delay, listener);
            timer.start();
        }

        //决定下一方块
        private void nextBlock() {
            blockType = lastblockType;
            turnState = lastturnState;
            try {
                dos.writeInt((blockType + 1) * 10 + turnState);
            } catch (IOException e) {
                e.printStackTrace();
            }
            lastblockType = (int) (Math.random() * 1000) % 7;
            lastturnState = (int) (Math.random() * 1000) % 4;
            x = 4;
            y = 0;
            if (crash(x, y, blockType, turnState) == 0) {
                timer.stop();
                try {
                    dos.writeInt(190);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int option = JOptionPane.showConfirmDialog(this, "Game Over!!，还敢来吗...");
                if (option == JOptionPane.OK_OPTION) {
                    newGame();
                } else if (option == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
            }
        }

        //键盘操作

        private void down() {
            try {
                dos.writeInt(1 * crash(x, y + 1, blockType, turnState));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (crash(x, y + 1, blockType, turnState) == 0) {
                add(x, y, blockType, turnState);
                nextBlock();
            } else {
                y++;
            }


            repaint();
        }

        private void left() {
            try {
                dos.writeInt(2 * crash(x - 1, y, blockType, turnState));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (x >= 0) {
                x -= crash(x - 1, y, blockType, turnState);
            }

            repaint();
        }

        private void right() {
            try {
                dos.writeInt(3 * crash(x + 1, y, blockType, turnState));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (x < 8) {
                x += crash(x + 1, y, blockType, turnState);
            }

            repaint();
        }

        private void turn() {
            try {
                dos.writeInt(4 * crash(x, y, blockType, (turnState + 1) % 4));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (crash(x, y, blockType, (turnState + 1) % 4) == 1) {
                turnState = (turnState + 1) % 4;
            }


            repaint();
        }

        private void Stop() {

            try {
                dos.writeInt(196);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (STOP == 1) {
                STOP = 0;
                timer.stop();
            } else {
                STOP = 1;
                timer.start();
            }
        }

        private void add(int x, int y, int blockType, int turnState) {
            for (int a = 0; a < 4; a++) {
                for (int b = 0; b < 4; b++) {
                    if (Constant.shapes[blockType][turnState][a * 4 + b] == 1) {
                        map[0][x + b + 1][y + a] = 1;
                        map[1][x + b + 1][y + a] = blockType;
                    }
                }
            }
            try {
                dos.writeInt(5);
            } catch (IOException e) {
                e.printStackTrace();
            }
            tryDelLine();
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
                    try {
                        dos.writeInt(100 + b);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    for (int d = b; d > 0; d--) {
                        for (int e = 0; e < 11; e++) {
                            map[0][e][d] = map[0][e][d - 1];
                            map[1][e][d] = map[1][e][d - 1];
                        }
                    }
                    score += 100;
                    try {
                        dos.writeInt(200 + score);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (score == 3000) {
                        try {
                            dos.writeInt(195);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    delay -= 30;//每消除一行增加难度
                    timer.setDelay(delay);
                }
            }

        }

        //判断是否发生碰撞
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
            g.setColor(new Color(153, 51, 205));
            for (int j = 0; j < 16; j++) {
                if (Constant.shapes[blockType][turnState][j] == 1) {
                    g.setColor(Color.pink);
                    g.drawRect((j % 4 + x + 1) * 30, (j / 4 + y) * 30, 30, 30);
                    g.setColor(Color.BLUE);
                    g.fillRect((j % 4 + x + 1) * 30 + 1, (j / 4 + y) * 30 + 1, 29, 29);
                }
            }
            for (int i = 0; i < 12; i++) {//走列
                for (int j = 0; j < 22; j++) {//走行
                    if (map[0][i][j] == -1) {
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillRect(i * 30, j * 30, 30, 30);
                    } else if (map[0][i][j] == 1) {

                        if (map[1][i][j] == 0) {
                            g.setColor(Color.pink);
                            g.drawRect(i * 30, j * 30, 30, 30);
                            g.setColor(Color.red);
                            g.fillRect(i * 30 + 1, j * 30 + 1, 29, 29);
                        }
                        if (map[1][i][j] == 1) {
                            g.setColor(Color.pink);
                            g.drawRect(i * 30, j * 30, 30, 30);
                            g.setColor(Color.orange);
                            g.fillRect(i * 30 + 1, j * 30 + 1, 29, 29);
                        }
                        if (map[1][i][j] == 2) {
                            g.setColor(Color.pink);
                            g.drawRect(i * 30, j * 30, 30, 30);
                            g.setColor(Color.yellow);
                            g.fillRect(i * 30 + 1, j * 30 + 1, 29, 29);
                        }
                        if (map[1][i][j] == 3) {
                            g.setColor(Color.pink);
                            g.drawRect(i * 30, j * 30, 30, 30);
                            g.setColor(Color.green);
                            g.fillRect(i * 30 + 1, j * 30 + 1, 29, 29);
                        }
                        if (map[1][i][j] == 4) {
                            g.setColor(Color.pink);
                            g.drawRect(i * 30, j * 30, 30, 30);
                            g.setColor(Color.blue);
                            g.fillRect(i * 30 + 1, j * 30 + 1, 29, 29);
                        }
                        if (map[1][i][j] == 5) {
                            g.setColor(Color.pink);
                            g.drawRect(i * 30, j * 30, 30, 30);
                            g.setColor(Color.magenta);
                            g.fillRect(i * 30 + 1, j * 30 + 1, 29, 29);
                        }
                        if (map[1][i][j] == 6) {
                            g.setColor(Color.pink);
                            g.drawRect(i * 30, j * 30, 30, 30);
                            g.setColor(Color.gray);
                            g.fillRect(i * 30 + 1, j * 30 + 1, 29, 29);
                        }
                    }
                }
                for (int m = 0; m < 16; m++) {
                    if (Constant.shapes[lastblockType][lastturnState][m] == 1) {
                        g.setColor(Color.pink);
                        g.drawRect((m % 4) * 25 + 450, (m / 4) * 25 + 80, 25, 25);
                        g.setColor(Color.GREEN);
                        g.fillRect((m % 4) * 25 + 451, (m / 4) * 25 + 81, 24, 24);
                    }
                }
            }
            g.setColor(Color.blue);
            g.setFont(new Font("宋体", Font.PLAIN, 15));
            g.drawString("游戏已启动，可按空格键暂停或者继续", 376, 470);
            g.setFont(new Font("宋体", Font.PLAIN, 25));
            g.setColor(Color.RED);
            g.drawString("请先完成3000分", 405, 210);
            g.drawString("您的分数是", 435, 270);
            g.drawString("score=" + score, 450, 300);
            g.drawString("您对手的分数是", 405, 370);
            g.drawString("score=" + score1, 450, 400);
            g.setFont(new Font("宋体", Font.PLAIN, 30));
            g.setColor(Color.BLACK);
            g.drawString("下一个方块是", 405, 40);
        }

        class TimerKeyLister extends KeyAdapter implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                down();
            }

            public void keyPressed(KeyEvent e) {

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        down();
                        break;
                    case KeyEvent.VK_LEFT:
                        left();
                        break;
                    case KeyEvent.VK_RIGHT:
                        right();
                        break;
                    case KeyEvent.VK_UP:
                        turn();
                        break;
                    case KeyEvent.VK_SPACE:
                        Stop();
                        break;
                }

            }
        }
    }
}