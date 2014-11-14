package mayday.exportjs.exporter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HTMLExporter {

	private HTMLExporterSetting htmlExportSettings;
	private List<PlotExporter> plotExporters;

	private DataExporter dataExporter;
	private String protLib;

	private List<String> plotIds;
	private List<String> commentBoxIds;
	private List<String> descriptionIds;
	private List<String> visIds;

	private String probesTableName;
	private String metaInfoTableName;


	public HTMLExporter(HTMLExporterSetting htmlExportSettings, List<PlotExporter> plotExporters){
		this.htmlExportSettings = htmlExportSettings;
		this.plotExporters = plotExporters;

		this.dataExporter = null;
		this.protLib = null;

		this.plotIds = new ArrayList<String>();
		this.commentBoxIds = new ArrayList<String>();
		this.descriptionIds = new ArrayList<String>();
		this.visIds = new ArrayList<String>();

		// Fill lists with ids
		for (int i = 0; i < this.plotExporters.size(); i++){
			String variableName = this.plotExporters.get(i).getVariableName();
			this.plotIds.add(variableName);
			this.commentBoxIds.add(variableName + "CommentBox");
			this.descriptionIds.add(variableName + "Description");
			this.visIds.add(variableName + "Vis");
		}

		this.probesTableName = "probes";
		this.metaInfoTableName = "metainfo";
	}

	
	public String getString() {
		String html = "";

		if (this.htmlExportSettings.isCommentBoxInteraction()){
			html += "<?php echo \"<?xml version=\\\"1.0\\\" encoding=\\\"ISO-8859-1\\\" ?>\\n\"; ?>\n";
		}
		else {
			html += "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n";
		}
		html += "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"xhtml1-transitional.dtd\">\n"
			+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
			+ makeHeader()
			+ makeBody()
			+ "</html>";

		return html;
	}

	private String makeHeader(){
		String result = "<head>\n"
			+ "<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\" />\n"
			+ "<title>" + this.htmlExportSettings.getTitle() + "</title>\n"
			+ "<link rel=\"icon\" type=\"image/png\" href=\"" + this.htmlExportSettings.getFavicon() + "\" />\n";

		if (this.htmlExportSettings.isCommentBoxInteraction()){
			result += "<?php include('commentBox_functions.php') ?>\n";
		}

		// External Js (Mayday Data, Protovis Library)
		Iterator<String> it = this.htmlExportSettings.getExternalJsFiles().iterator();
		while(it.hasNext()){
			result += makeExternalJs(it.next());
		}

		// Internal Js
		result += "<script type=\"text/javascript\">\n\n"
			+ makeInternalData()
			+ makeInternalProtLib()
			+ makeGeneralFunctions()
			+ makeInteractionFunctions()
			+ makeInfoFunctions()
			+ "</script>\n";

		// Internal Css
		result += makeInternalCss();

		result += "</head>\n\n";
		return result;
	}

	private String makeCommentFunctions() {
		String result = "// ======== Comment Functions ========\n\n"

			+ "function checkEntries(form){\n"
			+ "\tvar result = true;\n"
			+ "\tif (!(form.name.value && form.name.value.length > 0 && form.comm.value && form.comm.value.length > 0)){\n"
			+ "\t\tresult = false;\n"
			+ "\t\talert(\"Please enter name and/or a comment.\");\n"
			+ "\t}\n"
			+ "\treturn result;\n"
			+ "}\n";

		return result;
	}

	private String makeInfoFunctions() {
		String result = "// ======== Info Functions ========\n\n"

			+ "function numberOfExperiments(){\n"
			+ "\tdocument.write(\"# Experiments: \" + experimentsLength + \"<br/>\");\n"
			+ "}\n\n"

			+ "function globalVals(){\n"
			+ "\tdocument.write(\"Global Min. Value: \" + globalMinVal + \"<br/>\");\n"
			+ "\tdocument.write(\"Global Max. Value: \" + globalMaxVal + \"<br/>\");\n"
			+ "}\n\n"

			+ "function numberOfProbeLists(){\n"
			+ "\tdocument.write(\"# ProbeLists: \" + probeListsLength + \"<br/>\");\n"
			+ "}\n\n"

			+ "function numberOfProbes(){\n"
			+ "\tvar n = 0;\n"
			+ "\tfor (var i = 0; i < probeListsLength; i++){\n"
			+ "\t\tn += probeLists[i].probes.length;\n"
			+ "\t}\n"
			+ "\tdocument.write(\"# Probes: \" + n + \"<br/>\");\n"
			+ "}\n\n"

			+ "function probeListsInformation(){\n"
			+ "\tvar n = 1;\n"
			+ "\tfor (var i = 0; i < probeListsLength; i++){\n"
			+ "\t\tdocument.write(\"ProbeList \" + n + \": \" + probeLists[i].name + \", \" + probeLists[i].probes.length + \" Probes<br/>\");\n"
			+ "\t\tn++;\n"
			+ "\t}\n"
			+ "}\n\n";

		return result;
	}

	private String makeInteractionFunctions(){
		String result = "";
		if(this.htmlExportSettings.isProbesTableInteraction() || this.htmlExportSettings.isMetaTableInteraction()){
			result += makeTableInteractionFunctions();
		}
		if(this.htmlExportSettings.isMoveInteraction()){
			result += makeDragAndDropJsFunctions();
		}
		if(this.htmlExportSettings.isResizeInteraction()){
			result += makeResizeInteraction();
		}
		if(this.htmlExportSettings.isCommentBoxInteraction()){
			result += makeCommentFunctions();
		}
		if(this.htmlExportSettings.isArrangementInteraction()){
			result += makeArrangementFunctions();
		}
		return result;
	}

	private String makeArrangementFunctions() {
		return "// ======== Arrangement Functions ========\n\n"

		+ "var objectIds = new Array();\n\n"

		+ "function addObjectId(id){\n"
		+ "\t(id) ? objectIds.push(id) : null;\n"
		+ "}\n\n"

		+ "function deleteObjectId(id){\n"
		+ "\tif (id) {\n"
		+ "\t\tfor (var i = 0; i < objectIds.length; i++){\n"
		+ "\t\t\tif (id == objectIds[i]){\n"
		+ "\t\t\t\tobjectIds.splice(i,1);\n"
		+ "\t\t\t\ti = objectIds.length;\n"
		+ "\t\t\t}\n"
		+ "\t\t}\n"
		+ "\t}\n"
		+ "}\n\n"


		+ "function moveUp(id){\n"
		+ "\tif (id && objectIds.length > 1) {\n"
		+ "\t\tfor (var i = 0; i < objectIds.length; i++){\n"
		+ "\t\t\tif (id == objectIds[i]){\n"
		+ "\t\t\t\tif (i > 0){\n"
		+ "\t\t\t\t\tvar ob = objectIds[i-1];\n"
		+ "\t\t\t\t\tobjectIds.splice(i-1,1);\n"
		+ "\t\t\t\t\tobjectIds.splice(i, 0, ob);\n"
		+ "\t\t\t\t\ti = objectIds.length;\n"
		+ "\t\t\t\t\tarrangeObjects();\n"
		+ "\t\t\t\t}\n"
		+ "\t\t\t}\n"
		+ "\t\t}\n"
		+ "\t}\n"
		+ "}\n\n"

		+ "function moveDown(id){\n"
		+ "\tif (id && objectIds.length > 1) {\n"
		+ "\t\tfor (var i = 0; i < objectIds.length; i++){\n"
		+ "\t\t\tif (id == objectIds[i]){\n"
		+ "\t\t\t\tif (i < objectIds.length-1){\n"
		+ "\t\t\t\t\tvar ob = objectIds[i+1];\n"
		+ "\t\t\t\t\tobjectIds.splice(i+1,1);\n"
		+ "\t\t\t\t\tobjectIds.splice(i, 0, ob);\n"
		+ "\t\t\t\t\ti = objectIds.length;\n"
		+ "\t\t\t\t\tarrangeObjects();\n"
		+ "\t\t\t\t}\n"
		+ "\t\t\t}\n"
		+ "\t\t}\n"
		+ "\t}\n"
		+ "}\n\n"

		+ "function arrangeObjects(){\n"
		+ "\tvar maxTop = 150;\n"
		+ "\tvar padding = 50;\n"
		+ "\tfor (var i=0; i < objectIds.length; i++){\n"
		+ "\t\tvar object = document.getElementById(objectIds[i]);\n"	
		+ "\t\tvar height = object.offsetHeight;\n"
		+ "\t\tvar width = object.offsetWidth;\n"
		+ "\t\tvar windowWidth = window.innerWidth;\n"
		+ "\t\tvar windowHeight = window.innerHeight;\n"
		+ "\t\tvar new_posX = windowWidth/2 - object.offsetWidth/2;\n"
		+ "\t\tvar new_posY = windowHeight/2 - object.offsetHeight/2;\n"	
		+ "\t\tif (document.getElementById(\"arrangeV\").checked){\n"
		+ "\t\t\tobject.style.left = Math.max(new_posX, 0) + \"px\";\n"
		+ "\t\t\tobject.style.top = (objectIds.length == 1) ? Math.max(new_posY, 150) + \"px\" : maxTop + \"px\";\n"	
		+ "\t\t\tmaxTop += height + padding;\n"
		+ "\t\t}\n"
		+ "\t\telse if (document.getElementById(\"arrangeH\").checked){\n"
		+ "\t\t\tobject.style.left = (objectIds.length == 1) ? Math.max(new_posX, 0) + \"px\" : maxTop + \"px\";\n"
		+ "\t\t\tobject.style.top = Math.max(new_posY, 150) + \"px\";\n"
		+ "\t\t\tmaxTop += width + padding;\n"
		+ "\t\t}\n"
		+ "\t}\n"
		+ "}\n\n"

		+ "function autoArrange(){\n"
		+ "\tif(document.getElementById('autoArranging') && document.getElementById('autoArranging').checked){\n"
		+ "\t\tarrangeObjects();\n"
		+ "\t}\n"
		+ "}\n\n";
	}

	private String makeResizeInteraction(){
		String result = "// ======== Resize Functions ========\n\n"

			+ "function zoomIn(vis, updateScales, getHeight, getWidth, id){\n"
			+ "\th = getHeight() * 1.2;\n"
			+ "\tw = getWidth() * 1.2;\n"
			+ "\tupdateScales(h, w);\n"
			+ "\tvis.render();\n"
			+ "\tdimPlotVis(vis, id);\n"
			+ "\tif (typeof(window.autoArrange) == \"function\")\n"
			+ "\t\tautoArrange();\n"
			+ "}\n\n"

			+ "function zoomOut(vis, updateScales, getHeight, getWidth, id){\n"
			+ "\th = getHeight() / 1.2;\n"
			+ "\tw = getWidth() / 1.2;\n"
			+ "\tupdateScales(h, w);\n"
			+ "\tvis.render();\n"
			+ "\tdimPlotVis(vis, id);\n"
			+ "\tif (typeof(window.autoArrange) == \"function\")\n"
			+ "\t\tautoArrange();\n"
			+ "}\n\n";
		return result;
	}

	private String makeGeneralFunctions() {
		return "// ======== General Functions ========\n\n"

		+ "/* Cut Values */\n"
		+ "function cutVals(array){\n"
		+ "\tvar result = new Array(array.length);\n"
		+ "\tfor (var i = 0; i < array.length; i++){\n"
		+ "\t\tif (!isNaN(array[i])){\n"
		+ "\t\t\tn=array[i]*100;\n"
		+ "\t\t\tn=Math.round(n);\n"
		+ "\t\t\tresult[i]=n/100;\n"
		+ "\t\t}\n"
		+ "\t\telse\n"
		+ "\t\t\tresult[i] = array[i];\n"
		+ "\t}\n"
		+ "\treturn result;\n"
		+ "}\n\n"

		+ "/* Convert rgb color to hex format*/\n"
		+ "/* rgb(255,255,255) will be converted to #FFFFFF */\n"
		+ "function rgb2hex(rgb){\n"
		+ "\trgb = rgb.match(/^rgb\\((\\d+),\\s*(\\d+),\\s*(\\d+)\\)$/);\n"
		+ "\tfunction hex(x) {\n"
		+ "\t\treturn (\"0\" + parseInt(x).toString(16)).slice(-2);\n"
		+ "\t}\n"
		+ "\treturn \"#\" + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]);\n"
		+ "}\n\n"

		+ "function initLayerPosition(object){\n"
		+ "\tvar height = object.offsetHeight;\n"
		+ "\tvar width = object.offsetWidth;\n"
		+ "\tvar windowWidth = window.innerWidth;\n"
		+ "\tvar windowHeight = window.innerHeight;\n"
		+ "\tvar new_posX = windowWidth/2 - object.offsetWidth/2;\n"
		+ "\tvar new_posY = windowHeight/2 - object.offsetHeight/2;\n"
		+ "\tobject.style.left = Math.max(new_posX, 0) + \"px\";\n"
		+ "\tobject.style.top = Math.max(new_posY, 150) + \"px\";\n"
		+ "}\n\n"

		+ "function dimPlotVis(vis, id){\n"
		+ "\tvar object = document.getElementById(id);\n"
		+ "\tobject.style.height = vis.height() + vis.bottom() + vis.top() + \"px\";\n"
		+ "\tobject.style.width = vis.width() + vis.left() + vis.right() + \"px\";\n"
		+ "}\n\n"

		+ makeLayerFunctions();
	}

	private String makeInternalProtLib() {
		String lib = "";
		if (this.protLib != null){
			lib = "/* ------------------ Protovis Library Start------------------ */\n\n"
				+ this.protLib
				+ "\n/* ------------------ Protovis Library End------------------ */\n\n";
		}
		return lib;
	}

	private String makeInternalData() {
		String data = "";
		if (this.dataExporter != null){
			data = "/* ------------------ Data Start------------------ */\n\n"
				+ this.dataExporter.getString()
				+ "/* ------------------ Data End------------------ */\n\n";
		}
		return data;
	}

	private String makeExternalJs(String fileName){
		return "<script type=\"text/javascript\" src=\"" + fileName + "\"></script>\n";
	}

	private String makeDragAndDropJsFunctions(){
		String result = "// ======== Drag Functions ========\n\n"

			+ "// Object which is currently moved.\n"
			+ "var dragobjekt = null;\n\n"

			+ "// Start position\n"
			+ "var dragx = 0;\n"
			+ "var dragy = 0;\n\n"

			+ "// Mouse position\n"
			+ "var posx = 0;\n"
			+ "var posy = 0;\n\n"

			+ "// Initializing\n"
			+ "function draginit() {\n"
			+ "\tdocument.onmousemove = drag;\n"
			+ "\tdocument.onmouseup = dragstop;\n"
			+ "}\n\n"

			+ "function dragstart(element) {\n"
			+ "\tdragobjekt = element;\n"
			+ "\tdragx = posx - dragobjekt.offsetLeft;\n"
			+ "\tdragy = posy - dragobjekt.offsetTop;\n"
			+ "}\n\n"

			+ "function dragstop() {\n"
			+ "\tdragobjekt=null;\n"
			+ "}\n\n"

			+ "function drag(event) {\n"
			+ "\tposx = document.all ? window.event.clientX : event.pageX;\n"
			+ "\tposy = document.all ? window.event.clientY : event.pageY;\n"
			+ "\tif(dragobjekt != null) {\n"
			+ "\t\tvar newX = posx - dragx;\n"
			+ "\t\tvar newY = posy - dragy;\n"
			+ "\t\tdragobjekt.style.left = newX + \"px\";\n"
			+ "\t\tdragobjekt.style.top = newY >= 0 ? newY + \"px\" : 0 + \"px\";\n"
			+ "\t}\n"
			+ "}\n\n";

		return result;
	}

	private String makeTableInteractionFunctions(){
		String result = "// ======== Table Functions ========\n\n"

			+ "function td(){\n"
			+ "\treturn document.createElement(\"td\");\n"
			+ "}\n\n"

			+ "function th(){\n"
			+ "\treturn document.createElement(\"th\");\n"
			+ "}\n\n"

			+ "function tr(){\n"
			+ "\treturn document.createElement(\"tr\");\n"
			+ "}\n\n"

			+ "function table(tableName){\n"
			+ "\tvar id = document.createAttribute(\"id\");\n"
			+ "\tid.nodeValue = tableName;\n"
			+ "\tvar table = document.createElement(\"table\");\n"
			+ "\ttable.setAttributeNode(id);\n"
			+ "\treturn table;\n"
			+ "}\n\n"

			+ "function createCheckbox(){\n"
			+ "\tvar type = document.createAttribute(\"type\");\n"
			+ "\ttype.nodeValue = \"checkbox\";\n"
			+ "\tvar input = document.createElement(\"input\");\n"
			+ "\tinput.setAttributeNode(type);\n"
			+ "\treturn input;\n"
			+ "}\n\n"

			+ "function createTable(tableName, id, headers){\n"
			+ "\tvar headerRow = tr();\n"
			+ "\tvar rowdata = document.createAttribute(\"rowdata\");\n"
			+ "\trowdata.nodeValue = headers.slice(1);\n"
			+ "\theaderRow.setAttributeNode(rowdata);\n"
			+ "\tfor (var i = 0; i < headers.length; i++){\n"
			+ "\t\tvar header = th();\n"
			+ "\t\tvar headerEntry = document.createTextNode(headers[i]);\n"
			+ "\t\theader.appendChild(headerEntry);\n"
			+ "\t\theaderRow.appendChild(header);\n"
			+ "\t}\n"
			+ "\tvar t = table(tableName);\n"
			+ "\tt.appendChild(headerRow);\n"
			+ "\tout = document.getElementById(id);\n"
			+ "\tout.appendChild(t);\n"
			+ "}\n\n"

			+ "function makeEntry(tableName, array, rgb){\n"
			+ "\tif(rgb != null && rgb.length > 0 && rgb.charAt(0) != \"#\")\n"
			+ "\t\trgb = rgb2hex(rgb);\n"
			+ "\tvar row = tr();\n"
			+ "\tvar rowdata = document.createAttribute(\"rowdata\");\n"
			+ "\trowdata.nodeValue = array;\n"
			+ "\trow.setAttributeNode(rowdata);\n"
			+ "\tvar colorCell = td();\n"
			+ "\tvar bgcolor = document.createAttribute(\"bgcolor\");\n"
			+ "\tbgcolor.nodeValue = rgb;\n"
			+ "\tcolorCell.setAttributeNode(bgcolor);\n"
			+ "\trow.appendChild(colorCell);\n"
			+ "\tvar cutarray = cutVals(array);\n"
			+ "\tfor (var i = 0; i < cutarray.length; i++){\n"
			+ "\t\tvar dataCell = td();\n"
			+ "\t\tvar data = document.createTextNode(cutarray[i]);\n"
			+ "\t\tdataCell.appendChild(data);\n"
			+ "\t\trow.appendChild(dataCell);\n"
			+ "\t}\n"
			+ "\tvar checkBoxCell = td();\n"
			+ "\tcheckBoxCell.appendChild(createCheckbox());\n"
			+ "\trow.appendChild(checkBoxCell);\n"
			+ "\tout = document.getElementById(tableName);\n"
			+ "\tout.appendChild(row);\n"
			+ "\tif (typeof(window.autoArrange) == \"function\")\n"
			+ "\t\tautoArrange();\n"
			+ "}\n\n"
			
			+ "function makeMetaTableEntry(tableName, probeName, array){\n"
			+ "\tvar row = tr();\n"
			+ "\tvar nameCell = td();\n"
			+ "\tvar pn = document.createTextNode(probeName);\n"
			+ "\t\tnameCell.appendChild(pn);\n"
			+ "\t\trow.appendChild(nameCell);\n"
			+ "\tfor (var i = 0; i < array.length; i++){\n"
			+ "\t\tvar dataCell = td();\n"
			+ "\t\tvar data = document.createTextNode(array[i]);\n"
			+ "\t\tdataCell.appendChild(data);\n"
			+ "\t\trow.appendChild(dataCell);\n"
			+ "\t}\n"
			+ "\tvar checkBoxCell = td();\n"
			+ "\tcheckBoxCell.appendChild(createCheckbox());\n"
			+ "\trow.appendChild(checkBoxCell);\n"
			+ "\tout = document.getElementById(tableName);\n"
			+ "\tout.appendChild(row);\n"
			+ "\tif (typeof(window.autoArrange) == \"function\")\n"
			+ "\t\tautoArrange();\n"
			+ "}\n\n"
			
			+ "function deleteSelectedEntries(tableName){\n"
			+ "\ttable = document.getElementById(tableName);\n"
			+ "\ttemp = new Array();\n"
			+ "\tif(table.childNodes.length > 1){\n"
			+ "\t\tfor (var i = 1; i < table.childNodes.length; i++){\n"
			+ "\t\t\tcheckBox = table.childNodes[i].lastChild.firstChild;\n"
			+ "\t\t\tif (checkBox.checked)\n"
			+ "\t\t\ttemp = temp.concat(table.childNodes[i]);\n"
			+ "\t\t}\n"
			+ "\t}\n"
			+ "\tfor (var i = 0; i < temp.length; i++){\n"
			+ "\t\ttable.removeChild(temp[i]);\n"
			+ "\t}\n"
			+ "}\n\n"

			+ "function deleteAllEntries(tableName){\n"
			+ "\ttable = document.getElementById(tableName);\n"
			+ "\ttemp = new Array();\n"
			+ "\tif(table.childNodes.length > 1){\n"
			+ "\t\tfor (var i = 1; i < table.childNodes.length; i++){\n"
			+ "\t\t\ttemp = temp.concat(table.childNodes[i]);\n"
			+ "\t\t}\n"
			+ "\t}\n"
			+ "\tfor (var i = 0; i < temp.length; i++){\n"
			+ "\t\ttable.removeChild(temp[i]);\n"
			+ "\t}\n"
			+ "}\n\n"

			+ "function createNewWindow(text){\n"
			+ "\tvar copyWindow = window.open(\"about:blank\", \"_blank\");\n"
			+ "\tcopyWindow.document.write(text);\n"
			+ "\tcopyWindow.stop();\n"
			+ "}\n\n"

			+ "function copyAllEntriesCSV(tableName){\n"
			+ "\ttable = document.getElementById(tableName);\n"
			+ "\tif(table.childNodes.length > 1){\n"
			+ "\t\tvar temp = \"<pre>\";\n"
			+ "\t\tfor (var i = 0; i < table.childNodes.length; i++){\n"
			+ "\t\t\tvar rowdata = table.childNodes[i].getAttribute(\"rowdata\");\n"
			+ "\t\t\trowdata = rowdata.replace(/,/g, \", \");\n"
			+ "\t\t\ttemp = temp + rowdata + \"<br/>\";\n"
			+ "\t\t}\n"
			+ "\t\ttemp = temp + \"</pre>\";\n"
			+ "\t\tcreateNewWindow(temp);\n"
			+ "\t}\n"
			+ "}\n\n"

			+ "function copySelectedEntriesCSV(tableName){\n"
			+ "\ttable = document.getElementById(tableName);\n"
			+ "\tif(table.childNodes.length > 1){\n"
			+ "\t\tvar headerdata = table.childNodes[0].getAttribute(\"rowdata\");\n"
			+ "\t\theaderdata = headerdata.replace(/,/g, \", \");\n"
			+ "\t\tvar temp = \"<pre>\" + headerdata + \"<br/>\";\n"
			+ "\t\tfor (var i = 1; i < table.childNodes.length; i++){\n"
			+ "\t\t\tcheckBox = table.childNodes[i].lastChild.firstChild;\n"
			+ "\t\t\tif (checkBox.checked){\n"
			+ "\t\t\t\tvar rowdata = table.childNodes[i].getAttribute(\"rowdata\");\n"
			+ "\t\t\t\trowdata = rowdata.replace(/,/g, \", \");\n"
			+ "\t\t\t\ttemp = temp + rowdata + \"<br/>\";\n"
			+ "\t\t\t}\n"
			+ "\t\t}\n"
			+ "\t\ttemp = temp + \"</pre>\";\n"
			+ "\t\tcreateNewWindow(temp);\n"
			+ "\t}\n"
			+ "}\n\n"

			+ "function copyAllEntriesTSV(tableName){\n"
			+ "\ttable = document.getElementById(tableName);\n"
			+ "\tif(table.childNodes.length > 1){\n"
			+ "\t\tvar temp = \"<pre>\";\n"
			+ "\t\tfor (var i = 0; i < table.childNodes.length; i++){\n"
			+ "\t\t\tvar rowdata = table.childNodes[i].getAttribute(\"rowdata\");\n"
			+ "\t\t\trowdata = rowdata.replace(/,/g, \"	\");\n"
			+ "\t\t\ttemp = temp + rowdata + \"<br/>\";\n"
			+ "\t\t}"
			+ "\t\ttemp = temp + \"</pre>\";\n"
			+ "\t\tcreateNewWindow(temp);\n"
			+ "\t}\n"
			+ "}\n\n"

			+ "function copySelectedEntriesTSV(tableName){\n"
			+ "\ttable = document.getElementById(tableName);\n"
			+ "\tif(table.childNodes.length > 1){\n"
			+ "\t\tvar headerdata = table.childNodes[0].getAttribute(\"rowdata\");\n"
			+ "\t\theaderdata = headerdata.replace(/,/g, \"	\");\n"
			+ "\t\tvar temp = \"<pre>\" + headerdata + \"<br/>\";\n"
			+ "\t\tfor (var i = 1; i < table.childNodes.length; i++){\n"
			+ "\t\t\tcheckBox = table.childNodes[i].lastChild.firstChild;\n"
			+ "\t\t\tif (checkBox.checked){\n"
			+ "\t\t\t\tvar rowdata = table.childNodes[i].getAttribute(\"rowdata\");\n"
			+ "\t\t\t\trowdata = rowdata.replace(/,/g, \"	\");\n"
			+ "\t\t\t\ttemp = temp + rowdata + \"<br/>\";\n"
			+ "\t\t\t}\n"
			+ "\t\t}\n"
			+ "\t\ttemp = temp + \"</pre>\";\n"
			+ "\t\tcreateNewWindow(temp);\n"
			+ "\t}\n"
			+ "}\n\n";

		return result;
	}

	private String makeLayerFunctions(){
		String result = "// ======== Layer Functions ========\n\n"

			+ "function displayLayer(checkBox, targetId){\n"
			+ "\tif (checkBox.checked){\n"	
			+ "\t\tvar object = document.getElementById(targetId);\n"
			+ "\t\tobject.style.display = \"block\";\n"
			+ "\t\t(typeof(window.addObjectId) == \"function\") ? addObjectId(targetId) : null;\n"
			+ "\t\tif(document.getElementById('autoArranging') && document.getElementById('autoArranging').checked){\n"
			+ "\t\t\tarrangeObjects();\n"
			+ "\t\t}\n"
			+ "\t\telse if (checkBox.value == 0){\n"
			+ "\t\t\tinitLayerPosition(object);\n"
			+ "\t\t\tcheckBox.value = 1;\n"
			+ "\t\t}\n"
			+ "\t}\n"
			+ "\telse {\n"
			+ "\t\tdocument.getElementById(targetId).style.display = \"none\";\n"
			+ "\t\t(typeof(window.deleteObjectId) == \"function\") ? deleteObjectId(targetId) : null;\n"
			+ "\t\tif(document.getElementById('autoArranging') && document.getElementById('autoArranging').checked){\n"
			+ "\t\t\tarrangeObjects();\n"
			+ "\t\t}\n"
			+ "\t}\n"
			+ "}\n\n"

			+ "function displayBorderAndOptions(layer){\n"
			+ "\tif (layer != null){\n"
			+ "\t\tlayer.style.borderColor = \"#BFEFFF\";\n"
			+ "\t\tlayer.lastChild.previousSibling.style.visibility = \"visible\";\n"
			+ "\t\tlayer.style.zIndex = 2;\n"
			+ "\t\tlayer.style.backgroundColor = \"#FFFFFF\";\n"
			+ "\t}\n"
			+ "}\n\n"

			+ "function hideBorderAndOptions(layer){\n"
			+ "\tif (layer != null){\n"
			+ "\t\tlayer.style.borderColor = \"transparent\";\n"
			+ "\t\tlayer.lastChild.previousSibling.style.visibility = \"hidden\";\n"
			+ "\t\tlayer.style.zIndex = 1;\n"
			+ "\t\tlayer.style.backgroundColor = \"transparent\";\n"
			+ "\t}\n"
			+ "}\n\n";

		return result;

	}

	private String makeInternalCss(){
		String result = "<style type=\"text/css\">\n\n"
			+ "body{\n"
			+ "\tmargin: 0;\n"
			+ "\tfont-family:Verdana;\n"
			+ "\tfont-size: small;\n"
			+ "}\n\n"

			+ "#topPanel{\n"
			+ "\tposition: fixed;\n"
			+ "\twidth: 100%;\n"
			+ "\tz-index: 3;\n"
			+ "}\n\n"

			+ "#options{\n"
			+ "\ttext-align: center;\n"
			+ "\tbackground-color: #BFEFFF;\n"
			+ "\tborder-bottom: 1px solid #9E9E9E;\n"
			+ "\tpadding: 6px;\n"
			+ "\theight: 48px;\n"
			+ "}\n\n"

			+ "#title{\n"
			+ "\ttext-align: center;\n"
			+ "\tbackground-color: #9E9E9E;\n"
			+ "\tpadding: 10px;\n"
			+ "\theight: 30px;\n"
			+ "\tfont-size: x-large;\n"
			+ "\tword-spacing: 0.3em;\n"
			+ "\tletter-spacing: 0.01em;\n"
			+ "\tborder-bottom: 1px solid black;\n"
			+ "}\n\n"

			+ "#infoBox{\n"
			+ "\tfloat: right;\n"
			+ "\tpadding: 10px;\n"
			+ "\tmin-height: 71px;\n"
			+ "\tbackground-color: #9E9E9E;\n"
			+ "\tcolor: black;\n"
			+ "\tborder-left: 1px dashed black;\n"
			+ "\tborder-bottom: 1px dashed black;\n"
			+ "\tfont-size: x-small;\n"
			+ "\tmin-height: 90px;\n"
			+ "}\n\n"

			+ ".plot{\n"
			+ "\tdisplay: none;\n"
			+ "\ttop: 100px;\n"
			+ "\tleft: 10%;\n"
			+ "\tposition: absolute;\n"
			+ "\tz-index: 1;\n"
			+ "\tborder-width: medium;\n"
			+ "\tborder-color: transparent;\n"
			+ "\tborder-style: solid;\n"
			+ "}\n\n"

			+ ".plotDescription{\n"
			+ "\tpadding: 10px;\n"
			+ "\ttext-align: center;\n"
			+ "\tclear:both;\n"
			+ "\tmax-width: 500px;\n"
			+ "\tmargin-left: auto;\n"
			+ "\tmargin-right: auto;\n"
			+ "\tfont-style: italic;\n"
			+ "}\n\n"

			+ ".plotOptions{\n"
			+ "\tpadding: 10px;\n"
			+ "\ttext-align: center;\n"
			+ "\tvisibility: hidden;\n"
			+ "\tbackground-color: #BFEFFF;\n"
			+ "\tclear: both;\n"
			+ "}\n\n"

			+ ".plotVis{\n"
			+ "\tpadding: 10px;\n"
			+ "\tfloat: left;\n"
			+ "\ttext-align:center;\n"
			+ "}\n\n";

		if (this.htmlExportSettings.isProbesTableInteraction()){
			// Probes table layer attributes
			result += "#probestable{\n"
				+ "\tpadding: 1px;\n"
				+ "\tdisplay: none;\n"
				+ "\ttop: 100px;\n"
				+ "\tposition: absolute;\n"
				+ "\tz-index: 2;\n"
				+ "}\n\n";
		}

		if (this.htmlExportSettings.isArrangementInteraction()){
			result += "#arranging{\n"
				+ "\tfloat: left;\n"
				+ "}\n\n";
		}

		if (this.htmlExportSettings.isCommentBoxInteraction()){
			result += ".commentBox{\n"
				+ "\tpadding:10px;\n"
				+ "\tmargin:10px;\n"
				+ "\twidth: 350px;\n"
				+ "\tborder-color: #666666;\n"
				+ "\tborder-style: solid;\n"
				+ "\tborder-width: 1px;\n"
				+ "\tfloat: left;\n"
				+ "}\n\n"

				+ ".commentItem{\n"
				+ "\tmargin: 5px;\n"
				+ "\tpadding-top: 10px;\n"
				+ "}\n\n"

				+ ".commentName{\n"
				+ "\tfont-weight: bold;\n"
				+ "\twidth: 180px;\n"
				+ "\tfloat: left;\n"
				+ "}\n\n"

				+ ".commentTime{\n"
				+ "\tfont-size: x-small;\n"
				+ "\tcolor: #666666;\n"
				+ "}\n\n"

				+ ".commentComment{\n"
				+ "\tclear: both;\n"
				+ "}\n\n";
		}

		return result + "</style>\n";
	}

	private String makeBody(){
		String result = "<body onload=\"draginit()\">\n\n"

			// Top Layer
			+ "<div id=\"topPanel\">\n"

			// Info Layer
			+ "<div id=\"infoBox\">\n"
			+ "<script type=\"text/javascript\">\n"
			+ "\tnumberOfExperiments();\n"
			+ "\tnumberOfProbes();\n"
			+ "\tnumberOfProbeLists();\n"
			+ "\tprobeListsInformation();\n"
			+ "\tglobalVals();\n"
			+ "</script>\n"
			+ "</div>\n"

			// Title Layer
			+ "<div id=\"title\">\n"
			+ this.htmlExportSettings.getTitle()
			+ "\n</div>\n"


			+ "<div id=\"options\">\n";

		// Option: arrange plots
		if(this.htmlExportSettings.isArrangementInteraction()){
			result += "<div id=\"arranging\">\n"
				+ "<form name=\"arrangeOptions\">\n"
				+ "\t<input type=\"radio\" name=\"arrange\" id=\"arrangeV\" checked=\"checked\">Vertical\n"
				+ "\t<input type=\"radio\" name=\"arrange\" id=\"arrangeH\">Horizontal<br/>\n"
				+ "\t<input type=\"checkbox\" id=\"autoArranging\" checked=\"checked\" />Auto Arranging\n"
				+ "\t<input type=\"button\" value=\"Arrange Objects\" onclick=\"arrangeObjects()\" />\n"
				+ "</form>\n"
				+ "</div>\n";
		}

		// Option: display plots
		result += "<div><form id=\"plots\">\n";

		for(int i = 0; i < this.plotExporters.size(); i++){
			result += "\t<input type=\"checkbox\" value=\"0\" onclick=\"displayLayer(this, '" + this.plotIds.get(i) + "')\" />" + this.plotExporters.get(i).getName() + "\n";
		}

		if (this.htmlExportSettings.isProbesTableInteraction()){
			result += "\t<input type=\"checkbox\" value=\"0\" onclick=\"displayLayer(this,'probesTable')\"/>Probes Table\n";
		}
		
		if (this.htmlExportSettings.isMetaTableInteraction()){
			result += "\t<input type=\"checkbox\" value=\"0\" onclick=\"displayLayer(this,'metaInfoTable')\"/>Meta Info Table\n";
		}

		result += "</form></div>\n"
			+ "</div></div>\n\n";

		// Plot layers
		for(int i = 0; i < this.plotExporters.size(); i++){

			PlotExporter plotExporter = this.plotExporters.get(i);
			String id = this.plotIds.get(i);
			String commentBoxId = this.commentBoxIds.get(i);
			String descriptionId = this.descriptionIds.get(i);
			String visId = this.visIds.get(i);

			result += "<div class=\"plot\" id=\"" + id + "\" onmouseover=\"displayBorderAndOptions(this)\" onmouseout=\"hideBorderAndOptions(this)\">\n"
			+ "<div class=\"plotVis\" id=\"" + visId + "\">\n"
			+ "<script type=\"text/javascript+protovis\">\n"
			+ plotExporter.getString()
			+ "dimPlotVis(" + id + ".plot, \"" + visId + "\");\n\n"
			+ "</script>\n"
			+ "</div>\n";

			// Comment Box
			if(this.htmlExportSettings.isCommentBoxInteraction()){
				result += "<div class=\"commentBox\" id=\"" + commentBoxId + "\">\n"
				+ "<?php\n"
				+ "\t$name = \"" + id + "CommentBox\";\n"
				+ "\twriteCommentBox($name);\n"
				+ "?>\n"
				+ "</div>\n";
			}

			// Plot Description
			result += "<div class=\"plotDescription\" id=\"" + descriptionId + "\">\n";

			String description = plotExporter.getPlotDescription();
			result += (description == null) ? "" : "<p>" + description + "</p>\n";

			result += "</div>\n";

			// General options
			result += "<div class=\"plotOptions\">\n";

			String general = "";
			String seperator = "&nbsp;|&nbsp;";

			if (this.htmlExportSettings.isResizeInteraction()){
				general += "<span style=\"cursor:pointer;\" onclick=\"zoomIn(" + id + ".plot, " + id + ".updateScales, " + id + ".height, " + id + ".width, '" + visId + "')\">Zoom in</span>\n"
				+ "&nbsp;|&nbsp;<span style=\"cursor:pointer;\" onclick=\"zoomOut(" + id + ".plot, " + id + ".updateScales, " + id + ".height, " + id + ".width, '" + visId + "')\">Zoom out</span>\n";
			}

			if (this.htmlExportSettings.isMoveInteraction()){
				if (!general.isEmpty()){
					general += seperator;
				}
				general += "<span style=\"cursor:move;\" onmousedown=\"dragstart(document.getElementById('" + id + "'))\">Move</span>\n";
			}

			if (this.htmlExportSettings.isCommentBoxInteraction()){
				if (!general.isEmpty()){
					general += seperator;
				}
				general += "<span><input type=\"checkbox\" checked=\"checked\" onclick=\"displayLayer(this, '" + commentBoxId + "');\"/>Comments</span>\n";
			}

			if (!(description == null)){
				if (!general.isEmpty()){
					general += seperator;
				}
				general += "<span><input type=\"checkbox\" checked=\"checked\" onclick=\"displayLayer(this, '" + descriptionId + "');\"/>Description</span>\n";
			}

			if (this.htmlExportSettings.isArrangementInteraction()){
				if (!general.isEmpty()){
					general += seperator;
				}
				general += "<span style=\"cursor:pointer;\" onmousedown=\"moveUp('" + id + "')\">Pop</span>\n"
				+ "&nbsp;|&nbsp;<span style=\"cursor:pointer;\" onmousedown=\"moveDown('" + id + "')\">Push</span>\n";
			}

			if (!general.isEmpty()){
				general = "<p>\n" + general;
				general = general.concat("</p>\n");
			}
			result += general;

			// Plot specific options
			result += (plotExporter.getPlotOptions() == null) ? "" : plotExporter.getPlotOptions();	

			result += "</div>\n"
				+ "</div>\n\n";
		}

		// Probes Table
		if (this.htmlExportSettings.isProbesTableInteraction()){
			result += "<div class=\"plot\" id=\"probesTable\" onmouseover=\"displayBorderAndOptions(this, 'probesTableOptions')\" onmouseout=\"hideBorderAndOptions(this, 'probesTableOptions')\">\n"
				+ "<div id=\"probesTableDiv\">\n"
				+ "<script type=\"text/javascript+protovis\">\n"
				+ "\tcreateTable(\"" + this.probesTableName + "\", \"probesTableDiv\", new Array(\"Color\", \"Probe\").concat(experiments));\n"
				+ "</script>\n"
				+ "</div>\n"
				+ "<div class=\"plotOptions\" id=\"probesTableOptions\">\n"


				+ "<p>\n"
				+ "\t<input type=\"button\" id=\"Delete Selected\" name=\"Delete Selected\" value=\"Delete Selected\" onclick='deleteSelectedEntries(\"" + this.probesTableName + "\");'\\>\n"
				+ "\t<input type=\"button\" id=\"Delete All\" name=\"Delete All\" value=\"Delete All\" onclick='deleteAllEntries(\"" + this.probesTableName + "\");'\\>\n"
				+ "\t<input type=\"button\" id=\"Copy Selected (CSV)\" name=\"Copy Selected (CSV)\" value=\"Copy Selected (CSV)\" onclick='copySelectedEntriesCSV(\"" + this.probesTableName + "\");'\\>\n"
				+ "\t<input type=\"button\" id=\"Copy All (CSV)\" name=\"Copy All (CSV)\" value=\"Copy All (CSV)\" onclick='copyAllEntriesCSV(\"" + this.probesTableName + "\");'\\>\n"
				+ "\t<input type=\"button\" id=\"Copy Selected (TSV)\" name=\"Copy Selected (TSV)\" value=\"Copy Selected (TSV)\" onclick='copySelectedEntriesTSV(\"" + this.probesTableName + "\");'\\>\n"
				+ "\t<input type=\"button\" id=\"Copy All (TSV)\" name=\"Copy All (TSV)\" value=\"Copy All (TSV)\" onclick='copyAllEntriesTSV(\"" + this.probesTableName + "\");'\\>\n"
				+ "\t</p>\n";


			// Probes Table Options
			String options = "";
			String seperator = "&nbsp;|&nbsp;";

			if (this.htmlExportSettings.isMoveInteraction()){
				options += "<span style=\"cursor:move;\" onmousedown=\"dragstart(document.getElementById('probesTable'))\">Move</span>\n";
			}

			if (this.htmlExportSettings.isArrangementInteraction()){
				if (!options.isEmpty()){
					options += seperator;
				}
				options += "<span style=\"cursor:pointer;\" onmousedown=\"moveUp('probesTable')\">Pop</span>\n"
					+ "&nbsp;|&nbsp;<span style=\"cursor:pointer;\" onmousedown=\"moveDown('probesTable')\">Push</span>\n";
			}

			if (!options.isEmpty()){
				options = "<p>\n" + options;
				options = options.concat("</p>\n");
			}
			result += options;

			result += "</div>\n"
				+ "</div>\n\n";
		}

		// Meta Info Table
		if (this.htmlExportSettings.isMetaTableInteraction()){
			result += "<div class=\"plot\" id=\"metaInfoTable\" onmouseover=\"displayBorderAndOptions(this, 'metaInfoTableOptions')\" onmouseout=\"hideBorderAndOptions(this, 'metaInfoTableOptions')\">\n"
				+ "<div id=\"metaInfoTableDiv\">\n"
				+ "<script type=\"text/javascript+protovis\">\n"
				+ "\tcreateTable(\"" + this.metaInfoTableName + "\", \"metaInfoTableDiv\", new Array(\"Probe\").concat(metaHeader));\n"
				+ "</script>\n"
				+ "</div>\n"
				+ "<div class=\"plotOptions\" id=\"metaInfoTableOptions\">\n"
				
				+ "<p>\n"
				+ "\t<input type=\"button\" id=\"Delete Selected\" name=\"Delete Selected\" value=\"Delete Selected\" onclick='deleteSelectedEntries(\"" + this.metaInfoTableName + "\");'\\>\n"
				+ "\t<input type=\"button\" id=\"Delete All\" name=\"Delete All\" value=\"Delete All\" onclick='deleteAllEntries(\"" + this.metaInfoTableName + "\");'\\>\n"
				+ "</p>\n";

			// Meta Info Table Options
			String options = "";
			String seperator = "&nbsp;|&nbsp;";

			if (this.htmlExportSettings.isMoveInteraction()){
				options += "<span style=\"cursor:move;\" onmousedown=\"dragstart(document.getElementById('metaInfoTable'))\">Move</span>\n";
			}

			if (this.htmlExportSettings.isArrangementInteraction()){
				if (!options.isEmpty()){
					options += seperator;
				}
				options += "<span style=\"cursor:pointer;\" onmousedown=\"moveUp('metaInfoTable')\">Pop</span>\n"
					+ "&nbsp;|&nbsp;<span style=\"cursor:pointer;\" onmousedown=\"moveDown('metaInfoTable')\">Push</span>\n";
			}

			if (!options.isEmpty()){
				options = "<p>\n" + options;
				options = options.concat("</p>\n");
			}
			result += options;

			result += "</div>\n"
				+ "</div>\n\n";
		}

		result +=  "</body>\n";

		return result;
	}

	public DataExporter getDataExporter() {
		return dataExporter;
	}

	public void setDataExporter(DataExporter dataExporter) {
		this.dataExporter = dataExporter;
	}

	public void setProtLib(String protLib) {
		this.protLib = protLib;
	}

	public List<String> getCommentBoxIds() {
		return commentBoxIds;
	}

}
