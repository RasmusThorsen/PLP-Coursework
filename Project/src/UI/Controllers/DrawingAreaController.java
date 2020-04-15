package UI.Controllers;

import Scala.Painter;
import UI.MainController;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import scala.collection.immutable.List;


public class DrawingAreaController {
    private MainController mainController;
    @FXML
    private Pane drawingPane;
    public void init(MainController mainController) {
        this.mainController = mainController;
        this.drawingPane.toBack();
    }

    public void draw(List<Painter.Shape> shapes) {
        this.drawingPane.getChildren().clear();
        shapes.foreach((s ->  {
            s.points().foreach(p -> this.drawingPane.getChildren().add(new Circle(((int) p.x()), ((int) p.y()), 1, Color.RED)));
            return null;
        }));
    }

}

