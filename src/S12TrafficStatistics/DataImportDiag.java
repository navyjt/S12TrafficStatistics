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
	
	public static JFrame f = new JFrame("读取文件");
//	private JButton importBtn = new JButton("导入");
//	private JPanel top = new JPanel();
	private JPanel filesourcePanel = new JPanel();
	private JRadioButton jrbDay;
	private JRadioButton jrbHour;
	private JRadioButton jrbManual;
	private JButton jBtn = new JButton("确定");
	private JButton jBtncancel = new JButton("取消");
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
		// TODO 自动生成的构造函数存根
		init();
	}

	public void init() 
	{
		f.setSize(500, 260);
		f.setVisible(true);
		f.setLocationRelativeTo(null);
	
		//初始化文件源radiobutton
		filesourcePanel.setBorder(new TitledBorder(new EtchedBorder(),"选择导入方式"));
		filesourcePanel.setLayout(new GridLayout(3, 1));
		filesourcePanel.add(jrbDay = new JRadioButton("每日自动导入一次数据"));
		filesourcePanel.add(jrbHour = new JRadioButton("每小时自动导入一次数据"));
		filesourcePanel.add(jrbManual = new JRadioButton("手动选择数据导入"));
		
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
		
		//窗体初始化后读取配置文件，获取数据导入方式
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
			JOptionPane.showMessageDialog(null, "配置文件有误，读取不到正确的数据导入方式！", "错误", JOptionPane.ERROR_MESSAGE); 
		}
			
		//点击确定按钮，不执行任何操作，只是关闭数据导入窗口，具体的操作已经在点击radiobutton时完成。
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
			
		//修改数据导入方式
		jrbDay.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				
					JOptionPane.showMessageDialog(null, "每日自动导入一次数据！", "警告", JOptionPane.INFORMATION_MESSAGE); 
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
				
					JOptionPane.showMessageDialog(null, "每小时自动导入一次数据！", "警告", JOptionPane.INFORMATION_MESSAGE); 
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
				
					//JOptionPane.showMessageDialog(null, "手动导入数据！", "警告", JOptionPane.INFORMATION_MESSAGE); 
					
					try {
						confFile.writeImportManner("importmanner", "manual");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					//设置鼠标为忙
					f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					bar.setMinimum(0);
					bar.setMaximum(100);
					bar.setValue(0);
					bar.setVisible(true);
					ThreadEndFlag=0;
					Thread rfThread = new ReadFileThread();
					rfThread.start();
					//此处处理方式欠妥，应该把耗时的任务放到子线程中完成，如果在这里完成耗时的任务，将造成程序在此时界面ui无响应，
					//得等程序处理UI完才能响应button，同时也造成progressbar的刷新不及时现象，需要调用bar.paintImmediately(rect)才可以及时刷新。
					MyWindowDemo.ImportTrafficManner("manual",true);
					
				
	
					
					ThreadEndFlag=1;	//线程结束标志
					f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
					bar.setValue(100);
					try {
			            Thread.sleep(2*1000);
			        } catch (InterruptedException e1) {
			            // TODO Auto-generated catch block
			            e1.printStackTrace();
			        }
					
					//rfThread.interrupt();
					//endTimeInt = 24;	//此句确保程序在读取不完整文件时仍能将进度条跑满至100%
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
				System.out.println("============flag为  "+ThreadEndFlag+"  endtime为  "+endTimeInt+" bl为" +bl );
				try {
					
					
					Dimension d= bar.getSize();
					Rectangle rect = new Rectangle(0,0,d.width,d.height);
					bar.setValue((int)(100/24.0*endTimeInt));
					//bar.repaint();
					bar.paintImmediately(rect);
					System.out.println("flag为  "+ThreadEndFlag+"  endtime为  "+endTimeInt+" bl为" +bl +"进度条走了 "+(int)(100/24.0*endTimeInt)+"% .");
					
					Thread.sleep(500);
					//线程睡眠，保证1秒钟更新一次进度条，线程睡眠这条语句放在后面写是为了保证线程先执行完全部动作再睡眠1秒钟，如果线程先睡眠，会造成睡眠前和睡眠后的数据不一致。
					
				} 
					catch (InterruptedException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}

				
			}
				
		}

	}
	
	public static  void handleTraffic(int hour) throws IOException
	{
		try
		{
			//获取数据库连接
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
	
	//从line行中读取日期
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
			System.out.println("没找到日期参数");
	}
	//从line行中读取时间
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
			System.out.println("此次时间为"+beginTimeInt+"至"+endTimeInt);
			//DataImportDiag.bar.setValue((int)(100/24.0*endTimeInt));
		}
		else
			System.out.println("没找到时间参数");
	}
	
	//换行
	public static void skipLine(int lineToSkip) throws IOException
	{
		for (int i = 0;i <lineToSkip;i++)
		{
			line = lnr.readLine();
		}
	}
	
	//读取话务量文本数据
	public static void getTrafficFromLine(int hour)throws IOException
	{
		traffic1 = line.trim().split(" +");
		skipLine(1);
		traffic2 = line.trim().split(" +");

		
		//因为traffic2参数在入局话务量和出局话务量中的数量不同，通过此参数的数量进行判断，决定写入哪张表中
		if (traffic2.length == 4)
			{
				storageToDatabase("inc",trafficDate,beginTimeInt,endTimeInt,traffic1,traffic2,hour);
				MyWindowDemo.tbLabel.setText("写入入局话务量至数据库成功！");
				//System.out.println("这条话务量是入局");
			}
		else if (traffic2.length == 5)
			
			{
				storageToDatabase("out",trafficDate,beginTimeInt,endTimeInt,traffic1,traffic2,hour);
				MyWindowDemo.tbLabel.setText("写入出局话务量至数据库成功！");
				//System.out.println("这条话务量是出局");
			}
		
	}
	
	//存储至数据库
	private static void storageToDatabase(String incout,String trafficDate,int beginTimeInt,int endTimeInt,String[] traffic1,String[] traffic2,int hour) 
	{
		
		//System.out.println("此次话务统计日期为"+trafficDate+"时间为"+beginTimeInt+"至"+endTimeInt+"时,\r\n统计单位为"+traffic1[0]);
		
		//此时需要将date格式2014-sep-02转为2014-09-02
		//这个函数的hour参数用来判断文件中读取的时间是否与当前时间相同（杜绝每小时读取话务量文件而导致的重复写入数据库行为）
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
			//如果该字段为inc，则判断为入局话务量，写入入局话务量数据表。
			
			String query = "insert ignore into statistics_inc (ID,Time,Assgn,Avlb,Seiz,Answ,Occ,Convocc,Attlocal,Atttrans,Answlocal,Answtrans) values('"+traffic1[0]+"','"+trafficDateTime+"','"+traffic1[1]+"','"+traffic1[2]+"',"
					+ "'"+traffic1[3]+"','"+traffic1[4]+"','"+traffic1[5]+"','"+traffic1[6]+"',"
					+ "'"+traffic2[0]+"','"+traffic2[1]+"','"+traffic2[2]+"','"+traffic2[3]+"')";
			//查询用户选择的数据表
			try {
				if((hour == -1)||(hour != 0 && hour-1 == beginTimeInt))
				stmt.executeUpdate(query);
			} catch (SQLException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			
		}
		
		else if (incout.equals("out"))
		{
			//出局话务量，写入出局话务量表
			String query = "insert ignore into statistics_out(ID,Time,Assgn,Avlb,Seiz,Answ,Occ,Convocc,Callatt,Thrsw,Retries,Noansw,Congdist) values('"+traffic1[0]+"','"+trafficDateTime+"','"+traffic1[1]+"','"+traffic1[2]+"',"
					+ "'"+traffic1[3]+"','"+traffic1[4]+"','"+traffic1[5]+"','"+traffic1[6]+"',"
					+ "'"+traffic2[0]+"','"+traffic2[1]+"','"+traffic2[2]+"','"+traffic2[3]+"','"+traffic2[4]+"')";
			//查询用户选择的数据表
			try {
				if ((hour == -1)||(hour != 0 && hour-1 == beginTimeInt))
				stmt.executeUpdate(query);
			} catch (SQLException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		else
			WriteLog.WriteLogFile("storageToDatabase函数错误");
		
			
	}
	
	
	public static void readTrafficFile(String strfilename,int hour)
	{
	
			try {
				fr = new FileReader(strfilename);
			} catch (FileNotFoundException e1) {
				// TODO 自动生成的 catch 块
				//JOptionPane.showMessageDialog(null, "日志文件未找到！", "警告", JOptionPane.INFORMATION_MESSAGE); 
				MyWindowDemo.tbLabel.setText("打开文件  "+strfilename+"  失败，请核实配置文件中的日志目录！");
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
								System.out.println(lnr.getLineNumber()+":"+"假的Part1找到了");
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
								System.out.println(lnr.getLineNumber()+":"+"假的Part2找到了");
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
								System.out.println(lnr.getLineNumber()+":"+"假的Part3找到了");
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
								System.out.println(lnr.getLineNumber()+":"+"假的Part4找到了");
							}
						}
					}
					
				}
			} catch (IOException e1) {
				// TODO 自动生成的 catch 块
				e1.printStackTrace();
			}
	}
	
}
