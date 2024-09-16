# Ksharp-compiler
A compiler (transpiler) for my own language called K sharp. It is a procedural language that is meant to be easy to understand and quick to learn.

## Running the compiler

When running the program using java, the first argument must be the path to the .ks file to read, the second argument must be the name of the output file you would like. The output file will be placed in the current directory.

to run program in terminal, enter:

    java -cp [ a ] [ b ] [ c ] [ d ]
    
- a - classpath, where compiled .class file are located
- b - fully qualified name of class to run: package.path.to.program
- c - path to .ks file (don't include .ks in your path)
- d - name of output .java file, which will be saved in the current directory (don't include .java)

      e.g. java -cp code/java compiler.program code/ksharp/example example

The output of the compiler can be configured easily in the code in the Program.java file

By default the output of the compiler (if the K sharp code is valid) is:
1. Code without comments
2. Tokens
3. Abstract Syntax Tree
4. Symbol Table
5. Java output

## About K sharp language

Example code written in my K sharp language can be found in './src/main/java/example.ks', the compiled version of that example can be found at './src/main/java/example.java'.

My example code includes a game of Hangman which takes input, outputs text and performs various operations. The example code is meant to be proof that my language is a capable one, and that much more complex programs could be made using it.

To play the Hangman game and see that it works, simply run compile and run the file './src/main/java/example.java'.

Comments can be added by using '//'.

The compiler shows errors with a helpful description and line number to help the developer debug the code easily

K sharp is a type of compiler called a transpiler. It first transpiles from K sharp to Java code which can then be run with the JRE on any platform.

This language has 3 standard functions that are built into the language:
> ### print(string) -> void
> 'print' outputs text to the screen

> ### read() -> string
> 'read' reads a string from the user

> ### length(string) -> integer
> 'length' returns the number of characters in a string

K sharp uses the following keywords: 
- "if"
- "while"
- "return"
- "void"

Datatypes:
- 'string'
- 'int'

K sharp is meant to be easy to understand, so long expressions are evaluated from right to left for all operators to remove any complexity and ambiguity in understanding how the language evaluates expressions.
To represent true or false, like in C, you integers. If an integer is 0 it is 'false', otherwise it is 'true'.
Expressions that would evaluate to booleans simply evaluate to either 0 or 1, so you could still use the output of boolean expressions in mathematical expressions unlike in Java.
You can index into a string using variable[index] which returns a new string of length 1.
As seen in the examples, defining the parameters for a function is minimalistic (a single ':' instead of a '(' and a ')'). I wanted function definitions to be this way as it is quicker to type and improves readability.
Like in C, statements must end with a ';'.
In K sharp, the '!' operator is a 'bit not', so it will apply 'not' to each bit.
A K sharp file must contain a 'main' function which takes 0 arguments and has a return type of 'void' as the entry point to the program
