package UI.Controllers;

import javafx.scene.paint.Color;

public class Point {
    public int X_Coordinate;
    public int Y_Coordinate;
    public Color color;

    public void Point(int x, int y, Color c) {
        this.X_Coordinate = x;
        this.Y_Coordinate = y;
        this.color = c;
    }
}
