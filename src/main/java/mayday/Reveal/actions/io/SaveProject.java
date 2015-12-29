package mayday.Reveal.actions.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import mayday.Reveal.RevealPlugin;
import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.gui.RevealGUI;
import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.core.DataSet;
import mayday.core.io.dataset.SimpleSnapshot.Snapshot;
import mayday.core.tasks.AbstractTask;

/**
 * @author jaeger
 *
 */
public class SaveProject extends RevealPlugin {
	
	public void save() {
		saveAndExit(null, false);
	}

	public void saveAndExit(final RevealGUI gui, final boolean exit) {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setDialogTitle("Save Reveal-Snapshot to ...");
		fc.setApproveButtonText("Save");
		int r = fc.showSaveDialog(null);
		
		if(r == JFileChooser.APPROVE_OPTION) {
			final File f = fc.getSelectedFile();
			
			boolean writingAllowed = true;
			
			if(f.exists()) {
				int option = JOptionPane.showConfirmDialog(null, "The selected file does already exist. Override?");

				if(option != JOptionPane.YES_OPTION) {
					writingAllowed = false;
				}
			}
			
			if(writingAllowed) {
				AbstractTask task = new AbstractTask("Writing Snapshot") {

					@Override
					protected void initialize() {}

					@Override
					protected void doWork() throws Exception {
						try {
							writeLog("Your data will be saved now!\n");
							writeLog("Do not shutdown Reveal before the data is saved, or your data will get lost!\n");
							
							ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
							BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
							
							out.putNextEntry(new ZipEntry("Reveal.txt"));
							for(int i = 0; i < projectHandler.numberOfProjects(); i++) {
								DataStorage ds = projectHandler.get(i);
								ds.serialize(bw);
								bw.newLine();
								bw.flush();
							}
							out.closeEntry();
							
							for(int i = 0; i < projectHandler.numberOfProjects(); i++) {
								DataSet ds = projectHandler.get(i).getDataSet();
								out.putNextEntry(new ZipEntry(ds.getName() + ".dataset"));
								
								Snapshot maydaySnapshot = Snapshot.getNewestVersion();
								maydaySnapshot.setProcessingTask(this);
								maydaySnapshot.setDataSet(ds);
								maydaySnapshot.write(out);
								out.closeEntry();
							}
							
							out.finish();
							out.close();
							
							writeLog("Your data has successfully been saved!\n");
							
							if(exit) {
								if(gui != null) {
									writeLog("Thanks for using Reveal.");
									projectHandler.clear();
									gui.dispose();
								}
							}
							
						}catch (IOException ex) {
							ex.printStackTrace();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				};
				
				task.start();
			}
		}
	}

	@Override
	public String getName() {
		return "Save Reveal Projects";
	}

	@Override
	public String getType() {
		return "project.saveProject";
	}

	@Override
	public String getDescription() {
		return "Save all current projects";
	}

	@Override
	public String getMenuName() {
		return "Save Project(s)";
	}

	@Override
	public void run(Collection<SNVList> snpLists) {
		save();	
	}

	@Override
	public String getMenu() {
		return RevealMenuConstants.FILE_MENU;
	}

	@Override
	public String getCategory() {
		return "Project";
	}
}
