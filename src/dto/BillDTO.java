package dto;

import java.sql.Date;

public class BillDTO {
	private int id;
	private int order_id;
	private float total_bill;
	private Date export_date;
	private boolean status;
	
	public BillDTO(int id, int order_id, float total_bill, Date export_date, boolean status) {
		super();
		this.id = id;
		this.order_id = order_id;
		this.total_bill = total_bill;
		this.export_date = export_date;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public int getOrder_id() {
		return order_id;
	}

	public float getTotal_bill() {
		return total_bill;
	}

	public Date getExport_date() {
		return export_date;
	}

	public boolean isStatus() {
		return status;
	}
	
	
}
