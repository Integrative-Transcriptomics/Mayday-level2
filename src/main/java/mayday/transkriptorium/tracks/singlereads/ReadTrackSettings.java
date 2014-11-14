package mayday.transkriptorium.tracks.singlereads;

import mayday.core.Experiment;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.typed.ExperimentSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.genetics.basic.Strand;
import mayday.transkriptorium.data.MappingStore;
import mayday.transkriptorium.meta.MappingStoreMIO;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class ReadTrackSettings extends AbstractTrackSettings{
	
	protected MIGroupSetting msm;
	
	protected ExperimentSetting experiment;

	protected RestrictedStringSetting coloring;
	
	public final static String COL_BLACK = "black for unique, red for multireads";
	public final static String COL_POSITION = "by chromosome position";
	public final static String COL_QUALITY = "by mapping quality";
	public final static String COL_PAIRED = "black for paired reads, red for unpaired";
	
	protected ColorGradientSetting gradient;
	protected ColorGradient grad;
	
	protected IntSetting readHeight;
	
	protected RestrictedStringSetting showWhich;
	public final static String SHOW_ALL = "Show all reads";
	public final static String SHOW_UNIQUE = "Show only uniquely mapping reads";
	public final static String SHOW_MULTI = "Show only multi-reads";
	
	public ReadTrackSettings(GenomeOverviewModel Model, AbstractTrackPlugin Tp) {
		super(Model, Tp);		

		experiment = new ExperimentSetting("experiment",null,Model.getDataSet().getMasterTable());
		coloring = new RestrictedStringSetting("Read coloring",null,0,new String[]{ COL_BLACK, COL_POSITION, COL_QUALITY, COL_PAIRED });
		grad = ColorGradient.createRainbowGradient(0, 10);
		grad.setResolution(65536);

		gradient = new ColorGradientSetting("Color gradient",null,grad);
				//"Set the color of a read according to its FIRST mapping position.\nThis can be useful to visualize multireads");
		
		readHeight = new IntSetting("Height of a read", null, 4, 1, 10, true, true);

		showWhich = new RestrictedStringSetting("Display options",null,0,new String[]{SHOW_ALL, SHOW_UNIQUE, SHOW_MULTI});
		
		// replace the root
		root.removeChangeListener(tp);
		
		root = new HierarchicalSetting("Read Track")
			.addSetting(new HierarchicalSetting("General")
				.addSetting(root) // old root
				.addSetting(msm = new MIGroupSetting("Mapped Reads",null,null,Model.getDataSet().getMIManager(), false).setAcceptableClass(MappingStoreMIO.class))
				.addSetting(readHeight.setLayoutStyle(IntSetting.LayoutStyle.SLIDER))
				.addSetting(showWhich)
				.addSetting(experiment)
			)
			.addSetting(new HierarchicalSetting("Coloring")
				.addSetting(coloring.setLayoutStyle(ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS))
				.addSetting(gradient.setLayoutStyle(ColorGradientSetting.LayoutStyle.FULL))
			)
		.setLayoutStyle(LayoutStyle.TABBED);
		
		root.addChangeListener(tp);
	}

	public MappingStore getMSM(Experiment e) {
		return ( (MappingStoreMIO) msm.getMIGroup().getMIO(e) ).getValue();
	}
	
	public MappingStore getMSM(int e) {
		return getMSM(model.getDataSet().getMasterTable().getExperiment(e));
	}

	protected void getInternalIdentifierLabel(){
		identString = "Mapped Reads "+experiment.getStringValue();
	}
	
	public Strand getStrand() {
		return Strand.BOTH;
	}

	@Override
	public void setInitialExperiment(int experiment) {
		this.experiment.setSelectedIndex(experiment);
	}
	
	public int getExperiment() {
		return experiment.getSelectedIndex();
	}
	
	public String getColoringMode() {
		return coloring.getObjectValue();
	}
	
	public ColorGradient getGradient() {
		return gradient.getColorGradient();
	}
	
	public int getReadHeight() {
		return readHeight.getIntValue();
	}
	
	public void setGradientRange(double min, double max) {
		if (max==min)
			return;
		grad.setMax(max);
		grad.setMin(min);
		gradient.setColorGradient(grad);
	}
	
	public String getDisplayOption() {
		return showWhich.getStringValue();
	}
}
