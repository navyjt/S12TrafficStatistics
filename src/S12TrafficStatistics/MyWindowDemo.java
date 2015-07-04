/**
 * Description:
 * <br/>Copyright (C)
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:S1240话务量监控系统
 * <br/>Date: 2014-10-08
 * @author  姜涛 	navyjt@163.com
 * @version  1.0
 */
package S12TrafficStatistics;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.Calendar;
import java.util.TimerTask;
import java.util.Timer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.event.*;
import java.awt.FileDialog;

public class MyWindowDemo
{
	static JFrame f = new JFrame("S1240话务量监控系统");
	private static ResultSetTableModel model;
	private JButton inquery = new JButton("入局话务量查询");
	private JButton outquery = new JButton("出局话务量查询");
	private JButton conquery = new JButton("条件查询");
	private static JScrollPane scrollPane;
	private static JScrollPane outscrollPane;
	private static JScrollPane conscrollPane;
	
	//判断当前计时器选择的对象
	private static int timerSelected;
	
	private static ResultSet rs;
	private static ResultSet outrs;
	private static ResultSet conrs;
	private static Connection conn;
	private static Statement stmt;
	private static Statement outstmt;
	private static Statement constmt;
	private static FileDialog openDia;
	public static JLabel tbLabel;
	private static JPanel outPanel = new JPanel();
	private static JPanel incPanel = new JPanel();
	private static JPanel conPanel = new JPanel();
	
	private static JComboBox<String> inoutselected = new JComboBox<String>();
	private static JComboBox<String> IDQuery = new JComboBox<String>() ;
	private static JComboBox<String> timeselected = new JComboBox<String>();
		

	public void init() throws ClassNotFoundException, SQLException, IOException
	{
		Font font = new Font("微软雅黑",Font.PLAIN,13);
		
		//确保程序的UI显示一致
		UIManager.put("ToolTip.font",font);
		UIManager.put("Table.font",font);
		UIManager.put("TableHeader.font",font); 
		UIManager.put("TextField.font",font); 
		UIManager.put("ComboBox.font",font); 
		UIManager.put("TextField.font",font); 
		UIManager.put("PasswordField.font",font); 
		UIManager.put("TextArea.font",font); 
		UIManager.put("TextPane.font",font); 
		UIManager.put("EditorPane.font",font); 
		UIManager.put("FormattedTextField.font",font); 
		UIManager.put("Button.font",font); 
		UIManager.put("CheckBox.font",font); 
		UIManager.put("RadioButton.font",font); 
		UIManager.put("ToggleButton.font",font); 
		UIManager.put("ProgressBar.font",font); 
		UIManager.put("DesktopIcon.font",font); 
		UIManager.put("TitledBorder.font",font); 
		UIManager.put("Label.font",font); 
		UIManager.put("List.font",font); 
		UIManager.put("TabbedPane.font",font); 
		UIManager.put("MenuBar.font",font); 
		UIManager.put("Menu.font",font); 
		UIManager.put("MenuItem.font",font); 
		UIManager.put("PopupMenu.font",font); 
		UIManager.put("CheckBoxMenuItem.font",font); 
		UIManager.put("RadioButtonMenuItem.font",font); 
		UIManager.put("Spinner.font",font); 
		UIManager.put("Tree.font",font); 
		UIManager.put("ToolBar.font",font); 
		UIManager.put("OptionPane.messageFont",font); 
		UIManager.put("OptionPane.buttonFont",font);
     
		//创建一个状态栏
		JToolBar tb = new JToolBar();
		tbLabel = new JLabel("S1240话务量监控软件初始化完成");
		tb.add(tbLabel);
		tb.setFloatable(false);
		f.add(tb,BorderLayout.SOUTH);
		
		//添加tabpanel
		JPanel outtop = new JPanel();
		JPanel inctop = new JPanel();
		JPanel contop = new JPanel();
				
		inctop.add(inquery);
		outtop.add(outquery);
	
		
		//以下代码为查询panel中相应的组件
/*		Vector<String> inoutselection = new Vector<String>();
		inoutselection.add("------选择话务量方向------");
		inoutselection.add("入局话务量查询");
		inoutselection.add("出局话务量查询");
		inoutselected = new JComboBox(inoutselection);
		inoutselected.setMaximumRowCount(3);
		inoutselected.setVisible(true);*/
		

		inoutselected.addItem("------选择话务量方向------");
		inoutselected.addItem("入局话务量查询");
		inoutselected.addItem("出局话务量查询");
		inoutselected.setVisible(true);
		
		
		IDQuery.addItem("------选择局向------");
		IDQuery.setVisible(true);
		
		timeselected.addItem("------选择查询时间------");
		timeselected.addItem("最近一天");
		timeselected.addItem("最近三天");
		timeselected.addItem("最近一周");
		timeselected.setVisible(true);
	
	
		contop.add(inoutselected);
		contop.add(IDQuery);
		contop.add(timeselected);
		contop.add(conquery);
		
		incPanel.add(inctop,  BorderLayout.NORTH);
		outPanel.add(outtop,  BorderLayout.NORTH);
		conPanel.add(contop,  BorderLayout.NORTH);
		JTabbedPane tabPane = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);
		tabPane.addTab("入局话务量实时观察",null,incPanel,null);
		tabPane.addTab("出局话务量实时观察",null,outPanel,null);
		tabPane.addTab("话务量自定义查询",null,conPanel,null);
	
