package mayday.wapiti.experiments.impl.imagene;

/**
 * @author "Nastasja Trunk"
 * This class realises the representation of an microarray as a data structure.
 * It includes Background and foreground information of both colors in a two color microarray, flaginformation, geneIDs given in the 
 * data file and printtips. 
 */

public class ImaGeneArray {
	//A string describing the microarray to be used in Plots to identify them
	String name;
	double[][] gbackground;
	double[][] gforeground;
	double[][] rbackground;
	double[][] rforeground;
	boolean[][] flagPoor;
	boolean[][] flagNegative;
	boolean[][] flagEmpty;
	String[][] geneID;
	int height;
	int width;
	private int printipWidth;
	private int printipHeight;

	boolean emptySpots = false;
	boolean poorSpots = false;
	boolean negativeSpots = false;
	
	int numberOfFlaggedSpots;

	public ImaGeneArray(int height, int width){
		gbackground   = new double[height][width];
		gforeground   = new double[height][width];
		rbackground   = new double[height][width];
		rforeground   = new double[height][width];
		flagPoor      = new boolean[height][width];
		flagNegative  = new boolean[height][width];
		flagEmpty     = new boolean[height][width];
		geneID        = new String[height][width];
		this.height = height;
		this.width = width;
		numberOfFlaggedSpots =0;
	}
	
	
	
	/**
	 * Set the green value of a specific spot in the array
	 * This also requires the flag information and geneID
	 * @param i
	 * @param j
	 * @param backg
	 * @param foreg
	 * @param poor
	 * @param negative
	 * @param empty
	 * @param geneID
	 */
	public void setSpotG(int i, int j, double backg, double foreg,
							boolean poor, boolean negative, boolean empty, String geneID){
		if((i >=0) && (i< height) && (j>=0) && (j<width)){
			gbackground[i][j] = backg;
			gforeground[i][j] = foreg;
			flagPoor[i][j] = poor;
			flagNegative[i][j]= negative;
			flagEmpty[i][j] = empty;
			this.geneID[i][j] = geneID;
		}
	}
	
	/**
	 * Set the red value of a specific spot in the array
	 * @param i
	 * @param j
	 * @param backg
	 * @param foreg
	 * the GeneID should be given by the green Channel
	 */
	public void setSpotR(int i, int j, double backg, double foreg){
		if((i >=0) && (i< height) && (j>=0) && (j<width)){
			rbackground[i][j] = backg;
			rforeground[i][j] = foreg;
		}
	}

	/**
	 * gives back as String representation of the Spot with the coordinates i and j.
	 * It was mainly written for debug purposes but can be used by everyone who seems to need it 
	 * @param i
	 * @param j
	 * @return
	 */
	public String toString(int i, int j){
		StringBuffer ret = new StringBuffer();
				ret.append(geneID[i][j] + " Control Background  " + gbackground[i][j] + " Control Foreground " + gforeground[i][j]
				                  + " Experiment background" + rbackground[i][j] + " Experiment foreground " + rforeground[i][j] + " Flag Poor  "
				                  + flagPoor[i][j] + " Flag Negative " + flagNegative[i][j] + " Flag empty " + flagEmpty[i][j]  );	
		return ret.toString();
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public double[] getBackground(int i, int j) {
		double[]ret = {gbackground[i][j], rbackground[i][j] };
		return ret;
	}

	public double getBackgroundG(int i, int j){
		return gbackground[i][j];
	}
	
	public double getBackgroundR(int i, int j){
		return rbackground[i][j];
	}
	
	public double[] getForeground(int i, int j) {
		double[]ret = {gforeground[i][j], rforeground[i][j] };
		return ret;
	}
	
	public double getForegroundG(int i, int j){
		return gforeground[i][j];
	}
	
	public double getForegroundR(int i, int j){
		return rforeground[i][j];
	}
	public String getGeneID(int i, int j){
		return geneID[i][j];
	}

	public boolean isEmptySpots() {
		return emptySpots;
	}

	public void setEmptySpots(boolean emptySpots) {
		this.emptySpots = emptySpots;
	}

	public boolean isNegativeSpots() {
		return negativeSpots;
	}

	public void setNegativeSpots(boolean negativeSpots) {
		this.negativeSpots = negativeSpots;
	}

	public boolean isPoorSpots() {
		return poorSpots;
	}

	public void setPoorSpots(boolean poorSpots) {
		this.poorSpots = poorSpots;
	}

	public boolean getFlagEmpty(int i, int j) {
		return flagEmpty[i][j];
	}

	public boolean getFlagNegative(int i, int j) {
		return flagNegative[i][j];
	}

	public boolean getFlagPoor(int i, int j) {
		return flagPoor[i][j];
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the number of Spots on the Microarray
	 * @return
	 */
	public int getNumberOfSpots(){
		int ret = height*width;
		return ret;
	}

	public int getNumberOfFlaggedSpots() {
		return numberOfFlaggedSpots;
	}

	public void setNumberOfFlaggedSpots(int numberOfFlaggedSpots) {
		this.numberOfFlaggedSpots = numberOfFlaggedSpots;
	}

	public int getPrintipHeight() {
		return printipHeight;
	}

	public void setPrintipHeight(int printipHeight) {
		this.printipHeight = printipHeight;
	}

	public int getPrintipWidth() {
		return printipWidth;
	}

	public void setPrintipWidth(int printipWidth) {
		this.printipWidth = printipWidth;
	}
	
}
