package mayday.Reveal.visualizations.matrices.association;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import mayday.Reveal.data.Gene;
import mayday.Reveal.data.SNV;
import mayday.Reveal.viewmodel.RevealViewModel;
import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.core.MaydayDefaults;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

@SuppressWarnings("serial")
public class MatrixComponent extends JPanel implements ViewModelListener {

	private String[] rowHeader = new String[0];
	private String[] colHeader = new String[0];
	
	private DataComponent dataComp;
	private RowHeaderComponent rowComp;
	private ColHeaderComponent colComp;
	
	protected JScrollPane compScroller;
	protected JScrollPane rowScroller;
	protected JScrollPane colScroller;
	
	private JPanel colPanel;
	private PlaceHolder placeHolderLeft;
	private PlaceHolder placeHolderRight;
	
	protected AssociationMatrix matrix;
	
	private AggregatedAffectionRow aggregatedAffectionRow;
	
	public MatrixComponent(AssociationMatrix matrix_arg, String[] colHeader, String[] rowHeader) {
		this.matrix = matrix_arg;
		this.colHeader = colHeader;
		this.rowHeader = rowHeader;
		
		this.setLayout(new BorderLayout());
		
		this.dataComp = new DataComponent();
		this.rowComp = new RowHeaderComponent();
		this.colComp = new ColHeaderComponent();
		this.aggregatedAffectionRow = new AggregatedAffectionRow(matrix_arg);
		
		compScroller = new JScrollPane(dataComp);
		add(compScroller, BorderLayout.CENTER);
		
		JPanel top = new JPanel(new BorderLayout());
		
		colPanel = new JPanel(new BorderLayout());
		colPanel.add(placeHolderLeft = new PlaceHolder(), BorderLayout.WEST);
		colScroller = new JScrollPane(colComp);
		colPanel.add(colScroller, BorderLayout.CENTER);
		colPanel.add(placeHolderRight = new PlaceHolder(), BorderLayout.EAST);
		
		top.add(colPanel, BorderLayout.NORTH);
		top.add(aggregatedAffectionRow, BorderLayout.SOUTH);
		add(top, BorderLayout.NORTH);
		
		rowScroller = new JScrollPane(rowComp);
		add(rowScroller, BorderLayout.WEST);
		
		JScrollBar hSBcomp = compScroller.getHorizontalScrollBar();
		JScrollBar hSBcol = colScroller.getHorizontalScrollBar();
		
		JScrollBar sb1 = new JScrollBar(hSBcol.getOrientation()) {
			public void paint(Graphics g) {
				g.setColor(Color.WHITE);
				g.clearRect(0, 0, getWidth(), getHeight());
				if(!matrix.isExporting())
					super.paint(g);
			}
		};
		sb1.setBackground(Color.WHITE);
		sb1.setOpaque(false);
		
		colScroller.setHorizontalScrollBar(sb1);
		hSBcol = sb1;
		
		hSBcol.setModel(hSBcomp.getModel()); // synchronize scrollbars
		
		//hide scrollbars
		colScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		colScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		JScrollBar vSBcomp = compScroller.getVerticalScrollBar();
		JScrollBar vSBrow = rowScroller.getVerticalScrollBar();
		
		JScrollBar sb2 = new JScrollBar(vSBcomp.getOrientation()) {
			public void paint(Graphics g) {
				g.setColor(Color.WHITE);
				g.clearRect(0, 0, getWidth(), getHeight());
				if(!matrix.isExporting())
					super.paint(g);
			}
		};
		
		compScroller.setVerticalScrollBar(sb2);
		vSBcomp = sb2;
		
		vSBrow.setModel(vSBcomp.getModel()); // synchronize scrollbars
		
		
		//hide scrollbars
		rowScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		rowScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		
		JScrollPane aggScroller = this.aggregatedAffectionRow.getDataCompScroller();
		JScrollBar hAGcomp = aggScroller.getHorizontalScrollBar();
		hAGcomp.setModel(hSBcomp.getModel());
		
		rowScroller.setBorder(null);
		colScroller.setBorder(null);
		
		compScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		compScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		//remove scrollpane border
		compScroller.setViewportBorder(null);
		compScroller.setBorder(null);
	}
	
