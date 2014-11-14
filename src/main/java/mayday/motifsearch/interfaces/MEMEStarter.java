package mayday.motifsearch.interfaces;

import java.util.ArrayList;

import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;


public class MEMEStarter implements IMotifSearchAlgoStarter{
    
    @Override
	public String toString() {
	return "MEME_4_x";
    }
    
    protected String name = "MEME Multiple EM for Motif Elicitation";
    protected String authors = "Timothy L. Bailey and Charles Elkan";
    protected String homepage = "http://meme.sdsc.edu";
    protected String manualLink = "http://meme.sdsc.edu/meme/doc/meme.html";
    protected String execLocation = "meme";

    protected Arguments arguments;
    
    
    public String getExecCommand(){
	
	this.arguments.updateParameterFromEditableSettings();
	
	return
		arguments.getParameterStringByArgumentName("execLocation") +
		arguments.getParameterStringByArgumentName("inputFASTAPath") +
		arguments.getParameterStringByArgumentName("outputFolderPath") +
		arguments.getParameterStringByArgumentName("DNA") +
		arguments.getParameterStringByArgumentName("distribution") +
		arguments.getParameterStringByArgumentName("forcePalindromes") +
		arguments.getParameterStringByArgumentName("minMotifLength") +
		arguments.getParameterStringByArgumentName("maxMotifLength") +
		arguments.getParameterStringByArgumentName("numberOfMotifsToSearchFor") +
//		arguments.getParameterStringByArgumentName("maxEvalue") +
		arguments.getParameterStringByArgumentName("maxSequenceLength") +
		arguments.getParameterStringByArgumentName("nostatus");
    }

	public MEMEStarter() {
	    this.arguments = new Arguments();
	    this.addArgument(new MotifSearchAlgoArgument(
		    "execLocation", 
		    "execution command",
		    "the command that starts the algorithm without his arguments or parameter",
		    MotifSearchAlgoArgument.TYPE_PATH_ARGUMENT, 
		    "",
		    new String[]{this.execLocation},
		    true));
	    this.addArgument(new MotifSearchAlgoArgument(
		    "inputFASTAPath", 
		    "input FASTA file path",
		    "the path to the FASTA file containing the sequences",
		    MotifSearchAlgoArgument.TYPE_PATH_ARGUMENT, 
		    " ",
		    new String[]{this.getStandardOutputFolderPath()},
		    true));
	    this.addArgument(new MotifSearchAlgoArgument(
		    "outputFolderPath", 
		    "output file path",
		    "the path of the folder MEME puts the results of the motif search",
		    MotifSearchAlgoArgument.TYPE_PATH_ARGUMENT, 
		    " -oc ",
		    new String[]{this.getStandardOutputFolderPath()},
		    true));
	    
	    
	    this.addArgument(new MotifSearchAlgoArgument(
		    "minMotifLength", 
		    "minimum motif length",
		    "the minimum length of a motif that should be searchd for",
		    MotifSearchAlgoArgument.TYPE_INTEGER_ARGUMENT, 
		    " -minw ",
		    new String[]{"6"},
		    true));
	    
	    this.addArgument(new MotifSearchAlgoArgument(
		    "maxMotifLength", 
		    "maximum motif length",
		    "the maximum length of a motif that should be searchd for",
		    MotifSearchAlgoArgument.TYPE_INTEGER_ARGUMENT, 
		    " -maxw ",
		    new String[]{"12"},
		    true));
	    
	    this.addArgument(new MotifSearchAlgoArgument(
		    "numberOfMotifsToSearchFor", 
		    "number of motifs to search for",
		    "for how many motifs should be search at maximum",
		    MotifSearchAlgoArgument.TYPE_INTEGER_ARGUMENT, 
		    " -nmotifs ",
		    new String[]{"1"},
		    true));
	    
	    this.addArgument(new MotifSearchAlgoArgument(
		    "maxSequenceLength", 
		    "maximal length of all sequences together",
		    "the length of all sequences concatenated together.\n May not be allowed to be greater than 1000000!",
		    MotifSearchAlgoArgument.TYPE_INTEGER_ARGUMENT, 
		    " -maxsize ",
		    new String[]{"1000000"},
		    true));
	    
	    this.addArgument(new MotifSearchAlgoArgument(
		    "distribution", 
		    "distribution of motifs",
		    "The type of distribution to assume:\n" 
		    + " oops — One Occurrence Per Sequence\n" +
		    		"   MEME assumes that each sequence\n" +
		    		"   in the dataset contains exactly one occurrence of each motif.\n" +
		    		"   This option is the fastest and most sensitive but the motifs returned\n" +
		    		"   by MEME may be \"blurry\" if any of the sequences is missing them.\n" 
		    + " zoops — Zero or One Occurrence Per Sequence\n" +
		    		"   MEME assumes that each sequence\n" +
		    		"   may contain at most one occurrence of each motif.\n" +
		    		"   This option is useful when you suspect that some motifs may be missing\n" +
		    		"   from some of the sequences. In that case, the motifs found will be more\n" +
		    		"   accurate than using the first option. This option takes more computer\n" +
		    		"   time than the first option (about twice as much)\n" +
		    		"   and is slightly less sensitive to weak motifs present in all of the sequences.\n"
		    + " anr — Any Number of Repetitions\n" +
		    		"   MEME assumes each sequence\n " +
		    		"   may contain any number of non-overlapping occurrences of each motif.\n" +
		    		"   This option is useful when you suspect that motifs repeat multiple times\n" +
		    		"   within a single sequence. In that case, the motifs found will be much more\n" +
		    		"   accurate than using one of the other options. This option can also be\n " +
		    		"   used to discover repeats within a single sequence.\n" +
		    		"   This option takes the much more computer time than the first option (about ten times\n" +
		    		"   as much) and is somewhat less sensitive to weak motifs which\n" +
		    		"   do not repeat within a single sequence than the other two options.",
		    MotifSearchAlgoArgument.TYPE_LIST_ARGUMENT, 
		    " -mod ",
		    new String[]{"zoops", "oops", "anr"},
		    true));
	    
	    this.addArgument(new MotifSearchAlgoArgument(
		    "DNA", 
		    "is DNA used as Data",
		    "des",
		    MotifSearchAlgoArgument.TYPE_BOOLEAN_ARGUMENT, 
		    " -dna ",
		    new String[]{"true"},
		    false));
	    
	    this.addArgument(new MotifSearchAlgoArgument(
		    "forcePalindromes", 
		    "force palindromes",
		    "causes MEME to look for palindromes in DNA datasets.",
		    MotifSearchAlgoArgument.TYPE_BOOLEAN_ARGUMENT, 
		    " -pal ",
		    new String[]{"false"},
		    true));
	    
	    this.addArgument(new MotifSearchAlgoArgument(
	    		"backgroundModel", 
	    		"background Markov model file path",
	    		"The name of the file containing the background model for sequences.\n The background model is the model of random sequences used by MEME. \nThe background model is used by MEME"
	    		+" 1. during EM as the \"null model\"\n"
	    		+" 2. for calculating the log likelihood ratio of a motif,\n"
	    		+" 3. for calculating the significance (E-value) of a motif, and,\n"
	    		+" 4. for creating the position-specific scoring matrix (log-odds matrix).",
	    		MotifSearchAlgoArgument.TYPE_PATH_ARGUMENT, 
	    		" -bfile ",
	    		new String[]{""},
	    		false));  
 
	    this.addArgument(new MotifSearchAlgoArgument(
		    "nostatus", 
		    "no status",
		    "do not print progress reports to terminal or system output.",
		    MotifSearchAlgoArgument.TYPE_BOOLEAN_ARGUMENT, 
		    " -nostatus ",
		    new String[]{"true"},
		    false));

	    this.arguments.add(new MotifSearchAlgoArgument(
	    		"maxEvalue", 
	    		"max E-Value",
	    		"stops the search for a motif if motif E-value greater than given value",
	    		MotifSearchAlgoArgument.TYPE_CONSTANT_ARGUMENT, 
	    		" -evt ",
	    		new String[]{"0.1"},
	    		true));   
	}
	
