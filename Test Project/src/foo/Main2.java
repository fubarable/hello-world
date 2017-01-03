package foo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
// import java.awt.SpritePosition;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * link: http://stackoverflow.com/a/41418250/522444
 * 
 * @author Pete
 *
 */
@SuppressWarnings("serial")
public class Main2 extends JPanel {
    private MainPanel mainPanel;

    public Main2(MatrixModel matrixModel) {
        mainPanel = new MainPanel(matrixModel);
        new Controller(matrixModel, mainPanel);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    private static void createAndShowGui(MatrixModel model) {
        Main2 mainPanel = new Main2(model);

        JFrame frame = new JFrame("Main2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
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

class Controller {
    private MatrixModel model;
    private MainPanel view;
    private Map<Direction, KeyStroke> dirKeyMap = new EnumMap<>(Direction.class);

    public Controller(MatrixModel model, MainPanel view) {
        this.model = model;
        this.view = view;

        dirKeyMap.put(Direction.DOWN, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        dirKeyMap.put(Direction.UP, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
        dirKeyMap.put(Direction.LEFT, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0));
        dirKeyMap.put(Direction.RIGHT, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));

        model.addPropertyChangeListener(new ModelListener());
        setUpKeyBindings(view);
    }

    private void setUpKeyBindings(MainPanel view) {
        int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap inputMap = view.getInputMap(condition);
        ActionMap actionMap = view.getActionMap();
        for (Direction dir : Direction.values()) {
            KeyStroke keyStroke = dirKeyMap.get(dir);
            hookUp(inputMap, actionMap, dir, keyStroke);
        }
    }

    private void hookUp(InputMap inputMap, ActionMap actionMap, Direction dir, KeyStroke key) {
        inputMap.put(key, key.toString());
        actionMap.put(key.toString(), new MoveAction(dir, model));
    }

    public MatrixModel getModel() {
        return model;
    }

    public MainPanel getView() {
        return view;
    }

    class ModelListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (MatrixModel.SPRITE_POINT.equals(evt.getPropertyName())) {
                SpritePosition p = model.getSpritePosition();
                view.setSpritePoint(p);
            }
        }
    }

}

@SuppressWarnings("serial")
class MoveAction extends AbstractAction {
    private Direction dir;
    private MatrixModel model;

    public MoveAction(Direction dir, MatrixModel model) {
        super(dir.toString());
        this.dir = dir;
        this.model = model;
    }

    public void actionPerformed(ActionEvent e) {
        if (model.isMoveValid(dir)) {
            model.move(dir);
        }
    }
}

@SuppressWarnings("serial")
class MainPanel extends JPanel {
    private static final int CELL_WIDTH = 20;
    private static final Color CORRIDOR_COLOR = Color.LIGHT_GRAY;
    private static final Color WALL_COLOR = Color.DARK_GRAY;
    private static final Color END_COLOR = Color.ORANGE;
    private static final Color SPRITE_COLOR = Color.RED;
    private static final int GAP = 1;
    private BufferedImage gridImg = null;
    private SpritePosition spritePosition;

    public MainPanel(MatrixModel matrixModel) {
        gridImg = createImg(matrixModel);
        spritePosition = matrixModel.getSpritePosition();
    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet() || gridImg == null) {
            return super.getPreferredSize();
        }
        int prefW = gridImg.getWidth();
        int prefH = gridImg.getHeight();
        return new Dimension(prefW, prefH);
    }

    public void setSpritePoint(SpritePosition spritePosition) {
        this.spritePosition = spritePosition;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gridImg != null) {
            g.drawImage(gridImg, 0, 0, this);
        }
        g.setColor(SPRITE_COLOR);
        int y = spritePosition.row * CELL_WIDTH + GAP;
        int x = spritePosition.column * CELL_WIDTH + GAP;
        g.fillRect(x, y, CELL_WIDTH - 2 * GAP, CELL_WIDTH - 2 * GAP);
    }

