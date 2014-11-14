package mayday.transkriptorium.tracks.curve;

import mayday.core.Experiment;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ExperimentSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.genetics.basic.Strand;
import mayday.transkriptorium.data.MappingStore;
import mayday.transkriptorium.meta.MappingStoreMIO;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class CurveTrackSettings extends AbstractTrackSettings{
	
	protected MIGroupSetting msm;
	
	protected ExperimentSetting experiment;
	protected BooleanSetting useLog;
	protected BooleanSetting useMean;

	
	public CurveTrackSettings(GenomeOverviewModel Model, AbstractTrackPlugin Tp) {
		super(Model, Tp);		

		experiment = new ExperimentSetting("experiment",null,Model.getDataSet().getMasterTable());
		useLog = new BooleanSetting("Use logarithmic scale",null,true);
		useMean = new BooleanSetting("Summarize using mean", 
				"Normally, coverage is added up for each pixel.\nThe alternative is to summarize using the mean for all covered bases", false);
		
		root
		.addSetting(new HierarchicalSetting("Read Curve Track")
			.addSetting(msm = new MIGroupSetting("Mapped Reads",null,null,Model.getDataSet().getMIManager(), false).setAcceptableClass(MappingStoreMIO.class))
			.addSetting(experiment)
			.addSetting(useLog)
			.addSetting(useMean)
			.setCombineNonhierarchicalChildren(true)
		).setLayoutStyle(LayoutStyle.PANEL_VERTICAL);
		
	}

	public MappingStore getMSM(Experiment e) {
		return ( (MappingStoreMIO) msm.getMIGroup().getMIO(e) ).getValue();
	}
	
	public MappingStore getMSM(int e) {
		return getMSM(model.getDataSet().getMasterTable().getExperiment(e));
	}

	protected void getInternalIdentifierLabel(){
		identString = "Mapping Coverage "+experiment.getStringValue();
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
	
	public boolean useLog() {
		return useLog.getBooleanValue();
	}
	
	public boolean useMean() {
		return useMean.getBooleanValue();
	}
	
}
