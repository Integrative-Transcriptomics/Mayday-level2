package mayday.transkriptorium.tracks.singlereads;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class ReadTrackPlugin extends AbstractTrackPlugin{

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo((Class) this.getClass(),
				"PAS.transkriptorium.mappedreadstrack", 
				new String[0], 
				MC2,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Rendering mapped reads.",
				"Mapped reads");
		return pli;
	}

	public ReadTrackPlugin() {}

	protected AbstractTrackSettings makeSetting() {
		return new ReadTrackSettings(model, this);
	}
	
	protected AbstractTrackRenderer makeRenderer() {
		return new ReadTrackRenderer(model, this);
	}
	
	protected int getDefaultHeight() {
		return 150;
	}
	
}
