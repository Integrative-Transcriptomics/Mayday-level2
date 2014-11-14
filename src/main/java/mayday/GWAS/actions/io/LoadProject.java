package mayday.GWAS.actions.io;

import java.io.File;
import java.util.Collection;

import javax.swing.JFileChooser;

import mayday.GWAS.RevealPlugin;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.io.SnapshotReader;
import mayday.GWAS.utilities.RevealMenuConstants;
import mayday.core.tasks.AbstractTask;

/**
 * @author jaeger
 *
 */
public class LoadProject extends RevealPlugin {

	@Override
	public String getName() {
		return "Load Reveal Projects";
	}

	@Override
	public String getType() {
		return "project.loadProjects";
	}

	@Override
	public String getDescription() {
		return "Load existing Reveal Projects";
	}

	@Override
	public String getMenuName() {
		return "Load Project(s)";
	}

	@Override
	public void run(Collection<SNPList> snpLists) {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setDialogTitle("Load Reveal-Snapshot File ...");
		fc.setApproveButtonText("Load");
		fc.setMultiSelectionEnabled(false);
		
		int r = fc.showOpenDialog(null);
		
		if(r == JFileChooser.APPROVE_OPTION) {
			final File input = fc.getSelectedFile();
			
			if(input.canRead()) {
				AbstractTask task = new AbstractTask("Reading Snapshot") {
					@Override
					protected void initialize() {}

					@Override
					protected void doWork() throws Exception {
						SnapshotReader reader = new SnapshotReader(projectHandler);
						reader.setProcessingTask(this);
						reader.read(input);
					}
				};
				
				task.start();
			}
		}
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
