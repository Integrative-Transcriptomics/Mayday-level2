package mayday.vis3.plots.volcano;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.meta.MIGroup;
import mayday.core.meta.NumericMIO;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.methods.ManipulationMethodSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.vis3.model.ManipulationMethodSingleValue;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.manipulators.None;


public class VolcanoValueProviderSetting extends HierarchicalSetting implements ChangeListener {
	
	public static final int MIO_VALUE=1;
	protected MIGroupSetting mioGroupPath;
	protected ManipulationMethodSetting mioManipulator;
	protected SelectableHierarchicalSetting source;
	
	protected ViewModel viewModel;
	protected mayday.vis3.plots.volcano.VolcanoValueProvider target;
	protected StringSetting menuName;
	
	public VolcanoValueProviderSetting(String Name, String Description,
									   mayday.vis3.plots.volcano.VolcanoValueProvider volcanoValueProvider, ViewModel vm) {
		super(Name);
	
		viewModel = vm;
		target=volcanoValueProvider;
		
		if (vm!=null) {
			addSetting(source = new SelectableHierarchicalSetting("Source",null,0,new Object[]{					
					mioGroupPath = new MIGroupSetting("meta information",null, null, vm.getDataSet().getMIManager(), true)
								.setAcceptableClass(NumericMIO.class)					
			})).
			addSetting(mioManipulator = new ManipulationMethodSetting("meta information manipulator", null, new None(), true));
		}
		setChildrenAsSubmenus(true);
		volcanoValueProvider.addChangeListener(this);
		addChangeListener(this);
	}
	
	protected void initialSettings() {
		addSetting(menuName = new StringSetting("Name","Enter a name for this data source, e.g. for labelling", 
				target.getMenuTitle()));
	} 
	@Override
	public void stateChanged(SettingChangeEvent e) {
		if (e.getSource()==menuName)
				target.setMenuTitle(menuName.getValueString());
			if ((getMode()==MIO_VALUE && target.getSourceType()!=MIO_VALUE) 
					|| e.getSource()==mioGroupPath || e.hasSource(mioManipulator)) {
				MIGroup mg = getMIGroup();
				ManipulationMethodSingleValue manip = getManipulation();
				if (mg!=null) {
					target.setProvider(target.new MIOProvider(mg, manip));
				}
			}
		fireChanged(e);	
	}
	
	public void stateChanged(ChangeEvent e) {
		if (viewModel==null)
			return;
		if (e.getSource()==target) {
//			source.setObjectValue(mioGroupPath);
//			mioGroupPath.setMIGroup(target.getProvider().getMIGroup());
//				mioManipulator.setInstance(new None());
//				mioManipulator.setInstance(target.getProvider().getManipulator());
		}
	}	
	
	public VolcanoValueProviderSetting clone(){
		VolcanoValueProviderSetting cloned = new VolcanoValueProviderSetting(name, description, target, viewModel);
		return cloned;
	}
	
	
	protected int getMode() {
		return MIO_VALUE;
	}
	
	
	protected MIGroup getMIGroup() {
		return mioGroupPath.getMIGroup();
	}
	
	public ManipulationMethodSingleValue getManipulation() {
		return (ManipulationMethodSingleValue)mioManipulator.getInstance();
	}
	
	
}

