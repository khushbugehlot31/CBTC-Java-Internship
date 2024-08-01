import java.util.Random;
import java.util.Scanner;

public class GuessTheNumber {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        
        int maxAttempts = 10; // Limit the number of attempts per round
        int totalRounds = 5; // Number of rounds
        int score = 0; // Player's score
        
        System.out.println("Welcome to Guess the Number!");
        
        while (true) {
            for (int round = 1; round <= totalRounds; round++) {
                int targetNumber = random.nextInt(100) + 1; // Generate a random number between 1 and 100
                int attempts = 0;
                boolean guessedCorrectly = false;

                System.out.println("\nRound " + round + " - Guess the number between 1 and 100!");

                while (attempts < maxAttempts) {
                    System.out.print("Enter your guess: ");
                    int userGuess = scanner.nextInt();
                    attempts++;

                    if (userGuess < targetNumber) {
                        System.out.println("Too low! Try again.");
                    } else if (userGuess > targetNumber) {
                        System.out.println("Too high! Try again.");
                    } else {
                        System.out.println("Congratulations! You guessed the number!");
                        guessedCorrectly = true;
                        break;
                    }
                }

                if (!guessedCorrectly) {
                    System.out.println("Sorry, you've used all attempts. The correct number was " + targetNumber + ".");
                }
                
                // Calculate points based on the number of attempts
                int points = maxAttempts - attempts + 1;
                if (guessedCorrectly) {
                    score += points;
                    System.out.println("You earned " + points + " points for this round.");
                } else {
                    System.out.println("You did not earn any points this round.");
                }
            }

            System.out.println("\nGame Over! Your final score is: " + score + " out of " + (totalRounds * maxAttempts));
            
            // Ask the user if they want to play again
            System.out.print("Do you want to play again? (yes/no): ");
            String response = scanner.next().trim().toLowerCase();
            if (!response.equals("yes")) {
                break;
            }

            // Reset score for new game session
            score = 0;
        }
        
        scanner.close();
        System.out.println("Thank you for playing!");
    }
}
