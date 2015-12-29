/*
 * modified by Guenter Jaeger
 * on May 22, 2010
 * 
 * this class is not needed any longer!
 * 
 */


//additional Code from PointPicker.java

/*
package mayday.expressionmapping.view.interaction;


import javax.media.j3d.*;

import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;

import mayday.expressionmapping.controller.MainFrame;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.vecmath.*;

public class PointSelectBehavior extends PickMouseBehavior {
  
    
    private MainFrame master = null;
    
    private WakeupOnAWTEvent wakeupCriterion;
    
    private int index = -1;
    private int oldindex = -1;
    
    PointArray points = null;

    public PointSelectBehavior(MainFrame master, Canvas3D canvas, BranchGroup root,
			       Bounds bounds) {
      super(canvas, root, bounds);
      this.setSchedulingBounds(bounds);
      
      this.master = master;
      
      pickCanvas.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
      
      this.setTolerance(10.0f);
  
    }
  
  

	@Override
    public void initialize()   {
      
	// Wake up when a mouse button is pressed.
	wakeupCriterion = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
	wakeupOn(wakeupCriterion);
  
    }

    
	@Override
    public void processStimulus(Enumeration criteria)
    {
	while (criteria.hasMoreElements())    {
	    
	    WakeupCriterion wakeup = (WakeupCriterion)criteria.nextElement();
	    
	    if (wakeup instanceof WakeupOnAWTEvent)     {
          
		AWTEvent[] events = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
          
		if (events.length > 0 && events[events.length-1] instanceof MouseEvent)   {
		    
		    MouseEvent event = (MouseEvent)events[events.length-1];

		    int id  = event.getID();
		    int mod = event.getModifiers();

		    // If you wanted to pick only when you press certain mouse buttons
		    // you would use an if statement similar to the following:
		    if (mod == (InputEvent.BUTTON1_MASK | InputEvent.CTRL_MASK) )    {
                 

			// The location of the mouse event.
			int x = event.getX();
			int y = event.getY();

			// Pick the closest node
			pickCanvas.setShapeLocation(x, y);
			Point3d eyePos        = pickCanvas.getStartPosition();
			PickResult pickResult = pickCanvas.pickClosest();

			// If nothing was picked (didn't click on one of the points) the PickResult
			// will be null.  If we continue with a null result a NullPointerException
                              // will be thrown and the wakeupOn(wakeupCriterion) method will not be
			// called, effectivly disabling the behavior.
			if (pickResult != null)
			{
			    // Get the closest intersection with the geometry.
			    PickIntersection pickIntersection = pickResult.getClosestIntersection(eyePos);

			    // Check that a PointArray was picked
			    if (pickIntersection.getGeometryArray() instanceof PointArray)
			    {
				
				//get the PointArray
				points = (PointArray)pickIntersection.getGeometryArray();
				
				// Get the vertex indices of the intersected primitive,
				// in this case the index of the intersected point in the PointArray.
				int[] indices = pickIntersection.getPrimitiveVertexIndices();

				oldindex = index;
				// There should only be one index since this is a PointArray,
				// so we use indices[0].
				index = indices[0];
				
				updateScene(x, y);
			    }
			}
			
		    }
		}
	    }
	}

	wakeupOn(wakeupCriterion);
 
    }
  
    //override: update the scene
    //here we change the color of the clicked point
    public void updateScene(int x, int y) {
	
	
	if (points.getCapability(PointArray.ALLOW_COLOR_WRITE) )  {
                    
	    //open Window with Probe information
	    master.runInformationWindow(index);
	    
	    points.setColor(index, new Color4f(Color.MAGENTA));
	 
	    if (oldindex != index && oldindex != -1)  {
		
		points.setColor(oldindex, new Color4f(Color.WHITE));
		
	    }
            else
        	System.out.println("EM: No Color can be set for picked point!");
                
	}
	else     
	    System.err.println("EM: Color can't bet set for picked point!");

    }
}
*/