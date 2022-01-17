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
  count_vectT.fit(  trainDF['text2']   )
  xtrain_countT =  count_vectT.transform(  trainDF['text2']   )

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
    count_vect.fit(  trainDF[trainDF[genre]==1]['text2']   )
    xtrain_count =  count_vect.transform( trainDF[trainDF[genre]==1]['text2'] )

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
trainDF = loadDB("train.csv")

# Label extraction
encoder = preprocessing.LabelEncoder()
genres_columns = []
for row in trainDF['genres']:
    for a in row: 
        if a not in genres_columns: 
           genres_columns.append(a)

genres_columns = sorted(genres_columns) 

# Encoding of multiple genres into binary vectors 
labels = np.zeros( ( len(genres_columns), len(trainDF)) ) 
encoder = preprocessing.LabelEncoder()
encoder.fit( genres_columns )
for j in range(len(trainDF['genres'])):
    row = trainDF.loc[j]['genres']
    x = encoder.transform(row)    
    for i in x:     
        labels[ i ][j] = 1

for i in range(len(genres_columns)):
    trainDF[ genres_columns[i]  ] = labels[i]



# Text Preprocessing
pos_family = {
    'noun' : ['NN','NNS','NNP','NNPS'],
    'pron' : ['PRP','PRP$','WP','WP$'],
    'verb' : ['VB','VBD','VBG','VBN','VBP','VBZ'],
    'adj' :  ['JJ','JJR','JJS'],
    'adv' : ['RB','RBR','RBS','WRB']
}


trainDF['text2'] = trainDF['text'] .apply(text_preprocess)

trainDF['char_count'] = trainDF['text'].apply(len)
trainDF['word_count'] = trainDF['text'].apply(lambda x: len(x.split()))
trainDF['punctuation_count'] = trainDF['text'].apply(lambda x: len("".join(_ for _ in x if _ in string.punctuation))) 
trainDF['title_word_count'] = trainDF['text'].apply(lambda x: len([wrd for wrd in x.split() if wrd.istitle()]))
trainDF['upper_case_word_count'] = trainDF['text'].apply(lambda x: len([wrd for wrd in x.split() if wrd.isupper()]))



trainDF['noun_count'] = trainDF['text'].apply(lambda x: check_pos_tag(x, 'noun'))
trainDF['verb_count'] = trainDF['text'].apply(lambda x: check_pos_tag(x, 'verb'))
trainDF['adj_count'] = trainDF['text'].apply(lambda x: check_pos_tag(x, 'adj'))
trainDF['adv_count'] = trainDF['text'].apply(lambda x: check_pos_tag(x, 'adv'))
trainDF['pron_count'] = trainDF['text'].apply(lambda x: check_pos_tag(x, 'pron'))


trainDF['punctuation_density'] = trainDF['punctuation_count'] / trainDF['word_count']
trainDF['title_word_density'] = trainDF['title_word_count'] / trainDF['word_count']
trainDF['noun_density'] = trainDF['noun_count'] / trainDF['word_count']
trainDF['verb_density'] = trainDF['verb_count'] / trainDF['word_count']
trainDF['adj_density'] = trainDF['adj_count'] / trainDF['word_count']
trainDF['adv_density'] = trainDF['adv_count'] / trainDF['word_count']
trainDF['pron_density'] = trainDF['pron_count'] / trainDF['word_count']
trainDF['title_word_density2'] = trainDF['title_word_count'] / trainDF['punctuation_count']
trainDF['lenword_density'] = trainDF['char_count'] / (trainDF['word_count']+1)

nlp_features = [ 'punctuation_density', 'title_word_density', 
                'noun_density', 'verb_density', 
                'adj_density', 'adv_density', 'pron_density']


# Selection of a vocabulary 
vocabulary2 = getVocabulary(1200)

# Feature extraction
tfidf_vect = TfidfVectorizer(vocabulary = vocabulary2)
#tokenizer = lambda x: x.split(" "), sublinear_tf=False, ngram_range=(1,1) min_df=0.00009, smooth_idf=True, norm="l2", 
tfidf_vect.fit(trainDF['text2'])
xtrain_tfidf =  tfidf_vect.transform(trainDF['text2'])


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



xx = xtrain_tfidf.todense()
xx2 = np.asarray(trainDF[nlp_features])
xx3 = np.concatenate((xx, xx2), axis=1)

train_x, valid_x, train_y, valid_y = model_selection.train_test_split(xtrain_tfidf.todense(), trainDF[genres_columns])

