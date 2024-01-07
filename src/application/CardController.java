package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import database.Database;
import dto.ProductDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import store.authStore;

public class CardController {
	@FXML
    private Label card_name;

    @FXML
    private TextField card_quantity;

    @FXML
    private Button card_btn;

    @FXML
    private ImageView card_image;

    @FXML
    private Label card_price;

    @FXML
    private AnchorPane card_form;
    
    private ProductDTO prodData;
    private Image image;
    
    private int product_id;
    private String category;
    private String product_banner;
    
    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet rs;
    
    private Alert alert;
    
    private int quantity;
    private int order_id;
    private float price;
    
    public void setData(ProductDTO prodData) {
    	this.prodData = prodData;
    	
    	product_banner = prodData.getProduct_banner();
    	category = prodData.getCategory();
    	product_id = prodData.getId();
    	card_name.setText(prodData.getProduct_name());
    	card_price.setText("$" + String.valueOf(prodData.getProduct_unit_price()));
    	String path = "File:" + prodData.getProduct_banner();
    	image = new Image(path, 100, 100, false, true);
    	card_image.setImage(image);
    	price = prodData.getProduct_unit_price();
    	
    }
    
    private float totalP;
    
    public void addBtn() {
    	
    	MenuController menuController = new MenuController();
    	menuController.getCustomerId();
    	
    	quantity = Integer.parseInt(card_quantity.getText());
    	
    	String check = "";
    	
    	connect = new Database().connectDB();
    	
    	try {
			int checkStock = 0;
			String sqlCheckStock = "SELECT product_quantity FROM products WHERE product_id = '" + product_id + "'";
			
			prepare = connect.prepareStatement(sqlCheckStock);
			rs = prepare.executeQuery();
			
			if (rs.next())
				checkStock = rs.getInt("product_quantity");
			
			if (checkStock == 0) {
				alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Invalid. This product is Out of stock");
                alert.showAndWait();
			}
			
			if (quantity == 0) {
				alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Something Wrong :3");
                alert.showAndWait();
			} else {
				if (checkStock < quantity) {
					alert = new Alert(AlertType.ERROR);
	                alert.setTitle("Error Message");
	                alert.setHeaderText(null);
	                alert.setContentText("Invalid. This product is Out of stock");
	                alert.showAndWait();
				} else {
					// Check exist order in bill
					boolean checkUnpaid = checkUnpaidOrder();
					
					if (!checkUnpaid) {
						String makeAnOrder = "INSERT INTO orders " 
								+ "(customer_id, created_at) "
								+ "VALUES(?, ?)";
						prepare = connect.prepareStatement(makeAnOrder);
						prepare.setInt(1, authStore.customer_id);
						java.util.Date date = new java.util.Date();
			            Date timestamp = new Date(date.getTime());
			            prepare.setDate(2, timestamp);
			            
			            prepare.executeUpdate();
					}
					
					order_id = getOrderId();
					boolean checkExistProduct = checkExistProduct(product_id, order_id);
					if (checkExistProduct) {
						int current_quantity = getCurrentQuantity(order_id, product_id);
						String updateQuantityInMappingTable = "UPDATE order_product SET quantity = " 
								+ (current_quantity + quantity) 
								+ " WHERE order_id = " + order_id + " AND product_id = " + product_id;
						prepare = connect.prepareStatement(updateQuantityInMappingTable);
						prepare.executeUpdate();
					} else {
						String insertData = "INSERT INTO order_product"
								+ "(order_id, product_id, quantity)"
								+ " VALUES (?, ?, ?)";
						prepare = connect.prepareStatement(insertData);
						prepare.setInt(1, order_id);
						prepare.setInt(2, product_id);
						prepare.setInt(3, quantity);
						
						prepare.executeUpdate();
					}
					
					
					int calculateQuantity = checkStock - quantity;
					
					
					String updateQuantity = "UPDATE products SET product_quantity = " 
							+ calculateQuantity + ", status = " + (calculateQuantity > 0 ? 1 : 0)
							+ " WHERE product_id = " + product_id;
					prepare = connect.prepareStatement(updateQuantity);
					prepare.executeUpdate();
					alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Added!");
                    alert.showAndWait();
                    
                    menuController.menuGetSubtotal();
					
				}
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }
    
    public boolean checkUnpaidOrder() {
    	String checkExistOrder = "SELECT MAX(order_id) AS max_order_id FROM orders";
    	String checkOrderIdBill = "SELECT MAX(order_id) AS max_order_id FROM bill";
    	connect = new Database().connectDB();
    	try {
			prepare = connect.prepareStatement(checkExistOrder);
			rs = prepare.executeQuery();
			int order_id = 0;
			if (rs.next())
				order_id = rs.getInt("max_order_id");
			
			prepare = connect.prepareStatement(checkOrderIdBill);
			rs = prepare.executeQuery();
			int order_id_bill = 0;
			if (rs.next()) 
				order_id_bill = rs.getInt("max_order_id");
			
			if (order_id == order_id_bill)
				return false;
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    	return true;
    }
    
    public int getOrderId() {
    	String checkExistOrder = "SELECT MAX(order_id) AS max_order_id FROM orders";
    	connect = new Database().connectDB();
    	try {
			prepare = connect.prepareStatement(checkExistOrder);
			rs = prepare.executeQuery();
			int order_id = 0;
			if (rs.next())
				order_id = rs.getInt("max_order_id");
			
			return order_id;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    	return 0;
    }
    
    public boolean checkExistProduct(int productId, int order_id) {
    	String check = "SELECT product_id FROM order_product WHERE order_id = " + order_id;
    	connect = new Database().connectDB();
    	try {
			prepare = connect.prepareStatement(check);
			rs = prepare.executeQuery();
			int product_id_check = 0;
			if (rs.next()) 
				product_id_check = rs.getInt("product_id");
			
			if (product_id_check == productId)
				return true;
				
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    	return false;
    }
    
    public int getCurrentQuantity(int order_id, int productId) {
    	String check = "SELECT quantity FROM order_product WHERE order_id = " + order_id + " AND product_id = " + productId;
    	connect = new Database().connectDB();
    	int quantity = 0;
    	try {
			prepare = connect.prepareStatement(check);
			rs = prepare.executeQuery();
			if (rs.next()) 
				quantity = rs.getInt("quantity");

			return quantity;
				
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    	return quantity;
    }
    
    public int getCategoryId(String name) {
    	String getCategory = "SELECT id FROM categories WHERE name='" + name + "'";
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
    
    public void initialize() {}
}
