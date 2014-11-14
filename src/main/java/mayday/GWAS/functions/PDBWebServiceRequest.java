package mayday.GWAS.functions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import mayday.GWAS.utilities.BrowserToolkit;

public class PDBWebServiceRequest {

	public static final String SERVICE_LOCATION = "http://www.rcsb.org/pdb/rest/search/";
	public static final String FILE_ADDRESS_PREFIX = "http://www.rcsb.org/pdb/files/";
	public static final String FILE_ADDRESS_SUFFIX = ".pdb";
	public static final String JMOL_ADDRESS_PREFIX = "http://www.rcsb.org/pdb/explore/jmol.do?structureId=";
	
	public static final String CUSTOM_REPORT_SERVICE_LOCATION = "http://www.rcsb.org/pdb/rest/customReport";
	
	private boolean sort = true;
	
	public static final String SORT_E_VALUE = "rank%20Descending";
	public static final String SORT_RELEASE_DATE = "Release%20Date";
	public static final String SORT_RESIDUE_COUNT = "Residue%20Count";
	public static final String SORT_RESOLUTION = "Resolution";
	
	private String xml;
	private double eValue = 0.000001;
	private String sortMethod = SORT_E_VALUE;
	private boolean openBestHitInBrowser = false;
	
	public PDBWebServiceRequest(String proteinSequence) {
		xml = createSequenceRequest(proteinSequence);
	}
	
	private URL getCustomReportServiceURL(List<String> pdbids, List<String> fields) throws MalformedURLException {
		StringBuffer buf = new StringBuffer();
		buf.append(CUSTOM_REPORT_SERVICE_LOCATION);
		
		if(pdbids.size() > 0) {
			buf.append("?pdbids=");
			for(int i = 0; i < pdbids.size(); i++) {
				buf.append(pdbids.get(i));
				if(i != pdbids.size()-1) {
					buf.append(",");
				}
			}
			
			if(fields.size() > 0) {
				buf.append("&customReportColumns=");
				
				for(int i = 0; i < fields.size(); i++) {
					buf.append(fields.get(i));
					if(i != fields.size()-1) {
						buf.append(",");
					}
				}
			}
			
			buf.append("&format=csv");
			buf.append("&service=wsfile");
			
			return new URL(buf.toString());
		}
		
		return null;
	}
	
	private String createSequenceRequest(String sequence) {
		StringBuffer xml = new StringBuffer();
		
		xml.append("<orgPdbCompositeQuery version=\"1.0\">");
		xml.append("<queryRefinement>");
		xml.append("<queryRefinementLevel>0</queryRefinementLevel>");
		
		xml.append("<orgPdbQuery>");
		xml.append("<queryType>org.pdb.query.simple.SequenceQuery</queryType>");
		
		xml.append("<sequence><![CDATA[" + sequence + "]]></sequence>");
		xml.append("<eCutOff><![CDATA[" + eValue + "]]></eCutOff>");
		xml.append("<searchTool><![CDATA[blast]]></searchTool>");
		//xml.append("<sortfield>rank_Descending</sortfield>");
		
		xml.append("</orgPdbQuery>");
		xml.append("</queryRefinement>");
		xml.append("</orgPdbCompositeQuery>");
		
		return xml.toString();
	}
	
	private URL getServiceURL() throws MalformedURLException{
		String location = SERVICE_LOCATION;
		
		if(sort) {
			location += "?sortfield=" + sortMethod;
		}
		
		return new URL(location);
	}
	
	public PDBCustomReport getCustomReport(List<String> pdbIds, List<String> fields) throws IOException {
		URL serviceLocation = this.getCustomReportServiceURL(pdbIds, fields);
		
		if(serviceLocation == null)
			return null;
		
		HttpURLConnection connection = (HttpURLConnection)serviceLocation.openConnection();
		
		if(connection.getResponseCode() != 200) {
			throw new IOException(connection.getResponseMessage());
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line = null;
		PDBCustomReport report = new PDBCustomReport(fields);
		
		boolean headerLine = true;
		
		while((line = br.readLine()) != null) {
			if(headerLine) {
				headerLine = false;
				continue;
			}
			
			String[] split = line.split(",");
			
			for(int i = 0; i < split.length; i++) {
				report.add(i, split[i].replaceAll("\"", ""));
			}
		}
		
		br.close();
		connection.disconnect();
		
		return report;
	}
	
	public List<String> postQuery() throws IOException {
		URL url = getServiceURL();
		String encodedXML = URLEncoder.encode(xml, "UTF-8");
		
		InputStream in = doPOST(url, encodedXML);
		
		List<String> pdbIDs = new ArrayList<String>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = null;
		
		while((line = br.readLine()) != null) {
			pdbIDs.add(line);
		}
		
		br.close();
		
		return pdbIDs;
	}
	
	public InputStream doPOST(URL url, String data) throws IOException {
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		
		OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
		
		osw.write(data);
		osw.flush();
		
		osw.close();
		
		return connection.getInputStream();
	}
	
	public String getPDBFileLink(String pdbID) {
		return FILE_ADDRESS_PREFIX + pdbID + FILE_ADDRESS_SUFFIX;
	}
	
	public String getJMolVisLink(String pdbID) {
		return JMOL_ADDRESS_PREFIX + pdbID;
	}
	
	public static void main(String[] args) throws Exception {
		String s = "PQVTLWQRPLVTIKIGGQLKEALLDTGADDTVLEEMSLPGRWKPKMIGGIGGFIKVRQYDQILIEICGHKAIGTVLVGPTPVNIIGRNLLTQIGCTLNF";
		
		PDBWebServiceRequest r = new PDBWebServiceRequest(s);
		List<String> pdbIDs = r.postQuery();

		System.out.println("Number of hits: " + pdbIDs.size());
		
		System.out.println("Best hit -> file address:");
		String bestID = pdbIDs.get(0);
		System.out.println(r.getPDBFileLink(bestID));
		
		System.out.println("JMol Adress for Visualization");
		System.out.println(r.getJMolVisLink(bestID));
		
		if(r.openBestHitInBrowser) {
			BrowserToolkit.openURL(r.getPDBFileLink(bestID));
		}
		
		List<String> fields = new ArrayList<String>();
		
		fields.add(PDBReportFields.STRUCTURE_ID);
		fields.add(PDBReportFields.CHAIN_ID);
		fields.add(PDBReportFields.BIOLOGICAL_PROCESS);
		fields.add(PDBReportFields.CELLULAR_COMPONENT);
		fields.add(PDBReportFields.MOLECULAR_FUNCTION);
		fields.add(PDBReportFields.EC_NUMBER);
		
		PDBCustomReport report = r.getCustomReport(pdbIDs, fields);
		System.out.println(report.toString());
	}
}
