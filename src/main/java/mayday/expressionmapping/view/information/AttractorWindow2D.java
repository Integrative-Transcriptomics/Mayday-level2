/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mayday.expressionmapping.view.information;

import java.awt.BasicStroke;
import java.awt.Color;
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
public class AttractorWindow2D extends AttractorWindow {

	private PointList<? extends Point> points;
	private int dim = 2;
	
	private int windowSize = 600;
	private Graphics2D g2d;
	private final static Color drawColor = Color.black;
	private final static Color writeColor = Color.black;
	private final static Color backgroundColor = Color.white;
	
	private int[][] xCoordsMain;
	private int[][] yCoordsMain;
	private int[][] xCoordsAtt;
	private int[][] yCoordsAtt;
	private int[] xCoordsFreq;
	private int[] yCoordsFreq;
	private int[] xCoordsMainFreq;
	private int[] yCoordsMainFreq;
	private int yCoordLabel;
	
	private String[] freqs;
	private String[] mainFreqs;
	private String gravyCenter;

	/**
	 * @param master
	 */
	public AttractorWindow2D(MainFrame master) {
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
		this.addMouseListener(this);
		this.setBackground(backgroundColor);
		this.mainAttractors = new Polygon[2];
		this.attractors = new Polygon[3];
		
		this.xCoordsMain = new int[2][4];
		this.yCoordsMain = new int[2][4];

		this.xCoordsAtt = new int[3][4];
		this.yCoordsAtt = new int[3][4]; 
		
		this.xCoordsFreq = new int[3];
		this.yCoordsFreq = new int[3];
		
		this.xCoordsMainFreq = new int[2];
		this.yCoordsMainFreq = new int[2];
		
		this.mainFreqs = new String[2];
		this.freqs = new String[3];
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

		int sideLength = (int) (size * 0.8);
		int height = (int) (size * 0.2);

		double x_0 = (int) (getWidth() - sideLength)/2;
		double y_0 = (int) (getHeight() * 0.8);
		
		/* the main attractors
		 */
	
		this.xCoordsMain[0][0] = (int)x_0;
		this.xCoordsMain[0][1] = (int) (x_0 + sideLength / 2);
		this.xCoordsMain[0][2] = (int) (x_0 + sideLength / 2);
		this.xCoordsMain[0][3] = (int)x_0;
		this.yCoordsMain[0][0] = (int)y_0;
		this.yCoordsMain[0][1] = (int)y_0;
		this.yCoordsMain[0][2] = (int) (y_0 - height);
		this.yCoordsMain[0][3] = (int) (y_0 - height);
		
		this.xCoordsMain[1][0] = (int) (x_0 + sideLength / 2);
		this.xCoordsMain[1][1] = (int) (x_0 + sideLength);
		this.xCoordsMain[1][2] = (int) (x_0 + sideLength);
		this.xCoordsMain[1][3] = (int) (x_0 + sideLength / 2);
		this.yCoordsMain[1][0] = (int)y_0;
		this.yCoordsMain[1][1] = (int)y_0;
		this.yCoordsMain[1][2] = (int) (y_0 - height);
		this.yCoordsMain[1][3] = (int) (y_0 - height);
	
		/* all attractors
		 */
		
		//A_0
		this.xCoordsAtt[0][0] = (int)x_0;
		this.xCoordsAtt[0][1] = (int) (x_0 + sideLength / 4);
		this.xCoordsAtt[0][2] = (int) (x_0 + sideLength / 4);
		this.xCoordsAtt[0][3] = (int)x_0;
		this.yCoordsAtt[0][0] = (int) (y_0 - 2 * height); 
		this.yCoordsAtt[0][1] = (int) (y_0 - 2 * height);
		this.yCoordsAtt[0][2] = (int) (y_0 - 3 * height);
		this.yCoordsAtt[0][3] = (int) (y_0 - 3 * height);
		
		//A_1
		this.xCoordsAtt[1][0] = (int) (x_0 + sideLength *3 / 4);
		this.xCoordsAtt[1][1] = (int) (x_0 + sideLength);
		this.xCoordsAtt[1][2] = (int) (x_0 + sideLength);
		this.xCoordsAtt[1][3] = (int) (x_0 + sideLength *3 / 4);
		this.yCoordsAtt[1][0] = (int) (y_0 - 2 * height); 
		this.yCoordsAtt[1][1] = (int) (y_0 - 2 * height);
		this.yCoordsAtt[1][2] = (int) (y_0 - 3 * height);
		this.yCoordsAtt[1][3] = (int) (y_0 - 3 * height);
		
		//A_2
		this.xCoordsAtt[2][0] = (int) (x_0 + sideLength / 4);
		this.xCoordsAtt[2][1] = (int) (x_0 + sideLength * 3 / 4);
		this.xCoordsAtt[2][2] = (int) (x_0 + sideLength * 3 / 4);
		this.xCoordsAtt[2][3] = (int) (x_0 + sideLength / 4);
		this.yCoordsAtt[2][0] = (int) (y_0 - 2 * height); 
		this.yCoordsAtt[2][1] = (int) (y_0 - 2 * height);
		this.yCoordsAtt[2][2] = (int) (y_0 - 3 * height);
		this.yCoordsAtt[2][3] = (int) (y_0 - 3 * height);
		
		
		/* coordinates of the freqs
		 */
		
		// main attractor freqs
		this.xCoordsMainFreq[0] = (int) (x_0 + sideLength / 4 - 20); 
		this.yCoordsMainFreq[0] = (int) (y_0 - height / 2 + 5); 
		
		this.xCoordsMainFreq[1] = (int) (x_0 + sideLength * 3 / 4 -20); 
		this.yCoordsMainFreq[1] = (int) (y_0 - height / 2 + 5);
		
		// attractor freqs
		this.xCoordsFreq[0] = (int) (x_0 + sideLength / 8 - 20);
		this.yCoordsFreq[0] = (int) (y_0 - height * 2.5 + 5);
		
		this.xCoordsFreq[1] = (int) (x_0 + sideLength * 7 / 8 - 20);
		this.yCoordsFreq[1] = (int) (y_0 - height * 2.5 + 5);
		
		this.xCoordsFreq[2] = (int) (x_0 + sideLength / 2 - 20);
		this.yCoordsFreq[2] = (int) (y_0 - height * 2.5 + 5);
		
		
		/* y coordinate of the labels
		 */
		yCoordLabel = (int) (y_0 - height * 1.5 + 20); 
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

		/* create main attractors
		 */
		for (int i = 0; i < this.dim; ++i) {
			this.mainAttractors[i] = new Polygon(this.xCoordsMain[i], this.yCoordsMain[i], 4);

			g2d.setColor(drawColor);
			g2d.draw(this.mainAttractors[i]);

			g2d.setColor(this.mainColor[i]);
			g2d.fill(this.mainAttractors[i]);
		}
		
		/* create attractors
		 */
		for (int i = 0; i < 3; ++i) {
			this.attractors[i] = new Polygon(this.xCoordsAtt[i], this.yCoordsAtt[i], 4);

			g2d.setColor(drawColor);
			g2d.draw(this.attractors[i]);

			g2d.setColor(this.attColor[i]);
			g2d.fill(this.attractors[i]);
		}
	}
	
