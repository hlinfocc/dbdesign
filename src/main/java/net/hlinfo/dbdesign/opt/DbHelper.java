package net.hlinfo.dbdesign.opt;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.hlinfo.opt.Func;
import net.hlinfo.opt.Jackson;

public class DbHelper {
	private final static String PG_FIELD_SQL="SELECT a.attname as \"fieldName\",format_type(a.atttypid,a.atttypmod) as \"fieldType\",case a.attnotnull when true then 'Y' when false then 'N' end as \"nonempty\" "
			+ ",d.column_default AS defaultval,col_description(a.attrelid,a.attnum) as \"fieldComment\" "
			+ "FROM pg_class as c,pg_attribute as a,information_schema.columns as d where c.relname=? "
			+ "and a.attrelid = c.oid and c.relname=d.table_name and d.column_name=a.attname and a.attnum>0";
	private final static String PG_PKSQL = "select pg_attribute.attname as colname,pg_constraint.contype as contype "
			+ "from pg_constraint "
			+ "inner join pg_class on pg_constraint.conrelid = pg_class.oid "
			+ "inner join pg_attribute on pg_attribute.attrelid = pg_class.oid and  pg_attribute.attnum = pg_constraint.conkey[1] "
			+ "inner join pg_type on pg_type.oid = pg_attribute.atttypid "
			+ "where pg_class.relname=? ";
	private Connection conn=null;
	
	public DbHelper(){
		conn=DbConn.getConn();
	}
	public void destroy(){
		if(conn!=null)
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public boolean isConn() {
		return conn==null?false:true;
	}
	
	public List<TableInfo> findTableInfo() throws SQLException {
		if(TbCache.tblist.size()>0) {
			return TbCache.tblist;
		}
		DatabaseMetaData db = conn.getMetaData();
		ResultSet rs;
		if(TbCache.databaseType==0) {
			rs = db.getTables(null, null, null, new String[] { "TABLE" });
		}else {
			rs = db.getTables(TbCache.databaseName, TbCache.databaseName, null, new String[] { "TABLE" });
		}
		List<TableInfo> tblist = new ArrayList<TableInfo>();
		while (rs.next()) {
	    	String tableName = rs.getString(3);//表名
	    	String tableComment = rs.getString(5);//表注释
	    	TableInfo tb = new TableInfo();
	    	tb.setTableName(tableName);
	    	tb.setTableComment(Func.isBlank(tableComment)?"":tableComment);
	    	tblist.add(tb);
		}
		TbCache.tblist = tblist;
		return TbCache.tblist;
	}
	
	
	public List<TableFields> findTableFields(String tableName) throws SQLException{
		if(TbCache.tblist.isEmpty()) {
			this.findTableInfo();
		}
		List<TableFields> fieldList = new ArrayList<TableFields>();
		if(TbCache.databaseType==0) {
			PreparedStatement ps = conn.prepareStatement(PG_FIELD_SQL);
			ps.setString(1, tableName);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				TableFields tf = new TableFields();
				tf.setFieldName(Func.isNotBlank(rs.getString("fieldName"))?rs.getString("fieldName"):"");
				tf.setFieldType(Func.isNotBlank(rs.getString("fieldType"))?rs.getString("fieldType"):"");
				tf.setNonempty(Func.isNotBlank(rs.getString("nonempty"))?rs.getString("nonempty"):"");
				tf.setDefaultval(Func.isNotBlank(rs.getString("defaultval"))?rs.getString("defaultval"):"");
				tf.setFieldComment(Func.isNotBlank(rs.getString("fieldComment"))?rs.getString("fieldComment"):"");
				tf.setFieldPK("N");
				tf.setFieldUK("N");
				tf.setFieldFK("N");
				fieldList.add(tf);
			}
			//获取主键、唯一键等信息
			PreparedStatement psk = conn.prepareStatement(PG_PKSQL);
			psk.setString(1, tableName);
			ResultSet rsk = psk.executeQuery();
			while(rsk.next()){
				//字段名
				String attname = rsk.getString("colname");
				//类型：p主键，u唯一
				String contype = rsk.getString("contype");
				for(TableFields tf:fieldList) {
					if(Func.notequals(attname, tf.getFieldName())) {
						continue;
					}
					if(Func.equals(contype, "p")) {
						tf.setFieldPK("Y");
					}
					if(Func.equals(contype, "u")) {
						tf.setFieldUK("Y");
					}
				}
			}
		}else {
			//处理MySQL
			PreparedStatement pStemt = conn.prepareStatement("SELECT * FROM "+tableName);
//	       ResultSetMetaData rsmd = pStemt.getMetaData();
	       ResultSet rs = pStemt.executeQuery("show full columns from "+tableName);
	       while (rs.next()) {
	    	   TableFields tf = new TableFields();
				tf.setFieldName(Func.isNotBlank(rs.getString("Field"))?rs.getString("Field"):"");
				tf.setFieldType(Func.isNotBlank(rs.getString("Type"))?rs.getString("Type"):"");
				String isnull = rs.getString("Null");
				tf.setNonempty(Func.equals(isnull,"Yes")?"N":"Y");
				tf.setDefaultval(Func.isNotBlank(rs.getString("Default"))?rs.getString("Default"):"");
				tf.setFieldComment(Func.isNotBlank(rs.getString("Comment"))?rs.getString("Comment"):"");
				tf.setFieldPK("N");
				tf.setFieldUK("N");
				tf.setFieldFK("N");
				String key = rs.getString("Key");
				if(Func.equals(key, "PRI")) {
					tf.setFieldPK("Y");
				}
				if(Func.equals(key, "UNI")) {
					tf.setFieldUK("Y");
				}
				fieldList.add(tf);
	    	   
	       }
		}
		return fieldList;
	}
	/**
	 * 获取数据库列表
	 * @return
	 * @throws SQLException
	 */
	public ObservableList<String> queryDBList() throws SQLException {
		ResultSet rs;
		if(TbCache.databaseType==0) {
			String pgGetDbSql = "SELECT datname FROM pg_database where datistemplate='f' ORDER BY datname;";
			CallableStatement cStmt = conn.prepareCall(pgGetDbSql);
			rs = cStmt.executeQuery();
		}else {
			//处理MySQL
			CallableStatement cStmt = conn.prepareCall("show databases");
			rs = cStmt.executeQuery();
		}
		ObservableList<String> dblist = FXCollections.observableArrayList();
		while (rs.next()) {			
	    	String db = rs.getString(1);//表名
	    	dblist.add(db);
		}
		return dblist;
	}
	
}
