package mayday.wapiti.containers.featuresummarization.importer;

import java.util.List;

import mayday.wapiti.containers.featuresummarization.FeatureSummarizationMap;

public interface FeatureMapFileImportPlugin {
	
	public FeatureSummarizationMap importFrom(List<String> files);

}
