package ec.gov.senplades.optimiza;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/*
 * This program finds new locations of new facilities that minimize the total time spent by families from
 * their homes to the facilities (new and existing facilities) (p-median model wirh boundaries). 
 * This model assumes that the capacities of new facilities are bounded  
 * 
 */



public class Pmediana_conlimite extends Pmediana {

	
	public Pmediana_conlimite(Distancia oDistancia, Areas oAreas) {
		super(oDistancia, oAreas);
		// TODO Auto-generated constructor stub
	}

	
	public boolean Optimiza( ){
				
		String dateFormat = "yyyy.MMMMM.dd GGG hh:mm aaa"; 
	    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	    Calendar cal1 = Calendar.getInstance();
	    String sFecIni =  sdf.format(cal1.getTime());   
            	
        if (!this.ValidaDatos())
	  	    return false; 
        	
        areas.nFObjetivoMin=999999999;
        areas.nFObjetivo=0; 
		InicializaFactible(areas); 
		
		int nCambioAnterior =0; 
		int nRepeticion = 0, nIter =0 ; 
		areas.nCambio = 1; 
		while (areas.nCambio > 0 && nIter < 30 ){
			nIter++; 
	    	if ( this.Gap(areas)) {
	    	   areas.GrabaAreas("c:/Agrupa.txt"); 
 	    	   System.out.printf("Agrupa %f Tiempo Promedio %f Libres %d\n", areas.nFObjetivo, areas.nFObjetivo/areas.nDemanda, areas.nTotLibre);
	    	   this.EstimaMediana(areas);      	
	    	   areas.GrabaAreas("c:/Estima.txt");
	   		   System.out.printf("Estima Mediana %f Cambios %d  Tiempo Promedio %f\n", areas.nFObjetivo, areas.nCambio, areas.nFObjetivo/areas.nDemanda);
	    	   if (nCambioAnterior == areas.nCambio) { 
            	   nRepeticion++; 
               } else {
            	   nRepeticion =0; 
               }
               // Si ha mejorado la funcion objetivo, entonces guarda esta solcuion 
	    	   if (areas.nFObjetivo < areas.nFObjetivoMin) {
	    		   areas.nFObjetivoMin = areas.nFObjetivo; 
	    		   this.areas.GuardaOptimo(); 
	    	   }
	    		   
               nCambioAnterior = areas.nCambio ;   
               if (nRepeticion > 3)
            	   break; 
	       }
	    	else {
		   		   System.out.printf("Solucion no factible"); 
	    		return false;	
	    	}
	    	  
	    } 

		areas.RestauraOptimo(); 
		areas.nFObjetivo = areas.nFObjetivoMin ;
		this.IntercambioTransferencia(areas);
 	    if (areas.nFObjetivo < areas.nFObjetivoMin && areas.nFObjetivo>= 0) {
 	    	areas.nFObjetivoMin = areas.nFObjetivo; 
		    areas.GuardaOptimo(); 
	    }
		System.out.printf("Intercambio Transferencia %f Tiempo Promedio %f\n", areas.nFObjetivo, areas.nFObjetivo/areas.nDemanda);
	    Calendar cal2 = Calendar.getInstance();
	    String sFecFin =  sdf.format(cal2.getTime());   
    	System.out.printf("Fecha Inicio %s Fecha Fin %s\n", sFecIni, sFecFin);

    	return true; 
	  
	}	    
	    
