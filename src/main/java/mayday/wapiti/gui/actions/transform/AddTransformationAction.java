
package mayday.wapiti.gui.actions.transform;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.*;

import mayday.core.gui.MaydayDialog;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.PluginInstanceSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.gui.ExperimentPanel;
import mayday.wapiti.gui.TransMatrixFrame;
import mayday.wapiti.gui.layeredpane.ReorderableHorizontalPanel;
import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class AddTransformationAction extends AbstractAction {

	
	private final TransMatrix transMatrix;
	private final SelectionModel selection;

	public AddTransformationAction(TransMatrix transMatrix, SelectionModel sm) {
		super("Add Transformation");
		this.transMatrix = transMatrix;
		this.selection = sm;
	}

	public static PluginInstanceSetting<AbstractTransformationPlugin> showTransformationSettingDialog(List<Experiment> le, String title) {
		TreeSet<PluginInfo> plis = new TreeSet<PluginInfo>(PluginManager.getInstance().getPluginsFor(AbstractTransformationPlugin.MC));
		
		Set<AbstractTransformationPlugin> apls = new TreeSet<AbstractTransformationPlugin>(new Comparator<AbstractTransformationPlugin>() {
			public int compare(AbstractTransformationPlugin o1,
					AbstractTransformationPlugin o2) {
				String s1 = PluginManager.getInstance().getPluginFromClass(o1.getClass()).getName();
				String s2 = PluginManager.getInstance().getPluginFromClass(o2.getClass()).getName();
				return s1.compareTo(s2);
			}
		});		
		
		final ArrayList<String> reasons = new ArrayList<String>();
		final TransMatrixFrame frame = le.get(0).getTransMatrix().getFrame();
		
		for (PluginInfo pli : plis) {
			AbstractTransformationPlugin atp = (AbstractTransformationPlugin)pli.newInstance();
			if (atp.applicableTo(le))
				apls.add(atp);
			else
				reasons.add(atp.getName()+"\t"+atp.getApplicabilityRequirements());
		}
		
		if (apls.size()==0) {
			int res = JOptionPane.showConfirmDialog(null, 
					"No applicable transformations found. \n" +
					"Would you like to see the reasons for each inapplicable transformation?", 
					"Unable to add transformation",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.ERROR_MESSAGE);
			if (res==JOptionPane.YES_OPTION) {
				showApplicabilityReasons(frame,reasons);
			}
			return null;
		}
		
		Set<AbstractTransformationPlugin> satp = new HashSet<AbstractTransformationPlugin>();
		
		// initialize transformation settings now.
		for (AbstractTransformationPlugin atp : apls) {
			try {
				atp.getSetting();
				atp.updateSettings(le);
			} catch (Exception e) {
				satp.add(atp);
				System.err.println("Can't add transformation "+atp.getPluginInfo().getName());
				e.printStackTrace();
			}
		}
		
		apls.removeAll(satp);
		
		AbstractTransformationPlugin first = (AbstractTransformationPlugin)apls.iterator().next();
		
		PluginInstanceSetting<AbstractTransformationPlugin> pls = new PluginInstanceSetting<AbstractTransformationPlugin>(
				"Transformation",
				"Select which transformation to add to these experiments",
				first, 
				apls
		);
		
		HierarchicalSetting hs = new HierarchicalSetting("Transformation")
		.addSetting(pls)
		.addSetting(new ComponentPlaceHolderSetting("", new JButton(
			new AbstractAction("Show me why some transformations cannot be applied") {
				public void actionPerformed(ActionEvent e) {
					showApplicabilityReasons(frame,reasons);
				}
			}
		)))
		.setTopMost(true);
		
		SettingDialog sd = new SettingDialog(null, title, hs).showAsInputDialog();
		
		if (!sd.canceled())
			return pls;
		
		return null;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (selection.size()>0) {
			
			LinkedList<Experiment> le = new LinkedList<Experiment>();
			for (ReorderableHorizontalPanel rhp : selection.getSelection()) {
				le.add(((ExperimentPanel)rhp).getExperiment());
			}
			
			PluginInstanceSetting<AbstractTransformationPlugin> pls = showTransformationSettingDialog(le, "Add transformation");
			
			if (pls!=null) {
				AbstractTransformationPlugin t = pls.getInstance();
				LinkedList<Experiment> exps = new LinkedList<Experiment>();
				for (ReorderableHorizontalPanel rhp : selection.getSelection())
					exps.add(((ExperimentPanel)rhp).getExperiment());				
				transMatrix.addTransformation(t, exps);
			}
		} else {
			JOptionPane.showMessageDialog(null, 
					"Please select which experiments the transformation should work on.", 
					"Unable to add transformation", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	} 
	
	public static void showApplicabilityReasons(Window parent, List<String> reasons) {
		final MaydayDialog md = new MaydayDialog(parent, "Applicability requirements of transformations");
		StringBuffer rtext = new StringBuffer();
		rtext.append("<html><table border=0>");
		for (String r : reasons) {
			String rr[] = r.split("\t");
			rtext.append("<tr><td colspan=2><b><font face='sans-serif' size=-1>");
			rtext.append(rr[0]);
			rtext.append("</b></td></tr><tr><td width=50>&nbsp;</td><td width=99%><font face='sans-serif' size=-1>");
			rtext.append(rr[1]);
			rtext.append("</td></tr>\n");
		}	
		rtext.append("</table></html>");
		JEditorPane jta = new JEditorPane("text/html",rtext.toString());
		jta.setEditable(false);
		jta.setCaretPosition(0);
		md.add(new JScrollPane(jta));

		// Add close button with fancy border for seperation
		JButton closeBtn;
		{
			JPanel BottomPanel = new JPanel();
			BoxLayout BottomPanelLayout = new BoxLayout(BottomPanel, javax.swing.BoxLayout.Y_AXIS);
			BottomPanel.setLayout(BottomPanelLayout);
			md.add(BottomPanel, BorderLayout.SOUTH);
			BottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			{
				JSeparator jSeparator2 = new JSeparator();
				BottomPanel.add(jSeparator2);
				JPanel ButtonPanel = new JPanel();
				BottomPanel.add(ButtonPanel);
				FlowLayout ButtonPanelLayout = new FlowLayout();
				ButtonPanelLayout.setAlignment(FlowLayout.RIGHT);
				ButtonPanel.setLayout(ButtonPanelLayout);
				{
					closeBtn = new JButton();
					ButtonPanel.add(closeBtn);
					closeBtn.setText("Close");
				}
			}
		}
		// make that button actually close the window
		closeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				md.setVisible(false);
			}
		});


		md.pack();
		md.setSize(600, 400);
		md.setModal(true);
		md.setVisible(true);
	}
}