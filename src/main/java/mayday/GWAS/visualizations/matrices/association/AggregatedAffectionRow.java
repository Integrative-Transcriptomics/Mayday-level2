package mayday.GWAS.visualizations.matrices.association;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mayday.GWAS.data.GeneList;
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
	
	private AggregationSetting setting;
	
	public AggregatedAffectionRow(AssociationMatrix matrix) {
		this.matrix = matrix;
		this.setLayout(new BorderLayout());
		
		setting = new AggregationSetting(matrix);
		
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
		}
		
		setting.getColorGradient().setMax(max);
		setting.getColorGradient().setMin(min);
		
		this.data = new DoubleMatrix(2, genes.size());
		
		this.data.setRow(0, affectedValues);
		this.data.setRow(1, unaffectedValues);
	}

	public void resizeComps(int rowWidth, int placeHolderWidth) {
		MatrixSetting setting = matrix.getMatrixComponent().getSetting();
		
		int compWidth = setting.getCellWidth() * data.ncol();
		int compHeight = setting.getCellHeight() * data.nrow();
		
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
			
			MatrixSetting matrixSetting = matrix.getMatrixComponent().getSetting();
			
//			double max = data.getMaxValue(false) * setting.getCircleScaling();
			int cellWidth = matrixSetting.getCellWidth();
			int cellHeight = matrixSetting.getCellHeight();
//			int cellSize = Math.min(cellWidth, cellHeight);
			
			ColorGradient gradient = getSetting().getColorGradient();
			
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
			
			int cellHeight = matrix.getMatrixComponent().getSetting().getCellHeight();

			AffineTransform af = g2.getTransform();
			
			String[] rowNames = {"Affected", "Unaffected"};
			
			for(int i = 0; i < data.nrow(); i++) {
				String headerString = rowNames[i];
				
				Rectangle2D bounds = g2.getFontMetrics().getStringBounds(headerString, g2);
				
				if(bounds.getHeight()/1.5 <= cellHeight) {
					g2.translate(0, i * cellHeight + 1);
					
					g2.translate(0, bounds.getHeight() + (cellHeight - bounds.getHeight())/2. - 2);
					g2.drawString(headerString, 0, 0);
					g2.setTransform(af);
				}
			}
		}
	}

	public AggregationSetting getSetting() {
		return this.setting;
	}
}
