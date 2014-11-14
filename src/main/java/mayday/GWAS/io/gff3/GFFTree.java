package mayday.GWAS.io.gff3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import mayday.GWAS.data.meta.MetaInformationPlugin;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.PathSetting;
import mayday.core.tasks.AbstractTask;

public class GFFTree extends MetaInformationPlugin {
	
	public static final String MYTYPE = "GFF";
	
	private Map<String, GFFElement> elementMapping;
	
	private String gff3FilePath;
	
	public GFFTree() {
		elementMapping = new HashMap<String, GFFElement>();
	}
	
	public void addGFFElement(String functionType, String type, ChromosomalLocation chrLoc, int phase, String ID, String name, String parent) {
		GFFElement parentElement = null;
		
		if(elementMapping.containsKey(parent)) {
			parentElement = elementMapping.get(parent);
		}
		
		GFFElement e = new GFFElement(functionType, type, ID, name, parentElement, chrLoc, phase);
		
		if(ID != null) {
			elementMapping.put(ID, e);
		}
		
		//assumption: parent is contained already
		//TODO is it possible that children are added before their parents are?
		if(parentElement != null) {
			parentElement.addChild(e);
		}
	}
	
	public GFFElement getGFFElement(String ID) {
		return this.elementMapping.get(ID);
	}
	
	public boolean containsGFFElement(String ID) {
		return this.elementMapping.containsKey(ID);
	}
	
	public boolean containsGFFElement(GFFElement e) {
		return this.elementMapping.containsValue(e);
	}
	
	public boolean buildTree(String gff3FilePath) {
		this.gff3FilePath = gff3FilePath; //new file path
		
		File file = new File(this.gff3FilePath);
		//check if file exists and specify new location if it cannot be found
		if(!file.exists()) {
			int approve = JOptionPane.showConfirmDialog(null, "The GFF file " + gff3FilePath + " could not be found!" +
					"\nPlease specify the correct file location.", "GFF not found!", JOptionPane.OK_CANCEL_OPTION);
			if(approve == JOptionPane.OK_OPTION) {
				while(true) {
					PathSetting ps = new PathSetting("GFF file", "Specify the GFF file location", null, false, true, false);
					SettingDialog sd = new SettingDialog(null, "Select the GFF file ...", ps);
					sd.showAsInputDialog();
					
					if(sd.closedWithOK()) {
						File newFile = new File(ps.getStringValue());
						if(!newFile.getName().equals(file.getName())) {
							approve = JOptionPane.showConfirmDialog(null, "The file name of your selected file (" + newFile.getName() + ") does not match the expected file name (" + file.getName() + ")." +
									"\nPlease provide the correct file.", "GFF files do not match", JOptionPane.OK_CANCEL_OPTION);
							if(approve == JOptionPane.OK_OPTION) {
								//try again
								continue;
							} else {
								return false;
							}
						}
						//files match -> continue with regular code
						break;
					} else {
						return false;
					}
				}
				//continue with regular code
			} else {
				return false;
			}
		}
		
		//clean up old gff tree, if existing
		if(elementMapping.size() > 0) {
			elementMapping.clear();
			System.gc();
		}
		
		//do calculations in a task, since this may take some time
		AbstractTask task = new AbstractTask("Parse GFF File ...") {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				writeLog("Importing GFF information...\n\t");
				writeLog("this can take several minutes...\n");
				
				File file = new File(GFFTree.this.gff3FilePath);
				
				//make sure gff3 file exists
				if(!file.exists()) {
					int approve = JOptionPane.showConfirmDialog(null, "No genome annotation found at the given location:\n\t"
							+ GFFTree.this.gff3FilePath + "\n\n"
							+ "Change genome annotation file location?");
					
					if(approve == JOptionPane.OK_OPTION) {
						JFileChooser fc = new JFileChooser("Select GFF3 File");
						fc.showOpenDialog(null);
						file = fc.getSelectedFile();
						GFFTree.this.gff3FilePath = file.getAbsolutePath();
					} else {
						writeLog("No GFF3 file found!\n");
						this.cancel();
						return;
					}
				}
				
				BufferedReader br = new BufferedReader(new FileReader(GFFTree.this.gff3FilePath));
				String line = null;
				
				while((line = br.readLine()) != null) {
					//skip comment lines and empty lines
					line = line.trim();
					if(line.length() == 0 || line.startsWith("#"))
						continue;
					
					//line is not empty, assume \t separator
					String[] splitted = line.split("\t");
					
					//gff3 files have exactly 9 columns!
					if(splitted.length != 9) {
						br.close();
						elementMapping.clear();
						throw new IOException("GFFTree: The gff3 file is not in the right format!");
					}
					
					//parse gff3 columns
					String seqID = splitted[0];
					String source = splitted[1];
					String feature = splitted[2];
					
					//skip genomic regions, we just need the elements contained in the regions
					if(source.equals("Genomic") || feature.equals("region"))
						continue;
					
					int start = Integer.parseInt(splitted[3]);
					int stop = Integer.parseInt(splitted[4]);
					char strand = splitted[6].charAt(0);
					int phase = splitted[7].equals(".") ? 0 : Integer.parseInt(splitted[7]);
					String attributes = splitted[8];
					
					String[] attSplitted = attributes.split(";");
					
					String id = null;
					String name = null;
					String parent = null;
					
					for(int i = 0; i < attSplitted.length; i++) {
						String s = attSplitted[i];
						if(s.startsWith("ID")) {
							id = s.split("=")[1];
						} else if(s.startsWith("Name")) {
							name = s.split("=")[1];
						} else if(s.startsWith("Parent")) {
							parent = s.split("=")[1];
						}
					}
					
					ChromosomalLocation loc = new ChromosomalLocation(seqID, start, stop, strand);
					addGFFElement(source, feature, loc, phase, id, name, parent);
				}
				
				br.close();
				
				// Get the Java runtime
			    Runtime runtime = Runtime.getRuntime();
			    // Run the garbage collector
			    runtime.gc();
			    // Calculate the used memory
			    long memory = runtime.totalMemory() - runtime.freeMemory();
			    memory /= 1024L;
			    System.out.println("Used Memory: " + memory + " kb\n");
				
				writeLog("GFF imported!\n");
				
			}
		};
		
