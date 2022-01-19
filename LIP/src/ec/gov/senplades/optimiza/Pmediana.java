package ec.gov.senplades.optimiza;

import java.util.ArrayList;
import java.util.Iterator;

public class Pmediana implements HeuristicaLocalizacion{

	public Distancia distancia; 
	public  Areas areas ;
	
	public Pmediana(Distancia oDistancia, Areas oAreas) {
		distancia = oDistancia ;
		areas=oAreas ;
	}
	
	public boolean Optimiza( ){
		return true;  
	}
	


	public boolean ReOptimiza() {
	
		return true ; 
	}
	
	
	protected void InicializaFactible(Areas oArea){
	   ArrayList<Short> ListaNodos; 
	   if (oArea.actual) 
		   this.IniActual(oArea); 

	   for (short i=0; i < oArea.k; i++) { 
		   ListaNodos = oArea.GetNodos(); 
		   this.ColocaCentro(oArea, i+oArea.nCentrosActuales, ListaNodos);
	   }
	   for (short i=0; i < oArea.nNodos; i++)
		      oArea.nodos[i].eliminado=false; 
	   
	}

	private void ColocaCentro(Areas oArea, int n, ArrayList<Short> ListaNodos) {
		
		double nRadioMin = 999999999; 
		short imin = -1, nInd,nSateliteId, iDistaCero=0;
		int capFicticia; 
		Iterator<Short> iter = ListaNodos.iterator();
		int nDemanda =0, nDemandaCero = 0; 
		while( iter.hasNext()) {
		    short i=iter.next();
		    short nCentroId = oArea.nodos[i].id; 
		    if (oArea.nodos[i].capNuevo>0 ){
			   capFicticia = (int) (oArea.nFacCen*oArea.nDemanda/(oArea.k+oArea.nCentrosActuales));
		       capFicticia=capFicticia-oArea.nodos[i].demanda;
		       nDemanda =oArea.nodos[i].demanda;   
		       double nDistaTotal=0;	 
               //Acceso a los vecinos mas cercanos 
			   for (short j=0; j < distancia.vecinos[nCentroId].nOrden ; j++){
				    nSateliteId =  distancia.getOrden(nCentroId, j);
                    nInd = oArea.Find(nSateliteId); 
                    if (nInd!=-1) {
                        if (!oArea.nodos[nInd].eliminado && capFicticia-oArea.nodos[nInd].demanda<0) {
                       	    //System.out.printf("Demanda %d \n", areas.nodos[nInd].demanda);
                        	break; 
                        }
    			        if (!oArea.nodos[nInd].eliminado && capFicticia-oArea.nodos[nInd].demanda>=0) {
    			        	capFicticia=capFicticia-oArea.nodos[nInd].demanda;
    			        	nDemanda = nDemanda + oArea.nodos[nInd].demanda; 
    			        	nDistaTotal =  nDistaTotal + distancia.Dista(nCentroId, nSateliteId)*oArea.nodos[nInd].demanda ;    			        	
    			        	//System.out.printf("Nodo %d  Demanda %d Capacidad %d Distotal %f\n", nSateliteId, areas.nodos[nInd].demanda, capFicticia, nDistaTotal );	        	
    			        }	                    	
                    }
			   }			   
			   
			   double nRadioPro = nDistaTotal/nDemanda;  
			   if (nRadioPro < oArea.nodos[i].nRadioPro){
				   oArea.nodos[i].nRadioPro =nRadioPro; 
				   oArea.nodos[i].demcluster =  (short) nDemanda ; 
			   }
               if (nDistaTotal==0 && nDemandaCero < nDemanda){
            	   nDemandaCero = nDemanda ; 
            	   iDistaCero =i ; 
               }
			   
			   if (nDistaTotal>0 && nRadioPro < nRadioMin  ){
			      nRadioMin = nRadioPro;  
			      imin = i ; 			
			   }
			}
		}		
		// Si no puedo obtener un centro minimo de distacia total positiva
		// entonces asigno a un centro de distancia cero 
		if (imin==-1){
			imin = iDistaCero;  
			nDemanda = nDemandaCero; 
		}
		// Eliminacion del nodo optimo y sus satelites 
		capFicticia = (int) (oArea.nFacCen* oArea.nDemanda/(oArea.k+oArea.nCentrosActuales));
        short nCentroId = oArea.nodos[imin].id;
        System.out.printf("Nª %d Centro Inicial : %s  Radio Promedio %f Demanda %d CapFicticia %d \n", n,  oArea.nodos[imin].sCodigo, nRadioMin, nDemanda, oArea.nDemanda/(oArea.k+oArea.nCentrosActuales));
        oArea.AddCentro(imin,false);      	
        //Acceso a los vecinos mas cercanos del nodo optimo 
  	    for (short j=0; j < distancia.vecinos[nCentroId].nOrden ; j++){
             nSateliteId =  distancia.getOrden(nCentroId, j);
             nInd = oArea.Find(nSateliteId);
             if (nInd!=-1) {
                 if (!oArea.nodos[nInd].eliminado && capFicticia-oArea.nodos[nInd].demanda<0) {  
                	 break; 
                 }
    		     if (!oArea.nodos[nInd].eliminado) {
    		       	capFicticia=capFicticia-oArea.nodos[nInd].demanda;
    		        oArea.AddNodo(oArea.nodos[imin].clusterId,nInd); 
    		      	oArea.nodos[nInd].eliminado = true;
    		     }	
             }
		}
	}
	
	private void  IniActual(Areas oArea){
			
	 oArea.nCentrosActuales = 0; 		
	// Elimino la demanda cercana a los establecimientos actuales 	
	  for (short i=0; i < oArea.nNodos; i++) 
	       if (oArea.nodos[i].capActual > 0) {
	    	   oArea.AddCentro(i,true);
		       oArea.nodos[i].eliminado = true;
	    	   oArea.nCentrosActuales++; 
	       }; 

	  short nCentroId, nCapacidad; 
	// Para cada cluster escojo los nodos mas cercanos a un centro actual, hasta que se me acabe la capacidad
      for (short i=0; i < oArea.nCentrosActuales; i++) {
    	  System.out.printf("Centro Actual %d\n",i);
    	  nCentroId = oArea.nodos[ oArea.cluster[i].area_ind ].id;
    	  nCapacidad =  oArea.nodos[ oArea.cluster[i].area_ind ].capActual ;
    	  nCapacidad = (short) (nCapacidad - oArea.nodos[ oArea.cluster[i].area_ind ].demanda) ;
	      
    	  // Acceso a todos los satelites mas cercanos al centro
    	  if (nCapacidad > 0)
       	      for (short j=0; j < distancia.vecinos[nCentroId].nOrden ; j++){
                  short nSatelite =  distancia.getOrden(nCentroId, j);
                  short nInd = oArea.Find(nSatelite);
                  if (nInd !=-1){
          			 // Reduzco las demandas de los nodos en base a las capacidades de los centros actuales y en base a la cercanias de los nodos al centro actual
        		      if ( (nCapacidad - oArea.nodos[ nInd ].demanda)>= 0)  {
        	             nCapacidad = (short)(nCapacidad - oArea.nodos[ nInd ].demanda);  
                         oArea.AddNodo(i,nInd);
        		      	 oArea.nodos[nInd].eliminado = true;                     
        		      }
        		        else
        	             break;      	                  	  
                  }
              }	       
         }
 	}
	

	public boolean ValidaDatos() {
          return true; 		
	}
}
