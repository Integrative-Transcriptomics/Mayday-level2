package mayday.Reveal.data.meta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.core.io.BufferedRandomAccessFile;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.PathSetting;
import mayday.core.structures.maps.BidirectionalHashMap;
import mayday.core.tasks.AbstractTask;

public class Genome extends MetaInformationPlugin {

	public static final String MYTYPE = "GENOME";
	
	private List<String> seqNames = new ArrayList<String>();
	private BidirectionalHashMap<String, String> seqNameMapping = new BidirectionalHashMap<String, String>();
	private HashMap<String, Index> indexMap;
	
	private String filePath;
	private boolean indexCreated = false;
	
	public Genome() {
		filePath = null;
		indexMap = new HashMap<String, Index>();
	}
	
	public Genome(String fastaFilePath) {
		this.filePath = fastaFilePath;
		this.indexMap = new HashMap<String, Index>();
	}
	
	public void mapSeqName(String originalName, String newName) {
		seqNameMapping.put(originalName, newName);
	}
	
	public String getMappedSequenceName(String originalName) {
		String mappedName = this.seqNameMapping.get(originalName);
		if(mappedName == null)
			return originalName;
		return mappedName;
	}
	
	public String getOriginalSequenceName(String mappedName) {
		String originalName = this.seqNameMapping.get(mappedName); 
		if(originalName == null)
			return mappedName;
		return originalName;
	}
	
	/**
	 * Creates a index for the specified fasta file
	 * @return
	 * @throws IOException 
	 */
	public boolean createIndex(AbstractTask task) throws IOException {
		File file = new File(filePath);
		
		BufferedReader bf = new BufferedReader(new FileReader(file));
		String line = null;
		
		String title = null;
		int lineSize = -1;
		long startPosition = 0;
		long endPosition = 0;
		long seqLength = 0;
		long currentPos = 0;
		
		if(task != null)
			task.writeLog("Calculating fasta index ...\n");
		
		while((line = bf.readLine()) != null) {
			currentPos += line.length() + 1; //+1 because of the line break at the end of each line
			
			if(line.startsWith(">")) {
				if(title != null) {
					indexMap.put(title, new Index(title, startPosition, endPosition, lineSize, seqLength));
					seqNames.add(title);
					if(task != null)
						task.writeLog("Finished sequence:\n\t" + title + "\n");
				}
				
				//restore values
				title = line.substring(1); //remove the '>' symbol
				
				//check if titles contain | as delimiter
				//if | is a delimiter then the fourth element is the chromosome id
				String[] titleSplit = title.split("\\|");
				if(titleSplit.length >=4) {
					title = titleSplit[3];
				}
				
				lineSize = -1;
				seqLength = 0;
				startPosition = currentPos;
				
				continue;
			}
			
			//set the line size
			if(lineSize == -1)
				lineSize = line.length();
			
			//update sequence length
			seqLength += line.length();
			
			//end is right before the new line
			endPosition = currentPos - 1;
			
			if(task != null) {
				if(task.hasBeenCancelled())
					break;
			}
		}
		
		bf.close();
		
		if(task != null) {
			if(task.hasBeenCancelled()) {
				indexMap.clear();
				seqNames.clear();
				return false;
			}
		}	
		
		if(title != null) {
			indexMap.put(title, new Index(title, startPosition, endPosition, lineSize, seqLength));
			seqNames.add(title);
			if(task != null)
				task.writeLog("Finished sequence:\n\t" + title + "\n");
		}
		
		//sort the names
		Collections.sort(seqNames, new AlphanumComparator());
		
		if(task != null) {
			if(!task.hasBeenCancelled()) {
				indexCreated = true;
				return true;
			}
			return false;
		} else {
			indexCreated = true;
			return true;
		}
	}
	
	public String getSequenceOnChromosome(String chromosome, int start, int end) throws Exception {
		Set<String> keySet = indexMap.keySet();
		
		for(String title : keySet) {
			if(title.contains(chromosome)) {
				return getSequence(title, start, end);
			}
		}
		
		throw new Exception("FastaIndex: no entry found for " + chromosome);
	}
	
	public void updateReferenceNucleotides() throws Exception {
		SNVList snps = dataStorage.getGlobalSNVList();
		this.updateReferenceNucleotides(snps);
	}
	
