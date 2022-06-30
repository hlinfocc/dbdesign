package net.hlinfo.dbdesign.opt;

import java.io.Serializable;

import net.hlinfo.opt.Jackson;
/**
 * 表字段信息
 * @author hlinfo.net
 *
 */
public class TableFields implements Serializable  {
	private static final long serialVersionUID = 1L;
	/**
	 * 字段名
	 */
	private String fieldName;
	/**
	 * 类型(长度)
	 */
	private String fieldType;
	/**
	 * 是否主键
	 */
	private String fieldPK;
	/**
	 * 是否外键
	 */
	private String fieldFK;
	/**
	 * 是否唯一
	 */
	private String fieldUK;
	/**
	 * 是否为空
	 */
	private String nonempty;
	/**
	 * 默认值
	 */
	private String defaultval;
	/**
	 * 字段注释
	 */
	private String fieldComment;
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getFieldPK() {
		return fieldPK;
	}
	public void setFieldPK(String fieldPK) {
		this.fieldPK = fieldPK;
	}
	public String getFieldFK() {
		return fieldFK;
	}
	public void setFieldFK(String fieldFK) {
		this.fieldFK = fieldFK;
	}
	public String getFieldUK() {
		return fieldUK;
	}
	public void setFieldUK(String fieldUK) {
		this.fieldUK = fieldUK;
	}
	public String getNonempty() {
		return nonempty;
	}
	public void setNonempty(String nonempty) {
		this.nonempty = nonempty;
	}
	public String getDefaultval() {
		return defaultval;
	}
	public void setDefaultval(String defaultval) {
		this.defaultval = defaultval;
	}
	public String getFieldComment() {
		return fieldComment;
	}
	public void setFieldComment(String fieldComment) {
		this.fieldComment = fieldComment;
	}
	@Override
	public String toString() {
		return Jackson.entityToString(this);
	}
	
}
