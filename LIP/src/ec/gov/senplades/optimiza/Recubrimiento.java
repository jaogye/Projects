package ec.gov.senplades.optimiza;

import java.util.ArrayList;
import java.util.Iterator;

public class Recubrimiento  implements HeuristicaLocalizacion {

	public Distancia distancia; 
	public  Areas areas ;
	public double nRadio; 
	
	public Recubrimiento(Distancia oDistancia, Areas oAreas) {
		distancia = oDistancia ;
		areas=oAreas ;
	}
	
	public boolean Optimiza(double nRadio) {

		  this.nRadio = nRadio ; 
		  //Colocacion de los clusters
		  ArrayList<Short> ListaNodos; 		
		  if (areas.actual) {
			  // Ordenos los centros actuales de acuerdo al tamaño de su capacidad de menor a mayor
              this.IniActual(areas);			  
		  }

	      int nDemanda = 0 ; 
	      for (int i=0; i < areas.nNodos; i++) {
	          if (areas.nodos[i].clusterId!= -1)
	              nDemanda = nDemanda + areas.nodos[i].demanda;   
	      }
	     
	      double nCobertura = (double) ((double) nDemanda/(double)areas.nDemanda); 
	      System.out.printf("Total %d Demanda %d  Cobertura %f Cluster %d\n", areas.nDemanda, nDemanda, nCobertura, areas.nCluster);

		  
		  ListaNodos = areas.GetNodos(); 
		  areas.nCluster=areas.nCentrosActuales;
		  while (this.ColocaCluster(areas, areas.nCluster, ListaNodos )) { 
				ListaNodos = areas.GetNodos();  
		 }
		  areas.SetEstadisticasCluster();     
		  ListaNodos = areas.GetNodos();
		  if (areas.oParametro.nCapMin> 0) {
		      while (this.ColocaClusterComplementarios(areas, areas.nCluster, ListaNodos)) {
			      ListaNodos = areas.GetNodos();
		      }
		  }
	     
        areas.SetEstadisticasCluster();
        nDemanda = 0 ; 
        for (int i=0; i < areas.nNodos; i++) {
        	if (areas.nodos[i].clusterId!= -1)
               nDemanda = nDemanda + areas.nodos[i].demanda;   
        }
     
        nCobertura = (double) ((double) nDemanda/(double)areas.nDemanda); 
        System.out.printf("Total %d Demanda %d  Cobertura %f Cluster %d\n", areas.nDemanda, nDemanda, nCobertura, areas.nCluster);
    
        return true ;
      }


	public boolean Optimiza() {
		return Optimiza(areas.oParametro.nRadio); 
	}
	
	public boolean ReOptimiza(){
		return ReOptimiza(areas.oParametro.nRadio); 
	}
	
