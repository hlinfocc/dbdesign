package net.hlinfo.dbdesign.opt;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import cn.hutool.core.io.FileUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import net.hlinfo.opt.Func;

public class ExportService {
	private static final Log log = LogFactory.get();
	
	private static final String HTML_DOCTYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n"
			+ "<html xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n"
			+ "xmlns:w=\"urn:schemas-microsoft-com:office:word\" xmlns:m=\"http://schemas.microsoft.com/office/2004/12/omml\"\n"
			+ "xmlns=\"http://www.w3.org/TR/REC-html40\">\n"
			+ "<head>\n"
			+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=${metaCharset}\" />\n"
			+ "<!--[if gte mso 9]><xml><w:WordDocument><w:View>Print</w:View><w:TrackMoves>false</w:TrackMoves><w:TrackFormatting/><w:ValidateAgainstSchemas/><w:SaveIfXMLInvalid>false</w:SaveIfXMLInvalid><w:IgnoreMixedContent>false</w:IgnoreMixedContent><w:AlwaysShowPlaceholderText>false</w:AlwaysShowPlaceholderText><w:DoNotPromoteQF/><w:LidThemeOther>EN-US</w:LidThemeOther><w:LidThemeAsian>ZH-CN</w:LidThemeAsian><w:LidThemeComplexScript>X-NONE</w:LidThemeComplexScript><w:Compatibility><w:BreakWrappedTables/><w:SnapToGridInCell/><w:WrapTextWithPunct/><w:UseAsianBreakRules/><w:DontGrowAutofit/><w:SplitPgBreakAndParaMark/><w:DontVertAlignCellWithSp/><w:DontBreakConstrainedForcedTables/><w:DontVertAlignInTxbx/><w:Word11KerningPairs/><w:CachedColBalance/><w:UseFELayout/></w:Compatibility><w:BrowserLevel>MicrosoftInternetExplorer4</w:BrowserLevel><m:mathPr><m:mathFont m:val=\"Cambria Math\"/><m:brkBin m:val=\"before\"/><m:brkBinSub m:val=\"--\"/><m:smallFrac m:val=\"off\"/><m:dispDef/><m:lMargin m:val=\"0\"/> <m:rMargin m:val=\"0\"/><m:defJc m:val=\"centerGroup\"/><m:wrapIndent m:val=\"1440\"/><m:intLim m:val=\"subSup\"/><m:naryLim m:val=\"undOvr\"/></m:mathPr></w:WordDocument></xml><![endif]-->"
			+ "</head><body>";
	private static final String HTML_END_TAG = "</body></html>";
   @SuppressWarnings("unused")
	private static final String HTML_DOC_TITLE_V1 ="<div style=\"width:100%;margin:0\">\n"
			+ "    <p style=\"text-align:center;white-space:pre-wrap;margin:20px 0;\">\n"
			+ "        <span style=\"font-family:'Times New Roman';font-weight: bold;font-size:18.0pt;color:#ff0000;white-space:pre-wrap;\">\n"
			+ "        ${title}</span>\n"
			+ "    </p>\n"
			+ "    <p style=\"text-align:left;white-space:pre-wrap;\">\n"
			+ "        <span style=\"font-family:'Times New Roman';font-size:10.0pt;color:#808080;white-space:pre-wrap;\">"
			+ "			政策文号：${docno}&nbsp;&nbsp;&nbsp;&nbsp;发布机构：${docPushUnit}&nbsp;&nbsp;&nbsp;&nbsp;发布日期：${docPushDate}"
			+ "			</span>\n"
			+ "    </p>\n"
			+ "    <p style=\"text-align:left;white-space:pre-wrap;\">\n"
			+ "        <span style=\"border-bottom:5px solid red;display:block;width:100%;height:5px;\">"
			+ "				<img width=\"100%\" src=\"${baseUrl}/docx/export-docx-line.png\">"
			+ "			</span>\n"
			+ "    </p>\n"
			+ "</div>";
    private static final String HTML_DOC_TITLE ="<div style=\"width:100%;margin:0\">\n"
			+ "    <p style=\"text-align:center;white-space:pre-wrap;margin:20px 0;\">\n"
			+ "        <span style=\"font-family:'宋体,Times New Roman';font-weight: bold;font-size:22pt;color:#000000;white-space:pre-wrap;\">\n"
			+ "        ${title}</span>\n"
			+ "    </p>\n"
			+ "    <p style=\"text-align:center;white-space:pre-wrap;margin-bottom:10px;\">\n"
			+ "        <span style=\"font-family:'宋体,Times New Roman';font-size:12.0pt;color:#000000;white-space:pre-wrap;\">"
			+ "			${docno}"
			+ "			</span>\n"
			+ "    </p>\n"
			+ "</div>";
    
