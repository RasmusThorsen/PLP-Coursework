package UI.Controllers;

import Scala.Painter;
import UI.MainController;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import scala.collection.immutable.List;


public class DrawingAreaController {
    private MainController mainController;
    @FXML
    private Pane drawingPane;
    public void init(MainController mainController) {
        this.mainController = mainController;
        this.drawingPane.toBack();
    }

    public void draw(List<Painter.Element> elements) {
        this.drawingPane.getChildren().clear();
        elements.foreach((e ->  {
            if (e instanceof Painter.Shape) {
                ((Painter.Shape) e).points().foreach(p -> this.drawingPane.getChildren().add(new Circle(((int) p.x()), ((int) p.y()), 1, Color.RED)));
            } else if (e instanceof Painter.Text) {
                Painter.Text te = (Painter.Text) e;
                Text t = new Text(te.x1(), te.y1(), te.text());
                t.setFill(Color.valueOf(te.color()));
                this.drawingPane.getChildren().add(t);
            }
            return null;
        }));
    }
}

