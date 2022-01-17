# -*- coding: utf-8 -*-
"""
Created on Sun Feb 21 08:47:55 2021

@author: jaaxx
"""

# Ignore  the warnings
import warnings
warnings.filterwarnings('always')
warnings.filterwarnings('ignore')

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib import style
import seaborn as sns
import missingno as msno
import re
import os.path
import math
from sklearn import model_selection, preprocessing, linear_model, naive_bayes, metrics, svm
from sklearn.model_selection import StratifiedShuffleSplit, KFold
from sklearn.naive_bayes import MultinomialNB
from sklearn.pipeline import Pipeline
from sklearn.svm import LinearSVC
from sklearn.svm import SVC
from sklearn.multiclass import OneVsRestClassifier
from sklearn.metrics import classification_report
from sklearn.model_selection import GridSearchCV
from scipy import sparse
from sklearn.metrics import make_scorer
from sklearn.metrics import f1_score

import nltk
from wordcloud import WordCloud
from nltk.stem.snowball import SnowballStemmer
from nltk.stem import WordNetLemmatizer
from nltk.corpus import stopwords
import unicodedata

from sklearn.feature_extraction.text import TfidfVectorizer


nltk.download('stopwords')
nltk.download('wordnet')

#plt.style.use('fivethirtyeight')
sns.set_style("whitegrid")
sns.set_context("talk", font_scale=0.8)


def remove_tags(sentence):
    html_tag = '<.*?>'
    cleaned_sentence = re.sub(html_tag, ' ',  sentence)
    return cleaned_sentence

def remove_accents(sentence):
    cleaned_sentence = unicodedata.normalize('NFD', sentence)
    cleaned_sentence = cleaned_sentence.encode('ascii', 'ignore')
    cleaned_sentence = cleaned_sentence.decode("utf-8")
    return cleaned_sentence

def remove_punctuation(sentence):
    cleaned_sentence = re.sub(r'[?|!|\'|"|#]', '', sentence)
    cleaned_sentence = re.sub(r'[,|.|;|:|(|)|{|}|\|/|<|>]|-', ' ', cleaned_sentence)
    cleaned_sentence = cleaned_sentence.replace("\n"," ")
    return cleaned_sentence

def keep_alpha(sentence):
    alpha_sentence = re.sub('[^a-z A-Z]+', ' ', sentence)
    return alpha_sentence

def lower_case(sentence):
    lower_case_sentence = sentence.lower()
    return lower_case_sentence

def stemming(sentence):
    stemmer = SnowballStemmer("english")
    stemmed_words = [stemmer.stem(word) for word in sentence.split()]
    stemmed_sentence=' '.join(stemmed_words)
    return stemmed_sentence

def lemmatize_words(sentence):
    lem = WordNetLemmatizer()
    lemmatized_words = [lem.lemmatize(word, 'v') for word in sentence.split()]
    lemmatized_sentence=' '.join(lemmatized_words)
    return lemmatized_sentence

def remove_stop_words(sentence):
    stop_words = set(stopwords.words('english'))
    stop_words.update(['zero','one','two','three','four','five','six','seven','eight','nine','ten',
                       'may','also','across','among','beside','however','yet','within'])
    no_stop_words=[word for word in sentence.split() if word not in stop_words]
    no_step_sentence = ' '.join(no_stop_words)
    return no_step_sentence

def text_preprocess(sentence):
    pre_processed_sentence = remove_tags(sentence)
    pre_processed_sentence = remove_accents(pre_processed_sentence)
    pre_processed_sentence = remove_punctuation(pre_processed_sentence)
    pre_processed_sentence = keep_alpha(pre_processed_sentence)
    pre_processed_sentence = lower_case(pre_processed_sentence)
    #pre_processed_sentence = stemming(pre_processed_sentence) # Use Lemmatize instead of stemming
    pre_processed_sentence = lemmatize_words(pre_processed_sentence)
    pre_processed_sentence = remove_stop_words(pre_processed_sentence)
    
    return pre_processed_sentence




 # Creation list of genres
def getListgenres(genres):

  listgenres = []
  for a in genres: 
      for b in a: 
         if not b in listgenres:
            listgenres.append(b)
  listgenres = sorted(listgenres)


  # Creation list of labels for genres
  le = preprocessing.LabelEncoder()
  le.fit( listgenres )
  arrgenres= np.zeros( ( len(listgenres) , len(genres)  )) 
  i = 0
  for a in genres: 
      x = le.transform(a)
      for j in x:     
         arrgenres[j][i] = 1
      i = i + 1

  return listgenres, arrgenres


