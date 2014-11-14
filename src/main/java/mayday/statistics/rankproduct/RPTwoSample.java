package mayday.statistics.rankproduct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import mayday.core.ClassSelectionModel;
import mayday.core.math.stattest.CorrectedStatTestResult;
import mayday.core.math.stattest.StatTestPlugin;
import mayday.core.math.stattest.StatTestResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.core.tasks.AbstractTask;

public class RPTwoSample extends StatTestPlugin {

	protected RPTwoSampleSetting setting;

	private Random random = new Random();

	public Map<Object, double[]> thevals;

	public RPTwoSample(){
		setting = new RPTwoSampleSetting("Rank Product (two-sample)");		
	}

	@Override
	public Setting getSetting() {
		return setting;
	}

	@Override
	public StatTestResult runTest(Map<Object, double[]> values, final ClassSelectionModel classes) {


		if(setting.isPaired()){

			AbstractTask task = new AbstractTask("Check user input validity") {
				@Override
				protected void initialize() {
				}

				@Override
				protected void doWork() throws Exception {
					List<String> classLabels = classes.getClassesLabels();
					if(classes.toIndexList(classLabels.get(0)).size() != classes.toIndexList(classLabels.get(1)).size()){
						throw new Exception("To perform a test on paired values, the classes must be equal in size.");
					}
				}
			};
			task.start();
		}	

		//******************************************* prepare data ************************************************//
		int permutations 	= setting.getPermutationCount();
		double _totalRuns_ 	= permutations+1;
		boolean isLog 		= setting.isLogged();
		boolean paired 		= setting.isPaired();
		
		CorrectedStatTestResult res = new CorrectedStatTestResult();

		// transform the data into a form that is much easier to work with for RP
		Map<Object, Integer> indexMap = new TreeMap<Object, Integer>();		
		PermutableMatrix allInputData = Algebra.matrixFromMap(values, indexMap);			
		Integer[][] index = classIndices(classes);
		
		PermutableMatrix data1 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[0]));	// data of class 1
		PermutableMatrix data2 = allInputData.submatrix(null, Algebra.<int[]>createNativeArray(index[1]));	// data of class 2

		int num_gene = values.size();	
		
		
		//********************************* rank product of original data ****************************************//
		RPTools tools = new RPTools();
		
		//RP of genes up-regulated in class 2
		DoubleVector RP_ori_upin2 = tools.computeRankProduct(data1, data2, 2, isLog, false, paired);
		//rank of RP of genes up-regulated in class 2
		DoubleVector rank_ori_upin2 = RP_ori_upin2.rank();
		
		//RP of genes down-regulated in class 2
		DoubleVector RP_ori_downin2 = tools.computeRankProduct(data1, data2, 2, isLog, true, paired);
		DoubleVector rank_ori_downin2 = RP_ori_downin2.rank(); 

		setProgress(100.0/_totalRuns_);

		//		System.out.println("Starting "+permutations+" permutations");

		DoubleVector temp2_up 	= rank_ori_upin2.clone();
		DoubleVector temp2_down = rank_ori_downin2.clone();

		
		//****************************** perform permutations of expression values *******************************//
		
		for (int p=1; p<=permutations; ++p) {
			//perform permutation on data1 and data2

			DoubleMatrix[] temp_data = tools.generateNewData( data1, data2, 2 );
		
			//compute rank product of permuted data
			DoubleVector RP_per_upin2_oneCol 	= tools.computeRankProduct( temp_data[0], temp_data[1], 2, isLog, false, paired);
			DoubleVector RP_per_downin2_oneCol 	= tools.computeRankProduct( temp_data[0], temp_data[1], 2, isLog, true, paired);
			
			tools.updateRank(temp2_up, RP_ori_upin2, RP_per_upin2_oneCol);
			tools.updateRank(temp2_down, RP_ori_downin2, RP_per_downin2_oneCol);
			
			setProgress((double)p*10000.0/_totalRuns_);
		}

		// vector containing stats for up-regulated genes, position 0 -> p-value; position 1 -> pfp-value
		DoubleVector[] upStat = tools.computePFP( temp2_up, rank_ori_upin2, num_gene, permutations );
		// vector containing stats for down-regulated genes
		DoubleVector[] downStat = tools.computePFP( temp2_down, rank_ori_downin2, num_gene, permutations );
		
		// **************************** prepare statistical values **********************************************//
		
		// Store pval, pfp, RP as MIO in results
		MIGroup overallP 	= 	res.getPValues();
		MIGroup pval_up 	= 	res.addAdditionalValue("pval up");
		MIGroup pval_down 	= 	res.addAdditionalValue("pval down");
		MIGroup pfp_up 		= 	res.addAdditionalValue("pfp up");
		MIGroup pfp_down 	= 	res.addAdditionalValue("pfp down");
		MIGroup RP_up 		= 	res.addAdditionalValue("RP up");
		MIGroup RP_down 	= 	res.addAdditionalValue("RP down");

		int pvalSourceIndex = setting.returnPFP()?1:0;

		//add information to MI-objects
		for (Object o : values.keySet()) {
			int i = indexMap.get(o);
			
			pval_up.add(o, new DoubleMIO(upStat[0].get(i)));
			pval_down.add(o, new DoubleMIO(downStat[0].get(i)));

			pfp_up.add(o, new DoubleMIO(upStat[1].get(i)));
			pfp_down.add(o, new DoubleMIO(downStat[1].get(i)));

			RP_up.add(o, new DoubleMIO(RP_ori_upin2.get(i)));
			RP_down.add(o, new DoubleMIO(RP_ori_downin2.get(i)));

			overallP.add(o, new DoubleMIO(Math.min( upStat[pvalSourceIndex].get(i), downStat[pvalSourceIndex].get(i) )));
		}

		return res;

	}

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.statistics.RankProd_paired",
				new String[0],
				StatTestPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Calculate RP-scores and corrected p-values",
				"Rank Product_paired"
				);
		//		DoubleVector or = new DoubleVector(new double[]{11 , 78 , 31 , 88 , 28 , 69 , 33 , 26 , 34 , 4 , 57 , 13 , 63 , 89 , 72 , 42 , 54 , 83 , 43 , 73 , 62 , 30 , 80 , 46 , 35 , 64 , 81 , 92 , 3 , 85 , 93 , 84 , 76 , 47 , 12 , 70 , 36 , 56 , 100 , 86 , 9 , 29 , 45 , 65 , 27 , 41 , 21 , 94 , 7 , 8 , 14 , 25 , 39 , 61 , 67 , 38 , 79 , 48 , 95 , 97 , 16 , 20 , 55 , 51 , 19 , 24 , 66 , 52 , 53 , 10 , 99 , 98 , 68 , 2 , 22 , 32 , 6 , 37 , 71 , 23 , 59 , 74 , 17 , 5 , 18 , 75 , 60 , 90 , 96 , 77 , 87 , 82 , 50 , 91 , 58 , 1 , 15 , 44,49,40 });
		//		DoubleVector nc = new DoubleVector(new double[]{Double.NaN , Double.NaN , Double.NaN , Double.NaN , Double.NaN , Double.NaN , Double.NaN , Double.NaN , Double.NaN , Double.NaN , 56 , 70 , 74 , 57 , 60 , 92 , 47 , 69 , 58 , 23 , 63 , 5 , 41 , 82 , 18 , 19 , 32 , 93 , 62 , 68 , 40 , 45 , 21 , 9 , 72 , 24 , 88 , 51 , 31 , 61 , 97 , 16 , 46 , 25 , 12 , 65 , 7 , 79 , 64 , 80 , 8 , 98 , 99 , 28 , 86 , 50 , 33 , 66 , 42 , 10 , 53 , 52 , 22 , 34 , 39 , 29 , 20 , 95 , 49 , 96 , 84 , 71 , 3 , 90 , 78 , 1 , 26 , 73 , 59 , 38 , 11 , 14 , 17 , 77 , 48 , 67 , 4 , 6 , 54 , 36 , 87 , 76 , 81 , 91 , 35 , 13 , 75 , 85 , 55 , 100});
		//		DoubleVector c1 = new DoubleVector(new double[]{76 , 79 , 74 , 94 , 86 , 2 , 3 , 77 , 55 , 10 , 12 , 39 , 63 , 95 , 60 , 7 , 66 , 82 , 71 , 22 , 92 , 89 , 91 , 81 , 4 , 52 , 85 , 43 , 75 , 59 , 56 , 24 , 11 , 36 , 57 , 26 , 31 , 97 , 16 , 53 , 40 , 47 , 58 , 51 , 68 , 34 , 61 , 23 , 15 , 35 , 50 , 88 , 98 , 87 , 49 , 13 , 17 , 14 , 80 , 90 , 84 , 99 , 30 , 72 , 70 , 54 , 20 , 46 , 18 , 96 , 28 , 41 , 44 , 62 , 21 , 1 , 69 , 73 , 32 , 93 , 67 , 8 , 83 , 42 , 6 , 38 , 48 , 100 , 9 , 29 , 33 , 37 , 27 , 25 , 78 , 19 , 5 , 64 , 45 , 65});
		//		
		//		c1.set(Double.NaN,0,9);
		//		
		//		updateRank(or, c1, nc);
		//		System.out.println(or);
		//		
		//		DoubleVector ur = new DoubleVector(new double[]{101 , 168 , 121 , 178 , 118 , 159 , 123 , 116 , 124 , 94 , 67 , 46 , 118 , 173 , 124 , 47 , 112 , 157 , 106 , 92 , 144 , 110 , 161 , 119 , 37 , 108 , 157 , 129 , 70 , 136 , 141 , 105 , 85 , 78 , 61 , 93 , 62 , 142 , 113 , 131 , 43 , 68 , 95 , 108 , 87 , 70 , 74 , 114 , 20 , 38 , 56 , 104 , 126 , 139 , 108 , 49 , 93 , 60 , 167 , 177 , 91 , 108 , 81 , 115 , 81 , 70 , 83 , 90 , 68 , 95 , 123 , 133 , 105 , 56 , 40 , 32 , 67 , 102 , 98 , 106 , 118 , 80 , 92 , 41 , 22 , 107 , 100 , 179 , 103 , 102 , 115 , 114 , 74 , 113 , 128 , 17 , 18 , 100 , 86 , 97 });
		//		System.out.println(ur);
		//		System.out.println(or);
		//		System.out.println("Result as expected? "+ur.allValuesEqual(or));
		pli.addCategory("");
		return pli;
	}

}
