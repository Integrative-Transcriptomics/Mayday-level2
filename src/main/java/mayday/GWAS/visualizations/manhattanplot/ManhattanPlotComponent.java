package mayday.GWAS.visualizations.manhattanplot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.GWAS.data.SNP;
import mayday.GWAS.visualizations.SNPValueProvider;
import mayday.core.settings.Setting;
import mayday.vis3.SparseZBuffer;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.vis2base.DataSeries;
import mayday.vis3.vis2base.Shape;

@SuppressWarnings("serial")
public class ManhattanPlotComponent extends AbstractManhattanPlotComponent {

	private SparseZBuffer szb = new SparseZBuffer();
	private Rectangle selRect;
	
	private SNPValueProvider Y;
	private SNPValueProvider X;
	
	public ManhattanPlotComponent(ManhattanPlot thePlot) {
		super(thePlot);
		
		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				double[] clicked = getPoint(e.getX(), e.getY());
				switch(e.getButton()) {
				case MouseEvent.BUTTON1:
					SNP s = (SNP)szb.getObject(clicked[0], clicked[1]);
					if(s != null) {
						int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
						if((e.getModifiers()&CONTROLMASK) == CONTROLMASK) {
							plot.getViewModel().toggleSNPSelected(s);
						} else {
							plot.getViewModel().setSNPSelection(s);
						}
					}
					break;
				case MouseEvent.BUTTON3:
					break;
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (selRect!=null) {
//					System.out.println("Selecting SNPs intersecting: "+selRect);
					Graphics2D g = ((Graphics2D)farea.getGraphics());
					drawSelectionRectangle(g);
					int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
					boolean control = ((e.getModifiers()&CONTROLMASK) == CONTROLMASK);
					boolean alt = e.isAltDown();
					selectByRectangle(selRect, control, alt);
					selRect = null;
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
		});
		
		farea.addMouseMotionListener(new MouseMotionListener() {
			
			protected Point dragPoint;
			protected Point targPoint;
			
			@Override
			public void mouseDragged(MouseEvent e) {
				Graphics2D g = ((Graphics2D)farea.getGraphics());
				if (selRect==null) {
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
	public DataSeries doSelect(Collection<SNP> snps) {
		DataSeries ds = viewSNPs(snps, true);
		return ds;
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
		Set<SNP> newSelection = new HashSet<SNP>();
		double[] clicked1 = getPoint(r.x, r.y);
		double[] clicked2 = getPoint(r.x+r.width, r.y+r.height);
		
		for(SNP s : plot.snps) {
			double xval = X.getValue(s);
			double yval = Y.getValue(s);
			boolean inX = (xval >= clicked1[0] && xval < clicked2[0]);
			boolean inY = (yval < clicked1[1] && yval > clicked2[1]);
			if (inX && inY)
				newSelection.add(s);
		}
		
		Set<SNP> previousSelection = plot.getViewModel().getSelectedSNPs();
		if(control && alt) {
			previousSelection = new HashSet<SNP>(previousSelection);
			previousSelection.retainAll(newSelection);
			newSelection = previousSelection;
		} else if(control) {
			newSelection.addAll(previousSelection);
		} else if(alt) {
			newSelection.retainAll(previousSelection);
		} else {
			//nothing to do
		}
		
		plot.getViewModel().setSNPSelection(newSelection);
	}

	@Override
	public DataSeries viewSNPs(Collection<SNP> snps, final boolean isSelectionLayer) {
		DataSeries ds = new DataSeries();
		
		ds.setShape(new Shape() {
			public void paint(Graphics2D g) {
				Color c = g.getColor();
				
				if(!isSelectionLayer)
					g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
				
				g.fillRect(-2, -2, 5, 5);
				
				if(!isSelectionLayer)
					g.setColor(c);
			}
			public boolean wantDeviceCoordinates() {
				return true;
			}
		});
		
		if(X != null && Y != null) {
			for(SNP s: snps) {
				double xx = X.getValue(s);
				double yy = Y.getValue(s);
				ds.addPoint(xx, yy, s);
				szb.setObject(xx, yy, s);
			}
		}
		
		ds.setColor(Color.DARK_GRAY);
		
		return ds;
	}
	
	protected void initValueProviders(PlotContainer plotContainer) {
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
			Y.setProvider(Y.new StatisticalTestResultProvider());
		}
		
//		plotContainer.addViewSetting(X.getSetting(), plot);
		
		for(Setting s: Y.getSetting().getChildren()) {
			plotContainer.addViewSetting(s, plot);
		}
	}

	@Override
	public String getAutoTitleY(String ytitle) {
		if(Y != null)
			return Y.getSourceName();
		return ytitle;
	}

	@Override
	public String getAutoTitleX(String xtitle) {
		if(X != null)
			return X.getSourceName();
		return xtitle;
	}
}
