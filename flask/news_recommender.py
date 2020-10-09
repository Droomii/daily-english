import pandas as pd
from sklearn.decomposition import NMF
from sklearn.preprocessing import Normalizer, MaxAbsScaler
from sklearn.pipeline import make_pipeline

def save_related_articles(db):

    if 'relatedArticles' in db.collection_names():
        db.drop_collection('relatedArticles')
    
    db.create_collection('relatedArticles')
    db.relatedArticles.create_index('newsUrl')
    
    articles = []
    index = []
    print('loading articles...', end='')
    for article in db.tfIdfCol.find().sort("newsUrl", -1).limit(1000):
        articles.append(article['tfIdf'])
        index.append(article['newsUrl'])
    print('done')
    
    print('making dataframe...', end='')
    df = pd.DataFrame(articles, index=index)
    df.fillna(0, inplace=True)
    df[df < 0] = 0
    val = df.values
    print('done')
    print('df shape :', val.shape)
    
    scaler = MaxAbsScaler()
    nmf = NMF(n_components=20)
    normalizer = Normalizer()
    pipeline = make_pipeline(scaler, nmf, normalizer)
    
    print('NMF in progress...', end='')
    norm_features = pipeline.fit_transform(val)
    print('done')
    res = pd.DataFrame(norm_features, index=index)
    
    insList = []
    
    for i in index:
        insert_obj = dict()
        insert_obj['newsUrl'] = i
        
        article = res.loc[i]
        similarities = res.dot(article)
        
        insert_obj['related'] = list(similarities.nlargest(6)[1:].keys())
        insert_obj['related'].sort(reverse=True)
        insList.append(insert_obj)
        
    db.relatedArticles.insert_many(insList)