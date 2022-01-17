package sri;

public  class chiSquare {

	static double[][] cValues ;
	static int i=0; 
	
	static {
		  cValues = new double[21][9] ;
		  addValues(1, 0.00016, 0.00098, 0.00393, 0.01579, 2.706, 3.841, 5.024, 6.635) ;
		  addValues(2, 0.0201, 0.0506, 0.1026, 0.2107, 4.605, 5.991, 7.378, 9.21) ;
		  addValues(3, 0.115, 0.216, 0.352, 0.584, 6.251, 7.815, 9.348, 11.345) ;
		  addValues(4, 0.297, 0.484, 0.711, 1.064, 7.779, 9.488, 11.143, 13.277) ;
		  addValues(5, 0.554, 0.831, 1.145, 1.61, 9.236, 11.07, 12.832, 15.086) ;
		  addValues(6, 0.872, 1.237, 1.635, 2.204, 10.645, 12.592, 14.449, 16.812) ;
		  addValues(7, 1.239, 1.69, 2.167, 2.833, 12.017, 14.067, 16.013, 18.475) ;
		  addValues(8, 1.647, 2.18, 2.733, 3.49, 13.362, 15.507, 17.535, 20.09) ;											  
		  addValues(9, 2.088, 2.7, 3.325, 4.168, 14.684, 16.919, 19.023, 21.666) ;
		  addValues(10, 2.558, 3.247, 3.94, 4.865, 15.987, 18.307, 20.483, 23.209) ;
		  addValues(11, 3.053, 3.816, 4.575, 5.578, 17.275, 19.675, 21.92, 24.725) ;
		  addValues(12, 3.571, 4.404, 5.226, 6.304, 18.549, 21.026, 23.337, 26.217) ;
		  addValues(13, 4.107, 5.009, 5.892, 7.041, 19.812, 22.362, 24.736, 27.688) ;
		  addValues(14, 4.66, 5.629, 6.571, 7.79, 21.064, 23.685, 26.119, 29.141) ;
		  addValues(15, 5.229, 6.262, 7.261, 8.547, 22.307, 24.996, 27.488, 30.578) ;
		  addValues(20, 8.26, 9.591, 10.851, 12.443, 28.412, 31.41, 34.17, 37.566) ;
		  addValues(25, 11.524, 13.12, 14.611, 16.473, 34.382, 37.652, 40.646, 44.314) ;
		  addValues(30, 14.953, 16.791, 18.493, 20.599, 40.256, 43.773, 46.979, 50.892) ;
		  addValues(40, 22.164, 24.433, 26.509, 29.051, 51.805, 55.758, 59.342, 63.691) ;
		  addValues(50, 29.707, 32.357, 34.764, 37.689, 63.167, 67.505, 71.42, 76.154) ;
		  addValues(100, 70.065, 74.222, 77.929, 82.358, 118.498, 124.342, 129.561, 135.807) ;

	}
	
	public chiSquare() {
	}


	
	
	static private void addValues(int n,  double v1, double v2, double v3, double v4, double v5, double v6, double v7, double v8  )  {

		cValues[i][0] =  n ; 
		cValues[i][1] = v1 ;
		cValues[i][2] = v2 ;
		cValues[i][3] = v3 ;
		cValues[i][4] = v4 ;
		cValues[i][5] = v5 ;
		cValues[i][6] = v6 ;
		cValues[i][7] = v7 ;
		cValues[i][8] = v8 ;
	    i++ ; 
	}
	

	
	public static int binarySearch(int l, int r, int x) 
	{ 
	    while (l <= r) { 
	        int m = l + (r - l) / 2; 
	  
	        // Check if x is present at mid 
	        if (cValues[m][0] == x) 
	            return m; 
	  
	        // If x greater, ignore left half 
	        if (cValues[m][0] < x  ) 
	            l = m + 1; 
	  
	        // If x is smaller, ignore right half 
	        else
	            r = m - 1; 
	    } 
	  
	    // if we reach here, then element was 
	    // not present 
	    return l; 
	} 

	
	public static double getCriticalValue(int x) {
		double d= 0; 
		final int level=2;
		if (x == 0)
		   return 999999; 
		int h = binarySearch(0, cValues.length, x)  ;

		if (cValues[h][0] == x)
			d =  cValues[h][level] ;
		else {
			double dx = cValues[h][0] - cValues[h-1][0] ;
			double dy = cValues[h][level] - cValues[h-1][level] ;
			d = cValues[h-1][level] + (x- cValues[h-1][0] ) * dy/dx ; 
		}		    		
		return d; 
	}
	
	
	public static void main(String[] args)  {
		
	    // chiSquare o = new chiSquare() ; 
		/*
		 		for (int k=1; k < 131; k++) {
			
			// int h = binarySearch(0, cValues.length, k)  ; 
            System.out.printf( "%d  %10.6f \n", k, getCriticalValue(k)  );            
		}
		 */
		
		
		
	}
	
}
