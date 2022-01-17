package sri;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;


public class KMeans {

    public int[] acluster  ;
    public ArrayList<Integer>[] lstclusters ;	
    public double[][] centers  ;
    public double SSE; 
    
    public KMeans() {
    
    }
    
	static int[] getPermutation(int K) {

        double[][] h = new double[K][2] ;   
		for (int i=0; i < K ; i++) {
			h[i][0] = Math.random() ; 
			h[i][1] = i ; 
		}
		
		for (int i=0; i < K; i++) {
			for (int j=0; j < K; j++) {
			    if (h[i][0] > h[j][0]) {
			    	double temp = h[i][0] ;  
			    	 h[i][0] = h[j][0] ; 
			    	 h[j][0] = temp ; 			    	 
			    	 temp = h[i][1] ;
			    	 h[i][0] = h[j][1] ; 
			    	 h[j][1] = temp ; 
			    }
		    }
		}	

		int[] permutation = new int[K];
		for (int i=0; i < K ; i++) 
			permutation[i] = (int) h[i][1] ; 
				
		return permutation ; 
	}
	
	static double distance(double[] a, double[] b) {
		double d = 0 ; 
		for (int i=0; i < a.length; i++) 
			d = d + ( a[i] - b[i] ) * ( a[i] - b[i] ) ; 
		
		d = Math.sqrt(d) ; 
		
		return d ; 
	}

	static double distance(int[] a, int[] b) {
		double d = 0 ; 
		for (int i=0; i < a.length; i++) 
			d = d + ( a[i] - b[i] ) * ( a[i] - b[i] ) ; 				
		return d ; 
	}

	
    public double kmeans(double[][]  data, int K){

    	acluster = new int[data.length] ;         
        centers = new double[K][data[0].length] ; 
        lstclusters = new ArrayList[K];
        int[] acluster_prev = new int[data.length] ;

        
    	// Inizialization of centers
        ArrayList<Integer> lst = new ArrayList<Integer>() ; 
        for (int i=0; i < data.length ; i++ ) 
        	lst.add(i) ; 
        
 	   
        int kk=0 ; 
        while (kk < K && lst.size() > 0  ) {
        	int k = (int) Math.floor( lst.size() * Math.random() ) ;
        	// Check if data[k] is taken as centers
        	boolean found = false ; 
        	for (int j=0; j < kk ; j++) { 
        		if (distance(centers[j], data[k]) == 0 )
        			found = true ; 
        	}
        	
            if (!found) {
         	   for (int j=0; j < data[0].length; j++)
        		   centers[kk][j] =  data[ lst.get(k) ][j] ;
               kk++ ; 
            }
        	lst.remove(k) ; 
        }
        
        for (int i=0; i < K ; i++ )
 	        lstclusters[i] = new ArrayList<Integer>() ;
        
        while (true) {
            for (int i=0; i < K; i++)   
            	lstclusters[i].clear(); 
            
            for (int i=0; i < acluster.length ; i++)
            	acluster_prev[i] = acluster[i] ; 
            
            // assign observations to centers  
        	for (int i=0; i < data.length; i++)  {
        		double dmin = Double.MAX_VALUE;
        		int imin = -1 ; 
        		for (int j=0; j < K; j++)  {
        			double d = distance( data[i], centers[j] ) ; 
        			if (d < dmin) {
        			   dmin = d ; 
        			   imin = j ; 	
        			}        			
        		}
        		acluster[i] = imin ; 
        		lstclusters[imin].add(i) ; 
        	}
            if (distance(acluster, acluster_prev) == 0) {
            	break; 
            }
        	        	
        	// Computation of new centers
        	for (int k=0; k < K; k++)  {
                for (int j=0; j < data[0].length; j++)  
                	centers[k][j] = 0 ; 
            
                for (Integer i: lstclusters[k] ) {
                	for (int j=0; j < data[0].length; j++)
                	     centers[k][j] = centers[k][j] + data[i][j] ;           		
        	     }
                for (int j=0; j < data[0].length; j++)  
                	centers[k][j] = centers[k][j] / lstclusters[k].size() ; 
        	
            }	
       }
       SSE = 0; 
     	// Computation of new centers
     	for (int k=0; k < K; k++)        
           for (Integer i: lstclusters[k] ) 
        	   SSE = SSE + distance(centers[k], data[i]) ;     

        return SSE ; 
       }

    
    
}