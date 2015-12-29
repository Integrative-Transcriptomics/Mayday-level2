package mayday.wapiti.containers.identifiermapping;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class IdentifierMapContainer extends HashMap<String, IdentifierMap>{

	public static IdentifierMapContainer INSTANCE = new IdentifierMapContainer();
	
	private IdentifierMapContainer() {		
	}
	
	public List<IdentifierMap> list() {
		return new LinkedList<IdentifierMap>(values());
	}
	
	public void add(IdentifierMap lm) {
		put(lm.getName(), lm);
	}
	
	
	
	public void writeToStream(BufferedWriter bw) throws IOException {
		for (IdentifierMap im : values())
			im.writeToStream(bw);		
	}
	
	public void readFromStream(BufferedReader br) throws IOException {
		while(br.ready()) {
			IdentifierMap im = new IdentifierMap(br);
			add(im);
		}
	}
	
}
