# -*- coding: utf-8 -*-
"""
Created on Mon Mar  8 21:28:59 2021

@author: jaaxx
"""

import pandas as pd #import pandas
from sklearn.datasets import load_iris #import toy dataset @ sklearn
data = load_iris() #data is a dictionary
X, y = data['data'], pd.get_dummies(data['target']).values #label encode the target

from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3)

import autokeras as ak
search = ak.StructuredDataClassifier(max_trials=5)

search.fit(x=X_train, y=y_train, verbose=1/0)