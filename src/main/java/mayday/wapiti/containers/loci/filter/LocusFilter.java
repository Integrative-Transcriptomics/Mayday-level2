/*
 * Created on Jan 24, 2005
 *
 */
package mayday.wapiti.containers.loci.filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import mayday.core.gui.PreferencePane;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.ExtendableObjectSelectionSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting.LayoutStyle;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.tasks.AbstractTask;
import mayday.genetics.advanced.LocusData;
import mayday.genetics.advanced.StrandFilterIterator;
import mayday.genetics.advanced.chromosome.AbstractLocusChromosome;
import mayday.genetics.advanced.chromosome.AbstractLocusGeneticCoordinate;
import mayday.genetics.advanced.chromosome.LocusChromosomeObject;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate.DistanceAnchor;
import mayday.genetics.locusmap.LocusMap;
import mayday.genetics.locusmap.LocusMapContainer;
import mayday.genetics.locusmap.LocusMapSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.transformations.matrix.TransMatrix;

/* mostly identical to MapToLoci */
public class LocusFilter
{

	protected StringSetting name;
	protected ExtendableObjectSelectionSetting<LocusData> inputSet;
	protected LocusMapSetting filterSet;
	protected IntSetting maxUpstream, maxDownstream;
	protected RestrictedStringSetting referenceAnchor, candidateAnchor;
	protected BooleanSetting strandSpecific;
	protected HierarchicalSetting setting;

	protected final static String DIST_UPSTREAM="upstream coordinate (e.g. gene start)";
	protected final static String DIST_DOWNSTREAM="downstream coordinate (e.g. gene end)";
	protected final static String DIST_CENTER="middle coordinate (average of start and end)";
	protected final static String DIST_CLOSEST="closest coordinate (gene distance)";

	public Setting getSetting(TransMatrix tm) {
		if (setting == null) {
			name = new StringSetting("New name","Enter a name for the filtered set of loci","",false);
			
			Set<LocusData> av = new HashSet<LocusData>();
			av.addAll(LocusMapContainer.INSTANCE.list());
			
			for (Experiment e : tm.getExperiments())
				if (e.hasLocusInformation())
					av.add(e.getLocusData());		
			
			inputSet = new ExtendableObjectSelectionSetting<LocusData>("Input Locus Data",null,0,new LinkedList<LocusData>(av));
			
			filterSet = new LocusMapSetting();
			filterSet.setName("Filter Set");

			referenceAnchor = new RestrictedStringSetting("Compute distance relative to the reference's",
					"Loci from the selected target locus map are \"reference\" loci.",
					2, new String[]{DIST_UPSTREAM, DIST_DOWNSTREAM, DIST_CENTER, DIST_CLOSEST}
			);
			referenceAnchor.setLayoutStyle(LayoutStyle.RADIOBUTTONS);

			candidateAnchor = new RestrictedStringSetting("Compute distance relative to the candidate's",
					"Loci from the selected MI Group are \"candidate\" loci.",
					2, new String[]{DIST_UPSTREAM, DIST_DOWNSTREAM, DIST_CENTER, DIST_CLOSEST}
			);
			candidateAnchor.setLayoutStyle(LayoutStyle.RADIOBUTTONS);

			HierarchicalSetting maxdist = new HierarchicalSetting("Maximal distances")
			.addSetting(
					maxUpstream = new IntSetting("Maximal distance to upstream reference",null, 1500, 0, null, true, false)
			).addSetting(
					maxDownstream = new IntSetting("Maximal distance to downstream reference",null, 1500, 0, null, true, false)				
			);

			strandSpecific = new BooleanSetting("Strand specific search", null, true);

			setting = new HierarchicalSetting("Filter Loci, keep only those close to a reference set")
			.addSetting(name)
			.addSetting(inputSet)
			.addSetting(filterSet)
			.addSetting(referenceAnchor)
			.addSetting(candidateAnchor)
			.addSetting(maxdist)
			.addSetting(strandSpecific);
		}
		return setting;
	}

	public PreferencePane getPreferencePane() {
		return null;
	}

