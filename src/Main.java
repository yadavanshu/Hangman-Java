import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Main extends JFrame {
    private static final int MAX_TRIES = 5;
    private String[] categories = {
            "Countries",
            "Mobile Phones",
            "Harry Potter Characters",
            "Programming Languages",
            "Famous Scientists"
    };

    private String[][] words = {
            {"india", "pakistan", "nepal", "malaysia", "philippines", "australia", "iran", "ethiopia", "oman", "indonesia"},
            {"iphone", "samsung", "nokia", "oneplus", "xiaomi", "motorola", "huawei", "google", "sony", "lg"},
            {"harry", "hermione", "ron", "dumbledore", "voldemort", "snape", "draco", "hagrid", "sirius", "lupin"},
            {"python", "java", "cplusplus", "javascript", "ruby", "swift", "kotlin", "golang", "rust", "typescript"},
            {"einstein", "newton", "galileo", "curie", "darwin", "tesla", "hawking", "bohr", "fermi", "planck"}
    };

    private String secretWord;
    private StringBuilder guessedWord;
    private Set<Character> usedLetters;
    private int attemptsLeft;

    private JLabel wordLabel;
    private JLabel attemptsLabel;
    private JTextField inputField;

    public Main() {
        setTitle("Hangman Game");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(173, 216, 230)); // Light blue background

        initializeGame();

        // Input field and buttons panel
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(new Color(173, 216, 230)); // Same background color
        inputField = new JTextField(10);
        inputField.setFont(new Font("Arial", Font.BOLD, 16)); // Larger font
        inputField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JButton guessButton = new JButton("Guess");
        guessButton.setFont(new Font("Arial", Font.BOLD, 16));
        guessButton.setBackground(new Color(255, 165, 0)); // Orange button
        guessButton.setForeground(Color.WHITE);
        guessButton.setFocusPainted(false);
        guessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeGuess();
            }
        });

        // Add Enter functionality to input field
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeGuess();
            }
        });

        JButton quitButton = new JButton("Quit");
        quitButton.setFont(new Font("Arial", Font.BOLD, 16));
        quitButton.setBackground(Color.RED); // Red button for Quit
        quitButton.setForeground(Color.WHITE);
        quitButton.setFocusPainted(false);
        quitButton.addActionListener(e -> System.exit(0)); // Exit application

        inputPanel.add(inputField);
        inputPanel.add(guessButton);
        inputPanel.add(quitButton); // Add Quit button to panel
        add(inputPanel, BorderLayout.SOUTH);

        // Word and attempts display
        wordLabel = new JLabel();
        wordLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Larger font for the word
        wordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        attemptsLabel = new JLabel();
        attemptsLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Font for attempts
        attemptsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(wordLabel, BorderLayout.NORTH);
        add(attemptsLabel, BorderLayout.CENTER);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem restartItem = new JMenuItem("Restart");
        JMenuItem quitItem = new JMenuItem("Quit");

        restartItem.addActionListener(e -> resetGame());
        quitItem.addActionListener(e -> System.exit(0));

        gameMenu.add(restartItem);
        gameMenu.add(quitItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        updateDisplay();
        setVisible(true);
    }

    private void initializeGame() {
        usedLetters = new HashSet<>();
        attemptsLeft = MAX_TRIES;
        secretWord = chooseCategory();
        guessedWord = new StringBuilder("*".repeat(secretWord.length()));
    }

    private String chooseCategory() {
        String category = (String) JOptionPane.showInputDialog(
                this,
                "Choose a category:",
                "Category Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                categories,
                categories[0]);

        if (category != null) {
            int categoryIndex = -1;
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(category)) {
                    categoryIndex = i;
                    break;
                }
            }
            if (categoryIndex != -1) {
                return getRandomWord(words[categoryIndex]);
            }
        }
        return "";
    }

    private String getRandomWord(String[] wordList) {
        Random rand = new Random();
        return wordList[rand.nextInt(wordList.length)];
    }

    private void makeGuess() {
        String input = inputField.getText().toLowerCase();
        inputField.setText("");

        if (input.length() == 1 && Character.isLetter(input.charAt(0))) {
            char letter = input.charAt(0);

            if (!usedLetters.contains(letter)) {
                usedLetters.add(letter);
                int matches = letterFill(letter);
                if (matches == 0) {
                    attemptsLeft--;
                    JOptionPane.showMessageDialog(this, "Whoops! That letter isn't in there!", "Incorrect Guess", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "You found a letter! Isn't that exciting!", "Correct Guess", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "You've already guessed that letter. Try again.", "Repeated Guess", JOptionPane.ERROR_MESSAGE);
            }

            updateDisplay();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a letter.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int letterFill(char guess) {
        int matches = 0;

        for (int i = 0; i < secretWord.length(); i++) {
            if (guess == secretWord.charAt(i)) {
                guessedWord.setCharAt(i, guess);
                matches++;
            }
        }

        return matches;
    }

    private void updateDisplay() {
        wordLabel.setText(guessedWord.toString());
        attemptsLabel.setText("Attempts left: " + attemptsLeft + "\nUsed letters: " + usedLetters);

        if (attemptsLeft == 0) {
            int option = JOptionPane.showConfirmDialog(this, "Sorry, you lose... you've been hanged. The word was: " + secretWord + "\nWould you like to restart the game?", "Game Over", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                resetGame();
            } else {
                System.exit(0); // Close the game if No is selected
            }
        } else if (guessedWord.toString().equals(secretWord)) {
            int option = JOptionPane.showConfirmDialog(this, "Congratulations! You've guessed the word: " + secretWord + "\nWould you like to restart the game?", "You Won!", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                resetGame();
            } else {
                System.exit(0); // Close the game if No is selected
            }
        }
    }

    private void resetGame() {
        initializeGame();
        updateDisplay();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
