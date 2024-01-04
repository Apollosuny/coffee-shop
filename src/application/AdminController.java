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
    private ComboBox<?> product_type;

    @FXML
    private ImageView product_image;

    @FXML
    private Button btn_update_product;

    @FXML
    private Button btn_add_product;

    @FXML
    private TextField product_price;

    @FXML
    private TableView<?> product_table;

    @FXML
    private TextField product_name;

    @FXML
    private Button btn_import_image;

    @FXML
    private Button btn_clear_product;

    @FXML
    private TextField product_quantity;

    @FXML
    private Button btn_delete_product;
    
    @FXML
    private TableView<CategoryDTO> category_table;
    
    @FXML
    private TableColumn<CategoryDTO, String> col_desc_category;

    @FXML
    private TableColumn<CategoryDTO, Integer> col_id_category;

    @FXML
    private TableColumn<CategoryDTO, String> col_name_category;

	
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
	
	public ObservableList<CategoryDTO> categoryDataList() {
		ObservableList<CategoryDTO> list = FXCollections.observableArrayList();
		
		String sql = "SELECT * FROM categories";
		
		connect = new Database().connectDB();
		
		try {
			prepare = connect.prepareStatement(sql);
			rs = prepare.executeQuery();
			
			CategoryDTO categoryData;
			
			while(rs.next()) {
				categoryData = new CategoryDTO(rs.getInt("id"), rs.getString("name"), rs.getString("description"));
				list.add(categoryData);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public void showCategoryData () {
		categoryList = categoryDataList();
		System.out.println(category_table);
		
		col_id_category.setCellValueFactory(new PropertyValueFactory<>("id"));
		col_name_category.setCellValueFactory(new PropertyValueFactory<>("name"));
		col_desc_category.setCellValueFactory(new PropertyValueFactory<>("description"));
		
		category_table.setItems(categoryList);
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
//		showCategoryData();
	}
}