	public void updateReferenceNucleotides(SNVList snps) throws Exception {
		if(!indexCreated) {
			int approve = JOptionPane.showConfirmDialog(null, "The index has not been created yet!\nCreate index first!");
			
			if(approve == JOptionPane.CANCEL_OPTION || approve == JOptionPane.NO_OPTION) {
				throw new Exception("FastaIndex: task has been canceled!");
			} else if(approve == JOptionPane.OK_OPTION) {
				boolean done = this.createIndex(null);
				if(!done) {
					throw new Exception("FastaIndex: index creation not completed!");
				}
			}
		}
		
		BufferedRandomAccessFile braf = new BufferedRandomAccessFile(filePath, "r");
		
		for(int i = 0; i < snps.size(); i++) {
			SNV s = snps.get(i);
			String seq = s.getChromosome();
			
			if(seqNameMapping.get(seq) != null) {
				seq = seqNameMapping.get(seq);
			}
			
			Index index = indexMap.get(seq);
			int position = s.getPosition();
			
			if(index == null) {
				braf.close();
				throw new Exception("FastaIndex: index for entry " + seq + " not found!");
			}
			
			if((position-1) >= index.seqLength) {
				braf.close();
				throw new Exception("FastaIndex: requested character is out of fasta entry range!");
			}
			
			long finalPos = index.startPosition + position - 1;
			long lineBreaks = (position-1) / index.lineSize;
			finalPos += lineBreaks;
			
			braf.seek(finalPos);
			int next = braf.read();
			
			if(next == -1) {
				braf.close();
				throw new Exception("FastaIndex: reference for SNP" + s.getID() + "could not be read, requested position is out of fasta index range");
			}
			
			char c = (char)next;
			s.setReferenceNucleotide(c);
		}
		
		braf.close();
	}
	
	public String getSequence(String title, int start, int end) throws Exception {
		if(!indexCreated) {
			int approve = JOptionPane.showConfirmDialog(null, "The index has not been created yet!\nCreate index first!");
			
			if(approve == JOptionPane.CANCEL_OPTION || approve == JOptionPane.NO_OPTION) {
				throw new Exception("FastaIndex: task has been canceled!");
			} else if(approve == JOptionPane.OK_OPTION) {
				boolean done = this.createIndex(null);
				if(!done) {
					throw new Exception("FastaIndex: index creation not completed!");
				}
			}
		}
		
		Index index = indexMap.get(title);
		
		if(index == null) {
			throw new Exception("FastaIndex: index for entry " + title + " not found!");
		}
		
		if((start-1) >= index.seqLength || (end-1) >= index.seqLength) {
			throw new Exception("FastaIndex: requested sequence is out of fasta entry range!");
		}
		
		if(start > end) {
			throw new Exception("FastaIndex: start is not allowed to be larger than end");
		}
		
		BufferedRandomAccessFile raf = new BufferedRandomAccessFile(this.filePath, "r");
		String line = null;

		long finalStartPos = index.startPosition + start - 1;
		long lineBreaksStart = (start-1) / index.lineSize;
		finalStartPos += lineBreaksStart;
		long finalEndPos = index.startPosition + end;
		long lineBreaksEnd = end / index.lineSize;
		finalEndPos += lineBreaksEnd;
		int seqLength = end - start + 1; //end position counts!
		
		//jump to the correct start position
		raf.seek(finalStartPos);
		
		StringBuffer subseq = new StringBuffer();
		long lineEnd = finalStartPos;
		
		while((line = raf.readLine()) != null) {
			//we are in the correct line now
			lineEnd += line.length();
			
			//subsequence goes over several lines
			if(lineEnd < finalEndPos) {
				subseq.append(line);
				//continue to append lines until we are in the correct endline
				while((line = raf.readLine()) != null) {
					lineEnd += line.length()+1;
					if(lineEnd >= finalEndPos) { //we have found the correct endline
						//get correct end position in line
						int lineEndPos = seqLength - subseq.length();
						subseq.append(line.substring(0, lineEndPos));
						break;
					} else { //not yet in the correct endline, continue to append whole lines
						subseq.append(line);
					}
				}
				if(line != null)
					break;
			} else { //start and end are in the same line
				int lineEndPos = seqLength;
				subseq.append(line.substring(0, lineEndPos));
				break;
			}
			
			raf.close();
			throw new Exception("FastaIndex: something is wrong! Sequence could not be extracted");
		}
		
		String finalString = subseq.toString().trim();
		
		raf.close();
		
		if(finalString == null || finalString.equals("")) {
			throw new Exception("FastaIndex: the sequence could not be extracted!");
		}
		
		return finalString;
	}
	
