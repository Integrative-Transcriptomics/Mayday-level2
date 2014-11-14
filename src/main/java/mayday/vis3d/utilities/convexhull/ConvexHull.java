package mayday.vis3d.utilities.convexhull;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;

import mayday.vis3d.primitives.Point3D;
import mayday.vis3d.primitives.Vector3D;

/**
 * This class implements the 3D Quickhull algorithm as described by C. Bradford
 * Barber, David P. Dobkin and Hannu Huhdanpaa in their paper
 * "The Quickhull Algorithm for Convex Hulls" published on December 1996
 * 
 * <a href=http://citeseer.ist.psu.edu/barber96quickhull.html>
 * 
 * complexity: O(n log(n)) , with n = number of points
 * 
 * @author G\u00FCnter J\u00E4ger
 * @date June 23, 2010
 * 
 */
public class ConvexHull {
	/**
	 * Clockwise order for output of vertex indices
	 */
	public static final int CLOCKWISE = 0x1;

	/**
	 * number vertex indices for output, starting by 1
	 */
	public static final int INDEXED_FROM_ONE = 0x2;

	/**
	 * number vertex indices for output, starting by 0
	 */
	public static final int INDEXED_FROM_ZERO = 0x4;

	/**
	 * number vertex indices with respect to the original input points on output
	 */
	public static final int POINT_RELATIVE = 0x8;

	/**
	 * compute the distance tolerance automatically from the input point data?
	 */
	public static final double AUTOMATIC_TOLERANCE = -1;

	protected int findIndex = -1;
	protected double charLength;

	protected Vertex[] pointBuffer = new Vertex[0];
	protected int[] vertexPointIndices = new int[0];
	private Face[] discardedFaces = new Face[3];

	private Vertex[] maxVertices = new Vertex[3];
	private Vertex[] minVertices = new Vertex[3];

	protected Vector<Face> faces = new Vector<Face>(16);
	protected Vector<HalfEdge> horizon = new Vector<HalfEdge>(16);

	private FaceList newFaces = new FaceList();
	private VertexList unclaimed = new VertexList();
	private VertexList claimed = new VertexList();

	protected int numVertices;
	protected int numFaces;
	protected int numPoints;

	protected double explicitTolerance = AUTOMATIC_TOLERANCE;
	protected double tolerance;

	/**
	 * double precision
	 */
	static private final double DOUBLE_PREC = 2.2204460492503131e-16;

	private void addPointToFace(Vertex v, Face face) {
		v.face = face;
		if (face.outside == null) {
			claimed.add(v);
		} else {
			claimed.insertBefore(v, face.outside);
		}
		face.outside = v;
	}

	private void removePointFromFace(Vertex v, Face face) {
		if (v == face.outside) {
			if (v.next != null && v.next.face == face) {
				face.outside = v.next;
			} else {
				face.outside = null;
			}
		}
		claimed.delete(v);
	}

	private Vertex removeAllPointsFromFace(Face face) {
		if (face.outside != null) {
			Vertex end = face.outside;
			while (end.next != null && end.next.face == face) {
				end = end.next;
			}
			claimed.delete(face.outside, end);
			end.next = null;
			return face.outside;
		} else {
			return null;
		}
	}

	/**
	 * Default constructor
	 */
	public ConvexHull() {
	}

	/**
	 * @param points
	 * @throws IllegalArgumentException
	 */
	public ConvexHull(Point3D[] points) throws IllegalArgumentException {
		build(points, points.length);
	}

	private HalfEdge findHalfEdge(Vertex tail, Vertex head) {
		for (Iterator<Face> it = faces.iterator(); it.hasNext();) {
			HalfEdge he = ((Face) it.next()).findEdge(tail, head);
			if (he != null) {
				return he;
			}
		}
		return null;
	}

