package mayday.Reveal.visualizations.SLProfilePlot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.Reveal.data.Gene;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.meta.Genome;
import mayday.Reveal.visualizations.SNPValueProvider;
import mayday.vis3.SparseZBuffer;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.vis2base.ChartComponent;
import mayday.vis3.vis2base.ChartSetting;
import mayday.vis3.vis2base.DataSeries;
import mayday.vis3.vis2base.PointIterator;
import mayday.vis3.vis2base.Shape;
import mayday.vis3.vis2base.VisibleRectSetting;

@SuppressWarnings("serial")
public class SLProfilePlotComponent extends ChartComponent {

	protected SLProfilePlot plot;
	
	protected DataSeries[] layers;
	protected DataSeries selectionLayer;
	protected Gene gene;
	
	protected Rectangle selRect;
	
	protected SNPValueProvider X;
	protected SNPValueProvider Y;
	
	SparseZBuffer szb = new SparseZBuffer();
	
	public SLProfilePlotComponent(SLProfilePlot thePlot, Gene g) {
		this.plot = thePlot;
		this.gene = g;
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
				double[] clicked = getPoint(e.getX(), e.getY());
				SNV s = (SNV)szb.getObject(clicked[0], clicked[1]);
				switch(e.getButton()) {
				case MouseEvent.BUTTON1:
					if(s != null) {
						if((e.getModifiers()&CONTROLMASK) == CONTROLMASK) {
							plot.getViewModel().toggleSNPSelected(s);
						} else {
							plot.getViewModel().setSNPSelection(s);
						}
					}
					break;
				}
			}
			
