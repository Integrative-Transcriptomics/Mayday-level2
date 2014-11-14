package mayday.motifsearch.exec;

import java.io.*;

/**
 * This to execute commands with a process builder
 * 
 * @author Frederik Weber
 */

public final class CommandExecution {

    String  LastCommandlinesFormExecution;
    Process process;

    /**
     * executes a command with a process builder
     * 
     * @param command
     * 		the command line and all parameters/arguments to execute in one String
     */
    public final int execute(String command){

	String linesReturned = new String(); 

	int exitVal = 1;

	try{
	    /*split command in arguments and run*/
	    this.process = new ProcessBuilder(command.split("(\\s)+")).redirectErrorStream(true).start();
	    System.out.println("started Process with command:\'"+command+"\'" );

	    InputStream is = this.process.getInputStream();
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader ibr = new BufferedReader(isr);

	    /*read the output of executed command*/
	    String l;
	    while ((l = ibr.readLine()) != null) {
		linesReturned = linesReturned.concat(l);
		if (linesReturned.length()>5000){
		    linesReturned = "";
		}

	    }

	    exitVal = this.process.waitFor();
	    System.out.println("Exited process with command:\n  '"+ command + "'\n  with exit value code "+exitVal);

	    this.LastCommandlinesFormExecution = linesReturned;


	}
	catch ( Exception ioe ) {
	    System.err.println( "IO error: " + ioe + " with command: "+ command);
	    exitVal = 1;
	}

	return exitVal;
    }

    public CommandExecution() {
	super();
	LastCommandlinesFormExecution = "";
    }

    /**
     * executes a command with a process builder
     * 
     * @return String of the last returned Lines from last call of execute()
     */
    public String getLastCommandlinesFormExecution() {
	return LastCommandlinesFormExecution;
    }

    public void doCancel(){
	if (this.process != null){
	    this.process.destroy();
	}
    }

}
