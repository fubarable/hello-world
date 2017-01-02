package foo;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * link: http://stackoverflow.com/a/41418250/522444
 * @author Pete
 *
 */
public class Main2 extends JPanel {
    public static final String INPUT = "1111111111111111111111111111111111111111111\n"
            + "1000000010001000001000000010000000100000001\n"
            + "1010111010101010101111101011111010111111101\n"
            + "1010001010100010100000001010000010000010001\n"
            + "1011101010111110101111111010111111111010111\n"
            + "1000101010100000101000001000100010000010001\n"
            + "1011101011101011111011101111111010111110101\n"
            + "1010001000001010000010100000001010000010101\n"
            + "1010111111111010111110111111101011111011101\n"
            + "1010100000100010100000000000101000000000101\n"
            + "1110101111101110111110111011101011111110101\n"
            + "1000100000000010000010100010001000100010001\n"
            + "1011111111111111111011101010111111101011101\n"
            + "1000000000000000100010001010000000001010001\n"
            + "1011111111111011101110111011111111111010111\n"
            + "1000100010001000001010001000100000001010101\n"
            + "1110101011101111111010101110111110111010101\n"
            + "1000101010001000100000101000100000100010001\n"
            + "1011101010111010101111101011101110101111111\n"
            + "1000001010000010000000101000001000100010001\n"
            + "1111111011111110111111101111111011111010101\n"
            + "1000001010000010100010001000000010000010101\n"
            + "1011111010111011101010111011111110101110101\n"
            + "1010000010001010001010001000100000101010101\n"
            + "1010111111101010111011101111101111101011101\n"
            + "1000100000001010101010001000100010101000101\n"
            + "1011111011111010101010111010111010101011101\n"
            + "1010000010001000101010000010001010001000001\n"
            + "1010101110101111101011101111101011111010101\n"
            + "1010101000101000001000101000001000000010101\n"
            + "1011101011111010111110111011101111111110111\n"
            + "1000001000000010000000000010000000000010021\n"
            + "1111111111111111111111111111111111111111111\n";
    private List<List<MatrixPosition>> matrix = new ArrayList<>();

    public Main2() {
    }

    private static void createAndShowGui(MatrixModel model) {
        Main2 mainPanel = new Main2();

        JFrame frame = new JFrame("Main2");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        final MatrixModel model = MatrixUtil.getInput(MatrixUtil.PATH_TO_RSC);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGui(model);
            }
        });
    }
}

class MatrixUtil {
    public static final String PATH_TO_RSC = "input.txt";
    
    public static MatrixModel getInput(String resourcePath) {
        InputStream is = MatrixUtil.class.getResourceAsStream(resourcePath);
        return getInput(is);
    }
    
    public static MatrixModel getInput(InputStream is) {
        MatrixModel model = null;
        try (Scanner scan = new Scanner(is)) {
            List<List<MatrixPosition>> listOfLists = new ArrayList<>();
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if (line.trim().isEmpty()) {
                    continue;
                }
                List<MatrixPosition> list = new ArrayList<>();
                for (char c : line.toCharArray()) {
                    list.add(MatrixPosition.getMatrixPosition(String.valueOf(c)));
                }
                listOfLists.add(list);
            }
            MatrixPosition[][] grid = new MatrixPosition[listOfLists.size()][];
            for (int i = 0; i < grid.length; i++) {
                List<MatrixPosition> list = listOfLists.get(i);
                grid[i] = list.toArray(new MatrixPosition[] {});
            }
            model = new MatrixModel(grid, new Point(0, 0));
        }

        return model;
    }

}

class MatrixModel {
    public static final String SPRITE_POINT = "sprite point";
    private SwingPropertyChangeSupport pcSupport = new SwingPropertyChangeSupport(this);
    private MatrixPosition[][] grid;
    private Point spritePoint;

    public MatrixModel(MatrixPosition[][] grid, Point spritePoint) {
        this.grid = grid;
        this.spritePoint = spritePoint;
    }

    public void setSpritePoint(Point spritePoint) {
        Point oldValue = this.spritePoint;
        Point newValue = spritePoint;
        this.spritePoint = spritePoint;
        pcSupport.firePropertyChange(SPRITE_POINT, oldValue, newValue);
    }

    public boolean isPointValid(Point p) {
        if (p.x < 0 || p.y < 0) {
            return false;
        }
        if (p.x >= grid[0].length || p.y >= grid.length) {
            return false;
        }
        return grid[p.x][p.y] == MatrixPosition.CORRIDOR;
    }

    public boolean isMoveValid(Direction direction) {
        int x = spritePoint.x;
        int y = spritePoint.y;
        switch (direction) {
        case UP:
            return isPointValid(new Point(x, y - 1));
        case DOWN:
            return isPointValid(new Point(x, y + 1));
        case LEFT:
            return isPointValid(new Point(x - 1, y));
        case RIGHT:
            return isPointValid(new Point(x + 1, y));
        default:
            return false;
        }
    }

    public void move(Direction direction) {
        if (!isMoveValid(direction)) {
            String text = "For move to " + direction + "spritePoint: " + spritePoint;
            throw new IllegalArgumentException(text);
        }
        int x = spritePoint.x;
        int y = spritePoint.y;
        switch (direction) {
        case UP:
            setSpritePoint(new Point(x, y - 1));
        case DOWN:
            setSpritePoint(new Point(x, y + 1));
        case LEFT:
            setSpritePoint(new Point(x - 1, y));
        case RIGHT:
            setSpritePoint(new Point(x + 1, y));
            break;

        default:
            break;
        }
    }

    public Point getSpritePoint() {
        return spritePoint;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcSupport.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
        pcSupport.addPropertyChangeListener(name, listener);
    }

    public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
        pcSupport.removePropertyChangeListener(name, listener);
    }
}

enum Direction {
    UP, DOWN, LEFT, RIGHT
}

enum MatrixPosition {
    WALL(1), CORRIDOR(0), END(2);

    private int value;

    private MatrixPosition(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MatrixPosition getMatrixPosition(int value) {
        for (MatrixPosition position : MatrixPosition.values()) {
            if (value == position.getValue()) {
                return position;
            }
        }
        String text = "value of " + value;
        throw new IllegalArgumentException(text);
    }

    public static MatrixPosition getMatrixPosition(String strValue) {
        int value = -1;
        try {
            value = Integer.parseInt(strValue);
        } catch (NumberFormatException e) {
            String text = "NumberFormatException for strValue " + strValue;
            throw new IllegalAccessError(text);
        }
        return getMatrixPosition(value);
    }
}