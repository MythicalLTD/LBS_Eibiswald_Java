package systems.mythical.myjavaproject.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MultiplicationDivision extends JFrame {
    private JTextArea resultArea;
    private JTextField inputField;
    private JButton calculateButton;
    private JButton clearButton;

    public MultiplicationDivision() {
        setTitle("Multiplikation und Division");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Multiplikator:"));
        inputField = new JTextField(10);
        inputPanel.add(inputField);
        calculateButton = new JButton("Berechnen");
        clearButton = new JButton("Löschen");
        inputPanel.add(calculateButton);
        inputPanel.add(clearButton);

        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Add components to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculate();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultArea.setText("");
                inputField.setText("");
            }
        });

        // Add main panel to frame
        add(mainPanel);
    }

    private void calculate() {
        try {
            int multiplier = Integer.parseInt(inputField.getText());
            StringBuilder result = new StringBuilder();
            
            // Multiplication part
            result.append("Multiplikation:\n");
            int currentValue = multiplier;
            for (int i = 2; i <= 10; i++) {
                currentValue = currentValue * i;
                result.append(String.format("%d * %d = %d\n", currentValue/i, i, currentValue));
            }

            // Division part
            result.append("\nDivision:\n");
            for (int i = 2; i <= 10; i++) {
                currentValue = currentValue / i;
                result.append(String.format("%d / %d = %d\n", currentValue*i, i, currentValue));
            }

            resultArea.setText(result.toString());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Bitte geben Sie eine gültige Zahl ein!", 
                "Fehler", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MultiplicationDivision().setVisible(true);
        });
    }
} 