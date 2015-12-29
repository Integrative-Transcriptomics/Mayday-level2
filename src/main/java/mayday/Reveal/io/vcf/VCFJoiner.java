package mayday.Reveal.io.vcf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

public class VCFJoiner {

	private static final String REF_GT = "0/0";
	
	TreeMap<String, VCFEntry> entries;
	File joinedFile = null;
	
	
	public VCFJoiner() {
		entries = new TreeMap<String, VCFEntry>();
	}
	
	public void join(List<String> vcfFiles) throws IOException {
		
		int numSamplesTotal = 0;
		int numSamples = 0;
		
		String header = null;
		
		System.out.println(new File(vcfFiles.get(0)).getParent());
		joinedFile = new File(new File(vcfFiles.get(0)).getParent()+"/joined.vcf");
		
		for(int i = 0; i < vcfFiles.size(); i++) {
			String vcfFile = vcfFiles.get(i);
			
			BufferedReader br = new BufferedReader(new FileReader(vcfFile));
			String line = null;
			
			while((line = br.readLine()) != null) {
				if(line.startsWith("##")) {
					continue;
				}
				
				if(line.startsWith("#")) {
					String[] split = line.split("\t");
					numSamples = split.length - 9;
					numSamplesTotal += numSamples;
					enlargeExistingEntries(numSamples);
					if(header == null) {
						header = line;
					} else {
						for(int j = 9; j < split.length; j++) {
							header += "\t" + split[j];
						}
					}
					continue;
				}
				
				VCFEntry e = VCFEntry.fromVCFLine(line);
				
				if(e == null) {
					//something is wrong with that line, skip it!
					continue;
				}
				
				int numPreviousSamples = numSamplesTotal - numSamples;
				
				if(entries.containsKey(e.getChromosomalLocation())) {
					VCFEntry ee = this.entries.get(e.getChromosomalLocation());
					mergeEntries(ee, e, numPreviousSamples);
				} else {
					enlargeEntry(e, numPreviousSamples);
					this.entries.put(e.getChromosomalLocation(),  e);
				}				
			}
			
			br.close();
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(joinedFile));
		bw.write(header);
		bw.newLine();
		
		for(VCFEntry e : this.entries.values()) {
			bw.write(e.toString());
			bw.newLine();
		}
		
		bw.flush();
		bw.close();
	}

	private void mergeEntries(VCFEntry ee, VCFEntry e, int numPrevSamples) {
		String prevID = ee.getiD();
		String ID = e.getiD();
		
		//TODO GJ: when indels come into play, we have to change this!
		if(!prevID.startsWith("rs") && ID.startsWith("rs")) {
			ee.setiD(ID);
		} else if(!prevID.startsWith("rs") && !ID.startsWith("rs")) {
			ee.setiD(new String(e.getChrom() + ":" + e.getPos()));
		}
		
		String prevRef = ee.getRef();
		String ref = e.getRef();
		
		//TODO GJ: what about indels?
		if(prevRef.equals("N") && !ref.equals("N")) {
			ee.setRef(ref);
		}
		
		String prevAlt = ee.getAlt();
		String alt = e.getAlt();
		
		if(!prevAlt.equals(alt)) {
			//for simplicity just append the both alternative fields
			String newAlt = prevAlt + "," + alt;
			ee.setAlt(newAlt);
			
			String sampleIds = e.getSampleIds();
			String[] split = sampleIds.split("\t");
			
			int offset = prevAlt.split(",").length;
			
			for(int i = 0; i < split.length; i++) {
				//assume that the first field is always the genotype field
				String gt = split[i].split(":")[0];
				String [] gtSplit = null;
				char splitChar = '/';
				gtSplit = gt.split("/");
				if(gtSplit.length == 1) {
					splitChar = '|';
					gtSplit = gt.split("\\|");
				}
				
				if(gtSplit.length == 1) {
					int pos = Integer.parseInt(gt);
					if(pos != 0) {
						pos += offset;
					}
					gt = Integer.toString(pos);
				} else {
					int first = Integer.parseInt(gtSplit[0]);
					int second = Integer.parseInt(gtSplit[1]);
					
					if(first != 0) {
						first += offset;
					}
					
					if(second != 0) {
						second += offset;
					}
					
					gt = Integer.toString(first) + splitChar + Integer.toString(second);
				}
				
				split[i] = gt;
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append(split[0]);
			
			for(int i = 1; i < split.length; i++) {
				sb.append("\t");
				sb.append(split[i]);
			}
			
			e.setSampleIds(sb.toString());
		}
		
		//replace the default sample ids
		
		String sampleIDs = ee.getSampleIds();
		String[] split = sampleIDs.split("\t");
		
		StringBuilder sb = new StringBuilder();
		sb.append(split[0]);
		
		for(int i = 1; i < numPrevSamples; i++) {
			sb.append("\t");
			sb.append(split[i]);
		}
		
		sb.append("\t");
		sb.append(e.getSampleIds());
		
		ee.setSampleIds(sb.toString());
		
		//TODO what about the other vcf file fields?
	}

	private void enlargeEntry(VCFEntry e, int numPreviousSamples) {
		if(numPreviousSamples > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(REF_GT);
			
			for(int i = 1; i < numPreviousSamples; i++) {
				sb.append("\t");
				sb.append(REF_GT);
			}
			
			e.addSamplesBefore(sb.toString());
		}
	}

	private void enlargeExistingEntries(int numSamples) {
		StringBuilder sb = new StringBuilder();
		sb.append(REF_GT);
		
		for(int i = 1; i < numSamples; i++) {
			sb.append("\t");
			sb.append(REF_GT);
		}
		
		for(VCFEntry e : this.entries.values()) {
			e.addSamplesAfter(sb.toString());
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(VCFEntry e : this.entries.values()) {
			sb.append(e.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public File getJoinedFile() {
		return this.joinedFile;
	}
}