    private BufferedImage createImg(MatrixModel matrixModel) {
        BufferedImage img = null;
        if (matrixModel != null && matrixModel.getRows() > 0) {
            int w = matrixModel.getColumns() * CELL_WIDTH;
            int h = matrixModel.getRows() * CELL_WIDTH;
            img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            for (int row = 0; row < matrixModel.getRows(); row++) {
                for (int col = 0; col < matrixModel.getColumns(); col++) {
                    MatrixPosition position = matrixModel.getPosition(row, col);
                    Color c = null;
                    switch (position) {
                    case CORRIDOR:
                        c = CORRIDOR_COLOR;
                        break;
                    case WALL:
                        c = WALL_COLOR;
                        break;
                    case END:
                        c = END_COLOR;
                        break;
                    }
                    g2.setColor(c);
                    int x = col * CELL_WIDTH;
                    int y = row * CELL_WIDTH;
                    g2.fillRect(x, y, CELL_WIDTH, CELL_WIDTH);
                }
            }
            g2.dispose();
        }
        return img;
    }

}

class MatrixUtil {
    public static final String PATH_TO_RSC = "input.txt";

    public static MatrixModel getInput(String resourcePath) {
        InputStream is = MatrixUtil.class.getResourceAsStream(resourcePath);
        if (is == null) {
            String text = "resourcePath is not found and not loading text: " + resourcePath;
            throw new IllegalArgumentException(text);
        }
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
            model = new MatrixModel(grid, new SpritePosition(1, 1));
        }

        return model;
    }

}

class MatrixModel {
    public static final String SPRITE_POINT = "sprite point";
    private SwingPropertyChangeSupport pcSupport = new SwingPropertyChangeSupport(this);
    private MatrixPosition[][] grid;
    private SpritePosition spritePosition;

    public MatrixModel(MatrixPosition[][] grid, SpritePosition spritePosition) {
        this.grid = grid;
        this.spritePosition = spritePosition;
    }

    public int getRows() {
        return grid.length;
    }

    public int getColumns() {
        return grid[0].length;
    }

    public MatrixPosition getPosition(SpritePosition p) {
        return getPosition(p.row, p.column);
    }

    public MatrixPosition getPosition(int row, int col) {
        return grid[row][col];
    }

    public void setSpritePoint(SpritePosition spritePosition) {
        SpritePosition oldValue = this.spritePosition;
        SpritePosition newValue = spritePosition;
        this.spritePosition = spritePosition;
        pcSupport.firePropertyChange(SPRITE_POINT, oldValue, newValue);
    }

    public boolean isPointValid(SpritePosition p) {
        if (p.column < 0 || p.row < 0) {
            return false;
        }
        if (p.column >= grid[0].length || p.row >= grid.length) {
            return false;
        }
        return grid[p.row][p.column] == MatrixPosition.CORRIDOR;
    }

    public boolean isMoveValid(Direction direction) {
        int row = spritePosition.row;
        int column = spritePosition.column;
        switch (direction) {
        case UP:
            return isPointValid(new SpritePosition(row - 1, column));
        case DOWN:
            return isPointValid(new SpritePosition(row + 1, column));
        case LEFT:
            return isPointValid(new SpritePosition(row, column - 1));
        case RIGHT:
            return isPointValid(new SpritePosition(row, column + 1));
        default:
            return false;
        }
    }

    public void move(Direction direction) {
        if (!isMoveValid(direction)) {
            String text = "For move to " + direction + "spritePosition: " + spritePosition;
            throw new IllegalArgumentException(text);
        }
        int row = spritePosition.row;
        int column = spritePosition.column;
        switch (direction) {
        case UP:
            setSpritePoint(new SpritePosition(row - 1, column));
            break;
        case DOWN:
            setSpritePoint(new SpritePosition(row + 1, column));
            break;
        case LEFT:
            setSpritePoint(new SpritePosition(row, column - 1));
            break;
        case RIGHT:
            setSpritePoint(new SpritePosition(row, column + 1));
            break;

        default:
            break;
        }
    }

    public SpritePosition getSpritePosition() {
        return spritePosition;
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

class SpritePosition {
    int row;
    int column;

    public SpritePosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
    
    public void setRowColumn(int row, int column) {
        this.row = row;
        this.column = column;
    }

}