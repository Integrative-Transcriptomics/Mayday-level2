package mayday.vis3d.utilities.convexhull;

import mayday.vis3d.primitives.Point3D;
import mayday.vis3d.primitives.Vector3D;

/**
 * @author G\u00FCnter J\u00E4ger
 * @date June 23, 2010
 *
 */
public class Face {

	/**
	 * 
	 */
	public HalfEdge he;
	private Vector3D normal;
	/**
	 * 
	 */
	public double area;
	private Point3D centroid;
	double planeOffset;
	int index;
	int numVertices;

	/**
	 * 
	 */
	public Face next;

	/**
	 * 
	 */
	public static final int VISIBLE = 1;
	/**
	 * 
	 */
	public static final int NON_CONVEX = 2;
	/**
	 * 
	 */
	public static final int DELETED = 3;

	/**
	 * 
	 */
	public int mark = VISIBLE;

	/**
	 * 
	 */
	public Vertex outside;
	
	/**
	 * default constructor
	 */
	public Face() {
		normal = new Vector3D();
		centroid = new Point3D();
		mark = VISIBLE;
	}

	/**
	 * @param centroid
	 */
	public void computeCentroid(Point3D centroid) {
		centroid.setZero();
		HalfEdge he = this.he;
		do {
			centroid.add(he.head().p);
			he = he.next;
		} while (he != this.he);
		centroid.scale(1 / (double) numVertices);
	}

	/**
	 * @param normal
	 * @param minArea
	 */
	public void computeNormal(Vector3D normal, double minArea) {
		computeNormal(normal);

		if (area < minArea) {
			HalfEdge hedgeMax = null;
			double lenSqrMax = 0;
			HalfEdge hedge = this.he;
		
			do {
				double lenSqr = hedge.lengthSquared();
				if (lenSqr > lenSqrMax) {
					hedgeMax = hedge;
					lenSqrMax = lenSqr;
				}
				hedge = hedge.next;
			} while (hedge != this.he);

			Point3D p2 = hedgeMax.head().p;
			Point3D p1 = hedgeMax.tail().p;
			
			double lenMax = Math.sqrt(lenSqrMax);
			
			double ux = (p2.x - p1.x) / lenMax;
			double uy = (p2.y - p1.y) / lenMax;
			double uz = (p2.z - p1.z) / lenMax;
			
			double dot = normal.x * ux + normal.y * uy + normal.z * uz;
			
			normal.x -= dot * ux;
			normal.y -= dot * uy;
			normal.z -= dot * uz;

			normal.normalize();
		}
	}

	/**
	 * @param normal
	 */
	public void computeNormal(Vector3D normal) {
		HalfEdge he1 = this.he.next;
		HalfEdge he2 = he1.next;

		Point3D p0 = this.he.head().p;
		Point3D p2 = he1.head().p;

		double d2x = p2.x - p0.x;
		double d2y = p2.y - p0.y;
		double d2z = p2.z - p0.z;

		normal.setZero();

		numVertices = 2;

		while (he2 != this.he) {
			double d1x = d2x;
			double d1y = d2y;
			double d1z = d2z;

			p2 = he2.head().p;
			d2x = p2.x - p0.x;
			d2y = p2.y - p0.y;
			d2z = p2.z - p0.z;

			normal.x += d1y * d2z - d1z * d2y;
			normal.y += d1z * d2x - d1x * d2z;
			normal.z += d1x * d2y - d1y * d2x;

			he1 = he2;
			he2 = he2.next;
			numVertices++;
		}
		area = normal.norm();
		normal.scale(1 / area);
	}

	private void computeNormalAndCentroid() {
		computeNormal(normal);
		computeCentroid(centroid);
		planeOffset = normal.dot(centroid);
		int numv = 0;
		HalfEdge he = this.he;
		do {
			numv++;
			he = he.next;
		} while (he != this.he);
		if (numv != numVertices) {
			throw new InternalErrorException("face " + getVertexString()
					+ " numVerts=" + numVertices + " should be " + numv);
		}
	}

	private void computeNormalAndCentroid(double minArea) {
		computeNormal(normal, minArea);
		computeCentroid(centroid);
		planeOffset = normal.dot(centroid);
	}

	/**
	 * @param v0
	 * @param v1
	 * @param v2
	 * @return face
	 */
	public static Face createTriangle(Vertex v0, Vertex v1, Vertex v2) {
		return createTriangle(v0, v1, v2, 0);
	}

