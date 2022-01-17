package sri;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

public class ClusterCiiuKL implements ClusterCiiuType {
	
	/*
	 *  This class provides methods to detect outliers of taxpayers
	 *  and assign outliers to other ciiu using symmetric KL distance adapting to difference scales 
	 */

	
	private List<Integer> lstEntry; 
	private int freeDegree  ;
	
	
    List<Integer> lstTaxpayers ; 	
	double[] mean ;
	double totmean ; 
	private double threshold ;
	private double mscore ;
	private double m2score ;
    double data[][] ;
    double scales[] ;
    int nType; // Sales:nType=1 , Purchases:nType=2
    String ciiu; 
         
    
	public ClusterCiiuKL(int nType, String ciiu, double a[][], double s[] ) {
		this.nType = nType ;  // nType = 1 if it is sales otherwise is nType = 2 
		this.ciiu = ciiu; 
		this.lstEntry= new  ArrayList<Integer>() ;		
		this.lstTaxpayers = new ArrayList<Integer>();		
		mean = new double[Sam.nCiiu]; 
		data = a ; 
		scales = s ; 
			 
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

    
	// Symmetric Kullback-Leibler divergence
	public double getScore( int x) {
		double d = 0 ; 
		
		for (int i=0; i < Sam.nCiiu; i++ ) {
			double ei =  mean[i] ;  // Expected value
			double oi =  data[x][i] ; // Observation 
 
			double w = 0 ; 
			if (ei > 0 && oi > 0)
				w=  Math.abs( Math.log(oi/ei))  ;
			else 
				w= scales[i]; 
			
			d = d + w * Math.abs(oi - ei) ; 
			
		}
		
		return d ; 
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
    	totmean=0 ;   
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

    

    // ==========================================================================



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
    

	private void setScoreMoments() {
	}
	
	
	
	// This method computes the variance difference of symKL of given a profile of x (a taxpayer) belonging to this ClusterCiiuType  
	// when  the ciiu of taxpayer is moved from nold to nnew
	// value : the value of the connection between the taxpayer and x  
	public double getDeltaScore(int nold, int nnew, int x, double fraction) {
		   
		    if (lstTaxpayers.size() <= 1  )
		    	return 0 ; 
		
            double inimeanscore = mscore / lstTaxpayers.size() ; 
            double inivarscore = m2score/ lstTaxpayers.size() - inimeanscore *  inimeanscore  ; 
            double iniscore = this.getScore(x) ;             
 	  	      
	  	    this.mean[nold] = this.mean[nold]  - fraction/lstTaxpayers.size() ;
	  	    this.mean[nnew] = this.mean[nnew]  + fraction/lstTaxpayers.size() ;
	  	    
      		// Computation of the score of x if its profile is equal to 'profile'
        	double finscore = this.getScore(x) ; 

        	// Reverse of the modification of the mean
	  	    this.mean[nold] = this.mean[nold]  + fraction/lstTaxpayers.size() ;
	  	    this.mean[nnew] = this.mean[nnew]  - fraction/lstTaxpayers.size() ;

            double finmeanscore = (mscore + finscore - iniscore) / lstTaxpayers.size() ; 
            double finvarscore = (m2score + finscore * finscore - iniscore*iniscore )/ lstTaxpayers.size() - finmeanscore *  finmeanscore  ; 
            
            return finvarscore - inivarscore ; 
	}

	
    // This method compute the variation of variance  of scores if x leaves the ClusteCiiu
	public double getDeltaVarianceScore(int x) { 
		
        double inimeanscore = mscore / lstTaxpayers.size() ; 
        double inivarscore = m2score/ lstTaxpayers.size() - inimeanscore *  inimeanscore  ;          
  		      	
        double m=0, m2= 0 ; 
  		// Computation of mean considering 'profile' of x        
		 for (Integer x1:lstTaxpayers) {
	    		if (x!=x1) {
	   			   double dista = this.getScore( x1 ) ; 
				   m = m + dista;
			       m2 = m2 + dista * dista ; 			     	    			
	    		}
		}								
    	        	
        double finmeanscore = m / (lstTaxpayers.size()-1) ; 
        double finvarscore = m2/ (lstTaxpayers.size()-1) - finmeanscore *  finmeanscore  ; 
        
        return finvarscore - inivarscore ; 

			 
	}
	
	
	
	
	public void UpdateStats() {		
		this.setMean() ;
		mscore = 0 ; m2score = 0 ; 
		for (Integer x:lstTaxpayers) {
			 double dista = this.getScore( x ) ; 
			 mscore = mscore + dista;
		     m2score = m2score + dista * dista ; 			     
		}
        double meanscore = mscore / lstTaxpayers.size() ; 
        double varscore = m2score/ lstTaxpayers.size() - meanscore *  meanscore  ;
        threshold = meanscore + Sam.zalpha * Math.sqrt(varscore) ;  
 
	}
	   
	
	

    public static void main(String[] args)  {
    
    }
	   




}
