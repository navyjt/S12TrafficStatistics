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
//�������ڶ�ȡ�����ļ��������ļ������ڸ�Ŀ¼�£���Ҫ��ȡ�������ļ���Ϊ���ݿ����Ӳ����Լ����ݵ��뷽ʽ����
public class confFile {
	
	public static Connection getConnection()
			throws SQLException, IOException ,ClassNotFoundException
	{
			//ͨ������conn.ini�ļ�����ȡ���ݿ����ӵ���ϸ��Ϣ
			Properties props = new Properties();
			FileInputStream in = new FileInputStream("conn.ini");
			props.load(in);
			in.close();
			String url = props.getProperty("jdbc.url");
			String username = props.getProperty("jdbc.username");
			String password = props.getProperty("jdbc.password");
			//�������ݿ�����
			Class.forName("com.mysql.jdbc.Driver");
			//ȡ�����ݿ�����
			//WriteLog.WriteLogFile("�������ݿ����ӳɹ����û���"+username);
			return DriverManager.getConnection(url, username, password);
	}

	public static void writeImportManner(String keyname,String keyvalue) throws IOException
	{
		//�ļ����뷽ʽ
		try {  
			Properties props = new Properties();
            props.load(new FileInputStream("conn.ini"));   
            OutputStream fos = new FileOutputStream("conn.ini");              
            props.setProperty(keyname, keyvalue);   
            // ���ʺ�ʹ�� load �������ص� Properties ���еĸ�ʽ��   
            // ���� Properties ���е������б�����Ԫ�ضԣ�д�������   
            props.store(fos, "Update '" + keyname + "' value");   
        } catch (IOException e) {   
            WriteLog.WriteLogFile("�����ļ����´���");   
        }   
		
	}
	
	public static String getImportManner()
	{
		Properties props = new Properties();   
        try {   
            InputStream in = new BufferedInputStream(new FileInputStream("conn.ini"));   
            props.load(in);   
            String value = props.getProperty("importmanner");   
            WriteLog.WriteLogFile("ImportManner����ֵ�ǣ�"+ value);   
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
            WriteLog.WriteLogFile("��־Ŀ¼��ֵ�ǣ�"+ value);   
            return value;   
        } catch (Exception e) {   
            e.printStackTrace();   
            return null;   
        }   

	}

}
