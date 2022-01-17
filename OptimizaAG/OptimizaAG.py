# -*- coding: utf-8 -*-
"""
Created on Sun Jan  3 14:19:26 2021

@author: Juan Alvarado
"""

"""
This program computes a maximum entropy  of  amalgamated stepfunctions constrained by 
 subgraph densities 

 The vector of m stepfunctions have two representations:
 1] A list of Matrices-Vector  [M_1, ...,M_r,Q] representation  
    where the entry r is the number of relations and M_i a symmetric matrix mxm
    and Q is the  partition vector

    
# 2] One dimension vector representation: It is denoted by X and it is a vector 
formed  by M_i converted to one-dimension representation using P_i = mat2vec [W_]
and concated all into [P_1,...,P_r, Q[m-1]] where Q[m-1] are m-1 first entries of Q

and Q minus the last entry of Q since all entries of Q are positive and sum 1

of P is  the vectorization of an upper triangle matrices W and Q is the vector weights of the partition
# To convert the vectorization of the upper triangle matrix into a symmetric matrix W, use  W = vec2mat [P] and to convert a symmetric matrix into the vectorization use 
# P = mat2vec [W]

# To go from one representation to other we use [M] = getMatPar [X]  

#  n = posn(u,v] and [u][v] = posuv[n] are auxiliary functions to map from upper matrix entries to vector entries

# There are 3 amalgamated graphs all of them have 2 edges :

# The edges in amalgamated graphs are represented as follow [p:i,j] where p is layer and ij is the edge of layer p
# triangle3path whose representation is [1:i,j], [2:j,k], [1:k,i], [2:i,j], [2:j,k], [1:k,l]
# doubleedge whose representation is [1:i,j] [2:i,j]
# 2star1edge whose representation is [1:i,j] [1:i,k] [2:i,j]

# The simple graphs as: triangle, 4cycle, 2star, 3star, 4clique, edge can be considered amalgamated graphs whose complementay layer is the graph with no edges
# Thus these graphs are associated with the corresponding layer id
 
 # Basic functions 
 # ---------------------------------------------------------------------------
 
 # To transform the position on the matrix into the position in the vetor
"""

import numpy as np
from scipy.optimize import minimize
from scipy.optimize import Bounds
from scipy.special import logit

from math import log, floor, sqrt
from random import random

from numpy.linalg import eig
import datetime

r=1;  # Number of amalgmated stepfunctions
m=2;  # Numbrr of stepss

def posn(u,v):
  if u > v:
     temp = u 
     u = v
     v = temp
  n= floor(v*(v+1)/2 + u )
  return n 
 

 # To transform the position on the vector into the position in the matrix
def posuv(n):
   
    if (n==2):
        u=1; v=1         
    else:
       v= floor((sqrt(1+8*(n))-1)/2) 
       if (v==0):
           u=0
       else:    
          u=( n % floor(v*(v+1)/2) )
    return u,v

 

 # Transformation from a vector 1x[n[n-1]/2] to a symmetric matrix nxn
def vec2mat(V):
     
     M = []
     for layer in range(r):         
         M.append( np.zeros( (m,m) )  )
         offset = floor( layer*m*(m+1)/2 )
         for i in range(m):
           for j in range(i,m):
               M[layer][i][j] = V[ offset+ posn(i,j) ]
               M[layer][j][i] = M[layer][i][j]
     

     offset = offset+ posn(i,j) + 1

     M.append( np.zeros( (m,1) )  )
     tot = 0
     for i in range(m-1):         
         M[r][i] =  V[ offset+ i ]
         tot = tot + M[r][i]
     M[r][m-1] = 1 - tot    
     return M
 
 
 
 # transform a symmetric matrix nxn into a vector 1x[m*[m-1]/2]
def  mat2vec(M):
     if (len(M)==(r+1)): 
        m2 = floor(r*( m*(m+1)/2 ) + m-1)         
     else:
        m2 = floor(r*( m*(m+1)/2 ) )
        
     V = np.zeros(( m2,1))
     k=0
     for layer in range(r):
      for i in range(m):
       for j in range(i,m):         
         V[k] = M[layer][i][j]
         k = k + 1
     
     if (len(M)==(r+1)):     
       for i in range(m-1):
          V[k] = M[r][i]
          k = k + 1
          
     return V
 
 
 # Transform  the one-dimension representation to multi-matrix 
def  getMatPar2(X):
 N = len(X); 
    
 Q = X[r*m*(m+1)/2+1:N]; 
 Q = [Q,  1-sum(Q)]; 

 for layer in range( r):  
     i1 = (layer-1)*m*(m+1)/2+1
     i2 = layer*m*(m+1)/2
     M[layer] = X[i1:i2];

 return M
    
    
 
 
 # Kronecker's delta
def delta(i,j):
   d=0
   if (i==j):
      d=1;
   return d 
 

# Graph Parameter interfase
class graphp:

  name = "" 
  def __init__( self):    
      self.name = "" 
       
  def density(self, M):
      print('Invalid method')
      return -1

  def gradient(self, M):
     print('Invalid method')
     return -1

  def hq(self, M):
     print('Invalid method') 
     return -1 

  def hp(self, M):
     print('Invalid method') 
     return -1 

  def hm(self, M):
     print('Invalid method') 
     return -1 
      
  def hessian(self, M):
       A = self.hq(M)
       B = self.hp(M)
       C = self.hm(M)  
       H1 = np.concatenate((A, C), axis=1)
       H2 = np.concatenate((C.T,B), axis=1)
       H = np.concatenate((H1, H2), axis=0)
       H0 = covhessian(m, H) 
       return H0
      
  def density2(self, X):
      M = vec2mat(X)
      return self.density(self, M)

  def gradient2(self, X):
      M =  vec2mat(X)
      return self.gradient(self, M)
  
  def hessian2(self, X):
      M =  vec2mat(X)
      return self.hessian(self, M)





class onelayeredge(graphp): 

  layer = 0  
  def __init__( self, layer):    
      self.name = "edge" 
      self.layer = layer

  #  Compute edge density       
  def density(self, M):
   A = M[self.layer]  

   d = 0; 
   for i in range(m):
      for j in range(i,m):
          d = d + (2-delta(i,j))*A[i][j] * Q[i] * Q[j]; 
   return d 

  def gradient(self, M):
    A =M[self.layer] 

    G = [ np.zeros((m,m)) for i in range(r) ] 
    for i in range(m):
       for j in range(m):
          G[self.layer][i][j] = Q[i]*Q[j]*(2-delta(i,j)); 
          G[self.layer][j][i] = G[self.layer][i][j];        


    G2 = mat2vec(G); 
    
    G3 = np.zeros((m,1)) 
    for i in range(m):
       G3[i] = 2*degree(A,Q,i)
     
    #G2 = [G2;G3]; 
    G1 = np.concatenate((G2, G3), axis=0)
    G0 = covgradient(m, G1) 
    
    return G0

  # Edge subgrap density considering Q as a constant parameter    
  def gradientQ(self, M):

    G = [ np.zeros((m, m)) for i in range(r) ] 
    for i in range(m):
     for j in range(i,m):
         G[self.layer][i][j] = (2-delta(i,j))*Q[i]*Q[j]; 
         G[self.layer][j][i] = G[self.layer][i][j];        
    G2 = mat2vec(G); 
    return G2
 

 # It computes the Hessian matrix of edge subgraph density 
  def hq(self, M):

    n = floor(r*m*(m+1)/2 )
    H = np.zeros((n,n))
    return H


 #  It computes the hessian of edges
  def hp(self, M):
    A = M[self.layer]  
    H = np.zeros((m, m));  
    for i in range(m):
     for j in range(i,m):
         H[i][j] = 2*A[i][j];
         H[j][i] = H[i][j] ; 
    return H
 

 # It Computes the Hessian matrix of edge density  for the partition 
  def hm(self, M):

    m2 = floor( r*( m*(m+1)/2 ) )
    H = np.zeros((m2,m))
    for i in range(m):
     for j in range(i,m):
       ij = posn(i,j);
       for k in range(m):
           H[ij][k] = (2-delta(i,j))*(Q[i]*delta(j,k)+Q[j]*delta(i,k) ); 

    return H








class onelayertriangle(graphp):
   
  layer = 0  
  def __init__( self, layer):    
      self.name = "triangle" 
      self.layer = layer

  def density(self, M):
     A = M[ self.layer] 
     m=len(A);
     d = 0; 
     for x1  in range(m):
      for x2  in range(m):
        for x3  in range(m):
            d = d + A[x1][x2] * A[x2][x3] * A[x3][x1] * Q[x1] * Q[x2] * Q[x3];    
     return d 


  def gradient(self, M):
      A = M[self.layer] 
      m =len(A);
 
      G = [ np.zeros((m,m)) for z in range(r) ]   

      for i in range(m):
        for j in range(m):
          G[self.layer][i][j] = 3*Q[i]*Q[j]*(2-delta(i,j)) * qdegree(A,Q,i,j)
          G[self.layer][j][i] = G[self.layer][i][j];

      G2 = mat2vec(G); 
 
      G3 = np.zeros((m,1)) 
      for i in range(m):
          G3[i] = 3*ttrianglec1(A,Q,i)

      # G2 = [G2;G3]; 
      G1 = np.concatenate( (G2, G3), axis=0)
      G0 = covgradient(m, G1) 
    
      return G0


 # Triangles subgraph density considering Q as a constant parameter
  def gradientQ(self, M):

    A = M[self.layer] 
    G = np.zeros((m, m)); 
    for i in range(m):
        for j in range(i,m):
          G[i][j] = 3*(2-delta(i,j)) * t2starc2(A,Q,i,j)*Q[i]*Q[j] ;
          G[j][i] = G[i][j];
    
    G2 = mat2vec(G); 
    return G2
 
 

 #  It computes the Hessian of triangle subgraph density        
  def hq(self, M):
      A =M[self.layer]  
      n = floor(r*m*(m+1)/2 )
      H =  np.zeros((n, n)); 
      for i in range(m):
       for j in range(i,m):
        ij = posn(i,j) 
        for k in range(m):
         for l in range(k,m):
            kl = posn(k,l) 
            H[ij][kl] = Q[l]*A[j][l]*delta(i,k)+(1-delta(k,l))*Q[k]*A[j][k]*delta(i,l) + Q[l]*A[i][l]*delta(j,k)+(1-delta(k,l))*Q[k]*A[i][k]*delta(j,l)  ;
            H[ij][kl] = 3*(2-delta(i,j))*Q[i]*Q[j]*H[ij][kl]; 
            H[kl][ij] = H[ij][kl];
      return H

