# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.
"""

from flask import Flask
import numpy as np
import pandas as pd
import re
from sklearn.feature_extraction.text import TfidfVectorizer, CountVectorizer
from sklearn.multiclass import OneVsRestClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.svm import LinearSVC
from kneed import KneeLocator
from sklearn.cluster import KMeans
 
from keras import layers, models, optimizers
from keras.utils.np_utils import to_categorical
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
import string
import matplotlib.pyplot as plt
from scipy import sparse
from sklearn.feature_selection import SelectFromModel
from collections import Counter
from numpy import where
from imblearn.over_sampling import SMOTE


import imblearn

# Generate and plot a synthetic imbalanced classification dataset
from collections import Counter
from sklearn.datasets import make_classification
from matplotlib import pyplot
from numpy import where


	
# Generate and plot a synthetic imbalanced classification dataset
from collections import Counter
from sklearn.datasets import make_classification
from matplotlib import pyplot
from numpy import where

#print(imblearn.__version__)


nltk.download('stopwords')
nltk.download('wordnet')

spath = "C:/Users/jaaxx/Documents/Python/radix-challenge/"

app=Flask(__name__)
# load the  dataset
def loadDB(sfile):
   data = pd.read_csv(spath + sfile) 
   genres, synopsys = [], []
   for index, row in data.iterrows():
       genres.append((  row['genres'].split(" " ) ) ) 
       synopsys.append(row['synopsis'] )
                 
    # create a dataframe using texts and lables
   df = pd.DataFrame()
   df['text'] = synopsys
   df['genres'] = genres
       
   return  df 



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
    pre_processed_sentence = stemming(pre_processed_sentence) 
    # Use Lemmatize instead of stemming
    #pre_processed_sentence = lemmatize_words(pre_processed_sentence)
    pre_processed_sentence = remove_stop_words(pre_processed_sentence)
    
    return pre_processed_sentence

# function to check and get the part of speech tag count of a words in a given sentence
def check_pos_tag(x, flag):
    cnt = 0
    try:
        wiki = textblob.TextBlob(x)
        for tup in wiki.tags:
            ppo = list(tup)[1]
            if ppo in pos_family[flag]:
                cnt += 1
    except:
        pass
    return cnt


def getVocabulary(nWords):

  # Selection of the words for each gender
  # The selection is based on the word frequency and the uniqueness of word ins 
  """
   Nw : Number of documents in which appears the word w
   Ngw:  Numbet of documents of given gender in which appears the word w  
  """

  count_vectT = CountVectorizer(analyzer='word', token_pattern=r'\w{1,}', ngram_range=(1,1), binary=True )
  count_vectT.fit(  data['text2']   )
  xtrain_countT =  count_vectT.transform(  data['text2']   )
  
  aNw = xtrain_countT.sum(axis=0)
  lwords = []
  lcount = []
  for k,v in count_vectT.vocabulary_.items(): 
      lwords.append(k)
      lcount.append(aNw[0,v])        

  dffrecT = pd.DataFrame({ 'word':lwords, 'count':lcount })
    
  vocabulary = []
  for genre in genres_columns:
    count_vect = CountVectorizer(analyzer='word', token_pattern=r'\w{1,}')
    count_vect.fit(  data[data[genre]==1]['text2']   )
    xtrain_count =  count_vect.transform( data[data[genre]==1]['text2'] )

    aNgw = xtrain_count.sum(axis=0)
    lwords = []
    lcount = []
    for k,v in count_vect.vocabulary_.items(): 
      lwords.append(k)
      lcount.append(aNgw[0,v])        

    dffrec = pd.DataFrame({ 'word':lwords, 'count':lcount })
    df = pd.merge( dffrecT, dffrec, on = "word"  )
    df['scale'] = df['count_x'] /(df['count_x'] - df['count_y'] + 1) 
    df['tfidf']  = df['count_y'] *  df['scale'].apply(math.log )

    df = df.sort_values(by=['tfidf'], ascending=False  )

    setA = set(vocabulary )
    setB = set(list( df.iloc[0:nWords,0]))
    setBminusSetA = setB - setA

    vocabulary  = vocabulary + list(setBminusSetA)
  return vocabulary, xtrain_countT


pos_family = {
    'noun' : ['NN','NNS','NNP','NNPS'],
    'pron' : ['PRP','PRP$','WP','WP$'],
    'verb' : ['VB','VBD','VBG','VBN','VBP','VBZ'],
    'adj' :  ['JJ','JJR','JJS'],
    'adv' : ['RB','RBR','RBS','WRB']
}


def nlp_processing(datadf): 
    nlpdata = pd.DataFrame(  ) 
    nlpdata['text2'] = datadf['text'] .apply(text_preprocess)
    nlpdata['char_count'] = datadf['text'].apply(len)
    nlpdata['word_count'] = datadf['text'].apply(lambda x: len(x.split()))
    nlpdata['punctuation_count'] = datadf['text'].apply(lambda x: len("".join(_ for _ in x if _ in string.punctuation))) 
    nlpdata['title_word_count'] = datadf['text'].apply(lambda x: len([wrd for wrd in x.split() if wrd.istitle()]))
    nlpdata['upper_case_word_count'] = datadf['text'].apply(lambda x: len([wrd for wrd in x.split() if wrd.isupper()]))
    return nlpdata 

"""
    nlpdata['noun_count'] = datadf['text'].apply(lambda x: check_pos_tag(x, 'noun'))
    nlpdata['verb_count'] = datadf['text'].apply(lambda x: check_pos_tag(x, 'verb'))
    nlpdata['adj_count'] = datadf['text'].apply(lambda x: check_pos_tag(x, 'adj'))
    nlpdata['adv_count'] = datadf['text'].apply(lambda x: check_pos_tag(x, 'adv'))
    nlpdata['pron_count'] = datadf['text'].apply(lambda x: check_pos_tag(x, 'pron'))

    nlpdata['punctuation_density'] = nlpdata['punctuation_count'] / nlpdata['word_count']
    nlpdata['title_word_density'] = nlpdata['title_word_count'] / nlpdata['word_count']
    nlpdata['noun_density'] = nlpdata['noun_count'] / nlpdata['word_count']
    nlpdata['verb_density'] = nlpdata['verb_count'] / nlpdata['word_count']
    nlpdata['adj_density'] = nlpdata['adj_count'] / nlpdata['word_count']
    nlpdata['adv_density'] = nlpdata['adv_count'] / nlpdata['word_count']
    nlpdata['pron_density'] = nlpdata['pron_count'] / nlpdata['word_count']
    nlpdata['title_word_density2'] = nlpdata['title_word_count'] / nlpdata['punctuation_count']
    nlpdata['lenword_density'] = nlpdata['char_count'] / (nlpdata['word_count']+1)
