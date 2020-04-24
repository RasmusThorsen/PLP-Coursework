package UI.Controllers;

import Scala.Painter;
import UI.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.util.List;


public class UserInputController {
    @FXML private Button btnDraw;
    @FXML private TextArea inputText;

    private MainController mainController;

    public void init(MainController mainController) {
        this.mainController = mainController;
    }

    public void handleBtnDrawClick() {
        try {
            List<Painter.Element> elements = Painter.Draw(this.inputText.getText());
            this.mainController.clearSystemOutput();
            this.mainController.sendShapesToDrawingArea(elements);
        } catch (IllegalArgumentException e) {
            this.mainController.clearSystemOutput();
            this.mainController.sendValueToSystemOutput(e.getMessage());
        }
    }
}
