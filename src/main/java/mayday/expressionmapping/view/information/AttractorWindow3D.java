/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author Stephan Gade
 */
@SuppressWarnings("serial")
public class AttractorWindow3D extends AttractorWindow {

	private PointList<? extends Point> points;
	private int dim = 3;
	private int windowSize = 600;
	private Graphics2D g2d;
	private final static Color drawColor = Color.black;
	private final static Color fillMain = Color.lightGray;
	private final static Color fillAtt = Color.gray;
	private final static Color writeColor = Color.black;
	private final static Color backgroundColor = Color.white;
	
	private int[][] xCoordsMain;
	private int[][] yCoordsMain;
	private int[][] xCoordsAtt;
	private int[][] yCoordsAtt;
	private int[] xCoordsMiddle;
	private int[] yCoordsMiddle;
	private int[] xCoordsFreq;
	private int[] yCoordsFreq;
	private int[] xCoordsMainFreq;
	private int[] yCoordsMainFreq;
	
	
	private String[] freqs;
	private String[] mainFreqs;
	private String gravyCenter;
	
	/**
	 * @param master
	 */
	public AttractorWindow3D(MainFrame master) {
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

		this.mainAttractors = new Polygon[3];
		this.attractors = new Polygon[7];

		this.xCoordsMain = new int[3][4];
		this.yCoordsMain = new int[3][4];
		
		this.xCoordsAtt = new int[6][4];
		this.yCoordsAtt = new int[6][4]; 
		
		this.xCoordsMiddle = new int[3];
		this.yCoordsMiddle = new int[3];
		
		this.xCoordsFreq = new int[7];
		this.yCoordsFreq = new int[7];
		
		this.xCoordsMainFreq = new int[3];
		this.yCoordsMainFreq = new int[3];
		
		this.mainFreqs = new String[3];
		this.freqs = new String[7];
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.g2d = (Graphics2D) g;
		computeCoordinates();
		paintAttractors();
		paintLabels(g2d);
	}