	public boolean ReOptimiza(double nRadio){
        this.nRadio=nRadio ; 
 		System.out.printf("Reoptimiza \n");
	    Areas oVariante = areas.GetVariante(false);
		// Accceso solo los centros establecidos 
        int nDemanda, nTotal =0; 
        short nCentroId, nSateliteId, nInd, k ;
        double nDista ;
        // Trato de determinar zonas de influencia que por lo menos satisfaga la capacidad minima de cada nuevo centro 
        for (int j=this.areas.nCluster; j < oVariante.nCluster; j++ ) {
            //Acceso a los vecinos mas cercanos del nodo optimo 
            nCentroId =oVariante.nodos[oVariante.cluster[j].area_ind].id;
            nDemanda = oVariante.nodos[oVariante.cluster[j].area_ind].demanda; 
      	    for (k=0; k < distancia.vecinos[nCentroId].nOrden ; k++){
                 nSateliteId = distancia.getOrden(nCentroId, k);
                 nDista =distancia.Dista(nSateliteId, nCentroId); 
     		     if (nDista <= this.nRadio) {             
                     nInd = oVariante.Find(nSateliteId);                            
                     if (nInd!=-1) {
                         // Solo asigna a este nuevo centro areas censales no asginadas a ningun cluster 
                   		 if  (oVariante.nodos[nInd].clusterId ==-1) { 
                   			 if (nCentroId != nSateliteId)
                   			    nDemanda = nDemanda + oVariante.nodos[nInd].demanda ;
       		                 oVariante.AddNodo(oVariante.nodos[j].clusterId,nInd); 
        		      	     oVariante.nodos[nInd].eliminado = true;
        		      	     oVariante.nodos[nInd].distacen = (float) nDista ; 
                        }
        		         if (nDemanda >= oVariante.oParametro.nCapMin) {
                            break; 
        		         }   
                     }
     		      } else
     		        break;
      	     }        	
        }
        
        // Trato de maximizar la cobertura de las  zonas de influencia  
        for (int j=this.areas.nCluster; j < oVariante.nCluster; j++ ) {
            //Acceso a los vecinos mas cercanos del nodo optimo 
            nCentroId =oVariante.nodos[oVariante.cluster[j].area_ind].id;
            nDemanda = oVariante.nodos[oVariante.cluster[j].area_ind].demanda;            
      	    for (k=0; k < distancia.vecinos[nCentroId].nOrden ; k++){
                 nSateliteId = distancia.getOrden(nCentroId, k);
                 nDista =distancia.Dista(nSateliteId, nCentroId); 
     		     if (nDista <= this.nRadio) {             
                     nInd = oVariante.Find(nSateliteId);                            
                     if (nInd!=-1) {
                         // Solo asigna a este nuevo centro areas censales no asginadas a ningun cluster 
                   		 if  (oVariante.nodos[nInd].clusterId ==-1 && nDemanda + oVariante.nodos[nInd].demanda <= oVariante.oParametro.nCapMax) { 
                   			 if (nCentroId != nSateliteId)
                   			    nDemanda = nDemanda + oVariante.nodos[nInd].demanda ;
       		                 oVariante.AddNodo(oVariante.nodos[j].clusterId,nInd); 
        		      	     oVariante.nodos[nInd].eliminado = true;
        		      	     oVariante.nodos[nInd].distacen = (float) nDista ; 
                        } else 
                           break; 
                     }
     		      } else
     		        break;
      	     }        	
        }

        for (int i=0; i < oVariante.nNodos; i++) {
        	if (oVariante.nodos[i].clusterId!= -1)
               nTotal = nTotal + oVariante.nodos[i].demanda;   
        }
        
  	    double nCobertura = (double) ((double) nTotal/(double)areas.nDemanda);
  	    System.out.printf("Total %d Demanda %d  Cobertura %f Cluster %d\n", areas.nDemanda, nTotal, nCobertura, areas.nCluster);
  	    oVariante.GrabaAreas("c:/colegios2.txt");
		return true ;	         
	}
	
