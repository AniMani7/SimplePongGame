import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PongGame extends JFrame {

    private GamePanel gamePanel;
    private String player1Name;
    private String player2Name;
    private int targetScore;
    private static int TIMER_DELAY = 5;

    public PongGame() {
        startNewGame();
    }

    private void startNewGame() {
        String[] options = {"Classic Mode", "Arcade Mode"};
        int mode = JOptionPane.showOptionDialog(
                this,
                "Choose your mode:",
                "Pong Game",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        boolean isArcadeMode = false;

        if((mode == 1)){
            isArcadeMode = true;
        }

        player1Name = JOptionPane.showInputDialog(this, "Enter Player 1 Name:", "Player 1");
        player2Name = JOptionPane.showInputDialog(this, "Enter Player 2 Name:", "Player 2");

        if (player1Name == null || player1Name.isEmpty()) {
            player1Name = "Player 1";
        }
        if (player2Name == null || player2Name.isEmpty()) {
            player2Name = "Player 2";
        }

        String targetScoreInput = JOptionPane.showInputDialog(this, "Enter the target score to win:", "5");
        try {
            targetScore = Integer.parseInt(targetScoreInput);
        } catch (NumberFormatException e) {
            targetScore = 5; // Default value
        }

        setTitle("Fun Pong Game");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        gamePanel = new GamePanel(player1Name, player2Name, targetScore, isArcadeMode);
        add(gamePanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        new PongGame();
    }

    class GamePanel extends JPanel implements KeyListener, ActionListener {

        private static final int PADDLE_WIDTH = 10;
        private static final int PADDLE_HEIGHT = 100;
        private static final int BALL_SIZE = 20;
        private static final int PADDLE_MOVE_STEP = 5;

        private int leftPaddleX = 30, leftPaddleY = 250;
        private int rightPaddleX = 750, rightPaddleY = 250;

        private int ballX = 390, ballY = 290;
        private double ballXVelocity = -6.5, ballYVelocity = 7.5;

        private int scoreLeft = 0, scoreRight = 0;

        private Timer gameTimer;

        private boolean upArrow, downArrow, wKey, sKey;

        private String leftPlayer, rightPlayer;
        private int winScore;

        private boolean arcadeMode;
        private Color bgCol = new Color(30, 30, 30);
        private Color paddleCol = new Color(100, 200, 255);
        private Color ballCol = new Color(255, 100, 150);

        public GamePanel(String playear1, String playear2, int target, boolean arcade) {
            leftPlayer = playear1;
            rightPlayer = playear2;
            winScore = target;
            arcadeMode = arcade;
            setBackground(bgCol);
            setFocusable(true);
            addKeyListener(this);
            gameTimer = new Timer(TIMER_DELAY, this);
            gameTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(paddleCol);
            g.fillRect(leftPaddleX, leftPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
            g.fillRect(rightPaddleX, rightPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
            g.setColor(ballCol);
            g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(Color.WHITE);
            g.drawString(leftPlayer + ": " + scoreLeft, 50, 50);
            g.drawString(rightPlayer + ": " + scoreRight, getWidth() - 250, 50);
            if (arcadeMode) {
                for (int i = 0; i < getWidth(); i += 50) {
                    g.setColor(new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255), 100));
                    g.fillRect(i, (int) (Math.random() * getHeight()), 50, 5);
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            moveBall();
            movePaddles();
            checkCollisions();
            checkForWinner();
            repaint();
        }

        private void moveBall() {
            ballX += ballXVelocity;
            ballY += ballYVelocity;

            if (ballY <= 0 || ballY >= getHeight() - BALL_SIZE) {
                ballYVelocity *= -1;
            }

            if (ballX <= 0) {
                scoreRight++;
                resetBall();
            }
            if (ballX >= getWidth() - BALL_SIZE) {
                scoreLeft++;
                resetBall();
            }
        }

        private void resetBall() {
            ballX = getWidth() / 2 - BALL_SIZE / 2;
            ballY = getHeight() / 2 - BALL_SIZE / 2;
            ballXVelocity *= -1;
            ballYVelocity *= -1;
        }

        private void movePaddles() {
            if (wKey && leftPaddleY > 0) {
                leftPaddleY -= PADDLE_MOVE_STEP;
            }
            if (sKey && leftPaddleY < getHeight() - PADDLE_HEIGHT) {
                leftPaddleY += PADDLE_MOVE_STEP;
            }
            if (upArrow && rightPaddleY > 0) {
                rightPaddleY -= PADDLE_MOVE_STEP;
            }
            if (downArrow && rightPaddleY < getHeight() - PADDLE_HEIGHT) {
                rightPaddleY += PADDLE_MOVE_STEP;
            }
        }

        private void checkCollisions() {
            if (ballX <= leftPaddleX + PADDLE_WIDTH && ballY + BALL_SIZE >= leftPaddleY && ballY <= leftPaddleY + PADDLE_HEIGHT) {
                ballXVelocity *= -1;
            }
            if (ballX + BALL_SIZE >= rightPaddleX && ballY + BALL_SIZE >= rightPaddleY && ballY <= rightPaddleY + PADDLE_HEIGHT) {
                ballXVelocity *= -1;
            }
        }

        private void checkForWinner() {
            if (scoreLeft >= winScore) {
                showWinner(leftPlayer);
            } else if (scoreRight >= winScore) {
                showWinner(rightPlayer);
            }
        }

        private void showWinner(String winner) {
            gameTimer.stop();

            Timer spiral = new Timer(15, null);
            spiral.addActionListener(new ActionListener() {
                int r = 10;
                double theta = 0;

                @Override
                public void actionPerformed(ActionEvent e) {
                    Graphics g = getGraphics();
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;

                    g.setColor(new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
                    int x = (int) (cx + r * Math.cos(theta));
                    int y = (int) (cy + r * Math.sin(theta));

                    g.fillOval(x, y, 10, 10);

                    theta += Math.PI / 16;
                    r += 2;

                    if (r > getWidth() / 2) {
                        spiral.stop();
                        JOptionPane.showMessageDialog(GamePanel.this, winner + " Wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                        SwingUtilities.invokeLater(() -> {
                            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(GamePanel.this);
                            topFrame.dispose();
                            new PongGame();
                        });
                    }
                }
            });
            spiral.start();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_W) wKey = true;
            if (key == KeyEvent.VK_S) sKey = true;
            if (key == KeyEvent.VK_UP) upArrow = true;
            if (key == KeyEvent.VK_DOWN) downArrow = true;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_W) wKey = false;
            if (key == KeyEvent.VK_S) sKey = false;
            if (key == KeyEvent.VK_UP) upArrow = false;
            if (key == KeyEvent.VK_DOWN) downArrow = false;
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }
    }
}