	protected void setHull(double[] coords, int nump, int[][] faceIndices,
			int numf) {
		initBuffers(nump);
		setPoints(coords, nump);
		computeMaxAndMin();
		for (int i = 0; i < numf; i++) {
			Face face = Face.create(pointBuffer, faceIndices[i]);
			HalfEdge he = face.he;
			do {
				HalfEdge heOpp = findHalfEdge(he.head(), he.tail());
				if (heOpp != null) {
					he.setOpposite(heOpp);
				}
				he = he.next;
			} while (he != face.he);
			faces.add(face);
		}
	}

	/**
	 * @param points
	 * @throws IllegalArgumentException
	 */
	public void build(Point3D[] points) throws IllegalArgumentException {
		build(points, points.length);
	}

	/**
	 * @param points
	 * @param nump
	 * @throws IllegalArgumentException
	 */
	public void build(Point3D[] points, int nump)
			throws IllegalArgumentException {
		if (nump < 4) {
			throw new IllegalArgumentException(
					"Less than four input points specified");
		}
		if (points.length < nump) {
			throw new IllegalArgumentException(
					"Point array too small for specified number of points");
		}
		initBuffers(nump);
		setPoints(points, nump);
		buildHull();
	}

	/**
	 * Triangulate faces
	 */
	public void triangulate() {
		double minArea = 1000 * charLength * DOUBLE_PREC;
		newFaces.clear();
		for (Iterator<Face> it = faces.iterator(); it.hasNext();) {
			Face face = (Face) it.next();
			if (face.mark == Face.VISIBLE) {
				face.triangulate(newFaces, minArea);
			}
		}
		for (Face face = newFaces.first(); face != null; face = face.next) {
			faces.add(face);
		}
	}

	protected void initBuffers(int nump) {
		if (pointBuffer.length < nump) {
			Vertex[] newBuffer = new Vertex[nump];
			vertexPointIndices = new int[nump];
			for (int i = 0; i < pointBuffer.length; i++) {
				newBuffer[i] = pointBuffer[i];
			}
			for (int i = pointBuffer.length; i < nump; i++) {
				newBuffer[i] = new Vertex();
			}
			pointBuffer = newBuffer;
		}
		faces.clear();
		claimed.clear();
		numFaces = 0;
		numPoints = nump;
	}

	protected void setPoints(double[] coords, int nump) {
		for (int i = 0; i < nump; i++) {
			Vertex v = pointBuffer[i];
			v.p.set(coords[i * 3 + 0], coords[i * 3 + 1], coords[i * 3 + 2]);
			v.index = i;
		}
	}

	protected void setPoints(Point3D[] pnts, int nump) {
		for (int i = 0; i < nump; i++) {
			Vertex v = pointBuffer[i];
			v.p.set(pnts[i]);
			v.index = i;
		}
	}

	protected void computeMaxAndMin() {
		Vector3D max = new Vector3D();
		Vector3D min = new Vector3D();

		for (int i = 0; i < 3; i++) {
			maxVertices[i] = minVertices[i] = pointBuffer[0];
		}
		max.set(pointBuffer[0].p);
		min.set(pointBuffer[0].p);

		for (int i = 1; i < numPoints; i++) {
			Point3D p = pointBuffer[i].p;
			if (p.x > max.x) {
				max.x = p.x;
				maxVertices[0] = pointBuffer[i];
			} else if (p.x < min.x) {
				min.x = p.x;
				minVertices[0] = pointBuffer[i];
			}
			if (p.y > max.y) {
				max.y = p.y;
				maxVertices[1] = pointBuffer[i];
			} else if (p.y < min.y) {
				min.y = p.y;
				minVertices[1] = pointBuffer[i];
			}
			if (p.z > max.z) {
				max.z = p.z;
				maxVertices[2] = pointBuffer[i];
			} else if (p.z < min.z) {
				min.z = p.z;
				maxVertices[2] = pointBuffer[i];
			}
		}

		charLength = Math.max(max.x - min.x, max.y - min.y);
		charLength = Math.max(max.z - min.z, charLength);
		if (explicitTolerance == AUTOMATIC_TOLERANCE) {
			tolerance = 3
					* DOUBLE_PREC
					* (Math.max(Math.abs(max.x), Math.abs(min.x))
							+ Math.max(Math.abs(max.y), Math.abs(min.y)) + Math
							.max(Math.abs(max.z), Math.abs(min.z)));
		} else {
			tolerance = explicitTolerance;
		}
	}

