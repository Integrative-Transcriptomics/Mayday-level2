package mayday.Reveal.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;

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
			SNVList external = new SNVList("External SNPs " + (ds.numberOfSNVLists()+1), ds);
			BufferedReader br = new BufferedReader(new FileReader(esf));
			String line = null;
			while((line = br.readLine()) != null) {
				String snpID = line;
				SNV s = new SNV(null, snpID, null, 0, 'N', -1);
				external.add(s);
			}
			ds.addSNVList("External SNPs " + (ds.numberOfSNVLists()+1), external);
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
