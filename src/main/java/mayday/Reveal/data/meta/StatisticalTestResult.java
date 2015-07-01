package mayday.Reveal.data.meta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.core.math.pcorrection.PCorrectionPlugin;

/**
 * @author jaeger
 *
 */
public class StatisticalTestResult extends MetaInformationPlugin {

	public static final String MYTYPE = "STR";
	
	private String testName;
	private double min = Double.MAX_VALUE;
	Map<SNV, Double> snpIndexToPVal = new HashMap<SNV, Double>();
	
	public StatisticalTestResult() {
		this("Statistical Test");
	}
	
	/**
	 * @param testName
	 */
	public StatisticalTestResult(String testName) {
		this.testName = testName;
	}
	
	/**
	 * @param snpIndex
	 * @param pVal
	 */
	public void setPValue(SNV s, double pVal) {
		this.snpIndexToPVal.put(s, pVal);
	}
	
	/**
	 * @param index
	 * @return p value at the specified index
	 */
	public double getPValue(SNV s) {
		Double p = this.snpIndexToPVal.get(s);
		if(p == null) {
			return Double.NaN;
		}
		return p;
	}

	/**
	 * @return number of statistical values
	 */
	public int size() {
		return this.snpIndexToPVal.size();
	}

	/**
	 * @return minimal p-value
	 */
	public double getMin() {
		if(Double.compare(min, Double.MAX_VALUE) == 0) {
			Collection<Double> values = snpIndexToPVal.values();
			for(double d : values) {
				if(Double.compare(d, min) < 0) {
					min = d;
				}
			}
		}
		return min;
	}
	
	public SNVList getSNPs(double pThreshold, DataStorage ds) {
		SNVList sigSNPs = new SNVList("p value Threshold SNPs", ds);
		for(SNV s : snpIndexToPVal.keySet()) {
			if(Double.compare(snpIndexToPVal.get(s), pThreshold) < 0) {
				sigSNPs.add(s);
			}
		}
		return sigSNPs;
	}

	public void serialize(BufferedWriter bw) throws IOException {
		//Header for Meta Information Plugins
		bw.append("$META\n");
		bw.append(getCompleteType());
		bw.append("\n");
		
		bw.append(getStatTestName());
		bw.append("\n");
		
		for(SNV s : snpIndexToPVal.keySet()) {
			bw.append(s.getID());
			bw.append("\t");
			bw.append(String.valueOf(snpIndexToPVal.get(s)));
			bw.append("\n");
		}
	}

	public String getStatTestName() {
		return testName;
	}
	
	public void setStatTestName(String name) {
		this.testName = name;
	}
	
	public String toString() {
		return this.getStatTestName() + " ( " + size() + ")";
	}

	@Override
	public boolean deSerialize(String serial) {
		BufferedReader br = new BufferedReader(new StringReader(serial));
		
		try {
			String line = br.readLine();
			this.testName = line;
			
			while((line = br.readLine()) != null) {
				//skip empty lines
				if(line.trim().equals("")) {
					continue;
				}
					
				String[] snpLine = line.split("\t");
				SNVList global = dataStorage.getGlobalSNVList(); 
				if(global.contains(snpLine[0])) {
					SNV s = global.get(snpLine[0]);
					snpIndexToPVal.put(s, Double.parseDouble(snpLine[1]));
				}
			}
			return true;
		} catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public String getType() {
		return "data.meta." + MYTYPE;
	}

	@Override
	public String getDescription() {
		return "Statistical Results";
	}
	
	@Override
	public String getName() {
		return "Statistical Test Result";
	}

	@Override
	public Class<?> getResultClass() {
		return Double.class;
	}

	public void correctPValues(PCorrectionPlugin method) {
		HashMap<Integer, SNV> snpToIndex = new HashMap<Integer, SNV>();
		List<Double> pValues = new LinkedList<Double>();
		
		//get values from map
		int index = 0;
		for(SNV s : snpIndexToPVal.keySet()) {
			snpToIndex.put(index, s);
			pValues.add(snpIndexToPVal.get(s));
			index++;
		}
		
		pValues = method.correct(pValues);
		
		//put corrected values back to map
		for(int i = 0; i < pValues.size(); i++) {
			Double pValue = pValues.get(i);
			snpIndexToPVal.put(snpToIndex.get(i), pValue);
		}
	}
}
