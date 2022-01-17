package sri;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.util.Pair;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

public class ClusterCiiuMaster {
	
	/*
	 *  This class contains the list of taxpayers with the same ciiu
	 *  It also detects the outliers of taxpayers
	 *  This class is independent of the method to detect outliers   
	 *  So we use ClusterCiiuType factoryCluster(int nType, String ciiu )  to instance the specific object
	 */
	
	int centerid ; 
	String ciiu; 
    ClusterCiiuType Sales ; 
    ClusterCiiuType Purchases ;
    
    //int[] inv_sales ; // map salesmean[x] --> lstEntrySales.get(y) 
    //int[] inv_purchases ; // map purchasesmean[x] --> lstEntryPurchases.get(y)
    
	public ClusterCiiuMaster(String sciiu, ClusterCiiuType oSales, ClusterCiiuType oPurchases  ) {
		this.ciiu = sciiu ; 
		Sales = oSales ; 
		Purchases = oPurchases ;
		this.centerid = -1 ; 
	}

	
	public void addTaxpayer(int x) {
		if (Sam.ProfilesInfo[x].hasSales)
			this.Sales.addTaxpayer(x) ;

		if (Sam.ProfilesInfo[x].hasPurchases)
			this.Purchases.addTaxpayer(x) ;
		
	}
	
		
	/// A profile is outlier if either sales or purchases profile is outlier
	/// This method computes the outliers  
	public List<Integer> getOutliers() {
		
		List<Integer> lstOutliersSales =  Sales.getOutliers() ;  
		List<Integer> lstOutliersPurchases =  Purchases.getOutliers() ;  
		
		List<Integer> lstOutliers =  Util.getUnion( lstOutliersSales,lstOutliersPurchases )  ; 
		Sales.getTaxpayers().removeAll(lstOutliersSales) ;
		Purchases.getTaxpayers().removeAll(lstOutliersPurchases) ;
						
		return  lstOutliers; 
	}

		
	public void UpdateStats() {
		
		this.Sales.UpdateStats();  
		this.Purchases.UpdateStats();  

		
		
	}
	   
    
	   
    // This method computes the variation of variance of KL distance when the taxpayer x is added 
    public double  deltavariance( int x) {

       int id = Sam.ProfilesInfo[ x ].TP_id ;  
       int nnew = Util.binarySearchCIIU( Sam.aCIIU, 0,  Sam.aCIIU.length, ciiu.substring(0, 3)) ;
   	        	    
       double delta = 0 ; 
       for(Integer i: Sam.TP[id].lstSales) {		   
	       // Note that TP[ AT[i].id1 ].idactive == x holds  	   
	      int x1 = Sam.TP[ Sam.AT[i].id2 ].idactive ;           
 	      int nold = Util.binarySearchCIIU( Sam.aCIIU, 0,  Sam.aCIIU.length, this.ciiu.substring(0, 3)) ;
	      double totvalue = Sam.TP[ Sam.ProfilesInfo[x].TP_id ].totSales ;        	
 	      double value = Sam.AT[i].value ;         	
 	      delta = delta + this.Sales.getDeltaScore(nold,  nnew, x1,  value/totvalue) ; 		   
       }

       for(Integer i:Sam.TP[id].lstPurchases) {
	      // Note that TP[ AT[i].id2 ].idactive == x holds 
          int x1 = Sam.TP[ Sam.AT[i].id1 ].idactive ; 	             
	      int nold = Util.binarySearchCIIU( Sam.aCIIU, 0,  Sam.aCIIU.length, this.ciiu.substring(0, 3)) ;
	      double totvalue = Sam.TP[ Sam.ProfilesInfo[x].TP_id ].totPurchases ;
	      double value = Sam.AT[ i ].value ;         	
	      delta = delta + this.Purchases.getDeltaScore(nold,  nnew, x1,  value/totvalue) ; 		   
       }
	   
    return delta; 

}



    
 // Outdated methods    
// ====================================================================================================    

	 private void PrintMatrix(SimpleMatrix data)  {			
		   FileWriter g = null;
		try {
			g = new FileWriter("Data/covarianceD289902.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   PrintWriter p = new PrintWriter(g);
			for (int i=0; i < data.numCols() ; i++) {
				for (int j=0; j < data.numCols() ; j++) {
					  p.printf( "%12.8s;", data.get(i,j) );
				}
				p.printf( "\n" );
			}
		    p.close(); 		    
	   } 
	   
    
}
