package mayday.gsanalysis.gsea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import mayday.core.ClassSelectionModel;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.Binomial;
import mayday.core.math.PNorm;
import mayday.core.math.Statistics;
import mayday.core.math.pcorrection.PCorrectionPlugin;
import mayday.core.math.pcorrection.methods.Bonferroni;
import mayday.core.math.scoring.ScoringPlugin;
import mayday.core.math.scoring.ScoringResult;
import mayday.core.math.scoring.TestPlugin;
import mayday.core.math.stattest.StatTestPlugin;
import mayday.core.math.stattest.StatTestResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.PluginTypeSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.methods.PCorrectionMethodSetting;
import mayday.core.settings.typed.ClassSelectionSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.genemining2.methods.SignalToNoisePlugin;
import mayday.gsanalysis.Enrichment;
import mayday.gsanalysis.AbstractGSAnalysisPlugin;
import mayday.gsanalysis.Geneset;

public class GSEAPlugin extends AbstractGSAnalysisPlugin {
	protected final String name = "GSEA";
	protected ClassSelectionSetting classes;
	protected PluginTypeSetting<TestPlugin<ScoringResult>> testSingleGenesSetting;
	protected TestPlugin<ScoringResult> testSingleGenes;
	protected RestrictedStringSetting permutationType;
	protected RestrictedStringSetting seedSetting;
	protected IntSetting permutationsSetting;
	protected DoubleSetting pSetting;
	protected double p;
	protected Random random;
	protected Map<Object,double[]> probesMap;
	protected PCorrectionMethodSetting correctionMethodSetting;
	protected SelectableHierarchicalSetting select;
	protected boolean permutationTests;
	protected boolean hasScore;
	protected List<Integer> listNumbers;
	protected Geneset randomGeneset;


	public GSEAPlugin() {
	}

	private int smallerOrEqualValues(List<Double> values, double value) {
		int pos=Collections.binarySearch(values,value);
		if(pos<0) {
			return (pos+1)*(-1);
		}
		else {
			for(int i=pos+1;i!=values.size();i++) { 
				if(values.get(i)>value) {
					return i;
				}
			}
		}
		return values.size();
	}
	public boolean getPermutationTests() {
		return permutationTests;
	}
	
	@Override
	public void calculateEnrichment() {
		if(select.getSelectedIndex()==0) { 
			permutationTests=true;
			long seed=149;
			if(seedSetting.getStringValue().equals("Current time")) {
				Date currentDate = new Date();
				seed = currentDate.getTime();
			}

			random=new Random(seed);
			
			p=pSetting.getDoubleValue();
		}
		else {
			permutationTests=false;
		}
		
		probesMap = new HashMap<Object,double[]>();
		for(Probe p: probes.toCollection()) {
			probesMap.put(p, p.getValues());
		}

		
		testSingleGenes = testSingleGenesSetting.getInstance();
		enrichmentWithClasses(classes.getModel());
	}
	
