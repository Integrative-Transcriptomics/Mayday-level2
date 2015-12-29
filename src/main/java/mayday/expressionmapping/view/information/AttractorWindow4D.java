package mayday.expressionmapping.view.information;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import mayday.expressionmapping.controller.MainFrame;
import mayday.expressionmapping.model.geometry.Point;
import mayday.expressionmapping.model.geometry.container.PointList;
import mayday.vis3.gui.PlotContainer;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class AttractorWindow4D extends AttractorWindow {
	private int dim = 4;
	private int windowSize = 600;
	private int mainLength;
	private int subLength;
	private int x_0;
	private int y_0;
	private int[][] xCoordsMain;
	private int[][] yCoordsMain;
	private int[] xCoordsSub;
	private int[] yCoordsSub;
	private int[] xCoordsFreq;
	private int[] yCoordsFreq;
	private int[] xCoordsMainFreq;
	private int[] yCoordsMainFreq;
	private int[] xCoordsGroupLabels;
	private int[] yCoordsGroupLabels;
	private final static Color drawColor = Color.black;
	private final static Color fillMain = Color.lightGray;
	private final static Color fillAtt = Color.gray;
	private final static Color writeColor = Color.black;
	private final static Color backgroundColor = Color.white;
	
	private PointList<? extends Point> points;
	private String[] freqs;
	private String[] freqsAdd;
	private String[] mainFreqs;
	private String gravyCenter;

	private Graphics2D g2d;

	/**
	 * @param master
	 */
	public AttractorWindow4D(MainFrame master) {
		this.master = master;
		this.points = master.getPoints();

		if (points.getDimension() != this.dim) {
			throw new IllegalArgumentException("The PointList has the wrong dimension: " + points.getDimension());
		}
		
		initialize();
		createLabels();
	}

	private void initialize() {
		this.setLayout(new java.awt.BorderLayout());
		this.setPreferredSize(new Dimension(this.windowSize, this.windowSize));
		this.setSize(600, 600);
		this.addMouseListener(this);
		this.setBackground(backgroundColor);

		this.xCoordsMain = new int[4][6];
		this.yCoordsMain = new int[4][6];
		this.xCoordsSub = new int[4];
		this.yCoordsSub = new int[4];
		this.mainAttractors = new Polygon[this.dim];
		this.attractors = new Polygon[this.dim];
		this.mainFreqs = new String[4];
		this.freqs = new String[4];
		this.freqsAdd = new String[4];
		this.xCoordsFreq = new int[4];
		this.yCoordsFreq = new int[4];
		this.xCoordsMainFreq = new int[4];
		this.yCoordsMainFreq = new int[4];
		this.xCoordsGroupLabels = new int[4];
		this.yCoordsGroupLabels = new int[4];
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.g2d = (Graphics2D) g;
		computeCoordinates();
		paintAttractors();
		paintLabels(g2d);
	}

	private void createLabels() {
		double[] tmpFreqs = this.points.getAttractorFreqs();
		double[] tmpMainFreqs = this.points.getMainAttractorFreqs();
		Point gravy = this.points.getCenterofMass();
		double[] tmpCoG = gravy.getCoordinates();
		double attracFreq;
		double counterFreq;

		/*round freqeuncies and transform them to percentage
		 */
		for (int i = 0; i < this.freqs.length; ++i) {
			attracFreq = tmpFreqs[i];
			/* counterpart of the attractor
			 * 0<->13
			 * 1<->12
			 * 2<->11
			 * 3<->10
			 */
			counterFreq = tmpFreqs[13 - i];

			//process attracFreq
			if (attracFreq < 0.001 && attracFreq > 0) {
				freqs[i] = "<0.1%";
			} else {
				float tmp = ((float) (int) (attracFreq * 10000)) / 100;
				freqs[i] = tmp + "%";
			}
			
			freqsAdd[i] = null;
			
			//process counterFreq
			if (counterFreq < 0.001 && counterFreq > 0) {
				freqsAdd[i] = "(<0.1%)";
			} else {
				float tmp = ((float) (int) (counterFreq * 10000)) / 100;
				freqsAdd[i] = "(" + tmp + "%)";
			}
		}

		for (int i = 0; i < this.mainFreqs.length; ++i) {
			float tmp = ((float) (int) (tmpMainFreqs[i] * 10000)) / 100;
			this.mainFreqs[i] = new String(tmp + "%");
		}

		StringBuffer tmp = new StringBuffer();
		tmp.append("Center of gravity: (");

		for (int i = 0; i < tmpCoG.length - 1; ++i) {
			/*round to two decimal places
			 */
			tmpCoG[i] = (double) ((int) (tmpCoG[i] * 1000)) / 1000;
			tmp.append(tmpCoG[i]);
			tmp.append(", ");
		}

		/* last entry
		 */
		tmpCoG[tmpCoG.length - 1] = (double) ((int) (tmpCoG[tmpCoG.length - 1] * 1000)) / 1000;
		tmp.append(tmpCoG[tmpCoG.length - 1]);
		tmp.append(")");

		this.gravyCenter = tmp.toString();
	}

	private void computeCoordinates() {
		this.windowSize = Math.min(this.getWidth(), this.getHeight());
		this.mainLength = (int) ((double) this.windowSize * 0.6);
		this.subLength = (int) ((double) this.mainLength * 0.25);
		this.x_0 = (this.getWidth() - this.mainLength) / 2;
		this.y_0 = (this.getHeight() - this.mainLength) / 2;

		int half_main = this.mainLength / 2;

		this.xCoordsMain[0][0] = x_0;
		this.xCoordsMain[0][1] = x_0;
		this.xCoordsMain[0][2] = x_0 + subLength;
		this.xCoordsMain[0][3] = x_0 + subLength;
		this.xCoordsMain[0][4] = x_0 + half_main + 1;
		this.xCoordsMain[0][5] = x_0 + half_main + 1;

		this.xCoordsMain[1][0] = x_0 + half_main + 1;
		this.xCoordsMain[1][1] = x_0 + half_main + 1;
		this.xCoordsMain[1][2] = x_0 + mainLength - subLength;
		this.xCoordsMain[1][3] = x_0 + mainLength - subLength;
		this.xCoordsMain[1][4] = x_0 + mainLength;
		this.xCoordsMain[1][5] = x_0 + mainLength;

		this.xCoordsMain[2][0] = x_0 + half_main;
		this.xCoordsMain[2][1] = x_0 + mainLength;
		this.xCoordsMain[2][2] = x_0 + mainLength;
		this.xCoordsMain[2][3] = x_0 + mainLength - subLength;
		this.xCoordsMain[2][4] = x_0 + mainLength - subLength;
		this.xCoordsMain[2][5] = x_0 + half_main;

		this.xCoordsMain[3][0] = x_0;
		this.xCoordsMain[3][1] = x_0 + half_main;
		this.xCoordsMain[3][2] = x_0 + half_main;
		this.xCoordsMain[3][3] = x_0 + subLength;
		this.xCoordsMain[3][4] = x_0 + subLength;
		this.xCoordsMain[3][5] = x_0;

		this.yCoordsMain[0][0] = y_0 + half_main;
		this.yCoordsMain[0][1] = y_0 + mainLength - subLength;
		this.yCoordsMain[0][2] = y_0 + mainLength - subLength;
		this.yCoordsMain[0][3] = y_0 + mainLength;
		this.yCoordsMain[0][4] = y_0 + mainLength;
		this.yCoordsMain[0][5] = y_0 + half_main;

		this.yCoordsMain[1][0] = y_0 + half_main;
		this.yCoordsMain[1][1] = y_0 + mainLength;
		this.yCoordsMain[1][2] = y_0 + mainLength;
		this.yCoordsMain[1][3] = y_0 + mainLength - subLength;
		this.yCoordsMain[1][4] = y_0 + mainLength - subLength;
		this.yCoordsMain[1][5] = y_0 + half_main;

		this.yCoordsMain[2][0] = y_0 + half_main;
		this.yCoordsMain[2][1] = y_0 + half_main;
		this.yCoordsMain[2][2] = y_0 + subLength;
		this.yCoordsMain[2][3] = y_0 + subLength;
		this.yCoordsMain[2][4] = y_0;
		this.yCoordsMain[2][5] = y_0;

		this.yCoordsMain[3][0] = y_0 + half_main;
		this.yCoordsMain[3][1] = y_0 + half_main;
		this.yCoordsMain[3][2] = y_0;
		this.yCoordsMain[3][3] = y_0;
		this.yCoordsMain[3][4] = y_0 + subLength;
		this.yCoordsMain[3][5] = y_0 + subLength;

		this.xCoordsSub[0] = x_0;
		this.xCoordsSub[1] = x_0 + mainLength - subLength;
		this.xCoordsSub[2] = x_0 + mainLength - subLength;
		this.xCoordsSub[3] = x_0;

		this.yCoordsSub[0] = y_0 + mainLength - subLength;
		this.yCoordsSub[1] = y_0 + mainLength - subLength;
		this.yCoordsSub[2] = y_0;
		this.yCoordsSub[3] = y_0;

		this.xCoordsFreq[0] = x_0 + (subLength / 2)-22;
		this.xCoordsFreq[1] = x_0 + mainLength - (subLength / 2) - 22;
		this.xCoordsFreq[2] = x_0 + mainLength - (subLength / 2) - 22;
		this.xCoordsFreq[3] = x_0 + (subLength / 2) - 22;

		this.yCoordsFreq[0] = y_0 + mainLength - (subLength / 2);
		this.yCoordsFreq[1] = y_0 + mainLength - (subLength / 2);
		this.yCoordsFreq[2] = y_0 + (subLength / 2);
		this.yCoordsFreq[3] = y_0 + (subLength / 2);

		this.xCoordsMainFreq[0] = x_0 + (mainLength / 4) - 10;
		this.xCoordsMainFreq[1] = x_0 + (mainLength * 3 / 4) - 10;
		this.xCoordsMainFreq[2] = x_0 + (mainLength * 3 / 4) - 10;
		this.xCoordsMainFreq[3] = x_0 + (mainLength / 4) - 10;

		this.yCoordsMainFreq[0] = y_0 + (mainLength * 3 / 4) - 10;
		this.yCoordsMainFreq[1] = y_0 + (mainLength * 3 / 4) - 10;
		this.yCoordsMainFreq[2] = y_0 + (mainLength / 4) + 20;
		this.yCoordsMainFreq[3] = y_0 + (mainLength / 4) + 20;

		this.xCoordsGroupLabels[0] = x_0 - (subLength);
		this.xCoordsGroupLabels[1] = x_0 + mainLength - (int) (subLength * 0);
		this.xCoordsGroupLabels[2] = x_0 + mainLength - (int) (subLength * 0);
		this.xCoordsGroupLabels[3] = x_0 - (subLength);

		this.yCoordsGroupLabels[0] = y_0 + mainLength + (subLength / 3);
		this.yCoordsGroupLabels[1] = y_0 + mainLength + (subLength / 3);
		this.yCoordsGroupLabels[2] = y_0 - (subLength / 4);
		this.yCoordsGroupLabels[3] = y_0 - (subLength / 4);
	}

	private void paintAttractors() {
		BasicStroke lineStroke = new BasicStroke(2);
		g2d.setStroke(lineStroke);

		//at first, paint the main Attracttors, as Polygons
		for (int i = 0; i < this.dim; ++i) {
			//create polygons
			this.mainAttractors[i] = new Polygon(this.xCoordsMain[i], this.yCoordsMain[i], 6);
			//draw polygons
			g2d.setColor(drawColor);
			g2d.draw(this.mainAttractors[i]);
			//fill Polygons
			g2d.setColor(this.mainColor[i]);
			g2d.fill(this.mainAttractors[i]);
		}

		for (int i = 0; i < this.dim; ++i) {
			//create rectangles
			int[] xcoords = new int[]{this.xCoordsSub[i], this.xCoordsSub[i], this.xCoordsSub[i]+subLength, this.xCoordsSub[i]+subLength};
			int[] ycoords = new int[]{this.yCoordsSub[i], this.yCoordsSub[i]+subLength, this.yCoordsSub[i]+subLength, this.yCoordsSub[i]};
			this.attractors[i] = new Polygon(xcoords, ycoords, 4);
			
			g2d.setColor(drawColor);
			g2d.draw(this.attractors[i]);

			g2d.setColor(this.attColor[i]);
			g2d.fill(this.attractors[i]);
		}
	}

	private void paintLabels(Graphics2D g2d) {
		Font tmpFont = g2d.getFont();
		Font verdaFont = new Font("Verdana", Font.BOLD, 12);

		//draw frequencies
		g2d.setColor(writeColor);
		g2d.setFont(tmpFont.deriveFont(12f));

		for (int i = 0; i < this.freqs.length; ++i) {
			g2d.drawString(this.freqs[i], this.xCoordsFreq[i], this.yCoordsFreq[i]);
			if(freqsAdd[i] != null) {
				g2d.drawString(freqsAdd[i], this.xCoordsFreq[i] - 5, this.yCoordsFreq[i] + tmpFont.getSize2D());
			}
		}

		//draw main frequencies
		g2d.setColor(Color.black);
		g2d.setFont(tmpFont.deriveFont(Font.BOLD, 14f));

		for (int i = 0; i < this.mainFreqs.length; ++i) {
			g2d.drawString(this.mainFreqs[i], this.xCoordsMainFreq[i], this.yCoordsMainFreq[i]);
		}

		//draw group labels
		String[] tmpLabels = this.points.getGroupLabels();
		
		g2d.setColor(writeColor);
		g2d.setFont(verdaFont);

		for (int i = 0; i < tmpLabels.length; ++i) {
			g2d.drawString(tmpLabels[i], this.xCoordsGroupLabels[i], this.yCoordsGroupLabels[i]);
		}

		//used to draw CoG centered
		FontRenderContext frc = g2d.getFontRenderContext();
		TextLayout tl = new TextLayout(this.gravyCenter, verdaFont, frc);

		Rectangle2D r = tl.getBounds();
		int width = this.getWidth();
		tl.draw(g2d, (width / 2) - (float) r.getCenterX(), 30f);
		
		g2d.setFont(tmpFont);
	}

	public void setupPlot(PlotContainer plotContainer) {
		plotContainer.setPreferredTitle("Attractor Window 4D", this);
		
		mainColor = new Color[]{
				fillMain,
				fillMain,
				fillMain,
				fillMain
			};
		attColor = new Color[]{
				fillAtt,
				fillAtt,
				fillAtt,
				fillAtt
			};
	}
}
