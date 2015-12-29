package mayday.wapiti.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import mayday.core.pluma.PluginInfo;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.gui.actions.experiments.ExperimentsPropertiesAction;
import mayday.wapiti.gui.actions.experiments.RemoveExperimentsAction;
import mayday.wapiti.gui.layeredpane.ReorderableHorizontalPanel;
import mayday.wapiti.transformations.base.Transformation;

@SuppressWarnings("serial")
public class ExperimentPanel extends ReorderableHorizontalPanel {

	protected JLabel indexLabel = new JLabel();
//	protected List<JComponent> transformationComponents = new LinkedList<JComponent>();
	protected JPanel transformationsPanel;
	protected JLabel featureCountLabel = new JLabel();
	protected JLabel locusCountLabel = new JLabel();
	protected JLabel sourceLabel = new JLabel();
	protected JLabel stateLabel = new JLabel();
	
	protected Experiment ex;
	
	public ExperimentPanel(Experiment e) {
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		setLayout(new BorderLayout());
		ex = e;
		
		transformationsPanel = new JPanel();		
		transformationsPanel.setLayout(null);
		transformationsPanel.setOpaque(false);
		add(transformationsPanel, BorderLayout.CENTER);
		
		JPanel topRow = new JPanel();
		topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));
		topRow.add(featureCountLabel);
		topRow.add(locusCountLabel);
		topRow.setOpaque(false);
		add(topRow, BorderLayout.EAST);		
		
		JPanel bottomRow = new JPanel();
		bottomRow.setLayout(new BoxLayout(bottomRow, BoxLayout.X_AXIS));
		bottomRow.add(sourceLabel);
		bottomRow.add(Box.createHorizontalGlue());
		bottomRow.add(stateLabel);	
		bottomRow.setOpaque(false);
		
		add(bottomRow, BorderLayout.SOUTH);		
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getButton()==MouseEvent.BUTTON3) {
					new ExperimentContextMenu().show(ExperimentPanel.this, evt.getX(), evt.getY());
					evt.consume();
				}
				if (evt.getButton()==MouseEvent.BUTTON1 && evt.getClickCount()==2)
					new ExperimentsPropertiesAction(ex.getTransMatrix().getPane().getSelectionModel()).actionPerformed(null);
			}
		});

		invalidate();
		validate();
		stateChanged(e.getTransMatrix().getLayout());
	}
	
	public int getTitleWidth() {
		return indexLabel.getPreferredSize().width;
	}
	
	public Experiment getExperiment() {
		return ex;
	}
	
	protected void updateIndex() {
		indexLabel.setText("<html><b>"+(index()+1)+") "+ex.getName()+"  ");
	}

	protected void updateCounts() {
		featureCountLabel.setText(ex.getNumberOfFeatures()+" features, ");
		if (ex.getNumberOfLoci()>0) {
			locusCountLabel.setText(ex.getNumberOfLoci()+" loci");
			locusCountLabel.setIcon(null);
		} else { 
			locusCountLabel.setText("no loci");
			locusCountLabel.setIcon(PluginInfo.getIcon("/mayday/interpreter/rinterpreter/images/warning12.gif"));
		}
		sourceLabel.setText(ex.getSourceDescription());
	}
	
	public void updateLayout(TransMatrixLayout tml) {
		transformationsPanel.removeAll();
		
		transformationsPanel.add(indexLabel);
		indexLabel.setSize( tml.getTitleWidth(), indexLabel.getPreferredSize().height);
		indexLabel.setLocation(0,0);
		
		int minw=0, minh=0;
		
		for (Transformation t : ex.getTransMatrix().getTransformations(ex)) {
			TransformationButton c = t.getGUIElement(ex);
			int tmlW = tml.getWidth(t);
			int tmlS = tml.getStart(t);
			int tmlH = c.getPreferredSize().height;
			c.setBounds(tmlS, 0, tmlW, tmlH);
			c.setColor(tml.getColor(t));
			transformationsPanel.add(c);

//			minh = Math.max(minh, tmlH);
			minw = Math.max(minw, tmlW+tmlS+5);
		}
		transformationsPanel.setMinimumSize(new Dimension(minw, minh));
		transformationsPanel.setPreferredSize(new Dimension(minw, minh));
		transformationsPanel.repaint();
		invalidate();
		validate();
//		getParent().invalidate();
//		getParent().validate();
	}
	
	public void stateChanged(TransMatrixLayout layout) {		
		stateLabel.setText(""+ex.getDataProperties());
		updateIndex();
		updateCounts();
		updateLayout(layout);
	}

	
	public void reordered(int newIndex) {
		updateIndex();
	}

	
	public class ExperimentContextMenu extends JPopupMenu {

		public ExperimentContextMenu() {
			add( new ExperimentsPropertiesAction(ex.getTransMatrix().getPane().getSelectionModel()) );
			add( new RemoveExperimentsAction(ex.getTransMatrix(), ex.getTransMatrix().getPane().getSelectionModel()) );
		}

	}
	
}
