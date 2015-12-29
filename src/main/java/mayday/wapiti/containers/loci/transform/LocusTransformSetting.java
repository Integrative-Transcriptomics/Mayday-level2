package mayday.wapiti.containers.loci.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.StringMapSetting;
import mayday.genetics.advanced.LocusTransformer;
import mayday.genetics.advanced.chromosome.AbstractLocusChromosome;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.basic.coordinate.GeneticCoordinate;
import mayday.genetics.coordinatemodel.GBAtom;
import mayday.genetics.coordinatemodel.GBNode;

public class LocusTransformSetting extends HierarchicalSetting {	

	BooleanHierarchicalSetting changeSpecies;
	BooleanHierarchicalSetting changeChromosome;
	BooleanHierarchicalSetting changeStrands;
	BooleanHierarchicalSetting changeMove;
	BooleanHierarchicalSetting changeFrom;
	BooleanHierarchicalSetting changeTo;
	BooleanHierarchicalSetting changeLength;
	BooleanSetting changePrimitive;
	BooleanSetting splitPrimitive;

	StringMapSetting speciesMap;

	StringMapSetting chromeMap;

	ObjectSelectionSetting<Strand> strandPosTarget;
	ObjectSelectionSetting<Strand> strandNegTarget;
	ObjectSelectionSetting<Strand> strandUndefTarget;
	ObjectSelectionSetting<Strand> strandBothTarget;

	IntSetting moveDelta;
	IntSetting upstreamDelta;
	IntSetting downstreamDelta;

	IntSetting fixedLength;
	ObjectSelectionSetting<String> keepWhich;		

	public LocusTransformSetting(String Name) {
		super(Name);
		Map<String,String> smapdefault = new TreeMap<String,String>();
		Map<String,String> cmapdefault = new TreeMap<String,String>();

		addSetting( 
				changeSpecies = new BooleanHierarchicalSetting("Change Species names",
						"Replace the species names in the first column with those in the second.",false)
				.addSetting( speciesMap = new StringMapSetting("Species Mapping",null,smapdefault))		
				.setTopMost(true)
		);

		addSetting(
				changeChromosome = new BooleanHierarchicalSetting("Change Chromosome names",
						"Replace the chromosome names in the first column with those in the second.",false)
				.addSetting( chromeMap = new StringMapSetting("Chromosome Mapping",null,cmapdefault)) 
				.setTopMost(true)
		);

		addSetting(
				changeStrands = new BooleanHierarchicalSetting("Change strand location",
						"Move coordinates from one strand to the other or remove strand information altogether.",false)
				.addSetting( strandPosTarget = makeStrandMapping(0) )
				.addSetting( strandNegTarget = makeStrandMapping(1) )
				.addSetting( strandBothTarget = makeStrandMapping(2) )
				.addSetting( strandUndefTarget = makeStrandMapping(3) )
				.setTopMost(true)
		);

		addSetting(
				changeMove = new BooleanHierarchicalSetting("Move coordinates",
						"Move the whole coordinate and all of its components in a specific direction.\n" +
						"\'Unspecified\' or \'both\' strands will be treated like \'plus\' strands.\n\n"+
						"NOTE: If a coordinate contains elements (e.g. exons) on different strands, these will be moved\n" +
						"in _different_ directions, which is probably not what you want. In this case, you might\n" +
						"want to also select to convert complex coordinates to primitive coordinates."
						,false)
				.addSetting( moveDelta = new IntSetting("Delta",null,0)) 
				.setTopMost(true)
		);

		addSetting(
				changeFrom = new BooleanHierarchicalSetting("Change upstream coordinate",
						"Move the \'upstream\' coordinate of each locus. \n " +
						"\'Unspecified\' or \'both\' strands will be treated like \'plus\' strands.\n\n"+
						"NOTE: If a coordinate contains elements (e.g. exons) these will be changed _individually_\n" +
						"which is probably not what you want. In this case, you might want to also select\n" +
						"to convert complex coordinates to primitive coordinates."
						,false)
				.addSetting( upstreamDelta = new IntSetting("Delta",null,0)) 
				.setTopMost(true)
		);

		addSetting(
				changeTo = new BooleanHierarchicalSetting("Change downstream coordinate",
						"Move the \'downstream\' coordinate of each locus. \n " +
						"\'Unspecified\' or \'both\' strands will be treated like \'plus\' strands.\n\n"+
						"NOTE: If a coordinate contains elements (e.g. exons) these will be changed _individually_\n" +
						"which is probably not what you want. In this case, you might want to also select\n" +
						"to convert complex coordinates to primitive coordinates."
						,false)
				.addSetting( downstreamDelta = new IntSetting("Delta",null,0)) 
				.setTopMost(true)
		);

		addSetting(
				changeLength = new BooleanHierarchicalSetting("Change locus length",
						"Change the length of the locus by moving either the \'upstream\' or the \'downstream\' coordinate. \n " +
						"\'Unspecified\' or \'both\' strands will be treated like \'plus\' strands.\n\n"+
						"NOTE: If a coordinate contains elements (e.g. exons) these will be changed _individually_\n" +
						"which is probably not what you want. In this case, you might want to also select\n" +
						"to convert complex coordinates to primitive coordinates."
						,false)
				.addSetting( fixedLength = new IntSetting("New length",null,25))
				.addSetting( 
						keepWhich = new ObjectSelectionSetting<String>("Reference coordinate",null,0,new String[]{"Upstream","Downstream"})
						.setLayoutStyle(ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS) )
						.setTopMost(true)
		);
		
		addSetting(
				new HierarchicalSetting("Convert to primitive coordinates").
				addSetting(
						changePrimitive = new BooleanSetting("Merge to primitive coordinates",
								"Convert complex, multi-part coordinates (e.g. exon models) to primitive coordinates.\n" +
								"The new coordinates will span the whole region defined by the start of the first exon\n" +
								"and the end of the last exon of the input coordinate.", false)
				).addSetting(
						splitPrimitive = new BooleanSetting("Split to primitive coordinates",
								"Convert complex, multi-part coordinates (e.g. exon models) to primitive coordinates.\n" +
								"Each part (exon) will be represented by a new coordinate.", false)
				)		
					
		);

		setLayoutStyle(LayoutStyle.TREE);

	}

