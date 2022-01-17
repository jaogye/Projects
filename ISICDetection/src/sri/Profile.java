package sri;


public class Profile  {

	/*
	 *  This class saves the datas (sales and purchases) of ay taxpayer
	 */
			
	int TP_id ; //Index of TP
	boolean special ;  
	String ciiu; 
	public boolean hasSales ; 
	public boolean hasPurchases ;
	public int clusterid ; 

	
	public Profile() {
		// TODO Auto-generated constructor stub
	    clusterid = -1 ;
	    hasSales = false; 
	    hasPurchases = false; 	    
	}

	public Profile(int n) {
		// TODO Auto-generated constructor stub
	    clusterid = -1 ; 
	}

	/*
	 
	public void setBinarydata(int nid) {
		   
		   this.id = nid; 
		   this.bdata = new String() ;  
		   for (int i=0; i < this.sales.length ; i++) {
			   if (this.sales[i] >0) 
				   bdata = bdata + (char) i ; 
		   }

		   for (int i=0; i < this.purchases.length ; i++) {
			   if (this.purchases[i] >0) 
				   bdata = bdata + (char) (i+this.sales.length) ; 
		   }
		   
	}
	  
	 */
	
  
  
}
