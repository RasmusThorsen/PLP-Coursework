package UI.Controllers;


import javafx.fxml.FXML;
import javafx.scene.control.TextArea;



public class SystemOutputController {
    @FXML
    private TextArea output;


    public void initialize() {
        System.out.println("SystemOutput");
    }

}
