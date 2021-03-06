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
    
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		log.debug("?????????javaFx??????.....");
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
		//??????????????????
		String homePath = System.getProperty("user.home");
		String defaultPath = homePath + File.separatorChar +"Desktop";
		String defaultPathCn = homePath + File.separatorChar +"??????";
		//??????Desktop????????????
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
		directoryChooser.setTitle("?????????????????????");
		//???????????????????????????
		String homePath = System.getProperty("user.home");
		String desktopPath = homePath + File.separatorChar +"Desktop";
		String defaultPathCn = homePath + File.separatorChar +"??????";
		//??????Desktop????????????
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
				PushMsg.get().appendLabShow("??????????????????????????????????????????...");
				this.alert(Alert.AlertType.ERROR, "??????????????????????????????????????????????????????");
			}else {
				chooserSavePath.setText(selectedDirectory.getAbsolutePath());
			}
		}
	}
	/**
	 * ????????????
	 * @param even
	 */
	@FXML
	void onConnServerTest(ActionEvent even) {
		PushMsg.get().clean();
		DbHelper dbHelper = null;
		try {
			if(this.validata()) {			
				PushMsg.get().appendLabShow("?????????????????????...");
				DbConn.jdbcUrl = this.buildJdbcUrl();
				DbConn.driverClassName = this.dbDriver.getValue();
				DbConn.jdbcUserName = this.dbUserName.getText();
				DbConn.jdbcPasswd = this.dbPassword.getText();
				dbHelper = new DbHelper();
				if(dbHelper.isConn()) {
					PushMsg.get().appendLabShow("?????????????????????...");
				}
				TbCache.tblist.clear();
		    	dbHelper.findTableInfo();
				PushMsg.get().appendLabShow("???????????????...");
				StringBuffer sb = new StringBuffer();
				sb.append("+--------------------------------+--------------------------------+\n"
						+ "|??????                          | ??????                          |\n"
						+ "+--------------------------------+--------------------------------+\n");
				for (TableInfo tb:TbCache.tblist) {
			    	String tableName = tb.getTableName();//??????
			    	String tableComment = tb.getTableComment();//?????????
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
			log.error("?????????:{}",e);
			PushMsg.get().appendLabShow("????????????"+e.getMessage());
		} finally {
			dbHelper.destroy();
		}
	}
	/**
	 * ????????????
	 * @param even
	 */
	@FXML
	void onNowCreateDesign(ActionEvent even) {
		DbHelper dbHelper = null;
		try {
			if(this.validata()) {			
				PushMsg.get().appendLabShow("???????????????????????????...");
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
					PushMsg.get().appendLabShow("?????????????????????????????????????????????????????????...");
					this.alert(Alert.AlertType.ERROR, "????????????????????????????????????");
				}else {
					ExportService es = new ExportService();
					html = html.replace("${tableListInfo}", es.buildTableInfo(TbCache.tblist));
					//?????????????????????
					String tableFieldDetail = es.buildTableDetail(dbHelper);
					html = html.replace("${tableFieldDetailInfo}", tableFieldDetail);
					String filePath = es.createDocx(html, saveFilePath,TbCache.databaseName);
					PushMsg.get().appendLabShow("?????????????????????????????????...");
					PushMsg.get().appendLabShow("??????????????????"+filePath);
				}
			}
		} catch (Exception e) {
			log.error("????????????????????????:{}",e);
			PushMsg.get().appendLabShow("???????????????????????????"+e.getMessage());
		}finally {
			dbHelper.destroy();
		}
		
	}
	/**
	 * ??????
	 * @return
	 */
	private boolean validata() {
		String pattern = "^(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[0-5]\\d{4}|[1-9]\\d{0,3})$";
		if(Func.isBlank(serverIp.getText())) {
			alert(Alert.AlertType.WARNING,"???????????????????????????");
			return false;
		}
		if(!ReUtil.isMatch(pattern, serverPort.getText())) {
			alert(Alert.AlertType.WARNING,"?????????????????????");
			return false;
		}
		if(Func.isBlank(dbName.getText())) {
			alert(Alert.AlertType.WARNING,"????????????????????????");
			return false;
		}
		if(Func.isBlank(dbUserName.getText())) {
			alert(Alert.AlertType.WARNING,"?????????????????????");
			return false;
		}
		if(Func.isBlank(dbPassword.getText())) {
			alert(Alert.AlertType.WARNING,"??????????????????");
			return false;
		}
		if(Func.isBlank(this.chooserSavePath.getText())) {
			alert(Alert.AlertType.WARNING,"????????????????????????");
			return false;
		}
		return true;
	}
	/**
	 * ??????
	 * @param alertType ??????
	 * @param msg ????????????
	 */
	private void alert(AlertType alertType,String msg) {
		Alert _alert = new Alert(alertType,msg,new ButtonType("??????", ButtonBar.ButtonData.YES));
		_alert.setTitle("??????");
//		_alert.setHeaderText(header);
		_alert.show();
	}
	/**
	 * ??????jdbc URL
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
	
	private String loadHtmlTpl() {
		Resource obj = ResourceUtil.getResourceObj("tpl.html");
		try {
			return obj.readUtf8Str();
		}catch (Exception e) {
			log.error("?????????????????????:{}",e);
		}
		return "";
	}

}
