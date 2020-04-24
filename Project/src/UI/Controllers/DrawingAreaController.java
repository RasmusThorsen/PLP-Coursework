package UI.Controllers;

import Scala.Painter;
import UI.MainController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.util.List;

public class DrawingAreaController {
    private MainController mainController;
    @FXML
    private Pane pane;
    @FXML
    private Canvas canvas;

    public void init(MainController mainController) {
        this.mainController = mainController;

        this.canvas.widthProperty().bind(pane.widthProperty());
        this.canvas.heightProperty().bind(pane.heightProperty());

        this.pane.toBack();
    }

    public void draw(List<Painter.Element> elements) {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        // clear canvas
        gc.clearRect(0,0,this.canvas.getWidth(), this.canvas.getHeight());

        Timeline timeline = new Timeline();
        Duration timepoint = Duration.ZERO;
        Duration pause = Duration.seconds(1);

        for (Painter.Element e : elements) {
            // highlight
            KeyFrame highligted = new KeyFrame(timepoint, evt -> {
                drawElementHighlighted(e, gc);
            });
            timeline.getKeyFrames().add(highligted);

            // delay
            timepoint = timepoint.add(pause);

            // remove highlight
            KeyFrame normal = new KeyFrame(timepoint, evt -> {
                drawElement(e, gc);
            });
            timeline.getKeyFrames().add(normal);
        }

        timeline.play();
    }

    private void drawElementHighlighted(Painter.Element e, GraphicsContext gc) {
        Color highlight = Color.YELLOWGREEN;

        if (e instanceof Painter.Shape) {
            PixelWriter writer = gc.getPixelWriter();
            ((Painter.Shape) e).points().foreach(p -> {
                writer.setColor(p.x(), p.y(), highlight);
                return null;
            });
        } else if (e instanceof Painter.Text) {
            Painter.Text te = (Painter.Text) e;
            gc.setFill(highlight);
            gc.fillText(te.text(), te.x1(), te.y1());
        }
    }

    private void drawElement(Painter.Element e, GraphicsContext gc) {
        if (e instanceof Painter.Shape) {
            PixelWriter writer = gc.getPixelWriter();
            ((Painter.Shape) e).points().foreach(p -> {
                writer.setColor(p.x(), p.y(), Color.valueOf(p.color()));
                return null;
            });
        } else if (e instanceof Painter.Text) {
            Painter.Text te = (Painter.Text) e;
            gc.setFill(Paint.valueOf(te.color()));
            gc.fillText(te.text(), te.x1(), te.y1());
        }
    }
}

