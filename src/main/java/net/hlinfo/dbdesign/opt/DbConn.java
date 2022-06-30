package net.hlinfo.dbdesign.opt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConn {
	public static String jdbcUrl = "";
	public static String jdbcUserName = "";
	public static String jdbcPasswd = "";
	public static String driverClassName = "";
	public static Connection con=null;
	
	public static Connection getConn(){
		try {
			Class.forName(DbConn.driverClassName);
			return DriverManager.getConnection(DbConn.jdbcUrl, DbConn.jdbcUserName, DbConn.jdbcPasswd);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void close(){
		if(con!=null){
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
