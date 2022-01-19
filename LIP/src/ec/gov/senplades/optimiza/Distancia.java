package ec.gov.senplades.optimiza;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Distancia {

    private final  String sFile = "datos/distancia.txt" ; 
    private final int TotNodos = 32104;
    public static final int rMin=0, rMax=3000;
    public Areas areas; 
    
    ListaVecinos vecinos[]; 
    
	public Distancia(){
		vecinos = new  ListaVecinos[TotNodos]; 
		this.CargaDistancia();
	//	this.CargaDistancia(); 
	}
	
	private void CargaDistancia(){		
		   Scanner s = null;
		   short nOrd1, nOrd2, nDistancia ;
		   double nDistancia2;
		   try {
		      s = new Scanner(new BufferedReader(new FileReader(sFile)));
	          // Salto la primera linea del archivo 
		      s.next(); 	      	      
		      s.useDelimiter("\r\n|,");
		      int i=0; 
		      while (s.hasNext() ) {
		    	  // Salto la primera columna
		    	  nOrd1 = Short.parseShort(s.next());
		    	  nOrd2 = Short.parseShort(s.next());
		    	  nDistancia2 = Double.parseDouble(s.next());
		    	  nDistancia = (short) Math.floor(nDistancia2*22.613872 - 32768); 
		    	  if (vecinos[nOrd1]==null)
		    		  vecinos[nOrd1] = new ListaVecinos((short) 301); 
		    	  vecinos[nOrd1].AddNodo(nOrd2, nDistancia);    
                  i++; 
                  if (i % 1000 == 0)
		    	     System.out.println( Runtime.getRuntime().freeMemory());	            
		      }
		      System.out.println("Fin Carga Distancia");
		   } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		      finally {
		        if (s != null) {
		        s.close();
		      }
		   }	
	}

	
	private void CargaDistancia2() {
	
	    Connection conn = null;
        try {
           conn = DriverManager.getConnection("jdbc:oracle:thin:@aquiles.senplades.int:1521:OLTPD", "LIP", "LIP");
           Statement stmt;
           stmt = conn.createStatement();
           String sQuery = "select a.orden ord1, b.orden ord2, d.distancia from lip_distancia d, lip_areacensal a, lip_areacensal b " +
       		" where d.AMBITO_GEOGRAFICO_DESDE=a.ambito_id and D.AMBITO_GEOGRAFICO_HASTA=b.ambito_id and d.orden < 300 " + 
    		"order by a.orden, d.distancia ";  
           
           ResultSet rset = stmt.executeQuery(sQuery);                 
        short nOrd1, nOrd2, nDistancia2;
		double nDistancia;
		int i=0; 
        while (rset.next()) {
         	i++;        	
	    	nOrd1 = rset.getShort(1); 
	    	nOrd2 = rset.getShort(2);
	    	nDistancia = rset.getDouble(3); 
	        if (nDistancia > rMax)
	        	nDistancia = rMax; 
     	
	        nDistancia2 = (short) Math.floor(nDistancia*22.613872 - 32768); 
    	    if (vecinos[nOrd1]==null)
	    	   vecinos[nOrd1] = new ListaVecinos((short)301); 
	    	vecinos[nOrd1].AddNodo(nOrd2, nDistancia2);            	
           	System.out.printf ("Area %d Memoria %d Reg %d \n", nOrd1, Runtime.getRuntime().freeMemory() , i); 
        }
           
        stmt.close();
	    } catch (SQLException e) {
          e.printStackTrace();
	    }
       try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
 		   e.printStackTrace();
		}  
	}
		
	

  public double Dista(short nOrd1, short nOrd2){
	
	  double nDista ; 
	  if (nOrd1 != nOrd2) {
	      nDista =  vecinos[nOrd1].GetDistancia(nOrd2);
	      if (nDista==rMax)
	    	  nDista =  vecinos[nOrd2].GetDistancia(nOrd1);
	  }
	   else 
	       nDista = 0;
	  return nDista ; 
  }
  
  public short getOrden(short  nCentroId, short n) {
	  return vecinos[nCentroId].aOrden[n];  
  }
	  
  public double getMaxDista(short  nCentroId) {

      short nId =  this.getOrden(nCentroId, (short)(vecinos[nCentroId].nOrden-1));
	  return this.Dista(nCentroId,nId) ;  
  }
  
  public void ExpandeDistancias(){
	  int n=0; 
	  short z=0; 
	  double nDista ; 
      for (int i=0; i < areas.nNodos; i++) {
    	  if (areas.nodos[i].sSector.equals("U") && areas.nodos[i].capActual> 0) {
		      System.out.printf("Expandiendo distancias del area censal %s \n", areas.nodos[i].sCodigo ); 
    		  n = this.ExpandeDistancia(areas.nodos[i].id, 8000); 
              if (areas.nodos[i].sCodigo.equals("090150166011") || areas.nodos[i].sCodigo.equals("170150144010")) {
                  short nCentroId = areas.nodos[i].id;  
            	  System.out.printf("Centro %s \n",areas.nodos[i].sCodigo);
	       	      for (short j=0; j < vecinos[nCentroId].nOrden ; j++){
	                  short nSateliteId =  this.getOrden(nCentroId, j);
					  nDista = Dista(nSateliteId, nCentroId);                     
					  System.out.printf("%d Satelite %s Distancia %f \n", j, areas.nodos[j].sCodigo, nDista);					  
                  }
    	      }
              z++;
    	  }
  	  }  
}
  
  
  // Expando todas las distancias desde el centro urbano nCentroId hasta todas las areas censales urbanas del canton 
  public int ExpandeDistancia(short nCentroId, int nMaxOrden2) {
	  
	  double nDistancia ; 
	  short nDistancia2; 
	  NodoItem aa[]; 
	  aa = new NodoItem[nMaxOrden2]; 
      int nMaxOrden = 0;
      short nInd = areas.Find(nCentroId); 
      String sCanton = areas.nodos[nInd].sCodigo.substring(0,4);
      int nX = areas.nodos[nInd].x; 
      int nY= areas.nodos[nInd].y;      
      double nMaxDista = this.getMaxDista(nCentroId); 
      for (int i=0; i < areas.nNodos; i++) {
		  if (sCanton.equals(areas.nodos[i].sCodigo.substring(0, 4)) && areas.nodos[i].sSector.equals("U")) {			  
			  nDistancia = Math.sqrt( (nX-areas.nodos[i].x)*(nX-areas.nodos[i].x) + (nY-areas.nodos[i].y)*(nY-areas.nodos[i].y)   );
			  // Convierto los mts en minutos, a una velocidad de 40Km/h
			  nDistancia = 6*nDistancia/4000;
			  if (nDistancia > nMaxDista) { 
			      // Convierto la distancia double a short 
		          nDistancia2 = (short) Math.floor(nDistancia*22.613872 - 32768);
		          aa[nMaxOrden] = new NodoItem(areas.nodos[i].id, nDistancia2);
		          nMaxOrden++; 
			  }
		  }   
	  }
      if (nMaxOrden>0) {
          ListaVecinos aLista;
    	  // Ordeno por medio del quicksort          
    	  this.sort(aa, 0, nMaxOrden-1);
    	  aLista = new ListaVecinos((short) (301+nMaxOrden)); 
          // Cargo los vecinos iniciales mas cercanos
   	      for (short j=0; j < this.vecinos[nCentroId].nOrden ; j++){
              short nSateliteId =  this.getOrden(nCentroId, j);
			  nDistancia = this.Dista(nSateliteId, nCentroId); 
		      // Convierto la distancia double a short 
	          nDistancia2 = (short) Math.floor(nDistancia*22.613872 - 32768);
			  aLista.AddNodo(nSateliteId, (short) nDistancia2);
   	      }
          // Cargo los vecinos complementarios ordenados
   	      for (short j=0; j < nMaxOrden ; j++){
              short nSateliteId = aa[j].nodoId;
	          nDistancia2 = aa[j].distancia; 
			  aLista.AddNodo(nSateliteId, (short) nDistancia2);
   	      }  
   	      // Reemplazo la lista de vecinos iniciales, por la expandida
   	     this.vecinos[nCentroId] = aLista ; 
	  }
	  return (nMaxOrden); 
  }
 
  // quicksort the array
  public void sort(NodoItem[] a) {
      //StdRandom.shuffle(a);
      sort(a, 0, a.length - 1);
  }

  // quicksort the subarray from a[lo] to a[hi]
  private void sort(NodoItem[] a, int lo, int hi) { 
      if (hi <= lo) return;
      int j = partition(a, lo, hi);
      sort(a, lo, j-1);
      sort(a, j+1, hi);
      //
  }

  // partition the subarray a[lo .. hi] by returning an index j
  // so that a[lo .. j-1] <= a[j] <= a[j+1 .. hi]
  private int partition(NodoItem[] a, int lo, int hi) {
      int i = lo;
      int j = hi + 1;
      NodoItem v = a[lo];
      while(true) { 

          // find item on lo to swap
          while (less(a[++i], v))
              if (i == hi) break;

          // find item on hi to swap
          while (less(v, a[--j]))
              if (j == lo) break;      // redundant since a[lo] acts as sentinel

          // check if pointers cross
          if (i >= j) break;

          exch(a, i, j);
      }

      // put v = a[j] into position
      exch(a, lo, j);

      // with a[lo .. j-1] <= a[j] <= a[j+1 .. hi]
      return j;
  }


 /***********************************************************************
  *  Helper sorting functions
  ***********************************************************************/
  
  // is v < w ?
  private boolean less(NodoItem v, NodoItem w) {
      return (v.distancia < w.distancia);
  }
      
  // exchange a[i] and a[j]
  private void exch(NodoItem[] a, int i, int j) {
      NodoItem swap = a[i];
      a[i] = a[j];
      a[j] = swap;
  }
  
}
