package ec.gov.senplades.planificador;

import java.awt.FileDialog;

import ec.gov.senplades.reportes.fInfActividad;
import ec.gov.senplades.reportes.fInfDesgloseImpactos;
import ec.gov.senplades.reportes.fInfImpactos;
import ec.gov.senplades.reportes.fInfLineal;
import ec.gov.senplades.reportes.fInfPoligonal;
import ec.gov.senplades.reportes.fInfPuntual;
import ec.gov.senplades.reportes.fInfRebalanceo;
import ec.gov.senplades.reportes.fInfVaciosCobertura;

 
public class Planificador {

	JMenuMap oForma;
	FileDialog fd;

	
	public Planificador(JMenuMap oFrame){
		this.oForma = oFrame; 
	}
	
	public void Abrir(){
		System.out.println(" Metodo Abrir"); 
		fd = new FileDialog(oForma, "Abrir", FileDialog.LOAD);
		fd.setVisible(true);
		System.out.println(fd.getDirectory()+fd.getFile());		
		
	}
	
	public void Guardar(){
		System.out.println(" Metodo Guardar");		
	}
	public void GuardarComo(){
		System.out.println(" Metodo GuardarComo");
		fd = new FileDialog(oForma, "Guardar Como", FileDialog.SAVE);
		fd.setVisible(true);
		System.out.println(fd.getDirectory()+fd.getFile());		
	}
	public void Exportar(){
	   System.out.println(" Metodo Excel");
	   new fExporta(); 
	}
			
	public void Consolidar(){
		   new fConsolidar();

	}
	public void Comparar(){
		   new fComparar();		
	}
	
	public void VerInfraestructuralActual(){
        new fVerInfraestructuraActual(); 		
	}
	
	public void VerZonasRiesgo(){
		new fVerZonaRiesgo(); 		
	}
	
	public void VerTiemposDesplazamiento(){
		 new fMuestra("Se visualiza el mapa base de los tiempos de desplazamiento");		
	}
	public void VerCostosConstruccionVias(){
		new fVerCostoConstruccionVias(); 
	}
	public void VerZonasInfluencia(){
		new fVerZonasInfluencia(); 
	}
	
	public void VerIndicadoresSociales(){
		new fVerIndicadoresSociales(); 		
	}

	public void IngresoInfraestructuralActual(){
        new fInfraActual();
	}

	public void ZonasRiesgos(){	
		new fZonaRiesgo();
	}
	
	public void Indicadores(){	
		new fIndicadores(); 
	}
	public void Productos(){
		new fProductos(); 
	}

	public void Insumos(){
		new fInsumos(); 
	}

	public void TipoIntervenciones(){
		new fTipoIntervencion(); 
	}
	
	public void FormulaIntervenciones(){
		new fFormulaIntervencion(); 
	}
	
	public void FijarTerritorio(){
		new fFijaTerritorio(); 
	}
	public void PMedianasSinLimite(){
		new fLocaliza(0); 
	}
	public void PMedianasConLimite(){
		new fLocaliza(1);		
	}
	public void PCentrosSinLimite(){
		new fLocaliza(2);		
	}
	public void PCentrosConLimite(){
		new fLocaliza(3);		
	
	}

	public void MaximaCobertura(){	
		new fCoberturaMaxima(); 
	}

	public void CalculosImpactos(){
		new fCalculoImpactos();		
	}
	
	public void CalculosZonasInfluencia(){
		new fCalculoZonasInfluencia(); 
	}
	
	public void Opciones(){	
		new fOpciones(); 
	}
	
	public void InfImpactos(){
		new fInfImpactos();		
	}
	public void InfDesgloseImpactos(){
		new fInfDesgloseImpactos();		
	}
	public void InfIntervencionPuntual(){
		new fInfPuntual(); 		
	}
	public void InfIntervencionLineal(){
		new fInfLineal();		
 
	}
	public void InfIntervencionPoligonal(){
		new fInfPoligonal();		
	}

	public void InfActividad(){
		new fInfActividad();			
	}

	public void InfVaciosCobertura(){
		new fInfVaciosCobertura();			
	}

	public void InfRebalanceo(){
		new fInfRebalanceo();			
	}

	public void Inflacion() {
		new fInflacion(); 
	}
	
	public void Rebalanceo() {
		new fRebalanceo(); 
	}

}
