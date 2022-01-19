package ec.gov.senplades.dataBase;

import java.util.ArrayList;

public class Tabla {

	public  Object [][] Datos;
	public  Object [][] Esquema;	
	public  Integer pk; 
	public  Integer npk;	
	public  Integer nMax;	
	
	public Tabla(Object [][] Esquema, Integer pk, Integer nMax){
		 this.Esquema = Esquema;
		 this.pk = pk;
		 this.npk = 0; 
		 this.nMax = 0; 
		 this.Datos = new Object[nMax][Esquema.length]; 
	}
	
	public void SetPK(Integer pk) {
		this.pk = pk; 
	}
	
	public void Insert(Object [] Valores) {
		for  (int i=0; i < Valores.length; i++) {
			 if (i != pk) {
				 Datos[npk][i] = Valores[i]; 
			 }
		}
		Datos[npk][pk] =npk ;   
		this.npk++;		
	}
	
	public void Update(Object [] Valores, Integer nId) {
		for  (int i=0; i < Valores.length; i++) {
			 if (i != pk) {
				 Datos[nId][i] = Valores[i]; 
			 }
		}  
	}
	
	public Object [] SelectPorId(String [] Columna, Integer nId) {
		
		Object [] ResultSet = new Object [Esquema.length] ; 
        if (Columna == null || (Columna.length==1 && Columna[0]=="*"))
		   for  (int i=0; i < Esquema.length; i++) {
			   	 ResultSet[i] = Datos[nId][i]; 
		   } 
        else {
           Integer nCol;
           for (int j=0; j< Columna.length; j++) {
        	   nCol = FindColumna(Columna[j]);
  			   ResultSet[j] =  Datos[nId][nCol]; 
           }
		}
		
		return ResultSet; 
	}

	public Object [] SelectPorNombre(String [] Columna, String sNombre) {
		Integer nNom, nId = -1 ; 
        nNom = FindColumna("Nombre"); 
        
		for  (int i=0; i < npk; i++) {
			if ( Datos[i][nNom] == sNombre) {
                nId = i; 
			    break; 
			}
	    }
		return SelectPorId(Columna, nId); 
	}

	public ArrayList<Object []> SelectPorFiltro(String [] Columna, Object[][] Filtro) {
		
		ArrayList<Object []> ResultSet = new ArrayList<Object []>();
		
		int [] aFiltro = new int[Filtro.length]; 
		for (int j=0; j < Filtro.length  ; j++) 
  		    aFiltro[j] = FindColumna((String)Filtro[j][0]) ; 

		//Object [][] ResultSet = (Object[][]) new Object [this.npk] [Esquema[0].length] ; 
		// Busqueda del registro por filtro
        if (Columna == null || (Columna.length==1 && Columna[0]=="*"))
		    for (int i=0; i < npk; i++) {
			    boolean Encontrado = true;			
			    for (int j =0; j < Filtro.length; j++ ) {
				    int n = aFiltro[j]; 
				    Encontrado = Encontrado && (Datos[i][n]==Filtro[j][1]);
			    }
			    if (Encontrado) {
			       Object [] Registro = new Object[Esquema.length]; 
				   for (int k=0; k < Esquema.length; k++) {
					   Registro[k] = Datos[i][k]; 
			       }
				   ResultSet.add(Registro); 
			    }
	       }
        else {
    		int [] aCol = new int[Columna.length]; 
    		for (int j=0; j < Columna.length ; j++) 
      		    aCol[j] = FindColumna(Columna[j]) ; 
   		    for (int i=0; i < npk; i++) {
   			    boolean Encontrado = true;			
   			    for (int j =0; j < Filtro.length; j++ ) {
   				    int n = aFiltro[j]; 
   				    Encontrado = Encontrado && (Datos[i][n]==Filtro[j][1]);
   			    }
   			    if (Encontrado) {
 			       Object [] Registro = new Object[Columna.length]; 
				   for (int k=0; k < Columna.length; k++) {
					   Registro[k] = Datos[i][aCol[k]]; 
			       }
				   ResultSet.add(Registro); 
   			    }     
   			    }
   	       }            	
		
		return ResultSet; 
	}

	public int FindColumna(String sNombre){
		int nIndex = -1 ;
		for (int i=0; i < Esquema.length  ; i++) {
			if ( Esquema[i][0] == sNombre) {
				nIndex = i ; 
				break;
			}
		}
		return nIndex; 
	}
	
}
