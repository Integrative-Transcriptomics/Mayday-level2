package mayday.transkriptorium.tracks.curve;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class CurveTrackPlugin extends AbstractTrackPlugin{

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo((Class) this.getClass(),
				"PAS.transkriptorium.readscurvetrack", 
				new String[0], 
				MC2,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Rendering coverage by mapped reads.",
				"Read coverage");
		return pli;
	}

	public CurveTrackPlugin() {}

	protected AbstractTrackSettings makeSetting() {
		return new CurveTrackSettings(model, this);
	}
	
	protected AbstractTrackRenderer makeRenderer() {
		return new CurveTrackRenderer(model, this);
	}
	
	protected int getDefaultHeight() {
		return 150;
	}
	
}