	public void setViewModel() {
		//add view model listener
		RevealViewModel viewModel = matrix.getViewModel(); 
		viewModel.addViewModelListener(dataComp);
		viewModel.addViewModelListener(colComp);
		viewModel.addViewModelListener(this);
	}
	
	public void removeNotify() {
		if(getViewModel() != null)
			getViewModel().removeViewModelListener(this);
		super.removeNotify();
	}
	
	public RevealViewModel getViewModel() {
		return matrix.getViewModel();
	}
	
	private boolean initial = true;
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setBackground(Color.white);
		g2.clearRect(0,0, getWidth(), getHeight());
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		super.paint(g2);
		
		if(initial) {
			resize();
			initial = false;
		}
	}
	
	private Integer rowWidth, colHeight;
	
	private int getColumnHeaderSize(Graphics g) {
		if(colHeight != null)
			return colHeight;
		
		colHeight = 0;
		
		for(int i = 0; i < matrix.getDataMatrix()[0].length; i++) {
			String id = colHeader[i];
			
			FontRenderContext l_frc = new FontRenderContext( MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.getTransform(), 
					false, MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.usesFractionalMetrics() );

			TextLayout sLayout = new TextLayout( id, MaydayDefaults.DEFAULT_PLOT_SMALL_LEGEND_FONT, l_frc );
			
			Rectangle2D bounds = sLayout.getBounds();
			if(bounds.getWidth() > colHeight)
				colHeight = (int)Math.rint(bounds.getWidth());
		}
		
		return colHeight;
	}
	
	private int getRowHeaderSize(Graphics g) {
		if(rowWidth != null)
			return rowWidth.intValue();
		
		rowWidth = 0;
		
		for(int i = 0; i < matrix.getDataMatrix().length; i++) {
			String id = rowHeader[i];
			Rectangle2D bounds = g.getFontMetrics().getStringBounds(id, g);
			if(bounds.getWidth() > rowWidth)
				rowWidth = (int)Math.rint(bounds.getWidth());
		}
		
		String a = "Affected";
		String ua = "Unaffected";
		
		FontRenderContext l_frc = new FontRenderContext( MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.getTransform(), 
				false, MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.usesFractionalMetrics() );

		TextLayout sLayout = new TextLayout( a, MaydayDefaults.DEFAULT_PLOT_SMALL_LEGEND_FONT, l_frc );
		
		Rectangle2D bounds = sLayout.getBounds();
		if(bounds.getWidth() > rowWidth)
			rowWidth = (int)Math.rint(bounds.getWidth());
		
		sLayout = new TextLayout( ua, MaydayDefaults.DEFAULT_PLOT_SMALL_LEGEND_FONT, l_frc );
		
		bounds = sLayout.getBounds();
		if(bounds.getWidth() > rowWidth)
			rowWidth = (int)Math.rint(bounds.getWidth());
		
		return rowWidth;
	}
	
	public void resize() {
		
		int compWidth = matrix.setting.getCellWidth() * matrix.getDataMatrix()[0].length;
		int compHeight = matrix.setting.getCellHeight() * matrix.getDataMatrix().length;
		
		Graphics g = this.getGraphics();
		int rowWidth = getRowHeaderSize(g) + 5;
		int colHeight = getColumnHeaderSize(g) + 5;
		
		int scrollBarSize = (int)compScroller.getVerticalScrollBar().getPreferredSize().getWidth();
		
		dataComp.setPreferredSize(new Dimension(compWidth, compHeight));
		colComp.setPreferredSize(new Dimension(compWidth, colHeight));
		rowComp.setPreferredSize(new Dimension(rowWidth, compHeight));
		placeHolderLeft.setPreferredSize(new Dimension(rowWidth, colHeight));
		placeHolderRight.setPreferredSize(new Dimension(scrollBarSize, colHeight));

		dataComp.revalidate();
		colComp.revalidate();
		rowComp.revalidate();
		
		revalidate();
		
		this.aggregatedAffectionRow.resizeComps(rowWidth, scrollBarSize);
		
		repaint();
	}
	
	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case ViewModelEvent.PROBE_SELECTION_CHANGED: 
			break;
		case RevealViewModelEvent.SNP_SELECTION_CHANGED:
			break;
		}
	}
	
	public void setRowHeader(String[] rowHeader) {
		this.rowHeader = rowHeader;
	}
	
	public void setColHeader(String[] colHeader) {
		this.colHeader = colHeader;
	}
	
	private class DataComponent extends JPanel implements MouseListener, ViewModelListener, MouseWheelListener {
		
		public DataComponent() {
			addMouseListener(this);
			addMouseWheelListener(this);
		}
		
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setBackground(Color.white);
			g2.clearRect(0,0, getWidth(), getHeight());
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			super.paint(g2);
		}
		
		public void removeNotify() {
			if(getViewModel() != null)
				getViewModel().removeViewModelListener(this);
			super.removeNotify();
		}
		
		@Override
		public void viewModelChanged(ViewModelEvent vme) {
			switch(vme.getChange()) {
			case RevealViewModelEvent.SNP_SELECTION_CHANGED:
				repaint();
				break;
			case RevealViewModelEvent.PROBE_SELECTION_CHANGED:
				repaint();
				break;
			}		
		}
		
		public void paintComponent(Graphics g) {
			double max = matrix.setting.getCircleScaling();
			int cellWidth = matrix.setting.getCellWidth();
			int cellHeight = matrix.setting.getCellHeight();
			int cellSize = Math.min(cellWidth, cellHeight);
			
			Graphics2D g2d = (Graphics2D)g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setBackground(Color.WHITE);
			g2d.clearRect(0, 0, getWidth(), getHeight());
			
			Color selectionColor = matrix.setting.getSelectionColor();
			ColorGradient gradient = matrix.setting.getBetaColorGradient();
			
			g2d.setColor(Color.DARK_GRAY);
			for(int i = 0; i < matrix.getDataMatrix().length; i++) {
				Integer rowIndex = matrix.sortedIndices.get(i);
				for(int j = 0; j < matrix.getDataMatrix()[0].length; j++) {
					double val = matrix.getDataMatrix()[rowIndex.intValue()][j] == null ? 
							0 : matrix.getDataMatrix()[rowIndex.intValue()][j].getSizeValue();

					int diameter = (int)Math.rint(((val / max) * cellSize));
					
					//this is the global maximum!
					if(diameter > cellSize)
						diameter = cellSize;
					
					int x = (cellWidth - diameter)/2;
					int y = (cellHeight - diameter)/2;
					
					double beta = matrix.getDataMatrix()[rowIndex.intValue()][j] == null ? 
							0 : matrix.getDataMatrix()[rowIndex.intValue()][j].getColorValue();
					
					Color c = gradient.mapValueToColor(beta);
					g2d.setColor(c);
					g2d.fillOval(j*cellWidth+x, i*cellHeight+y, diameter, diameter);
				}
				
				if(selectedIndices.contains(i)) {
					Color c = new Color(selectionColor.getRed(), selectionColor.getGreen(), selectionColor.getBlue(), 100);
					g2d.setColor(c);
					Rectangle2D rec = new Rectangle2D.Double(0, i*cellHeight, getWidth(), cellHeight);
					g2d.fill(rec);
					g2d.setColor(Color.BLACK);
				}
			}
			
			for(int i = 0; i < matrix.getDataMatrix()[0].length; i++) {
				Gene gene = matrix.getGenes().getGene(i);
				if(getViewModel().isSelected(gene)) {
					Color c = new Color(selectionColor.getRed(), selectionColor.getGreen(), selectionColor.getBlue(), 100);
					g2d.setColor(c);
					Rectangle2D rec = new Rectangle2D.Double(i*cellWidth, 0, cellWidth, getHeight());
					g2d.fill(rec);
					g2d.setColor(Color.BLACK);
				}
			}
			
			if(matrix.setting.plotDiagonal()) {
				int maxWidth = cellWidth * matrix.getDataMatrix()[0].length;
				int maxHeight = cellHeight * matrix.getDataMatrix().length;
				
				Line2D diagonalLine = new Line2D.Double(0, 0, maxWidth, maxHeight);
				g2d.setColor(Color.BLUE);
				g2d.draw(diagonalLine);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON1) {
				int x = e.getX();
				int cellSize = matrix.setting.getCellWidth();
				int xIndex = x / cellSize;
				
				cellSize = matrix.setting.getCellHeight();
				int y = e.getY();
				int yIndex = (y-3) / cellSize;
				
				boolean xSel = false, ySel = false;
				
				if(xIndex >= 0 && xIndex < colHeader.length) {
					if(yIndex < rowHeader.length) {
						Gene gene = matrix.getGenes().getGene(xIndex);
						if(e.isControlDown()) {
							getViewModel().toggleProbeSelected(gene);
						} else {
							getViewModel().setProbeSelection(gene);
						}
						xSel = true;
					}
				}
				
				if(yIndex >= 0 && yIndex < rowHeader.length) {
					if(xIndex < colHeader.length) {
						if(e.isControlDown()) {
							if(selectedIndices.contains(yIndex)) 
								selectedIndices.remove(yIndex);
							else
								selectedIndices.add(yIndex);
						} else {
							selectedIndices.clear();
							selectedIndices.add(yIndex);
						}
						ySel = true;
					}
				}
				
				if(xSel && ySel) {
					RevealViewModel model = getViewModel();
					Set<SNV> snps = matrix.getSNPsInCell(yIndex, xIndex);
					
					if(e.isControlDown()) {
						Set<SNV> selectedSNPs = model.getSelectedSNPs();
						Set<SNV> newSelection = new HashSet<SNV>(selectedSNPs);

						if(snps != null) {
							if(newSelection.containsAll(snps)) {
								newSelection.removeAll(snps);
							} else {
								newSelection.addAll(snps);
							}
							
							model.setSNPSelection(newSelection);
						}
					} else {
						if(snps != null)
							model.setSNPSelection(snps);
						else {	
							model.setSNPSelection(new HashSet<SNV>());
						}
					}
				}
			}
			
			rowComp.repaint();
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {}

		@Override
		public void mouseExited(MouseEvent arg0) {}

		@Override
		public void mousePressed(MouseEvent arg0) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int rot = e.getWheelRotation();
			if(e.isControlDown()) {
				if(e.isShiftDown() && ! e.isAltDown()) {
					matrix.setting.modifyCellHeight(rot);
				} else if(e.isAltDown() && !e.isShiftDown()) {
					matrix.setting.modifyCellWidth(rot);
				} else {
					matrix.setting.modifyCellSize(rot);
				}
				
				MatrixComponent.this.resize();
			}
		}
	}
	
	private Set<Integer> selectedIndices = new HashSet<Integer>();
	
	private class RowHeaderComponent extends JPanel implements MouseListener {		
		
		public RowHeaderComponent() {
			addMouseListener(this);
		}
		
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setBackground(Color.white);
			g2.clearRect(0,0, getWidth(), getHeight());
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			super.paint(g2);
		}
		
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			
			int cellHeight = matrix.setting.getCellHeight();

			AffineTransform af = g2.getTransform();
			Color selectionColor = matrix.setting.getSelectionColor();
			
			FontRenderContext l_frc = new FontRenderContext( MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.getTransform(), 
					false, MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.usesFractionalMetrics() );
			
			for(int i = 0; i < matrix.getDataMatrix().length; i++) {
				Integer rowIndex = matrix.sortedIndices.get(i);
				if(rowIndex == null)
					continue;
				String headerString = rowHeader[rowIndex.intValue()];

				TextLayout sLayout = new TextLayout( headerString, MaydayDefaults.DEFAULT_PLOT_SMALL_LEGEND_FONT, l_frc );
				
				Rectangle2D bounds = sLayout.getBounds();
				
				if(bounds.getHeight()/1.5 <= cellHeight) {
					g2.translate(0, i * cellHeight + 1);
					
					if(selectedIndices.contains(i)) {
						Color c = new Color(selectionColor.getRed(), selectionColor.getGreen(), selectionColor.getBlue(), 100);
						g2.setColor(c);
						Rectangle2D rec = new Rectangle2D.Double(0, 0, getWidth(), cellHeight);
						g2.fill(rec);
						g2.setColor(Color.BLACK);
					}
					
					g2.translate(0, bounds.getHeight() + (cellHeight - bounds.getHeight())/2. - 2);
					sLayout.draw(g2, 0, 0);
					g2.setTransform(af);
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON1) {
				int y = e.getY();
				
				int cellSize = matrix.setting.getCellHeight();
				int index = (y-3) / cellSize;
				
				if(index >= 0 && index < rowHeader.length) {
					if(e.isControlDown()) {
						if(selectedIndices.contains(index))
							selectedIndices.remove(index);
						else
							selectedIndices.add(index);
					} else {
						selectedIndices.clear();
						selectedIndices.add(index);
					}
				}
				
				repaint();
				dataComp.repaint();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
	}
	
	private class ColHeaderComponent extends JPanel implements MouseListener, ViewModelListener {
		
		public ColHeaderComponent() {
			addMouseListener(this);
		}
		
		public void removeNotify() {
			if(getViewModel() != null)
				getViewModel().removeViewModelListener(this);
			super.removeNotify();
		}
		
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setBackground(Color.white);
			g2.clearRect(0,0, getWidth(), getHeight());
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			super.paint(g2);
		}
		
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			
			int cellWidth = matrix.setting.getCellWidth();
			
			AffineTransform af = g2.getTransform();
			
			Color c = matrix.setting.getSelectionColor();
			Color c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 100);
			
			FontRenderContext l_frc = new FontRenderContext( MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.getTransform(), 
					false, MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.usesFractionalMetrics() );
			
			for(int i = 0; i < matrix.getDataMatrix()[0].length; i++) {

				String headerString = colHeader[i];

				TextLayout sLayout = new TextLayout( headerString, MaydayDefaults.DEFAULT_PLOT_SMALL_LEGEND_FONT, l_frc );
				
				Rectangle2D bounds = sLayout.getBounds();
				
				if(bounds.getHeight()/1.5 <= cellWidth) {
					Gene gene = matrix.getGenes().getGene(i);
					
					if(getViewModel().isSelected(gene)) {
						g2.translate((i+1) * cellWidth, 0);
						g2.rotate( Math.PI / 2 );
						g2.setColor(c2);
						Rectangle2D rec = new Rectangle2D.Double(0, 0, getHeight(), cellWidth);
						g2.fill(rec);
						g2.setColor(Color.BLACK);
						g2.setTransform(af);
					}
					
					g2.translate((i+1) * cellWidth - (cellWidth - bounds.getHeight())/2., getHeight() - bounds.getWidth() - 5);
					g2.rotate( Math.PI / 2 );
					
					sLayout.draw(g2, 0, 10);
					
					g2.setTransform(af);
				}
			}
		}

		@Override
		public void viewModelChanged(ViewModelEvent vme) {
			switch(vme.getChange()) {
			case RevealViewModelEvent.PROBE_SELECTION_CHANGED:
				repaint();
				break;
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
//			if(e.getButton() == MouseEvent.BUTTON1) {
//				int x = e.getX();
//				int index = x / matrix.setting.getCellWidth();
//				
//				if(index < colHeader.length && index >= 0) {
//					if(e.getClickCount() > 1) {
//						AbstractVector template = matrix.getDataMatrix().getColumn(index);
//						matrix.sort(template);
//					} else {
//						Gene gene = matrix.getGenes().getGene(index);
//						if(e.isControlDown()) {
//							getViewModel().toggleProbeSelected(gene);
//						} else {
//							getViewModel().setProbeSelection(gene);
//						}
//					}
//				}
//			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
	}
	
	private class PlaceHolder extends JComponent {
		
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setBackground(Color.white);
			g2.clearRect(0,0, getWidth(), getHeight());
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			super.paint(g2);
		}
		
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setBackground(Color.WHITE);
			g2.clearRect(0, 0, getWidth(), getHeight());
		}
	}

	public void updatePlot() {
		repaint();
	}

	public void setGradient(double betaMin, double betaMax, int numBeta) {
		if(matrix.setting != null) {
			matrix.setting.setBetaGradient(betaMin, betaMax, numBeta);
		}
	}

	public AggregatedAffectionRow getAggregationComp() {
		return this.aggregatedAffectionRow;
	}
}