	private void paintLabels(Graphics2D g2d) {
		Font tmpFont = g2d.getFont();
		Font verdaFont = new Font("Verdana", Font.BOLD, 12);
		Font verdaFontLarge = new Font("Verdana", Font.BOLD, 18);

		/*draw frequencies
		 */
		g2d.setColor(writeColor);
		g2d.setFont(tmpFont.deriveFont(14f));

		for (int i = 0; i < this.freqs.length; ++i) {
			g2d.drawString(this.freqs[i], this.xCoordsFreq[i], this.yCoordsFreq[i]);
		}

		/*draw main frequencies
		 */
		g2d.setColor(Color.black);

		g2d.setFont(tmpFont.deriveFont(Font.BOLD, 14f));

		for (int i = 0; i < this.mainFreqs.length; ++i) {
			g2d.drawString(this.mainFreqs[i], this.xCoordsMainFreq[i], this.yCoordsMainFreq[i]);
		}

		g2d.setColor(writeColor);
		FontRenderContext frc = g2d.getFontRenderContext();
		TextLayout tl;
		Rectangle2D r;
		
		/* draw group labels
		 */
		String[] tmpLabels = this.points.getGroupLabels();
		
		float x1 = (getWidth() - this.windowSize * 0.8f) / 2.0f;
		
		tl = new TextLayout(tmpLabels[0], verdaFontLarge, frc);
		r = tl.getBounds();
		tl.draw(g2d, x1, this.yCoordLabel - (float)r.getMaxY());
		
		tl = new TextLayout(tmpLabels[1], verdaFontLarge, frc);
		r = tl.getBounds();
		
		float x2 = (getWidth() - this.windowSize * 0.8f) / 2.0f + this.windowSize * 0.8f - (float)r.getMaxX();
		
		tl.draw(g2d, (float) x2 , this.yCoordLabel - (float)r.getMaxY());
		
		/*used to draw CoG centered
		 */
		tl = new TextLayout(this.gravyCenter, verdaFont, frc);
		r = tl.getBounds();
		
		int width = this.getWidth();
		float height = this.getHeight() * 0.2f - 20f;
		tl.draw(g2d, (width / 2) - (float) r.getCenterX(), height);
		
		/* reset font 
		 */
		g2d.setFont(tmpFont);
	}

	@Override
	public void setupPlot(PlotContainer plotContainer) {
		plotContainer.setPreferredTitle("Attractor Window 2D", this);
		
		mainColor = new Color[]{
				fillMain,
				fillMain
			};

		attColor = new Color[]{
				fillAtt,
				fillAtt,
				fillAtt
			};
	}
}
