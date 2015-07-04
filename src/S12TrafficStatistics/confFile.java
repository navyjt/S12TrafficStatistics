package S12TrafficStatistics;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
//本类用于读取配置文件，配置文件保存在根目录下，需要读取的配置文件分为数据库连接参数以及数据导入方式参数
public class confFile {
	
	public static Connection getConnection()
			throws SQLException, IOException ,ClassNotFoundException
	{
			//通过加载conn.ini文件来获取数据库连接的详细信息
			Properties props = new Properties();
			FileInputStream in = new FileInputStream("conn.ini");
			props.load(in);
			in.close();
			String url = props.getProperty("jdbc.url");
			String username = props.getProperty("jdbc.username");
			String password = props.getProperty("jdbc.password");
			//加载数据库驱动
			Class.forName("com.mysql.jdbc.Driver");
			//取得数据库连接
			//WriteLog.WriteLogFile("建立数据库连接成功，用户名"+username);
			return DriverManager.getConnection(url, username, password);
	}

	public static void writeImportManner(String keyname,String keyvalue) throws IOException
	{
		//文件导入方式
		try {  
			Properties props = new Properties();
            props.load(new FileInputStream("conn.ini"));   
            OutputStream fos = new FileOutputStream("conn.ini");              
            props.setProperty(keyname, keyvalue);   
            // 以适合使用 load 方法加载到 Properties 表中的格式，   
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流   
            props.store(fos, "Update '" + keyname + "' value");   
        } catch (IOException e) {   
            WriteLog.WriteLogFile("属性文件更新错误");   
        }   
		
	}
	
	public static String getImportManner()
	{
		Properties props = new Properties();   
        try {   
            InputStream in = new BufferedInputStream(new FileInputStream("conn.ini"));   
            props.load(in);   
            String value = props.getProperty("importmanner");   
            WriteLog.WriteLogFile("ImportManner键的值是："+ value);   
            return value;   
        } catch (Exception e) {   
            e.printStackTrace();   
            return null;   
        }   
	}

	public static String getLogDir() {
		Properties props = new Properties();   
        try {   
            InputStream in = new BufferedInputStream(new FileInputStream("conn.ini"));   
            props.load(in);   
            String value = props.getProperty("logdir");   
            WriteLog.WriteLogFile("日志目录的值是："+ value);   
            return value;   
        } catch (Exception e) {   
            e.printStackTrace();   
            return null;   
        }   

	}

}
