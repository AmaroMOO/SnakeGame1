import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.ImageIcon;

public class GamePanel extends JPanel implements ActionListener {
    private static final int TILE_SIZE = 20; // Reduced tile size for smoother movement
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int ALL_TILES = (WIDTH * HEIGHT) / (TILE_SIZE * TILE_SIZE);
    private static final int DELAY = 100; // Reduced delay for faster movement

    private final int[] x = new int[ALL_TILES];
    private final int[] y = new int[ALL_TILES];

    private int bodyParts = 6; // Increased initial size of the snake
    private int foodEaten;
    private int foodX;
    private int foodY;

    private Direction direction = Direction.RIGHT;
    private boolean running = false;
    private Timer timer;
    private Random random;

    public GamePanel() {
        random = new Random();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != Direction.RIGHT) {
                            direction = Direction.LEFT;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != Direction.LEFT) {
                            direction = Direction.RIGHT;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != Direction.DOWN) {
                            direction = Direction.UP;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != Direction.UP) {
                            direction = Direction.DOWN;
                        }
                        break;
                }
            }
        });
        startGame();
    }

    private void startGame() {
        placeFood();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void placeFood() {
        foodX = random.nextInt((int) (WIDTH / TILE_SIZE)) * TILE_SIZE;
        foodY = random.nextInt((int) (HEIGHT / TILE_SIZE)) * TILE_SIZE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);
        draw(g);
    }

    private void drawBackground(Graphics g) {
        for (int i = 0; i < WIDTH / TILE_SIZE; i++) {
            for (int j = 0; j < HEIGHT / TILE_SIZE; j++) {
                if ((i + j) % 2 == 0) {
                    g.setColor(new Color(170, 215, 81)); // Light green
                } else {
                    g.setColor(new Color(162, 209, 73)); // Slightly darker green
                }
                g.fillRect(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void draw(Graphics g) {
        if (running) {
            g.setColor(Color.RED);
            g.fillRect(foodX, foodY, TILE_SIZE, TILE_SIZE);

            g.setColor(Color.BLACK);
            for (int i = 0; i < bodyParts; i++) {
                g.fillRect(x[i], y[i], TILE_SIZE, TILE_SIZE);
            }

            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
        }
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case UP:
                y[0] -= TILE_SIZE;
                break;
            case DOWN:
                y[0] += TILE_SIZE;
                break;
            case LEFT:
                x[0] -= TILE_SIZE;
                break;
            case RIGHT:
                x[0] += TILE_SIZE;
                break;
        }
    }

    private void checkFood() {
        if ((x[0] == foodX) && (y[0] == foodY)) {
            bodyParts++;
            foodEaten++;
            placeFood();
        }
    }

    private void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    private void gameOver(Graphics g) {
        String message = "Game Over";
        Font font = new Font("Helvetica", Font.BOLD, 40);
        FontMetrics metrics = getFontMetrics(font);

        g.setColor(Color.RED);
        g.setFont(font);
        g.drawString(message, (WIDTH - metrics.stringWidth(message)) / 2, HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollisions();
        }
        repaint();
    }
}