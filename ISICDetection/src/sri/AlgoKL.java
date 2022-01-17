package sri;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;

public class AlgoKL implements AlgoType{

    ArrayList<Integer> lstOutliers;  // List of global outliers         
    int prev_outlier ; 
    //ArrayList<Integer> lstassigned2 ;
	
    public  AlgoKL() {
        lstOutliers = new ArrayList<>(); // List of global outliers         
  
		prev_outlier = 0 ; 
    	
    }
    
	public boolean Iteration(int iteration) {
		// TODO Auto-generated method stub
		
		   
  // Detection of outlier for each Cluster-Ciiu
   this.setOutliers(); 
	 System.out.printf("Total number of Outliers %d Number of previous outliers %d \n", lstOutliers.size(), this.prev_outlier ) ;
   int kk=0; 

	 System.out.printf("Total number of Re-Outliers %d \n", kk) ;
	 
	 
     if ( iteration > 1 && this.prev_outlier < lstOutliers.size()   )
         return true ; 

  this.prev_outlier = lstOutliers.size() ; 
  
  // Assignation of outliers to Ciiu in which they do not produce more outliers  
  List<Integer> lstassigned = this.assignOutliers() ; 
  
  // Update the field TP[ i ].ciiu_prev  
  for (int i=0; i < Sam.TP.length; i++)
  	 Sam.TP[ i ].ciiu_prev =  Sam.TP[ i ].ciiu ;  
  
  
  if (lstassigned.size() ==0)
  	return true; 
  
     this.UpdateParams() ;
		return false;
	}

	public ArrayList<Integer>  getOutliers() {
		return lstOutliers; 
	}
	
	public void UpdateParams() {
	  	  for (int j=0; j < Sam.nCiiu; j++) {
	  		  double minprofile = 10, maxprofile = 0; 
	  		  for (int i=0; i < Sam.profilesSales.length; i++) {
	  			  if (minprofile > Sam.profilesSales[i][j] && Sam.profilesSales[i][j] >0 )
	  				  minprofile =  Sam.profilesSales[i][j] ; 

	  			  if (maxprofile < Sam.profilesSales[i][j] && Sam.profilesSales[i][j] >0 )
	  				  maxprofile =  Sam.profilesSales[i][j] ; 
	  			  
	  		  }
	  		  if (minprofile == 10 &&  maxprofile == 0)
	  			  Sam.scalesSales[j]=1 ; 
	  		  else 
	  			  Sam.scalesSales[j] = Math.max( 1 , 0.5 * Math.abs( Math.log(maxprofile/minprofile) )) ;
	  	  }
	  		  

	  	  for (int j=0; j < Sam.nCiiu; j++) {
	  		  double minprofile = 10, maxprofile = 0; 
	  		  for (int i=0; i < Sam.profilesPurchases.length; i++) {
	  			  if (minprofile > Sam.profilesPurchases[i][j] && Sam.profilesPurchases[i][j] >0 )
	  				  minprofile =  Sam.profilesPurchases[i][j] ; 
;
	  			  if (maxprofile < Sam.profilesPurchases[i][j] && Sam.profilesPurchases[i][j] >0 )
	  				  maxprofile =  Sam.profilesPurchases[i][j] ; 
	  			  
	  		  }
	  		  if (minprofile == 10 &&  maxprofile == 0)
	  			   Sam.scalesPurchases[j]=1 ; 
	  		  else 
	  			   Sam.scalesPurchases[j]= Math.max( 1 , 0.5 * Math.abs( Math.log(maxprofile/minprofile) )) ;
	  	  }
	 	  
	  	  /*
	  	for (int j=0; j < nCiiu; j++) {
	  		System.out.printf("Ciiu_id %d Scala Sales %12.5f  Scala Purchases %12.5f  \n ", j, scalesSales[j], scalesPurchases[j]) ; 
	  	}

	  	   * 
	  	   */
	  	  

	};	
	
	public void setOutliers() {
		   
		    lstOutliers.clear();
		    Sam.dicCiiu.forEach((k,v)-> {
		    	ArrayList<Integer> lst = (ArrayList<Integer>) v.getOutliers() ;
		    	for (Integer i:lst) {
		    		// int index = Sam.ProfilesInfo[i].TP_id ; 
		    		lstOutliers.add( i ) ;	
		    	}	    		    	
		    	} ) ;
	  }
	  
		
		
