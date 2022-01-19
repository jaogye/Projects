package ec.gov.senplades.optimiza;

import java.util.ArrayList;

public class AreaItem {

	public short id, demanda, clusterId, capActual, capNuevo, clusterId_opt, demcluster; 
	public float distacen; 
	public boolean centro, eliminado, centro_opt, actual, escogido ;
	public String sCodigo, sSector;
	public double nRadioPro ; 
	public int x,y; 
	
	public AreaItem() {
		
	}
	
	public AreaItem(short nId, String sSec, String sCod, int nx, int ny, short nDemanda, short nCapActual, short nCapNuevo) {
		
		id = nId;
		sSector = sSec; 
		sCodigo = sCod; 
		demanda = nDemanda; 
		capActual = nCapActual; 
		capNuevo = nCapNuevo;
		
	    nRadioPro = 999999999; 
		if (nCapActual>0)
			capNuevo =nCapActual; 
		eliminado = false ;
		actual=false;
		clusterId = -1; 
		clusterId_opt = -1;
		x=nx; y=ny; 	
	}

	public AreaItem(short nId, String sSec, String sCod, int nx, int ny, short nDemanda, short nCapActual, short nCapNuevo, short Cluster, boolean Centro) {
		
		id = nId;
		sSector = sSec; 
		sCodigo = sCod; 
		demanda = nDemanda; 
		capActual = nCapActual; 
		capNuevo = nCapNuevo;
	    demcluster =0 ;
	    nRadioPro = 999999999; 
		if (nCapActual>0)
			capNuevo =nCapActual; 
		eliminado = false ;
		actual=false;
		clusterId=Cluster; 
		centro=Centro;
		x=nx; y=ny;

	}

	public AreaItem Clone2() {

	   AreaItem oItem=new AreaItem(); 
	   oItem.id=this.id;
	   oItem.sSector = this.sSector ; 
	   oItem.sCodigo=this.sCodigo; 
	   oItem.demanda=this.demanda; 
	   oItem.capActual=this.capActual; 
	   oItem.capNuevo=this.capNuevo;
	   oItem.demcluster=this.demcluster;
	   oItem.nRadioPro=this.nRadioPro; 
	   oItem.capNuevo=this.capNuevo; 
	   oItem.eliminado=this.eliminado ;
	   oItem.actual=this.actual;
	   oItem.clusterId=this.clusterId; 
	   oItem.centro=this.centro;
	   oItem.x=this.x; 
	   oItem.y=this.y;
	   
	   return oItem; 
	}
}
