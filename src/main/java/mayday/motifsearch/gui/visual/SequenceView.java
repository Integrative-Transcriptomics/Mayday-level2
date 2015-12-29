package mayday.motifsearch.gui.visual;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.SwingUtilities;

import mayday.motifsearch.gui.listeners.ColorModelListener;
import mayday.motifsearch.model.Sequence;
import mayday.motifsearch.model.Site;


/**
 * Represents the model of a sequence with sites of Motifs. After
 * instantiation a sequence has to be set to this object. this class holds two
 * types of color models, one with a black background and one with a white
 * background. (white is default)
 * 
 * @author Frederik Weber
 */
public class SequenceView
extends HighLightSelector implements ColorModelListener{

    private boolean isWhiteColorModel; // color model
    private Sequence sequence;
    private int longestSequenceSeqLength;
    private static final long serialVersionUID = 1L;
    protected double minSignificanceValueLog10;
    protected double maxSignificanceValueLog10; 


    Vector<MotifViewFrame> motifViewFrameList = new Vector<MotifViewFrame>();

    /**
     * creates a sequence colorModel with motifs
     * 
     */
    public SequenceView(double minSignificanceValueLog10, double maxSignificanceValueLog10) {
	super();
	this.minSignificanceValueLog10 = minSignificanceValueLog10;
	this.maxSignificanceValueLog10 = maxSignificanceValueLog10;
	this.setBackground(((this.isWhiteColorModel) ? Color.WHITE
		: Color.BLACK));
    }

    /**
     * sets the size of the longest sequence that is in the input dataset of
     * sequences for reasons of drawing the sequence in relative length of the
     * longest sequence
     * 
     * @param longestSequenceSeqLength
     *                the longest known sequence length (in e.g. the sequence
     *                table model)
     */
    public void setlongestSequenceLength(int longestSequenceLength) {
	this.longestSequenceSeqLength = longestSequenceLength;
    }

    public void setSequence(Sequence sequence) {
	this.sequence = sequence;
    }

    /**
     * @see javax.swing.JComponent paints a sequence as a line with its sites as colored rectangles bond to this line. Highlighting is drawn
     *      in this method too.
     * 
     * @param g
     *                the Graphics object of this Component
     * 
     */

    @Override
    protected final void paintComponent(Graphics g) {
	Graphics2D gc = (Graphics2D) g;

	/* clear all colorModel frames */
	this.motifViewFrameList.clear();

	Rectangle area = SwingUtilities.calculateInnerArea(this, null);

	gc
	.setColor(((this.sequence.getSites().isEmpty()) ? ((this.isWhiteColorModel) ? new Color(
		116, 112, 112, 20)
	: Color.DARK_GRAY)
	: ((this.isWhiteColorModel) ? Color.WHITE : Color.BLACK)));
	gc.fillRect(area.x, area.y, area.x + area.width, area.y + area.height);

	/* anti aliasing activation and other rendering hints */
	RenderingHints newRenderingHints = gc.getRenderingHints();
	newRenderingHints.put(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
	gc.setRenderingHints(newRenderingHints);
	newRenderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
		RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
	newRenderingHints.put(RenderingHints.KEY_RENDERING,
		RenderingHints.VALUE_RENDER_QUALITY);
	newRenderingHints.put(RenderingHints.KEY_COLOR_RENDERING,
		RenderingHints.VALUE_COLOR_RENDER_QUALITY);
	gc.setRenderingHints(newRenderingHints);

	/* begin drawing */
	if (this.longestSequenceSeqLength > 1 || this.sequence != null) {

	    /* set relative draw positions for draw orientation */
	    final int middleY = (area.height / 2);
	    final int endDrawPositionSequence = area.width - 40;
	    final int maxDrawWidth = endDrawPositionSequence - 10;
	    final int relativeSequenceDrawWidth = (int) ((double) (this.sequence
		    .getLength() * maxDrawWidth) / this.longestSequenceSeqLength);



	    final int beginDrawPosition = endDrawPositionSequence
	    - relativeSequenceDrawWidth + 1;
	    final int relativeTSSDrawPosition = -1 + beginDrawPosition +(int) ((double) (this.sequence.getTlSSPosition() * maxDrawWidth) / this.longestSequenceSeqLength);

	    final int SEQUENCE_DRAW_HEIGHT = 2;
	    final int MOTIF_DRAW_HEIGHT_FACTOR = ((area.height * 4) / 10);

	    if (sequence != null) {

		/* draw a line representing the sequence */
		gc.setColor((this.isWhiteColorModel) ? Color.BLACK
			: Color.WHITE);
		gc.fillRect(beginDrawPosition, middleY,
			relativeSequenceDrawWidth, SEQUENCE_DRAW_HEIGHT);
		gc.setColor((this.isWhiteColorModel) ? Color.DARK_GRAY
			: Color.GRAY);

		/* draw + - and 3'*/
		gc.drawString("+", 1, area.height/3);
		gc.drawString("-", 1, 3*(area.height/4)+ SEQUENCE_DRAW_HEIGHT) ;
		gc.drawString("3'", endDrawPositionSequence + 5, middleY); 

		/*draw the TlSS (translation start site)*/
		if ((this.sequence.getTlSSPosition() != -1) && (this.sequence.getTlSSPosition() <= this.sequence.getLength())) {
		    gc.setColor(new Color(200,0,0));
		    gc.drawLine(relativeTSSDrawPosition, 7, relativeTSSDrawPosition, area.height);
		    gc.drawLine(relativeTSSDrawPosition, 7, relativeTSSDrawPosition + 5, 7);
		    gc.drawString("> TlSS", relativeTSSDrawPosition + 4,12);
		}

		boolean isHighlighted = false;
		for (Site site : sequence.getSites()) {

		    Color TFColor = site.getMotif().getColor();
		    gc.setColor(TFColor);

		    /* determine highlighting */
		    isHighlighted = this.shouldMotifOfSiteBeHighlighted(site);
		    if (isHighlighted) {
			/* alpha blending set to full color */
			gc.setComposite(getAlphaCompositForMotif(site, 0, 1));
		    }
		    else {
			/* alpha blending with site score */
			gc.setComposite(getAlphaCompositForMotif(site, 0, (-1 * this
				.norm(Math.log10(site.getSignificanceValue()), this.minSignificanceValueLog10, this.maxSignificanceValueLog10, -1, -0.25))));
		    }

		    /*
		     * determine the relative draw length according to this
		     * components constraints
		     */
		    int relativeMotifDrawLength = (int) ((double) (site
			    .getMotif().getLength() * maxDrawWidth) / this.longestSequenceSeqLength);

		    /* the motif is allways drawn */
		    if (relativeMotifDrawLength == 0){
			relativeMotifDrawLength = 1;
		    }

		    int relativeMotifDrawPos = beginDrawPosition
		    + (int) ((double) ((site.getPosition()-1) * maxDrawWidth) / this.longestSequenceSeqLength);

		    /* normed absolute draw height of motif */
		    int absoluteMotifDrawHight = (int) (MOTIF_DRAW_HEIGHT_FACTOR * (-1 * this
			    .norm(Math.log10(site.getSignificanceValue()), this.minSignificanceValueLog10, this.maxSignificanceValueLog10, -1, -0.1)));

		    /*
		     * determine the orientation of a site and draw it
		     * above or under the sequence line
		     */
		    if (!sequence.isPlusStrand()) {

			gc.fillRect(relativeMotifDrawPos, middleY
				+ SEQUENCE_DRAW_HEIGHT, relativeMotifDrawLength,
				absoluteMotifDrawHight);
			gc.setColor((this.isWhiteColorModel) ? Color.BLACK
				: Color.WHITE);
			gc.drawRect(relativeMotifDrawPos, middleY
				+ SEQUENCE_DRAW_HEIGHT, relativeMotifDrawLength,
				absoluteMotifDrawHight);
			this.motifViewFrameList
			.add(new MotifViewFrame(new Rectangle(
				relativeMotifDrawPos, middleY
				+ SEQUENCE_DRAW_HEIGHT,
				relativeMotifDrawLength,
				absoluteMotifDrawHight), site));

			/* if it is highlighted paint highlighter */
			if (isHighlighted) {
			    gc.setColor(Color.RED);
			    int[] xPoints = {
				    relativeMotifDrawPos,
				    relativeMotifDrawPos + relativeMotifDrawLength
				    / 2 + 1,
				    relativeMotifDrawPos + relativeMotifDrawLength
				    + 1 };
			    int[] yPoints = {
				    (middleY + absoluteMotifDrawHight)
				    + ((area.height - (middleY + absoluteMotifDrawHight)) / 2),
				    middleY + absoluteMotifDrawHight + 2,
				    (middleY + absoluteMotifDrawHight)
				    + ((area.height - (middleY + absoluteMotifDrawHight)) / 2) };
			    gc.fillPolygon(xPoints, yPoints, 3);
			    gc.setColor(TFColor);
			    gc.drawLine(xPoints[0], yPoints[0], xPoints[2],
				    yPoints[0]);
			}

		    }
		    else {
			gc.fillRect(relativeMotifDrawPos, middleY
				- absoluteMotifDrawHight - 1,
				relativeMotifDrawLength, absoluteMotifDrawHight);

			gc.setColor((this.isWhiteColorModel) ? Color.BLACK
				: Color.WHITE);
			gc.drawRect(relativeMotifDrawPos, middleY
				- absoluteMotifDrawHight - 1,
				relativeMotifDrawLength, absoluteMotifDrawHight);

			this.motifViewFrameList
			.add(new MotifViewFrame(new Rectangle(
				relativeMotifDrawPos, middleY
				- absoluteMotifDrawHight - 1,
				relativeMotifDrawLength,
				absoluteMotifDrawHight), site));

			/* if it is highlighted paint high lighter triangle*/
			if (isHighlighted) {
			    gc.setColor(Color.RED);
			    int[] xPoints = {
				    relativeMotifDrawPos,
				    relativeMotifDrawPos + relativeMotifDrawLength
				    / 2 + 1,
				    relativeMotifDrawPos + relativeMotifDrawLength
				    + 1 };
			    int[] yPoints = {
				    (middleY - absoluteMotifDrawHight - 1) / 2,
				    middleY - absoluteMotifDrawHight - 1,
				    (middleY - absoluteMotifDrawHight - 1) / 2 };
			    gc.fillPolygon(xPoints, yPoints, 3);
			    gc.setColor(TFColor);
			    gc.drawLine(xPoints[0], yPoints[0], xPoints[2],
				    yPoints[0]);
			}
		    }
		}
	    }
	}
    }

    /**
     * determines the alpha composite with the corresponding alpha value for a
     * site. the alpha value is normed for the score to the
     * interval between threshold value and 1.0
     * 
     * @param site
     *                the site to get the alpha value for
     * @param thWert
     *                the threshold value to norm as minimum interval
     * @param alphaValue
     *                a alpha flaoting point value
     * @return an alpha composite to set for an Graphics object
     */
    private final AlphaComposite getAlphaCompositForMotif(Site site,
	    double thWert, double alphaValue) {
	AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
		this.norm(site.getSignificanceValue(), 0, 1, alphaValue,
			1));
	return ac;
    }


    /**
     * determines which sites are at a specific 2D coordinate in the
     * colorModel
     * 
     * @param x
     *                horizontal position of the click relative to the colorModel
     * @param y
     *                vertical position of the click relative to the colorModel
     * @return the sites that are beneath the coordinate in the colorModel
     */
    private ArrayList<Site> sitesAt(int x, int y) {
	ArrayList<Site> sites = new ArrayList<Site>();
	for (MotifViewFrame vf : this.motifViewFrameList) {
	    if (vf.area.contains(x, y)) {
		sites.add(vf.site);
	    }
	}
	return sites;
    }

    /**
     * if a mouse event on the sequence table occured and it was a double click
     * the then this click is perfromed A popup is showed that listes all
     * sites which the user clicked at
     * 
     * @param e
     * @see java.awt.event.MouseEvent
     * @param sequenceTable
     *                the table the click was originated
     */
    public final void mouseClicked(MouseEvent e, SequenceTable sequenceTable) {
	ArrayList<Site> sites = this.sitesAt(e.getX(), e.getY());
	if (!sites.isEmpty()) {
	    sequenceTable.setMotifSelectPopupMenu(new MotifSelectPopupMenu(sites,
		    this.siteSelectionModel,
		    MotifSelectPopupMenu.LIST_ALL_SITES));
	    sequenceTable.getMotifSelectPopupMenu().show(sequenceTable,
		    e.getXOnScreen() + 10, e.getYOnScreen());
	    this.fireSelected(sites);
	} 
    }

    /**
     * norms a value from one interval to an other
     * 
     * @param toNorm
     *                the parameter that should be normed
     * @param minInterval
     *                the minimal value of the former interval
     * @param maxInterval
     *                the maximal value of the former interval
     * @param minNormInterval
     *                the minimal value of the interval to norm on
     * @param maxNormInterval
     *                the maximal value of the interval to norm on
     * 
     */
    private final float norm(double toNorm, double minInterval,
	    double maxInterval, double minNormInterval, double maxNormInterval) {
	float f = (float) (((toNorm - minInterval) / (maxInterval - minInterval))
		* (maxNormInterval - minNormInterval) + minNormInterval);
	return f;
    }

    public final Sequence getSequence() {
	return this.sequence;
    }

    /**
     * gets a clone of the sequence colorModel to draw it on an other Container
     * 
     * @param sequence
     *                the sequence the clone gets to draw
     * @param longestSequenceSeqLength
     *                longest sequence length that occurs in the data subset
     */
    public SequenceView cloneForPreView(Sequence sequence,
	    int longestSequenceLength) {
	SequenceView sequenceViewClone = new SequenceView(this.minSignificanceValueLog10, this.minSignificanceValueLog10);
	sequenceViewClone.colorModelChanged(this.isWhiteColorModel);
	sequenceViewClone.setSequence(sequence);
	sequenceViewClone.setlongestSequenceLength(longestSequenceLength);
	sequenceViewClone.siteSelectionModel = this.siteSelectionModel;
	return sequenceViewClone;
    }

    /**
     * sets the color model to
     * 
     * @param isWhiteColorModel
     *                true if the white color model is used an the background is
     *                white, false if the black color model is used and
     *                background is black
     */

    public void colorModelChanged(boolean isWhiteColorModel){
	this.isWhiteColorModel = isWhiteColorModel;
	this.setBackground(((this.isWhiteColorModel) ? Color.WHITE
		: Color.BLACK));
    }
}

