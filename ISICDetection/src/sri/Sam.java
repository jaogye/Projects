package sri;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.math3.util.Pair;

/* This class save the the social accounting matrix of commercial relations between taxpayezrs 
 * 
 * 
 */

public class Sam {

	static double zalpha= 2 ;
    static double maxVar = 0.95 ; 
    static int spurious = 8 ; 
    static double outlierFraction = 0.02 ; 
    static String path = "C:\\Users\\jaaxx\\eclipse-workspace\\ISICDetection\\out\\" ; 
    static String url = "jdbc:sqlite:C:/Users/jaaxx/datasets/datosSRI/SRI2012.sqlite";
    static int Iteration;     
    static int nCiiu; // Number of entry ciius     
    
	static int[] TP_index ;
    static CIIU[] aCIIU ;
    static Transaction[] AT ; 
    static Taxpayer[] TP ;
    static Profile[] ProfilesInfo ; // Profiles of active taxpayers
	static Map< String, ClusterCiiuMaster > dicCiiu = new HashMap<String, ClusterCiiuMaster >(); // Dictionary of list of Clusters for each ciiu // Dictionary of list of Clusters for each ciiu
    static double scalesSales[];
    static double scalesPurchases[];
    static double profilesSales[][];
    static double profilesPurchases[][];

    static DataFactory datafactory = new DataFactory();
    
    static AlgoType oAlgo ; 
    
    
	public Sam() {
	   
	    aCIIU = datafactory.getCiiu() ; 
	    AT = datafactory.getAT() ;
	    TP = datafactory.getTaxpayer() ;
	    nCiiu = aCIIU.length ;
	    Iteration = 0;
	    
        this.setActiveIndex() ;
		ProfilesInfo = new Profile[TP_index.length];
		profilesSales = new double [TP_index.length][Sam.nCiiu];
		profilesPurchases = new double [TP_index.length][Sam.nCiiu];
		scalesSales = new double [Sam.nCiiu];
		scalesPurchases = new double [Sam.nCiiu];
        oAlgo = new AlgoKL() ; 
		// Creation of profiles			
	}
	
	
    public static ClusterCiiuType factoryCluster(int nType, String ciiu )  {
    	
    	if (nType == 1 )
    		return new ClusterCiiuKL(1, ciiu, Sam.profilesSales, Sam.scalesSales) ; 
        else 
        	return new ClusterCiiuKL(2, ciiu, Sam.profilesPurchases, Sam.scalesPurchases) ; 		
    }
    


	private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
	
	
	/// This method computes the index of active taxpayezrs into the table of total taxpayers TP  
	private void setActiveIndex() {

		// Creation of  the map  TP[x].idactive |--> x  
		int nActive = 0 ; // Number of active Taxpayers
		for (int i=0; i < TP.length; i++) {
			if (TP[i].idactive >= 0)
				nActive++ ; 
		}
 
		TP_index = new int[nActive] ; 
		for (int i=0; i < TP.length; i++) 
			if (TP[i].idactive >= 0) {
				TP_index[TP[i].idactive] = i;
		 }
	}
	
	/// This method fills the dictionary dicCiiu
    public static void setdicCiiu() {
    	
    	dicCiiu.clear();
    	for (int i=0; i < ProfilesInfo.length; i++ ) {
            // Check is key exists in dicCiiu
            if (TP[ TP_index[i]].active ) {
        		if  (!dicCiiu.containsKey( ProfilesInfo[i].ciiu ) ) {
        			ClusterCiiuType Sales =  Sam.factoryCluster(1, ProfilesInfo[i].ciiu ) ; 
        			ClusterCiiuType Purchases = Sam.factoryCluster(2,ProfilesInfo[i].ciiu ) ;
        			
                	dicCiiu.put( ProfilesInfo[i].ciiu , new ClusterCiiuMaster( ProfilesInfo[i].ciiu, Sales, Purchases ) ) ;

        		}	
                dicCiiu.get( ProfilesInfo[i].ciiu ).addTaxpayer(i);  ;            	
            }
    	}                        	     		

   	     	// Update means      	
	    for (Entry<String, ClusterCiiuMaster> entry : dicCiiu.entrySet()) {
	    	 entry.getValue().UpdateStats(); 
	    }	    	 

    	
    	/*
    	 * 
    	 *     	// Update means      	
	    for (Entry<String, ClusterCiiu> entry : dicCiiu.entrySet()) {
	    	if (entry.getValue().ciiu.compareTo("I630400")==0  || entry.getKey().compareTo("I630400")==0 ) {
	    		int hh = 0;
	    		hh++ ; 
	    		
	    	}	    		    	
	    	 entry.getValue().UpdateStats();  
	    }	    	 

    	 */
    	
    	
    }
	

