package mayday.Reveal.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNP;
import mayday.Reveal.data.SNPList;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;

/**
 * @author jaeger
 *
 */
public class MAPParser extends AbstractDataParser {
	
	private BooleanSetting hasGeneticDistancesSetting;
	
	protected SNPList snps;
	
	protected HierarchicalSetting setting;
	
	private DataStorage ds;
	
	
	public MAPParser() {
		this(null);
	}
	
	public void setDataStorage(DataStorage dataStorage) {
		this.ds = dataStorage;
	}
	
	/**
	 * default constructor
	 */
	public MAPParser(DataStorage ds) {
		setting = new HierarchicalSetting("MAP File Setting");
		setting.addSetting(hasGeneticDistancesSetting = new BooleanSetting("Genetic distances column?", null, true));
		this.ds = ds;
	}
	
	public Setting getSetting() {
		return this.setting;
	}
	
	public void read(File mapFile) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(mapFile));
			snps = new SNPList("Global", ds);
			String line = null;
			int snpIndex = 0;
			
			while((line = br.readLine()) != null) {
				if(line.startsWith("#")) {
					continue;
				}
				
				String[] splitted = split(line);
				
				if(splitted.length < 3) {
					br.close();
					throw new FileFormatUnknownException();
				}
				
				String chromosome = splitted[0];
				String snpID = splitted[1];
				String geneID = null;
				
				String[] idSplitted = snpID.split("_");
				if(idSplitted.length > 1) {
					geneID = idSplitted[0];
					snpID = idSplitted[1];
				}
				
				double geneticDistance = 0;
				int position = 0;
				
				if(hasGeneticDistancesSetting.getBooleanValue()) {
					geneticDistance = Double.parseDouble(splitted[2]);
					position = Integer.parseInt(splitted[3]);
				} else {
					position = Integer.parseInt(splitted[2]);
				}
				
				SNP s = new SNP(snpID, chromosome, geneticDistance, position, snpIndex++);
				s.setGene(geneID);
				snps.add(s);
			}
			
			br.close();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * @return snps contained in the map file
	 */
	public SNPList getSNPs() {
		return this.snps;
	}

	@Override
	public void write(File output) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "MAP File Parser";
	}

	@Override
	public String getType() {
		return "data.map";
	}

	@Override
	public String getDescription() {
		return "Read/Write PLINK MAP files";
	}

	@Override
	public String getCategory() {
		return "Data Import & Export";
	}
}