# It computes the Hessian of Partititon of triangle subgraph density   
  def hp(self, M):
   A =M[self.layer]  
   m=len(A); 
   H = np.zeros((m, m));  
   for i in range(m):
    for j in range(m):
       H[i][j]= 6*A[i][j]*qdegree(A,Q,i,j) ;   
       H[j][i]= H[i][j];
   return H

 # It Computes the Hessian matrix of triangle density  for the partition  
  def hm(self, M):

   A = M[self.layer]  
   m2 = floor(r*m*(m+1)/2 )

   H = np.zeros((m2,m))
   for i in range(m):
    for j in range(i,m):
       ij = posn(i,j);
       for k in range(m):
           H[ij][k] = 3*(2-delta(i,j))*((delta(i,k)*Q[j]+delta(j,k)*Q[i])*qdegree(A,Q,i,j) + Q[i]*Q[j]*A[i][k]*A[j][k]); 

   return H






class onelayerpath3(graphp):

  layer = 0  
  def __init__( self,  layer):    
      self.name = "path4" 
      self.layer = layer

  # 3-paths    
  def density(self, M):

    A = M[self.layer]  
    m=len(A);
    d = 0; 
    for x1 in range(m):
     for x2 in range(m):
      for x3 in range(m):
       for x4 in range(m):
        d = d + A[x1][x2] * A[x2][x3] * A[x3][x4] * Q[x1] * Q[x2] * Q[x3] * Q[x4];
    return d 


  def gradient(self, M):

    A = M[self.layer]  
 
    G = [ np.zeros((m,m)) for i in range(r) ]

    for i in range(m):
      for j in range(m):
        G[self.layer][i][j] = (2-delta(i,j))*(degree2paths(A,Q,i)+degree(A,Q,i)*degree(A,Q,j)+degree2paths(A,Q,j) );
        G[self.layer][i][j] = Q[i]*Q[j]*G[self.layer][i][j]; 
        G[self.layer][j][i] = G[self.layer][i][j];        
 
    # G2 = [G2;mat2vec(G)]; 
    G2 = mat2vec(G); 

    G3 = np.zeros((m,1)) 
    for i in range(m):
      G3[i] = 2*(degree3paths(A,Q,i)+t3pathc1(A,Q,i) ) ;
 
    # G2 = [G2;G3]; 
    G1 = np.concatenate( (G2, G3), axis=0)
 
    return G1 

  # Gradient of 3-paths subgraph density 
  def gradientQ(self, M):
     
   A = M[self.layer] 
   m=len(A);

   G = [ np.zeros((m, m)) for i in range(r)]
   for i in range(m):
       for j in range(i,m):
         G[self.layer][i][j] = (2-delta(i,j))*Q[i]*Q[j]*(degree2paths(A,Q,i)
         +degree(A,Q,i)*degree(A,Q,j)
        +degree2paths(A,Q,j))
         G[self.layer][j][i] = G[self.layer][i][j]

   G2 = mat2vec(G); 
 
   return G2
 

 #  It computes the Hessian of 3-path subgraph density   
  def hq(self, M):

    A = M[self.layer]  

    n = floor(r*m*(m+1)/2 )
    H = np.zeros((n,n))
    for i in range(m):
      for j in range(m):
        ij = posn(i,j) 
        for k in range(m):
         for l in range(m):
            kl = posn(k,l)
            H[ij][kl] = Q[k]*Q[l]*(A[i][k]+A[j][k]+(1-delta(k,l))*(A[i][l]+A[j][l])  );
            H[ij][kl] = H[ij][kl]+ Q[l]*degree(A,Q,l)*(delta(i,k)+delta(j,k)) +( 1-delta(k,l) )*Q[k]*degree(A,Q,k) * (delta(i,l)+delta(j,l));
            H[ij][kl] = H[ij][kl]+ degree(A,Q,j)*(delta(i,k)*Q[l] + (1-delta(k,l))*delta(i,l)*Q[k]) +degree(A,Q,i)*(delta(j,k)*Q[l]+ (1-delta(k,l))*delta(j,l)*Q[k]); 
            H[ij][kl] = (2-delta(i,j))*Q[i]*Q[j]*H[ij][kl];
            H[kl][ij] = H[ij][kl];
    return H


 # It computes the hessian of 3-paths subgraph density  
  def hp(self, M):
   A = M[self.layer]  
   m = len(A); 
   H = np.zeros((m, m));  
   for i in range(m):
     for j in range(m):
       H[i][j]=2*(A[i][j]*(degree2paths(A,Q,i) + degree2paths(A,Q,j) + degree(A,Q,i)*degree(A,Q,j)) +t3pathc2(A,Q,i,j)+t3pathc(A,Q,i,j)+t3pathc2(A,Q,j,i) );
       H[j][i]= H[i][j];
   return H
     

# It Computes the Hessian matrix of 3-path density  for the partition  
  def hm(self, M):

   A = M[self.layer]  
   m2 = floor(r*m*(m+1)/2 )

   H = np.zeros((m2,m))
   for i in range(m):
    for j in range(i,m):
       ij = posn(i,j);
       for k in range(m):
           H[ij][k] = (2-delta(i,j))*( (Q[i]*delta(j,k)+Q[j]*delta(i,k))*
                                   (degree2paths(A,Q,i)+degree(A,Q,i)*degree(A,Q,j)+degree2paths(A,Q,j)) 
          + Q[i]*Q[j]*( (A[i][k]+A[j][k])*degree(A,Q,k) + qdegree(A,Q,i,k) + qdegree(A,Q,j,k) +A[j][k]*degree(A,Q,i) + A[i][k]*degree(A,Q,j)    )); 
 
   return H
 



    

    
class onelayerstark(graphp):

  layer = 0  
  n = 0
  def __init__( self,  layer,k):  
      self.name = "star" + str(k) 
      self.layer = layer
      self.n = k

  # k-star       
  def density(self, M):
      
     A = M[self.layer]  
     m=len(A);
     d = 0; 
     for i in range(m):
        d = d + Q[i]*degree(A,Q,i)**self.n; 
 
     return d 


 #  It gives the gradient of k-star subgraph density gradients  
  def gradient(self, M):
     
    A = M[self.layer] 
    m=len(A);
 
    G = [ np.zeros((m,m)) for z in range(r) ]   

    for i in range(m):
       for j in range(i,m):
         G[self.layer][i][j] = self.n*Q[i]*Q[j]*(degree(A,Q,i)**(self.n-1)+(1-delta(i,j))*degree(A,Q,j)**(self.n-1)) ; 
         G[self.layer][j][i] = G[self.layer][i][j];        
 
    G2 = mat2vec(G); 
    G3 = np.zeros((m,1)) 
    for i in range(m):
      G3[i] = 0;
      for k in range(m):
         G3[i] = G3[i]  + self.n*A[i][k]*Q[k]*degree(A,Q,k)**(self.n-1);  

      G3[i] = G3[i] + degree(A,Q,i)**self.n; 
 
    # G2 = [G2;G3]; 
    G1 = np.concatenate( (G2, G3) , axis=0)

    # Gradient conversion to consider the constraint \sum_i Q[[i]] = 1     
    G0 = covgradient(m, G1) 
    
    return G0

 
  # k star subgraph density gradients considering Q as a constant parameter
  def gkstarQ(self,M):
   A = M[self.layer]  
   m=len(A);
   G = np.zeros((m, m)); 
   for i in range(m):
       for j in range(i,m):
           G[i][j] = self.n*degree(A,Q,i)**(self.n-1)+(1-delta(i,j))*degree(A,Q,j)**(self.n-1)*Q[i]*Q[j]; 
           G[j][i] = G[i][j];        
   G2 = mat2vec(G); 
   return G2
 
 

  # It computes the Hessian of k-star subgraph density   
  def hq(self, M):
 
    A = M[self.layer]  
    n2 = floor(r*m*(m+1)/2 )
    H = np.zeros((n2,n2))
    for i in range(m):
     for j in range(i,m):
       ij = posn(i,j)
       for k in range(m):
        for l in range(k,m):
          kl = posn(k,l)
          H[ij][kl] = (1-delta(i,j))*degree(A,Q,j)**(self.n-2)*ddegree(Q,j,l,k)
 # H[ij][kl] = (1-delta(i,j))*degree(A,Q,j]**[n-2]*[Q[k]*delta(j,l]+(1-delta(k,l))*delta(j,k)*Q[l]];
          H[ij][kl] = H[ij][kl] +     degree(A,Q,i)**(self.n-2)*ddegree(Q,i,l,k)
 #        H[ij][kl] = H[ij][kl] +     degree(A,Q,i]**[n-2]*[Q[k]*delta(i,l]+(1-delta(k,l))*delta(i,k)*Q[l]];
 
          H[ij][kl] = self.n*(self.n-1)*Q[i]*Q[j]*H[ij][kl];
          H[kl][ij] = H[ij][kl];
    return H
 

 # It computes the hessian of k-star subgraph density
  def hp(self, M):
   A = M[self.layer]  
   m=len(A); 
   H = np.zeros((m, m));  
   for i in range(m):
    for j in range(m):
       d = 0 ;
       for k in range(m):
           d = d + Q[k]*A[i][k]*A[j][k]*degree(A,Q,k)**(self.n-2);
  
       H[i][j] = self.n*A[i][j]*( degree(A,Q,i)**(self.n-1) + degree(A,Q,j)**(self.n-1))+ self.n*(self.n-1)*d  ; 
       H[j][i]= H[i][j];

   return H
     