	/// # This method computes the commercial profile of each TP (taxpayer)
	public  void setProfiles()  {
				        		    
		int id1, id2, idciiu1, idciiu2, idactive ; 
		double value; 		   		   							
        System.out.printf("setProfiles() \n");
            			
		for (int i=0; i < TP.length ; i++) {
			if (TP[i].idactive >= 0) {
				int nid = TP[i].idactive ;  
				ProfilesInfo[nid] = new Profile();
				ProfilesInfo[nid].special = TP[i].special ;
				ProfilesInfo[nid].ciiu = TP[i].ciiu ;

					//  TP[ Profiles[x].id ].idactive == x  and if TP[x].idactive>=0 ==> Profiles[ TP[x].idactive ].id ==x
					ProfilesInfo[nid].TP_id = i ;    
					
			}                    
		}
		//long[][] asales =     new long[ Profiles.length  ][Sam.nCiiu] ; 
		//long[][] apurchases = new long[ Profiles.length  ][Sam.nCiiu]  ; 
			
		for (int i=0; i < AT.length; i++) {
		    			    	
		        id1 = AT[i].id1 ; 
		        id2 = AT[i].id2 ;
		        value = AT[i].value ;
		        		        
		        idciiu1 = TP[id2].ciiu_id ;
		        idactive = TP[id1].idactive ;
		        		        
		        if (TP[id1].active) 
		            if (value > 0) {		        	
		               profilesSales[idactive][idciiu1] =profilesSales[idactive][idciiu1] + value  ;
		               ProfilesInfo[idactive].hasSales = true ;
		               TP[id1].lstSales.add( i ) ;
		               TP[id1].totSales = TP[id1].totSales + value ;
		              		               
		            }
		        
		        idciiu2 = TP[id1].ciiu_id ;
		        idactive = TP[id2].idactive ;
		        if (TP[id2].active) 
		            if (value > 0) {
		            	profilesPurchases[idactive][ idciiu2] = profilesPurchases[idactive][ idciiu2] + value  ;
			            ProfilesInfo[idactive].hasPurchases = true ;
			            TP[id2].lstPurchases.add(i) ; 
			            TP[id2].totPurchases = TP[id2].totPurchases + value ; 
		            }

		        
		        /*
		        if (idactive == 0 ) 
		        	System.out.printf("1: %d %d %d %d %12.2f %d %d \n ", TP[id1].idactive, TP[id2].idactive ,
		        			id1, id2, value, TP[id2].ciiu_id , TP[id1].ciiu_id );
 
		         */
		    }

		    this.setProfileNormalization( );
 
	}

	
	private void setProfileNormalization(  ) {
	    double totsales = 0, totpurchases = 0 ;
	    
	    for (int i=0; i < ProfilesInfo.length; i++) {
            totsales = 0 ; totpurchases = 0 ; 
	    	for (int j=0; j < nCiiu; j++) {
	    		totsales = totsales + profilesSales[i][j]  ;
	    		totpurchases = totpurchases + profilesPurchases[i][j]   ;
	    	}
	    	
	    	
	        if (totsales > 0)	
	    	  for (int j=0; j < nCiiu; j++) 
	    		   profilesSales[i][j] = profilesSales[i][j]   /totsales ; 		    			    	

	        if (totpurchases > 0)	
		    	  for (int j=0; j < nCiiu; j++) 
		    		   profilesPurchases[i][j] = profilesPurchases[i][j] /totpurchases ;
	        
	        
	       // profiles[i].setBinaryProfile(i) ; 
	    }
	}

		
	
    	
	// This method performs one iteration of EM Algorithm 
	public boolean IterationKL()  {
	    
        return false; 
	}
   
   
    public void run() {
    	
   	 this.setProfiles() ;
   	 this.setdicCiiu() ;  // Setting of Cluster-Ciiu

   	 ArrayList<String> lst = new ArrayList<String>() ;

   	 Iteration=0; 
   	 while (true) {
   		Report.PrintCiiuCiiu()  ;   		   		
   		Report.PrintCiiuProfile() ;
   		Report.PrintProfilesCiiu(lst, path + "profiles"+Integer.toString(Iteration)+".csv", 0) ;
   		Report.PrintTaxpayersCiiuChange() ;   		   		
   		Report.PrintOutliers(); 
   		 
   	    System.out.printf("Iteration  %d \n", Iteration) ;
    	if (oAlgo.Iteration(Iteration) )
    	   break ;

        Iteration++ ; 
	 }
 	
   	 
   	 this.setProfiles(); 
   	 Sam.setdicCiiu();
   	 
   	Report.PrintCiiuCiiu()  ;
   	Report.PrintCiiuProfile() ;
   	Report.PrintTaxpayersCiiuChange() ;   
   	Report.PrintProfilesCiiu(lst, path + "profiles"+Integer.toString(Iteration)+".csv", 0) ;
   	Report.PrintOutliers(); 
   	
     datafactory.saveMovTaxpayers();
	 datafactory.saveIsicprofile();
	 datafactory.saveOutliers();
	 datafactory.saveMovisic();
	 //datafactory.UpdatetNewCiiu( ) ;	 
	 // System.out.println(dicCiiu);
	 // System.out.println(countCiiu);
    	
    }
    
    
    public static ArrayList< TabIntegerStringString >  getMovISIC() {
		ArrayList< TabIntegerStringString > lst = new ArrayList<>(  ) ;
		 
		 for (int i=0; i < Sam.TP.length; i++ ) {
			  String sCiiu = Sam.TP[i].ciiu_old ; 
		       	  
		      if (Sam.TP[i].active && Sam.TP[i].ciiu.compareTo( sCiiu )!=0) {
		    	 lst.add( new TabIntegerStringString(Sam.ProfilesInfo[ Sam.TP[i].idactive ].TP_id ,  Sam.TP[i].ciiu_old , Sam.TP[i].ciiu ) );  
		      }
		 }
		 Collections.sort(lst) ; // new Comparator<TabIntegerStringString>() à;
		 
		 ArrayList< TabIntegerStringString > lstgrp = new ArrayList<>() ;
		 int index = 0 ; 
		 while(index < lst.size()) {
			 String s1 = lst.get(index).s1 ; 
			 String s2 = lst.get(index).s2 ; 
			 int cnt = 0 ; 
			 while(  index < lst.size() && lst.get(index).s1.compareTo(s1)==0 && lst.get(index).s2.compareTo(s2)==0 ) {
				 cnt++ ; 
				 index++; 
			 }
			 lstgrp.add( new TabIntegerStringString(cnt,  s1 , s2 ) );
		 }
         int tot= 0;
         for (TabIntegerStringString o: lstgrp)
        	 tot =tot +o.id;
		 
		 System.out.printf("getMovISIC: Total of changes of ciiu %d total 2 %d \n", lst.size(), tot) ;
        return lstgrp ; 
    }
    
    
    