	public boolean ColocaCluster(Areas oArea, int n, ArrayList<Short> ListaNodos) {	
		
		short imax = -1, nInd,nSateliteId; 

		double nDista =0; 
		Iterator<Short> iter = ListaNodos.iterator();
		int nDemanda =0, nDemandaMax = 0; 
		while( iter.hasNext()) {
		    short i=iter.next();
		    short nCentroId = oArea.nodos[i].id; 

		    if (oArea.nodos[i].capNuevo>0 ){
		       nDemanda = oArea.nodos[i].demanda;    
               //Acceso a los vecinos mas cercanos 
 	  	       // Omito el primer vecino, ya que es el mismo centro, este ya sumado su demanda
			   for (short j=0; j < distancia.vecinos[nCentroId].nOrden ; j++){
				    nSateliteId =  distancia.getOrden(nCentroId, j);
				    nDista = distancia.Dista(nSateliteId, nCentroId); 
				    if (nDista <= this.nRadio) {
				    	nInd = oArea.Find(nSateliteId); 
	                    if (nInd!=-1 && nCentroId != nSateliteId ) {
	    			        if (!oArea.nodos[nInd].eliminado ) {
	    			        	nDemanda = nDemanda + oArea.nodos[nInd].demanda;    			        	
	    			        	//System.out.printf("Nodo %d  Demanda %d \n", nSateliteId, areas.nodos[nInd].demanda );	        	
	    			        }	                    	
	                    }				    	
				    }
			   }			   
			   
			   if (nDemanda > nDemandaMax){
				   nDemandaMax = nDemanda; 
				   imax=i; 
			   }
			}
		}		
		// Si no se encuentra cluster que tenga al menos una demanda necesaria para llenar una capacidad minima
		if (nDemandaMax < oArea.oParametro.nCapMin || imax==-1) {
			return false;  
		}

        short nCentroId = oArea.nodos[imax].id;
        System.out.printf("Nª %d Centro Fijo : %s  Demanda %d  \n", n,  oArea.nodos[imax].sCodigo, nDemandaMax);
        nDemanda = oArea.nodos[imax].demanda;  
        oArea.AddCentro(imax,false);      	
        //Acceso a los vecinos mas cercanos del nodo optimo 
  	    // Omito el primer vecino, ya que es el mismo centro, este ya sumado su demanda
        for (short j=0; j < distancia.vecinos[nCentroId].nOrden ; j++){
             nSateliteId =  distancia.getOrden(nCentroId, j);
             nDista = distancia.Dista(nSateliteId, nCentroId); 
 		     if (nDista <= this.nRadio ) {             
                 nInd = oArea.Find(nSateliteId);                                  
                 if (nInd!=-1 && nCentroId != nSateliteId) {
      		        if (!oArea.nodos[nInd].eliminado && nDemanda + oArea.nodos[nInd].demanda <= oArea.oParametro.nCapMax) {
                        nDemanda = nDemanda + oArea.nodos[nInd].demanda ;     
    		    	    oArea.AddNodo(oArea.nodos[imax].clusterId,nInd); 
    		      	    oArea.nodos[nInd].eliminado = true;
    		      	    oArea.nodos[nInd].distacen = (float) nDista ; 
    		        }	
                }
 		     }
  	    }
  	    return true ; 
	}
	
