package mayday.GWAS.gui.genes;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.Gene;
import mayday.GWAS.data.GeneList;
import mayday.core.EventFirer;
import mayday.core.MaydayDefaults;

@SuppressWarnings("serial")
public class GeneSelectionDialog extends JDialog {

	private DataStorage data;
	
	private JList allGenes;
	private JList selectedGenes;
	
	private JButton moveRightButton;
	private JButton moveLeftButton;
	
	private JButton closeButton;
	
	private EventFirer<ChangeEvent, ChangeListener> eventfirer
    = new EventFirer<ChangeEvent, ChangeListener>() {
		@Override
		protected void dispatchEvent(ChangeEvent event, ChangeListener listener) {
			listener.stateChanged(event);
		}		
    };
	
	@SuppressWarnings("deprecation")
	public GeneSelectionDialog(DataStorage data) {
		this.data = data;
		
		setLayout(new BorderLayout());
		setTitle("Gene Selection Dialog");
		
		createWidgets();
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		
		MaydayDefaults.centerWindowOnScreen(this);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createWidgets() {
		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireChanged();
				dispose();
			}
		});
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(closeButton);
		
		add(buttonPanel, BorderLayout.SOUTH);
		
		GeneList genes = data.getGenes();
		
		final SortableListModel allGenesModel = new SortableListModel();
		allGenes = new JList(allGenesModel);
		
		for(int i = 0; i < genes.size(); i++) {
			allGenesModel.addElement(genes.getGene(i), true);
		}
		
		final SortableListModel selectedGenesModel = new SortableListModel();
		selectedGenes = new JList(selectedGenesModel);
		
		JPanel rightPanel = new JPanel(new BorderLayout());
		JPanel leftPanel = new JPanel(new BorderLayout());
		
		rightPanel.add(new JLabel("Selected Genes"), BorderLayout.NORTH);
		leftPanel.add(new JLabel("Available Genes"), BorderLayout.NORTH);
		
		rightPanel.add(new JScrollPane(selectedGenes), BorderLayout.CENTER);
		leftPanel.add(new JScrollPane(allGenes), BorderLayout.CENTER);
		
		moveRightButton = new JButton(">>");
		moveLeftButton = new JButton("<<");
		
		moveRightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				Object[] values = allGenes.getSelectedValues();
				for(int i = 0; i < values.length; i++) {
					selectedGenesModel.addElement((Gene)values[i], true);
					allGenesModel.removeElement((Gene)values[i]);
				}
				
				selectedGenes.clearSelection();
				selectedGenes.revalidate();
				selectedGenes.repaint();
				
				allGenes.clearSelection();
				allGenes.revalidate();
				allGenes.repaint();
				
				fireChanged();
			}
		});
		
		moveLeftButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				Object[] values = selectedGenes.getSelectedValues();
				for(int i = 0; i < values.length; i++) {
					allGenesModel.addElement((Gene)values[i], true);
					selectedGenesModel.removeElement((Gene)values[i]);
				}
				
				allGenes.clearSelection();
				allGenes.revalidate();
				allGenes.repaint();
				
				selectedGenes.clearSelection();
				selectedGenes.revalidate();
				selectedGenes.repaint();
				
				fireChanged();
			}
		});
		
		moveRightButton.setToolTipText("Move Right");
		moveLeftButton.setToolTipText("Move Left");
		
		rightPanel.add(moveLeftButton, BorderLayout.SOUTH);
		leftPanel.add(moveRightButton, BorderLayout.SOUTH);
		
		
		JPanel centerPanel = new JPanel(new GridLayout(1,2));
		centerPanel.add(leftPanel);
		centerPanel.add(rightPanel);
		
		add(centerPanel, BorderLayout.CENTER);
	}
	
	public void addChangeListener(ChangeListener cl) {
		this.eventfirer.addListener(cl);
	}
	
	public void removeChangeListener(ChangeListener cl) {
		this.eventfirer.removeListener(cl);
	}
	
	public void fireChanged() {
		eventfirer.fireEvent(new ChangeEvent(this));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Set<Gene> getSelectedGenes() {
		Set<Gene> genes = new HashSet<Gene>();
		
		SortableListModel model = (SortableListModel)selectedGenes.getModel();
		List<Gene> elements = model.elements();
		
		for(Gene g : elements) {
			genes.add(g);
		}
		
		return genes;
	}
	
	private class SortableListModel<T extends Comparable<? super T>> extends AbstractListModel {
		 
	    private List<T> model = new ArrayList<T>();
	    private boolean isSorted = true;
	 
	    public void sort() {
	        if (!isSorted) {
	            Collections.sort(model);
	            fireContentsChanged(this, 0, model.size() - 1);
	        }
	    }
	 
	    public List<T> elements() {
			return Collections.unmodifiableList(model);
		}

		public void removeElement(Gene gene) {
			model.remove(gene);
		}

		private void addElement(T element) {
	        addElement(element, model.size());
	    }
	 
	    private void addElement(T element, int index) {
	        model.add(index, element);
	        fireIntervalAdded(this, index, index);
	    }
	 
	    public void addElement(T element, boolean sort) {
	        if (!sort) {
	            addElement(element);
	            isSorted = false;
	        } else {
	            if (!isSorted) {
	                sort();
	            }
	            int index = Collections.binarySearch(model, element);
	            if (index < 0)
	                addElement(element, -index - 1);
	            else
	                addElement(element, index);
	            isSorted = true;
	        }
	    }
	 
	    @Override
	    public Object getElementAt(int index) {
	        return model.get(index);
	    }
	 
	    @Override
	    public int getSize() {
	        return model.size();
	    }
	}
}
