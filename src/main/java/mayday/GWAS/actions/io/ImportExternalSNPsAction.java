package mayday.GWAS.actions.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;

import mayday.GWAS.actions.snplist.SNPListPlugin;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.utilities.RevealMenuConstants;
import mayday.GWAS.utilities.SNPLists;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.PathSetting;

/**
 * @author jaeger
 *
 */
public class ImportExternalSNPsAction extends SNPListPlugin {

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
	public void run(Collection<SNPList> snpLists) {
		PathSetting path = new PathSetting("SNP ID file", null, null, false, true, false);
		SettingDialog sd = new SettingDialog(null, "Select the SNP ID file ...", path);
		sd.showAsInputDialog();
		
		if(sd.closedWithOK()) {
			SNPList snps = SNPLists.createUniqueSNPList(snpLists);
			File f = new File(path.getStringValue());
			SNPList newSL = new SNPList(f.getName(), projectHandler.getSelectedProject());
			
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
					projectHandler.getSelectedProject().addSNPList(newSL.getAttribute().getName(), newSL);
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
}