		//添加菜单
		JMenuBar mb = new JMenuBar();
		Icon newIcon = new ImageIcon("ico/new.png");
		JMenu file = new JMenu("系统配置");
		JMenuItem newItem = new JMenuItem("数据导入" , newIcon);
		Icon saveIcon = new ImageIcon("ico/save.png");
		JMenuItem confItem = new JMenuItem("局向配置" , saveIcon);
		Icon exitIcon = new ImageIcon("ico/exit.png");
		JMenuItem exitItem = new JMenuItem("退出" , exitIcon);	
		JMenuItem logItem = new JMenuItem("操作日志");
		file.add(newItem);
		file.add(confItem);
		file.add(logItem);
		file.add(exitItem);
		//将file菜单添加到mb菜单条中
		mb.add(file);
		//为f窗口设置菜单条
		f.setJMenuBar(mb);
	
		f.add(tabPane, BorderLayout.CENTER);
		//确保窗体启动是最大化
		Toolkit toolkit= Toolkit.getDefaultToolkit();
		Dimension scmSize = toolkit.getScreenSize();
		int taskBarHeight = toolkit.getScreenInsets(f.getGraphicsConfiguration()).bottom;
		f.setBounds(0, 0, (int)(scmSize.getWidth()), (int)((scmSize.getHeight())-taskBarHeight));
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		f.setResizable(false);
		f.validate();
		
		//设置关闭窗口时，退出程序
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//初次启动时即启动定时器
		String strManner = confFile.getImportManner();
		ImportTrafficManner(strManner, false);
	
		//日志对话框
		JTextArea logta = new JTextArea ();
		JDialog logDiag = new JDialog(f,"操作日志",false);
		logDiag.add(logta);
		logDiag.setSize(800, 600);
		logDiag.setLocationRelativeTo(null);
		