	public static void main(String[] args) {

		Sam oApp = new Sam();
		oApp.run();
		
							
		 				
        }	
	
	// =================================================================================
	
    private void checkMaps() {
      	 boolean isok = true;  
      	 for (int x=0; x < TP.length;   x++) {
      		 if (TP[x].idactive>=0)
      			 if  ( ProfilesInfo[ TP[x].idactive ].TP_id !=x) 
      			    isok = false; 			      		  
      	 }
      	 if (isok)
      		 System.out.println("Map TP --> Profiles is ok");
      	 else 
      		 System.out.println("Map TP --> Profiles is not ok");

      	 isok = true;  
      	 for (int x=0; x < ProfilesInfo.length;   x++) {
      	      if (TP[ ProfilesInfo[x].TP_id ].idactive != x )
      	    	  isok = false;
      	 }
      	 if (isok)
      		 System.out.println("Map Profiles --> TP is ok");
      	 else 
      		 System.out.println("Map Profiles --> TP is not ok");
      	 
      	
       }

    

	public List<Integer> getOutliers_old() {
		
		List<Integer> lstOutliers = new ArrayList<>(); // List of outliers
		// System.out.printf("%s %d \n", this.ciiu, this.nType) ; 
		
		int iter = 0, maxiter = 10;   
		while (iter < maxiter) {
			ArrayList<OutlierCandidate> lstCantidatesOutliers  = new ArrayList<>() ;  // List of candidates to be outliers
			int freeDegree = 0 ; // this.freeDegree(); 
		   	    					
	   	    // To be outlier the mahalabois distance must be large and the taxpayers mub be not special
			if (freeDegree > 0 ) { // && lstTaxpayers.size() > Sam.spurious
				// for (Integer x:lstTaxpayers) 
				{
					int x= 0; 		 					
					 double d = 0 ; // this.getMahalanobisDistance(x) ; 
					 // boolean special = false ;
					 boolean special = Sam.TP[Sam.TP_index[x] ].special ;  				 
					 if (d > chiSquare.getCriticalValue( freeDegree)  &  !special  )   {
						 lstCantidatesOutliers.add( new OutlierCandidate(x,d) ) ;
					 }    
				}			
			}		
			if (lstCantidatesOutliers.size() == 0)
				break ; 
					
			// Selection of outliers
			// Descending Sort by Mahalanobis distance of candidates 
			Collections.sort(lstCantidatesOutliers) ;
			double dismax = lstCantidatesOutliers.get(0).getValue() ; 
							
	        int k = 0 ; 
	        List<Integer> lst = new ArrayList<>(); // List of outliers
			while ( k < lstCantidatesOutliers.size() &&  lstCantidatesOutliers.get(k).getValue() > dismax*0.95 ) {
				OutlierCandidate o = lstCantidatesOutliers.get(k) ;
				lst.add( o.getId() ) ;
				k++ ;				
			}
			
			iter++ ; 					
		}		
		return  lstOutliers; 
	}


