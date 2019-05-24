package test;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyListen implements KeyListener {

    private GamePanel gamePanel;

    KeyListen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {
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
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
