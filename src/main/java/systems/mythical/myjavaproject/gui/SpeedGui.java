package systems.mythical.myjavaproject.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class SpeedGui extends JFrame {
    private final JTextField speedField;
    private final JLabel resultLabel;
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    
    public SpeedGui() {
        setTitle("Geschwindigkeitsüberschreitung");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title panel
        JLabel titleLabel = new JLabel("Geschwindigkeitsüberschreitung berechnen", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(BUTTON_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Center panel for input and result
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1, 10, 20));
        centerPanel.setBackground(BACKGROUND_COLOR);
        
        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        inputPanel.setBackground(BACKGROUND_COLOR);
        JLabel speedLabel = new JLabel("Geschwindigkeitsüberschreitung (km/h):");
        speedLabel.setFont(LABEL_FONT);
        speedField = new JTextField(10);
        speedField.setFont(LABEL_FONT);
        inputPanel.add(speedLabel);
        inputPanel.add(speedField);
        
        // Result panel
        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        resultPanel.setBackground(BACKGROUND_COLOR);
        resultLabel = new JLabel("", SwingConstants.CENTER);
        resultLabel.setFont(LABEL_FONT);
        resultPanel.add(resultLabel);
        
        centerPanel.add(inputPanel);
        centerPanel.add(resultPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        JButton calculateButton = createStyledButton("Berechnen");
        JButton backButton = createStyledButton("Zurück");
        
        calculateButton.addActionListener(e -> calculateFine());
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
    
    private void calculateFine() {
        try {
            int speedExcess = Integer.parseInt(speedField.getText());
            int fine;
            
            if (speedExcess <= 10) {
                fine = 0;
            } else if (speedExcess <= 30) {
                fine = 50;
            } else if (speedExcess <= 45) {
                fine = 100;
            } else if (speedExcess <= 60) {
                fine = 150;
            } else {
                fine = 200;
            }
            
            resultLabel.setText("Die Strafe beträgt: " + fine + " Euro");
            resultLabel.setForeground(BUTTON_COLOR);
        } catch (NumberFormatException ex) {
            resultLabel.setText("Bitte geben Sie eine gültige Zahl ein!");
            resultLabel.setForeground(Color.RED);
        }
    }
} 