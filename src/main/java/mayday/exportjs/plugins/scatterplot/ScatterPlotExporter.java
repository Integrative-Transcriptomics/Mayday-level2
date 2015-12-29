package mayday.exportjs.plugins.scatterplot;

import mayday.exportjs.exporter.PlotExporter;

public class ScatterPlotExporter extends PlotExporter {

	ScatterPlotExporterSetting scatterPlotExportSettings;

	public ScatterPlotExporter(ScatterPlotExporterSetting scatterPlotExportSettings){
		super("scatterPlot");
		this.scatterPlotExportSettings = scatterPlotExportSettings;
	}

	@Override
	public String getName() {
		return this.scatterPlotExportSettings.getName();
	}

	@Override
	public String getString() {
		String plotScript = "/* " + this.scatterPlotExportSettings.getName() + " */\n\n"
		+	"function vis_scatterPlot(){\n\n"

		+ "\t// New names for experiments to use as keys\n"
		+ "\t// mappedExperiments = [\"meta\", \"probelist\", \"selected\", \"color\", \"name\", \"exp1\", \"exp2\", ...]\n"
		+ "\tvar mappedExperiments = new Array(\"meta\", \"probelist\", \"selected\", \"color\", \"name\");\n"
		+ "\tfor (var i = 0; i < experimentsLength; i++){\n"
		+ "\t\tmappedExperiments = mappedExperiments.concat(\"exp\" + i);\n"
		+ "\t}\n\n"

		+ "\t// Map probe lists\n"
		+ "\t// mappedProbeLists = [[probelist: \"asd\", selected: true, color: \"rgb(1,1,1)\", name: \"probe1\", exp1: 12, exp2: 11, ...], ...]\n"
		+ "\tvar mappedProbeLists = new Array();\n"
		+ "\tvar probeListNames = new Array();\n\n"

		+ "\tfor (var i = 0; i < probeListsLength; i++){\n"
		+ "\t\tprobeListNames = probeListNames.concat(probeLists[i].name);\n"
		+ "\t\tmappedProbes = probeLists[i].probes.map(function(d) pv.dict(mappedExperiments, function() new Array(d.meta).concat(probeLists[i].name).concat(d.selected).concat(d.color).concat(d.name).concat(d.values)[this.index]));\n"
		+ "\t\tmappedProbeLists = mappedProbeLists.concat(mappedProbes);\n"
		+ "\t}\n\n"

		+ "\tmappedExperiments.shift();\n"
		+ "\tmappedExperiments.shift();\n"
		+ "\tmappedExperiments.shift();\n"
		+ "\tmappedExperiments.shift();\n"
		+ "\tmappedExperiments.shift();\n\n"

		+ "\t/* Size parameters. */\n"
		+ "\tvar height = " + this.scatterPlotExportSettings.getHeight() + ";\n"
		+ "\tvar width = " + this.scatterPlotExportSettings.getWidth() + ";\n"
		+ "\tvar dotSize = " + this.scatterPlotExportSettings.getProbesDotSize() + ";\n"
		+ "\tvar selectedDotSize = " + this.scatterPlotExportSettings.getSelectedProbesDotSize() + ";\n\n"

		+ "\t/* Color */\n\t"
		+ super.createSelectedProbesColorString("selectedProbesColor", this.scatterPlotExportSettings.getSelectedProbesColor().getRed(), this.scatterPlotExportSettings.getSelectedProbesColor().getGreen(), this.scatterPlotExportSettings.getSelectedProbesColor().getBlue())
		+ "\tvar transparency = " + this.scatterPlotExportSettings.getTransparency() + ";\n\n"

		+ "\t/* Scales for position */\n"
		+ "\tvar x_position;\n"
		+ "\tvar y_position;\n\n"

		+ "\t/* Update Scales */\n"
		+ "\tfunction updateScales(h, w){\n"
		+ "\t\theight = h;\n"
		+ "\t\twidth = w;\n"
		+ "\t\tx_position = pv.dict(mappedExperiments, function(t) pv.Scale.linear(mappedProbeLists, function(d) d[t]).range(0, width));\n"
		+ "\t\ty_position = pv.dict(mappedExperiments, function(t) pv.Scale.linear(mappedProbeLists, function(d) d[t]).range(0, height));\n"
		+ "\t}\n\n"

		+ "\tupdateScales(height, width);\n\n";

		if (this.scatterPlotExportSettings.isShowPlotMatrix()){
			plotScript += createPlotMatrix();
		}
		else  {
			plotScript += createSinglePlot();
		}

		plotScript += "\tvis.render();\n\n";

		if (this.scatterPlotExportSettings.isShowPlotMatrix()) {
			plotScript += "\treturn {plot: vis, updateScales: updateScales, height: getHeight, width: getWidth};\n"
				+ "\t}\n\n";
		}
		else {
			plotScript += "\treturn {plot: vis, updateScales: updateScales, height: getHeight, width: getWidth, changeSelectedExperiments: changeSelectedExperiments, selectedExperiments:selectedExperiments};\n"
				+ "\t}\n\n";
		}

		plotScript += "var scatterPlot = vis_scatterPlot();\n\n";

		return plotScript;
	}

