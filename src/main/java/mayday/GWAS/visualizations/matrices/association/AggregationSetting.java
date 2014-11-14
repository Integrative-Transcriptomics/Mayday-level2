package mayday.GWAS.visualizations.matrices.association;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;

public class AggregationSetting extends HierarchicalSetting {

	private ColorGradientSetting colorGradient;
	private AssociationMatrix matrix;
	
	public AggregationSetting(AssociationMatrix matrix) {
		super("Aggregation Setting");
		
		this.matrix = matrix;
		
		this.addSetting(colorGradient = new ColorGradientSetting("Aggregation Color Gradient", null, ColorGradient.createDefaultGradient(-16, 16)));
		
		this.addChangeListener(new SettingChangeListener() {
			@Override
			public void stateChanged(SettingChangeEvent e) {
				AggregationSetting.this.matrix.updatePlot();
			}
		});
	}
	
	public ColorGradient getColorGradient() {
		return this.colorGradient.getColorGradient();
	}
	
	public AggregationSetting clone() {
		AggregationSetting s = new AggregationSetting(matrix);
		s.fromPrefNode(this.toPrefNode());
		return s;
	}
}
