package UI;

import UI.Controllers.DrawingAreaController;
import UI.Controllers.MenuBarController;
import UI.Controllers.SystemOutputController;
import UI.Controllers.UserInputController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Window;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController {
    @FXML private MenuBarController menuBarController;
    @FXML private DrawingAreaController drawingAreaController;
    @FXML private UserInputController userInputController;
    @FXML private SystemOutputController systemOutputController;

    public void initialize(){
        System.out.println("Test");
    }

}