	private String createPlotMatrix() {
		String result = "\tvar matrixPadding = " + this.scatterPlotExportSettings.getPlotMatrixPadding() + ";\n\n"

		+ "\tfunction getHeight(){\n"
		+ "\t\treturn ((vis.height() - matrixPadding) / experimentsLength) - matrixPadding;\n"
		+ "\t}\n\n"

		+ "\tfunction getWidth(){\n"
		+ "\t\treturn (vis.width()  / experimentsLength) - matrixPadding;\n"
		+ "\t}\n\n"

		+ "\t/* Main panel. */\n"
		+ "\tvar vis = new pv.Panel()\n"
		+ "\t\t.width(function() (width + matrixPadding) * experimentsLength)\n"
		+ "\t\t.height(function() ((height + matrixPadding) * experimentsLength) + matrixPadding)\n"
		+ "\t\t.left(40)\n"
		+ "\t\t.right(40)\n"
		+ "\t\t.bottom(40)\n"
		+ "\t\t.top(40);\n\n"

		+ "\t/* One cell per experiment pair. */\n"
		+ "\tvar cell = vis.add(pv.Panel)\n"
		+ "\t\t.data(mappedExperiments)\n"
		+ "\t\t.top(function() this.index * (height + matrixPadding) + matrixPadding / 2)\n"
		+ "\t\t.height(function() height)\n"
		+ "\t\t.add(pv.Panel)\n"
		+ "\t\t.data(function(y) mappedExperiments.map(function(x) ({px:x, py:y})))\n"
		+ "\t\t.left(function() this.index * (width + matrixPadding) + matrixPadding / 2)\n"
		+ "\t\t.width(function() width);\n\n"

		+ "\t/* Framed dot plots not along the diagonal. */\n"
		+ "\tvar plot = cell.add(pv.Panel)\n"
		+ "\t\t.visible(function(t) t.px != t.py)\n"
		+ "\t\t.strokeStyle(\"#aaa\")\n"
		+ "\t\t.def(\"i\", -1);\n\n"

		+ "\t/* X-axis ticks. */\n"
		+ "\tvar xtick = plot.add(pv.Rule)\n"
		+ "\t\t.data(function(t) x_position[t.px].ticks(5))\n"
		+ "\t\t.left(function(d, t) x_position[t.px](d))\n"
		+ "\t\t.strokeStyle(\"#eee\");\n\n"

		+ "\t/* Bottom label. */\n"
		+ "\txtick.anchor(\"bottom\").add(pv.Label)\n"
		+ "\t\t.visible(function() cell.parent.index == experimentsLength - 1)\n"
		+ "\t\t.text(function(d, t) x_position[t.px].tickFormat(d));\n\n"

		+ "\t/* Top label. */\n"
		+ "\txtick.anchor(\"top\").add(pv.Label)\n"
		+ "\t\t.visible(function() cell.parent.index == 0)\n"
		+ "\t\t.text(function(d, t) x_position[t.px].tickFormat(d));\n\n"

		+ "\t/* Y-axis ticks. */\n"
		+ "\tvar ytick = plot.add(pv.Rule)\n"
		+ "\t\t.data(function(t) y_position[t.py].ticks(5))\n"
		+ "\t\t.bottom(function(d, t) y_position[t.py](d))\n"
		+ "\t\t.strokeStyle(\"#eee\");\n\n"

		+ "\t/* Left label. */\n"
		+ "\tytick.anchor(\"left\").add(pv.Label)\n"
		+ "\t\t.visible(function() cell.index == 0)\n"
		+ "\t\t.text(function(d, t) y_position[t.py].tickFormat(d));\n\n"

		+ "\t/* Right label. */\n"
		+ "\tytick.anchor(\"right\").add(pv.Label)\n"
		+ "\t\t.visible(function() cell.index == experimentsLength - 1)\n"
		+ "\t\t.text(function(d, t) y_position[t.py].tickFormat(d));\n\n"

		+ "\t/* Labels along the diagonal. */\n"
		+ "\tcell.anchor(\"center\").add(pv.Label)\n"
		+ "\t\t.visible(function(t) t.px == t.py)\n"
		+ "\t\t.font(\"bold 14px sans-serif\")\n"
		+ "\t\t.text(function(t) \"Experiment: \" + experiments[mappedExperiments.indexOf(t.px)]);\n\n"

		+ "\t/* Add dots. */\n"
		+ "\tvar dot = plot.add(pv.Dot)\n"
		+ "\t\t.data(mappedProbeLists)\n"
		+ "\t\t.left(function(d, t) x_position[t.px](d[t.px]))\n"
		+ "\t\t.bottom(function(d, t) y_position[t.py](d[t.py]))\n"
		+ "\t\t.strokeStyle(null)\n"
		+ "\t\t.title(function(d, t) \"ProbeList: \" + d.probelist + \" || Probe: \" + d.name + \" || x-Axis Experiment \" + experiments[mappedExperiments.indexOf(t.px)] + \": \" + d[t.px] + \" || y-Axis Experiment \" + experiments[mappedExperiments.indexOf(t.py)] + \": \" + d[t.py])\n";

		if(this.scatterPlotExportSettings.isProbesTableInteraction()){
			result += "\t\t.event(\"mouseover\", function() this.parent.i(this.index))\n"
				+ "\t\t.event(\"mouseout\", function() this.parent.i(-1))\n"
				+ "\t\t.event(\"mousedown\", function(d){";
			if (this.scatterPlotExportSettings.isProbesTableInteraction())
				result += "makeEntry(\"probes\", pv.values(d).slice(4), d.color););";
			if (this.scatterPlotExportSettings.isMetaTableInteraction()) 
				result += "makeMetaTableEntry(\"metainfo\", d.name, d.meta);";
			result += "})\n";

			if(this.scatterPlotExportSettings.isShowSelectedProbes()){
				result += "\t\t.size(function(d) {if (this.parent.i() == this.index)\n"
					+ "\t\t\treturn d.selected ? 2*selectedDotSize : 2*dotSize;\n"
					+ "\t\telse\n"
					+ "\t\t\treturn d.selected ? selectedDotSize : dotSize;\n"
					+ "\t\t})\n"
					+ "\t\t.fillStyle(function(d) d.selected ? selectedProbesColor : pv.color(d.color).rgb().alpha(transparency));\n\n";
			}
			else {
				result += "\t\t.size(function() this.parent.i() == this.index ? 2*dotSize : dotSize)\n"
					+ "\t\t.fillStyle(function(d) pv.color(d.color).rgb().alpha(transparency));\n\n";
			}
		}
		else {
			if(this.scatterPlotExportSettings.isShowSelectedProbes()){
				result += "\t\t.size(function(d) d.selected ? selectedDotSize : dotSize)\n"
					+ "\t\t.fillStyle(function(d) d.selected ? selectedProbesColor : pv.color(d.color).rgb().alpha(transparency));\n\n";
			}
			else {
				result += "\t\t.size(dotSize)\n"
					+ "\t\t.fillStyle(function(d) pv.color(d.color).rgb().alpha(transparency));\n\n";
			}
		}
		if (this.scatterPlotExportSettings.isShowSelectedProbes() && this.scatterPlotExportSettings.isShowSelectedProbesLabels()){
			result += "\tdot.anchor(\"right\").add(pv.Label)\n"
				+ "\t\t.text(function(d) d.selected ? d.name : null)\n"
				+ "\t\t.font(\"10px sans-serif\");\n\n";
		}

		return result;
	}

