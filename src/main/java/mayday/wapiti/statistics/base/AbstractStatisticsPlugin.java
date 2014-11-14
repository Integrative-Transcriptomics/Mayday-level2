package mayday.wapiti.statistics.base;

import java.util.List;

import mayday.core.gui.PreferencePane;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;
import mayday.wapiti.transformations.matrix.TransMatrix;

/** Statistics are special transformations which do not change the experiment state (upon being added) or the 
 * experiment data (upon execution), but compute some statistics and output them appropriately.
 * @author battke
 */
public abstract class AbstractStatisticsPlugin extends AbstractTransformationPlugin {

//	public final static String MC = Constants.MCBASE+"Statistic";
	
	public AbstractStatisticsPlugin() {
		// empty for pluma
	}

	// this is where the work is done
	public abstract void computeStatistics(TransMatrix transMatrix, List<Experiment> exps);
	
	
	public void init() {}

	public PreferencePane getPreferencesPanel() {
		// do not export settings as preferences
        return null;
    }
	
	@Override
	public final void compute() {
		computeStatistics(transMatrix, transMatrix.getExperiments(this));		
		// do not store new intermediate data in transmatrix
	}

	@Override
	protected final ExperimentState makeState(ExperimentState inputState) {
		return inputState; // statistics may not change input state
	}
	
}
