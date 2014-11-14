package mayday.motifsearch.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import mayday.core.Probe;

public class MotifSearchModel {

	private ConcurrentHashMap<String, Sequence> sequencesHashMap;
	private ConcurrentHashMap<String, Motif> motifsHashMap;
	private Sequences sequences;
	
	public MotifSearchModel(ConcurrentHashMap<String, Sequence> sequencesHashMap,
			ConcurrentHashMap<String, Motif> motifsHashMap) {
		super();
		this.sequencesHashMap = sequencesHashMap;
		this.motifsHashMap = motifsHashMap;
		
		this.sequences = new Sequences();
		ArrayList<Sequence> tempSequences = new ArrayList<Sequence>(this.sequencesHashMap.values());
		/* add the Sequences to this sequences and sort the Sites after their
		 *  significance so that they do not overlap or vanish behind other motifs after being drawn on the Sequence color model*/
		for (Sequence sequence : tempSequences) {
//			SiteComparator c = new SiteComparator(
//				SiteComparator.SORT_BY_SIGNIFICANCE_VALUE);
//			Collections.sort(sequence.getSites(), c);

		    this.sequences.add(sequence);
		}
		
		/*give each Motif an own color*/
		MotifColorer.colorizeMotifs(this.getMotifs());
	}

	public ConcurrentHashMap<String, Sequence> getHashMapedSequences() {
		return sequencesHashMap;
	}

	public ConcurrentHashMap<String, Motif> getHashMapedMotifs() {
		return motifsHashMap;
	}
	
	public ArrayList<Motif> getMotifs() {
		return new ArrayList<Motif>(this.motifsHashMap.values());
	}

	public Sequences getSequences() {
		return sequences;
	}
}
