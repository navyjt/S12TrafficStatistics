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
 * �ṩ�Խ����־���Ĳ�ѯ�������γ�һһ��Ӧ��ϵ
 * @author ��
 *
 */

public class ExchangeQueryDiag extends JDialog{

	private static final long serialVersionUID = -3626096191147797668L;
	
	private JFrame f = new JFrame("��������");
	private JScrollPane scrollPane;
	private ResultSet rs;
	private Connection conn;
	private Statement stmt;
	private ResultSetTableModel model;
	private JPanel btnPanel = new JPanel();
	private JButton btnExtract = new JButton("��ȡ����ID");
	private JLabel btnQuery = new JLabel("                ���ѣ�����ɸ��ݻ����������еľ���ID���ݣ��޸����Ӧ�ľ������ơ��м̷�ʽ�Ȳ�����");
	
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
					//��ȡ���ݿ�����
					conn = confFile.getConnection();
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
						f.remove(scrollPane);
					}
					//����������Ϊ�գ���رս����
					if (rs != null) 
					{
						rs.close();
					}
					String query = "insert ignore into alias(ID) select distinct statistics_inc.ID from statistics_inc"; 
					String query1 = "insert ignore into alias(ID) select distinct statistics_out.ID from statistics_out"; 
							//"select distinct statistics_inc.ID,alias.Name,alias.Signal,alias.Location from statistics_inc,alias where statistics_inc.ID = alias.ID";
					//��ѯ�û�ѡ������ݱ�
					stmt.executeUpdate(query);
					stmt.executeUpdate(query1);
					
					String query2 = "select * from alias";
					//��ѯ�û�ѡ������ݱ�
					rs = stmt.executeQuery(query2);
					//ʹ�ò�ѯ����ResultSet����TableModel����
					model = new ResultSetTableModel(rs,true);
					//model = new TableModel(rs);
					//ʹ��TableModel����JTable��������Ӧ�����ӵ�������
					Object[] columnTitle = {"�������","��������","�м̷�ʽ","��������"};
					JTable table = new JTable(model);
					//�޸ı�����������޸ı����
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
					//��ȡ���ݿ�����
					conn = confFile.getConnection();
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
						f.remove(scrollPane);
					}
					//����������Ϊ�գ���رս����
					if (rs != null) 
					{
						rs.close();
					}
					String query = "select * from alias";
					//��ѯ�û�ѡ������ݱ�
					rs = stmt.executeQuery(query);
					//ʹ�ò�ѯ����ResultSet����TableModel����
					model = new ResultSetTableModel(rs,true);
					//model = new TableModel(rs);
					//ʹ��TableModel����JTable��������Ӧ�����ӵ�������
					Object[] columnTitle = {"�������","��������","�м̷�ʽ","��������"};
					JTable table = new JTable(model);
					//�޸ı�����������޸ı����
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
