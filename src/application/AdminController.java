package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Observable;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import database.Database;
import dto.CategoryDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
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
    
    @FXML
    private BarChart<?, ?> db_income_chart;
    
    @FXML
    private LineChart<?, ?> db_order_chart;

	
	private Alert alert;
	
	private Connection connect;
	private PreparedStatement prepare;
	private ResultSet rs;
	
	private ObservableList<CategoryDTO> categoryList;
	
	@FXML
	public void dashboard (MouseEvent event) {
		bp.setCenter(contentArea);
	}
	
	@FXML
	public void products (MouseEvent event) {
		loadPage("Products");
	}
	
	@FXML 
	public void categories (MouseEvent event) {
		loadPage("Categories");
	}
	
	@FXML 
	public void menu (MouseEvent event) {
		loadPage("Menu");
	}
	
	@FXML 
	public void orders (MouseEvent event) {
		loadPage("Orders");
	}
	
	public void income_chart() {
		db_income_chart.getData().clear();
		
		String sql = "SELECT export_date, SUM(total_bill) AS total_amount "
				+ "FROM bill "
				+ "GROUP BY export_date "
				+ "ORDER BY export_date";
		connect = new Database().connectDB();
		XYChart.Series chart = new XYChart.Series();
		
		try {
			prepare = connect.prepareStatement(sql);
			rs = prepare.executeQuery();
			
			while(rs.next()) {
				chart.getData().add(new XYChart.Data<>(rs.getString(1), rs.getFloat(2)));
			}
			
			db_income_chart.getData().add(chart);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void order_chart() {
		db_order_chart.getData().clear();
		
		String sql = "SELECT created_at, SUM(order_id) AS total_order "
				+ "FROM orders "
				+ "GROUP BY created_at "
				+ "ORDER BY created_at";
		connect = new Database().connectDB();
		XYChart.Series chart = new XYChart.Series();
		
		try {
			prepare = connect.prepareStatement(sql);
			rs = prepare.executeQuery();
			
			while(rs.next()) {
				chart.getData().add(new XYChart.Data<>(rs.getString(1), rs.getFloat(2)));
			}
			
			db_order_chart.getData().add(chart);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
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
	
	public void initialize() {
		income_chart();
		order_chart();
	}
}
