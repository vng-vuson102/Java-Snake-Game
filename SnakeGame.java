import javax.swing.JFrame;

public class SnakeGame extends JFrame {

    public SnakeGame() {
        // Cấu hình cửa sổ Game
        this.setTitle("Snake Game - Java");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(600, 600);
        this.setLocationRelativeTo(null); // Hiển thị cửa sổ ở chính giữa màn hình
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new SnakeGame();
    }
}