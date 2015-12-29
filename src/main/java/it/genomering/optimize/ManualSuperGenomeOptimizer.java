package it.genomering.optimize;

import it.genomering.structure.Block;
import it.genomering.structure.SuperGenome;
import it.genomering.structure.SuperGenomeEvent;
import it.genomering.structure.SuperGenomeListener;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import mayday.core.gui.MaydayFrame;
import mayday.core.gui.components.ReorderableJList;

/**
 * @author jaeger
 * 
 */
@SuppressWarnings("serial")
public class ManualSuperGenomeOptimizer extends MaydayFrame {

	private SuperGenome superGenome;

	JButton applyButton;
	JButton closeButton;
	JButton inverseSelectedButton;

	List<Block> tmpBlocks;
	JList list;
	SuperGenomeListener sgl;

	HashMap<String, Integer> mapBlockNameToIndex = new HashMap<String, Integer>();

	boolean mousePressed = false;

	/**
	 * @param superGenome
	 * @param ringdim
	 * @param cm
	 */
	public ManualSuperGenomeOptimizer(SuperGenome superGenome) {
		super("Change Block Order");
		this.superGenome = superGenome;

		createWidgtes();
	}

	private void createWidgtes() {
		setLayout(new BorderLayout());
		//list = new JList();
		list = new ReorderableJList();
		list.setSelectionModel(new DefaultListSelectionModel());
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setVisibleRowCount(10);
		list.setEnabled(true);
		
		DefaultListModel dlm = new DefaultListModel();
		list.setModel(dlm);
		
		sgl = new SuperGenomeListener() {
			@Override
			public void superGenomeChanged(SuperGenomeEvent evt) {
				System.out.println("Super Genome Changed -> notifying drag&drop list...");
				initialize();
			}
		};
		
		superGenome.addListener(sgl);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		add(new JLabel("Use Drag&Drop to change the block order"), "North");
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane, "Center");
		buttonPanel.add(applyButton = new JButton("Apply"));
		buttonPanel.add(inverseSelectedButton = new JButton("Inverse Selected")); 
		buttonPanel.add(closeButton = new JButton("Close"));
		add(buttonPanel, "South");

		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateOrdering();
			}
		});

		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		inverseSelectedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel dlm = (DefaultListModel) list.getModel();
				int[] selection = list.getSelectedIndices();
				if(selection.length == 0) 
					return;
				for(int i = 0; i < selection.length/2; i++) {
					int firstIndex = selection[i];
					int secondIndex = selection[selection.length - i - 1];
					//get block names
					String firstBlockName = (String)dlm.get(firstIndex);
					String secondBlockName = (String)dlm.get(secondIndex);
					//change order
					dlm.set(firstIndex, secondBlockName);
					dlm.set(secondIndex, firstBlockName);
				}
				
				updateOrdering();
			}
		});

		pack();
	}
	
	private void updateOrdering() {
		DefaultListModel dlm = (DefaultListModel) list.getModel();
		tmpBlocks = new ArrayList<Block>();
		for (int i = 0; i < dlm.getSize(); i++) {
			String blockName = (String)dlm.get(i);
			tmpBlocks.add(superGenome.getBlocks().get(
					mapBlockNameToIndex.get(blockName)));
		}
		superGenome.setBlocks(tmpBlocks);
	}

	private void initialize() {
		int i = 0;
		DefaultListModel dlm = (DefaultListModel) list.getModel();
		dlm.clear();
		mapBlockNameToIndex.clear();
		for (Block b : superGenome.getBlocks()) {
			dlm.addElement(b.getName());
			mapBlockNameToIndex.put(b.getName(), i++);
		}
	}

	/**
	 * start the manual optimizer
	 */
	public void start() {
		this.initialize();
		this.setVisible(true);
	}
}
