from pathlib import Path
import sys
import os
import shutil

if len(sys.argv) < 2:
    print("USAGE: adapt_resource_refs.py <path_to_project_dir>")
    sys.exit(-1)

ppath = sys.argv[1]
rpath = os.path.join(ppath, 'resources')
corpus_base = Path(os.path.join(ppath, 'target', 'fuzzing_seed', 'corpus'))

print("Correcting references for XML files in input corpus...")

xml_cnt = 0
decoding_failed = 0

def handle_directory(dir_path: Path, prefix: str):

    global xml_cnt
    global decoding_failed

    all_files = [f for f in dir_path.iterdir() if f.is_file()]
    all_directories = [d for d in dir_path.iterdir() if d.is_dir()]

    for xml_file in dir_path.glob("*.xml"):

        if xml_cnt % 500 == 0:
            print("Processing XML file #" + str(xml_cnt))

        try:
            with xml_file.open('r') as read_xml:
                content = read_xml.read()


            for potential_resource in all_files:
                if potential_resource.name in content:
                    if len(prefix) == 0:
                        resource_file_name = potential_resource.name
                    else:
                        resource_file_name = prefix + "_" + potential_resource.name

                    shutil.copyfile(potential_resource.absolute(), os.path.join(rpath, resource_file_name))
                    content = content.replace(potential_resource.name, os.path.join('/resources', resource_file_name))
            
            with xml_file.open('w') as write_xml:
                write_xml.write(content)


        except Exception as e:
            is_encoding_issue = "can't decode" in str(e)

            if is_encoding_issue:
                decoding_failed += 1
            else:
                print("Failed to process " + str(xml_file.absolute) + ": " + str(e))
        
        xml_cnt += 1

    for sub_dir in all_directories:
        if len(prefix) == 0:
            new_prefix = sub_dir.name
        else:
            new_prefix = prefix + "_" + sub_dir.name
        handle_directory(sub_dir, new_prefix)

handle_directory(corpus_base, '')

print("Done correcting resource references for " + str(xml_cnt) + " XML files (" + str(decoding_failed)+" decoding issues)")

