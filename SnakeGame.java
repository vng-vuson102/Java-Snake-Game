import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class SnakeGame extends JFrame {

    private static final long serialVersionUID = 1L;

    public SnakeGame() {
        GamePanel gamePanel = new GamePanel();
        this.add(gamePanel);

        this.setTitle("Snake Game - Java");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        gamePanel.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SnakeGame::new);
    }
}