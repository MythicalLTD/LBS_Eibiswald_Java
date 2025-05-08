package systems.mythical.myjavaproject.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class CalculatorGui extends JFrame {
    private final JTextField number1Field;
    private final JTextField number2Field;
    private final JComboBox<String> operationCombo;
    private final JLabel resultLabel;
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    
    public CalculatorGui() {
        setTitle("Taschenrechner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title panel
        JLabel titleLabel = new JLabel("Taschenrechner", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(BUTTON_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Center panel for inputs and result
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(4, 1, 10, 15));
        centerPanel.setBackground(BACKGROUND_COLOR);
        
        // First number input
        JPanel number1Panel = createInputPanel("Erste Zahl:", number1Field = new JTextField(10));
        
        // Second number input
        JPanel number2Panel = createInputPanel("Zweite Zahl:", number2Field = new JTextField(10));
        
        // Operation selection
        JPanel operationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        operationPanel.setBackground(BACKGROUND_COLOR);
        JLabel operationLabel = new JLabel("Operation:");
        operationLabel.setFont(LABEL_FONT);
        String[] operations = {"+", "-", "*", "/"};
        operationCombo = new JComboBox<>(operations);
        operationCombo.setFont(LABEL_FONT);
        operationCombo.setPreferredSize(new Dimension(100, 30));
        operationPanel.add(operationLabel);
        operationPanel.add(operationCombo);
        
        // Result panel
        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        resultPanel.setBackground(BACKGROUND_COLOR);
        resultLabel = new JLabel("", SwingConstants.CENTER);
        resultLabel.setFont(LABEL_FONT);
        resultPanel.add(resultLabel);
        
        centerPanel.add(number1Panel);
        centerPanel.add(number2Panel);
        centerPanel.add(operationPanel);
        centerPanel.add(resultPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        JButton calculateButton = createStyledButton("Berechnen");
        JButton backButton = createStyledButton("Zurück");
        
        calculateButton.addActionListener(e -> calculate());
        backButton.addActionListener(e -> {
            new GuiMenu();
            dispose();
        });
        
        buttonPanel.add(calculateButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createInputPanel(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(BACKGROUND_COLOR);
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        textField.setFont(LABEL_FONT);
        textField.setPreferredSize(new Dimension(150, 30));
        panel.add(label);
        panel.add(textField);
        return panel;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(LABEL_FONT);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        return button;
    }
    
    private void calculate() {
        try {
            double number1 = Double.parseDouble(number1Field.getText());
            double number2 = Double.parseDouble(number2Field.getText());
            String operation = (String) operationCombo.getSelectedItem();
            double result = 0;
            
            switch (operation) {
                case "+" -> result = number1 + number2;
                case "-" -> result = number1 - number2;
                case "*" -> result = number1 * number2;
                case "/" -> {
                    if (number2 == 0) {
                        resultLabel.setText("Fehler: Division durch Null ist nicht erlaubt!");
                        resultLabel.setForeground(Color.RED);
                        return;
                    }
                    result = number1 / number2;
                }
            }
            
            resultLabel.setText(String.format("Ergebnis: %.2f", result));
            resultLabel.setForeground(BUTTON_COLOR);
        } catch (NumberFormatException ex) {
            resultLabel.setText("Bitte geben Sie gültige Zahlen ein!");
            resultLabel.setForeground(Color.RED);
        }
    }
} 