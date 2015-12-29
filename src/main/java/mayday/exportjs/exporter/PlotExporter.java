package mayday.exportjs.exporter;

import java.awt.Color;

public abstract class PlotExporter {
	
	protected String variableName;
	
	protected PlotExporter(String variableName){
		this.variableName = variableName;
	}
	
	public abstract String getString();
	
	public abstract String getName();
	
	public abstract String getPlotOptions();
	
	public abstract String getPlotDescription();

	protected String createSelectedProbesColorString(String name, int red, int green, int blue){
		String result = "var " + name +  " = \""
			+ createRGBColorString(red, green, blue) + "\";\n\n";
		return result;
	}
	
	protected String createRGBColorString(int red, int green, int blue){
		return "rgb(" + red + "," + green + "," + blue + ")";
	}
	
	protected String createRGBColorString(Color color){
		return "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
	}

	public String getVariableName() {
		return variableName;
	}
	
}
