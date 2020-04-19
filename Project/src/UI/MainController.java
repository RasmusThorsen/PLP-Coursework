package UI;

import Scala.Painter;
import UI.Controllers.DrawingAreaController;
import UI.Controllers.MenuBarController;
import UI.Controllers.SystemOutputController;
import UI.Controllers.UserInputController;
import javafx.fxml.FXML;
import scala.collection.immutable.List;

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

    public void clearSystemOutput() {systemOutputController.clear();}

    public void sendValueToSystemOutput (String text) {
        systemOutputController.append(text);
    }

    public void sendShapesToDrawingArea(List<Painter.Element> elements) {
        drawingAreaController.draw(elements);
    }
}
