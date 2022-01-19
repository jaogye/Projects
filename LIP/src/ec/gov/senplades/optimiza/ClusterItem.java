package ec.gov.senplades.optimiza;

import java.util.ArrayList;
import java.util.Iterator;

public class ClusterItem {

	public short capacidad, area_ind, area_ind_opt, cap, nDemanda, nDemanda2; 
	public double nRadioPro, nRadioMax;
	
	ArrayList<Short> ListaNodo ;

	public ClusterItem(){
		
	}
	public ClusterItem(short nInd, short nCapacidad){
        area_ind = nInd; 
		capacidad = nCapacidad; 
		cap = nCapacidad;		
		ListaNodo = new ArrayList<Short>();
		nDemanda = 0; 
		nDemanda2 = 0;
	}
	
	public void AddNodo(short nOrd){	  	
		ListaNodo.add(nOrd);
	}

	public void RemoveNodo(short nOrd){	  	
		 Iterator<Short> iter = ListaNodo.iterator();
		 short n; 
		 while (iter.hasNext() ){
			 n=iter.next(); 
			 if (n==nOrd){
				 iter.remove(); 
			 }
		 }
		 
	}
	
	
	public void ClearLista(){	  	
		 ListaNodo.clear(); 
	}
	
	public ClusterItem Clone2() {
		ClusterItem oItem = new ClusterItem();
		oItem.area_ind=this.area_ind; 
		oItem.capacidad=this.capacidad; 
		oItem.cap=this.cap;		
		oItem.ListaNodo = new ArrayList<Short>();
		oItem.nDemanda = 0; 
		oItem.nDemanda2 = 0;

		return oItem ; 		
	}
	
}
