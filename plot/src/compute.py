from parse import read_results
import os
import pandas as pd
import numpy as np

def compute_metrics(data):
    data['precision']      = np.where(data['true_positive'] == 0,
                                      float(1),
                                      data['true_positive'] / (data['true_positive'] + data['false_positive'])) * 100
    data['recall']         = np.where(data['true_positive'] == 0,
                                      float(0),
                                      data['true_positive'] / (data['true_positive'] + data['false_negative'])) * 100
    data['f1']             = harmonic_mean(data['precision'], data['recall'])
    data['static_cg_size'] = data['true_positive'] + data['false_positive']
    return data


def harmonic_mean(a: float, b: float) -> float:
    return 2 * (a*b) / (a+b)


def columns_to_rows(data):
    result = pd.melt(data.reset_index(),
                     id_vars=['project', 'framework', 'algo', 'scope', 'package'],
                     value_vars=['precision', 'recall', 'f1', 'static_cg_size'],
                     var_name='metric',
                     value_name='metric_value')
    result['metric'] = result['metric'].map(lambda m: pretty_print_metric(m))
    result = result.set_index(['project', 'framework', 'algo', 'scope', 'package', 'metric'])

    return result


def pretty_print_metric(metric: str) -> str:
    if metric == 'static_cg_size':
        return 'Static CG Size'
    else:
        return metric.capitalize()
