package ec.gov.senplades.optimiza;

public class NodoItem {

    public short nodoId, distancia;
    public NodoItem siguiente ;
    
    public NodoItem() {
    	siguiente = null;
    }

    public NodoItem(short nNodoId, short nDistancia) {    	
    	nodoId=nNodoId; 
    	distancia=nDistancia;  
    	siguiente = null;
	} 

    
}
