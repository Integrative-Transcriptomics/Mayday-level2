package mayday.wapiti.containers.featuresummarization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import mayday.core.structures.maps.MultiHashMap;

@SuppressWarnings("serial")
public class FeatureSummarizationMap extends MultiHashMap<String, String> implements IFeatureSummarizationMap {
	
	String name;

	public FeatureSummarizationMap(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return name+ " ("+size()+" summary features)";
	}

	public List<String> getSubFeatures(String feature) {
		return get(feature);
	}

	public Set<String> featureNames() {
		return keySet();
	}
	
	public void writeToStream(BufferedWriter bw) throws IOException {
		writeToStream(this, bw);
	}
	
	public static void writeToStream(IFeatureSummarizationMap ism, BufferedWriter bw) throws IOException {
		bw.write(ism.getName()+"\n");
		bw.write(ism.featureNames().size()+"\n");
		for (String s : ism.featureNames()) {
			bw.write(s+"\n");
			List<String> subf = ism.getSubFeatures(s);
			for (String sf : subf)
				bw.write(sf+",");
			bw.write("\n");
		}
		bw.flush();
	}
	
	public FeatureSummarizationMap(BufferedReader br) throws IOException {
		name = br.readLine();
		int i = Integer.parseInt(br.readLine());
		for (int j=0; j!=i; ++j) {
			String key = br.readLine();
			String valstr = br.readLine();
			String[] vals = valstr.split(",");
			for (String val : vals)
				if (val.length()>0)
					put(key,val);
		}
	}
}
