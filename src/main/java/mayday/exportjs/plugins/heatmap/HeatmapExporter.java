package mayday.exportjs.plugins.heatmap;

import mayday.exportjs.exporter.PlotExporter;



public class HeatmapExporter extends PlotExporter {

	HeatmapExporterSetting heatmapExportSettings;
	
	public HeatmapExporter(HeatmapExporterSetting heatmapExportSettings){
		super("heatmap");
		this.heatmapExportSettings = heatmapExportSettings;
	}

	@Override
	public String getString() {
		String plotScript = "/* " + this.heatmapExportSettings.getName() + " */\n\n"

		+ "function vis_heatmap(){\n\n"
		
		+ "\t/* New names for experiments to use as keys\n"
		+ "\tmappedExperiments = [\"meta\", \"color\", \"name\", \"selected\", \"exp1\", \"exp2\", ...] */\n"
		+ "\tvar mappedExperiments = new Array(\"meta\", \"color\", \"selected\",\"name\");\n"
		+ "\tfor (var i = 0; i < experimentsLength; i++){\n"
		+ "\t\tmappedExperiments = mappedExperiments.concat(\"exp\" + i);\n"
		+ "\t}\n\n"

		+ "\t/* Map probe lists\n"
		+ "\tmappedProbeLists = [[selected: \"asd\", name:\"xyz\", exp1: 12, exp2: 11, ...], [selected: \"asd\", name:\"xyz\", exp1: 12, exp2: 11, ...]] */\n"
		+ "\tvar mappedProbeLists = new Array();\n"
		+ "\tfor (var i = 0; i < probeListsLength; i++){\n"
		+ "\t\tmappedProbes = probeLists[i].probes.map(function(d) pv.dict(mappedExperiments, function() new Array(d.meta).concat(d.color).concat(d.selected).concat(d.name).concat(d.values)[this.index]));\n"
		+ "\t\tmappedProbeLists = mappedProbeLists.concat(mappedProbes);\n"
		+ "\t}\n\n"
		
		+ "\tmappedExperiments.shift();\n"
		+ "\tmappedExperiments.shift();\n"
		+ "\tmappedExperiments.shift();\n"
		+ "\tmappedExperiments.shift();\n\n"

		+ "\tvar height = " + this.heatmapExportSettings.getHeight()+ ";\n"
		+ "\tvar width = " + this.heatmapExportSettings.getWidth() + ";\n\n"

		+ "\tvar selectedProbesLineWidth_Heatmap = " + this.heatmapExportSettings.getSelectedProbesLineWidth() + ";\n\t"
		+ super.createSelectedProbesColorString("selectedProbesColor_Heatmap",this.heatmapExportSettings.getSelectedProbesColor().getRed(), this.heatmapExportSettings.getSelectedProbesColor().getGreen(), this.heatmapExportSettings.getSelectedProbesColor().getBlue())

		+ "\t/* Color gradient */\n"
		+ "\tvar meanGlobalVals = pv.mean([globalMaxVal, globalMinVal]);\n"
		+ "\tvar fill = pv.Scale.linear(globalMinVal, meanGlobalVals ,globalMaxVal).range(\"" + this.createRGBColorString(this.heatmapExportSettings.getMinColor()) + "\", \"" + this.createRGBColorString(this.heatmapExportSettings.getMidColor()) + "\", \"" + this.createRGBColorString(this.heatmapExportSettings.getMaxColor()) + "\");\n"
		+ "\tvar stepPixel = 1;\n"
		+ "\tvar step;\n\n"

		+ "\t/* Update Scales */\n"
		+ "\tfunction updateScales(h, w){\n"
		+ "\t\theight = h;\n"
		+ "\t\twidth = w;\n"
		+ "\t\tstep = (globalMaxVal-globalMinVal)/ ((mappedExperiments.length * width) / stepPixel);\n"
		+ "\t}\n\n"
		
		+ "\tupdateScales(height, width);\n\n"

		+ "\tfunction getHeight(){\n"
		+ "\t\treturn (vis.height() / mappedProbeLists.length);\n"
		+ "\t}\n\n"
		
		+ "\tfunction getWidth(){\n"
		+ "\t\treturn (vis.width() / mappedExperiments.length);\n"
		+ "\t}\n\n"
		
		+ "\t/* Main panel */\n"
		+ "\tvar vis = new pv.Panel()\n"
		+ "\t\t.width(function() mappedExperiments.length * width)\n"
		+ "\t\t.height(function() mappedProbeLists.length * height)\n"
		+ "\t\t.top(100)\n"
		+ "\t\t.left(100)\n"
		+ "\t\t.right(50)\n"
		+ "\t\t.bottom(5);\n\n"

		+ "\t/* Add Panel for each probe */\n"
		+ "\tvar probesPanel = vis.add(pv.Panel)\n"
		+ "\t\t.data(mappedProbeLists)\n"
		+ "\t\t.top(function() this.index * height)\n"
		+ "\t\t.height(function() height);\n\n"

		+ "\t/* Add Panel for each experiment */\n"
		+ "\tprobesPanel.add(pv.Panel)\n"
		+ "\t\t.data(mappedExperiments)\n"
		+ "\t\t.left(function() this.index * width)\n"
		+ "\t\t.width(function() width)\n"
		+ "\t\t.fillStyle(function(d, f) fill(f[d]))\n"
		+ "\t\t.strokeStyle(\"white\")\n"
		+ "\t\t.lineWidth(0)\n"
		+ "\t\t.antialias(false)\n"
		+ "\t\t.title(function(d, f) \"Name: \" + f.name + \", Experiment: \" + experiments[this.index] + \", Value: \" + f[d]);\n\n";

		if ((this.heatmapExportSettings.isProbesTableInteraction() || this.heatmapExportSettings.isMetaTableInteraction()) && this.heatmapExportSettings.isShowSelectedProbes()){
			plotScript += "\t/* Panel for interaction */\n"
				+ "\tvar interactionPanel = probesPanel.add(pv.Panel)\n"
				+ "\t\t.width(function() mappedExperiments.length * width -selectedProbesLineWidth_Heatmap)\n"
				+ "\t\t.height(function() height -selectedProbesLineWidth_Heatmap)\n"
				+ "\t\t.def(\"active\", false);\n\n"

				+ "\t/* Mark selected Probes */\n"
				+ "\tvar selected = interactionPanel.add(pv.Panel)\n"
				+ "\t\t.top(selectedProbesLineWidth_Heatmap/2)\n"
				+ "\t\t.left(selectedProbesLineWidth_Heatmap/2)\n"
				+ "\t\t.lineWidth(selectedProbesLineWidth_Heatmap)\n"
				+ "\t\t.strokeStyle(function(d) d.selected ? selectedProbesColor_Heatmap : null)\n"
				+ "\t\t.fillStyle(function() this.parent.active() ? \"rgba(0, 0, 0, 0.3)\" : \"rgba(0, 0, 0, 0.000001)\")\n"
				+ "\t\t.title(function(d,f) \"Name: \" + f.name)\n"
				+ "\t\t.event(\"mouseover\", function() this.parent.active(true))\n"
				+ "\t\t.event(\"mouseout\", function() this.parent.active(false))\n"
				+ "\t\t.event(\"mousedown\", function(d, f){";
				if (this.heatmapExportSettings.isProbesTableInteraction())
					plotScript += "makeEntry(\"probes\", pv.values(f).slice(3), f.color);";
				if (this.heatmapExportSettings.isMetaTableInteraction()) 
					plotScript += "makeMetaTableEntry(\"metainfo\", f.name, f.meta);";
				plotScript += "});\n\n"
				
				+ "\t/* Probe labels */\n"
				+ "\tvis.add(pv.Label)\n"
				+ "\t\t.data(mappedProbeLists)\n"
				+ "\t\t.top(function() this.index * height + height / 2)\n"
				+ "\t\t.textAlign(\"right\")\n"
				+ "\t\t.textBaseline(\"middle\")\n"
				+ "\t\t.antialias(false)\n"
				+ "\t\t.textStyle(function(d) d.selected ? selectedProbesColor_Heatmap : \"black\")\n"
				+ "\t\t.text(function(d) d.name);\n\n";
		}
		else if ((this.heatmapExportSettings.isProbesTableInteraction() || this.heatmapExportSettings.isMetaTableInteraction()) && !this.heatmapExportSettings.isShowSelectedProbes()){
			plotScript += "\t/* Panel for interaction */\n"
				+ "\tvar interactionPanel = 	probesPanel.add(pv.Panel)\n"
				+ "\t\t.width(function() mappedExperiments.length * width -selectedProbesLineWidth_Heatmap)\n"
				+ "\t\t.height(function() height -selectedProbesLineWidth_Heatmap)\n"
				+ "\t\t.def(\"active\", false);\n\n"

				+ "\t/* Mark selected Probes */\n"
				+ "\tvar selected = interactionPanel.add(pv.Panel)\n"
				+ "\t\t.top(selectedProbesLineWidth_Heatmap/2)\n"
				+ "\t\t.left(selectedProbesLineWidth_Heatmap/2)\n"
				+ "\t\t.fillStyle(function() this.parent.active() ? \"rgba(0, 0, 0, 0.3)\" : \"rgba(0, 0, 0, 0.000001)\")\n"
				+ "\t\t.title(function(d,f) \"Name: \" + f.name)\n"
				+ "\t\t.event(\"mouseover\", function() this.parent.active(true))\n"
				+ "\t\t.event(\"mouseout\", function() this.parent.active(false))\n"
				+ "\t\t.event(\"mousedown\", function(d, f){";
				if (this.heatmapExportSettings.isProbesTableInteraction())
					plotScript += "makeEntry(\"probes\", pv.values(f).slice(3), f.color);";
				if (this.heatmapExportSettings.isMetaTableInteraction()) 
					plotScript += "makeMetaTableEntry(\"metainfo\", f.name, f.meta);";
				plotScript += "});\n\n"
					
				+ "\t/* Probe labels */\n"
				+ "\tvis.add(pv.Label)\n"
				+ "\t\t.data(mappedProbeLists)\n"
				+ "\t\t.top(function() this.index * height + height / 2)\n"
				+ "\t\t.textAlign(\"right\")\n"
				+ "\t\t.antialias(false)\n"
				+ "\t\t.textBaseline(\"middle\")\n"
				+ "\t\t.text(function(d) d.name);\n\n";
		}
		else if (!this.heatmapExportSettings.isProbesTableInteraction() && !this.heatmapExportSettings.isMetaTableInteraction() && this.heatmapExportSettings.isShowSelectedProbes()){
			plotScript += "\t/* Mark selected Probes */\n"
				+ "\tvar selected = probesPanel.add(pv.Panel)\n"
				+ "\t\t.width(function() mappedExperiments.length * width - selectedProbesLineWidth_Heatmap)\n"
				+ "\t\t.height(function() height - selectedProbesLineWidth_Heatmap)\n"
				+ "\t\t.top(selectedProbesLineWidth_Heatmap/2)\n"
				+ "\t\t.left(selectedProbesLineWidth_Heatmap/2)\n"
				+ "\t\t.lineWidth(selectedProbesLineWidth_Heatmap)\n"
				+ "\t\t.strokeStyle(function(d) d.selected ? selectedProbesColor_Heatmap : null);\n\n"

				+ "\t/* Probe labels */\n"
				+ "\tvis.add(pv.Label)\n"
				+ "\t\t.data(mappedProbeLists)\n"
				+ "\t\t.top(function() this.index * height + height / 2)\n"
				+ "\t\t.textAlign(\"right\")\n"
				+ "\t\t.antialias(false)\n"
				+ "\t\t.textBaseline(\"middle\")\n"
				+ "\t\t.textStyle(function(d) d.selected ? selectedProbesColor_Heatmap : \"black\")\n"
				+ "\t\t.text(function(d) d.name);\n\n";
		}
		else {
			plotScript += "\t/* Probe labels */\n"
				+ "\tvis.add(pv.Label)\n"
				+ "\t\t.data(mappedProbeLists)\n"
				+ "\t\t.top(function() this.index * height + height / 2)\n"
				+ "\t\t.textAlign(\"right\")\n"
				+ "\t\t.antialias(false)\n"
				+ "\t\t.textBaseline(\"middle\")\n"
				+ "\t\t.text(function(d) d.name);\n\n";
		}

		plotScript += "\t/* Paint experiment names */\n"
			+ "\tvis.add(pv.Label)\n"
			+ "\t\t.data(experiments)\n"
			+ "\t\t.left(function() this.index * width + width / 2)\n"
			+ "\t\t.textAngle(-Math.PI / 2)\n"
			+ "\t\t.antialias(false)\n"
			+ "\t\t.textBaseline(\"middle\");\n\n"

			+ "\t/* Gradient Panel */\n"
			+ "\tvar gradientScalePanel = vis.add(pv.Panel)\n"
			+ "\t\t.left(0)\n"
			+ "\t\t.height(10)\n"
			+ "\t\t.top(-60)\n"
			+ "\t\t.lineWidth(1)\n"
			+ "\t\t.antialias(false)\n"
			+ "\t\t.strokeStyle(\"black\")\n\n"

			+ "\t/* Add bars to gradient panel => color gradient */\n"
			+ "\tgradientScalePanel.add(pv.Bar)\n"
			+ "\t\t.data(function() pv.range(globalMinVal, globalMaxVal, step))\n"
			+ "\t\t.left(function() this.index * stepPixel)\n"
			+ "\t\t.width(stepPixel)\n"
			+ "\t\t.fillStyle(fill)\n\n"

			+ "\t/* Add labels to gradient panel */\n"
			+ "\tgradientScalePanel.add(pv.Label)\n"
			+ "\t\t.data(cutVals([globalMinVal, meanGlobalVals, globalMaxVal]))\n"
			+ "\t\t.textAlign(\"center\")\n"
			+ "\t\t.left(function() this.index * mappedExperiments.length * width / 2)\n"
			+ "\t\t.textBaseline(\"bottom\")\n"
			+ "\t\t.antialias(false)\n"
			+ "\t\t.text(function(d) d);\n\n"

			+ "\tvis.render();\n\n"
			+ "\treturn {plot: vis, updateScales: updateScales, height: getHeight, width: getWidth};\n}\n\n"
			+ "var " + this.variableName + " = vis_heatmap();\n\n";

		return plotScript;
	}
	
/*	private String makeColorGradient(){
		String result = "";
		int res = this.heatmapExportSettings.getColorGradient().getResolution();
		for(int i=0; i<res; i++){
			int red = this.heatmapExportSettings.getColorGradient().getColor(i).getRed();
			int green = this.heatmapExportSettings.getColorGradient().getColor(i).getGreen();
			int blue = this.heatmapExportSettings.getColorGradient().getColor(i).getBlue();
			result +=  "\"" + createRGBColorString(red, green, blue) + "\", ";
		}
		
		if (result.endsWith(", "))
			result = result.substring(0, result.length()-2);
		
		return result;
	}*/
	
	@Override
	public String getName() {
		return this.heatmapExportSettings.getName();
	}

	@Override
	public String getPlotOptions() {
		return null;
	}
	
	@Override
	public String getPlotDescription() {
		String d = this.heatmapExportSettings.getDescription();
		if (!d.isEmpty())
			return d;
		else
			return null;
	}

}