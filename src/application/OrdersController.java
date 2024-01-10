package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import database.Database;
import dto.BillDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class OrdersController {
	@FXML
    private TableView<BillDTO> table_bill;

    @FXML
    private TableColumn<BillDTO, Date> col_bill_date;

    @FXML
    private TableColumn<BillDTO, Integer> col_bill_order_id;

    @FXML
    private TableColumn<BillDTO, Float> col_bill_total;

    @FXML
    private TableColumn<BillDTO, Integer> col_bill_id;
    
    private Alert alert;
    
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet rs;
    
    private ObservableList<BillDTO> billList;
    
    public ObservableList<BillDTO> billDataList() {
    	ObservableList<BillDTO> listData = FXCollections.observableArrayList();
    	
    	String sql = "SELECT * FROM bill";
    	
    	connect = new Database().connectDB();
    	
    	try {
			prepare = connect.prepareStatement(sql);
			rs = prepare.executeQuery();
			
			BillDTO billDto;
			while(rs.next()) {
				billDto = new BillDTO(rs.getInt("bill_id"), 
							rs.getInt("order_id"), 
							rs.getFloat("total_bill"), 
							rs.getDate("export_date"), 
							rs.getBoolean("status")
						);
				listData.add(billDto);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    	return listData;
    }
    
    public void showBillData() {
    	billList = billDataList();
    	
    	col_bill_id.setCellValueFactory(new PropertyValueFactory<>("id"));
    	col_bill_order_id.setCellValueFactory(new PropertyValueFactory<>("order_id"));
    	col_bill_total.setCellValueFactory(new PropertyValueFactory<>("total_bill"));
    	col_bill_date.setCellValueFactory(new PropertyValueFactory<>("export_date"));
    	
    	table_bill.setItems(billList);
    }
    
    public void initialize() {
    	showBillData();
    }
}
