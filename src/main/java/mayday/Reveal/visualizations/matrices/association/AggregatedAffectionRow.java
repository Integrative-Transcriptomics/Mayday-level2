package mayday.Reveal.visualizations.matrices.association;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mayday.Reveal.data.GeneList;
import mayday.core.MaydayDefaults;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.vis3.gradient.ColorGradient;

@SuppressWarnings("serial")
public class AggregatedAffectionRow extends JPanel {
	
	private AssociationMatrix matrix;
	private DoubleMatrix data;
	
	private DataComponent dataComp;
	private RowHeader rowHeader;
	private JPanel placeHolderRight;
	private JScrollPane dataScroller;
	
	public AggregatedAffectionRow(AssociationMatrix matrix) {
		this.matrix = matrix;
		this.setLayout(new BorderLayout());
		
		this.dataComp = new DataComponent();
		this.rowHeader = new RowHeader();
		
//		this.calculateAggregationData();
		
		this.add(rowHeader, BorderLayout.WEST);
		
		dataScroller = new JScrollPane(dataComp);
		dataScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		dataScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		dataScroller.setBorder(null);
		
		this.add(dataScroller, BorderLayout.CENTER);
		this.placeHolderRight = new JPanel();
		this.placeHolderRight.setBackground(Color.WHITE);
		this.add(placeHolderRight, BorderLayout.EAST); 
	}
	
	public JScrollPane getDataCompScroller() {
		return this.dataScroller;
	}
	
	public void calculateAggregationData() {
		GeneList genes = matrix.getGenes();
		Integer[] affected = matrix.getData().getSubjects().getAffectedSubjectIndices();
		Integer[] unaffected = matrix.getData().getSubjects().getUnaffectedSubjectIndices();
		
		Set<Double> distinctExpression = new HashSet<Double>();
		
		double[] affectedValues = new double[genes.size()];
		double[] unaffectedValues = new double[genes.size()];
		
		Arrays.fill(affectedValues, 0);
		Arrays.fill(unaffectedValues, 0);
		
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for(int i = 0; i < genes.size(); i++) {
			for(int subjectIndex : affected) {
				affectedValues[i] += matrix.getViewModel().getDataManipulator().getProbeValues(genes.getGene(i))[subjectIndex];
			}
			
			affectedValues[i] /= affected.length;
			
			if(affectedValues[i] > max)
				max = affectedValues[i];
			if(affectedValues[i] < min)
				min = affectedValues[i];
			
			for(int subjectIndex : unaffected) {
				unaffectedValues[i] += matrix.getViewModel().getDataManipulator().getProbeValues(genes.getGene(i))[subjectIndex];
			}
			
			unaffectedValues[i] /= unaffected.length;
			
			if(unaffectedValues[i] > max)
				max = unaffectedValues[i];
			if(unaffectedValues[i] < min)
				min = unaffectedValues[i];
			
			distinctExpression.add(affectedValues[i]);
			distinctExpression.add(unaffectedValues[i]);
		}
		
		matrix.setting.setExpressionColorGradient(min, max, Math.max(distinctExpression.size(),1024));
		
		this.data = new DoubleMatrix(2, genes.size());
		
		this.data.setRow(0, affectedValues);
		this.data.setRow(1, unaffectedValues);
	}

	public void resizeComps(int rowWidth, int placeHolderWidth) {
		int compWidth = matrix.setting.getCellWidth() * data.ncol();
		int compHeight = matrix.setting.getCellHeight() * data.nrow();
		
		dataComp.setPreferredSize(new Dimension(compWidth, compHeight));
		rowHeader.setPreferredSize(new Dimension(rowWidth, compHeight));
		placeHolderRight.setPreferredSize(new Dimension(placeHolderWidth, compHeight));
		
		dataComp.revalidate();
		rowHeader.revalidate();
		placeHolderRight.revalidate();
		
		revalidate();
		repaint();
	}
	
	private class DataComponent extends JPanel {
		
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setBackground(Color.white);
			g2.clearRect(0,0, getWidth(), getHeight());
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			super.paint(g2);
		}
		
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			
//			double max = data.getMaxValue(false) * setting.getCircleScaling();
			int cellWidth = matrix.setting.getCellWidth();
			int cellHeight = matrix.setting.getCellHeight();
//			int cellSize = Math.min(cellWidth, cellHeight);
			
			ColorGradient gradient = matrix.setting.getExpressionColorGradient();
			
			for(int i = 0; i < data.nrow(); i++) {
//				Integer rowIndex = matrix.sortedIndices.get(i);
				for(int j = 0; j < data.ncol(); j++) {
					double val = data.getValue(i, j);
					
					Color c = gradient.mapValueToColor(val);
					g2.setColor(c);
					g2.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
				}
			}
		}
	}
	
	private class RowHeader extends JPanel {
		
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
			
			String[] rowNames = {"Affected", "Unaffected"};
			
			FontRenderContext l_frc = new FontRenderContext( MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.getTransform(), 
					false, MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.usesFractionalMetrics() );
			
			for(int i = 0; i < data.nrow(); i++) {
				String headerString = rowNames[i];
				
				TextLayout sLayout = new TextLayout( headerString, MaydayDefaults.DEFAULT_PLOT_SMALL_LEGEND_FONT, l_frc );
				
				Rectangle2D bounds = sLayout.getBounds();
				
				if(bounds.getHeight()/1.5 <= cellHeight) {
					g2.translate(0, i * cellHeight + 1);
					
					g2.translate(0, bounds.getHeight() + (cellHeight - bounds.getHeight())/2. - 2);
					sLayout.draw(g2, 0, 0);
					g2.setTransform(af);
				}
			}
		}
	}
}
