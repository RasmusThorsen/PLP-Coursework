package UI.Controllers;

import Scala.Painter;
import UI.MainController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
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

        this.canvas.heightProperty().addListener(observable -> {
            this.clear();
            this.drawGrid();
        });
    }

    public void draw(List<Painter.Element> elements) {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        // clear canvas and draw grid
        this.clear();
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
            this.drawShape((Painter.Shape) e, gc, highlight);
        } else if (e instanceof Painter.Text) {
            this.drawText((Painter.Text) e, gc, highlight);
        }
    }

    private void drawElement(Painter.Element e, GraphicsContext gc) {
        if (e instanceof Painter.Shape) {
            this.drawShape((Painter.Shape) e, gc);
        } else if (e instanceof Painter.Text) {
            this.drawText((Painter.Text) e, gc, Color.valueOf(((Painter.Text) e).color()));
        }
    }

    private void drawShape(Painter.Shape shape, GraphicsContext gc, Color color) {
        PixelWriter writer = gc.getPixelWriter();
        shape.points().foreach(p -> {
            writer.setColor(this.mapX(p.x()), this.mapY(p.y()), color);
            return null;
        });
    }

    private void drawShape(Painter.Shape shape, GraphicsContext gc) {
        PixelWriter writer = gc.getPixelWriter();
        shape.points().foreach(p -> {
            writer.setColor(this.mapX(p.x()), this.mapY(p.y()), Color.valueOf(p.color()));
            return null;
        });
    }

    private void drawText(Painter.Text text, GraphicsContext gc, Color color) {
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(new Font(18));
        gc.setFill(color);
        gc.fillText(text.text(), this.mapX(text.x()), this.mapY(text.y()));
    }

    private int mapX(int x) {
        return x;
    }

    private int mapY(int y) {
        return (int)(this.canvas.getHeight()) - y;
    }

    private double mapX(double x) {
        return x;
    }

    private double mapY(double y) {
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

    private void clear() {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        gc.clearRect(0,0,this.canvas.getWidth(), this.canvas.getHeight());
    }
}

