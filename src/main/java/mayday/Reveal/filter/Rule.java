package mayday.Reveal.filter;

import java.util.Stack;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import mayday.Reveal.data.SNP;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.filter.gui.RuleGUI;
import mayday.Reveal.gui.OptionPanelProvider;
import mayday.core.io.StorageNode;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;

@SuppressWarnings("unchecked")
public class Rule implements SNPFilter, ChangeListener, StorageNodeStorable, OptionPanelProvider {

	@SuppressWarnings("rawtypes")
	protected Stack<AbstractDataProcessor> sources = new Stack<AbstractDataProcessor>();
	private boolean defaultValue = false;
	private boolean isInactive = false;
	private boolean invertedMode = false;
	private EventListenerList eventListenerList = new EventListenerList();
	private SNPList snpList;
	
	public Rule(SNPList snpList) {
		this.snpList = snpList;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void stateChanged(ChangeEvent e) {
		// find out which source changed, this might result in dropping the chain at that point
		// find in stack
		AbstractDataProcessor adp = (AbstractDataProcessor)e.getSource();
		int pos = sources.indexOf(adp);
		if (pos==-1) return;
		if (pos+1<sources.size()) {
			AbstractDataProcessor next = sources.get(pos+1);
			if (!next.isAcceptableSource(adp))
				removeProcessor(next);
		}
		// propagate		
		fireChanged();
	}
	
	@SuppressWarnings("rawtypes")
	public void removeProcessor(AbstractDataProcessor adp) {
		// find in stack
		int pos = sources.indexOf(adp);
		if (pos==-1) return;
		// remove
		while (sources.size()>pos)
			removeProcessor();
		if (sources.size()>0)
			sources.peek().linkTarget(null);
		fireChanged();
	}
	
	@SuppressWarnings("rawtypes")
	protected void removeProcessor() {
		AbstractDataProcessor adp;
		adp = sources.pop();
		adp.removeChangeListener(this);
		adp.dispose();
		if (sources.size()>0)
			sources.peek().linkTarget(null);
	}

	@Override
	public Boolean passesFilter(SNP snp) {
		if (isInactive || !isFinishedChain())
			return null;
		
		Boolean b = sources.get(0).processChain(snp); //XOR
		
		if (b!=null)
			return invertedMode^b;
		else
			return defaultValue;
	}
	
	public boolean isFinishedChain() {
		return sources.size()>0 
			  && sources.peek().getDataClass()!=null
			  && sources.peek().getDataClass()[0]==Boolean.class;
	}

	@Override
	public void addChangeListener( ChangeListener listener ) {
		eventListenerList.add( ChangeListener.class, listener );
	}

	@Override
	public void removeChangeListener( ChangeListener listener ) {
		eventListenerList.remove( ChangeListener.class, listener );
	}

	@Override
	public String toDescription() {
		return toString();
	}

	@Override
	public void dispose() {
		while (sources.size()>0)
			removeProcessor();
	}
	
	@SuppressWarnings("rawtypes")
	public String toString() {
		if (!isFinishedChain())
			return "<s>Unfinished rule";
		StringBuilder sb = new StringBuilder();
		for (AbstractDataProcessor adp : sources)
			sb.append(adp.toString());
		return 
			(isInactive?"<s>":"")
			+(invertedMode?"<font color=#FF0000>NOT </font>":"")
			+sb.toString();
	}
	
	@SuppressWarnings("rawtypes")
	public void addProcessor(AbstractDataProcessor nextInChain) {
		if (sources.size()==0) {
			if (nextInChain.isAcceptableInput( new Class[]{SNP.class} )) {
				sources.push(nextInChain);
				nextInChain.addChangeListener(this);
				fireChanged();				
			}
		} else {
			AbstractDataProcessor previous = sources.peek();
			if (nextInChain.isAcceptableSource(previous)) {
				previous.linkTarget(nextInChain);
				sources.push(nextInChain);
				nextInChain.addChangeListener(this);
				fireChanged();
			}
		}
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
	
	public void setActive(boolean act) {
		if (act==isInactive) { // inverted semantics in both variables
			isInactive=!act;
			fireChanged();
		}
	}
	
	public boolean isActive() {
		return !isInactive;
	}

	public boolean defaultsToTrue() {
		return defaultValue;
	}
	
	public void setDefault(boolean t) {
		if (defaultValue!=t) {
			defaultValue=t;
			fireChanged();
		}
	}
	
	public void setInverted(boolean inv) {
		if (inv!=invertedMode) {
			invertedMode = inv;
			fireChanged();
		}
	}
	
	public boolean isInverted() {
		return invertedMode;
	}
	
	public void setSNPList(SNPList snpList) {
		this.snpList = snpList;
	}
	
	public SNPList getSNPList() {
		return this.snpList;
	}
	
	@SuppressWarnings("rawtypes")
	public Stack<AbstractDataProcessor> getProcessors() {
		return sources;
	}
	
	@SuppressWarnings("rawtypes")
	public void fromStorageNode(StorageNode sn) {
		StorageNode sourceNode;
		if ((sourceNode = sn.getChild("Sources"))!=null) {
			int max = Integer.parseInt(sourceNode.Value);
			for (int i=0; i!=max; ++i) {
				StorageNode subNode = sourceNode.getChild(""+i);
				if (subNode.Value!=null) {
					PluginInfo pli = PluginManager.getInstance().getPluginFromID(subNode.Value);
					if (pli!=null) {
						AbstractDataProcessor aSource = (AbstractDataProcessor)pli.newInstance();				
						aSource.setSNPList(getSNPList());
						try {
							if (aSource instanceof StorageNodeStorable)
								((StorageNodeStorable)aSource).fromStorageNode(subNode.getChildren().iterator().next()); // only one child here
						} catch (Exception e) {
							System.err.println(e.getMessage());
							e.printStackTrace();
						}
						addProcessor(aSource);
					}
				}				
			}
		}
		setActive(parseSafely(sn.getChild("isActive"),isActive()));
		setInverted(parseSafely(sn.getChild("isInverted"),isInverted()));
		setDefault(parseSafely(sn.getChild("defaultTrue"),defaultsToTrue()));
	}
	
	@SuppressWarnings("rawtypes")
	public StorageNode toStorageNode() {
		StorageNode sn = new StorageNode();
		sn.Name="Rule";
		sn.addChild("isActive", Boolean.toString(!this.isInactive));
		sn.addChild("isInverted", Boolean.toString(this.invertedMode));
		sn.addChild("defaultTrue", Boolean.toString(this.defaultValue));
		StorageNode sourceNode = new StorageNode("Sources",""+sources.size());
		for (int i=0; i!=sources.size(); ++i) {
			AbstractDataProcessor aSource = sources.get(i);
			StorageNode subNode = new StorageNode(
					""+i,
					PluginManager.getInstance().getPluginFromClass(aSource.getClass()).getIdentifier()
					);
			sourceNode.addChild(subNode);
			if (aSource instanceof StorageNodeStorable)
				subNode.addChild(((StorageNodeStorable)aSource).toStorageNode());
		}
		sn.addChild(sourceNode);
		return sn;
	}
	
	protected boolean parseSafely(StorageNode sn, boolean defaultValue) {
		if (sn!=null) {
			try {
				return Boolean.parseBoolean(sn.Value);
			} catch (Exception t) {
			}
		}
		return defaultValue;
	}

	@Override
	public JPanel getOptionPanel() {
		return new RuleGUI(this);
	}
}
