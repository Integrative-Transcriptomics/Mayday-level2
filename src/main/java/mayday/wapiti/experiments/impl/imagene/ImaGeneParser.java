package mayday.wapiti.experiments.impl.imagene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ImaGeneParser {

	private int sMean = -1;	
	private int bMean = -1;
	private int sMedian = -1;
	private int bMedian = -1;
	private int sMode = -1;
	private int bMode= -1;
	private int sTotal = -1;
	private int bTotal = -1;


	private int geneId = -1;
	private int emptySpotOffset = -1;
	private int negativSpotOffset = -1;

	//offsets for poor spot parameters
	private int backContaminationOffset = -1;
	private int signalContaminationOffset = -1;
	private int percentageOffset = -1;
	private int perimeterOffset = -1;
	private int shapeRegularityOffset = -1;
	private int areaToPerimeterRatioOffset = -1;
	private int offsetFlag = -1;

	private ImaGeneArray ar;
	//one Input for the Channel with the control data
	//and one for the channel with the experiment data
	private ArrayList<String> filenames;
	//Which intensity values should be read in?
	private int choice;


	//an additional data structure to hold the information specific for the ImaGeneArray
	private ImaGeneDataStructure imagene;

	/**
	 * The constructor takes the filenames and the choice which values to use
	 * @param filenames
	 * @param choice
	 */
	public ImaGeneParser(ArrayList<String> filenames, int choice){
		this.filenames = filenames;
		this.choice = choice;
		//we cannot create our MicroArray ar now because
		//we do not know the dimensions yet
		this.imagene = new ImaGeneDataStructure();
	}

	/**
	 * This function parses the given Array 
	 * @throws Exception
	 */
	public void parseArray() throws Exception{

		//Test if choice is a valid choice
		if(!(choice == ImaGeneDataStructure.sMean || choice==ImaGeneDataStructure.sMedian || choice ==ImaGeneDataStructure.sMode 
				|| choice == ImaGeneDataStructure.sTotal)){
			throw new Exception(choice + " is not valid as input for ImaGeneParser: " +
			"take ImaGeneDataStructure.sMean, sMedian, sMode or sTotal");
		}
		//We get two Buffered Readers one for the Control Channel and one for the file containing
		// the data of the experiment channel
		BufferedReader control = new BufferedReader(new FileReader(filenames.get(0)));
		BufferedReader exp     = new BufferedReader(new FileReader(filenames.get(1)));

		System.out.println("Parsing Header");
		try
		{
			parseHeader(control);
		}
		catch(Exception e){
			throw(new Exception("Error while parsing header"));
		}
		System.out.println("Begin Parsing Raw Data Control");
		try
		{
			parseRawData(control,choice,0);
		}
		catch(Exception e ){
			throw new Exception(e.getMessage());
		}

		System.out.println("Begin Parsing Raw Data Experiment");
		try{
			parseRawData(exp,choice,1);
		}
		catch(Exception e){
			throw new Exception("Error while Parsing the RawData of the experiment file");
		}

		//We don't need our BufferedReaders any more and close them.
		control.close();
		exp.close();


	}

	/**
	 * parse the Header of an ImaGenefile
	 * to get the dimensions and the flagsettings
	 * @param r
	 * @throws Exception
	 */
	private void parseHeader (BufferedReader r) throws Exception
	{

		String line = r.readLine();
		line = line.trim(); 
		//We have nothing to do until the begin of the Header
		while(!line.startsWith("Begin Header")) // oder endswith?
		{
			line = r.readLine();
			line = line.trim();
		}

		//and we throw everything away of the header we don't need
		while(line!=null && !line.startsWith("Begin Field Dimensions"))
		{
			line = r.readLine();
			line = line.trim();
		}
		//Now we have the Line which tells us the names of the dimensions
		r.readLine();
		//we get the line with the actual numbers we are interested in here
		try{
			line= r.readLine();
		}
		catch(Exception e){
			throw new Exception("Unexcpected End Of File while reading the dimensions");
		}
		parseDimensionLine(line);
		//Parsing of the FlagSettings
		try{
			parseFlagSettings(r);
		}
		catch(Exception e){
			throw new Exception("Error while parsing Flag Settings");
		}

	}

	/**
	 * This function parses the DimensionInformation of an Imagenefile
	 * 
	 * @param line
	 */
	private void parseDimensionLine(String line)
	{		
		StringTokenizer tk=new StringTokenizer(line.trim(),"\t");
		//The width of the array is Metacolumn multiplied by column
		//The height of the array is Metarow multiplied by rows
		//ar.setField(tk.nextToken().charAt(0));
		//the A at the beginning is skipped
		tk.nextToken();
		int metarows = Integer.parseInt(tk.nextToken());

		imagene.setMetaRows(metarows);

		//ar.setMetaRows(Integer.parseInt(tk.nextToken()));
		int metacols = Integer.parseInt(tk.nextToken());
		//ar.setMetaCols(Integer.parseInt(tk.nextToken()));
		imagene.setMetaCols(metacols);
		int rows = Integer.parseInt(tk.nextToken());
		imagene.setRows(rows);
		//ar.setRows(Integer.parseInt(tk.nextToken()));
		int cols = Integer.parseInt(tk.nextToken());
		imagene.setCols(cols);
		// ar.setCols(Integer.parseInt(tk.nextToken()));
		//we set the Diemensions of the Array according to the file
		ar = new ImaGeneArray(metarows*rows, metacols*cols);
		//setting the printtip size of the array
		ar.setPrintipHeight(metarows);
		ar.setPrintipWidth(metacols);
	}


	/**
	 * This Function parses and sets the flags
	 * @param r
	 * @throws Exception
	 */
	private void parseFlagSettings (BufferedReader r) throws Exception
	{
		String line = r.readLine();
		line = line.trim(); 
		while((line != null) && !line.endsWith("Begin Quality settings")){
			line = r.readLine();
			line = line.trim();
		}

		while(line!=null && !line.startsWith("Empty Spots")){
			line = r.readLine();
			line = line.trim();
		}

		String[] st;

		if (line!=null) {
			//Now we have to set the Flag settings
			st = line.trim().split("\t"); 
			if(Boolean.parseBoolean(st[1])){ //If the flag setting is true for Emptyspots
				this.ar.setEmptySpots(true);
				this.imagene.setEmptySpots(true);				
			}
		}

		while(line!=null && !line.startsWith("Poor Spots")){
			line = r.readLine();
			line = line.trim(); 
		}

		if (line!=null) {
			st = line.trim().split("\t"); 

			if(Boolean.parseBoolean(st[1])){	
				this.ar.setPoorSpots(true);
				this.imagene.setPoorSpots(true);			
				//Now we set the settings for the poor spot flag
				parsePoorSpotParameters(r);
			}
		}

		while(line!=null && !line.startsWith("Negative Spots")){
			line = r.readLine();
			line = line.trim();

		}

		if (line!=null) {
			st = line.trim().split("\t");
			if(Boolean.parseBoolean(st[1])){
				this.ar.setNegativeSpots(true);	
				this.imagene.setNegativeSpots(true);

			}
		}
	}

	private void parsePoorSpotParameters(BufferedReader r) throws Exception
	{	
		String line = r.readLine();
		//hier aenderung aufgrund findbugs
		if(line != null) line = line.trim();
		while(line!=null && !line.startsWith("Begin Poor Spots Parameters"))
			line = r.readLine().trim();

		//We get the first line with information
		line = r.readLine().trim();
		String[] split = line.split("\t");
		//If the Background Contamination is to be true
		if(Boolean.parseBoolean(split[1])) this.imagene.setBackContamination(true); 
		//we get the next important line
		line = r.readLine().trim();
		line = r.readLine().trim();
		split = line.split("\t");
		if(Boolean.parseBoolean(split[1])) this.imagene.setSignalContamination(true);
		//We are Ignoring the Signal contamination test connected to background contamination threshold		
		line =r.readLine().trim();
		line = r.readLine().trim();
		split = line.split("\t");
		if(Boolean.parseBoolean(split[1])) this.imagene.setPercentage(true);
		line = r.readLine().trim();
		split = line.split("\t");
		if(Boolean.parseBoolean(split[1])) this.imagene.setOpenPerimeter(true);
		line = r.readLine().trim();
		split = line.split("\t");
		if(Boolean.parseBoolean(split[1])) this.imagene.setShapeRegularity(true);
		line = r.readLine().trim();
		split = line.split("\t");
		if(Boolean.parseBoolean(split[1])) this.imagene.setPerimeterToArea(true);
		line = r.readLine().trim();
		split = line.split("\t");
		if(Boolean.parseBoolean(split[1])) this.imagene.setOffsetFailed(true);
	}

	/**
	 * Function to test if a generated Spot is a poor Spot
	 * @param s This is the line containing the raw data of a spot
	 * @return
	 */
	private boolean isPoorSpot(String s)
	{	//TODO
		boolean poor = false;
		String[] sp = s.trim().split("\t");
		//first test is the background contamination
		//if the background contamination in the flagsettings is true 
		//and the spot is actually contaminated then the spot is according to this criteria poor

		poor = ((imagene.isBackContamination() && (Integer.parseInt(sp[backContaminationOffset]) == 1))
				|| (imagene.isSignalContamination() && (Integer.parseInt(sp[signalContaminationOffset]) ==1))
				|| (imagene.isPercentage() && (Integer.parseInt(sp[percentageOffset]) ==1))
				|| (imagene.isOpenPerimeter() && (Integer.parseInt(sp[perimeterOffset])==1))
				|| (imagene.isShapeRegularity() && (Integer.parseInt(sp[shapeRegularityOffset])==1))
				|| (imagene.isPerimeterToArea() && (Integer.parseInt(sp[areaToPerimeterRatioOffset])==1))
				|| (imagene.isOffsetFailed() && (Integer.parseInt(sp[offsetFlag])==1))
		);
		return poor;
	}



	/** Function to parse the RawData of an ImageneFile
	 * @param r
	 */
	private void parseRawData(BufferedReader r, int choice, int channel) throws Exception
	{
		String line = r.readLine();

		//Testing if choice is valid as an offset
		if(!(choice == ImaGeneDataStructure.sMean || choice==ImaGeneDataStructure.sMedian || choice ==ImaGeneDataStructure.sMode 
				|| choice == ImaGeneDataStructure.sTotal)){
			throw new Exception(choice + " is not valid as input for ImaGeneParser: " +
			"take ImaGeneDataStructure.sMean, sMedian, sMode or sTotal");
		}
		//Search for the start of the Raw Data Section
		while(!line.startsWith("Begin Raw Data"))
		{
			line = r.readLine();
			line = line.trim();
		}
		//getting the first line of real information
		line = r.readLine();
		String[] split =line.trim().split("\t"); // this line is build up by the column names of the table
		//parsing the offsets corresponding to the columnames, this is done for one channel only
		if (channel == 0) {
			parseOffsets(split);
		}
		// Set the offsets values to specifiy the intensity data to be read
		// in
		int offsetF;
		int offsetB;
		if (choice == ImaGeneDataStructure.sMean) {
			offsetF = sMean;
			offsetB = bMean;
		} else if (choice == ImaGeneDataStructure.sMedian) {
			offsetF = sMedian;
			offsetB = bMedian;
		} else if (choice == ImaGeneDataStructure.sMode) {
			offsetF = sMode;
			offsetB = bMode;
		} else {
			offsetF = sTotal;
			offsetB = bTotal;
		}




		line = r.readLine();
		line= line.trim(); //Todo gehört da noch ein Line trim hin? und wiso wird noch mal eine Line eingelesen
		//If we are reading our control file we have to create new Spots
		if(channel==0)
		{
			//			now the spot data is read in, to ensure that the Microarraydatastructure corresponds
			//to the real Array the structure of the imagenedatafile requires these four for-loops
			//first the dimensions of the array are saved
			int metarows = imagene.getMetaRows();
			int metacols = imagene.getMetaCols();
			int rows    = imagene.getRows();
			int cols    = imagene.getCols();

			//Variable to hold the number of flagged spots
			int numberOfFlaggedSpots =0;
			//the filestructure ofimagene gives the spots of one print of the printhead in an continous block
			//first for loop over the metarows of the file
			for(int mr=0; mr < metarows; ++mr ){
				//second loop over the metacols of the file
				for(int mc=0; mc < metacols;++mc){
					//third loop over the rows of the printhead print
					for(int rw =0; rw<rows; ++rw){
						//fourth loop over the cols of the printhead print

						for(int c =0; c<cols;++c){

							if(line!=null && !line.endsWith("End Raw Data")){
								split = line.trim().split("\t");
								String GeneID = split[geneId];

								double fground = Double.parseDouble(split[offsetF]);
								double bground = Double.parseDouble(split[offsetB]); 

								//is the Spot marked as empty negative or poor?
								boolean emptySpot = false;
								boolean negativeSpot = false;
								if (imagene.isEmptySpots() && Integer.parseInt(split[emptySpotOffset]) == 1) {
									emptySpot = true;
								}
								if (imagene.isNegativeSpots() && Integer.parseInt(split[negativSpotOffset]) ==1) {
									negativeSpot = true;

								}
								//Test if the spot is a poor one
								boolean poorSpot = false;
								if(imagene.isPoorSpots()){
									poorSpot = isPoorSpot(line);
								}


								//calculating of the spot coordinates
								int i = mr*rows+rw;
								int j = mc*cols+c;
								ar.setSpotG(i,j,bground, fground, poorSpot, negativeSpot, emptySpot, GeneID);
								if(poorSpot || negativeSpot || emptySpot) numberOfFlaggedSpots +=1;

								//we get the next line
								line =r.readLine();			

							}//end of If

						}//fourth for
					}//third for
				}//second for
			}//first for
			ar.setNumberOfFlaggedSpots(numberOfFlaggedSpots);
		}
		else
		{	//we are working with the second file, we already have the information of the first file
			//			now the spot data for the experiment file is read in, to ensure that the Microarraydatastructure corresponds
			//to the real Array the structure of the imagenedatafile requires these four for-loops
			//first the dimensions of the array are saved

			int metarows = imagene.getMetaRows();
			int metacols = imagene.getMetaCols();
			int rows    = imagene.getRows();
			int cols    = imagene.getCols();
			//the filestructure ofimagene gives the spots of one print of the printhead in an continous block
			//first for loop over the metarows of the file
			for(int mr=0; mr < metarows; ++mr )
			{
				//second loop over the metacols of the file
				for(int mc=0; mc < metacols;++mc)
				{
					//third loop over the rows of the printhead print
					for(int rw =0; rw<rows; ++rw)
					{
						//fourth loop over the cols of the printhead print
						for(int c =0; c<cols;++c)
						{
							if(line!=null && !line.endsWith("End Raw Data"))
							{

								split = line.trim().split("\t");
								String GeneID = split[geneId];
								//calculating of the spot coordinates
								int i = mr*rows+rw;
								int j = mc*cols+c;
								if(GeneID.equals(ar.getGeneID(i, j)))
								{
									double fground = Double.parseDouble(split[offsetF]);
									double bground = Double.parseDouble(split[offsetB]);

									//Add the foreground and background values of our experiment 
									//to the corresponding spot
									ar.setSpotR(i,j,bground, fground);

								}
								else throw new Exception("End Raw Data is missing. Corrupted File.");								
								//we are getting the next line in the file
								line = r.readLine();
							}//if  loop						
						}//fourth for
					}//third for
				}//second for
			}//first for

		}//else block


	}

	/**
	 * Function to parse the offsets by the given columnames
	 * @param columnames
	 */
	private void parseOffsets(String[] columnames) throws Exception{

		for(int i=0; i < columnames.length; ++i){
			if(columnames[i].equals("Gene ID")) geneId = i;
			else if(columnames[i].equals("Signal Mean")) sMean = i;
			else if(columnames[i].equals("Background Mean")) bMean = i;
			else if(columnames[i].equals("Signal Median")) sMedian=i;
			else if(columnames[i].equals("Background Median")) bMedian=i;
			else if(columnames[i].equals("Signal Total")) sTotal=i;
			else if(columnames[i].equals("Background Total")) bTotal=i;
			else if(columnames[i].equals("Signal Mode")) sMode =i;
			else if(columnames[i].equals("Background Mode")) bMode=i;
			else if(columnames[i].equals("Empty spot")) emptySpotOffset=i;
			else if(columnames[i].equals("Negative spot")) negativSpotOffset=i;
			else if(columnames[i].equals("Background contamination present")) backContaminationOffset =i;
			else if(columnames[i].equals("Signal contamination present")) signalContaminationOffset=i;
			else if(columnames[i].equals("Ignored % failed")) percentageOffset =i;
			else if(columnames[i].equals("Open perimeter failed")) perimeterOffset =i;
			else if(columnames[i].equals("Shape regularity failed")) shapeRegularityOffset =i;
			else if(columnames[i].equals("Perim-to-area failed")) areaToPerimeterRatioOffset =i;
			else if(columnames[i].equals("Offset failed")) offsetFlag =i;


		}
		//Are all necessary Fields present?
		boolean isValid = false;		

		if((geneId != -1) && (sMean != -1) && (bMean != -1) && (sMedian != -1) 
				&& (bMedian != -1) && (sTotal != -1) && (bTotal != -1) && (emptySpotOffset != -1)
				&& (negativSpotOffset != -1) && (backContaminationOffset != -1) 
				&& (signalContaminationOffset != -1) && (percentageOffset != -1) && (perimeterOffset != -1)
				&& (shapeRegularityOffset != -1) && (areaToPerimeterRatioOffset != -1) && (offsetFlag != -1))
		{
			if((choice != ImaGeneDataStructure.sMode) || (sMode != -1)) isValid = true;
			else{
				throw new Exception("There is no mode intensity value present in the data file. Please select the mean, median or total intensity values for this data files.");
			}
		}
		else{
			throw new Exception("Error while parsing the data files. This parser was tested with the file versions 5.5 and 5.6.1.  Others may work but are currently not supported.");
		}	
		if (!isValid)
			System.out.println("Format is not valid");
	}

	public static void main(String[] args){
		ArrayList<String> test = new ArrayList<String>();
		System.out.println(args[0]);
		System.out.println(args[1]);
		test.add(args[0]);
		test.add(args[1]);
		ImaGeneParser tut = new ImaGeneParser(test,ImaGeneDataStructure.sMode);

		try{
			tut.parseArray();
		}
		catch(Exception e){

			System.out.println("Error: " + e.getMessage());
		}


		System.out.println("Test of our array structure");
		System.out.println("the first element");
		System.out.println(tut.ar.toString(0, 0));
		System.out.println("the second element");
		System.out.println(tut.ar.toString(0,1));
		System.out.println("the last element in the row");
		System.out.println(tut.ar.toString(0,tut.ar.getWidth()-1));
		System.out.println(tut.ar.toString(1,tut.ar.getWidth()-1));

	}

	public ImaGeneArray getAr() {
		return ar;
	}

	public ImaGeneDataStructure getStructure() {
		return imagene;
	}

}