	@Override
	public void serialize(BufferedWriter bw) throws IOException {
		bw.append("$META\n");
		bw.append(getCompleteType());
		bw.append("\n");
		
		bw.append(">");
		bw.append(filePath);
		bw.append("\n");
		
		for(String key :  seqNames) {
			bw.append(indexMap.get(key).serialize());
			bw.append("\n");
		}
	}
	
	@Override
	public boolean deSerialize(String serial) {
		indexMap.clear();
		seqNames.clear();
		
		try {
			BufferedReader br = new BufferedReader(new StringReader(serial));
			String line = br.readLine();
			this.filePath = line.substring(1);
			
			File file = new File(filePath);
			if(!file.exists()) {
				int approve = JOptionPane.showConfirmDialog(null, "The genome file:\n" 
						+ filePath 
						+ "\n could not be found! Please specify the location of this file.", 
						"Genome file not found!", JOptionPane.ERROR_MESSAGE);
				if(approve == JOptionPane.OK_OPTION) {
					while(true) {
						PathSetting ps = new PathSetting("Genome File", "Specify the genome file", null, false, true, false);
						SettingDialog sd = new SettingDialog(null, "Select genome file", ps);
						sd.showAsInputDialog();
						if(sd.closedWithOK()) {
							String path = ps.getStringValue();
							File newfile = new File(path);
							String newName = newfile.getName();
							String oldName = file.getName();
							if(!newName.equals(oldName)) {
								JOptionPane.showMessageDialog(null, "The selected genome filename (" + newName +") does not match the filename in the Reveal Snapshot (" + oldName + ")!" +
										"\nPlease select the correct genome file.", "ERROR", JOptionPane.ERROR_MESSAGE);
								continue;
							} else {
								this.filePath = path;
								break;
							}
						} else {
							return false;
						}
					}
				} else {
					return false;
				}
			}
			
			while((line = br.readLine()) != null) {
				if(line.length() == 0)
					continue;
				String[] splitted = line.split("\t");
				String title = splitted[0];
				String mappedTitle = splitted[1];
				long startPos = Long.parseLong(splitted[2]);
				long endPos = Long.parseLong(splitted[3]);
				long lineSize = Long.parseLong(splitted[4]);
				long seqLength = Long.parseLong(splitted[5]);
				Index index = new Index(title, startPos, endPos, lineSize, seqLength);
				indexMap.put(title, index);
				seqNames.add(title);
				if(!mappedTitle.equals("null"))
					seqNameMapping.put(title, mappedTitle);
			}
			
			indexCreated = true;
			br.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * represents an index of a fasta sequence
	 */
	private class Index {
		String title;
		long lineSize;
		long startPosition;
		long endPosition;
		long seqLength;
		
		public Index(String title, long startPos, long endPos, long lineSize, long seqLength) {
			this.title = title;
			this.startPosition = startPos;
			this. endPosition = endPos;
			this.lineSize = lineSize;
			this.seqLength = seqLength;
		}
		
		public String serialize() {
			return title + "\t" + seqNameMapping.get(title) + "\t" + startPosition + "\t" + endPosition + "\t" + lineSize + "\t" + seqLength;
		}
	}

	@Override
	public String getName() {
		if(filePath == null) {
			return "Genome";
		}
		File f = new File(filePath);
		String name = f.getName();
		return name;
	}

	public int getNumberOfSequences() {
		return seqNames.size();
	}

	public long getSequencLength(String seqName) {
		String name = seqName;
		//use original name if possible
		if(seqNameMapping.getRight(seqName) != null)
			name = seqNameMapping.getRight(seqName);
		return indexMap.get(name).seqLength;
	}

	private long totalLength = -1;
	
	public long getTotalLength() {
		if(totalLength != -1)
			return totalLength;
		
		totalLength = 0;
		for(int i = 0; i < seqNames.size(); i++) {
			totalLength += indexMap.get(seqNames.get(i)).seqLength;
		}
		
		return totalLength;
	}

	public Map<Double, String> getLabeling() {
		Map<Double, String> labeling = new HashMap<Double, String>();
		for(int i = 0; i < this.getNumberOfSequences(); i++) {
			String name = seqNames.get(i);
			double value = (getOffset(name)+indexMap.get(name).seqLength) / (double)this.getTotalLength();
			double v = Math.round(value * 100.) / 100.;
			
			//use mapping if possible
			if(seqNameMapping.get(name) != null) {
				name = seqNameMapping.get(name);
			}
			labeling.put(v, name);
		}
		return labeling;
	}

	public long getGlobalPosition(String seqName, int localPosition) {
		String name = seqName;
		//use original name if possible
		if(seqNameMapping.get(seqName) != null)
			name = seqNameMapping.get(seqName);
		return getOffset(name) + localPosition;
	}
	
	private long getOffset(String seqName) {
		int index = seqNames.indexOf(seqName);
		long offSet = 0;
		for(int i = 0; i < index; i++)
			offSet += indexMap.get(seqNames.get(i)).seqLength;
		return offSet;
	}

	public String getSequenceName(int i) {
		return seqNames.get(i);
	}
	
	public void sortSequenceNames() {
		Collections.sort(seqNames, new AlphanumComparator());
	}
	
	public void sortByMappedSequenceNames() {
		ArrayList<String> names = new ArrayList<String>();
		Collection<String> newNames = seqNameMapping.getRightElements();
		names.addAll(newNames);
		Collections.sort(names, new AlphanumComparator());
		
		ArrayList<String> unmappedNames = new ArrayList<String>();
		if(newNames.size() < seqNames.size()) {
			for(int i = 0; i < seqNames.size(); i++) {
				if(seqNameMapping.get(seqNames.get(i)) == null) {
					unmappedNames.add(seqNames.get(i));
				}
			}
			Collections.sort(unmappedNames, new AlphanumComparator());
		}
		
		seqNames.clear();
		for(int i = 0; i < names.size(); i++) {
			seqNames.add((String)seqNameMapping.get(names.get(i)));
		}
		for(int i = 0; i < unmappedNames.size(); i++) {
			seqNames.add(unmappedNames.get(i));
		}
	}

	@Override
	public String getType() {
		return "data.meta." + MYTYPE;
	}

	@Override
	public String getDescription() {
		return "Get genome information from a fasta genome file";
	}

	@Override
	public Class<?> getResultClass() {
		return Genome.class;
	}
	
	public String toString() {
		return getName() + " (" + seqNames.size() + ") ";
	}
	
	private class AlphanumComparator implements Comparator<Object> {
	    private final boolean isDigit(char ch) {
	        return ch >= 48 && ch <= 57;
	    }

	    // Length of string is passed in for improved efficiency (only need to calculate it once)
	    private final String getChunk(String s, int slength, int marker) {
	        StringBuilder chunk = new StringBuilder();
	        char c = s.charAt(marker);
	        chunk.append(c);
	        marker++;
	        if (isDigit(c)) {
	            while (marker < slength) {
	                c = s.charAt(marker);
	                if (!isDigit(c))
	                    break;
	                chunk.append(c);
	                marker++;
	            }
	        } else {
	            while (marker < slength) {
	                c = s.charAt(marker);
	                if (isDigit(c))
	                    break;
	                chunk.append(c);
	                marker++;
	            }
	        }
	        return chunk.toString();
	    }

	    public int compare(Object o1, Object o2) {
	        if (!(o1 instanceof String) || !(o2 instanceof String)) {
	            return 0;
	        }
	        String s1 = (String)o1;
	        String s2 = (String)o2;

	        int thisMarker = 0;
	        int thatMarker = 0;
	        int s1Length = s1.length();
	        int s2Length = s2.length();

	        while (thisMarker < s1Length && thatMarker < s2Length) {
	            String thisChunk = getChunk(s1, s1Length, thisMarker);
	            thisMarker += thisChunk.length();

	            String thatChunk = getChunk(s2, s2Length, thatMarker);
	            thatMarker += thatChunk.length();

	            // If both chunks contain numeric characters, sort them numerically
	            int result = 0;
	            if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0))) {
	                // Simple chunk comparison by length.
	                int thisChunkLength = thisChunk.length();
	                result = thisChunkLength - thatChunk.length();
	                // If equal, the first different number counts
	                if (result == 0) {
	                    for (int i = 0; i < thisChunkLength; i++) {
	                        result = thisChunk.charAt(i) - thatChunk.charAt(i);
	                        if (result != 0) {
	                            return result;
	                        }
	                    }
	                }
	            } else {
	                result = thisChunk.compareTo(thatChunk);
	            }

	            if (result != 0)
	                return result;
	        }
	        return s1Length - s2Length;
	    }
	}
}
