package sri;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

public class testCiiu {
   
	int[] acluster ;
	int  optK= 0 ; 
    
    List<Integer> lstTaxpayers ; 	
	double[] mean ;
	List<Integer> lstEntry; 
    SimpleMatrix invCov;
    SimpleMatrix V;
    double[] singValues;
    double data[][] ; 
    int nType;
    String ciiu; 
    private int freeDegree  ;     

	public testCiiu(int nType, String ciiu, double a[][] ) {
		this.nType = nType ; 
		this.ciiu = ciiu; 
		this.lstEntry= new  ArrayList<Integer>() ;		
		this.lstTaxpayers = new ArrayList<Integer>();		
		mean = new double[Sam.nCiiu]; 
		data = a ; 			 
	}

	
	public void addTaxpayer(int x) {
	     this.lstTaxpayers.add(x) ;
	
	}
	
    public int freeDegree() {
    	return freeDegree ;  
    }

    public int numEntries() {
    	return lstEntry.size() ; 
    }

    public int numTaxpayers() {
    	return lstTaxpayers.size() ; 
    }

    
	/// This method computes the mean of profiles assigned to aVeci
    private  double[] getMean() {
    	
    	double[] mean = new double[Sam.nCiiu] ;     	
    	double tot=0 ;   
    	for (Integer i:lstTaxpayers) 
  	  	  for (int j=0; j < Sam.nCiiu; j++ ) 
  	  		   mean[j] = mean[j] + data [i][j] ;
    	
    	for (int j=0; j < Sam.nCiiu; j++ ) 
    		tot = tot + mean[j] ;
    	
    	if (tot > 0)    	
     	   for (int i=0; i < Sam.nCiiu; i++ ) 
    		   mean[i] = mean[i]/  tot ;
    	
    	return mean ; 				   	
    }

	
	/// This method computes the covariance of columns  i and j  
	private double getProfileCovariance(int i, int j ) {
		double d = 0;
		
	  	for (Integer k: lstTaxpayers) 
	  		 d = d + (data[k][i]-mean[i]) *  (data[k][i]-mean[i]) ;  	  	   		
			
		return d; 
	}

	
	
	/// This method computes the significance level 
	public double getMahalanobisDistance(int x ) {

		SimpleMatrix d; 	    
				
		if (lstTaxpayers.size() <= Sam.spurious  )
				return 0; 

		SimpleMatrix delta = this.getScores(x) ; 
			
		d = delta.mult(this.invCov.mult(delta.transpose())); 
			
		if (d.get(0,0) < 0)
			return 99999999; 
		else
		    return d.get(0,0) ; 
	}


    // ==========================================================================


