FUZZING_THREADS := "8"
FUZZING_TIME := "3600"

DYNCG_TIMEOUT := "999m"
DYNCG_MEMORY := "400G"

STATICCG_TIMEOUT := "180m"
STATICCG_MEMORY := "400G"

PLOT_IMAGE := "dyncg-python-plots"
PLOT_CONTAINER := "plot-project-stats"
BASE_DIR := absolute_path(".")
PLOT_DIR := absolute_path(".") / "plot"

for_all_projects RECIPE *ARGS:
    for project in $(ls projects); do \
        just \
          --justfile projects/Justfile \
          --dotenv-filename projects/$project/.env \
          FUZZING_TIME={{FUZZING_TIME}} \
          FUZZING_THREADS={{FUZZING_THREADS}} \
          DYNCG_TIMEOUT={{DYNCG_TIMEOUT}} \
          DYNCG_MEMORY={{DYNCG_MEMORY}} \
          STATICCG_TIMEOUT={{STATICCG_TIMEOUT}} \
          STATICCG_MEMORY={{STATICCG_MEMORY}} \
          {{RECIPE}} \
          {{ARGS}}; \
    done

fuzz EXPERIMENT: (for_all_projects "fuzz" EXPERIMENT)

# Record coverage of seed corpus
coverage_seed: (for_all_projects "coverage_seed")

# Record coverage after fuzzing without seed corpus, but dictionary
coverage_fuzzing: (for_all_projects "coverage_fuzzing")

# Record coverage after fuzzing with seed corpus and dictionary
coverage_fuzzing_seed: (for_all_projects "coverage_fuzzing_seed")

# Record a dynamic call graph for a corpus created by fuzzing and an initial seed corpus
dynamic_callgraph_fuzzing_seed: (for_all_projects "dynamic_callgraph_fuzzing_seed")

dynamic_callgraph_add_declared_targets_fuzzing_seed: (for_all_projects "dynamic_callgraph_add_declared_targets_fuzzing_seed")

static_callgraph FRAMEWORK ALGO: (for_all_projects "static_callgraph" FRAMEWORK ALGO)

static_callgraphs: (for_all_projects "static_callgraphs")

compare_callgraphs: (for_all_projects "compare_callgraphs")

jdk_callbacks: (for_all_projects "jdk_callbacks")

build_plot_image:
    docker build --tag {{PLOT_IMAGE}} ./plot

build_plots: build_plot_image
    mkdir -p {{PLOT_DIR}}/results

    docker rm -f {{PLOT_CONTAINER}}
    docker run -it \
            --name {{PLOT_CONTAINER}} \
            --mount type=bind,source={{BASE_DIR}}/projects,target=/in,readonly \
            --mount type=bind,source={{PLOT_DIR}}/data,target=/data \
            --mount type=bind,source={{PLOT_DIR}}/results,target=/out \
            {{PLOT_IMAGE}}
    

clean_staticcg: (for_all_projects "clean_staticcg")

clean: (for_all_projects "clean")

default:
    just --list