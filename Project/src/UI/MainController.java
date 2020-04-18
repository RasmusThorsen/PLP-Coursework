package UI;

import Scala.Painter;
import UI.Controllers.DrawingAreaController;
import UI.Controllers.MenuBarController;
import UI.Controllers.SystemOutputController;
import UI.Controllers.UserInputController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Window;
import scala.collection.immutable.List;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController {
    @FXML private MenuBarController menuBarController;
    @FXML private DrawingAreaController drawingAreaController;
    @FXML private UserInputController userInputController;
    @FXML private SystemOutputController systemOutputController;

    public void initialize(){
        menuBarController.init(this);
        drawingAreaController.init(this);
        userInputController.init(this);
        systemOutputController.init(this);
    }


    public void sendValueToSystemOutput (String text) {
        systemOutputController.setTextArea(text);

    }

    public void sendShapesToDrawingArea(List<Painter.Element> elements) {
        drawingAreaController.draw(elements);
        systemOutputController.setTextArea("");
    }
}
