package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
	public static Connection connectDB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/coffee_shop", "root", "Apollo2003.");
			System.out.println("Connect successfully");
			return connect;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
}
