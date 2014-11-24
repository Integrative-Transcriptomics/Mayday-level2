package mayday.Reveal.functions;

import java.util.ArrayList;
import java.util.List;

public class PDBCustomReport {

	private List<List<String>> data;
	private int cols, rows;
	private List<String> header;
	
	public PDBCustomReport(List<String> fields) {
		cols = fields.size(); 
		data = new ArrayList<List<String>>(cols);
		header = fields;
		
		for(int i = 0; i < cols; i++) {
			data.add(new ArrayList<String>());
		}
	}
	
	public void add(int i, String d) {
		data.get(i).add(d);
		
		if(data.get(i).size() > rows) {
			rows = data.get(i).size();
		}
	}
	
	public String get(int i, int j) {
		return data.get(i).get(j);
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				buf.append(data.get(j).get(i));
				if(j != rows-1)
					buf.append("\t");
			}
			buf.append("\n");
		}
		return buf.toString();
	}
	
	public List<String> getHeader() {
		return this.header;
	}
 }
