package mayday.GWAS.data.ld;

import mayday.GWAS.data.SNPList;
import mayday.clustering.qt.algorithm.QTPSettings;
import mayday.clustering.qt.algorithm.clustering.QTPAdListCreatorThread;
import mayday.clustering.qt.algorithm.clustering.QTPClustering;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;

public class LDClustering extends QTPClustering {

	private LDResults ldResults;
	private SNPList snps;
	private double threshold;
	
	public LDClustering(LDResults ldResults, SNPList snps, double threshold) {
		super(new PermutableMatrix(){
			@Override
			protected String getDimName0(int dim, int index) {
				return null;
			}

			@Override
			protected void setDimName0(int dim, int index, String name) {}

			@Override
			protected double get0(int row, int col) {
				return 0;
			}

			@Override
			protected void set0(int row, int col, double value) {}

			@Override
			protected int dim0(int dimension) {
				return 0;
			}

			@Override
			protected AbstractVector getDimVec0(int dimension, int index) {
				return null;
			}
			
		}, new QTPSettings());
		rows_ClusterData = snps.size();
		
		settings.setEnableEnhancement(false);
		settings.setMinNumOfElem(2);
		
		this.ldResults = ldResults;
		this.snps = snps;
		this.threshold = threshold;
	}
	
	protected void createQTAdList() {
		this.qtAdList = new LDAdList(snps, ldResults, threshold);

		int coreCount = Runtime.getRuntime().availableProcessors()-1;
		
		int subCount = (int) Math.ceil(this.rows_ClusterData/coreCount);
		
		int currentThread = 0;
		
		for (int i=1; i<= coreCount; i++) {
			currentThread++;
			QTPAdListCreatorThread thread;
			if (i != coreCount) {
				thread = new QTPAdListCreatorThread(
						currentThread, 
						(i-1) * subCount, 
						i* subCount, 
						this);
			} else {
				thread = new QTPAdListCreatorThread(
						currentThread, 
						(i-1) * subCount, 
						this.rows_ClusterData, 
						this);
			}
			listOfThreads.add(thread);
			thread.execute();
		}
	}
}