# It Computes the Hessian matrix of k-star density  forv the partition   
  def hm(self, M):

   A = M[self.layer]  
   m2 = floor(r*m*(m+1)/2 )

   H = np.zeros((m2,m))
   for i in range(m):
    for j in range(i,m):
       ij = posn(i,j);
       for s in range(m):
           H[ij,s] = self.n * (Q[i]*delta(j,s)+Q[j]*delta(i,s))  * (degree(A,Q,i)**(self.n-1) 
           + (1-delta(i,j))*degree(A,Q,j)**(self.n-1) ); 
           H[ij,s] = H[ij,s] + self.n*(self.n-1)*Q[i]*Q[j]*(A[i][s]*degree(A,Q,i)**(self.n-2) 
                         + (1-delta(i,j))*A[j,s]*degree(A,Q,j)**(self.n-2)  ) 
 
   return H
 






        
class onelayercycle4(graphp): 

  layer = 0  
  def __init__( self,  layer):    
      self.name = "cycle4" 
      self.layer = layer
  
  # 4-Cycle
  def density(self, M):     
    A = M[self.layer]  
    d = 0; 
    for x1 in range(m):
     for x2 in range(m):
      for x3 in range(m):
       for x4 in range(m):
         d = d + A[x1][x2] * A[x2][x3] * A[x3][x4] * A[x4][x1] * Q[x1] * Q[x2] * Q[x3] * Q[x4];
 
    return d 


  # It gives the gradient of 4-cycle subgraph density   
  def gradient(self, M):

    A = M[self.layer] 
    G = [ np.zeros((m,m)) for z in range(r) ]   
      
    for i in range(m):
       for j in range(m):
         G[self.layer][i][j] = 4*(2-delta(i,j))*t3pathc(A,Q,i,j)
         G[self.layer][i][j] = Q[i]*Q[j]*G[self.layer][i][j]; 
         G[self.layer][j][i] = G[self.layer][i][j];        
 
    G2 = mat2vec(G); 
    G3 = np.zeros((m,1)) 
    for i in range(m):
        G3[i] = 4*t4cyclec1(A,Q,i)

    # G2 = [G2;G3]; 
    G1 = np.concatenate( (G2, G3), axis=0)
    # Gradient conversion to consider the constraint \sum_i Q[[i]] = 1     
    G0 = covgradient(m, G1) 
    
    return G0


  # 4 cycle subgraph density considering Q as a constant parameter  
  def gradientQ(self, M):

   A = M[self.layer]  
   G2=[]; 
   G = np.zeros((m, m)); 
   for i in range(m):
       for j in range(m):
           G[i][j] = 4*(2-delta(i,j))*t3pathc(A,Q,i,j)*Q[i]*Q[j];
           G[j][i] = G[i][j];        
   G2 = mat2vec(G); 
   return G2




 # It computes the Hessian of triangle subgraph density   
  def hq(self, M):

   A = M[self.layer]  
   n2 = floor(r*m*(m+1)/2 )
   H = np.zeros((n2,n2)) 
   for i in range(m):
    for j in range(i,m):
      ij = posn(i,j) 
      for k in range(m):
       for l in range(k,m):
          kl = posn(k,l) 
          H[ij][kl] = Q[k]*Q[l]*( A[i][k]*A[j][l]+(1-delta(k,l))*A[i][l]*A[j][k] )  ;
          H[ij][kl] = H[ij][kl] + delta(j,k)*Q[l]*qdegree(A,Q,l,i) + (1-delta(k,l))*delta(j,l)*Q[k]*qdegree(A,Q,k,i) ;
          H[ij][kl] = H[ij][kl] + delta(i,k)*Q[l]*qdegree(A,Q,l,j) + (1-delta(k,l))*delta(i,l)*Q[k]*qdegree(A,Q,k,j) ;
          H[ij][kl] = 4*(2-delta(i,j))*Q[i]*Q[j]*H[ij][kl];
          H[kl][ij] = H[ij][kl];
   return H

 # It computes the hessian of 4-Cycle  subgraph density 
  def hp(self, M):

   A = M[self.layer]  
   m=len(A); 
   H = np.zeros((m, m));   
   for i in range(m):
    for j in range(m):
       H[i][j]=4*(2*A[i][j]*t3pathc(A,Q,i,j)+t4cyclec(A,Q,i,j));
       H[j][i]= H[i][j];
   return H     

  # It Computes the Hessian matrix of 4-cycle density  for the partition  
  def hm(self, M):

   A = M[self.layer]  
   m2 = floor(r*m*(m+1)/2 )

   H = np.zeros((m2,m))
   for i in range(m):
    for j in range(i,m):
       ij = posn(i,j);
       for k in range(m):
           H[ij][k] = 4*(2-delta(i,j))*( (Q[i]*delta(j,k)+Q[j]*delta(i,k))*t3pathc(A,Q,i,j) 
           + Q[i]*Q[j]*( A[i][k]*qdegree(A,Q,j,k) + A[j][k]*qdegree(A,Q,i,k)  )); 
 
   return H




 




    
class onelayerclique4(graphp):

  layer = 0  
  def __init__( self, layer):    
      self.name = "clique4" 
      self.layer = layer

  # 4-Clique subgraph density     
  def density(self, M):

    A = M[self.layer]  
    m=len(A);
    d = 0; 
    for x1 in range(m):
     for x2  in range(m):
      for x3 in range(m):
       for x4 in range(m):
        d = d + A[x1, x2] * A[x1][x3] * A[x1][x4] * A[x2][x3] * A[x2][x4] * A[x3][x4] * Q[x1] * Q[x2] * Q[x3] * Q[x4] ;    

    return d  


# 4 clique subgraph density   
  def gradient(self, M):

    A = M[self.layer]  
    G = [ np.zeros((m,m)) for i in range(r) ]
     
    for i in range(m):
      for j in range(i,m):
         G[self.layer][i][j] = 6*(2-delta(i,j))*t4cliquec(A,Q,i,j)
         G[self.layer][i][j] = Q[i]*Q[j]*G[self.layer][i][j]; 
         G[self.layer][j][i] = G[self.layer][i][j];
 
    G2 = mat2vec(G); 
    G3 = np.zeros((m,1)) 
    for i in range(m):
       G3[i] = 4*t4cliquec1(A,Q,i)
 
    # G2 = [G2;G3]; 
    G1 = np.concatenate( (G2, G3), axis=0)
 
    # Gradient conversion to consider the constraint \sum_i Q[[i]] = 1     
    G0 = covgradient(m, G1) 
    
    return G0


  # It gives the gradient of 4-clique subgraph density considering Q as a constant parameter  
  def gradientQ(self, M):
   A = M[self.layer] 
   G2=[]; 
   G = np.zeros((m, m)); 
   for i in range(m):
     for j in range(m):
         G[i][j] = 6*(2-delta(i,j))*t4cliquec(A,Q,i,j)*Q[i]*Q[j];
         G[j][i] = G[i][j];        
   G2 = mat2vec(G); 
   return G2



 # It computes the  Hessian of 4-clique subgraph density 
  def hq(self, M):

   A = M[self.layer]  
   n2 = floor(r*m*(m+1)/2 )
   H = np.zeros((n2,n2)) 
   for i in range(m):
    for j in range(i,m):
        ij = posn(i,j) 
        for k in range(m):
         for l in range(k,m):
            kl = posn(k,l) 
            H[ij][kl] = Q[k]*Q[l]*(2-delta(k,l))*A[i][l]*A[j][l]*A[i][k]*A[j][k] ;       
            H[ij][kl] = H[ij][kl] + 2*( (delta(i,k)*A[j][l]+ delta(j,k)*
            A[i][l])*Q[l]*t3starc3(A,Q,i,j,l) + (1-delta(k,l))*(delta(i,l)*A[j][k]+delta(j,l)*A[i][k])*Q[k]*t3starc3(A,Q,i,j,k) );
            H[ij][kl] = 6*(2-delta(i,j))*Q[i]*Q[j]*H[ij][kl];
  
            H[kl][ij] = H[ij][kl];
 
 
   return H
 

# It computes the hessian of 4-Clique subgraph density 
  def hp(self, M):

   A = M[self.layer]  
   H = np.zeros((m, m));  
   for i in range(m):
    for j in range(m):
       H[i][j]=12*A[i][j]*t4cliquec(A,Q,i,j);
       H[j][i]= H[i][j];
   return H
     

# It Computes the Hessian matrix of 4-clique density  for the partition  
  def hm(self, M):

   A = M[self.layer]   
   m2 = floor(r*m*(m+1)/2 )
    
   H = np.zeros((m2,m))
   for i in range(m):
    for j in range(i,m):
       ij = posn(i,j);
       for k in range(m):
         H[ij][k] = 6*(2-delta(i,j))*( [Q[i]*delta(j,k)+Q[j]*delta(i,k)]*t4cliquec(A,Q,i,j) + 2*Q[i]*Q[j]*A[i][k]*A[j][k]*t3starc3(A,Q,i,j,k) ); 
 
   return H





class ratefunction(graphp):
    
  def __init__( self):    
      self.name = "ratefunction" 

# Compute graphon raterfunction    
  def density(self,M):
      
    d = 0 ;
    for layer in range(r):
      A = M[layer]
      for i in range(m):
         for j in range(m):
             if (A[i][j] > 0 and A[i][j] < 1):
                d = d + (2-delta(i,j))*Q[i]*Q[j]*(A[i][j]*
                    log(A[i][j]) + (1-A[i][j])*log(1-A[i][j]) ) 
      return d 
 
  # Compute ratefunction gradient
  def gradient(self, M):
 
    G2 = self.gradientQ(M) ; 
    Q = M[r] 
    G=np.zeros((m,1))    
    for k in range(m):
       for j in range(m):
         for layer in range(r):
           G[k][0] = G[k][0] + 2*Q[j]*I0(M[layer][k][j]) ; 
 
    G1 = np.concatenate( (G2, G) , axis=0)
    # Gradient conversion to consider the constraint \sum_i Q[[i]] = 1     
    G0 = covgradient(m, G1) 
    
    return G0

  # Compute ratefunction gradient considering Q as a constant parameter  
  def gradientQ(self, M):

     G = [ np.zeros((m,m)) for i in range(r) ]

     Q = M[r]
     for layer in range(r):
      for i in range(m):
        for j in range(i,m):
         G[layer][i][j] = (2-delta(i,j))*logit(M[layer][i][j])*Q[i]*Q[j]; 
         G[layer][j][i] = G[layer][i][j]; 

     print(G)
     G2 = mat2vec(G); 
     return G2
 



  def hq(self, M):
    m2 = floor(r*m*(m+1)/2 )
    H = np.zeros((m2 ,m2 ))
    A = M[self.layer] 
    for ij in range(m2 ):
        i,j = posuv(ij) 
        H[ij,ij] = (2-delta(i,j))*Q[i]*Q[j]/(A[i][j]*[1-A[i][j]]); 

    return H


  # Compute the Hessian matrix of ratefunction(M] for the partition
  def hp(self, M):

    H = np.zeros((m,m)); 
    A = M[self.layer] 
    for i in range( m ):
      for j in range(i, m ):
         H[i][j] = 2*I0(A[i][j]);
         H[j][i] = H[i][j];

    return H


  def hm(self, M):

    m2 = floor(r*m*(m+1)/2 )
    H = np.zeros((m2 ,m2))
    for layer in range(r):
       A = M[layer]
       for i in range( m ):
         for j in range( i, m ):
            ij= posn(i,j) 
            for k in range(i, m ):
              H[ij][k] = (2-delta(i,j))*(  Q[i]*delta(k,j) + Q[j]*delta(k,i) )*logit(A[i][j]);

    return H
 