    private static final String HTML_DOC_UNIT_DATE="<div style=\"width:100%;margin:0\">\n"
    		+ "<br><br>"
			+ "    <p style=\"text-align:right;white-space:pre-wrap;margin-top:20px;\">\n"
			+ "        <span style=\"font-family:'宋体,Times New Roman';font-size:12pt;color:#000000;white-space:pre-wrap;\">\n"
			+ "        ${docPushUnit}</span>\n"
			+ "    </p>\n"
			+ "    <p style=\"text-align:right;white-space:pre-wrap;\">\n"
			+ "        <span style=\"font-family:'宋体,Times New Roman';font-size:12.0pt;color:#000000;white-space:pre-wrap;\">"
			+ "			 ${docPushDate}"
			+ "			</span>\n"
			+ "    </p>\n"
			+ "</div>";
    @SuppressWarnings("unused")
    private static final String DocSplitLine="<div style=\"width:100%;margin:0\">\n"
			+ "    <p style=\"text-align:left;white-space:pre-wrap;\">\n"
			+ "        <span style=\"border-bottom:5px solid red;display:block;width:100%;height:5px;\">"
			+ "				<img width=\"100%\" src=\"${baseUrl}/docx/export-docx-line.png\">"
			+ "			</span>\n"
			+ "    </p>\n"
			+ "</div>";
    /**
     * 表结构模板
     */
    private static final String TableDetail = "<h3 style=\"mso-para-margin-left:0.0000gd;\" ><a name=\"_Toc27805\" ></a><b><span style=\"mso-spacerun:'yes';font-family:黑体;font-weight:bold;\n"
    		+ "font-size:15.0000pt;mso-font-kerning:1.0000pt;\" ><a name=\"_Toc16478874\" >${tableTitle}</a></span></b><b><span style=\"mso-spacerun:'yes';font-family:黑体;font-weight:bold;\n"
    		+ "font-size:15.0000pt;mso-font-kerning:1.0000pt;\" ><o:p></o:p></span></b></h3>\n"
    		+ "<table style=\"border-collapse: collapse; width: 430pt;\" border=\"1\" cellspacing=\"0\">\n"
    		+ "<tbody>\n"
    		+ "<tr>\n"
    		+ "<td style=\"width: 127.867px; padding: 0pt; background: #d9d9d9 none repeat scroll 0% 0%; text-align: center; vertical-align: middle; border: 1pt solid #000000;\">表名</td>\n"
    		+ "<td style=\"padding: 0pt; border: 1px solid #000000; background: #ffffff none repeat scroll 0% 0%; width: 481.133px; text-align: center; vertical-align: middle;\" colspan=\"6\">${tableName}</td>\n"
    		+ "</tr>\n"
    		+ "<tr>\n"
    		+ "<td style=\"width: 127.867px; padding: 0pt; background: #d9d9d9 none repeat scroll 0% 0%; text-align: center; vertical-align: middle; border: 1pt solid #000000;\" valign=\"top\" width=\"139\"><strong>字段名</strong></td>\n"
    		+ "<td style=\"width: 91px; padding: 0pt; border: 1px solid #000000; background: #d9d9d9 none repeat scroll 0% 0%; text-align: center; vertical-align: middle;\" valign=\"top\" width=\"69\">\n"
    		+ "<p class=\"MsoNormal\" style=\"mso-para-margin-top: 0.0000gd; mso-para-margin-bottom: 0.0000gd;\"><strong><span style=\"mso-spacerun: 'yes'; font-family: 宋体; mso-bidi-font-family: 'Times New Roman'; color: #000000; font-weight: bold; font-size: 12.0000pt;\">字段类型</span></strong></p>\n"
    		+ "</td>\n"
    		+ "<td style=\"width: 51.0833px; padding: 0pt; border: 1px solid #000000; background: #d9d9d9 none repeat scroll 0% 0%; text-align: center; vertical-align: middle;\" valign=\"top\" width=\"69\"><strong>主键</strong></td>\n"
    		+ "<td style=\"width: 55.1667px; padding: 0pt; border: 1px solid #000000; background: #d9d9d9 none repeat scroll 0% 0%; text-align: center; vertical-align: middle;\" valign=\"top\" width=\"69\"><strong>唯一键</strong></td>\n"
    		+ "<td style=\"width: 77.5833px; padding: 0pt; border: 1px solid #000000; background: #d9d9d9 none repeat scroll 0% 0%; text-align: center; vertical-align: middle;\" valign=\"top\" width=\"69\"><strong>非空</strong></td>\n"
    		+ "<td style=\"width: 66.45px; padding: 0pt; border: 1px solid #000000; background: #d9d9d9 none repeat scroll 0% 0%; text-align: center; vertical-align: middle;\" valign=\"top\" width=\"69\"><strong>默认值</strong></td>\n"
    		+ "<td style=\"width: 139.85px; padding: 0pt; border: 1px solid #000000; background: #d9d9d9 none repeat scroll 0% 0%; text-align: center; vertical-align: middle;\" valign=\"top\" width=\"139\"><strong>说明</strong></td>\n"
    		+ "</tr>\n"
    		+ "${tableDetailField}\n"
    		+ "</tbody>\n"
    		+ "</table>\n";
    /**
     * 表结构字段数据模板
     */
    private static final String tableDetailField="<tr>\n"
    		+ "<td style=\"width: 127.867px; padding: 0pt; border: 1px solid #000000; background: #ffffff none repeat scroll 0% 0%;\" valign=\"top\" width=\"139\">${fieldName}</td>\n"
    		+ "<td style=\"width: 91px; padding: 0pt; border: 1px solid #000000; background: #ffffff none repeat scroll 0% 0%;\" valign=\"top\" width=\"69\">${fieldType}</td>\n"
    		+ "<td style=\"width: 51.0833px; padding: 0pt; border: 1px solid #000000; background: #ffffff none repeat scroll 0% 0%; text-align: center;\" valign=\"top\" width=\"69\">${fieldPK}</td>\n"
    		+ "<td style=\"width: 55.1667px; padding: 0pt; border: 1px solid #000000; background: #ffffff none repeat scroll 0% 0%; text-align: center;\" valign=\"top\" width=\"69\">${fieldUK}</td>\n"
    		+ "<td style=\"width: 77.5833px; padding: 0pt; border: 1px solid #000000; background: #ffffff none repeat scroll 0% 0%; text-align: center;\" valign=\"top\" width=\"69\">${nonempty}</td>\n"
    		+ "<td style=\"width: 66.45px; padding: 0pt; border: 1px solid #000000; background: #ffffff none repeat scroll 0% 0%; text-align: center;\" valign=\"top\" width=\"69\">${defaultval}</td>\n"
    		+ "<td style=\"width: 139.85px; padding: 0pt; border: 1px solid #000000; background: #ffffff none repeat scroll 0% 0%;\" valign=\"top\" width=\"139\">${fieldComment}</td>\n"
    		+ "</tr>\n";
    