# load the dataset
def loadDF(sfile):
   data = pd.read_csv(sfile) 
   genres, synopsys = [], []
   for index, row in data.iterrows():
       genres.append((  row['genres'].split(" " ) ) ) 
       synopsys.append(row['synopsis'] )
                 
    # create a dataframe using texts and lables
   DF = pd.DataFrame()
   DF['text'] = synopsys

   listgenres, arrgenres = getListgenres(genres)
   for i in range(len(listgenres)): 
       DF[listgenres[i]] = arrgenres[i]

   return  DF 

trainDF = loadDF("train.csv")

print('Shape of data: ', trainDF.shape)

#trainDF.info()
#msno.matrix(trainDF)

genres_columns = trainDF.columns.drop(['text'])
sum_genre = trainDF[genres_columns].sum()
df_movies_per_genre = pd.DataFrame({'Genre':genres_columns, 'Total': sum_genre})


trainDF['text'] = trainDF['text'] .apply(text_preprocess)
#trainDF.to_csv('cleancsv.csv', index=False)



fig = plt.figure(figsize=(18, 70))
num_cols = 2
num_rows = math.ceil(len(genres_columns)/num_cols)
for idx, col in enumerate(genres_columns):
    wordcloud = WordCloud(max_font_size=50).generate(' '.join(trainDF[trainDF[col]==1]['text']))
    ax = fig.add_subplot(num_rows, num_cols, idx+1)
    ax.imshow(wordcloud)
    ax.axis("off")
    ax.set(title='Movie Genre: {0}'.format(col))
plt.show()


