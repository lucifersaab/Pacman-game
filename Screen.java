package Pacman;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static Pacman.PopUp.showCustomPopup;

public class Screen extends JFrame implements ActionListener {
    private static final int MAP_WIDTH = 20;
    private static final int MAP_HEIGHT = 17;
    private static final int CELL_SIZE = 25;

    private char[][] map;
    private int pacmanX;
    private int pacmanY;

    private final int[] ghostX = new int[3];
    private final int[] ghostY = new int[3];

    private int dx;
    private int dy;
    private static final int MOVE_DELAY_MS = 100;
    private static final int MOVE_DELAY_Ghost_MS = 160;

    int score= 0;

    int screen=CELL_SIZE*16;
    Stack<Character> stk= new Stack<>();


    public Screen() {
        loadMap();
        stk.push('.');
        setTitle("Pac-Man");
        setSize(MAP_WIDTH * CELL_SIZE, MAP_HEIGHT * CELL_SIZE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        Timer timer = new Timer(MOVE_DELAY_MS, e -> {
            if (death(pacmanX, pacmanY)) {
                showCustomPopup("YOUR SCORE: <br>" + score + " <br> <br> PRESS SPACE BAR TO CONTINUE");
                restartgame();
            }
            movePacman(pacmanX + dx, pacmanY + dy);

        });
        Timer timer2 = new Timer(MOVE_DELAY_Ghost_MS, e -> {
            for (j = 0; j < 2; j++) {
                moveGhost();
            }
        });
        timer.start();
        timer2.start();
    }

    private void loadMap() {
        int i=0;
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\hashi\\IdeaProjects\\package 2\\src\\Pacman\\path"))) {
            map = new char[MAP_WIDTH][MAP_HEIGHT];
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                for (int col = 0; col < line.length(); col++) {
                    char cell = line.charAt(col);
                    map[col][row] = cell;
                    if (cell == 'P') {
                        pacmanX = col;
                        pacmanY = row;
                    }
                    if (cell == 'G') {
                        ghostX[i] = col;
                        ghostY[i] = row;
                        i++;
                    }
                }
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static int prevGhostX , prevGhostY;
    int j = 0;

    private Point getNextMove() {
        int[][] distance = new int[MAP_WIDTH][MAP_HEIGHT];


        distance[ghostX[j]][ghostY[j]] = 0;

        PriorityQueue<Point> queue = new PriorityQueue<>(Comparator.comparingInt(p -> distance[p.x][p.y]));
        queue.add(new Point(ghostX[j],ghostY[j]));

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.x == pacmanX && current.y == pacmanY) {
                break;
            }

            int[][] neighbors = {
                    {-1, 0},
                    {1, 0},
                    {0, -1},
                    {0, 1}};
            for (int[] neighbor : neighbors) {
                int nx = current.x + neighbor[0];
                int ny = current.y + neighbor[1];
                if (nx >= 0 && nx < MAP_WIDTH && ny >= 0 && ny < MAP_HEIGHT && map[nx][ny] != '#' && distance[current.x][current.y] + 1 < distance[nx][ny]) {
                    distance[nx][ny] = distance[current.x][current.y] + 1;
                    queue.add(new Point(nx, ny));
                }
            }
        }

        int[][] moves = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int nextX = ghostX[j];
        int nextY = ghostY[j];
        int minDistance = Integer.MAX_VALUE;

        for (int[] move : moves) {
            int nx = ghostX[j] + move[0];
            int ny = ghostY[j] + move[1];

            if (nx == prevGhostX && ny == prevGhostY) {
                continue;
            }

            if (nx >= 0 && nx < MAP_WIDTH && ny >= 0 && ny < MAP_HEIGHT && map[nx][ny] != '#') {
                int dist = distance[nx][ny];
                if (dist < minDistance) {
                    nextX = nx;
                    nextY = ny;
                    minDistance = dist;
                } else if (dist == minDistance) {

                    int currentDist = Math.abs(nx - pacmanX) + Math.abs(ny - pacmanY);
                    int nextDist = Math.abs(nextX - pacmanX) + Math.abs(nextY - pacmanY);

                    if (currentDist < nextDist) {
                        nextX = nx;
                        nextY = ny;
                    }
                }
            }
        }

        return new Point(nextX, nextY);
    }


