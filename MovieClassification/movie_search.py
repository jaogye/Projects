# -*- coding: utf-8 -*-
"""
Created on Wed Mar  3 11:16:49 2021

@author: jaaxx
"""

# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.
"""


import numpy as np
import pandas as pd
import re
from sklearn.feature_extraction.text import TfidfVectorizer, CountVectorizer
import math

import nltk
from nltk.stem.snowball import SnowballStemmer
from nltk.stem import WordNetLemmatizer
from nltk.corpus import stopwords
import unicodedata
from sklearn import model_selection
from sklearn import preprocessing 
from sklearn import metrics 
import textblob 
import strin
import json g

#p = re.compile('(?<!\\\\)\'')
#ss = p.sub('\"', ss)


spath = "C:/Users/jaaxx/Documents/Python/radix-challenge/"

data = pd.read_csv(spath + 'movies_metadata.csv') 
test = pd.read_csv(spath + 'test.csv') 
data = data[ ['overview','genres'] ]



ind = (data["overview"].str.find('trounced', 0) != -1) & (data["overview"].str.find('IBM', 0) != -1) & (data["overview"].notnull() )
df = data.loc[ ind ] 
ss = df['genres'].tolist()[0]


lgenres = []
for s in data['genres'].tolist():
    s2 = p.sub('\"',  s)
    lgenres.append(s2)

data.to_csv(spath + 'movies_metadata.csv') 

data['genres'] = lgenres
data.loc[:] = lgenres


dict0 = {}
dict0[ 'Action'] ='Action'
dict0[ 'Adventure'] = 'Adventure'
dict0[ 'Animation'] = 'Animation'
dict0[ 'Comedy'] = 'Comedy'
dict0[ 'Crime'] = 'Crime'
dict0['Documentary'] = 'Documentary'
dict0[ 'Drama'] = 'Drama'
dict0[ 'Fantasy'] = 'Fantasy'
dict0[ 'Horror'] = 'Horror'
dict0[ 'Thriller'] = 'Thriller'
dict0[ 'Foreign'] = ''
dict0[ 'Family'] = 'Children'
dict0[ 'Music'] = 'Musical'
dict0[ 'Mystery'] = 'Mystery'
dict0[ 'Science Fiction'] = 'Sci-Fi'
dict0[ 'History'] = ''
dict0[ 'Romance'] = 'Romance'
dict0[ 'War'] = 'War'
dict0[ 'Western'] = 'Western'
dict0[ 'TV Movie'] = ''


l2genres = []
llgenres = []
lmore1 = []
nn = 0 
for plot in test['synopsis']:
    ss = plot[0:59]
    ind = (data["overview"].str.find(ss, 0) != -1) &  (data["overview"].notnull() )
    df = data.loc[ ind ]
    print(len(df) )
    if len(df) > 0:
      text = df['genres'].tolist()[0]
      lgenres = json.loads( text ) 
      names = ''
      for dd in lgenres:  
          if dict0[ dd['name'] ] != '': 
             names = names + dict0[ dd['name'] ] + ' '
      llgenres.append(names.strip() )
    else: 
      llgenres.append('')
      nn += 1
          

genres_columns = ['Action','Adventure','Animation','Children',
      'Comedy','Crime','Documentary','Drama','Fantasy',
      'Film-Noir','Horror','IMAX','Musical','Mystery',
      'Romance','Sci-Fi','Thriller','War','Western']

# Encoding of multiple genres into binary vectors 
labels = np.zeros( ( len(genres_columns), len(test)) ) 
encoder = preprocessing.LabelEncoder()
encoder.fit( genres_columns )
for j in range( len(test['genres'])):
    row = test.loc[j]['genres']
    row = row.strip()
    row = row.replace('  ',' ')
    if row != '':
       lrow = row.split(' ' ) 
       x = encoder.transform(lrow)
       for i in x:     
           labels[ i ][j] = 1

for i in range(len(genres_columns)):
    test[ genres_columns[i]  ] = labels[i]

test.to_csv( spath + 'test2.csv' )




