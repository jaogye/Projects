package ec.gov.senplades.optimiza;

public class ParametroInput {

	
    private final int maxProvincias = 24, maxCantones = 230, maxParroquias=1200;
	
	public String  Provincias[]; 	
	public String  Cantones[];
	public String  Parroquias[];
	public Boolean[] NuevoCentro; 
	public String  sSector; 
	public double nRadio ; 
	public short nProvincias, nCantones, nParroquias; 
	public boolean Actual, Pobreza, Pobreza2, Educacion;  
	public Short  nGrupoFuncional, nAño, nTipoFacilidad, k; 
	public int nCapMax, nCapMin;  
    

	
	public ParametroInput() {
		nProvincias=0; 
		nCantones=0; 
		nParroquias=0;
		Provincias = new  String[maxProvincias];
		Cantones = new  String[maxCantones];
		Parroquias = new  String[maxParroquias];
	    NuevoCentro = new Boolean[Areas.TotNodos]; 
		sSector=null; 
	}
	
	public void AddProvincia(String sProvincia) {
		Provincias[nProvincias]=sProvincia; 
		nProvincias++; 
	}

	public void AddCanton(String sCanton) {
		Cantones[nCantones]=sCanton; 
		nCantones++; 
	}

	public void AddParroquia(String sParroquia) {
		Parroquias[nParroquias]=sParroquia; 
		nParroquias++; 
	}
	
	// Construyo un query que va cargar los datos del area censal
	public String GetQuery() {
		String sQuery = null ; 
		
		sQuery = "select a.orden, a.sector, a.codigo, a.x, a.y, d.valor_demanda, B.CAPACIDAD_ACTUAL, B.CAPACIDAD_NUEVA" +
		", a.pobreza, a.pobreza2, a.educacion " +
		" from lip_areacensal a, v_demanda d,LIP.V_INFRAESTRUCTURA_ACTUAL b " +  
		" where A.AMBITO_ID=D.CODIGO_AREA_CENSAL  and a.ambito_id = B.CODIGO_AREA_CENSAL and  substr(a.codigo,1,2)!='20' " +
		"and D.CODIGO_GRUPO_FUNCIONAL='"+nGrupoFuncional.toString()+"' and D.ANIO_DEMANDA='"+nAño.toString()+
		"' and B.TIPO_FACILIDAD = " + nTipoFacilidad.toString() ;  
		
		if (sSector!=null){
			sQuery = sQuery + " and a.sector='"+sSector+"' "; 
		}
		
		
		//" and (substr(a.codigo,1,2) in ('03', '07') or substr(a.codigo,1,4) in ('0401','0901') )" ; 
		String sFiltro = null; 
		
		if (nProvincias > 0) {
		   sFiltro = " and  (substr(a.codigo,1,2) in (";
		   for (int i=0; i < nProvincias; i++){
			   if (i>0)
				  sFiltro = sFiltro + ","; 
			   sFiltro = sFiltro + "'"+Provincias[i] +"'"; 
		   }
		   sFiltro = sFiltro + ")";
		}

		if (nCantones > 0) {
			   if (sFiltro==null)
			       sFiltro = " and  (substr(a.codigo,1,4) in (";
			   else 
				   sFiltro = sFiltro + " or substr(a.codigo,1,4) in ("; 
			   for (int i=0; i < nCantones; i++){
				   if (i>0)
					  sFiltro = sFiltro + ","; 
				   sFiltro = sFiltro + "'"+Cantones[i] +"'"; 
			   }
			   sFiltro = sFiltro + ")";
			}

		if (nParroquias > 0) {
			   if (sFiltro==null)
			       sFiltro = " and  (substr(a.codigo,1,6) in (";
			   else 
				   sFiltro = sFiltro + " or substr(a.codigo,1,6) in ("; 
			   for (int i=0; i < nParroquias; i++){
				   if (i>0)
					  sFiltro = sFiltro + ","; 

				   sFiltro = sFiltro + "'"+Parroquias[i] +"'"; 
			   }
			   sFiltro = sFiltro + ")";
			}
		if (nProvincias+nCantones+nParroquias>0)
		   sFiltro = sFiltro + ")"; 
			
        if (sFiltro!=null)      	
		   sQuery = sQuery + sFiltro ; 
        sQuery = sQuery + " order by a.orden";
        return sQuery;  
	}
	
}
