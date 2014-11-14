package mayday.wapiti.experiments.impl.imagene;

/**
 * @author Nastasja Trunk
 * Fields and Methods to process an ImaGene output file - this class has no processing logic of its own
 */

public class ImaGeneDataStructure {
  
    //the following ints are offsets for the imagene Data file
    public final static int sMean=0;	
    public final static int sMedian=2;
    public final static int sMode =4;
    public final static int sTotal=8;
    private int offset=7;
    private int id=5;
    private int emptySpot=49;
    private int negativSpot=50;
	
	//offsets for poor spot parameters
    private int backContaminationOffset = 42;
    private int signalContaminationOffset=43;
    private int percentageOffset =44;
    private int perimeterOffset =45;
    private int shapeRegularityOffset=46;
    private int areaToPerimeterRatioOffset = 47;
    private int offsetFlag = 48;
	
    //	Array Dimensions
    private char field;
    private int metarows;
    private int metacols;
    private int rows;
    private int cols;
    private boolean emptySpots = false;
    private boolean poorSpots  = false;
    private boolean negativeSpots=false;
    
	//Settings for the poor Spot parameters
	private boolean backContamination =false;
	private boolean signalContamination = false;
	private boolean percentage = false;
	private boolean openPerimeter = false;
	private boolean shapeRegularity = false;
	private boolean perimeterToArea = false;
	private boolean offsetFailed =false;

    
	public String toString(){
		String out = new String(field + " " + metarows + " " + metacols + " " + rows + " " + cols + " " + emptySpots
								+ " " + poorSpots + " " + negativeSpots + "\n" );
		return out;
	}
	
	
    public int getEmptySpot() {
		return emptySpot;
	}

	public int getNegativSpot() {
		return negativSpot;
	}

	public int getId() {
		return id;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public boolean isEmptySpots() {
		return emptySpots;
	}

	public void setEmptySpots(boolean emptySpots) {
		this.emptySpots = emptySpots;
	}

	public char getField() {
		return field;
	}

	public void setField(char field) {
		this.field = field;
	}

	public int getMetaCols() {
		return metacols;
	}
	
	public void setMetaCols(int metacols) {
		this.metacols = metacols;
	}
	
	public int getMetaRows() {
		return metarows;
	}
	
	public void setMetaRows(int metarows) {
		this.metarows = metarows;
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

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getOffset() {
		return offset;
	}
	

	public int getSMean() {
		return sMean;
	}

	public int getSMedian() {
		return sMedian;
	}

	public int getSMode() {
		return sMode;
	}

	public int getSTotal() {
		return sTotal;
	}

	public boolean isBackContamination() {
		return backContamination;
	}

	public void setBackContamination(boolean backContamination) {
		this.backContamination = backContamination;
	}

	public boolean isOffsetFailed() {
		return offsetFailed;
	}

	public void setOffsetFailed(boolean offsetFailed) {
		this.offsetFailed = offsetFailed;
	}

	public boolean isOpenPerimeter() {
		return openPerimeter;
	}

	public void setOpenPerimeter(boolean openPerimeter) {
		this.openPerimeter = openPerimeter;
	}

	public boolean isPercentage() {
		return percentage;
	}

	public void setPercentage(boolean percentage) {
		this.percentage = percentage;
	}

	public boolean isPerimeterToArea() {
		return perimeterToArea;
	}

	public void setPerimeterToArea(boolean perimeterToArea) {
		this.perimeterToArea = perimeterToArea;
	}

	public boolean isShapeRegularity() {
		return shapeRegularity;
	}

	public void setShapeRegularity(boolean shapeRegularity) {
		this.shapeRegularity = shapeRegularity;
	}

	public boolean isSignalContamination() {
		return signalContamination;
	}

	public void setSignalContamination(boolean signalContamination) {
		this.signalContamination = signalContamination;
	}

	public int getAreaToPerimeterRatioOffset() {
		return areaToPerimeterRatioOffset;
	}

	public int getBackContaminationOffset() {
		return backContaminationOffset;
	}

	public int getPercentageOffset() {
		return percentageOffset;
	}

	public int getPerimeterOffset() {
		return perimeterOffset;
	}

	public int getShapeRegularityOffset() {
		return shapeRegularityOffset;
	}

	public int getSignalContaminationOffset() {
		return signalContaminationOffset;
	}

	public int getOffsetFlag() {
		return offsetFlag;
	}

	public void setOffsetFlag(int offsetFlag) {
		this.offsetFlag = offsetFlag;
	}

	public void setAreaToPerimeterRatioOffset(int areaToPerimeterRatioOffset) {
		this.areaToPerimeterRatioOffset = areaToPerimeterRatioOffset;
	}

	public void setBackContaminationOffset(int backContaminationOffset) {
		this.backContaminationOffset = backContaminationOffset;
	}

	public void setEmptySpot(int emptySpot) {
		this.emptySpot = emptySpot;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNegativSpot(int negativSpot) {
		this.negativSpot = negativSpot;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setPercentageOffset(int percentageOffset) {
		this.percentageOffset = percentageOffset;
	}

	public void setPerimeterOffset(int perimeterOffset) {
		this.perimeterOffset = perimeterOffset;
	}

	public void setShapeRegularityOffset(int shapeRegularityOffset) {
		this.shapeRegularityOffset = shapeRegularityOffset;
	}

	public void setSignalContaminationOffset(int signalContaminationOffset) {
		this.signalContaminationOffset = signalContaminationOffset;
	}
	
}
