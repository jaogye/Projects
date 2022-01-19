package ec.gov.senplades.optimiza;

public class DistribucionDemanda {

	public String sCodigo; 
	public short k;
	public String sSector; 
	public DistribucionDemanda(String sCodigo, String sSector, short k) {
		this.sCodigo=sCodigo; 
		this.k=k; 
		this.sSector=sSector; 
	}
}
