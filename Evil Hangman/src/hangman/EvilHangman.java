package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangman {

    private static char prompt(Scanner scan, EvilHangmanGame game) {
        System.out.print("Enter guess: ");
        String guess = scan.next();
        char ch = guess.charAt(0);
        try {
            if ((ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z')) throw new InvalidGuessException();
            game.makeGuess(ch);
        }
        catch (GuessAlreadyMadeException ex) {
            System.out.printf("%nYou already used that letter%n");
            prompt(scan, game);
        }
        catch (InvalidGuessException ex) {
            System.out.printf("%nInvalid input%n");
            prompt(scan, game);
        }
        finally {
            return ch;
        }
    }

    public static void main(String[] args) throws IOException, EmptyDictionaryException, GuessAlreadyMadeException {

        if (args.length < 3) {
            System.out.println("dude. provide the right number of arguments.\n" +
                    "it is just a dictionary, the number of letters, and the max number of guesses.\n");
            return;
        }
        File dictionary = new File(args[0]);
        int word_length = Integer.parseInt(args[1]);
        int max_guesses = Integer.parseInt(args[2]);
        int guess_num = 0;
        Scanner scan = new Scanner(System.in);

        EvilHangmanGame game = new EvilHangmanGame();
        game.startGame(dictionary, word_length);

        //while there are more turns left
        while (guess_num < max_guesses) {
            int remaining = max_guesses - guess_num;
            System.out.printf("You have %d guesses left%n", remaining);
            System.out.print("Used letters: ");

            StringBuilder guessedLetters = new StringBuilder(game.getGuessedLetters().toString());
            guessedLetters.deleteCharAt(0);
            guessedLetters.deleteCharAt(guessedLetters.length() - 1);
            System.out.println(guessedLetters);
            System.out.println("Word: ");
            System.out.println(game.toString());
            char guess = prompt(scan, game);

            if (game.getNum() == 0) {
                System.out.printf("Sorry, there are no %c's%n", guess);
                ++guess_num;
            }
            else {
                System.out.printf("Yes, there is %d %c%n", game.getNum(), guess);
            }

            if (!game.toString().contains("_")) {
                System.out.println("You win!");
                System.out.print(game.toString());
                return;
            }
        }


        System.out.println("You lose!");
        System.out.print("The word was: ");
        System.out.print(game.getWord_set().toArray()[0]);

    }

}
