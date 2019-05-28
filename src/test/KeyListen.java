package test;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * 键盘监听
 */
public class KeyListen implements KeyListener {

    private GamePanel gamePanel;

    KeyListen(GamePanel gamePanel) {
        super();
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                gamePanel.turn();
                break;
            case KeyEvent.VK_A:
                gamePanel.left();
                break;
            case KeyEvent.VK_S:
                gamePanel.down();
                break;
            case KeyEvent.VK_D:
                gamePanel.right();
                break;
            case KeyEvent.VK_SPACE:
                gamePanel.stop();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
