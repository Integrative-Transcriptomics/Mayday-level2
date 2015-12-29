package mayday.wapiti.experiments.impl.affy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMDirectory;
import mayday.core.pluma.filemanager.FMFile;
import mayday.core.pluma.filemanager.FileManager;
import mayday.core.tasks.AbstractTask;
import affymetrix.fusion.cdf.FusionCDFData;

public class CDFRepository {

	protected static HashMap<String, String> cdfs;
	protected static String directory = MaydayDefaults.Prefs.getPluginDirectory()+"/CDF/";
	
	protected static void init() {
		if (cdfs==null)  {
			 cdfs = new HashMap<String, String>();
			 FileManager fm = PluginManager.getInstance().getFilemanager();
			 FMDirectory fd = fm.getDirectory("/CDF");
			 if (fd==null)
				 return;
			 for (FMFile f : fd.getFiles(false)) {
				 f.extract();				 
				 cdfs.put(f.Name.replaceAll("[.]CDF", ""), f.getFullPath());
			 }
		}
	}
	
	public static String addCDF(String CDFFile, boolean persistent) {
		init();
		
		FusionCDFData cdf = new FusionCDFData();
		cdf.setFileName(CDFFile);
		cdf.readHeader();
		String cdfID = cdf.getChipType(); // make sure the file is named correctly
		String newName = directory+cdfID+".CDF";

		if (persistent && !cdfs.containsKey(cdfID)) {
			try {
				FileManager.copy(CDFFile, newName);
			} catch (IOException ioe) {
				System.out.println(ioe+"\n"+ioe.getMessage());
			}
		}
		cdfs.put(cdfID, newName);
		
		return cdfID;
	}
	
	/** Add a CDF to the in-memory repository but do not store it in the plugin directory */
	public static String addCDFTransient(String CDFfile) {
		return addCDF(CDFfile, false);
	}
	
	/** Add a CDF to the in-memory repository and copy it to the plugin directory */
	public static String addCDFPersistent(String CDFfile) {
		return addCDF(CDFfile, true);
	}
	
	public static FusionCDFData getCDF(String cdfID) {
		init();

		final String name = cdfs.get(cdfID);
		if (name!=null) {
			
			final FusionCDFData cdf = new FusionCDFData();

			AbstractTask at = new AbstractTask("Parsing CDF") {
				protected void doWork() throws Exception {
					cdf.setFileName(name);
					cdf.read();
				}

				protected void initialize() {
				}
			};
			at.start();
			at.waitFor();
			return cdf;
		}
		
		return null;
	}
	
	public static FusionCDFData getCDFinteractive(String cdfId, String searchDir) {
		FusionCDFData cdfd = getCDF(cdfId);
		while (cdfd==null) {
			JOptionPane.showMessageDialog(null, 
					"Please select a CDF file for Chip Type \""+cdfId+"\".", 
					"CDF file not found", JOptionPane.INFORMATION_MESSAGE);
			JFileChooser jfc = new JFileChooser(searchDir);
			jfc.setDialogTitle("Select a CDF file for "+cdfId);
			jfc.setDialogType(JFileChooser.OPEN_DIALOG);
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jfc.setMultiSelectionEnabled(false);
			jfc.setFileFilter(new FileFilter() {

				@Override
				public boolean accept(File f) {
					return f.isDirectory() || f.getName().toUpperCase().endsWith(".CDF");
				}

				@Override
				public String getDescription() {
					return "Affymetrix CDF files";
				}
				
			});
			int status = jfc.showOpenDialog(null);
			if (status==JFileChooser.APPROVE_OPTION) {
				File f = jfc.getSelectedFile();
				try {
					addCDF(f.getCanonicalPath(), true);
					cdfd = getCDF(cdfId);
				} catch (Exception e) {
					// don't break
				}
			} else if (status==JFileChooser.CANCEL_OPTION) {
				break;
			}
		}
		return cdfd;
	}
	
	public static boolean containsCDF(String cdfID) {
		init();
		return cdfs.get(cdfID)!=null;
	}

	
	
}
