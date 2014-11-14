package mayday.GWAS.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.StringTokenizer;

import mayday.GWAS.RevealPlugin;
import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.SNPList;
import mayday.core.tasks.AbstractTask;

/**
 * @author jaeger
 *
 */
public abstract class AbstractDataParser extends RevealPlugin {
	
	protected DataStorage ds;
	
	public AbstractDataParser() {
		ds = null;
	}
	
	public AbstractDataParser(DataStorage ds) {
		this.ds = ds;
	}
	
	public abstract void read(File input);
	
	public abstract void write(File output);
	
	/**
	 * Determines the right delimiter and splits the sequence according to this delimiters
	 * @param strLine
	 * @return splitted string
	 */
	public String[] split(String strLine) {
		String[] delimiters = new String[] { "\t", " ", ",", ";" };
		String[] elements;
		for (int i = 0; i < delimiters.length; i++) {
			elements = strLine.split(delimiters[i]);
			if (elements.length > 1) {
				return elements;
			}
		}
		return new String[]{strLine};
	}
	
	protected StringTokenizer tokenize(String line) {
		String[] delimiters = new String[] { "\t", " ", ",", ";" };
		for(int i = 0; i < delimiters.length; i++) {
			StringTokenizer st = new StringTokenizer(line, delimiters[i]);
			if(st.countTokens() > 1) {
				return st;
			}
		}
		return null;
	}
	
	protected int numberOfLines(File file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			int lines = 0;
			while(br.readLine() != null)
				lines++;
			br.close();
			return lines;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}
	
	@Override
	public void run(Collection<SNPList> snpLists) {
		return; //nothing to do here!
	}
	
	@Override
	public String getMenuName() {
		return null;
	}

	@Override
	public String getMenu() {
		return null;
	}
}
