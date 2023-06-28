package net.hlinfo.dbdesign.controlle;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.stage.DirectoryChooser;
import net.hlinfo.dbdesign.opt.DbConn;
import net.hlinfo.dbdesign.opt.DbHelper;
import net.hlinfo.dbdesign.opt.ExportService;
import net.hlinfo.dbdesign.opt.PushMsg;
import net.hlinfo.dbdesign.opt.TableInfo;
import net.hlinfo.dbdesign.opt.TbCache;
import net.hlinfo.opt.Func;

public class MainController implements Initializable {
	private static final Log log = LogFactory.get();
	@FXML
	private ToggleGroup dbtype;
	@FXML
	private RadioButton dbpgsql;
	@FXML
	private RadioButton dbmysql;
	@FXML
	private TextField serverIp;
	@FXML
	private TextField serverPort;
    @FXML
    private TextField dbUserName;
    @FXML
    private CheckBox showPwd;
    @FXML
    private TextField dbPasswordShow;
    @FXML
    private PasswordField dbPassword;
    @FXML
    private Button chooserSaveDir;
    @FXML
    private TextField chooserSavePath;
    @FXML
    private TextField dbName;
    @FXML
    private ChoiceBox<String> dbDriver;
    @FXML
    private Button connServerTest;
    @FXML
    private Button nowCreateDesign;
    @FXML
    private TextArea progressMsg;
    @FXML
    private Button fetchDatabases;
    @FXML
    private ChoiceBox<String> databasesList;
    
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		log.debug("初始化javaFx设置.....");
		serverPort.setText("5432");
		dbUserName.setText("postgres");
		serverIp.setText("127.0.0.1");
		ObservableList<String> items = FXCollections.observableArrayList("org.postgresql.Driver");
		dbDriver.setItems(items);
		dbDriver.setValue("org.postgresql.Driver");
		this.serverPort.setTextFormatter(new TextFormatter<String>(new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change change) {
                String value = change.getText();
                if (value.matches("[0-9]*")) {
                		return change;
                   }
                return null;
            }
        }));
		progressMsg.textProperty().bindBidirectional(PushMsg.get().labShow());
		progressMsg.setText("");
		TbCache.databaseType = 0;
		TbCache.tblist.clear();
		//默认保存桌面
		String homePath = System.getProperty("user.home");
		String defaultPath = homePath + File.separatorChar +"Desktop";
		String defaultPathCn = homePath + File.separatorChar +"桌面";
		//判断Desktop是否存在
		if(!FileUtil.exist(defaultPath)) {
			if(!FileUtil.exist(defaultPathCn)) {				
				defaultPath = homePath;
			}else {
				defaultPath = defaultPathCn;
			}
		}
		chooserSavePath.setText(defaultPath);
	}
	
	@FXML
    void onShowPassword(ActionEvent event) {
		if(showPwd.isSelected()) {
			dbPasswordShow.setPrefWidth(265.0);
			dbPassword.setPrefWidth(0);
			dbPasswordShow.setText(dbPassword.getText());
			dbPasswordShow.setVisible(true);
			dbPassword.setVisible(false);
		}else {
			dbPasswordShow.setPrefWidth(0);
			dbPassword.setPrefWidth(265.0);
			dbPassword.setText(dbPasswordShow.getText());
			dbPasswordShow.setVisible(false);
			dbPassword.setVisible(true);
		}
    }
	
	@FXML
    void onPwdChange(ActionEvent event) {
		dbPasswordShow.setText(dbPassword.getText());
    }
	
	@FXML
    void onPwdShowChange(ActionEvent event) {
		dbPassword.setText(dbPasswordShow.getText());
    }
	
	@FXML
	void onChangeDbType(ActionEvent event) {
		TbCache.tblist.clear();
		if(dbpgsql.isSelected()) {
			serverPort.setText("5432");
			dbUserName.setText("postgres");
			ObservableList<String> items = FXCollections.observableArrayList("org.postgresql.Driver");
			dbDriver.setItems(items);
			dbDriver.setValue("org.postgresql.Driver");
			TbCache.databaseType = 0;
		}
		if(dbmysql.isSelected()) {
			serverPort.setText("3306");
			dbUserName.setText("root");
			ObservableList<String> items = FXCollections.observableArrayList("com.mysql.cj.jdbc.Driver","com.mysql.jdbc.Driver");
			dbDriver.setItems(items);
			dbDriver.setValue("com.mysql.cj.jdbc.Driver");
			TbCache.databaseType = 1;
		}
	}
	
	@FXML
    void onDatabaseChange(ActionEvent event) {
		TbCache.tblist.clear();
    }
	
	@FXML
	void onChooserSaveDir(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("选择保存的位置");
		//设置默认打开的目录
		String homePath = System.getProperty("user.home");
		String desktopPath = homePath + File.separatorChar +"Desktop";
		String defaultPathCn = homePath + File.separatorChar +"桌面";
		//判断Desktop是否存在
		File homePathFile = new File(desktopPath);
		if(!homePathFile.exists()) {
			if(!FileUtil.exist(defaultPathCn)) {				
				homePathFile = new File(homePath);
			}else {
				homePathFile = new File(defaultPathCn);
			}
		}
		directoryChooser.setInitialDirectory(new File(homePath));
		File selectedDirectory =  directoryChooser.showDialog(null);
		if(selectedDirectory!=null) {
			if(!selectedDirectory.canWrite()) {
				PushMsg.get().appendLabShow("选择的保存目录没有可写的权限...");
				this.alert(Alert.AlertType.ERROR, "选择的目录没有可写的权限，请重新选择");
			}else {
				chooserSavePath.setText(selectedDirectory.getAbsolutePath());
			}
		}
	}
	
	/**
	 * 获取数据库
	 * @param even
	 */
	@FXML
	void onFetchDatabases(ActionEvent even) {
		PushMsg.get().clean();
		DbHelper dbHelper = null;
		try {
			if(this.validata(true)) {	
				PushMsg.get().appendLabShow("加载数据库配置...");
				DbConn.jdbcUrl = this.buildJdbcBaseUrl();
				DbConn.driverClassName = this.dbDriver.getValue();
				DbConn.jdbcUserName = this.dbUserName.getText();
				DbConn.jdbcPasswd = this.dbPassword.getText();
				dbHelper = new DbHelper();
				if(dbHelper.isConn()) {
					PushMsg.get().appendLabShow("连接数据库成功...");
				}
				ObservableList<String> dblist = dbHelper.queryDBList();
				if(dblist!=null && dblist.size()>0) {
					PushMsg.get().appendLabShow("获取数据库列表成功...");
					this.databasesList.setItems(dblist);
				}
			}
		}catch (Exception e) {
			log.error("出错了:{}",e);
			PushMsg.get().appendLabShow("出错了："+e.getMessage());
		} finally {
			if(dbHelper!=null) {
				dbHelper.destroy();
			}
		}
	}
	
	@FXML
	void onDatabasesChoose(ActionEvent even) {
		String nowdb = this.databasesList.getValue();
		this.dbName.setText(nowdb);
	}
	
	/**
	 * 连接测试
	 * @param even
	 */
	@FXML
	void onConnServerTest(ActionEvent even) {
		PushMsg.get().clean();
		DbHelper dbHelper = null;
		try {
			if(this.validata(false)) {			
				PushMsg.get().appendLabShow("加载数据库配置...");
				DbConn.jdbcUrl = this.buildJdbcUrl();
				DbConn.driverClassName = this.dbDriver.getValue();
				DbConn.jdbcUserName = this.dbUserName.getText();
				DbConn.jdbcPasswd = this.dbPassword.getText();
				dbHelper = new DbHelper();
				if(dbHelper.isConn()) {
					PushMsg.get().appendLabShow("连接数据库成功...");
				}
				TbCache.tblist.clear();
		    	dbHelper.findTableInfo();
				PushMsg.get().appendLabShow("获取表信息...");
				StringBuffer sb = new StringBuffer();
				sb.append("+--------------------------------+--------------------------------+\n"
						+ "|表名                          | 说明                          |\n"
						+ "+--------------------------------+--------------------------------+\n");
				for (TableInfo tb:TbCache.tblist) {
			    	String tableName = tb.getTableName();//表名
			    	String tableComment = tb.getTableComment();//表注释
			    	if(tableName.length()<30) {
			    		int tableNameLength = 30-tableName.length();
			    		for(int i=0;i<tableNameLength;i++) {
			    			tableName +=" ";
			    		}
			    	}
			    	if(tableComment.length()<30) {
			    		int tableCommentLength = 30-(tableComment.length()*2);
			    		for(int i=0;i<tableCommentLength;i++) {
			    			tableComment +=" ";
			    		}
			    	}
			    	sb.append("|"+tableName+"|"+tableComment+"|\n");
			      }
			    sb.append("+--------------------------------+--------------------------------+");
			    PushMsg.get().appendLabShow(sb.toString());
			}
		} catch (Exception e) {
			log.error("出错了:{}",e);
			PushMsg.get().appendLabShow("出错了："+e.getMessage());
		} finally {
			dbHelper.destroy();
		}
	}
	/**
	 * 立即创建
	 * @param even
	 */
	@FXML
	void onNowCreateDesign(ActionEvent even) {
		DbHelper dbHelper = null;
		try {
			if(this.validata(false)) {			
				PushMsg.get().appendLabShow("创建数据库设计文档...");
				DbConn.jdbcUrl = this.buildJdbcUrl();
				DbConn.driverClassName = this.dbDriver.getValue();
				DbConn.jdbcUserName = this.dbUserName.getText();
				DbConn.jdbcPasswd = this.dbPassword.getText();
				dbHelper = new DbHelper();
				if(TbCache.tblist.isEmpty()) {
		    		dbHelper.findTableInfo();
				}
				String html = this.loadHtmlTpl();
				String saveFilePath = chooserSavePath.getText();
				File fileDir = new File(saveFilePath);
				if(!fileDir.canWrite()) {
					PushMsg.get().appendLabShow("任务终止：选择的保存目录没有可写的权限...");
					this.alert(Alert.AlertType.ERROR, "选择的目录没有可写的权限");
				}else {
					ExportService es = new ExportService();
					html = html.replace("${tableListInfo}", es.buildTableInfo(TbCache.tblist));
					//构建表结构信息
					String tableFieldDetail = es.buildTableDetail(dbHelper);
					html = html.replace("${tableFieldDetailInfo}", tableFieldDetail);
					String filePath = es.createDocx(html, saveFilePath,TbCache.databaseName);
					PushMsg.get().appendLabShow("数据库设计文档创建成功...");
					PushMsg.get().appendLabShow("文件保存于："+filePath);
				}
			}
		} catch (Exception e) {
			log.error("操作失败，出错了:{}",e);
			PushMsg.get().appendLabShow("操作失败，出错了："+e.getMessage());
		}finally {
			dbHelper.destroy();
		}
		
	}
	/**
	 * 校验
	 * @param isbase 是否只校验基本信息
	 * @return
	 */
	private boolean validata(boolean isbase) {
		String pattern = "^(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[0-5]\\d{4}|[1-9]\\d{0,3})$";
		if(Func.isBlank(serverIp.getText())) {
			alert(Alert.AlertType.WARNING,"服务器地址不能为空");
			return false;
		}
		if(!ReUtil.isMatch(pattern, serverPort.getText())) {
			alert(Alert.AlertType.WARNING,"端口号格式不对");
			return false;
		}
		if(!isbase && Func.isBlank(dbName.getText())) {
			alert(Alert.AlertType.WARNING,"数据库名不能为空");
			return false;
		}
		if(Func.isBlank(dbUserName.getText())) {
			alert(Alert.AlertType.WARNING,"用户名不能为空");
			return false;
		}
		if(Func.isBlank(dbPassword.getText())) {
			alert(Alert.AlertType.WARNING,"密码不能为空");
			return false;
		}
		if(!isbase && Func.isBlank(this.chooserSavePath.getText())) {
			alert(Alert.AlertType.WARNING,"请选择保存的目录");
			return false;
		}
		return true;
	}
	/**
	 * 弹框
	 * @param alertType 类型
	 * @param msg 弹框内容
	 */
	private void alert(AlertType alertType,String msg) {
		Alert _alert = new Alert(alertType,msg,new ButtonType("确定", ButtonBar.ButtonData.YES));
		_alert.setTitle("确认");
//		_alert.setHeaderText(header);
		_alert.show();
	}
	/**
	 * 组装jdbc URL
	 * @return
	 */
	private String buildJdbcUrl() {
		TbCache.databaseName = dbName.getText();
		String url = "";
		if(dbpgsql.isSelected()) {
			url = "jdbc:postgresql://${host}:${port}/${dbname}";
		}
		if(dbmysql.isSelected()) {
			if(Func.equals(dbDriver.getValue(), "com.mysql.jdbc.Driver")) {
				url="jdbc:mysql://${host}:${port}/${dbname}?useUnicode=true&characterEncoding=UTF8&useSSL=false&useInformationSchema=true";
			}else {
				url = "jdbc:mysql://${host}:${port}/${dbname}?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF8&useSSL=false&useInformationSchema=true";
			}
		}
		url = url.replace("${host}", serverIp.getText());
		url = url.replace("${port}", serverPort.getText());
		url = url.replace("${dbname}", dbName.getText());
		return url;
	}
	/**
	 * 组装jdbc URL，不包含数据库
	 * @return
	 */
	private String buildJdbcBaseUrl() {
		String url = "";
		if(dbpgsql.isSelected()) {
			url = "jdbc:postgresql://${host}:${port}/";
		}
		if(dbmysql.isSelected()) {
			if(Func.equals(dbDriver.getValue(), "com.mysql.jdbc.Driver")) {
				url="jdbc:mysql://${host}:${port}?useUnicode=true&characterEncoding=UTF8&useSSL=false&useInformationSchema=true";
			}else {
				url = "jdbc:mysql://${host}:${port}?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF8&useSSL=false&useInformationSchema=true";
			}
		}
		url = url.replace("${host}", serverIp.getText());
		url = url.replace("${port}", serverPort.getText());
		return url;
	}
	
	private String loadHtmlTpl() {
		Resource obj = ResourceUtil.getResourceObj("tpl.html");
		try {
			return obj.readUtf8Str();
		}catch (Exception e) {
			log.error("读取模板出错了:{}",e);
		}
		return "";
	}

}
