from datetime import datetime
import os
import sys
import shutil

EXPERIMENT = "fuzzing_seed"

def handle_project(project_dir, project_name, data_dir):

    compare_dir = os.path.join(project_dir, "target", "comparecg", EXPERIMENT)

    if not os.path.exists(compare_dir):
        print(f"WARN: Missing comparison results for project {project_name} at {compare_dir}")
        return False

    print(f"Processing data for project {project_name} ...")

    out_dir = os.path.join(data_dir, project_name)
    os.makedirs(out_dir)

    for item in os.listdir(compare_dir):
        full_framework_path = os.path.join(compare_dir, item)
        output_framework_path = os.path.join(out_dir, item)
        if os.path.isdir(full_framework_path):
            shutil.copytree(full_framework_path, output_framework_path)

            if item == "Doop":
                folder_to_rename = os.path.join(output_framework_path, "context-insensitive")
                if os.path.exists(folder_to_rename):
                    os.rename(folder_to_rename, os.path.join(output_framework_path, "0-CFA"))
    
    print(f"Done processing {project_name}.")
    return True



def collect_data():

    if len(sys.argv) < 3:
        print("ERROR: Usage: python prepare_data.py <projects_root> <out_dir>")
        sys.exit(-1)
    
    projects_root = sys.argv[1]
    out_dir = sys.argv[2]

    if not os.path.exists(projects_root):
        print(f"ERROR: Project root directory does not exist at {projects_root}")
        sys.exit(1)
    
    if not os.path.exists(out_dir):
        print(f"ERROR: Output directory does not exist at {out_dir}")
        sys.exit(1)

    try:
        success_cnt = 0
        print(f"Collecting plot data in new directory: {out_dir}")

        for item in os.listdir(projects_root):
            full_item_path = os.path.join(projects_root, item)
            if os.path.isdir(full_item_path):
                if handle_project(full_item_path, item, out_dir) == True:
                    success_cnt += 1

        if success_cnt == 0:
            raise RuntimeError("No comparison results found")

    except Exception as x:
        print(f"Error collecting plot data: {x}")
        print(f"Cleaning up data dir {out_dir} ...")
        if os.path.exists(out_dir):
            os.rmdir(out_dir)
            print(f"Corrupted data dir has been deleted.")


if __name__ == "__main__":
    collect_data()