	/**
	 * @param v0
	 * @param v1
	 * @param v2
	 * @param minArea
	 * @return face
	 */
	public static Face createTriangle(Vertex v0, Vertex v1, Vertex v2,
			double minArea) {
		Face face = new Face();
		HalfEdge he0 = new HalfEdge(v0, face);
		HalfEdge he1 = new HalfEdge(v1, face);
		HalfEdge he2 = new HalfEdge(v2, face);

		he0.prev = he2;
		he0.next = he1;
		he1.prev = he0;
		he1.next = he2;
		he2.prev = he1;
		he2.next = he0;

		face.he = he0;

		// compute the normal and offset
		face.computeNormalAndCentroid(minArea);
		return face;
	}

	/**
	 * @param vtxArray
	 * @param indices
	 * @return face
	 */
	public static Face create(Vertex[] vtxArray, int[] indices) {
		Face face = new Face();
		HalfEdge hePrev = null;
		for (int i = 0; i < indices.length; i++) {
			HalfEdge he = new HalfEdge(vtxArray[indices[i]], face);
			if (hePrev != null) {
				he.setPrev(hePrev);
				hePrev.setNext(he);
			} else {
				face.he = he;
			}
			hePrev = he;
		}
		face.he.setPrev(hePrev);
		hePrev.setNext(face.he);

		// compute the normal and offset
		face.computeNormalAndCentroid();
		return face;
	}

	/**
	 * @param i
	 * @return wanted edge
	 */
	public HalfEdge getEdge(int i) {
		HalfEdge he = this.he;
		while (i > 0) {
			he = he.next;
			i--;
		}
		while (i < 0) {
			he = he.prev;
			i++;
		}
		return he;
	}

	/**
	 * @return first edge
	 */
	public HalfEdge getFirstEdge() {
		return this.he;
	}

	/**
	 * @param vt
	 * @param vh
	 * @return wanted edge
	 */
	public HalfEdge findEdge(Vertex vt, Vertex vh) {
		HalfEdge he = this.he;
		do {
			if (he.head() == vh && he.tail() == vt) {
				return he;
			}
			he = he.next;
		} while (he != this.he);
		return null;
	}

	/**
	 * @param p
	 * @return distance to plane
	 */
	public double distanceToPlane(Point3D p) {
		return normal.x * p.x + normal.y * p.y + normal.z * p.z - planeOffset;
	}

	/**
	 * @return normal
	 */
	public Vector3D getNormal() {
		return normal;
	}

	/**
	 * @return centroid
	 */
	public Point3D getCentroid() {
		return centroid;
	}

	/**
	 * @return number of vertices
	 */
	public int numVertices() {
		return numVertices;
	}

	/**
	 * @return string representation
	 */
	public String getVertexString() {
		String s = null;
		HalfEdge he = this.he;
		do {
			if (s == null) {
				s = "" + he.head().index;
			} else {
				s += " " + he.head().index;
			}
			he = he.next;
		} while (he != this.he);
		return s;
	}

	/**
	 * @param indices
	 */
	public void getVertexIndices(int[] indices) {
		HalfEdge he = this.he;
		int i = 0;
		do {
			indices[i++] = he.head().index;
			he = he.next;
		} while (he != this.he);
	}

	private Face connectHalfEdges(HalfEdge hedgePrev, HalfEdge hedge) {
		Face discardedFace = null;

		if (hedgePrev.oppositeFace() == hedge.oppositeFace()) {
			Face oppFace = hedge.oppositeFace();
			HalfEdge hedgeOpp;

			if (hedgePrev == this.he) {
				this.he = hedge;
			}
			if (oppFace.numVertices() == 3) {
				hedgeOpp = hedge.getOpposite().prev.getOpposite();

				oppFace.mark = DELETED;
				discardedFace = oppFace;
			} else {
				hedgeOpp = hedge.getOpposite().next;

				if (oppFace.he == hedgeOpp.prev) {
					oppFace.he = hedgeOpp;
				}
				hedgeOpp.prev = hedgeOpp.prev.prev;
				hedgeOpp.prev.next = hedgeOpp;
			}
			hedge.prev = hedgePrev.prev;
			hedge.prev.next = hedge;

			hedge.opposite = hedgeOpp;
			hedgeOpp.opposite = hedge;

			oppFace.computeNormalAndCentroid();
		} else {
			hedgePrev.next = hedge;
			hedge.prev = hedgePrev;
		}
		return discardedFace;
	}

