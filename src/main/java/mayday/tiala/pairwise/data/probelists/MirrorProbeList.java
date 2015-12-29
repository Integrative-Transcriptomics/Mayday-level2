package mayday.tiala.pairwise.data.probelists;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.tiala.pairwise.data.ProbeMapper;

public class MirrorProbeList extends ProbeList implements ProbeListListener {

	private boolean ignoreChanges = false;
	protected ProbeList sourcePL;
	protected MasterTable sourceMT;
	
	public MirrorProbeList(DataSet targetDS, ProbeList sourcePL) {
		super(targetDS, true);
		this.sourcePL = sourcePL;
		init();
	}
	
	protected void init() {
		sourceMT = sourcePL.getDataSet().getMasterTable();
        setName(sourcePL.getName());
        setColor(sourcePL.getColor());
		getAnnotation().setInfo("Mirrored Probe List");		
		sourcePL.addProbeListListener(this);
		populate();
	}
	
	public ProbeList getSourceProbeList() {
		return sourcePL;
	}
	
    public void clearProbes_internal()
    {
        Object[] l_probes = toArray();
        
        if ( isSticky() )  {
            for ( int i = 0; i < l_probes.length; ++i ) {
                super.removeProbe( (Probe)l_probes[i] ); 
            }
        }        
        probes.clear();        
        if ( isSticky() ) {
            fireProbeListChanged( ProbeListEvent.CONTENT_CHANGE );
        }
    }
    
	protected void populate() {		
		boolean wasSilent = isSilent();
		setSilent(true);
		clearProbes_internal();
		MasterTable myMT = getDataSet().getMasterTable();
		for (Probe pb : sourcePL.getAllProbes()) {
			for (Probe mpb : ProbeMapper.map(sourceMT, pb, myMT))
				if (mpb!=null)
					super.addProbe(mpb);
		}			
		setSilent(wasSilent);
		fireProbeListChanged(ProbeListEvent.CONTENT_CHANGE);
	} 
	
	public boolean isIgnoreChanges() {
		return ignoreChanges;
	}

	public void setIgnoreChanges(boolean ignoreChanges) {
		if (this.ignoreChanges && !ignoreChanges)
			populate();
		this.ignoreChanges = ignoreChanges;
	}

	public void probeListChanged(ProbeListEvent event) {
		switch (event.getChange()) {
		case ProbeListEvent.CONTENT_CHANGE:
			if (!ignoreChanges)
				populate();
			break;
		case ProbeListEvent.LAYOUT_CHANGE:
			this.setColor(sourcePL.getColor());
			this.setName(sourcePL.getName());
		}
	}

    public void propagateClosing() {
    	super.propagateClosing();
    	sourcePL.removeProbeListListener(this);
    }


}
