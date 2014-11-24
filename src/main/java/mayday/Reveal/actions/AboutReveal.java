package mayday.Reveal.actions;

import java.util.Collection;

import javax.swing.JOptionPane;

import mayday.Reveal.RevealPlugin;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.utilities.RevealMenuConstants;

/**
 * @author jaeger
 *
 */
public class AboutReveal extends RevealPlugin {

	@Override
	public String getName() {
		return "About Reveal";
	}

	@Override
	public String getType() {
		return "help.about";
	}

	@Override
	public String getDescription() {
		return "Show information about Reveal";
	}

	@Override
	public String getMenuName() {
		return "About";
	}

	@Override
	public void run(Collection<SNPList> snpLists) {
		String infoText = "Reveal - Visual eQTL Analytics\n\n"
				+ "Author:\nGünter Jäger\n"
				+ "Center for Bioinformatics Tübingen\n"
				+ "University of Tübingen";
		System.out.println("Showing about dialog");
		JOptionPane.showMessageDialog(null, infoText,
				"About this application",
				JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public String getMenu() {
		return RevealMenuConstants.HELP_MENU;
	}

	@Override
	public String getCategory() {
		return "Help/About";
	}
}
