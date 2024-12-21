package wackyourstressaway;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class ColorChangeGame {

    private static int score = 0;
    private static int hearts = 3;
    private static ArrayList<Integer> topScores = new ArrayList<>();

    private static JLabel scoreLabel;
    private static JLabel heartsLabel;
    private static JLabel missedLabel;
    private static JButton newGameButton;
    private static JButton pauseButton;
    private static JButton saveButton;
    private static JButton button;

    private static boolean isPaused = false;
    private static JFrame frame;

    public static void main(String[] args) {
        frame = new JFrame("Color Change Button Game");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Prevent immediate closing

        // Adding a window listener to handle forced closing (the 'X' button)
        //prevents sudden close and loss of progress. Could be changed later
        //to close immediately after saving, yes without the confirm after.
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int userChoice = JOptionPane.showConfirmDialog(frame, 
                        "Are you sure you want to exit? Don't forget to save your progress first.", 
                        "Exit Game", JOptionPane.YES_NO_OPTION);
                if (userChoice == JOptionPane.YES_OPTION) {
                    saveGame(); // Optionally save before exiting
                    System.exit(0); // Exit the game
                }
            }
        });

        // Create the "Click" button
        button = new JButton("Click");

        // Create and position score label
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        scoreLabel.setBounds(10, 10, 100, 30);
        frame.add(scoreLabel);

        // Create and position hearts label
        heartsLabel = new JLabel("Hearts: ♥♥♥");
        heartsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        heartsLabel.setBounds(10, 40, 150, 30);
        frame.add(heartsLabel);

        // Create the missed label but hide it initially
        //show "missed" but remove it moment later
        missedLabel = new JLabel("");
        missedLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        missedLabel.setVisible(false);
        frame.add(missedLabel);

        // Action for when the "Click" button is clicked
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isPaused) {
                    // Randomly reposition the "Click" button, without changing its color
                    int maxX = frame.getWidth() - button.getWidth();
                    int maxY = frame.getHeight() - 200; // Avoid bottom part of the screen

                    // Add boundary conditions to prevent overlap. 
                    //button was overlapping causing a burden
                    int x = (int) (Math.random() * maxX);
                    int y = (int) (Math.random() * maxY);
                    if (x < 150) x = 150;  // Avoid left side where the score/heart is located
                    if (y < 100) y = 100;  // Avoid overlap with the buttons

                    button.setBounds(x, y, 150, 50);
                    score++;  // Increment the score by 1 when clicked
                    scoreLabel.setText("Score: " + score);
                    missedLabel.setVisible(false);
                }
            }
        });

        // Mouse listener to handle missed clicks
        frame.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (!button.getBounds().contains(evt.getPoint()) && !isPaused) {
                    hearts--; // Deduct one heart on a miss
                    heartsLabel.setText("Hearts: " + "♥".repeat(hearts));

                    missedLabel.setText("Missed!");
                    missedLabel.setLocation(evt.getX() - 30, evt.getY() - 30);
                    missedLabel.setVisible(true);

                    if (hearts == 0) {
                        endGame(frame); // End the game when out of hearts
                    }
                }
            }
        });

        // New game button action
        newGameButton = new JButton("New Game");
        newGameButton.setBounds(10, 150, 100, 30);
        frame.add(newGameButton);
        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(frame, 
                        "Are you sure you want to start a new game?", 
                        "Confirm New Game", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    hearts = 3;  // Reset hearts
                    score = 0;  // Reset score
                    heartsLabel.setText("Hearts: ♥♥♥");
                    scoreLabel.setText("Score: 0");
                    isPaused = false; // Unpause the game
                }
            }
        });

        // Pause/Resume button action
        pauseButton = new JButton("Pause");
        pauseButton.setBounds(10, 190, 100, 30);
        frame.add(pauseButton);
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isPaused = !isPaused; // Toggle pause state
                pauseButton.setText(isPaused ? "Resume" : "Pause");
            }
        });

        // Save button action
        saveButton = new JButton("Save");
        saveButton.setBounds(10, 230, 100, 30);
        frame.add(saveButton);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveGame();
                JOptionPane.showMessageDialog(frame, "Game saved. You can close and continue later.");
            }
        });

        // "Click" button setup
        button.setFont(new Font("Times New Roman", Font.BOLD, 8));
        //changed font to be better visible and shortened word to click instead of click me!
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBounds(100, 100, 100, 50);  // Initial position of the button

        // Setup the game frame layout
        frame.setLayout(null);
        frame.add(button);
        frame.setVisible(true);

        // Load previously saved game state if available
        loadProgress();
    }

    // End game and show leaderboard top score board top 3
    public static void endGame(JFrame frame) {
        topScores.add(score);
        Collections.sort(topScores, Collections.reverseOrder());

        String leaderboard = "<html><b>Top 3 Scores:</b><br>";
        for (int i = 0; i < Math.min(3, topScores.size()); i++) {
            leaderboard += (i + 1) + ". " + topScores.get(i) + "<br>";
        }
        leaderboard += "</html>";

        JOptionPane.showMessageDialog(frame, "Game Over! Your score: " + score + "\n" + leaderboard);
        hearts = 3;  // Reset hearts
        score = 0;   // Reset score
        heartsLabel.setText("Hearts: ♥♥♥");
        scoreLabel.setText("Score: 0");
    }

    // Save game state to a file to prevent loss of progress. tested
    public static void saveGame() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("game_save.dat"))) {
            oos.writeObject(score);
            oos.writeObject(hearts);
            JOptionPane.showMessageDialog(frame, "Game progress saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load saved game state from a file - upon opening to confirm feature.Tested many times. 
    public static void loadProgress() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("game_save.dat"))) {
            score = (int) ois.readObject();
            hearts = (int) ois.readObject();

            scoreLabel.setText("Score: " + score);
            heartsLabel.setText("Hearts: " + "♥".repeat(hearts));
            JOptionPane.showMessageDialog(frame, "Game progress loaded.");
        } catch (IOException e) {
            System.out.println("Error loading game: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found: " + e.getMessage());
        }
    }
}
