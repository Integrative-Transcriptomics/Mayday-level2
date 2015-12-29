package mayday.Reveal.gui;

import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;

import mayday.Reveal.actions.DeleteProjectAction;
import mayday.Reveal.actions.io.LoadProject;
import mayday.Reveal.actions.io.SaveProject;
import mayday.Reveal.io.project.NewProject;
import mayday.Reveal.visualizations.RevealVisualizationPlugin;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class RevealToolBar extends JToolBar {

	JButton newProjectButton;
	JButton loadProjectButton;
	JButton saveProjectButton;
	JButton deleteProjectButton;
	
	JComboBox availableGenomes;
	
	private RevealGUI gui;
	
	/**
	 * construct a new Reveal Toolbar
	 * @param gui 
	 */
	public RevealToolBar(RevealGUI gui) {
		this.gui = gui;
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.addWidgets();
	}

	private void addWidgets() {
		newProjectButton = new JButton();
		newProjectButton.setToolTipText("Create a new eQTL project");
		final NewProject newProject = new NewProject();
		newProject.setProjectHandler(gui.getProjectHandler());
		newProjectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newProject.run(null);
			}
		});
		
		ImageIcon newProjectIcon = loadImage("mayday/GWAS/icons/rectangle1.png");
		newProjectButton.setIcon(newProjectIcon);
		
		loadProjectButton = new JButton();
		loadProjectButton.setToolTipText("Load existing eQTL projects");
		final LoadProject loadProject = new LoadProject();
		loadProject.setProjectHandler(gui.getProjectHandler());
		loadProjectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadProject.run(null);	
			}
		});
		
		ImageIcon loadProjectIcon = loadImage("mayday/GWAS/icons/open131.png");
		loadProjectButton.setIcon(loadProjectIcon);
		
		saveProjectButton = new JButton();
		saveProjectButton.setToolTipText("Save the current projects");
		final SaveProject saveProject = new SaveProject();
		saveProject.setProjectHandler(gui.getProjectHandler());
		saveProjectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveProject.run(null);
			}
		});
		
		ImageIcon saveProjectIcon = loadImage("mayday/GWAS/icons/floppy1.png");
		saveProjectButton.setIcon(saveProjectIcon);
		
		deleteProjectButton = new JButton();
		deleteProjectButton.setToolTipText("Delete the selected project");
		deleteProjectButton.addActionListener(new DeleteProjectAction(gui.getProjectHandler()));
		
		ImageIcon deleteProjectIcon = loadImage("mayday/GWAS/icons/delete47.png");
		deleteProjectButton.setIcon(deleteProjectIcon);
		
		this.add(newProjectButton);
		this.add(loadProjectButton);
		this.add(saveProjectButton);
		this.add(deleteProjectButton);
		this.addSeparator();
	}
	
	/**
	 * @param title
	 * @param description
	 * @param a
	 */
	public void addItem(String title, String description, Action a) {
		JButton button = new JButton(title);
		button.addActionListener(a);
		button.setToolTipText(description);
		this.add(button);
	}
	
	private ImageIcon loadImage(String file) {
		FMFile fmf = PluginManager.getInstance().getFilemanager().getFile(file);									
		try {
			return new ImageIcon(getScaledImage(ImageIO.read(fmf.getStream()), 24, 24));
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

	public void addItem(RevealVisualizationPlugin plotPlugin, AbstractAction a) {
		JButton button = new JButton();
		button.addActionListener(a);
		button.setToolTipText(plotPlugin.getDescription());
		
		if(plotPlugin.getIconPath() != null) {
			ImageIcon icon = loadImage(plotPlugin.getIconPath());
			button.setIcon(icon);
		} else {
			button.setText(plotPlugin.getMenuName());
		}
		
		this.add(button);
	}
}
