package mayday.tiala.multi.gui.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;

import mayday.core.gui.components.VerticalLabel;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.AlignmentStoreEvent;
import mayday.tiala.multi.data.AlignmentStoreListener;
import mayday.tiala.multi.data.AlignedDataSets.DII;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class Zahlenstrahl extends JComponent implements AlignmentStoreListener {
	
	AlignmentStore store;
	JLabel timeLabel = new VerticalLabel(false);
	
	int stroke1 = 1;
	int stroke2 = 3;
	int stroke3 = 5;
	
	Stroke s1 = new BasicStroke(stroke1);
	Stroke s2 = new BasicStroke(stroke2);
	Stroke s3 = new BasicStroke(stroke3);
	
	//data set labels on the left
	JLabel[] dsLabels;
	//maximum width of all data set labels
	int maxDLabelWidth;
	int maxLabelHeight;
	//maximum height for the experiment labels for each data set
	int[] heights;
	
	private double[] linesY;
	
	/**
	 * @param store
	 */
	public Zahlenstrahl(AlignmentStore store) {
		this.store = store;
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		getInsets().set(10,10,10,10);
		timeLabel.setFont(new Font(timeLabel.getFont().getName(),Font.PLAIN, 8));
		
		init();
		store.addListener(this);
	}
	
	protected void init() {
		int numDataSets = store.getAlignedDataSets().getNumberOfDataSets();
		Font f = new Font(timeLabel.getFont().getName(),Font.PLAIN, 9);
		//initialize the data set labels
		dsLabels = new JLabel[numDataSets];
		for(int i = 0; i < dsLabels.length; i++) {
			dsLabels[i] = new JLabel();
			if(i == 0) {
				dsLabels[i].setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
			} else {
				dsLabels[i].setFont(f);
			}
			dsLabels[i].setText(store.get(i).getDataSet().getName());
		}
		
		//calculate the maximum label width
		maxDLabelWidth = this.getMaxLabelWidth(dsLabels);
		
		//calculate the heights of the experiment labels for each dataset
		heights = new int[numDataSets + 1];
		Arrays.fill(heights, 0);
		
		maxLabelHeight = 0;
		
		// compute the height of experiment labels for all datasets
		for(int i = 1; i < heights.length; i++) {
			for (String label : store.getAlignedDataSets().getDataSet(i-1).getMasterTable().getExperimentDisplayNames()) {
				int start = label.lastIndexOf("_");
				if(start != -1) {
					label = label.substring(start+1, label.length());
				}
				timeLabel.setText(label);
				heights[i] = Math.max(timeLabel.getPreferredSize().height+5, heights[i]);
				if(heights[i] > maxLabelHeight) {
					maxLabelHeight = heights[i];
				}
			}
		}
		
		heights[0] = maxLabelHeight;
		
		//determine the width of the time line widget
		List<List<DII>> all = store.getAlignedDataSets().getMappingAll();
		
		int myWidth = 0;
		for(int i = 0; i < all.size(); i++) {
			DII last = null;
			double mintimedist = 100;
			for(DII dii : all.get(i)) {
				if(last != null) {
					mintimedist = Math.min(mintimedist, dii.getTime()-last.getTime());
				}
				last = dii;
			}
			double mintime = all.get(i).get(0).getTime();
			double maxtime = all.get(i).get(all.get(i).size()-1).getTime();
			double deltatime = maxtime - mintime;
			
			int labelWidth = timeLabel.getPreferredSize().width;
			
			myWidth = Math.max((int)((deltatime/mintimedist)*labelWidth)+maxDLabelWidth, myWidth);
		}
		
		int maxTimepointLabelHeight = maxLabelHeight;
		
		//set preferred and minimum size
		Dimension mySize = new Dimension(myWidth, sum(heights) + maxTimepointLabelHeight * (numDataSets+1));
		
		setPreferredSize(mySize);
		setMinimumSize(mySize);
		
		//calculate y position of time lines
		linesY = new double[numDataSets + 1];
		double sumHeight = heights[0];
		
		for(int i = 1; i < linesY.length; i++) {
			sumHeight += heights[i];
			linesY[i] = sumHeight;
			sumHeight += maxTimepointLabelHeight;
		}
	}
	
	public Dimension getPreferredSize() {
		return super.getPreferredSize();
	}

	public Dimension getMinimumSize() {
		return super.getMinimumSize();
	}

	public void paint(Graphics g) {
		//get the number of data sets
		int numDataSets = store.getAlignedDataSets().getNumberOfDataSets();
//		List<Double> alignment = store.getAlignment();
		
		//determine the width of the time line without borders and labels
		double componentWidth = getWidth() - getInsets().left - getInsets().right - maxDLabelWidth;
		
		double maximumTime = Integer.MIN_VALUE;
		double minimumTime = Integer.MAX_VALUE;
		
		for(int i = 0; i < numDataSets; i++) {
			List<DII> all = store.getAlignedDataSets().getMappingAll().get(i);
			double mintime = all.get(0).getTime();
			double maxtime = all.get(all.size()-1).getTime();
			
			//update minimumTime
			if(minimumTime > mintime) {
				minimumTime = mintime;
			}
			//update maximumTime
			if(maximumTime < maxtime) {
				maximumTime = maxtime;
			}
		}
		
		// pixels per time unit
		double unitWidth = componentWidth / (maximumTime - minimumTime);
		
		//distance to start from the left
		int xshift = getInsets().left + maxDLabelWidth;
		//distance to start from the top
		int yshift = getInsets().top;
		
		Double[] linesFirstTime = new Double[numDataSets + 1];
		Double[] linesLastTime = new Double[numDataSets + 1];
		Arrays.fill(linesFirstTime, null);
		Arrays.fill(linesLastTime, null);
		
		for (DII dii : store.getAlignedDataSets().getMappingAll().get(0)) {
			Integer i1 = dii.getIdx1();
			if(i1 != null) {
				linesLastTime[0] = dii.getTime();
				if (linesFirstTime[0]==null)
					linesFirstTime[0] = dii.getTime();
			}
		}
		
		for(int i = 0; i < numDataSets; i++) {
			for (DII dii : store.getAlignedDataSets().getMappingAll().get(i)) {
				Integer i2 = dii.getIdx2();
				if (i2!=null) {
					linesLastTime[i+1] = dii.getTime();
					if (linesFirstTime[i+1]==null)
						linesFirstTime[i+1] = dii.getTime();
				}
			}
		}
		
		if(anyNull(linesFirstTime) || anyNull(linesLastTime)) {
			throw new RuntimeException("Strange, some times are null?");
		}
		
		Graphics2D g2 = (Graphics2D)g;
		
		AffineTransform tr = g2.getTransform();
		double s3_2 = stroke3 / 2.0;
		
		//draw data set names
		for(int i = 1; i < numDataSets + 1; i++) {
			double tmp = dsLabels[i-1].getPreferredSize().getHeight() / 2.0 - s3_2;
			//position the label in the middle of the time line it represents
			g2.translate(getInsets().left, linesY[i] + tmp);
			dsLabels[i-1].setSize(dsLabels[i-1].getPreferredSize());
			dsLabels[i-1].paint(g2);
			g2.setTransform(tr);
		}
		
		g2.setStroke(s3);
		
		//draw time lines
		for(int i = 1; i < numDataSets + 1; i++) {
			int xposStart = xshift + (int)((linesFirstTime[i] - minimumTime) * unitWidth);
			int xposStop = xshift + (int)((linesLastTime[i] - minimumTime) * unitWidth);
			int ypos = yshift + (int)linesY[i];
			g2.drawLine(xposStart, ypos, xposStop, ypos);
		}

		for(int i = 0; i < numDataSets; i++) {
			for (DII dii : store.getAlignedDataSets().getMappingAll().get(i)) {
				Integer i1 = dii.getIdx1();
				Integer i2 = dii.getIdx2();
				
				boolean both = i2 != null && i1 != null;
				
				int shiftHeightStart = maxLabelHeight;
				int shiftHeightStop = maxLabelHeight;
				
				if(!both) {
					shiftHeightStart = 8;
					shiftHeightStop = 8;
				}
				
				if(i == 0) {
					shiftHeightStart = 8;
				}
				
				if(i == numDataSets - 1) {
					shiftHeightStop = 8;
				}
				
				double start = yshift + (linesY[i+1] - shiftHeightStart);
				double stop = yshift + (linesY[i+1] + shiftHeightStop);
				int xpos = xshift + (int)((dii.getTime() - minimumTime) * unitWidth);
								
				g2.setStroke(both ? s2 : s1);
				g2.setColor(Color.black);
				
				if(i2 != null) {
					g2.drawLine(xpos, (int)start, xpos,(int)stop);
				}
								
				timeLabel.setForeground(both ? Color.black : Color.red);

				if (i2 != null) {
					String tpLabel = store.getAlignedDataSets().getDataSet(i).getMasterTable().getExperimentDisplayName(i2);
					int sIx = tpLabel.lastIndexOf("_");
					
					if(sIx != -1) {
						tpLabel = tpLabel.substring(sIx+1, tpLabel.length());
					}
					
					timeLabel.setText(tpLabel);
					timeLabel.setSize(timeLabel.getPreferredSize());
					tr = g2.getTransform();
					g2.translate(xpos - (timeLabel.getWidth()/2), yshift + (int)linesY[i+1] - 10 - timeLabel.getHeight());
					
					//draw a little box around time-points that are shared between all data sets
					if(both) {
						g2.setColor(Color.WHITE);
						g2.fillRect(-1, -2, timeLabel.getWidth()+1, timeLabel.getHeight()+4);
						g2.setColor(Color.LIGHT_GRAY.darker());
						Stroke tmp = g2.getStroke();
						g2.setStroke(s1);
						g2.drawRect(-1, -2, timeLabel.getWidth()+1, timeLabel.getHeight()+4);
						g2.setStroke(tmp);
						g2.setColor(Color.BLACK);
					}
					
					timeLabel.paint(g2);
					g2.setTransform(tr);
				}
			}
		}
	}

	public void alignmentChanged(AlignmentStoreEvent evt) {
		switch(evt.getChange()) {
		case AlignmentStoreEvent.SHIFT_CHANGED:
			repaint();
			break;
		case AlignmentStoreEvent.CENTER_CHANGED:
			init();
			repaint();
			break;
		}
	}

	/**
	 * @param input
	 * @return sum over the elements in the array
	 */
	public static int sum(int[] input) {
		int sum = 0;
		for(int i : input) {
			sum += i;
		}
		return sum;
	}
	
	/**
	 * @param input
	 * @return true, if one of the values in the array is null, else false
	 */
	public boolean anyNull(Double[] input) {
		if(input[0] == null){
			input[0]= 0.0;
		}
		boolean anyNull = false;
		for(Double d : input) {
			if(d == null) {
				anyNull = true;
			}
		}
		return anyNull;
	}
	
	/**
	 * @param labels
	 * @return maximum label width
	 */
	public int getMaxLabelWidth(JLabel[] labels) {
		int max = Integer.MIN_VALUE;
		for(int i = 0; i < labels.length; i++) {
			if(max < labels[i].getPreferredSize().width) {
				max = labels[i].getPreferredSize().width;
			}
		}
		return max + 10;
	}

	public void dispose() {
		store.removeListener(this);
	}
}
