package mayday.jsc.snippets;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import mayday.core.structures.StringListModel;
import mayday.mushell.dispatch.DispatchEvent;
import mayday.mushell.dispatch.DispatchListener;
import mayday.mushell.snippet.AbstractSnippet;
import mayday.mushell.snippet.SnippetEvent;

/** 
 * Displays all defined variables and their classes+values 
 *
 * @version 1.0
 * @author Tobias Ries, ries@exean.net
 */
public class JSDefinitions  extends AbstractSnippet implements DispatchListener
{
	private JScrollPane jsp;
	private ScriptEngine engine; 
	private StringListModel objectsAndFunctions;
	private JTable definitionTable;
	private ArrayList<Object[]> definitions;	
	
	public JSDefinitions(ScriptEngine scriptEngine)
	{		
		this.engine = scriptEngine;
		
		this.definitions = new ArrayList<Object[]>();
		
		this.definitionTable = new JTable( new JSDefTableModel() );
		this.definitionTable.setAutoCreateRowSorter(true);
		this.definitionTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton()==MouseEvent.BUTTON3) {
					int rowId = definitionTable.rowAtPoint(e.getPoint());
					rowId = definitionTable.getRowSorter().convertRowIndexToModel(rowId);					
					String def = definitionTable.getModel().getValueAt(rowId,0).toString();					
					eventfirer.fireEvent(new SnippetEvent(JSDefinitions.this,def,false));					
				}
			}
		});
		this.definitionTable.setRowSelectionAllowed(false);		
				
		this.jsp = new JScrollPane( this.definitionTable );
		this.jsp.setName( "Definitions" );
		
		this.update();		
	}
	
	public JComponent getComponent() 
	{
		return this.jsp;
	}
	
	/* 
	 * fill gui with information from scriptengine
	 */
	private void fill()
	{					
		Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);		
				
		for(String s : bindings.keySet())
		{
			Object bind = bindings.get(s);
			Object[] entry = new Object[]{ s, bind, bind.getClass() };			
			this.definitions.add(entry);
		}

	}
	
	public StringListModel getFullModel() {
		return this.objectsAndFunctions;
	}		
	

	public void update()
	{
		this.definitions.clear();
		this.fill();		
		this.definitionTable.getRowSorter().allRowsChanged();
		this.definitionTable.updateUI();
	}
	
	/* 
	 * update value of a variable
	 */
	private void changeBinding(Object value, int row, int col)
	{
		Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		Object key = this.definitions.get(row)[0];
		if(col == 0)
		{			
			Object originalValue = bindings.get( key );
			bindings.put(value.toString(), originalValue);
			bindings.remove( key );
		}
		else if(col == 1)
		{
			try
			{
				bindings.put(key.toString(), Double.parseDouble((String) value));
			}
			catch(Exception e)
			{
				bindings.put(key.toString(), value);
			}			
		}
		
		this.update();
	}

	@SuppressWarnings("serial")
	class JSDefTableModel extends AbstractTableModel
	{
		private String[] columnNames = new String[] {"Name","Value","Type"};		

		public int getColumnCount()
		{
			return columnNames.length;
		}

		public int getRowCount()
		{
			return definitions.size();
		}

		public String getColumnName(int col)
		{
			return columnNames[col];			
		}

		public Object getValueAt(int row, int col)
		{
			if(col == columnNames.length - 1)//ClassName
			{
				Class<?> c = ((Class<?>)definitions.get(row)[col]);
				String result = "";
				if(c != null)
				{									
					try {
					if(c.getSimpleName().equals("NativeJavaObject"))						
							c = ((Class<?>)engine.eval(definitions.get(row)[0]+".getClass()"));
					} catch (ScriptException e) {
						e.printStackTrace();
					}
					
					String className = c.getSimpleName();
					String paketName = "";
					if(c.getPackage() != null)
						paketName = " (" + c.getPackage().getName() + ")";
					
					
					//JavaScript Types 	
					try {
						if(c.equals(Double.class) || c.equals(Integer.class)
								&& engine.eval("typeof "+definitions.get(row)[0]).equals("number"))
						{
							className = "Number";
							paketName = "";
						}
						else if(c.equals(String.class)
								&& engine.eval("typeof "+definitions.get(row)[0]).equals("string"))
						{
							className = "String";
							paketName = "";
						}

						result += className;					
						result += paketName;
					} catch (ScriptException e) {
						e.printStackTrace();
					}
					//JavaScript Types - EOF
					
				}
				return result;
			}
				
			return definitions.get(row)[col].toString();//Avoids crashing the autoCreated rowSorter
		}

		public Class<?> getColumnClass(int c)
		{
			return String.class;//getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col)
		{
			if(col == columnNames.length - 1)//type can never be changed
				return false;
			
			Class<?> type = (Class<?>)definitions.get(row)[columnNames.length-1];		
			return type.equals(String.class)
					|| type.equals(Double.class)
					|| type.equals(Integer.class);
		}

		public void setValueAt(Object value, int row, int col)
		{
			if(value != null)
				changeBinding(value, row, col);
		}
	}
	
	//Border same as border around input field
	public void setEnabled(boolean enabled)
	{
		this.definitionTable.setEnabled(enabled);
		if(!enabled)
			this.jsp.setBorder(BorderFactory.createLineBorder( Color.red, 2));			
		else
			this.jsp.setBorder(BorderFactory.createLineBorder( Color.green, 2));
	}

	@Override
	public void dispatching(DispatchEvent evt)
	{
		if(evt.getWhen() == DispatchEvent.event.NOW_READY)
		{			
			this.update();
			this.setEnabled(true);
		}
		else
			this.setEnabled(false);					
	}


}