# Heatmap relative to all numeric columns
corr_matrix = (trainDF[genres_columns].astype('int')).corr()
mask = np.array(corr_matrix)
mask[np.tril_indices_from(mask)] = False
corr_matrix = (100*corr_matrix//1)/10

fig = plt.figure(figsize=(20, 20))
sns.heatmap(corr_matrix, mask=mask, annot=True, cbar=True, vmax=7, vmin=-7, cmap='RdYlGn')
plt.show()



def save_genre_pdf_given_genre_plots(data, labels):
    for idx, col in enumerate(labels):
        df_sum_given_genre = pd.DataFrame({'Genre':labels, 'Total': data[data[col]==1][labels].sum()})
        df_sum_given_genre = df_sum_given_genre.sort_values('Total', ascending=False).head(10)
        df_sum_given_genre['Total'] = df_sum_given_genre['Total']/df_sum_given_genre['Total'].max()
        ax = plt.figure(figsize=(9, 9)).add_subplot(1, 1, 1)
        sns.barplot(data=df_sum_given_genre, x='Genre', y='Total', axes=ax)
        ax.set(title='Distribution given {0} Genre'.format(col), xlabel='', ylabel='Normalized Number of Movies')
        plt.xticks(rotation=90)
        plt.savefig('./Images/results/genre_pdf_given_{0}.png'.format(col), bbox_inches='tight')
        plt.close()
        
def save_genre_pdf_given_genre_subplots(data, labels):
    num_plot = 3
    fig_per_plot = math.ceil(len(genres_columns)/num_plot)
    num_cols = 3
    num_rows = math.ceil(fig_per_plot/num_cols)
    for idx, col in enumerate(genres_columns):
        if idx%fig_per_plot==0:
            fig = plt.figure(figsize=(20, 30))
        df_sum_given_genre = pd.DataFrame({'Genre':labels, 'Total': data[data[col]==1][labels].sum()})
        df_sum_given_genre = df_sum_given_genre.sort_values('Total', ascending=False).head(10)
        df_sum_given_genre['Total'] = df_sum_given_genre['Total']/df_sum_given_genre['Total'].max()
        ax = fig.add_subplot(num_rows, num_cols, idx%fig_per_plot+1)
        sns.barplot(data=df_sum_given_genre, x='Genre', y='Total', axes=ax)
        ax.set(title='Distribution given {0} Genre'.format(col), xlabel='', ylabel='Normalized Number of Movies')
        plt.xticks(rotation=90)
        if (idx+1)%fig_per_plot==0 or idx==len(genres_columns)-1:
            plt.savefig('./Images/results/genre_pdf_part{0}.png'.format(1+idx//fig_per_plot), bbox_inches='tight')
            plt.close()
            fig = plt.figure(figsize=(20, 30))
            
            
            

fig = plt.figure(figsize=(18, 60))
num_cols = 3
num_rows = math.ceil(len(genres_columns)/num_cols)
for idx, col in enumerate(genres_columns):
    df_sum_given_genre = pd.DataFrame({'Genre':genres_columns, 'Total': trainDF[trainDF[col]==1][genres_columns].sum()})
    df_sum_given_genre = df_sum_given_genre.sort_values('Total', ascending=False).head(10)
    df_sum_given_genre['Total'] = df_sum_given_genre['Total']/df_sum_given_genre['Total'].max()
    ax = fig.add_subplot(num_rows, num_cols, idx+1)
    sns.barplot(data=df_sum_given_genre, x='Genre', y='Total', axes=ax)
    ax.set(title='Distribution given {0} Genre'.format(col), xlabel='', ylabel='Normalized Number of Movies')
    plt.xticks(rotation=90)

plt.tight_layout()
plt.show()


def save_numGenre_pdf_given_genre_plots(data, labels):
    for idx, col in enumerate(labels):
        df_genres_per_movie = pd.DataFrame({'Total': data[data[col]==1][labels].sum(axis=1)})
        df_numG_given_genre = pd.DataFrame(df_genres_per_movie['Total'].value_counts().sort_index().head(10))
        df_numG_given_genre['Total'] = df_numG_given_genre['Total']/df_numG_given_genre['Total'].max()
        ax = plt.figure(figsize=(9, 9)).add_subplot(1, 1, 1)
        sns.barplot(data=df_numG_given_genre, x=df_numG_given_genre.index, y='Total', axes=ax)
        ax.set(title='Number of Genres distribution given {0} Genre'.format(col), xlabel='', ylabel='Normalized Number of Movies')
        plt.savefig('./Images/results/numGenre_pdf_given_{0}.png'.format(col), bbox_inches='tight')
        plt.close()
        
def save_numGenre_pdf_given_genre_subplots(data, labels):
    num_plot = 3
    fig_per_plot = math.ceil(len(genres_columns)/num_plot)
    num_cols = 3
    num_rows = math.ceil(fig_per_plot/num_cols)
    for idx, col in enumerate(genres_columns):
        if idx%fig_per_plot==0:
            fig = plt.figure(figsize=(20, 30))
        df_genres_per_movie = pd.DataFrame({'Total': data[data[col]==1][labels].sum(axis=1)})
        df_numG_given_genre = pd.DataFrame(df_genres_per_movie['Total'].value_counts().sort_index().head(10))
        df_numG_given_genre['Total'] = df_numG_given_genre['Total']/df_numG_given_genre['Total'].max()
        ax = fig.add_subplot(num_rows, num_cols, idx%fig_per_plot+1)
        sns.barplot(data=df_numG_given_genre, x=df_numG_given_genre.index, y='Total', axes=ax)
        ax.set(title='Number of Genres distribution given {0} Genre'.format(col), xlabel='', ylabel='Normalized Number of Movies')
        if (idx+1)%fig_per_plot==0 or idx==len(genres_columns)-1:
            plt.savefig('./Images/results/numGenre_pdf_part{0}.png'.format(1+idx//fig_per_plot), bbox_inches='tight')
            plt.close()
            fig = plt.figure(figsize=(20, 30))       
            
            

fig = plt.figure(figsize=(18, 50))
num_cols = 3
num_rows = math.ceil(len(genres_columns)/num_cols)
for idx, col in enumerate(genres_columns):
    df_genres_per_movie = pd.DataFrame({'Total': trainDF[trainDF[col]==1][genres_columns].sum(axis=1)})
    df_numG_given_genre = pd.DataFrame(df_genres_per_movie['Total'].value_counts().sort_index().head(10))
    df_numG_given_genre['Total'] = df_numG_given_genre['Total']/df_numG_given_genre['Total'].max()
    ax = fig.add_subplot(num_rows, num_cols, idx+1)
    sns.barplot(data=df_numG_given_genre, x=df_numG_given_genre.index, y='Total', axes=ax)
    ax.set(title='Number of Genres distribution given {0} Genre'.format(col), xlabel='', ylabel='Normalized Number of Movies')

plt.tight_layout()
plt.show()

    

        