package sri;

import java.util.ArrayList;
import java.util.List;

import org.ejml.simple.SimpleMatrix;

public class  ClusterCiiuUniVar implements ClusterCiiuType  {

	private List<Integer> lstEntry; 
		
    List<Integer> lstTaxpayers ; 	
	double[] m1 ; // First moment 
	double[] m2 ; // Second moment 
	private double threshold ;
    double data[][] ;
    int nType;
    String ciiu; 
         
	public ClusterCiiuUniVar(int nType, String ciiu, double a[][], double s[] ) {
		this.nType = nType ;  // nType = 1 if it is sales otherwise is nType = 2 
		this.ciiu = ciiu; 
		this.lstEntry= new  ArrayList<Integer>() ;		
		this.lstTaxpayers = new ArrayList<Integer>();		
		m1 = new double[Sam.nCiiu];
		m2 = new double[Sam.nCiiu];
		data = a ; 
 			 
	}

	
	
	@Override
	public void addTaxpayer(int x) {
		// TODO Auto-generated method stub
		this.lstTaxpayers.add(x) ;
	}


	@Override
	public double threshold() {
		// TODO Auto-generated method stub
		return threshold;
	}

	@Override
	public int numEntries() {
		// TODO Auto-generated method stub
	   	return lstEntry.size() ;
	}

	@Override
	public int numTaxpayers() {
		// TODO Auto-generated method stub
    	return lstTaxpayers.size() ; 
	}

	@Override
	public List<Integer> getTaxpayers() {
		// TODO Auto-generated method stub
		return lstTaxpayers ;
	}

	@Override
	public boolean nullIntersection(int x) {
		// TODO Auto-generated method stub
	    boolean d = true ; 
		
		for (int i=0; i < Sam.nCiiu; i++ ) {
            if (m1[i] >0 && data[x][i] > 0) {
            	d = false ; 
            	break ; 
            }
		}		
		return d ; 
	}

	@Override
	public List<Integer> getOutliers() {
		// TODO Auto-generated method stub

		this.UpdateStats();
		boolean[] isOutlier = new boolean[lstTaxpayers.size()]  ;
  		double[] scores = new double[lstTaxpayers.size()]  ;	
		boolean[] isOutlierprev = new boolean[lstTaxpayers.size()]  ;
  						
  		while (true) {

  	        double mscore = 0 , m2score = 0 ; 
            for (int i=0; i < scores.length ; i++) { 
      		    scores[i] = this.getScore( i ) ;    				
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
		
		
		
		ArrayList<Integer> lstOutliers = new ArrayList<>() ; 
		
		
		
		
		
		return lstOutliers;
	}

	@Override
	public void UpdateStats() {
		// TODO Auto-generated method stub

    	double[] m1 = new double[Sam.nCiiu] ; // First moments 
    	double[] m2 = new double[Sam.nCiiu] ; // Second moments    
    	for (Integer i:lstTaxpayers) 
  	  	  for (int j=0; j < Sam.nCiiu; j++ ) {
  	  		   m1[j] = m1[j] + data[i][j] ;
 	           m2[j] = m2[j] + data[i][j] * data[i][j];
  	  	  }
    	
    	

	}

	
	
	@Override
	public double getmean(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDeltaScore(int nold, int nnew, int x, double fraction) {
		// TODO Auto-generated method stub
		return 0;
	}


	// The score is the reduction of variance when the taxpayer x is deleted from the cluster 
	@Override
	public double getScore(int x) {
		// TODO Auto-generated method stub
        double var ; 
		double var2 ; 
		double score = 0 ; 
		int n = lstTaxpayers.size() ; 
		if (n < Sam.spurious)
			return 0 ; 
		
		for (int i=0; i < m1.length; i++) {
			var = m2[i] / n - (m1[i]/n) * (m1[i]/n) ; 
			double mm1 = (m1[i] - data[x][i])/(n-1) ; 
			var2 = (m2[i] - data[x][i] * data[x][i]) / (n-1) - mm1 * mm1 ;		
			score = score + (var2 - var) ; 
		}
		
		
		return score;
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


}
