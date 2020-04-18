package UI.Controllers;


import Scala.Painter;
import UI.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import scala.Tuple3;
import scala.collection.immutable.List;

import static jdk.nashorn.internal.objects.Global.print;
import static jdk.nashorn.internal.objects.Global.println;


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
            this.mainController.sendShapesToDrawingArea(elements);
        } catch (IllegalArgumentException e) {
            this.mainController.sendValueToSystemOutput(e.getMessage());
        }
    }
}
