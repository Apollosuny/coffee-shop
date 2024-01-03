package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
	public static Connection connectDB() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection connect = DriverManager.getConnection("jdbc:sqlserver://localhost;DatabaseName=Coffeeshop; username=sa;password=12345678; encrypt=false;");
			System.out.println("Connect successfully");
			return connect;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
}
