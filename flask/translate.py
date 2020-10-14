from konlpy.tag import Kkma


class NLP:
    def __init__(self):
        self.nlp_engine = Kkma()
        self.nlp_engine.pos('시작')

    def compare(self, sentence1: str, sentence2: str):
        pos1 = self.nlp_engine.pos(sentence1)
        pos2 = self.nlp_engine.pos(sentence2)
        
        dic1 = dict()
        dic2 = dict()

        for pos in pos1:
            if pos not in dic1:
                dic1[pos] = 1
            else:
                dic1[pos] += 1

        for pos in pos2:
            if pos not in dic2:
                dic2[pos] = 1
            else:
                dic2[pos] += 1

        all_keys = set(dic1.keys()).union(dic2.keys())
        union = 0
        intersect = 0
        for key in all_keys:
            val1 = dic1.get(key)
            val2 = dic2.get(key)

            val1 = val1 if val1 else 0
            val2 = val2 if val2 else 0

            union += max(val1, val2)
            intersect += min(val1, val2)
        
        return str(intersect / union)