	/**
	 * Creates the initial simplex from which the hull will be built.
	 */
	protected void createInitialSimplex() throws IllegalArgumentException {
		double max = 0;
		int imax = 0;

		for (int i = 0; i < 3; i++) {
			double diff = maxVertices[i].p.get(i) - minVertices[i].p.get(i);
			if (diff > max) {
				max = diff;
				imax = i;
			}
		}

		if (max <= tolerance) {
			throw new IllegalArgumentException(
					"Input points appear to be coincident");
		}
		Vertex[] vertices = new Vertex[4];
		// set first two vertices to be those with the greatest
		// one dimensional separation

		vertices[0] = maxVertices[imax];
		vertices[1] = minVertices[imax];

		// set third vertex to be the farthest vertex from
		// the line between vertex0 and vertex1
		Vector3D u01 = new Vector3D();
		Vector3D diff02 = new Vector3D();
		Vector3D normal = new Vector3D();
		Vector3D xprod = new Vector3D();

		double maxSqr = 0;
		u01.sub(vertices[1].p, vertices[0].p);
		u01.normalize();
		for (int i = 0; i < numPoints; i++) {
			diff02.sub(pointBuffer[i].p, vertices[0].p);
			xprod.cross(u01, diff02);
			double lenSqr = xprod.normSquared();
			if (lenSqr > maxSqr && pointBuffer[i] != vertices[0]
					&& pointBuffer[i] != vertices[1]) {
				maxSqr = lenSqr;
				vertices[2] = pointBuffer[i];
				normal.set(xprod);
			}
		}
		if (Math.sqrt(maxSqr) <= 100 * tolerance) {
			throw new IllegalArgumentException(
					"Input points appear to be colinear");
		}
		normal.normalize();

		double maxDist = 0;
		double d0 = vertices[2].p.dot(normal);
		for (int i = 0; i < numPoints; i++) {
			double dist = Math.abs(pointBuffer[i].p.dot(normal) - d0);
			if (dist > maxDist && pointBuffer[i] != vertices[0]
					&& pointBuffer[i] != vertices[1]
					&& pointBuffer[i] != vertices[2]) {
				maxDist = dist;
				vertices[3] = pointBuffer[i];
			}
		}
		if (Math.abs(maxDist) <= 100 * tolerance) {
			throw new IllegalArgumentException(
					"Input points appear to be coplanar");
		}

		Face[] tris = new Face[4];

		if (vertices[3].p.dot(normal) - d0 < 0) {
			tris[0] = Face
					.createTriangle(vertices[0], vertices[1], vertices[2]);
			tris[1] = Face
					.createTriangle(vertices[3], vertices[1], vertices[0]);
			tris[2] = Face
					.createTriangle(vertices[3], vertices[2], vertices[1]);
			tris[3] = Face
					.createTriangle(vertices[3], vertices[0], vertices[2]);

			for (int i = 0; i < 3; i++) {
				int k = (i + 1) % 3;
				tris[i + 1].getEdge(1).setOpposite(tris[k + 1].getEdge(0));
				tris[i + 1].getEdge(2).setOpposite(tris[0].getEdge(k));
			}
		} else {
			tris[0] = Face
					.createTriangle(vertices[0], vertices[2], vertices[1]);
			tris[1] = Face
					.createTriangle(vertices[3], vertices[0], vertices[1]);
			tris[2] = Face
					.createTriangle(vertices[3], vertices[1], vertices[2]);
			tris[3] = Face
					.createTriangle(vertices[3], vertices[2], vertices[0]);

			for (int i = 0; i < 3; i++) {
				int k = (i + 1) % 3;
				tris[i + 1].getEdge(0).setOpposite(tris[k + 1].getEdge(1));
				tris[i + 1].getEdge(2)
						.setOpposite(tris[0].getEdge((3 - i) % 3));
			}
		}

		for (int i = 0; i < 4; i++) {
			faces.add(tris[i]);
		}

		for (int i = 0; i < numPoints; i++) {
			Vertex v = pointBuffer[i];

			if (v == vertices[0] || v == vertices[1] || v == vertices[2]
					|| v == vertices[3]) {
				continue;
			}

			maxDist = tolerance;
			Face maxFace = null;
			for (int k = 0; k < 4; k++) {
				double dist = tris[k].distanceToPlane(v.p);
				if (dist > maxDist) {
					maxFace = tris[k];
					maxDist = dist;
				}
			}
			if (maxFace != null) {
				addPointToFace(v, maxFace);
			}
		}
	}

