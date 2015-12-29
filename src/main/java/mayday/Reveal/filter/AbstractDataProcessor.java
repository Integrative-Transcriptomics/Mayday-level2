package mayday.Reveal.filter;

import java.util.Collection;
import java.util.HashMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import mayday.Reveal.RevealPlugin;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public abstract class AbstractDataProcessor<InType, OutType> extends RevealPlugin implements SNPFilter {
	
	public static final String CATEGORY = "SNPList/Data Processor";
	public static final String MC = Constants.MC_REVEAL + "/" + CATEGORY;
	
	protected SNVList snpList;
	protected AbstractDataProcessor<OutType, ?> nextInChain;
	private EventListenerList eventListenerList = new EventListenerList();
	
	public void linkTarget(AbstractDataProcessor<OutType,?> adp) {
		nextInChain = adp;
	}
	
	@SuppressWarnings("unchecked")
	public Boolean passesFilter(SNV snp) {
		return processChain((InType)snp); // should only be called if intype is probe
	}
	
	@SuppressWarnings("rawtypes")
	public final boolean isAcceptableSource(AbstractDataProcessor adp) {
		if (adp.getDataClass()==null) return false;
		return isAcceptableInput(adp.getDataClass());
	}
	
	public abstract Class<?>[] getDataClass();
	
	public abstract boolean isAcceptableInput(Class<?>[] inputClass);
	
	public void init() {}
	
	public abstract String toString();
	
	protected abstract OutType convert(InType value);
	
	public Boolean processChain(InType value) {
		OutType converted = convert(value);
		
		if (converted==null)
			return null;
		
		if (nextInChain==null)
			if (converted instanceof Boolean)
				return (Boolean)converted;
			else
				return null;
		else
			return nextInChain.processChain(converted);
	}
	
	public void addChangeListener( ChangeListener listener ) {
		eventListenerList.add( ChangeListener.class, listener );
	}


	public void removeChangeListener( ChangeListener listener ) {
		eventListenerList.remove( ChangeListener.class, listener );
	}
	
	public void fireChanged() {
		Object[] l_listeners = this.eventListenerList.getListenerList();

		if (l_listeners.length==0)
			return;

		ChangeEvent ce = new ChangeEvent(this);

		// process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = l_listeners.length-2; i >= 0; i-=2 )  {
			if ( l_listeners[i] == ChangeListener.class )  {
				ChangeListener list = ((ChangeListener)l_listeners[i+1]);
				list.stateChanged(ce);
			}
		}
	}
	
	public void setSNPList(SNVList snpList) {
		this.snpList = snpList;
	}
	
	public SNVList getSNPList() {
		return this.snpList;
	}
	
	public String toDescription() {
		return toString();
	}
	
	@Override
	public void run(Collection<SNVList> snpLists) {
		return; //nothing to do
	}
	
	@Override
	public String getCategory() {
		return CATEGORY;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				getClass(),
				getCompleteType(),
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"G&uuml;nter J&auml;ger",
				"jaeger@informatik.uni-tuebingen.de",
				getDescription(),
				getName()
		);
		if(getMenuName() != null)
			pli.setMenuName(getMenuName());
		return pli;
	}
	
	@Override
	public String getMenuName() {
		return null;
	}

	@Override
	public String getMenu() {
		return null; //no menu for filters
	}
}
