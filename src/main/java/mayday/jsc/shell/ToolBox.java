package mayday.jsc.shell;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;
/*
 * Different frequently used methods with no better place to be then right here in this class as static functions available for use at any time, even right now. 
 */
public class ToolBox
{	
	public static String readFile(String path) throws IOException
	{
		String result = "";		
		FMFile rconn = PluginManager.getInstance().getFilemanager().getFile(path);
		InputStream fs = rconn != null ? rconn.getStream() : new FileInputStream(path);		
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));				
		while (br.ready())
			result += br.readLine()+"\n";
		br.close();
		return result;
	}
	
	public static boolean save(File f, String content, Component parent)
	{
		if( f == null )
			return false;
		else
		{
			BufferedWriter w = null;
			try {
				w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
				w.write(content);
				w.flush(); 
				w.close(); 
				} catch (Exception e) {
					if(w != null)
						try//Make sure writer is closed
						{
							w.close();
						} catch (IOException e1) {}
					JOptionPane.showConfirmDialog(parent,
							"Failed to save file:\n"+e.getCause(),
							"Saving Failed",
							JOptionPane.OK_OPTION,
							JOptionPane.ERROR_MESSAGE);
					return false;
				}			
			return true;
		}
	}
		
	public static void expandJTree(JTree tree, TreePath parent)
	{
		TreeNode node = (TreeNode)parent.getLastPathComponent();

		for(Enumeration<?> e=node.children(); e.hasMoreElements(); )
		{
			TreeNode n = (TreeNode)e.nextElement();
			TreePath path = parent.pathByAddingChild(n);
			expandJTree(tree, path);
		}

		tree.expandPath(parent); 
	}
	
	public static ImageIcon loadImage(String file)
	{
		FMFile fmf = PluginManager.getInstance().getFilemanager().getFile(file);									
		try {
			return new ImageIcon(ImageIO.read(fmf.getStream()));
		} catch (IOException e) {
			e.printStackTrace();			
		}
		return new ImageIcon();
	}
}