	/// This method sets the coordinates of sales and purchase profiles that are positive
	private void setlstCoordinates() {
		
		double[] tot1 = new double[Sam.nCiiu] ;
		this.lstEntry.clear();
 
		for (Integer x: lstTaxpayers )
		    for (int j=0; j < Sam.nCiiu; j++)
		    	 tot1[j] = tot1[j] + data[x][j] ;   

		// int[] inv_sales ; // map salesmean[x] --> lstEntrySales.get(y)
		for (int i=0; i < tot1.length; i++) 
			if (tot1[i] > 0 ) {
			    // inv_sales[i] = this.lstEntrySales.size() ;
				this.lstEntry.add(i) ;			  
			}		      
	}

	
	/// A profile is outlier if either sales or purchases profile is outlier
	/// This method computes the outliers  
	public List<Integer> getOutliers(int test) {
		
		List<Integer> lstOutliers = new ArrayList<>(); // List of outliers
		// System.out.printf("%s %d \n", this.ciiu, this.nType) ; 
		
		int iter = 0, maxiter = 10;   
		while (iter < maxiter) {
			ArrayList<OutlierCandidate> lstCantidatesOutliers  = new ArrayList<>() ;  // List of candidates to be outliers
			int freeDegree = this.freeDegree(); 
		   	    
			if (this.ciiu.compareTo("G503001")==0 && nType ==  2 ) {
				int hh=0;
				hh++ ; 				
			}
			
			
	   	    // To be outlier the mahalabois distance must be large and the taxpayers mub be not special
			if (freeDegree > 0 && lstTaxpayers.size() > Sam.spurious) {
				for (Integer x:lstTaxpayers) {
							 					
					 double d = this.getMahalanobisDistance(x) ; 
					 // boolean special = false ;
					 boolean special = Sam.TP[Sam.TP_index[x] ].special ;  				 
					 if (d > chiSquare.getCriticalValue( freeDegree)  &  !special  )   {
						 lstCantidatesOutliers.add( new OutlierCandidate(x,d) ) ;
					 }    
				}			
			}		
			if (lstCantidatesOutliers.size() == 0)
				break ; 
					
			
			if (iter==0) {
				 int n1 =  lstTaxpayers.size(), n2=  lstCantidatesOutliers.size() ;
					System.out.printf("%s %d %d %d %7.4f \n", this.ciiu, this.nType,n1,n2, (double) n2/n1 ) ;
			}
				
		

			
			// Selection of outliers
			// Descending Sort by Mahalanobis distance of candidates 
			Collections.sort(lstCantidatesOutliers) ;
			double dismax = lstCantidatesOutliers.get(0).getDistance() ; 
							
	        int k = 0 ; 
	        List<Integer> lst = new ArrayList<>(); // List of outliers
			while ( k < lstCantidatesOutliers.size() &&  lstCantidatesOutliers.get(k).getDistance() > dismax*0.95 ) {
				OutlierCandidate o = lstCantidatesOutliers.get(k) ;
				lst.add( o.getId() ) ;
				k++ ;				
			}
			
			if (lstTaxpayers.size() - lst.size() < Sam.spurious)
				break; 
 			
			lstOutliers.addAll(lst) ; 
			this.lstTaxpayers.removeAll(lst) ;			
			this.UpdateStats(); 
			iter++ ; 					
		}		
		return  lstOutliers; 
	}
    
	
	public void UpdateStats() {
		
		this.setlstCoordinates() ;				
		if (this.lstEntry.size() > 0 && lstTaxpayers.size() > Sam.spurious) 
		    this.setPCA() ;
 			
	}
	   
	
	private void setPCA() {
		
		SimpleMatrix Xdata = new SimpleMatrix( lstTaxpayers.size() , lstEntry.size() ) ;   
				
		
		// Computation of covariance matrix 
			
		mean = this.getMean() ; 
		for (int j = 0 ; j < lstEntry.size() ; j++) {
			 for (int i = 0 ; i < lstTaxpayers.size() ; i++)
			     Xdata.set( i,j,data[ lstTaxpayers.get(i) ][ lstEntry.get(j) ]  );			     
			}						
 		
				
		// Centering matrix 
		for (int i=0; i < Xdata.numRows() ; i++)
			for (int j=0; j < Xdata.numCols() ; j++) 
                 Xdata.set(i,j, Xdata.get(i,j) - mean[j] ) ;
                               		
        // Computation of Cov
		SimpleMatrix cov = Xdata.transpose().mult(Xdata) ;
		double alpha = cov.numRows()-1 ;
		for (int i=0; i < cov.numRows() ; i++)
			for (int j=0; j < Xdata.numCols() ; j++) 
                 cov.set(i,j, cov.get(i,j) / alpha  ) ;
						
		SimpleSVD<SimpleMatrix> svd = cov.svd();
    	SimpleMatrix  V2 =  (SimpleMatrix) svd.getV() ;
        double[] singValues2 = svd.getSingularValues() ; 

        // Selection of PCA
        double totvar = 0 ; 
        for (int i=0; i < singValues2.length; i++) {
        	totvar = totvar + singValues2[i] * singValues2[i]  ;
        }
        
        double acumvar = 0 ; 
        freeDegree = 0; 
        
        while ( freeDegree < singValues2.length ) {
        	acumvar = acumvar +  singValues2[freeDegree] * singValues2[freeDegree] ;
        	freeDegree++; 
        	if (acumvar > Sam.maxVar * totvar)
        		break ;         	
        }
        
                
        singValues = new  double[freeDegree] ;
        // Copy the first  index singular values  
        for (int i=0; i < freeDegree ; i++) 
        	singValues[i] = singValues2[i] ;  

        V = new SimpleMatrix( lstEntry.size(),   freeDegree  ) ; 
        // Copy the first  index singular values  
        for (int i=0; i < lstEntry.size() ; i++) 
            for (int j=0; j < freeDegree ; j++)
                V.set(i,j, V2.get(i,j)) ; 
        
        // Computation of inverse square of covariance
        invCov = new SimpleMatrix( freeDegree, freeDegree  ) ;    

        for (int i=0; i < freeDegree ; i++) {
        	for (int j=0; j < freeDegree ; j++) {
        		double d = 0 ; 
        		for (int k=0; k < freeDegree ; k++) 
        			 d = d + (lstTaxpayers.size()-1) *V.get(i,k) * V.get(j,k) / singValues[k];   					
        			invCov.set(i,j, d )	; 			
        		}
        	}
        	
                 

	}
	
