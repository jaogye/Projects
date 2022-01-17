package sri;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;

import org.ejml.simple.SimpleMatrix;

public class Report {

	
	static public void PrintTaxpayersCiiuChange()  {
	     FileWriter g = null;
		try {
			g = new FileWriter(Sam.path + "TaxpayersCiiuChange"+Integer.toString(Sam.Iteration)+".csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		ArrayList< TabIntegerStringString > lst = new ArrayList<>() ;
		 
		for (int i=0; i < Sam.TP.length; i++ ) {
			 String sCiiu = Sam.TP[i].ciiu_old ; 
		       	  
		      if (Sam.TP[i].active && Sam.TP[i].ciiu.compareTo( sCiiu )!=0) {
		    	 lst.add( new TabIntegerStringString( Sam.ProfilesInfo[ Sam.TP[i].idactive ].TP_id,  Sam.TP[i].ciiu_old , Sam.TP[i].ciiu ) );  
		      }
		 }
		 Collections.sort(lst) ; // new Comparator<TabIntegerStringString>() à;
		 
		 PrintWriter p = new PrintWriter(g);
		 p.printf("TP.id; Ciiu_old; Ciiu_new; \n" );
		        
		 for (TabIntegerStringString o:lst  ) {
		  	  p.printf("%d %s; %s; \n" ,  o.id , o.s1 , o.s2);
		 }
		 p.close(); 
		 
		 
		//  for (int i=0; i < TP.length; i++ ) {
		//	  String sCiiu = TP[i].ciiu_old ; 
		//      if (TP[i].active && TP[i].ciiu.compareTo( sCiiu )!=0) {
		//    	  p.printf("%d; %s; %s; \n" , TP[i].idactive,  TP[i].ciiu_old , TP[i].ciiu );  
		//      }
		//   }		    	    		 
	    
		 
	}

	
	static public void PrintCiiuCiiu()  {
	     FileWriter g = null;
		try {
			g = new FileWriter(Sam.path + "CiiuCiiu"+Integer.toString(Sam.Iteration)+".csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 ArrayList< TabIntegerStringString > lstgrp =  Sam.getMovISIC(); 
		 		 
		 PrintWriter p = new PrintWriter(g);
		 p.printf("Ciiu_old;Ciiu_new;cnt \n" );
		        
		 for (TabIntegerStringString o:lstgrp  ) {
		  	  p.printf("%s;%s;%d \n" , o.s1 , o.s2, o.id );
		 }
		 p.close(); 
		 
		 
		//  for (int i=0; i < TP.length; i++ ) {
		//	  String sCiiu = TP[i].ciiu_old ; 
		//      if (TP[i].active && TP[i].ciiu.compareTo( sCiiu )!=0) {
		//<    	  p.printf("%d; %s; %s; \n" , TP[i].id,  TP[i].ciiu_old , TP[i].ciiu );  
		//      }
		//   }		    	    		 	    		 
	}
	
	
  static public void PrintCiiuProfile()  {
		
	   FileWriter g = null;
	try {
		g = new FileWriter( Sam.path + "ciiuprofile"+Integer.toString( Sam.Iteration)+".csv");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   PrintWriter p = new PrintWriter(g);

	    p.printf("Ciiu;Type;NEntries;NDegree;NTaxpayers;"  );
	   for (int j=0; j < Sam.nCiiu ; j++)
		   p.printf("%s;" , Sam.aCIIU[j].cod );

	   p.printf("\n"  );
	   for (Entry<String, ClusterCiiuMaster > entry : Sam.dicCiiu.entrySet()) {
          ClusterCiiuMaster o = entry.getValue() ; 
		   boolean hasSales = false ; 
		   for (int j=0; j < Sam.nCiiu ; j++)
			   if (o.Sales.getmean(j)  > 0)
                  hasSales = true ; 			    

		   if (hasSales) {
			   double tot = 0; 
			   p.printf("%s;Sales;%d;%d;%d;" , o.ciiu, o.Sales.numEntries(), o.Sales.freeDegree(),  o.Sales.numTaxpayers() );
			   for (int j=0; j < Sam.nCiiu ; j++) {
				   p.printf("%6.4f;" , o.Sales.getmean(j)   );
				   tot = tot +  o.Sales.getmean(j)  ; 
			   }	 			   
			   p.printf("%6.4f;\n", tot  );		   			   
		   }

		   boolean hasPurchases = false ; 
		   for (int j=0; j < Sam.nCiiu ; j++)
			   if (o.Purchases.getmean(j)   > 0)
                  hasPurchases = true ; 			    

		   if (hasPurchases) {
			   double tot = 0;
			   p.printf("%s;Purchases;%d;%d;%d;" , o.ciiu,o.Purchases.numEntries(), o.Purchases.freeDegree(), o.Purchases.numTaxpayers() );
			   for (int j=0; j < Sam.nCiiu ; j++) {
				    p.printf("%6.4f;" , o.Purchases.getmean(j)  );
			        tot = tot +  o.Purchases.getmean(j)  ;				   
			   }
			   p.printf("%6.4f;\n", tot  );			   
		   }
	   }		   
	    p.close(); 	    
  } 
  


  

  // Print the profiles of a given ciiu code 
  static public void PrintProfilesCiiu(ArrayList<String> lstcod, String file, int nType)  {
		
	   FileWriter g = null;
	try {
		g = new FileWriter(file);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   PrintWriter p = new PrintWriter(g);

	   int nScores = 16; 
	   p.printf("Ciiu;Type;Profile_id;Active;distanceToCenter;CriticalValue;"  );
	   for (int j=0; j < nScores ; j++)
		   p.printf("%s;" , "sc" + Integer.toString(j) );
	   	   	   
	   for (int j=0; j < Sam.nCiiu ; j++)
		   p.printf("%s;" , Sam.aCIIU[j].cod );
	   
	   p.printf("\n"  );

	   	   
	   for (String scod : lstcod) {
		   ClusterCiiuMaster o = Sam.dicCiiu.get(scod) ;

		   // Print Sales mean 
		   
		   if ((nType ==0 || nType == 1) && o.Sales.numTaxpayers()  > Sam.spurious  ) {
			   p.printf("%s;Sales-Center;;%d;%d;%d;" , o.ciiu,o.Sales.numEntries(), o.Sales.freeDegree() ,o.Sales.numTaxpayers() );
			   for (int j=0; j < nScores ; j++) {
				   p.printf(";"  ); 
			   }	 			   
			   for (int j=0; j < Sam.nCiiu ; j++) {
				   p.printf("%7.5f;" , o.Sales.getmean(j)   ); 
			   }	 			   
			   p.printf("\n");
			   			   		    
			   for (Integer i: o.Sales.getTaxpayers() ) {
				    double d1 = 0 ;  // o.Sales.getMahalanobisDistance(i) ; 
				    double d2 = chiSquare.getCriticalValue( o.Sales.freeDegree() ) ;
						    			    
					p.printf("%s;Sales;%d;%b;" , Sam.ProfilesInfo[i].ciiu, i, Sam.TP[ Sam.ProfilesInfo[i].TP_id].active);
					p.printf("%7.5f;%7.5f;" ,d1, d2);
				   
/*
 * 					//SimpleMatrix scores = o.Sales.getScores(i) ;
					int k = 0; 
					for (k=0; k < nScores && k < scores.numCols()  ; k++)  
						p.printf("%7.5f;" , scores.get(0,k)   );

					
					for (int j=k; j < nScores   ; j++)  
						p.printf(";"  );

 */
										
				    for (int j=0; j < Sam.nCiiu ; j++)			
				 		p.printf("%7.5f;" , Sam.profilesSales[i][j]   );
				    p.printf("\n"  );		   			   
			   }   
			   
		   }
		   
         // Print Purchases mean  
		   if ( (nType ==0 || nType == 2 )  &&  o.Purchases.numTaxpayers() > Sam.spurious ){
			   p.printf("%s;Purchases-Center;;%d;%d;%d;" , o.ciiu,o.Purchases.numEntries(), o.Purchases.freeDegree(),o.Purchases.numTaxpayers() );
			   for (int j=0; j < nScores ; j++) {
				   p.printf(";"  ); 
			   }	 			   

			   for (int j=0; j < Sam.nCiiu ; j++) {
				   p.printf("%7.5f;" , o.Purchases.getmean(j)  ); 
			   }	 			   

			   p.printf("\n"  );
			   				   
			   for (Integer i: o.Purchases.getTaxpayers() ) {			   
				    double d1 =  0 ;  // o.Purchases.getMahalanobisDistance(i) ; 
				    double d2 = chiSquare.getCriticalValue( o.Purchases.freeDegree()) ;
				    			    
					p.printf("%s;Purchases;%d;%b;" , Sam.ProfilesInfo[i].ciiu, i, Sam.TP[ Sam.ProfilesInfo[i].TP_id].active);
					p.printf("%7.5f;%7.5f;" , d1, d2);

/*
 * 					SimpleMatrix scores = o.Purchases.getScores(i) ;
					int k = 0; 
					for (k=0; k < nScores && k < scores.numCols()  ; k++)  
						p.printf("%7.5f;" , scores.get(0,k)   );
					
					for (int j=k; j < nScores   ; j++)  
						p.printf(";"  );
					
					for (int j=0; j < Sam.nCiiu ; j++)			
				 		p.printf("%7.5f;" , Sam.profilesPurchases[i][j]   );
				    p.printf("\n"  );		   			  
 */
					
					
			   }  
			   			  			   
		   }
 				   
	   }			      
	    p.close(); 	    
 } 
 

  
  
   
	static public void PrintOutliers()  {
		FileWriter g = null;
		try {
			g = new FileWriter(Sam.path + "Outliers"+Integer.toString(Sam.Iteration)+".csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 PrintWriter p = new PrintWriter(g);
		 p.printf("Ciiu;Type;Profile_id;distanceToCenter;CriticalValue;"  );
	     for (int j=0; j < Sam.nCiiu ; j++)
			  p.printf("%s;" , Sam.aCIIU[j].cod );

		 p.printf("\n"  );


		 
		 for (Integer x: Sam.oAlgo.getOutliers() ) {
			 
			 ClusterCiiuMaster o = Sam.dicCiiu.get( Sam.ProfilesInfo[x].ciiu ) ; 
			 boolean hasSales = false ; 
			 for (int j=0; j < Sam.nCiiu ; j++)
				 if (Sam.profilesSales[x][j]  > 0)
	                 hasSales = true ; 			    

            if (hasSales) {
            	               	
     		   p.printf("%s;Sales;%d;%d;%d;Center;" , o.ciiu,o.Sales.numEntries(), o.Sales.freeDegree() ,o.Sales.numTaxpayers() );
    		   for (int j=0; j < Sam.nCiiu ; j++) {
    			   p.printf("%6.4f;" , o.Sales.getmean(j)  ); 
    		   }	 			   
    		   p.printf("\n"  );
            	            	
   			   p.printf("%d;Sales;;;;;" , x );			 
   			   for (int j=0; j < Sam.nCiiu ; j++)
   			       p.printf("%12.4f;" , Sam.profilesSales[x][j] );            	 
               }

			   boolean hasPurchases = false ; 
			   for (int j=0; j < Sam.nCiiu ; j++)
				 if ( Sam.profilesPurchases[x][j]  > 0)
	                 hasPurchases = true ; 			    
            
			 if (hasPurchases) {
	     		   p.printf("%s;Purchases;%d;%d;%d;Center;" , o.ciiu,o.Purchases.numEntries(), o.Purchases.freeDegree() ,o.Purchases.numTaxpayers() );
	    		   for (int j=0; j < Sam.nCiiu ; j++) {
	    			   p.printf("%6.4f;" , o.Purchases.getmean(j)  ); 
	    		   }	 			   
	    		 p.printf("\n"  );
				 p.printf("%d;Purchases;" , x );
				 for (int j=0; j < Sam.nCiiu ; j++)
				       p.printf("%12.4f;;;;;" , Sam.profilesPurchases[x][j] );
				
				 p.printf("\n" );			 				 
			 }
		 }
		   		    	    		 
	      p.close(); 
	}
		   

	

	
	
}
