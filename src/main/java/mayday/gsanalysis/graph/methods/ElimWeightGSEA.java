package mayday.gsanalysis.graph.methods;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;

import mayday.core.ClassSelectionModel;
import mayday.core.math.Binomial;
import mayday.core.math.PNorm;
import mayday.core.math.Statistics;
import mayday.core.math.pcorrection.methods.Bonferroni;
import mayday.core.math.scoring.ScoringPlugin;
import mayday.core.math.scoring.ScoringResult;
import mayday.core.math.scoring.TestPlugin;
import mayday.core.math.stattest.StatTestPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.PluginTypeSetting;
import mayday.core.settings.methods.PCorrectionMethodSetting;
import mayday.core.settings.typed.ClassSelectionSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Node;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.genemining2.methods.SignalToNoisePlugin;
import mayday.gsanalysis.Enrichment;
import mayday.gsanalysis.Geneset;
import mayday.gsanalysis.graph.GraphEnrichmentMethod;
import mayday.gsanalysis.gsea.GSEAPlugin;
import mayday.gsanalysis.gsea.SimpleGSEAEnrichment;

public class ElimWeightGSEA extends ElimWeight{
	protected final String name="Elim-Weight (with full GSEA)";
	protected PluginTypeSetting<TestPlugin<ScoringResult>> testSingleGenesSetting;
	protected RestrictedStringSetting genesetScoringSetting;
	protected GSEAPlugin gseaPlugin;
	protected DoubleMatrix geneList;
	protected double mean;
	protected double sd;
	protected boolean noZ;
	protected boolean noScale;
	
	@Override
	public void calculateEnrichment() {
		gseaPlugin = new GSEAPlugin();
		
		noZ=false;
		noScale=false;
		if(genesetScoringSetting.getSelectedIndex()==1) {
			noScale=true;
		}
		if(genesetScoringSetting.getSelectedIndex()==2) {
			noZ=true;
		}
		enrichmentWithClasses(m3_classes.getModel());
	}

	public ElimWeightGSEA() {
		genesetSetting=false;
	}
	
	@Override
	protected List<Enrichment> calculateEnrichmentWithClasses(
			ClassSelectionModel csm) {

		geneList = new DoubleMatrix(probes.getNumberOfProbes(),2);
		gseaPlugin.createGeneList(probes,geneList,csm,false,testSingleGenesSetting.getInstance());
		mean = geneList.getColumn(0).mean();
		sd=geneList.getColumn(0).sd();
		List<Enrichment> results = calculateTestStatistics();
		gseaPlugin.correction(results,correctionMethodSetting.getInstance());
		
		return results;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.GraphEnrichmentMethods.ElimWeight(GSEA)",
				new String[0], 
				GraphEnrichmentMethod.MC,
				(HashMap<String,Object>)null,
				"Roland Keller",
				"no e-mail provided",
				"Plugin for ElimWeight (with GSEA)",
		"Elim-Weight (with full GSEA)");
		return pli;
	}

	@Override
	protected String additionalPreferences() {
		String preferences = methodSetting.getStringValue() + " (GSEA)<p/>";
		preferences+= "Statistic for single genes: " + testSingleGenesSetting.getPluginInfo().getName() +"<p/>";
		preferences+= "ClassSelectionModel: " + m3_classes.getModel().toString(true)+ "<p/>";
		preferences+= "Method for p-value correction: " + correctionMethodSetting.getPluginInfo().getName()+ "<p/>";
		return preferences;
	}

	@Override
	protected List<Setting> additionalSettings() {
		super.additionalSettings();
		List<Setting> settings = new LinkedList<Setting>();
		methodSetting = new RestrictedStringSetting("Elim or Weight?", null, 0, new String[] {"Elim","Weight"});
		settings.add(methodSetting);
		genesetScoringSetting = new RestrictedStringSetting("Scoring of genesets", null, 0, new String[] {"z and scale score combined","only z score", "only scale score"});
		settings.add(genesetScoringSetting);
		
		ClassSelectionModel csm = new ClassSelectionModel(dataSet.getMasterTable());
		m3_classes = new ClassSelectionSetting("Class selection","Determine phenotypes of the experiments", csm, 2,null,dataSet);
		Set<PluginInfo> plugins = new TreeSet<PluginInfo>();
		plugins.addAll(PluginManager.getInstance().getPluginsFor(StatTestPlugin.MC));
		plugins.addAll(PluginManager.getInstance().getPluginsFor(ScoringPlugin.MC));
		testSingleGenesSetting = new PluginTypeSetting<TestPlugin<ScoringResult>>("Statistical Test for single genes",null, new SignalToNoisePlugin(), plugins);
		correctionMethodSetting = new PCorrectionMethodSetting("Method for p-value correction", null, new Bonferroni());
		settings.add(m3_classes);
		settings.add(testSingleGenesSetting);
		settings.add(correctionMethodSetting);
		return settings;
	}
	
	protected Enrichment createEnrichment(Geneset currentGeneset) {
		return new SimpleGSEAEnrichment(currentGeneset);
	}

	@Override
	protected void calculateTestStatistic(Enrichment enr, Set<String> genesToTest) {
		gseaPlugin.calculateSimpleEnrichment((SimpleGSEAEnrichment) enr, geneList, mean, sd,genesToTest,noZ,noScale);
		progress+= 10000 / ((double)Binomial.binomial(m3_classes.getModel().getNumClasses(),2) * genesets.size());
		t.setProgress((int)progress);
	}

	@Override
	protected double weightedGenesetTest(Node n) {
		DoubleVector weights=weightsMap.get(n);
		ArrayList<Double> logs = new ArrayList<Double>();
		for(double weight:weights) {	
			logs.add(Math.log(Math.max(weight,0.0000001)));
		}
		
		double minLog = Collections.min(logs);
		double maxLog = Collections.max(logs);
		for(int i=0;i!=logs.size();i++) {
			logs.set(i,logs.get(i)-minLog+0.5*(maxLog-minLog+1));
			System.out.println(logs.get(i));
		}
		double meanLog=Statistics.mean(logs);
		
		double sumScores=0;
		for(int i=0;i!=weights.size();i++) {
			sumScores+=(((geneList.getValue(weights.getName(i), 0))-mean)/sd)*(logs.get(i)/meanLog);
		}
		
		SimpleGSEAEnrichment e=(SimpleGSEAEnrichment) enrichmentMap.get(n);
		int testSize = e.getGeneset().getGenes().size();
		double meanScoreGeneset=sumScores/testSize;
		double score = Math.sqrt(testSize)*meanScoreGeneset;
		
		if(noZ) {
			score=0;
		}
		e.setScore(score);
		
		//two-sided
		double zPValue = 2*PNorm.getDistribution(Math.abs(score), true);
		e.setZPValue(zPValue);
		
		double sum=0;
		for(int i=0;i!=weights.size();i++) {
			sum+=(((geneList.getValue(weights.getName(i), 0)-mean)/sd)*(logs.get(i)/meanLog)-meanScoreGeneset)*(((geneList.getValue(weights.getName(i), 0)-mean)/sd)*(logs.get(i)/meanLog)-meanScoreGeneset);
		}
		
		double scaleScore = (sum-testSize+1)/Math.sqrt(2*(testSize-1));
		if(noScale) {
			scaleScore=0;
		}
		
		double scalePValue=2*PNorm.getDistribution(Math.abs(scaleScore), true);
		
		e.setScaleScore(scaleScore);
		e.setScalePValue(scalePValue);
		e.setPValue(Math.min(scalePValue, zPValue));
		return e.getPValue();
	}

}
