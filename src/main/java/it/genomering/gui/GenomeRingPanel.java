package it.genomering.gui;

import it.genomering.render.DashMover;
import it.genomering.render.GenomeShape;
import it.genomering.render.RingDimensions;
import it.genomering.render.paths.GRPath;
import it.genomering.render.paths.LegendSegment;
import it.genomering.render.paths.SuperGenomePath;
import it.genomering.structure.Block;
import it.genomering.structure.Genome;
import it.genomering.structure.GenomeEvent;
import it.genomering.structure.GenomeListener;
import it.genomering.structure.GenomePosition;
import it.genomering.structure.SuperGenome;
import it.genomering.structure.SuperGenomeEvent;
import it.genomering.structure.SuperGenomeListener;
import it.genomering.structure.SuperGenomePosition;
import it.genomering.visconnect.ConnectionManager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.ToolTipManager;

import mayday.core.EventFirer;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewLayeredPane;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;

@SuppressWarnings("serial")
public class GenomeRingPanel extends JPanel implements SuperGenomeListener, GenomeListener {

	protected SuperGenome superGenome;
	protected RingDimensions ringdim;
	protected ConnectionManager cm;
	protected double theRotation=0;
	public VisualizationSetting setting;
	
	protected ArrayList<GRPath> viewElements = new ArrayList<GRPath>();
	
