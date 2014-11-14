package mayday.transkriptorium.tracks.singlereads;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.structures.natives.LinkedIntArray;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.transkriptorium.data.MappedRead;
import mayday.transkriptorium.data.MappingStore;
import mayday.transkriptorium.data.MappingStore.AMappedRead;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;

public class ReadTrackRenderer extends AbstractTrackRenderer {

	protected MappingStore theData;
	protected final static Color uniqueColor = new Color(0,0,0,100);
	protected final static Color nonuniqueColor = new Color(255,0,0,100);
	
	protected int READHEIGHT = 4;
	
	protected LinkedIntArray rows = new LinkedIntArray(10000);
	protected ColorGradient gradient;
	protected String coloringMode;
	protected String showWhich;
	
	protected MouseListener mouseListener = new ReadMouseListener();
	
	public ReadTrackRenderer(GenomeOverviewModel Model, AbstractTrackPlugin track) {
		super(Model,track);
	}

	public void updateInternalVariables() {
		theData = null;
		ReadTrackSettings rts = ((ReadTrackSettings)tp.getTrackSettings());
		try {
			theData = rts.getMSM(rts.getExperiment());
			rows = new LinkedIntArray(10000);
			
			READHEIGHT = rts.getReadHeight();

			gradient = rts.getGradient();
			coloringMode = rts.getColoringMode();
			
			if (coloringMode==ReadTrackSettings.COL_POSITION) {
				String specID = chromeModel.getActualSpecies().getName();
				String chromeID = chromeModel.getActualChrome().getId();
				Chromosome c = theData.getCSC().getChromosome(SpeciesContainer.getSpecies(specID), chromeID);
				rts.setGradientRange(1, c.getLength());
			} else if (coloringMode==ReadTrackSettings.COL_QUALITY) {
				rts.setGradientRange(theData.getMinQuality(), theData.getMaxQuality());
			} else {
				gradient = null ;
			}
			showWhich = rts.getDisplayOption();
		} catch (Exception e) {}
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		if (theData==null)
			updateInternalVariables();

		if (theData==null)
			return;
		
		String specID = chromeModel.getActualSpecies().getName();
		String chromeID = chromeModel.getActualChrome().getId();
		Chromosome c = theData.getCSC().getChromosome(SpeciesContainer.getSpecies(specID), chromeID);
		if (c==null)
			return;
		
		int rh = READHEIGHT;
		
		DataMapper.getBpOfView(width, chromeModel, beg_x, ftp);
		long start = ftp.getFrom();
		DataMapper.getBpOfView(width, chromeModel, end_x, ftp);
		long end = ftp.getTo();
		
		int fontAscent = g.getFontMetrics().getAscent();

		if (start<0)
			start=0;
		if (end<0)
			end=0;
		
		List<MappedRead> items = theData.getOverlappingReads(c, start, end, Strand.UNSPECIFIED);

		Rectangle2D renderingWindowClip = new Rectangle2D.Double(beg_x, 0, end_x-beg_x, height);
		
		Rectangle2D innerClip = new Rectangle2D.Double();
		
		int nrow = (height-READHEIGHT)/(READHEIGHT+1);
		
//		int[] arrowX = new int[5];
//		int[] arrowY = new int[]{ 0, (rh/2), rh, rh, 0 };
				
		for (MappedRead mr: items) {
			
			
			if (showWhich!=ReadTrackSettings.SHOW_ALL) {
				if (showWhich == ReadTrackSettings.SHOW_MULTI) {
					if (mr.isUniqueMapping())
						continue; // don't show unique reads here
				} else {
					if (!mr.isUniqueMapping())
						continue; // don't show multi-reads here
				}
			}
			
			AbstractGeneticCoordinate agc = mr.getTargetCoordinate();
			int graphicsStart = DataMapper.getXPosition(					
					agc.getFrom(),
					width, chromeModel.getChromosomeStart(), 
					chromeModel.getChromosomeEnd());
			int graphicsEnd = DataMapper.getXPosition(					
					agc.getTo(),
					width, chromeModel.getChromosomeStart(), 
					chromeModel.getChromosomeEnd());
			int rw = graphicsEnd-graphicsStart;
			boolean fwd = agc.getStrand().similar(Strand.PLUS);
			boolean bwd = agc.getStrand().similar(Strand.MINUS);
			
			Color col = null;
			
			if (gradient!=null) {
				if (coloringMode==ReadTrackSettings.COL_POSITION) {
					long colorBase = agc.getFrom();
					if (!mr.isUniqueMapping()) {
						Iterator<MappedRead> imr = mr.getAllReadMappings();
						while (imr.hasNext()) 
							colorBase = Math.min(colorBase, imr.next().getTargetCoordinate().getFrom());
											
					}
					col = gradient.mapValueToColor(colorBase);
					col = new Color(col.getRed(), col.getGreen(), col.getBlue(), 100);
				} else if (coloringMode==ReadTrackSettings.COL_QUALITY){
					col = gradient.mapValueToColor(mr.quality());
					col = new Color(col.getRed(), col.getGreen(), col.getBlue(), 100);
				}
			} else {
				if (coloringMode == ReadTrackSettings.COL_BLACK) {
					if (mr.isUniqueMapping())
						col = uniqueColor;
					else
						col = nonuniqueColor;
				} else {
					// paired coloring
					if (mr.getRead().getPartner()!=null)
						col = Color.black;
					else
						col = Color.red;
				}
			}
			
			g.setColor(col);
			
			if (rw<1)
				rw=1;
			
			if (fwd&&bwd) {
				System.out.println("What? Reads must be on ONE strand!");
				System.out.println(agc);
			} else if (fwd) {
				int curRowF = getElementRow(mr,nrow/2);
				plotLocus(g, mr, innerClip, renderingWindowClip, ((READHEIGHT+1)*curRowF), 0, graphicsStart, rh, rw, fontAscent);
			} else {
				int curRowB = getElementRow(mr,nrow/2);
				plotLocus(g, mr, innerClip, renderingWindowClip, height-((READHEIGHT+1)*(curRowB+1)), 1, graphicsStart, rh, rw, fontAscent);
			}
		}		
	}
	
