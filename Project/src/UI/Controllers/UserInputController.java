package UI.Controllers;


import UI.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import scala.Int;


public class UserInputController {
    @FXML private Button btnDraw;
    @FXML private TextArea inputText;

    private MainController mainController;

    public void init(MainController mainController) {
        this.mainController = mainController;
    }

    public void handleBtnDrawClick() {
        String test = Scala.Painter.Line(0,0,4,4,0).toString();
        mainController.sendValueToSystemOutput(test);
    }
}
