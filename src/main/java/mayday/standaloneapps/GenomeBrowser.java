package mayday.standaloneapps;



import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.prefs.BackingStoreException;

import javax.swing.JOptionPane;

import mayday.core.DataSet;
import mayday.core.Experiment;
import mayday.core.MasterTable;
import mayday.core.Preferences;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.gui.MaydayWindowManager;
import mayday.core.meta.MIGroup;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.FilesSetting;
import mayday.core.settings.typed.StringMapSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.tasks.AbstractTask;
import mayday.genetics.Locus;
import mayday.genetics.LocusMIO;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.GeneticCoordinate;
import mayday.genetics.sequences.FastaChromosomeSequenceFactory;
import mayday.vis3.PlotPlugin;

public class GenomeBrowser extends AbstractPlugin implements GenericPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.GenomeTracks.MakeGenome",
				new String[0],
				Constants.MC_SUPPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"",
				"Make a new genome"
		);
		return pli;	
	}

	@Override
	public void run() {
		

//		int[] seq = new int[4];
//		int count=0;
//		int extracount=0;
//		int total=0;
//
//		for (int l0=0; l0!=4; ++l0) {
//			for (int l1=0; l1!=4; ++l1) {
//				for (int l2=0; l2!=4; ++l2) {
//					for (int l3=0; l3!=4; ++l3) {
////						for (int l4=0; l4!=4; ++l4) {
////							for (int l5=0; l5!=4; ++l5) {
//								seq[0]=l0;
//								seq[1]=l1;
//								seq[2]=l2;
//								seq[3]=l3;
////								seq[4]=l4;
////								seq[5]=l5;
//								++total;
//								// count all occurrences of motif "23" = GT
//								int lastSeen1 = seq[0];
//								for (int m=2; m!= seq.length; ++m) {
//									int nowSeen = seq[m];
//									if (lastSeen1==2 && nowSeen==3)
//										++count;
//									lastSeen1 = nowSeen;
//								}
//								// count the border case
//								if (seq[0]==3 && seq[seq.length-1]==2)
//									++extracount;
////							}
////						}
//					}
//				}
//			}
//		}
//
//		double percentage = ((double)count+(double)extracount) / (double)total;
//		System.out.println((count+extracount)+" patterns in "+total+" sequences --> "+percentage);
//		System.out.println(count+" regular patterns");
//		System.out.println(extracount+" wrapping patterns");
//		System.exit(0);
		
//		try {
//			FastaChromosomeSequenceFactory.addChromsomeSequences(new File("/tmp/test.fa"), "hsa");
//		} catch (Exception e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
//		ChromosomeSequence cs = SequenceContainer.getDefault().getSequence( "xsa", "1" );
//		System.out.println(cs.length()+"\t"+cs.subSequence(0, 10));
//		cs = SequenceContainer.getDefault().getSequence( "hsa", "1" );
//		System.out.println(cs.length()+"\t"+cs.subSequence(0, 10));
//		System.out.println(cs.length()+"\t"+cs.subSequence(5000, 5010));
		

		DataSet ds = new DataSet("Genome dataset");

		Map<String, String> theMap = new HashMap<String, String>();

		final StringMapSetting sms = new StringMapSetting("by manual specification",
				"Define chromosomes by putting a name in the left column\n and the length (bases) in the right column.\n" +
				"The name should contain a species and the chromosome id, \ne.g. \"Sco X\" for Streptomyces coelicolor, chromosome x",
				theMap);

		final FilesSetting fastaFiles = new FilesSetting("FastA files", "You need one file per chromosome, the header must contain only the chromosome ID", null, false, null);
		final StringSetting thespecies = new StringSetting("Species name",null,"");
		final HierarchicalSetting fromFasta = new HierarchicalSetting("from Fasta Files").addSetting(thespecies).addSetting(fastaFiles);

		final SelectableHierarchicalSetting makeGenome = new SelectableHierarchicalSetting("Specify a genome",null,0,new Object[]{fromFasta,sms})
		.setLayoutStyle(SelectableHierarchicalSetting.LayoutStyle.COMBOBOX);

		SettingDialog sd = new SettingDialog(null, "Define a genome", makeGenome);

		sd.showAsInputDialog();

		if (!sd.closedWithOK()) {
			System.out.println("Thank you for using Genome Tracks.");
			System.exit(0);
		}

		final ChromosomeSetContainer csc = ChromosomeSetContainer.getDefault();
		
		if (makeGenome.getObjectValue()==sms) {

			theMap = sms.getStringMapValue();			

			// add dummy probes
			for (Entry<String, String> e : theMap.entrySet()) {
				String name = e.getKey();
				String[] parts = name.split("[\\s]+");
				String species = "unknown species";
				String chrome = "X";
				if (parts.length>1) {
					species = parts[0];
					chrome = parts[1];
				} else {
					chrome = parts[0];
				}
				String ssize = e.getValue();
				long size = Long.parseLong(ssize.trim());

				// will implicitely add species and chromosomes
				Chromosome chromosome = csc.getChromosome(SpeciesContainer.getSpecies(species), chrome);
				chromosome.setLength(size);

			}
		} else {
			
			// will implicitely add species and chromosomes
			AbstractTask at = new AbstractTask("Loading chromosome sequences") {
				protected void doWork() throws Exception {
					int total = fastaFiles.getFileNames().size();
					int i=0;
					for (String f : fastaFiles.getFileNames()) {
						setProgress(10000*i/total);
						FastaChromosomeSequenceFactory.addChromosomeSequences(new File(f), thespecies.getStringValue(), true);
						++i;
						setProgress(10000*i/total);
					}
				}
				protected void initialize() {}
			};
			at.start();
			at.waitFor();
			
		}
		
		
		MasterTable mata = ds.getMasterTable();
		mata.addExperiment(new Experiment(mata, "1"));

		MIGroup mg = ds.getMIManager().newGroup(LocusMIO.myType, "Location");
		
		for (Chromosome c : csc.getAllChromosomes()) {
			Probe pb = new Probe(mata);
			pb.setName(c.toString()+"_dummy");
			mata.addProbe(pb);
			LocusMIO lm = (LocusMIO)mg.add(pb);
			lm.setValue(new Locus(new GeneticCoordinate(c, Strand.UNSPECIFIED, c.getLength()-2, c.getLength()-1)));
		}

		
		
		DataSetManager.singleInstance.addObject(ds);

		PlotPlugin plp = new mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverview();		
		LinkedList<ProbeList> lpl   = new LinkedList<ProbeList>();
		lpl.add(ds.getProbeListManager().getProbeLists().get(0));
		plp.run(lpl,ds.getMasterTable());

		// add shutdown hook
		Window w = MaydayWindowManager.getWindows().iterator().next();
		w.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.showConfirmDialog(null, "Exit Genome Browser?", "Confirm exit", 
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
					PluginManager.getInstance().shutdown();
					try {
						Preferences.userRoot().flush();
					} catch (BackingStoreException e1) {
						e1.printStackTrace();
					}
					System.out.println("Thank you for using Genome Tracks.");
					System.exit(0);
				}		
			}
		});

	}

}