	public void createGeneList(ProbeList probes, DoubleMatrix geneList, ClassSelectionModel cs,boolean permutationList,TestPlugin<ScoringResult> testSingleGenes) {
		geneList.unpermuteRows();
		ScoringResult t = null;
		if(probesMap==null) {
			probesMap = new HashMap<Object,double[]>();
			for(Probe p: probes.toCollection()) {
				probesMap.put(p, p.getValues());
			}
		}
		t = testSingleGenes.runTest(probesMap, cs);
		if(t==null) {
			throw new RuntimeException("Test for single genes failed");
		}

		if(!permutationList) {
			if(t instanceof StatTestResult) {
				if(((StatTestResult) t).hasRawScore()) {
					hasScore=true;
					geneList.setColumnName(0, "Score");
					geneList.setColumnName(1, "p-Value");
				}
				else {
					geneList.setColumnName(0, "p-Value");
					hasScore=false;
				}
			}
			else {
				geneList.setColumnName(0, "Score");
				hasScore=true;
			}
		}
		int row=0;
		if(t instanceof StatTestResult && hasScore) {
			MIGroup pValues=null;
			pValues=((StatTestResult)t).getPValues();
			for(Entry<Object, MIType> entry:t.getRawScore().getMIOs()) {
				Probe p = (Probe) entry.getKey();
				geneList.setRowName(row,p.getName());
				double score = ((DoubleMIO)entry.getValue()).getValue();
				geneList.setValue(row,0,score);
				if(!permutationList) {
					double pValue = ((DoubleMIO)pValues.getMIO(p)).getValue();
					geneList.setValue(row, 1, pValue);
				}
				row++;
			}

		}
		else if(t instanceof StatTestResult && !hasScore){
			for(Entry<Object, MIType> entry:((StatTestResult)t).getPValues().getMIOs()) {
				Probe p = (Probe) entry.getKey();
				geneList.setRowName(row,p.getName());
				double pValue = ((DoubleMIO)entry.getValue()).getValue();
				geneList.setValue(row,0,pValue);
				row++;
			}
		}
		else {
			for(Entry<Object, MIType> entry:t.getRawScore().getMIOs()) {
				Probe p = (Probe) entry.getKey();
				geneList.setRowName(row,p.getName());
				double score = ((DoubleMIO)entry.getValue()).getValue();
				geneList.setValue(row,0,score);
				row++;
			}
		}
		int[] permutation = geneList.getColumn(0).order();
		if(hasScore) {
			int[] reversedPermutation = new int[permutation.length];
			for(int i=0;i!=permutation.length;i++) {
				reversedPermutation[permutation.length-1-i]=permutation[i];			
			}
			permutation=reversedPermutation;
		}
		geneList.setRowPermutation(permutation);
	}
	
	protected void permuteScores(DoubleMatrix geneList,AbstractVector scores) {
		geneList.unpermuteRows();
		scores.permute(random);
		geneList.setColumn(0,scores);
		
		int[] permutation = geneList.getColumn(0).order();
		if(hasScore) {
			int[] reversedPermutation = new int[permutation.length];
			for(int i=0;i!=permutation.length;i++) {
				reversedPermutation[permutation.length-1-i]=permutation[i];			
			}
			permutation=reversedPermutation;
		}
		geneList.setRowPermutation(permutation);
	}


	protected double calculateES(GSEAEnrichment e, DoubleMatrix allGenes, boolean writeEnrichment,boolean useRandomGeneset) {
		Geneset g=e.getGeneset();
		if(useRandomGeneset) {
			g=getRandomGeneset(g.getGenes().size(),allGenes);
		}

		double N = allGenes.nrow();

		double N_H = g.getGenes().size();
		double N_R = 0;

		LinkedList<Integer> gsPositions = new LinkedList<Integer>();
		for (String gene : g.getGenes()) {
			int pos=allGenes.getRowIndex(gene);
			gsPositions.add(pos);
		}
		Collections.sort(gsPositions);
		for(int pos:gsPositions) {
			if(p!=0) {	
				N_R+=Math.pow(Math.abs(allGenes.getValue(pos, 0)),p);
			}
			else {
				N_R+=1;
			}
		}


		if(N_R ==0 || N_H == N || N_H==0) {
			if(writeEnrichment) {
				e.setUnnormalizedScore(0);
			}
			return 0;
		}

		DoubleMatrix rankedGeneSet = null;
		if(writeEnrichment) {
			if(allGenes.getColumnName(1)!=null) {
				rankedGeneSet = new DoubleMatrix(g.getGenes().size(),2);
				rankedGeneSet.setColumnName(0, "Score");
				rankedGeneSet.setColumnName(1, "p-Value");
			}
			else {
				rankedGeneSet = new DoubleMatrix(g.getGenes().size(),1);
				rankedGeneSet.setColumnName(0, allGenes.getColumnName(0));

			}
		}
		int membersOfGeneSet=0;
		int positionOfBest=0;

		double PHit=0, PMiss=0;
		double bestRS=0;

		double pmadd = 1.0/(N-N_H);


		for(int i=0;i!=allGenes.nrow();i++) {
			if (gsPositions!=null && gsPositions.get(0)==i) {
				gsPositions.removeFirst();
				if (gsPositions.size()==0)
					gsPositions=null;
				if(p!=0) {
					PHit+= Math.pow(Math.abs(allGenes.getValue(i,0)),p)/ N_R;
				}
				else {
					PHit+= 1/ N_R;
				}
				if(writeEnrichment && rankedGeneSet!=null) {
					rankedGeneSet.setRowName(membersOfGeneSet, allGenes.getRowName(i));
					if(rankedGeneSet.ncol()==2) {
						rankedGeneSet.setValue(membersOfGeneSet, 0, allGenes.getValue(i, 0));
						rankedGeneSet.setValue(membersOfGeneSet, 1, allGenes.getValue(i,1));
					}
					else {
						rankedGeneSet.setValue(membersOfGeneSet, 0, allGenes.getValue(i, 0));
					}
				}
				membersOfGeneSet++;
			}
			else {
				PMiss+= pmadd;
			}

			double dev=PHit-PMiss;
			if(Math.abs(dev) > Math.abs(bestRS)) {
				bestRS=dev;
				positionOfBest=membersOfGeneSet-1;
			}
		}
		if(writeEnrichment && rankedGeneSet!=null) {
			e.setUnnormalizedScore(bestRS);
			int[] rows = null;
			int[] columns = new int[rankedGeneSet.ncol()];
			for(int i=0;i!=columns.length;i++) {
				columns[i]=i;
			}
			if(bestRS>0) {
				rows = new int[positionOfBest+1];
				for(int i=0;i!=rows.length;i++) {
					rows[i]=i;
				}
			}
			else {
				rows = new int[membersOfGeneSet-positionOfBest];
				for(int i=0;i!=rows.length;i++) {
					rows[i]=positionOfBest+i;
				}
			}
			e.setLeadingEdge(rankedGeneSet.submatrix(rows, columns));
		}
		return bestRS;
	}

