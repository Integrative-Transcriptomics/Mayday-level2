package mayday.dataset.columnOrder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A parser for the user-provided file containing the column titles in the requested order, separated by new lines. 
 * @author Alicia Owen
 *
 */
public class ColumnOrderParser{

	ArrayList<String> columnTitles = new ArrayList<String>();
	
	/**
	 * 
	 * @param filepath the path to the file containing the ordered column titles
	 * @return	the column titles as an Array List
	 * @throws IOException
	 */
	public ArrayList<String> parse(String filepath) throws IOException{
		
		File file = new File(filepath);
		FileReader reader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(reader);
		
		String line;
		
		while((line = bufferedReader.readLine())!=null){
			
			if(!line.isEmpty() && line.trim().length()>0){  //ignore whitespaces
				columnTitles.add(line.trim());
			}
			
		}
		
		bufferedReader.close();
		return columnTitles;
	}
	
}
