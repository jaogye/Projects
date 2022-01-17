# -*- coding: utf-8 -*-
"""
Created on Sat Feb 20 21:26:33 2021

@author: https://www.analyticsvidhya.com/blog/2019/04/predicting-movie-genres-nlp-multi-label-classification/
"""

import pandas as pd
import numpy as np
import json
import nltk
import re
import csv
import matplotlib.pyplot as plt 
import seaborn as sns
from tqdm import tqdm
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.model_selection import train_test_split

#matplotlib inline
pd.set_option('display.max_colwidth', 300)

meta = pd.read_csv("MovieSummaries/movie.metadata.tsv", sep = '\t', header = None)
meta.head()


# rename columns
meta.columns = ["movie_id",1,"movie_name",3,4,5,6,7,"genre"]

plots = []

with open("MovieSummaries/plot_summaries.txt", 'r',   encoding="utf8") as f:
       reader = csv.reader(f, dialect='excel-tab') 
       for row in tqdm(reader):
            plots.append(row)
            
            
movie_id = []
plot = []

# extract movie Ids and plot summaries
for i in tqdm(plots):
  movie_id.append(i[0])
  plot.append(i[1])

# create dataframe
movies = pd.DataFrame({'movie_id': movie_id, 'plot': plot})            
            

movies.head()

# change datatype of 'movie_id'
meta['movie_id'] = meta['movie_id'].astype(str)

# merge meta with movies
movies = pd.merge(movies, meta[['movie_id', 'movie_name', 'genre']], on = 'movie_id')

movies.head()

movies['genre'][0]

# an empty list
genres = [] 

# extract genres
for i in movies['genre']: 
  genres.append(list(json.loads(i).values())) 

# add to 'movies' dataframe  
movies['genre_new'] = genres


# remove samples with 0 genre tags
movies_new = movies[~(movies['genre_new'].str.len() == 0)]

movies_new.shape, movies.shape



