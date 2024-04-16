import os
from typing import IO
import re
import pandas as pd

def read_results(path: str):
    results = []
    for project in os.listdir(path):
        for framework in os.listdir(os.path.join(path, project)):
            for algo in os.listdir(os.path.join(path, project, framework)):
                for package in ['all', 'package']:
                    result_file_path = os.path.join(path, project, framework, algo, "result-"+package+".txt")
                    if os.path.exists(result_file_path):
                        result_file = open(result_file_path, 'r')
                        result = read_result(result_file)
                        result_file.close()
                        if(result):
                            for scope in ['method', 'edge']:
                                res = {}
                                res['project'] = project
                                res['framework'] = framework
                                res['algo'] = algo
                                res['scope'] = scope
                                res['package'] = package
                                for metric in ['true_positive', 'false_positive', 'false_negative']:
                                    res[metric] = result[scope][metric]
                                results.append(res)
    return pd.DataFrame.from_records(data=results, index=['project', 'framework', 'algo', 'scope', 'package'])


def read_result(result_file: IO):
    str = result_file.read()
    method = {}
    match = re.search('Method precision: (\d+)/(\d+)', str)
    if match:
        method['true_positive'] = int(match.group(1))
        method['false_positive'] = int(match.group(2)) - method['true_positive']
    else:
        return None
    match = re.search('Method recall: (\d+)/(\d+)', str)
    if match:
        method['false_negative'] = int(match.group(2)) - method['true_positive']
    else:
        return None

    edge = {}
    match = re.search('Edge precision: (\d+)/(\d+)', str)
    if match:
        edge['true_positive'] = int(match.group(1))
        edge['false_positive'] = int(match.group(2)) - edge['true_positive']
    else:
        return None
    match = re.search('Edge recall: (\d+)/(\d+)', str)
    if match:
        edge['false_negative'] = int(match.group(2)) - edge['true_positive']
    else:
        return None

    return {'method': method, 'edge': edge}


if __name__ == "__main__":
    main()
