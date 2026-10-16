import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int BOARD_TOP = UNIT_SIZE * 2;
    static final int BOARD_BOTTOM = SCREEN_HEIGHT - UNIT_SIZE * 2;
    static final int BOARD_COLUMNS = SCREEN_WIDTH / UNIT_SIZE;
    static final int BOARD_ROWS = (BOARD_BOTTOM - BOARD_TOP) / UNIT_SIZE;
    static final int GAME_UNITS = BOARD_COLUMNS * BOARD_ROWS;

    enum GameState {
        MENU, PLAYING, PAUSED, GAME_OVER, WON
    }

    private static final long serialVersionUID = 1L;

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
    char nextDirection = 'R';
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
        nextDirection = 'R';
        for (int i = 0; i < bodyParts; i++) {
            x[i] = SCREEN_WIDTH / 2 - i * UNIT_SIZE;
            y[i] = SCREEN_HEIGHT / 2;
        }

        playerName = playerName.trim().isEmpty() ? "Player 1" : playerName.trim();
        currentDelay = diffDelays[selectedDiffIndex];
        gameState = GameState.PLAYING;

        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        newApple();
        timer = new Timer(currentDelay, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (gameState == GameState.MENU) {
            drawMenu(g2d);
        } else if (gameState == GameState.PLAYING || gameState == GameState.PAUSED) {
            drawGame(g2d);
            if (gameState == GameState.PAUSED) {
                drawOverlay(g2d, "PAUSED", "Press P to Resume");
            }
        } else if (gameState == GameState.GAME_OVER || gameState == GameState.WON) {
            drawEndScreen(g2d);
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
        g2d.drawString((menuFocus == 0 ? "> " : "  ") + "Player Name: " + playerName + "_", 100, 200);

        // Field 1: Difficulty
        g2d.setColor(menuFocus == 1 ? Color.YELLOW : Color.WHITE);
        g2d.drawString((menuFocus == 1 ? "> " : "  ") + "Difficulty: < " + diffNames[selectedDiffIndex] + " >", 100,
                260);

        // Field 2: Snake Color
        g2d.setColor(menuFocus == 2 ? Color.YELLOW : Color.WHITE);
        g2d.drawString((menuFocus == 2 ? "> " : "  ") + "Snake Color: < " + colorNames[selectedColorIndex] + " >",
                100, 320);

        // Color Preview Box
        g2d.setColor(snakeColors[selectedColorIndex]);
        g2d.fillRoundRect(430, 300, 30, 30, 8, 8);

        // Field 3: Start Button
        g2d.setColor(menuFocus == 3 ? Color.GREEN : Color.GRAY);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 26));
        g2d.drawString((menuFocus == 3 ? "> [ START GAME ] <" : "  [ START GAME ]"), 150, 420);

        // Instructions
        g2d.setColor(new Color(148, 163, 184));
        g2d.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        g2d.drawString("Up/Down: Select | Left/Right: Change | Enter: Start | Backspace: Edit name", 60, 520);
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

        g2d.setColor(gridColor2);
        g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int column = 0; column < BOARD_COLUMNS; column++) {
                if ((row + column) % 2 == 0) {
                    g2d.setColor(gridColor1);
                } else {
                    g2d.setColor(gridColor2);
                }
                g2d.fillRect(column * UNIT_SIZE, BOARD_TOP + row * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
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
        g2d.drawString("Score: " + applesEaten, 190, 25);
        g2d.drawString("Level: " + level, 300, 25);
        g2d.drawString("Best: " + highScore, 410, 25);
        g2d.drawString(locationName, 15, 590);
    }

    public void drawOverlay(Graphics2D g2d, String title, String instruction) {
        g2d.setColor(new Color(15, 23, 42, 210));
        g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 44));
        FontMetrics titleMetrics = g2d.getFontMetrics();
        g2d.drawString(title, (SCREEN_WIDTH - titleMetrics.stringWidth(title)) / 2, 200);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        FontMetrics instructionMetrics = g2d.getFontMetrics();
        g2d.drawString(instruction, (SCREEN_WIDTH - instructionMetrics.stringWidth(instruction)) / 2, 260);
    }

    public void drawEndScreen(Graphics2D g2d) {
        String title = gameState == GameState.WON ? "YOU WIN!" : "GAME OVER";
        drawOverlay(g2d, title, "Player: " + playerName + "  |  Score: " + applesEaten + "  |  Best: " + highScore);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        String restart = "Press SPACE to Restart";
        String menu = "Press M to Return to Menu";
        FontMetrics metrics = g2d.getFontMetrics();
        g2d.drawString(restart, (SCREEN_WIDTH - metrics.stringWidth(restart)) / 2, 360);
        g2d.drawString(menu, (SCREEN_WIDTH - metrics.stringWidth(menu)) / 2, 400);
    }

    public boolean newApple() {
        int availableCells = 0;
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int column = 0; column < BOARD_COLUMNS; column++) {
                if (!isSnakeAt(column * UNIT_SIZE, BOARD_TOP + row * UNIT_SIZE)) {
                    availableCells++;
                }
            }
        }

        if (availableCells == 0) {
            return false;
        }

        int targetCell = random.nextInt(availableCells);
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int column = 0; column < BOARD_COLUMNS; column++) {
                int cellX = column * UNIT_SIZE;
                int cellY = BOARD_TOP + row * UNIT_SIZE;
                if (!isSnakeAt(cellX, cellY) && targetCell-- == 0) {
                    appleX = cellX;
                    appleY = cellY;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSnakeAt(int cellX, int cellY) {
        for (int i = 0; i < bodyParts; i++) {
            if (x[i] == cellX && y[i] == cellY) {
                return true;
            }
        }
        return false;
    }

    public void move() {
        direction = nextDirection;
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

            if (applesEaten % 5 == 0) {
                level++;
                if (currentDelay > 50) {
                    currentDelay = Math.max(50, currentDelay - 5);
                    timer.setDelay(currentDelay);
                }
            }

            if (!newApple()) {
                gameState = GameState.WON;
            }
        }
    }

    public void checkCollisions() {
        for (int i = 1; i < bodyParts; i++) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                gameState = GameState.GAME_OVER;
            }
        }

        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < BOARD_TOP || y[0] >= BOARD_BOTTOM) {
            gameState = GameState.GAME_OVER;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == GameState.PLAYING) {
            move();
            checkApple();
            if (gameState == GameState.PLAYING) {
                checkCollisions();
            }
            if (gameState != GameState.PLAYING) {
                timer.stop();
            }
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
                } else if (menuFocus == 0) {
                    if (key == KeyEvent.VK_BACK_SPACE && playerName.length() > 0) {
                        playerName = playerName.substring(0, playerName.length() - 1);
                    } else if (isNameCharacter(e.getKeyChar()) && playerName.length() < 12) {
                        if (playerName.equals("Player 1")) {
                            playerName = "";
                        }
                        playerName += e.getKeyChar();
                    }
                }
                repaint();
            } else if (gameState == GameState.PLAYING) {
                if (key == KeyEvent.VK_P) {
                    gameState = GameState.PAUSED;
                    timer.stop();
                    repaint();
                    return;
                }
                switch (key) {
                    case KeyEvent.VK_LEFT:
                        queueDirection('L');
                        break;
                    case KeyEvent.VK_RIGHT:
                        queueDirection('R');
                        break;
                    case KeyEvent.VK_UP:
                        queueDirection('U');
                        break;
                    case KeyEvent.VK_DOWN:
                        queueDirection('D');
                        break;
                }
            } else if (gameState == GameState.PAUSED) {
                if (key == KeyEvent.VK_P) {
                    gameState = GameState.PLAYING;
                    timer.start();
                    repaint();
                }
            } else if (gameState == GameState.GAME_OVER || gameState == GameState.WON) {
                if (key == KeyEvent.VK_SPACE) {
                    startGame();
                } else if (key == KeyEvent.VK_M) {
                    gameState = GameState.MENU;
                    repaint();
                }
            }
        }
    }

    private boolean isNameCharacter(char character) {
        return Character.isLetterOrDigit(character) || character == ' ' || character == '-' || character == '_';
    }

    private void queueDirection(char requestedDirection) {
        boolean isOpposite = (direction == 'U' && requestedDirection == 'D')
                || (direction == 'D' && requestedDirection == 'U')
                || (direction == 'L' && requestedDirection == 'R')
                || (direction == 'R' && requestedDirection == 'L');
        if (!isOpposite) {
            nextDirection = requestedDirection;
        }
    }
}