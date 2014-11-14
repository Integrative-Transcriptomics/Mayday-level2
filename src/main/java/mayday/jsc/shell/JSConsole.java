package mayday.jsc.shell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import mayday.core.datasetmanager.DataSetManager;
import mayday.core.settings.Settings;
import mayday.core.settings.typed.FilesSetting;
import mayday.jsc.adjustableBehaviour.JSOverloadingOperators;
import mayday.jsc.adjustableBehaviour.JSReplacements;
import mayday.jsc.autocomplete.JSAutoCompleter;
import mayday.jsc.autocomplete.JSChoiceChooser;
import mayday.jsc.recognition.JSCommandRecognizer;
import mayday.jsc.scriptframe.JSScriptFrame;
import mayday.jsc.snippets.JSCommandQueue;
import mayday.jsc.snippets.JSDataSetManager;
import mayday.jsc.snippets.JSDefinitions;
import mayday.jsc.snippets.JSOperators;
import mayday.jsc.tokenize.JSTokenizer;
import mayday.mushell.Console;
import mayday.mushell.InputField;

/** 
 * Main Class of JSC, based on mayday's Console 
 *
 * @version 1.0
 * @author Tobias Ries, ries@yuricon.de
 */
@SuppressWarnings("serial")
public class JSConsole extends Console
{	
	private JSDispatcher jsDispatcher;
	private JSReplacements replacer;
	private JSOverloadingOperators overlOps;
	protected InputField inputField;
	private ArrayList<JSScriptFrame> scriptFrames;
	private winAdapter windowListener;
	private JSSettings settings;
	private JSDefinitions snippetDefinitions;

	public JSConsole( String title )
	{
		super(title);		
		
		super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);	
		this.windowListener = new winAdapter();//For checking ScriptFrame before exiting
		super.addWindowListener(this.windowListener);
		
		this.settings = JSSettings.getInstance();
	
		this.initInputField();
		
		this.scriptFrames = new ArrayList<JSScriptFrame>();

		this.replacer = new JSReplacements();							

