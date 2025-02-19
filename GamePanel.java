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
    private static final int INITIAL_DELAY = 100; // Initial delay for movement

    private final int[] x = new int[ALL_TILES];
    private final int[] y = new int[ALL_TILES];

    private int bodyParts = 6; // Increased initial size of the snake
    private int foodEaten;
    private int foodX;
    private int foodY;
    private long startTime;

    private Direction direction = Direction.RIGHT;
    private boolean running = false;
    private Timer timer;
    private Random random;

    private Color foodColor = Color.RED;

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
        startTime = System.currentTimeMillis();
        timer = new Timer(INITIAL_DELAY, this);
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
            g.setColor(foodColor);
            g.fillRect(foodX, foodY, TILE_SIZE, TILE_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.WHITE); // Head of the snake
                } else {
                    g.setColor(Color.BLACK); // Body of the snake
                }
                g.fillRect(x[i], y[i], TILE_SIZE, TILE_SIZE);
            }

            drawScore(g);
            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
        }
    }

    private void drawScore(Graphics g) {
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        String scoreText = "Time: " + elapsedTime + "s  Apples: " + foodEaten;
        Font font = new Font("Helvetica", Font.BOLD, 20);
        FontMetrics metrics = getFontMetrics(font);

        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString(scoreText, (WIDTH - metrics.stringWidth(scoreText)) / 2, g.getFont().getSize());
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
            // Increase speed by reducing the delay
            int newDelay = Math.max(10, timer.getDelay() - 5); // Ensure delay doesn't go below 10ms
            timer.setDelay(newDelay);

            // Change food color every 10 apples
            if (foodEaten % 10 == 0) {
                foodColor = getRandomColor();
            }
        }
    }

    private Color getRandomColor() {
        Color[] colors = {Color.RED, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.PINK, Color.MAGENTA, Color.CYAN};
        Color newColor;
        do {
            newColor = colors[random.nextInt(colors.length)];
        } while (newColor.equals(new Color(170, 215, 81)) || newColor.equals(new Color(162, 209, 73))); // Avoid green colors
        return newColor;
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