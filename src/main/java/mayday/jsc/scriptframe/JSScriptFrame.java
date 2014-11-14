package mayday.jsc.scriptframe;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.undo.UndoableEdit;

import mayday.core.gui.MaydayFrame;
import mayday.jsc.autocomplete.JSChoiceChooser;
import mayday.jsc.tokenize.JSTokenizer;
import mayday.mushell.Console;
import mayday.mushell.InputField;
import mayday.mushell.autocomplete.AutoCompleter;
import mayday.mushell.autocomplete.DefaultChoiceChooser;
import mayday.mushell.tokenize.Tokenizer;


/** 
 * A simple editor for scripts. Basically offers the same features as the
 * input-field of the console plus line-numbering, auto-indent, redo/undo
 * and execution of only parts of the code as well as all of it.  
 *
 * @version 1.0
 * @author Tobias Ries, mail@exean.net
 */
@SuppressWarnings("serial")
public class JSScriptFrame extends MaydayFrame implements DocumentListener
{
	/** The ScriptFrame is entirely based on the input-field */
	protected InputField inputField;
	/** Is the current file saved? */
	private boolean isSaved;
	/** Currently opened file */
	private File file;
	/** Console to which this ScriptFrame belongs (multiple consoles maybe open at the same time) */
	private Console console;
	/** The key(s) used to invoke auto completion. */
	private int completionKey;
	private AutoCompleter autoCompleter;
	/** Own undomanager, as normal um would undo syntax-highlighting */
	private JSUndoManager undomanager;
	private UndoableEdit lastSavedEdit;	
	private DefaultChoiceChooser choiceChooser;
	private boolean completionInvoked;

	public JSScriptFrame(Console c, AutoCompleter completer)
	{
		this.console = c;
		this.autoCompleter = completer;						
	}

	public JSScriptFrame(Console c, AutoCompleter completer, File fil)
	{
		this.console = c;
		this.autoCompleter = completer;			
		this.file = fil;				
	}

