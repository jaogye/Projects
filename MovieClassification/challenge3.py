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
from keras.utils.np_utils import to_categorical

from keras import layers, models, optimizers
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
  """
   Nw : Number of documents in which appears the word w
   Ngw:  Numbet of documents of given gender in which appears the word w  
  """

  count_vectT = CountVectorizer(analyzer='word', token_pattern=r'\w{1,}')
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
  return vocabulary



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

# Encoding of multiple genres into binary vectors 
labels = np.zeros( ( len(genres_columns), len(data)) ) 
encoder = preprocessing.LabelEncoder()
encoder.fit( genres_columns )
for j in range(len(data['genres'])):
    row = data.loc[j]['genres']
    x = encoder.transform(row)    
    for i in x:     
        labels[ i ][j] = 1

for i in range(len(genres_columns)):
    data[ genres_columns[i]  ] = labels[i]



# Text Preprocessing
pos_family = {
    'noun' : ['NN','NNS','NNP','NNPS'],
    'pron' : ['PRP','PRP$','WP','WP$'],
    'verb' : ['VB','VBD','VBG','VBN','VBP','VBZ'],
    'adj' :  ['JJ','JJR','JJS'],
    'adv' : ['RB','RBR','RBS','WRB']
}


data['text2'] = data['text'] .apply(text_preprocess)

data['char_count'] = data['text'].apply(len)
data['word_count'] = data['text'].apply(lambda x: len(x.split()))
data['punctuation_count'] = data['text'].apply(lambda x: len("".join(_ for _ in x if _ in string.punctuation))) 
data['title_word_count'] = data['text'].apply(lambda x: len([wrd for wrd in x.split() if wrd.istitle()]))
data['upper_case_word_count'] = data['text'].apply(lambda x: len([wrd for wrd in x.split() if wrd.isupper()]))


data['noun_count'] = data['text'].apply(lambda x: check_pos_tag(x, 'noun'))
data['verb_count'] = data['text'].apply(lambda x: check_pos_tag(x, 'verb'))
data['adj_count'] = data['text'].apply(lambda x: check_pos_tag(x, 'adj'))
data['adv_count'] = data['text'].apply(lambda x: check_pos_tag(x, 'adv'))
data['pron_count'] = data['text'].apply(lambda x: check_pos_tag(x, 'pron'))


data['punctuation_density'] = data['punctuation_count'] / data['word_count']
data['title_word_density'] = data['title_word_count'] / data['word_count']
data['noun_density'] = data['noun_count'] / data['word_count']
data['verb_density'] = data['verb_count'] / data['word_count']
data['adj_density'] = data['adj_count'] / data['word_count']
data['adv_density'] = data['adv_count'] / data['word_count']
data['pron_density'] = data['pron_count'] / data['word_count']
data['title_word_density2'] = data['title_word_count'] / data['punctuation_count']
data['lenword_density'] = data['char_count'] / (data['word_count']+1)

nlp_features = [ 'punctuation_density', 'title_word_density', 
                'noun_density', 'verb_density', 'word_count',
                'adj_density', 'adv_density', 'pron_density']


# Selection of a vocabulary 
vocabulary2 = getVocabulary(1200)

# Feature extraction
tfidf_vect = TfidfVectorizer(vocabulary = vocabulary2)
#tokenizer = lambda x: x.split(" "), sublinear_tf=False, ngram_range=(1,1) min_df=0.00009, smooth_idf=True, norm="l2", 
tfidf_vect.fit(data['text2'])
xtrain_tfidf =  tfidf_vect.transform(data['text2'])


# ngram level tf-idf 
tfidf_vect_ngram = TfidfVectorizer(analyzer='word', token_pattern=r'\w{1,}', ngram_range=(2,3), vocabulary = vocabulary2)
tfidf_vect_ngram.fit(data['text2'])
xtrain_tfidf_ngram =  tfidf_vect_ngram.transform(data['text2'])

# characters level tf-idf
tfidf_vect_ngram_chars = TfidfVectorizer(analyzer='char', token_pattern=r'\w{1,}', ngram_range=(2,3), vocabulary = vocabulary2)
tfidf_vect_ngram_chars.fit(data['text2'])
xtrain_tfidf_ngram_chars =  tfidf_vect_ngram.transform(data['text2'])


def create_model_architecture(input_size):
    # create input layer 
    input_layer = layers.Input((input_size, ), sparse=True)
    
    # create hidden layer
    hidden_layer = layers.Dense(100, activation="gelu")(input_layer)
    
    # create output layer
    output_layer = layers.Dense(1, activation="sigmoid")(hidden_layer)

    classifier = models.Model(inputs = input_layer, outputs = output_layer)
    classifier.compile(optimizer=optimizers.Adam(), loss='binary_crossentropy')
    return classifier 



