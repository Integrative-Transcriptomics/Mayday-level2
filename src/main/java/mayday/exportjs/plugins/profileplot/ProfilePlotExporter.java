package mayday.exportjs.plugins.profileplot;

import mayday.exportjs.exporter.PlotExporter;

public class ProfilePlotExporter extends PlotExporter {

	ProfilePlotExporterSetting profilePlotExportSettings;

	public ProfilePlotExporter(ProfilePlotExporterSetting profilePlotExportSettings){
		super("profilePlot");
		this.profilePlotExportSettings = profilePlotExportSettings;
	}


	@Override
	public String getString() {
		String plotScript = "/* " + this.profilePlotExportSettings.getName() + " */\n\n"
		+ "function vis_profilePlot(){\n"
		+ "\tvar height = " + this.profilePlotExportSettings.getHeight()+ ";\n"
		+ "\tvar width = " + this.profilePlotExportSettings.getWidth() + ";\n"
		+ "\tvar probesLineWidth = " + this.profilePlotExportSettings.getProbesLineWidth() + ";\n\n"

		+ "\tvar selectedProbesLineWidth = " + this.profilePlotExportSettings.getSelectedProbesLineWidth() + ";\n"
		+ super.createSelectedProbesColorString("selectedProbesColor", this.profilePlotExportSettings.getSelectedProbesColor().getRed(), 
				this.profilePlotExportSettings.getSelectedProbesColor().getGreen(), this.profilePlotExportSettings.getSelectedProbesColor().getBlue())

				+ "var transparency = " + this.profilePlotExportSettings.getTransparency() + ";\n\n"		

				+ "\t/* Scaling*/\n"
				+ "\tvar x_position;\n"
				+ "\tvar y_position;\n\n"

				+ "\t/* Update Scales */\n"
				+ "\tfunction updateScales(h, w){\n"
				+ "\t\theight = h;\n"
				+ "\t\twidth = w;\n"
				+ "\t\tx_position = pv.Scale.linear(0, experimentsLength-1).range(0, width);\n"
				+ "\t\ty_position = pv.Scale.linear(globalMinVal,globalMaxVal).range(0, height);\n"
				+ "\t}\n\n"

				+ "\tupdateScales(height, width);\n\n";

		if (this.profilePlotExportSettings.isShowMultiplePlots()){
			plotScript += makeMultiplePlotScript();
		}
		else 
			plotScript += makeSinglePlotScript();

		plotScript += "\tvis.render();\n\n"
			+ "\treturn {plot: vis, updateScales: updateScales, height: getHeight, width: getWidth";

		if(this.profilePlotExportSettings.isMean()){
			plotScript += ", vis_meanLine: vis_meanLine";
		}
		if(this.profilePlotExportSettings.isMedian()){
			plotScript += ", vis_medianLine: vis_medianLine";
		}
		if(this.profilePlotExportSettings.isFirstQuartile()){
			plotScript += ", vis_firstQuartileLine: vis_firstQuartileLine";
		}
		if(this.profilePlotExportSettings.isThirdQuartile()){
			plotScript += ", vis_thirdQuartileLine: vis_thirdQuartileLine";
		}

		plotScript += "};\n}\n\n"
			+ "var " + variableName + " = vis_profilePlot();\n\n";

		return plotScript;
	}


