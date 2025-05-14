package systems.mythical.myjavaproject.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class NumberGuessingGameGui extends JFrame {
    private final Random random;
    private JTextField textField;
    private JTextArea resultArea;
    private JButton guessButton;

    public NumberGuessingGameGui() {
        this.random = new Random();
        setTitle("Number Guessing Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Rate eine Zahl (1-100):"));
        textField = new JTextField(10);
        inputPanel.add(textField);
        guessButton = new JButton("Guess");
        inputPanel.add(guessButton);

        // Result area
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Add components to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add action listener
        guessButton.addActionListener((ActionEvent e) -> {
            try {
                int guess = Integer.parseInt(textField.getText());
                int targetNumber = random.nextInt(100) + 1;
                if (guess < targetNumber) {
                    resultArea.append("Zahl zu klein!\n");
                } else if (guess > targetNumber) {
                    resultArea.append("Zahl zu groß!\n");
                } else {
                    resultArea.append("Gratulation - nach " + (resultArea.getLineCount() + 1) + " Versuchen erraten!\n");
                    guessButton.setEnabled(false);
                }
                textField.setText("");
            } catch (NumberFormatException ex) {
                resultArea.append("Bitte geben Sie eine gültige Zahl ein!\n");
            }
        });

        // Add main panel to frame
        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new NumberGuessingGameGui().setVisible(true);
        });
    }
} 