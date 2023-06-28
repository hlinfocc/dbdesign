package net.hlinfo.dbdesign;

import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.text.Font;

public class MainApplication extends Application {
	static {
		Resource obj = ResourceUtil.getResourceObj("AlibabaPuHuiTi-3-55-Regular.ttf");
        Font.loadFont(obj.getStream(), 12);
    }
	@Override
	public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
        primaryStage.setTitle("数据库表设计导出工具　-　hlinfo.net");
        primaryStage.setScene(new Scene(root, 1024, 700));
        primaryStage.show();
        primaryStage.setResizable(false);
	}
	
	public static void main(String[] args) {
        launch(args);
    }
	
}