	private String makeMultiplePlotScript(){
		String result = "\t/* ================================= Multiple Part - START ================================= */\n\n"

			+ "\tvar multiplePlotsPadding = " + this.profilePlotExportSettings.getMultiplePlotsPadding() + ";\n\n"

			+ "\tfunction getHeight(){\n"
			+ "\t\treturn ((vis.height() + multiplePlotsPadding) / probeListsLength) - multiplePlotsPadding;\n"
			+ "\t}\n\n"

			+ "\tfunction getWidth(){\n"
			+ "\t\treturn vis.width();\n"
			+ "\t}\n\n"

			+ "\t/* Main panel */\n"
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
			+ "\t\t.data(probeLists)\n"
			+ "\t\t.height(function() height)\n"
			+ "\t\t.top(function() this.index * (height + multiplePlotsPadding))\n"
			+ "\t\t.strokeStyle(\"#000\")\n"
			+ "\t\t.lineWidth(1)\n"
			+ "\t\t.antialias(false);\n\n"

			+ "\t/* Y-axis ticks to each probeListPanel. */\n"
			+ "\tvar yaxis = probeListPanel.add(pv.Rule)\n"
			+ "\t\t.data(function() y_position.ticks())\n"
			+ "\t\t.bottom(function(d) y_position(d))\n"
			+ "\t\t.strokeStyle(\"#eee\");\n\n"

			+ "\t/* Vertical tick labels to each probeListPanel */\n"
			+ "\tvar ylabel = yaxis.anchor(\"left\").add(pv.Label)\n"
			+ "\t\t.text(y_position.tickFormat);\n\n"

			+ "\t/* X-axis ticks to each probeListPanel */\n"
			+ "\tvar xaxis = probeListPanel.add(pv.Rule)\n"
			+ "\t\t.data(function() x_position.ticks(experimentsLength-1))\n"
			+ "\t\t.left(function(d) x_position(d))\n"
			+ "\t\t.strokeStyle(\"#eee\");\n\n"

			+ "\t/* Horizontal tick labels to each probeListPanel */\n"
			+ "\tvar xlabel = xaxis.anchor(\"bottom\").add(pv.Label)\n"
			+ "\t\t.text(function() experiments[this.index])\n"
			+ "\t\t.textAngle(-Math.PI / 2)\n"
			+ "\t\t.textAlign(\"right\")\n"
			+ "\t\t.textBaseline(\"middle\");\n\n"

			+ "\t/* ================================= Multiple Part - END ================================= */\n\n";

		if (this.profilePlotExportSettings.isShowSelectedProbes()){
			result += paintUnselectedProbes()
			+ paintSelectedProbes();

			if(this.profilePlotExportSettings.isShowSelectedProbesLabels()){
				result += addSelectedProbesLabels();	
			}
		}
		else {
			result += paintAllProbes();
		}

		if (this.profilePlotExportSettings.isMean()){
			result += "\t/* Mean Line */\n"
				+ "\tvar meanBool = false;\n\n"

				+ "\tfunction vis_meanLine(bool){\n"
				+ "\t\tmeanBool = bool;\n"
				+ "\t}\n\n"

				+ "\tprobeListPanel.add(pv.Line)\n"
				+ "\t\t.visible(function() meanBool)\n"
				+ "\t\t.data(function(d) d.means)\n"
				+ "\t\t.left(function() x_position(this.index))\n"
				+ "\t\t.bottom(function(d) y_position(d))\n"
				+ "\t\t.lineWidth(5)\n"
				+ "\t\t.strokeStyle(\"darkblue\")\n"
				+ "\tprobeListPanel.add(pv.Label)\n"
				+ "\t\t.visible(function() meanBool)\n"
				+ "\t\t.text(\"Mean\")\n"
				+ "\t\t.bottom(function(d) y_position(d.means[experimentsLength-1]))\n"
				+ "\t\t.right(0)\n"
				+ "\t\t.textBaseline(\"middle\");\n\n";
		}
		if (this.profilePlotExportSettings.isMedian()){
			result += "\t/* Median Line */\n"
				+ "\tvar medianBool = false;\n\n"

				+ "\tfunction vis_medianLine(bool){\n"
				+ "\t\tmedianBool = bool;\n"
				+ "\t}\n\n"

				+ "\tprobeListPanel.add(pv.Line)\n"
				+ "\t\t.visible(function() medianBool)\n"
				+ "\t\t.data(function(d) d.medians)\n"
				+ "\t\t.left(function() x_position(this.index))\n"
				+ "\t\t.bottom(function(d) y_position(d))\n"
				+ "\t\t.lineWidth(5)\n"
				+ "\t\t.strokeStyle(\"darkblue\")\n"
				+ "\tprobeListPanel.add(pv.Label)\n"
				+ "\t\t.visible(function() medianBool)\n"
				+ "\t\t.text(\"Median\")\n"
				+ "\t\t.bottom(function(d) y_position(d.medians[experimentsLength-1]))\n"
				+ "\t\t.right(0)\n"
				+ "\t\t.textBaseline(\"middle\");\n\n";
		}
		if (this.profilePlotExportSettings.isFirstQuartile()){
			result += "\t/* First Quartile Line */\n"
				+ "\tvar firstQuartileBool = false;\n\n"

				+ "\tfunction vis_firstQuartileLine(bool){\n"
				+ "\t\tfirstQuartileBool = bool;\n"
				+ "\t}\n\n"

				+ "\tprobeListPanel.add(pv.Line)\n"
				+ "\t\t.visible(function() firstQuartileBool)\n"
				+ "\t\t.data(function(d) d.firstQuartiles)\n"
				+ "\t\t.left(function() x_position(this.index))\n"
				+ "\t\t.bottom(function(d) y_position(d))\n"
				+ "\t\t.lineWidth(5)\n"
				+ "\t\t.strokeStyle(\"darkblue\")\n"
				+ "\tprobeListPanel.add(pv.Label)\n"
				+ "\t\t.visible(function() firstQuartileBool)\n"
				+ "\t\t.text(\"First Quartile\")\n"
				+ "\t\t.bottom(function(d) y_position(d.firstQuartiles[experimentsLength-1]))\n"
				+ "\t\t.right(0)\n"
				+ "\t\t.textBaseline(\"middle\");\n\n";
		}

		if (this.profilePlotExportSettings.isThirdQuartile()){
			result += "\t/* Third Quartile Line */\n"
				+ "\tvar thirdQuartileBool = false;\n\n"

				+ "\tfunction vis_thirdQuartileLine(bool){\n"
				+ "\t\tthirdQuartileBool = bool;\n"
				+ "\t}\n\n"

				+ "\tprobeListPanel.add(pv.Line)\n"
				+ "\t\t.visible(function() thirdQuartileBool)\n"
				+ "\t\t.data(function(d) d.thirdQuartiles)\n"
				+ "\t\t.left(function() x_position(this.index))\n"
				+ "\t\t.bottom(function(d) y_position(d))\n"
				+ "\t\t.lineWidth(5)\n"
				+ "\t\t.strokeStyle(\"darkblue\")\n"
				+ "\tprobeListPanel.add(pv.Label)\n"
				+ "\t\t.visible(function() thirdQuartileBool)\n"
				+ "\t\t.text(\"Third Quartile\")\n"
				+ "\t\t.bottom(function(d) y_position(d.thirdQuartiles[experimentsLength-1]))\n"
				+ "\t\t.right(0)\n"
				+ "\t\t.textBaseline(\"middle\");\n\n";
		}

		return result;
	}

