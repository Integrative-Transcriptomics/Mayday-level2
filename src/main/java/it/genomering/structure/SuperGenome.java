package it.genomering.structure;


import it.genomering.render.GenomeColors;
import it.genomering.render.RingDimensions;
import it.genomering.supergenome.GenomeRingBlocker;
import it.genomering.supergenome.XmfaBlock;
import it.genomering.supergenome.XmfaParser;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import mayday.core.EventFirer;
import mayday.core.Preferences;
import mayday.core.pluma.PluginInfo;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.tasks.AbstractTask;

public class SuperGenome {
	
	protected final static String LAST_PATH = "LAST_SUPERGENOME_PATH";
	
	protected int total_length;  // sum of all block lengths
	protected ArrayList<Block> blocks;
	protected ArrayList<Genome> genomes;
	protected ScalingInfo scalingInfo;
	protected GenomeColors initialColors;
	protected ArrayList<Block> initialBlockOrder;
	
	protected SuperGenomeSetting settings;
	
	protected boolean silent=false; //fire no events
	
	protected EventFirer<SuperGenomeEvent, SuperGenomeListener> firer = new EventFirer<SuperGenomeEvent, SuperGenomeListener>() {
		@Override
		protected void dispatchEvent(SuperGenomeEvent event,
				SuperGenomeListener listener) {
			listener.superGenomeChanged(event);
		}
	}; 
	
	public SuperGenome() {
		init();
	}
	
	protected void init() {
		this.blocks = new ArrayList<Block>();
		this.genomes = new ArrayList<Genome>();
		this.initialColors = new GenomeColors();
		total_length = 0;
		scalingInfo = null;
		settings = new SuperGenomeSetting();
	}
	
	public SuperGenome(String fileName) throws IOException {
		load(fileName);
	}
	
	public void storeInitialOrder() {
		initialBlockOrder = new ArrayList<Block>(getBlocks());
	}
	