	private void computeCoordinates() {
		this.windowSize = Math.min(this.getWidth(), this.getHeight());
		
		/* double value holding the size, so we don't have to cast 
		 * every time
		 */
		double size = (double)this.windowSize;

		int sideLength = (int) ((double)windowSize * 0.4);

		double x_0;
		double y_0;

		/* first main attractor
		 */ 
		x_0 = (getWidth() - sideLength*2) / 2; //size * 0.07;
		y_0 = size * 0.75;
		this.xCoordsMain[0][0] = (int) x_0;
		this.xCoordsMain[0][1] = (int) (x_0 + (double) sideLength / 2);
		this.xCoordsMain[0][2] = (int) (x_0 + (double) sideLength / 2);
		this.xCoordsMain[0][3] = (int) (x_0 + (double) sideLength / 4);
		this.yCoordsMain[0][0] = (int) y_0;
		this.yCoordsMain[0][1] = (int) y_0;
		this.yCoordsMain[0][2] = (int) (y_0 - (sideLength / 2 / Math.sqrt(3)));
		this.yCoordsMain[0][3] = (int) (y_0 - (sideLength * Math.sqrt(3) / 4));
		
		this.xCoordsMainFreq[0] = (int)x_0 + 30;
		this.yCoordsMainFreq[0] = (int)y_0 - 20;

		/* second main attractor
		 */
		x_0 =  (getWidth() - sideLength) / 2; //x_0 + sideLength / 2;
		//y_0 remains unchanged
		this.xCoordsMain[1][0] = (int) x_0;
		this.xCoordsMain[1][1] = (int) (x_0 + (double) sideLength / 2);
		this.xCoordsMain[1][2] = (int) (x_0 + (double) sideLength / 4);
		this.xCoordsMain[1][3] = (int) x_0;
		this.yCoordsMain[1][0] = (int) y_0;
		this.yCoordsMain[1][1] = (int) y_0;
		this.yCoordsMain[1][2] = (int) (y_0 - (sideLength * Math.sqrt(3) / 4));
		this.yCoordsMain[1][3] = (int) (y_0 - (sideLength / 2 / Math.sqrt(3)));
		
		this.xCoordsMainFreq[1] = (int)x_0 + 30;
		this.yCoordsMainFreq[1] = (int)y_0 - 20;
		
		/* third main attractor
		 */
		x_0 = (getWidth() - sideLength*2) / 2; //size * 0.07;
		y_0 = size * 0.75;
		this.xCoordsMain[2][0] = (int) (x_0 + (double) sideLength / 4);
		this.xCoordsMain[2][1] = (int) (x_0 + (double) sideLength / 2);
		this.xCoordsMain[2][2] = (int) (x_0 + (double) sideLength * 3 / 4);
		this.xCoordsMain[2][3] = (int) (x_0 + (double) sideLength / 2);
		this.yCoordsMain[2][0] = (int) (y_0 - (sideLength * Math.sqrt(3) / 4));
		this.yCoordsMain[2][1] = (int) (y_0 - (sideLength / 2 / Math.sqrt(3)));
		this.yCoordsMain[2][2] = (int) (y_0 - (sideLength * Math.sqrt(3) / 4));
		this.yCoordsMain[2][3] = (int) (y_0 - (sideLength * Math.sqrt(3) / 2));
		
		this.xCoordsMainFreq[2] = (int)(x_0 + (double) sideLength / 4 + 30);
		this.yCoordsMainFreq[2] = (int)(y_0 - (sideLength * Math.sqrt(3) / 4) - 20);
		
		/* Compute the attractors
		 * 
		 */
		x_0 = (getWidth() + sideLength/2) / 2 - 30; //size * 0.93 - sideLength;
		y_0 = size * 0.75;
		
		/* A_0
		 */
		this.xCoordsAtt[0][0] = (int) x_0;
		this.xCoordsAtt[0][1] = (int) (x_0 + sideLength / 4);
		this.xCoordsAtt[0][2] = (int) (x_0 + sideLength / 4);
		this.xCoordsAtt[0][3] = (int) (x_0 + sideLength / 8);
		this.yCoordsAtt[0][0] = (int) y_0;
		this.yCoordsAtt[0][1] = (int) y_0;
		this.yCoordsAtt[0][2] = (int) (y_0 - sideLength / 4 / Math.sqrt(3));
		this.yCoordsAtt[0][3] = (int) (y_0 - sideLength / 4 * Math.sin(Math.PI/3));
		
		this.xCoordsFreq[0] = (int)(x_0 + 15); 
		this.yCoordsFreq[0] = (int)(y_0 - 10);
		
		/* A_1
		 */
		this.xCoordsAtt[1][0] = (int) (x_0 + sideLength * 3 / 4);
		this.xCoordsAtt[1][1] = (int) (x_0 + sideLength);
		this.xCoordsAtt[1][2] = (int) (x_0 + sideLength * 7 / 8);
		this.xCoordsAtt[1][3] = (int) (x_0 + sideLength * 3 / 4);
		this.yCoordsAtt[1][0] = (int) y_0;
		this.yCoordsAtt[1][1] = (int) y_0;
		this.yCoordsAtt[1][2] = (int) (y_0 - sideLength / 4 * Math.sin(Math.PI/3));
		this.yCoordsAtt[1][3] = (int) (y_0 - sideLength / 4 / Math.sqrt(3));
		
		this.xCoordsFreq[1] = (int)(x_0 + sideLength * 3 / 4 + 10);
		this.yCoordsFreq[1] = (int)(y_0 - 10);
		
		/* A_2
		 */
		this.xCoordsAtt[2][0] = (int) (x_0 + sideLength * 3 / 8);
		this.xCoordsAtt[2][1] = (int) (x_0 + sideLength / 2);
		this.xCoordsAtt[2][2] = (int) (x_0 + sideLength * 5 / 8);
		this.xCoordsAtt[2][3] = (int) (x_0 + sideLength / 2);
		this.yCoordsAtt[2][0] = (int) (y_0 - sideLength * 3 / 4 * Math.sin(Math.PI/3));
		this.yCoordsAtt[2][1] = (int) (y_0 - sideLength / Math.sqrt(3));
		this.yCoordsAtt[2][2] = (int) (y_0 - sideLength * 3 / 4 * Math.sin(Math.PI/3));
		this.yCoordsAtt[2][3] = (int) (y_0 - sideLength / 2 * Math.sqrt(3));
		
		this.xCoordsFreq[2] = (int)(x_0 + sideLength * 3 / 8 + 12);
		this.yCoordsFreq[2] = (int)(y_0 - sideLength * 3 / 4 * Math.sin(Math.PI/3) - 10);
		
		/* A_3 
		 */ 
		this.xCoordsAtt[3][0] = (int) (x_0 + sideLength / 4);
		this.xCoordsAtt[3][1] = (int) (x_0 + sideLength * 3 / 4);
		this.xCoordsAtt[3][2] = (int) (x_0 + sideLength * 3 / 4);
		this.xCoordsAtt[3][3] = (int) (x_0 + sideLength / 4);
		this.yCoordsAtt[3][0] = (int) y_0;
		this.yCoordsAtt[3][1] = (int) y_0;
		this.yCoordsAtt[3][2] = (int) (y_0 - sideLength / 4 /  Math.sqrt(3));
		this.yCoordsAtt[3][3] = (int) (y_0 - sideLength / 4 /  Math.sqrt(3));
		
		this.xCoordsFreq[3] = (int)(x_0 + sideLength / 2 - 10);
		this.yCoordsFreq[3] = (int)(y_0 - 10);
		
		/* A_4 
		 */ 
		this.xCoordsAtt[4][0] = (int) (x_0 + sideLength / 8);
		this.xCoordsAtt[4][1] = (int) (x_0 + sideLength / 4);
		this.xCoordsAtt[4][2] = (int) (x_0 + sideLength / 2);
		this.xCoordsAtt[4][3] = (int) (x_0 + sideLength * 3 / 8);
		this.yCoordsAtt[4][0] = (int) (y_0 - sideLength / 4 * Math.sin(Math.PI/3));
		this.yCoordsAtt[4][1] = (int) (y_0 - sideLength / 4 / Math.sqrt(3));
		this.yCoordsAtt[4][2] = (int) (y_0 - sideLength / Math.sqrt(3));
		this.yCoordsAtt[4][3] = (int) (y_0 - sideLength * 3 / 4 * Math.sin(Math.PI/3));
		
		this.xCoordsFreq[4] = (int)(x_0 + sideLength / 8 + 15);
		this.yCoordsFreq[4] = (int)(y_0 - sideLength / 4 * Math.sin(Math.PI/3) - 20);
		
		/* A_5 
		 */ 
		this.xCoordsAtt[5][0] = (int) (x_0 + sideLength * 3 / 4);
		this.xCoordsAtt[5][1] = (int) (x_0 + sideLength * 7 / 8);
		this.xCoordsAtt[5][2] = (int) (x_0 + sideLength * 5 / 8);
		this.xCoordsAtt[5][3] = (int) (x_0 + sideLength / 2);
		this.yCoordsAtt[5][0] = (int) (y_0 - sideLength / 4 / Math.sqrt(3));
		this.yCoordsAtt[5][1] = (int) (y_0 - sideLength / 4 * Math.sin(Math.PI/3));
		this.yCoordsAtt[5][2] = (int) (y_0 - sideLength * 3 / 4 * Math.sin(Math.PI/3));
		this.yCoordsAtt[5][3] = (int) (y_0 - sideLength / Math.sqrt(3));
		
		this.xCoordsFreq[5] = (int)(x_0 + sideLength * 3 / 4 - 15);
		this.yCoordsFreq[5] = (int)(y_0 - sideLength / 4 * Math.sin(Math.PI/3) - 20);
		
		/* A_6
		 */
		this.xCoordsMiddle[0] = (int)(x_0 + sideLength / 4);
		this.xCoordsMiddle[1] = (int)(x_0 + sideLength * 3 / 4);;
		this.xCoordsMiddle[2] = (int)(x_0 + sideLength / 2);
		this.yCoordsMiddle[0] = (int) (y_0 - sideLength / 4 / Math.sqrt(3));
		this.yCoordsMiddle[1] = (int) (y_0 - sideLength / 4 / Math.sqrt(3));
		this.yCoordsMiddle[2] = (int) (y_0 - sideLength / Math.sqrt(3));
		
		this.xCoordsFreq[6] = (int)(x_0 + sideLength / 2 - 20);
		this.yCoordsFreq[6] = (int)(y_0 - sideLength / 4 * Math.sin(Math.PI/3) - 20);
	}