    private void moveGhost() {
        prevGhostX = ghostX[j];
        prevGhostY = ghostY[j];
        Point nextMove = getNextMove();
        int nextX = nextMove.x;
        int nextY = nextMove.y;
        if(map[nextX][nextY]!='G' && map[nextX][nextY]!='P')
            stk.push(map[nextX][nextY]);

        map[ghostX[j]][ghostY[j]] = stk.pop();
        ghostX[j] = nextX;
        ghostY[j] = nextY;
        stk.push(map[nextX][nextY]);
        map[ghostX[j]][ghostY[j]] = 'G';
        if(map[ghostX[j]][ghostY[j]]=='P'){
            showCustomPopup("YOUR SCORE: <br>" + score+" <br> <br> PRESS SPACE BAR TO CONTINUE");
            restartgame();
        }
        repaint();
    }

    public void paint(Graphics g) {

        Image buffer = createImage(getWidth(), getHeight());
        Graphics bufferGraphics = buffer.getGraphics();

        bufferGraphics.setColor(getBackground());
        bufferGraphics.fillRect(0, 0, getWidth(), getHeight());


        for (int row = 0; row < MAP_HEIGHT; row++) {
            for (int col = 0; col < MAP_WIDTH; col++) {
                char cell = map[col][row];
                Color color;
                switch (cell) {
                    case '.', '?' -> color = Color.GREEN;
                    case 'P' -> {
                        bufferGraphics.setColor(Color.GREEN);
                        bufferGraphics.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        bufferGraphics.setColor(Color.YELLOW);
                        bufferGraphics.fillOval(col * CELL_SIZE + 5, row * CELL_SIZE + 5, CELL_SIZE - 7, CELL_SIZE - 7);
                        continue;
                    }
                    case 'G' -> {
                        bufferGraphics.setColor(Color.GREEN);
                        bufferGraphics.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        bufferGraphics.setColor(Color.RED);
                        bufferGraphics.fillOval(col * CELL_SIZE + 5, row * CELL_SIZE + 5, CELL_SIZE - 7, CELL_SIZE - 7);
                        continue;
                    }
                    case ' ' -> color = Color.green;
                    default -> color = Color.BLACK;
                }
                bufferGraphics.setColor(color);
                bufferGraphics.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                if (cell == '.') {
                    bufferGraphics.setColor(Color.BLACK);
                    Font font = new Font("Arial", Font.BOLD, 20);
                    bufferGraphics.setFont(font);
                    bufferGraphics.drawString(".", col * CELL_SIZE + (CELL_SIZE / 2) - 5, row * CELL_SIZE + (CELL_SIZE / 2) + 5);
                }
            }
        }

        Font f= new Font("Arial", Font.BOLD, 15);
        bufferGraphics.setFont(f);
        bufferGraphics.setColor(new Color(0, 0, 0));
        String s = "Score: " + score;
        bufferGraphics.drawString(s, screen / 2 + 96, screen + 16);
        bufferGraphics.setColor(new Color(255, 255, 255));
        String s1 = "MADE BY H&F";
        bufferGraphics.drawString(s1,screen / 8 -10 ,screen + 16);


        g.drawImage(buffer, 0, 0, null);
    }

    boolean completed() {

        for (int row = 0; row < MAP_HEIGHT; row++) {
            for (int col = 0; col < MAP_WIDTH; col++) {
                char s = map[col][row];
                if (s == '.') {
                    return false;
                }
            }
        }
        return true;
    }

    private void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP -> {
                dx = 0;
                dy = -1;
            }
            case KeyEvent.VK_DOWN -> {
                dx = 0;
                dy = 1;
            }
            case KeyEvent.VK_LEFT -> {
                dx = -1;
                dy = 0;
            }
            case KeyEvent.VK_RIGHT -> {
                dx = 1;
                dy = 0;
            }
        }
    }

    boolean death(int newX, int newY){
        return ghostX[0] == pacmanX &&  ghostY[0] == pacmanY || ghostX[1] == pacmanX &&  ghostY[1] == pacmanY || map[newX][newY]=='G';
    }
    private void movePacman(int newX, int newY) {

        if (newX >= 0 && newX < MAP_WIDTH && newY >= 0 && newY < MAP_HEIGHT) {
            char destinationCell = map[newX][newY];

            if (destinationCell != '#' && destinationCell != 'G') {
                if (destinationCell == '.') {
                    score++;
                }
                map[pacmanX][pacmanY] = ' ';
                pacmanX = newX;
                pacmanY = newY;
                map[pacmanX][pacmanY] = 'P';
                repaint();
            }
            if(death(newX,newY)){
                showCustomPopup("YOUR SCORE: <br>" + score+" <br> <br> PRESS SPACE BAR TO CONTINUE");
                restartgame();
            }

            if(completed()){
                showCustomPopup("YOU WON! <br> PRESS SPACE BAR TO CONTINUE");

                restartgame();
            }
        }
    }

    void restartgame(){
        loadMap();
        score=0;
        repaint();

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Screen::new);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}