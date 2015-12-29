package mayday.Reveal.visualizations.snpmap.meta;

import java.util.ArrayList;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;

public class SNPMapMetaDataSetting extends HierarchicalSetting {

	private SNPMapMetaComponent snpMapComp;
	
	public static final int SORTING_CHANGED = 0;
	public static final int AGGREGATION_CHANGED = 1;
	public static final int COLOR_GRADIENT_CHANGED = 2;
	
	
	private ArrayList<ColorGradientSetting> gradients;
	private HierarchicalSetting colorGradients;
	
	public SNPMapMetaDataSetting(SNPMapMetaComponent snpMapComp) {
		super("Meta Data");
		this.snpMapComp = snpMapComp;
		
		gradients = new ArrayList<ColorGradientSetting>();
		addSetting(colorGradients = new HierarchicalSetting("Color Gradients"));
		
		addChangeListener(new SMMetaDataChangeListener());
	}
	
	public ColorGradient getColorGradient(int index) {
		int numGenes = snpMapComp.snpMap.getData().getGenes().size();
		
		if(index > 0 && index <= numGenes)
			return this.gradients.get(1).getColorGradient();
		
		return this.gradients.get(index).getColorGradient();
	}
	
	public void addColorGradientSetting(String name, double min, double max) {
		ColorGradientSetting cgs = new ColorGradientSetting(name, null, ColorGradient.createDefaultGradient(min, max));
		gradients.add(cgs);
		colorGradients.addSetting(cgs);
	}
	
	private class SMMetaDataChangeListener implements SettingChangeListener {
		@Override
		public void stateChanged(SettingChangeEvent e) {
			int change = SORTING_CHANGED;
			snpMapComp.update(change);
		}
	}
	
	public SNPMapMetaDataSetting clone() {
		SNPMapMetaDataSetting s = new SNPMapMetaDataSetting(snpMapComp);
		s.fromPrefNode(this.toPrefNode());
		return s;
	}
}
