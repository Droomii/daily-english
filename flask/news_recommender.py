import pandas as pd
from sklearn.decomposition import NMF
from sklearn.preprocessing import Normalizer, MaxAbsScaler
from sklearn.pipeline import make_pipeline

def save_related_articles(db):
    articles = []
    index = []
    print('loading articles...', end='')
    for article in db.tfIdfCol.find().limit(3000):
        articles.append(article['tfIdf'])
        index.append(article['newsUrl'])
    print('done')
    
    print('making dataframe...', end='')
    df = pd.DataFrame(articles, index=index)
    df.fillna(0, inplace=True)
    val = df.values
    print('done')
    print('df shape :', val.shape)
    
    scaler = MaxAbsScaler()
    nmf = NMF(n_components=30)
    normalizer = Normalizer()
    pipeline = make_pipeline(scaler, nmf, normalizer)
    
    print('NMF in progress...', end='')
    norm_features = pipeline.fit_transform(val)
    print('done')
    res = pd.DataFrame(norm_features, index=index)
    similarities = res.dot(index[0])
    print(similarities.nlargest(6))