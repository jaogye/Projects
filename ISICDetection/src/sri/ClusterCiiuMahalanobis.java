package sri;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

public class ClusterCiiuMahalanobis implements ClusterCiiuType {

	
	/*
	 *  This class contains the list of taxpayers with the same ciiu
	 *  It also detects the outliers of taxpayers
	 */

	
	private List<Integer> lstEntry; 
    SimpleMatrix invCov;
    SimpleMatrix V    ;
    double[] singValues;
	int[] acluster ;
	int  optK= 0 ; 
	int[] cntCluster ; 
	private int freeDegree  ;
	
	
    List<Integer> lstTaxpayers ; 	
	double[] mean ;
	private double threshold ;
    double data[][] ;
    int nType;
    String ciiu; 
         
    
	public ClusterCiiuMahalanobis(int nType, String ciiu, double a[][] ) {
		this.nType = nType ;  // nType = 1 if it is sales otherwise is nType = 2 
		this.ciiu = ciiu; 
		this.lstEntry= new  ArrayList<Integer>() ;		
		this.lstTaxpayers = new ArrayList<Integer>();		
		mean = new double[Sam.nCiiu]; 
		data = a ; 

			 
	}

	
	public void addTaxpayer(int x) {
	     this.lstTaxpayers.add(x) ;
	
	}
	
	public List<Integer> getTaxpayers() {
		return lstTaxpayers ; 
	}
	
	public double getmean(int i) {
		return mean[i] ; 
	}
	
    public int freeDegree() {
    	return freeDegree ;  
    }

    public double threshold() {
    	return threshold ;  
    }

    
    public int numEntries() {
    	return lstEntry.size() ; 
    }

    public int numTaxpayers() {
    	return lstTaxpayers.size() ; 
    }

    
	// Mahalanobis Distance 
	public double getScore( int x) {
		SimpleMatrix d; 	    
		
		
		if (lstTaxpayers.size() <= Sam.spurious  )
				return 0; 

		if (lstTaxpayers.size() > Sam.spurious && V == null ) {
			this.UpdateStats();			
		}
		SimpleMatrix delta = this.getScores(x) ; 
			
		d = delta.mult(this.invCov.mult(delta.transpose())); 
			
		if (d.get(0,0) < 0)
			return 99999999; 
		else
		    return d.get(0,0) ; 
		
	}


	// Symmetric Kullback-Leibler divergence 
	public boolean nullIntersection( int x) {
	    boolean d = true ; 
		
		for (int i=0; i < Sam.nCiiu; i++ ) {
            if (mean[i] >0 && data[x][i] > 0) {
            	d = false ; 
            	break ; 
            }
		}		
		return d ; 
	}


	
	// Symmetric Kullback-Leibler divergence 
	private double SymKL2( int x) {
		double d = 0 ; 
		
		for (int i=0; i < Sam.nCiiu; i++ ) {
			double ei =  mean[i] ;  // Expected value
			double oi =  data[x][i] ; // Observation 
 
			if (ei > 0 && oi > 0)
				d = d + oi * Math.abs( Math.log(oi/ei))  ;						
		}
		
		return d ; 
	}

	
	
	private double SymKL( int x, double[] mean ) {
		double d = 0 ; 
		
		for (int i=0; i < Sam.nCiiu; i++ ) {
			double va =  mean[i] ; 
			double vb =  data[x][i] ;
			double w=0 ; 
			if (va > 0 && vb > 0) 
				w = Math.abs( Math.log(va/vb) ) ;
			else
				w = 2 ; 
			
			d = d + w * Math.abs( va - vb ) ; 			
		}
		
		return d ; 
	}

    
    
	/// This method computes the mean of profiles assigned to the cluster
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