	public SimpleMatrix getScores(int x ) {

		SimpleMatrix scores ; 
		SimpleMatrix delta = new SimpleMatrix( 1, this.V.numRows()) ;
		for (int  i=0; i < this.lstEntry.size() ; i++)
			delta.set(0,i, data[x][ lstEntry.get(i)  ]  - mean[ lstEntry.get(i) ]);
								
		scores = delta.mult(this.V) ; 		
		return scores;  
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
	   

    
    
/// This method computes the inverse covariance of columns associated with sales and purchases profiles 
private SimpleMatrix getCov() {
	SimpleMatrix Cov = new SimpleMatrix( lstEntry.size(),  lstEntry.size()) ; 

    double cov =0; 
	if (lstTaxpayers.size() > Sam.spurious  )		{
        for (int i=0; i< lstEntry.size(); i++) 
            for (int j=0; j< lstEntry.size(); j++)
			   if (i <= j) {
                   cov = this.getProfileCovariance(lstEntry.get(i),lstEntry.get(j)) ; 
                   Cov.set(i, j, cov);
                   Cov.set(j,i, cov);                      
				}        		             			
	}


	return Cov; 
}

private SimpleMatrix getInvCov_old(int nType) {
	
	SimpleMatrix Xdata ;   
		
	// Computation of covariace matrix 
	Xdata = new SimpleMatrix( lstTaxpayers.size() , lstEntry.size() ) ;
	double[] Xmean = new double[ lstEntry.size()] ;
	for (int j = 0 ; j < lstEntry.size() ; j++) {
		 Xmean[j] = this.mean[ lstEntry.get(j)  ];
		 for (int i = 0 ; i < lstTaxpayers.size() ; i++)
			 Xdata.set( i,j, data[ lstTaxpayers.get(i) ] [ lstEntry.get(j) ]  );		     
	}						

			
	// Centering matrix 
	for (int i=0; i < Xdata.numRows() ; i++)
		for (int j=0; j < Xdata.numCols() ; j++) 
             Xdata.set(i,j, Xdata.get(i,j) - Xmean[j] ) ;
                           		
    // Computation of Cov
	SimpleMatrix cov = Xdata.transpose().mult(Xdata) ;
	double alpha = cov.numRows()-1 ;
	for (int i=0; i < cov.numRows() ; i++)
		for (int j=0; j < Xdata.numCols() ; j++) 
             cov.set(i,j, cov.get(i,j) / alpha  ) ;
					
	SimpleSVD<SimpleMatrix> svd = cov.svd();
	SimpleMatrix V =  (SimpleMatrix) svd.getV() ;
	SimpleMatrix U =  (SimpleMatrix) svd.getU() ;
    double[] singValues = svd.getSingularValues() ; 

    int conditionNumber = 100;   
    // Perturbing singular values
    for (int i=0; i < singValues.length ; i++)
        if (singValues[0] > conditionNumber * singValues[i])
        	singValues[i] = singValues[0] / conditionNumber;
        
	// Computation of inverse square of covariance
    SimpleMatrix InvCov = new SimpleMatrix( Xdata.numCols(), Xdata.numCols()  ) ;    

	for (int i=0; i < Xdata.numCols() ; i++) {
		for (int j=0; j < Xdata.numCols() ; j++) {
			double d = 0 ; 
			for (int k=0; k < Xdata.numCols() ; k++) 
			     d = d + U.get(i,k) * V.get(j,k) / singValues[k];   					
			InvCov.set(i,j, d )	; 			
		}
	}
	
   return InvCov ;     			
}
    
    public void setOptimalCluster( double[][] ddata  ) {
    	
        KMeans o = new KMeans() ;
        int maxK = 6; 
        double SSEopt = 0, SSE = 0, SSE_prev=0, elbow = Double.MAX_VALUE;
        acluster = new int[ddata.length] ;
        int[][] aacluster = new int[ ddata.length ][maxK-1 ] ;
        int times = 3 ; 
		 for (int i=1; i < maxK ; i++ ) {			 
			 SSE = Double.MAX_VALUE;   
			 for (int j=0; j < times; j++) {
				 SSEopt = o.kmeans(ddata, i) ;
				 if (SSEopt < SSE ) {
					 SSE = SSEopt ;
					 for (int k=0; k < ddata.length; k++)
						 aacluster[k][i-1] = o.acluster[k] ; 
				 }				 
			 }

			 for (int j=0; j < ddata.length ; j++ )
				 aacluster[j][i-1] = o.acluster[j] ; 
			 if (i > 1 && SSE_prev - SSE < elbow && SSE_prev - SSE >= 0) {
				elbow =  SSE_prev - SSE ;
				optK = i-1 ; 
			 }
            System.out.printf("K=%d SSE=%12.6f elbow=%12.6f \n ", i, SSE,  SSE_prev - SSE ) ;
			SSE_prev = SSE; 
		 }
		 
		 acluster = new int[ddata.length] ; 
		 for (int i=0; i < ddata.length ; i++ )
		     acluster[i] = aacluster[i][optK-1] ; 
			 
        System.out.printf("Elbow = %12.6f  optK=%d  \n ", elbow, optK ) ;
        int[] cnt = new int[ optK ] ; 
		for (int j=0; j < ddata.length ; j++ ) {
			 System.out.printf("%d) score = %12.6f cluster=%d  \n ", j, ddata[j][0], acluster[j] ) ;
			 cnt[  acluster[j]  ]++ ; 
		 }
		for (int j=0; j < optK ; j++ ) 	 
			System.out.printf("%d) cnt = %d  \n ", j, cnt[j] ) ;
    }


    
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		    String sCiiu = "S250000" ; 
		    String file = "C:\\Users\\jaaxx\\eclipse-workspace\\ISICDetection\\out\\" +  "P_"+ sCiiu +".csv" ;
		    double[][] adata ; 
		    		    
		    // Creates an array of character
		    BufferedReader reader; 
		    ArrayList< Double[] > lstdata = new ArrayList< >() ;  

		      // Creates a reader using the FileReader
		      FileReader input =  new FileReader(file);
		      reader = new BufferedReader( input ) ;  

		      // Reads lines
		     String line = reader.readLine() ;
		     line = reader.readLine() ;
		     line = reader.readLine() ;
			 int kk = 0 ; 
		     while (line != null ) {
		    	 String[] aline = line.split(";") ;
		    	  //System.out.printf("%d) %s  \n ", kk, line) ;
			      Double[] ad = new Double[Sam.nCiiu] ; 
			      for (int i=22; i < aline.length; i++) 
			           ad[i-22] = Double.parseDouble(aline[i]) ; 
			   	
			      lstdata.add(ad) ; 
		    	 // System.out.println(line);
		    	 line = reader.readLine() ;
		    	 
		    	 kk++ ; 
		     }		     
		     reader.close(); 
		     input.close();
			 System.out.printf("kk= %d \n ", kk) ;

			 // System.out.println(lstdata);
		     adata = new double[lstdata.size()][Sam.nCiiu];
             kk= 0 ;  
			 for (Double[] ad : lstdata) {
				 for (int i=0; i < ad.length ; i++)
					 adata[kk][i] = ad[i] ; 
				 kk++ ; 
			 }
		
			 System.out.printf("kk= %d \n ", kk) ; 
			 testCiiu App = new testCiiu(2, sCiiu, adata ) ; 
			 for (int i=0; i < adata.length; i++) {
				 App.addTaxpayer(i);
				 				 
			 }
				
			 App.UpdateStats(); 	
		     //System.out.printf("numCols= %d  numRows= %d \n ",  adata[0].length, adata.length) ;
		     //System.out.printf("Entry= %d FreeDegree= %d Taxpayers = %d Spurious= %d \n ",  App.lstEntry.size(), App.freeDegree(), App.lstTaxpayers.size(),  Sam.spurious) ;  
			 
             // App.V.print(); 
			 // App.invCov.print(); 
			 
			 // System.out.println() ;
			 double[][] ddata = new double[adata.length] [ App.V.numCols() ] ;  
			 for (int i=0; i < adata.length ; i++ ) {
				 SimpleMatrix d = App.getScores(i) ;
				 for (int j=0; j < d.numCols() ; j++ ) {
					 ddata[i][j] = d.get(0,j)  ;  
					 // System.out.printf("%d) Scores = %7.5f  ", i, d.get(0,0) ) ;  					 
				 }
				 // System.out.printf(" \n " ) ;				 
			 }
								 
			 App.setOptimalCluster( ddata  ) ; 
			 
	}

}
