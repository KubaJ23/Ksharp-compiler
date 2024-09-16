# Ksharp-compiler
A compiler (transpiler) for my own language called K sharp. It is a procedural language that is meant to be easy to understand and quick to learn.

example code written in my K sharp language can be found in './src/main/java/example.ks', the compiled version of that example can be found at './src/main/java/example.java'.

comments can be added by using '//'.

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

and datatypes:
- 'string'
- 'int'

- K sharp is meant to be easy to understand, so long expressions are evaluated from right to left for all operators to remove any complexity and ambiguity in understanding how the language evaluates expressions.
- to represent true or false, like in C, you integers. If an integer is 0 it is 'false', otherwise it is 'true'.
- expressions that would evaluate to booleans simply evaluate to either 0 or 1, so you could still use the output of boolean expressions in mathematical expressions unlike in Java.
- you can index into a string using variable[index] which returns a new string of length 1.
- as seen in the examples, defining the parameters for a function is minimalistic (a single ':' instead of a '(' and a ')'). I wanted function definitions to be this way as it is quicker to type and improves readability.
- like in C, statements must end with a ';'.
- In K sharp, the '!' operator is a 'bit not', so it will apply 'not' to each bit.
- A K sharp file must contain a 'main' function which takes 0 arguments and has a return type of 'void' as the entry point to the program
