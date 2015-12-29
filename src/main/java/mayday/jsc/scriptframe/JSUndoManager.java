package mayday.jsc.scriptframe;

import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * UndoManager which does *not* take highlighting as single action
 *
 * @author Tobias Ries
 * @version 1.0
 */
@SuppressWarnings("serial")
public class JSUndoManager extends UndoManager
						implements UndoableEditListener
{
	private JTextComponent textComponent;


	public JSUndoManager(JTextComponent textComponent)
	{
		this.textComponent = textComponent;		
		textComponent.getDocument().addUndoableEditListener( this );	
	}


	public void undoableEditHappened(UndoableEditEvent e)
	{
		DefaultDocumentEvent event = (DefaultDocumentEvent)e.getEdit();
		
		if(super.edits.size() == 0)
		{
			if(event.getType().equals(DocumentEvent.EventType.CHANGE))//Change implies highlighting
				return;
			
			CompoundEdit ce = new CompoundEdit();
			ce.addEdit( e.getEdit() );	
			super.addEdit(ce);
			return;
		}
						
		//Unite Highlighting Actions with previous actions
		if(event.getType().equals(DocumentEvent.EventType.CHANGE))
		{
			((CompoundEdit)super.edits.lastElement()).addEdit( e.getEdit() );				
			return;
		}
		
		((CompoundEdit)super.edits.lastElement()).end();		
		CompoundEdit ce = new CompoundEdit();
		ce.addEdit( e.getEdit() );	
		super.addEdit(ce);
	}		
	
	public UndoableEdit endCurrentEdit()
	{
		if(super.edits.isEmpty())
			return null;
		
		((CompoundEdit)super.edits.lastElement()).end();						
		return super.editToBeUndone();
	}
	
	public boolean isCurrent(UndoableEdit e)
	{
		return super.editToBeUndone() == e;
	}
	
	public UndoableEdit getCurrent()
	{
		return super.editToBeUndone();
	}

	
	public void undo()
	{	
		if(!super.edits.isEmpty())
			((CompoundEdit)super.edits.lastElement()).end();		
		if ( super.canUndo() )
		{
			super.undo();		
			this.textComponent.updateUI();//Makes sure line-numbers do not disappear
			this.textComponent.requestFocus();			
		}				
	}
	public void redo()
	{		
		if ( super.canRedo() )
		{
			super.redo();
			this.textComponent.updateUI();//Makes sure line-numbers do not disappear
			this.textComponent.requestFocus();			
		}			
	}
	

}
