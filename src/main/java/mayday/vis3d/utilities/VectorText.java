package mayday.vis3d.utilities;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUtessellator;
import com.jogamp.opengl.glu.GLUtessellatorCallback;
import com.jogamp.opengl.util.awt.TextRenderer;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Renderer for vectorized 3D-text.
 * Adaptation of source code from: http://forum.lwjgl.org/index.php?topic=1430.0
 * Created by adrian on 2/3/16.
 */
public class VectorText implements GLUtessellatorCallback {

    /**
     * Measure for resolution of rendered text. Smaller means higher Quality.
     */
    private static double EPS=0.001;
    /**
     * Upper limit of how many element will be allowed to tesselated.
     * If the number of stored
     */
    private static int LIMIT = 100;

    private GLU glu;
    private GL gl;
    private GLUtessellator tesselator;

    /**
     * Remember how many different strings have been drawn sofar.
     */
    private HashSet<String> uniqStrings = new HashSet<>();

    public VectorText(GLU glu) {
        this.glu= glu;

        // init tessellator callback references
        tesselator = GLU.gluNewTess();
        glu.gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN, this);
        glu.gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX, this);
        glu.gluTessCallback(tesselator, GLU.GLU_TESS_COMBINE, this);
        glu.gluTessCallback(tesselator, GLU.GLU_TESS_END, this);
        glu.gluTessCallback(tesselator, GLU.GLU_TESS_ERROR, this);
    }

    /**
     * Make sure to call this method before drawing.
     */
    public void setGL(GL gl) {
        this.gl = gl;
    }

    /**
     * Calculate the contour outline for a given text. A vertex is set within
     * EPS distance.
     */
    private PathIterator calcOutline(String text, float scale, TextRenderer renderer) {
        FontRenderContext frc = renderer.getFontRenderContext();
        GlyphVector gv = renderer.getFont().createGlyphVector(frc, text);
        Shape shp = gv.getOutline();
        AffineTransform aff = new AffineTransform();
        aff.scale(scale, scale);

        return  shp.getPathIterator(aff, EPS);
    }


    /**
     * Draw a text at the specified position. Because we use tessellation, the
     * rgb values need to be explicitly stated for each vertex.
     */
    public void drawText(String text, TextRenderer renderer,
                         float x, float y, float z, float scale,
                         float r, float g, float b) {
        GL2 gl2 = gl.getGL2();

        // possible fallback to bitmap text
        if (uniqStrings == null || uniqStrings.size() > LIMIT) {
            // too many tesselation objects => Fallback
            renderer.setColor(r, g, b, 1);
            renderer.begin3DRendering();
            renderer.draw3D(text, x, y, z, scale);
            renderer.end3DRendering();
            // free uniqStrings space
            uniqStrings = null;
            return;
        }
        uniqStrings.add(text);

        PathIterator iter = calcOutline(text, scale, renderer);

        // Go to correct position
        gl2.glPushMatrix();
        gl2.glTranslated(x, y, z);

        // Setup wwinding rules.
        // Explanation: http://www.glprogramming.com/red/chapter11.html
        switch(iter.getWindingRule()) {
            case PathIterator.WIND_EVEN_ODD:
                glu.gluTessProperty(tesselator,
                        GLU.GLU_TESS_WINDING_RULE,
                        GLU.GLU_TESS_WINDING_ODD);
                break;
            case PathIterator.WIND_NON_ZERO:
                glu.gluTessProperty(tesselator,
                        GLU.GLU_TESS_WINDING_RULE,
                        GLU.GLU_TESS_WINDING_NONZERO);
                break;
        }

        // just some settings
        gl2.glShadeModel(GL2.GL_FLAT);
        gl2.glPushAttrib(GL2.GL_LINE_BIT);
        gl2.glLineWidth(15.0f);

        // draw the polygon
        glu.gluTessBeginPolygon(tesselator, null);
        while(!iter.isDone()) {
            double[] coords = new double[6];
            double[] vertexData;
            int lineType = iter.currentSegment(coords);
            switch(lineType) {
                case PathIterator.SEG_MOVETO:
                    // A new contour started
                    glu.gluTessBeginContour(tesselator);
                    vertexData = new double[] {
                            coords[0], -coords[1], 0.0,
                            r, g, b
                    };
                    glu.gluTessVertex(tesselator, vertexData, 0, vertexData);
                    break;
                case PathIterator.SEG_CLOSE:
                    // A contour ended
                    glu.gluTessEndContour(tesselator);
                    break;
                case PathIterator.SEG_LINETO:
                    // draw a line
                    vertexData = new double[]  {
                            coords[0], -coords[1], 0.0,
                            r, g, b
                    };
                    glu.gluTessVertex(tesselator, vertexData, 0, vertexData);
                    break;
                case PathIterator.SEG_CUBICTO:
                case PathIterator.SEG_QUADTO:
                    // This cases should not happen for text rendering.
                    // we use the first control-point, just to be on the safe site.
                    vertexData = new double[]  {
                            coords[0], -coords[1], 0.0,
                            r, g, b
                    };
                    glu.gluTessVertex(tesselator, vertexData, 0, vertexData);
                    break;
            }
            iter.next();
        }
        // end of polygon
        glu.gluTessEndPolygon(tesselator);

        // undo settings
        gl2.glPopAttrib();
        gl2.glPopMatrix();
    }


    /**
     * Tessellation will use this function for drawing a vertex.
     * @param vertexData
     */
    @Override
    public void vertex(Object vertexData) {
        double[] pointer;
        if (vertexData instanceof double[]) {
            pointer = (double[]) vertexData;
            if (pointer.length == 6)
                gl.getGL2().glColor3dv(pointer, 3);
            gl.getGL2().glVertex3dv(pointer, 0);
        }
    }

    /**
     * This function will do the tessellation work.
     * Here: Just use original coordinate, nothing more.
     * @param coords
     * @param vertex_data
     * @param weight
     * @param dataOut
     */
    @Override
    public void combine(double[] coords, Object[] vertex_data, float[] weight, Object[] dataOut) {
        // must! be of size 6, or you get native code errors
        double[] vertex = new double[6];
        vertex[0] = coords[0];
        vertex[1] = coords[1];
        vertex[2] = coords[2];
        /*
        the commented part would mix the colors of the other neighboring points
        in. Not needed for mayday, but I'll leave it here for documentary purposes;
        just in case someone gets interested in Tessellation.
         */

//        for (int i = 3; i < 6; i++) {
//            vertex[i] = 0;
//            if (vertex_data[0] != null)
//                vertex[i] += weight[0] * ((double[]) vertex_data[0])[i];
//            if (vertex_data[1] != null)
//                vertex[i] += weight[1] * ((double[]) vertex_data[1])[i];
//            if (vertex_data[2] != null)
//                vertex[i] += weight[2] * ((double[]) vertex_data[2])[i];
//            if (vertex_data[3] != null)
//                vertex[i] += weight[3] * ((double[]) vertex_data[3])[i];
//        }
        dataOut[0] = vertex;
    }

    /*
     * Some default Tessellation Callback functions, don't worry about them.
     */

    @Override
    public void combineData(
            double[] position3DVertex,
            Object[] dataOfFourNeighbourVertices, //some may be null
            float[] weightsOfFourNeighbourVertices,
            Object[] dataForSplitOrMergeVertex, //length 1
            Object userData
    ) {
        combine(position3DVertex, dataOfFourNeighbourVertices,
                weightsOfFourNeighbourVertices, dataForSplitOrMergeVertex);
    }

    @Override
    public void begin(int primitiveType) {
        gl.getGL2().glBegin(primitiveType);
    }

    @Override
    public void beginData(int primitiveType, Object userData) {
        begin(primitiveType);
    }

    @Override
    public void edgeFlag(boolean b) {
        // nothing to do here
    }

    @Override
    public void edgeFlagData(boolean b, Object o) {
        // nothing to do here
    }


    @Override
    public void vertexData(Object vertexData, Object userData) {
        vertex(vertexData);
    }

    @Override
    public void end() {
        gl.getGL2().glEnd();
    }

    @Override
    public void endData(Object o) {
        end();
    }

    @Override
    public void error(int errorID) {
        System.err.println("Tesselation error: " + errorID + ", " +
                glu.gluErrorString(errorID));
    }

    @Override
    public void errorData(int errorID, Object userData) {
        error(errorID);
    }
}
