package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class ProfileController {
	@FXML
    private TextField txt_address;

    @FXML
    private TextField txt_firstname;

    @FXML
    private ImageView ava_image;

    @FXML
    private TextField txt_phone;

    @FXML
    private Button btn_save;

    @FXML
    private TextField txt_lastname;

    @FXML
    private TextField txt_email;

    @FXML
    private DatePicker txt_dob;
}