	protected int getElementRow(MappedRead mr, int max) {		
		long mr_id = ((AMappedRead)mr).getInternalID();
		int r = -1;
		if (mr_id < rows.size())
			r = rows.get(mr_id)-1;

		if (r==-1) {			
			AbstractGeneticCoordinate agc = mr.getTargetCoordinate();
			final Strand s = agc.getStrand();
			List<MappedRead> others = theData.getOverlappingReadsCluster(agc.getChromosome(), agc.getFrom(), agc.getTo(), s);
			
			Collections.sort(others, new Comparator<MappedRead>() {
				public int compare(MappedRead o1, MappedRead o2) {
					return s==Strand.PLUS?
							Long.valueOf(o2.getTargetCoordinate().getFrom()).compareTo(o1.getTargetCoordinate().getFrom()):
							Long.valueOf(o1.getTargetCoordinate().getTo()).compareTo(o2.getTargetCoordinate().getTo());										
				}
			});
			
			long[] rowStart = new long[max+1]; 
			if (s==Strand.PLUS) {
				Arrays.fill(rowStart, Long.MAX_VALUE);
			}			
			
			for (int i=0; i!=others.size(); ++i) {
				long ostart = others.get(i).getTargetCoordinate().getDownstreamCoordinate();
				int row;
				for (row=0; row<max; ++row) { 					
					if (s==Strand.PLUS && ostart<rowStart[row] || s==Strand.MINUS && ostart>rowStart[row])
						break;
				}
				rowStart[row] = others.get(i).getTargetCoordinate().getUpstreamCoordinate();
				long mrr_id = ((AMappedRead)others.get(i)).getInternalID();
				rows.ensureSize(mrr_id+1);
				rows.set(mrr_id, row+1); 
			}				
			r = rows.get(mr_id)-1;
		}
		return r;
	}
	
