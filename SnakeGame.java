import javax.swing.JFrame;

public class SnakeGame extends JFrame {

    public SnakeGame() {
        // Thêm màn hình GamePanel vào khung cửa sổ
        this.add(new GamePanel());

        // Cấu hình cửa sổ Game
        this.setTitle("Snake Game - Java");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack(); // Tự động co giãn vừa vặn với kích thước GamePanel
        this.setLocationRelativeTo(null); // Hiển thị giữa màn hình
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new SnakeGame();
    }
}