	public GenomeRingPanel(SuperGenome superGenome, RingDimensions ringdimen, ConnectionManager coma) {
		this.superGenome = superGenome;
		this.ringdim = ringdimen;
		this.cm=coma;
		this.setting = new VisualizationSetting();
		
		superGenome.addListener(this);

		for (Genome g : superGenome.getGenomes()) {
			g.addListener(this);
		}

		this.addMouseWheelListener(new MouseAdapter() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent evt) {
				if (evt.isShiftDown() && !evt.isAltDown()) {
					ringdim.changeGenomeWidth(ringdim.getGenomeWidth()+evt.getWheelRotation());
					refresh();
				} else	if (evt.isAltDown() && !evt.isShiftDown()) {
					ringdim.changeBlockGap(ringdim.getBlockGap()+evt.getWheelRotation());
					refresh();
				} else if(evt.isAltDown() && evt.isShiftDown()) {
//					ringdim.changeRadiusInner(evt.getWheelRotation() * 10);
//					refresh();
				} else if (evt.isAltGraphDown()) {
					ringdim.changeRingDistance(ringdim.getRingDistance()+10 * evt.getWheelRotation());
					refresh();
				} else if (!evt.isControlDown()){
					theRotation += evt.getWheelRotation();
					theRotation %= 360;
					repaint();
				}
			}
		});
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent evt) {
				
				GenomePosition gp = getClickedGenomePosition(evt.getPoint());
				final int SINGLE_CLICK = 1;
				final int DOUBLE_CLICK = 2;

				switch (evt.getButton()) {
					case MouseEvent.BUTTON1:
						switch (evt.getClickCount()) {
							case SINGLE_CLICK: 
								// if clicked on genome, get coordinate and do something with it
								if (gp!=null)  
									forceShowToolTip("<html>"+gp.toString().replace("\n", "<br>"), evt.getX(), evt.getY());
								else 
									forceShowToolTip("No genome at this position", evt.getX(), evt.getY());
								break;
							case DOUBLE_CLICK:
								if (gp!=null)
									scrollGenomeBrowsersTo(gp);
								else {
									setting.setShowBlockLabels(!setting.getShowBlockLabels());
									repaint();	
								}
								break;
						}
						break; // BUTTON 1
					case MouseEvent.BUTTON3:
//						final Timer t = new Timer("Waschmaschine");
//						TimerTask tt = new TimerTask() {
//							protected double speed=0.01; 
//							protected int waschgang=0;
//							protected int repeat=0;
//							public void run() {
//								theRotation+=speed;
//								theRotation%= 360;
//								repaint();
//													
//								switch(waschgang) {
//								case 0 : speed*=1.005;
//										 if (Math.abs(speed)>((repeat==0)?90:1)) ++waschgang;									 
//										 break;
//								case 1: speed*=0.995;
//								 	    if (Math.abs(speed)<0.02) ++waschgang;
//										 break;
//								case 2: speed *= -1; 
//										waschgang=0;
//										repeat++;
//										if (repeat==4) waschgang=3;
//										break;
//								case 3: t.cancel();
//								}							
//
//							}
//						};
//						t.schedule(tt, 10, 10);
						DashMover.grp=GenomeRingPanel.this;
						DashMover.start(50, 20000/50);
						break; // BUTTON 3						
				} 
			}
		});
	}

	protected void forceShowToolTip(String text, int x, int y) {
		setToolTipText(text);
		boolean canShowImmediately = false;
		try {
			Field f = ToolTipManager.class.getDeclaredField("showImmediately");
			f.setAccessible(true);
			f.set(ToolTipManager.sharedInstance(), Boolean.TRUE);
			canShowImmediately = true;
		} catch (Exception whatever) {}
		ToolTipManager.sharedInstance().mouseMoved(new MouseEvent(GenomeRingPanel.this, 0, 0, 0, x, y, 0, false));
		if (canShowImmediately) // remove text now
			setToolTipText(null);
	}

	@SuppressWarnings("rawtypes")
	protected void scrollGenomeBrowsersTo(GenomePosition gp) {
		// scroll associated genome browser(s)
		Genome g = gp.genome();
		ViewModel vm = cm.getViewModel(g);
		if (vm!=null) {
			try { // oh yes, DO try. be my guest
				Field f_ev = ViewModel.class.getDeclaredField("eventfirer");
				f_ev.setAccessible(true);
				EventFirer ev = (EventFirer)f_ev.get(vm);
				Field f_el = EventFirer.class.getDeclaredField("eventListeners");
				f_el.setAccessible(true);
				Set el = (Set)f_el.get(ev);
				// iterate over viewmodel listeners
				for (Object vmlo : el) {
					if (vmlo instanceof GenomeOverviewLayeredPane) {
						GenomeOverviewLayeredPane golp = (GenomeOverviewLayeredPane)vmlo;
						Field f_controller = GenomeOverviewLayeredPane.class.getDeclaredField("c");
						f_controller.setAccessible(true);
						Controller controller = (Controller)f_controller.get(golp);
						Field f_chromeModel = GenomeOverviewLayeredPane.class.getDeclaredField("chromeModel");
						f_chromeModel.setAccessible(true);
						GenomeOverviewModel gom = (GenomeOverviewModel)f_chromeModel.get(golp);
						gom.getFps().setPosition(gp.gPosition());
						controller.searchPositionInChromosome();
					}
				}
			} catch (Exception aWorldOfPain) {
				aWorldOfPain.printStackTrace();
			}
		}
	}
	
	public void refresh() {
		viewElements.clear();
		repaint();
	}
	
	@Override
	public void removeNotify() {
		cm.removeNotify(); // remove listeners
		super.removeNotify();
	}
	
	protected GenomePosition getClickedGenomePosition(Point2D pos) {
		AffineTransform at = getTransform();
		try {
			if(setting.getVerbose())
				System.out.println("Click position ("+pos.getX()+","+pos.getY()+")");

			Point2D.Double pos_orig = new Point2D.Double();
			at.invert();
			at.transform(pos, pos_orig);
			// find degree position
			double x = pos_orig.getX();
			double y = pos_orig.getY();
			double alpha = GRPath.XYtoPolarAlpha(x, y);
			double radius = GRPath.XYtoPolarRadius(x, y);
			
			int genome_in_outer = (int)((radius-ringdim.getOuterRingRadiusInner())/ringdim.getGenomeWidth());
			int genome_in_inner = (int)(superGenome.getNumberOfGenomes()-(radius-ringdim.getInnerRingRadiusInner())/ringdim.getGenomeWidth());
			
			Genome genome = null;
			Boolean forward = null;
			
			if (genome_in_outer>=0 && genome_in_outer<superGenome.getNumberOfGenomes()) {
				genome = superGenome.getGenomes().get(genome_in_outer);
				forward = true;
			}
			if (genome_in_inner>=0 && genome_in_inner<superGenome.getNumberOfGenomes()) {
				genome = superGenome.getGenomes().get(genome_in_inner);
				forward = false;
			}
			if(setting.getVerbose())
				System.out.println("Transformed position ("+x+","+y+") = ("+alpha+"°,"+radius+")");
			
			if (forward!=null && genome!=null) {			
				SuperGenomePosition sgPos = SuperGenomePosition.from_angle(superGenome, ringdim, alpha);
				if (sgPos!=null) {
					GenomePosition gp = sgPos.asGenomePosition(genome);
					gp.setForward(forward);
					if (gp.isValid())
						return gp;
				}					
			}
			
			
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}		
		return null;
	}
	
	protected Graphics2D transformGraphics(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, getWidth(), getHeight());
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2d.transform(getTransform());
		return g2d;
	}
	
	protected AffineTransform getTransform() {
		int xdim = getWidth();		
		int ydim = getHeight();
		AffineTransform at = new AffineTransform();
		// translate graphics to put center in center of window
		at.translate(xdim/2, ydim/2);
		
		// scale graphics such that a 1000x1000 grid fills the panel 
		int maxscale = Math.min(xdim,ydim);
		double factor = maxscale/(2.1*ringdim.getLegendRadius()+ringdim.getGenomeWidth());	
		at.scale(factor, factor);
		at.rotate(Math.toRadians(theRotation));
		return at;
	}
	
	protected void createViewElements() {
		
		List<Genome> genomes = superGenome.getGenomes();
		
		double[] costs = new double[3];
		
		for (int gidx = 0; gidx != genomes.size(); ++gidx) {
			GenomeShape gs = new GenomeShape(genomes.get(gidx), ringdim, cm);
			gs.setSetting(setting);
			viewElements.add(gs);
			double[] subcost = gs.getCosts();
			for (int j = 0; j != costs.length; ++j)
				costs[j] += subcost[j];
		}
		
		if(setting.getVerbose())
			System.out.println("GenomeRingPanel: Total cost\tJumps="+costs[0]+"\tBlocks="+costs[1]+"\tAngles="+costs[2]);
		
		// add example gene bases 
//		if (superGenome.getTotalLength()>900000) {
//			int gidx=0;
//			ElementsPath elements = new ElementsPath(superGenome.getGenomes().get(gidx), ringdim);
//			elements.addElement(1000, 2000, true);
//			elements.addElement(100000, 200000, true);
//			elements.addElement(100000, 150000, false);
//			elements.setStyle(Color.RED, new BasicStroke((float)ringdim.getGenomeWidth(),BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
//			viewElements.add(elements);
//
//			gidx=1;
//			elements = new ElementsPath(superGenome.getGenomes().get(gidx), ringdim);
//			elements.addElement(5, 6, true);
//			elements.addElement(300000, 900000, true);
//			elements.setStyle(Color.RED, new BasicStroke((float)ringdim.getGenomeWidth(),BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
//			viewElements.add(elements);
//		}
//		
		// block boundaries
		SuperGenomePath sgp = new SuperGenomePath(superGenome, ringdim);
		sgp.setStyle(new Color(0,0,0,255), new BasicStroke(1f,BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
		viewElements.add(sgp);
	}

	@Override
	public void paint(Graphics g) {
		
		// create the view elements first so that RingDimensions can calculate the 
		// maximal used lane for scaling the plot
		if (viewElements.size()==0)
			createViewElements();
		
		AffineTransform originalTransform = ((Graphics2D)g).getTransform();
		
		((Graphics2D)g).setBackground(Color.black);
		((Graphics2D)g).clearRect(0, 0, getWidth(), getHeight());

		Graphics2D g2d = transformGraphics(g);
		
		for (GRPath s : viewElements) {
			s.paint(g2d);
		}
		
		if(setting.getShowRingLegend()) {
			// paint measure
			LegendSegment ls = new LegendSegment(superGenome, ringdim, 45, 3);
			g2d.rotate(Math.toRadians(-theRotation));
			ls.paint(g2d);
			g2d.rotate(Math.toRadians(theRotation));
		}
		
		if (setting.getShowBlockLabels()) {
			// write block names
			Font f = g2d.getFont();
			g2d.setFont(f.deriveFont(50f));
			AffineTransform at = g2d.getTransform();
			for (Block b : superGenome.getBlocks()) {

				String info = b.getName(); 
				double width = g2d.getFontMetrics().getStringBounds(info, g2d).getWidth();
				double height= g2d.getFontMetrics().getStringBounds(info, g2d).getHeight();
				double textdist = Math.max(width, height)/2 + ringdim.getRingDistance()/2;

				double a1 = ringdim.getStartDegree(b);
				double a2 = ringdim.getEndDegree(b);
				double a = (a1+a2)/2;
				double r = ringdim.getOuterRingRadiusOuter()+textdist;

				double x = GRPath.polarToX(r,a);
				double y = GRPath.polarToY(r,a);
				g2d.translate(x, y);
				g2d.rotate(Math.toRadians(-theRotation));

				g2d.drawString(info, (int)-width/2,(int)height/2);

				g2d.setTransform(at);
			}
			g2d.setFont(f);
		}
		

		// restore original transformation
		g2d.setTransform(originalTransform);
		
		// paint block names and ringdim info 
		JViewport jsp = (JViewport)getParent();
		g2d.translate(jsp.getViewPosition().getX()+5, jsp.getViewPosition().getY());
		int h=0;
		
		if(setting.getShowRingDimInfo()) {
			g2d.drawString("Genome width: "+ringdim.getGenomeWidth(), 0,(++h)*15);
			g2d.drawString("Block gap degrees: "+ringdim.getBlockGap(), 0, (++h)*15);
			g2d.drawString("Circle spacing: "+ringdim.getRingDistance(), 0, (++h)*15);
			g2d.drawString("Rotation: "+theRotation, 0, (++h)*15);
		}
		
		if(setting.getShowUsageInfo()) {
			if(h > 0)
				++h;
			g2d.drawString("--Genome Ring Usage Information--", 0, (++h)*15);
			g2d.drawString("Mouse Wheel:", 0, (++h)*15);
			g2d.drawString("     Rotate genome ring", 0, (++h)*15);
			g2d.drawString("Shift + MouseWheel:", 0, (++h)*15);
			g2d.drawString("     Change the genome width", 0, (++h)*15);
			g2d.drawString("Alt + MouseWheel:", 0, (++h)*15);
			g2d.drawString("     Change the block gap degree", 0, (++h)*15);
			g2d.drawString("AltGr + MouseWheel:", 0, (++h)*15);
			g2d.drawString("     Change the ring distance", 0, (++h)*15);
			g2d.drawString("Left Mouse Click:", 0, (++h)*15);
			g2d.drawString("     Show tooltip information for click position", 0, (++h)*15);
			g2d.drawString("Double Click:", 0, (++h)*15);
			g2d.drawString("     Toggle display of block labels", 0, (++h)*15);
			++h;
			g2d.drawString("-- Genome Ring Legend Usage Information --", 0, (++h)*15);
			g2d.drawString("Double click on genome name", 0, (++h)*15);
			g2d.drawString("     Change block order according to the clicked genome", 0, (++h)*15);
			g2d.drawString("Single click on genome box", 0, (++h)*15);
			g2d.drawString("     Toggle display of the clicked genome", 0, (++h)*15);
			g2d.drawString("Double click on genome box", 0, (++h)*15);
			g2d.drawString("     Change the color of the clicked genome", 0, (++h)*15);
		}
		
		// restore original transformation
		g2d.setTransform(originalTransform);
		
		// update size for export
		if (!this.isPreferredSizeSet()) {
			this.setPreferredSize(jsp.getSize());
		}
	}

	public void superGenomeChanged(SuperGenomeEvent evt) {
		switch(evt.getChange()) {
		case SuperGenomeEvent.SILENT_MODE:
			this.setVisible(false);
			break;
		case SuperGenomeEvent.NO_SILENT_MODE:
			this.setVisible(true);
			break;
		default:
			refresh();		
			for (Genome g : superGenome.getGenomes()) {
				g.addListener(this);
			}
		}
	}
	
	public void genomeChanged(GenomeEvent evt) {
		switch(evt.getType()) {
		case GenomeEvent.COLOR_CHANGED: // fall
		case GenomeEvent.VISIBILITY_CHANGED:
			repaint();
			break;
		case GenomeEvent.NAME_CHANGED: // ignore
			break;
		case GenomeEvent.CONNECTED_VIEWMODEL_CHANGED:			
			repaint(); // should anything else be done? better: only repaint the respective genome
			break;
		}
	}
	
	public class VisualizationSetting extends HierarchicalSetting {
		
		private BooleanSetting enablePaths;
		private BooleanSetting showRingDimInfo;
		private BooleanSetting showUsageInfo;
		private BooleanSetting showRingLegend;
		private BooleanSetting showBlockLabels;
		private BooleanSetting verbose;
		
		private BooleanSetting showGenes;
		private BooleanSetting showSNPs;
		
		private ColorSetting snvColor;
		private ColorSetting snvSelectionColor;
		
		private IntSetting snvSizeFactor;
		
		private boolean oldUsePaths = true;
		
		public VisualizationSetting() {
			super("Visualization");
			
			addSetting(enablePaths = new BooleanSetting("Show Paths", null, true));
			addSetting(showRingDimInfo = new BooleanSetting("Show Ring Dimension Information", null, true));
			addSetting(showRingLegend = new BooleanSetting("Show Ring Legend", null, true));
			addSetting(showBlockLabels = new BooleanSetting("Show block labels", null, true));
			
			HierarchicalSetting additionalViewElements = new HierarchicalSetting("Additional View Elements");
			
			additionalViewElements.addSetting(showGenes = new BooleanSetting("Show Genes", "Enable Gene Visualization for connected Mayday Visualizers", true));
			additionalViewElements.addSetting(showSNPs = new BooleanSetting("Show SNVs", "Enable SNV Visualization for connected Reveal Visualizers", true));
			
			additionalViewElements.addSetting(snvColor = new ColorSetting("SNV Color", null, Color.DARK_GRAY));
			additionalViewElements.addSetting(snvSelectionColor = new ColorSetting("SNV Selection Color", null, Color.RED));
			additionalViewElements.addSetting(snvSizeFactor = new IntSetting("SNV Size Factor", "Allows to increase the size of an SNV in the genome ring visualization", 20));
			
			addSetting(additionalViewElements);
			
			addSetting(verbose = new BooleanSetting("Enable Verbose Mode", "Prints status messages to Maydays Log Window", false));
			addSetting(showUsageInfo = new BooleanSetting("Show Usage Information", null, false));
			
			addChangeListener(new SettingChangeListener() {
				@Override
				public void stateChanged(SettingChangeEvent e) {
					if(getEnablePaths() != oldUsePaths) {
						ringdim.toggleUseJumpLanes();
						oldUsePaths = getEnablePaths();
						refresh();
					}
					repaint();
				}
			});
		}
		
		public Color getSNVSelectionColor() {
			return this.snvSelectionColor.getColorValue();
		}
		
		public Color getSNVColor() {
			return this.snvColor.getColorValue();
		}
		
		public boolean showAdditionalElements() {
			return showGenes() || showSNVs();
		}
		
		public boolean showSNVs() {
			return this.showSNPs.getBooleanValue();
		}
		
		public boolean showGenes() {
			return this.showGenes.getBooleanValue();
		}
		
		public boolean getEnablePaths() {
			return this.enablePaths.getBooleanValue();
		}
		
		public boolean getShowRingDimInfo() {
			return this.showRingDimInfo.getBooleanValue();
		}
		
		public boolean getShowRingLegend() {
			return this.showRingLegend.getBooleanValue();
		}
		
		public boolean getShowBlockLabels() {
			return this.showBlockLabels.getBooleanValue();
		}
		
		public void setShowBlockLabels(boolean show) {
			this.showBlockLabels.setBooleanValue(show);
		}
		
		public boolean getVerbose() {
			return this.verbose.getBooleanValue();
		}
		
		public boolean getShowUsageInfo() {
			return this.showUsageInfo.getBooleanValue();
		}
		
		public VisualizationSetting clone() {
			VisualizationSetting s = new VisualizationSetting();
			s.fromPrefNode(this.toPrefNode());
			return s;
		}

		public int getSNPSizeFactor() {
			return this.snvSizeFactor.getIntValue();
		}
	}
}
