package mayday.motifsearch;

import java.util.ArrayList;
import java.util.HashMap;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;
import mayday.core.settings.Settings;
import mayday.core.settings.SettingsDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.typed.PathSetting;
import mayday.motifsearch.gui.MotifSearchVisSettingDialog;
import mayday.motifsearch.interfaces.IMotifSearchAlgoStarter;
import mayday.motifsearch.interfaces.SupportedAlgorithms;

public class MotifSearchVis extends AbstractPlugin implements GenericPlugin {
	private PathSetting filesSettingParseAndVisualize;
	private SupportedAlgorithms supportedAlgorithms;
	private SelectableHierarchicalSetting sHSforAlgorithms;

	static PluginInfo pli;

	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		pli = new PluginInfo(this.getClass(), "PAS.motifsearch.MotifSearchVis",
				new String[0],
				Constants.MC_SESSION,
				// Constants.MC_SESSION,
				new HashMap<String, Object>(), "Frederik Weber",
				"frederik.weber@student.uni-tuebingen.de",
				"Visualisation of motif search resluts", "Motif Search Vis");
		pli.setMenuName("Motif Search Visualization");
		// pli.setIcon(iconPath);
		return pli;
	}

	public void run() {
		this.filesSettingParseAndVisualize = new PathSetting(
				"parsing folder:",
				"choose folder to parse from with a parser for the selected algorithm",
				"", true, false, true);

		this.supportedAlgorithms = new SupportedAlgorithms();

		ArrayList<HierarchicalSetting> sHSAlgos = new ArrayList<HierarchicalSetting>();

		for (IMotifSearchAlgoStarter ma : this.supportedAlgorithms.values()) {
			HierarchicalSetting hSAlgos = new HierarchicalSetting(ma.toString())
					.setCombineNonhierarchicalChildren(true).setLayoutStyle(
							HierarchicalSetting.LayoutStyle.PANEL_VERTICAL);
			sHSAlgos.add(hSAlgos);
		}

		this.sHSforAlgorithms = new SelectableHierarchicalSetting("algorithms",
				null, 0, sHSAlgos.toArray(),
				SelectableHierarchicalSetting.LayoutStyle.COMBOBOX, false);

		HierarchicalSetting hierarchicalSetting = new HierarchicalSetting(
				"parse and visualize", LayoutStyle.PANEL_VERTICAL, true)
				.addSetting(this.filesSettingParseAndVisualize).addSetting(
						sHSforAlgorithms);

		Settings settings = new Settings(hierarchicalSetting,
				pli.getPreferences());

		SettingsDialog dialog = new MotifSearchVisSettingDialog(null,
				"Motif Search Visualization", settings,
				this.supportedAlgorithms.get(this.sHSforAlgorithms
						.getValueString()));
		dialog.setVisible(true);
	}
}
