package foo1;

public class SpritePosition {
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