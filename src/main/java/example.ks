// Here are is some example code written in my K sharp language.
// comments can be added by using the double '/' as you can see.
// some of the below functions are written in strange ways to show that the compiler can parse many statements
// The compiler shows errors with a helpful description and line number to help the developer debug the code easily
// K sharp is a type of compiler called a transpiler. It first transpiles from K sharp to Java code which can then be run with the JRE on any platform.

// This language has 3 standard functions that are built into the language:
// print(string) -> void
// 'print' outputs text to the screen
// read() -> string
// 'read' reads a string from the user
// length(string) -> integer
// 'length' returns the number of characters in a string

// K sharp uses the following keywords: "if", "while", "return", "void" and datatypes 'string', 'int'
// K sharp is meant to be easy to understand, so long expressions are evaluated from right to left for all operators to remove any complexity and ambiguity in understanding how the language evaluates expressions.
// to represent true or false, like in C, you use integers. If an integer is 0 it is 'false', otherwise it is 'true'.
// expressions that would evaluate to booleans simply evaluate to either 0 or 1.
// you can index into a string using [index] which returns a new string of length 1.
// as seen in the examples, defining the parameters for a function is minimalistic (a single ':' instead of a '(' and a ')').
// I wanted function definitions to be this way as it is quicker to type and improves readability.
// like in C, statements must end with a ';'.
// In K sharp, the '!' operator is a 'bit not', so it will apply 'not' to each bit.

// A K sharp file must contain a 'main' function which takes 0 arguments
// and has a return type of 'void' as the entry point to the program
void main:
{
    showStandardFunctions("Welcome to the demo for the K sharp compiler!!!");

    int playagain = 1;
    while(playagain)
    {
        print("\nLets play hangman!\n");
        playHangman();
        print("Play again? (yes/no): ");
        string response = read();
        while(not((response == "yes") | (response == "no")))
        {
            print("answer must be 'yes' or 'no'\n");
            print("Play again? (yes/no): ");
            response = read();
        }
        if(response == "no")
        {
            playagain = 0;
        }
    }
}
int not:int x
{
    if(x == 0)
    {
        return 1;
    }
    return 0;
}
void showStandardFunctions:string welcomeMessage
{
    print(welcomeMessage);
    print("\n");
    print("I will count how many characters you entered:");
    string input = read();
    print("You input " + length(input) + " Characters\n");
    print("The first char is:" + input[0]);
    print("\n");
    print("The last char is:" + input[length(input) - 1]);
    print("\n");
}
void playHangman:
{
    print("Enter your secret message: ");
    string secret = read();
    int unknownletters = length(secret);
    string hiddenWord = repeat("*", unknownletters);
    int numGuesses = 0;

    print("-----------------------------------------\n");
    while(unknownletters)
    {
        print("hidden word: " + hiddenWord + "\n");
        print("guess secret: ");
        string guess = read();

        if(length(guess) == 1)
        {
            numGuesses = numGuesses + 1;
            int occurrences = numOccurrences(secret, guess);
            unknownletters = unknownletters - occurrences;

            hiddenWord = unveilLetters(secret, hiddenWord, guess);

            print("hidden word: " + hiddenWord + "\n");
        }
        if(length(guess) != 1)
        {
            print("your guess must be a single letter!\n");
        }
    }
    print("Congratulations! The secret word was: " + hiddenWord + "\n");
    print("It took you " + numGuesses + " guesses.\n");
}
string unveilLetters: string secret, string unveiled, string letter
{
    int i = 0;
    while(i < length(secret))
    {
        if(secret[i] == letter)
        {
            unveiled = replace(unveiled, letter, i);
        }
        i = i + 1;
    }
    return unveiled;
}
string replace: string text, string replacement, int index
{
    string result = "";
    int i = 0;
    while(i < length(text))
    {
        if(i == index)
        {
            result = result + replacement;
        }
        if(i != index)
        {
            result = result + text[i];
        }
        i = i + 1;
    }
    return result;
}
int numOccurrences: string text, string character
{
    int occurrences = 0;
    int i = 0;
    while(i < length(text))
    {
        if(text[i] == character)
        {
            occurrences = occurrences + 1;
        }
        i = i + 1;
    }
    return occurrences;
}
string repeat: string text, int times
{
    string result = "";
    while(times > 0)
    {
        result = result + text;
        times = times - 1;
    }
    return result;
}

