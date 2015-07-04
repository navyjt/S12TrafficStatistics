/**
 * Description:
 * <br/>Copyright (C)
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:S1240���������ϵͳ
 * <br/>Date: 2014-10-08
 * @author  ���� 	navyjt@163.com
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
	static JFrame f = new JFrame("S1240���������ϵͳ");
	private static ResultSetTableModel model;
	private JButton inquery = new JButton("��ֻ�������ѯ");
	private JButton outquery = new JButton("���ֻ�������ѯ");
	private JButton conquery = new JButton("������ѯ");
	private static JScrollPane scrollPane;
	private static JScrollPane outscrollPane;
	private static JScrollPane conscrollPane;
	
	//�жϵ�ǰ��ʱ��ѡ��Ķ���
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
		Font font = new Font("΢���ź�",Font.PLAIN,13);
		
		//ȷ�������UI��ʾһ��
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
     
		//����һ��״̬��
		JToolBar tb = new JToolBar();
		tbLabel = new JLabel("S1240��������������ʼ�����");
		tb.add(tbLabel);
		tb.setFloatable(false);
		f.add(tb,BorderLayout.SOUTH);
		
		//���tabpanel
		JPanel outtop = new JPanel();
		JPanel inctop = new JPanel();
		JPanel contop = new JPanel();
				
		inctop.add(inquery);
		outtop.add(outquery);
	
		
		//���´���Ϊ��ѯpanel����Ӧ�����
/*		Vector<String> inoutselection = new Vector<String>();
		inoutselection.add("------ѡ����������------");
		inoutselection.add("��ֻ�������ѯ");
		inoutselection.add("���ֻ�������ѯ");
		inoutselected = new JComboBox(inoutselection);
		inoutselected.setMaximumRowCount(3);
		inoutselected.setVisible(true);*/
		

		inoutselected.addItem("------ѡ����������------");
		inoutselected.addItem("��ֻ�������ѯ");
		inoutselected.addItem("���ֻ�������ѯ");
		inoutselected.setVisible(true);
		
		
		IDQuery.addItem("------ѡ�����------");
		IDQuery.setVisible(true);
		
		timeselected.addItem("------ѡ���ѯʱ��------");
		timeselected.addItem("���һ��");
		timeselected.addItem("�������");
		timeselected.addItem("���һ��");
		timeselected.setVisible(true);
	
	
		contop.add(inoutselected);
		contop.add(IDQuery);
		contop.add(timeselected);
		contop.add(conquery);
		
		incPanel.add(inctop,  BorderLayout.NORTH);
		outPanel.add(outtop,  BorderLayout.NORTH);
		conPanel.add(contop,  BorderLayout.NORTH);
		JTabbedPane tabPane = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);
		tabPane.addTab("��ֻ�����ʵʱ�۲�",null,incPanel,null);
		tabPane.addTab("���ֻ�����ʵʱ�۲�",null,outPanel,null);
		tabPane.addTab("�������Զ����ѯ",null,conPanel,null);
	
		//��Ӳ˵�
		JMenuBar mb = new JMenuBar();
		Icon newIcon = new ImageIcon("ico/new.png");
		JMenu file = new JMenu("ϵͳ����");
		JMenuItem newItem = new JMenuItem("���ݵ���" , newIcon);
		Icon saveIcon = new ImageIcon("ico/save.png");
		JMenuItem confItem = new JMenuItem("��������" , saveIcon);
		Icon exitIcon = new ImageIcon("ico/exit.png");
		JMenuItem exitItem = new JMenuItem("�˳�" , exitIcon);	
		JMenuItem logItem = new JMenuItem("������־");
		file.add(newItem);
		file.add(confItem);
		file.add(logItem);
		file.add(exitItem);
		//��file�˵���ӵ�mb�˵�����
		mb.add(file);
		//Ϊf�������ò˵���
		f.setJMenuBar(mb);
	
		f.add(tabPane, BorderLayout.CENTER);
		//ȷ���������������
		Toolkit toolkit= Toolkit.getDefaultToolkit();
		Dimension scmSize = toolkit.getScreenSize();
		int taskBarHeight = toolkit.getScreenInsets(f.getGraphicsConfiguration()).bottom;
		f.setBounds(0, 0, (int)(scmSize.getWidth()), (int)((scmSize.getHeight())-taskBarHeight));
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		f.setResizable(false);
		f.validate();
		
		//���ùرմ���ʱ���˳�����
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//��������ʱ��������ʱ��
		String strManner = confFile.getImportManner();
		ImportTrafficManner(strManner, false);
	
		//��־�Ի���
		JTextArea logta = new JTextArea ();
		JDialog logDiag = new JDialog(f,"������־",false);
		logDiag.add(logta);
		logDiag.setSize(800, 600);
		logDiag.setLocationRelativeTo(null);
		
		//-----------���濪ʼ��ϲ˵�����Ϊ�˵�����¼�������----------
		//ΪnewItem���ÿ�ݼ������ÿ�ݼ�ʱҪʹ�ô�д��ĸ
		newItem.setAccelerator(KeyStroke.getKeyStroke('N' , InputEvent.CTRL_MASK)); 
		newItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
						new DataImportDiag(f,"��ȡ�ļ�",false);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
		}
		});
		
		tabPane.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO �Զ����ɵķ������
				if ( tabPane.getSelectedIndex() == 1)
					outPanel.setVisible(true);
					
				else if ( tabPane.getSelectedIndex() == 2)
					conPanel.setVisible(true);
			}
		});
		//��ֻ�������ѯҳ��
		inquery.addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				inquerybtnClicked();
			}
			
		});
		//���ֻ�������ѯҳ��
		outquery.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{	
				outquerybtnClicked();
			}
			}
			
		);
		//�Զ��廰������ѯҳ��
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
				new ExchangeQueryDiag("��ȡ�ļ�",false);
				
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
						// TODO �Զ����ɵ� catch ��
						e1.printStackTrace();
						WriteLog.WriteLogFile("�쳣�����ļ�traffic.log����");
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
						//��ȡ���ݿ�����
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
						//��ȡ���ݿ�����
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


	//ģ����ֻ�������ѯ��ť�������
	public static void outquerybtnClicked() {
		
		if (!confFile.getImportManner().equals("hour"))
		{
			JOptionPane.showMessageDialog(null, "ʵʱ�������۲�ֻ�����ݵ��뷽ʽΪÿСʱ����һ������ʱʹ�ã��뵽���ݵ���˵������������ã�", "����", JOptionPane.INFORMATION_MESSAGE); 
			return;
		}
				
		try
		{
			//��ȡ���ݿ�����
			conn = confFile.getConnection();
			//����Statement
			outstmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		}

		catch (Exception e1)
		{  
			e1.printStackTrace();
		}
				
		//��ȡ���ݲ�����������
		try
		{
					//���װ��JTable��JScrollPane��Ϊ��
					if (outscrollPane != null)
					{
						//����������ɾ�����
						outPanel.remove(outscrollPane);
					}
					//����������Ϊ�գ���رս����
					if (outrs != null) 
					{
						outrs.close();
					}
					String query = "select alias.Name,alias.ID,alias.Signal,alias.Location ,statistics_out.Time,statistics_out.Assgn,statistics_out.Avlb,statistics_out.Seiz, "
							+ "statistics_out.Answ,statistics_out.Occ,statistics_out.Convocc,statistics_out.Callatt,statistics_out.Thrsw,"
							+ "statistics_out.Retries,statistics_out.Noansw,statistics_out.Congdist "
							+ "from statistics_out,alias "
							+ "WHERE UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(statistics_out.Time)<=3600 and statistics_out.ID=alias.ID ;";
					//��ѯ�û�ѡ������ݱ�
					
					outrs = outstmt.executeQuery(query);
					//ʹ�ò�ѯ����ResultSet����TableModel����
					model = new ResultSetTableModel(outrs,false);
					//ΪTableModel��Ӽ������������û����޸�
					model.addTableModelListener(new TableModelListener ()
					{

						public void tableChanged(TableModelEvent evt)
						{
							
							int row = evt.getFirstRow();
							int column = evt.getColumn();
							new WriteLog("�޸ĵ���:" + column  + " ���޸ĵ���:" + row+ " ���޸ĺ��ֵ:" + model.getValueAt(row , column));
						}
					});
					//ʹ��TableModel����JTable��������Ӧ�����ӵ�������
					Object[] columnTitle = {"��������","����ID","�м̷�ʽ","��������","ͳ��ʱ��","�м̷�������","�м̿�������","ռ�ô���","Ӧ�����","ռ�û�����","ͨ��������","�Ժ�����","��ͨ����"
							,"�м���ѡ����","��Ӧ�����","����æ����"};
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
			
		//ģ����ֻ�������ѯ��ť�������
	public static void inquerybtnClicked(){
		if (!confFile.getImportManner().equals("hour"))
		{
			JOptionPane.showMessageDialog(null, "ʵʱ�������۲�ֻ�����ݵ��뷽ʽΪÿСʱ����һ������ʱʹ�ã��뵽���ݵ���˵������������ã�", "����", JOptionPane.INFORMATION_MESSAGE); 
			return;
		}
			
		
		try
		{
			//��ȡ���ݿ�����
			conn = confFile.getConnection();
			//����Statement
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		}

		catch (Exception e1)
		{  
			e1.printStackTrace();
		}
		
		//��ȡ���ݲ�����������
		
		try
		{
			//���װ��JTable��JScrollPane��Ϊ��
			if (scrollPane != null)
			{
				//����������ɾ�����
				incPanel.remove(scrollPane);
			}
			//����������Ϊ�գ���رս����
			if (rs != null) 
			{
				rs.close();
			}
			String query = "select alias.Name,alias.ID,alias.Signal,alias.Location ,statistics_inc.Time,statistics_inc.Assgn,statistics_inc.Avlb,statistics_inc.Seiz, "
					+ "statistics_inc.Answ,statistics_inc.Occ,statistics_inc.Convocc,statistics_inc.Attlocal,statistics_inc.Atttrans,"
					+ "statistics_inc.Answlocal,statistics_inc.Answtrans "
					+ "from statistics_inc,alias "
					+ "WHERE UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(statistics_inc.Time)<=3600 and statistics_inc.ID=alias.ID ;";
			//��ѯ�û�ѡ������ݱ�
			
			rs = stmt.executeQuery(query);
			//ʹ�ò�ѯ����ResultSet����TableModel����
			model = new ResultSetTableModel(rs,false);
			//ΪTableModel��Ӽ������������û����޸�
			model.addTableModelListener(new TableModelListener ()
			{

				public void tableChanged(TableModelEvent evt)
				{
					
					int row = evt.getFirstRow();
					int column = evt.getColumn();
					new WriteLog("�޸ĵ���:" + column  + " ���޸ĵ���:" + row+ " ���޸ĺ��ֵ:" + model.getValueAt(row , column));
				}
			});
			//ʹ��TableModel����JTable��������Ӧ�����ӵ�������
			Object[] columnTitle = {"��������","����ID","�м̷�ʽ","��������","ͳ��ʱ��","�м̷�������","�м̿�������","ռ�ô���","Ӧ�����","ռ�û�����","ͨ��������","����Ժ�����","ת���Ժ�����"
					,"���Ӧ�����","ת��Ӧ�����"};
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
			//��ȡ���ݿ�����
			conn = confFile.getConnection();
			//����Statement
			constmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		}

		catch (Exception e1)
		{  
			e1.printStackTrace();
		}
		
		//��ȡ���ݲ�����������
		try
		{
				//���װ��JTable��JScrollPane��Ϊ��
				if (conscrollPane != null)
				{
					//����������ɾ�����
					conPanel.remove(conscrollPane);
				}
				//����������Ϊ�գ���رս����
				if (conrs != null) 
				{
					conrs.close();
				}
				
				if (inoutselected.getSelectedIndex() == 0)
				{
					JOptionPane.showMessageDialog(null, "��ѡ���ѯ�Ļ���������", "����", JOptionPane.INFORMATION_MESSAGE); 
				}
				else if (inoutselected.getSelectedIndex() == 1)
				{
					String ID = (String)IDQuery.getSelectedItem();
					String TimeSel = "";
					//�˴��趨ѡ���ʱ�䷶Χ��һ���������һ��
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
						JOptionPane.showMessageDialog(null, "��ѡ���ѯʱ�䣡", "����", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					try
					{
						//���װ��JTable��JScrollPane��Ϊ��
						if (conscrollPane != null)
						{
							//����������ɾ�����
							conPanel.remove(conscrollPane);
						}
						//����������Ϊ�գ���رս����
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
						//��ѯ�û�ѡ������ݱ�
							
						conrs = constmt.executeQuery(query);
						//ʹ�ò�ѯ����ResultSet����TableModel����
						model = new ResultSetTableModel(conrs,false);
						//ΪTableModel��Ӽ������������û����޸�
						model.addTableModelListener(new TableModelListener ()
						{

							public void tableChanged(TableModelEvent evt)
							{
								
								int row = evt.getFirstRow();
								int column = evt.getColumn();
								new WriteLog("�޸ĵ���:" + column  + " ���޸ĵ���:" + row+ " ���޸ĺ��ֵ:" + model.getValueAt(row , column));
							}
						});
						//ʹ��TableModel����JTable��������Ӧ�����ӵ�������
						Object[] columnTitle = {"��������","����ID","�м̷�ʽ","��������","ͳ��ʱ��","�м̷�������","�м̿�������","ռ�ô���","Ӧ�����","ռ�û�����","ͨ��������","����Ժ�����","ת���Ժ�����"
								,"���Ӧ�����","ת��Ӧ�����"};
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
					
						
						   //ʵ��������ɫ�Ĵ���  
				        //���ĳһ�е�tableColumn��tableColumn��һ�еĹ�����  
				        TableColumn tableColumn = table.getColumn("����ID");  
				        //��ʼ��table����Ⱦ��  
				        DefaultTableCellRenderer cellRanderer = new DefaultTableCellRenderer();  
				        //����ǰ��ɫҲ����������ɫ  
				        cellRanderer.setForeground(Color.RED);  
				        //���������Ⱦ������ŵ�tableColumn��  
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
						JOptionPane.showMessageDialog(null, "��ѡ���ѯʱ�䣡", "����", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					try
					{
						//���װ��JTable��JScrollPane��Ϊ��
						if (conscrollPane != null)
						{
							//����������ɾ�����
							conPanel.remove(conscrollPane);
						}
						//����������Ϊ�գ���رս����
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
						//��ѯ�û�ѡ������ݱ�
							
						conrs = constmt.executeQuery(query);
						//ʹ�ò�ѯ����ResultSet����TableModel����
						model = new ResultSetTableModel(conrs,false);
						//ΪTableModel��Ӽ������������û����޸�
						model.addTableModelListener(new TableModelListener ()
						{

							public void tableChanged(TableModelEvent evt)
							{
								
								int row = evt.getFirstRow();
								int column = evt.getColumn();
								new WriteLog("�޸ĵ���:" + column  + " ���޸ĵ���:" + row+ " ���޸ĺ��ֵ:" + model.getValueAt(row , column));
							}
						});
						//ʹ��TableModel����JTable��������Ӧ�����ӵ�������
						Object[] columnTitle = {"��������","����ID","�м̷�ʽ","��������","ͳ��ʱ��","�м̷�������","�м̿�������","ռ�ô���","Ӧ�����","ռ�û�����","ͨ��������","�Ժ�����","��ͨ����"
								,"�м���ѡ����","��Ӧ�����","����æ����"};
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
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}

	public static void ImportTrafficManner(String str,Boolean isChanged) 
	{
	       //����ִ��ʱ��
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);//ÿ��
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
	        //����ÿ���00:10:00ִ�У�
	        calendar.set(year, month, day+1, 0, 10, 00);	//@20141025 ԭ��Ϊday-1�����ƴ��󣬸�Ϊday+1
	        java.util.Date date = calendar.getTime();

	        TimerTask task = new TimerTask(){ 
	        	public void run() {
	        		//��װ��ǰ���ڹ���file�ļ�����
	        		if (timerSelected != 0)
	        			cancel();
	        		else
	        		{
	           		 	DataImportDiag.readTrafficFile(logFilename,-1);
	        		}
	            }
	        
	        };
	        timerofDay.schedule(task, date);
			tbLabel.setText("��ǰ��������ȡģʽ��ÿ��һ�Σ���ȡʱ��Ϊ�賿00:10,��ȡ�ļ� "+logFilename);
		}
		else if (str.equals("hour"))
		{
			timerSelected = 1;
			//����ÿСʱ��10:00ִ�У�һСʱִ��һ��
	        calendar.set(year, month, day, hour, 10, 00);
	        java.util.Date date = calendar.getTime();
	        int period = 3600*1000;

	        TimerTask task = new TimerTask(){ 
	        	public void run() {
	            	if (timerSelected != 1)
	        			cancel();	
	        		else
	        		{
	        			
	  	              //��ȡtraffic�ļ�
	        			DataImportDiag.readTrafficFile(logFilename,hour);
	  	        		WriteLog.WriteLogFile("�������һֱ��ִ�У�5����һ�ΰ���");
	  	        		inquerybtnClicked();
	  	        		outquerybtnClicked();
	        		}
	            }
	        };
	  			timerofHour.schedule(task, date,period);
	  			tbLabel.setText("��ǰ��������ȡģʽ��ÿСʱһ�Σ���ȡ�ļ� "+logFilename);
		}
		//����ͨ��ischanged�ж��Ƿ��ǽ�����δ򿪻���ͨ�����ݵ���Ի���������Ϊ������δ򿪣����ֶ��򿪻������ļ���Ч��
		//��Ϊͨ�����ݵ���Ի���򿪣����ʱ�������ļ��Ի���
		else if (str.equals("manual"))
		{
			if(isChanged)
			{
				timerSelected = 2;
				openDia = new FileDialog(DataImportDiag.f,"���뻰�����ļ�",FileDialog.LOAD);
				openDia.setVisible(true);
				String dirPath = openDia.getDirectory();
				String fileName = openDia.getFile();
				DataImportDiag.readTrafficFile(dirPath+fileName,-1);
				WriteLog.WriteLogFile("ȫʱ�ֶ���"+dirPath+fileName);
			}
			else
				tbLabel.setText("��ǰ��������ȡģʽ���ֶ���ȡ��");

		}
		else
		{
			WriteLog.WriteLogFile("��ʱ�����ô��󣬵�ǰ��ʱ��Ϊ"+str+",isChanged����Ϊ"+isChanged);
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
	//����������ʼ��rs��rsmd��������
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

	//��дgetColumnName����������Ϊ��TableModel��������
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
	//��дgetColumnCount�������������ø�TableModel������
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
	//��дgetValueAt�������������ø�TableModelָ����Ԫ���ֵ
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
	//��дgetColumnCount�������������ø�TableModel������
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
	//��дisCellEditable����true����ÿ����Ԫ��ɱ༭���˴���������Ϊ�����޸�
	public boolean isCellEditable(int rowIndex, int columnIndex) 
	{
		if(isCellEditable)
			return true;
		else
		{
			//JOptionPane.showMessageDialog(null, "�ñ����������޸ģ�", "����", JOptionPane.INFORMATION_MESSAGE); 
			return false;
		}
	
	}
	//��дsetValueAt����������ʵ���û��༭��Ԫ��ʱ������������Ӧ�Ķ���
	public void setValueAt(Object aValue,
		int row,int column)
	{
		try
		{
			//�������λ����Ӧ������
			rs.absolute(row + 1);
			//�޸ĵ�Ԫ����Ӧ��ֵ
			rs.updateObject(column + 1 , aValue);
			//�ύ�޸�
			rs.updateRow();
			//������Ԫ����޸��¼�
			fireTableCellUpdated(row, column);
		}
		catch (SQLException evt)
		{
			evt.printStackTrace();
		}
	}
	
}