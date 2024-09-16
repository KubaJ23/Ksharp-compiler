package KSCompiler;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Program {
    // When running the program, the first argument must be the path to the .ks file to read,
    // the second argument must be the name of the output you would like.
    // The output file will be placed in the current directory.

    // to run program in terminal, type:
    // java -cp [a] [b] [c] [d]
    // a - classpath, where compiled .class file are located
    // b - fully qualified name of class to run: package.path.to.class
    // c - path to .ks file (don't include .ks in your path)
    // d - name of output .java file, which will be saved in the current directory (don't include .java)
    // e.g. java -cp code/java compiler.program code/ksharp/example example

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.printf("Program requires 2 command line arguments: [target file name] [output file name]");
            return;
        }
        String contents = "";
        try {
            contents = Files.readString(Path.of(args[0]+".ks"));
        } catch (Exception e) {
            System.err.printf("Error when reading file: '%s.ks'\n", args[0]);
            return;
        }
        Path path = Paths.get(args[1]);
        String fileName = path.getFileName().toString();

        String output = Compiler.compile(contents, fileName, true, true, true, true, true);

        try (PrintWriter out = new PrintWriter(fileName + ".java")) {
            out.println(output);
        }
        System.out.printf("successfully compiled file '%s.ks', output file is '%s.java'", args[0], fileName);
    }
}