	public boolean ColocaClusterComplementarios(Areas oArea, int n, ArrayList<Short> ListaNodos) {

		double nDista ; 
        int nInvasion, nInvasionMin=999999999, nDemanda,  nDemandaOpt=0; 
		short nClusterId, imin = -1, nInd,nSateliteId, iCentroAlterno; 
		Iterator<Short> iter = ListaNodos.iterator();

		while( iter.hasNext()) {
			// Actualizacion de la demanda 
			for (short i=0; i < oArea.nCluster; i++) {
				oArea.cluster[i].nDemanda2=oArea.cluster[i].nDemanda; 
			}
			
			for (short i=0; i < oArea.nNodos; i++)
				oArea.nodos[i].escogido=true; 
			
			short i=iter.next();		    
		
			short nCentroId = oArea.nodos[i].id;
		    nDemanda =oArea.nodos[i].demanda;
		    nInvasion = 0 ; 
			for (short j=0; j < distancia.vecinos[nCentroId].nOrden ; j++){
			     nSateliteId =  distancia.getOrden(nCentroId, j);
			     nDista = distancia.Dista(nSateliteId, nCentroId);   
			     if (nDista <= this.nRadio) {
				     nInd = oArea.Find(nSateliteId); 
	                 if (nInd!=-1 ) {
		                 if (!oArea.nodos[nInd].centro && nCentroId != nSateliteId && (oArea.nodos[nInd].clusterId >= oArea.nCentrosActuales || oArea.nodos[nInd].clusterId ==-1))
		                    nDemanda = nDemanda + oArea.nodos[nInd].demanda;
		                	// Verifico si el cluster de este nodo esta definido 
		                 if (!oArea.nodos[nInd].centro && oArea.nodos[nInd].clusterId >= oArea.nCentrosActuales ) {
		                	 nInvasion = nInvasion + oArea.nodos[nInd].demanda;
		                	 nClusterId = oArea.nodos[nInd].clusterId;
		                	 //Seteo el atributo de escogido para evitar que escoga este nodo en esta iteracion 
		                	 oArea.nodos[nInd].escogido=true; 
	                	     oArea.cluster[nClusterId].nDemanda2 = (short) (oArea.cluster[nClusterId].nDemanda2 -  oArea.nodos[nInd].demanda);
                            //Si se viola la factibilidad de demnda minima del cluster nClusterId
	                	    if (oArea.cluster[nClusterId].nDemanda2 < oArea.oParametro.nCapMin) {
                                nInvasion = 999999999;  
	                	        break ; 
	                		}
	                	    // Verifico si se viola la factibilidad de radio maximo del cluster nClusterId
	                	    if (oArea.nodos[nInd].centro) { 
	                	    	iCentroAlterno = this.ReubicaCentro(oArea, nClusterId, nInd); 
	                	    	if (iCentroAlterno ==-1){
                                   nInvasion = 999999999;  
	                	           break;  }  
	                	          else
	                	          oArea.nodos[iCentroAlterno].escogido=true;  
	                	        
	                	    } 	
		                }
	    			        //System.out.printf("Nodo %d  Demanda %d Distotal %f\n", nSateliteId, areas.nodos[nInd].demanda, nDistaTotal );	        		                    	
	                 }				    	
                     // Si alcanza fuera de los limites del radio minimo
	                 else
	                	break;  
				 }
                 // Si se completa la capacidad minima
			     if (nDemanda>=oArea.oParametro.nCapMin) {
			    	 break; 
			     }
			   }			   

			 if (nInvasion < nInvasionMin && nDemanda >= oArea.oParametro.nCapMin){
			 	nInvasionMin = nInvasion; 
				imin=i; 
				nDemandaOpt = nDemanda ; 
			  }
		}    
		
		if (imin==-1)
			return false ; 
		
		// Eliminacion del nodo optimo y sus satelites 
        short nCentroId = oArea.nodos[imin].id;

        //nDemanda =oArea.nodos[imin].demanda;   
        nDemanda =oArea.nodos[imin].demanda;
        System.out.printf("Nª %d Centro Complementario : %s  Demanda %d  \n", n, oArea.nodos[imin].sCodigo, nDemandaOpt);
        oArea.AddCentro(imin,false);      	
        //Acceso a los vecinos mas cercanos del nodo optimo 
  	    for (short j=0; j < distancia.vecinos[nCentroId].nOrden ; j++){
             nSateliteId = distancia.getOrden(nCentroId, j);
             nDista =distancia.Dista(nSateliteId, nCentroId); 
 		     if (nDista <= this.nRadio) {             
                 nInd = oArea.Find(nSateliteId);                            
                 if (nInd!=-1) {
                     // Si estoy invadiendo un cluster vecino, entonces reduzco la demanda del cluster invadido
   		             if (!oArea.nodos[nInd].centro && oArea.nodos[nInd].clusterId < oArea.nCluster-1 && oArea.nodos[nInd].clusterId >= oArea.nCentrosActuales) {
      		             nClusterId = oArea.nodos[nInd].clusterId;
      		             oArea.cluster[nClusterId].nDemanda=(short) (oArea.cluster[nClusterId].nDemanda - oArea.nodos[nInd].demanda) ;
      		        	 oArea.RemoveNodo((short)nClusterId, nInd);
                         // Reubico el nuevo centro del clusterId
      		             if (oArea.nodos[nInd].centro) {
      		            	 short nCentroInd = this.ReubicaCentro(oArea, nClusterId, nInd); 
      		        		 oArea.nodos[nCentroInd].centro=true;
      		       		     oArea.cluster[nClusterId].area_ind= nCentroInd; 
      		        		 System.out.printf("Reubicación del centro del cluster %d, al nodo %d \n", nClusterId, nCentroInd);
      		        	 }
      		        	 if (oArea.cluster[nClusterId].nDemanda < oArea.oParametro.nCapMin) {
                             int nn = 0; 
      		        		 nn++; 
      		        	  }      		        		
      		       	  }      		        	    
               		 if  (!oArea.nodos[nInd].centro && oArea.nodos[nInd].clusterId >= oArea.nCentrosActuales || oArea.nodos[nInd].clusterId ==-1) { 
               			 if (nCentroId != nSateliteId)
               			    nDemanda = nDemanda + oArea.nodos[nInd].demanda ;
   		                 oArea.AddNodo(oArea.nodos[imin].clusterId,nInd); 
    		      	     oArea.nodos[nInd].eliminado = true;
    		      	     oArea.nodos[nInd].distacen = (float) nDista ; 
                    }
    		         if (nDemanda >= oArea.oParametro.nCapMin) {
                        break; 
    		         }   
                 }
 		      } else
 		        break;
  	     }
				
		return true ; 
	}
	