class doubleedge(graphp):

  def __init__( self):    
      self.name = "doubleedge" 

      
  def density(self, M):

    d = 0 ;
    if (r < 2):
        return d 

    A1 = M[0]
    A2 = M[1]
    Q = M[2]
    
    for x1 in range(m):
      for x2 in range(m):
        d = d + A1[x1][x2] * A2[x1][x2] * Q[x1] * Q[x2];

    return d 


  def gradient(self, M):

      G = [ np.zeros((m,m)) for i in range(r) ]

      return G

  def gradientQ(self, M):
      G2 = np.zeros((m, m)); 
      return G2


  def hq(self, M):
      n = floor(r*m*(m+1)/2 )
      H12 = np.zeros((n,n))

      for i in range(m):
       for j in range(i,m):
        ij = posn(i,j) ;
        for k in range(m):
         for l in range(k,m):
          kl = posn(k,l) ;
          H12[ij][kl] = Q[i]*Q[j]* (2-delta(i,j))* (delta(i,k)*delta(j,l) + (1-delta(k,l))*delta(i,l)*delta(j,k) ) ;             
          H12[kl][ij] = H12[ij][kl]
          
      H1 = np.concatenate((np.zeros((n,n)), H12), axis=1)
      H2 = np.concatenate((H12.T, np.zeros((n,n)) ), axis=1)
      H = np.concatenate((H1, H2), axis=0)
      H0 = covhessian(m, H) 

      return H0

  def hp(self, M):
     return -1 

  def hm(self, M):
     return -1 






def getRandomStepFunction(m, offdiagonal):      
      M = []
      for layer in range(r): 
        M.append(  np.zeros((m,m)) )
        for i in range(m):
          for j in range(i,m):
             if (i==j):
                M[layer][i][j] = random() 
             else: 
               M[layer][i][j] = offdiagonal;
               M[layer][j][i] = offdiagonal; 

     
      M.append( np.ones((m,1)) /m ) 
      return M
 


# This  function computes the gradient of a function considering the 
# constraint   Q[m] = 1-\sum_i**[n-1] Q[i] 
def covgradient(m, G):
 n = len(G);
 G2 = np.zeros((n-1, 1))
 for i in range(n-m):
      G2[i]=G[i]; 
 
 for i in range((n-m),n-1):
      G2[i] = G[i] - G[n-1]; 
 
 return G2
 
 

# This function the Hessian H computed by the above functions into a Hessian where the parameter Q has m-1 parameter instead of m 
# since  Q[m] = 1-\sum_i**[n-1] Q[i] and m is the length of vector Q  
def covhessian(m, H):
 n = len(H);
 H2 = np.zeros((n-1, n-1))
 for i in range(n-m): 
  for j in range(i,n-m): 
     H2[i][j] = H[i][j]; 
 H2[j][i] = H[i][j]; 

 for i in range( (n-m) , (n-1) ):
  for j in range( i, (n-1) ):
     H2[i][j] = H[i][j] - H[i][n-1] - H[n-1][j] + H[n-1][n-1]; 
     H2[j][i] = H2[i][j];
     
 for i in range( (n-m) , (n-1) ):
  for j in range(  n-m ):
     H2[i][j] = H[i][j] - H[n-1][j] ; 
     H2[j][i] = H2[i][j];
 
 return H2



# Partial product-sum cimputation whose input is in the vector-matrix representation 
# These functions are used in the computation of first and second partial derivatives
# -----------------------------------------------------------------------------------
def ttrianglec1(A,Q,i):
 d= 0; 
 m = len(A); 
 for x1 in range(m):
  for x2 in range(m):
     d = d + A[x1][x2]*A[x1][i]*A[x2][i]*Q[x1]*Q[x2]; 
     
 return d  
 
def t3starc3(A,Q,i,j,k):
 d = 0;
 m = len(A); 
 for x1 in range(m):
     d = d + A[i][x1] * A[j][x1] * A[k][x1] * Q[x1]; 
 
 return d 
 

def t3starc1(A,Q,i):
  d = 0;
  m = len(A); 
  for x1 in range(m):
   for x2 in range(m):
    for x3 in range(m):
      d = d + A[i][x1] * A[i][x2] * A[i][x3] * Q[x1]*Q[x2]*Q[x3]; 
  return d 
 

def t3starc2(A,Q,i,j):
  d = 0;
  m = len(A); 
  for x1 in range(m):
   for x2 in range(m):
     d = d + A[x1,i] * A[x1][j] * A[x1][x2] * Q[x1]*Q[x2]; 
  return d 
 
 

    
def degree2paths(A,Q,i):
 m=len(A);
 d= 0; 
 for x1 in range(m):
     for x2 in range(m):
         d = d + A[i][x1]*A[x1][x2]*Q[x1]*Q[x2];   
 return d 
 
def degree3paths(A,Q,i):
  m=len(A);
  d= 0; 
  for x1 in range(m):
   for x2 in range(m):
    for x3 in range(m):
      d = d + A[i][x1]*A[x1][x2]*A[x2][x3]*Q[x1]*Q[x2]*Q[x3];   
  return d 
 
 
def t3pathc1(A,Q,i):
  m=len(A);
  d= 0; 
  for x1 in range(m):
   for x2 in range(m):
    for x3 in range(m):
       d = d + A[x1,i]*A[i][x2]*A[x2][x3]*Q[x1]*Q[x2]*Q[x3];
  return d 
 
 
def t3pathc(A,Q,i,j):
  m=len(A);
  d= 0; 
  for x1 in range(m):
     for x2 in range(m):
         d = d + A[i][x1]*A[x1][x2]*A[x2][j]*Q[x1]*Q[x2];   
  return d  
 
 
def t3pathc2(A,Q,i,j):
  m=len(A); 
  d=0;
  for x1 in range(m):
   for x2 in range(m):
      d = d + A[i][x1]*A[x1][j]*A[j][x2]*Q[x1]*Q[x2];
  return d 
 
 
# Quadratic degree This function computes the conditional subgraph density of 2-star graph
def qdegree(A,Q,i,j):
 d = 0;
 m = len(A); 
 for x1 in range(m):
     d = d + A[i][x1] * A[j][x1] * Q[x1]; 
 
 return d  
 




 def t2starc1(A,Q,i):
  d = 0;
  m = len(A); 
  for x1 in range(m):
   for x2 in range(m):
     d = d + A[i][x1] * A[i][x2] * Q[x1]*Q[x2]; 
  return d 
 


def t2starc2(A,Q,i,j): 
  d = 0;
  m = len(A); 
  for x1 in range(m):
     d = d + A[i][x1] * A[j][x1]*Q[x1]; 
 
  return d 
 
 

 
# Degree of i 
def degree(A,Q,i):
  m=len(A);
  d= 0; 
  for x1 in range(m):
     d = d + Q[x1] * A[i][x1] ;
 
  return d 
 

 
 # This method computes the partial derivative  \partial \sum_j Q[i] A_[ij] /\partial A_[kl]
 # when A_[ij] = A_[ji]
def ddegree(Q,i,k,l):
   dd = Q[l]*delta(i,k) + (1-delta(k,l))*Q[k]*delta(i,l); 
   return dd 
 


def t4cyclec(A,Q,i,j):
  m=len(A); 
  d = 0;
  for x1 in range(m):
   for x2 in range(m):
      d = d + A[x1,i]*A[i][x2]*A[x2,j]*A[j,x1]*Q[x1]*Q[x2];
 
  return d 
 
def t4cyclec1(A,Q,i):
 m=len(A); 
 d = 0;
 for x1 in range(m):
  for x2 in range(m):
   for x3 in range(m):
     d = d + A[x1,i]*A[i][x2]*A[x2][x3]*A[x3][x1]*Q[x1]*Q[x2]*Q[x3];
 return d
 
 
def t4cliquec(A,Q,i,j):
 m=len(A);
 d= 0; 
 for x1 in range(m):
     for x2 in range(m):
         d = d + A[x1][x2]*A[x1][i]*A[x1][j]*A[x2][i]*A[x2][j]*Q[x1]*Q[x2];   
 return d
 
def t4cliquec1(A,Q,i):
 m=len(A);
 d= 0; 
 for x1 in range(m):
  for x2 in range(m):
   for x3 in range(m):
     d = d + A[i][x1]*A[i][x2]*A[i][x3]*A[x1][x2]*A[x1][x3]*A[x2][x3]*Q[x1]*Q[x2]*Q[x3];
 return d
 



# -------------------------------------------------------------


def gradientQ( M ):

     G = [ np.zeros((m,m)) for i in range(r) ]

     Q = M[r]
     for layer in range(r):
      for i in range(m):
        for j in range(i,m):        
         G[layer][i][j] = (2-delta(i,j))*logit(M[layer][i][j])*Q[i]*Q[j]; 
         G[layer][j][i] = G[layer][i][j]; 
         print( G[layer][j][i]  )   

     print(G)
     G2 = mat2vec(G); 
     return G2


 
