package ec.gov.senplades.dataBase;

public class DataBase {

	public Tabla TipoIntervencion ;  
	public Tabla Parametro ;
	public Tabla ParaIntervencion ;
	public Tabla Intervencion ;	
	
	public DataBase() {
	
		TipoIntervencion = new Tabla(new Object [][] {
	               {"Id", "Integer" },
	               {"Clase", "Integer" },	               	               
	               {"Nombre", "String" } },0 ,10 );

		TipoIntervencion.Insert(new Object[] {null, 0, "Escuela"}); 
		TipoIntervencion.Insert(new Object[] {null, 0, "Colegio"});		
		TipoIntervencion.Insert(new Object[] {null, 0, "Dispensario"});		
		TipoIntervencion.Insert(new Object[] {null, 0, "EdificioAdministrativo"}); 
		TipoIntervencion.Insert(new Object[] {null, 1, "Carretera"}	);		
		TipoIntervencion.Insert(new Object[] {null, 1, "Puente"});	
		TipoIntervencion.Insert(new Object[] {null, 3, "Colacion Escolar"});
		TipoIntervencion.Insert(new Object[] {null, 3, "Prevencion Embarazo Adolecente"});
		TipoIntervencion.Insert(new Object[] {null, 2, "AreaAgricola"});		
		
		Parametro = new Tabla(new Object [][] {
	               {"Id", "Integer" },
	               {"TipoIntervencion_id", "Integer" },
	               {"Nombre", "String" },
	               {"TipoDato", "String" },	               
	               {"Default", "Integer" } },0 , 50 );
		Parametro.Insert(new Object[] {null, 0, "Niños_1_basica","Integer" , 50 });
		Parametro.Insert(new Object[] {null, 0, "Niños_2_basica","Integer" , 40 });		
		Parametro.Insert(new Object[] {null, 0, "Niños_3_basica","Integer" , 40 });
		Parametro.Insert(new Object[] {null, 0, "Niños_4_basica","Integer" , 40 });
		Parametro.Insert(new Object[] {null, 0, "Niños_5_basica","Integer" , 40 });
		Parametro.Insert(new Object[] {null, 0, "Niños_6_basica","Integer" , 40 });
		Parametro.Insert(new Object[] {null, 0, "Niños_7_basica","Integer" , 40 });
		
		Parametro.Insert(new Object[] {null, 1, "Jovenes_8_basica","Integer" , 150 });
		Parametro.Insert(new Object[] {null, 1, "Jovenes_9_basica","Integer" , 50 });
		Parametro.Insert(new Object[] {null, 1, "Jovenes_10_basica","Integer" , 60 });
		Parametro.Insert(new Object[] {null, 1, "Jovenes_11_basica","Integer" , 50 });
		Parametro.Insert(new Object[] {null, 1, "Jovenes_12_basica","Integer" , 50 });
		Parametro.Insert(new Object[] {null, 1, "Jovenes_13_basica","Integer" , 50 });		

		Parametro.Insert(new Object[] {null, 2, "Consultorios","Integer" , 10 });
		Parametro.Insert(new Object[] {null, 2, "Numero_camas","Integer" , 40 });		

		Parametro.Insert(new Object[] {null, 3, "Numero_Empleados","Integer" , 100 });
		Parametro.Insert(new Object[] {null, 3, "Numero_Pisos","Integer" , 4 });
		Parametro.Insert(new Object[] {null, 3, "Mts2_por_piso","Integer" , 100 });

		Parametro.Insert(new Object[] {null, 4, "Orden_via" , "Integer" , 3 });		
		Parametro.Insert(new Object[] {null, 4, "Numero_Carriles_por_via" , "Integer"  , 1 });

		Parametro.Insert(new Object[] {null, 5, "Longitud_Mts" , "Integer"  , 30 });		
		Parametro.Insert(new Object[] {null, 5, "Numero_Carriles_por_via" , "Integer"  , 1 });

		Parametro.Insert(new Object[] {null, 5, "Longitud_Mts" , "Integer"  , 30 });		
		Parametro.Insert(new Object[] {null, 5, "Numero_Carriles_por_via" , "Integer"  , 1 });
		
		Parametro.Insert(new Object[] {null, 6, "Numero_Raciones" , "Integer"  , 30 });		
		Parametro.Insert(new Object[] {null, 7, "Horas_capacitacion" , "Integer"  , 10 });
				
		Intervencion = new Tabla(new Object [][] {
	               {"Id", "Integer"},
	               {"TipoInvervencion_id", "Integer"},
	               {"X", "Integer"},
	               {"Y", "Integer"},
	               {"Codigo", "String"} },0 , 50 );
		
		ParaIntervencion = new Tabla(new Object [][] {
	               {"Id", "Integer"},
	               {"Invervencion_id", "Integer"},
	               {"Parametro_id", "Integer"},
	               {"Valor", "Integer"} },0 , 500 );		
	}
}
