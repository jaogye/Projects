package ec.gov.senplades.optimiza;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Pmediana_sinlimite extends Pmediana {

	public Pmediana_sinlimite(Distancia oDistancia, Areas oAreas) {
		super(oDistancia, oAreas);
		// TODO Auto-generated constructor stub
	}

	public boolean Optimiza( ){

		areas.nFObjetivo=0; 

		String dateFormat = "yyyy.MMMMM.dd GGG hh:mm aaa"; 
	    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	    Calendar cal1 = Calendar.getInstance();
	    String sFecIni =  sdf.format(cal1.getTime());   
		
		InicializaFactible(areas);
		int nRepeticion = 0, nIter =0; 
		int nCambioAnterior =0;  ; 
		areas.nCambio = 1; 
	    while (areas.nCambio > 0 && nIter < 30){
			nIter++; 
	    	this.Agrupa(areas);
	    	this.areas.GrabaAreas("c:/agrupa.txt"); 
	    	System.out.printf("Agrupa %f Tiempo Promedio %f Libres %d\n", areas.nFObjetivo, areas.nFObjetivo/areas.nDemanda, areas.nTotLibre);
	    	this.EstimaMediana(areas); 
	   		System.out.printf("Estima Mediana %f Cambios %d  Tiempo Promedio %f\n", areas.nFObjetivo, areas.nCambio, areas.nFObjetivo/areas.nDemanda);
	    	this.areas.GrabaAreas("c:/Estima.txt");
	    	if (nCambioAnterior == areas.nCambio) { 
               nRepeticion++; 
            } else {
              nRepeticion =0; 
             }
            nCambioAnterior = areas.nCambio ;   
            if (nRepeticion > 3)
         	   break; 
	    } 

	    this.areas.GrabaAreas("c:/Solucion.txt");
	    Calendar cal2 = Calendar.getInstance();
	    String sFecFin =  sdf.format(cal2.getTime());   
    	System.out.printf("Fecha Inicio %s Fecha Fin %s\n", sFecIni, sFecFin);	    
	    return true;  
	}
	
	public boolean ReOptimiza() {
	    Areas oVariante = areas.GetVariante();
        this.Agrupa(oVariante); 
	    return true ; 
	}
	
	public void  Agrupa(Areas oArea){
	 	oArea.ClearCluster();
	 	oArea.nFObjetivo =0; 
	 	oArea.nTotLibre=0;
		for (short i=0; i < oArea.nNodos; i++) {
			if (!oArea.nodos[i].centro) {
	 	         // Para cada satelite escojo el centro mas cercano
		    	 short nSateliteId = oArea.nodos[i].id; 
		    	 short nCentroInd = oArea.getCentroMasCercano(oArea.nodos[i].id); 
		    	 if (nCentroInd !=-1) {
			    	 short nCentroId = oArea.nodos[nCentroInd].id;
			    	 short nCluster = oArea.nodos[nCentroInd].clusterId;
                     double nDistancia = distancia.Dista(nCentroId, nSateliteId);                     
                     oArea.nodos[i].distacen = (float) nDistancia ; 
                     oArea.nFObjetivo = oArea.nFObjetivo + oArea.nodos[i].demanda * nDistancia ;

		    	     oArea.AddNodo(nCluster, i);
		    	 }
		    	 else { 
		    		 oArea.nTotLibre++ ;
		    		 oArea.nodos[i].clusterId=-1; 
		    	 }
		     }
		}	
	}
	
	
	private void EstimaMediana(Areas oArea){
	 	
		short nCentrosActuales = oArea.nCentrosActuales, nCentroMin=-1;
		double  nDistancia;
		oArea.nFObjetivo=0;
		oArea.nCambio =0;		
		
		// Calculo la funcion objetivo para los centros actuales
		for (int nCluster=0; nCluster < nCentrosActuales; nCluster++) {
			for (short nCentro : oArea.cluster[nCluster].ListaNodo) {
				oArea.nFObjetivo = oArea.nFObjetivo + oArea.nodos[nCentro].distacen * oArea.nodos[nCentro].demanda;     
			}
		}
			
		for (short nCluster=nCentrosActuales; nCluster < nCentrosActuales+oArea.k; nCluster++) {
			double nMinimo=999999999; 
			for (short nCentroInd : oArea.cluster[nCluster].ListaNodo) {				
				nDistancia = oArea.getTotalDistancia(nCluster, nCentroInd);
	            if (nDistancia < nMinimo) {
	            	nMinimo = nDistancia; 
	            	nCentroMin = nCentroInd; 
	            }
			}
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