	public void setInputFASTAPath(String inputFASTAPath){
	    this.arguments.getArgumentByName("inputFASTAPath").getSetting().setValueString(inputFASTAPath);
	    this.arguments.getArgumentByName("inputFASTAPath").updateParameterFromSetting();
	}
	
	public void setOutputFolderPath(String outputFolderPath){
	    this.arguments.getArgumentByName("outputFolderPath").getSetting().setValueString(outputFolderPath);
	    this.arguments.getArgumentByName("outputFolderPath").updateParameterFromSetting();
	}

	public String getName() {
	    return name;
	}

	
	public String getAuthors() {
	    return authors;
	}

	
	public String getHomepage() {
	    return homepage;
	}

	
	public String getManualLink() {
	    return manualLink;
	}

	
	public void addArgument(MotifSearchAlgoArgument arg) {
	    this.arguments.add(arg);
	}

	public ArrayList<MotifSearchAlgoArgument> getEditableArguments() {
	    return arguments.getEditable();
	}
	
	public String getStandardOutputFolderPath(){
	    return MaydayDefaults.Prefs.getPluginDirectory() + java.io.File.separator +"MotifSearch"+ java.io.File.separator +"tempAlgo" + java.io.File.separator + this.toString();
	}
	
	public String getActualOutputFolderPath(){
	   return this.arguments.getArgumentByName("outputFolderPath").getSetting().getValueString();
	}


	public IMotifSearchAlgoParser getAlgoParserDummy(){
		return new MEMEParser();
	}
	
	
	public IMotifSearchAlgoParser getAlgoParser(String motifSearchAlgoOutputFolderPath, ProbeList allProbes) throws Exception{
		return new MEMEParser(motifSearchAlgoOutputFolderPath, allProbes);
	}
	
	@Override
	public IMotifSearchAlgoStarter clone(){
		MEMEStarter clone = new MEMEStarter();
		clone.arguments = this.arguments.clone();
		return clone;
	}
	
}
