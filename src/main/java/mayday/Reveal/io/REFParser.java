package mayday.Reveal.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;

/**
 * @author jaeger
 *
 */
public class REFParser extends AbstractDataParser {
	
	private DataStorage ds;
	
	public REFParser() {
		this(null);
	}
	
	public void setDataStorage(DataStorage ds) {
		this.ds = ds;
	}
	
	/**
	 * @param ds
	 */
	public REFParser(DataStorage ds) {
		this.ds = ds;
	}
	
	/**
	 * @param refFile
	 */
	public void read(File refFile) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(refFile));
			String line = null;
			SNVList snps = ds.getGlobalSNVList();
			while((line = br.readLine()) != null) {
				String[] split = split(line);
				String snpId = split[0];
				if(!(split.length > 1)) {
					continue;
				}
				char referenceNucleotide = split[1] != null && split[1].length() > 0 ? split[1].charAt(0) : 'N';
				SNV s = snps.get(snpId);
				if(s == null) {
					continue;
				}
				s.setReferenceNucleotide(referenceNucleotide);
			}
			br.close();
		} catch(FileNotFoundException ex) {
			System.out.println("REF file could not be found!");
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
		return "REF File Parser";
	}

	@Override
	public String getType() {
		return "data.ref";
	}

	@Override
	public String getDescription() {
		return "Read SNP reference files";
	}

	@Override
	public String getCategory() {
		return "Data Import & Export";
	}
}