	private String makeSinglePlotScript(){
		String result = "\t/* ================================= Single Part - START ================================= */\n\n"

			+ "\tfunction getHeight(){\n"
			+ "\t\treturn vis.height();\n"
			+ "\t}\n\n"

			+ "\tfunction getWidth(){\n"
			+ "\t\treturn vis.width();\n"
			+ "\t}\n\n"

			+ "\t/* Main Panel */\n"
			+ "\tvar vis = new pv.Panel()\n"
			+ "\t\t.width(function() width)\n"
			+ "\t\t.height(function() height)\n"
			+ "\t\t.bottom(80)\n"
			+ "\t\t.left(80)\n"
			+ "\t\t.right(80)\n"
			+ "\t\t.top(5);\n\n"

			+ "\t/* Y-axis ticks. */\n"
			+ "\tvar yaxis = vis.add(pv.Rule)\n"
			+ "\t\t.data(function() y_position.ticks())\n"
			+ "\t\t.bottom(function(d) y_position(d))\n"
			+ "\t\t.strokeStyle(\"#eee\");\n\n"

			+ "\t/* Vertical tick labels */\n"
			+ "\tvar ylabel = yaxis.anchor(\"left\").add(pv.Label)\n"
			+ "\t\t.text(y_position.tickFormat);\n\n"

			+ "\t/* X-axis ticks. */\n"
			+ "\tvar xaxis = vis.add(pv.Rule)\n"
			+ "\t\t.data(function() x_position.ticks(experimentsLength-1))\n"
			+ "\t\t.left(function(d) x_position(d))\n"
			+ "\t\t.strokeStyle(\"#eee\");\n\n"

			+ "\t/* Horizontal tick labels */\n"
			+ "\tvar xlabel = xaxis.anchor(\"bottom\").add(pv.Label)\n"
			+ "\t\t.text(function() experiments[this.index])\n"
			+ "\t\t.textAngle(-Math.PI / 2)\n"
			+ "\t\t.textAlign(\"right\")\n"
			+ "\t\t.textBaseline(\"middle\");\n\n"

			+ "\t/* Title X-Axis */\n"
			+ "\tvis.add(pv.Label)\n"
			+ "\t\t.left(function() width/2)\n"
			+ "\t\t.bottom(-80)\n"
			+ "\t\t.textAlign(\"center\")\n"
			+ "\t\t.font(\"12px sans-serif\")\n"
			+ "\t\t.text(\"Experiment\");\n\n"

			+ "\t/* Title y-Axis */\n"
			+ "\tvis.add(pv.Label)\n"
			+ "\t\t.top(function() height/2)\n"
			+ "\t\t.left(-60)\n"
			+ "\t\t.textAngle(-Math.PI / 2)\n"
			+ "\t\t.textAlign(\"center\")\n"
			+ "\t\t.font(\"12px sans-serif\")\n"
			+ "\t\t.text(\"Expression Value\");\n\n"

			+ "\t/* Panel probeList */\n"
			+ "\tvar probeListPanel = vis.add(pv.Panel)\n"
			+ "\t\t.data(probeLists)\n"
			+ "\t.antialias(false);\n\n"

			+ "\t/* ================================= Single Part - END ================================= */\n\n";

		if (this.profilePlotExportSettings.isShowSelectedProbes()){
			result += paintUnselectedProbes()
			+ paintSelectedProbes();

			if(this.profilePlotExportSettings.isShowSelectedProbesLabels()){
				result += addSelectedProbesLabels();	
			}
		}
		else {
			result += paintAllProbes();
		}

		// Special Lines
		if(this.profilePlotExportSettings.isMean() || this.profilePlotExportSettings.isMedian() || this.profilePlotExportSettings.isFirstQuartile() || this.profilePlotExportSettings.isThirdQuartile()){
			result += "\t/* Paint Special Lines */\n"
				+ "\tfunction createSpecialData(){\n"
				+ "\t\tvar map = {means: [], medians: [], firstQuartiles: [], thirdQuartiles: []};\n"
				+ "\t\tif (probeLists.length > 1){\n"
				+ "\t\t\tfor (var i=0; i<experimentsLength; i++){\n"
				+ "\t\t\t\ttemp = new Array();\n"
				+ "\t\t\t\tfor (var j=0; j<probeListsLength; j++){\n"
				+ "\t\t\t\t\tfor(var k=0; k<probeLists[j].probes.length; k++){\n"
				+ "\t\t\t\t\t\ttemp = temp.concat(probeLists[j].probes[k].values[i]);\n"
				+ "\t\t\t\t\t}\n"
				+ "\t\t\t\t}\n"
				+ "\t\t\t\tmap.means = map.means.concat(pv.mean(temp));\n"
				+ "\t\t\t\tmap.medians = map.medians.concat(pv.median(temp));\n"
				+ "\t\t\t\tmap.firstQuartiles = map.firstQuartiles.concat(quantiles(0.25, temp));\n"
				+ "\t\t\t\tmap.thirdQuartiles = map.thirdQuartiles.concat(quantiles(0.75, temp));\n"
				+ "\t\t\t}\n"
				+ "\t\t}\n"
				+ "\t\telse {\n"
				+ "\t\t\tmap.means = probeLists[0].means;\n"
				+ "\t\t\tmap.medians = probeLists[0].medians;\n"
				+ "\t\t\tmap.firstQuartiles = probeLists[0].firstQuartiles;\n"
				+ "\t\t\tmap.thirdQuartiles = probeLists[0].thirdQuartiles;\n"
				+ "\t\t}\n"
				+ "\t\treturn map;\n"
				+ "\t}\n\n"

				+ "\t/* Compute quantiles. */\n"
				+ "\tfunction quantiles(n, array) {\n"
				+ "\t\tarray = array.sort(function(a, b) a - b);\n"
				+ "\t\tvar result = array[Math.round(n*(array.length+1))-1];\n"
				+ "\t\treturn result;\n"
				+ "\t}\n\n"

				+ "\tvar specialData = createSpecialData();\n\n";
		}
		if (this.profilePlotExportSettings.isMean()){
			result += "\t/* Mean Line */\n"
				+ "\tvar meanBool = false;\n\n"

				+ "\tfunction vis_meanLine(bool){\n"
				+ "\t\tmeanBool = bool;\n"
				+ "\t}\n\n"

				+ "\tvis.add(pv.Line)\n"
				+ "\t\t.visible(function() meanBool)\n"
				+ "\t\t.data(function() specialData.means)\n"
				+ "\t\t.left(function() x_position(this.index))\n"
				+ "\t\t.bottom(function(d) y_position(d))\n"
				+ "\t\t.lineWidth(5)\n"
				+ "\t\t.strokeStyle(\"darkblue\")\n"
				+ "\tvis.add(pv.Label)\n"
				+ "\t\t.visible(function() meanBool)\n"
				+ "\t\t.text(\"Mean\")\n"
				+ "\t\t.bottom(function() y_position(specialData.means[experimentsLength-1]))\n"
				+ "\t\t.right(0)\n"
				+ "\t\t.textBaseline(\"middle\");\n\n";
		}
		if (this.profilePlotExportSettings.isMedian()){
			result += "\t/* Median Line */\n"
				+ "\tvar medianBool = false;\n\n"

				+ "\tfunction vis_medianLine(bool){\n"
				+ "\t\tmedianBool = bool;\n"
				+ "\t}\n\n"

				+ "\tvis.add(pv.Line)\n"
				+ "\t\t.visible(function() medianBool)\n"
				+ "\t\t.data(function() specialData.medians)\n"
				+ "\t\t.left(function() x_position(this.index))\n"
				+ "\t\t.bottom(function(d) y_position(d))\n"
				+ "\t\t.lineWidth(5)\n"
				+ "\t\t.strokeStyle(\"darkblue\")\n"
				+ "\tvis.add(pv.Label)\n"
				+ "\t\t.visible(function() medianBool)\n"
				+ "\t\t.text(\"Median\")\n"
				+ "\t\t.bottom(function() y_position(specialData.medians[experimentsLength-1]))\n"
				+ "\t\t.right(0)\n"
				+ "\t\t.textBaseline(\"middle\");\n\n";
		}
		if (this.profilePlotExportSettings.isFirstQuartile()){
			result += "\t/* First Quartile Line */\n"
				+ "\tvar firstQuartileBool = false;\n\n"

				+ "\tfunction vis_firstQuartileLine(bool){\n"
				+ "\t\tfirstQuartileBool = bool;\n"
				+ "\t}\n\n"

				+ "\tvis.add(pv.Line)\n"
				+ "\t\t.visible(function() firstQuartileBool)\n"
				+ "\t\t.data(function() specialData.firstQuartiles)\n"
				+ "\t\t.left(function() x_position(this.index))\n"
				+ "\t\t.bottom(function(d) y_position(d))\n"
				+ "\t\t.lineWidth(5)\n"
				+ "\t\t.strokeStyle(\"darkblue\")\n"
				+ "\tvis.add(pv.Label)\n"
				+ "\t\t.visible(function() firstQuartileBool)\n"
				+ "\t\t.text(\"First Quartile\")\n"
				+ "\t\t.bottom(function() y_position(specialData.firstQuartiles[experimentsLength-1]))\n"
				+ "\t\t.right(0)\n"
				+ "\t\t.textBaseline(\"middle\");\n\n";
		}
		if (this.profilePlotExportSettings.isThirdQuartile()){
			result += "\t/* Third Quartile Line */\n"
				+ "\tvar thirdQuartileBool = false;\n\n"

				+ "\tfunction vis_thirdQuartileLine(bool){\n"
				+ "\t\tthirdQuartileBool = bool;\n"
				+ "\t}\n\n"

				+ "\tvis.add(pv.Line)\n"
				+ "\t\t.visible(function() thirdQuartileBool)\n"
				+ "\t\t.data(function() specialData.thirdQuartiles)\n"
				+ "\t\t.left(function() x_position(this.index))\n"
				+ "\t\t.bottom(function(d) y_position(d))\n"
				+ "\t\t.lineWidth(5)\n"
				+ "\t\t.strokeStyle(\"darkblue\")\n"
				+ "\tvis.add(pv.Label)\n"
				+ "\t\t.visible(function() thirdQuartileBool)\n"
				+ "\t\t.text(\"Third Quartile\")\n"
				+ "\t\t.bottom(function() y_position(specialData.thirdQuartiles[experimentsLength-1]))\n"
				+ "\t\t.right(0)\n"
				+ "\t\t.textBaseline(\"middle\");\n\n";
		}

		result += "\t/* Bottom border line */\n"
			+ "\tvis.add(pv.Rule)\n"
			+ "\t\t.strokeStyle(\"#000\")\n"
			+ "\t\t.bottom(0);\n\n"

			+ "\t/* Left border line */\n"
			+ "\tvis.add(pv.Rule)\n"
			+ "\t\t.strokeStyle(\"#000\")\n"
			+ "\t\t.left(0);\n\n";

		return result;
	}