		// This method assign ciiu when ciiu.subsutr(0,3) == ciiunew.substr(0,3)  
		private Pair < ArrayList< Integer > , ArrayList<Integer> >   assignEqualEntryCiiu() {
			ArrayList<Integer> lstassigned = new ArrayList<Integer>() ;  // lstassigned saves the assigned oultiers to a new cluster-ciiu
			ArrayList<Integer> lstOutliersnew = new ArrayList<>() ;  
			
	        for (Integer x: lstOutliers) { 
	        	boolean found = false; 
	    		double mindis = 9999999;
	    		String ciiu = ""; 
	        	for ( Map.Entry< String, ClusterCiiuMaster > entry : Sam.dicCiiu.entrySet() ) {
	        		// System.out.println(entry.getKey() + " = " + entry.getValue());

	                ClusterCiiuMaster o = entry.getValue() ;
	            	String s1 = Sam.ProfilesInfo[  x ].ciiu.substring(0, 3) ; 
	            	String s2 = o.ciiu.substring(0,3) ;  
	            	 
	        		double dSales = 0 ;
	        		if (o.Sales.numTaxpayers() > 0 && Sam.ProfilesInfo[x ].hasSales) {
	        		     dSales = o.Sales.getScore(x) ;
	        		     if (o.Sales.nullIntersection(x) )
	        		    	 dSales = o.Sales.threshold() ; 	 
	        			// dSales = o.Sales.getMahalanobisDistance( outlier.getId()) ;
	        		}
	        		           		
	        		double dPurchases = 0 ; 
	        		if (o.Purchases.numTaxpayers() > 0 && Sam.ProfilesInfo[ x ].hasPurchases ) {
	        			dPurchases = o.Purchases.getScore(x) ;
	        			if (o.Purchases.nullIntersection(x) )
	        				dPurchases = o.Purchases.threshold() ; 
	        			// dPurchases = o.Purchases.getMahalanobisDistance( outlier.getId()) ;
	        		}
	        		           		
	        		 boolean validCiiu = dSales < o.Sales.threshold() && dPurchases < o.Purchases.threshold() ;
	        		 //boolean validCiiu = dSales < chiSquare.getCriticalValue( o.Sales.freeDegree() ) && dPurchases < chiSquare.getCriticalValue( o.Purchases.freeDegree() );    
	                 if ( validCiiu  && s1.compareTo(s2)==0 ) {                   
	        		    found = true;
	        		    if (Math.max(dPurchases, dSales) < mindis) {
	        		    	mindis = Math.max(dPurchases, dSales)  ;
	        		    	ciiu = o.ciiu ; 
	        		    }        		            		            		            		                   	
	                 }                                
	        	}
	        	
	            if (found)  { // Assignation to the lowest distance

	            	ClusterCiiuMaster cCiiu = Sam.dicCiiu.get( ciiu ) ;
	     		   
	    	    	Sam.TP[ Sam.ProfilesInfo[x].TP_id ].ciiu = ciiu ; 
	    	    	Sam.ProfilesInfo[ x ].ciiu = cCiiu.ciiu ;
	        		int nEntry = Util.binarySearchCIIU( Sam.aCIIU, 0,  Sam.aCIIU.length, cCiiu.ciiu.substring(0, 3)) ;
	        		Sam.TP[ x ].ciiu_id = nEntry ;       		    
	    		    cCiiu.addTaxpayer( x ) ;
	    		    lstassigned.add( x ) ;  
	            } else {
		    		// int index = Sam.ProfilesInfo[x].TP_id ; 
		    		lstOutliersnew.add( x ) ;	             
	            }
	        }
	         
	        System.out.printf("Number of assigned taxpayers=%d \n ",   lstassigned.size()); 		                            
			return  new Pair < ArrayList< Integer > , ArrayList<Integer> >  ( lstOutliersnew,  lstassigned );
		}
		
		
		
