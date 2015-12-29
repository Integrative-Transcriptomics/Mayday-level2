/**
 * 
 */
package mayday.expressionmapping.view.information;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import mayday.expressionmapping.controller.MainFrame;

/**
 * @author Stephan Gade
 *
 */
@SuppressWarnings("serial")
public class InfoWindow extends JFrame implements Runnable, WindowListener {

	private MainFrame master;
	private int id = -1;
	private String information;
	private JEditorPane windowPane = null;
	private JScrollPane scrollPane = null;

	public InfoWindow(MainFrame master, int id, String title) {

		super(title);
		
		this.master = master;

		initComponents();
		
		this.id = id;
		
		//System.err.println("Start InfoWindow with id "+id);
		


	}

	private void initComponents() {

		
		//this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(600, 200);
		this.setResizable(true);
		
		this.addWindowListener(this);


		//scrollPane.add(windowLabel);
//		scrollPane.setVisible(true);
//		windowLabel.setVisible(true);

		

	}

	public void setInformation(String information) {

		this.information = information;

	}

	public void run(){

		if (this.information == null)
			throw new IllegalStateException("Information string has to be set before execution!");
		
		
			windowPane = new JEditorPane();
			windowPane.setContentType( "text/html" );
			windowPane.setText(information);
			
		
		windowPane.setEditable(false);

		scrollPane = new JScrollPane(windowPane);

		this.add(scrollPane);

		this.setVisible(true);

	}

	public void windowOpened(WindowEvent e) {
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowClosing(WindowEvent e) 
	{
		e.getWindow().dispose();
	}

	public void windowClosed(WindowEvent e) {
		
		this.master.removeInfoWindow(this.id);
	}

	public void windowIconified(WindowEvent e) {
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowDeiconified(WindowEvent e) {
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowActivated(WindowEvent e) {
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowDeactivated(WindowEvent e) {
		//throw new UnsupportedOperationException("Not supported yet.");
	}
}