    /**
     * 根据文章实体对象生成Word
     * @param html 由模板组装的html内容
     * @param saveFilePath 保存待生成的文档的路径
     * @param dbName 数据库名称
     * @return 生成的Word的路径
     * @throws IOException
     */
    public String createDocx(String html,String saveFilePath,String dbName) throws IOException{
    	PushMsg.get().appendLabShow("开始生成数据库设计文档...");
    	File fileDir = new File(saveFilePath);
		FileUtil.mkdir(fileDir);
		saveFilePath = saveFilePath+File.separatorChar+dbName+"-dbdesign-"+Func.Times.nowDateBasic()+".doc";
		String rsPath = this.createDocxByParem(html, saveFilePath);
		return rsPath;
	}
    /**
     * 
     * @param html 由模板组装的html内容
     * @param outDocxPath 保存的文件路径
     * @return
     * @throws IOException
     */
    private String createDocxByParem(String html,String outDocxPath) throws IOException{
		//组装内容
    	String htmlDoctype = HTML_DOCTYPE;
    	//根据操作系统设置导出文件编码，Windows为gbk
    	String osName = System.getProperty("os.name");
    	if(osName.startsWith("Windows")) {
    		htmlDoctype = htmlDoctype.replace("${metaCharset}", "gbk");
    	}else {
    		htmlDoctype = htmlDoctype.replace("${metaCharset}", "utf-8");
    	}
		String content = htmlDoctype+html+HTML_END_TAG;
		//poi 生成docx
		byte b[] = content.getBytes();
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		POIFSFileSystem poifs = new POIFSFileSystem();
		DirectoryEntry directory = poifs.getRoot();
		DocumentEntry documentEntry = directory.createDocument("WordDocument", bais);
				
		FileOutputStream ostream = new FileOutputStream(outDocxPath);
		poifs.writeFilesystem(ostream);
		bais.close();
		ostream.close();
		return outDocxPath;
	}
    /**
     * 组装表清单
     * @param list
     * @return
     */
    public String buildTableInfo(List<TableInfo> list) {
    	final String tableTpl = "<tr valign=\"top\">\n"
    			+ "        	<td width=\"134\" style=\"border:1px solid #00000a;padding-top:0;padding-bottom:0;padding-left:.2cm;padding-right:.19cm\">${tableName}"
    			+ "        	</td>\n"
    			+ "        	<td width=\"410\" style=\"border:1px solid #00000a;padding-top:0;padding-bottom:0;padding-left:.2cm;padding-right:.19cm\">${tableComment}"
    			+ "        	</td>\n"
    			+ "        </tr>";
    	StringBuffer sb = new StringBuffer();
    	for(TableInfo tb:list) {
    		String tpl = tableTpl;
    		tpl = tpl.replace("${tableName}", tb.getTableName());
    		tpl = tpl.replace("${tableComment}", tb.getTableComment());
    		sb.append(tpl);
    	}
    	return sb.toString();
    }
    
