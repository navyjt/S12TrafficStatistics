package S12TrafficStatistics;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;
import javax.swing.table.TableColumn;
/**
 * 提供对交换局局向的查询工作，形成一一对应关系
 * @author 涛
 *
 */

public class ExchangeQueryDiag extends JDialog{

	private static final long serialVersionUID = -3626096191147797668L;
	
	private JFrame f = new JFrame("局向配置");
	private JScrollPane scrollPane;
	private ResultSet rs;
	private Connection conn;
	private Statement stmt;
	private ResultSetTableModel model;
	private JPanel btnPanel = new JPanel();
	private JButton btnExtract = new JButton("提取局向ID");
	private JLabel btnQuery = new JLabel("                提醒：这里可根据话务量报告中的局向ID数据，修改其对应的局向名称、中继方式等参数！");
	
	public ExchangeQueryDiag(String string, boolean b) {
		f.setSize(1024, 728);
		f.setLocationRelativeTo(null);
		btnPanel.add(btnExtract);
		btnPanel.add(btnQuery);
		f.add(btnPanel,BorderLayout.NORTH);
		f.validate();
		f.setVisible(true);
		
		btnExtract.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				
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
				
				//获取数据并填充至表格中
				
				try
				{
					//如果装载JTable的JScrollPane不为空
					if (scrollPane != null)
					{
						//从主窗口中删除表格
						f.remove(scrollPane);
					}
					//如果结果集不为空，则关闭结果集
					if (rs != null) 
					{
						rs.close();
					}
					String query = "insert ignore into alias(ID) select distinct statistics_inc.ID from statistics_inc"; 
					String query1 = "insert ignore into alias(ID) select distinct statistics_out.ID from statistics_out"; 
							//"select distinct statistics_inc.ID,alias.Name,alias.Signal,alias.Location from statistics_inc,alias where statistics_inc.ID = alias.ID";
					//查询用户选择的数据表
					stmt.executeUpdate(query);
					stmt.executeUpdate(query1);
					
					String query2 = "select * from alias";
					//查询用户选择的数据表
					rs = stmt.executeQuery(query2);
					//使用查询到的ResultSet创建TableModel对象
					model = new ResultSetTableModel(rs,true);
					//model = new TableModel(rs);
					//使用TableModel创建JTable，并将对应表格添加到窗口中
					Object[] columnTitle = {"局向代号","局向名称","中继方式","局向类型"};
					JTable table = new JTable(model);
					//修改表的列名，并修改表格宽度
					table.getColumnModel().getColumn(0).setHeaderValue(columnTitle[0]);
					table.getColumnModel().getColumn(1).setHeaderValue(columnTitle[1]);
					table.getColumnModel().getColumn(2).setHeaderValue(columnTitle[2]);
					table.getColumnModel().getColumn(3).setHeaderValue(columnTitle[3]);
					TableColumn firsetColumn = table.getColumnModel().getColumn(0);
					firsetColumn.setPreferredWidth(250);
					firsetColumn.setMaxWidth(250);
					firsetColumn.setMinWidth(250);
			
					scrollPane = new JScrollPane(table);
					f.add(scrollPane, BorderLayout.CENTER);
					f.setVisible(true);
				}            
				catch (SQLException e3)
				{  
					e3.printStackTrace();
				}
			
			
			}
			
		});
		
		/*btnQuery.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent arg0) {
				

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
				
				//获取数据并填充至表格中
				
				try
				{
					//如果装载JTable的JScrollPane不为空
					if (scrollPane != null)
					{
						//从主窗口中删除表格
						f.remove(scrollPane);
					}
					//如果结果集不为空，则关闭结果集
					if (rs != null) 
					{
						rs.close();
					}
					String query = "select * from alias";
					//查询用户选择的数据表
					rs = stmt.executeQuery(query);
					//使用查询到的ResultSet创建TableModel对象
					model = new ResultSetTableModel(rs,true);
					//model = new TableModel(rs);
					//使用TableModel创建JTable，并将对应表格添加到窗口中
					Object[] columnTitle = {"局向代号","局向名称","中继方式","局向类型"};
					JTable table = new JTable(model);
					//修改表的列名，并修改表格宽度
					table.getColumnModel().getColumn(0).setHeaderValue(columnTitle[0]);
					table.getColumnModel().getColumn(1).setHeaderValue(columnTitle[1]);
					table.getColumnModel().getColumn(2).setHeaderValue(columnTitle[2]);
					table.getColumnModel().getColumn(3).setHeaderValue(columnTitle[3]);
					TableColumn firsetColumn = table.getColumnModel().getColumn(0);
					firsetColumn.setPreferredWidth(250);
					firsetColumn.setMaxWidth(250);
					firsetColumn.setMinWidth(250);
			
					scrollPane = new JScrollPane(table);
					f.add(scrollPane, BorderLayout.CENTER);
					f.setVisible(true);
				}            
				catch (SQLException e3)
				{  
					e3.printStackTrace();
				}
			
				
			}
			
		});		//init();
*/	}
		
}