	public void setVisible(boolean vis)
	{
		init();		
		super.setSize(500,400);
		super.setVisible(vis);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				inputField.requestFocusInWindow();		
			}
		});
	}

	public void setTokenizer(Tokenizer t)
	{
		this.inputField.setTokenizer(t);
	}

	
    /** 
     * Closes this script-frame after saving-dialog 
     *
     * @version 1.0
     * @return Did the script-frame really close?
     */
	public boolean close()
	{
		if(!this.isSaved && !this.inputField.getText().isEmpty())
		{
			int option = JOptionPane.showConfirmDialog(this,
					"Save '"+this.getScriptName()+"' before closing?",
					"Script not saved:"+this.getScriptName(),				    
					JOptionPane.YES_NO_CANCEL_OPTION);
			if(option == JOptionPane.CANCEL_OPTION)
				return false;
			else if(option == JOptionPane.YES_OPTION)
				if(!this.save(this.file))
					return this.close();//Failed to save, ask again
		}

		setVisible(false);
		dispose();

		return true;
	}

	
    /** 
     * Allows to select a file and saves current script to that file 
     *
     * @version 1.0
     * @return Was saving successful?
     */
	protected boolean save()
	{
		JFileChooser j = new JFileChooser(this.file);
		if(j.showSaveDialog(this) == JFileChooser.APPROVE_OPTION
				&& (!j.getSelectedFile().exists()
						|| JOptionPane.showConfirmDialog(this,
								"Overwrite existing file?",
								"Confirm Overwriting",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION))
			return this.save(j.getSelectedFile());

		return false;
	}

	
    /** 
     * Saves current script to a given file
     *
     * @version 1.0
     * @param f File to which current script shall be saved
     * @return Saving successful?
     */
	protected boolean save(File f)
	{
		if( f == null )
			return this.save();
		else
		{
			BufferedWriter w = null;
			try {
				w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
				w.write(inputField.getText());
				w.flush(); 
				w.close(); 
			} catch (Exception e)
			{
				if(w != null)
					try//Make sure writer is closed
				{
						w.close();
				} catch (IOException e1) {}
				JOptionPane.showConfirmDialog(this,
						"Failed to save script:\n"+e.getCause(),
						"Saving Failed",
						JOptionPane.OK_OPTION,
						JOptionPane.ERROR_MESSAGE);
				return false;
			};
			this.lastSavedEdit = this.undomanager.endCurrentEdit();
			this.isSaved = true;
			this.file = f;
			this.updateTitle();
			return true;
		}
	}

	
    /** 
     * Checks wether current script was saved, then offers
     * to choose a file for opening, tries to open it 
     *
     * @version 1.0
     * @return Opening successful?
     */
	public boolean open()
	{
		if(!this.isSaved)
		{
			int d = JOptionPane.showConfirmDialog(this,		
					"Save current script?",
					"Save script?",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if(d == JOptionPane.YES_OPTION
					&& !this.save(this.file))
				return false;			
			else if(d == JOptionPane.CANCEL_OPTION)
				return false;				
		}
		JFileChooser j = new JFileChooser(this.file);
		if(j.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			return this.open(j.getSelectedFile());

		return false;
	}

	
    /** 
     * Opens a given file. Also resets fields. 
     *
     * @version 1.0
     * @param file File to read
     * @return Was opening successful?
     */
	public boolean open(File file)
	{				
		BufferedReader br = null;
		String code = "";
		try {			
			br = new BufferedReader(new FileReader(file));
			while (br.ready())
				code += br.readLine()+"\n";
			br.close();
		} catch (Exception e)
		{
			try
			{//Make sure reader is closed
				if(br != null)
					br.close();
			} catch (IOException e1) {}
			JOptionPane.showConfirmDialog(this,
					"Failed to open script:\n"+e.getMessage(),
					"Loading Failed",
					JOptionPane.OK_OPTION,
					JOptionPane.ERROR_MESSAGE);
			return false;
		}		
		this.inputField.replaceContent(code);

		this.undomanager.discardAllEdits();//New File new game... you just lost it		
		this.lastSavedEdit = this.undomanager.getCurrent();

		this.file = file;
		this.isSaved = true;
		this.updateTitle();

		return true; 
	}

	
    /** 
     * Initiates Scriptframe and fields. 
     *
     * @version 1.0
     */
	private void init()	
	{
		super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);					
		this.completionKey = KeyEvent.VK_TAB;		
		this.inputField = new InputField(this.console);		

		Document d = this.inputField.getDocument();	
		JSNumberedEditorKit numbers = new JSNumberedEditorKit();		
		numbers.install(this.inputField);		
		this.inputField.setEditorKit(numbers);
		this.inputField.setDocument(d);

		this.undomanager = new JSUndoManager(this.inputField); 
		this.undomanager.setLimit( 1000 ); 

		d.addDocumentListener(this);
		
		this.inputField.setBorder(numbers.getNumberedBorder());
		
		this.inputField.removeKeyListener(inputField.getKeyListeners()[0]);
		this.inputField.addKeyListener(new InputListener());
		this.inputField.setAutoCompleter(this.autoCompleter);
		this.inputField.setTokenizer(JSTokenizer.getInstance());			
		this.inputField.setChoiceChooser(choiceChooser = JSChoiceChooser.getChoiceChooser(this.inputField));		
		this.inputField.getActionMap().put(DefaultEditorKit.insertBreakAction, new JSIndentBreakAction());//Line indent

		JScrollPane inputSP = new JScrollPane(this.inputField);
		inputSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.getContentPane().add(inputSP);

		super.setJMenuBar(JSScriptFrameGUI.getMenuBar(this));	

		if(this.file != null)		
			this.isSaved = this.open(this.file);
		else
		{
			this.lastSavedEdit = this.undomanager.getCurrent();
			this.isSaved = true;
		}

		this.updateTitle();

		pack();
	}

	
    /** 
     * Updates title if file was changes or filename changed 
     *
     * @version 1.0
     */
	private void updateTitle()
	{
		String title = "JavaScript: ";
		if(!this.isSaved)
			title += "*";		
		title += this.getScriptName();		
		super.setTitle(title);
	}

	
    /** 
     * It's not advisable to use the input-fields given InputListener
     * as it clears the text-area when pressing enter. 
     *
     * @version 1.0
     */
	private class InputListener extends KeyAdapter
	{		
		@Override
		public void keyPressed(KeyEvent e) 
		{
			if(e.getKeyCode()==completionKey)
			{					
				try
				{					
					autoComplete();
				
				} catch (BadLocationException e1)
				{
					e1.printStackTrace();
				}							
				e.consume();
				return;
			}
			completionInvoked = false;
		}
	}

	public String getScriptName()
	{
		if(this.file != null)
			return this.file.getName();
		return "Untitled Script";
	}	

	@Override
	public void changedUpdate(DocumentEvent e){}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		isSaved = this.undomanager.isCurrent(this.lastSavedEdit);
		updateTitle();
	}
	@Override
	public void removeUpdate(DocumentEvent e)
	{
		isSaved = this.undomanager.isCurrent(this.lastSavedEdit);
		updateTitle();
	}

	public JSUndoManager getUndomanager()
	{		
		return this.undomanager;
	}

	public File getFile()
	{
		return this.file;
	}

	protected void executeAll()
	{
		this.console.dispatch(inputField.getText());		
	}

	
    /** 
     * Only executes selected commands 
     *
     * @version 1.0
     */
	protected void executeSelected()
	{
		String code = inputField.getSelectedText();
		if(code != null)
			console.dispatch(code);	
	}	
	
	public void autoComplete() throws BadLocationException
	{
		if(completionInvoked)
		{
			this.inputField.allCompletions();
			this.choiceChooser.setInvoker(this);
			completionInvoked=false;
		}else
		{
			this.inputField.singleCompletion();
			completionInvoked=true;
		}		
	}

}