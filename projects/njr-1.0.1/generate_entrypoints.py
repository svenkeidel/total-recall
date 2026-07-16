from pathlib import Path
import subprocess

for project in Path('repo').iterdir():
    if project.is_dir() and not (project / "entrypoint.jar").exists():
        print('Generate entrypoint for ' + project.name)

        main_classes = []

        for class_file in (project / "classes").rglob("*.class"):
            result = subprocess.run(["javap", class_file], capture_output=True, text=True)
            if "public static void main(" in result.stdout:
                klass = str(Path(*class_file.parts[3:])).replace("/",".").replace(".class", "")

                # check if class is public
                if f"public class {klass}" in result.stdout:
                    main_classes.append(klass)

        print(f"Main classes: {main_classes}")

        if main_classes:
            entrypoint = ""
            entrypoint += "public class Entrypoint {\n"
            entrypoint += "    static void entrypoint() {\n"
            for main_class in main_classes:
                entrypoint += f"        try {{ {main_class}.main(new String[]{{}}); }} catch(Throwable exception) {{ exception.printStackTrace(System.err); }}\n"
            entrypoint += "    }\n"
            entrypoint += "    static void main(String[] args) { Entrypoint.entrypoint(); }\n"
            entrypoint += "}\n"

            entrypoint_java = project / "Entrypoint.java"
            with open(entrypoint_java, "w", encoding="utf-8") as file:
                file.write(entrypoint)

            project_jar = project / "jarfile" / f"{project.name}.jar"
            java_compile_command = ["javac", "-cp", str(project_jar), str(entrypoint_java)]
            print(" ".join(java_compile_command))
            output = subprocess.run(java_compile_command, capture_output=True, text=True)

            if output.stderr:
                print(output.stderr)
                raise RuntimeError(f"Compilation of entrypoint failed for project {project}")

            entrypoint_class = entrypoint_java.with_suffix(".class")
            entrypoint_jar = project / "entrypoint.jar"
            jar_command = ["jar", "cf", str(entrypoint_jar), str(entrypoint_class)]
            print(" ".join(jar_command))
            output = subprocess.run(jar_command, capture_output=True, text=True)

            if output.stderr:
                print(output.stderr)
                raise RuntimeError(f"Creating jar {entrypoint_jar} failed failed for project {project}")

        else:
            raise RuntimeError(f"No main classes found for project {project}")