package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

public class LoginController {
	@FXML
	private Hyperlink register_link;
	
	@FXML
	private void redirectRegister(ActionEvent e) throws IOException
	{
		Parent root = FXMLLoader.load(getClass().getResource("/ui/Register.fxml"));
		Stage stage = new Stage();
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
