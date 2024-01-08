package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

import database.Database;
import dto.ProductDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import store.authStore;

public class MenuController {
	
	@FXML
    private GridPane menu_gridPane;
	
    @FXML
    private TableView<ProductDTO> table_order;

    @FXML
    private TableColumn<ProductDTO, Integer> col_order_product_quantity;

    @FXML
    private TableColumn<ProductDTO, Float> col_order_product_price;

    @FXML
    private TableColumn<ProductDTO, String> col_order_product_name;
    
    @FXML
    private Label txt_total;

    @FXML
    private Label txt_subtotal;
    
    @FXML
    private Button btn_payment;
    
    @FXML
    private Label txt_discount;
    
    @FXML
    private Label txt_tax;
    
	private Connection connect;
	private PreparedStatement prepare;
	private ResultSet rs;
	
	private Alert alert;
	
	private float subtotal;
	private int order_id;
	
	public void menuGetSubtotal() {
		getCustomerId();
		String total = "SELECT o.order_id, SUM(p.product_unit_price * op.quantity) AS total_amount " +
		                "FROM orders o " +
		                "JOIN order_product op ON o.order_id = op.order_id " +
		                "JOIN products p ON op.product_id = p.product_id " +
		                "WHERE o.customer_id = " + authStore.customer_id +
		                " GROUP BY o.order_id";
		connect = new Database().connectDB();
		
		try {
			prepare = connect.prepareStatement(total);
			rs = prepare.executeQuery();
			
			if (rs.next()) {
				subtotal = rs.getFloat("total_amount");
				order_id = rs.getInt("order_id");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void menuDisplayTotal() {
		menuGetSubtotal();
		txt_subtotal.setText("$" + subtotal);
		txt_discount.setText("$0");
		txt_tax.setText("$" + (subtotal * 0.1));
		txt_total.setText("$" + (subtotal + subtotal * 0.1));
	}
	
	public void menuPaymentBtn() {
		if (subtotal == 0) {
			alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please choose your order first!");
            alert.showAndWait();
		} else {
			menuGetSubtotal();
			String insertBill = "INSERT INTO bill (order_id, total_bill, export_date, status) "
							+ "VALUES(?, ?, ?, ?)";
			connect = new Database().connectDB();
			
			try {
				alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Message");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure?");
                Optional<ButtonType> option = alert.showAndWait();
                
                if (option.get().equals(ButtonType.OK)) {
                    getCustomerId();
                    menuGetSubtotal();
                    System.out.println(order_id);
                    prepare = connect.prepareStatement(insertBill);
                    prepare.setInt(1, order_id);
                    prepare.setFloat(2, (float)((subtotal + subtotal * 0.1)));
                    
                    java.util.Date date = new java.util.Date();
                    Date timestamp = new Date(date.getTime());
                    
                    prepare.setDate(3, timestamp);
                    prepare.setBoolean(4, true);
                    
                    prepare.executeUpdate();
                    
                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Infomation Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successful.");
                    alert.showAndWait();
                    
                    clearData();
                    menuShowOrderData();
                    
                } else {
                    alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Infomation Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Cancelled.");
                    alert.showAndWait();
                }
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	public void clearData() {
		subtotal = 0;
		order_id = 0;
		txt_subtotal.setText("$0");
		txt_tax.setText("$0");
		txt_total.setText("$0");
	}
	
	private ObservableList<ProductDTO> cardListData = FXCollections.observableArrayList();
	
	public ObservableList<ProductDTO> menuGetData() {
		
		String sql = "SELECT * FROM products";
		
		ObservableList<ProductDTO> listData = FXCollections.observableArrayList();
		
		connect = new Database().connectDB();
		
		try {
			prepare = connect.prepareStatement(sql);
			rs = prepare.executeQuery();
			
			ProductDTO prod;
			while(rs.next()) {
				prod = new ProductDTO(rs.getInt("product_id"), 
						rs.getString("product_name"), 
						rs.getFloat("product_unit_price"), 
						rs.getString("product_banner")
						);
				listData.add(prod);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return listData;
	}
	
	public void menuDisplayCard() {
		cardListData.clear();
		cardListData.addAll(menuGetData());
		
		int row = 0;
		int column = 0;
		
		menu_gridPane.getChildren().clear();
		menu_gridPane.getRowConstraints().clear();
		menu_gridPane.getColumnConstraints().clear();
		
		for (int q = 0; q < cardListData.size(); q++) {
			
			try {
				FXMLLoader load = new FXMLLoader();
				load.setLocation(getClass().getResource("/ui/ProductCard.fxml"));
				AnchorPane pane = load.load();
				CardController cardController = load.getController();
				cardController.setData(cardListData.get(q));
				System.out.println(cardListData.get(q));
				
				if (column == 3) {
					column = 0;
					row += 1;
				}
				
				menu_gridPane.add(pane, column++, row);
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		}
	}
	
	public ObservableList<ProductDTO> menuGetOrder() {
		getCustomerId();
		ObservableList<ProductDTO> listData = FXCollections.observableArrayList();
		
		String sql = "SELECT o.order_id, op.quantity, p.product_name, p.product_unit_price " +
		                "FROM orders o " +
		                "JOIN order_product op ON o.order_id = op.order_id " +
		                "JOIN products p ON op.product_id = p.product_id " +
		                "WHERE o.customer_id = " + authStore.customer_id +  
		                " ORDER BY o.order_id";
		connect = new Database().connectDB();
		
		try {
			prepare = connect.prepareStatement(sql);
			rs = prepare.executeQuery();
			
			ProductDTO dto;
			
			while(rs.next()) {
				dto = new ProductDTO(rs.getString("product_name"), 
								rs.getInt("quantity"), 
								rs.getFloat("product_unit_price"));
				listData.add(dto);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return listData;
	}
	
	private ObservableList<ProductDTO> menuOrderListData;
	
	public void menuShowOrderData () {
		menuOrderListData = menuGetOrder();
		
		col_order_product_name.setCellValueFactory(new PropertyValueFactory<>("product_name"));
		col_order_product_quantity.setCellValueFactory(new PropertyValueFactory<>("product_quantity"));
		col_order_product_price.setCellValueFactory(new PropertyValueFactory<>("product_unit_price"));
		
		table_order.setItems(menuOrderListData);
	}
	
	private int customer_id;
	
	public void getCustomerId() {
		String sql = "SELECT MAX(customer_id) AS max_customer_id FROM orders";
		connect = new Database().connectDB();
		
		try {
			prepare = connect.prepareStatement(sql);
			rs = prepare.executeQuery();
			
			if (rs.next()) {
				customer_id = rs.getInt("max_customer_id");
			}
			
			String getOrderId = "SELECT order_id FROM orders WHERE customer_id = '" + customer_id + "'";
			prepare = connect.prepareStatement(getOrderId);
            rs = prepare.executeQuery();
			int orderId = 0;
            if (rs.next())
            	orderId = rs.getInt("order_id");
			
			
			String checkOrderId = "SELECT MAX(order_id) as max_order_id FROM bill";
            prepare = connect.prepareStatement(checkOrderId);
            rs = prepare.executeQuery();
            int checkID = 0;
            if (rs.next()) {
                checkID = rs.getInt("max_order_id");
            }
            
            if (customer_id == 0) {
            	customer_id += 1;
            } else if (orderId == checkID) {
            	customer_id += 1;
            }
            
            authStore.customer_id = customer_id;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	public void initialize() {
		menuDisplayCard();
		menuGetOrder();
		menuShowOrderData();
		menuDisplayTotal();
	}
}
