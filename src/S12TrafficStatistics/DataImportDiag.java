package S12TrafficStatistics;


import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


@SuppressWarnings("serial")
public class DataImportDiag extends JDialog{
	
	public static JFrame f = new JFrame("��ȡ�ļ�");
//	private JButton importBtn = new JButton("����");
//	private JPanel top = new JPanel();
	private JPanel filesourcePanel = new JPanel();
	private JRadioButton jrbDay;
	private JRadioButton jrbHour;
	private JRadioButton jrbManual;
	private JButton jBtn = new JButton("ȷ��");
	private JButton jBtncancel = new JButton("ȡ��");
	public static JProgressBar bar = new JProgressBar(JProgressBar.HORIZONTAL);
	
	static String line;
	static int lineToSkip;
	static FileReader fr;
	static LineNumberReader lnr;
	static String trafficDate;
	static int beginTimeInt;
	static int endTimeInt;
	static String[] traffic1;
	static String[] traffic2;
	static int ThreadEndFlag;
	
	private static Connection conn;
	private static Statement stmt;
	
	public DataImportDiag(JFrame f, String string, boolean b) throws IOException {
		// TODO �Զ����ɵĹ��캯�����
		init();
	}

	public void init() 
	{
		f.setSize(500, 260);
		f.setVisible(true);
		f.setLocationRelativeTo(null);
	
		//��ʼ���ļ�Դradiobutton
		filesourcePanel.setBorder(new TitledBorder(new EtchedBorder(),"ѡ���뷽ʽ"));
		filesourcePanel.setLayout(new GridLayout(3, 1));
		filesourcePanel.add(jrbDay = new JRadioButton("ÿ���Զ�����һ������"));
		filesourcePanel.add(jrbHour = new JRadioButton("ÿСʱ�Զ�����һ������"));
		filesourcePanel.add(jrbManual = new JRadioButton("�ֶ�ѡ�����ݵ���"));
		
		f.setLayout(null);
		//f.add(filesourcePanel, BorderLayout.CENTER);
		//filesourcePanel.setSize(400,200);
		filesourcePanel.setBounds(20, 15, 450, 120);

		//filesourcePanel.add(importBtn);
		f.add(filesourcePanel);
		jBtn.setBounds(155,175,80,32);
		jBtn.setVisible(true);
		jBtncancel.setBounds(255,175,80,32);
		jBtncancel.setVisible(false);
		bar.setBounds(20, 145, 450, 20);
		bar.setVisible(false);
		f.add(jBtn);
		f.add(jBtncancel);
		f.add(bar);
		bar.setMaximum(100);
		bar.setMinimum(0);
		bar.setStringPainted(true);
		f.setVisible(true);
	
		ButtonGroup group = new ButtonGroup();
		group.add(jrbDay);
		group.add(jrbHour);
		group.add(jrbManual);
		
		//�����ʼ�����ȡ�����ļ�����ȡ���ݵ��뷽ʽ
		String strManner = confFile.getImportManner();
		if (strManner.equals("day"))
		{
			jrbDay.setSelected(true);
		}
		else if(strManner.equals("hour"))
		{
			jrbHour.setSelected(true);
		}
		else if(strManner.equals("manual"))
		{
			jrbManual.setSelected(true);
		}
		else
		{
			JOptionPane.showMessageDialog(null, "�����ļ����󣬶�ȡ������ȷ�����ݵ��뷽ʽ��", "����", JOptionPane.ERROR_MESSAGE); 
		}
			
		//���ȷ����ť����ִ���κβ�����ֻ�ǹر����ݵ��봰�ڣ�����Ĳ����Ѿ��ڵ��radiobuttonʱ��ɡ�
		jBtn.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e) {
				f.setVisible(false);
			}
			
		}
		);
		jBtncancel.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e) {
				ThreadEndFlag = 1;
			}
			
		}
		);
			
		//�޸����ݵ��뷽ʽ
		jrbDay.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				
					JOptionPane.showMessageDialog(null, "ÿ���Զ�����һ�����ݣ�", "����", JOptionPane.INFORMATION_MESSAGE); 
					try {
						confFile.writeImportManner("importmanner", "day");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					MyWindowDemo.ImportTrafficManner("day", true);
				}
		}
		);
		
		jrbHour.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				
					JOptionPane.showMessageDialog(null, "ÿСʱ�Զ�����һ�����ݣ�", "����", JOptionPane.INFORMATION_MESSAGE); 
					try {
						confFile.writeImportManner("importmanner", "hour");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					MyWindowDemo.ImportTrafficManner("hour",true);
				}
		}
		);
		
		jrbManual.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				
					//JOptionPane.showMessageDialog(null, "�ֶ��������ݣ�", "����", JOptionPane.INFORMATION_MESSAGE); 
					
					try {
						confFile.writeImportManner("importmanner", "manual");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					//�������Ϊæ
					f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					bar.setMinimum(0);
					bar.setMaximum(100);
					bar.setValue(0);
					bar.setVisible(true);
					ThreadEndFlag=0;
					Thread rfThread = new ReadFileThread();
					rfThread.start();
					//�˴�����ʽǷ�ף�Ӧ�ðѺ�ʱ������ŵ����߳�����ɣ������������ɺ�ʱ�����񣬽���ɳ����ڴ�ʱ����ui����Ӧ��
					//�õȳ�����UI�������Ӧbutton��ͬʱҲ���progressbar��ˢ�²���ʱ������Ҫ����bar.paintImmediately(rect)�ſ��Լ�ʱˢ�¡�
					MyWindowDemo.ImportTrafficManner("manual",true);
					
				
	
					
					ThreadEndFlag=1;	//�߳̽�����־
					f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
					bar.setValue(100);
					try {
			            Thread.sleep(2*1000);
			        } catch (InterruptedException e1) {
			            // TODO Auto-generated catch block
			            e1.printStackTrace();
			        }
					
					//rfThread.interrupt();
					//endTimeInt = 24;	//�˾�ȷ�������ڶ�ȡ�������ļ�ʱ���ܽ�������������100%
	/*			Timer timer = new Timer(1000 , new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							bar.setValue((int)(100/24.0*endTimeInt));
						}
					});
					timer.start();*/
					
					
				}
		}
		);
	}
	