		// This method triers to assign outliers to a new clusterciiu
	   private List<Integer> assignOutliers() {
		   	        
		    // descending Sorting lstOutlier  based on the total number of connections     
			 Collections.sort(lstOutliers) ; 						
	 	 
			 Pair < ArrayList<Integer>  , ArrayList<Integer> >  res = assignEqualEntryCiiu()  ;
			 lstOutliers =  res.getFirst() ; 
			 List<Integer> lstassigned = res.getSecond() ; 
			 System.out.printf("Updating dicCiiu after assignEqualEntryCiiu Number of Outliers =%d \n ", lstOutliers.size()) ;
			         
			 Sam.setdicCiiu() ; 
				    		
	         for (Integer x: lstOutliers) {
         	
	        	// If the outlier has less that 100 connections to other taxpayers then proceed
	        	int numberConnections = Sam.TP[ Sam.ProfilesInfo[x].TP_id].lstPurchases.size() + Sam.TP[ Sam.ProfilesInfo[x].TP_id].lstSales.size() ;  
	            if ( numberConnections < 100 ) {	

	            	double mindelta = 0;
	            	String ciiunew = "";  
	            	for (Map.Entry< String, ClusterCiiuMaster > entry : Sam.dicCiiu.entrySet()) {
	            		// System.out.println(entry.getKey() + " = " + entry.getValue());
	                     	
	            		ClusterCiiuMaster onew = entry.getValue() ;            	             		            	            
	            		double delta = onew.deltavariance(  x ) ; 
	            		
	                    if ( delta < mindelta) {
	                    	mindelta = delta  ;   
	                    	ciiunew = onew.ciiu ;  	
	                    }                    	                                         	                                    
	            	}
	            	
	            	if (mindelta < 0) { 
	                	ClusterCiiuMaster cCiiu = Sam.dicCiiu.get( ciiunew ) ;
	         		   
	        	    	Sam.TP[ Sam.ProfilesInfo[x].TP_id ].ciiu = cCiiu.ciiu ; 
	        	    	Sam.ProfilesInfo[ x ].ciiu = cCiiu.ciiu ;
	            		int nEntryCiiu = Util.binarySearchCIIU( Sam.aCIIU, 0,  Sam.aCIIU.length, cCiiu.ciiu.substring(0, 3)) ;
	            		Sam.TP[ Sam.ProfilesInfo[x].TP_id ].ciiu_id = nEntryCiiu ;       		    
	        		    cCiiu.addTaxpayer( x ) ;
	        		    lstassigned.add( x ) ;  
	            		this.setOneProfile( x, ciiunew ) ; 
	            	}
	            }        	
	        }
		    return lstassigned ; 
	   }
		
	   
	   private void setOneProfile(int x, String ciiunew) {

		   int id = Sam.ProfilesInfo[x].TP_id ;	   
	   	   int nnew = Util.binarySearchCIIU( Sam.aCIIU, 0,  Sam.aCIIU.length, ciiunew.substring(0, 3)) ;
	   	   
	   	   for(Integer i: Sam.TP[id].lstSales) {
	   		   // Note that TP[ AT[i].id1 ].idactive == x holds 
	           int x1 = Sam.TP[ Sam.AT[i].id2 ].idactive ; 
	       	   String ciiuold = Sam.dicCiiu.get( Sam.ProfilesInfo[x1].ciiu ).ciiu ;       	   
	       	   int nold = Util.binarySearchCIIU( Sam.aCIIU, 0,  Sam.aCIIU.length, ciiuold.substring(0, 3)) ;

	       	   double totvalue = Sam.TP[ Sam.ProfilesInfo[x].TP_id ].totSales ;       	   
	       	   double value = Sam.AT[i].value ;
	       	   
	           double fraction = value/totvalue ;         	   
	     	   Sam.profilesSales[x1][nold] = Sam.profilesSales[x1][nold] - fraction ;      		
	     	   Sam.profilesSales[x1][nnew] = Sam.profilesSales[x1][nnew] + fraction  ;       	   
		   }

		   for(Integer i: Sam.TP[id].lstPurchases) {
			  // Note that TP[ AT[i].id2 ].idactive == x holds 
	           int x1 = Sam.TP[ Sam.AT[i].id1 ].idactive ; 
	      	   String ciiuold = Sam.dicCiiu.get( Sam.ProfilesInfo[x1].ciiu ).ciiu ;       	   
	      	   int nold = Util.binarySearchCIIU( Sam.aCIIU, 0,  Sam.aCIIU.length, ciiuold.substring(0, 3)) ;

	      	   double totvalue = Sam.TP[ Sam.ProfilesInfo[x].TP_id ].totPurchases ;
	       	   double value = Sam.AT[i].value ;
	       	          	         	   
	           double fraction = value/totvalue ;         	     
	    	   Sam.profilesPurchases[x1][nold] = Sam.profilesPurchases[x1][nold] - fraction ;      		
	    	   Sam.profilesPurchases[x1][nnew] = Sam.profilesPurchases[x1][nnew] + fraction  ;       	    
		   }
	   }
	   
}
