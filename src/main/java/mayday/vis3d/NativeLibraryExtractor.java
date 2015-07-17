package mayday.vis3d;

import java.io.File;
import java.util.HashMap;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.filemanager.FMDirectory;
import mayday.core.pluma.filemanager.FMFile;
import mayday.core.pluma.filemanager.FileManager;
import mayday.core.pluma.prototypes.GenericPlugin;

/**
 * @author G\u00FCnter J\u00E4ger
 * @date Nov 24, 2014
 */
public class NativeLibraryExtractor extends AbstractPlugin implements GenericPlugin {

	protected static boolean extractionDone = false; 
	
	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.vis3d.nativelib",
				new String[0],
				Constants.MC_SUPPORT,
				new HashMap<String, Object>(),
				"G\u00FCnter J\u00E4ger",
				"jaeger@informatik.uni-tuebingen.de",
				"Helper plugin to extract native libraries for jogl and gluegen-rt.",
				"JOGL native libs"
		);

		if (!extractionDone) {
			String os = System.getProperty("os.name");
			String arch = System.getProperty("os.arch");
			
			System.out.println("\n(Maybe) extracting native libraries for JOGL");
			System.out.println("Operating System: "+os);
			System.out.println("Architecture:     "+arch);
			
			os = os.toLowerCase();
			arch = arch.toLowerCase();
			
			if(os.contains("windows")) {
				os="windows";
				if(arch.contains("64")) {
					arch="amd64";
				} else {
					arch="i586";
				}
			}
			
			else if(os.contains("linux")) {
				os="linux";
				if(arch.contains("64")) {
					arch="amd64";
				} else {
					arch="i586";
				}
			}
			
			else if(os.contains("sunos")) {
				os="solaris";
				if(arch.contains("64")) {
					arch="amd64";
				} else {
					arch="i586";
				}
			}
			
			else if(os.contains("mac")) {
				os="macosx";
				arch="universal";
			}			
			
			String dirS = "mayday/jogl-native/"+os+"-"+arch;
			
			System.out.println("Using native libraries from: "+os+"-"+arch);

			FileManager fm = PluginManager.getInstance().getFilemanager();
			FMDirectory dir = fm.getDirectory(dirS);
			
			if (dir==null) {
				System.err.println("Native library files were not found.");
				pli.addDependencies(new String[]{"JOGL native libraries"});
			} else {
				for (FMFile f : dir.getFiles(false)) {
					System.out.println("-- "+f.Name);
					if (!f.extract()) {
						System.out.println("Error extracting file");
					} else {
						dirS = new File(f.getFullPath()).getParent();
					}
				}
				PluginManager.getInstance().addNativeLibraryPath(dirS);
			}
			
			extractionDone = true;
		}		
		
		return pli;	
	}

	@Override
	public void run() {}
}
