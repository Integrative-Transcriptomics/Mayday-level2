package mayday.Reveal.actions.io;

import java.io.File;
import java.util.Collection;

import javax.swing.JFileChooser;

import mayday.Reveal.RevealPlugin;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.io.SnapshotReader;
import mayday.Reveal.utilities.RevealMenuConstants;
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
	public void run(Collection<SNVList> snpLists) {
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