	private String paintAllProbes(){
		String result;
		if (this.profilePlotExportSettings.isProbesTableInteraction() || this.profilePlotExportSettings.isMetaTableInteraction()){

			result = "\t/* Panel for each probe */\n"
				+ "\tvar probesPanel = probeListPanel.add(pv.Panel)\n"
				+ "\t\t.data(function(d) d.probes);\n\n"

				+ "\t/* Panel for interaction */\n"
				+ "\t\tvar interactionPanel = probesPanel.add(pv.Panel)\n"
				+ "\t\t.def(\"active\", false);\n\n"

				+ "\t/* Paint all probes */\n"
				+ "\tvar line = interactionPanel.add(pv.Line)\n"
				+ "\t\t.data(function(d) d.values)\n"
				+ "\t\t.bottom(function(d) y_position(d))\n"
				+ "\t\t.left(function() x_position(this.index))\n"
				+ "\t\t.lineWidth(function() this.parent.active() ? 2*probesLineWidth : probesLineWidth)\n"
				+ "\t\t.strokeStyle(function(d, f) pv.color(f.color).rgb().alpha(transparency))\n"
				+ "\t\t.title(function(d, f) \"Name: \" + f.name + \" || Min. Value: \" + pv.min(f.values) + \" || Max. Value: \" + pv.max(f.values))\n"
				+ "\t\t.event(\"mouseover\", function() this.parent.active(true))\n"
				+ "\t\t.event(\"mouseout\", function() this.parent.active(false))\n"
				+ "\t\t.event(\"mousedown\", function(d, f){";
			if (this.profilePlotExportSettings.isProbesTableInteraction())
				result += "makeEntry(\"probes\", new Array(f.name).concat(f.values), f.color);";
			if (this.profilePlotExportSettings.isMetaTableInteraction()) 
				result += "makeMetaTableEntry(\"metainfo\", f.name, f.meta);";
			result += "});\n\n";
		}
		else {

			result = "\t/* Panel for each probe */\n"
				+ "\tvar probesPanel = probeListPanel.add(pv.Panel)\n"
				+ "\t\t.data(function(d) d.probes);\n\n"

				+ "\t/* Paint all probes */\n"
				+ "\tvar line = probesPanel.add(pv.Line)\n"
				+ "\t\t.data(function(d) d.values)\n"
				+ "\t\t.bottom(function(d) y_position(d))\n"
				+ "\t\t.left(function() x_position(this.index))\n"
				+ "\t\t.lineWidth(probesLineWidth)\n"
				+ "\t\t.strokeStyle(function(d, f) pv.color(f.color).rgb().alpha(transparency)\n"
				+ "\t\t.title(function(d, f) \"Name: \" + f.name + \" || Min. Value: \" + pv.min(f.values) + \" || Max. Value: \" + pv.max(f.values));\n\n";
		}

		return result;
	}


