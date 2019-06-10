package NewGame;

import javax.swing.*;
import java.awt.*;

/**
 * 游戏中间的信息面板
 */
public class InfoPanel extends JPanel {

    public static InfoPanel me = new InfoPanel();

    static int meScore;//自己的分数
    static int enemyScore;//队友的分数
    static int lastBlockType;//提示的方块类型
    static int lastTurnState;//提示的方块状态
    int x = 0;
    int y = 0;

    InfoPanel() {
        this.setBounds(Constant.x2, Constant.y2, Constant.InfoPanel_Width, Constant.InfoPanel_Height);
        meScore = 0;
        enemyScore = 0;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        for (int m = 0; m < 16; m++) {
            if (Constant.shapes[lastBlockType][lastTurnState][m] == 1) {
                g.setColor(Color.pink);
                g.drawRect((m % 4) * 25 + x + 50, (m / 4) * 25 + y + 80, 25, 25);
                g.setColor(Color.GREEN);
                g.fillRect((m % 4) * 25 + x + 51, (m / 4) * 25 + y + 81, 24, 24);
            }
        }

        g.setColor(Color.blue);
        g.setFont(new Font("宋体", Font.PLAIN, 30));
        g.setColor(Color.BLACK);
        g.drawString("下一个方块是", x + 20, y + 40);
        g.setFont(new Font("宋体", Font.PLAIN, 25));
        g.setColor(Color.RED);
        g.drawString("请先完成3000分", x + 15, y + 210);
        g.drawString("您的分数是", x + 35, y + 270);
        g.drawString("meScore=" + meScore, x + 40, y + 300);
        g.drawString("您对手的分数是", x + 30, y + 370);
        g.drawString("enemyScore=" + enemyScore, x + 40, y + 400);
        g.setFont(new Font("宋体", Font.PLAIN, 15));
        g.drawString("游戏已启动，可按空格键暂停或者继续", x, y + 470);
    }
}