		task.start();
		task.waitFor();
		
		if(!task.hasBeenCancelled()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void serialize(BufferedWriter bw) throws IOException {
		bw.append("$META\n");
		bw.append(getCompleteType());
		bw.append("\n");
		bw.append(gff3FilePath);
		bw.append("\n");
	}

	@Override
	public boolean deSerialize(String serial) {
		BufferedReader br = new BufferedReader(new StringReader(serial));
		try {
			return this.buildTree(br.readLine());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Class<?> getResultClass() {
		return GFFTree.class;
	}

	@Override
	public String getName() {
		return "GFF Tree";
	}

	@Override
	public String getType() {
		return "data.meta."+MYTYPE;
	}

	@Override
	public String getDescription() {
		return "GFF file in tree representation";
	}
	
	public String toString() {
		if(gff3FilePath == null) {
			return "GFF3 file";
		}
		File f = new File(gff3FilePath);
		String name = f.getName();
		return name + " (" + elementMapping.size() + ")";
	}

	public Map<GFFElement, Integer> getElementsAtPosition(String chromosome, long position, long upstream, long downstream) {
		Map<GFFElement, Integer> elements = new HashMap<GFFElement, Integer>();
		for(String ID : elementMapping.keySet()) {
			GFFElement e = elementMapping.get(ID);
			
			switch(e.getChromosomalLocation().contains(chromosome, position, upstream, downstream)) {
			case ChromosomalLocation.UPSTREAM:
				elements.put(e, ChromosomalLocation.UPSTREAM);
				break;
			case ChromosomalLocation.DOWNSTREAM:
				elements.put(e, ChromosomalLocation.DOWNSTREAM);
				break;
			case ChromosomalLocation.CONTAINED:
				elements.put(e, ChromosomalLocation.CONTAINED);
			}
		}
		return elements;
	}
}
