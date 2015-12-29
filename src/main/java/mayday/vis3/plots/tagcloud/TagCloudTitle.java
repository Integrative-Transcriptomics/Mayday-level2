package mayday.vis3.plots.tagcloud;

import java.util.List;

import mayday.core.ProbeList;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.legend.SimpleTitle;

public class TagCloudTitle extends SimpleTitle {

	private TagCloudViewer viewer;
	
	public TagCloudTitle(TagCloudViewer viewer) {
		super();
		this.viewer = viewer;
	}
	
	public void setup(PlotContainer plotContainer) {
		plotContainer.addViewSetting(titleSetting, viewer); // pretend to be someone else
		if (captionText.getStringValue().length()==0) {
			List<ProbeList> lpl = plotContainer.getViewModel().getProbeLists(false);
			String c = "";
			for (ProbeList pl : lpl)
				c+=pl.getName()+", ";
			if (c.length()>0)
				c = c.substring(0, c.length()-2);
			captionText.setStringValue(c);
		}
	}
}
