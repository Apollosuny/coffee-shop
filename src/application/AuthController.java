package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import store.authStore;

public class AuthController {
	@FXML
	private Hyperlink register_link;
	
	@FXML
	private Hyperlink login_link;
	
	@FXML
	private TextField login_username;
	
	@FXML
	private TextField register_username;
	
	@FXML
	private TextField register_email;
	
	@FXML
	private PasswordField login_password;
	
	@FXML
	private PasswordField register_password;
	
	@FXML
	private Button btn_login;
	
	@FXML
	private Button btn_register;
	
	private Alert alert;
	
	private Connection connect;
	private PreparedStatement prepare;
	private ResultSet rs;
	
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
	
	@FXML
	private void redirectLogin(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/ui/Login.fxml"));
		Stage stage = new Stage();
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	@FXML
	private void redirectDashboard(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/ui/Layout.fxml"));
		Stage stage = new Stage();
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	@FXML
	private void redirectAdminDashboard(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/ui/AdminLayout.fxml"));
		Stage stage = new Stage();
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	@FXML
	private void handleRegister(ActionEvent event) {
		if (register_username.getText().isEmpty() || register_email.getText().isEmpty() || register_password.getText().isEmpty()) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
        } else {
            
            String regData = "INSERT INTO users (username, email, password, role, created_at, updated_at) "
                    + "VALUES(?,?,?,?,?,?)";
            connect = new Database().connectDB();
            
            try {
                // CHECK IF THE USERNAME IS ALREADY RECORDED
                String checkUsername = "SELECT username FROM users WHERE username = '"
                        + register_username.getText() + "'";
                
                prepare = connect.prepareStatement(checkUsername);
                rs = prepare.executeQuery();
                
                if (rs.next()) {
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText(register_username.getText() + " is already taken");
                    alert.showAndWait();
                } else if (register_password.getText().length() < 8) {
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid Password, atleast 8 characters are needed");
                    alert.showAndWait();
                } else {
                    prepare = connect.prepareStatement(regData);
                    prepare.setString(1, register_username.getText());
                    prepare.setString(2, register_email.getText());
                    prepare.setString(3, register_password.getText());
                    prepare.setString(4, "user");
                    
                    java.util.Date date = new java.util.Date();
                    Date timestamp = new Date(date.getTime());
                    
                    prepare.setDate(5, timestamp);
                    prepare.setDate(6, timestamp);
                    prepare.executeUpdate();
                    
                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully registered Account!");
                    alert.showAndWait();
                    
                    register_username.setText("");
                    register_email.setText("");
                    register_password.setText("");

                    redirectLogin(event);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

	@FXML
	private void handleLogin(ActionEvent event) {
		if (login_username.getText().isEmpty() || login_password.getText().isEmpty()) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Incorrect Username/Password");
            alert.showAndWait();
        } else {
            
            String selctData = "SELECT username, password, role FROM users WHERE username = ? and password = ?";
            
            connect = new Database().connectDB();
            
            try {
                
                prepare = connect.prepareStatement(selctData);
                prepare.setString(1, login_username.getText());
                prepare.setString(2, login_password.getText());
                
                rs = prepare.executeQuery();
                // IF SUCCESSFULLY LOGIN, THEN PROCEED TO ANOTHER FORM WHICH IS OUR MAIN FORM 
                if (rs.next()) {
                    // TO GET THE USERNAME THAT USER USED
                    authStore.username = login_username.getText();
                    authStore.role = rs.getString("role");
                    
                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Login!");
                    alert.showAndWait();
                    
                 // LINK YOUR MAIN FORM
                    if (authStore.role.equalsIgnoreCase("user"))
                    	redirectDashboard(event);
                    else if (authStore.role.equalsIgnoreCase("admin"))
                    	redirectAdminDashboard(event);
                    
                    
                } else { // IF NOT, THEN THE ERROR MESSAGE WILL APPEAR
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Incorrect Username/Password");
                    alert.showAndWait();
                } 
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
	}
}
