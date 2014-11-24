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
 * @date Jun 10, 2010
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
			
			if(os.equals("windows 8")) // try to use "old" windows libraries on the new windows
				os= "windows";
			
			if (os.equals("windows 7")) // try to use "old" windows libraries on the new windows
				os = "windows";
			
			if (os.startsWith("windows server 2008")) // try to use "old" windows libraries on the new windows
				os = "windows";
			
			if (os.equals("windows") && arch.equals("x86_64")) 
				arch = "amd64";
			
			if (os.equals("windows") && arch.equals("x86"))
				arch = "i586";
			
			else if (os.equals("sunos")) {
				os = "solaris";
				if (arch.equals("x86"))
					arch = "i586";
				else if (arch.equals("x86_64"))
					arch = "amd64";
			}
			
			else if (os.equals("linux")) {
				if (arch.equals("i386") || arch.equals("x86"))
					arch = "i586";
				else if (arch.equals("x86_64"))
					arch = "amd64";
			}
			
			else if (os.equals("mac os x")) {
				os="macosx";
				if (!arch.equals("ppc"))
					arch="universal";
			}				
			
			String dirS = "mayday/native/jogl/"+os+"-"+arch;
			
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
