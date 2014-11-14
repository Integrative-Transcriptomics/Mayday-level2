package mayday.exportjs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;
import mayday.exportjs.exporter.DataExporter;
import mayday.exportjs.exporter.DataExporterSettings;
import mayday.exportjs.exporter.HTMLExporter;
import mayday.exportjs.exporter.HTMLExporterSetting;
import mayday.exportjs.exporter.PlotExporter;

public class OutputWriter {

	OutputWriterSetting outputWriterSetting;

	private boolean externalDataFile;
	private boolean externalLibFile;

	private File htmlFile;
	private File dataFile;
	private FMFile protovisLibFileSource;
	private File protovisLibFile;

	private FMFile commentBoxPostFileSource;
	private FMFile commentBoxFunctionsFileSource;

	public OutputWriter() {

		this.externalDataFile = false;
		this.externalLibFile = false;

		String pathName = System.getProperty("user.home");
		this.htmlFile = new File(pathName, "index.html");
		this.dataFile = new File(pathName, "index_data.js");
		this.protovisLibFileSource = PluginManager.getInstance().getFilemanager().getFile("mayday/exportjs/protovis-r3.2.js");
		this.protovisLibFile = new File(pathName, this.protovisLibFileSource.Name);
		this.commentBoxPostFileSource = PluginManager.getInstance().getFilemanager().getFile("mayday/exportjs/commentBox_post.php");
		this.commentBoxFunctionsFileSource = PluginManager.getInstance().getFilemanager().getFile("mayday/exportjs/commentBox_functions.php");

		this.outputWriterSetting = new OutputWriterSetting(this);
	}

	public void export(DataExporterSettings dataExporterSettings, HTMLExporterSetting htmlExportSettings, List<PlotExporter> plotExporters){

		// Initialize Exporters
		dataExporterSettings.initDataExporter();
		htmlExportSettings.createHTMLExporter(plotExporters);

		// Internal or external DataFile?
		if (this.externalDataFile){
			htmlExportSettings.addExternalJsFile(this.dataFile.getName());
			writeData(dataExporterSettings);
		}
		else {
			htmlExportSettings.getHTMLExporter().setDataExporter(dataExporterSettings.getDataExporter());
		}

		// Internal or external LibFile?
		if (this.externalLibFile){
			htmlExportSettings.addExternalJsFile(this.protovisLibFile.getName());
			copyProtovisLib();
		}
		else {
			htmlExportSettings.getHTMLExporter().setProtLib(getProtovisLibString());
		}

		// Check for PHP
		if (htmlExportSettings.isCommentBoxInteraction()){
			String path = this.htmlFile.getParent();

			// Change .htm(l) to .php
			String name = this.htmlFile.getName();
			if (name.endsWith(".html")){
				name = name.replace(".html", ".php");
			}
			else if (name.endsWith(".htm")){
				name = name.replace(".htm", ".php");
			}
			else {
				name = name.concat(".php");
			}

			this.htmlFile = new File(path, name);

			// Create empty files for comment box entries
			List<String> commentBoxIds = htmlExportSettings.getHTMLExporter().getCommentBoxIds();
			Iterator<String> iterator = commentBoxIds.iterator();
			while(iterator.hasNext()){
				File emptyFile = new File(path, iterator.next());
				write(emptyFile, null);
			}
			copyPhpFiles();
		}

		// Write Html
		writeHtml(htmlExportSettings, plotExporters);
	}

	private void writeData(DataExporterSettings dataExporterSettings){
		DataExporter dataExporter = new DataExporter(dataExporterSettings);
		write(this.dataFile, dataExporter.getString());
	}

	private void copyProtovisLib(){

		BufferedReader in = new BufferedReader(new InputStreamReader(this.protovisLibFileSource.getStream())); 

		try {
			BufferedWriter out;
			out = new BufferedWriter(new FileWriter(this.protovisLibFile));
			String line = in.readLine();
			while(line != null){
				out.write(line);
				out.newLine();
				line = in.readLine();
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void copyPhpFiles(){
		BufferedReader in1 = new BufferedReader(new InputStreamReader(this.commentBoxFunctionsFileSource.getStream())); 

		try {
			BufferedWriter out;
			out = new BufferedWriter(new FileWriter(new File(this.htmlFile.getParent(), this.commentBoxFunctionsFileSource.Name)));
			String line = in1.readLine();
			while(line != null){
				out.write(line);
				out.newLine();
				line = in1.readLine();
			}
			in1.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		BufferedReader in2 = new BufferedReader(new InputStreamReader(this.commentBoxPostFileSource.getStream())); 

		try {
			BufferedWriter out;
			out = new BufferedWriter(new FileWriter(new File(this.htmlFile.getParent(), this.commentBoxPostFileSource.Name)));
			String line = in2.readLine();
			while(line != null){
				out.write(line);
				out.newLine();
				line = in2.readLine();
			}
			in2.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getProtovisLibString(){
		String lib = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(this.protovisLibFileSource.getStream()));
		String line;
		try {
			line = in.readLine();
			while(line != null){
				lib += line + "\n";
				line = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lib; 
	}

	private void writeHtml(HTMLExporterSetting htmlExportSettings, List<PlotExporter> plotsExporter){		
		HTMLExporter hTMLExporter = htmlExportSettings.getHTMLExporter();
		write(this.htmlFile, hTMLExporter.getString());
	}

	private void write(File file, String s){
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			if (s != null){
				out.write(s);
			}
			out.close();
		}
		catch(Exception e){
			System.err.println( "Daten konnten nicht geschrieben werden." );
		}
	}

	public File getHtmlFile() {
		return htmlFile;
	}

	public void setHtmlFile(File htmlFile) {
		String name = htmlFile.getName();

		if (!(name.endsWith(".html") || name.endsWith(".htm"))){
			name = name.concat(".html");
			this.htmlFile = new File(htmlFile.getParent(), name);
		}
		else 
			this.htmlFile = htmlFile;
	}

	public File getDataFile() {
		return dataFile;
	}

	public void setDataFile(File dataFile) {
		String name = dataFile.getName();

		if (!name.endsWith(".js")){
			name = name.concat(".js");
			this.dataFile = new File(dataFile.getParent(), name);
		}
		else 
			this.dataFile = dataFile;
	}

	public File getProtovisLibFile() {
		return protovisLibFile;
	}

	public void setProtovisLibFile(File protovisLibFile) {
		String name = protovisLibFile.getName();

		if (!name.endsWith(".js")){
			name = name.concat(".js");
			this.protovisLibFile = new File(protovisLibFile.getParent(), name);
		}
		else
			this.protovisLibFile = protovisLibFile;
	}

	public OutputWriterSetting getOutputWriterSetting() {
		return outputWriterSetting;
	}

	public boolean isExternalDataFile() {
		return externalDataFile;
	}

	public void setExternalDataFile(boolean externalDataFile) {
		this.externalDataFile = externalDataFile;
	}

	public boolean isExternalLibFile() {
		return externalLibFile;
	}

	public void setExternalLibFile(boolean externalLibFile) {
		this.externalLibFile = externalLibFile;
	}

}