# def to obtain Jacobians and Lagrange Multipliers
# -----------------------------------------------------------


# get gradient vectors 
def getJacobian(M): 
 m = len(Q); 

 J = [ ]; 
 for i in range(len(vecgraph)):
    J.append(  covgradient(m, vecgraph[i].gradient( M)) )

 return J
 

 
 
 # get gradient vectors considering Q as a constant parameter
def getJacobianQ(M):
 J = []; 
 for i in range(len(vecgraph)):
     J = [J,  vecgraph[i].gradientQ( M)] 
 
 return J
 
 # Get lagrange multipliers 
 # beta : Lagrange multipliers
 # rerror : Relative error
 # e : error vector 
def getLagrange(vecgraph,X):

 M = vec2mat(X) 
 M = getJacobian(M) 
 Y = gradientI(M) 
 
 # beta = inv[M'*M]*M'*Y; 
 beta = (M.T.dot(M)**(-1)).dot(M.T).dot(Y)
 Yhat = M*beta; 
 E = Y - Yhat; 
 rerror = np.inner(E,E)*np.inner(Yhat,Yhat); 
 return beta, E, rerror 
 
 
 # This function computes the \mu coefficients of the geodesic hessian whne Q is  constant 
def getmuQ(M):
 Jh =   getJacobianQ[M] ; 
 Gent =  rateI.gradientQ(M) 
 # mu =  Gent'*Jh*inv[Jh'*Jh]; 
 mu =  Gent.T.dot(Jh).dot((Jh.T.dot(Jh))**(-1) ) 
 return mu 
 


 
 # This function computes the geodsic hessian of the ratefunction def at [A,Q]
def getGeoHessian(M):
 Jh =  getJacobian(M) ; 
 Hg =  rateI.hessian( M) 
 Gent =  rateI.gradient(M)
 # mu =  Gent'*Jh*inv[Jh'*Jh];     
 
 mu =  Gent.T.dot(Jh).dot((Jh.T.dot(Jh))**(-1) ) 
 for i in range(len(vecgraph)):
     Hg = Hg - mu[i]* vecgraph[i].hessian( M )

 return Hg


 
 
 
 # This function computes the geodesic hession of the ratefunction def at W 
 # and considering Q as a constant parameter
def getGeoHessianQ(M):

 Jh =  getJacobianQ(M) ; 
 Hg =  rateI.hessianQ( M) 
 Gent = rateI.gradientQ(M)
 # mu =  Gent'*Jh*inv[Jh'*Jh]; 
 mu =  Gent.T.dot(Jh).dot((Jh.T.dot(Jh))**(-1) ) 
 for i in range(len(vecgraph) ):
     Hg = Hg - mu[i]* vecgraph[i].hessianQ( M)
 
 return Hg
 
 
 
 # This function computes the max and min eigenvalues of the geodesic hessian projected into the tangent space
def qform(M):
 mm = m*(m+1)/2 + m - 1; 
 Pj = getProjection[M]; 
 Hg = getGeoHessian[M]; 
 A = [np.identity(mm) - Pj ]
 # B = A'*Hg*A; 
 B = A.T.dot(Hg).dot(A)
 ee,v = eig(B) ; 
 for i in range(len(ee) ):
      for j in range(i,len(ee)):
          if (ee[i][1] > ee[j][1]):
             temp = ee[i][1]; 
             ee[i][1] = ee[j][1]; 
             ee[j][1] = temp; 
             temp = ee[i][2]; 
             ee[i][2] = ee[j][2]; 
             ee[j][2] = temp; 

 qmin = ee[len(vecgraph)+1,2]; 
 qmax = ee[len(ee),2];  
 
 return qmin, qmax, B
 
 
 
 # This function computes the max and min eigenvalues of the geodesic hessian 
 # projected into the tangent space where Q is a constant parameter
def qformQ(M):
 m = len(Q); 
 mm = m*(m+1)/2; 
 Pj = getProjectionQ(M); 
 Hg = getGeoHessianQ(M); 
 A = [np.identity(mm) - Pj]; 
 B = A.T.dot(Hg).dot(A )
 ee = eig[B] ; 

 ecut = np.sort( abs(ee) ); 
 qmin =  min[ee[abs[ee] > ecut]] ; 
 qmax =  max[ee[abs[ee] > ecut]] ;
 
 return qmin, qmax, B
 
 
 
  
 # get the projection matrix into the normal space of the feasible at P considering Q as a constant parameter
def getProjectionQ(M):
 Jh =  getJacobianQ(M) ; 
 # Pj = Jh*inv[Jh'*Jh]*Jh';
 Pj =  Jh.dot((Jh.T.dot(Jh))**(-1) ).dot(Jh.T)            
                         
 return Pj
 
 

  
 
 
 # get the projection matrix into the normal space of the feasible at P 
def getProjection(M):
 Jh =  getJacobian[M] ; 
 # Pj = Jh*inv[Jh'*Jh]*Jh';

 Pj =  Jh.dot((Jh.T.dot(Jh))**(-1) ).dot(Jh.T)                         
 
 return Pj 
 


# This function computes the \mu coefficients of the geodesic hessian 
def getmu(M):
 Jh =  getJacobian[M] ; 
 Gent = rateI.gradient(M) 
    
 # mu =  Gent'*Jh*inv[Jh'*Jh]; 
 mu =  Gent.T.dot(Jh).dot((Jh.T.dot(Jh))**(-1) )     
 return mu

def optimalstepfunction(rho, tau):
 Q = np.zeros( [0.5, 0.5])
 x = [rho**3 - tau]**[1/3]; 
 P = np.zeros( [rho-x, rho+x, rho-x] )
 
 return [ vec2mat(P), Q ]



# Miscelaneous functions
# -------------------------------------------------------------------
# This function computes the minimum triangle density based on Alexander Razborov  formula 
def mintri(rho):
 t = floor[1/[1-rho]]; 
 d = [t-1] * [t - 2*sqrt[ t*[t-rho*[t+1]] ] ] * [ t + sqrt[ t*[t-rho*[t+1]] ] ]**2;
 d = d /[ t**2 * [t+1]**2 ]; 
 return d 
 
 
 # functions to compute the optimql 2-stepdef for edge-triangle constraints when edge densities is less than 0.5 and triangle densities is less than edge-density**3  
def getSolution(n, aW):
 m= aW[n,4]; 
 X = np.zeros((m*(m+1)/2 + m-1,1))
 X = aW[n,7:(6 + m*(m+1)/2 + m -1)].T 
 return X
 
 

 
 
 # Optimization methods, basic functions 
 # ------------------------------------------

 
 
 # Compute distance  of a stepdef to the constrained region in terms of sufficient statistics  sum_i [t[F_i,W]-stas_i]**2
def distanceF2(X):
 M = vec2mat(X) 
 d=distanceF(M)  
 return d
 
 
 # Compute distance  of a stepdef to the constrained region in terms of sufficient statistics  sum_i [t[F_i,W]-stas_i]**2 considereinmg as constant
def distanceFQ(P):
 d=0; 
 for i in range(len(vecgraph)):
     d = d + ((vecgraph[i].density(M) - vecstat[i]))**2; 

 return d
 

 
 
 # def that computes the hessian of the distance to the feasible region defined by a vector of graphs and a vector statistics at [M]
 # distance = sum_i [t[F_i,W]-stas_i]**2
def hessianF(M):
 m2 = floor( r*( m*(m+1)/2 ) + m-1  ) 
 H =np.zeros((m2,m2))
 for i in range(len(vecgraph)):
      # If the hessian total I apply covariant derivation 
      G2 = vecgraph[i].gradient( M )
      H2 = vecgraph[i].hessian( M) 
      H = H + 2*((vecgraph[i].density(M) - vecstat[i])*H2 + G2.dot(G2.T)) ; 

 return H


 
 # def that computes the hessian of the distance to the feasible region defined by a vector of graphs and a vector statistics at [M] when Q is constant 
 # distance = sum_i [t[F_i,W]-stas_i]**2
def hessianFQ(P):

 H = np.zeros((len(P) ,len(P) ))
 for i in range( len(vecgraph) ):
      # If the hessian total I apply covariant derivation 
     G2 =  vecgraph[i].gradientQ( M) 
     H2 =  vecgraph[i].hessianQ( M) 
     d = vecgraph[i].density(M) - vecstat[i]
     H = H + 2*(d*H2 + G2.dot( G2.T)) ; 

 return H
 
 
 
 
def hessianF2(X): 
 M = vec2mat(X) 
 H =hessianF(M)
 return H
 
 
def gradientF(M):
 m2 = floor( r*( m*(m+1)/2 ) + m-1 )
 G =np.zeros((m2,1))
 for i in range( len(vecgraph) ):
      # If the hessian total I apply covariant derivation 
      d = vecgraph[i].density(M) - vecstat[i]
      G = G + 2*d* vecgraph[i].gradient( M )
 
 return G
 
def gradientFQ(P):
 G = np.zeros((len(P) ,1))
 for i in range( len(vecgraph) ):
      G = G + 2*[vecgraph[i].density( M) 
                 - vecstat[i]]*vecgraph[i].gradientQ( M); 

 return G
 
  
def gradientF2(X):
 M =vec2mat(X) 
 G =gradientF(M) ;
 return G
 
 
def ratefunction2(X):
 M = vec2mat(X) 
 if ( M[r][m] >= 0):
    d=ratefunction(M) 
 else:
    d=0; 

 return d
 
def I0(x):
 d = x*log(x) + (1-x)*log(1-x) ; 
 return d
 
 
 # Compute ratefunction gradient
def gradientI(M):
 
 
 G = [np.zeros((m, m)) for i in range(r)]    
 for layer in range(r):
   A = M[layer]
   for i in range(m):
    for j in range(i,m):
      G[layer][i][j] = (2-delta(i,j))*Q[i]*Q[j]*logit[A[i][j]]; 
      G[layer][j][i] = G[layer][i][j]; 


    for k in range(m-1):
      for j in range(m):
         G[layer][k] = G[layer][k] + 2*Q[j]*( I0(A[k][j]) - I0(A[m,j]) ); 


   return G
 
 
 