	protected void plotLocus(Graphics g, 
			MappedRead mr,
			Rectangle2D innerClip, Rectangle2D renderingWindowClip, 
			int ystart, int rectshft, int graphicsStart, 
			int rh, int rw, int fontAscent
	) {
		
		innerClip.setRect(graphicsStart, ystart, rw, rh);
		Rectangle.intersect(renderingWindowClip, innerClip, innerClip);
		g.setClip(innerClip);
		
		if (rw<1) {
			g.drawLine(graphicsStart, ystart, 1, rh);
		} else if (rw<10) {
			g.fillRect(graphicsStart, ystart, rw, rh);
		} else {		
			/* fillPolygon does unfortunately fill all kinds of things, but not the polygon that it is given.
			 * drawLine on the other hand draws nice lines all over the place, but their length is not correct.
			 * fillRect with a 1px high rectangle seems to do the trick.
			 */
			for (int i=0; i!=rh; ++i) {
				int delta = Math.min(rh-i-1, i);
				delta -= rh/2;
				if (rectshft==0)
					g.fillRect(graphicsStart, ystart+i, rw+delta, 1);
//					g.drawLine(graphicsStart, ystart+i, graphicsStart+rw+delta, ystart+i);
				else 
					g.fillRect(graphicsStart-delta, ystart+i, rw+delta, 1);
			}
		}		
				
		g.setClip(null);	
	}
	
	protected List<MappedRead> elementsAt(Point point) {
		if (theData==null)
			return Collections.emptyList();
		
		String specID = chromeModel.getActualSpecies().getName();
		String chromeID = chromeModel.getActualChrome().getId();
		Chromosome c = theData.getCSC().getChromosome(SpeciesContainer.getSpecies(specID), chromeID);
		if (c==null )
			return Collections.emptyList();
		
		DataMapper.getBpOfView(width, chromeModel, point.getX(),ftp);

		if (!ftp.isValid())
			return Collections.emptyList();
		
		int nrow = height/(READHEIGHT+1);
		
		Strand s = Strand.PLUS;
		point.y-=2;

		if (point.getY()>height/2)
			s = Strand.MINUS;		
		
		// get point row
		int row = 
			s==Strand.PLUS? 
				(int)(point.getY()/(READHEIGHT+1)) : 
				(int)((height-point.getY())/(READHEIGHT+1));
		
		List<MappedRead> items = theData.getOverlappingReads(c, ftp.getFrom(), ftp.getTo(), s);
		
		Iterator<MappedRead> ii = items.iterator();
		while (ii.hasNext()) {
			if (getElementRow(ii.next(),nrow/2)!=row)
					ii.remove();
		}
		
		return items;
	}
	
	public String getInformationAtMousePosition(Point point) {		
		
		List<MappedRead> items = elementsAt(point);		

		String t = "<html>"+items.size()+" elements, loc: ";
		
		Strand s = items.size()>0?items.get(0).getTargetCoordinate().getStrand():Strand.UNSPECIFIED;
		
		if (!ftp.isValid())
			return null;
		
		if (ftp.getFrom() == ftp.getTo()) {
			t+= ftp.getFrom()+ " (" + 1 + "bp "+s+") ";
		} else {
			t+= ftp.getFrom()+ "-" + ftp.getTo() + " ("+ ((ftp.getTo() - ftp.getFrom() + 1)) + "bp "+s+")";
		}
		
		int max=20;
		for (MappedRead mr : items) {
			AbstractGeneticCoordinate agc = mr.getTargetCoordinate();
			t+="<br>"+mr.getReadIdentifier()+": "+agc.getFrom()+"-"+agc.getTo()+": "+mr.quality();
			if (!mr.isUniqueMapping()) {
				t+="  -> ";
				Iterator<MappedRead> irr = mr.getAllReadMappings();
				while (irr.hasNext()) {
					MappedRead mrr = irr.next();
					AbstractGeneticCoordinate agcc = mrr.getTargetCoordinate();
					if (!agc.equals(agcc))
						t+="["+agcc.getFrom()+"-"+agcc.getTo()+": "+mrr.quality()+"]";
				}
			}
			max--;
			if (max==0) {
				t+="<br>(and "+(items.size()-20)+" more)";
				break;
			}
		}
			
			
		return t;
	}
	
	public MouseListener getMouseClickHandler() {
		return mouseListener;
	}
	
	
	protected class ReadMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==2) {
				List<MappedRead> items = elementsAt(e.getPoint());
				if (items.size()>0)
					PropertiesDialogFactory.createDialog(items.toArray()).setVisible(true);
			}
		}
	}
	
}
