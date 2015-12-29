package mayday.tiala.pairwise.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.tiala.pairwise.data.mastertables.DerivedMasterTable;
import mayday.tiala.pairwise.data.probes.DerivedProbe;

public class ProbeMapper {
	
    public static List<Probe> map(MasterTable sourceMT, Probe sourcePb, MasterTable targetMT) {
    	String name;
    	
    	if (sourcePb instanceof DerivedProbe)
    		name = ((DerivedProbe)sourcePb).getSourceName();
    	else
    		name = sourcePb.getName();
    	
    	if (targetMT instanceof DerivedMasterTable) {
    		return ((DerivedMasterTable)targetMT).getProbes(name);
    	} else {
    		Probe pb = targetMT.getProbe(name);
    		if (pb==null) {
    			return Collections.emptyList();
    		} else {
    			LinkedList<Probe> llP = new LinkedList<Probe>();
    			llP.add(pb);
    			return llP;
    		}
    	}
    }
    
}