			public void mouseReleased(MouseEvent e) {
				if(selRect != null) {
					Graphics2D g = ((Graphics2D)farea.getGraphics());
					drawSelectionRectangle(g);
					int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
					boolean control = ((e.getModifiers()&CONTROLMASK) == CONTROLMASK);
					boolean alt = e.isAltDown();
					selectByRectangle(selRect, control, alt);
					selRect = null;
				}
			}
		});
		
		farea.addMouseMotionListener(new MouseMotionListener() {
			protected Point dragPoint;
			protected Point targPoint;
			
			@Override
			public void mouseDragged(MouseEvent e) {
				Graphics2D g = ((Graphics2D)farea.getGraphics());
				if(selRect == null) {
					dragPoint = e.getPoint();
				} else {
					drawSelectionRectangle(g);
				}
				targPoint = e.getPoint();
				selRect = new Rectangle(dragPoint, new Dimension(1,1));
				selRect.add(targPoint);
				drawSelectionRectangle(g);
			}

			@Override
			public void mouseMoved(MouseEvent e) {}
		});
	}
	
	@Override
	public void createView() {
		if(plot.getViewModel() == null)
			return;
		
		selRect = null;
		
		layers = new DataSeries[]{view(plot.snps, false), view(gene)};
		layers[0].setColor(Color.DARK_GRAY);
		addDataSeries(layers[0]);
		addDataSeries(layers[1]);
		
		select(plot.setting.getSelectionColor());
	}

	public void select(Color selectionColor) {
		Set<SNV> snps = new HashSet<SNV>();
		snps.addAll(plot.snps);
		
		snps.retainAll(plot.getViewModel().getSelectedSNPs());
		
		if(selectionLayer != null) {
			removeDataSeries(selectionLayer);
		}
		
		selectionLayer = view(snps, true);
		selectionLayer.setColor(selectionColor);
		selectionLayer.setStroke(new BasicStroke(3));
		
		addDataSeries(selectionLayer);
		
		clearBuffer();
		repaint();
	}

	@Override
	public String getAutoTitleY(String ytitle) {
		if(Y != null) {
			return Y.getSourceName();
		}
		return ytitle;
	}

	@Override
	public String getAutoTitleX(String xtitle) {
		if(X != null) {
			return X.getSourceName();
		}
		return xtitle;
	}
	
	public void setup(PlotContainer plotContainer) {		
		if (firstTime) {
			viewModel = plotContainer.getViewModel();
			
			getZoomController().setTarget(fareapanel);
			getZoomController().setAllowXOnlyZooming(true);
			getZoomController().setAllowYOnlyZooming(true);
		}
		plotContainer.addViewSetting(chartSettings, plot);
	}
	
	protected void drawSelectionRectangle(Graphics2D g) {
		if (selRect==null)
			return;
		g.setXORMode(getBackground());
		g.setColor(Color.RED);
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(2));
		g.draw(selRect);
		g.setStroke(oldStroke);
		g.setPaintMode();
	}
	
	protected void selectByRectangle(Rectangle r, boolean control, boolean alt) {
		Set<SNV> newSelection = new HashSet<SNV>();
		double[] clicked1 = getPoint(r.x, r.y);
		double[] clicked2 = getPoint(r.x+r.width, r.y+r.height);
		
		for(SNV s : plot.snps) {
			double xval = X.getValue(s);
			double yval = Y.getValue(s);
			boolean inX = (xval >= clicked1[0] && xval < clicked2[0]);
			boolean inY = (yval < clicked1[1] && yval > clicked2[1]);
			if (inX && inY)
				newSelection.add(s);
		}
		
		Set<SNV> previousSelection = plot.getViewModel().getSelectedSNPs();
		if(control && alt) {
			previousSelection = new HashSet<SNV>(previousSelection);
			previousSelection.removeAll(newSelection);
			newSelection = previousSelection;
		} else if(control) {
			newSelection.addAll(previousSelection);
		} else if(alt) {
			newSelection.retainAll(previousSelection);
		}
		
		plot.getViewModel().setSNPSelection(newSelection);
	}
	
	public SNV getClickedSNP(Point coordinates) {
		return getClickedSNP(coordinates, layers);
	}

	public SNV getClickedSNP(Point coordinates, DataSeries[] layers) {
		double[] clicked = getPoint(coordinates.x, coordinates.y);
		if(clicked ==null)
			return null;
		
		SNV best_snpid = null;
		double min_imprecision = 1;
		double cutoff_imprecision = .1;
		
		double cx = clicked[0];
		double cy = clicked[1];
		
		PointIterator<SNV> it = new PointIterator<SNV>(layers);
		while(it.hasNext()) {
			Double[] point = it.next();
			double px = point[0];
			double py = point[1];
			// fast evaluation of perfect hits
			if (cx==px && cy==py)
				return it.getObject();
			// only check the point if we are left of it (slope only applies there)
			// symmetry to DataSeries.addPoint: clicked[] is the predecessor of point[]
			if ( cx<=px ) {
				double pslope = point[2];
				double cslope = (py-cy)/(px-cx); 
				// compute how far we are from the line defined by point and slope
				double cgamma = Math.atan(cslope);
				double pgamma = Math.atan(pslope);
				//double angle_1 = 90;
				double angle = Math.abs(pgamma-cgamma);
				//double angle_3 = 180-angle_1-angle_2;
				double hyp= Math.sqrt((cx-px)*(cx-px)+(cy-py)*(cy-py));
				double dist = hyp * Math.sin(angle);
				if (dist < min_imprecision && (px-cx)<=it.getStepSize()) {
					best_snpid = it.getObject();
					min_imprecision = dist;
					//					xdist=(px-cx);
					if (dist<cutoff_imprecision)
						break;
				}
			}
		}
		
		return best_snpid;
	}
	
	protected DataSeries view(Gene g) {
		DataSeries geneLayer = new DataSeries();
		
		Genome genome = plot.getData().getGenome();
		
		if(genome != null) {
			double[] position = getGeneLocation();
			
			VisibleRectSetting vrS = plot.setting.chartSetting.synchronizedSetting.getVisibleRect();
			
			double y = vrS.getYmin().getDoubleValue();
			double h = vrS.getYmax().getDoubleValue();
			
			geneLayer.addPoint(position[0], y, g);
			geneLayer.addPoint(position[1], y, g);
			geneLayer.addPoint(position[1], h, g);
			geneLayer.addPoint(position[0], h, g);
			geneLayer.addPoint(position[0], y, g);
		}
		
		geneLayer.setConnected(true);
		geneLayer.setStroke(new BasicStroke(1));
		geneLayer.setColor(plot.setting.getGeneColor());
		geneLayer.setFixedAlpha(.5f);
		return geneLayer;
	}
	
	protected DataSeries view(Collection<SNV> snps, boolean isSelectionLayer) {
		DataSeries series = new DataSeries();
		if(plot.setting.showDots()) {
			series.setShape(new Shape() {
				public void paint(Graphics2D g) {
					g.fillRect(-2, -2, 5, 5);
				}
				public boolean wantDeviceCoordinates() {
					return true;
				}
			});
		}
		
		if(X != null && Y != null) {
			for(SNV s: snps) {
				double xx = X.getValue(s);
				double yy = Y.getValue(s);
				series.addPoint(xx, yy, s);
				szb.setObject(xx, yy, s);
			}
		}
		if(!isSelectionLayer)
			series.setConnected(false);
		else
			series.setConnected(false);
		
		return series;
	}

	public void initValueProviders(PlotContainer plotContainer) {
		if(X == null || Y == null) {
			ChangeListener cl = new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					updatePlot();
				}
			};
			
			X = new SNPValueProvider(plot.getViewModel(), "X axis");
			Y = new SNPValueProvider(plot.getViewModel(), "Y axis");
			
			X.addChangeListener(cl);
			Y.addChangeListener(cl);
			
			X.setProvider(X.new ChromosomalLocationProvider());
			Y.setProvider(Y.new SingleLocusResultProvider(gene));
		}
		
		//TODO add Y.getSetting() to combined setting!
	}

	public ChartSetting getSetting() {
		return this.chartSettings;
	}
	
	public double getMinLocation() {
		double min = X.getMinValue(plot.snps);
		double gMin = getGeneLocation()[0];
		return Math.min(min, gMin);
	}
	
	public double getMaxLocation() {
		double max = X.getMaxValue(plot.snps);
		double gMax = getGeneLocation()[1];
		return Math.max(max, gMax);
	}
	
	public double[] getGeneLocation() {
		Genome genome = plot.getData().getGenome();
		String chromosome = gene.getChromosome();
		int startPosition = gene.getStartPosition();
		int stopPosition = gene.getStopPosition();
		
		long globalStartPos = genome.getGlobalPosition(chromosome, startPosition);
		long globalEndPos = genome.getGlobalPosition(chromosome, stopPosition);
		
		double finalStartPos = globalStartPos / (double)genome.getTotalLength();
		double finalStopPos = globalEndPos / (double)genome.getTotalLength();
		
		return new double[]{finalStartPos, finalStopPos};
	}
}
