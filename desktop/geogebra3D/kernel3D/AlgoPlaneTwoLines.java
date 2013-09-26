package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoLineND;

/**
 * 
 * Plane containing the two lines, if possible
 * 
 * @author mathieu
 *
 */
public class AlgoPlaneTwoLines extends AlgoElement3D {
	
	/** the 2D coord sys created */
	protected GeoCoordSys2D cs;
	
	
	/** 3D points */
	private GeoLineND a, b;
	
	/**
	 * create a plane joining lines, with label.
	 * @param c construction
	 * @param label label of the polygon
	 * @param a first point
	 * @param b second point
	 */
	public AlgoPlaneTwoLines(Construction c, String label, GeoLineND a, GeoLineND b) {
		super(c);
		 
		this.a = a;
		this.b = b;

		cs = new GeoPlane3D(c);
		
		//set input and output		
		setInputOutput(new GeoElement[]{(GeoElement) a, (GeoElement) b}, new GeoElement[]{(GeoElement) cs});
		
		compute();
		
		((GeoElement) cs).setLabel(label);
		
	}
	
	
	@Override
	public void compute() {
		
		CoordSys coordsys = cs.getCoordSys();
		

		if ((!a.isDefined()) || (!b.isDefined())){
			coordsys.setUndefined();
			return;
		}
		
		Coords oa = a.getStartInhomCoords();
		Coords va = a.getDirectionInD3();
		
		Coords ob = b.getStartInhomCoords();
		Coords vb = b.getDirectionInD3();
		

		Coords vn = va.crossProduct(vb);
		Coords oo = ob.sub(oa);
		
		if(!Kernel.isZero(vn.dotproduct(oo))){
			//lines are not in the same plane
			coordsys.setUndefined();
			return;
		}
		
		
		//recompute the coord sys
		coordsys.resetCoordSys();
		
		coordsys.addPoint(oa);
		coordsys.addVector(va);
		if (vn.isZero()){
			// when lines are parallel
			coordsys.addVector(oo);
		}else{
			coordsys.addVector(vb);
		}
		
		if (coordsys.makeOrthoMatrix(false,false)){
			if (coordsys.isDefined())
				coordsys.makeEquationVector();
		}
		
		//Application.debug(cs.getCoordSys().getMatrixOrthonormal().toString());
		
	}

	
	/**
	 * return the cs
	 * @return the cs
	 */
	public GeoCoordSys2D getCoordSys() {		
		return cs;
	}
	
	
	
	@Override
	public Commands getClassName() {
		return Commands.Plane;
	}
	
    @Override
	final public String toString(StringTemplate tpl) {
    	return loc.getPlain("PlaneThroughAB",a.getLabel(tpl),b.getLabel(tpl));

    }

	// TODO Consider locusequability

}
