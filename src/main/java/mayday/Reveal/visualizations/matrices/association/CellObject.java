package mayday.Reveal.visualizations.matrices.association;

public class CellObject {

	private double sizeValue;
	private double colorValue;
	
	public CellObject(double sizeValue, double colorValue) {
		this.sizeValue = sizeValue;
		this.colorValue = colorValue;
	}
	
	public double getSizeValue() {
		return this.sizeValue;
	}
	
	public double getColorValue() {
		return this.colorValue;
	}
	
	public void setSizeValue(double value) {
		this.sizeValue = value;
	}

	public void setColorValue(double colorValue) {
		this.colorValue = colorValue;
	}
}