	private void createLabels() {
		double[] tmpFreqs = this.points.getAttractorFreqs();
		double[] tmpMainFreqs = this.points.getMainAttractorFreqs();
		Point gravy = this.points.getCenterofMass();
		double[] tmpCoG = gravy.getCoordinates();
		
		//process attrac freqs	
		for (int i = 0; i < this.freqs.length; ++i) {
			if (tmpFreqs[i] < 0.001 && tmpFreqs[i] > 0) {
				this.freqs[i] = new String("<0.1%");
			} else {
				float tmp = ((float) (int) (tmpFreqs[i] * 10000)) / 100;
				this.freqs[i] = new String(tmp + "%");
			}
		}
		
		//process main freqs
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
	
	private void paintAttractors() {
		
		BasicStroke lineStroke = new BasicStroke(2);
		g2d.setStroke(lineStroke);

		for (int i = 0; i < this.dim; ++i) {
			/* create polygons
			 */
			this.mainAttractors[i] = new Polygon(this.xCoordsMain[i], this.yCoordsMain[i], 4);

			/* draw polygons
			 */
			g2d.setColor(drawColor);
			g2d.draw(this.mainAttractors[i]);

			/* fill Polygons
			 */
			g2d.setColor(this.mainColor[i]);
			g2d.fill(this.mainAttractors[i]);

		}
		
		/* create attractors
		 * 
		 */
		
		/* A_0 - A_5
		 */
		for (int i = 0; i < 6; ++i) {
			this.attractors[i] = new Polygon(this.xCoordsAtt[i], this.yCoordsAtt[i], 4);

			g2d.setColor(drawColor);
			g2d.draw(this.attractors[i]);

			g2d.setColor(this.attColor[i]);
			g2d.fill(this.attractors[i]);
		}
		
		// A_6
		this.attractors[6] = new Polygon(this.xCoordsMiddle, this.yCoordsMiddle, 3);
		
		g2d.setColor(drawColor);
		g2d.draw(this.attractors[6]);
		
		g2d.setColor(this.attColor[6]);
		g2d.fill(this.attractors[6]);
	}
	
	private void paintLabels(Graphics2D g2d) {
		Font tmpFont = g2d.getFont();
		Font verdaFont = new Font("Verdana", Font.BOLD, 12);
		Font verdaFontLarge = new Font("Verdana", Font.BOLD, 18);

		//draw frequencies
		g2d.setColor(writeColor);
		g2d.setFont(tmpFont.deriveFont(Font.BOLD, 12f));

		for (int i = 0; i < this.freqs.length; ++i) {
			g2d.drawString(this.freqs[i], this.xCoordsFreq[i], this.yCoordsFreq[i]);
		}

		// draw main frequencies
		g2d.setColor(Color.black);

		g2d.setFont(tmpFont.deriveFont(Font.BOLD, 14f));

		for (int i = 0; i < this.mainFreqs.length; ++i) {
			g2d.drawString(this.mainFreqs[i], this.xCoordsMainFreq[i], this.yCoordsMainFreq[i]);
		}

		
		g2d.setColor(writeColor);
		FontRenderContext frc = g2d.getFontRenderContext();
		TextLayout tl;
		Rectangle2D r;
		
		// draw group labels
		String[] tmpLabels = this.points.getGroupLabels();
		
		tl = new TextLayout(tmpLabels[0], verdaFontLarge, frc);
		r = tl.getBounds();
		
		float x1 = (getWidth() - windowSize * 0.8f)/2 + (float)r.getMinX();
		tl.draw(g2d, x1, this.windowSize*0.85f);
		
		tl = new TextLayout(tmpLabels[1], verdaFontLarge, frc);
		r = tl.getBounds();
		
		float x2 = (getWidth() + windowSize-50)/2.0f - (float)r.getMaxX();
		tl.draw(g2d, x2, this.windowSize*0.85f);
		
		tl = new TextLayout(tmpLabels[2], verdaFontLarge, frc);
		r = tl.getBounds();
		
		float x3 = getWidth() / 2 - (float) r.getCenterX();
		tl.draw(g2d, x3, this.windowSize*0.3f);
		
		// used to draw CoG centered
		tl = new TextLayout(this.gravyCenter, verdaFont, frc);
		r = tl.getBounds();
		
		int width = this.getWidth();
		tl.draw(g2d, (width / 2) - (float) r.getCenterX(), this.windowSize*0.1f);
		
		// reset font 
		g2d.setFont(tmpFont);
	}
	
	@Override
	public void setupPlot(PlotContainer plotContainer) {
		plotContainer.setPreferredTitle("Attractor Window 3D", this);
		
		mainColor = new Color[]{
				fillMain,
				fillMain,
				fillMain
			};

		attColor = new Color[]{
				fillAtt,
				fillAtt,
				fillAtt,
				fillAtt,
				fillAtt,
				fillAtt,
				fillAtt
			};
	}
}
