package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AdminController {
	
	@FXML
	BorderPane bp;
	
	@FXML
	StackPane contentArea;
	
	@FXML 
	private Button btn_logout;
	
	private Alert alert;
	
	private Connection connect;
	private PreparedStatement prepare;
	private ResultSet rs;
	
	public void initialize() {
	}
	
	@FXML
	public void dashboard (MouseEvent event) {
		bp.setCenter(contentArea);
	}
	
	@FXML
	public void products (MouseEvent event) {
		loadPage("Products");
	}
	
	private void loadPage(String page) {
		Parent root = null;
		
		try {
			root = FXMLLoader.load(getClass().getResource("/ui/" + page + ".fxml"));
		} catch (Exception e) {
			Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, e);
		}
		
		bp.setCenter(root);
	}
	
	@FXML
	public void logout(ActionEvent event) {
		try {
            
            alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");
            Optional<ButtonType> option = alert.showAndWait();
            
            if (option.get().equals(ButtonType.OK)) {
                // LINK YOUR LOGIN FORM AND SHOW IT 
                Parent root = FXMLLoader.load(getClass().getResource("/ui/Login.fxml"));
        		Stage stage = new Stage();
        		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        		Scene scene = new Scene(root);
        		stage.setScene(scene);
        		stage.show();
                
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