# xx = xtrain_tfidf.todense()
# xx = xtrain_tfidf_ngram.todense()

xx = xtrain_tfidf_ngram.todense()
xx2 = np.asarray(data[nlp_features])
xx3 = np.concatenate((xx, xx2), axis=1)

train_x, valid_x, train_y, valid_y = model_selection.train_test_split(xtrain_tfidf.todense(), data[genres_columns])

# The model uses a  chain of shallow neural networks 
classifiers = []
label = np.zeros((0,0))
for n in range(len(genres_columns)):

    clf =  create_model_architecture(train_x.shape[1] )      
    clf.fit(train_x, train_y[ genres_columns[n]  ] )
    predictions = clf.predict(train_x)
    label = pd.DataFrame(predictions).apply(round)

    print(genres_columns[n] )
    classifiers.append(clf)    


    
# Computation of f1_score
accuracy = 0 
genremovies = 0
labeldf = pd.DataFrame()
label = np.zeros((0,0))
predictiondf = pd.DataFrame()
for i in range(len(genres_columns)):

    predictions = classifiers[i].predict(valid_x)
    label = pd.DataFrame(predictions).apply( lambda x:  round(x))
    nmovies = valid_y[ genres_columns[i] ].sum()
    genremovies = genremovies + nmovies
    sc1 = metrics.accuracy_score(label, valid_y[ genres_columns[i] ])
    labeldf[ genres_columns[i] ] = list( pd.DataFrame(predictions).apply(round)[0] )

    print(genres_columns[i], sc1)
    accuracy = accuracy + sc1*nmovies
    
    
print('Average accuracy', accuracy/ genremovies) 
print('f1 score (micro)', metrics.f1_score( valid_y, labeldf,  average='micro' ) ) 
print('Precision score (micro)', metrics.precision_score(valid_y, labeldf, average='micro') ) 
print('Recall score (micro)', metrics.recall_score(valid_y, labeldf, average='micro') ) 



# Power set label 
powersetlabel = pd.DataFrame( data[genres_columns].drop_duplicates() )
powersetlabel['id'] = range(len(powersetlabel))
df = pd.merge(data, powersetlabel, on = genres_columns)

df = pd.DataFrame( df.groupby(by='id').count()['text'])
powersetlabel['cnt'] = list( df['text'] ) 
powersetlabel = pd.DataFrame( powersetlabel.sort_values('cnt',ascending= False) ) 

powersetlabel['multiclass'] = -1
ncols = len(powersetlabel.columns)
nclass = 50
for i in range(len(powersetlabel)):
      if i < nclass:
         powersetlabel.iloc[i,ncols-1] = i    
      else:
         powersetlabel.iloc[i,ncols-1] = nclass

nclass=50
data = pd.merge(data, powersetlabel, left_on = genres_columns, right_on = genres_columns)
y_train = to_categorical(y=data['multiclass'], num_classes=nclass+1)         



y_train = to_categorical(y=kmeans.labels_, num_classes=kk)         

xx = xtrain_tfidf_ngram.todense()
xx2 = np.asarray(data[nlp_features])
xx3 = np.concatenate((xx, xx2), axis=1)


train_x, valid_x, train_y, valid_y, train_z, valid_z = model_selection.train_test_split( 
    xtrain_tfidf.todense(), y_train, data[genres_columns] , stratify=kmeans.labels_)


def PowersetLabel_architecture(input_size, nclasses):
    # create input layer 
    input_layer = layers.Input((input_size, ), sparse=True)
    
    # create hidden layer
    hidden_layer = layers.Dense(100, activation="relu")(input_layer)
    
    # create output layer
    output_layer = layers.Dense(nclasses, activation="softmax")(hidden_layer)

    classifier = models.Model(inputs = input_layer, outputs = output_layer)
    classifier.compile(optimizer=optimizers.Adam(), loss='categorical_crossentropy')
    return classifier 

# Power set label 


kmeans = KMeans(   init="random",
    n_clusters=3,  n_init=10, max_iter=300, random_state=42)

kmeans_kwargs = {"init": "random", "n_init": 10,"max_iter": 300,
 "random_state": 42,}

kk = 25
kmeans = KMeans(n_clusters=kk, **kmeans_kwargs)
kmeans.fit(col_genres)
unique_elements, counts_elements = np.unique(kmeans.labels_, return_counts=True)

res = np.where(counts_elements == 1)

while len(res[0]) >0:
   todrop = []
   for i in res: 
       for j in res[0]: 
          todrop.append( np.where(kmeans.labels_== j)[0][0] )

   data = data.drop( todrop )
   col_genres = data[genres_columns]
   kmeans.fit(col_genres)
   unique_elements, counts_elements = np.unique(kmeans.labels_, return_counts=True)
   res = np.where(counts_elements == 1)
