package net.hlinfo.dbdesign.opt;

import java.io.Serializable;

import net.hlinfo.opt.Jackson;

public class TableInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String tableName;
	
	private String tableComment;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableComment() {
		return tableComment;
	}

	public void setTableComment(String tableComment) {
		this.tableComment = tableComment;
	}
	@Override
	public String toString() {
		return Jackson.entityToString(this);
	}
}
