import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JFrame {

    public SnakeGame() {
        add(new GamePanel());
        setTitle("Snake Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SnakeGame::new);
    }
}

class GamePanel extends JPanel implements ActionListener {
    private static final int TILE_SIZE = 25;
    private static final int BOARD_WIDTH = 20;
    private static final int BOARD_HEIGHT = 20;
    private static final int SCREEN_WIDTH = TILE_SIZE * BOARD_WIDTH;
    private static final int SCREEN_HEIGHT = TILE_SIZE * BOARD_HEIGHT;
    private static final int DELAY = 150;

    private final int[] x = new int[BOARD_WIDTH * BOARD_HEIGHT];
    private final int[] y = new int[BOARD_WIDTH * BOARD_HEIGHT];

    private int snakeLength;
    private int appleX;
    private int appleY;
    private boolean running = false;
    private Timer timer;

    private char direction = 'R'; // U, D, L, R

    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyHandler());
        startGame();
    }

    private void startGame() {
        snakeLength = 3;
        for (int i = 0; i < snakeLength; i++) {
            x[i] = (5 - i) * TILE_SIZE;
            y[i] = 5 * TILE_SIZE;
        }
        spawnApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void spawnApple() {
        Random random = new Random();
        appleX = random.nextInt(BOARD_WIDTH) * TILE_SIZE;
        appleY = random.nextInt(BOARD_HEIGHT) * TILE_SIZE;
    }

    private void move() {
        for (int i = snakeLength; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' -> y[0] -= TILE_SIZE;
            case 'D' -> y[0] += TILE_SIZE;
            case 'L' -> x[0] -= TILE_SIZE;
            case 'R' -> x[0] += TILE_SIZE;
        }
    }

    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            snakeLength++;
            spawnApple();
        }
    }

    private void checkCollisions() {
        // Check if head collides with body
        for (int i = snakeLength; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }

        // Check if head collides with walls
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (running) {
            // Draw apple
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, TILE_SIZE, TILE_SIZE);

            // Draw snake
            for (int i = 0; i < snakeLength; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(x[i], y[i], TILE_SIZE, TILE_SIZE);
            }
        } else {
            gameOver(g);
        }
    }

    private void gameOver(Graphics g) {
        String message = "Game Over";
        String score = "Score: " + (snakeLength - 3);
        Font font = new Font("Arial", Font.BOLD, 20);
        FontMetrics metrics = getFontMetrics(font);

        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString(message, (SCREEN_WIDTH - metrics.stringWidth(message)) / 2, SCREEN_HEIGHT / 2 - 20);
        g.drawString(score, (SCREEN_WIDTH - metrics.stringWidth(score)) / 2, SCREEN_HEIGHT / 2 + 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP -> {
                    if (direction != 'D') direction = 'U';
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U') direction = 'D';
                }
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R') direction = 'L';
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L') direction = 'R';
                }
            }
        }
    }
}