	public LocusTransformSetting clone() {
		LocusTransformSetting gs = new LocusTransformSetting(getName());
		gs.fromPrefNode(this.toPrefNode());
		return gs;
	}

	protected ObjectSelectionSetting<Strand> makeStrandMapping(int input) {
		return new ObjectSelectionSetting<Strand>("Map \'"+Strand.values()[input]+"\' to", null, input, Strand.values());
	}

	public LocusTransformer getTransformer() {
		return new Transformer();
	}

	private class Transformer implements LocusTransformer {

		// settings values are fixed on construction
		boolean changeS = changeSpecies.getBooleanValue();
		boolean changeC =  changeChromosome.getBooleanValue();
		boolean changeM = changeMove.getBooleanValue();
		boolean changeF = changeFrom.getBooleanValue();
		boolean changeT = changeTo.getBooleanValue();
		boolean changeL = changeLength.getBooleanValue();
		boolean changeST = changeStrands.getBooleanValue();
		boolean changeP1 = changePrimitive.getBooleanValue();
		boolean changeP2 = splitPrimitive.getBooleanValue();
		
		Map<String,String> smap = speciesMap.getStringMapValue();
		Map<String,String> cmap = chromeMap.getStringMapValue();
		int movedelta = moveDelta.getIntValue();
		int upstreamdelta = upstreamDelta.getIntValue();
		int dnstreamdelta= downstreamDelta.getIntValue();
		int length = fixedLength.getIntValue();
		boolean upstreamReference = keepWhich.getObjectValue().equals("Upstream");
		Strand[] newstrand = new Strand[]{
				strandPosTarget.getObjectValue(),
				strandNegTarget.getObjectValue(),
				strandBothTarget.getObjectValue(),
				strandUndefTarget.getObjectValue()
		};

		public AbstractGeneticCoordinate[] transform(AbstractGeneticCoordinate inputCoordinate, ChromosomeSetContainer csc) {
			Chromosome chr = inputCoordinate.getChromosome();

			String sname = chr.getSpecies().getName();
			String cname = chr.getId();

			// Map species name
			if (changeS) {
				String sname2 = smap.get(sname);
				if (sname2!=null)
					sname=sname2;
			}
			
			// Map chromosome name
			if (changeC) {
				String cname2 = cmap.get(cname);
				if (cname2!=null)
					cname=cname2;			
			}
			
			// Change model details
			GBNode model = inputCoordinate.getModel();
			List<GBAtom> gba;
			
			// make the model primitive if needed/requested
			if (changeP1) {
				GBAtom atom = new GBAtom(model.getStart(), model.getEnd(), model.getStrand());
				gba = new ArrayList<GBAtom>();
				gba.add(atom);
			} else {
				gba = model.getCoordinateAtoms(); 
			}
				
			// atoms will be transformed individually.
			for (GBAtom atom : gba) {
				
				// change strand
				if (changeST)
					atom.strand = newstrand[atom.strand.ordinal()];
				
				int dir = atom.strand==Strand.MINUS ? -1 : +1;
				long upstream = atom.getUpstreamCoordinate();
				long dnstream = atom.getDownstreamCoordinate();					

				// move coordinate by delta
				if (changeM) {
					upstream += movedelta*dir;
					dnstream += movedelta*dir;
				}
				
				// move upstream coordinate by delta
				if (changeF)
					upstream += upstreamdelta*dir;
				
				// move downstream coordinate by delta
				if (changeT)
					dnstream += dnstreamdelta*dir;

				// change the length
				if (changeL) {
					if (upstreamReference)
						dnstream=upstream+(length*dir);
					else
						upstream=dnstream-(length*dir);
				}

				if (upstream<0)
					upstream=0;
				if (dnstream<0)
					dnstream=0;
				
				atom.setUpstreamCoordinate(upstream);
				atom.setDownstreamCoordinate(dnstream);

			}
			
			Chromosome c = csc.getChromosome(SpeciesContainer.getSpecies(sname), cname);
			
			AbstractGeneticCoordinate[] result;
			
			// split atoms if required
			if (changeP2) {
				result = new AbstractGeneticCoordinate[gba.size()+1];
				for (int i=0; i!=gba.size(); ++i)
					result[i+1] = new GeneticCoordinate(c,gba.get(i));
			} else {
				result = new AbstractGeneticCoordinate[1];
			}
			
			result[0] = new GeneticCoordinate(c, gba);
						
			return result; 
		}

		@SuppressWarnings("unchecked")
		public void addTransformedCoordinate(ChromosomeSetContainer target, AbstractGeneticCoordinate inputCoordinate) {

			AbstractGeneticCoordinate[] gc = transform(inputCoordinate, target);

			AbstractLocusChromosome out_chr = (AbstractLocusChromosome)target.getChromosome(
					gc[0].getChromosome().getSpecies(), 
					gc[0].getChromosome().getId()
			);

			for (AbstractGeneticCoordinate agc : gc) {
				out_chr.addLocus(agc.getModel(), inputCoordinate);
			}

		}


	}




}
