from parse import read_results
import os
import pandas as pd
import numpy as np

def columns_to_rows(data):
    result = pd.melt(data.reset_index(),
                     id_vars=['project', 'framework', 'algo', 'scope', 'package'],
                     value_vars=['precision', 'recall', 'f1-score'],
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