	protected long distance(AbstractGeneticCoordinate ref, AbstractGeneticCoordinate candidate) {
		if (candidate==null || ref==null)
			return Long.MAX_VALUE;
		boolean fwd = candidate.getStrand().similar(Strand.PLUS);

		DistanceAnchor upstream_coordinate_in_candidate_view = fwd ? DistanceAnchor.FROM : DistanceAnchor.TO; 
		DistanceAnchor downstream_coordinate_in_candidate_view = fwd ? DistanceAnchor.TO : DistanceAnchor.FROM;

		DistanceAnchor ref_anchor = DistanceAnchor.CLOSEST;
		switch (referenceAnchor.getSelectedIndex()) {
		case 0: ref_anchor = upstream_coordinate_in_candidate_view; break;
		case 1:	ref_anchor = downstream_coordinate_in_candidate_view; break;
		case 2:	ref_anchor = DistanceAnchor.CENTER; break;
		}

		DistanceAnchor cand_anchor = DistanceAnchor.CLOSEST;
		switch (candidateAnchor.getSelectedIndex()) {
		case 0: cand_anchor = upstream_coordinate_in_candidate_view; break;
		case 1: cand_anchor = downstream_coordinate_in_candidate_view; break;
		case 2: cand_anchor = DistanceAnchor.CENTER; break;
		}

		long ref_pos = ref.getAnchor(ref_anchor);
		long cand_pos = candidate.getAnchor(cand_anchor);

		// if cand=closest, we compute overlap-distance to ref_pos
		// if ref=closest, we compute overlap-distance to cand_pos
		// if both=closest, we compute overlap distance between loci
		if (ref_anchor == DistanceAnchor.CLOSEST && cand_anchor == DistanceAnchor.CLOSEST) {
			return candidate.getDistanceTo(ref, true);
		} else if (ref_anchor == DistanceAnchor.CLOSEST) {
			return ref.getDistanceTo(cand_pos, ref_anchor);
		} else if (cand_anchor == DistanceAnchor.CLOSEST) {
			long rawDist = candidate.getDistanceTo(ref_pos, cand_anchor);
			// now if ref is on the same strand as cand, we have to take the negative distance, because 
			// the upstream and downstream roles are reversed
			if (candidate.getStrand()==ref.getStrand())
				rawDist *= -1;
			// if, however, ref is on the opposite strand, then ref's notion of "upstream" will be cand's 
			// "downstream" and all is well.
			return rawDist;
		}

		// distance must be negative if cand is upstream of ref, else positive
		long rawDist = (cand_pos - ref_pos);
		// and this is exactly the other way round on the backward strand
		if (!fwd)
			rawDist *= -1;
		return rawDist;
	}

	public void run(TransMatrix tm) {
		getSetting(tm);
		SettingDialog sdl = new SettingDialog(tm.getFrame(), "Filter loci, keep only those close to a reference set", setting);
		sdl.showAsInputDialog();
		if (!sdl.canceled()) {
			final LocusMap target = new LocusMap(name.getStringValue());
			AbstractTask at = new AbstractTask("Filtering loci") {

				@SuppressWarnings("unchecked")
				protected void doWork() throws Exception {
					ChromosomeSetContainer tcsc = filterSet.getLocusMap().asChromosomeSetContainer();

					ChromosomeSetContainer scsc = inputSet.getObjectValue().asChromosomeSetContainer();

					for (Chromosome sc : scsc.getAllChromosomes()) {
						LocusChromosomeObject<String> tc = 
							(LocusChromosomeObject<String>)tcsc.getChromosome(sc);

						for (Strand s : new Strand[]{Strand.PLUS, Strand.MINUS}) {

							Iterator<LocusGeneticCoordinateObject<String>> titer = tc.iterateByStartPosition(true);
							Iterator<AbstractLocusGeneticCoordinate> siter = 
								((AbstractLocusChromosome)sc).iterateByStartPosition(true);

							siter = new StrandFilterIterator<AbstractLocusGeneticCoordinate>(siter, s);

							if (strandSpecific.getBooleanValue()) {
								titer = new StrandFilterIterator<LocusGeneticCoordinateObject<String>>(titer, s);
							}

							map(target, siter, titer);

						} // strand

					} // chrome

					LocusMapContainer.INSTANCE.add(target);

				}

				protected void initialize() {
				}

			};
			at.start();
		}
	}

