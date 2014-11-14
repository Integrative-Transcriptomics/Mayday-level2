
package mayday.wapiti.gui.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import mayday.core.MaydayDefaults;
import mayday.core.Preferences;
import mayday.wapiti.Constants;
import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class LoadAction extends AbstractAction {

	public static String lastLoadSource; 
	protected static Preferences prefs = MaydayDefaults.Prefs.getPluginPrefs().node( Constants.MCBASE );
	
	private final TransMatrix transMatrix;
	//	private final SelectionModel selection;

	public LoadAction(TransMatrix transMatrix, SelectionModel sm) {
		super("Load matrix");
		this.transMatrix = transMatrix;
		lastLoadSource = prefs.get("LoadPath", System.getProperty("user.home"));
	}
	
	public static void setLastLoadSource(String source) {
		lastLoadSource = source;
		prefs.put("LoadPath", lastLoadSource);
	}

	public void actionPerformed(ActionEvent e) {
		
		if (transMatrix.getExperimentCount()>0 && JOptionPane.showConfirmDialog(transMatrix.getFrame(), 
				"If you continue, the current configuration will be discarded. Continue loading?",
				"Discard current configuration",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)
			return;
		
		JFileChooser chooser;
		chooser = new JFileChooser(lastLoadSource);
		chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
		chooser.setFileFilter(new FileFilter() {
		
			public String getDescription() {
				return "SeaSight matrix files";
			}
		
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().toLowerCase().endsWith(".seasight");
			}
		});

		String fileName=null;
		while(fileName==null) {
			int l_option = chooser.showOpenDialog( transMatrix.getFrame() );
			if ( l_option  == JFileChooser.APPROVE_OPTION ) {
				fileName = chooser.getSelectedFile().getAbsolutePath();
				// if the user presses cancel, then quit

				if (fileName!=null) {
					setLastLoadSource(new File(fileName).getParent()); 
					transMatrix.loadFromFile(fileName);
					return;
				}
			} else {
				return;
			}
		}
	}

}