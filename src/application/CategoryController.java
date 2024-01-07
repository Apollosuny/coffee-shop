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
import store.categoryStore;



public class CategoryController {
		
		@FXML
		BorderPane bp;
		
		@FXML
		StackPane contentArea;
		
		@FXML
	    private TextField category_description;

	    @FXML
	    private Button btn_update_category;

	    @FXML
	    private TextField category_name;

	    @FXML
	    private Button btn_add_category;

	    @FXML
	    private Button btn_clear_category;

	    @FXML
	    private Button btn_delete_category;
	    
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
		
		public void addNewCategory() {
			if (category_name.getText().isEmpty()) {
	            
	            alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Error Message");
	            alert.setHeaderText(null);
	            alert.setContentText("Please fill name field!");
	            alert.showAndWait();
	            
	        } else {

	            // CHECK PRODUCT ID
	            String checkCategoryID = "SELECT name FROM categories WHERE name = '"
	                    + category_name.getText() + "'";
	            
	            connect = new Database().connectDB();
	            
	            try {
	                
	                prepare = connect.prepareStatement(checkCategoryID);
	                rs = prepare.executeQuery();
	                
	                if (rs.next()) {
	                    alert = new Alert(AlertType.ERROR);
	                    alert.setTitle("Error Message");
	                    alert.setHeaderText(null);
	                    alert.setContentText(category_name.getText() + " is already taken");
	                    alert.showAndWait();
	                } else {
	                    String insertData = "INSERT INTO categories "
	                            + "(name, description) "
	                            + "VALUES(?,?)";
	                    
	                    prepare = connect.prepareStatement(insertData);
	                    prepare.setString(1, category_name.getText());
	                    prepare.setString(2, category_description.getText() != null ? category_description.getText() : null);
	                    
	                    prepare.executeUpdate();
	                    
	                    alert = new Alert(AlertType.INFORMATION);
	                    alert.setTitle("Error Message");
	                    alert.setHeaderText(null);
	                    alert.setContentText("Successfully Added!");
	                    alert.showAndWait();
	                    
	                    showCategoryData();
	                    categoryClearBtn();
	                }
	                
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
		}
		
		public void categoryUpdateBtn() {
	        
	        if (category_name.getText().isEmpty()) {
	            
	            alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Error Message");
	            alert.setHeaderText(null);
	            alert.setContentText("Please fill name field");
	            alert.showAndWait();
	            
	        } else {
	            
	            String updateData = "UPDATE categories SET "
	                    + "name = '" + category_name.getText() + "', description = '"
	                    + category_description.getText() +  "' WHERE id = " + categoryStore.id;
	            
	            connect = new Database().connectDB();
	            
	            try {
	                
	                alert = new Alert(AlertType.CONFIRMATION);
	                alert.setTitle("Error Message");
	                alert.setHeaderText(null);
	                alert.setContentText("Are you sure you want to UPDATE category ID: " + categoryStore.id + "?");
	                Optional<ButtonType> option = alert.showAndWait();
	                
	                if (option.get().equals(ButtonType.OK)) {
	                    prepare = connect.prepareStatement(updateData);
	                    prepare.executeUpdate();
	                    
	                    alert = new Alert(AlertType.INFORMATION);
	                    alert.setTitle("Error Message");
	                    alert.setHeaderText(null);
	                    alert.setContentText("Successfully Updated!");
	                    alert.showAndWait();

	                    // TO UPDATE YOUR TABLE VIEW
	                    showCategoryData();
	                    // TO CLEAR YOUR FIELDS
	                    categoryClearBtn();
	                } else {
	                    alert = new Alert(AlertType.ERROR);
	                    alert.setTitle("Error Message");
	                    alert.setHeaderText(null);
	                    alert.setContentText("Cancelled.");
	                    alert.showAndWait();
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }
		
		public void categoryDeleteBtn() {
	        if (categoryStore.id == 0) {
	            
	            alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Error Message");
	            alert.setHeaderText(null);
	            alert.setContentText("Please fill all blank fields");
	            alert.showAndWait();
	            
	        } else {
	            alert = new Alert(AlertType.CONFIRMATION);
	            alert.setTitle("Error Message");
	            alert.setHeaderText(null);
	            alert.setContentText("Are you sure you want to DELETE Product ID: " + categoryStore.id + "?");
	            Optional<ButtonType> option = alert.showAndWait();
	            
	            if (option.get().equals(ButtonType.OK)) {
	                String deleteData = "DELETE FROM categories WHERE id = " + categoryStore.id;
	                try {
	                    prepare = connect.prepareStatement(deleteData);
	                    prepare.executeUpdate();
	                    
	                    alert = new Alert(AlertType.ERROR);
	                    alert.setTitle("Error Message");
	                    alert.setHeaderText(null);
	                    alert.setContentText("Successfully Deleted!");
	                    alert.showAndWait();

	                    // TO UPDATE YOUR TABLE VIEW
	                    showCategoryData();
	                    // TO CLEAR YOUR FIELDS
	                    categoryClearBtn();
	                    
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            } else {
	                alert = new Alert(AlertType.ERROR);
	                alert.setTitle("Error Message");
	                alert.setHeaderText(null);
	                alert.setContentText("Cancelled");
	                alert.showAndWait();
	            }
	        }
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
		
		public void categorySelectData() {
	        
	        CategoryDTO categoryData = category_table.getSelectionModel().getSelectedItem();
	        int num = category_table.getSelectionModel().getSelectedIndex();
	        
	        if ((num - 1) < -1) {
	            return;
	        }
	        
	        category_name.setText(categoryData.getName());
	        category_description.setText(categoryData.getDescription());
	        
	        categoryStore.id = categoryData.getId();
	    }
		
		
		public void categoryClearBtn() {
			category_name.setText("");
			category_description.setText("");
		}
		
		public void initialize() {
			showCategoryData();
		}
	}

