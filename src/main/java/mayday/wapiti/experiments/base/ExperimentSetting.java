package mayday.wapiti.experiments.base;

import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.JLabel;

import mayday.core.settings.Setting;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.StringSetting;

public class ExperimentSetting extends HierarchicalSetting {
	
	protected StringSetting name;
	protected Experiment e;

	@SuppressWarnings("serial")
	public ExperimentSetting(Experiment e, String exname) {
		this(e);
		addSetting( name = new StringSetting("Name","Specify how the experiment will be labelled after importing",exname) );
		addSetting( new ComponentPlaceHolderSetting("SourceName", new ExUpdateLabel(e) {
			protected String extractInfo() {
				return ex.getSourceDescription();
			}
		}) );
		addSetting( new ComponentPlaceHolderSetting("RawData", new ExUpdateLabel(e) {
			protected String extractInfo() {
				return "Initial state: "+ex.getInitialState().getNumberOfFeatures()+" features"
						+(ex.getInitialState().hasLocusInformation()?", "+ex.getInitialState().getNumberOfLoci()+" loci":"");
			}
		}) );
		addSetting( new ComponentPlaceHolderSetting("FeatureCount", new ExUpdateLabel(e) {
			protected String extractInfo() {
				return "Current state: "+ex.getNumberOfFeatures()+" features"
						+(ex.hasLocusInformation()?", "+ex.getNumberOfLoci()+" loci":"");
			}
		}) );
		setCombineNonhierarchicalChildren(false);
		setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL);
	}
	
	@SuppressWarnings("serial")
	protected abstract class ExUpdateLabel extends JLabel {
		protected Experiment ex;
		public ExUpdateLabel(Experiment e) {
			ex = e;
		}
		public Dimension getPreferredSize() {
			update();
			return super.getPreferredSize();
		}
		public void update() {
			setText(extractInfo());
		}
		protected abstract String extractInfo();
	}
	
	public String getExperimentName() {
		return name.getStringValue();
	}
	
	public void setExperimentName(String newName) {
		name.setStringValue(newName);
	}
	
	private ExperimentSetting(Experiment e) {
		super(e.getClass().getSimpleName()+" properties");
		this.e = e;
	}
	
	public ExperimentSetting clone() {
		ExperimentSetting gs = new ExperimentSetting(e);
		gs.fromPrefNode(this.toPrefNode());
		return gs;
	}
	
	public void reduce() {
		Iterator<Setting> ci = children.iterator();
		while (ci.hasNext()) {
			Setting s = ci.next();
			if ((s instanceof ComponentPlaceHolderSetting) || s.getName().equals("Name"))
				ci.remove();
		}
		Setting childSetting = new ComponentPlaceHolderSetting("~~~", new JLabel("<Combined settings for several experiments>"));
		children.add(0,childSetting);
		childrenMap.put(childSetting.getName(), childSetting);
	}
	
	public boolean nothingLeft() {
		return children.size()==1;
	}

}
