package mayday.Reveal.gui.genome;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.meta.Genome;
import mayday.core.MaydayDefaults;

@SuppressWarnings("serial")
public class GenomeNameMappingDialog extends JDialog {
	
	private DataStorage ds;
	
	private JTextField[] oriNames;
	private JTextField[] mapNames;
	
	@SuppressWarnings("deprecation")
	public GenomeNameMappingDialog(ProjectHandler projectHandler) {
		setTitle("Map genome sequence names");
		this.ds = projectHandler.getSelectedProject();
		JPanel panel = createWidgets();
		
		this.setLayout(new BorderLayout());
		this.add(panel, BorderLayout.CENTER);
		
		this.setPreferredSize(new Dimension(600, 600));
		this.setMinimumSize(new Dimension(400,400));
		this.setModal(true);
		
		pack();
		
		MaydayDefaults.centerWindowOnScreen(this);
	}

	private JPanel createWidgets() {
		final JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel centerPanel = new JPanel(new GridLayout(1,2));
		
		JPanel oriNamePanel = new JPanel();
		JPanel mapNamePanel = new JPanel();
		
		JLabel originalNameLabel = new JLabel("Original names");
		JLabel mappedNameLabel = new JLabel("Mapped names");
		
		final Genome genome = (Genome) ds.getGenome();
		final int numSeq = genome.getNumberOfSequences();
		
		oriNamePanel.setLayout(new GridLayout(numSeq,1));
		mapNamePanel.setLayout(new GridLayout(numSeq,1));
		
		this.oriNames = new JTextField[numSeq];
		this.mapNames = new JTextField[numSeq];
		
		for(int i = 0; i < numSeq; i++) {
			oriNames[i] = new JTextField(25);
			mapNames[i] = new JTextField(25);
			oriNames[i].setFocusable(false);
			oriNames[i].setEditable(false);
			mapNames[i].setEditable(true);
			oriNamePanel.add(oriNames[i]);
			mapNamePanel.add(mapNames[i]);
		}
		
		setFieldText(genome, numSeq);
		
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(originalNameLabel, BorderLayout.NORTH);
		leftPanel.add(oriNamePanel, BorderLayout.CENTER);
		
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(mappedNameLabel, BorderLayout.NORTH);
		rightPanel.add(mapNamePanel, BorderLayout.CENTER);
		
		rightPanel.setMinimumSize(new Dimension(300,1));
		leftPanel.setMinimumSize(new Dimension(300,1));
		
		centerPanel.add(leftPanel, BorderLayout.WEST);
		centerPanel.add(rightPanel, BorderLayout.EAST);
		
		JButton cancelButton = new JButton("Cancel");
		JButton applyButton = new JButton("Apply");
		JButton sortButton = new JButton("Sort sequences");
		JButton sortByMappingButton = new JButton("Sort mapping");
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(sortButton);
		buttonPanel.add(sortByMappingButton);
		buttonPanel.add(applyButton);
		buttonPanel.add(cancelButton);
		
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < numSeq; i++) {
					String oriName = oriNames[i].getText();
					String mapName = mapNames[i].getText();
					
					if(!mapName.trim().equals(""))
						genome.mapSeqName(oriName, mapName.trim());
				}
				
				dispose();
			}
		});
		
		sortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				genome.sortSequenceNames();
				setFieldText(genome, numSeq);
			}
		});
		
		sortByMappingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				genome.sortByMappedSequenceNames();
				setFieldText(genome, numSeq);
			}
		});
		
		JScrollPane scroller = new JScrollPane(centerPanel);
		mainPanel.add(scroller, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		return mainPanel;
	}

	private void setFieldText(final Genome genome, int numSeq) {
		for(int i = 0; i < numSeq; i++) {
			String originalName = ds.getGenome().getSequenceName(i); 
			oriNames[i].setText(originalName);
			oriNames[i].setCaretPosition(0);
			String mappedName = genome.getMappedSequenceName(originalName);
			if(mappedName != null) {
				mapNames[i].setText(mappedName);
			}
		}
	}
}
