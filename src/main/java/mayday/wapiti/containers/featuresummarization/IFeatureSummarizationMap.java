package mayday.wapiti.containers.featuresummarization;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface IFeatureSummarizationMap {
	
	public String getName();
	
	public List<String> getSubFeatures(String feature);
	
	public Set<String> featureNames();
	
	public void writeToStream(BufferedWriter bw) throws IOException;
}
