package mayday.wapiti.experiments.generic.mapping.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import mayday.core.io.csv.ParserSettings;
import mayday.core.io.csv.ParsingTableModel;

@SuppressWarnings("serial")
public class LazyParsingTableModel extends ParsingTableModel {

	protected int MAXLINES;
	
	public LazyParsingTableModel(File f, int maxLines) throws FileNotFoundException {
		super();
		MAXLINES = maxLines;
		doParse(new FileInputStream(f), 0);
	}

	protected boolean parseCondition(BufferedReader bfr) throws Exception {
		return bfr.ready() && lines.size()<MAXLINES;
	}		
	
	public ParserSettings getSettings() {
		return parserSettings;
	}

}