	private String paintUnselectedProbes(){
		String result;

		if (this.profilePlotExportSettings.isProbesTableInteraction() || this.profilePlotExportSettings.isMetaTableInteraction()){

			result = "\t/* Panel for each probe */\n"
				+ "\tvar unselectedProbesPanel = probeListPanel.add(pv.Panel)\n"
				+ "\t\t.data(function(d) d.probes);\n\n"

				+ "\t/* Panel for interaction */\n"
				+ "\t\tvar unselectedInteractionPanel = unselectedProbesPanel.add(pv.Panel)\n"
				+ "\t\t.def(\"active\", false);\n\n"

				+ "\t/* Paint unselected Probes */\n"
				+ "\tvar line = unselectedInteractionPanel.add(pv.Line)\n"
				+ "\t\t.data(function(d) (d.selected == false) ? d.values : [])\n"
				+ "\t\t.bottom(function(d) y_position(d))\n"
				+ "\t\t.left(function() x_position(this.index))\n"
				+ "\t\t.lineWidth(function() this.parent.active() ? 2*probesLineWidth : probesLineWidth)\n"
				+ "\t\t.strokeStyle(function(d,f) pv.color(f.color).rgb().alpha(transparency))\n"
				+ "\t\t.title(function(d, f) \"Name: \" + f.name + \" || Min. Value: \" + pv.min(f.values) + \" || Max. Value: \" + pv.max(f.values))\n"
				+ "\t\t.event(\"mouseover\", function() this.parent.active(true))\n"
				+ "\t\t.event(\"mouseout\", function() this.parent.active(false))\n"
				+ "\t\t.event(\"mousedown\", function(d, f){";
			if (this.profilePlotExportSettings.isProbesTableInteraction())
				result += "makeEntry(\"probes\", new Array(f.name).concat(f.values), f.color);";
			if (this.profilePlotExportSettings.isMetaTableInteraction()) 
				result += "makeMetaTableEntry(\"metainfo\", f.name, f.meta);";
			result += "});\n\n";
		}

		else { 

			result = "\t/* Panel for each probe */\n"
				+ "\tvar unselectedProbesPanel = probeListPanel.add(pv.Panel)\n"
				+ "\t\t.data(function(d) d.probes);\n\n"

				+ "\t/* Paint unselected Probes */\n"
				+ "\tvar line = unselectedProbesPanel.add(pv.Line)\n"
				+ "\t\t.data(function(d) (d.selected == false) ? d.values : [])\n"
				+ "\t\t.bottom(function(d) y_position(d))\n"
				+ "\t\t.left(function() x_position(this.index))\n"
				+ "\t\t.lineWidth(probesLineWidth)\n"
				+ "\t\t.strokeStyle(function(d, f) pv.color(f.color).rgb().alpha(transparency))\n"
				+ "\t\t.title(function(d, f) \"Name: \" + f.name + \" || Min. Value: \" + pv.min(f.values) + \" || Max. Value: \" + pv.max(f.values));\n\n";
		}
		return result;
	}

