package sri;

import org.apache.commons.math3.util.Pair;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;
import org.apache.commons.math3.util.Pair;


public class Mahala {

	public static void test(SimpleMatrix a ) {

		 	        		
		SimpleSVD<SimpleMatrix> svd = a.svd();
    	SimpleMatrix V =  (SimpleMatrix) svd.getV() ;
    	SimpleMatrix U =  (SimpleMatrix) svd.getU() ;
    	SimpleMatrix W =  (SimpleMatrix) svd.getW() ;
    	
    	System.out.println("Matrix");
    	a.print();
    	
    	System.out.println("U");
    	U.print();

    	System.out.println("V");
    	V.print();
    	
    	//System.out.printf("U=%12.6f V=%12.6f V-U=%12.6f \n ", U.get(1,1),V.get(1,1) ,V.get(1,1) - U.get(1,1) ); 
    	//System.out.println("Diffference");
    	// V.minus(U).print() ; 
    	
    	System.out.println("W");
    	W.print();

    	System.out.println("Reconstruction");
    	
    	SimpleMatrix O = V.mult(W).mult(V.transpose()); 
    	O.print() ;
    	

		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int nRows = 10, nCols = 50 ; 
		SimpleMatrix Sales = new SimpleMatrix(nRows,nCols) ;
		SimpleMatrix SalesCov = new SimpleMatrix(nCols,nCols) ;
        double[] mean = new double [ Sales.numCols() ];
		System.out.println(Sales.numRows());
		System.out.println(Sales.numCols());
                
		for (int i=0; i < Sales.numRows() ; i++)
			for (int j=0; j < Sales.numCols() ; j++) {
                 Sales.set(i,j, Math.random()) ;
                 mean[j] = mean[j] + Sales.get(i,j) ;                                 
			}
		
		// Computation of the covariance matrix of Sales 
		for (int i=0; i < SalesCov.numCols() ; i++)
			mean[i] = mean[i] / Sales.numRows() ; 
 
		// Centering matrix 
		for (int i=0; i < Sales.numRows() ; i++)
			for (int j=0; j < Sales.numCols() ; j++) {
                 Sales.set(i,j, Sales.get(i,j) - mean[j] ) ;
                                 
			}
						
        // Computation of SalesCov
		SalesCov = Sales.transpose().mult(Sales) ;
		double alpha = Sales.numRows()-1 ;
		for (int i=0; i < Sales.numRows() ; i++)
			for (int j=0; j < Sales.numCols() ; j++) 
                 Sales.set(i,j, Sales.get(i,j) / alpha  ) ;
		
        System.out.println("mean");
		for (int i=0; i < Sales.numCols() ; i++)
              System.out.printf("%9.4f ", mean[i]);
		System.out.printf("\n");

		SimpleSVD<SimpleMatrix> svd = SalesCov.svd();
    	SimpleMatrix V =  (SimpleMatrix) svd.getV() ;
    	SimpleMatrix U =  (SimpleMatrix) svd.getU() ;
    	SimpleMatrix W =  (SimpleMatrix) svd.getW() ;
        double[] singValues = svd.getSingularValues() ; 
    	
    	System.out.println("U");
    	U.print();

    	System.out.println("V");
    	V.print();

    	System.out.println("Singular Values ");
        for (int i=0; i < singValues.length; i++)        
        	System.out.printf("%10.5f ", singValues[i]); 
        	
    	System.out.printf("\n");

        
    	System.out.println("Sales");
    	Sales.print();  
    	
    	    	
        // Column selection 	
        double conditionNumber = 100; 
    	double wmax = singValues[0]   ;
    	int nSelCol = 0 ;  // W.set(5,5,0);
    	for (nSelCol=0; nSelCol < Sales.numCols() && wmax < conditionNumber * singValues[nSelCol] ; nSelCol++ ) {    		

    	}

    	SimpleMatrix Scores = new SimpleMatrix( Sales.numRows(), nSelCol) ;
        System.out.printf("Selected Col %d \n", nSelCol) ; 
    	// Computation of scores
        for (int i=0; i < U.numRows(); i++) 
        	for(int j=0; j < nSelCol; j++) {       
        		Scores.set(i,j, U.get(i,j)  ) ; 
        	}
    	
    	System.out.println("Scores");
    	Scores.print();  
    	System.out.println("New Covariance 1");
    	Scores.transpose().mult(Scores).print();  
    	         
    	// Second computation of scores
        for (int i=0; i < Sales.numRows(); i++) 
        	for(int j=0; j < nSelCol; j++) {
        		double d = 0 ; 
        		for(int k=0; k < nSelCol; k++) {
        	        d = d + Sales.get(i,k) * V.get(k,j) ;  		
        		}
        		Scores.set(i,j,d);
        	}
    	
    	System.out.println("Second Scores");
    	Scores.print();  


	
	}

}
