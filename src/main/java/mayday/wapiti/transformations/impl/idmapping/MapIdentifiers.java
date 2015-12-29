package mayday.wapiti.transformations.impl.idmapping;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.wapiti.containers.identifiermapping.IdentifierMap;
import mayday.wapiti.containers.identifiermapping.IdentifierMapSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class MapIdentifiers extends AbstractTransformationPlugin {

	public IdentifierMapSetting mySetting;

	public MapIdentifiers() {}

	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps)
			if (!FeatureExpressionData.class.isAssignableFrom(e.getDataClass()) || e.hasLocusInformation())
				return false;
		return true;
	}
	
	public String getApplicabilityRequirements() {
		return "Requires feature expression data without locus information.\n" +
				"First map identifiers, then attach locus information to the new identifiers";
	}


	public void compute() {		
		for (Experiment e : transMatrix.getExperiments(this)) {
			
			IdentifierMap imap = mySetting.getIdentifierMap();			
			
			ExperimentData ex = transMatrix.getIntermediateData(e);
			transMatrix.setIntermediateData(e, 
					new IdentifierMappedExperimentData((FeatureExpressionData)ex, imap)
			);
						
			for (MIGroup mg : e.getAnnotations()) {
				HashSet<String> elementsToConvert = new HashSet<String>();
				for (Entry<Object,MIType> element : mg.getMIOs()) {
					if (element.getKey() instanceof String) { // a probe annotation 
						 elementsToConvert.add((String)element.getKey());
					}
				}
				for (String s : elementsToConvert) {
					String s2 = imap.map(s);
					if (s!=s2) { //fast comparison is allowed here
						MIType mt = mg.getMIO(s);
						mg.remove(s);
						mg.add(s2, mt);
					}
				}
			}
		}		
	}

	public IdentifierMapSetting getSetting() {
		if (mySetting==null)
			mySetting = new IdentifierMapSetting();
		return mySetting;
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		return new IdentifierMappedExperimentState(inputState, mySetting.getIdentifierMap());
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".AddId", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Map identifiers", 
		"Map Identifiers");
	}
	
	

}
