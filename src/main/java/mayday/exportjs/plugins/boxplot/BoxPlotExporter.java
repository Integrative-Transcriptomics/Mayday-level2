package mayday.exportjs.plugins.boxplot;

import mayday.exportjs.exporter.PlotExporter;

public class BoxPlotExporter extends PlotExporter {

	BoxPlotExporterSetting boxPlotExportSettings;

	public BoxPlotExporter(BoxPlotExporterSetting boxPlotExportSettings){
		super("boxPlot");
		this.boxPlotExportSettings = boxPlotExportSettings;
	}

	@Override
	public String getString() {
		String plotScript = "/* " + this.boxPlotExportSettings.getName() + " */\n\n"
		+ "function vis_boxPlot(){\n\n"

		// Data Mapping
		+ "\t/* Data Mapping */\n"

		+ "\tvar keys = [\"experiment\", \"min\", \"max\", \"median\", \"firstQuartile\", \"thirdQuartile\"];\n\n"

		+ "\tvar mappedData = new Array(probeListsLength);\n\n"

		+ "\tfor (var i = 0; i < probeListsLength; i++){\n"
		+ "\t\tvar el = new Array(experimentsLength);\n"
		+ "\t\tmappedData[i] = new Array(experimentsLength)\n"
		+ "\t\tfor(var j = 0; j < experimentsLength; j++){\n"
		+ "\t\t\ttemp = new Array();\n"
		+ "\t\t\ttemp = temp.concat(experiments[j]).concat(probeLists[i].minVals[j]).concat(probeLists[i].maxVals[j]).concat(probeLists[i].medians[j]).concat(probeLists[i].firstQuartiles[j]).concat(probeLists[i].thirdQuartiles[j]);\n"
		+ "\t\t\ttemp = pv.dict(keys, function() temp[this.index]);\n"
		+ "\t\t\tmappedData[i][j] = temp;\n"
		+ "\t\t}\n"
		+ "\t}\n\n"

		+ "\tvar height = " + this.boxPlotExportSettings.getHeight()+ ";\n"
		+ "\tvar width = " + this.boxPlotExportSettings.getWidth() + ";\n"
		+ "\tvar boxColor = \"" + super.createRGBColorString(this.boxPlotExportSettings.getBoxColor().getRed(), this.boxPlotExportSettings.getBoxColor().getGreen(), this.boxPlotExportSettings.getBoxColor().getBlue()) + "\";\n\n"

		+ "\tvar selectedProbesLineWidth = " + this.boxPlotExportSettings.getSelectedProbesLineWidth() + ";\n\t"
		+ super.createSelectedProbesColorString("selectedProbesColor", this.boxPlotExportSettings.getSelectedProbesColor().getRed(), this.boxPlotExportSettings.getSelectedProbesColor().getGreen(), this.boxPlotExportSettings.getSelectedProbesColor().getBlue())

		+ "\tvar multiplePlotsPadding = " + this.boxPlotExportSettings.getMultiplePlotsPadding() + ";\n\n"

		+ "\t/* Scaling*/\n"
		+ "\tvar x_position;\n"
		+ "\tvar y_position;\n"
		+ "\tvar s;\n"
		+ "\tvar x_selected;\n\n"

		+ "\t/* Update Scales */\n"
		+ "\tfunction updateScales(h, w){\n"
		+ "\t\theight = h;\n"
		+ "\t\twidth = w;\n"
		+ "\t\tx_position = pv.Scale.ordinal(experiments).splitBanded(0, width, 3/5);\n"
		+ "\t\ty_position = pv.Scale.linear(globalMinVal, globalMaxVal).range(0, height);\n"
		+ "\t\ts = x_position.range().band / 2;\n"
		+ "\t\tx_selected = pv.Scale.linear(0, experimentsLength-1).range(x_position(experiments[0])+s, x_position(experiments[experimentsLength-1])+s);\n"
		+ "\t}\n\n"

		+ "\tupdateScales(height, width);\n\n"

		+ "\tfunction getHeight(){\n"
		+ "\t\treturn ((vis.height() + multiplePlotsPadding) / probeListsLength) - multiplePlotsPadding;\n"
		+ "\t}\n\n"

		+ "\tfunction getWidth(){\n"
		+ "\treturn vis.width();\n"
		+ "\t}\n\n"

		+ "\t/* Main Panel */\n"
		+ "\tvar vis = new pv.Panel()\n"
		+ "\t\t.width(function() width)\n"
		+ "\t\t.height(function() ((height + multiplePlotsPadding) * probeListsLength)-multiplePlotsPadding)\n"
		+ "\t\t.bottom(100)\n"
		+ "\t\t.left(100)\n"
		+ "\t\t.right(100)\n"
		+ "\t\t.top(5);\n\n"

		+ "\t/* Main Panel Title x-Axis */\n"
		+ "\tvis.add(pv.Label)\n"
		+ "\t\t.left(function() width/2)\n"
		+ "\t\t.bottom(-90)\n"
		+ "\t\t.textAlign(\"center\")\n"
		+ "\t\t.font(\"12px sans-serif\")\n"
		+ "\t\t.text(\"Experiment\");\n\n"

		+ "\t/* Main Panel Title y-Axis */\n"
		+ "\tvis.add(pv.Label)\n"
		+ "\t\t.top(function() (((height + multiplePlotsPadding) * probeListsLength)-multiplePlotsPadding)/2)\n"
		+ "\t\t.left(-70)\n"
		+ "\t\t.textAngle(-Math.PI / 2)\n"
		+ "\t\t.textAlign(\"center\")\n"
		+ "\t\t.font(\"12px sans-serif\")\n"
		+ "\t\t.text(\"Expression Value\");\n\n"

		+ "\t/* Main panel bottom border line */\n"
		+ "\tvis.add(pv.Rule)\n"
		+ "\t\t.strokeStyle(\"#000\")\n"
		+ "\t\t.bottom(-70);\n\n"

		+ "\t/* Main panel left border line */\n"
		+ "\tvis.add(pv.Rule)\n"
		+ "\t\t.strokeStyle(\"#000\")\n"
		+ "\t\t.left(-65);\n\n"

		+ "\t/* Panel for each probeList */\n"
		+ "\tvar probeListPanel = vis.add(pv.Panel)\n"
		+ "\t\t.data(mappedData)\n"
		+ "\t\t.height(function() height)\n"
		+ "\t\t.top(function() this.index * (height + multiplePlotsPadding))\n"
		+ "\t\t.strokeStyle(\"#000\")\n"
		+ "\t\t.lineWidth(1)\n"
		+ "\t\t.antialias(false);\n\n"

		+ "\t/* Add the y-axis rules and labels */\n"
		+ "\tvar yaxis = probeListPanel.add(pv.Rule)\n"
		+ "\t\t.data(function() y_position.ticks())\n"
		+ "\t\t.bottom(function(d) y_position(d))\n"
		+ "\t\t.strokeStyle(\"#eee\")\n"
		+ "\t\t.anchor(\"left\").add(pv.Label)\n"
		+ "\t\t.text(y_position.tickFormat);\n\n"

		+ "\t/* Add a panel for each data point */\n"
		+ "\tvar experimentsPanel = probeListPanel.add(pv.Panel)\n"
		+ "\t\t.data(function(d) d)\n"
		+ "\t\t.left(function(d) x_position(d.experiment))\n"
		+ "\t\t.width(function() s * 2);\n\n"

		+ "\t/* Add the experiment id label */\n"
		+ "\texperimentsPanel.anchor(\"bottom\").add(pv.Label)\n"
		+ "\t\t.textAlign(\"right\")\n"
		+ "\t\t.textBaseline(\"middle\")\n"
		+ "\t\t.textAngle(-Math.PI / 2)\n"
		+ "\t\t.text(function(d) d.experiment);\n\n"

		+ "\t/* Add the range line */\n"
		+ "\texperimentsPanel.add(pv.Rule)\n"
		+ "\t\t.left(function() s)\n"
		+ "\t\t.bottom(function(d) y_position(d.min))\n"
		+ "\t\t.height(function(d) y_position(d.max) - y_position(d.min));\n\n"

		+ "\t/* Add the min and max indicators */\n"
		+ "\texperimentsPanel.add(pv.Rule)\n"
		+ "\t\t.data(function(d) [d.min, d.max])\n"
		+ "\t\t.bottom(function(d)  y_position(d))\n"
		+ "\t\t.left(function() s / 2)\n"
		+ "\t\t.width(function() s);\n\n"

		+ "\t/* Add the upper/lower quartile ranges */\n"
		+ "\texperimentsPanel.add(pv.Bar)\n"
		+ "\t\t.bottom(function(d) y_position(d.firstQuartile))\n"
		+ "\t\t.height(function(d) y_position(d.thirdQuartile) - y_position(d.firstQuartile))\n"
		+ "\t\t.fillStyle(function() boxColor)\n"
		+ "\t\t.strokeStyle(\"black\")\n"
		+ "\t\t.lineWidth(1)\n"
		+ "\t\t.antialias(false);\n\n"

		+ "\t/* Add the median line */\n"
		+ "\texperimentsPanel.add(pv.Rule)\n"
		+ "\t\t.bottom(function(d) y_position(d.median));\n\n";

		if (this.boxPlotExportSettings.isShowSelectedProbes()){
			plotScript += paintSelectedProbes();

			if (this.boxPlotExportSettings.isShowSelectedProbesLabels()){
				plotScript += addSelectedProbesLabels();
			}
		}

		return plotScript += "\tvis.render();\n\n"
			+ "\treturn {plot: vis, updateScales: updateScales, height: getHeight, width: getWidth};\n}\n\n"
			+ "var " + this.variableName + "= vis_boxPlot();\n\n";
	}