# The model uses a  chain of shallow neural networks 
classifiers = []
label = np.zeros((0,0))
for n in range(len(genres_columns)):
    if len(label) > 0:
      train_x = np.concatenate((train_x, label), axis=1)

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
    if len(label) > 0:
        valid_x = np.concatenate((valid_x, label), axis=1)

    predictions = classifiers[i].predict(valid_x)
    label = pd.DataFrame(predictions).apply( lambda x:  round(1.25*x))
    nn = valid_y[ genres_columns[i] ].sum()
    genremovies = genremovies + nn
    sc1 = metrics.accuracy_score(label, valid_y[ genres_columns[i] ])
    labeldf[ genres_columns[i] ] = list( pd.DataFrame(predictions).apply(round)[0] )

    print(genres_columns[i], sc1)
    accuracy = accuracy + sc1*nn
    
    
print('Average accuracy', accuracy/ genremovies) 
print('f1 score (micro)', metrics.f1_score( valid_y, labeldf,  average='micro' ) ) 
print('Precision score (micro)', metrics.precision_score(valid_y, labeldf, average='micro') ) 
print('Recall score (micro)', metrics.recall_score(valid_y, labeldf, average='micro') ) 







# Load  test file
data = pd.read_csv(spath + 'test.csv') 
synopsys = []
for index, row in data.iterrows():   
    synopsys.append(row['synopsis'] )
                 
 # create a dataframe using texts and lables
testDF = pd.DataFrame()
testDF['text'] = synopsys
  

# Text preprocessing 
testDF['text2'] = testDF['text'] .apply(text_preprocess)

testDF['char_count'] = testDF['text'].apply(len)
testDF['word_count'] = testDF['text'].apply(lambda x: len(x.split()))
testDF['punctuation_count'] = testDF['text'].apply(lambda x: len("".join(_ for _ in x if _ in string.punctuation))) 
testDF['title_word_count'] = testDF['text'].apply(lambda x: len([wrd for wrd in x.split() if wrd.istitle()]))

testDF['noun_count'] = testDF['text'].apply(lambda x: check_pos_tag(x, 'noun'))
testDF['verb_count'] = testDF['text'].apply(lambda x: check_pos_tag(x, 'verb'))
testDF['adj_count'] = testDF['text'].apply(lambda x: check_pos_tag(x, 'adj'))
testDF['adv_count'] = testDF['text'].apply(lambda x: check_pos_tag(x, 'adv'))
testDF['pron_count'] = testDF['text'].apply(lambda x: check_pos_tag(x, 'pron'))

testDF['punctuation_density'] = testDF['punctuation_count'] / testDF['word_count']
testDF['title_word_density'] = testDF['title_word_count'] / testDF['word_count']
testDF['noun_density'] = testDF['noun_count'] / testDF['word_count']
testDF['verb_density'] = testDF['verb_count'] / testDF['word_count']
testDF['adj_density'] = testDF['adj_count'] / testDF['word_count']
testDF['adv_density'] = testDF['adv_count'] / testDF['word_count']
testDF['pron_density'] = testDF['pron_count'] / testDF['word_count']
testDF['title_word_density2'] = testDF['title_word_count'] / testDF['punctuation_count']
testDF['lenword_density'] = testDF['char_count'] / (testDF['word_count']+1)

# Feature extraction
tfidf_vect2 = TfidfVectorizer(vocabulary = vocabulary2)
#tokenizer = lambda x: x.split(" "), sublinear_tf=False, ngram_range=(1,1) min_df=0.00009, smooth_idf=True, norm="l2", 
tfidf_vect2.fit( testDF['text2'] )
xtrain_tfidf2 =  tfidf_vect2.transform(testDF['text2'])

xx = xtrain_tfidf2.todense()
xx2 = np.asarray(testDF[nlp_features])


testDF = testDF.iloc[:,0:20]
inputdf = np.concatenate((xx, xx2), axis=1)
# Computation of accurracy     
label = np.zeros((0,0))
for i in range(len(genres_columns)):
    if len(label) > 0:     
       inputdf = np.concatenate((inputdf, label), axis=1)
    predictions = classifiers[i].predict(inputdf)
    testDF[genres_columns[i]+'0' ] = predictions 
    label = pd.DataFrame(predictions).apply(round)    
    testDF[genres_columns[i] ] = label

genres_columns0 = []
for col in genres_columns: 
   genres_columns0.append(col+'0')



# Setting of a label for rows without labels 
nlabels = testDF[genres_columns].apply(sum, axis=1)
for index, value in nlabels.items():
    if value==0:
       rr = testDF.loc[index][genres_columns0]
       i = rr.astype(float).argmax()
       testDF[genres_columns[i]][index] = 1
       

listgenres = [] 
for index, row in testDF.iterrows():
    genres =[]
    for col in genres_columns:
        if row[ col ]==1:
           genres.append(col)          
    listgenres.append( ' '.join(genres))
    
    
data['genres'] = listgenres


data.to_csv( spath + 'testpredict.csv') 



