package mayday.Reveal.data.meta.snpcharacterization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import mayday.Reveal.data.meta.MetaInformationPlugin;

public class SNPCharacterizations extends MetaInformationPlugin {

	public static final String MYTYPE = "SNPCHAR";
	
	private ArrayList<SNPCharacterization> snpChars = new ArrayList<SNPCharacterization>();
	
	@Override
	public void serialize(BufferedWriter bw) throws IOException {
		bw.append("$META\n");
		bw.append(getCompleteType());
		bw.append("\n");
		
		for(int i = 0; i < snpChars.size(); i++) {
			snpChars.get(i).serialize(bw);
		}
	}

	@Override
	public boolean deSerialize(String serial) {
		BufferedReader br = new BufferedReader(new StringReader(serial));
		String line = null;
		try {
			while((line = br.readLine()) != null) {
				SNPCharacterization snpChar = new SNPCharacterization(null, null, null);
				snpChar.setDataStorage(this.dataStorage);
				snpChar.deSerialize(line);
				this.add(snpChar);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public Class<?> getResultClass() {
		return SNPCharacterization.class;
	}

	@Override
	public String getName() {
		return "SNP Characterizations";
	}

	@Override
	public String getType() {
		return "data.meta." + MYTYPE;
	}

	public SNPCharacterization get(int index) {
		return this.snpChars.get(index);
	}
	
	@Override
	public String getDescription() {
		return "SNP Characterization Results";
	}
	
	public void add(SNPCharacterization snpChar) {
		snpChar.setDataStorage(dataStorage);
		this.snpChars.add(snpChar);
	}
	
	public void remove(SNPCharacterization snpChar) {
		this.snpChars.remove(snpChar);
	}
	
	public boolean contains(SNPCharacterization snpChar) {
		return snpChars.contains(snpChar);
	}

	public void clear() {
		snpChars.clear();
	}
	
	public String toString() {
		return "SNP Characterizations (" + snpChars.size() + ")";
	}

	public int size() {
		return snpChars.size();
	}
}