	public boolean ReOptimiza() {
	    Areas oVariante = areas.GetVariante();
        boolean Factible = this.Gap(oVariante);
        if (Factible)
        	this.IntercambioTransferencia(oVariante);
        System.out.printf("Reoptimizacion %f Tiempo Promedio %f\n", oVariante.nFObjetivo, oVariante.nFObjetivo/oVariante.nDemanda);
        return Factible ; 
	}
	public boolean  Gap(Areas oArea){
	 	oArea.ClearCluster();
	 	oArea.nFObjetivo =0; 
	 	oArea.nTotLibre=0;

 	    ArrayList<Short> ListaSatelites; 
 	    ListaSatelites = oArea.GetNodos(false);
 	    short nCentroInd = -1 ; 
        int nCnt =0;
 	    while (!ListaSatelites.isEmpty()) {
 	    	  nCnt++; 
			  // Selecciona los nodos vecinos de acuerdo con la cercania del nodo principal            
			  short nSateliteInd = this.AsignaSateliteCentroFactible(oArea, ListaSatelites); 
	 	      if (nSateliteInd !=-1) {
	 	    	  nCentroInd = oArea.cluster[oArea.nodos[nSateliteInd].clusterId].area_ind;  
	 	    	  short nSateliteId = oArea.nodos[nSateliteInd].id; 
                  short nCentroId = oArea.nodos[nCentroInd].id ;  
                  double nDistancia = distancia.Dista(nSateliteId, nCentroId);  
                  short nDemanda = oArea.nodos[nSateliteInd].demanda; 
    	 	      oArea.nodos[nSateliteInd].distacen = (float) nDistancia ;
    	 	      oArea.nFObjetivo = oArea.nFObjetivo + nDemanda * nDistancia ; 
	 	    	 }
		    	 else  
		    		 oArea.nTotLibre++ ;

	 	    	//System.out.printf("Asigna Satelite-Centro- Iteracion %d Satelite %d Centro %d \n", nCnt, nCentroInd, nSateliteInd); 
		}
		
	 	// Mejoramiento de la solucion factible
		/*for (short i=0; i < areas.nNodos; i++) {
	    	short nSateliteId = areas.nodos[i].id;
			if (!areas.nodos[i].centro) {
				//Busco el centro cercano que puede albergar al nodo
				nCentroInd = this.GetCentroFactibleAlterno(i);
 	    	    if (nCentroInd !=-1) {
 	    	    	System.out.printf("Mejora Solucion Satelite %d\n", i); 
			        short nCentroId = areas.nodos[nCentroInd].id;
			    	areas.RemoveNodo(areas.nodos[i].clusterId, i); 
			    	areas.AddNodo(areas.nodos[nCentroInd].clusterId, i); 			    	
			    	double nDistancia = distancia.Dista(nCentroId, nSateliteId);                     
		    		nFObjetivo = nFObjetivo + areas.nodos[i].demanda * (areas.nodos[i].distacen-nDistancia) ;
			    	areas.nodos[i].distacen = (float) nDistancia ;  
			   }
			  }
			}  */
		
		return true; 
	}
	
	
	private void EstimaMediana(Areas oArea){
	 	
		oArea.SetEstadisticasCluster();
 
		short nCentrosActuales = oArea.nCentrosActuales, nCentroMin=-1;
		double  nDistancia;
		oArea.nFObjetivo=0;
		oArea.nCambio =0;
		
		for (int nCluster=0; nCluster < nCentrosActuales; nCluster++) {
			for (short nCentro : oArea.cluster[nCluster].ListaNodo) {
				oArea.nFObjetivo = oArea.nFObjetivo + oArea.nodos[nCentro].distacen * oArea.nodos[nCentro].demanda;     
			}
		}		
		
		for (short nCluster=nCentrosActuales; nCluster < nCentrosActuales+oArea.k; nCluster++) {
			double nMinimo=999999999; 
			for (short nCentro : oArea.cluster[nCluster].ListaNodo) {				
                if (oArea.cluster[nCluster].nDemanda <= oArea.nodos[nCentro].capNuevo) {
				    nDistancia = oArea.getTotalDistancia(nCluster, nCentro);
	                if (nDistancia < nMinimo) {
	            	   nMinimo = nDistancia; 
	            	   nCentroMin = nCentro;
	                }   
	            }
			}

			
    		if (nCentroMin!=-1) {
    			oArea.nFObjetivo = oArea.nFObjetivo + nMinimo;
    			if (!oArea.nodos[nCentroMin].centro) {
    				oArea.nCambio++; 
    			    oArea.nodos[nCentroMin].centro = true; 
    			    oArea.nodos[oArea.cluster[nCluster].area_ind].centro = false;
    			    oArea.cluster[nCluster].area_ind = (short) nCentroMin; 
    		    } 
    		}
		}
	}

	
    public void IntercambioTransferencia(Areas oArea) {

        for (short nCluster_i =0; nCluster_i < oArea.nCluster; nCluster_i++) {
        	
        	for (short nCluster_j =0; nCluster_j < oArea.nCluster; nCluster_j++) {
                if (nCluster_i > nCluster_j) {
                	// Intercambio
                	for (Short i : oArea.cluster[nCluster_i].ListaNodo) {
						short nDemanda_i = oArea.nodos[i].demanda;
						double nDistancia_ij = distancia.Dista(oArea.nodos[i].id, oArea.nodos[oArea.cluster[nCluster_j].area_ind].id ); 
                		for (Short j : oArea.cluster[nCluster_j].ListaNodo) {
                    		short nDemanda_j = oArea.nodos[j].demanda;
    						double nDistancia_ji = distancia.Dista(oArea.nodos[j].id, oArea.nodos[oArea.cluster[nCluster_i].area_ind].id );
                    		if (nDemanda_j <= oArea.cluster[nCluster_i].capacidad+nDemanda_i && 
                    			nDemanda_i <= oArea.cluster[nCluster_j].capacidad+nDemanda_j && 
                    			nDemanda_i*(nDistancia_ij-oArea.nodos[i].distacen)+nDemanda_j*(nDistancia_ji-oArea.nodos[j].distacen) <0) {
                                oArea.cluster[nCluster_i].capacidad = (short) (oArea.cluster[nCluster_i].capacidad - nDemanda_j + nDemanda_i );                                   
                                oArea.cluster[nCluster_j].capacidad = (short) (oArea.cluster[nCluster_j].capacidad - nDemanda_i + nDemanda_j );
                                oArea.nodos[i].clusterId=nCluster_j;                                 
                                oArea.nodos[j].clusterId=nCluster_i;
                                oArea.nFObjetivo = oArea.nFObjetivo + nDemanda_i*(nDistancia_ij-oArea.nodos[i].distacen)+nDemanda_j*(nDistancia_ji-oArea.nodos[j].distacen) ;
                                if (oArea.nFObjetivo < 0){
                                	int hh=0; hh++; 
                                }
                    		}		
                		}
                	}
                }
                if (nCluster_i > nCluster_j) {
                    //Transferencia 
                	for (Short i : oArea.cluster[nCluster_i].ListaNodo) {
						short nDemanda_i = oArea.nodos[i].demanda;
						double nDistancia_ij = distancia.Dista(oArea.nodos[i].id, oArea.nodos[oArea.cluster[nCluster_j].area_ind].id ); 
                        if (nDemanda_i < oArea.cluster[nCluster_j].capacidad && 
                        	nDistancia_ij < oArea.nodos[i].distacen	) {                                   
                            oArea.cluster[nCluster_j].capacidad = (short) (oArea.cluster[nCluster_j].capacidad - nDemanda_i  );
                            oArea.cluster[nCluster_i].capacidad = (short) (oArea.cluster[nCluster_i].capacidad + nDemanda_i );
                            oArea.nodos[i].clusterId=nCluster_j;
                            oArea.nFObjetivo = oArea.nFObjetivo + nDemanda_i*(nDistancia_ij-oArea.nodos[i].distacen);
                            if (oArea.nFObjetivo < 0){
                            	int hh=0; hh++; 
                            }
                        }
                }
        	}   
         }  	
      }
	
    }

