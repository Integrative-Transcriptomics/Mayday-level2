package mayday.Reveal.data.ld;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Set;

import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.SNVPair;
import mayday.Reveal.data.meta.MetaInformationPlugin;

public class LDResults extends MetaInformationPlugin {
	
	public static final String MYTYPE = "LDR";
	
	private HashMap<SNVPair, Double> results = new HashMap<SNVPair, Double>();
	
	public void serialize(BufferedWriter bw) throws IOException {
		//Header for Meta Information Plugins
		bw.append("$META\n");
		bw.append(getCompleteType());
		bw.append("\n");
		
		for(SNVPair sp : keySet()) {
			bw.append(sp.snp1.getID());
			bw.append("\t");
			bw.append(sp.snp2.getID());
			bw.append("\t");
			bw.append(String.valueOf(get(sp)));
			bw.append("\n");
		}
	}

	@Override
	public boolean deSerialize(String serial) {
		clear();
		BufferedReader br = new BufferedReader(new StringReader(serial));
		
		try {
			String line = null;
			while((line = br.readLine()) != null) {
				//skip empty lines
				if(line.trim().equals("")) {
					continue;
				}
				
				String[] snpLine = line.split("\t");
				SNVList global = dataStorage.getGlobalSNVList();
				if(global.contains(snpLine[0]) && global.contains(snpLine[1])) {
					SNV s1 = global.get(snpLine[0]);
					SNV s2 = global.get(snpLine[1]);
					Double d = Double.parseDouble(snpLine[2]);
					put(new SNVPair(s1,s2), d);
				}
			}
			return true;
		} catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public void clear() {
		results.clear();
	}

	@Override
	public String getName() {
		return "Linkage Disequilibrium Results";
	}

	@Override
	public String getType() {
		return "data.meta." + MYTYPE;
	}

	@Override
	public String getDescription() {
		return "R2 Valuse for pairs of SNPs describing their potential to be in LD";
	}
	
	public Double put(SNVPair key, Double value) {
		return results.put(key, value);
	}
	
	public Double get(SNVPair key) {
		return results.get(key);
	}
	
	public Set<SNVPair> keySet() {
		return results.keySet();
	}
	
	public String toString() {
		return "Results for " + results.size() + " SNP pairs";
	}

	@Override
	public Class<?> getResultClass() {
		return Double.class;
	}

	public boolean contains(SNVPair sp) {
		return this.results.containsKey(sp);
	}
}