	private String createSinglePlot() {
		String result = "\tvar selectedExperiments = [0, 1]\n\n";

		if (this.scatterPlotExportSettings.isSwitchExperimentsInteraction()){
			result += "\tfunction changeSelectedExperiments(exp1, exp2){\n"
				+ "\t\tvar x = experiments.indexOf(exp1);\n"
				+ "\t\tvar y = experiments.indexOf(exp2);\n"
				+ "\t\tselectedExperiments = [x, y];\n"
				+ "\t\tvis.render();\n"
				+ "\t}\n\n";
		}

		result += "\tfunction getHeight(){\n"
			+ "\t\treturn vis.height();\n"
			+ "\t}\n\n"

			+ "\tfunction getWidth(){\n"
			+ "\t\treturn vis.width();\n"
			+ "\t}\n\n"

			+ "\t/* Main panel. */\n"
			+ "\tvar vis = new pv.Panel()\n"
			+ "\t\t.width(function() width)\n"
			+ "\t\t.height(function() height)\n"
			+ "\t\t.left(80)\n"
			+ "\t\t.top(50)\n"
			+ "\t\t.right(80)\n"
			+ "\t\t.bottom(80)\n"
			+ "\t\t.strokeStyle(\"#000\")\n"
			+ "\t\t.antialias(false)\n"
			+ "\t\t.lineWidth(1)\n"
			+ "\t\t.def(\"i\", -1);\n\n"

			+ "\t/* Title x-Axis */\n"
			+ "\tvis.add(pv.Label)\n"
			+ "\t\t.bottom(-70)\n"
			+ "\t\t.left(function() width/2)\n"
			+ "\t\t.textAlign(\"center\")\n"
			+ "\t\t.font(\"12px sans-serif\")\n"
			+ "\t\t.text(function() \"Experiment: \" + experiments[selectedExperiments[0]]);\n\n"

			+ "\t/* Title y-Axis */\n"
			+ "\tvis.add(pv.Label)\n"
			+ "\t\t.left(-60)\n"
			+ "\t\t.top(function() height/2)\n"
			+ "\t\t.textAlign(\"center\")\n"
			+ "\t\t.textAngle(-Math.PI / 2)\n"
			+ "\t\t.font(\"12px sans-serif\")\n"
			+ "\t\t.text(function() \"Experiment: \" + experiments[selectedExperiments[1]]);\n\n"

			+ "\t/* X-axis ticks. */\n"
			+ "\tvar xtick = vis.add(pv.Rule)\n"
			+ "\t\t.data(function() x_position[mappedExperiments[selectedExperiments[0]]].ticks())\n"
			+ "\t\t.left(function(d) x_position[mappedExperiments[selectedExperiments[0]]](d))\n"
			+ "\t\t.strokeStyle(\"#eee\");\n\n"

			+ "\t/* Bottom label. */\n"
			+ "\txtick.anchor(\"bottom\").add(pv.Label)\n"
			+ "\t\t.text(function(d) x_position[mappedExperiments[selectedExperiments[0]]].tickFormat(d));\n\n"

			+ "\t/* Y-axis ticks. */\n"
			+ "\tvar ytick = vis.add(pv.Rule)\n"
			+ "\t\t.data(function() y_position[mappedExperiments[selectedExperiments[1]]].ticks())\n"
			+ "\t\t.bottom(function(d) y_position[mappedExperiments[selectedExperiments[1]]](d))\n"
			+ "\t\t.strokeStyle(\"#eee\");\n\n"

			+ "\t/* Left label. */\n"
			+ "\tytick.anchor(\"left\").add(pv.Label)\n"
			+ "\t\t.text(function(d) y_position[mappedExperiments[selectedExperiments[1]]].tickFormat(d));\n\n";

		if(!this.scatterPlotExportSettings.isShowSelectedProbes()){
			result += "\t/* Add dots. */\n"
				+ "\tvar dot = vis.add(pv.Dot)\n"
				+ "\t\t.data(mappedProbeLists)\n"
				+ "\t\t.left(function(d) x_position[mappedExperiments[selectedExperiments[0]]](d[mappedExperiments[selectedExperiments[0]]]))\n"
				+ "\t\t.bottom(function(d) y_position[mappedExperiments[selectedExperiments[1]]](d[mappedExperiments[selectedExperiments[1]]]))\n";

			if (this.scatterPlotExportSettings.isProbesTableInteraction())
				result += "\t\t.size(function() this.parent.i() == this.index ? 2*dotSize : dotSize)\n";
			else 
				result += "\t\t.size(dotSize)\n";

			result += "\t\t.strokeStyle(null)\n"
				+ "\t\t.fillStyle(function(d) pv.color(d.color).rgb().alpha(transparency))\n"
				+ "\t\t.title(function(d) \"ProbeList: \" + d.probelist + \" || Probe: \" + d.name + \" || x-Axis Experiment \" + experiments[mappedExperiments.indexOf(mappedExperiments[selectedExperiments[0]])] + \": \" + d[mappedExperiments[selectedExperiments[0]]] + \" || y-Axis Experiment \" + experiments[mappedExperiments.indexOf(mappedExperiments[selectedExperiments[1]])] + \": \" + d[mappedExperiments[selectedExperiments[1]]]);\n\n";
		}
		else { result += "\t/* Add dots. */\n"
			+ "\tvar dot = vis.add(pv.Dot)\n"
			+ "\t\t.data(mappedProbeLists)\n"
			+ "\t\t.left(function(d) x_position[mappedExperiments[selectedExperiments[0]]](d[mappedExperiments[selectedExperiments[0]]]))\n"
			+ "\t\t.bottom(function(d) y_position[mappedExperiments[selectedExperiments[1]]](d[mappedExperiments[selectedExperiments[1]]]))\n";

		if (this.scatterPlotExportSettings.isProbesTableInteraction()){
			result += "\t\t.size(function(d) {if (this.parent.i() == this.index)\n"
				+ "\t\t\treturn d.selected ? 2*selectedDotSize : 2*dotSize;\n"
				+ "\t\telse\n"
				+ "\t\t\treturn d.selected ? selectedDotSize : dotSize;\n"
				+ "\t\t})\n";
		}
		else {
			result += "\t\t.size(function(d) d.selected ? selectedDotSize : dotSize)\n";
		}

		result += "\t\t.strokeStyle(null)\n"
			+ "\t\t.fillStyle(function(d) d.selected ? selectedProbesColor : pv.color(d.color).rgb().alpha(transparency))\n"
			+ "\t\t.title(function(d) \"ProbeList: \" + d.probelist + \" || Probe: \" + d.name + \" || x-Axis Experiment \" + experiments[mappedExperiments.indexOf(mappedExperiments[selectedExperiments[0]])] + \": \" + d[mappedExperiments[selectedExperiments[0]]] + \" || y-Axis Experiment \" + experiments[mappedExperiments.indexOf(mappedExperiments[selectedExperiments[1]])] + \": \" + d[mappedExperiments[selectedExperiments[1]]]);\n\n";

		if (this.scatterPlotExportSettings.isShowSelectedProbesLabels()){
			result += "\tdot.anchor(\"right\").add(pv.Label)\n"
				+ "\t\t.text(function(d) d.selected ? d.name : null)\n"
				+ "\t\t.font(\"10px sans-serif\");\n\n";
		}
		}

		if (this.scatterPlotExportSettings.isProbesTableInteraction()){
			result += "\tdot.event(\"mouseover\", function() this.parent.i(this.index))\n"
				+ "\t\t.event(\"mouseout\", function() this.parent.i(-1))\n"
				+ "\t\t.event(\"mousedown\", function(d){";
			if (this.scatterPlotExportSettings.isProbesTableInteraction())
				result += "makeEntry(\"probes\", pv.values(d).slice(4), d.color);";
			if (this.scatterPlotExportSettings.isMetaTableInteraction()) 
				result += "makeMetaTableEntry(\"metainfo\", d.name, d.meta);";
			result += "});\n\n";
		}

		return result;
	}

