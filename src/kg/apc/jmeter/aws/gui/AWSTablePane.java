package kg.apc.jmeter.aws.gui;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class AWSTablePane extends JTabbedPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public AWSCredentialsTable aWSCredentialsTable;
	private JScrollPane aWSCredentialsScroll;
	
	public AWSClientsTable aWSClientsTable;
	private JScrollPane aWSClientsScroll;
	
	public AWSServersTable aWSServersTable;
	private JScrollPane aWSServersScroll;

	public AWSTablePane() {

		aWSCredentialsTable = new AWSCredentialsTable();
		aWSCredentialsScroll = new JScrollPane(aWSCredentialsTable);
		
		aWSClientsTable = new AWSClientsTable();
		aWSClientsScroll = new JScrollPane(aWSClientsTable);
		
		aWSServersTable = new AWSServersTable();
		aWSServersScroll = new JScrollPane(aWSServersTable);
		
		render();
	}

	public void render() {	

		this.addTab("Credentials", aWSCredentialsScroll);
		this.addTab("Clients", aWSClientsScroll);
		this.addTab("Servers", aWSServersScroll);
	}
}