	protected void save(final String fileName) throws IOException {
		AbstractTask task = new AbstractTask("SuperGenome") {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				writeLog("Writing SuperGenome blocks...\n");
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileName)));
				bw.write(SuperGenome.this.toString());
				bw.newLine();
				writeLog("Finished SuperGenome\n");
				
				for(int i = 0; i < genomes.size(); i++) {
					Genome g = genomes.get(i);
					bw.write(g.getName());
					List<CoveredBlock> gBlocks = g.getBlocks();
					
					if(gBlocks.size() > 0) {
						bw.write("\t");
						for(int j = 0; j < gBlocks.size(); j++) {
							CoveredBlock cb = gBlocks.get(j);
							if(!cb.forward) {
								bw.write("-");
							}
							
							bw.write(Integer.toString(cb.getIndex()+1));
							bw.write(":");
							bw.write(cb.getStart() + "-" + cb.getEnd());
							
							if(j != gBlocks.size()-1) {
								bw.write(",");
							}
						}
					}
					
					bw.newLine();
					writeLog("Finished Genome " + g.getName() + "\n");
				}
				
				bw.close();
				writeLog("Done!\n");
			}
		};
		task.start();
	}
	
	private int minBlockLength = 10000;
	private boolean subBlocks = false;
	private static String[] genomeIds = null;
	
	private void determineGenomeNames(String fileName) {
		Set<String> genomeIdentifier = new HashSet<String>(); 
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
			String line = null;
			while((line = br.readLine()) != null) {
				//the identifier is the first string without " " that follows until the first occurrence of ":" 
				if(line.startsWith(">")) {
					String[] split = line.split(":");
					if(split.length > 1) {
						String id = split[0];
						if(id.length() > 1) {
							//remove ">" and whitespaces
							id = id.substring(1).trim();
							genomeIdentifier.add(id);
						}
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		genomeIds = genomeIdentifier.toArray(new String[0]);
		Arrays.sort(genomeIds);
	}
	
	protected void load(final String fileName) throws IOException {	
		
		if(!fileName.toLowerCase().endsWith(".blocks")) {
			
			if(settings == null) {
				settings = new SuperGenomeSetting();
			}
			
			SettingDialog dialog = new SettingDialog(null, "Super Genome Settings Dialog", settings);
			dialog.showAsInputDialog();
			
			if(!dialog.closedWithOK()) {
				return;
			}
			
			minBlockLength = settings.blockLength.getIntValue();
			subBlocks = settings.createSubBlocks.getBooleanValue();
		}
		
		AbstractTask task = new AbstractTask("SuperGenome") {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				BufferedReader br;
				//file must be in the xmfa format!
				if(!fileName.toLowerCase().endsWith(".blocks")) {
					//automatic number of genomes determination
					writeLog("Determining number of genomes in the xmfa file ...\n");
					determineGenomeNames(fileName);

					PipedInputStream in = new PipedInputStream();
					final PipedOutputStream out = new PipedOutputStream(in);
					new Thread(new Runnable(){
						public void run(){
							try {
								ringMode(out, fileName, minBlockLength, subBlocks);
							} catch (Exception e) {
								e.printStackTrace();
							}
					    }
					}).start();
					
					writeLog("Parsing XMFA file...\n");
					
					br = new BufferedReader(new InputStreamReader(in));
				} else { //or use a blocks file instead
					br = new BufferedReader(new FileReader(new File(fileName)));
				}

				Block[] blocks;
				
				// Read the blocks
				String blockline = br.readLine();
				if(blockline.trim().equals("")) {
					JOptionPane.showMessageDialog(null, "The SuperGenome contains no blocks!" +
							"\nUse less strict block length filter.", "Error", JOptionPane.ERROR_MESSAGE);

					br.close();
					writeLog("Error - Stopped!\n");
					return;
				}
				
				init();
				
				writeLog("Creating SuperGenome blocks...\n");
				
				String[] blockInfos = blockline.split(",");
				blocks = new Block[blockInfos.length];
				
				for (int i=0; i!=blockInfos.length; ++i) {			
					String blockInfo = blockInfos[i];
					// get block name if included
					int pos = blockInfo.indexOf(":");
					String name = Integer.toString(i+1);
					if (pos>-1) {
						name = blockInfo.substring(pos+1);
						blockInfo = blockInfo.substring(0,pos);
					}
					// find out which format was used
					pos = blockInfo.indexOf('-');			
					if (pos==-1) { // "length" format
						int length = Integer.parseInt(blockInfo);				
						blocks[i] = new Block(SuperGenome.this, name, length);
					} else { // "start-end" format
						int start = Integer.parseInt(blockInfo.substring(0,pos));
						int end = Integer.parseInt(blockInfo.substring(pos+1));			
						blocks[i] = new Block(SuperGenome.this, name, start, end);
					}
					addBlock(blocks[i]);
				}
				
				// Read the Genomes		
				ArrayList<Genome> _genomes = new ArrayList<Genome>();
				
				String genomeLine;
				while ( (genomeLine=br.readLine())!=null ) {
					String[] name_and_stuff = genomeLine.split("\t");
					String name = name_and_stuff[0];
					writeLog("Calculating covered blocks for genome " + name + "\n");
					if(name_and_stuff.length == 1) {
						writeLog("Genome " + name + " has no blocks!\n");
						continue;
					}
					Genome g = new Genome(SuperGenome.this, false, name); // TODO: im moment keine Zirkulären
					String[] parts = name_and_stuff[1].split(",");
					for (int i=0; i<parts.length; ++i) {
						String partInfo = parts[i];
						String position = null;
						int pos = partInfo.indexOf(':');
						if (pos!=-1) { // "[-]block:start-end" format
							position = partInfo.substring(pos+1);
							partInfo = partInfo.substring(0, pos);					
						}
						CoveredBlock nextBlock;
						int value = Integer.parseInt(partInfo);
						int bidx = Math.abs(value)-1;
						boolean bstrand = value>0;
						nextBlock = new CoveredBlock(blocks[bidx],bstrand);
						if (position!=null) { // Block has specific coordinates in Genome's space
							pos = position.indexOf('-');
							int start = Integer.parseInt(position.substring(0,pos));
							int end = Integer.parseInt(position.substring(pos+1));	
							nextBlock.setLocationInGenome(start, end);
						} else {
							nextBlock.setLocationInGenome(blocks[bidx].getStart(), blocks[bidx].getEnd());
						}
						g.addCoveredBlock(nextBlock);
					}
					_genomes.add(g);
				}
				
				// prepare colors
				initialColors.getColor(_genomes.size()-1);
				// add genomes
				for (Genome g : _genomes)
					addGenome(g);
				
				br.close();
								
				storeInitialOrder();
				
				fireChange(SuperGenomeEvent.GENOMES_CHANGED);
				
				writeLog("Finished!\n");
			}
			
		};
		
		task.start();
	}
	
	public void addBlock(Block b) {
		b.index = blocks.size();
		b.setOffset(total_length);
		blocks.add(b);
		total_length += b.getLength();
		scalingInfo = null;
	}
	
	public int getNumberOfBases() {
		return total_length;
	}
	
	public int getLastBase() {
		return blocks.get(blocks.size()-1).getEnd();
	}
	
	public void setBlocks(List<Block> newOrder) {
		blocks.clear();
		blocks.addAll(newOrder);
		blockOrderChanged();
	}
	
	public void blockOrderChanged() {
		int currentStart=0;
		for (int i=0; i!=blocks.size(); ++i) {
			Block b = blocks.get(i);
			b.index = i;
			b.start = -1; // now we lose the original SG coordinates
			b.setOffset(currentStart);
			currentStart+=b.length;
		}
		scalingInfo = null;
		fireChange(SuperGenomeEvent.BLOCKS_CHANGED);
	}
	
	public void genomeNamesChanged() {
		fireChange(SuperGenomeEvent.GENOMES_CHANGED);
	}
	
	public List<Block> getBlocks() {
		return Collections.unmodifiableList(blocks);
	}
	
	public List<Block> getInitialBlockOrder() {
		if (initialBlockOrder==null) {
			if (blocks.size()==0)
				return Collections.emptyList();			
			storeInitialOrder();
		}		
		return Collections.unmodifiableList(initialBlockOrder);
	}
	
	public List<Block> getBlocksInternal() {
		return blocks;
	}
	
	public void addGenome(Genome g) {
		genomes.add(g);
		// set color of that genome
		if (g.getColor()==null) {
			int index = genomes.size()-1;
			Color c = initialColors.getColor(index);
			g.setColor(c);			
		}
	}
	
	public int getNumberOfGenomes() {
		return genomes.size();
	}
	
	public int getIndex(Genome genome) {
		return genomes.indexOf(genome); // not very efficient but we only have about 15 genomes tops
	}

	public List<Genome> getGenomes() {
		// update colors if genome was added
		return Collections.unmodifiableList(genomes);
	}

	public int getNumberOfBlocks() {
		return blocks.size();
	}
	
	
	/* == Coordinate system mapping ==
	 * There are four types of coordinate systems
	 * - Angular coordinates (0-360°), translated from sgOffsets using RingDimensions
	 * - sgOffsets: base offset in the supergenome as stored here (i.e. in the range [0,total_length[)
	 * - sgPosition: "real" position in the superGenome, according for gaps between blocks, i.e. including Block.start info
	 * - gPosition: real base position in a given Genome g, according to CoveredBlock.start info
	 */
	


	// ============0
	
	
	public int getMaximalOuterSkip(RingDimensions ringdim) {
		int mos=0;
		for (Genome g : genomes) {
			mos = Math.max(mos, g.getMaximalOuterSkip(ringdim));
		}
		return mos;
	}
	
	protected void checkScalingInfo(){
		if (scalingInfo==null) {	
			this.scalingInfo = new ScalingInfo(getNumberOfBlocks());
			for (int i=0; i!=getNumberOfBlocks(); ++i) {
				scalingInfo.setSize(i, blocks.get(i).getLength());
			}
		}
	}
	
	public void addListener(SuperGenomeListener sgl) {
		firer.addListener(sgl);
	}
	
	public void removeListener(SuperGenomeListener sgl) {
		firer.removeListener(sgl);
	}
	
	protected void fireChange(int change) {
		switch(change) {
		case SuperGenomeEvent.SILENT_MODE:
		case SuperGenomeEvent.NO_SILENT_MODE:
			firer.fireEvent(new SuperGenomeEvent(this, change));
			break;
		default:
			if (!silent) {
				firer.fireEvent(new SuperGenomeEvent(this, change));
			}
		}
	}
	
	public void setSilent(boolean silent) {
		this.silent = silent;
		
		if(this.silent) {
			fireChange(SuperGenomeEvent.SILENT_MODE);
		} else {
			fireChange(SuperGenomeEvent.NO_SILENT_MODE);
		}
	}
	
	@SuppressWarnings("serial")
	public class SaveToBlocksFileAction extends AbstractAction {
		public SaveToBlocksFileAction() {
			super("Save SuperGenome ...");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser jfc = new JFileChooser();
			jfc.setAcceptAllFileFilterUsed(false);
			
			jfc.addChoosableFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					
					if(f.isDirectory())
						return true;
					
					String path = f.getAbsolutePath();
					String[] ending = path.split("\\.");
					if(ending.length == 0)
						return false;
					
					String end = ending[ending.length-1];
					//accept blocks file
					if(end.toLowerCase().equals("blocks")) {
						return true;
					}
					
					return false;
				}

				@Override
				public String getDescription() {
					return "SuperGenome Blocks File (.blocks)";
				}
			});
			
			Preferences pref = PluginInfo.getPreferences("IT.GenomeRing.SuperGenome");
			String lastDir = pref.get(LAST_PATH, "");			
			jfc.setCurrentDirectory(new File(lastDir));
			
			if (jfc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION) {
				pref.put(LAST_PATH, jfc.getSelectedFile().getParent());
				try {
					SuperGenome.this.save(jfc.getSelectedFile().getAbsolutePath());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	@SuppressWarnings("serial")
	public class LoadFromFileAction extends AbstractAction {
		public LoadFromFileAction() {
			super("Load SuperGenome ...");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser jfc = new JFileChooser();
			jfc.setAcceptAllFileFilterUsed(false);
			
			//accept xmfa files
			jfc.addChoosableFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					//accept folders
					if(f.isDirectory())
						return true;
					
					String path = f.getAbsolutePath();
					String[] ending = path.split("\\.");
					if(ending.length == 0)
						return false;
					String end = ending[ending.length-1];
					//accept blocks file
					if(end.toLowerCase().equals("xmfa")) {
						return true;
					}
					return false;
				}

				@Override
				public String getDescription() {
					return "Mauve XMFA Alignment File (.xmfa)";
				}
			});
			
			//accept blocks files
			jfc.addChoosableFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					//accept folders
					if(f.isDirectory())
						return true;
					
					String path = f.getAbsolutePath();
					String[] ending = path.split("\\.");
					if(ending.length == 0)
						return false;
					String end = ending[ending.length-1];

					if(end.toLowerCase().equals("blocks")) {
						return true;
					}
					return false;
				}

				@Override
				public String getDescription() {
					return "SuperGenome Blocks File (.blocks)";
				}
			});
			
			// store last used directory in supergenome plugin storage
			Preferences pref = PluginInfo.getPreferences("IT.GenomeRing.SuperGenome");
			String lastDir = pref.get(LAST_PATH, "");			
			jfc.setCurrentDirectory(new File(lastDir));
			if (jfc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				pref.put(LAST_PATH, jfc.getSelectedFile().getParent());
				try {
					SuperGenome.this.load(jfc.getSelectedFile().getAbsolutePath());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public static void ringMode(PipedOutputStream out, String inputFilePath, int blockLength, boolean createSubBlocks) throws Exception
	{
//		if(ids.length!=Config.getInt("numberOfDatasets"))
//			throw new Error("numberOfDatasets does not match length of idList!");
		
		//output
		//String outDir = Config.getString("outputDirectory");
		
		//read alignment
		System.out.println("Reading alignment blocks...");
		List<XmfaBlock> alignmentBlocks = XmfaParser.parseXmfa(inputFilePath);
		
		//SuperGenome
		System.out.println("Building SuperGenome...");
		it.genomering.supergenome.SuperGenome superG = new it.genomering.supergenome.SuperGenome(alignmentBlocks, genomeIds);
		
		GenomeRingBlocker grb = new GenomeRingBlocker(superG.getRefBlocks(), genomeIds, blockLength, createSubBlocks);
		
		//BlockMap
		//BufferedWriter bw = new BufferedWriter(new FileWriter(outDir+"blocks.out"));
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
		
		List<int[]> blockPositions = new LinkedList<int[]>();
		List<int[]> tmpPos;
		int tmpStart;
		
//		int lengthCount = 0;
		
		for(XmfaBlock b:grb.newBlockList)
		{
			tmpStart = superG.superGenomifyXmfaStart(b);
			
			tmpPos = b.getSubBlockPositions();
			for(int[] posPair:tmpPos)
			{
				posPair[0] = posPair[0]+tmpStart;
				posPair[1] = posPair[1]+tmpStart;
				
//				lengthCount += posPair[1] - posPair[0] + 1;
			}
				
			blockPositions.addAll(tmpPos);
		}
		
		boolean first = true;
		for(int[] posPair:blockPositions)
		{
			if(first)
				first=false;
			else
				bw.append(",");
				
			bw.append(posPair[0]+"-"+posPair[1]);
		}
		bw.newLine();
		
		int nameCounter = 0;
		for(String id : genomeIds)
		{
			bw.append(genomeIds[nameCounter++]+"\t");
			first = true;
			for(Integer i:grb.getGenomeBlockLists().get(id))
			{
				if(first)
					first=false;
				else
					bw.append(",");
				
				int start = Math.abs(superG.getNextMappingPosInGenome(id, blockPositions.get(Math.abs(i)-1)[0]));
				int stopInGenome = blockPositions.get(Math.abs(i)-1)[1];
				
				int stop;
				/*
				 * FIXME is there a better solution for that case?!
				 * prevent from having stop = 0 when there is no mappable position in the genome from the supergenome
				 * set stop in this case to the last mappable position
				 */
				while((stop = Math.abs(superG.getNextMappingPosInGenome(id, stopInGenome))) == 0) {
					stopInGenome--;
				}
				
				bw.append(Integer.toString(i)+":"+  start  +"-"+  stop);
			}
			bw.newLine();
		}
		
		bw.close();
	}
	
	public static int i(boolean b)
	{
		if(b)
			return(1);
		return(0);
	}
	
	/**
	 * Setting for loading xmfa files
	 * @author jaeger
	 *
	 */
	private class SuperGenomeSetting extends HierarchicalSetting {

		BooleanSetting createSubBlocks;
		IntSetting blockLength;
		
		public SuperGenomeSetting() {
			super("Super Genome Setting");
			
			blockLength = new IntSetting("Minimal Block Length", null, 10000);
			createSubBlocks = new BooleanSetting("Create Sub-Blocks", null, false);
			
			addSetting(blockLength);
			addSetting(createSubBlocks);
		}
		
		@Override
		public SuperGenomeSetting clone() {
			SuperGenomeSetting sgs = new SuperGenomeSetting();
			sgs.fromPrefNode(this.toPrefNode());
			return sgs;
		}
	}
	
	public String toString() {
		if(blocks.size() == 0)
			return "";
		
		String superGenomeString = blocks.get(0).toOutputString();
		
		for(int i = 1; i < blocks.size(); i++) {
			Block b = blocks.get(i);
			superGenomeString += "," + b.toOutputString();
		}
		
		return superGenomeString;
	}
}