    // Se asigna el centro mas cercano, al nodo cuyo diferencia de distancias entre 
    // el centro mas cercano y el 2do centro mas cercano sea mayor
    private short AsignaSateliteCentroFactible(Areas oArea, ArrayList<Short> ListaSatelites){
    	
       short iMaxCentro = -1, iMaxSatelite=-1, iCentro=-1, nSateliteInd, nPos=0, iPos=0; 
       double dMaximo = -1; 
	   Iterator<Short> iter = ListaSatelites.iterator();
       while (iter.hasNext()) {
    	   nSateliteInd = iter.next(); 
           short nDemanda = oArea.nodos[nSateliteInd].demanda; 
           short nSatelite = oArea.nodos[nSateliteInd].id;
           double nMinimo = 999999999, nMinimo2 = 999999999 , nDistancia=0;  
           // Busco secuencialmente el centro mas cercano y luego el segundo centro
           // mas cercano y saco la diferencia de distancias

            short n =0; 
		  	for (n=0; n < distancia.vecinos[nSatelite].nOrden; n++) {
		        short nCentroInd = oArea.Find(distancia.getOrden(nSatelite, n));
		        if (nCentroInd!=-1) {
		            short nCluster = oArea.nodos[nCentroInd].clusterId;		        
			  		if (oArea.nodos[nCentroInd].centro && nDemanda <= oArea.cluster[nCluster].capacidad){
	                    nDistancia = distancia.Dista(nSatelite, oArea.nodos[nCentroInd].id); 
			  			nMinimo = nDistancia ;  
			  			iCentro = nCentroInd; 
			  			break;   
			  		}				        	
		        }
		  	}           
		  	n++; 
		  	for (short i=n; i < distancia.vecinos[nSatelite].nOrden; i++) {
		        short nCentroInd = oArea.Find(distancia.getOrden(nSatelite, i));
		        if (nCentroInd != -1 ) {
		            short nCluster = oArea.nodos[nCentroInd].clusterId;		        
			  		if (oArea.nodos[nCentroInd].centro && nDemanda <= oArea.cluster[nCluster].capacidad){
	                    nDistancia = distancia.Dista(nSatelite, oArea.nodos[nCentroInd].id);
			  			nMinimo2 = nDistancia ;  
			  			break;   
			  		}				        	
		        }
		  	}
		   if (nMinimo==999999999) {
               oArea.nodos[nSateliteInd].clusterId=-1;
               //No es posible satisfacer
               ListaSatelites.remove(nPos);                
			   return -1; 
		   } else if (nMinimo <999999999 && nMinimo2==999999999) {
			   // Si solo se ha encontrado un solo centro factible cercano
			   // Asigno directamente este centro al nodo satelite
			   oArea.nodos[nSateliteInd].clusterId = iCentro; 
			   if (iCentro==0) {
				   int hh=0; hh++; 
			   }
			   
			   
			   iMaxCentro = iCentro;
			   iMaxSatelite = nSateliteInd; 
			   iPos = nPos ; 
               break;                
			   
		   } else if (nMinimo < 999999999 && nMinimo2 < 999999999) {
			   double d = nMinimo2 - nMinimo; 
			   if (d > dMaximo) {
				   dMaximo = d; 
				   iMaxCentro = iCentro;
				   iMaxSatelite = nSateliteInd;
				   iPos = nPos ; 
			   }
		   }
		nPos++;    
	 }
     ListaSatelites.remove(iPos); 
     
     short nCentro = oArea.nodos[iMaxCentro].clusterId;   
     oArea.cluster[nCentro].capacidad = (short) (oArea.cluster[nCentro].capacidad - oArea.nodos[iMaxSatelite].demanda) ;
     oArea.AddNodo(nCentro, iMaxSatelite); 
     return iMaxSatelite; 
    }

