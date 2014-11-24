package mayday.Reveal.io.vcf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.Haplotypes;
import mayday.Reveal.data.HaplotypesList;
import mayday.Reveal.data.SNP;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.data.Subject;
import mayday.Reveal.data.SubjectList;
import mayday.Reveal.data.meta.Genome;
import mayday.Reveal.io.AbstractDataParser;
import mayday.core.structures.maps.BidirectionalHashMap;

public class VCFParser extends AbstractDataParser {

	private SNPList snps;
	private DataStorage ds;
	private SubjectList subjects;
	
	public void setSNPs(SNPList snps) {
		this.snps = snps;
	}
	
	public void setProject(DataStorage ds) {
		this.ds = ds;
	}
	
	public void setPersons(SubjectList subjects) {
		this.subjects = subjects;
	}
	
	@Override
	public void read(File input) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(input));
			String line = null;
			
			int numSamples = 0;
			boolean isOK = true;
			
			ArrayList<ArrayList<char[]>> haplotypes = new ArrayList<ArrayList<char[]>>();
			snps = new SNPList("New SNPs", ds);
			
			int snpID = 0;
			SNPList global = ds.getGlobalSNPList();
			
			if(global != null)
				snpID = global.size();
			
			while((line = br.readLine()) != null) {
				//pre info lines
				if(line.startsWith("##")) {
					//TODO parse pre infos
					continue;
				}
				//header line
				if(line.startsWith("#")) {
					String header = line.substring(1); //remove # symbol
					
					String[] headersplit = header.split("\t");
					
					if(headersplit.length >= 8) {
						//check whether header is in the correct format
						if(!headersplit[0].equals("CHROM")) {
							isOK = false; //1. column = CHROM
						}
						if(!headersplit[1].equals("POS")) {
							isOK = false; //2. column = POS
						}
						if(!headersplit[2].equals("ID")) {
							isOK = false; //3. column = ID
						}
						if(!headersplit[3].equals("REF")) {
							isOK = false; //4. column = REF
						}
						if(!headersplit[4].equals("ALT")) {
							isOK = false; //5. column = ALT
						}
						if(!headersplit[5].equals("QUAL")) {
							isOK = false; //6. column = QUAL
						}
						if(!headersplit[6].equals("FILTER")) {
							isOK = false; //7. column = FILTER
						}
						if(!headersplit[7].equals("INFO")) {
							isOK = false; //8. column = INFO
						}
						//if header is larger than 8, then filed number 9 must be the FORMAT field followed
						//by an arbitrary number of sample IDs
						if(headersplit.length > 8) {
							if(!headersplit[8].equals("FORMAT")) {
								isOK = false;
							}
							//there are 9 fixed fields if genotype data is present in the vcf file
							numSamples = headersplit.length - 9;
							
							
							if(numSamples > 0) {
								//initialize person list
								subjects = new SubjectList(numSamples);
								//add people to person list
								for(int i = 9; i < headersplit.length; i++) {
									String subjectID = headersplit[i];
									String[] idSplit = subjectID.split(":");
									
									String pID = i-9 + "";
									String famID = pID;
									
									if(idSplit.length == 2) {
										try {
											pID = idSplit[0];
											famID = idSplit[1];
										} catch(Exception ex) {
											System.out.println("Person ID not parseable! Using new IDs!");
											pID = i-9 + "";
											famID = pID;
										}
									}
									
									Subject p = new Subject(pID, famID, false);
									p.setName(subjectID);
									if(ds != null) {
										
										int pIndex = (i-9);
										SubjectList existingPersons = ds.getSubjects();
										if(existingPersons != null)
											pIndex += existingPersons.size();
										
										p.setIndex(pIndex);
									}
									subjects.add(p);
									haplotypes.add(new ArrayList<char[]>());
								}
							}
						}
					} else {
						isOK = false;
					}
					
					if(!isOK) {
						br.close();
						throw new IOException("The file seems to be invalid! Please check the file format!");
					}
					
					continue;
				}
				//parse data content lines				
				String[] dataLine = line.split("\t");
				
				if(dataLine.length >= 8) {
					String chromosome = dataLine[0].toLowerCase();
					
					String[] chromSplit = chromosome.split("\\|");
					
					if(chromSplit.length >= 4) {
						chromosome = chromSplit[3].toUpperCase();
					}
					
					int position = Integer.parseInt(dataLine[1]);
					String ID = dataLine[2].toLowerCase();
					
					if(ID.equals(".")) {
						ID = chromosome + ":" + position;
					}
					
					char referenceNucleotide = dataLine[3].charAt(0);
					
					String[] alternatives = dataLine[4].split(",");
//					int numAlternatives = alternatives.length;
					
					//TODO do we need these three fields?
//					String quality = dataLine[5];
//					String filter = dataLine[6];
//					String information  = dataLine[7];
					
					//is genotype data available
					if(dataLine.length > 8) {
						String[] format = dataLine[8].split(":");
						BidirectionalHashMap<String, Integer> formatIndices = new BidirectionalHashMap<String, Integer>();
						
						//parse format column
						for(int i = 0; i < format.length; i++) {
							formatIndices.put(format[i], i);
						}
						
						for(int i = 9; i < dataLine.length; i++) {
							String[] formatInd = dataLine[i].split(":");
							if(formatInd.length != format.length) {
								isOK = false;
								break;
							} else {
								for(int j = 0; j < formatIndices.size(); j++) {
									String formatId = formatIndices.get(j);
									if(formatId.equals("GT")) {
										char[] haplotype = new char[2];
										if(formatInd[j].equals(".")) { //genotype missing
											haplotype[0] = referenceNucleotide;
											haplotype[1] = referenceNucleotide;
										} else {
											String[] hapSplit = formatInd[j].split("\\|");
											if(hapSplit.length == 1) {
												hapSplit = formatInd[j].split("/");
											}
											
											if(hapSplit[0].equals(".") || hapSplit[0].equals("0")) {
												haplotype[0] = referenceNucleotide;
											} else {
												int index = Integer.parseInt(hapSplit[0]) - 1;
												haplotype[0] = alternatives[index].charAt(0);
											}
											
											if(hapSplit[1].equals(".") || hapSplit[1].equals("0")) {
												haplotype[1] = referenceNucleotide;
											} else {
												int index = Integer.parseInt(hapSplit[1]) - 1;
												haplotype[1] = alternatives[index].charAt(0);
											}
										}
										
										int pIndex = i - 9;
										
										haplotypes.get(pIndex).add(haplotype);
										
									}
									//TODO other fields needed?
								}
							}
						}
					}
					
					//create a new snp and add it to the snplist
					SNP s = new SNP(ID, referenceNucleotide, snpID++);
					s.setPosition(position);
					s.setGeneticDistance(0);
					s.setChromosome(chromosome);
					
					snps.add(s);
				} else {
					isOK = false;
				}
				
				if(!isOK) {
					br.close();
					throw new IOException("The file seems to be invalid! Please check the file format!");
				}
			}
			
			br.close();
			
			//TODO merge new data with already existing data in datastorage
			
			//check if everything worked fine
			
			System.out.println("Number of SNPs = " + snps.size());
			System.out.println("Number of people = " + subjects.size());
			System.out.println("Number of haplotypes = " + haplotypes.size());
			
			boolean numHaploOK = true;
			
			for(int i = 0; i < haplotypes.size(); i++) {
				if(haplotypes.get(i).size() != snps.size()) {
					numHaploOK = false;
					break;
				}
			}
			
			System.out.println("Number of haplotypes OK ? " + numHaploOK);
			
			ds.setGlobalSNPList(snps);
			ds.setSubjects(subjects);
			ds.setGenes(null);
			
			HaplotypesList haploList = new HaplotypesList(numSamples);
			
			int numSNPs = snps.size();
			
			for(int i = 0; i < haploList.size(); i++) {
				Haplotypes h = new Haplotypes(numSNPs);
				ArrayList<char[]> haplos = haplotypes.get(i);
				
				for(int j = 0; j < haplos.size(); j++) {
					char[] gt = haplos.get(j);
					h.addSNPA(j, gt[0]);
					h.addSNPB(j, gt[1]);
				}
				
				haploList.add(h);
			}
			
			haplotypes.clear();
			
			ds.setHaplotypes(haploList);
			
			//clean up as soon as possible
			System.gc();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void write(File output) {
		if(snps != null) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(output));
				
				//write info fields
				bw.write("##fileformat=VCFv4.1");
				bw.newLine();
				bw.write("##FORMAT=<ID=GT,Number=1,Type=String,Description=\"Genotype\">");
				bw.newLine();
				
				bw.write("##INFO=<ID=AN,Number=1,Type=Integer,Description=\"Total number of alleles in called genotypes\">");
				bw.newLine();
				
				bw.write("##Reveal - Visual eQTL Analysis");
				bw.newLine();
				
				//write header
				bw.write("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT");
				
				for(int i = 0; i < subjects.size(); i++) {
					bw.write("\t" + subjects.get(i).getID());
				}
				
				Genome g = ds.getGenome();
				
				for(int i = 0; i < snps.size(); i++) {
					SNP s = snps.get(i);
					
					bw.newLine();
					bw.write(g.getMappedSequenceName(s.getChromosome())); //CHROM
					bw.write("\t");
					bw.write(Integer.toString(s.getPosition())); //POS
					bw.write("\t");
					bw.write(s.getID()); //ID
					bw.write("\t");
					
					char ref = s.getReferenceNucleotide(); 
					
					bw.write(ref); //REF
					bw.write("\t");
					
					Set<Character> alternatives = new HashSet<Character>();
					alternatives.add(ref);
					
					for(int j = 0; j < subjects.size(); j++) {
						Haplotypes h = ds.getHaplotypes().get(subjects.get(j).getIndex());
						alternatives.add(h.getSNPA(s.getIndex()));
						alternatives.add(h.getSNPB(s.getIndex()));	
					}
					
					alternatives.remove(ref);
					ArrayList<Character> alternativesArray = new ArrayList<Character>(alternatives);
					
					String alt = "";
					if(alternativesArray.size() > 0) {
						alt += alternativesArray.get(0);
						for(int j = 1; j < alternativesArray.size(); j++) {
							alt += "," + alternativesArray.get(j);
						}
					}
					
					bw.write(alt); //ALT
					bw.write("\t");
					bw.write("."); //QUAL
					bw.write("\t");
					bw.write("."); //FILTER
					bw.write("\t");
					bw.write("AN=2"); //INFO
					bw.write("\t");
					bw.write("GT"); //FORMAT
					
					
					for(int j = 0; j < subjects.size(); j++) {
						Haplotypes h = ds.getHaplotypes().get(subjects.get(j).getIndex());
						
						char a = h.getSNPA(s.getIndex());
						char b = h.getSNPB(s.getIndex());
						
						char[] gt = {'0','|','0'};
						
						
						if(a != ref) {
							gt[0] = (alternativesArray.indexOf(a)+1+"").charAt(0);
						}
						
						if(b != ref) {
							gt[2] = (alternativesArray.indexOf(b)+1+"").charAt(0);
						}

						bw.write("\t");
						bw.write(gt); //Person
					}
				}
				
				bw.close();
				
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public String getName() {
		return "VCF Parser";
	}

	@Override
	public String getType() {
		return "data.io.vcf";
	}

	@Override
	public String getDescription() {
		return "VCF File Parser";
	}

	@Override
	public String getCategory() {
		return "Data Import & Export";
	}
}
