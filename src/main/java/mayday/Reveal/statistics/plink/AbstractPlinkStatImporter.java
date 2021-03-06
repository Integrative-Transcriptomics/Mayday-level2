package mayday.Reveal.statistics.plink;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.meta.StatisticalTestResult;

public abstract class AbstractPlinkStatImporter implements IPlinkStatTestImporter {

	protected String name = "StatTest-PLINK";
	protected int snpid_field = 1;
	protected int p_field = 8;
	
	@Override
	public StatisticalTestResult importTestResults(DataStorage ds, File plinkFile, boolean header, String separator)
			throws Exception {
		this.setName();
		this.setPIndex();
		this.setSNPIDIndex();
		
		BufferedReader br = new BufferedReader(new FileReader(plinkFile));
		String line = null;
		int lineCount = 0;
		SNVList global = ds.getGlobalSNVList();
		StatisticalTestResult r = new StatisticalTestResult(name);
		
		while((line = br.readLine()) != null) {
			if(line.length() == 0)
				continue; //skip empty lines
			if(lineCount == 0 && header) {
				header = false;
				continue; //skip header line if present
			}
			
			String[] split = line.split(separator);
			//String chr = split[0].trim();
			String snpid = split[1].trim();
			
			if(global.contains(snpid)) {
				SNV s = global.get(snpid);
				double p = Double.parseDouble(split[8].trim());
				r.setPValue(s, p);
			}
			
			lineCount++;
		}
		
		br.close();
		
		return r;
	}
	
	public abstract void setName();
	
	public abstract void setSNPIDIndex();
	
	public abstract void setPIndex();
}
