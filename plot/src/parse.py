import os
import pandas as pd
import json

def read_results(path: str):
    results = []
    for project in os.listdir(path):
        for framework in os.listdir(os.path.join(path, project)):
            for algo in os.listdir(os.path.join(path, project, framework)):
                for package in ['all', 'package']:
                    result_file_path = os.path.join(path, project, framework, algo, project+"-"+package+"-precision-recall.json")
                    if os.path.exists(result_file_path):
                        with open(result_file_path, 'r') as result_file:
                            result = json.load(result_file)
                            for scope in ['methods', 'edges', 'edges-with-callsite-line-numbers']:
                                res = {}
                                res['project'] = project
                                res['framework'] = framework
                                res['algo'] = algo
                                res['scope'] = scope
                                res['package'] = package
                                for metric in ['true_positive', 'false_positive', 'false_negative', 'precision', 'recall', 'f1-score']:
                                    res[metric] = result[scope][metric]
                                results.append(res)
    return pd.DataFrame.from_records(data=results)

if __name__ == "__main__":
    print(read_results(os.path.abspath('target')))
