package mayday.exportjs.exporter;

import java.util.ArrayList;
import java.util.List;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.StringSetting;

public class HTMLExporterSetting extends HierarchicalSetting {
	
	private StringSetting title;

	// Interaction
	private HierarchicalSetting interaction;
	private BooleanSetting probesTable;
	private BooleanSetting moveLayers;
	private BooleanSetting resizePlots;
	private BooleanSetting arrangePlots;
	private BooleanSetting commentBox;
	private BooleanSetting metaInfoTable;
	
	private String favicon;
	private List<String> externalJsFiles = new ArrayList<String>();

	public HTMLExporterSetting() {
		super("Document");
		
		this.title = new StringSetting("Title", null, "Mayday Plotfolio");
		
		this.interaction = new HierarchicalSetting("Interaction");
		this.probesTable = new BooleanSetting("Probes table", "Extract probes out of a plot into a table.", true);
		this.metaInfoTable = new BooleanSetting("Meta Info Table", null, true);
		this.moveLayers = new BooleanSetting("Move Layers", "Enables you to move plots and tables.", true);
		this.resizePlots = new BooleanSetting("Resize Plots", "Enables you to resize plots.", true);
		this.arrangePlots = new BooleanSetting("Arrange Plots", "Easy arrange plots with this feature.", true);
		this.commentBox = new BooleanSetting("Comment Box (PHP required)", "Comment function. PHP required", false);
		this.interaction.addSetting(this.probesTable).addSetting(this.metaInfoTable).addSetting(this.moveLayers).addSetting(this.resizePlots).addSetting(this.arrangePlots).addSetting(this.commentBox);
		
		this.addSetting(this.title).addSetting(this.interaction);
	}
	
	@Override
	public HierarchicalSetting clone() {
		HTMLExporterSetting gs = new HTMLExporterSetting();
		gs.fromPrefNode(this.toPrefNode());
		return gs;
	}
	
	public String getTitle() {
		return title.getStringValue();
	}

	public void setTitle(String title) {
		this.title.setStringValue(title);
	}

	public String getFavicon() {
		return favicon;
	}

	public void setFavicon(String favicon) {
		this.favicon = favicon;
	}

	public List<String> getExternalJsFiles() {
		return externalJsFiles;
	}

	public void addExternalJsFile(String externalJsFile) {
		this.externalJsFiles.add(externalJsFile);
	}

	public boolean isProbesTableInteraction() {
		return probesTable.getBooleanValue();
	}

	public void setProbesTableInteraction(boolean probesTableInteraction) {
		this.probesTable.setBooleanValue(probesTableInteraction);
	}

	public boolean isResizeInteraction() {
		return resizePlots.getBooleanValue();
	}

	public void setResizeInteraction(boolean resizeInteraction) {
		this.resizePlots.setBooleanValue(resizeInteraction);
	}

	public boolean isMoveInteraction() {
		return moveLayers.getBooleanValue();
	}

	public void setMoveInteraction(boolean moveInteraction) {
		this.moveLayers.setBooleanValue(moveInteraction);
	}

	public boolean isCommentBoxInteraction() {
		return this.commentBox.getBooleanValue();
	}

	public void setCommentBoxInteraction(boolean commentBoxInteraction) {
		this.commentBox.setBooleanValue(commentBoxInteraction);
	}

	public boolean isArrangementInteraction() {
		return arrangePlots.getBooleanValue();
	}

	public void setArrangementInteraction(boolean arrangementInteraction) {
		this.arrangePlots.setBooleanValue(arrangementInteraction);
	}

	public boolean isMetaTableInteraction() {
		return metaInfoTable.getBooleanValue();
	}

	public void setMetaTableInteraction(boolean metaTableInteraction) {
		this.metaInfoTable.setBooleanValue(metaTableInteraction);
	}
	
	private HTMLExporter htmlExporter;
	
	public HTMLExporter getHTMLExporter() {
		return htmlExporter;
	}

	public void createHTMLExporter(List<PlotExporter> plotExporters) {
		htmlExporter = new HTMLExporter(this, plotExporters);
		
	}
	
}