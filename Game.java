import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Random;

public class Game extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int width;
    int height;
    int tileSize = 25;

    // Snake
    ArrayList<Tile> snakeBody;

    // Food
    Tile food;
    Random random;

    // Game Logic
    Timer gameLoop;
    Tile velocity;
    Boolean pickedApple = false;
    Boolean alive = true;

    Game(int width, int height) {
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(this.width, this.height));
        setBackground(new Color(30, 30, 30));
        addKeyListener(this);
        setFocusable(true);

        this.random = new Random();
        this.snakeBody = new ArrayList<Tile>();
        this.snakeBody.add(new Tile(this.random.nextInt(width/tileSize), this.random.nextInt(height/tileSize)));
        this.food = new Tile(0, 0);

        placeFood();

        gameLoop = new Timer(400, this);
        gameLoop.start();

        velocity = new Tile(0, 0);
    }

    public void move() {
        for (int i = snakeBody.size()-1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.x += velocity.x;
                snakePart.y += velocity.y;
            } else {
                Tile prevSnakePart = snakeBody.get(i-1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        if (collision(snakeBody.get(0), food)) {
            pickedApple = true;
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // grid
        for (int i = 0; i < width/tileSize; i++) {
            g.drawLine(0, i*tileSize, width, i*tileSize);
            g.drawLine(i*tileSize, 0, i*tileSize, height);
        }

        // Snake
        g.setColor(Color.green);
        Tile snakeHead = snakeBody.get(0);
        g.fillRect(snakeHead.x*tileSize, snakeHead.y*tileSize, tileSize, tileSize);

        g.setColor(new Color(0, 200, 0));

        for (int i = 1; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            g.fillRect(snakePart.x*tileSize, snakePart.y*tileSize, tileSize, tileSize);
        }

        // Food
        g.setColor(Color.red);
        g.fillRect(food.x*tileSize, food.y*tileSize, tileSize, tileSize);

        // Score
        g.setFont(new Font("Monospace", 0, 72));
        g.setColor(new Color(200, 200, 200));
        g.drawString(Integer.toString(snakeBody.size()), width/2-20, height/5);

        // Game Over Screen
        if (!alive) {
            g.setColor(Color.red);
            g.setFont(new Font("Monospace", 0, 96));
            g.drawString("Game Over!", width/2-290, height / 2);
        }
    }

    public boolean checkFoodCollision(Tile food) {
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            if (collision(snakePart, food)) {
                return true;
            }
        }

        return false;
    }

    public boolean checkIfAlive() {
        Tile snakeHead = snakeBody.get(0);

        if (snakeHead.x < 0 || snakeHead.y < 0 || snakeHead.x >= width/tileSize || snakeHead.y >= height/tileSize) {
            return false;
        }

        for (int i = 1; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            if (collision(snakeHead, snakePart)) {
                return false;
            }
        }

        return true;
    }

    public void placeFood() {
        boolean performed = false;
        while (checkFoodCollision(food) || !performed) {
            food.x = random.nextInt(width / tileSize);
            food.y = random.nextInt(height / tileSize);
            performed = true;
        }
    }

    public boolean collision(Tile t1, Tile t2) {
        return t1.x == t2.x && t1.y == t2.y;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocity.y != 1) {
            velocity.x = 0;
            velocity.y = -1;
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN && velocity.y != -1) {
            velocity.x = 0;
            velocity.y = 1;
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT && velocity.x != 1) {
            velocity.x = -1;
            velocity.y = 0;
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocity.x != -1) {
            velocity.x = 1;
            velocity.y = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        if (!pickedApple) {
            alive = checkIfAlive();
            if (!alive) {
                gameLoop.stop();
                System.out.println(snakeBody.size());
            }
        }
        repaint();
        pickedApple = false;
    }
}
