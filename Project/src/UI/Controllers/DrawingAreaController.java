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
    }

    @FXML
    public void initialize() {
        this.canvas.widthProperty().bind(pane.widthProperty());
        this.canvas.heightProperty().bind(pane.heightProperty());

        this.canvas.heightProperty().addListener(observable -> drawGrid());
        // this.canvas.widthProperty().addListener(observable -> drawGrid());
    }

    public void draw(List<Painter.Element> elements) {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        // clear canvas and draw grid
        gc.clearRect(0,0,this.canvas.getWidth(), this.canvas.getHeight());
        this.drawGrid();


        Timeline timeline = new Timeline();
        Duration timepoint = Duration.ZERO;
        Duration pause = Duration.seconds(1);

        for (Painter.Element e : elements) {
            // highlight
            KeyFrame highlighted = new KeyFrame(timepoint, evt -> {
                drawElementHighlighted(e, gc);
            });
            timeline.getKeyFrames().add(highlighted);

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
                writer.setColor(this.mapX(p.x()), this.mapY(p.y()), highlight);
                return null;
            });
        } else if (e instanceof Painter.Text) {
            Painter.Text te = (Painter.Text) e;
            gc.setFill(highlight);
            gc.fillText(te.text(), this.mapX(te.x1()), this.mapY(te.y1()));
        }
    }

    private void drawElement(Painter.Element e, GraphicsContext gc) {
        if (e instanceof Painter.Shape) {
            PixelWriter writer = gc.getPixelWriter();
            ((Painter.Shape) e).points().foreach(p -> {
                writer.setColor(this.mapX(p.x()), this.mapY(p.y()), Color.valueOf(p.color()));
                return null;
            });
        } else if (e instanceof Painter.Text) {
            Painter.Text te = (Painter.Text) e;
            gc.setFill(Paint.valueOf(te.color()));
            gc.fillText(te.text(), this.mapX(te.x1()), this.mapY(te.y1()));
        }
    }

    private int mapX(int x) {
        return x;
    }

    private int mapY(int y) {
        return (int)(this.canvas.getHeight()) - y;
    }

    private void drawGrid() {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        double height = this.canvas.getHeight();
        double width = this.canvas.getWidth();

        // vertical lines
        gc.setStroke(Color.LIGHTGRAY);
        for(int i = 0; i < width ; i+=30){
            gc.strokeLine(i, 0, i, height);
        }

        // horizontal lines
        for(int i = (int) height ; i > 0 ; i-=30){
            gc.strokeLine(0, i, width, i);
        }
    }
}

