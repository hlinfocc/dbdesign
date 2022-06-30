package net.hlinfo.dbdesign;

import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.*;

public class MainApplication extends Application {

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
