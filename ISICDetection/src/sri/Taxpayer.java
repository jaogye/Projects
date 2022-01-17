package sri;

import java.util.ArrayList;

/*  This class saves the information of Taxpayers
 * 
 */

public class Taxpayer {
	public int id;
	public int ciiu_id;  
	public String ciiu;
	public String ciiu_old;
	public String ciiu_prev;
	public int idactive ;
	public ArrayList<Integer> lstSales; // pointers to AT.id1 
	public ArrayList<Integer> lstPurchases; // pointers to AT.id2
	public double totSales ; 
	public double totPurchases  ;
	public boolean special;
    public boolean active ; 
    
    
    
   public Taxpayer(int nid, int nciiu_id, int nidactive, boolean bspecial, String sciiu) {
	   this.id = nid ;
	   this.ciiu_id = nciiu_id;  
	   this.idactive = nidactive ;  
	   this.special = bspecial ; 
	   this.ciiu = sciiu ;
	   this.ciiu_old = sciiu ;
	   lstSales = new ArrayList<>(); 
	   lstPurchases  = new ArrayList<>();
	   totSales = 0 ;
	   totPurchases = 0 ; 

	  
	   if  (nidactive == -1)
		   this.active = false ; 
	   else 
	       this.active = true ; 
   }

	      
}

