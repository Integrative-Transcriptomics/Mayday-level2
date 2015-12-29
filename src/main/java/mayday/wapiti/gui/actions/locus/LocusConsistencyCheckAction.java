package mayday.wapiti.gui.actions.locus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import mayday.core.gui.MaydayFrame;
import mayday.core.structures.maps.MultiHashMap;
import mayday.core.tasks.AbstractTask;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class LocusConsistencyCheckAction extends AbstractAction {

	public static class SpeciesChromes extends TreeMap<String, TreeSet<String>> {
		public void addSpecies(String spec) {
			if (get(spec)==null)
				put(spec, new TreeSet<String>());
		}
		
	};

	
	private final TransMatrix transMatrix;

	public LocusConsistencyCheckAction(TransMatrix transMatrix, SelectionModel sm) {
		super("Check Locus Consistency");
		this.transMatrix = transMatrix;
//		this.selection = sm;
	}

	
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		AbstractTask checker = new AbstractTask("Checking locus data") {

			@Override
			protected void doWork() throws Exception {
				
				Collection<Experiment> le = transMatrix.getExperiments();
				
				int totalExperiments = le.size();
				int currentExperiment=0;
				boolean firstlog=true;
				
				TreeSet<String> speciesNames = new TreeSet<String>();
				SpeciesChromes speciesChromes = new SpeciesChromes();
				
				for (Experiment e : le) {
					ExperimentState current_state = e.getCurrentState();
					ExperimentState initial_state = e.getInitialState();

					ChromosomeSetContainer csc_current = null;
					ChromosomeSetContainer csc_initial = null;
					
					setProgress(10000*currentExperiment/totalExperiments, "Checking experiment "+e.getName());
					
					if (current_state.hasLocusInformation()) {
						csc_current = current_state.getLocusData().asChromosomeSetContainer();
						for (Chromosome c : csc_current.getAllChromosomes()) {
							speciesNames.add(c.getSpecies().getName());
							speciesChromes.addSpecies(c.getSpecies().getName());
							speciesChromes.get(c.getSpecies().getName()).add(c.getId());
						}				
					}	
					// do the same for the initial state

					if (initial_state.hasLocusInformation()) {
						csc_initial = initial_state.getLocusData().asChromosomeSetContainer();
						for (Chromosome c : csc_initial.getAllChromosomes()) {
							speciesNames.add(c.getSpecies().getName());
							speciesChromes.addSpecies(c.getSpecies().getName());
							speciesChromes.get(c.getSpecies().getName()).add(c.getId());
						}				
					}			
					
					if (csc_current!=null && csc_initial!=null) {
						// Experiment consistency check
						TreeSet<String> currentChromes = new TreeSet<String>();
						TreeSet<String> initialChromes = new TreeSet<String>();
						for (Chromosome chrome : csc_current.getAllChromosomes())
							currentChromes.add(chrome.getSpecies().getName()+": "+chrome.getId());
						for (Chromosome chrome : csc_initial.getAllChromosomes())
							initialChromes.add(chrome.getSpecies().getName()+": "+chrome.getId());
						
						TreeSet<String> emptyOutputChromes = new TreeSet<String>(currentChromes);
						emptyOutputChromes.removeAll(initialChromes);
						TreeSet<String> ignoredInputChromes = new TreeSet<String>(initialChromes);
						ignoredInputChromes.removeAll(currentChromes);
						
						if (emptyOutputChromes.size()>0 || ignoredInputChromes.size()>0) {
							if (firstlog) {
								writeLog("*** Locus operations using inconsistent data ***\n" +
										"If locus data is used for mapping e.g. reads to genes, \n" +
										"chromosome names need to be consistent in the input data \n" +
										"and the locus information used for mapping. Otherwise, SeaSight \n" +
										"will not find any elements mapping to the target locus set. \n" +
										"The following experiment(s) may have this problem:\n");
								firstlog=false;
							}
						}
						
						if (emptyOutputChromes.size()>0) {
							writeLog("- Experiment \""+e.getName()+"\": These output chromosomes were not found in the input data and may remain empty: \n");
							for (String cstr : emptyOutputChromes) 
								writeLog("   - "+cstr+"\n");
						}
						
						if (ignoredInputChromes.size()>0) {
							writeLog("- Experiment \""+e.getName()+"\": These input chromosomes appear to be unused: \n");
							for (String cstr : ignoredInputChromes) 
								writeLog("   - "+cstr+"\n");
						}			
						
					}
					
					++currentExperiment;
					
				}
				
				if (!firstlog)
					writeLog("\n");
				

				
				MultiHashMap<String,String> uniqueSpeciesNames = new MultiHashMap<String,String>();
				for (String s : speciesNames) {
					uniqueSpeciesNames.put(s.toLowerCase(), s);
				}
				if (uniqueSpeciesNames.size()!=speciesNames.size()) {
					writeLog("*** Duplicated species names with differing capitalization ***\n" +
							"The following species names appear more than once with different spelling:\n");
					for (String s : uniqueSpeciesNames.keySet()) {
						List<String> speciesnames = uniqueSpeciesNames.get(s);
						if (speciesnames.size()>1) {
							writeLog("- ");
							for (String sc : speciesnames) 
								writeLog("\""+sc+"\"  ");
							writeLog("\n");
						}
					}
					writeLog("\n");
				}
				
				firstlog = true;
				
				for (String s : speciesChromes.keySet()) {					
					firstlog = checkSpeciesChromosomes(s, speciesChromes.get(s), this, firstlog);
				}
				
				setProgress(10000,"");
				
				String lc = getGUI().getLogContent();
				
				final MaydayFrame mf = new MaydayFrame();
				mf.setTitle("Locus Consistency Check: Results");
				JTextArea logTextArea = new JTextArea();
				logTextArea.setEditable(false);
				logTextArea.setWrapStyleWord(true);
				JScrollPane centerPanel = new JScrollPane();
				centerPanel.setViewportView(logTextArea);
				centerPanel.setBackground(Color.white);
				centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
				mf.add(centerPanel, BorderLayout.CENTER);
				logTextArea.setText(lc);
				if (lc.length()==0)
					logTextArea.setText("*** No inconsistencies found.");				
				JPanel buttons = new JPanel(new BorderLayout());
				buttons.add(new JButton(new AbstractAction("Close") {
					@Override
					public void actionPerformed(ActionEvent e) {
						mf.dispose();							
					}
				}), BorderLayout.EAST);
				mf.add(buttons,BorderLayout.SOUTH);
				mf.pack();
				mf.setSize(800,600);					
				mf.setVisible(true);
				getGUI().clearLog();

			}
			
			@Override
			protected void initialize() {
			}
			
		};
		checker.start();
		
		

		
	}
	
	protected boolean checkSpeciesChromosomes(String species, TreeSet<String> tsc, AbstractTask at, boolean firstlog) {
		MultiHashMap<Integer, String> numericChromes = new MultiHashMap<Integer, String>();
		MultiHashMap<String, String> nonNumericChromes = new MultiHashMap<String, String>();
		
		Pattern pnumeric = Pattern.compile("[0-9]+");
		Pattern pprefix = Pattern.compile("(?>.*chromosome|.*chr)?(.+)", Pattern.CASE_INSENSITIVE);
		
		for (String chromeName : tsc) {
			Matcher m = pnumeric.matcher(chromeName);
			if (m.find()) {
				String chromeSecondName = m.group();
				try {
					Integer chromeIndex = Integer.parseInt(chromeSecondName);
					numericChromes.put(chromeIndex, chromeName);
				} catch (Exception e) {};
			} else {				
				m = pprefix.matcher(chromeName);
				try {
					if (m.find() && m.groupCount()>0) {
						String chromeId = m.group(1).trim();						
						nonNumericChromes.put(chromeId, chromeName);
					} else {
						nonNumericChromes.put(chromeName, chromeName);
					}
				} catch (Exception ex) { 
					nonNumericChromes.put(chromeName, chromeName);
				}
			}
		}
		
		boolean alreadyWroteSomething = false;
	
		for (Integer i : numericChromes.keySet()) {
			List<String> chr = numericChromes.get(i);
			if (chr.size()>1) {
				if (firstlog) {
					at.writeLog("**** Some species seem to contain duplicated chromosomes with inconsistent naming ****\n");
					firstlog=false;
				}
				if (!alreadyWroteSomething)
					at.writeLog("- Species \""+species+"\": \n");
				at.writeLog("   - ");
				for (String s : chr) 
					at.writeLog("\""+s+"\"  ");
				at.writeLog("\n");
				alreadyWroteSomething = true;
			}
		}
		
		for (String s : nonNumericChromes.keySet()) {
			List<String> chr = nonNumericChromes.get(s);
			if (chr.size()>1) {
				if (firstlog) {
					at.writeLog("**** Some species seem to contain duplicated chromosomes with inconsistent naming ****\n");
					firstlog=false;
				}
				if (!alreadyWroteSomething)
					at.writeLog("- Species \""+species+"\": \n");
				at.writeLog("   - ");
				for (String sc : chr) 
					at.writeLog("\""+sc+"\"  ");
				at.writeLog("\n");
				alreadyWroteSomething = true;
			}
		}

		if (alreadyWroteSomething)
			at.writeLog("\n");
		
		if (numericChromes.size()+nonNumericChromes.size()!=tsc.size() && !alreadyWroteSomething) {
			if (firstlog) {
				at.writeLog("**** Some species seem to contain duplicated chromosomes with inconsistent naming ****\n");
				firstlog=false;
			}			
			at.writeLog("- Species \""+species+"\" appears to have inconsistent naming but I cannot say what is wrong.\n");
		}		
		
		return firstlog;
	}

}
