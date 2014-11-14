package mayday.wapiti.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.transkriptorium.data.MappedRead;
import mayday.transkriptorium.data.MappingStore;
import mayday.transkriptorium.data.Read;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.generic.reads.ReadsData;
import mayday.wapiti.statistics.base.AbstractStatisticsPlugin;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class EmpiricalMateDistance extends AbstractStatisticsPlugin {

	@Override
	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps) 
			if (!ReadsData.class.isAssignableFrom(e.getDataClass()))
				return false;	
		return true;
	}

	@Override
	public void computeStatistics(TransMatrix transMatrix, List<Experiment> exps) {
		for (Experiment e : exps) {
			computeStatistics(e);
		}
	}
	
	public void computeStatistics(Experiment e) {
		long total1=0, total2=0;
		long nonunique1=0, nonunique2=0;
		long unique1=0, unique2=0;
		long wrongchrome=0;
		long mated=0;
		
		MappingStore theData = ((ReadsData)e.getInitialData()).getFullData();
		
		ArrayList<Double> dists = new ArrayList<Double>();
		for (long r = 0; r!=theData.getTotalMappingCount(); ++r) {
			MappedRead mappedread = theData.getMappedRead(r);
			Read read = mappedread.getRead();
			if (read.getIdentifier().endsWith("/1")) {
				++total1;
				if (read.hasUniqueMapping()) {
					++unique1;
					Read partner = read.getPartner();
					if (partner!=null) {
						++total2;
						if (partner.hasUniqueMapping()) {
							++unique2;
							AbstractGeneticCoordinate coord = mappedread.getTargetCoordinate(); 
							MappedRead partnermappedread = partner.getAllMappings().next();
							AbstractGeneticCoordinate partnercoord = partnermappedread.getTargetCoordinate();
							if (!coord.getChromosome().equals(partnercoord.getChromosome())) {
								++wrongchrome;
							} else {
								long dist = coord.getDistanceTo(partnercoord, true);
								if (dist==0)
									dist = -coord.getOverlappingBaseCount(partnercoord.getFrom(),partnercoord.getTo());
								dists.add((double) dist);
							}
							++mated;
						} else
							++nonunique2;
					}
				} else {
					++nonunique1;
				}
			} else {
				if (read.getPartner()==null) {
					++total2;
					if (read.hasUniqueMapping())
						++unique2;
					else
						++nonunique2;
				}
			}
		}
		

		DoubleVector d = new DoubleVector(dists);
		
		showResults(e.getName(), theData.getTotalMappingCount(), total1, total2, unique1, unique2, nonunique1, nonunique2, mated, wrongchrome, d);		
	}
	
	protected void showResults(String en, long total, long total1, long total2, long unique1, long unique2, 
			long nonunique1, long nonunique2, long mated, long wrongchrome, DoubleVector d) {
		System.out.println("=== Experiment "+en);
		System.out.println("Mapping positions:          "+total);
		System.out.println("First mate mappings:        "+total1);
		System.out.println("- Unique                    "+unique1);
		System.out.println("- Nonunique                 "+nonunique1);
		System.out.println("Second mate mappings        "+total2);
		System.out.println("- Unique                    "+unique2);
		System.out.println("- Nonunique                 "+nonunique2);
		System.out.println("Mate pairs mapped uniquely  "+mated+"     equivalent to single reads: "+mated*2);
		System.out.println("- on different chromosomes  "+wrongchrome);
		System.out.println("Distances considered        "+d.size());
		System.out.println("- mean                      "+d.mean());
		System.out.println("- sd                        "+d.sd());
		System.out.println("- median                    "+d.median());
		System.out.println("- mad                       "+d.mad());
		System.out.println("- min                       "+d.min());
		System.out.println("- max                       "+d.max());
		
//		HistogramPlotComponent distHist = new HistogramPlotComponent(100);
//		distHist.getValueProvider().setValues(d.asList());
//	
//		MaydayFrame mf = new MaydayFrame("Empirical mate distances");
//		mf.setLayout(new BorderLayout());
//		mf.add(distHist, BorderLayout.CENTER);
//		
//		JLabel jl = new JLabel(
//				"=== Experiment "+en+"\n"
//				+ "Mapping positions:          "+total+"\n"
//				+ "First mate mappings:        "+total1+"\n"
//				+ "- Unique                    "+unique1+"\n"
//				+ "- Nonunique                 "+nonunique1+"\n"
//				+ "Second mate mappings        "+total1+"\n"
//				+ "- Unique                    "+unique2+"\n"
//				+ "- Nonunique                 "+nonunique2+"\n"
//				+ "Mate pairs mapped uniquely  "+mated+"     equivalent to single reads: "+mated*2+"\n"
//				+ "- on different chromosomes  "+wrongchrome+"\n"
//				+ "Distances considered        "+d.size()+"\n"
//				+ "- mean                      "+d.mean()+"\n"
//				+ "- sd                        "+d.sd()+"\n"
//				+ "- median                    "+d.median()+"\n"
//				+ "- mad                       "+d.mad()+"\n"
//				+ "- min                       "+d.min()+"\n"
//				+ "- max                       "+d.max()+"");
//		mf.add(jl, BorderLayout.SOUTH);
//		mf.pack();
//		mf.setVisible(true);
	}

	@Override
	public String getApplicabilityRequirements() {
		return "Requires mapped read data with detailed read information"; 
	}

	@Override
	public void updateSettings(Collection<Experiment> experiments) {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".EmpiricalDistances", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Compute empirical mate-pair distance", 
		"Statistic: Empirical mate pair distances");
	}
	
	public String getIdentifier() {
		return "[Mate Distance]";
	}

}
