package mayday.GWAS.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mayday.GWAS.data.Gene;
import mayday.GWAS.data.GeneList;
import mayday.core.MasterTable;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;

/**
 * @author jaeger
 *
 */
public class PHENOParser extends AbstractDataParser {
	
	private MasterTable masterTable;
	
	private BooleanSetting hasHeaderSetting;
	
	protected HierarchicalSetting setting;
	
	protected GeneList genes;
	
	public PHENOParser() {
		this(null);
	}
	
	public void setMasterTable(MasterTable mt) {
		this.masterTable = mt;
	}
	
	/**
	 * @param masterTable
	 */
	public PHENOParser(MasterTable masterTable) {
		this.masterTable = masterTable;
		setting = new HierarchicalSetting("Phenotype File Settings");
		setting.addSetting(hasHeaderSetting = new BooleanSetting("Has header row?", null, true));
	}
	
	public Setting getSetting() {
		return this.setting;
	}
	
	/**
	 * @param phenotypeFile
	 */
	public void read(File phenotypeFile) {
		int line = 0;
		genes = new GeneList(masterTable.getDataSet());
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(phenotypeFile));
			String strLine;
			
			List<ArrayList<Double>> expressionValues = new ArrayList<ArrayList<Double>>();
			List<String> geneNames = new ArrayList<String>();
			
			while ((strLine = br.readLine()) != null) {
				// ignore comment lines and header line as well as empty lines
				if (strLine.startsWith("#") || line == 0 || strLine.trim().equals("")) {
					//read gene names from header line
					if(line == 0) {
						String[] elements = split(strLine);
						if (elements == null || elements.length < 3) {
							br.close();
							throw new IOException("File has an unknown format!");
						}
						
						for(int i = 0; i < elements.length - 2; i++) {
							Gene g;
							
							if(hasHeaderSetting.getBooleanValue()) {
								String geneName = elements[i+2];
								g = new Gene(masterTable, geneName, 0, 0, "");
							} else {
								g = new Gene(masterTable, "Gene "+(i+1), 0, 0, "");
							}
							
							genes.addGene(g);
							//new ArrayList<Double> for each gene
							expressionValues.add(new ArrayList<Double>());
							geneNames.add(g.getName());
						}
					}
					line++;
					continue;
				}
				
				// determine the correct delimiter
				String[] elements = split(strLine);
				if (elements == null || elements.length < 3) {
					br.close();
					throw new IOException("File has an unknown format!");
				}
					
				
				for(int i = 0; i < elements.length - 2; i++) {
					try {
					expressionValues.get(i).add(Double.parseDouble(elements[i+2]));
					} catch(Exception ex) {
						System.out.println();
					}
				}
			}
			
			br.close();
			
			for(int i = 0; i < genes.size(); i++) {
				double[] evs = new double[expressionValues.get(i).size()];
				for(int j = 0; j < evs.length; j++) {
					evs[j] = expressionValues.get(i).get(j).doubleValue();
				}
				String geneName = geneNames.get(i);
				genes.getGene(geneName).setValues(evs);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(File output) {
		// TODO Auto-generated method stub
		
	}

	public GeneList getGenes() {
		return genes;
	}

	@Override
	public String getName() {
		return "PHENO File Parser";
	}

	@Override
	public String getType() {
		return "data.pheno";
	}

	@Override
	public String getDescription() {
		return "Read/Write PLINK phenotype files";
	}

	@Override
	public String getCategory() {
		return "Data Import & Export";
	}
}
