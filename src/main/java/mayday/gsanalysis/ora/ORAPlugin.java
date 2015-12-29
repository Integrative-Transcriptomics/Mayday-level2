package mayday.gsanalysis.ora;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


import mayday.core.ClassSelectionModel;
import mayday.core.Probe;
import mayday.core.math.Binomial;
import mayday.core.math.pcorrection.PCorrectionPlugin;
import mayday.core.math.pcorrection.methods.Bonferroni;
import mayday.core.math.stattest.StatTestPlugin;
import mayday.core.math.stattest.StatTestResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.generic.PluginTypeSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.methods.PCorrectionMethodSetting;
import mayday.core.settings.typed.ClassSelectionSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.gsanalysis.Enrichment;
import mayday.gsanalysis.AbstractGSAnalysisPlugin;
import mayday.gsanalysis.Geneset;
import mayday.gsanalysis.ora.tests.BinomialTest;
import mayday.gsanalysis.ora.tests.ChiSquareTest;
import mayday.gsanalysis.ora.tests.FisherTest;
import mayday.gsanalysis.ora.tests.HypergeometricTest;
import mayday.gsanalysis.pcorrect.PValueCorrectionWithPermutationsPlugin;
import mayday.gsanalysis.pcorrect.PermutedFDR;
import mayday.statistics.TTest.TTestPlugin;

public class ORAPlugin extends AbstractGSAnalysisPlugin{
	protected final String name = "ORA";
	protected Set<String> interestingGenes;
	protected Set<String> allGenes;
	protected ClassSelectionSetting m3_classes;
	protected SelectableHierarchicalSetting select,selectCorrection;
	protected PluginTypeSetting<StatTestPlugin> m3_testmethod;
	protected PCorrectionMethodSetting correctionMethodSetting;
	protected ObjectSelectionSetting<GenesetTest> overreptest;
	protected DoubleSetting m2_or_m3_threshold;
	protected MIGroupSetting m2_pvalues;
	protected IntSetting nPermutations;
	protected PluginTypeSetting<PValueCorrectionWithPermutationsPlugin> correctionMethodSettingPerm;
	
	
	public ORAPlugin()  {
	}
	
	@Override
	protected void removeSmallAndBigGenesets(int minSize,int maxSize,List<Geneset> genesets) {
		if(select.getSelectedIndex()==0) {
			List<Geneset> genesetsToRemove= new LinkedList<Geneset>();
			for(Geneset g: genesets) {
				if(g.getGenes().size()<minSize || g.getGenes().size()>maxSize) {
					genesetsToRemove.add(g);
					g.setRemoved(true);
				}
			}
			genesets.removeAll(genesetsToRemove);
		}
		else {
			super.removeSmallAndBigGenesets(minSize,maxSize,genesets);
		}
	}
	
