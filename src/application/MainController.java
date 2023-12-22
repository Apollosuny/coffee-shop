package application;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class MainController {
	
	@FXML
	BorderPane bp;
	
	@FXML
	StackPane contentArea;
	
	public void initialize() {
	}
	
	@FXML
	public void home (MouseEvent event) {
		bp.setCenter(contentArea);
	}
	
	@FXML
	public void coffee (MouseEvent event) {
		loadPage("Coffee");
	}
	
	private void loadPage(String page) {
		Parent root = null;
		
		try {
			root = FXMLLoader.load(getClass().getResource("/ui/" + page + ".fxml"));
		} catch (Exception e) {
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
		}
		
		bp.setCenter(root);
	}
}
