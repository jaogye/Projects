package ec.gov.senplades.optimiza;

public class ListaVecinos {

	public final static int MaxDistancia = Distancia.rMax; 
	NodoItem aNodos[]; 
	public final int nNodos = 211;
	public short aOrden[]; 
	short nOrden =0; 
	
	public ListaVecinos(short MaxOrden) {
		aNodos = new NodoItem[nNodos]; 
		for (int i=0; i < nNodos; i++)
		     aNodos[i] = null;
		aOrden = new short[MaxOrden]; 
	}

	
    public void AddNodo(short nNodoId, short nDistancia) {
    	int nInd = nNodoId % nNodos ;
    	NodoItem oNodo;  
    
    	if (aNodos[nInd]==null)
    	   aNodos[nInd] = new NodoItem(nNodoId,nDistancia); 
    	else {
    	  oNodo = new NodoItem(nNodoId,nDistancia);
    	  oNodo.siguiente = aNodos[nInd]; 
    	  aNodos[nInd] = oNodo; 
    	}
    	// Cargo los vecinos mas cercanos
        aOrden[nOrden]= nNodoId; 	
    	nOrden++;  
	} 

    public double GetDistancia(short nOrd)  {
    	
    	int nInd = nOrd % nNodos ;
    	NodoItem oNodo = aNodos[nInd]; 
    	if (oNodo==null)
    		return  MaxDistancia;
    	else {
    	 if (oNodo.nodoId==nOrd)	
    		 return 2898 *((double) oNodo.distancia+32768)/65535; 
         while (oNodo!=null) {
        	oNodo =oNodo.siguiente;
        	if (oNodo==null)
        		return  MaxDistancia; 
        	else if (oNodo.nodoId==nOrd) 
        		return 2898 *((double)oNodo.distancia+32768)/65535;
         } 		
   		 return  MaxDistancia; 
    	}  
    }    
 
}
