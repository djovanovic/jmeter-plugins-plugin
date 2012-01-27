package kg.apc.jmeter.aws.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class AWSClientsTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DefaultTableModel model;
	
	public AWSClientsTable() {	

		super(new DefaultTableModel());
		model = (DefaultTableModel) this.getModel();
		
		this.setShowHorizontalLines( true );
		this.setRowSelectionAllowed( true );
		this.setColumnSelectionAllowed( true );
		
		this.setSelectionForeground( Color.white );

		model.addColumn("AMI");
		model.addColumn("Instance ID");
		model.addColumn("State");
		model.addColumn("Snapshot");
		model.addColumn("Launch Time");
	}
	
	public Component prepareRenderer(TableCellRenderer tableCellRenderer,int row, int column) {
		
			Component c = super.prepareRenderer(tableCellRenderer, row, column);
			return c;
	}
	
	public void addRow(Object[] data) {
		
 		model.addRow(data);
	}
	
	public void updateTable(String orderID) {
		
		for (int i = 0 ; i < this.getRowCount() ; i ++) {
			
			if (this.getValueAt(i, 0).equals(orderID)) {
				
				//this.setValueAt(comment, i, 4);
			}
		}
	}
}