    /**
     * 构建表详细信息
     * @return
     * @throws SQLException 
     */
    public String buildTableDetail(DbHelper dbHelper) throws SQLException {
    	PushMsg.get().appendLabShow("开始解析表结构信息...");
    	if(TbCache.tblist.isEmpty()) {
    		dbHelper.findTableInfo();
		}
    	log.debug("{}",TbCache.tblist);
    	int tbCount = TbCache.tblist.size();
    	StringBuffer sb = new StringBuffer();
    	for(int i=0;i<tbCount;i++) {
    		TableInfo table = TbCache.tblist.get(i);
    		PushMsg.get().appendLabShow("解析表"+table.getTableName()+"【"+table.getTableComment()+"】");
    		List<TableFields> fieldList = dbHelper.findTableFields(table.getTableName());
    		log.debug("{}",fieldList);
    		String tableTitle = "4.2."+(i+1)+" 表"+table.getTableName();
    		if(Func.isNotBlank(table.getTableComment())) {
    			tableTitle += "("+table.getTableComment()+")";
    		}
    		String tableDetail = TableDetail;
    		tableDetail = tableDetail.replace("${tableTitle}", tableTitle);
    		tableDetail = tableDetail.replace("${tableName}", table.getTableName());
    		StringBuffer sub = new StringBuffer();
    		for(TableFields tf:fieldList) {
    			String fieldData = tableDetailField;
    			fieldData = fieldData.replace("${fieldName}", tf.getFieldName());
    			fieldData = fieldData.replace("${fieldType}", tf.getFieldType());
    			fieldData = fieldData.replace("${fieldPK}", tf.getFieldPK());
    			fieldData = fieldData.replace("${fieldUK}", tf.getFieldUK());
    			fieldData = fieldData.replace("${nonempty}", tf.getNonempty());
    			fieldData = fieldData.replace("${defaultval}", tf.getDefaultval()==null?"":tf.getDefaultval());
    			fieldData = fieldData.replace("${fieldComment}", tf.getFieldComment()==null?"":tf.getFieldComment());
    			sub.append(fieldData);
    		}
    		tableDetail = tableDetail.replace("${tableDetailField}", sub.toString());
    		sb.append(tableDetail);
    	}
    	PushMsg.get().appendLabShow("解析表结构信息结束...OK");
    	return sb.toString();
    }
    
}
