package ec.gov.senplades.optimiza;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class Areas {

    static final int TotNodos = 32104;
	static final int maxCluster = 8000;
	
	public AreaItem[] nodos; 	
	public ClusterItem cluster[];
	// ClusterActual guarda los indices, donde estan las areas censales que tienen capacidad actual positiva 
    public short ClusterActual[];  
	Distancia distancia;
	
	public short nCluster, nCentrosActuales, k, modelo, nClusterActual ;
	public int corrida, nDemanda, nNodos, nNumCentros, nTotCapacidad;   
	public ParametroInput oParametro; 
    boolean actual, variante; 
	public double nFacCen=0, nRadioPro, nRadioMax;  
	public double nFObjetivo, nFObjetivoMin ;
	public int nTotLibre, nCambio; 

	
    public Areas(){
		nNodos =0;
		nCluster = 0; nClusterActual  = 0;
		nDemanda =0;
		nodos = new  AreaItem[TotNodos];
		cluster = new ClusterItem[maxCluster];
		ClusterActual = new short[maxCluster]; 
    }
    
	public Areas(Distancia oDista, ParametroInput oPara){
        oParametro = oPara;
		distancia = oDista; 
		actual = oPara.Actual; 
		k=oParametro.k;   
		nodos = new  AreaItem[TotNodos];
		nNodos =0; 
		cluster = new ClusterItem[maxCluster];
		nCluster = 0; 
		ClusterActual = new short[maxCluster];
		nClusterActual  = 0;

		this.CargaAreas();
		// Inicilizacion de nDemanda 
    	nDemanda =0; 
   	    for (short i=0; i<nodos.length; i++)
		  if (nodos[i]!=null)
		     if (!nodos[i].eliminado)
		         nDemanda = nDemanda + nodos[i].demanda;

   	    for (int i=0; i < this.nNodos; i++ ) {
            if (this.nodos[i].capNuevo>0) {
	    	   nTotCapacidad = nTotCapacidad + this.nodos[i].capNuevo; 
	    	   nNumCentros++; 
            }
	    }
 	   nFacCen = (double) nNumCentros/nNodos;  	    
		
	}
	
		
	
	private void CargaAreas(String sFile){		
		   Scanner s = null;
		   short nId, nDemanda, nCapNuevo;
		   int nCapActual, nX, nY ; 
		   String sCodigo, sSector; 
		   try {
		      s = new Scanner(new BufferedReader(new FileReader(sFile)));
	          // Salto la primera linea del archivo 
		      s.next(); 	      	      
		      s.useDelimiter("\r\n|,");
		      while (s.hasNext() ) {
		    	  // Salto la primera columna
		    	  String ss=s.next(); 
		    	  nId = Short.parseShort(ss);
		    	  sCodigo = s.next(); 
		    	  sSector = s.next();
		    	  nX=Integer.parseInt(s.next()); 
		    	  nY=Integer.parseInt(s.next());		    	  
		    	  nDemanda = Short.parseShort(s.next());
		    	  nCapActual = Short.parseShort(s.next());
		    	  nCapNuevo = Short.parseShort(s.next());

		    	  nodos[nNodos] = new AreaItem(nId, sSector, sCodigo,nX, nY, nDemanda, (short) nCapActual, nCapNuevo);  
		    	  
		    	  //System.out.printf("Memoria %d  Area %d \n", Runtime.getRuntime().freeMemory(), nNodos);
		    	  if (nNodos % 100 ==0 )
		    	     System.out.printf("Id %d Demanda %d CapActual %d CapNuevo %d \n", nodos[nNodos].id, nodos[nNodos].demanda, nodos[nNodos].capActual, nodos[nNodos].capNuevo);
		    	  if (nCapActual > 0) {
		    		  ClusterActual[nClusterActual]=(short)nNodos; 
		    		  nClusterActual++; 
		    	  }		    	  
		    	  nNodos++; 
		      }
		      System.out.println("Fin CargaAreas");
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

	private void CargaAreas() {

	     Connection conn = null;

         try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@aquiles.senplades.int:1521:OLTPD", "LIP", "LIP");
            Statement stmt;
            stmt = conn.createStatement();
            // El query retorna las columnas a.orden, a.sector, a.codigo, d.valor_demanda, B.CAPACIDAD_ACTUAL, B.CAPACIDAD_NUEVA
            ResultSet rset = stmt.executeQuery(oParametro.GetQuery());
            
            short nId, nDemanda, nCapActual, nCapNuevo;
            float Pobreza, Pobreza2, Educacion;
            int nX, nY; 
            String sCodigo, sSector; 
            nNodos=0; 
            while (rset.next()) { 
                  nId = rset.getShort(1);
                  sSector = rset.getString(2);
                  sCodigo = rset.getString(3);
                  nX=rset.getInt(4); 
                  nY=rset.getInt(5);                  
                  nDemanda = rset.getShort(6); 
                  nCapActual = rset.getShort(7);
                  nCapNuevo = rset.getShort(8);  
                  Pobreza = rset.getFloat(9); 
                  Pobreza2 = rset.getFloat(10);
                  Educacion = rset.getFloat(11);
                  if (oParametro.Pobreza) 
                	  nDemanda = (short)(nDemanda * Pobreza); 
                  if (oParametro.Pobreza2) 
                	  nDemanda = (short)(nDemanda * Pobreza2); 
                  if (oParametro.Educacion) 
                	  nDemanda = (short)(nDemanda * Educacion); 
                                    
		    	  nodos[nNodos] = new AreaItem(nId, sSector, sCodigo, nX, nY, nDemanda, nCapActual, nCapNuevo);
		    	  
		    	  if (nCapActual > 0) {
		    		  ClusterActual[nClusterActual]=(short)nNodos; 
		    		  nClusterActual++; 
		    	  }
		    	  
                  nNodos++; 
            	  //System.out.printf ("Reg %d \n", nNodos); 
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
	
	public void GrabaAreas(String sFile){
		   FileOutputStream g = null;
  	       Double nRadioPro, nRadioMax ;            	  
	       Short  nDemandaCluster; 

		   try {
			  g = new FileOutputStream(sFile);
			  PrintStream p = new PrintStream(g);
			  p.println("Area_id,Codigo,Sector,Cluster_id,centro,Demanda,Distacen,nuevo,RadioPro,RadioMax,DemClus") ; 
              for (int i=0; i < nNodos; i++) {
            	  Short Area_id = new Short(nodos[i].id);
            	  String sCodigo = nodos[i].sCodigo; 
            	  String sSector = nodos[i].sSector; 
            	  Short Cluster_id = new Short(nodos[i].clusterId); 
            	  Boolean Centro = new Boolean(nodos[i].centro);
            	  Short Demanda = new Short(nodos[i].demanda);
            	  Float Distacen = new Float(nodos[i].distacen);
            	  Boolean Nuevo = new Boolean(nodos[i].actual);
            	  if (nodos[i].clusterId!=-1) {
            	      nRadioPro = new Double(cluster[ nodos[i].clusterId].nRadioPro);
            	      nRadioMax = new Double(cluster[ nodos[i].clusterId].nRadioMax);            	  
            	      nDemandaCluster = new Short( cluster[ nodos[i].clusterId].nDemanda);
            	  } else {
            	      nRadioPro = new Double(0);
            	      nRadioMax = new Double(0);            	  
            	      nDemandaCluster = new Short((short) 0);

            	  }
            		  
            	  p.println( Area_id.toString() +","+ sCodigo+ ","+ sSector + "," + Cluster_id.toString()+","+
            			  Centro.toString()+","+Demanda.toString()+","+Distacen.toString()+","+Nuevo.toString()+
            			  ","+nRadioPro.toString()+","+nRadioMax.toString()+","+nDemandaCluster.toString()); 
              }
			  p.close();
		   } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		      finally {
		        if (g != null) {
		        try {
					g.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      }
		   }
	}
	
	// Ordeno los cluster actuales de acuerdo con las capacidades actuales del cluster, usando el metodo de la burbuja 
	public void OrdenaClusterActual() {
		
		short temp, i2, j2; 
		for (int i=0; i < nClusterActual; i++) {
			for (int j=i+1; j < nClusterActual; j++) {
				i2 = ClusterActual[i];
				j2 = ClusterActual[j];
				if (nodos[i2].capActual > nodos[j2].capActual) {
					temp = ClusterActual[i]; 
					ClusterActual[i] = ClusterActual[j];  
					ClusterActual[j] = temp; 			
				}
			}
		}
	}
	
	
	public short Find(short nId) {
		
 	  int inicio = 0;
	  int fin =  (short)(nNodos  - 1);
      int pos;
	  while (inicio <= fin) {
	     pos = (inicio+fin) / 2;
	     if ( nodos[pos].id == nId )
	        return (short) pos;
	     else if ( nodos[pos].id < nId ) {
	   	    inicio =  (pos+1);
		 } 
	     else {
		    fin =  (pos-1);
		 }
	  }
	 return -1;
	}

   // Crea una lista enlazada de los nodos no eliminados 

    public ArrayList<Short> GetNodos() {
	  ArrayList<Short> Lista = new ArrayList<Short>(); 
	  for (short i=0; i<nNodos; i++)
		  if (nodos[i]!=null)
		     if (!nodos[i].eliminado)
		         Lista.add(i); 
	  return Lista ; 
}
    public ArrayList<Short> GetNodos(boolean lCentro) {
  	  ArrayList<Short> Lista = new ArrayList<Short>(); 
  	  for (short i=0; i<nNodos; i++)
  		  if (nodos[i]!=null && nodos[i].centro==lCentro)
  		     if (!nodos[i].eliminado)
  		         Lista.add(i); 
  	  return Lista ; 
  }

    
    public void AddNodo(short nCentro, short j){
    	
     if (nCentro != -1)
        cluster[nCentro].AddNodo(j);
   	 nodos[j].clusterId=nCentro;  		    	  

    }	

    public void RemoveNodo(short nCentro, short j){
    	
    	 nodos[j].clusterId=-1;
    	 nodos[j].eliminado=false; 
         cluster[nCentro].RemoveNodo(j);
       }	
    
    public void AddCentro(short i, boolean lActual){
    	if (cluster[nCluster]==null)
    	    cluster[nCluster]=new ClusterItem(i, nodos[i].capNuevo);
    	else 
    	{
    		cluster[nCluster].area_ind=i; 
    		cluster[nCluster].capacidad = nodos[i].capNuevo; 
    		cluster[nCluster].cap = nodos[i].capNuevo;    		
    	}
 	   // Agrego el nodo del centro como un elemento del cluster
 	    cluster[nCluster].AddNodo(i);  	   
 	    nodos[i].eliminado = true;
 	    nodos[i].clusterId=nCluster;
 	    nodos[i].centro = true;
 	    nodos[i].actual = lActual;
    	nCluster++; 
    }	

    // Elimino los nodos satelites, los nodos centros de cluster no
    public void ClearCluster( ) {  	
    	for (int i=0; i < nCluster; i++) {
    	    cluster[i].ClearLista();  
            // Inicializo las capacidades de temporales de calculo
    	    cluster[i].capacidad = (short) (cluster[i].cap-nodos[cluster[i].area_ind].demanda);  
    	    cluster[i].AddNodo(cluster[i].area_ind);
    	}
    }
     
    public void ClearArea( ) {  	
    	for (int i=0; i < nNodos; i++) {    	    
            nodos[i].clusterId=-1;    	     
            nodos[i].clusterId_opt=-1;
            nodos[i].eliminado=false; 
    	}

    	for (int i=0; i < nCluster; i++) {
    	    cluster[i].ClearLista();  
    	    cluster[i] = null; 
    	}
    }
    //Este metodo elimina todas las asignaciones de nodos satelites al interior del Cluster  
    public void ClearAsigacionesCluster(short nClusterId, boolean Completo) {
		Iterator<Short> iter = cluster[nClusterId].ListaNodo.iterator();
		short i; 
		while( iter.hasNext()) {
		    i=iter.next();
		    nodos[i].eliminado=false; 
		    nodos[i].clusterId=-1; 
		}
	    nodos[cluster[nClusterId].area_ind].eliminado=false; 
	    nodos[cluster[nClusterId].area_ind].clusterId=-1; 		
    	cluster[nClusterId].ClearLista();
    	if (Completo) {
    		cluster[nClusterId]=null; 
    	} else {
    	    cluster[nClusterId].AddNodo(cluster[nClusterId].area_ind);    		
    	}
 
    }

    public void ClearAsignacionCluster(short nCluster, short nInd) {
    	this.cluster[nCluster].ListaNodo.remove(nInd);     	
    }
    
    public Areas GetVariante(){ 
    	return GetVariante(true); 
    }
    
    
    // Realiza una copia del objeto Area
    // Si la copia es nueva, solo copia con los nuevos centros propuestos en la reoptimizacion
    // Si la copia no es nueva, copia respetando las asignaciones de la optimizacion 
    public Areas GetVariante(boolean Nuevo){
      Areas oArea = new Areas();
      oArea.variante = true; 
      oArea.nNodos = this.nNodos; 
      oArea.distancia = this.distancia;
      oArea.nDemanda = this.nDemanda; 
      oArea.oParametro = this.oParametro;     
      if (Nuevo) {
          oArea.nCluster =0 ;
    	  for (short i=0; i<this.nNodos; i++){
    	     oArea.nodos[i]= new AreaItem(this.nodos[i].id, this.nodos[i].sSector, this.nodos[i].sCodigo, this.nodos[i].x, this.nodos[i].y, this.nodos[i].demanda, this.nodos[i].capActual, this.nodos[i].capNuevo );
    	     oArea.nodos[i].centro = oParametro.NuevoCentro[i] ;
    	     if (oParametro.NuevoCentro[i]) 
        	     oArea.AddCentro(i, false); 
    		   else  
    		     oArea.nodos[i].clusterId=-1; 	      	  
    	       oArea.nodos[i].actual=this.nodos[i].actual;
         } 
        } else {        	
       	   for (short i=0; i<this.nNodos; i++){
       		   oArea.nodos[i] = (AreaItem) this.nodos[i].Clone2();   
       		   oArea.nodos[i].clusterId = -1 ; 
       	   }
       	   // Cargos las asignaciones dentro de los cluster, siempre y cuando esten definidas en NuevoCentro
           short nClus=0;
       	   for (short j=0; j < this.nCluster; j++) {		
       		   
               if (oParametro.NuevoCentro[this.cluster[j].area_ind ]) {
                  oArea.cluster[nClus] = this.cluster[j].Clone2(); 
            	  short i; 
            	  oArea.cluster[nClus].nDemanda = 0; 
              	  Iterator<Short> iter = this.cluster[j].ListaNodo.iterator();
            	  while( iter.hasNext()) {
            		   i=iter.next();
            		   if (!oParametro.NuevoCentro[i] || this.nodos[i].centro ) {           			              			   
            		      oArea.cluster[nClus].ListaNodo.add(i); 
            		      oArea.nodos[i].eliminado=false; 
            		      oArea.nodos[i].clusterId=nClus;
            		      oArea.cluster[nClus].nDemanda = (short) (oArea.cluster[nClus].nDemanda + oArea.nodos[i].demanda);
            		   }
            	  }
                  nClus++;        			   
               }               
          }
       	  oArea.nCluster=nClus;
    	  // Cargo los nuevos centros, propuestos por el usuario  
      	  for (short i=0; i<this.nNodos; i++){
              if (oParametro.NuevoCentro[i] && oArea.nodos[i].clusterId == -1) {
           	     oArea.AddCentro(i, false); 
              }
      	   }
        }
      return oArea;   
    }

    
    public void GuardaOptimo() {
 
    	for (int i=0; i < this.nNodos; i++) {
    		this.nodos[i].clusterId_opt=this.nodos[i].clusterId; 
    		this.nodos[i].centro_opt=this.nodos[i].centro; 
    	}
    		
    	for (int i=0; i < this.nCluster; i++)
    		this.cluster[i].area_ind_opt=this.cluster[i].area_ind; 
    }
    
    public void RestauraOptimo(){
    
    	for (int i=0; i < this.nNodos; i++) {
    		this.nodos[i].clusterId=this.nodos[i].clusterId_opt; 
    		this.nodos[i].centro=this.nodos[i].centro_opt; 
    	}
    		
    	for (int i=0; i < this.nCluster; i++)
    		this.cluster[i].area_ind=this.cluster[i].area_ind_opt; 

        // Restauro las distancias
		short nSateliteId, nCentroId; 
    	for (int i=0; i < this.nNodos; i++) {
    		if (this.nodos[i].clusterId!=-1) {
    			short nCentro = this.nodos[i].clusterId; 
    			nCentroId = this.nodos[this.cluster[nCentro].area_ind].id;
    			nSateliteId= this.nodos[i].id;  
        		this.nodos[i].distacen = (float) distancia.Dista(nCentroId, nSateliteId);     			
    		}
    	}
    	
    }
 
    // Elimino los nodos satelites, los nodos centros de cluster no
    public int SetEstadisticasCluster() {

    	int nDemanda = 0, nDemanda2=0;
    	double nDemandaDis = 0; 
    	short nSateliteInd, nSateliteId, nCentroInd, nCentroId; 
    	nRadioMax =0 ; 
    	nRadioPro = 0; 
        for (int j=0; j < nCluster; j++ ){
    	    cluster[j].nDemanda =0 ;   
    	    cluster[j].nRadioPro =0 ;
    	    cluster[j].nRadioMax =0 ;    	    
        	if (nodos[j].clusterId!=-1) {
        		nDemanda2 = 0;
        		nDemandaDis = 0; 
        		nCentroInd = this.cluster[j].area_ind; 
        		nCentroId = this.nodos[nCentroInd].id; 
        	    for (int i=0; i < this.cluster[j].ListaNodo.size();  i++ ){
        	        nSateliteInd = this.cluster[j].ListaNodo.get(i);
        	        nSateliteId = this.nodos[nSateliteInd].id; 
                    nDemanda2 = nDemanda2 + nodos[nSateliteInd].demanda;
                    nodos[nSateliteInd].distacen = (float) this.distancia.Dista(nCentroId, nSateliteId);
                    nDemandaDis = nDemandaDis + nodos[nSateliteInd].demanda * nodos[nSateliteInd].distacen ; 
            	    nRadioPro = nRadioPro +  nodos[nSateliteInd].demanda * nodos[nSateliteInd].distacen;  
            	    if (nodos[nSateliteInd].distacen > cluster[j].nRadioMax )
            	 	    cluster[j].nRadioMax = nodos[nSateliteInd].distacen ;
            	    if (nodos[nSateliteInd].distacen > nRadioMax )
            		    nRadioMax = nodos[nSateliteInd].distacen ;            	
               }
        	    cluster[j].nDemanda = (short)nDemanda2;
        	    cluster[j].nRadioPro =  nDemandaDis/nDemanda2;
        	    nDemanda = nDemanda + nDemanda2 ; 
            }   
    	}
    	nRadioPro = nRadioPro / nDemanda ; 
    	return nDemanda; 
    }

    
    public void AddNodo(short nId, String sSec, String sCod, int nX, int nY, short nDemanda, short nCapActual, short nCapNuevo, short nCluster, boolean lCentro) {
		nodos[nNodos] = new AreaItem(nId, sSec, sCod, nX, nY, nDemanda, nCapActual, nCapNuevo, nCluster, lCentro);  
		nNodos++; 	
    }
    
	public short getCentroMasCercano(short nSatelite) {
	  	
	  	// apenas encuentro algun centro cercano retorno
	  	for (short n=0; n < distancia.vecinos[nSatelite].nOrden; n++) {
	        short nInd = this.Find(distancia.getOrden(nSatelite, n));
	        if (nInd!=-1) {
		  		if (this.nodos[nInd].centro){
		  			return nInd; 
		  		}				        	
	        }
	  	}	
	  	return -1; 
	  }	
	  	
   public double getTotalDistancia(short nCluster, short nCentroInd){

	short nSatelite, nSateliteInd; 
	double nFObjetivo =0, nDistancia =0;
	short nCentro = this.nodos[nCentroInd].id; 
    for (int i=0; i < this.cluster[nCluster].ListaNodo.size();  i++ ){
        nSateliteInd = this.cluster[nCluster].ListaNodo.get(i);
        nSatelite = this.nodos[nSateliteInd].id; 
        nDistancia = this.distancia.Dista(nCentro, nSatelite);
        if (nDistancia < ListaVecinos.MaxDistancia) {
           nFObjetivo = nFObjetivo + this.nodos[nSateliteInd].demanda*nDistancia;
        }     
    }   
	return nFObjetivo; 
}

   public double getMaxDistancia(short nCluster, short nCentroInd){

		short nSatelite, nSateliteInd; 
		double nDistaMax =0, nDistancia =0;
		short nCentro = this.nodos[nCentroInd].id;
		
	    for (int i=0; i < this.cluster[nCluster].ListaNodo.size();  i++ ){
	        nSateliteInd = this.cluster[nCluster].ListaNodo.get(i);
	        nSatelite = this.nodos[nSateliteInd].id; 
	        nDistancia = this.distancia.Dista(nCentro, nSatelite);
	        if (nDistancia > nDistaMax) {
	        	nDistaMax = nDistancia ; 
	        }     
	    }   
		return nDistaMax; 
	}
   
}