	/// This method computes the Mahalanobis distance as long as the profile x is not an outlier 
	// in sales and purchase profiles otherwise the result is <"", -1>  
	public Pair< String, Double>  getCandidate(int x) {
				 
		double dSales, dPurchases ;  
  
		double mindis = 9999999;  
		String Ciiu = new String() ;      	 
	    for (Entry<String, ClusterCiiuMaster > entry : dicCiiu.entrySet()) {
	    	ClusterCiiuMaster o = entry.getValue() ; 
	        // System.out.println(entry.getKey() + ":" + o);
		     
			 //    System.out.printf("x= %d Ciiu %s  \n",x,o.ciiu ) ;
	        
	    	
	    	/*
	    	 if (ProfilesInfo[x].hasSales && o.Sales.numTaxpayers()>0)
	    	    	    dSales =  o.Sales.SymKL( x ) ;	    
	    	 else
	    		dSales = 0 ; 
	    	
	    	 if (ProfilesInfo[x].hasPurchases && o.Purchases.numTaxpayers()>0)
	//    	    dPurchases =  o.Purchases.SymKL( x ) ;
	    	 else 
	    		dPurchases = 0 ;  
	    	 
	    	 */
	    	 
	    	//if (this.Iteration == 4)
	      	//   System.out.printf("x= %d Ciiu %s dSales= %10.5f freeDegreeSales=%d dPurchases= %10.5f freeDegreePurchases=%d \n",x,o.ciiu,  dSales, freeDegreeSales,dPurchases, freeDegreePurchases ) ;
	    	 	      	    
	    	 double distancia = 0 ; 
	    	// double distancia = Math.max(dSales, dPurchases) ; 
			 if (distancia < mindis ) {
				 mindis = distancia; 
				 Ciiu = entry.getKey() ; 
	         }

	    }		
	    return  new Pair<String, Double>(Ciiu, mindis ); 
	}
    

/*
 * 	   
// This method computes the variation of variance of KL distance when the taxpayer x is added 
public double  deltavariance( int x, String ciiu) {

int id = ProfilesInfo[ x ].TP_id ;  
  int nnew = Util.binarySearchCIIU( aCIIU, 0,  aCIIU.length, ciiu.substring(0, 3)) ;
  	        	    
 double delta = 0 ; 
for(Integer i:TP[id].lstSales) {		   
    // Note that TP[ AT[i].id1 ].idactive == x holds  
   
    int x1 = TP[ AT[i].id2 ].idactive ; 
	ClusterCiiu cCiiu = dicCiiu.get( ProfilesInfo[x1].ciiu ) ;          
	int nold = Util.binarySearchCIIU( aCIIU, 0,  aCIIU.length, cCiiu.ciiu.substring(0, 3)) ;
	    double totvalue = Sam.TP[ Sam.ProfilesInfo[x].TP_id ].totSales ;        	
	double value = AT[i].value ;         	
	delta = delta + cCiiu.Sales.getDeltaVarScore(nold,  nnew, x1,  value/totvalue) ; 		   
}


for(Integer i:TP[id].lstPurchases) {
   // Note that TP[ AT[i].id2 ].idactive == x holds 
   int x1 = TP[ AT[i].id1 ].idactive ; 
	   ClusterCiiu cCiiu = dicCiiu.get( ProfilesInfo[x1].ciiu ) ;          
	   int nold = Util.binarySearchCIIU( aCIIU, 0,  aCIIU.length, cCiiu.ciiu.substring(0, 3)) ;
	   double totvalue = Sam.TP[ Sam.ProfilesInfo[x].TP_id ].totPurchases ;
	   double value = AT[ i ].value ;         	
	   delta = delta + cCiiu.Purchases.getDeltaVarScore(nold,  nnew, x1,  value/totvalue) ; 		   
}
	   
return delta; 

}


 * 
 */
	
	

}
	
