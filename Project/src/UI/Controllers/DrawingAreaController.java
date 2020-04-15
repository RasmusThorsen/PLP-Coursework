package UI.Controllers;

import Scala.Painter;
import UI.MainController;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import scala.Tuple2;
import scala.collection.JavaConverters;
import scala.collection.immutable.List;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DrawingAreaController {
    private MainController mainController;
    @FXML
    private Pane drawingPane;
    public void init(MainController mainController) {
        this.mainController = mainController;
        this.drawingPane.toBack();
    }

    public void draw() {
        Circle c = new Circle(500, Color.DARKGRAY);
        List<Tuple2<Object, Object>> el = Painter.Circle(100,100,50);
        el.foreach((e -> this.drawingPane.getChildren().add(new Circle(((int) e._1()), ((int) e._2()), 1, Color.RED))));

    }
}

