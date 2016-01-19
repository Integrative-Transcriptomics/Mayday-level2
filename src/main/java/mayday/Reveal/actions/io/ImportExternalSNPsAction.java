package mayday.Reveal.actions.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;

import mayday.Reveal.actions.snplist.SNVListPlugin;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.gui.menu.SNPListPopupMenu;
import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.Reveal.utilities.SNVLists;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.PathSetting;

/**
 * @author jaeger
 *
 */
public class ImportExternalSNPsAction extends SNVListPlugin {

	@Override
	public String getName() {
		return "Import SNP Identifier";
	}

	@Override
	public String getType() {
		return "data.io.snpids";
	}

	@Override
	public String getDescription() {
		return "Import SNP IDs from an external file and create a SNPList based on these SNP IDs";
	}

	@Override
	public String getMenuName() {
		return "SNP IDs from file";
	}

	@Override
	public void run(Collection<SNVList> snpLists) {
		PathSetting path = new PathSetting("SNP ID file", null, null, false, true, false);
		SettingDialog sd = new SettingDialog(null, "Select the SNP ID file ...", path);
		sd.showAsInputDialog();
		
		if(sd.closedWithOK()) {
			SNVList snps = SNVLists.createUniqueSNVList(snpLists);
			File f = new File(path.getStringValue());
			SNVList newSL = new SNVList(f.getName(), projectHandler.getSelectedProject());
			
			try {
				BufferedReader br = new BufferedReader(new FileReader(path.getStringValue()));
				String line = null;
				
				while((line = br.readLine()) != null) {
					//skip empty lines
					if(line.trim().length() == 0)
						continue;
					if(snps.contains(line)) {
						newSL.add(snps.get(line));
					}
				}
				
				br.close();
				
				if(newSL.size() > 0) {
					projectHandler.getSelectedProject().addSNVList(newSL.getAttribute().getName(), newSL);
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public String getMenu() {
		return RevealMenuConstants.SNPLIST_MENU + "/Import";
	}
	
	@Override
	public String getPopupMenuCategroy() {
		return SNPListPopupMenu.IO_CATEGORY;
	}
}