	@SuppressWarnings("unchecked")
	protected void map(
			LocusMap target,
			Iterator<AbstractLocusGeneticCoordinate> siter,
			Iterator<LocusGeneticCoordinateObject<String>> titer
	) {

		// I always need to keep three reference loci in memory. if the current candidate
		// is closer to the third than to the second reference locus, drop the first, shift the others and get a new third

		LocusGeneticCoordinateObject<String> left = null;
		LocusGeneticCoordinateObject<String> middle = null;
		LocusGeneticCoordinateObject<String> right = null;

		int maxUp = maxUpstream.getIntValue();
		int maxDown = maxDownstream.getIntValue();
		boolean strand = strandSpecific.getBooleanValue();
		
//		long addCount=0;
//		long dropCount=0;
//		
//		long debug=0;

		while(siter.hasNext()) {
			AbstractLocusGeneticCoordinate candidate = siter.next();			
			// contract: RIGHT is always downstream of candidate
			if (right!=null && candidate.isDownstreamOf(right)) {
				right = null;
			}

			// fill the neighbors on the reference
			while (right==null && titer.hasNext()) {
				LocusGeneticCoordinateObject<String> tmp = titer.next();
//				System.out.println(debug+"\t"+tmp.getFrom()+"  "+tmp.length());
//				debug++;
				if (strand && !(tmp.getStrand().similar(candidate.getStrand())))
					tmp = null;
				if (tmp!=null && candidate.isDownstreamOf(tmp))
					tmp = null;
				right = tmp;
				if (left==null && right!=null) { // first filling situation
					left=middle;
					middle = right;
					right = null;
				}
			}

			// compute distances to all neighbors, depending on distance settings, treat NULL correctly. All distances are ABSOLUTE
			long dleft = Math.abs(distance( left, candidate));
			long dmiddle = Math.abs(distance( middle, candidate ));
			long dright = Math.abs(distance( right, candidate ));

			LocusGeneticCoordinateObject<String> partner;
			long dpartner;

			// select partner by overlap if two distance are equally small
			long minDist = Math.min( dleft, Math.min (dright, dmiddle));
			boolean chooseLeft = dleft==minDist;
			boolean chooseMiddle = dmiddle==minDist;
			boolean chooseRight = dright==minDist;
			if ((chooseLeft && (chooseMiddle || chooseRight)) || (chooseMiddle && chooseRight))  {
				// select partner by overlap if two distance are equally small
				long overLeft = safeOverlap(candidate, left);
				long overMiddle = safeOverlap(candidate, middle);
				long overRight = safeOverlap(candidate, right);
				long maxOver = Math.max(overLeft, Math.max(overRight, overMiddle));
				chooseLeft = overLeft==maxOver;
				chooseMiddle = overMiddle==maxOver;
				chooseRight = overRight==maxOver;
				// if still not resolved, we always pick "middle", then "left", then "right"
			}

			if (chooseMiddle) {
				dpartner = dmiddle;
				partner = middle;
			} else if (chooseLeft) {
				dpartner = dleft;
				partner = left;
			} else {
				dpartner = dright;
				partner = right;
			}

			//			if (left!=null) System.out.print(left.getFrom());
			//			if (middle!=null) System.out.print("\t"+middle.getFrom());
			//			if (right!=null) System.out.print("\t"+right.getFrom());
			//			System.out.println("\t\t"+candidate.getFrom());

			boolean isOK = true;

			if (partner==null)
				return;

			// check minima, maxima correctly for upstream/downstream. Must use candidate's coordinate system as reference!
			if (candidate.isDownstreamOf(partner)) { 
				if (dpartner>maxUp)		
					isOK = false;
			} else {
				if (dpartner>maxDown) {
					isOK = false;
				}
			}

			if (isOK) {
				// get a name
				String name;
				if (candidate instanceof LocusGeneticCoordinateObject)
					 name = ((LocusGeneticCoordinateObject)candidate).getObject().toString();
				else
					name = candidate.toString();
				// compute final distance values, these can now also be negative
				target.put(name, candidate);
//				++addCount;
			} else {
//				System.out.println("Drop "+candidate.toString()+"\t"+left+"\t"+middle+"\t"+right);
//				++dropCount;
			}

			// if right is closer than middle, we can discard left
			if (chooseRight) {
				left = middle;
				middle = right;
				right = null;
			}

		}
		
//		System.out.println(addCount+" "+dropCount+" "+target.size());

	}

	protected long safeOverlap(AbstractGeneticCoordinate candidate, AbstractGeneticCoordinate partnerOrNull) {
		if (partnerOrNull==null)
			return -1;
		return partnerOrNull.getOverlappingBaseCount(candidate.getFrom(), candidate.getTo()); //MUST NOT be strand specific!
	}

}
