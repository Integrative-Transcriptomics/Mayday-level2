package mayday.GWAS.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.Gene;
import mayday.GWAS.data.GeneList;
import mayday.GWAS.data.Haplotypes;
import mayday.GWAS.data.HaplotypesList;
import mayday.GWAS.data.ProjectHandler;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.data.Subject;
import mayday.GWAS.data.SubjectList;
import mayday.GWAS.data.meta.MetaInformationPlugin;
import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.io.ReadyBufferedReader;
import mayday.core.io.dataset.SimpleSnapshot.Snapshot;
import mayday.core.pluma.PluginManager;
import mayday.core.tasks.AbstractTask;

public class SnapshotReader extends AbstractDataParser {

	private ProjectHandler projectHandler;
	private AbstractTask task;
	
	public SnapshotReader() {
		this(null);
	}
	
	public void setProjectHandler(ProjectHandler projectHandler) {
		this.projectHandler = projectHandler;
	}
	
	public SnapshotReader(ProjectHandler projectHandler) {
		this.projectHandler = projectHandler;
	}
	
	public void setProcessingTask(AbstractTask task) {
		this.task = task;
	}
	
	@SuppressWarnings({ "rawtypes" })
	@Override
	public void read(File input) {
		try {
			ZipFile zFile = new ZipFile(input);
			Enumeration entries = zFile.entries();
			
			Map<String, DataSet> dataSets = new HashMap<String, DataSet>();
			Map<String, DataStorage> dataStorages = new HashMap<String, DataStorage>();
			
			while(entries.hasMoreElements()) {
				ZipEntry ze = (ZipEntry)entries.nextElement();
				
				//the zip-entry corresponds to a mayday dataset file
				if(ze.getName().endsWith(".dataset")) {
					if(task != null) {
						task.setProgress(0, "Reading Mayday Snapshot ...\n");
					} else {
						System.out.println("Reading Mayday Snapshot ...");
					}
					
					ReadyBufferedReader br = new ReadyBufferedReader(new InputStreamReader(zFile.getInputStream(ze)));
		            Snapshot snap = Snapshot.getCorrectVersion(br);
		            
		            if (snap == null)
		            	throw new Exception("Snapshot format not supported");
		            
		            snap.setProcessingTask(task);
					snap.read(br);
					DataSet ds = snap.getDataSet();
					dataSets.put(ds.getName(), ds);
					
					br.close();
				}
			}
			
			entries = zFile.entries();
			
			while(entries.hasMoreElements()) {
				ZipEntry ze = (ZipEntry)entries.nextElement();
				
				if(ze.getName().equals("Reveal.txt")) {
					if(task != null) {
						task.setProgress(0, "Reading Reveal Snapshot ...\n");
					} else {
						System.out.println("Reading Reveal Snapshot ...");
					}
					
					ReadyBufferedReader br = new ReadyBufferedReader(new InputStreamReader(zFile.getInputStream(ze)));
					String line = br.readLine();
					DataStorage dataStorage = null;
					
					while(line != null) {
						//skip empty lines
						if(line.trim().equals(""))
							line = br.readLine();
						
						if(line != null && line.startsWith("$$DATASTORAGE")) {
							dataStorage = new DataStorage(projectHandler);
							String[] attributes = br.readLine().split("\t");
							String dataSetName = br.readLine();
							
							dataStorage.getAttribute().setName(attributes[0]);
							dataStorage.getAttribute().setInformation(attributes[1]);
							
							DataSet dataSet = dataSets.get(dataSetName);
							dataStorage.setDataSet(dataSet);

							dataStorages.put(dataSetName, dataStorage);
							//done parsing dataStorage, continue with next line
							line = br.readLine();
						}
						
						if(line != null && line.startsWith("$GENES")) {
							
							if(task!=null)
								task.writeLog("Reading gene information ...\n");
							
							GeneList genes = new GeneList(dataStorage.getDataSet());
							while((line = br.readLine())!= null && !line.startsWith("$")) {
								//skip empty lines
								if(line.trim().equals(""))
									continue;
								String[] geneLine = line.split("\t");
								String geneName = geneLine[0];
								Probe pb = genes.getDataSet().getMasterTable().getProbe(geneName);
								Gene gene = new Gene(pb.getMasterTable());
								gene.setName(pb.getName());
								if(geneLine.length > 1)
									gene.setStartPosition(Integer.parseInt(geneLine[1]));
								if(geneLine.length > 2)
									gene.setStopPosition(Integer.parseInt(geneLine[2]));
								if(geneLine.length > 3)
									gene.setChromosome(geneLine[3]);
								gene.setValues(pb.getValues());
								
								genes.addGene(gene);
							}
							
							dataStorage.setGenes(genes);
						}
						
						if(line != null && line.startsWith("$SNPLISTS")) {
							
							if(task != null)
								task.writeLog("Reading SNPList information ...\n");
							
							line = br.readLine();
							while(line != null && line.startsWith(">")) {
								line = line.substring(1);
								String[] snpListLine = line.split("\t");
								SNPList snpList = new SNPList(snpListLine[0], dataStorage);
								snpList.getAttribute().setInformation(snpListLine[1]);
								
								while((line = br.readLine()) != null && 
										!(line.startsWith(">") || line.startsWith("$"))) {
									//skip empty lines
									if(line.trim().equals(""))
										continue;
									
									if(snpList.getAttribute().getName().equals("Global")) {
										//no snps have been created so far
										String[] snpLine = line.split("\t");
										SNP s = new SNP(snpLine[0], snpLine[2].charAt(0), Integer.parseInt(snpLine[5]));
										s.setPosition(Integer.parseInt(snpLine[1]));
										s.setChromosome(snpLine[3]);
										s.setGene(snpLine[4]);
										s.setGeneticDistance(Double.parseDouble(snpLine[6]));
										snpList.add(s);
									} else {
										//the snp has already been created
										//it just has to be added to a second list
										SNPList global = dataStorage.getGlobalSNPList();
										SNP s = global.get(line.split("\t")[0]);
										if(s != null)
											snpList.add(s);
									}
								}
								
								dataStorage.addSNPList(snpList.getAttribute().getName(), snpList);
							}
						}
						
						if(line != null && line.startsWith("$PERSONS")) {
							if(task != null)
								task.writeLog("Reading patient information ...\n");
							
							line = br.readLine();
							SubjectList pl = new SubjectList(Integer.parseInt(line));
							while((line = br.readLine()) != null && !line.startsWith("$")) {
								//skip empty lines
								if(line.trim().equals(""))
									continue;
								String[] personLine = line.split("\t");
								String familyID = personLine[0];
								String individualID = personLine[1];
								boolean affection = Boolean.parseBoolean(personLine[2]);
								String paternalID = personLine[3];
								String maternalID = personLine[4];
								int index = Integer.parseInt(personLine[5]);
								
								Subject p = new Subject(familyID, individualID, paternalID, maternalID, affection);
								p.setIndex(index);
								pl.add(p);
							}
							dataStorage.setSubjects(pl);
						}
						
						if(line != null && line.startsWith("$HAPLOTYPES")) {
							if(task != null)
								task.writeLog("Reading haplotype information ...\n");
							line = br.readLine();
							int numHaplotypes = Integer.parseInt(line);
							HaplotypesList hl = new HaplotypesList(numHaplotypes);
							while((line = br.readLine()) != null && !line.startsWith("$")) {
								//skip empty lines
								if(line.trim().equals(""))
									continue;
								//parse first allel
								String[] allels = line.split("\t");
								allels[0] = allels[0].substring(1, allels[0].length()-1);
								allels[1] = allels[1].substring(1, allels[1].length()-1);
								String[] valuesAsString1 = allels[0].split(", ");
								String[] valuesAsString2 = allels[1].split(", ");
								
								byte[] allel1 = new byte[valuesAsString1.length];
								for(int i = 0; i < valuesAsString1.length; i++)
									if(!valuesAsString1[i].equals(""))
										allel1[i] = Byte.parseByte(valuesAsString1[i]);
								byte[] allel2 = new byte[valuesAsString2.length];
								for(int i = 0; i < valuesAsString2.length; i++)
									if(!valuesAsString2[i].equals(""))
										allel2[i] = Byte.parseByte(valuesAsString2[i]);
								
								if(allel1.length != allel2.length) {
									br.close();
									throw new Exception("Haplotype arrays do not have the same length!");
								}
								
								Haplotypes h = new Haplotypes(allel1.length);
								h.setAllel1(allel1);
								h.setAllel2(allel2);
								
								hl.add(h);
							}
							dataStorage.setHaplotypes(hl);
						}
						
//						if(line != null && line.startsWith("$GENOME")) {
//							if(task != null)
//								task.writeLog("Reading genome information ...\n");
//							StringBuffer buf = new StringBuffer();
//							String path = null;
//							while((line = br.readLine()) != null && !line.startsWith("$")) {
//								if(line.startsWith(">")) {
//									if(buf.length() > 0) {
//										Genome index = new Genome(path);
//										index.deSerialize(buf.toString());
//										buf = new StringBuffer();
//										if(index.getNumberOfSequences() > 0) {
//											projectHandler.addGenome(index);
//											dataStorage.setGenome(index);
//										} else {
//											if(task != null) {
//												task.writeLog("ERROR: Genome file\n\t" + path + "\n" + "could not be loaded!");
//											}
//										}
//									}
//									path = line;
//								}
//								buf.append(line);
//								buf.append("\n");
//							}
//							
//							if(buf.length() > 0) {
//								Genome index = new Genome(path);
//								index.deSerialize(buf.toString());
//								projectHandler.addGenome(index);
//								//only one genome allowed for a single project
//								dataStorage.setGenome(index);
//							}
//							
//							//done parsing, continue with next line
//						}
						
						//parse meta information
						if(line != null && line.startsWith("$META")) {
							
							String miType = br.readLine();
							StringBuffer serial = new StringBuffer();
							
							MetaInformationPlugin mip = (MetaInformationPlugin)PluginManager.getInstance().getInstance(miType);
							mip.setDataStorage(dataStorage);
							
							String[] typeSplit = mip.getType().split("\\.");
							String theType = typeSplit[typeSplit.length-1];
							
							if(task != null)
								task.writeLog("Importing meta-information (" + theType + ") ...\n");
							
							while((line = br.readLine()) != null && !(line.startsWith("$"))) {
								serial.append(line);
								serial.append("\n");
							}

							//we are done parsing meta information
							boolean success = mip.deSerialize(serial.toString());
							
							if(success) {
								dataStorage.getMetaInformationManager().add(theType, mip);
								if(task != null)
									task.writeLog("\t Done.\n");
							} else
								if(task != null) 
									task.writeLog("\t" + theType + " import canceled!\n");
						}
					}
					
					br.close();
				}
			}
			
			//add datastorages to the project-handler 
			//and add all datasets to maydays dataset manager
			for(String dataSetName : dataSets.keySet()) {
				DataSetManager.singleInstance.addObject(dataSets.get(dataSetName));
			}
			for(String dataSetName : dataStorages.keySet()) {
				DataStorage ds = dataStorages.get(dataSetName);
				projectHandler.add(ds);
				projectHandler.setupViewModel(ds);
			}
			//everything is done, close the zip file
			zFile.close();
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(File output) {
		//nothing to do here
	}

	@Override
	public String getName() {
		return "Snapshot Reader";
	}

	@Override
	public String getType() {
		return "data.snapshot";
	}

	@Override
	public String getDescription() {
		return "Import Reveal Snapshot Files";
	}

	@Override
	public String getCategory() {
		return "Data Import & Export";
	}
}