	@Override
	public String getName() {
		return this.boxPlotExportSettings.getName();
	}

	private String paintSelectedProbes(){
		String result;
		if (this.boxPlotExportSettings.isProbesTableInteraction() || this.boxPlotExportSettings.isMetaTableInteraction()){

			result = "\t/* Panel for each probeList */\n"
				+ "\tvar probeListPanel = vis.add(pv.Panel)\n"
				+ "\t\t.data(probeLists)\n"
				+ "\t\t.height(function() height)\n"
				+ "\t\t.top(function() this.index * (height + multiplePlotsPadding));\n\n"

				+ "\t/* Panel for each probe to panel of probeList */\n"
				+ "\tvar  selectedProbesPanel = probeListPanel.add(pv.Panel)\n"
				+ "\t\t.data(function(d) d.probes);\n\n"

				+ "\t/* Panel for interaction */\n"
				+ "\t\tvar selectedInteractionPanel = selectedProbesPanel.add(pv.Panel)\n"
				+ "\t\t.def(\"active\", false);\n\n"

				+ "\t/* Add selected probe line to the probe panel */\n"
				+ "\tvar line = selectedInteractionPanel.add(pv.Line)\n"
				+ "\t\t.data(function(d) (d.selected == true) ? d.values : [])\n"
				+ "\t\t.bottom(function(d) y_position(d))\n"
				+ "\t\t.left(function() x_selected(this.index))\n"
				+ "\t\t.lineWidth(function() this.parent.active() ? 5 : selectedProbesLineWidth)\n"
				+ "\t\t.strokeStyle(function() selectedProbesColor)\n"
				+ "\t\t.title(function(d, f) \"Name: \" + f.name + \" || Min. Value: \" + pv.min(f.values) + \" || Max. Value: \" + pv.max(f.values))\n"
				+ "\t\t.event(\"mouseover\", function() this.parent.active(true))\n"
				+ "\t\t.event(\"mouseout\", function() this.parent.active(false))\n"
				+ "\t\t.event(\"mousedown\", function(d, f){";
				if (this.boxPlotExportSettings.isProbesTableInteraction())
					result += "makeEntry(\"probes\", new Array(f.name).concat(f.values), f.color);";
				if (this.boxPlotExportSettings.isMetaTableInteraction()) 
					result += "makeMetaTableEntry(\"metainfo\", f.name, f.meta);";
				result += "});\n\n";
		}
		else {
			result = "\t/* Panel for each probeList */\n"
				+ "\tvar probeListPanel = vis.add(pv.Panel)\n"
				+ "\t\t.data(probeLists)\n"
				+ "\t\t.height(function() height)\n"
				+ "\t\t.top(function() this.index * (height + multiplePlotsPadding));\n\n"
				+ "\t/* Panel for each probe to panel of probeList */\n"
				+ "\tvar  selectedProbesPanel = probeListPanel.add(pv.Panel)\n"
				+ "\t\t.data(function(d) d.probes);\n\n"

				+ "\t/* Add selected probe line to the probe panel */\n"
				+ "\tvar line = selectedProbesPanel.add(pv.Line)\n"
				+ "\t\t.data(function(d) (d.selected == true) ? d.values : [])\n"
				+ "\t\t.bottom(function(d) y_position(d))\n"
				+ "\t\t.left(function() x_selected(this.index))\n"
				+ "\t\t.lineWidth(function() selectedProbesLineWidth)\n"
				+ "\t\t.strokeStyle(function() selectedProbesColor)\n"
				+ "\t\t.title(function(d, f) \"Name: \" + f.name + \" || Min. Value: \" + pv.min(f.values) + \" || Max. Value: \" + pv.max(f.values));\n\n";
		}
		return result;
	}

	private String addSelectedProbesLabels(){
		String result = "\t/* Add labels to selected Probes */\n"
			+ "\tvar label = selectedProbesPanel.add(pv.Label)\n"
			+ "\t\t.text(function(d) (d.selected == true) ? d.name : null)\n"
			+ "\t\t.bottom(function(d) (d.selected == true) ? y_position(d.values[experimentsLength-1]) : null)\n"
			+ "\t\t.right(0)\n"
			+ "\t\t.textBaseline(\"middle\");\n\n";

		return result;
	}

	@Override
	public String getPlotOptions() {
		return null;
	}

	@Override
	public String getPlotDescription() {
		String d = this.boxPlotExportSettings.getDescription();
		if (!d.isEmpty())
			return d;
		else
			return null;
	}
}
