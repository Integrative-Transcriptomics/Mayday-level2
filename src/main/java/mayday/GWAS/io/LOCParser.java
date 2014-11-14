package mayday.GWAS.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.Gene;
import mayday.GWAS.data.GeneList;

/**
 * @author jaeger
 *
 */
public class LOCParser extends AbstractDataParser {
	
	private DataStorage ds;
	
	public LOCParser() {
		super();
	}
	
	public void setDataStorage(DataStorage ds) {
		this.ds = ds;
	}
	
	/**
	 * @param ds
	 */
	public LOCParser(DataStorage ds) {
		this.ds = ds;
	}
	
	/**
	 * @param locFile
	 */
	public void read(File locFile) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(locFile));
			String line = null;
			GeneList genes = ds.getGenes();
			while((line = br.readLine()) != null) {
				String[] split = split(line);
				String geneName = split[0];
				int start = Integer.parseInt(split[1]);
				int stop = Integer.parseInt(split[2]);
				String chr = split[3];
				
				Gene gene = genes.getGene(geneName);
				gene.setStartPosition(start);
				gene.setStopPosition(stop);
				gene.setChromosome(chr);
			}
			
			br.close();
		} catch(FileNotFoundException ex) {
			System.out.println("LOC file could not be found!");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void write(File output) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "Gene Location Parser";
	}

	@Override
	public String getType() {
		return "data.loc";
	}

	@Override
	public String getDescription() {
		return "Read/Write gene location files";
	}

	@Override
	public String getCategory() {
		return "Data Import & Export";
	}
}
