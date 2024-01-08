package application;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import database.Database;
import dto.CategoryDTO;
import dto.ProductDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import store.productStore;

public class ProductController {
	@FXML
    private ComboBox<String> product_type;

    @FXML
    private ImageView product_image;

    @FXML
    private Button btn_update_product;

    @FXML
    private Button btn_add_product;

    @FXML
    private TextField product_price;

    @FXML
    private TableView<ProductDTO> product_table;
    
    @FXML
    private TableColumn<ProductDTO, String> col_product_name;
    
    @FXML
    private TableColumn<ProductDTO, Integer> col_product_id;
    
    @FXML
    private TableColumn<ProductDTO, Boolean> col_product_status;
    
    @FXML
    private TableColumn<ProductDTO, String> col_product_category;
    
    @FXML
    private TableColumn<ProductDTO, Integer> col_product_quantity;

    @FXML
    private TableColumn<ProductDTO, Float> col_product_price;

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
    private AnchorPane product_form;
    
    @FXML
    private TextField product_search;
    
    private Image image;
    
    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet rs;
    
    private Alert alert;
    
    public void productAddBtn() {
//    	System.out.println(getCategoryId(product_type.getSelectionModel().getSelectedItem()));
        if (product_name.getText().isEmpty()
                || product_price.getText().isEmpty()
                || product_type.getSelectionModel().getSelectedItem() == null
                || product_quantity.getText().isEmpty()
                || productStore.product_banner == null) {
            
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
            
        } else {

            // CHECK PRODUCT ID
            String checkProdID = "SELECT product_id FROM products WHERE product_name = '"
                    + product_name.getText() + "'";
            
            connect = new Database().connectDB();
            
            try {
            	statement = connect.createStatement();
            	rs = statement.executeQuery(checkProdID);
                
                if (rs.next()) {
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText(product_name.getText() + " is already taken");
                    alert.showAndWait();
                } else {
                    String insertData = "INSERT INTO products "
                            + "(product_name, product_quantity, product_unit_price, product_banner, created_at, updated_at, category_id) "
                            + "VALUES(?,?,?,?,?,?,?)";
                    
                    prepare = connect.prepareStatement(insertData);
                    prepare.setString(1, product_name.getText());
                    prepare.setInt(2, Integer.parseInt(product_quantity.getText()));
                    prepare.setFloat(3, Float.parseFloat(product_price.getText()));
                    
                    String path = productStore.product_banner;
                    path = path.replace("\\", "\\\\");
                    
                    prepare.setString(4, path);
                    
                    java.util.Date date = new java.util.Date();
                    Date timestamp = new Date(date.getTime());
                    
                    prepare.setDate(5, timestamp);
                    prepare.setDate(6, timestamp);
                    
                    
                    prepare.setInt(7, getCategoryId(product_type.getSelectionModel().getSelectedItem()));
                    
                    
                    
                    prepare.executeUpdate();
                    
                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Added!");
                    alert.showAndWait();
                    
                    productShowData();
                    productClearBtn();
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public int getCategoryId(String name) {
    	String getCategory = "SELECT id FROM categories WHERE name='" + product_type.getSelectionModel().getSelectedItem() + "'";
    	connect = new Database().connectDB();
    	try {
    		statement = connect.createStatement();
    		rs = statement.executeQuery(getCategory);
			if (rs.next()) {
				return rs.getInt("id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return 0;
    }
    
    public void productUpdateBtn() {
//    	System.out.println(getCategoryId(product_type.getSelectionModel().getSelectedItem()));
    	if (product_name.getText().isEmpty()
                || product_price.getText().isEmpty()
                || product_type.getSelectionModel().getSelectedItem() == null
                || product_quantity.getText().isEmpty()
                || productStore.product_banner == null) {
            
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
            
        } else {
            
            String path = productStore.product_banner;
            path = path.replace("\\", "\\\\");
            java.util.Date date = new java.util.Date();
            Date timestamp = new Date(date.getTime());
            
            String updateData = "UPDATE products SET "
                    +  "product_name = '"
                    + product_name.getText() + "', category_id = '"
                    + getCategoryId(product_type.getSelectionModel().getSelectedItem()) + "', product_quantity = '"
                    + Integer.parseInt(product_quantity.getText()) + "', product_unit_price = '"
                    + Float.parseFloat(product_price.getText()) +  "', product_banner = '"
                    + path + "', updated_at = '"
                    + timestamp + "' WHERE product_id = " + productStore.id;
            
            connect = new Database().connectDB();
            
            try {
                
                alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to UPDATE PRoduct ID: " + product_name.getText() + "?");
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
                    productShowData();
                    // TO CLEAR YOUR FIELDS
                    productClearBtn();
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
    
    public void productDeleteBtn() {
    	if (productStore.id == 0) {
            
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
            
        } else {
            alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to DELETE Product ID: " + product_name.getText() + "?");
            Optional<ButtonType> option = alert.showAndWait();
            
            if (option.get().equals(ButtonType.OK)) {
                String deleteData = "DELETE FROM products WHERE product_id = " + productStore.id;
                try {
                    prepare = connect.prepareStatement(deleteData);
                    prepare.executeUpdate();
                    
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("successfully Deleted!");
                    alert.showAndWait();

                    // TO UPDATE YOUR TABLE VIEW
                    productShowData();
                    // TO CLEAR YOUR FIELDS
                    productClearBtn();
                    
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
    
    public void productImportBtn() {
        
        FileChooser openFile = new FileChooser();
        openFile.getExtensionFilters().add(new ExtensionFilter("Open Image File", "*png", "*jpg"));
        
        File file = openFile.showOpenDialog(product_form.getScene().getWindow());
        
        if (file != null) {
            
            productStore.product_banner = file.getAbsolutePath();
            image = new Image(file.toURI().toString(), 120, 127, false, true);
            
            product_image.setImage(image);
        }
    }
    
    public void productSelectData() throws NumberFormatException, SQLException {
        
        ProductDTO prodData = product_table.getSelectionModel().getSelectedItem();
        int num = product_table.getSelectionModel().getSelectedIndex();
        
        if ((num - 1) < -1) {
            return;
        }
        
        product_name.setText(prodData.getProduct_name());
        product_price.setText(String.valueOf(prodData.getProduct_unit_price()));
        product_quantity.setText(String.valueOf(prodData.getProduct_quantity()));
        product_type.setValue(getCategoryName(Integer.parseInt(prodData.getCategory())));
        
        productStore.product_banner = prodData.getProduct_banner();
        
        String path = "File:" + prodData.getProduct_banner();
        productStore.id = prodData.getId();
        
        image = new Image(path, 128, 115, false, true);
        product_image.setImage(image);
    }
    
    public void productClearBtn() {
    	product_name.setText(null);
    	product_price.setText(null);
    	product_quantity.setText(null);
    	product_type.setValue(null);
    	productStore.id = 0;
    	productStore.product_banner = "";
    	product_image.setImage(null);
    }
    
    public ObservableList<ProductDTO> productDataList() {
    	ObservableList<ProductDTO> listData = FXCollections.observableArrayList();
    	String sql = "SELECT * FROM products";
    	connect = new Database().connectDB();
    	
    	try {
			prepare = connect.prepareStatement(sql);
			rs = prepare.executeQuery();
			
			ProductDTO productData;
			
			while(rs.next()) {
				productData = new ProductDTO(rs.getInt("product_id"), rs.getString("product_name"),
							rs.getInt("product_quantity"), rs.getFloat("product_unit_price"), 
							rs.getString("product_banner"), String.valueOf(rs.getInt("category_id"))
						);
				listData.add(productData);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    	return listData;
    }
    
    private ObservableList<ProductDTO> productListData;
    
    public void productShowData() {
    	productListData = productDataList();
    	
    	col_product_id.setCellValueFactory(new PropertyValueFactory<ProductDTO, Integer>("id"));
    	col_product_name.setCellValueFactory(new PropertyValueFactory<ProductDTO, String>("product_name"));
    	col_product_quantity.setCellValueFactory(new PropertyValueFactory<ProductDTO, Integer>("product_quantity"));
    	col_product_price.setCellValueFactory(new PropertyValueFactory<ProductDTO, Float>("product_unit_price"));
    	col_product_category.setCellValueFactory(new PropertyValueFactory<ProductDTO, String>("category"));
    	
    	product_table.setItems(productListData);
    	
    	// Filter part
    	FilteredList<ProductDTO> filteredData = new FilteredList<ProductDTO>(productListData, b->true);
    	
    	product_search.textProperty().addListener((observable, oldValue, newValue) -> {
    		filteredData.setPredicate(productDto -> {
    			if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
    				return true;
    			}
    			
    			String searchKeyword = newValue.toLowerCase();
    			
    			if (productDto.getProduct_name().toLowerCase().indexOf(searchKeyword) > -1) {
    				return true;
    			} else 
    				return false;
    		});
    	});
    	
    	SortedList<ProductDTO> sortedData = new SortedList<ProductDTO>(filteredData);
    	
    	sortedData.comparatorProperty().bind(product_table.comparatorProperty());
    	product_table.setItems(sortedData);
    }
    
    public String getCategoryName(int id) throws SQLException {
    	String getCategory = "SELECT name FROM categories WHERE id='" + id + "'";
    	connect = new Database().connectDB();
    	try {
    		statement = connect.createStatement();
    		rs = statement.executeQuery(getCategory);
			if (rs.next()) {
				return rs.getString("name");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
    	return "";
    }

    
    public void initialCategories() {
    	List<String> categoryList = new ArrayList<String>();
    	String all_category = "SELECT name FROM categories";
    	connect = new Database().connectDB();
    	
    	try {
    		prepare = connect.prepareStatement(all_category);
    		rs = prepare.executeQuery();
    		while(rs.next()) {
    			categoryList.add(rs.getString("name"));
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	ObservableList listData = FXCollections.observableArrayList(categoryList);
    	product_type.setItems(listData);
    }

    
    public void initialize() {
    	initialCategories();
    	productShowData();
    }
}