	/**
	 * @return number of vertices needed to construct the hull
	 */
	public int getNumVertices() {
		return numVertices;
	}

	/**
	 * @return vertices needed to construct the hull
	 */
	public Point3D[] getVertices() {
		Point3D[] vtxs = new Point3D[numVertices];
		for (int i = 0; i < numVertices; i++) {
			vtxs[i] = pointBuffer[vertexPointIndices[i]].p;
		}
		return vtxs;
	}

	/**
	 * @return indices of the vertices needed to construct the hull
	 */
	public int[] getVertexPointIndices() {
		int[] indices = new int[numVertices];
		for (int i = 0; i < numVertices; i++) {
			indices[i] = vertexPointIndices[i];
		}
		return indices;
	}

	/**
	 * @return number of faces
	 */
	public int getNumFaces() {
		return faces.size();
	}

	/**
	 * a face is an int[] containing all indices of vertices needed to build the
	 * face
	 * 
	 * @return faces needed to construct the hull
	 */
	public int[][] getFaces() {
		return getFaces(0);
	}

	/**
	 * a face is an int[] containing all indices of vertices needed to build the
	 * face
	 * 
	 * @param indexFlags
	 * @return faces needed to construct the hull
	 */
	public int[][] getFaces(int indexFlags) {
		int[][] allFaces = new int[faces.size()][];
		int k = 0;
		for (Iterator<Face> it = faces.iterator(); it.hasNext();) {
			Face face = (Face) it.next();
			allFaces[k] = new int[face.numVertices()];
			getFaceIndices(allFaces[k], face, indexFlags);
			k++;
		}
		return allFaces;
	}

	/**
	 * @param ps
	 */
	public void print(PrintStream ps) {
		print(ps, 0);
	}

	/**
	 * @param ps
	 * @param indexFlags
	 */
	public void print(PrintStream ps, int indexFlags) {
		if ((indexFlags & INDEXED_FROM_ZERO) == 0) {
			indexFlags |= INDEXED_FROM_ONE;
		}
		for (int i = 0; i < numVertices; i++) {
			Point3D pnt = pointBuffer[vertexPointIndices[i]].p;
			ps.println("v " + pnt.x + " " + pnt.y + " " + pnt.z);
		}
		for (Iterator<Face> fi = faces.iterator(); fi.hasNext();) {
			Face face = (Face) fi.next();
			int[] indices = new int[face.numVertices()];
			getFaceIndices(indices, face, indexFlags);

			ps.print("f");
			for (int k = 0; k < indices.length; k++) {
				ps.print(" " + indices[k]);
			}
			ps.println("");
		}
	}

	private void getFaceIndices(int[] indices, Face face, int flags) {
		boolean ccw = ((flags & CLOCKWISE) == 0);
		boolean indexedFromOne = ((flags & INDEXED_FROM_ONE) != 0);
		boolean pointRelative = ((flags & POINT_RELATIVE) != 0);

		HalfEdge hedge = face.he;
		int k = 0;
		do {
			int idx = hedge.head().index;
			if (pointRelative) {
				idx = vertexPointIndices[idx];
			}
			if (indexedFromOne) {
				idx++;
			}
			indices[k++] = idx;
			hedge = (ccw ? hedge.next : hedge.prev);
		} while (hedge != face.he);
	}

