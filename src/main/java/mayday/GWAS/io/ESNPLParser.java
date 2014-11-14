package mayday.GWAS.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.SNPList;

/**
 * @author jaeger
 *
 */
public class ESNPLParser extends AbstractDataParser {

	private DataStorage ds;
	
	public ESNPLParser() {
		super();
	}
	
	public void setDataStorage(DataStorage dataStorage) {
		this.ds = dataStorage;
	}
	
	/**
	 * @param ds
	 */
	public ESNPLParser(DataStorage ds) {
		this.ds = ds;
	}
	
	/**
	 * @param esf
	 */
	public void read(File esf) {
		try {
			SNPList external = new SNPList("External SNPs " + (ds.numberOfSNPLists()+1), ds);
			BufferedReader br = new BufferedReader(new FileReader(esf));
			String line = null;
			while((line = br.readLine()) != null) {
				String snpID = line;
				SNP s = new SNP(null, snpID, null, 0, 'N', -1);
				external.add(s);
			}
			ds.addSNPList("External SNPs " + (ds.numberOfSNPLists()+1), external);
			br.close();
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
		return "SNPList Parser";
	}

	@Override
	public String getType() {
		return "data.esnpl";
	}

	@Override
	public String getDescription() {
		return "Read/Write SNPList files";
	}

	@Override
	public String getCategory() {
		return "Data Import & Export";
	}
}
