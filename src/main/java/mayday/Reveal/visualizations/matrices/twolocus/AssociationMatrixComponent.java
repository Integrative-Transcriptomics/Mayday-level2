package mayday.Reveal.visualizations.matrices.twolocus;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import mayday.Reveal.data.Gene;
import mayday.Reveal.data.GeneList;
import mayday.Reveal.data.GenePair;
import mayday.Reveal.data.SNP;
import mayday.Reveal.visualizations.matrices.twolocus.viewelements.Overview;
import mayday.Reveal.visualizations.matrices.twolocus.viewelements.PlaceHolder;
import mayday.Reveal.visualizations.matrices.twolocus.viewelements.SelectionTypeMenu;
import mayday.core.Probe;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class AssociationMatrixComponent extends JPanel {
	
	private Color fgColor = Color.BLACK;
	private Color bgColor = Color.WHITE;
	
	private AssociationMatrix matrix;
	private CellComponent[] cellComponents;
	private BottomLabelPanel[] bottomLabels;
	private LeftLabelPanel[] leftLabels;
	
	private GeneList genes;
	private BasicStroke borderStroke = new BasicStroke(2);
	private BasicStroke normalStroke = new BasicStroke(1);
	private Color labelHighlightColor = new Color(0, 255, 255, 120); //254, 178, 76
	
	private SNPMouseListener snpMouseListener;
	
	private JPanel leftPanel;
	private JPanel bottomPanel;
	private JPanel centerPanel;
	
	private SelectionTypeMenu selectionTypeMenu;
	
	private Set<SNP> overallSelectedSNPS = new HashSet<SNP>();
	
	/**
	 * @param associationMatrix
	 */
	public AssociationMatrixComponent(AssociationMatrix associationMatrix) {
		this.matrix = associationMatrix;
		this.genes = matrix.getData().getGenes();

		this.setLayout(new BorderLayout());
		this.setBackground(bgColor);
		
		centerPanel = new JPanel();
		JScrollPane centerPanelScroller = new JScrollPane(centerPanel);
		
		centerPanelScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		centerPanelScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		centerPanelScroller.setBackground(fgColor);
		centerPanelScroller.setForeground(bgColor);
		
		leftPanel = new JPanel();
		centerPanelScroller.setRowHeaderView(leftPanel);
		
		bottomPanel = new JPanel();
		centerPanelScroller.setColumnHeaderView(bottomPanel);
		
		selectionTypeMenu = new SelectionTypeMenu();
		
		centerPanelScroller.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new PlaceHolder(bgColor));
		centerPanelScroller.setCorner(JScrollPane.LOWER_LEFT_CORNER, new PlaceHolder(bgColor));
		centerPanelScroller.setCorner(JScrollPane.UPPER_LEFT_CORNER, new Overview(bgColor));
		centerPanelScroller.setCorner(JScrollPane.LOWER_RIGHT_CORNER, selectionTypeMenu);
		
		this.add(centerPanelScroller, BorderLayout.CENTER);
	}
	
	/**
	 * initialize components
	 */
	public void initialize() {
		selectionTypeMenu.setSetting(matrix.setting);
		arrangeComponents();
		resize();
	}
	
	/**
	 * arrange components according to the setting
	 */
	public void arrangeComponents() {
		int cols = matrix.setting.getNumberOfProbeColumns();
		int rows = (int)Math.ceil((double)genes.size() / cols);
		
		centerPanel.removeAll();
		bottomPanel.removeAll();
		leftPanel.removeAll();
		
		centerPanel.setLayout(new GridLayout(rows, cols));
		centerPanel.setBackground(bgColor);
		centerPanel.setForeground(fgColor);
		
		int j = 0;
		int r = 0;
		int c = 0;
		
		snpMouseListener = new SNPMouseListener();
		
		cellComponents = new CellComponent[genes.size()];
		for(Probe gene : genes) {
			cellComponents[j] = new CellComponent(gene.getName(), r, c);
			cellComponents[j].addMouseMotionListener(snpMouseListener);
			centerPanel.add(cellComponents[j]);
			
			j++;
			c++;
			
			if(c % cols == 0) {
				r++;
				c = 0;
			}
		}
		
		if(genes.size() < cols * rows) {
			for(int i = 0; i < cols*rows-genes.size(); i++) {
				centerPanel.add(new JPanel());
			}
		}
		
		leftPanel.setLayout(new GridLayout(rows, 1));
		leftPanel.setBackground(bgColor);
		leftPanel.setForeground(fgColor);
		
		leftLabels = new LeftLabelPanel[rows];
		for(int i = 0; i < rows; i++) {
			leftLabels[i] = new LeftLabelPanel(i);
			leftPanel.add(leftLabels[i]);
		}
		
		bottomPanel.setLayout(new GridLayout(1, cols));
		bottomPanel.setBackground(bgColor);
		bottomPanel.setForeground(fgColor);
		bottomLabels = new BottomLabelPanel[cols];
		
		for(int i = 0; i < cols; i++) {
			bottomLabels[i] = new BottomLabelPanel(i);
			bottomPanel.add(bottomLabels[i]);
		}
		
		centerPanel.revalidate();
		centerPanel.repaint();
		bottomPanel.revalidate();
		bottomPanel.repaint();
		leftPanel.revalidate();
		leftPanel.repaint();
		
		resize();
	}
	
	protected Color getCellColor(Double intensity) {
		int index = matrix.distinctIntensitiesArray.indexOf(intensity);
		Color c = matrix.setting.getColorGradient().getColor(index);
		return c;
	}
	
	protected class CellComponent extends JComponent implements MouseListener {
		
		private Gene gene;
		private int row, column;
		private double cellWidth, cellHeight;
		protected HashMap<GenePair, Boolean> selectedGenePairs = new HashMap<GenePair, Boolean>();
		protected Set<SNP> selectedSNPs = new HashSet<SNP>();
		
		public CellComponent(String geneName, int row, int column) {
			this.gene = matrix.getData().getGenes().getGene(geneName);
			this.addMouseListener(this);
			setBackground(bgColor);
			setForeground(fgColor);
			this.row = row;
			this.column = column;
		}
		
		public void resize() {
			int numGenes = matrix.genesToUse.size();
			int minWidth = (int)Math.ceil(matrix.setting.getMinCellWidth() * numGenes);
			int minHeight = (int)Math.ceil(matrix.setting.getMinCellHeight() * numGenes);
			
			this.setMinimumSize(new Dimension(minWidth, minHeight));
			this.setPreferredSize(new Dimension(minWidth, minHeight));
			revalidate();
			repaint();
		}
		
		public int getRow() {
			return this.row;
		}
		
		public int getColumn() {
			return this.column;
		}
		
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			
			String geneName = gene.getName();
			
			Rectangle2D border = new Rectangle2D.Double(1, 1, this.getWidth()-2, this.getHeight()-2);
			g2.setStroke(borderStroke);
			
			//submatrix border
			if(matrix.getViewModel().isSelected(gene)) {
				Color r = matrix.setting.getSelectionColor();
				g2.setColor(r);
				g2.draw(border);
				Color sbg = new Color(r.getRed(), r.getGreen(), r.getBlue(), 40);
				g2.setColor(sbg);
				g2.fill(border);
			} else {
				g2.setColor(Color.DARK_GRAY);
				g2.draw(border);
			}
			
			
			//geneName
			g2.setColor(fgColor);
			g2.setStroke(normalStroke);
			
			Rectangle2D bounds = g2.getFontMetrics().getStringBounds(geneName, g2);
			g2.drawString(geneName, (int)((getWidth()-bounds.getWidth())/2), (int)bounds.getHeight());
			
			//if there is already data to display
			if(matrix.genesToCells.size() > 0) {
				List<Integer> cellIDs = matrix.genesToCells.get(geneName);
				
				cellWidth = (this.getWidth()-4.) / matrix.genesToUse.size();
				cellHeight = (this.getHeight()-4.) / matrix.genesToUse.size();
				
				AffineTransform af = g2.getTransform();
				
				for(int i = 0; i < cellIDs.size(); i++) {
					Rectangle2D cell = new Rectangle2D.Double(0, 0, cellWidth, cellHeight);
					GenePair genePair = matrix.cellsToGenePairs.get(cellIDs.get(i));
					int index1 = matrix.genesToUse.indexOf(genePair.gene1);
					int index2 = matrix.genesToUse.indexOf(genePair.gene2);
					
					if(index1 > index2) {
						int tmp = index1;
						index1 = index2;
						index2 = tmp;
					}
					
					g2.translate(index1 * cellWidth + 2, index2 * cellHeight + 2);
					g2.setColor(getCellColor(matrix.cellIntensities.get(cellIDs.get(i))));
					g2.fill(cell);
					g2.setColor(fgColor);
					
					if(selectedGenePairs.size() > 0) {
						if(selectedGenePairs.get(genePair)) {
							g2.setColor(matrix.setting.getSelectionColor());
							Rectangle2D cellHighlight = new Rectangle2D.Double(cell.getX()+2, cell.getY()+2, cell.getWidth()-3, cell.getHeight()-3);
							g2.setStroke(borderStroke);
							g2.draw(cellHighlight);
							g2.setColor(fgColor);
							g2.setStroke(normalStroke);
						}
					}
					
					g2.draw(cell);
					g2.setTransform(af);
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent me) {}

		@Override
		public void mouseEntered(MouseEvent me) {}

		@Override
		public void mouseExited(MouseEvent me) {}

		@Override
		public void mousePressed(MouseEvent me) {
			if(matrix.setting.isGeneSelectionMode()) {
				if(me.isControlDown()) {
					matrix.getViewModel().toggleProbeSelected(gene);
				} else {
					matrix.getViewModel().setProbeSelection(gene);
				}
			}
			if(matrix.setting.isSNPSelectionMode()) {
				int mX = me.getX();
				int mY = me.getY();
				
				int boxX = (int)Math.round((mX-2+cellWidth/2) / cellWidth) - 1;
				int boxY = (int)Math.round((mY-4+cellHeight/2) / cellHeight) - 1;
				
//				System.out.println(boxX + " : " + boxY);
				
				if(boxY >= boxX && boxX >= 0) {
					Gene g1 = (Gene)matrix.genesToUse.getProbe(boxX);
					Gene g2 = (Gene)matrix.genesToUse.getProbe(boxY);
					
					GenePair gp = new GenePair(g1, g2);
					
//					System.out.println(g1.getName());
//					System.out.println(g2.getName());
					
					List<Integer> cellIDs = matrix.genesToCells.get(gene.getName());
					
					//if selectedGenePairs has not been initialized yet
					if(selectedGenePairs.size() == 0) {
						clearSelection();
					}
					
					if(me.isControlDown()) {
						boolean selected = selectedGenePairs.get(gp);
						selectedGenePairs.put(gp, !selected);
					} else {
						clearSelection();
						selectedGenePairs.put(gp, true);
					}
					
					overallSelectedSNPS.removeAll(selectedSNPs);
					selectedSNPs.clear();
					for(GenePair gpair : selectedGenePairs.keySet()) {
						if(selectedGenePairs.get(gpair) == true) {
							for(int id : cellIDs) {
								GenePair gp2 = matrix.cellsToGenePairs.get(id);
								if(gp2.equals(gpair)) {
									Set<SNP> snpsToAdd = matrix.cellsToSNPs.get(id); 
									selectedSNPs.addAll(snpsToAdd);
									break;
								}
							}
						}
					}
					overallSelectedSNPS.addAll(selectedSNPs);
					matrix.getViewModel().setSNPSelection(overallSelectedSNPS);
					
					matrix.updatePlot();
				} else {
					clearSelection();
				}
			}
		}

		private void clearSelection() {
			//set all values to false
			for(GenePair gpt : matrix.cellsToGenePairs.values()) {
				selectedGenePairs.put(gpt, false);
			}
		}

		@Override
		public void mouseReleased(MouseEvent me) {}
	}
	
	private class LeftLabelPanel extends JPanel {
		
		private int row;
		
		public LeftLabelPanel(int row) {
			setBackground(bgColor);
			setForeground(fgColor);
			this.row = row;
		}
		
		public void resize() {
			int numGenes = matrix.genesToUse.size();
			int minWidth = (int)Math.ceil(matrix.setting.getHeaderSize());
			int minHeight = (int)Math.ceil(matrix.setting.getMinCellHeight() * numGenes);
			
			this.setMinimumSize(new Dimension(minWidth, minHeight));
			this.setPreferredSize(new Dimension(minWidth, minHeight));
			revalidate();
			repaint();
		}
		
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			
			double cellHeight = (this.getHeight()-4.) / matrix.genesToUse.size();
			
			if(this.row == snpMouseListener.getRow()) {
				Rectangle2D highlightRect = new Rectangle2D.Double(0, snpMouseListener.getMouseY() - cellHeight/2., getWidth(), cellHeight);
				g2.setColor(labelHighlightColor);
				g2.fill(highlightRect);
				g2.setColor(fgColor);
			}
			
			AffineTransform af = g2.getTransform();
			
			int index = 0;
			for(Probe gene : matrix.genesToUse) {
				String geneName = gene.getName();
				Rectangle2D bounds = g2.getFontMetrics().getStringBounds(geneName, g2);
				g2.translate(0, index * cellHeight + bounds.getHeight()/2. - 1 + cellHeight/2.);
//				g2.translate(getWidth() - bounds.getWidth()-2, 0);
				g.drawString(geneName, 0, 0);
				g2.setTransform(af);
				index++;
			}
		}
	}
	
	private class BottomLabelPanel extends JPanel {
		
		private int column;
		
		public BottomLabelPanel(int column) {
			setBackground(bgColor);
			setForeground(fgColor);
			this.column = column;
		}
		
		public void resize() {
			int numGenes = matrix.genesToUse.size();
			int minWidth = (int)Math.ceil(matrix.setting.getMinCellWidth() * numGenes);
			int minHeight = (int)Math.ceil(matrix.setting.getHeaderSize());
			
			this.setMinimumSize(new Dimension(minWidth, minHeight));
			this.setPreferredSize(new Dimension(minWidth, minHeight));
			revalidate();
			repaint();
		}
		
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			
			double cellWidth = (this.getWidth()-4.) / matrix.genesToUse.size();
			
			if(this.column == snpMouseListener.getColumn()) {
				Rectangle2D highlightRect = new Rectangle2D.Double(snpMouseListener.getMouseX() - cellWidth/2., 0, cellWidth, getHeight());
				g2.setColor(labelHighlightColor);
				g2.fill(highlightRect);
				g2.setColor(fgColor);
			}
			
			AffineTransform af = g2.getTransform();
			
			int index = matrix.genesToUse.size() - 1;
			for(Probe gene : matrix.genesToUse) {
				String geneName = gene.getName();
				Rectangle2D bounds = g2.getFontMetrics().getStringBounds(geneName, g2);
				
				g2.rotate( Math.PI / 2 ); 
				g2.translate( 0, -getWidth() );
				g2.translate(0, index * cellWidth - bounds.getHeight()/2. + cellWidth/2.);
				g.drawString(geneName, 0, (int)bounds.getHeight());
				g2.setTransform(af);
				index--;
			}
		}
	}
	
	private class SNPMouseListener implements MouseMotionListener {
		
		private int mouseX = 0;
		private int mouseY = 0;
		private int highlightRow = -1;
		private int highlightColumn = -1;
		
		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			CellComponent cc = (CellComponent)e.getSource();
			highlightRow = cc.getRow();
			highlightColumn = cc.getColumn();
			repaint();
		}
		
		public int getMouseX() {
			return this.mouseX;
		}
		
		public int getMouseY() {
			return this.mouseY;
		}
		
		public int getRow() {
			return this.highlightRow;
		}
		
		public int getColumn() {
			return this.highlightColumn;
		}
	}
	
	/**
	 * resize all components
	 */
	public void resize() {
		for(int i = 0; i < cellComponents.length; i++) {
			cellComponents[i].resize();
		}
		
		for(int i = 0; i < leftLabels.length; i++) {
			leftLabels[i].resize();
		}
		
		for(int i = 0; i < bottomLabels.length; i++) {
			bottomLabels[i].resize();
		}
	}
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(bgColor);
		g2.clearRect(0, 0, getWidth(), getHeight());
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		super.paint(g2);
	}
}
