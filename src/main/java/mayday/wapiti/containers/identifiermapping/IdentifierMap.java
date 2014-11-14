package mayday.wapiti.containers.identifiermapping;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;

public class IdentifierMap  {
	
	HashMap<String, String> forward = new HashMap<String, String>();
	HashMap<String, String> reverse = new HashMap<String, String>();

	String name;

	public IdentifierMap(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public IdentifierMap(MIGroup mg) {
		this(mg.getMIManager().getDataSet().getName()+": "+mg.getName());
		for (Entry<Object, MIType> e : mg.getMIOs()) 
			put( e.getKey().toString(), e.getValue().toString() );
	}
	
	public void put(String from, String to) {
		forward.put(from, to);
		reverse.put(to, from);
	}

	public String toString() {
		return name+ " ("+forward.size()+" ids)";
	}
	
	public String map(String identifier) {
		String res = forward.get(identifier);
		if (res==null)
			res = identifier;
		return res;
	}
	
	public String mapReverse(String identifier) {
		String res = reverse.get(identifier);
		if (res==null)
			res = identifier;
		return res;
	}
	
	public void writeToStream(BufferedWriter bw) throws IOException {
		bw.write(name+"\n");
		bw.write(forward.size()+"\n");
		for (Entry<String, String> e : forward.entrySet()) {
			bw.write(e.getKey()+"\n");
			bw.write(e.getValue()+"\n");
		}
		bw.flush();
	}
	
	public IdentifierMap(BufferedReader br) throws IOException {
		name = br.readLine();
		int i = Integer.parseInt(br.readLine());
		for (int j=0; j!=i; ++j) {
			String key = br.readLine();
			String val = br.readLine();
			put(key,val);
		}
	}

}
