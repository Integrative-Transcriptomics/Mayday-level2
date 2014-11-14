package mayday.wapiti.containers.featuresummarization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class FeatureSummarizationMapContainer extends HashMap<String, IFeatureSummarizationMap>{

	public static FeatureSummarizationMapContainer INSTANCE = new FeatureSummarizationMapContainer();
	
	private FeatureSummarizationMapContainer() {		
	}
	
	public List<IFeatureSummarizationMap> list() {
		return new LinkedList<IFeatureSummarizationMap>(values());
	}
	
	public void add(IFeatureSummarizationMap fsm) {
		put(fsm.getName(), fsm);
	}
	
	
	
	public void writeToStream(BufferedWriter bw) throws IOException {
		for (IFeatureSummarizationMap im : values())
			im.writeToStream(bw);		
	}
	
	public void readFromStream(BufferedReader br) throws IOException {		
		while(br.ready()) {
			FeatureSummarizationMap im = new FeatureSummarizationMap(br);
			add(im);
		}
	}
	
}