	/// This method computes the mean of profiles assigned to the cluster
    private  double[] setMean() {
    	
    	mean = new double[Sam.nCiiu] ;     	
    	double totmean=0 ;   
    	for (Integer i:lstTaxpayers) 
  	  	  for (int j=0; j < Sam.nCiiu; j++ ) 
  	  		   mean[j] = mean[j] + data [i][j] ;
    	
    	for (int j=0; j < Sam.nCiiu; j++ ) 
    		totmean = totmean + mean[j] ;
    	
    	if (totmean > 0)    	
     	   for (int i=0; i < Sam.nCiiu; i++ ) 
    		   mean[i] = mean[i]/  totmean ;
    	
    	return mean ; 				   	
    }


    
	/// This method computes the mean filtered by non-outlier
    private  double[] getMean(boolean[] isOutlier) {
    	
    	double[] mean = new double[Sam.nCiiu] ;     	
    	double tot=0 ;   
    	    	
    	for (int i=0; i < lstTaxpayers.size() ; i++) {    		
    		int x = lstTaxpayers.get(i) ; 
    		if (!isOutlier[i])
  	  	       for (int j=0; j < Sam.nCiiu; j++ ) 
  	  		       mean[j] = mean[j] + data [x][j] ;
    	
    	}
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

	
	

    // ==========================================================================

    private void setOptimalCluster( double[][] ddata  ) {
    	
        KMeans o = new KMeans() ;
        int maxK = 6; 
        double SSEopt = 0, SSE = 0, SSE_prev=0, elbow = Double.MAX_VALUE;
        acluster = new int[ddata.length] ;
        int[][] aacluster = new int[maxK-1 ] [ ddata.length ];
        int times = 3 ; 
		 for (int i=0; i < maxK ; i++ ) {			 
			 SSE = Double.MAX_VALUE;   
			 for (int j=0; j < times; j++) {
				 SSEopt = o.kmeans(ddata, i) ;
				 if (SSEopt < SSE ) {
					 SSE = SSEopt ;
					 for (int k=0; k < ddata.length; k++)
						 aacluster [i-1] [k]= o.acluster[k] ; 
				 }				 
			 }

			 for (int j=0; j < ddata.length ; j++ )
				 aacluster[i-1][j] = o.acluster[j] ; 
			 if (i > 1 && SSE_prev - SSE < elbow && SSE_prev - SSE >= 0) {
				elbow =  SSE_prev - SSE ;
				optK = i-1 ; 
			 }
            // System.out.printf("K=%d SSE=%12.6f elbow=%12.6f \n ", i, SSE,  SSE_prev - SSE ) ;
			SSE_prev = SSE; 
		 }
		 
		 acluster = new int[ddata.length] ; 
		 for (int i=0; i < ddata.length ; i++ )
		     acluster[i] = aacluster[optK-1][i] ; 
			 
        // System.out.printf("Elbow = %12.6f  optK=%d  \n ", elbow, optK ) ;
        cntCluster = new int[ optK ] ; 
		for (int j=0; j < ddata.length ; j++ ) {
			 // System.out.printf("%d) score = %12.6f cluster=%d  \n ", j, ddata[j][0], acluster[j] ) ;
			 cntCluster[  acluster[j]  ]++ ; 
		 }
    }

	
	/// A profile is outlier if either sales or purchases profile is outlier
	/// This method computes the outliers  
	public List<Integer> getOutliers2() {	

 		List<Integer> lstOutliers = new ArrayList<>(); // List of outliers
		if (  this.freeDegree()==0 || this.lstTaxpayers.size() <= Sam.spurious )
			return lstOutliers; 
		
	    // System.out.printf("Ciiu=%s %d Entry= %d FreeDree= %d Taxpayers = %d ", this.ciiu, this.nType, lstEntry.size(), this.freeDegree(), this.lstTaxpayers.size()) ;  
		
		double[][] ddata = new double[lstTaxpayers.size()] [ this.V.numCols() ] ;
				
		for (int i=0; i < lstTaxpayers.size() ; i++ ) {
			 SimpleMatrix d = this.getScores( lstTaxpayers.get(i)   ) ;
			 for (int j=0; j < d.numCols() ; j++ ) {
				 ddata[i][j] = d.get(0,j)  ;    					 
			 }			 
		 }
						
		// Selection of the optimal k-means cluster
		 this.setOptimalCluster( ddata  ) ; 

		 // Selection of the largest cluster
		 int mainClusterid = -1;
		 int cntmax = 0; 
         for (int i=0; i < cntCluster.length; i++)
        	 if (cntCluster[i] > cntmax) {
        		 cntmax = cntCluster[i] ; mainClusterid= i ;  
        	 }
         
        // The initial outliers are those ones which are not in the main cluster and not special
 		for (int i=0; i < lstTaxpayers.size() ; i++ ) {
 			if (acluster[i] != mainClusterid) {
 				int x = lstTaxpayers.get(i) ; 
 				boolean special = Sam.TP[Sam.TP_index[ x ] ].special ;
 				if (!special)
 				    lstOutliers.add( x ) ;
 			}
 		}

		// System.out.printf("- Entry= %d FreeDree= %d Taxpayers %d  ",  this.lstEntry.size(), this.freeDegree(), this.lstTaxpayers.size()) ;
		// System.out.printf("Outliers %d \n ", lstOutliers.size() );  

 		
 		this.lstTaxpayers.removeAll(lstOutliers) ;
 		int nAddition = 1; 
 		
 		while (nAddition > 0 ) {
 	 		this.UpdateStats(); 
 	 		nAddition  = 0 ; 
 	 		// Adding of some initial outlier to lstTaxpayers 
 	 		if (this.lstEntry.size() > 0 &&  this.lstTaxpayers.size() > Sam.spurious) {
 	 			for (int i=0; i < lstOutliers.size(); i++ ) {	
 	 				 int x = lstOutliers.get(i) ; 
 	 				 double d = this.getScore(x) ; 
 	 				 // boolean special = false ;  				 
 	 				 if (d < chiSquare.getCriticalValue( freeDegree)    )   {
 	 					 nAddition++ ; 
 	 					 lstTaxpayers.add(x) ;
 	 					 lstOutliers.remove(i) ; 
 	 				 }    			 
 	 			} 			
 	 		} 			
 		}

		// System.out.printf("- Entry= %d FreeDree= %d Taxpayers %d  ",  this.lstEntry.size(), this.freeDegree(), this.lstTaxpayers.size()) ;
		// System.out.printf("Outliers %d \n ", lstOutliers.size() );  
 		 		
		return  lstOutliers; 
	}
    


	/// A profile is outlier if either sales or purchases profile is outlier
	/// This method computes the outliers using KL divergence   
	public List<Integer> getOutliers() {	


  		double[] mean = this.getMean() ;
		boolean[] isOutlier = new boolean[lstTaxpayers.size()]  ;
  		double[] scores = new double[lstTaxpayers.size()]  ;	
		boolean[] isOutlierprev = new boolean[lstTaxpayers.size()]  ;
  		
				
  		while (true) {

  	        double mscore = 0 , m2score = 0 ; 
            for (int i=0; i < scores.length ; i++) { 
      		    scores[i] = this.SymKL( lstTaxpayers.get(i), mean ) ;    				
      		    mscore = mscore + scores[i] ; 
      		    m2score = m2score + scores[i]*scores[i] ; 
      		}

            double meanscore = mscore / scores.length ; 
            double varscore = m2score/ scores.length -meanscore *  meanscore  ; 
            
     	    double devscore = Math.sqrt( varscore ) ;   		
     	    threshold = meanscore + Sam.zalpha * devscore ;
     	    
     	    
     	    for (int i=0; i < scores.length; i++) {

     	    	if (scores[i] > threshold) 
     	    		isOutlier[i] = true ; 
     	    }
     	   //if (lstTaxpayers.size() > 100) 
     		//   Printarray(scores) ; 
     	    
     	    mean = this.getMean( isOutlier ) ;
     	    boolean endloop = true ; 
     	    for (int i=0; i < scores.length ; i++) 
     	    	if (isOutlierprev[i] != isOutlier[i] ) {
     	    		endloop = true;
     	    		break ; 
     	    	}
     	    
     	    	     	    	

     	           
     	    if (endloop)
     	    	break; 
     	    
     	    for (int i=0; i < scores.length; i++) {
     	    	isOutlierprev[i] = isOutlier[i] ; 
     	    	isOutlier[i] = false ;
     	    }
     	    	
        	
        }

 		List<Integer> lstOutliers = new ArrayList<>(); // List of outliers
 	    for (int i = 0 ; i < lstTaxpayers.size() ; i++) 
 	       if (isOutlier[i])
 	    	  lstOutliers.add(lstTaxpayers.get(i)) ; 
 	    	
		return  lstOutliers; 
	}
    
	
	
	// This method computes the variance difference of symKL of given a profile of x (a taxpayer) belonging to this ClusterCiiuType  
	// when  the ciiu of taxpayer is moved from nold to nnew
	// value : the value of the connection between the taxpayer and x  
	public double getDeltaScore(int nold, int nnew, int x, double fraction) {
		   
		    if (lstTaxpayers.size() <= 1  )
		    	return 0 ; 
		
            double inimeanscore = 0 ; 
            double inivarscore = 0  ; 
            double iniscore = this.getScore(x) ;             

            // Computation of the score of x if its profile is equal to 'profile'
        	double finscore = this.getScore(x) ; 

        	// Reverse of the modification of the mean
	  	    this.mean[nold] = this.mean[nold]  + fraction/lstTaxpayers.size() ;
	  	    this.mean[nnew] = this.mean[nnew]  - fraction/lstTaxpayers.size() ;

            double finmeanscore = 0 ; 
            double finvarscore = 0 ;              
            return finvarscore - inivarscore ;
            
	}


	
	private void Printarray(double[] a) {		
		for (int i=0; i < a.length; i++)
			System.out.printf("%12.7f \n ", a[i]) ; 
	}
	
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


	
	
	public void UpdateStats() {
		
		this.setlstCoordinates() ;				
		if (this.lstEntry.size() > 0 && lstTaxpayers.size() > Sam.spurious) 
		    this.setPCA() ;
 			 
	}
	   
	
	private void setPCA() {
		
		SimpleMatrix Xdata = new SimpleMatrix( lstTaxpayers.size() , lstEntry.size() ) ;   
				
		
		// Computation of covariance matrix 
			
		this.setMean() ; 
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
	
	private SimpleMatrix getScores(int x ) {

		SimpleMatrix scores ; 
		SimpleMatrix delta = new SimpleMatrix( 1, this.lstEntry.size()) ;
		
		for (int  i=0; i < this.lstEntry.size() ; i++)
			delta.set(0,i, data[x][ lstEntry.get(i)  ]  - mean[ lstEntry.get(i) ]);

		if (delta.numCols() != this.V.numRows() ) {
			int hh = 0 ;
			hh++ ; 
		}
		
		scores = delta.mult(this.V) ; 		
		return scores;  
	}

	
	

    public static void main(String[] args)  {
    
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




	
}