	private void  IniActual(Areas oArea){

         double nDista ; 		
		 short i2; 
		 short nCentroId, nCapacidad; 		 
		 oArea.nCentrosActuales = 0; 		
         oArea.OrdenaClusterActual();
		 // Elimino la demanda cercana a los establecimientos actuales 	
		  for (short i=0; i < oArea.nClusterActual; i++) {
	  
              i2 = oArea.ClusterActual[i]; 
     	      oArea.AddCentro(i2,true);
			  oArea.nodos[i2].eliminado = true;
		      oArea.nCentrosActuales++; 
  		      // Para cada cluster escojo los nodos mas cercanos a un centro actual, hasta que se me acabe la capacidad
	    	  System.out.printf("Centro Actual %d\n",i);
	    	  nCentroId = oArea.nodos[ i2 ].id;
	    	  nCapacidad =  oArea.nodos[ i2 ].capActual ;
	    	  nCapacidad = (short) (nCapacidad - oArea.nodos[ i2 ].demanda) ;

	    	  // Acceso a todos los satelites mas cercanos al centro
	    	  if (nCapacidad > 0)
	       	      for (short j=0; j < distancia.vecinos[nCentroId].nOrden ; j++){
	                  short nSateliteId =  distancia.getOrden(nCentroId, j);
					  nDista = distancia.Dista(nSateliteId, nCentroId); 
					  if (nDista <= this.nRadio) {
	                     short nInd = oArea.Find(nSateliteId);
	                     if (nInd !=-1 && nCentroId != nSateliteId){
	                    	 if (!oArea.nodos[nInd].eliminado )
	          			        // Reduzco las demandas de los nodos en base a las capacidades de los centros actuales y en base a la cercanias de los nodos al centro actual
	        		            if ( (nCapacidad - oArea.nodos[ nInd ].demanda)>= 0)  {
	        	                    nCapacidad = (short)(nCapacidad - oArea.nodos[ nInd ].demanda);  
	        	                    //nCapacidad = 9999; 
	        	                   // 
	        	                    oArea.AddNodo(i,nInd);
	        		      	        oArea.nodos[nInd].eliminado = true;                     
	        		             }
	        		             else {
	        		              // Hago una asignacion fraccionaria al cluster 	 
                                  //oArea.nodos[nInd].AddClusterFraccion(new AsignacionFraccionaria(i,nCapacidad) ); 
                                  // oArea.nodos[ nInd ].demanda = nCapacidad;  
	        	                  break; 
	                             }
	                      }
					  }   
	              }	       
	         }
	 	}

	@Override
	public boolean ValidaDatos() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private short ReubicaCentro(Areas oArea, short nCluster, short nCentroIndOld){
	
		short nCentroInd; 
		double nDistaMin =99999999, nDistancia =0;
		short imin=-1; 		
	    for (int i=0; i < oArea.cluster[nCluster].ListaNodo.size();  i++ ){
	        nCentroInd = oArea.cluster[nCluster].ListaNodo.get(i);
	        if (nCentroInd != nCentroIndOld && !oArea.nodos[nCentroInd].escogido) {
                nDistancia = oArea.getMaxDistancia(nCluster, nCentroInd); 
	            if (nDistancia < nDistaMin) {
	           	    nDistaMin = nDistancia ;
	           	    imin = nCentroInd; 
	            }
	        }
	    }   
        if (nDistaMin > oArea.oParametro.nRadio)
	       imin = -1 ; 
	    return imin; 
	}
		
}