	protected void resolveUnclaimedPoints(FaceList newFaces) {
		Vertex vtxNext = unclaimed.first();
		for (Vertex vtx = vtxNext; vtx != null; vtx = vtxNext) {
			vtxNext = vtx.next;

			double maxDist = tolerance;
			Face maxFace = null;
			for (Face newFace = newFaces.first(); newFace != null; newFace = newFace.next) {
				if (newFace.mark == Face.VISIBLE) {
					double dist = newFace.distanceToPlane(vtx.p);
					if (dist > maxDist) {
						maxDist = dist;
						maxFace = newFace;
					}
					if (maxDist > 1000 * tolerance) {
						break;
					}
				}
			}
			if (maxFace != null) {
				addPointToFace(vtx, maxFace);
			}
		}
	}

	protected void deleteFacePoints(Face face, Face absorbingFace) {
		Vertex faceVtxs = removeAllPointsFromFace(face);
		if (faceVtxs != null) {
			if (absorbingFace == null) {
				unclaimed.addAll(faceVtxs);
			} else {
				Vertex vtxNext = faceVtxs;
				for (Vertex vtx = vtxNext; vtx != null; vtx = vtxNext) {
					vtxNext = vtx.next;
					double dist = absorbingFace.distanceToPlane(vtx.p);
					if (dist > tolerance) {
						addPointToFace(vtx, absorbingFace);
					} else {
						unclaimed.add(vtx);
					}
				}
			}
		}
	}

	private static final int NONCONVEX_WRT_LARGER_FACE = 1;
	private static final int NONCONVEX = 2;

	protected double oppFaceDistance(HalfEdge he) {
		return he.face.distanceToPlane(he.opposite.face.getCentroid());
	}

	private boolean doAdjacentMerge(Face face, int mergeType) {
		HalfEdge hedge = face.he;

		boolean convex = true;
		do {
			Face oppFace = hedge.oppositeFace();
			boolean merge = false;

			// merge faces if they are definitively non-convex
			if (mergeType == NONCONVEX) {
				if (oppFaceDistance(hedge) > -tolerance
						|| oppFaceDistance(hedge.opposite) > -tolerance) {
					merge = true;
				}
			} else { 
				// merge faces if they are parallel or non-convex
				if (face.area > oppFace.area) {
					if ((oppFaceDistance(hedge)) > -tolerance) {
						merge = true;
					} else if (oppFaceDistance(hedge.opposite) > -tolerance) {
						convex = false;
					}
				} else {
					if (oppFaceDistance(hedge.opposite) > -tolerance) {
						merge = true;
					} else if (oppFaceDistance(hedge) > -tolerance) {
						convex = false;
					}
				}
			}

			if (merge) {
				int numd = face.mergeAdjacentFace(hedge, discardedFaces);
				for (int i = 0; i < numd; i++) {
					deleteFacePoints(discardedFaces[i], face);
				}
				return true;
			}
			hedge = hedge.next;
		} while (hedge != face.he);
		if (!convex) {
			face.mark = Face.NON_CONVEX;
		}
		return false;
	}

	protected void calculateHorizon(Point3D eyePnt, HalfEdge edge0, Face face,
			Vector<HalfEdge> horizon) {
		deleteFacePoints(face, null);
		face.mark = Face.DELETED;
		HalfEdge edge;
		if (edge0 == null) {
			edge0 = face.getEdge(0);
			edge = edge0;
		} else {
			edge = edge0.getNext();
		}
		do {
			Face oppFace = edge.oppositeFace();
			if (oppFace.mark == Face.VISIBLE) {
				if (oppFace.distanceToPlane(eyePnt) > tolerance) {
					calculateHorizon(eyePnt, edge.getOpposite(), oppFace,
							horizon);
				} else {
					horizon.add(edge);
				}
			}
			edge = edge.getNext();
		} while (edge != edge0);
	}

