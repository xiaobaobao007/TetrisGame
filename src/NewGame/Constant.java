package NewGame;

import java.awt.*;

/**
 * 游戏中所需要的各种参数
 */
class Constant {

    static int ServerId = 1;//用于区别服务器
    static int ClientId = 2;//用于区别客户端

    static String ServerIp = "localhost";//服务器地址
    static int ServerPort = 8081;//服务器开放端口号
    static int BlockDelay = 1000;//方块下落延迟
    static int Panel_Width = 380;//游戏面板大小
    static int JFrame_Height = 710;
    static int Panel_Height = JFrame_Height;
    static int InfoPanel_Width = 280;//信息面板大小
    static int InfoPanel_Height = JFrame_Height;
    static int z = 50;//面板之间距离
    static int x1 = 10;//面板的位置
    static int y1 = 10;
    static int x2 = x1 + Panel_Width + z;
    static int y2 = y1;
    static int x3 = x2 + InfoPanel_Width + z;
    static int y3 = y2;
    static int JFrame_Width = x3 + Panel_Width;//窗口大小


    static int BlockSize = 30;//方块大小
    static Color MapLine = Color.LIGHT_GRAY;//边界颜色
    static Color BlockBack = Color.pink;//方块背景色
    static Color NextBlock = Color.blue;//下个提示方块颜色

    static Color[] BlockColors = {Color.red, Color.orange, Color.yellow, Color.green, Color.blue, Color.magenta, Color.gray};

    /**
     * 俄罗斯方块七种类型和四种变换形态
     */
    static int[][][] shapes = new int[][][]{
            //一
            {{0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0}},
            // S
            {{0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                    {0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0}},
            // Z
            {{1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                    {1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}},
            // J
            {{0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0},
                    {1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                    {1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0}},
            // O
            {{1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}},
            // L
            {{1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0},
                    {1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                    {0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0}},
            // T
            {{0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                    {1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0}}
    };
}
