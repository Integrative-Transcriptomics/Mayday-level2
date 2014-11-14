package mayday.tiala.pairwise.gui.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;

import mayday.core.gui.components.VerticalLabel;
import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.data.AlignmentStoreEvent;
import mayday.tiala.pairwise.data.AlignmentStoreListener;
import mayday.tiala.pairwise.data.AlignedDataSets.DII;

@SuppressWarnings("serial")
public class Zahlenstrahl extends JComponent implements AlignmentStoreListener {
	
	AlignmentStore store;
	JLabel timeLabel = new VerticalLabel(false);
	Stroke s1 = new BasicStroke(1);
	Stroke s2 = new BasicStroke(2);
	Stroke s3 = new BasicStroke(5);
	JLabel d1label=new JLabel();
	JLabel d2label=new JLabel();
	int maxDLabelWidth;
	
	int topHeight, botHeight;
	
	public Zahlenstrahl(AlignmentStore store) {
		this.store=store;
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		getInsets().set(10,10,10,10);
		timeLabel.setFont(new Font(timeLabel.getFont().getName(),Font.PLAIN, 9));
		d1label.setFont(timeLabel.getFont());
		d2label.setFont(timeLabel.getFont());
		init();
		store.addListener(this);
	}
	
	protected void init() {
		// compute the width of dataset names
		d1label.setText(store.getOne().getDataSet().getName());
		d2label.setText(store.getTwo().getDataSet().getName());
		maxDLabelWidth = Math.max(d1label.getPreferredSize().width, d2label.getPreferredSize().width)+10;
		
		topHeight = 0;
		botHeight = 0;
		// compute the height of experiment labels for both datasets
		for (String label : store.getAlignedDataSets().getFirstDataSet().getMasterTable().getExperimentDisplayNames()) {
			timeLabel.setText(label);
			topHeight = Math.max(timeLabel.getPreferredSize().height, topHeight);
		}
		for (String label : store.getAlignedDataSets().getSecondDataSet().getMasterTable().getExperimentDisplayNames()) {
			timeLabel.setText(label);
			botHeight = Math.max(timeLabel.getPreferredSize().height, botHeight);
		}
		
		List<DII> all = store.getAlignedDataSets().getAll();
		DII last = null;
		double mintimedist = 100; 
		for (DII dii : all) {
			if (last!=null)
				mintimedist = Math.min(mintimedist, dii.getTime()-last.getTime());
			last = dii;
		}
		double mintime = all.get(0).getTime();
		double maxtime = all.get(all.size()-1).getTime();
		double deltatime = maxtime-mintime;
		
		int labelWidth = timeLabel.getPreferredSize().width;
		
		Dimension mySize = new Dimension(
				(int)((deltatime/mintimedist)*labelWidth)+maxDLabelWidth,
				topHeight+botHeight+50
				);
		setPreferredSize(mySize);
		setMinimumSize(mySize);
	}
	
	public Dimension getPreferredSize() {
		return super.getPreferredSize();
	}

	public Dimension getMinimumSize() {
		return super.getMinimumSize();
	}

	
	public void paint(Graphics g) {
		List<DII> all = store.getAlignedDataSets().getAll();
		double mintime = all.get(0).getTime();
		double maxtime = all.get(all.size()-1).getTime();
		double deltatime = maxtime-mintime;
		
		double componentWidth = getWidth()-getInsets().left-getInsets().right-maxDLabelWidth;
		int xshift = getInsets().left + maxDLabelWidth;
		int yshift = getInsets().top;
		double unitWidth = componentWidth / deltatime; // pixels per time unit
		
		double componentHeight = getHeight()-getInsets().top-getInsets().bottom;
		double startTopTick = topHeight+5;		
		double stopBotTick = componentHeight - botHeight -5;
		double tickrange = stopBotTick-startTopTick;
		double topLineY = startTopTick+.33*tickrange;
		double botLineY = startTopTick+.66*tickrange;
		
		Double topLineFirstTime=null;
		Double topLineLastTime=null;
		Double botLineFirstTime=null;
		Double botLineLastTime=null;
		for (DII dii : all) {
			Integer i1 = dii.getIdx1();
			Integer i2 = dii.getIdx2();
			if (i1!=null) {
				topLineLastTime = dii.getTime();
				if (topLineFirstTime==null)
					topLineFirstTime = dii.getTime();
			}
			if (i2!=null) {
				botLineLastTime = dii.getTime();
				if (botLineFirstTime==null)
					botLineFirstTime = dii.getTime();
			}
		}
		
		if (topLineFirstTime==null || topLineLastTime==null || botLineFirstTime==null || botLineLastTime==null)
			throw new RuntimeException("Strange, some times are null?");
		
		Graphics2D g2 = (Graphics2D)g;
		
		// start with dataset names
		AffineTransform tr = g2.getTransform();
		g2.translate(getInsets().left, topLineY);
		d1label.setSize(d1label.getPreferredSize());
		d1label.paint(g2);
		g2.setTransform(tr);
		g2.translate(getInsets().left, botLineY+d2label.getPreferredSize().getHeight());
		d2label.setSize(d2label.getPreferredSize());
		d2label.paint(g2);
		g2.setTransform(tr);				
		
		g2.setStroke(s3);
		// draw the top line
		g2.drawLine((int)(xshift+(topLineFirstTime-mintime)*unitWidth), yshift+(int)topLineY, 
				xshift+(int)((topLineLastTime-mintime)*unitWidth), yshift+(int)topLineY);
		
		// draw the bottom line
		g2.drawLine((int)(xshift+(botLineFirstTime-mintime)*unitWidth), yshift+(int)botLineY, 
				xshift+(int)((botLineLastTime-mintime)*unitWidth), yshift+(int)botLineY);
		
		// draw all the ticks
		for (DII dii : all) {
			Integer i1 = dii.getIdx1();
			Integer i2 = dii.getIdx2();
			
			double start = yshift+ (i1!=null? startTopTick : botLineY);
			double stop = yshift+ (i2!=null? stopBotTick : topLineY);
			int xpos = xshift+(int)((dii.getTime()-mintime)*unitWidth);
			
			boolean both = i2!=null && i1!=null; 

			g2.setStroke(both ? s2 : s1);
			g2.setColor(Color.black);

			g2.drawLine(xpos, (int)start, xpos,(int)stop);
			
			timeLabel.setForeground(both ? Color.black : Color.red);
			
			if (i1!=null) {
				timeLabel.setText(store.getAlignedDataSets().getFirstDataSet().getMasterTable().getExperimentDisplayName(i1));
				timeLabel.setSize(timeLabel.getPreferredSize());
				tr = g2.getTransform();
				g2.translate(xpos-(timeLabel.getWidth()/2), yshift+(int)startTopTick-5-timeLabel.getHeight());
				timeLabel.paint(g2);
				g2.setTransform(tr);
			}
			if (i2!=null) {
				timeLabel.setText(store.getAlignedDataSets().getSecondDataSet().getMasterTable().getExperimentDisplayName(i2));
				timeLabel.setSize(timeLabel.getPreferredSize());
				tr = g2.getTransform();
				g2.translate(xpos-(timeLabel.getWidth()/2), yshift+5+(int)stopBotTick);
				timeLabel.paint(g2);
				g2.setTransform(tr);
			}
		}
	}

	public void alignmentChanged(AlignmentStoreEvent event) {
		if (event.getChange()==AlignmentStoreEvent.SHIFT_CHANGED) {
			repaint();
		}
	}

}