	private HalfEdge addAdjoiningFace(Vertex eyeVtx, HalfEdge he) {
		Face face = Face.createTriangle(eyeVtx, he.tail(), he.head());
		faces.add(face);
		face.getEdge(-1).setOpposite(he.getOpposite());
		return face.getEdge(0);
	}

	protected void addNewFaces(FaceList newFaces, Vertex eyeVtx,
			Vector<HalfEdge> horizon) {
		newFaces.clear();

		HalfEdge hedgeSidePrev = null;
		HalfEdge hedgeSideBegin = null;

		for (Iterator<HalfEdge> it = horizon.iterator(); it.hasNext();) {
			HalfEdge horizonHe = (HalfEdge) it.next();
			HalfEdge hedgeSide = addAdjoiningFace(eyeVtx, horizonHe);

			if (hedgeSidePrev != null) {
				hedgeSide.next.setOpposite(hedgeSidePrev);
			} else {
				hedgeSideBegin = hedgeSide;
			}
			newFaces.add(hedgeSide.getFace());
			hedgeSidePrev = hedgeSide;
		}
		hedgeSideBegin.next.setOpposite(hedgeSidePrev);
	}

	protected Vertex nextPointToAdd() {
		if (!claimed.isEmpty()) {
			Face eyeFace = claimed.first().face;
			Vertex eyeVtx = null;
			double maxDist = 0;
			for (Vertex vtx = eyeFace.outside; vtx != null
					&& vtx.face == eyeFace; vtx = vtx.next) {
				double dist = eyeFace.distanceToPlane(vtx.p);
				if (dist > maxDist) {
					maxDist = dist;
					eyeVtx = vtx;
				}
			}
			return eyeVtx;
		} else {
			return null;
		}
	}

	protected void addPointToHull(Vertex eyeVtx) {
		horizon.clear();
		unclaimed.clear();

		removePointFromFace(eyeVtx, eyeVtx.face);
		calculateHorizon(eyeVtx.p, null, eyeVtx.face, horizon);
		newFaces.clear();
		addNewFaces(newFaces, eyeVtx, horizon);

		// first merge pass
		for (Face face = newFaces.first(); face != null; face = face.next) {
			if (face.mark == Face.VISIBLE) {
				while (doAdjacentMerge(face, NONCONVEX_WRT_LARGER_FACE))
					;
			}
		}
		// second merge pass
		for (Face face = newFaces.first(); face != null; face = face.next) {
			if (face.mark == Face.NON_CONVEX) {
				face.mark = Face.VISIBLE;
				while (doAdjacentMerge(face, NONCONVEX))
					;
			}
		}
		resolveUnclaimedPoints(newFaces);
	}

	protected void buildHull() {
		int cnt = 0;
		Vertex eyeVtx;

		computeMaxAndMin();
		createInitialSimplex();
		while ((eyeVtx = nextPointToAdd()) != null) {
			addPointToHull(eyeVtx);
			cnt++;
		}
		reindexFacesAndVertices();
	}

	private void markFaceVertices(Face face, int mark) {
		HalfEdge he0 = face.getFirstEdge();
		HalfEdge he = he0;
		do {
			he.head().index = mark;
			he = he.next;
		} while (he != he0);
	}

	protected void reindexFacesAndVertices() {
		for (int i = 0; i < numPoints; i++) {
			pointBuffer[i].index = -1;
		}
		// remove inactive faces and mark active vertices
		numFaces = 0;
		for (Iterator<Face> it = faces.iterator(); it.hasNext();) {
			Face face = (Face) it.next();
			if (face.mark != Face.VISIBLE) {
				it.remove();
			} else {
				markFaceVertices(face, 0);
				numFaces++;
			}
		}
		// re-index vertices
		numVertices = 0;
		for (int i = 0; i < numPoints; i++) {
			Vertex vtx = pointBuffer[i];
			if (vtx.index == 0) {
				vertexPointIndices[numVertices] = i;
				vtx.index = numVertices++;
			}
		}
	}
}