	protected void calculatePermutationScores(DoubleMatrix geneListPermutation, List<Enrichment> results,DoubleMatrix allGenes) {
		int nPermutations = permutationsSetting.getIntValue();
		ClassSelectionModel csm = classes.getModel();

		if(permutationType.getStringValue().equals("Classes")) {
			double progressStep = 9000/((double)Binomial.binomial(csm.getNumClasses(),2) * nPermutations);
			for(int i=0;i!=nPermutations;i++) {
				createGeneList(probes,geneListPermutation, csm.permute(random),true, testSingleGenes);
				for(Enrichment enr:results) {
					GSEAEnrichment e = (GSEAEnrichment)enr;
					double currentScore = calculateES(e,geneListPermutation,false,false);
					e.setPermutationScore(i,currentScore);
				}
				progress+= progressStep;
				t.setProgress((int)progress);
			}
		}
		else if(permutationType.getStringValue().equals("Scores")) {
			double progressStep = 9000/((double)Binomial.binomial(csm.getNumClasses(),2) * nPermutations);
			AbstractVector scores=geneListPermutation.getColumn(0).clone();
			for(int i=0;i!=nPermutations;i++) {
				permuteScores(geneListPermutation,scores);
				for(Enrichment enr:results) {
					GSEAEnrichment e = (GSEAEnrichment)enr;
					double currentScore = calculateES(e,geneListPermutation,false,false);
					e.setPermutationScore(i,currentScore);
				}
				progress+= progressStep;
				t.setProgress((int)progress);
			}
		}
		else {
			double progressStep = 9000/((double)Binomial.binomial(csm.getNumClasses(),2) * genesets.size() * nPermutations);
			for(int i=0;i!=nPermutations;i++) {
				for(Enrichment enr:results) {
					GSEAEnrichment e = (GSEAEnrichment)enr;
					double currentScore = calculateES(e,allGenes,false,true);
					e.setPermutationScore(i,currentScore);
					progress+= progressStep;
					t.setProgress((int)progress);
				}
			}
		}
	}

	protected Geneset getRandomGeneset(int size, DoubleMatrix allGenes) {
		if(listNumbers==null) {
			listNumbers = new LinkedList<Integer>();
			for(int i=0;i!=allGenes.nrow();i++) {
				listNumbers.add(i);
			}
		}
		Collections.shuffle(listNumbers, random);
		
		if(randomGeneset==null) {
			randomGeneset = new Geneset("Random");
		}
		randomGeneset.removeAllGenes();
		for(int i=0;i!=size;i++) {
			randomGeneset.addGene(allGenes.getRowName(listNumbers.get(i)));
		}
		return randomGeneset;
	}

