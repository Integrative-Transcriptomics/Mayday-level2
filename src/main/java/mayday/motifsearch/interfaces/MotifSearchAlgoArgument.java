package mayday.motifsearch.interfaces;
import mayday.core.settings.*;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.settings.typed.StringSetting;

/**
 * class to construct different Arguments for a motif search algorithm
 * 
 */

public class MotifSearchAlgoArgument {

    public static final byte TYPE_INTEGER_ARGUMENT = 0;
    public static final byte TYPE_BOOLEAN_ARGUMENT = 1;
    public static final byte TYPE_PATH_ARGUMENT = 2;
    public static final byte TYPE_LIST_ARGUMENT = 3;
    public static final byte TYPE_CONSTANT_ARGUMENT = 4;

    private String prameterPrefix;
    private String[] parameterList; // this.parameterList[0] is assumed standard value
    private String parameter;
    private String name;
    private String desciption;
    private String shortDesciption;
    private byte type;
    private boolean isEditable;

    protected Setting setting;

    public MotifSearchAlgoArgument(String name, String shortDesciption, String desciption, 
	    byte type, String prameterPrefix, String[] parameterList, boolean isEditable) {
	super();
	this.name = name;
	this.prameterPrefix = prameterPrefix;
	this.parameterList = parameterList;
	this.desciption = desciption;
	this.shortDesciption = shortDesciption;
	this.type= type;
	this.parameter = parameterList[0];
	this.isEditable = isEditable;
	this.createSetting();
    }

    /**
     * creates a setting which is appropriate to set up the parameters of an argument
     * 
     */
    private void createSetting() {
	if (this.type == MotifSearchAlgoArgument.TYPE_INTEGER_ARGUMENT){
	    this.setting = new IntSetting(
		    this.shortDesciption,
		    this.desciption, 
		    Integer.valueOf(this.parameterList[0]));
	}else if (this.type == MotifSearchAlgoArgument.TYPE_BOOLEAN_ARGUMENT){
	    this.setting = new BooleanSetting(
		    this.shortDesciption,
		    this.desciption, 
		    ((this.parameterList[0].toLowerCase() == "true")?true:false));
	}else if (this.type == MotifSearchAlgoArgument.TYPE_PATH_ARGUMENT){
	    this.setting = new PathSetting(
		    this.shortDesciption,
		    this.desciption, 
		    this.parameterList[0], 
		    false,
		    false,
		    true);
	}else if (this.type == MotifSearchAlgoArgument.TYPE_LIST_ARGUMENT){
	    this.setting = new RestrictedStringSetting(
		    this.shortDesciption,
		    this.desciption, 
		    0,
		    this.parameterList);
	}else if (this.type == MotifSearchAlgoArgument.TYPE_CONSTANT_ARGUMENT){
	    this.setting = new StringSetting(
		    this.shortDesciption,
		    this.desciption, 
		    this.parameterList[0]);
	}
    }

    public Setting getSetting() {
	return this.setting;
    }

    /**
     * updates argument by getting the value from its settings 
     * 
     */
    public void updateParameterFromSetting(){
	this.parameter = this.setting.getValueString();    
    }

    /**
     * gets the String of the parameter and its value
     * 
     */
    public String getParameterString(){
	if (this.type == MotifSearchAlgoArgument.TYPE_BOOLEAN_ARGUMENT){
	    if (this.setting.getValueString() == "true"){
		return this.prameterPrefix;
	    }else{
		return "";
	    }

	}else {
	    return this.prameterPrefix + this.parameter;
	}
    }

    public boolean isEditable() {
	return isEditable;
    }


    public String getName() {
	return name;
    }

    @Override
    public MotifSearchAlgoArgument clone(){
	MotifSearchAlgoArgument clone = new MotifSearchAlgoArgument(this.name, this.shortDesciption, this.desciption, this.type, this.prameterPrefix, this.parameterList, this.isEditable);
	clone.setting = this.setting.clone();
	return clone;
    }
}
