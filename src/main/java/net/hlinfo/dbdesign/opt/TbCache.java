package net.hlinfo.dbdesign.opt;

import java.util.ArrayList;
import java.util.List;

public class TbCache {
	/**
	 * 表信息缓存
	 */
	public static List<TableInfo> tblist = new ArrayList<TableInfo>();
	/**
	 * 数据库类型，0：pgsql，1：mysql
	 */
	public static int databaseType = 0;
	/**
	 * 数据库名
	 */
	public static String databaseName = "";
}