/*	public class ReadFileThread extends Thread
	{


		public void run()
		{
			MyWindowDemo.ImportTrafficManner("manual",true);
		}
	}*/
	public class ReadFileThread extends Thread
	{
		public void run()
		{
			while((endTimeInt<= 24)&&(ThreadEndFlag != 1))
			{
				boolean bl = (endTimeInt<= 24)&&(ThreadEndFlag != 1);
				System.out.println("============flagΪ  "+ThreadEndFlag+"  endtimeΪ  "+endTimeInt+" blΪ" +bl );
				try {
					
					
					Dimension d= bar.getSize();
					Rectangle rect = new Rectangle(0,0,d.width,d.height);
					bar.setValue((int)(100/24.0*endTimeInt));
					//bar.repaint();
					bar.paintImmediately(rect);
					System.out.println("flagΪ  "+ThreadEndFlag+"  endtimeΪ  "+endTimeInt+" blΪ" +bl +"���������� "+(int)(100/24.0*endTimeInt)+"% .");
					
					Thread.sleep(500);
					//�߳�˯�ߣ���֤1���Ӹ���һ�ν��������߳�˯�����������ں���д��Ϊ�˱�֤�߳���ִ����ȫ��������˯��1���ӣ�����߳���˯�ߣ������˯��ǰ��˯�ߺ�����ݲ�һ�¡�
					
				} 
					catch (InterruptedException e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				}

				
			}
				
		}

	}
	
	public static  void handleTraffic(int hour) throws IOException
	{
		try
		{
			//��ȡ���ݿ�����
			conn = confFile.getConnection();
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		}

		catch (Exception e1)
		{  
			e1.printStackTrace();
		}
		
		skipLine(1);
		getDateFromLine(line);
		skipLine(2);
		getTimeFromLine(line);
		skipLine(8);
		while(line.length() != 0)
		{
			getTrafficFromLine(hour);
			skipLine(1);
		}
	}
	
	//��line���ж�ȡ����
	public static void getDateFromLine(String line)
	{
		String reg = "[0-9]{4}-[A-Z]{3}-[0-9]{1,2}";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(line);
		if (m.find())
		{
			trafficDate = m.group();
		//	System.out.println(m.group());
		}
		else
			System.out.println("û�ҵ����ڲ���");
	}
	//��line���ж�ȡʱ��
	public static void getTimeFromLine(String line)
	{
		String reg = "[0-9]{1,2}H  0M  -\\s{2,3}[0-9]{1,2}H  0M";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(line);
		if (m.find())
		{
			String beginTime = m.group().substring(0, m.group().indexOf("H"));
			beginTimeInt = Integer.parseInt(beginTime);
			endTimeInt = beginTimeInt+1;
			System.out.println("�˴�ʱ��Ϊ"+beginTimeInt+"��"+endTimeInt);
			//DataImportDiag.bar.setValue((int)(100/24.0*endTimeInt));
		}
		else
			System.out.println("û�ҵ�ʱ�����");
	}
	
	//����
	public static void skipLine(int lineToSkip) throws IOException
	{
		for (int i = 0;i <lineToSkip;i++)
		{
			line = lnr.readLine();
		}
	}
	
	//��ȡ�������ı�����
	public static void getTrafficFromLine(int hour)throws IOException
	{
		traffic1 = line.trim().split(" +");
		skipLine(1);
		traffic2 = line.trim().split(" +");

		
		//��Ϊtraffic2��������ֻ������ͳ��ֻ������е�������ͬ��ͨ���˲��������������жϣ�����д�����ű���
		if (traffic2.length == 4)
			{
				storageToDatabase("inc",trafficDate,beginTimeInt,endTimeInt,traffic1,traffic2,hour);
				MyWindowDemo.tbLabel.setText("д����ֻ����������ݿ�ɹ���");
				//System.out.println("���������������");
			}
		else if (traffic2.length == 5)
			
			{
				storageToDatabase("out",trafficDate,beginTimeInt,endTimeInt,traffic1,traffic2,hour);
				MyWindowDemo.tbLabel.setText("д����ֻ����������ݿ�ɹ���");
				//System.out.println("�����������ǳ���");
			}
		
	}
	
	//�洢�����ݿ�
	private static void storageToDatabase(String incout,String trafficDate,int beginTimeInt,int endTimeInt,String[] traffic1,String[] traffic2,int hour) 
	{
		
		//System.out.println("�˴λ���ͳ������Ϊ"+trafficDate+"ʱ��Ϊ"+beginTimeInt+"��"+endTimeInt+"ʱ,\r\nͳ�Ƶ�λΪ"+traffic1[0]);
		
		//��ʱ��Ҫ��date��ʽ2014-sep-02תΪ2014-09-02
		//���������hour���������ж��ļ��ж�ȡ��ʱ���Ƿ��뵱ǰʱ����ͬ���ž�ÿСʱ��ȡ�������ļ������µ��ظ�д�����ݿ���Ϊ��
		String strDay[] = trafficDate.split("-");
		String strintMon = "";
		
		String strAccDay = strDay[2].length()>1?strDay[2]:("0"+strDay[2]);
		
		switch(strDay[1])
		{
			case "JAN": strintMon = "01";break;
			case "FEB": strintMon = "02";break;
			case "MAR": strintMon = "03";break;
			case "APR": strintMon = "04";break;
			case "MAY": strintMon = "05";break;
			case "JUN": strintMon = "06";break;
			case "JUL": strintMon = "07";break;
			case "AUG": strintMon = "08";break;
			case "SEP": strintMon = "09";break;
			case "OCT": strintMon = "10";break;
			case "NOV": strintMon = "11";break;
			case "DEC": strintMon = "12";break;
		}
		
		
		String trafficDateTime = strDay[0]+"-"+strintMon+"-"+strAccDay+" "+String.valueOf(beginTimeInt)+":00:00";
		//System.out.println(trafficDateTime);
		if(incout.equals("inc"))
		{
			//������ֶ�Ϊinc�����ж�Ϊ��ֻ�������д����ֻ��������ݱ�
			
			String query = "insert ignore into statistics_inc (ID,Time,Assgn,Avlb,Seiz,Answ,Occ,Convocc,Attlocal,Atttrans,Answlocal,Answtrans) values('"+traffic1[0]+"','"+trafficDateTime+"','"+traffic1[1]+"','"+traffic1[2]+"',"
					+ "'"+traffic1[3]+"','"+traffic1[4]+"','"+traffic1[5]+"','"+traffic1[6]+"',"
					+ "'"+traffic2[0]+"','"+traffic2[1]+"','"+traffic2[2]+"','"+traffic2[3]+"')";
			//��ѯ�û�ѡ������ݱ�
			try {
				if((hour == -1)||(hour != 0 && hour-1 == beginTimeInt))
				stmt.executeUpdate(query);
			} catch (SQLException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			
		}
		
		else if (incout.equals("out"))
		{
			//���ֻ�������д����ֻ�������
			String query = "insert ignore into statistics_out(ID,Time,Assgn,Avlb,Seiz,Answ,Occ,Convocc,Callatt,Thrsw,Retries,Noansw,Congdist) values('"+traffic1[0]+"','"+trafficDateTime+"','"+traffic1[1]+"','"+traffic1[2]+"',"
					+ "'"+traffic1[3]+"','"+traffic1[4]+"','"+traffic1[5]+"','"+traffic1[6]+"',"
					+ "'"+traffic2[0]+"','"+traffic2[1]+"','"+traffic2[2]+"','"+traffic2[3]+"','"+traffic2[4]+"')";
			//��ѯ�û�ѡ������ݱ�
			try {
				if ((hour == -1)||(hour != 0 && hour-1 == beginTimeInt))
				stmt.executeUpdate(query);
			} catch (SQLException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		}
		else
			WriteLog.WriteLogFile("storageToDatabase��������");
		
			
	}
	
	
	public static void readTrafficFile(String strfilename,int hour)
	{
	
			try {
				fr = new FileReader(strfilename);
			} catch (FileNotFoundException e1) {
				// TODO �Զ����ɵ� catch ��
				//JOptionPane.showMessageDialog(null, "��־�ļ�δ�ҵ���", "����", JOptionPane.INFORMATION_MESSAGE); 
				MyWindowDemo.tbLabel.setText("���ļ�  "+strfilename+"  ʧ�ܣ����ʵ�����ļ��е���־Ŀ¼��");
				e1.printStackTrace();
			}
			lnr = new LineNumberReader(fr);		
			 line = null;
			String reg = "\\bACTIVATE-TKG-REPORT\\b";
			
			Pattern p = Pattern.compile(reg);
		
			
			try {
				while ((line = lnr.readLine())!= null)
				{
					Matcher m = p.matcher(line);
					if(m.find())
					{	
						line = lnr.readLine();
						if (line.indexOf("PART 0001")!= -1)
						{
							line = lnr.readLine();
							line = lnr.readLine();
							if(line.indexOf("R E S U L T S") != -1)
							{
								handleTraffic(hour);
		
							}
							else 
							{
								System.out.println(lnr.getLineNumber()+":"+"�ٵ�Part1�ҵ���");
							}
						}
						if (line.indexOf("PART 0002")!= -1)
						{
							line = lnr.readLine();
							line = lnr.readLine();
							if(line.indexOf("R E S U L T S") != -1)
							{
								handleTraffic(hour);
							}
							else 
							{
								System.out.println(lnr.getLineNumber()+":"+"�ٵ�Part2�ҵ���");
							}
						}
						if (line.indexOf("PART 0003")!= -1)
						{
							line = lnr.readLine();
							line = lnr.readLine();
							if(line.indexOf("R E S U L T S") != -1)
							{
								handleTraffic(hour);
		
							}
							else 
							{
								System.out.println(lnr.getLineNumber()+":"+"�ٵ�Part3�ҵ���");
							}
						}
						if (line.indexOf("PART 0004")!= -1)
						{
							line = lnr.readLine();
							line = lnr.readLine();
							if(line.indexOf("R E S U L T S") != -1)
							{
								handleTraffic(hour);
							}
							else 
							{
								System.out.println(lnr.getLineNumber()+":"+"�ٵ�Part4�ҵ���");
							}
						}
					}
					
				}
			} catch (IOException e1) {
				// TODO �Զ����ɵ� catch ��
				e1.printStackTrace();
			}
	}
	
}