	@Override
	public String getPlotOptions() {
		String result = "";
		if (this.scatterPlotExportSettings.isSwitchExperimentsInteraction() && !this.scatterPlotExportSettings.isShowPlotMatrix()){
			result += "<p>\n"

				+ "<form>\n"
				+ "Experiment x:&nbsp;\n"
				+ "<select id=\"exp1\" name=\"exp1\" size=\"2\">\n"
				+ "</select>\n\n"

				+ "Experiment y:&nbsp;\n"
				+ "<select id=\"exp2\" name=\"exp2\" size=\"2\">\n"
				+ "</select>\n\n"

				+ "<input type=\"button\" value=\"Ok\" onclick=\"scatterPlot.changeSelectedExperiments(this.form.exp1.value, this.form.exp2.value)\"/>\n"
				+ "</form>\n\n"

				+ "<script type=\"text/javascript+protovis\">\n\n"
				+ "function createDropDownMenu(node, selected){\n"
				+ "\tfor (var i = 0; i < experiments.length; i++){\n"
				+ "\t\tvar option = document.createElement(\"option\");\n"
				+ "\tif (i == selected){\n"
				+ "\t\tvar selected = document.createAttribute(\"selected\");\n"
				+ "\t\tselected.nodeValue = \"selected\";\n"
				+ "\t\toption.setAttributeNode(selected);\n"
				+ "\t}\n"
				+ "\t\tvar exp = document.createTextNode(experiments[i]);\n"
				+ "\t\toption.appendChild(exp);\n"
				+ "\t\tnode.appendChild(option);\n"
				+ "\t}\n"
				+ "}\n\n"

				+ "createDropDownMenu(document.getElementById(\"exp1\"), scatterPlot.selectedExperiments[0]);\n"
				+ "createDropDownMenu(document.getElementById(\"exp2\"), scatterPlot.selectedExperiments[1]);\n"
				+ "</script>\n"

				+ "</p>\n\n";
		}

		return result;
	}

	@Override
	public String getPlotDescription() {
		String d = this.scatterPlotExportSettings.getDescription();
		if (!d.isEmpty())
			return d;
		else
			return null;
	}
}