		motd();
		//UN addWindowListener(new CloseListener());
	}		
		
	public void init()
	{
		super.init();
		this.initEngineSpecifics();
		this.initSnippets();		
		this.initMenuBar();	
		this.pack();
	}
	
	/*
	 * Init ScriptEngine includes running startup scripts and creating
	 * overloading-Ops and Dispatcher
	 */
	private void initEngineSpecifics()
	{
		//Try to initiate engine
		try
		{
			ScriptEngine engine = JSConsole.initEngine();
			JSCommandRecognizer cmdRecognizer = new JSCommandRecognizer( engine );
			this.overlOps = new JSOverloadingOperators( cmdRecognizer.getClassRecognizer() );		
			this.jsDispatcher = new JSDispatcher( engine, this.replacer, overlOps, cmdRecognizer );
			
			//eval startup scripts
			Settings set = this.settings.getSettings();
			if(set.getChild("StartUp-Scripts",false) != null)
			{
				List<String> filenames = ((FilesSetting)set.getChild("StartUp-Scripts",false)).getFileNames();
				for(String s : filenames)
					this.jsDispatcher.evalFile(s);
			}
			//eval startup scripts				
			
			super.setDispatcher( jsDispatcher );
									
			super.inputField.setAutoCompleter( new JSAutoCompleter( engine,
					this.replacer,
					cmdRecognizer,
					overlOps) );
		
		} catch (Exception e)
		{ 
			e.printStackTrace();
		}   	
	}
	
	public void initInputField()
	{
		this.inputField = new InputField(this);
		this.inputField.setChoiceChooser(JSChoiceChooser.getChoiceChooser(this.inputField));
		this.inputField.setTokenizer(JSTokenizer.getInstance());
		super.inputField = inputField;	
	}
	
	private void initMenuBar()
	{
		JMenuBar menuBar = super.getJMenuBar();
		
		JMenu scriptMenu = new JMenu("Scripts");
		scriptMenu.setMnemonic(KeyEvent.VK_S);
		
		JMenuItem newScriptItem = new JMenuItem("New Script", KeyEvent.VK_N);
		newScriptItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getNewScriptFrame();
			}			
		});
		scriptMenu.add(newScriptItem);
		
		JMenuItem openScriptItem = new JMenuItem("Open Script", KeyEvent.VK_O);	
		openScriptItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadNewScriptFrame();			
			}			
		});
		scriptMenu.add(openScriptItem);
		
		menuBar.add(scriptMenu);
		
		JMenu operatorMenu = new JMenu("Operators");
		operatorMenu.setMnemonic(KeyEvent.VK_O);
		
		JMenuItem importOpItem = new JMenuItem("Import Operators", KeyEvent.VK_I);
		importOpItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectAndEval();
			}			
		});
		operatorMenu.add(importOpItem);
		
		JMenuItem exportOpItem = new JMenuItem("Export Operators", KeyEvent.VK_E);	
		exportOpItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser f = new JFileChooser();
				if(f.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
					overlOps.export(f.getSelectedFile());			
			}			
		});
		operatorMenu.add(exportOpItem);
		
		menuBar.add(operatorMenu);
		
		menuBar.add(this.settings.getSettings().getMenu(this));
		
		super.setJMenuBar(menuBar);
	}
	
	
	/**
	 * Initializes snippets and adds them to the gui
	 *
	 * @version 1.0	 
	 */
	private void initSnippets()
	{
		this.snippetDefinitions = new JSDefinitions( jsDispatcher.getScriptEngine() );
		jsDispatcher.addDispatchListener( snippetDefinitions );
		JSCommandQueue snippetCommandQueue = new JSCommandQueue(super.queue, jsDispatcher);
		jsDispatcher.addDispatchListener( snippetCommandQueue );
		super.addSnippetField( new JSDataSetManager() );		
		super.addSnippetField( snippetDefinitions );
		super.addSnippetField( snippetCommandQueue );
		super.addSnippetField( new JSOperators(this.overlOps) );	
		
	}
	
	
	/**
	 * Tries to create a new JavaScript-ScriptEngine, also adds a DataSetManager-Instance to the engine-scope
	 *
	 * @version 1.0
	 * @throws Exception if Engine-Creation failed
	 * @return New ScriptEngine
	 */
	//Try to initiate scriptEngine 
	public static ScriptEngine initEngine() throws Exception
	{
		ScriptEngine engine;
		// create a script engine manager
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a JavaScript engine
        engine = factory.getEngineByName("JavaScript");
        
        //Add DataSetManager-Object to engine
		engine.put("DataSetMgrInstance", DataSetManager.singleInstance);
		
		/* NOTE:
		 * engine-specific commands (e.g. print)
		 * are not available in Engine Scope Bindings (used by 
		 * JSAutoCompleter) before first input
		 */
		
		return engine;				
	}

	private boolean selectAndEval()
	{
		JFileChooser f = new JFileChooser();
		if(f.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			try {
				this.jsDispatcher.evalFile(f.getSelectedFile().getAbsolutePath());
			} catch (ScriptException e) {
				JOptionPane.showConfirmDialog(this,
						"Failed to open file:\n"+e.getMessage(),
						"Loading Failed",
						JOptionPane.OK_OPTION,
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Handles brutally killing innocent threads and executing commands
	 */
	public synchronized void dispatch(final String command)
	{
		if(command.equals("kill"))
			this.jsDispatcher.kill();
		else if(!command.equals("kill"))
			super.dispatch(command);
	}

	private void motd()
	{
		try
		{
			outputField.print(ToolBox.readFile("mayday/jsc/shell/motd.txt"));
		} catch (IOException e)
		{
			outputField.print("Welcome to Mayday JSC!\n");
		}			
	}	

	/**
	 * Removes all commands from queue and kills current evaluation.
	 *
	 * @version 1.0
	 */
	public void killThemAll()
	{
		super.queue.removeAllElements();
		this.jsDispatcher.kill();
	}

	private JSScriptFrame getNewScriptFrame()
	{
		JSScriptFrame f = new JSScriptFrame(this, super.inputField.getAutoCompleter());
		f.addWindowListener(this.windowListener);
		this.scriptFrames.add(f);
		f.setVisible(true);
		return f;
	}
	
	private void loadNewScriptFrame()
	{
		JFileChooser j = new JFileChooser();
		if(j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			getNewScriptFrame().open(j.getSelectedFile());
		}		
	}

	/*
	 * Check all corresponding scriptframes before
	 * closing
	 */
	class winAdapter extends WindowAdapter
	{	
		@Override
		public void windowClosing(WindowEvent w)
		{
			Object window = w.getSource();			
			if(scriptFrames.contains(window))
			{
				if(((JSScriptFrame)window).close())
					scriptFrames.remove(window);			
			}
			else
			{					
				for(JSScriptFrame f : scriptFrames)
				{
					if(!f.close())				
						return;
					else
						scriptFrames.remove(window);
				}
				
				killThemAll();//Stop all evaluations			
				setVisible(false);
				dispose();
			}
		}
	}
}