def gradientI2(X):
 M =vec2mat(X); 
 G=rateI.gradient(M);
 return G
 
 
 # Compute the Hessian matrix of ratefunction(A]
def hessianI2(X):
 M = vec2mat(X); 
 H = rateI.hessian(M)
 return H
 
 


# This function computes the objetive def to find optimal stepfunction
# ----------------------------------------------------------------------------
def funtot(X):
 # Penalty factor 

 M = vec2mat(X)
 dis = distanceF(M) 
 d = rateI.density(M)+penalty*dis; 
 return d

# This function computes the gradient of the objetive def 
# ---------------------------------------------------------------------------- 
def gfuntot(X):
 # Penalty factor 
 M = vec2mat(X)
 G = rateI.gradient(M)+penalty*gradientF(M); 
 return G
 

# This function computes the hessian of the objetive def 
# -----------------------------------------------------------
def hfuntot(X):
 M = vec2mat(X) 
 H = rateI.hessian(M)+penalty*hessianF(M) 
 return H
 
 
 # Compute distance  of a stepdef to the constrained region in terms of sufficient statistics  sum_i [t[F_i,W]-stas_i]**2
def distanceF(M):
 d=0; 
 for i in range(len(vecgraph)):
     d = d + ((vecgraph[i].density( M) - vecstat[i]))**2; 
 return d
 
 
  
 # Compute the distance to the feasble region i.e. sum_i [t[F_i,W]-stas_i]**2
def distance(X): 
 M = vec2mat(X)
 d=0; 
 for i in range( len(vecgraph) ):
     d = d + ((vecgraph[i].density( M) - vecstat[i]))**2; 

 return d
 
 
  
 
 # This function computes the gradient of frate[P]
def gfrate(X): 
 P = X[ 1:m*(m+1)/2 ];
 Q = X[ m*(m+1)/2+1:m*(m+1)/2+m-1];
 Q = [Q, 1-sum[Q]];
 G= rateI.gradient(M) + penalty*gdistance(X) 
 return G
 
 
def gdistance(X):
 P = X[ 1:m*(m+1)/2 ];
 Q = X[ m*(m+1)/2+1:m*(m+1)/2+m-1];
 Q = [Q, 1-sum[Q]];
 G = np.zeros((len(P) +m-1,1))

 for i in range( len(vecgraph) ):
      # If the hessian total I apply covariant derivation 
      G = G + 2*(vecgraph[i].density( M) - vecstat[i])* vecgraph[i].gradient( M ) 

 return G
 
 
 
 
 
 # def that computes the hessian of the distance to the feasible region defined by a vector of graphs and a vector statistics at [M]
 # distance = sum_i [t[F_i,W]-stas_i]**2
def hdistance(M): 
 m2 = r*( m*(m+1)/2 ) + m-1
 H = np.zeros((m2,m2))
 for i in range( len(vecgraph)):
      # If the hessian total I apply covariant derivation 
      G2 =  vecgraph[i].gradient( M )
      H2 =  vecgraph[i].hessian( M )  
      H = H + 2*((vecgraph[i].density(M) - vecstat[i])*H2 + G2.dot[G2.T] ) ; 

 return H
 



    
 # Main Program 
# ---------------------------------------------------------------------------------------------------------------


 
global vecgraph; # vector of graph names 
global penalty;  # Penalty constant
global m;        # Stepdef size. However the def iniStepfunction[m, Xini] computes the minimal stepdef to satisfies constraints    
global r;        # Number of stepfunctions   
global Q

eps = 1e-18
inf = 2**10000


m=2;
r = 2; 
M = getRandomStepFunction(m, 0.95) 
Q = M[r]

# Vector of graphs [name, number of edges] 
# vecgraph = ["triangle", "4clique"]
#vecgraph = ["3star", "triangle"]
#vecgraph = ["4cycle", "triangle"]
# vecgraph = ["2star",   "triangle"]
vecgraph = [onelayeredge(1),    onelayertriangle(1)]
 
 
# Vector of sufficient statistics 
vecstat = np.array(  [ 0.735, 0.37171725] ) 
vecstat = np.array( [ 0.4,    0.056] )
penalty = 1e+8; 


rateI = ratefunction()
o1 = onelayeredge(0)
o2 = onelayertriangle(0)
o3 = onelayerpath3(0)
o4 = onelayerstark(0,2)
o5 = onelayercycle4(0)
o6 = onelayerclique4(0)
o7 = doubleedge()


o6.hessian(M)
rateI = ratefunction()
rateI.gradient(M)







 

# --------------------------------------------------------



 
 

# it gives the number of edge of a given graph name 
def eGraph(sName):
 
   if sName == "triangle":
     d = 3; 
   if sName == "3paths":
     d = 3;
   if sName == "edges":
     d = 1; 
   if sName == "2star":
     d =  2;
   if sName == "3star":
     d = 3; 
   if sName == "4star":
     d = 4; 
   if sName == "4cycle":
     d = 4;
   if sName == "4clique":
     d = 6;
     
   return d 
 
# if the graph is bipartite
def Isbipartite(sName):
 
   if sName == "triangle":
     d = 0; 
   if sName == "3paths":
     d = 1;
   if sName == "edges":
     d = 1; 
   if sName == "2star":
     d = 1;
   if sName == "3star":
     d = 1; 
   if sName == "4star":
     d = 1; 
   if sName == "4cycle":
     d = 1;
   if sName == "4clique":
     d = 0; 
  
   return d; 


 
 
 
 
# Temporary def to validate t4star2
def t4star22(X,layer):
 M = vec2mat(X)
 A = vec2mat(M[layer])
 d = 0;
 m = len(A); 
 for x0 in range(m):
  for x1 in range(m):
   for x2 in range(m):
    for x3 in range(m):
     for x4 in range(m):
           d = d + A[x0][x1] * A[x0][x2] * A[x0][x3] * A[x0][x4] * Q[x0]*Q[x1]*Q[x2]*Q[x3]*Q[x4];
 return d 
 
 
 
 






 
# Intialization functions 
# -----------------------------------------------------------------------------------------------------------------------------------------------------------------
 
  
 
def iniStepfunction(m, Xini=0, maxattempts=10):
 info=0; 
 obj=inf 
 found =0; 
 if ( len(Xini)  != m ):
    Xini = 0 ; 

 XF=0; 
 margin = 10*eps; 
 mini = 2
 # Adaptation [deformation] of the previous solution to the new constraints  
 if (len(Xini) > 1 and mini==m ):
    X = Xini ; 
    Xini2 = X; 
 #   [XF, dis, info, iter, nf, lambda] = sqp[X, [@distanceF2, @gradientF2, @hessianF2], [], [@partition] , zeros[len(X),1]+margin, ones[len(X),1]-margin,1000];
    # XF, dis, info, iter, nf, lambda = sqp[X, [distanceF2, gradientF2], [], [partition] , zeros[len(X),1]+margin, ones[len(X),1]-margin,1000];
    if (obj < 1e-8):
       found=1; 
       return ; 
 
 n=0;
 dis = 1; 
 dismin = 10; 
 XFmin = 0; 
 while (n<maxattempts and dis > 1e-8):
 #     [XF, dis, info, iter, nf, lambda] = sqp[X, [@distanceF2, @gradientF2, @hessianF2], [], [@partition] , zeros[len(X),1]+margin, ones[len(X),1]-margin,1000];
 #     X = [rand[m*(m+1)/2,1];ones[m-1,1]/m] ;
      X = getRandomStepFunction[m, 0.95];
      #[XF, dis, info, iter, nf, lambda] = sqp[X, [@distanceF2, @gradientF2 ], [], [@partition] , zeros[len(X),1]+margin, ones[len(X),1]-margin,1000];
      n=n+1
      if (dis < dismin ):
         dismin = dis; 
         XFmin = XF; 
 dis = dismin ; 
 XF = XFmin; 
 Xini1 = X; 
 if (dis <= 1e-8):
    found = 1; 

 return found, XF, Xini1, dis, info, n

 
 
def feasibility(vecstat_new, m, offdiagonal):

 vecstat = vecstat_new; 
 mindis = 99999999; 
 margin = 10*eps; 
 avgtime = 0; 
 for k in range(10):
      X = getRandomStepFunction[m, offdiagonal];
      time0 = datetime.datetime.now()
      dis= 0
      # [XF, dis, info, iter, nf, lambda] = sqp[X, [@distanceF2, @gradientF2 ], [], [@partition] , zeros[len(X),1]+margin, ones[len(X),1]-margin,1000];
      time1 = datetime.datetime.now() 
      avgtime = avgtime + time1 -time0 ; 
      if (dis < mindis):
         mindis = dis; 

 avgtime = avgtime / 10; 
 
 return mindis, avgtime 
 
 


 # Optimization functions: The input parameter are the global vectors  vecgraph and vecstat
# The def computeSolution[vecstat_new, Xini, mini] computes an optimal stepdef 
 # ---------------------------------------------------------------------------------------------------
 

 # def to constraint partititons  
def partition(X):
  m = getPar[len(X)];
  P = X[ 1:m*(m+1)/2 ];
  Q = X[ m*(m+1)/2+1:len(X) ];  
  d=1-sum[Q];
  return d 
  
 

 # This function computes the optimal step def given a valid initial solution. 
def getOptimalStepfunction(Xini):
 info=0;
 found = 0; 
 penalty = 1e+8; 
 margin = 10*eps; 
 XF=0; 
 X=Xini; 
 # try
   # [XF, obj, info, iter, nf, lambda] = sqp[X, [@funtot, @gfuntot, @hfuntot], [], [], zeros[len(X),1]+margin, ones[len(X),1]-margin,1000];
 #  catch
 obj = 1; 
 # end_try_catch
 if (obj < 1): 
    X = XF;
    iter = 10;
    while (iter > 2): 
      X = XF; 
      #[XF, obj, info, iter, nf, lambda] = sqp[X, [@funtot, @gfuntot, @hfuntot], [], [], zeros[len(X),1]+margin, ones[len(X),1]-margin,300];

    # When the soluton is closed to the optimum we relax constraints in order that rate def optimize further 
    penalty = 1e+6;
    X = XF ;    
   #[XF, obj, info, iter, nf, lambda] = sqp[X, [@funtot, @gfuntot, @hfuntot], [], [], zeros[len(X),1]+margin, ones[len(X),1]-margin,300];
   # After the optimation dominated the rate def we harden the constriants
    penalty = 1e+8; 
    X = XF ;    
   #[XF, obj, info, iter, nf, lambda] = sqp[X, [@funtot, @gfuntot, @hfuntot], [], [], zeros[len(X),1]+margin, ones[len(X),1]-margin,300];

 beta,E, rerror =getLagrange(vecgraph, XF) 
 nerror = rerror ; 
 return nerror, XF, obj, info 
 
 
 
 
 
 
