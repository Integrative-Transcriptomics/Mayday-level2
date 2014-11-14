package mayday.wapiti.transformations.impl.intra_norm;

import java.util.Collection;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.ExtendableObjectSelectionSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.AbstractFeatureExpressionData;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.impl.microarray.ArrayLayout;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.PropertyParticle;
import mayday.wapiti.experiments.properties.channels.BackgroundType;
import mayday.wapiti.experiments.properties.channels.ChannelCount;
import mayday.wapiti.experiments.properties.processing.Normalized;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;
import mayday.wapiti.transformations.impl.intra_norm.internal.Loess_R_MaydayPart;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class PrinttipLoess extends AbstractTransformationPlugin {

	protected ExtendableObjectSelectionSetting<String> mChannel;
	protected ExtendableObjectSelectionSetting<String> aChannel;
	protected HierarchicalSetting mySetting;
	
	protected static final String DescA = "The channel that is fitted, i.e. the dependent variable (usually M values)";
	protected static final String DescB = "The channel that is used to fit, i.e. the independent variable (usually A values)";
	
	protected Loess_R_MaydayPart loessSmoother;
	
	public PrinttipLoess() {}
	
	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps) {
			boolean isFED = FeatureExpressionData.class.isAssignableFrom(e.getDataClass());
			boolean isMulti = ChannelCount.isMultiChannel(e.getDataProperties());			
			if (!isFED || !isMulti )
				return false;
		}
		return true;
	}
	
	public void updateSettings(Collection<Experiment> experiments) {
		// get some data ;)
		Experiment e = experiments.iterator().next();
		ChannelCount c = (ChannelCount)e.getDataProperties().getType(ChannelCount.class);
		if (c!=null) {
			String[] channels = c.getNames();			
			mChannel.updatePredefined(channels);			
			aChannel.updatePredefined(channels);
			if (aChannel.getSelectedIndex()==mChannel.getSelectedIndex()) {
				mChannel.setSelectedIndex(0);
				aChannel.setSelectedIndex(1);
			}
		}		
	}

	
	public String getApplicabilityRequirements() {
		return "Requires multi-channel feature expression data";
	}

	public Setting getSetting() {
		if (mySetting==null) {
			loessSmoother = new Loess_R_MaydayPart();			
			mySetting = new HierarchicalSetting("Printtip Loess normalization");
			mChannel = new ExtendableObjectSelectionSetting<String>("Dependent channel",DescA,0,new String[]{""});
			aChannel = new ExtendableObjectSelectionSetting<String>("Independent channel",DescB,0,new String[]{""});
			mChannel.setLayoutStyle(ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS);
			aChannel.setLayoutStyle(ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS);
			mySetting.addSetting(mChannel).addSetting(aChannel);
			mySetting.addSetting(loessSmoother.getSetting());
		}
		return mySetting;
	}
	
	public void setTransMatrix(TransMatrix t) {
		super.setTransMatrix(t);
	}
	
	public void compute() {
		int indexM,indexA;
		getSetting();
		indexM = mChannel.getSelectedIndex();
		indexA = aChannel.getSelectedIndex();
		
		for (Experiment e : transMatrix.getExperiments(this)) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			AbstractVector m = ((FeatureExpressionData)ex).getExpressionVector(indexM); // will be changed
			AbstractVector a = ((FeatureExpressionData)ex).getExpressionVector(indexA); // will stay unchanged
			ArrayLayout layout = ((FeatureExpressionData)ex).getArrayLayout();
			
			if (layout==null) {
				layout = new ArrayLayout(1, m.size()); // emulate one printtip
			}
			
			int blockCount = layout.blocks();
			AbstractVector m_smooth = new DoubleVector(m.size());
			
			// do the loess for each block
			for (int block=0; block!=blockCount; ++block) {
				int[] subset = layout.blockIndices(block);
				AbstractVector block_m = m.subset(subset);
				AbstractVector block_a = a.subset(subset);
				AbstractVector target_m = m_smooth.subset(subset);
				AbstractVector tmp = loessSmoother.performLoess(block_m, block_a);
				target_m.set(tmp);
			}
			
			m_smooth.setNames(m);
			// save the result			
			HashMap<String,Double> flagTypes = ((FeatureExpressionData)ex).getFlagTypes();
			AbstractVector flags = ((FeatureExpressionData)ex).getFlagVector();
			String nM = ((FeatureExpressionData)ex).getChannelName(indexM)+" printtip loess";
			String nA = ((FeatureExpressionData)ex).getChannelName(indexA);
			transMatrix.setIntermediateData(e, new AbstractFeatureExpressionData(
					flags,
					flagTypes,
					new AbstractVector[]{m_smooth,a},
					null,
					new String[]{nM,nA})
			);
		}	
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		ExperimentState es = new AbstractExperimentState(inputState) {
			public DataProperties getDataProperties() {
				DataProperties dp = inputState.getDataProperties().clone();
				// remove background channel information
		 		PropertyParticle pp = dp.getType(BackgroundType.class);
		 		// there can only be one background type
		 		dp.remove(pp);
		 		// set type of data
		 		dp.add(new ChannelCount(2, new String[]{"printtip loess-smoothed M","A"}) , true);
		 		dp.add(new Normalized(), true);
		 		return dp;
			}
		};
		return es;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".PTLoess", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Compute the printtip-wise loess fit of M versus A values to correct for intensity-dependent bias", 
		"Printtip Loess normalization");
	}
	
	public String getIdentifier() {
		return "PT Loess";
	}

}