	private String paintSelectedProbes(){
		String result;

		if (this.profilePlotExportSettings.isProbesTableInteraction() || this.profilePlotExportSettings.isMetaTableInteraction()){

			result = "\t/* Panel for each probe */\n"
				+ "\tvar selectedProbesPanel = probeListPanel.add(pv.Panel)\n"
				+ "\t\t.data(function(d) d.probes);\n\n"

				+ "\t/* Panel for interaction */\n"
				+ "\tvar selectedInteractionPanel = selectedProbesPanel.add(pv.Panel)\n"
				+ "\t\t.def(\"active\", false);\n\n"

				+ "\t/* Paint selected Probes */\n"
				+ "\tvar line = selectedInteractionPanel.add(pv.Line)\n"
				+ "\t\t.data(function(d) (d.selected == true) ? d.values : [])\n"
				+ "\t\t.bottom(function(d) y_position(d))\n"
				+ "\t\t.left(function() x_position(this.index))\n"
				+ "\t\t.lineWidth(function() this.parent.active() ? 2*selectedProbesLineWidth : selectedProbesLineWidth)\n"
				+ "\t\t.strokeStyle(selectedProbesColor)\n"
				+ "\t\t.title(function(d, f) \"Name: \" + f.name + \" || Min. Value: \" + pv.min(f.values) + \" || Max. Value: \" + pv.max(f.values))\n"
				+ "\t\t.event(\"mouseover\", function() this.parent.active(true))\n"
				+ "\t\t.event(\"mouseout\", function() this.parent.active(false))\n"
				+ "\t\t.event(\"mousedown\", function(d, f){";
			if (this.profilePlotExportSettings.isProbesTableInteraction())
				result += "makeEntry(\"probes\", new Array(f.name).concat(f.values), f.color);";
			if (this.profilePlotExportSettings.isMetaTableInteraction()) 
				result += "makeMetaTableEntry(\"metainfo\", f.name, f.meta);";
			result += "});\n\n";
		}
		else {

			result = "\t/* Panel for each probe */\n"
				+ "\tvar selectedProbesPanel = probeListPanel.add(pv.Panel)\n"
				+ "\t\t.data(function(d) d.probes);\n\n"

				+ "\t/* Paint selected Probes */\n"
				+ "\tvar line = selectedProbesPanel.add(pv.Line)\n"
				+ "\t\t.data(function(d) (d.selected == true) ? d.values : [])\n"
				+ "\t\t.bottom(function(d) y_position(d))\n"
				+ "\t\t.left(function() x_position(this.index))\n"
				+ "\t\t.lineWidth(selectedProbesLineWidth)\n"
				+ "\t\t.strokeStyle(selectedProbesColor)\n"
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
	public String getName() {
		return this.profilePlotExportSettings.getName();
	}

	@Override
	public String getPlotOptions() {
		if(this.profilePlotExportSettings.isMean() || this.profilePlotExportSettings.isMedian() || this.profilePlotExportSettings.isFirstQuartile() || this.profilePlotExportSettings.isThirdQuartile()){
			String result = "";
			result += "<p>\n";

			if(this.profilePlotExportSettings.isMean()){
				result += "\t<span><input type=\"checkbox\" onclick=\"vis_specialLine(this, profilePlot.plot, profilePlot.vis_meanLine)\"/>Mean</span>\n";
			}
			if(this.profilePlotExportSettings.isMedian()){
				result += "\t<span><input type=\"checkbox\" onclick=\"vis_specialLine(this, profilePlot.plot, profilePlot.vis_medianLine)\"/>Median</span>\n";
			}
			if(this.profilePlotExportSettings.isFirstQuartile()){
				result += "\t<span><input type=\"checkbox\" onclick=\"vis_specialLine(this, profilePlot.plot, profilePlot.vis_firstQuartileLine)\"/>First Quartile</span>\n";
			}
			if(this.profilePlotExportSettings.isThirdQuartile()){
				result += "\t<span><input type=\"checkbox\" onclick=\"vis_specialLine(this, profilePlot.plot, profilePlot.vis_thirdQuartileLine)\"/>Third Quartile</span>\n";
			}

			result += "</p>\n\n"

				+ "<script type=\"text/javascript+protovis\">\n\n"
				+ "function vis_specialLine(checkBox, vis, line){\n"
				+ "\tif (checkBox.checked){\n"
				+ "\t\tline(true);\n"
				+ "\t\tvis.render();\n"
				+ "\t}\n"
				+ "\telse\n"
				+ "\t\tline(false);\n"
				+ "\t\tvis.render();\n"
				+ "\t}\n\n"
				+ "</script>\n";

			return result;
		}
		else return null;
	}

	@Override
	public String getPlotDescription() {
		String d = this.profilePlotExportSettings.getDescription();
		if (!d.isEmpty())
			return d;
		else
			return null;
	}
}
