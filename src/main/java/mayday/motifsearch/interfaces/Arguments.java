package mayday.motifsearch.interfaces;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * class that holds a List of arguments for Motif search algorithms
 * 
 * @author Frederik Weber
 * 
 */
public class Arguments {

    protected ConcurrentHashMap<String, MotifSearchAlgoArgument> argumentHashMap;
    protected ArrayList<MotifSearchAlgoArgument> argumentList;

    public Arguments() {
	super();
	this.argumentHashMap = new ConcurrentHashMap<String, MotifSearchAlgoArgument>();
	this.argumentList = new ArrayList<MotifSearchAlgoArgument>();
    }

    /**
     * gets an argument by its name
     * 
     * @param name
     * 		the name of the Argument
     * 
     */
    public MotifSearchAlgoArgument getArgumentByName(String name){
	return argumentHashMap.get(name);
    }

    /**
     * gets an parameter String with its value from an Argument
     * 
     * @param name
     * 		the name of the Argument
     * 
     */
    public String getParameterStringByArgumentName(String name){
	return argumentHashMap.get(name).getParameterString();
    }

    public void add(MotifSearchAlgoArgument arg){
	this.argumentHashMap.put(arg.getName(), arg);
	this.argumentList.add(arg);
    }

    public ArrayList<MotifSearchAlgoArgument> getEditable(){
	ArrayList<MotifSearchAlgoArgument> tempList = new ArrayList<MotifSearchAlgoArgument>();
	for (MotifSearchAlgoArgument argument : argumentList) {
	    if (argument.isEditable()){
		tempList.add(argument);
	    }
	}
	return tempList;
    }

    /**
     * updates all arguments by getting the values from the arguments settings 
     * 
     */
    public void updateParameterFromEditableSettings(){	  
	for (MotifSearchAlgoArgument argument : this.getEditable()) {
	    argument.updateParameterFromSetting();
	}
    }

    @Override
    public Arguments clone(){
	Arguments clone = new Arguments();
	for(MotifSearchAlgoArgument ma: this.argumentList){
	    clone.add(ma.clone());
	}
	return clone;

    }

}

