package UI.Controllers;


import UI.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;



public class SystemOutputController {
    @FXML private TextArea output;

    private MainController mainController;

    public void init(MainController mainController) {
        this.mainController = mainController;
    }

    public void setTextArea(String text) {
        output.setText(text);
    }

}