	/**
	 * 
	 */
	public void checkConsistency() {
		HalfEdge hedge = this.he;
		double maxd = 0;
		int numv = 0;

		if (numVertices < 3) {
			throw new InternalErrorException("degenerate face: "
					+ getVertexString());
		}
		do {
			HalfEdge hedgeOpp = hedge.getOpposite();
			if (hedgeOpp == null) {
				throw new InternalErrorException("face " + getVertexString()
						+ ": " + "unreflected half edge "
						+ hedge.getVertexString());
			} else if (hedgeOpp.getOpposite() != hedge) {
				throw new InternalErrorException("face " + getVertexString()
						+ ": " + "opposite half edge "
						+ hedgeOpp.getVertexString() + " has opposite "
						+ hedgeOpp.getOpposite().getVertexString());
			}
			if (hedgeOpp.head() != hedge.tail()
					|| hedge.head() != hedgeOpp.tail()) {
				throw new InternalErrorException("face " + getVertexString()
						+ ": " + "half edge " + hedge.getVertexString()
						+ " reflected by " + hedgeOpp.getVertexString());
			}
			Face oppFace = hedgeOpp.face;
			if (oppFace == null) {
				throw new InternalErrorException("face " + getVertexString()
						+ ": " + "no face on half edge "
						+ hedgeOpp.getVertexString());
			} else if (oppFace.mark == DELETED) {
				throw new InternalErrorException("face " + getVertexString()
						+ ": " + "opposite face " + oppFace.getVertexString()
						+ " not on hull");
			}
			double d = Math.abs(distanceToPlane(hedge.head().p));
			if (d > maxd) {
				maxd = d;
			}
			numv++;
			hedge = hedge.next;
		} while (hedge != this.he);

		if (numv != numVertices) {
			throw new InternalErrorException("face " + getVertexString()
					+ " numVerts=" + numVertices + " should be " + numv);
		}

	}

	/**
	 * @param hedgeAdj
	 * @param discarded
	 * @return number of discarded faces
	 */
	public int mergeAdjacentFace(HalfEdge hedgeAdj, Face[] discarded) {
		Face oppFace = hedgeAdj.oppositeFace();
		int numDiscarded = 0;

		discarded[numDiscarded++] = oppFace;
		oppFace.mark = DELETED;

		HalfEdge hedgeOpp = hedgeAdj.getOpposite();

		HalfEdge hedgeAdjPrev = hedgeAdj.prev;
		HalfEdge hedgeAdjNext = hedgeAdj.next;
		HalfEdge hedgeOppPrev = hedgeOpp.prev;
		HalfEdge hedgeOppNext = hedgeOpp.next;

		while (hedgeAdjPrev.oppositeFace() == oppFace) {
			hedgeAdjPrev = hedgeAdjPrev.prev;
			hedgeOppNext = hedgeOppNext.next;
		}

		while (hedgeAdjNext.oppositeFace() == oppFace) {
			hedgeOppPrev = hedgeOppPrev.prev;
			hedgeAdjNext = hedgeAdjNext.next;
		}

		HalfEdge hedge;

		for (hedge = hedgeOppNext; hedge != hedgeOppPrev.next; hedge = hedge.next) {
			hedge.face = this;
		}

		if (hedgeAdj == this.he) {
			this.he = hedgeAdjNext;
		}

		// handle the half edges at the head
		Face discardedFace;

		discardedFace = connectHalfEdges(hedgeOppPrev, hedgeAdjNext);
		if (discardedFace != null) {
			discarded[numDiscarded++] = discardedFace;
		}

		// handle the half edges at the tail
		discardedFace = connectHalfEdges(hedgeAdjPrev, hedgeOppNext);
		if (discardedFace != null) {
			discarded[numDiscarded++] = discardedFace;
		}

		computeNormalAndCentroid();
		checkConsistency();

		return numDiscarded;
	}

	/**
	 * @param newFaces
	 * @param minArea
	 */
	public void triangulate(FaceList newFaces, double minArea) {
		HalfEdge hedge;

		if (numVertices() < 4) {
			return;
		}

		Vertex v0 = this.he.head();

		hedge = this.he.next;
		HalfEdge oppPrev = hedge.opposite;
		Face face0 = null;

		for (hedge = hedge.next; hedge != this.he.prev; hedge = hedge.next) {
			Face face = createTriangle(v0, hedge.prev.head(), hedge.head(),
					minArea);
			face.he.next.setOpposite(oppPrev);
			face.he.prev.setOpposite(hedge.opposite);
			oppPrev = face.he;
			newFaces.add(face);
			if (face0 == null) {
				face0 = face;
			}
		}
		hedge = new HalfEdge(this.he.prev.prev.head(), this);
		hedge.setOpposite(oppPrev);

		hedge.prev = this.he;
		hedge.prev.next = hedge;

		hedge.next = this.he.prev;
		hedge.next.prev = hedge;

		computeNormalAndCentroid(minArea);
		checkConsistency();

		for (Face face = face0; face != null; face = face.next) {
			face.checkConsistency();
		}
	}
}