	protected void correction(List<Enrichment> testResults, int selectedCorrection,int permutations,PCorrectionPlugin correctionPlugin,PValueCorrectionWithPermutationsPlugin correctionPluginPermutations) {

		ArrayList<Double> pvalues = new ArrayList<Double>();
		List<Double> correctedValues=null;
		
		for(Enrichment e:testResults) {
			pvalues.add(e.getPValue());
		}
		
		if(selectedCorrection==0) {
			if(correctionPlugin==null) {
				throw new RuntimeException("Plugin for p-value correction must be defined");
			}
			correctedValues = correctionPlugin.correct(pvalues);
		}
		else {
			if(correctionPluginPermutations==null) {
				throw new RuntimeException("Plugin for p-value correction must be defined");
			}
			DoubleMatrix permutationPValues = new DoubleMatrix(permutations,testResults.size());
			
			List<Enrichment> permutationEnrichments=new LinkedList<Enrichment>();
			for(Enrichment e:testResults) {
				permutationEnrichments.add(e.clone());
			}
			
			Random random = new Random(System.currentTimeMillis());
			
			int nInterestingGenes=interestingGenes.size();
			int nAllGenes=allGenes.size();
			
			Set<String> interestingGenesPerm = new TreeSet<String>();
			Set<Integer> numbers = new TreeSet<Integer>();
			Map<Integer,String> probesMap = new TreeMap<Integer,String>();
			int number=0;
			if(select.getSelectedIndex()==0) {
				for(String probeName: probesMasterTable.toMap().keySet()) {
					probesMap.put(number, probeName);
					number++;
				}
			}
			else {
				for(Probe p: probes.getAllProbes()) {
					probesMap.put(number, p.getName());
					number++;
				}
			}
			
			for(int permutation=0;permutation!=permutations;permutation++) {
				interestingGenesPerm.clear();
				numbers.clear();
				while(numbers.size()<nInterestingGenes) {
					int randomNumber=random.nextInt(nAllGenes);
					boolean added=numbers.add(randomNumber);
					if(added) {
						interestingGenesPerm.add(probesMap.get(randomNumber));
					}
				}
				int column=0;
				for(Enrichment e:permutationEnrichments) {
					calculateTestStatistic(interestingGenesPerm,allGenes.size(),e,e.getGeneset().getGenes());
					permutationPValues.setValue(permutation, column, e.getPValue());
					column++;
				}
				
			}
			
			correctedValues = correctionPluginPermutations.correct(pvalues, permutationPValues);
		}
		
		Iterator<Enrichment> resultIterator = testResults.iterator();
		for(Double val : correctedValues) {
			Enrichment currentEnrichment = resultIterator.next();
			currentEnrichment.setPValue(val);
		}
		if(selectCorrection.getSelectedIndex()==1) {
			if(select.getSelectedIndex()==2) {
				progress+= 1000 / ((double)Binomial.binomial(m3_classes.getModel().getNumClasses(),2));
			}
			else {
				progress+= 1000;
			}
			t.setProgress((int)progress);
		}
		
	}
	
	
	
	protected void singleGenesTest(ClassSelectionModel csm) {
		StatTestResult t = m3_testmethod.getInstance().runTest(probes.toCollection(), csm);
		interestingGenes = new TreeSet<String>();
		
		for(int i=0;i!=probes.getNumberOfProbes();i++) {
			Probe p = probes.getProbe(i);
			double value = ((DoubleMIO)t.getPValues().getMIO(p)).getValue();
			if(value<=m2_or_m3_threshold.getDoubleValue()) {
				interestingGenes.add(p.getName());
			}
		}
		System.out.println(interestingGenes.size());
		System.out.println(allGenes.size());
		
	}

