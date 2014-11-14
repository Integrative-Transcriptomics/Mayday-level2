package mayday.statistics.rankproduct;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import mayday.GWAS.data.meta.MetaInformationManager;
import mayday.core.ClassSelectionModel;
import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.scoring.TestPlugin;
import mayday.core.math.stattest.CorrectedStatTestResult;
import mayday.core.math.stattest.StatTestPlugin;
import mayday.core.math.stattest.StatTestResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.Settings;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.DoubleVector;
/**
 * Implementation of the one-sample Rank Product method.
 * @author Alicia Owen
 *
 */
public class RPOneSample extends AbstractPlugin implements ProbelistPlugin{

	/**
	 * the setting for the statistical test			
	 */
	RPOneSampleSetting setting;


	public RPOneSample(){
		setting = new RPOneSampleSetting("Rank Product (one-sample)");		
	}
	
	@Override
	public Setting getSetting() {
		return setting;
	}
	
	public List<ProbeList> run( List<ProbeList> probeLists, MasterTable masterTable )	{
		
		ProbeList uniqueProbes = ProbeList.createUniqueProbeList(probeLists);

		SettingDialog dialog = new SettingDialog(null, "One-sample rank product", setting);
		dialog.showAsInputDialog();
		
		if (dialog.canceled()){
			return null;
		}
			
		HashMap<Object, double[]> mappedProbes = new HashMap<Object,double[]>();
		
		for (Probe probe : uniqueProbes.getAllProbes()){
			mappedProbes.put(probe, probe.getValues());
		}
		
		//runs the one sample RP on the mapped probes and returns pvalues, pfp, ... 
		StatTestResult testResult = runTest(mappedProbes , null);

		MIManager mim = masterTable.getDataSet().getMIManager();
		
		//extract p-values
		int index_pValues = mim.addGroup(testResult.getPValues());
		
		for(MIGroup grp :  testResult.getAdditionalValues()){
			mim.addGroupBelow(grp, mim.getGroup(index_pValues));
		}
		
	
		if (setting.getReturnSignificantProbes().getBooleanValue()) {
			//create a new probe list of significant probes
			double threshold = setting.getFilterPValue().getDoubleValue();
			ProbeList probelist = new ProbeList(masterTable.getDataSet(), true);
			
			for (Probe pb : uniqueProbes) {
				MIType miType = testResult.getPValues().getMIO(pb);
				if (miType!=null) {
					if (((DoubleMIO)miType).getValue()<threshold)
						probelist.addProbe(pb);
				}
			}
			//give new probe list a name
			String pfp = setting.returnPFP()?"pfp":"p-value";
			probelist.setName("Significant probes "+ pfp +"  < "+setting.getFilterPValue().getDoubleValue());
		
			return Arrays.asList(new ProbeList[]{probelist});
		}
		return null;
	}

	public StatTestResult runTest(Map<Object, double[]> values, ClassSelectionModel classes) {
	
		System.out.println("stat test is running");
		int permutations 	= setting.getPermutationCount();
		double _totalRuns_ 	= permutations+1;
		boolean logged 		= setting.isLogged();

		CorrectedStatTestResult res = new CorrectedStatTestResult();

		// transform the data into a form that is much easier to work with for RP
		Map<Object, Integer> indexMap 	= new TreeMap<Object, Integer>();	
		
		PermutableMatrix foldChanges 	= Algebra.matrixFromMap(values, indexMap);

		int num_gene = values.size();
		RPTools tools = new RPTools();

		//********************************** compute rank product ********************************************/

		DoubleVector rankProd_up 	= 	tools.computeRankProduct(foldChanges, null, 1, true, true, false);
		DoubleVector ranks_up 		= 	rankProd_up.rank();
		DoubleVector rankProd_down 	= 	tools.computeRankProduct(foldChanges, null, 1, true, false, false);
		DoubleVector ranks_down 	= 	rankProd_down.rank();

		DoubleVector temp2_up 	= ranks_up.clone();		// a copy of the rank vector that will be manipulated for pfp-value computation
		DoubleVector temp2_down = ranks_down.clone();


		//****************************** perform permutations of expression values *******************************//

		for (int p=1; p<=permutations; ++p) {
			//perform permutation on data1 and data2
			DoubleMatrix[] temp_data = tools.generateNewData( foldChanges, null , 1);

			//compute rank product of permuted data
			DoubleVector RP_per_upin2_oneCol 	= tools.computeRankProduct(temp_data[0], null, 1, logged, false, false);
			DoubleVector RP_per_downin2_oneCol 	= tools.computeRankProduct(temp_data[0], null, 1, logged, true, false);

			tools.updateRank( temp2_up, rankProd_up, RP_per_upin2_oneCol);
			tools.updateRank( temp2_down, rankProd_down, RP_per_downin2_oneCol);			

		}

		// vector containing stats for up-regulated genes, position 0 -> p-value; position 1 -> pfp-value
		DoubleVector[] upStat 	= tools.computePFP( temp2_up, ranks_up, num_gene, permutations );
		// vector containing stats for down-regulated genes
		DoubleVector[] downStat = tools.computePFP( temp2_down, ranks_down, num_gene, permutations );


		// **************************** prepare statistical values **********************************************//

		// Store pval, pfp, RP as MIOs in results
		MIGroup overallP 	= res.getPValues();
		MIGroup pval_up 	= res.addAdditionalValue("pval up");
		MIGroup pval_down 	= res.addAdditionalValue("pval down");
		MIGroup pfp_up 		= res.addAdditionalValue("pfp up");
		MIGroup pfp_down 	= res.addAdditionalValue("pfp down");
		MIGroup RP_up 		= res.addAdditionalValue("RP up");
		MIGroup RP_down 	= res.addAdditionalValue("RP down");

		int pvalSourceIndex = setting.returnPFP()?1:0;

		//add information to MI-objects
		for (Object o : values.keySet()) {
			int i = indexMap.get(o);

			pval_up.add(o, new DoubleMIO(upStat[0].get(i)));
			pval_down.add(o, new DoubleMIO(downStat[0].get(i)));

			pfp_up.add(o, new DoubleMIO(upStat[1].get(i)));
			pfp_down.add(o, new DoubleMIO(downStat[1].get(i)));

			RP_up.add(o, new DoubleMIO(rankProd_up.get(i)));
			RP_down.add(o, new DoubleMIO(rankProd_down.get(i)));

			overallP.add( o, new DoubleMIO(Math.min( upStat[pvalSourceIndex].get(i), downStat[pvalSourceIndex].get(i) )));
		}

		return res;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.statistics.RankProd_oneSample",
				new String[0],
				Constants.MC_PROBELIST,
				new HashMap<String, Object>(),
				"Alicia Owen",
				"alicia.owen@student.uni-tuebingen.de",
				"Calculate RP-scores and corrected p-values",
				"Rank Product (one Sample)"
				);
		pli.addCategory(MaydayDefaults.Plugins.SUBCATEGORY_STATISTICS);
		return pli;

	}

	@Override
	public void init() {

	}

}
