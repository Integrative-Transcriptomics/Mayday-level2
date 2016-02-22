package mayday.vis3.plots.volcano;

import java.util.Collection;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.NumericMIO;
import mayday.core.settings.Setting;
import mayday.vis3.model.ManipulationMethod;
import mayday.vis3.model.ManipulationMethodSingleValue;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.manipulators.None;

public class VolcanoValueProvider {

	public interface Provider {
		public double getValue(Probe pb);
		public String getName();
		public MIGroup getMIGroup();
		public ManipulationMethod getManipulator();
	}
	
	public class MIOProvider implements Provider {
		protected MIGroup mg=null;
		protected ManipulationMethodSingleValue manip;
		@SuppressWarnings("unchecked")
		public double getValue(Probe pb) {
			double value;
			if (mg==null)
				value = Double.NaN;
			else {
				NumericMIO<Number> nm = (NumericMIO<Number>)mg.getMIO(pb);
				if (nm==null) 
					value = Double.NaN;
				else {
					value = ((Number)nm.getValue()).doubleValue();
					//check for infinity and replace with NaN for visualization
					if(Double.isInfinite(value)) {
						value = Double.NaN;
					}
				}
			}
//			return manip.manipulate(value); 
			return value;
		
		}
		
		public MIOProvider(MIGroup m, ManipulationMethodSingleValue manip) {
			mg=m;
			this.manip=manip;
		}
		public String getName() {
			return "";
		}				
		public MIGroup getMIGroup() {
			return mg;
		}
		
		public ManipulationMethod getManipulator() {
			return (ManipulationMethod) manip;
		}
	}
	private ViewModel viewModel;
	private String title;
	private Provider provider; 
	protected VolcanoValueProviderSetting setting;
//	private MaydayFrame expListFrame;
	
	private EventListenerList eventListenerList = new EventListenerList();
	
	public void addChangeListener(ChangeListener cl) {
		eventListenerList.add(ChangeListener.class, cl);		
	}
	
	public void removeChangeListener(ChangeListener cl) {
		eventListenerList.remove(ChangeListener.class, cl);
	}

	public double getValue(Probe pb) {
		return provider.getValue(pb);
	}
	
	//to implement
	public double getPValue(Probe pb) {
		// TODO Auto-generated method stub
		return 0;
	}
	//to implement
	public double getFCValue(Probe pb) {
		if(pb!=null){
			
		}
		return 0;
	}

	
	public String getSourceName() {
		return provider.getName();
	}
	
	public double getMaximum() {
		double max=Double.NaN;
		for (Probe pb : viewModel.getProbes()) {
			double d = getValue(pb);
			if (!Double.isNaN(d) && (d>max || Double.isNaN(max)))
				max=d;
		}
		return max;	
	}
	
	public double getMinimum() {
		double min=Double.NaN;
		for (Probe pb : viewModel.getProbes()) {
			double d = getValue(pb);
			if (!Double.isNaN(d) && (d<min || Double.isNaN(min)))
				min=d;
		}
		return min;	
	}
	
//	public Provider getProvider() {
//		return null;
//		return provider;
//	}
	
	public ViewModel getViewModel() {
		return viewModel;
	}
	
	public Collection<Double> getValues() {
		LinkedList<Double> values = new LinkedList<Double>();
		for (Probe pb : viewModel.getProbes())
			values.add(getValue(pb));
		return values;
	}
	
	protected void fireChanged() {
		Object[] l_listeners = this.eventListenerList.getListenerList();

		if (l_listeners.length==0)
			return;
		
		ChangeEvent event = new ChangeEvent(this);

		// process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = l_listeners.length-2; i >= 0; i-=2 )  {
			if ( l_listeners[i] == ChangeListener.class )  {
				ChangeListener list = ((ChangeListener)l_listeners[i+1]);
				list.stateChanged(event);
			}
		}
	}
	
	public VolcanoValueProvider(ViewModel vm, String menuTitle) {
		viewModel = vm;
		title=menuTitle;
		setting = new VolcanoValueProviderSetting(title, null, this, vm);
		
	}
	
	public VolcanoValueProvider(ViewModel vm, String menuTitle, MIGroup mig) {
		viewModel = vm;
		title=menuTitle;
		setting = new VolcanoValueProviderSetting(title, null, this, vm);
		provider = new MIOProvider(mig, new None());
		
	}
	
	public String getMenuTitle() {
		return title;
	}
	
	public Setting getSetting() {
		return setting;
	}
	
	protected ListSelectionModel selectionModel;	// needed for JList's to sync selected entries
	@SuppressWarnings("rawtypes")
	protected DefaultListModel dataModel = new DefaultListModel();	// data model for the JList's
	
	public void setProvider(Provider p) {
		provider=(MIOProvider) p;
		fireChanged();
	}

	public void setMenuTitle(String valueString) {
		title=valueString;
	}

	//Return SourceType = MIO_VALUE (1)
	public int getSourceType() {
		return 1;
	}

	public Provider getProvider() {
		return provider;
	}

}