# Get the minimal size of stepfunctions such that satisfy the constraints and minimize rate 
def computeSolution(vecstat_new, Xini, mini):

 found=0;
 rerror=inf;
 m=0;
 XF=0;
 obj=0;
 info=0; 
 Xini1=0;
 Xini2=0;
 time1 = 0;
 for m in range(mini,5):
     found = 0;
     Xini1 = 0; 
     Xini2 = 0; 
     [found, Xini2, Xini1,  dis, info, n]= iniStepfunction[m, Xini];
     if found:
        time1 = datetime.datetime.now()
        rerror, XF, obj, info = getOptimalStepfunction[Xini2]; 
        break;   

 return  found,rerror,m,XF, obj,info, Xini1, Xini2, time1
 
  
 
 """
# Functions to run experimients  
# ---------------------------------------------------------------------------------------------

 
# This function saves the head of file
def saveHead(d,vecgraph):
 for i in range(len(vecgraph)):
     fprintf(fd, "%s;", vecgraph[l]); 
     fprintf(fd, "%s;", "ratefunction");
     fprintf(fd, "%s;", "size")
     fprintf(fd, "%s;", "tfeas")
     fprintf(fd, "%s;", "topti")
     fprintf(fd, "%s;", "error")
     for j in range(19):
      fprintf(fd, "x%d;", j) 

 fprintf(fd, "%s;", "edges")
 fprintf(fd, "%s;", "triangle")
 fprintf(fd, "%s;", "4clique")
 fprintf(fd, "%s;", "2star")
 fprintf(fd, "%s;", "3star")
 fprintf(fd, "%s;", "4star")
 fprintf(fd, "%s;", "3paths")
 fprintf(fd, "%s;", "4cycle")
 fprintf(fd, "\n") 
 return 
 
 
 
 # This function saves a solution 
def saveSolution(fd, vecstat, m, tfeas, topti, rerror, X):
 for i in  range( len(vecstat) ):
    fprintf(fd, "%2.5f;", vecstat[i]) 

 fprintf(fd, "%2.12f;", ratefunction2(X))  
 fprintf(fd, "%d;", m) 
 fprintf(fd, "%4.4f;", tfeas)
 fprintf(fd, "%4.4f;", topti)
 fprintf(fd, "%4.12f;", rerror)
 
 for i in range( len(X)):
    fprintf(fd, "%2.18f;", X[i])

 
 fprintf(fd, "%1.4f;", density2("edges",X))
 fprintf(fd, "%1.4f;", density2("triangle",X))
 fprintf(fd, "%1.8f;", density2("4clique",X))
 fprintf(fd, "%1.8f;", density2("2star",X))
 fprintf(fd, "%1.8f;", density2("3star",X))
 fprintf(fd, "%1.8f;", density2("4star",X))
 fprintf(fd, "%1.8f;", density2("3paths",X))
 fprintf(fd, "%1.8f;", density2("4cycle",X))
 fprintf(fd, "\n")
 
 return 
 


 
 
 # This function runs experiments on rate def stepfuncion minization. 
def runExperiment(vecgraph_new, sName=""):

 vecgraph = vecgraph_new; 
 
 if isempty(sName):
    sName = strcat[vecgraph[1],"_", vecgraph[2], ".csv"];
 endif 
 fd = fopen [sName, "w"];
 saveHead[fd,vecgraph]; 
 Xini = 0; 
 for i in range(199):
     dedges=i*0.005;
     dsubgraph1=[dedges]**eGraph[vecgraph[1]];
     # Computation of optimal graphon above the Erods-Renyi curve 
     dsubgraph2=[dedges]**eGraph[vecgraph[2]];
     # Saving the Erdos-Renyi case
     saveSolution[fd, [dsubgraph1, dsubgraph2], 1, 0, 0, 0, dedges]; 
     dsubgraph2=dsubgraph2+0.005;
     m = 2 ; Xini=0; 
     found=1;
     while (dsubgraph2 < 1 and m != 0 and found ):
        vecstat_new = [dsubgraph1, dsubgraph2]; 
        t2=time() 
        [found, rerror,m,XF, obj,info, Xini1, Xini2, time1] = computeSolution[vecstat_new, Xini, 2 ];
        t1=time()
        if found:
           saveSolution[fd, [dsubgraph1, dsubgraph2], m, time1-t2, t1-time1, rerror, XF]; 
           Xini=XF; 
        endif
        dsubgraph2 = dsubgraph2 + 0.005;
     endwhile 
     # Computation of optimal graphon below the Erods-Renyi curve 
     dsubgraph2=[dedges]**eGraph[vecgraph[2]]-0.005;
     m = 2 ; Xini=0; found=1; 
     while (dsubgraph2 > 0 and m != 0 and found ):
        vecstat_new = [dsubgraph1, dsubgraph2]; 
        t2=time()
        [found, rerror,m,XF, obj,info, Xini1, Xini2, time1] = computeSolution[vecstat_new, Xini,2 ];
        t1=time()
        if found:
           saveSolution[fd, [dsubgraph1, dsubgraph2], m, time1-t2, t1-time1,  rerror, XF];
           Xini=XF; 
        endif
        dsubgraph2 = dsubgraph2 - 0.005;

 fclose[fd]; 
 return 
 
 
 
 
 # This function runs experiments on rate def stepfuncion minimization. 
def runExperimentlog(alog, vecgraph_new, sName):
 
 vecgraph = vecgraph_new; 
 
 fd = fopen [sName, "w"];
 saveHead[fd,vecgraph];
 for j in range(19):
    fprintf(fd, "x%d;", j)

 fprintf(fd, "\n")
 Xini = 0; 
 for i in range(len(alog)):
     dsubgraph1=alog[i,1];
     dsubgraph2=alog[i,2];
     t2=time()
     [found, rerror,m,XF, obj,info, Xini1, Xini2, time1] = computeSolution[[dsubgraph1, dsubgraph2], 0, 2 ];
     iter = 0; 
     rerrormin = 10; 
     Xmin=0;
     while (rerror > 10**-5 and iter < 5 ):
        [found, rerror,m,XF, obj,info, Xini1, Xini2, time1] = computeSolution[[dsubgraph1, dsubgraph2], 0, 2 ];
        if (rerror < rerrormin):
           rerrormin = rerror; 
           Xmin = XF; 
        endif 
        iter=iter+1 
     endwhile 
     t1=time()
     saveSolution[fd, [dsubgraph1, dsubgraph2], m, t1-time1, time1 -t1 , rerror, XF]; 
  
 fclose[fd]; 
 return  
 
 

 
def readtable(fname, skipline=0):
 fd = fopen [fname, "r"];
 if skipline:
    sline = fgets[fd]; 
 endif 
 i=0;
 while ( not feof[fd] ):
    i=i+1 
    sline = fgets[fd]; 
    tokens = tokenize[sline, ";" ]; 
    for j in range(len(tokens)):
        aW[i][j] = str2double[ tokens[j] ] ;

 fclose[fd]; 
 return aW
 
 
 
def savefile(sName, aW):
 fd = fopen [sName, "w"];
 n= len[aW[1,]]; 
 for i in range(len(aW)):
     for j in range(n-1):
        fprintf(fd, "%2.12f;", aW[i][j]) 

     fprintf(fd, "%2.12f \n", aW[i,n]) 
 
 fclose[fd]; 
 return
 


def checkIniValues(vecgraph_new ):
 
 aExp2 = readExperimients[vecgraph_new];
 # aExp  = aExp2(find[aExp2(:,3]!=2] ,:];  
 aExp  = 0
 
 
 vecgraph = vecgraph_new; 
 
 fd = fopen [strcat["log", vecgraph[1], vecgraph[2],".csv"], "a"];
 for j in range( len(aExp) ):
     [mindis, avgtime]  = feasibility[aExp[j,1,2], aExp[j,3], 0.95];
     fprintf(fd, "%s;", vecgraph[1])
     fprintf(fd, "%s;", vecgraph[2])
     fprintf(fd, "%12.6f;", aExp[j,1] )
     fprintf(fd, "%12.6f;", aExp[j,2] )
     fprintf(fd, "%d;", aExp[j,3])
     fprintf(fd, "%8.2f;", avgtime )
     fprintf(fd, "%18.14f;", mindis )
     fprintf(fd, "\n") 
 
 
 
 for j in range(len(aExp)):
     [mindis, avgtime]  = feasibility[aExp[j,1,2], aExp[j,3], 0.05];
     fprintf(fd, "%s;", vecgraph[1])
     fprintf(fd, "%s;", vecgraph[2])
     fprintf(fd, "%12.6f;", aExp[j,1] )
     fprintf(fd, "%12.6f;", aExp[j,2] )
     fprintf(fd, "%d;", aExp[j,3])
     fprintf(fd, "%8.2f;", avgtime )
     fprintf(fd, "%18.14f;", mindis )
     fprintf(fd, "\n") 
 

 fclose[fd]; 
 return
 
 
 

 
 """
 
 
 
 
 
def getOptimalEdgetriangle(stat):
    
 X = np.zeros((4,1))    
 X[0][0] = stat[1] - (stat[1]**3 - stat[2] )**[1/3] ; 
 X[1][0] = stat[1] + (stat[1]**3 - stat[2] )**[1/3] ; 
 X[2][0] = X[0][0]; 
 X[3][0] = 0.5; 
 return X
 
 
 