"""



@app.route('/genres/train',methods=['POST'])
def train():
    i=0
    return i

@app.route('/genres/predict',methods=['POST'])
def predict():
    i=0
    return i


# Main Program 
data = loadDB("train.csv")



# Label extraction
encoder = preprocessing.LabelEncoder()
genres_columns = []
for row in data['genres']:
    for a in row: 
        if a not in genres_columns: 
           genres_columns.append(a)

genres_columns = sorted(genres_columns) 
ncols = len(genres_columns)

# Encoding of multiple genres into binary vectors 
labels = np.zeros( ( ncols, len(data)) ) 
encoder = preprocessing.LabelEncoder()
encoder.fit( genres_columns )
for j in range(len(data['genres'])):
    row = data.loc[j]['genres']
    x = encoder.transform(row)    
    for i in x:     
        labels[ i ][j] = 1

for i in range(ncols):
    data[ genres_columns[i]  ] = labels[i]

col_genres = data[genres_columns]


# Text Preprocessing
#data = pd.concat( [data, nlp_processing(data)] , axis = 1 ) 


#nlp_features = [ 'punctuation_density', 'title_word_density',                 'noun_density', 'verb_density', 'word_count',                'adj_density', 'adv_density', 'pron_density']
data['text2'] = data['text'] .apply(text_preprocess)




# Vocabultary computation for all genres
count_vectT = CountVectorizer(analyzer='word', token_pattern=r'\w{1,}', ngram_range=(1,1))
count_vectT.fit(  data['text2']   )
docwords =  count_vectT.transform(  data['text2']   )
  
vocabulary = count_vectT.vocabulary_
inv_vocabulary  = {v: k for k, v in vocabulary.items()}
  
#def get_genresvocabulary():

# Selection of the order of columns for the chain of classifiers
lselectedCol = ['IMAX']
while len(lselectedCol) < len(genres_columns):
  macc=0
  mcol = ''
  model_genres = LinearSVC(C=0.01, penalty="l1", dual=False)
  for c in genres_columns:
      if c not in lselectedCol:
         mfit = model_genres.fit( col_genres[ lselectedCol ]  , col_genres[ c ] )        
         y = mfit.predict( col_genres[ lselectedCol ]  )
         acc = metrics.accuracy_score(col_genres[ c ], y )
         if acc > macc:
            macc = acc
            mcol = c 
  print( mcol, macc )
  lselectedCol.append( mcol )


# Selection of vocabulary for each genre
genres_vocabulary = {}
for i in range(len( lselectedCol )):
  model = LinearSVC(C=0.1, penalty="l1", dual=False)
  if i == 0:
     lsvc = model.fit( docwords, col_genres[ lselectedCol[0] ] )
     selector = SelectFromModel(lsvc, prefit=True)
     X_new = selector.transform(docwords )
  else:
     col_genresa = np.array( col_genres[ lselectedCol[0:i] ]  )
     aa = sparse.csr_matrix( col_genresa ) 
     lsvc = model.fit( sparse.hstack( [docwords  , aa ]), col_genres[ lselectedCol[i] ] )
     # lsvc = model.fit( xtrain_countT, col_genres[ lselectedCol[i] ] )
     selector = SelectFromModel(lsvc, prefit=True)
     X_new = selector.transform( sparse.hstack( [docwords  , aa ]) )
  if len( X_new.nonzero()[0] ) >0: 
  # X_new.shape
    ones = np.ones( shape = X_new[0].shape )
    tt = selector.inverse_transform(ones)
    print( lselectedCol[i])
    vocabu = []
    for c in  tt.nonzero()[1]: 
        if c < docwords .shape[1]: 
           vocabu.append( inv_vocabulary[c] )
    genres_vocabulary[ lselectedCol[i] ] = vocabu 

# Compactification of docwords
train_x, valid_x, train_y, valid_y = model_selection.train_test_split( 
    docwords.todense(),  col_genres )

def Union(lst1, lst2): 
    final_list = list(set(lst1) | set(lst2)) 
    return final_list 

new_vocabulary = []
for k,v in genres_vocabulary.items():
    new_vocabulary = Union(new_vocabulary, v )


#model = LinearSVC(C=0.1, penalty="l1", dual=False)
#lsvc = model.fit( docwords  , col_genres.IMAX )
#selector = SelectFromModel(lsvc, prefit=True)
#X_new = selector.transform(docwords )
#print(X_new.shape)

count_vectT = CountVectorizer(analyzer='word', token_pattern=r'\w{1,}', vocabulary=new_vocabulary )
count_vectT.fit(  data['text2']   )
docwords =  count_vectT.transform(  data['text2']   )


# Selection of a vocabulary 
# vocabulary2, xtrain_countT = getVocabulary(1200)

# Feature extraction
#tfidf_vect = TfidfVectorizer(vocabulary = vocabulary2)
#tokenizer = lambda x: x.split(" "), sublinear_tf=False, ngram_range=(1,1) min_df=0.00009, smooth_idf=True, norm="l2", 
#tfidf_vect.fit(data['text2'])
#xtrain_tfidf =  tfidf_vect.transform(data['text2'])


# ngram level tf-idf 
# tfidf_vect_ngram = TfidfVectorizer(analyzer='word', token_pattern=r'\w{1,}', ngram_range=(2,3), vocabulary = vocabulary2)
# tfidf_vect_ngram.fit(data['text2'])
# xtrain_tfidf_ngram =  tfidf_vect_ngram.transform(data['text2'])

# characters level tf-idf
# tfidf_vect_ngram_chars = TfidfVectorizer(analyzer='char', token_pattern=r'\w{1,}', ngram_range=(2,3), vocabulary = vocabulary2)
# tfidf_vect_ngram_chars.fit(data['text2'])
# xtrain_tfidf_ngram_chars =  tfidf_vect_ngram.transform(data['text2'])


# Text modeling 
def BinaryRelevance_NN(input_size):
    # create input layer 
    input_layer = layers.Input((input_size, ), sparse=True)
    
    # create hidden layer
    nhidden = math.floor(input_size/2)
    nhidden = max(nhidden,1)
    nhidden = min(nhidden,100)
    
    hidden_layer = layers.Dense(nhidden, activation="relu")(input_layer)
    
    # create output layer
    output_layer = layers.Dense(1, activation="sigmoid")(hidden_layer)

    classifier = models.Model(inputs = input_layer, outputs = output_layer)
    classifier.compile(optimizer=optimizers.Adam(), loss='binary_crossentropy')
    return classifier 


# Split of data 
train_x, valid_x, train_y, valid_y = model_selection.train_test_split( 
    data['text2'],  col_genres )


# Train dataset preparation
#xx =  xtrain_tfidf_ngram.todense()
#xx =  xtrain_tfidf_ngram_chars.todense()

#xx = xtrain_tfidf.todense()
#xx2 = np.asarray(data[nlp_features])
#xx3 = np.concatenate((xx, xx2), axis=1)



# The model uses a  chain of shallow neural networks 
classifiers = [ ]
prev_trainpredictions = np.zeros((0,0))
prev_testpredictions = np.zeros((0,0))
y_prediction = np.zeros(shape=(0,0))
acc = {}
avg_acc = 0
tmovies = col_genres.sum()
totmoviesgenres = tmovies.sum()
for n in range( len(lselectedCol) ):
    c = lselectedCol[n]
    if c in genres_vocabulary:
       wordcounter = CountVectorizer(analyzer='word', token_pattern=r'\w{1,}', vocabulary=genres_vocabulary[c], binary=True )
    else:
       wordcounter = CountVectorizer(analyzer='word', token_pattern=r'\w{1,}', vocabulary=new_vocabulary, binary=True ) 

    wordcounter.fit( train_x )
    x_train = wordcounter.transform( train_x )
    x_valid = wordcounter.transform( valid_x )
    if n > 0:
        x_train = sparse.hstack( [ x_train , prev_trainpredictions ])
        x_valid = sparse.hstack( [ x_valid , prev_testpredictions ])
    # clf =  BinaryRelevance_NN(x_train.shape[1] )      

    clf = LogisticRegression(random_state=0, max_iter=100000).fit(x_train, train_y[ c ])
    #clf.fit(x_train.todense(), train_y[ c ] )
    
    predtest = clf.predict(x_valid.todense())
    prev_testpredictions =  pd.DataFrame(predtest).apply(round).values
    if n == 0:
       y_prediction = prev_testpredictions
    else:    
       y_prediction = np.concatenate( (y_prediction,  prev_testpredictions), axis = 1 )
    acc[c] = metrics.accuracy_score( valid_y[ c ],  prev_testpredictions   )
    avg_acc = avg_acc + tmovies[c] * acc[c] 

    predtrain = clf.predict(x_train.todense() )
    prev_trainpredictions = sparse.csr_matrix( pd.DataFrame(predtrain).apply(round) )

    print(lselectedCol[n] , acc[c])
    classifiers.append(clf)    


y_prediction = pd.DataFrame(y_prediction, columns = lselectedCol )
tt = pd.DataFrame( columns = genres_columns )
for c in genres_columns:
    tt[c] = y_prediction[c]
    
print( avg_acc / totmoviesgenres ) 
print( metrics.f1_score(  valid_y ,  tt , average= 'micro' )  ) 



# Generate and plot a synthetic imbalanced classification dataset

counter = Counter(Y2)
print(counter)


# define dataset
X, y = make_classification(n_samples=10000, n_features=2, n_redundant=0,
	n_clusters_per_class=1, weights=[0.99], flip_y=0, random_state=1)
# summarize class distribution
counter = Counter(valid_y[c])
print(counter)
# scatter plot of examples by class label
for label, _ in counter.items():
	row_ix = where(y == label)[0]
	pyplot.scatter(X[row_ix, 0], X[row_ix, 1], label=str(label))
pyplot.legend()
pyplot.show()















a =  1 - col_genres.sum() / len(col_genres)
for n in range( len(lselectedCol) ):    
    c = genres_columns[n]
    print(c,  acc[c] , a[c], acc[c] / a[c]) 






y_prediction.insert( 0, c, prev_testpredictions.transpose().tolist()  , True) 

#Training the model uses a  chain of shallow neural networks 
classifiers = []
predictBin = np.zeros( shape = (ncols,  len(valid_x) ) )
print('Training and prediction')
for c in range(ncols):
    cla =  BinaryRelevance_NN(train_x.shape[1] )      
    print(genres_columns[c])
    cla.fit( train_x, train_y[ genres_columns[c] ]  )
    predictBin[c] = list( cla.predict(valid_x) ) 
    classifiers.append( cla )           

    
# Estimatiop of the optimal threshold (hyperparameter)
scores = np.zeros( shape = (30,) )
for n in range(30):
    delta = 0.01 * n 
    y_predict = np.zeros( shape = (ncols, valid_y.shape[0])  )     
    for i in range(ncols):
        tt = pd.DataFrame( predictBin[i] ).apply(lambda  x: round(x+delta)) 
        y_predict[i] = list( tt[0] )

    #predictBin = predictBin.transpose()
    y_predict = y_predict.transpose()
    s1 = metrics.recall_score(valid_y, y_predict , average= 'micro')
    s2 = metrics.precision_score(valid_y, y_predict , average= 'micro')
    s3 = metrics.f1_score(valid_y, y_predict , average= 'micro')
    scores[n]  = s3 
    print('recall', delta, s1 )
    print('precision', delta, s2 )
    print('f1_score', delta, s3 )

tt = np.where(scores == scores.max() ) 
delta_opt=  tt[0][0] * 0.01
print( 'Max score ', tt[0][0],  scores[ tt[0][0] ]  ) 



# Load  test file
testDF = pd.read_csv(spath + 'test.csv') 
synopsys = []
for index, row in testDF.iterrows():   
    synopsys.append(row['synopsis'] )
                 
 # create a dataframe using texts and lables
testDF['text'] = synopsys
  

# Text preprocessing 
testDF = pd.concat( [testDF, nlp_processing(testDF)] , axis = 1 ) 

# Feature extraction
tfidf_test = TfidfVectorizer(vocabulary = vocabulary2)
#tokenizer = lambda x: x.split(" "), sublinear_tf=False, ngram_range=(1,1) min_df=0.00009, smooth_idf=True, norm="l2", 
tfidf_test.fit( testDF['text2'] )
xtest_tfidf =  tfidf_test.transform(testDF['text2'])

xx = xtest_tfidf.todense()
xx2 = np.asarray(testDF[nlp_features])
xtest = np.concatenate((xx, xx2), axis=1)

xx = xtrain_tfidf.todense()
xx2 = np.asarray(data[nlp_features])
train_x  = np.concatenate((xx, xx2), axis=1)

train_y = col_genres
classifiers = []
predictBin = np.zeros( shape = (ncols,  len(xtest) ) )
print('Training total model and prediction')
for c in range(ncols):
    cla =  BinaryRelevance_NN(train_x.shape[1] )      
    print(genres_columns[c])
    # Training with the total dataset
    cla.fit( train_x, train_y[ genres_columns[c] ]  )
    predictBin[c] = list( cla.predict(  xtest ) )
    classifiers.append( cla )           



y_predict = np.zeros( shape = (ncols,  len(xtest) ) )
for i in range(ncols):
   tt = pd.DataFrame( predictBin[i] ).apply(lambda  x: round(x+delta_opt)) 
   y_predict[i] = list( tt[0] )

y_predict = y_predict.transpose()
predictBin = predictBin.transpose()
for i in range(len(y_predict)):
    if y_predict[i].sum() == 0: 
       k = np.where( predictBin[i] == predictBin[i].max() )[0][0]
       y_predict[i][k] = 1
predictBin = predictBin.transpose()    





def genres_descriptions(x):
    s= ""
    if x[0] == 1:
       s =  genres_columns[ 0 ]
    for i in range(1, len(genres_columns )):
        if x[i] == 1:
            s = s + ' ' + genres_columns[ i ]
    return s.strip()

tt = pd.DataFrame( y_predict, columns = genres_columns )
testDF['predicted_genres'] =  tt.apply( genres_descriptions , axis=1)


testDF = testDF[ [ 'movie_id','predicted_genres'] ] 
testDF.to_csv( spath + 'testpredict.csv') 


testDF2 = pd.read_csv(spath + 'test2.csv') 

tt = testDF2 [ testDF2.predicted_genres.notnull() ] 
df = tt[ ['movie_id', 'predicted_genres'] ]


lgenres = []
for i in range(len(testDF)):
    id = testDF.movie_id[i]
    s = df[ df.movie_id == id ].predicted_genres.values
    if len(s) >0 and len(s[0].strip()) >0:
       lgenres.append(s[0].strip())
    else: 
       lgenres.append(testDF.predicted_genres[i]) 

testDF['predicted_genres'] = lgenres
testDF = testDF[ [ 'movie_id','predicted_genres'] ] 
testDF.to_csv( spath + 'testpredict.csv') 







