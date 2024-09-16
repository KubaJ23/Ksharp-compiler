import java.util.Scanner;

public class example {
    public static void main(String args[]) {
        showStandardFunctions("Welcome to the demo for the K sharp compiler!!!");
        int playagain = 1;
        while (playagain != 0) {
            print("\nLets play hangman!\n");
            playHangman();
            print("Play again? (yes/no): ");
            String response = read();
            while (not(((response.equals("yes") ? 1 : 0) | (response.equals("no") ? 1 : 0))) != 0) {
                print("answer must be 'yes' or 'no'\n");
                print("Play again? (yes/no): ");
                response = read();
            }
            if ((response.equals("no") ? 1 : 0) != 0) {
                playagain = 0;
            }
        }
    }

    public static int not(int x) {
        if ((x == 0 ? 1 : 0) != 0) {
            return 1;
        }
        return 0;
    }

    public static void showStandardFunctions(String welcomeMessage) {
        print(welcomeMessage);
        print("\n");
        print("I will count how many characters you entered:");
        String input = read();
        print(("You input " + (length(input) + " Characters\n")));
        print(("The first char is:" + input.substring(0, 0 + 1)));
        print("\n");
        print(("The last char is:" + input.substring((length(input) - 1), (length(input) - 1) + 1)));
        print("\n");
    }

    public static void playHangman() {
        print("Enter your secret message: ");
        String secret = read();
        int unknownletters = length(secret);
        String hiddenWord = repeat("*", unknownletters);
        int numGuesses = 0;
        print("-----------------------------------------\n");
        while (unknownletters != 0) {
            print(("hidden word: " + (hiddenWord + "\n")));
            print("guess secret: ");
            String guess = read();
            if ((length(guess) == 1 ? 1 : 0) != 0) {
                numGuesses = (numGuesses + 1);
                int occurrences = numOccurrences(secret, guess);
                unknownletters = (unknownletters - occurrences);
                hiddenWord = unveilLetters(secret, hiddenWord, guess);
                print(("hidden word: " + (hiddenWord + "\n")));
            }
            if ((length(guess) != 1 ? 1 : 0) != 0) {
                print("your guess must be a single letter!\n");
            }
        }
        print(("Congratulations! The secret word was: " + (hiddenWord + "\n")));
        print(("It took you " + (numGuesses + " guesses.\n")));
    }

    public static String unveilLetters(String secret, String unveiled, String letter) {
        int i = 0;
        while ((i < length(secret) ? 1 : 0) != 0) {
            if ((secret.substring(i, i + 1).equals(letter) ? 1 : 0) != 0) {
                unveiled = replace(unveiled, letter, i);
            }
            i = (i + 1);
        }
        return unveiled;
    }

    public static String replace(String text, String replacement, int index) {
        String result = "";
        int i = 0;
        while ((i < length(text) ? 1 : 0) != 0) {
            if ((i == index ? 1 : 0) != 0) {
                result = (result + replacement);
            }
            if ((i != index ? 1 : 0) != 0) {
                result = (result + text.substring(i, i + 1));
            }
            i = (i + 1);
        }
        return result;
    }

    public static int numOccurrences(String text, String character) {
        int occurrences = 0;
        int i = 0;
        while ((i < length(text) ? 1 : 0) != 0) {
            if ((text.substring(i, i + 1).equals(character) ? 1 : 0) != 0) {
                occurrences = (occurrences + 1);
            }
            i = (i + 1);
        }
        return occurrences;
    }

    public static String repeat(String text, int times) {
        String result = "";
        while ((times > 0 ? 1 : 0) != 0) {
            result = (result + text);
            times = (times - 1);
        }
        return result;
    }

    public static void print(String text) {
        System.out.print(text);
    }

    public static String read() {
        Scanner reader = new Scanner(System.in);
        return reader.nextLine();
    }

    public static int length(String text) {
        return text.length();
    }
}
