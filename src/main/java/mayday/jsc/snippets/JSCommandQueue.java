package mayday.jsc.snippets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import mayday.jsc.shell.JSDispatcher;
import mayday.mushell.dispatch.DispatchEvent;
import mayday.mushell.dispatch.DispatchListener;
import mayday.mushell.queue.CommandQueue;
import mayday.mushell.snippet.AbstractSnippet;


/** 
 * Snippet for displaying all entered commands waiting to 
 * be executed 
 *
 * @version 1.0
 * @author Tobias Ries, ries@exean.net
 */
public class JSCommandQueue extends AbstractSnippet implements ListDataListener, DispatchListener
{
	private JScrollPane jsp;
	private JList list;
	private JButton killButton;	
	private JPanel mainPanel;
	private JLabel currentCommandLabel;
	private DefaultListModel listModel;
	protected CommandQueue queue;
	private JSDispatcher dispatcher;

	public JSCommandQueue(CommandQueue cmdQueue, JSDispatcher jsDispatcher)
	{
		this.dispatcher = jsDispatcher;
		
		this.queue = cmdQueue;
		this.queue.addListDataListener(this);
		
		this.initGui();
				
		this.fill();		
	}
	
	private void initGui()
	{
		this.mainPanel = new JPanel(new BorderLayout());
		this.mainPanel.setName("Command Queue");
		
		
		JPanel curCmdPanel = new JPanel(new BorderLayout());
		curCmdPanel.setBorder(BorderFactory.createTitledBorder("Currently executing"));
		curCmdPanel.setBackground(Color.white);
		this.currentCommandLabel = new JLabel(" Nothing ");
		curCmdPanel.add(this.currentCommandLabel, BorderLayout.WEST);
		
		this.killButton = new JButton("Kill");
		this.killButton.setForeground(Color.red);
		this.killButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispatcher.kill();
			}			
		});
		curCmdPanel.add(this.killButton, BorderLayout.EAST);
		
		this.mainPanel.add(curCmdPanel, BorderLayout.NORTH);
		
		
		JPanel queuePanel = new JPanel(new BorderLayout());
		queuePanel.setBackground(Color.white);
		queuePanel.setBorder(BorderFactory.createTitledBorder("Command queue"));
		this.listModel = new DefaultListModel();
		this.list = new JList(this.listModel);
		this.jsp = new JScrollPane(list);
		queuePanel.add(this.jsp, BorderLayout.CENTER);
		
		JButton deleteSelected = new JButton("Remove selected commands from queue");
		deleteSelected.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int[] indices = list.getSelectedIndices();
				Arrays.sort(indices);
				for(int i = indices.length-1; i >= 0; i--)
					queue.remove(indices[i]);				
			}			
		});
		queuePanel.add(deleteSelected, BorderLayout.SOUTH);
		
		this.mainPanel.add(queuePanel, BorderLayout.CENTER);
	}
	
	//read command-queue and insert into gui
	private void fill()
	{		
		for(int i = 0; i < this.queue.size(); i++)		
			this.listModel.addElement((i+1)+": "+this.queue.get(i).toString());		
	}
	
	private void update()
	{		
		this.listModel.removeAllElements();
		this.fill();
		this.list.updateUI();
	}

	@Override
	public void contentsChanged(ListDataEvent arg0) {
		this.update();		
	}

	@Override
	public void intervalAdded(ListDataEvent arg0) {
		this.update();		
	}

	@Override
	public void intervalRemoved(ListDataEvent arg0) {
		this.update();		
	}

	@Override
	public JComponent getComponent() {
		return this.mainPanel;
	}

	@Override
	public void dispatching(DispatchEvent evt) {
		if(evt.getCommand() != null)
			this.currentCommandLabel.setText(evt.getCommand());
		else
			this.currentCommandLabel.setText(" Nothing ");
		
	}
}
