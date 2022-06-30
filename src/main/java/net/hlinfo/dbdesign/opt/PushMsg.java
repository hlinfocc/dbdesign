package net.hlinfo.dbdesign.opt;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.hlinfo.opt.Func;

/**
 * 向多行文本框追加数据
 * @author hlinfo.net
 *
 */
public class PushMsg {
private StringProperty labShow = new SimpleStringProperty("");
	
	private PushMsg() {}
	private static PushMsg pushMsg = null;
	public static PushMsg get() {
		if(pushMsg == null) {
			pushMsg = new PushMsg();
		}
		return pushMsg;
	}
	
	public void setLabShow(String str) {
		Platform.runLater(()->{
			labShow.setValue(str);
		});
	}
	
	public void appendLabShow(String str) {
		Platform.runLater(()->{
			String str1 = Func.Times.now()	+ "\n" + str;
			String s = labShow.getValue();
			//由于多行文本框的滚动条不能自动往下拉，故这里采用往前追加
			labShow.setValue(str1 + "\n\n" + s);
		});
	}
	
	public StringProperty labShow() {
		return labShow;
	}
	
	public void clean() {
		Platform.runLater(()->{
			labShow.setValue("");
		});
	}

}