"""

# Numerical methods to compute gradients and hessians 
# ----------------------------------------------------

# Numerically computed hessian 
def fdhess(f,M):
 deltax = sqrt[eps];
 m2 = r*( m*(m+1)/2 ) + m-1 
 H2 = np.zeros ((m2, m2))
 for k in range(m2):
    if (k <= np):
       U = np.zeros((np,1))
       U[k]=1; 
       g = [f.gradient(P+deltax*U,Q)-f.gradient(M)]/deltax ; 
    else: 
       U = np.zeros((nq,1))
       U[k-np]=1; 
       g = [f.gradient(M+deltax*U)-f.gradient(M)]/deltax ; 
    
    for i in range( n):
        H2[k][i] = g[i];        

 return H2
 

# Numerically computed hessian 
def fdhess2(f,X):
 deltax = sqrt[eps];
 n = len(X); 
 H = np.zeros((n, n))
 for k  in range(n):
     U = np.zeros((n,1))
     U[k]=1; 
     g = [f.gradient2(X+deltax*U)-f.gradient2(X)]/deltax ; 
     for i in range(n):
         H[k][i] = g[i];        

 return H
 


def fdgrad (f,M):
    y0 = f.density(M)
    nx = len(P) ;
    G = np.zeros((nx,1))
    deltax = sqrt[eps];
    for i  in range(nx):
      t = P[i];
      P[i] += deltax;
      G[i] = [f.density(M) - y0] / deltax;
      P[i] = t;

    return G


def fdgrad2(f,X):
    y0 = f.density2(X)
    nx = len(X);
    G = np.zeros((nx,1))
    deltax = sqrt[eps];
    for i  in range(nx):
      t = X[i];
      X[i] = X[i] + deltax;
      G[i] = [f.density2(X) - y0] / deltax;
      X[i] = t;

    return G


"""
 

"""
class triangle3paths(graphp):

  def __init__( self):    
      self.name = "triangle3paths" 

     
  def density(self, M):

     d = 0 ;
     if (r < 2):
        return d 

     A1 = M[0]
     A2 = M[1]
     Q = M[2]
     
     for x1 in range(m):
      for x2 in range(m):
       for x3 in range(m):
        for x4 in range(m):
          d = d + A1[x1][x2] * A1[x2][x3] * A1[x3][x1] * A2[x1][x2] * A2[x2][x3] * A2[x3][x4] * Q[x1] * Q[x2] * Q[x3] * Q[x4];

     return d 


  def gradient(self, M):

     G = [ np.zeros((m,m)) for z in range(r) ]   
     Ma = self.mat1gtriangle3paths(M)
     for i in range(m):
         for j in range(m):  
             G[0][i][j] = Ma[i][j] + Ma[j][j] - Ma[i][i]

     Ma = self.mat2gtriangle3paths(M)
     for i in range(m):
        for j in range(m): 
            G[1][i][j] = Ma[i][j] + Ma[j,j] - Ma[i][i]

     G2 = mat2vec(G); 
     G3 = np.zeros((m,1))
     
     for i in range(m):
        G3[i] = 0; 
        for j in range(m):
         for k in range(m):
          for l in range(m):
            G3[i] = G3[i] + G[0][i][k]*G[0][i][j]*G[0][j][k] * G[1][i][k]*G[1][i][j]*G[1][j][l] +  G[0][i][k]*G[0][i][j]*G[0][j][k] * G[1][i][j]*G[1][j][k]*G[1][k][l];
            G3[i] = G3[i] + G[0][i][k]*G[0][i][j]*G[0][j][k] * G[1][j][k]*G[1][i][j]*G[1][i][l]  +  G[0][k][l]*G[0][k][j]*G[0][l,j] * G[1][k][l]*G[1][k][j]*G[1][i][j];

     # G2 = [G2;G3];
     G1 = np.concatenate( (G2, G3), axis=0)
 
     # Gradient conversion to consider the constraint \sum_i Q[[i]] = 1     
     G0 = covgradient(m, G1) 
    
     return G0


  def mat1gtriangle3paths(self, M):

      Ma = np.zeros((m,m))
      for i in range(m):
       for j in range(m):
         Ma[i][j] = 0; 
         for k in range(m):
          for l in range(m):
           Ma[i][j] = Ma[i][j] + M[0][k][j]*M[0][k][i]*M[1][k][j]*M[1][k][i]*M[1][i][l]; 
           Ma[i][j] = Ma[i][j] + M[0][i][k]*M[0][k][j]*M[1][i][k]*M[1][i][j]*M[1][k][l]; 
           Ma[i][j] = Ma[i][j] + M[0][i][k]*M[0][j][k]*M[1][i][k]*M[1][i][j]*M[1][j][l]; 
           Ma[i][j] = Ma[i][j]*Q[k]*Q[l]; 

         Ma[i][j] = Ma[i][j]*Q[i]*Q[j]; 
       return Ma


  def mat2gtriangle3paths(self,M):

      Ma = np.zeros((m,m))
      for i in range(m):
       for j in range(m):
        Ma[i][j] = 0; 
        for k in range(m):
         for l in range(m):
           Ma[i][j] = Ma[i][j] + M[0][i][k]*M[0][k][l]*M[0][i][l]*M[1][k][i]*M[1][k][l]; 
           Ma[i][j] = Ma[i][j] + M[0][i][j]*M[0][j][k]*M[0][i][k]*M[1][j][k]*M[1][i][l]; 
           Ma[i][j] = Ma[i][j] + M[0][i][k]*M[0][i][j]*M[0][j][k]*M[1][i][k]*M[1][k][l]; 
           Ma[i][j] = Ma[i][j]*Q[k]*Q[l]; 
        Ma[i][j] = Ma[i][j]*Q[i]*Q[j]; 

      return Ma


  def gtriangle3pathsQ(M):
     G2 = np.zeros((m, m)); 
     return G2

  def hq(self, M):

    A1 = vec2mat(M[0])
    A2 = vec2mat(M[1])
    H = np.zeros((m,m))
    for i in range(m):
     for j in range(i,m):
       # ij = posn(i,j) ;
       for k in range(m):
         for l in range(k,m):
          H[i][j]= H[i][j] +  Q[k]*Q[l]*( A1[i][j]*A1[j][l]*A1[i][l]*A2[i][l]*A2[i][j]*A2[j][k] + A1[i][j]*A1[j][k]*A1[i][k]*A2[i][j]*A2[i][k]*A2[k][l] + A1[i][l]*A1[i][k]*A1[k][l]*A2[i][l]*A2[i][k]*A2[k][j] );    
          H[i][j] = H[i][j] + Q[k]*Q[l]*( A1[i][j]*A1[j][k]*A1[k][i]*A2[i][j]*A2[j][k]*A2[k][l] + A1[i][j]*A1[j][k]*A1[k][i]*A2[k][i]*A2[k][j]*A2[j][l] + A1[i][k]*A1[k][l]*A1[l][i]*A2[k][i]*A2[k][l]*A2[l][j] );    
          H[i][j] = H[i][j] + Q[k]*Q[l]*( A1[i][j]*A1[j][k]*A1[k][i]*A2[j][k]*A2[i][j]*A2[i][l] + A1[i][j]*A1[j][k]*A1[k][i]*A2[k][j]*A2[k][i]*A2[i][l] + A1[i][k]*A1[k][l]*A1[i][l]*A2[k][l]*A2[k][i]*A2[i][j] );    
          H[i][j] = H[i][j] + Q[k]*Q[l]*( A1[j][k]*A1[j][l]*A1[k][l]*A2[j][k]*A2[j][l]*A2[l][i] + A1[k][j]*A1[k][l]*A1[j][l]*A2[k][j]*A2[k][l]*A2[l][i] + A1[k][j]*A1[k][l]*A1[l][j]*A2[k][l]*A2[k][j]*A2[i][j] );    
       H[j][i]= H[i][j];
    return H
 
  def hp(self, M):
     return -1 

  def hm(self, M):
     return -1 





class c3star1edge(graphp):

  def __init__( self):    
      self.name = "c3star1edge" 
      
  def density(self, M):

    d = 0 ;
    if (r < 2):
       return d 

    A1 = M[0]
    A2 = M[1]
    Q = M[2]

    for x1 in range(m):
     for x2 in range(m):
      for x3 in range(m):
       d = d + A1[x1][x2] * A1[x1][x2] * A2[x1][x2] * Q[x1] * Q[x2];

    return d 


  def gradient(self, M):
      G = [ np.zeros((m,m)) for i in range(r) ]
      return G

  def gradientQ(self, M):
     G2 = np.zeros((m, m)); 
     return G2


  def hq(self, M):
     return -1 

  def hp(self, M):
     return -1 

  def hm(self, M):
     return -1 


  
 # Compute the distance how far Lagrange multiplier is satisfied i.e. sum_i [t[F_i,W]-stas_i]**2
def fstat(X):
 P = X[1 , m*(m+1)/2];
 Q = X[ m*(m+1)/2+1 , m*(m+1)/2+m-1 ];
 Q = [Q, 1-sum[Q]];
 beta = X[m*(m+1)/2+m:len(X)];
 D= rateI.gradient(M)
 #D=zeros[len(P) ]; 
 np=len(vecgraph);
 for i in range(np):
     D = D - beta[i]* vecgraph[i].gradient(M) 

 d = np.inner(D,D)**2 + penalty*distance(X);
 return d
 


 
 # Compute a penalized version for the rate function
def frate(X):
 P = X[1:m*(m+1)/2];
 Q = X [ m*(m+1)/2+1:m*(m+1)/2+m-1];
 Q = np.zeros( [Q, 1-sum(Q)] )
 d = ratefunction(M) + penalty*distance(X);
 return d
 


"""



mini = 2 ; Xini = 0 ; 
found,rerror,m,XF, obj,info, Xini1, Xini2, time1 = computeSolution[vecstat, Xini, mini]
 
#* t[K2,W] = 1/16
#* t[S3,W] = 0.064/64
#* t[S4,W] = 0.01637664/256 and where we want to find the smallest possible t[S2,W].
m=4; 
# vecgraph = ["edges", "3star", "4star"] ; 
# vecstat = [1/16; 0.064/64; 0.01637664/256 ]; 

 #runs[0.406, 0.99, "edges_triangle_2starAll.csv"]
 #checkIniValues[vecgraph ]
 #computeTime[vecgraph]