    // Este metodo encuentra, si existe, un centro diferente al centro del nodod nSateliteInd
    // tal que la asignacion del nodos nInd a este nuevo centro disminuya la distancia 
    // total del problema y este nuevo centro tenga capacidad para albergar al nodo nInd
    private short GetCentroFactibleAlterno(Areas oArea, short nSateliteInd) {

    	   short nCentro = oArea.nodos[nSateliteInd].clusterId; 
    	   short nSatelite = oArea.nodos[nSateliteInd].id;
    	   short nDemanda = oArea.nodos[nSateliteInd].demanda;
  	  	   for (short n=0; n < distancia.vecinos[nSatelite].nOrden; n++) {
		        short nInd = oArea.Find(distancia.getOrden(nSatelite, n));
		        if (nInd!=-1){
			        short nCentroAlt = oArea.nodos[nInd].clusterId; 
			        if (nCentroAlt!=-1) {
			        	// Si se alcanza al centro actual como vecino significa que no�
			        	// existe otro centro mas cercano 
	                    if (nCentroAlt==nCentro)
	                    	return -1; 
			        	
			        	short nCapacidad = oArea.cluster[nCentroAlt].capacidad; 
				  		if (oArea.nodos[nInd].centro && nCentro!=nInd && nDemanda <= nCapacidad  ){
				  			return nInd; 
				  		}				        	
			        }		        	
		        }
		  	}
  	  	   return -1 ;        	    	 
    }

	public boolean ValidaDatos(Areas oArea) {
		
        // Validacion si la capacidad instalada es suficiente para satisface a la demanda
	    double nCapPromedio = oArea.nTotCapacidad/oArea.nNumCentros, nDemPromedio = oArea.nDemanda/oArea.k ; 
        // Si la demanda supera a la capacidad
	    if (nDemPromedio > nCapPromedio*0.88)

	    	return false ;  
	    else	
	    	return true ;
	    	
	    
	    
    }
    
}