	@Override
	protected List<Setting> additionalSettings() {
		ClassSelectionModel csm = new ClassSelectionModel(dataSet.getMasterTable());
		m2_or_m3_threshold = new DoubleSetting("Threshold",
				"Select threshold of p-Value for significant genes. \nGenes with p<threshold are considered significant.", 0.05,0.0,1.0,false,false);
		
		HierarchicalSetting method1 = new HierarchicalSetting("Method 1: Significant genes in selected ProbeList, MasterTable is list of all genes");

		m2_pvalues = new MIGroupSetting("p-value annotation","Select which p-values to use for ORA",null,dataSet.getMIManager(),false);
		m2_pvalues.setAcceptableClass(DoubleMIO.class);
		HierarchicalSetting method2 = new HierarchicalSetting("Method 2: Significant genes are determined from given p-Values")
			.addSetting(m2_pvalues)
			.addSetting(m2_or_m3_threshold);		
		
		m3_classes = new ClassSelectionSetting("Experiment classes",
				"Define the phenotypes of the experiments", csm, 2,null, dataSet);
		m3_testmethod = new PluginTypeSetting<StatTestPlugin>("Statistical Test for single genes",null, new TTestPlugin(), StatTestPlugin.MC);
		HierarchicalSetting method3 = new HierarchicalSetting("Method 3: Significant genes are determined from results of the statistical test")
			.addSetting(m3_classes)
			.addSetting(m3_testmethod)
			.addSetting(m2_or_m3_threshold); 
		select=new SelectableHierarchicalSetting("Select method","ORA can be computed from several types of input. Select one.",2,new Setting[]{method1,method2,method3}).setLayoutStyle(SelectableHierarchicalSetting.LayoutStyle.COMBOBOX);;
		
		correctionMethodSetting = new PCorrectionMethodSetting("Method for p-value correction", null, new Bonferroni());
		correctionMethodSettingPerm = new PluginTypeSetting<PValueCorrectionWithPermutationsPlugin>(
				"Method for p-value correction with permutations", null, new PermutedFDR(),PValueCorrectionWithPermutationsPlugin.MC);
		nPermutations=new IntSetting("Select number of permutations", null, 100);
		HierarchicalSetting correctionWithoutPermutations = new HierarchicalSetting("Method without permutations")
			.addSetting(correctionMethodSetting);
		HierarchicalSetting correctionWithPermutations = new HierarchicalSetting("Method with permutations")
			.addSetting(correctionMethodSettingPerm)
			.addSetting(nPermutations);
		
		selectCorrection=new SelectableHierarchicalSetting("p-value correction",null,0,new Setting[]{correctionWithoutPermutations,correctionWithPermutations}).setLayoutStyle(SelectableHierarchicalSetting.LayoutStyle.COMBOBOX);;
		
		overreptest = new ObjectSelectionSetting<GenesetTest>("Overrepresentation Test", 
				"Select test for overrepresentation",0,
				new GenesetTest[]{new HypergeometricTest(),new BinomialTest(),new ChiSquareTest(),new FisherTest()});
		
		List<Setting> settings = new LinkedList<Setting>();
		settings.add(select);
		settings.add(overreptest);
		settings.add(selectCorrection);
		return settings;
		
	}

	
	@Override
	public void calculateEnrichment() {
		List<Enrichment> enrichmentResults=null;
		if(select.getSelectedIndex()==0) {
			allGenes = new TreeSet<String>(); 
			for(Probe p: probesMasterTable.toCollection()) {
				allGenes.add(p.getName());
			}
			interestingGenes = new TreeSet<String>();
			for(Probe p: probes.getAllProbes()) {
				interestingGenes.add(p.getName());
			}
			System.out.println(allGenes.size());
			System.out.println(interestingGenes.size());
			enrichmentResults = calculateTestStatistics();
			correction(enrichmentResults, selectCorrection.getSelectedIndex(), nPermutations.getIntValue(), correctionMethodSetting.getInstance(),correctionMethodSettingPerm.getInstance());
			result.addEnrichments(enrichmentResults);
		}
		else if(select.getSelectedIndex()==1) {
			allGenes = new TreeSet<String>(); 
			interestingGenes = new TreeSet<String>();
			double threshold = m2_or_m3_threshold.getDoubleValue();
			MIGroup mi = m2_pvalues.getMIGroup();
			for(Probe p: probes.getAllProbes()) {
				allGenes.add(p.getName());
				if(((DoubleMIO)mi.getMIO(p)).getValue()<=threshold) {
					interestingGenes.add(p.getName());
				}
			}
			System.out.println(interestingGenes.size());
			System.out.println(allGenes.size());
			enrichmentResults = calculateTestStatistics();
			correction(enrichmentResults, selectCorrection.getSelectedIndex(), nPermutations.getIntValue(), correctionMethodSetting.getInstance(),correctionMethodSettingPerm.getInstance());
			result.addEnrichments(enrichmentResults);
		}
		else if(select.getSelectedIndex()==2) {
			allGenes = new TreeSet<String>(); 
			for(Probe p: probes.getAllProbes()) {
				allGenes.add(p.getName());
			}
			enrichmentWithClasses(m3_classes.getModel());
		}
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.GeneSetAnalysis.ORA",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Roland Keller",
				"no e-mail provided",
				"Over Representation Analysis is the simplest method for enrichment analysis",
				"Over-representation analysis (ORA)");
		pli.addCategory("Geneset Analysis");
		pli.getProperties().put(Constants.NO_CACHE_CLASS_INSTANCE, true);
		return pli;
	}
	
	
	protected void calculateTestStatistic(Set<String> interestingGenes,int numberOfAllGenes, Enrichment e, Set<String> genesInSet) {
		int interestingGenesInSet=0;
		int notInterestingGenesInSet=0;
		
		for(String gene:genesInSet) {
			if(interestingGenes.contains(gene)) {
				interestingGenesInSet++;
			}
			else if(!interestingGenes.contains(gene) && allGenes.contains(gene)) {
				notInterestingGenesInSet++;
			}
		}
		int n11=interestingGenesInSet;
		int n12=notInterestingGenesInSet;
		int n21=interestingGenes.size()-n11;
		int n22=numberOfAllGenes-interestingGenes.size()-n12;
		GenesetTest test = overreptest.getObjectValue();
		test.runTest(n11,n12,n21,n22,e);	
		if(selectCorrection.getSelectedIndex()==0) {
			if(select.getSelectedIndex()==2) {
				progress+= 10000 / ((double)Binomial.binomial(m3_classes.getModel().getNumClasses(),2) * genesets.size());
			}
			else {
				progress+= 10000/ (double)genesets.size();
			}
			t.setProgress((int)progress);
		}
		else {
			if(select.getSelectedIndex()==2) {
				progress+= 9000 / ((double)Binomial.binomial(m3_classes.getModel().getNumClasses(),2) * genesets.size()*(nPermutations.getIntValue()+1));
			}
			else {
				progress+= 9000/ ((double)genesets.size()*(nPermutations.getIntValue()+1));
			}
			t.setProgress((int)progress);
		}
	}
	
	protected List<Enrichment> calculateTestStatistics() {
		List<Enrichment> testResults = new LinkedList<Enrichment>();
		for(Geneset g: genesets) {
			Enrichment e = new Enrichment(g);
			calculateTestStatistic(interestingGenes,allGenes.size(),e,g.getGenes());
			testResults.add(e);
		}
		return testResults;
		
	}


	@Override
	protected List<Enrichment> calculateEnrichmentWithClasses(
			ClassSelectionModel csm) {
		singleGenesTest(csm);
		List<Enrichment> enrichmentResults = calculateTestStatistics();
		correction(enrichmentResults, selectCorrection.getSelectedIndex(), nPermutations.getIntValue(), correctionMethodSetting.getInstance(),correctionMethodSettingPerm.getInstance());
		return enrichmentResults;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	protected String additionalPreferences() {
		String preferences = "Over Representation Analysis with " + overreptest.getStringValue() +"<p/>";
		if(select.getSelectedIndex()!=0) {
			preferences+= "Threshold: " + m2_or_m3_threshold.getDoubleValue() + "<p/>";
		}
		if(select.getSelectedIndex()==1) {
			preferences+="MIGroup for p-values " + m2_pvalues.getMIGroup().getName() + "<p/>";
		}
		if(select.getSelectedIndex()==2) {
			preferences+= "Statistic for single genes: " + m3_testmethod.getPluginInfo().getName() +"<p/>";
			preferences+= "ClassSelectionModel: " + m3_classes.getModel().toString(true)+ "<p/>";
		}
		
		if(selectCorrection.getSelectedIndex()==0) {
			preferences+= "Method for p-value correction: " + correctionMethodSetting.getPluginInfo().getName()+ "<p/>";
		}
		else {
			preferences+= "Method for p-value correction: " + correctionMethodSettingPerm.getPluginInfo().getName()+ "<p/>";
			preferences+= "Number of permutations: " + nPermutations.getIntValue()+ "<p/>";
		}
		return preferences;
	}
	
}