		//-----------下面开始组合菜单、并为菜单添加事件监听器----------
		//为newItem设置快捷键，设置快捷键时要使用大写字母
		newItem.setAccelerator(KeyStroke.getKeyStroke('N' , InputEvent.CTRL_MASK)); 
		newItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
						new DataImportDiag(f,"读取文件",false);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
		}
		});
		
		tabPane.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO 自动生成的方法存根
				if ( tabPane.getSelectedIndex() == 1)
					outPanel.setVisible(true);
					
				else if ( tabPane.getSelectedIndex() == 2)
					conPanel.setVisible(true);
			}
		});
		//入局话务量查询页面
		inquery.addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				inquerybtnClicked();
			}
			
		});
		//出局话务量查询页面
		outquery.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{	
				outquerybtnClicked();
			}
			}
			
		);
		//自定义话务量查询页面
		conquery.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				conquerybtnClicked();
			}
				
			}
			
		);
		
		
		confItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new ExchangeQueryDiag("读取文件",false);
				
			}
			
		});
		logItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
			//	logDiag.pack();
				logDiag.setVisible(true);
				logta.setText("");
				logta.setEditable(false);
				FileReader fr = null;
	
				try {
						fr = new FileReader("traffic.log");
						BufferedReader br = new BufferedReader(fr);
						while(br.ready())
						{
							logta.append(br.readLine()+"\r\n");
						}
						br.close();
						fr.close();
						
					}
	
					catch (IOException e1) {
						// TODO 自动生成的 catch 块
						e1.printStackTrace();
						WriteLog.WriteLogFile("异常：打开文件traffic.log错误");
					}
			}
		});
		exitItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		
		inoutselected.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (inoutselected.getSelectedIndex() == 1)
				{
					try
					{
						IDQuery.removeAllItems();
						//获取数据库连接
						conn = confFile.getConnection();
						stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
						String query = "select distinct ID from statistics_inc"; 
						if (rs != null) 
						{
							rs.close();
						}
						rs = stmt.executeQuery(query);
						
						while (rs.next())
						{
							IDQuery.addItem(rs.getString(1));
						}
					}

					catch (Exception e1)
					{  
						e1.printStackTrace();
					}
				}
				if (inoutselected.getSelectedIndex() == 2)
				{
					try
					{
						IDQuery.removeAllItems();
						//获取数据库连接
						conn = confFile.getConnection();
						stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
						String query = "select distinct ID from statistics_out"; 
						if (rs != null) 
						{
							rs.close();
						}
						rs = stmt.executeQuery(query);
						
						while (rs.next())
						{
							IDQuery.addItem(rs.getString(1));
						}
					}

					catch (Exception e1)
					{  
						e1.printStackTrace();
					}
				}
			}
		}
		);
	}


	//模拟出局话务量查询按钮点击动作
	public static void outquerybtnClicked() {
		
		if (!confFile.getImportManner().equals("hour"))
		{
			JOptionPane.showMessageDialog(null, "实时话务量观察只在数据导入方式为每小时导入一次数据时使用，请到数据导入菜单项进行相关设置！", "警告", JOptionPane.INFORMATION_MESSAGE); 
			return;
		}
				
		try
		{
			//获取数据库连接
			conn = confFile.getConnection();
			//创建Statement
			outstmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		}

		catch (Exception e1)
		{  
			e1.printStackTrace();
		}
				
		//获取数据并填充至表格中
		try
		{
					//如果装载JTable的JScrollPane不为空
					if (outscrollPane != null)
					{
						//从主窗口中删除表格
						outPanel.remove(outscrollPane);
					}
					//如果结果集不为空，则关闭结果集
					if (outrs != null) 
					{
						outrs.close();
					}
					String query = "select alias.Name,alias.ID,alias.Signal,alias.Location ,statistics_out.Time,statistics_out.Assgn,statistics_out.Avlb,statistics_out.Seiz, "
							+ "statistics_out.Answ,statistics_out.Occ,statistics_out.Convocc,statistics_out.Callatt,statistics_out.Thrsw,"
							+ "statistics_out.Retries,statistics_out.Noansw,statistics_out.Congdist "
							+ "from statistics_out,alias "
							+ "WHERE UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(statistics_out.Time)<=3600 and statistics_out.ID=alias.ID ;";
					//查询用户选择的数据表
					
					outrs = outstmt.executeQuery(query);
					//使用查询到的ResultSet创建TableModel对象
					model = new ResultSetTableModel(outrs,false);
					//为TableModel添加监听器，监听用户的修改
					model.addTableModelListener(new TableModelListener ()
					{

						public void tableChanged(TableModelEvent evt)
						{
							
							int row = evt.getFirstRow();
							int column = evt.getColumn();
							new WriteLog("修改的列:" + column  + " ，修改的行:" + row+ " ，修改后的值:" + model.getValueAt(row , column));
						}
					});
					//使用TableModel创建JTable，并将对应表格添加到窗口中
					Object[] columnTitle = {"局向名称","局向ID","中继方式","局向类型","统计时间","中继分配条数","中继可用条数","占用次数","应答次数","占用话务量","通话话务量","试呼次数","接通次数"
							,"中继重选次数","无应答次数","被叫忙次数"};
					JTable table = new JTable(model);
					for (int i = 0;i<table.getColumnCount();i++ )
					{
						table.getColumnModel().getColumn(i).setHeaderValue(columnTitle[i]);
					}
					outscrollPane = new JScrollPane(table);
					outscrollPane.setPreferredSize(new Dimension(outPanel.getWidth()-30,outPanel.getHeight()-50));
					outscrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
					outPanel.add(outscrollPane,BorderLayout.CENTER);
					f.validate();
					

				}            
				catch (SQLException e3)
				{  
					e3.printStackTrace();
				}
		}
			
		//模拟入局话务量查询按钮点击动作
	public static void inquerybtnClicked(){
		if (!confFile.getImportManner().equals("hour"))
		{
			JOptionPane.showMessageDialog(null, "实时话务量观察只在数据导入方式为每小时导入一次数据时使用，请到数据导入菜单项进行相关设置！", "警告", JOptionPane.INFORMATION_MESSAGE); 
			return;
		}
			
		
		try
		{
			//获取数据库连接
			conn = confFile.getConnection();
			//创建Statement
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		}

		catch (Exception e1)
		{  
			e1.printStackTrace();
		}
		
		//获取数据并填充至表格中
		
		try
		{
			//如果装载JTable的JScrollPane不为空
			if (scrollPane != null)
			{
				//从主窗口中删除表格
				incPanel.remove(scrollPane);
			}
			//如果结果集不为空，则关闭结果集
			if (rs != null) 
			{
				rs.close();
			}
			String query = "select alias.Name,alias.ID,alias.Signal,alias.Location ,statistics_inc.Time,statistics_inc.Assgn,statistics_inc.Avlb,statistics_inc.Seiz, "
					+ "statistics_inc.Answ,statistics_inc.Occ,statistics_inc.Convocc,statistics_inc.Attlocal,statistics_inc.Atttrans,"
					+ "statistics_inc.Answlocal,statistics_inc.Answtrans "
					+ "from statistics_inc,alias "
					+ "WHERE UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(statistics_inc.Time)<=3600 and statistics_inc.ID=alias.ID ;";
			//查询用户选择的数据表
			
			rs = stmt.executeQuery(query);
			//使用查询到的ResultSet创建TableModel对象
			model = new ResultSetTableModel(rs,false);
			//为TableModel添加监听器，监听用户的修改
			model.addTableModelListener(new TableModelListener ()
			{

				public void tableChanged(TableModelEvent evt)
				{
					
					int row = evt.getFirstRow();
					int column = evt.getColumn();
					new WriteLog("修改的列:" + column  + " ，修改的行:" + row+ " ，修改后的值:" + model.getValueAt(row , column));
				}
			});
			//使用TableModel创建JTable，并将对应表格添加到窗口中
			Object[] columnTitle = {"局向名称","局向ID","中继方式","局向类型","统计时间","中继分配条数","中继可用条数","占用次数","应答次数","占用话务量","通话话务量","落地试呼次数","转话试呼次数"
					,"落地应答次数","转话应答次数"};
			JTable table = new JTable(model);
			for (int i = 0;i<table.getColumnCount();i++ )
			{
				table.getColumnModel().getColumn(i).setHeaderValue(columnTitle[i]);
			}

			scrollPane = new JScrollPane(table);
			scrollPane.setPreferredSize(new Dimension(incPanel.getWidth()-30,incPanel.getHeight()-50));
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			incPanel.add(scrollPane,BorderLayout.CENTER);
			f.validate();
		

		}            
		catch (SQLException e3)
		{  
			e3.printStackTrace();
		}
	}
			

	public static void conquerybtnClicked()
	{
		try
		{
			//获取数据库连接
			conn = confFile.getConnection();
			//创建Statement
			constmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		}

		catch (Exception e1)
		{  
			e1.printStackTrace();
		}
		
		//获取数据并填充至表格中
		try
		{
				//如果装载JTable的JScrollPane不为空
				if (conscrollPane != null)
				{
					//从主窗口中删除表格
					conPanel.remove(conscrollPane);
				}
				//如果结果集不为空，则关闭结果集
				if (conrs != null) 
				{
					conrs.close();
				}
				
				if (inoutselected.getSelectedIndex() == 0)
				{
					JOptionPane.showMessageDialog(null, "请选择查询的话务量方向！", "警告", JOptionPane.INFORMATION_MESSAGE); 
				}
				else if (inoutselected.getSelectedIndex() == 1)
				{
					String ID = (String)IDQuery.getSelectedItem();
					String TimeSel = "";
					//此处设定选择的时间范围，一天三天或者一周
					if (timeselected.getSelectedIndex() == 1)
					{
						TimeSel = " WHERE UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(statistics_inc.Time)<=86400 ";
					}
					else if (timeselected.getSelectedIndex() == 2)
					{
						TimeSel = " WHERE UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(statistics_inc.Time)<=259200 ";
					}
					else if (timeselected.getSelectedIndex() == 3)
					{
						TimeSel = " WHERE UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(statistics_inc.Time)<=60480000 ";
					}
					else
					{
						JOptionPane.showMessageDialog(null, "请选择查询时间！", "警告", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					try
					{
						//如果装载JTable的JScrollPane不为空
						if (conscrollPane != null)
						{
							//从主窗口中删除表格
							conPanel.remove(conscrollPane);
						}
						//如果结果集不为空，则关闭结果集
						if (rs != null) 
						{
							rs.close();
						}
						String query = "select alias.Name,alias.ID,alias.Signal,alias.Location ,statistics_inc.Time,statistics_inc.Assgn,statistics_inc.Avlb,statistics_inc.Seiz, "
								+ "statistics_inc.Answ,statistics_inc.Occ,statistics_inc.Convocc,statistics_inc.Attlocal,statistics_inc.Atttrans,"
								+ "statistics_inc.Answlocal,statistics_inc.Answtrans "
								+ "from statistics_inc,alias "
								+ TimeSel+ "and statistics_inc.ID=alias.ID and alias.ID ='"+ID+"';";
								//+ "WHERE UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(statistics_inc.Time)<=3600000000 and statistics_inc.ID=alias.ID and alias.ID ='"+ID+"';";
						System.out.println(query);
						//查询用户选择的数据表
							
						conrs = constmt.executeQuery(query);
						//使用查询到的ResultSet创建TableModel对象
						model = new ResultSetTableModel(conrs,false);
						//为TableModel添加监听器，监听用户的修改
						model.addTableModelListener(new TableModelListener ()
						{

							public void tableChanged(TableModelEvent evt)
							{
								
								int row = evt.getFirstRow();
								int column = evt.getColumn();
								new WriteLog("修改的列:" + column  + " ，修改的行:" + row+ " ，修改后的值:" + model.getValueAt(row , column));
							}
						});
						//使用TableModel创建JTable，并将对应表格添加到窗口中
						Object[] columnTitle = {"局向名称","局向ID","中继方式","局向类型","统计时间","中继分配条数","中继可用条数","占用次数","应答次数","占用话务量","通话话务量","落地试呼次数","转话试呼次数"
								,"落地应答次数","转话应答次数"};
						JTable table = new JTable(model);
						for (int i = 0;i<table.getColumnCount();i++ )
						{
							table.getColumnModel().getColumn(i).setHeaderValue(columnTitle[i]);
						}

						conscrollPane = new JScrollPane(table);
						conscrollPane.setPreferredSize(new Dimension(conPanel.getWidth()-30,conPanel.getHeight()-50));
						conscrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
						conPanel.add(conscrollPane,BorderLayout.CENTER);
						f.validate();
					
						
						   //实现字体颜色的代码  
				        //获得某一列的tableColumn，tableColumn是一列的管理器  
				        TableColumn tableColumn = table.getColumn("局向ID");  
				        //初始化table的渲染器  
				        DefaultTableCellRenderer cellRanderer = new DefaultTableCellRenderer();  
				        //设置前景色也就是字体颜色  
				        cellRanderer.setForeground(Color.RED);  
				        //将上面的渲染器对象放到tableColumn中  
				        tableColumn.setCellRenderer(cellRanderer); 
				       
					}            
					catch (SQLException e3)
					{  
						e3.printStackTrace();
					}
					return;
				}
				
				else if (inoutselected.getSelectedIndex() == 2)
				{
					String ID = (String)IDQuery.getSelectedItem();
					String TimeSel = "";
					
					if (timeselected.getSelectedIndex() == 1)
					{
						TimeSel = " WHERE UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(statistics_out.Time)<=86400 ";
					}
					else if (timeselected.getSelectedIndex() == 2)
					{
						TimeSel = " WHERE UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(statistics_out.Time)<=259200 ";
					}
					else if (timeselected.getSelectedIndex() == 3)
					{
						TimeSel = " WHERE UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(statistics_out.Time)<=60480000 ";
					}
					else
					{
						JOptionPane.showMessageDialog(null, "请选择查询时间！", "警告", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					try
					{
						//如果装载JTable的JScrollPane不为空
						if (conscrollPane != null)
						{
							//从主窗口中删除表格
							conPanel.remove(conscrollPane);
						}
						//如果结果集不为空，则关闭结果集
						if (rs != null) 
						{
							rs.close();
						}
						String query = "select alias.Name,alias.ID,alias.Signal,alias.Location ,statistics_out.Time,statistics_out.Assgn,statistics_out.Avlb,statistics_out.Seiz, "
								+ "statistics_out.Answ,statistics_out.Occ,statistics_out.Convocc,statistics_out.Callatt,statistics_out.Thrsw,"
								+ "statistics_out.Retries,statistics_out.Noansw,statistics_out.Congdist "
								+ "from statistics_out,alias "
								+ TimeSel +" and statistics_out.ID=alias.ID and alias.ID ='"+ID+"';";
								// + "WHERE UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(statistics_inc.Time)<=36000000 and statistics_inc.ID=alias.ID and alias.ID ='"+ID+"';";
						//查询用户选择的数据表
							
						conrs = constmt.executeQuery(query);
						//使用查询到的ResultSet创建TableModel对象
						model = new ResultSetTableModel(conrs,false);
						//为TableModel添加监听器，监听用户的修改
						model.addTableModelListener(new TableModelListener ()
						{

							public void tableChanged(TableModelEvent evt)
							{
								
								int row = evt.getFirstRow();
								int column = evt.getColumn();
								new WriteLog("修改的列:" + column  + " ，修改的行:" + row+ " ，修改后的值:" + model.getValueAt(row , column));
							}
						});
						//使用TableModel创建JTable，并将对应表格添加到窗口中
						Object[] columnTitle = {"局向名称","局向ID","中继方式","局向类型","统计时间","中继分配条数","中继可用条数","占用次数","应答次数","占用话务量","通话话务量","试呼次数","接通次数"
								,"中继重选次数","无应答次数","被叫忙次数"};
						JTable table = new JTable(model);
						for (int i = 0;i<table.getColumnCount();i++ )
						{
							table.getColumnModel().getColumn(i).setHeaderValue(columnTitle[i]);
						}

						conscrollPane = new JScrollPane(table);
						conscrollPane.setPreferredSize(new Dimension(conPanel.getWidth()-30,conPanel.getHeight()-50));
						conscrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
						conPanel.add(conscrollPane,BorderLayout.CENTER);
						f.validate();
					

					}            
					catch (SQLException e3)
					{  
						e3.printStackTrace();
					}
					return;
				}
				
				else 
					return ;
					
			}            
			catch (SQLException e3)
			{  
				e3.printStackTrace();
			}
	}

	public static void main(String[] args) 
	{
		JFrame.setDefaultLookAndFeelDecorated(false);
		try {
			new MyWindowDemo().init();
		} catch (ClassNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	public static void ImportTrafficManner(String str,Boolean isChanged) 
	{
	       //设置执行时间
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);//每天
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int tinyyear = year%100;
       	Timer timerofHour = new Timer();
        Timer timerofDay = new Timer();
        int accmonth = month+1;
        String strMonth = accmonth > 9 ? (accmonth+"") : ("0" + (accmonth+""));
        String strDay = day > 9 ? (day+"") : ("0" + (day+""));
       
        String logPath = confFile.getLogDir();
		String logFilename =logPath.concat(tinyyear+"").concat(strMonth).concat(strDay).concat("A.LOG");              
		if (str.equals("day"))
		{
			timerSelected = 0;
	        //定制每天的00:10:00执行，
	        calendar.set(year, month, day+1, 0, 10, 00);	//@20141025 原来为day-1，疑似错误，改为day+1
	        java.util.Date date = calendar.getTime();

	        TimerTask task = new TimerTask(){ 
	        	public void run() {
	        		//安装当前日期构建file文件名。
	        		if (timerSelected != 0)
	        			cancel();
	        		else
	        		{
	           		 	DataImportDiag.readTrafficFile(logFilename,-1);
	        		}
	            }
	        
	        };
	        timerofDay.schedule(task, date);
			tbLabel.setText("当前话务量读取模式：每日一次，读取时间为凌晨00:10,读取文件 "+logFilename);
		}
		else if (str.equals("hour"))
		{
			timerSelected = 1;
			//定制每小时的10:00执行，一小时执行一次
	        calendar.set(year, month, day, hour, 10, 00);
	        java.util.Date date = calendar.getTime();
	        int period = 3600*1000;

	        TimerTask task = new TimerTask(){ 
	        	public void run() {
	            	if (timerSelected != 1)
	        			cancel();	
	        		else
	        		{
	        			
	  	              //读取traffic文件
	        			DataImportDiag.readTrafficFile(logFilename,hour);
	  	        		WriteLog.WriteLogFile("这个东东一直在执行，5秒钟一次啊！");
	  	        		inquerybtnClicked();
	  	        		outquerybtnClicked();
	        		}
	            }
	        };
	  			timerofHour.schedule(task, date,period);
	  			tbLabel.setText("当前话务量读取模式：每小时一次，读取文件 "+logFilename);
		}
		//这里通过ischanged判断是否是界面初次打开或者通过数据导入对话框点击，若为界面初次打开，则手动打开话务量文件无效，
		//若为通过数据导入对话框打开，则此时弹出打开文件对话框
		else if (str.equals("manual"))
		{
			if(isChanged)
			{
				timerSelected = 2;
				openDia = new FileDialog(DataImportDiag.f,"导入话务量文件",FileDialog.LOAD);
				openDia.setVisible(true);
				String dirPath = openDia.getDirectory();
				String fileName = openDia.getFile();
				DataImportDiag.readTrafficFile(dirPath+fileName,-1);
				WriteLog.WriteLogFile("全时手动！"+dirPath+fileName);
			}
			else
				tbLabel.setText("当前话务量读取模式：手动读取！");

		}
		else
		{
			WriteLog.WriteLogFile("计时器设置错误，当前计时器为"+str+",isChanged参数为"+isChanged);
		}

		
	}

	public FileDialog getOpenDia() {
		return openDia;
	}



}

class ResultSetTableModel extends AbstractTableModel
{  
	private static final long serialVersionUID = 1L;
	private ResultSet rs;
	private ResultSetMetaData rsmd;
	private boolean isCellEditable;
	//构造器，初始化rs和rsmd两个属性
	public ResultSetTableModel(ResultSet aResultSet,Boolean isEditable)
	{
		isCellEditable = isEditable;
		rs = aResultSet;
		try
		{  
			rsmd = rs.getMetaData();
		}
		catch (SQLException e)
		{  
			e.printStackTrace();
		}
	}

	//重写getColumnName方法，用于为该TableModel设置列名
	public String getColumnName(int c)
	{  
		try
		{  
			return rsmd.getColumnName(c + 1);
		}
		catch (SQLException e)
		{  
			e.printStackTrace();
			return "";
		}
	}
	//重写getColumnCount方法，用于设置该TableModel的列数
	public int getColumnCount()
	{  
		try
		{  
			return rsmd.getColumnCount();
		}
		catch (SQLException e)
		{  
			e.printStackTrace();
			return 0;
		}
	}
	//重写getValueAt方法，用于设置该TableModel指定单元格的值
	public Object getValueAt(int r, int c)
	{  
		try
		{  
			rs.absolute(r + 1);
			return rs.getObject(c + 1);
		}
		catch(SQLException e)
		{  
			e.printStackTrace();
			return null;
		}
	}
	//重写getColumnCount方法，用于设置该TableModel的行数
	public int getRowCount()
	{  
		try
		{  
			rs.last();
			return rs.getRow();
		}
		catch(SQLException e)
		{  
			e.printStackTrace();
			return 0;
		}
	}
	//重写isCellEditable返回true，让每个单元格可编辑，此代码中设置为不可修改
	public boolean isCellEditable(int rowIndex, int columnIndex) 
	{
		if(isCellEditable)
			return true;
		else
		{
			//JOptionPane.showMessageDialog(null, "该表格不允许进行修改！", "警告", JOptionPane.INFORMATION_MESSAGE); 
			return false;
		}
	
	}
	//重写setValueAt方法，用于实现用户编辑单元格时，程序做出对应的动作
	public void setValueAt(Object aValue,
		int row,int column)
	{
		try
		{
			//结果集定位到对应的行数
			rs.absolute(row + 1);
			//修改单元格多对应的值
			rs.updateObject(column + 1 , aValue);
			//提交修改
			rs.updateRow();
			//触发单元格的修改事件
			fireTableCellUpdated(row, column);
		}
		catch (SQLException evt)
		{
			evt.printStackTrace();
		}
	}
	
}