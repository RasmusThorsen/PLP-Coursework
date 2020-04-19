package UI.Controllers;

import Scala.Painter;
import UI.MainController;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import scala.collection.immutable.List;


public class DrawingAreaController {
    private MainController mainController;
    @FXML
    private Canvas canvas;
    public void init(MainController mainController) {
        this.mainController = mainController;
        this.canvas.toBack();
    }

    public void draw(List<Painter.Element> elements) {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        // clear canvas
        gc.clearRect(0,0,this.canvas.getHeight(), this.canvas.getWidth());

        PixelWriter writer = gc.getPixelWriter();
        elements.foreach((e ->  {
            if (e instanceof Painter.Shape) {
                ((Painter.Shape) e).points().foreach(p -> {
                    writer.setColor(p.x(), p.y(), Color.valueOf(p.color()));
                    return null;
                });
            } else if (e instanceof Painter.Text) {
                Painter.Text te = (Painter.Text) e;
                gc.setFill(Paint.valueOf(te.color()));
                gc.fillText(te.text(), te.x1(), te.y1());
            }
            return null;
        }));
    }
}

