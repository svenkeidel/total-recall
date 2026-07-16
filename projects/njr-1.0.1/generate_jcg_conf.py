from pathlib import Path
import json

for project in Path('repo').iterdir():
    if project.is_dir():
        jcg_conf = {
            "name": project.name,
            "target": f"/repo/{project.name}/entrypoint.jar",
            "java": 25,
            "main": "Entrypoint",
            "jvm_args": [],
            "cp": [ { "path": f"/repo/{project.name}/jarfile/{project.name}.jar" } ]
        }

        jcg_conf_path = Path(project.parts[1]).with_suffix(".conf")
        print(jcg_conf_path)
        with open(jcg_conf_path, "w", encoding="utf-8") as file:
            file.write(json.dumps(jcg_conf, indent=4))
