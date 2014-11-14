package mayday.jsc.scriptframe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * Generates the JMenuBar used by JSScriptFrame 
 *
 * @author Tobias Ries
 * @version 1.0
 */
public class JSScriptFrameGUI
{
	
	public static JMenuBar getMenuBar(final JSScriptFrame scriptFrame)
	{
		JMenuBar menuBar = new JMenuBar();

		JMenu mFile = new JMenu("File");
		mFile.setMnemonic(KeyEvent.VK_F);

		JMenuItem miOpen = new JMenuItem("Open", KeyEvent.VK_O);
		miOpen.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				scriptFrame.open();				
			}		
		});
		miOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		mFile.add(miOpen);

		mFile.addSeparator();

		JMenuItem miSave = new JMenuItem("Save", KeyEvent.VK_S);
		miSave.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				scriptFrame.save(scriptFrame.getFile());				
			}		
		});
		miSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		mFile.add(miSave);

		JMenuItem miSaveAs = new JMenuItem("Save As...", KeyEvent.VK_A);
		miSaveAs.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				scriptFrame.save();				
			}		
		});
		mFile.add(miSaveAs);		

		mFile.addSeparator();

		JMenuItem miExit = new JMenuItem("Exit", KeyEvent.VK_X);
		miSaveAs.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				scriptFrame.close();				
			}		
		});
		miExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		mFile.add(miExit);		

		menuBar.add(mFile);		

		JMenu mEdit = new JMenu("Edit");
		mFile.setMnemonic(KeyEvent.VK_E);

		JMenuItem miUndo = new JMenuItem("Undo", KeyEvent.VK_U);
		miUndo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				scriptFrame.getUndomanager().undo();				
			}		
		});
		miUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		mEdit.add(miUndo);

		JMenuItem miRedo = new JMenuItem("Redo", KeyEvent.VK_R);
		miRedo.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				scriptFrame.getUndomanager().redo();				
			}		
		});
		miRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		mEdit.add(miRedo);

		menuBar.add(mEdit);

		JMenu mExecute = new JMenu("Execute");
		mExecute.setMnemonic(KeyEvent.VK_E);

		JMenuItem miExecAll = new JMenuItem("All");
		miExecAll.setMnemonic(KeyEvent.VK_A);
		miExecAll.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				scriptFrame.executeAll();				
			}		
		});
		miExecAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		mExecute.add(miExecAll);

		JMenuItem miExecSel = new JMenuItem("Selected");
		miExecSel.setMnemonic(KeyEvent.VK_S);
		miExecSel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				scriptFrame.executeSelected();				
			}					
		});		
		mExecute.add(miExecSel);

		menuBar.add(mExecute);

		return menuBar;
	}

}
