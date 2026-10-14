import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;

    enum GameState {
        MENU, PLAYING, GAME_OVER
    }

    GameState gameState = GameState.MENU;

    // Cấu hình người chơi & Cài đặt
    String playerName = "Player 1";
    int selectedDiffIndex = 0; // Mặc định chọn Easy
    String[] diffNames = { "EASY", "MEDIUM", "HARD" };

    // Tốc độ điều chỉnh: Số càng lớn rắn bò càng CHẬM
    // Easy: 280ms (Rất thong thả) | Medium: 180ms | Hard: 100ms
    int[] diffDelays = { 280, 180, 100 };

    int selectedColorIndex = 0;
    String[] colorNames = { "Green", "Cyan", "Pink", "Yellow", "Orange" };
    Color[] snakeColors = {
            new Color(34, 197, 94), // Green
            new Color(6, 182, 212), // Cyan
            new Color(236, 72, 153), // Pink
            new Color(234, 179, 8), // Yellow
            new Color(249, 115, 22) // Orange
    };

    int menuFocus = 0; // 0: Player Name, 1: Difficulty, 2: Snake Color, 3: Start Button

    // Game variables
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 3;
    int applesEaten = 0;
    int highScore = 0;
    int level = 1;

    int appleX;
    int appleY;

    char direction = 'R';
    Timer timer;
    Random random;
    int currentDelay;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(15, 23, 42));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
    }

    public void startGame() {
        bodyParts = 3;
        applesEaten = 0;
        level = 1;
        direction = 'R';
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }

        newApple();
        currentDelay = diffDelays[selectedDiffIndex];
        gameState = GameState.PLAYING;

        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        timer = new Timer(currentDelay, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (gameState == GameState.MENU) {
            drawMenu(g2d);
        } else if (gameState == GameState.PLAYING) {
            drawGame(g2d);
        } else if (gameState == GameState.GAME_OVER) {
            drawGameOver(g2d);
        }
    }

    // 1. Draw Menu
    public void drawMenu(Graphics2D g2d) {
        g2d.setColor(snakeColors[selectedColorIndex]);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 42));
        FontMetrics m1 = getFontMetrics(g2d.getFont());
        g2d.drawString("SNAKE GAME 2.0", (SCREEN_WIDTH - m1.stringWidth("SNAKE GAME 2.0")) / 2, 100);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 20));

        // Field 0: Player Name
        g2d.setColor(menuFocus == 0 ? Color.YELLOW : Color.WHITE);
        g2d.drawString((menuFocus == 0 ? "👉 " : "   ") + "Player Name: " + playerName + "_", 100, 200);

        // Field 1: Difficulty
        g2d.setColor(menuFocus == 1 ? Color.YELLOW : Color.WHITE);
        g2d.drawString((menuFocus == 1 ? "👉 " : "   ") + "Difficulty: < " + diffNames[selectedDiffIndex] + " >", 100,
                260);

        // Field 2: Snake Color
        g2d.setColor(menuFocus == 2 ? Color.YELLOW : Color.WHITE);
        g2d.drawString((menuFocus == 2 ? "👉 " : "   ") + "Snake Color: < " + colorNames[selectedColorIndex] + " >",
                100, 320);

        // Color Preview Box
        g2d.setColor(snakeColors[selectedColorIndex]);
        g2d.fillRoundRect(430, 300, 30, 30, 8, 8);

        // Field 3: Start Button
        g2d.setColor(menuFocus == 3 ? Color.GREEN : Color.GRAY);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 26));
        g2d.drawString((menuFocus == 3 ? "👉 [ START GAME ] 👈" : "   [ START GAME ]"), 150, 420);

        // Instructions
        g2d.setColor(new Color(148, 163, 184));
        g2d.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        g2d.drawString("Use Up/Down to navigate | Left/Right to change options | Enter to Start", 60, 520);
    }

    // 2. Draw Game với hiệu ứng Map/Thời tiết đổi theo Level
    public void drawGame(Graphics2D g2d) {
        Color gridColor1, gridColor2;
        String locationName;

        if (level == 1) {
            locationName = "🌱 Level 1: Green Valley (Day)";
            gridColor1 = new Color(34, 197, 94, 40);
            gridColor2 = new Color(15, 23, 42);
        } else if (level == 2) {
            locationName = "🌙 Level 2: Dark Jungle (Night)";
            gridColor1 = new Color(15, 23, 42);
            gridColor2 = new Color(30, 41, 59);
        } else if (level == 3) {
            locationName = "🏜️ Level 3: Scorching Desert";
            gridColor1 = new Color(217, 119, 6, 60);
            gridColor2 = new Color(120, 53, 15);
        } else {
            locationName = "❄️ Level 4+: Cyber Ice Land";
            gridColor1 = new Color(14, 165, 233, 60);
            gridColor2 = new Color(12, 74, 110);
        }

        // Vẽ Lưới Caro Map
        for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
            for (int j = 0; j < SCREEN_WIDTH / UNIT_SIZE; j++) {
                if ((i + j) % 2 == 0) {
                    g2d.setColor(gridColor1);
                } else {
                    g2d.setColor(gridColor2);
                }
                g2d.fillRect(j * UNIT_SIZE, i * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
            }
        }

        // Vẽ Quả Táo
        g2d.setColor(new Color(239, 68, 68));
        g2d.fillOval(appleX + 2, appleY + 2, UNIT_SIZE - 4, UNIT_SIZE - 4);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(appleX + UNIT_SIZE / 2 - 1, appleY + 1, 3, 5);

        // Vẽ Rắn
        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                g2d.setColor(snakeColors[selectedColorIndex]);
                g2d.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 12, 12);
                g2d.setColor(Color.WHITE);
                int eyeSize = 5;
                if (direction == 'R' || direction == 'L') {
                    g2d.fillOval(x[i] + 10, y[i] + 4, eyeSize, eyeSize);
                    g2d.fillOval(x[i] + 10, y[i] + 15, eyeSize, eyeSize);
                } else {
                    g2d.fillOval(x[i] + 4, y[i] + 10, eyeSize, eyeSize);
                    g2d.fillOval(x[i] + 15, y[i] + 10, eyeSize, eyeSize);
                }
            } else {
                g2d.setColor(snakeColors[selectedColorIndex].darker());
                g2d.fillRoundRect(x[i] + 1, y[i] + 1, UNIT_SIZE - 2, UNIT_SIZE - 2, 8, 8);
            }
        }

        // Top Bar thông số
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2d.drawString("Player: " + playerName, 15, 25);
        g2d.drawString("Score: " + applesEaten, 180, 25);
        g2d.drawString("High: " + highScore, 300, 25);
        g2d.drawString(locationName, 15, 590);
    }

    // 3. Draw Game Over
    public void drawGameOver(Graphics2D g2d) {
        g2d.setColor(new Color(15, 23, 42));
        g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g2d.setColor(new Color(239, 68, 68));
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 50));
        FontMetrics m1 = getFontMetrics(g2d.getFont());
        g2d.drawString("GAME OVER", (SCREEN_WIDTH - m1.stringWidth("GAME OVER")) / 2, 180);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 20));
        FontMetrics m2 = getFontMetrics(g2d.getFont());
        g2d.drawString("Player: " + playerName + "  |  Score: " + applesEaten + "  |  High Score: " + highScore,
                (SCREEN_WIDTH - m2.stringWidth(
                        "Player: " + playerName + "  |  Score: " + applesEaten + "  |  High Score: " + highScore)) / 2,
                260);

        g2d.setColor(new Color(148, 163, 184));
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        FontMetrics m3 = getFontMetrics(g2d.getFont());
        g2d.drawString("Press SPACE to Restart", (SCREEN_WIDTH - m3.stringWidth("Press SPACE to Restart")) / 2, 360);
        g2d.drawString("Press M to Return to Menu", (SCREEN_WIDTH - m3.stringWidth("Press M to Return to Menu")) / 2,
                400);
    }

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;

            if (applesEaten > highScore) {
                highScore = applesEaten;
            }

            // Ăn 5 quả táo tăng 1 Level
            if (applesEaten % 5 == 0) {
                level++;
                // Tăng tốc độ rất nhẹ nhàng (-5ms mỗi Level)
                if (currentDelay > 50) {
                    currentDelay -= 5;
                    timer.setDelay(currentDelay);
                }
            }

            newApple();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                gameState = GameState.GAME_OVER;
            }
        }

        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            gameState = GameState.GAME_OVER;
        }

        if (gameState == GameState.GAME_OVER && timer != null) {
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == GameState.PLAYING) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (gameState == GameState.MENU) {
                if (key == KeyEvent.VK_UP) {
                    menuFocus = (menuFocus - 1 + 4) % 4;
                } else if (key == KeyEvent.VK_DOWN) {
                    menuFocus = (menuFocus + 1) % 4;
                } else if (key == KeyEvent.VK_LEFT) {
                    if (menuFocus == 1) {
                        selectedDiffIndex = (selectedDiffIndex - 1 + 3) % 3;
                    } else if (menuFocus == 2) {
                        selectedColorIndex = (selectedColorIndex - 1 + 5) % 5;
                    }
                } else if (key == KeyEvent.VK_RIGHT) {
                    if (menuFocus == 1) {
                        selectedDiffIndex = (selectedDiffIndex + 1) % 3;
                    } else if (menuFocus == 2) {
                        selectedColorIndex = (selectedColorIndex + 1) % 5;
                    }
                } else if (key == KeyEvent.VK_ENTER) {
                    startGame();
                } else if (menuFocus == 0) { // Nhập tên
                    if (key == KeyEvent.VK_BACK_SPACE && playerName.length() > 0) {
                        playerName = playerName.substring(0, playerName.length() - 1);
                    } else if (Character.isDefined(e.getKeyChar()) && playerName.length() < 12
                            && key != KeyEvent.VK_BACK_SPACE) {
                        if (playerName.equals("Player 1"))
                            playerName = "";
                        playerName += e.getKeyChar();
                    }
                }
                repaint();
            } else if (gameState == GameState.PLAYING) {
                switch (key) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R')
                            direction = 'L';
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L')
                            direction = 'R';
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 'D')
                            direction = 'U';
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U')
                            direction = 'D';
                        break;
                }
            } else if (gameState == GameState.GAME_OVER) {
                if (key == KeyEvent.VK_SPACE) {
                    startGame();
                } else if (key == KeyEvent.VK_M) {
                    gameState = GameState.MENU;
                    repaint();
                }
            }
        }
    }
}