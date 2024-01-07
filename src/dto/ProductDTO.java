package dto;

public class ProductDTO {
	private int id;
	private String product_name;
	private int product_quantity;
	private float product_unit_price;
	private String product_banner;
	private String category;
	
	public ProductDTO(int id, String product_name, int product_quantity, float product_unit_price,
			String product_banner, String category) {
		super();
		this.id = id;
		this.product_name = product_name;
		this.product_quantity = product_quantity;
		this.product_unit_price = product_unit_price;
		this.product_banner = product_banner;
		this.category = category;
	}
	
	public ProductDTO(int id, String product_name, float product_unit_price, String product_banner) {
		this.id = id;
		this.product_name = product_name;
		this.product_unit_price = product_unit_price;
		this.product_banner = product_banner;
	}
	
	public ProductDTO(String product_name, int product_quantity, float product_unit_price) {
		this.product_name = product_name;
		this.product_quantity = product_quantity;
		this.product_unit_price = product_unit_price;
	}

	public int getId() {
		return id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public int getProduct_quantity() {
		return product_quantity;
	}

	public float getProduct_unit_price() {
		return product_unit_price;
	}

	public String getProduct_banner() {
		return product_banner;
	}
	
	public String getCategory() {
		return category;
	}
	
}
