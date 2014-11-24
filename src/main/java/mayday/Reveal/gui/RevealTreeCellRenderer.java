package mayday.Reveal.gui;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.GeneList;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.data.SubjectList;
import mayday.Reveal.data.meta.SLResults;
import mayday.Reveal.data.meta.TLResults;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;

@SuppressWarnings("serial")
public class RevealTreeCellRenderer extends DefaultTreeCellRenderer {
	
	private final ImageIcon topPrioritySNPListIcon;
	private final ImageIcon snpListIcon;
	private final ImageIcon subjectIcon;
	private final ImageIcon projectIcon;
	private final ImageIcon genesIcon;
	private final ImageIcon metaInfoIcon;
	
	private final ImageIcon folderIcon;
	
	public RevealTreeCellRenderer() {
		super();
		this.topPrioritySNPListIcon = loadImage("mayday/GWAS/icons/toppriority.png");
		this.snpListIcon = loadImage("mayday/GWAS/icons/directory1.png");
		this.subjectIcon = loadImage("mayday/GWAS/icons/people5.png");
		
		this.projectIcon = loadImage("mayday/GWAS/icons/opened18.png");
		this.genesIcon = loadImage("mayday/GWAS/icons/dna.png");
		this.metaInfoIcon = loadImage("mayday/GWAS/icons/document112.png");
		
		this.folderIcon = loadImage("mayday/GWAS/icons/documents14.png");
	}

	public Component getTreeCellRendererComponent(JTree arg0, Object arg1,
			boolean arg2, boolean arg3, boolean arg4, int arg5, boolean arg6) {

		// use parent class to set background etc.
		super.getTreeCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5, arg6);

		if (!(arg1 instanceof DefaultMutableTreeNode))
			return this;

		String nodeText = "";
		
		Object argUserObject = ((DefaultMutableTreeNode)arg1).getUserObject();		
		
		// Leaf nodes
		if (argUserObject instanceof SNPList) {
			SNPList sl = (SNPList)argUserObject;
			nodeText = "<html>" + sl.toString();
			
			String info = sl.getAttribute().getInformation();
			if(!info.equals("null")) {
				nodeText += "<br/>- " + sl.getAttribute().getInformation();
			}
			
			if(sl.isTopPriority()) {
				this.setIcon(topPrioritySNPListIcon);
			} else {
				this.setIcon(snpListIcon);
			}
		} else if(argUserObject instanceof SubjectList) {
			SubjectList pl = (SubjectList)argUserObject;
			nodeText = "<html>" + pl.toString() + "<br/>" + 
					"A: " + pl.getNumberAffected() + 
					" U: " + pl.getNumUnaffected();
			setIcon(subjectIcon);
		} else if(argUserObject instanceof GeneList) {
			nodeText = "<html>" + argUserObject.toString();
			setIcon(genesIcon);
		} else if(argUserObject instanceof DataStorage) {
			nodeText = "<html>" + argUserObject.toString();
			setIcon(projectIcon);
		} else if(argUserObject instanceof String) {
			nodeText = "<html>" + argUserObject.toString();
			setIcon(folderIcon);
		} else if(argUserObject instanceof TLResults) {
			nodeText = "<html>" + argUserObject.toString();
			setIcon(metaInfoIcon);
		} else if(argUserObject instanceof SLResults) {
			nodeText = "<html>" + argUserObject.toString();
			setIcon(metaInfoIcon);
		}
		
		else {
			if(argUserObject != null)
				nodeText = "<html>" + argUserObject.toString();
		}
		
		System.out.println(argUserObject.getClass() + " : " + nodeText);

		this.setText(nodeText);

		return this;
	}
	
	private ImageIcon loadImage(String file) {
		FMFile fmf = PluginManager.getInstance().getFilemanager().getFile(file);									
		try {
			return new ImageIcon(getScaledImage(ImageIO.read(fmf.getStream()), 20, 20));
		} catch (IOException e) {
			e.printStackTrace();			
		}
		return new ImageIcon();
	}
	
	private Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }
}
