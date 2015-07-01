package mayday.vis3d.plots.pcaplot;

import java.util.Collection;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.math.JamaSubset.Matrix;
import mayday.core.math.JamaSubset.PCA;

/**
 * This is a modified version of the PCAComputer written by Florian Battke
 * @author G\u00FCnter J\u00E4ger
 * @date Jun 10, 2010
 */
public class PCA3DComputer {
	
	/**
	 * an instance of PCAPlot3DPanel for call backs
	 */
	public PCAPlot3DPanel callBack;

	/**
	 * @param pcaPanel
	 */
	public PCA3DComputer(PCAPlot3DPanel pcaPanel) {
		callBack = pcaPanel;
	}
	/**
	 * perform a principal component analysis
	 * directly updates values in the provided callBack object
	 */
	public void startCalculation() {
		try {
			Object[] temp = doPCA(callBack.viewModel.getProbes(),
					callBack.viewModel.getDataSet().getMasterTable());
			callBack.PCAData = (Matrix) temp[0];
			callBack.EigenValues = (double[]) temp[1];
		} catch (Throwable e) {
			System.err.println("PCA Computation failed. The reason was a "
					+ e.getClass().getCanonicalName());
			e.printStackTrace();
			callBack.PCAData = null;
		}
		callBack.updateWithPCAResult(PCA3DComputer.this);
	}
	
	/**
	 * @param probeList
	 * @param masterTable
	 * @return array of matrices
	 */
	public Object[] doPCA(Collection<Probe> probeList, MasterTable masterTable) {
		int n = probeList.size();
		int m = masterTable.getNumberOfExperiments();

		double[][] indat;
		if (callBack.transpose_first)
			indat = new double[m][n];
		else
			indat = new double[n][m];

		try {
			if (callBack.transpose_first) {
				int i = 0;
				for (Probe tmp : probeList) {
					for (int j = 0; j != m; ++j) {
						indat[j][i] = tmp.getValue(j);
					}
					++i;
				}
			} else {
				int i = 0;
				for (Probe tmp : probeList) {
					for (int j = 0; j != m; ++j) {
						indat[i][j] = tmp.getValue(j);
					}
					++i;
				}
			}
		} catch (NullPointerException e) {
			throw new RuntimeException(
					"Cannot work on Probes containing missing values");
		}

		PCA pca = new PCA(indat);

		return new Object[] { pca.getResult(), pca.getEigenValues() };
	}
}
