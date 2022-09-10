package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {

    private SortedSet<Character> guess_set;
    private Set<String> word_set;
    private Map<String, Set<String>> partition;
    private StringBuilder display;
    private int num;

    public EvilHangmanGame() {
        guess_set = new TreeSet<>();
        word_set = new TreeSet<>();
        partition = new HashMap<>();
        num = 0;
        display = new StringBuilder("");
    }

    public Set<String> getWord_set() {
        return word_set;
    }

    private String getSubsetKey(String word, char guessedLetter) {
        StringBuilder key = new StringBuilder(word);
        for (int i = 0; i < word.length(); ++i) {
            if (key.charAt(i) == guessedLetter) {
                key.setCharAt(i, guessedLetter);
            }
            else {
                key.setCharAt(i, '_');
            }
        }
        return key.toString();
    }

    private void setPartition(char guess) {
        //Initialize partition to empty Map<SubsetKey, Set<Word>>
        partition.clear();

        //For each word in the current word set
        for (String word : word_set) {
            String s_key = getSubsetKey(word, guess);
            Set<String> subset = new TreeSet<>();
            if (partition.get(s_key) == null) {
                subset.add(word);
            }
            else {
                subset = partition.get(s_key);
                subset.add(word);
            }
            partition.put(s_key, subset);
        }
    }

    private Set<String> largestSubset(char guess) {
        Set<String> subset = new TreeSet<>();
        String old_key = "";

        //converting the guess char to a string
        StringBuilder temp = new StringBuilder();
        temp.append(guess);
        String letter = temp.toString();

        //go through each subset and return the one that is the largest
        for (Map.Entry mapElement : partition.entrySet()) {
            String temp_key = (String)mapElement.getKey();

            // Finding the subset
            Set<String> temp_set = partition.get(temp_key);

            //the new temp_set is bigger than whatever subset is
            if ((temp_set.size() > subset.size()) || (old_key.isEmpty())) {
                subset = temp_set;
                old_key = temp_key;
            }

            //use the tie breakers in the project specification
            else if (temp_set.size() == subset.size()) {

                //if either of the sets have none of the guess letter, set subset equal to that one
                if (temp_key.contains(letter) == false) {
                    subset = temp_set;
                    old_key = temp_key;
                }
                else if (old_key.contains(letter) == false) continue;
                else {
                    //set subset equal to whichever set has the guess appearing less frequently
                    int s = 0; //number of times the letter appears in subset
                    int t = 0; //number of times the letter appears in temp_set
                    for (int i = 0; i < temp_key.length(); ++i) {
                        if (temp_key.charAt(i) == guess) ++t;
                        if (old_key.charAt(i) == guess) ++s;
                    }
                    if (s > t) { //the guess appears more frequently in subset
                        old_key = temp_key;
                        subset = temp_set;
                        continue;
                    }
                    else if (t > s) continue; //the guess appears more frequently in temp_set

                    //set subset equal to whichever set has the first incidence of the letter further right
                    else {
                        for (int i = 0; i < old_key.length(); ++i) {
                            if (temp_key.charAt(i) == guess) {
                                if (old_key.charAt(i) == guess) continue;
                                else break; //subset has the first letter further right
                            }
                            else if (old_key.charAt(i) == guess) {
                                //temp_set has the first letter further right
                                old_key = temp_key;
                                subset = temp_set;
                                break;
                            }
                            else continue;
                        }
                    }
                }
            }
        }
        addToDisplay(old_key);
        return subset;
    }

    private void addToDisplay(String key) {
        num = 0;
        for (int i = 0; i < key.length(); i++) {
            if (key.charAt(i) != '_') {
                display.setCharAt(i, key.charAt(i));
                ++num;
            }
        }
    }

    public String toString() { return display.toString(); }

    public int getNum() { return num; }

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        partition.clear();
        word_set.clear();

        //create the initial display of guesses
        for (int i = 0; i < wordLength; i++) display.append("_");

        //initialize word_set to set of words from dictionary file
        Scanner scan;

        if (dictionary.length() == 0) throw new EmptyDictionaryException();

        //try to open the file given
        scan = new Scanner(dictionary);

        while (scan.hasNext()) {
            String word = scan.next();

            //if <input>.length != wordLength, throw it away
            if (word.length() != wordLength) continue;
            else word_set.add(word);
        }

        if (word_set.size() == 0) throw new EmptyDictionaryException();

        //close file
        scan.close();
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        StringBuilder temp = new StringBuilder();
        temp.append(guess);
        guess = temp.toString().toLowerCase().charAt(0);

        if (guess_set.contains(guess)) throw new GuessAlreadyMadeException();
        else guess_set.add(guess);

        //partition word_set relative to  the guessed letter
        setPartition(guess);

        //find the largest subset of words
        word_set = largestSubset(guess);

        //return the set of strings satisfying all guesses made so far
        return word_set;
    }


    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guess_set;
    }
}