	protected void calculateNormalizedScoresAndPValues(List<Enrichment> results) {
		LinkedList<Double> positivePermutationScores = new LinkedList<Double>();
		LinkedList<Double> negativePermutationScores = new LinkedList<Double>();
		int nPermutations=permutationsSetting.getIntValue();
		for(Enrichment enr:results) {
			GSEAEnrichment e = (GSEAEnrichment) enr;
			positivePermutationScores.clear();
			negativePermutationScores.clear();

			double greaterValues=0;
			double numberOfValues=0;
			
			for(double score: e.getPermutationScores()) {
				if(score>=0) {
					positivePermutationScores.add(score);
					if(e.getUnnormalizedScore()>=0) {
						numberOfValues++;
						if(score>=e.getUnnormalizedScore()) {
							greaterValues++;
						}						}
				}
				else {
					negativePermutationScores.add(score);
					if(e.getUnnormalizedScore()<0){
						numberOfValues++;
						if(score<=e.getUnnormalizedScore()) {
							greaterValues++;
						}
					}
				}
			}
			e.setPValue(((double)greaterValues)/numberOfValues);
			double meanpos = Statistics.mean(positivePermutationScores);
			double meanneg = Statistics.mean(negativePermutationScores);
			for(int i=0;i!=nPermutations;i++) {
				double score=e.getPermutationScores().get(i);
				if(score>=0) {
					e.setNormalizedPermutationScore(i,score/meanpos);
				}
				else {
					e.setNormalizedPermutationScore(i,-1*score/meanneg);
				}
			}
			if(e.getUnnormalizedScore()>=0) {
				e.setScore(e.getUnnormalizedScore()/meanpos);
			}
			else {
				e.setScore(-1*e.getUnnormalizedScore()/meanneg);
			}
		}
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.GeneSetAnalysis.GSEA",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Roland Keller",
				"no e-mail provided",
				"Geneset Enrichment Analysis",
		"Geneset Enrichment Analysis (GSEA)");
		pli.addCategory("Geneset Analysis");
		pli.getProperties().put(Constants.NO_CACHE_CLASS_INSTANCE, true);
		return pli;
	}


	@Override
	protected List<Setting> additionalSettings() {
		ClassSelectionModel csm = new ClassSelectionModel(dataSet.getMasterTable());
		classes = new ClassSelectionSetting("Experiment classes","Define the phenotypes of the experiments", csm, 2,null,dataSet);
		Set<PluginInfo> plugins = new TreeSet<PluginInfo>();
		plugins.addAll(PluginManager.getInstance().getPluginsFor(StatTestPlugin.MC));
		plugins.addAll(PluginManager.getInstance().getPluginsFor(ScoringPlugin.MC));
		
		testSingleGenesSetting = new PluginTypeSetting<TestPlugin<ScoringResult>>("Statistical Test for single genes",null, new SignalToNoisePlugin(), plugins);
		seedSetting = new RestrictedStringSetting("Seed for random permutations", null, 1, new String[]{"Default value(149)","Current time"});
		permutationsSetting = new IntSetting("Number of permutations","Select number of permutations for determination of significance.", 1000,10,10000,true,true);
		pSetting = new DoubleSetting("Value for p in algorithm","Value that determines how much the scores for the single genes contribute to the enrichment score.\n p=0: Only the ranks of the genes are important.", 1.0,0.0,10.0,true,true);
		permutationType = new RestrictedStringSetting("Permutation Type", "Select whether genesets or classes should be permuted in order to determine significance.", 2, new String[]{"Scores","Genesets","Classes"});
		HierarchicalSetting setting1 = new HierarchicalSetting("Permutation tests");
		setting1.addSetting(permutationsSetting).addSetting(permutationType).addSetting(seedSetting).addSetting(pSetting);
		
		correctionMethodSetting = new PCorrectionMethodSetting("Method for p-value correction", null, new Bonferroni());
		HierarchicalSetting setting2 = new HierarchicalSetting("Z-Test and Chi-Square-Test");
		setting2.addSetting(correctionMethodSetting);
		select=new SelectableHierarchicalSetting("Method for calculation of p-values","Determine significance with permutation tests or use Z-Test and Chi-Square-Test (distribution of statistic for single genes must be symmetrical!)?",0,new Setting[]{setting1,setting2})
		.setLayoutStyle(SelectableHierarchicalSetting.LayoutStyle.COMBOBOX);
		List<Setting> settings=new LinkedList<Setting>();
		settings.add(classes);
		settings.add(testSingleGenesSetting);
		settings.add(select);
		return settings;
		


	}


	//compute FDR q-Value with histogram
	protected void computeFDRHist(List<Enrichment> results,DoubleMatrix values,ArrayList<Double> realScoresPositive,ArrayList<Double> realScoresNegative) {
		String permType=permutationType.getStringValue();
		ArrayList<List<Double>> scoresPositive=new ArrayList<List<Double>>();
		ArrayList<List<Double>> scoresNegative=new ArrayList<List<Double>>();
		
		if(permType.equals("Classes") || permType.equals("Scores")) {
			for(int column=0;column!=values.ncol();column++) {
				AbstractVector colVector = values.getColumn(column);
				colVector.sort();
				int lastNegative=-1;
				for(double score:colVector) {
					if(score<0) {
						lastNegative++;
					}
					else {
						break;
					}
				}
				List<Double> listNegative=null;
				List<Double> listPositive=null;
				if(lastNegative>=0) {
					listNegative=colVector.subset(0,lastNegative).asList();
				}
				if(lastNegative<colVector.size()-1) {
					AbstractVector positives=colVector.subset(lastNegative+1,colVector.size()-1).clone();
					positives.multiply(-1);
					positives.reverse();
					listPositive=positives.asList();
				}
				scoresPositive.add(listPositive);
				scoresNegative.add(listNegative);	
			}
		}
		else {
			for(int row=0;row!=values.nrow();row++) {
				AbstractVector rowVector = values.getRow(row);
				rowVector.sort();
				int lastNegative=-1;
				for(double score:rowVector) {
					if(score<0) {
						lastNegative++;
					}
					else {
						break;
					}
				}
				List<Double> listNegative=null;
				List<Double> listPositive=null;
				if(lastNegative>=0) {
					listNegative=rowVector.subset(0,lastNegative).asList();
				}
				if(lastNegative<rowVector.size()-1) {
					AbstractVector positives=rowVector.subset(lastNegative+1,rowVector.size()-1).clone();
					positives.multiply(-1);
					positives.reverse();
					listPositive=positives.asList();
				}
				scoresPositive.add(listPositive);
				scoresNegative.add(listNegative);	
			}
		}
		
		
		
		
		for(Enrichment e: results) {
			double NES = e.getScore();
			double sumPercentages=0;
			int permutations=0;
			for(int i=0;i!=scoresPositive.size();i++) {
				List<Double> currentPositives=scoresPositive.get(i);
				List<Double> currentNegatives=scoresNegative.get(i);
				int numberOfValues=0;
				int greaterValues=0;
				if(NES>=0 && currentPositives!=null) {
					numberOfValues=currentPositives.size();
					greaterValues=smallerOrEqualValues(currentPositives, -1*NES);
					
				}
				else if(NES<0 && currentNegatives!=null){
					numberOfValues=currentNegatives.size();
					greaterValues=smallerOrEqualValues(currentNegatives, NES);
					
				}
				if(numberOfValues!=0) {
					double currentPercentage=((double)greaterValues)/numberOfValues;
					sumPercentages+=currentPercentage;
					permutations++;
				}
			}
			
			int numberOfValues=0;
			int greaterValues=0;
 			if(NES>=0) {
				numberOfValues=realScoresPositive.size();
				greaterValues=smallerOrEqualValues(realScoresPositive, -1*NES);
				
			}
			else {
				numberOfValues=realScoresNegative.size();
				greaterValues=smallerOrEqualValues(realScoresNegative, NES);
				
			}
			double meanPercentage = sumPercentages/permutations;
			double percentageReal = (double)greaterValues/numberOfValues;
			double qValue=meanPercentage/percentageReal;
			if(qValue>1) {
				qValue=1;
			}
			((GSEAEnrichment) e).setFDRhist(qValue);

		}
	}
		
		
		/*
		for(Enrichment e1: results) {
			double NES = e1.getScore();
			double sumPercentages=0;
			int permutations=0;
			if(permType.equals("Classes") || permType.equals("Scores")) {
				for(int i=0;i!=permutationsSetting.getIntValue();i++) {
					int numberOfValues=0;
					int greaterValues=0;
					for(Enrichment e2: results) {
						double currentNES=((GSEAEnrichment) e2).getNormalizedPermutationScores().get(i);
						if(NES>=0 && currentNES>=0) {
							numberOfValues++;
							if(currentNES>=NES) {
								greaterValues++;
							}
						}		
						else if(NES<0 && currentNES<0){
							numberOfValues++;
							if(currentNES<=NES) {
								greaterValues++;
							}
						}
					}
				
					
					if(numberOfValues!=0) {
						double currentPercentage=((double)greaterValues)/numberOfValues;
						sumPercentages+=currentPercentage;
						permutations++;
					}
				}
			}
			else {
				for(Enrichment e2: results) {
					int numberOfValues=0;
					int greaterValues=0;
					for(double currentNES:((GSEAEnrichment) e2).getNormalizedPermutationScores()) {
						if(NES>=0 && currentNES>=0) {
							numberOfValues++;
							if(currentNES>=NES) {
								greaterValues++;
							}
						}		
						else if(NES<0 && currentNES<0){
							numberOfValues++;
							if(currentNES<=NES) {
								greaterValues++;
							}
						}
					}
				
					
					if(numberOfValues!=0) {
						double currentPercentage=((double)greaterValues)/numberOfValues;
						sumPercentages+=currentPercentage;
						permutations++;
					}
				}
			}
			int numberOfValues=0;
			int greaterValues=0;
			for(Enrichment e2: results) { 
				double currentNES=e2.getScore();
				if(NES>=0 && currentNES>=0) {
					numberOfValues++;
					if(currentNES>=NES) {
						greaterValues++;
					}
				}		
				else if(NES<0 && currentNES<0){
					numberOfValues++;
					if(currentNES<=NES) {
						greaterValues++;
					}
				}
			}
			double meanPercentage = sumPercentages/permutations;
			double percentageReal = (double)greaterValues/numberOfValues;
			double qValue=meanPercentage/percentageReal;
			if(qValue>1) {
			qValue=1;
			}
			((GSEAEnrichment) e1).setFDRhist(qValue);

		}
	}*/

	//compute FWER q-value with histogram
	protected void computeFWERHist(List<Enrichment> results, DoubleMatrix values) {
		String permType=permutationType.getStringValue();
		
		ArrayList<Double> maxNES=new ArrayList<Double>();
		ArrayList<Double> minNES=new ArrayList<Double>();
		if(permType.equals("Classes") || permType.equals("Scores")) {
			for(int column=0;column!=values.ncol();column++) {
				double max=values.getColumn(column).max();
				double min=values.getColumn(column).min();
				if(max>=0) {
					maxNES.add(-1*max);
				}
				if(min<0) {
					minNES.add(min);
				}
			}
		}
		else {
			for(int row=0;row!=values.nrow();row++) {
				double max=values.getRow(row).max();
				double min=values.getRow(row).min();
				if(max>=0) {
					maxNES.add(-1*max);
				}
				if(min<0) {
					minNES.add(min);
				}
			}
		}
		Collections.sort(maxNES);
		Collections.sort(minNES);
		
		for(Enrichment e: results) {
			double NES = e.getScore();
			int numberOfValues=0;
			int greaterValues=0;
			
			if(NES>=0) {
				numberOfValues=maxNES.size();
				greaterValues=smallerOrEqualValues(maxNES, -1*NES);
			}
			else {
				numberOfValues=minNES.size();
				greaterValues=smallerOrEqualValues(minNES, NES);
			}
			double pValue=((double)greaterValues)/numberOfValues;
			((GSEAEnrichment) e).setFWERhist(pValue);
		}
	}
		/*
		for(Enrichment e1: results) {
			double NES = e1.getScore();
			int greaterMaxNES=0;
			int permutations=0;
			if(permType.equals("Classes") || permType.equals("Scores")) {
				for(int i=0;i!=((GSEAEnrichment)e1).getNormalizedPermutationScores().size();i++) {
					//determine maximal NES for permutation
					double maxNES=0;
					for(Enrichment e2: results) {
						double currentNES=((GSEAEnrichment) e2).getNormalizedPermutationScores().get(i);

						if(NES>=0 && currentNES>maxNES) {
							maxNES=currentNES;
						}		
						else if(NES<0 && currentNES<maxNES) {
							maxNES=currentNES;
						}
					}
					if(maxNES!=0) {
						permutations++;
						if(NES>=0 && maxNES>=NES) {
							greaterMaxNES++;
						}
						else if(NES<0 && maxNES<=NES) {
							greaterMaxNES++;
						}
					}
				}
			}
			else {
				for(Enrichment e2: results) {
					//determine maximal NES for gene set size
					double maxNES=0;
					for(double currentNES:((GSEAEnrichment) e2).getNormalizedPermutationScores()) {
						if(NES>=0 && currentNES>maxNES) {
							maxNES=currentNES;
						}		
						else if(NES<0 && currentNES<maxNES) {
							maxNES=currentNES;
						}
					}
					if(maxNES!=0) {
						permutations++;
						if(NES>=0 && maxNES>=NES) {
							greaterMaxNES++;
						}
						else if(NES<0 && maxNES<=NES) {
							greaterMaxNES++;
						}
					}
				}
			}
			double pValue=((double)greaterMaxNES)/permutations;
			((GSEAEnrichment) e1).setFWERhist(pValue);

		}
	}*/
	@Override
	protected List<Enrichment> calculateEnrichmentWithClasses(
			ClassSelectionModel csm) {

		t.setProgress((int)progress);
		DoubleMatrix geneList = new DoubleMatrix(probes.getNumberOfProbes(),2);
		createGeneList(probes,geneList,csm,false, testSingleGenes);
		List<Enrichment> results = null;
		if(select.getSelectedIndex()==0) {
			results = new LinkedList<Enrichment>(); 
			for(Geneset g: genesets) {
				GSEAEnrichment e = new GSEAEnrichment(g,permutationsSetting.getIntValue());
				calculateES(e,geneList,true,false);
				if(e.getUnnormalizedScore() != 0) {
					results.add(e);	
				}
			}
			calculatePermutationScores(geneList.deepClone(),results,geneList);
			calculateNormalizedScoresAndPValues(results);
			
			DoubleMatrix values = new DoubleMatrix(results.size(),permutationsSetting.getIntValue());
			int row=0;
			for(Enrichment e:results) {
				values.setRow(row,((GSEAEnrichment)e).getNormalizedPermutationScores());
				row++;
			}
			ArrayList<Double> realScoresPositive=new ArrayList<Double>();
			ArrayList<Double> realScoresNegative=new ArrayList<Double>();
			
			for(Enrichment e: results) { 
				double currentNES=e.getScore();
				if(currentNES>=0) {
					realScoresPositive.add(-1*currentNES);
				}		
				else {
					realScoresNegative.add(currentNES);
				}
			}
			Collections.sort(realScoresPositive);
			Collections.sort(realScoresNegative);
			computeFDRHist(results,values,realScoresPositive,realScoresNegative);
			progress+=(800)/((double)Binomial.binomial(csm.getNumClasses(),2));
			t.setProgress((int)progress);
			computeFWERHist(results,values);
			progress+=(200)/((double)Binomial.binomial(csm.getNumClasses(),2));
			t.setProgress((int)progress);
		}
		else {
			results = calculateScore(geneList);
			correction(results,correctionMethodSetting.getInstance());
		}
		return results;
	}

	@Override
	public String getName() {
		return name;
	}

	protected List<Enrichment> calculateScore(DoubleMatrix geneList) {
		List<Enrichment> results = new LinkedList<Enrichment>(); 
		double mean = geneList.getColumn(0).mean();
		double sd=geneList.getColumn(0).sd();
		System.out.println("Mean: " + mean);
		System.out.println("SD: " + sd);
		ClassSelectionModel csm = classes.getModel();
			
		for(Geneset g: genesets) {
			SimpleGSEAEnrichment e = new SimpleGSEAEnrichment(g);
			calculateSimpleEnrichment(e, geneList, mean, sd,e.getGeneset().getGenes(),false,false);
			results.add(e);
			progress+= 9000/((double)Binomial.binomial(csm.getNumClasses(),2) * genesets.size());
			t.setProgress((int)progress);
		}
		
		return results;
		
	}
	
	public void calculateSimpleEnrichment(SimpleGSEAEnrichment e, DoubleMatrix geneList, double mean, double sd,Set<String> genesToTest,boolean noZ, boolean noScale) {
		int testSize = genesToTest.size();
		double scaleScore=0;
		double score=0;
		
		if(testSize!=0) {
			double sumScores=0;
			for(String gene: genesToTest) {
				sumScores+=((geneList.getValue(gene, 0))-mean)/sd;
			}
			double meanScoreGeneset=sumScores/testSize;
			score = Math.sqrt(testSize)*meanScoreGeneset;

			
			double sum=0;
		
			for(String gene:genesToTest) {
				sum+=(((geneList.getValue(gene, 0)-mean)/sd)-meanScoreGeneset)*(((geneList.getValue(gene, 0)-mean)/sd)-meanScoreGeneset);
			}
			if(testSize>1) {	
				scaleScore = (sum-testSize+1)/Math.sqrt(2*(testSize-1));
			}
		}
		if(noZ) {
			score=0;
		}
		if(noScale) {
			scaleScore=0;
		}
		double scalePValue=2*PNorm.getDistribution(Math.abs(scaleScore), true);
		double zPValue = 2*PNorm.getDistribution(Math.abs(score), true);
		e.setZPValue(zPValue);
		e.setScalePValue(scalePValue);
		e.setPValue(Math.min(zPValue, scalePValue));
		e.setScore(score);
		e.setScaleScore(scaleScore);
		
	}
	
	public void correction(List<Enrichment> results,PCorrectionPlugin correctionMethod) {

		ArrayList<Double> pValuesZ = new ArrayList<Double>();
		ArrayList<Double> pValuesScale = new ArrayList<Double>();
		List<Double> correctedValuesZ;
		List<Double> correctedValuesScale;
		List<Double> zScores= new ArrayList<Double>();
		List<Double> scaleScores= new ArrayList<Double>();
		
		for(Enrichment e:results) {
			SimpleGSEAEnrichment enr = (SimpleGSEAEnrichment)e;
			zScores.add(enr.getScore());
			scaleScores.add(enr.getScaleScore());
		}
		
		double meanZ=Statistics.mean(zScores);
		double sdZ=Statistics.sd(zScores);
		double meanScale=Statistics.mean(scaleScores);
		double sdScale=Statistics.sd(scaleScores);
		
		System.out.println("Mean Z: " + meanZ);
		System.out.println("SD Z: " + sdZ);
		System.out.println("Mean Scale: " + meanScale);
		System.out.println("SD Scale: " + sdScale);
		
		for(Enrichment e:results) {
			SimpleGSEAEnrichment enr = (SimpleGSEAEnrichment)e;
			pValuesZ.add(enr.getZPValue());
			pValuesScale.add(enr.getScalePValue());	
		}
		correctedValuesZ = correctionMethod.correct(pValuesZ);
		correctedValuesScale = correctionMethod.correct(pValuesScale);
		Iterator<Integer> zPIterator= Statistics.rank(pValuesZ).iterator();
		Iterator<Integer> scalePIterator = Statistics.rank(pValuesScale).iterator();
		
		Iterator<Double> zIterator = correctedValuesZ.iterator();
		Iterator<Double> chiSquareIterator = correctedValuesScale.iterator();
		
		for(Enrichment e: results) {
			SimpleGSEAEnrichment enr = (SimpleGSEAEnrichment)e;
			enr.setZPValue(zIterator.next());
			enr.setScalePValue(chiSquareIterator.next());
			enr.setPValue(Math.min(enr.getZPValue(), enr.getScalePValue()));
			enr.setZRank(zPIterator.next());
			enr.setScaleRank(scalePIterator.next());
		}
		
		
		
	}


	@Override
	protected String additionalPreferences() {
		String preferences="";
		if(select.getSelectedIndex()==0) {
			preferences+="GSEA with " + permutationsSetting.getIntValue() + " permutations <p/>";
			preferences+="Permutation type: " + permutationType.getObjectValue() + "<p/>";
			preferences+="p = " + pSetting.getDoubleValue() + "<p/>"; 
			
			
		}
		else {
			preferences+= "GSEA without permutations <p/>";
			preferences+= "Method for p-value correction: " + correctionMethodSetting.getPluginInfo().getName()+ "<p/>";
		}
		preferences+= "Statistic for single genes: " + testSingleGenesSetting.getPluginInfo().getName() + "<p/>";
		preferences+= "ClassSelectionModel: " + classes.getModel().toString(true)+ "<p/>";
		return preferences;
	}


	


}
