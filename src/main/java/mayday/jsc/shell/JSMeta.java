package mayday.jsc.shell;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import mayday.jsc.adjustableBehaviour.JSReplacements;
import mayday.jsc.recognition.JSCommandRecognizer;
/**
 * Meta-commands like 'HELP' and corresponding methods. 
 *
 * @author Tobias Ries
 * @version 1.0
 */
public class JSMeta
{
	private JSDispatcher dispatch;
	private String helpText;
	private JSCommandRecognizer cmdRecon;
	private JSReplacements replacer;
	
	public JSMeta(JSDispatcher d,
			JSCommandRecognizer cmdRecognizer,
			JSReplacements repl)
	{
		this.dispatch = d;
		this.cmdRecon = cmdRecognizer;
		this.replacer = repl;
	}
	
	/*
	 * Recognize Meta-commands within input and act accordingly
	 */
	protected String executeMetaCommands(String command)
	{
		String result = "";
		
		String[] lines = command.split("\n");
		for(int i = 0; i < lines.length; i++)
		{
			if(lines[i].trim().equals("HELP"))
				this.dispatch.println(this.getHelp());
			else if(lines[i].startsWith("INFO:"))
				this.dispatch.println(this.getInfo(lines[i].substring(5)));
			//Replacements
			else if(lines[i].startsWith("REPLACE:"))
				this.replacer.defineReplacementRule(lines[i].substring(8));
			else if(lines[i].startsWith("?REPLACE"))
				this.dispatch.println(this.getReplacementRules());
			else if(lines[i].startsWith("!REPLACE:"))
				this.replacer.removeRuleFor(lines[i].substring(9));
			//Replacements - EOF
			//Include a file
			else if(lines[i].startsWith("FILE:"))
			{
				String filePath = command.substring(5).trim();
				try
				{
					result += this.executeMetaCommands(ToolBox.readFile(filePath));
				} catch (IOException e1)
				{			
					System.err.print(e1);					
					this.dispatch.println("File not found: "+filePath);;
				}
			}
			//Include a file - EOF
			//Overloaded Ops
			else if(i < lines.length-2
					&& lines[i].startsWith("DEFINE FOR CLASS:")
					&& lines[i+1].startsWith("FOR:")
					&& lines[i+2].startsWith("DO:"))
			{
				try
				{
					this.dispatch.getOverloadingOps().addOperator(
							Arrays.copyOfRange(lines, i, i + 3)
							);
				}
				catch (ClassNotFoundException e)
				{
					this.dispatch.println(e.toString());
				};
				i += 2;
			}
			//Overloaded Ops -EOF
			//Only add line to processed cmd if no meta command present
			else
				result += lines[i]+"\n";
		}
		return result;
	}	
	
	//Print Help
	protected String getHelp()
	{		
		if(this.helpText == null)
			try {
				this.helpText = ToolBox.readFile("mayday/jsc/shell/help.txt");
			} catch (IOException e)
			{			
				this.helpText = "Helpfile not available.";
			}	
		
		return this.helpText+"\n";
	}
	
	//Output List of replacement rules
	protected String getReplacementRules()
	{		
		ArrayList<String[]> rules = this.dispatch.getReplacements().getRules();
		
		String res = rules.size()+" rules are defined.\n\n";
		
		for( String[] s : rules)
			res += s[0] +"=>"+ s[1] + "\n";
		
		return res;
	}
	
	//Generate info-output for specific object
	protected String getInfo(String object)
	{		
		Class<?> theThing = this.cmdRecon.recognizeCommand(object).getRecognizedClass();		
		
		if(theThing == null)
			return "Object not found.\n";
			
			String res = "";
		Class<?>[] interfaces = theThing.getInterfaces();
		Class<?> superclass = theThing.getSuperclass();
		Class<?> componentType = theThing.getComponentType();
		Annotation[] annotations = theThing.getAnnotations();
		Method[] methods = theThing.getMethods();
		Field[] fields = theThing.getFields();

		res += "Class: "+theThing.getName()+"\n\n";

		if(superclass != null)
		{
			res += "Superclass: "+theThing.getSuperclass().getName()+"\n\n";				
		}
		if(interfaces.length > 0)
		{
			res += "Interfaces:\n";			
			for(Class<?> c : interfaces)
				res += " - "+c.getName()+"\n";			
			res += "\n";
		}
		if(componentType != null)
		{
			res += "Component type: "+componentType+"\n\n";							
		}
		if(annotations.length > 0)
		{
			res += "Annotations:\n";
			for(Annotation a : theThing.getAnnotations())
				res += " - "+a.annotationType()+"\n";
			res += "\n";
		}
		if(fields.length > 0)
		{
			res += "Fields:\n";
			for(Field f : fields)		
				res += " - "+f.getName()+" : "+f.getType().getName()+"\n";		
			res += "\n";
		}
		if(methods.length > 0)
		{
			res += "Methods:\n";
			for(Method m : methods)
			{		
				String method = " - "+m.getName()+"(";
				for(Class<?> param : m.getParameterTypes())			
					method += param.getSimpleName()+", ";
				if(method.endsWith(", "))
					method = method.substring(0, method.length()-2);//Delete last ','
				method += ")";
				res += method+" : "+m.getReturnType().getName()+"\n";
			}
			res += "\n";
		}		
		
		return res;
	}
}
