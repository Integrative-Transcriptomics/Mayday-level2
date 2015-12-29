package mayday.vis3.plots.tagcloud;

import java.awt.Color;

import mayday.vis3.legend.LegendItem;

public class TagCloudLegendItem extends LegendItem implements Comparable<TagCloudLegendItem> {

	public TagCloudLegendItem(Color c, String labelText) {
		super(c, labelText);
	}
	
	public int compareTo(TagCloudLegendItem item) {
		int number1 = Integer.parseInt(this.name.getText().split(" ")[0]);
		int number2 = Integer.parseInt(item.name.getText().split(" ")[0]);
		
		return number2 - number1;
	}
}
