package mayday.wapiti.transformations.impl.inter_norm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.ExtendableObjectSelectionSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.core.structures.linalg.matrix.VectorBasedMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.AbstractFeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.channels.BackgroundType;
import mayday.wapiti.experiments.properties.channels.ChannelCount;
import mayday.wapiti.experiments.properties.datamode.Logged;
import mayday.wapiti.experiments.properties.processing.Normalized;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;
import mayday.wapiti.transformations.impl.rma.qn.QNorm;

public class RGQuantile extends AbstractTransformationPlugin {

	protected ExtendableObjectSelectionSetting<String> dominantChannel;
	
	public RGQuantile() {}

	public boolean applicableTo(Collection<Experiment> exps) {
		// dimensions must be the same, we will ignore names
		long noe=-1;
		
		for (Experiment e:exps) {
			boolean isFEE = FeatureExpressionData.class.isAssignableFrom(e.getDataClass());
			boolean multiChannel = ChannelCount.isMultiChannel(e.getDataProperties());
			if (isFEE && multiChannel) {
				if (noe==-1)
					noe = e.getNumberOfFeatures();
				else
					if (noe!=e.getNumberOfFeatures())
						return false;
			} else 
				return false;
		}	
		
		return true;
	}
	
	@Override
	public void updateSettings(Collection<Experiment> experiments) {
		Experiment e = experiments.iterator().next();
		ChannelCount c = (ChannelCount)e.getDataProperties().getType(ChannelCount.class);
		if (c!=null) {
			String[] channels = c.getNames();			
			dominantChannel.updatePredefined(channels);
		}
	}
	
	public String getApplicabilityRequirements() {
		return "Requires dual-channel feature expression data. All input experiments must have the same number of features.";
	}

	public Setting getSetting() {
		if (dominantChannel==null) {
			dominantChannel = new ExtendableObjectSelectionSetting<String>("Quantile-normalized channel",
					"Select the dominant (reference) channel. The second channel will be normalized based on this one.",0,new String[]{""});
			dominantChannel.setLayoutStyle(ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS);
			
		}
		return dominantChannel;
	}
	
	
	public void compute() {

		List<Experiment> exps = transMatrix.getExperiments(this);
		
		// variable names are chosen to represent RQuantile
		
		ArrayList<AbstractVector> temp_R_columns = new ArrayList<AbstractVector>(exps.size());
		
		int channel = dominantChannel.getSelectedIndex();

		for (Experiment e : exps) {
			ExperimentState es = transMatrix.getInputState(this, e);
			boolean logged = Logged.isLogged(es.getDataProperties());

			ExperimentData ex = transMatrix.getIntermediateData(e);
			
			AbstractVector r_col = ((FeatureExpressionData)ex).getExpressionVector(channel);
			
			if (!logged) {
				r_col = r_col.clone();
				r_col.log(2.0);				
			}
			temp_R_columns.add(r_col);
			
		}
		
		VectorBasedMatrix temp_R = new VectorBasedMatrix(temp_R_columns, false);
		
		AbstractMatrix new_R = QNorm.qnormC(temp_R);
		
		for (int i=0; i!=exps.size(); ++i) {
			ExperimentState es = transMatrix.getInputState(this, exps.get(i));
			boolean logged = Logged.isLogged(es.getDataProperties());
			
			FeatureExpressionData dta = ((FeatureExpressionData)transMatrix.getIntermediateData(exps.get(i)));
			
			AbstractVector oldR = temp_R_columns.get(i);
			AbstractVector oldG = dta.getExpressionVector(1-channel);
			if (!logged) {
				oldG = oldG.clone();
				oldG.log(2.0);
			}
			
			// M = R-G  (in log2)
			AbstractVector m_col = oldR.clone();
			m_col.subtract(oldG);
			
			AbstractVector newR = new_R.getColumn(i);
			newR.setNames(temp_R_columns.get(i));
			
			// M = R-G --> G'=R'-M
			
			AbstractVector newG = m_col;
			newG.multiply(-1.0d);
			newG.add(newR);
			
			String n1 = dta.getChannelName(channel)+" (reference, quantile normalized)";
			String n2 = dta.getChannelName(1-channel);
			
			String[] names;
			AbstractVector[] output;
			if (channel==0) {
				names = new String[]{n1, n2};
				output = new AbstractVector[]{newR,newG};
			} else {
				names = new String[]{n2, n1};
				output = new AbstractVector[]{newG,newR};
			}
			
			ExperimentData ed =  new AbstractFeatureExpressionData(null, null, output,null, names);
			transMatrix.setIntermediateData(exps.get(i),ed);
		}
	}
	
	protected ExperimentState makeState(ExperimentState inputState) {
		return new AbstractExperimentState(inputState) {
			public DataProperties getDataProperties() {
				getSetting();
				DataProperties in = inputState.getDataProperties();
				DataProperties out = in.clone();
				out.add(new Normalized(), true);
				out.add(new Logged(2.0), true);
				BackgroundType bt = in.getType(BackgroundType.class);
				if (bt!=null)
					out.remove(bt);
				ChannelCount c = in.getType(ChannelCount.class);
				String[] names = c.getNames().clone();
				for (int i=0; i!=names.length;++i)
					if (i==dominantChannel.getSelectedIndex())
						names[i] = names[i]+" (reference channel)";
				c = new ChannelCount(c.getCount(), names);
				out.add(c, true);				
				return out;
			}
		};
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".RGnorm", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Applies Quantile Normalization based on a reference channel", 
		"Reference Channel Quantile Normalization (RQuantile or GQuantile)");
	}
	
	public String getIdentifier() {
		return "Ref.QNorm";
	}

}
