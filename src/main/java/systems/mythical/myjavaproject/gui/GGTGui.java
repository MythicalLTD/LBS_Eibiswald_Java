package systems.mythical.myjavaproject.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class GGTGui extends JFrame {
    private final JTextField number1Field;
    private final JTextField number2Field;
    private final JTextArea resultArea;
    private final JButton calculateButton;
    private final JButton backButton;

    public GGTGui() {
        setTitle("Größter gemeinsamer Teiler (ggT) Rechner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            System.out.println("Fehler beim setzen des LookAndFeel: " + e.getMessage());
        }

        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Eingabe"));

        inputPanel.add(new JLabel("Erste Zahl:"));
        number1Field = new JTextField();
        inputPanel.add(number1Field);

        inputPanel.add(new JLabel("Zweite Zahl:"));
        number2Field = new JTextField();
        inputPanel.add(number2Field);

        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Berechnungsschritte"));

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        calculateButton = new JButton("Berechnen");
        backButton = new JButton("Zurück zum Menü");
        
        calculateButton.addActionListener((ActionEvent e) -> {
            calculateGGT();
        });
        
        backButton.addActionListener((ActionEvent e) -> {
            new GuiMenu().setVisible(true);
            dispose();
        });
        
        buttonPanel.add(calculateButton);
        buttonPanel.add(backButton);

        // Add components to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);
        setVisible(true);
    }

    private void calculateGGT() {
        try {
            int number1 = Math.abs(Integer.parseInt(number1Field.getText().trim()));
            int number2 = Math.abs(Integer.parseInt(number2Field.getText().trim()));

            StringBuilder steps = new StringBuilder();
            steps.append("Berechnungsschritte:\n");
            steps.append("a = ").append(number1).append(", b = ").append(number2).append("\n\n");

            int a = number1;
            int b = number2;
            int remainder;

            while (b != 0) {
                remainder = a % b;
                steps.append(a).append(" % ").append(b).append(" = ").append(remainder).append("\n");
                a = b;
                b = remainder;
                steps.append("a = ").append(a).append(", b = ").append(b).append("\n\n");
            }

            steps.append("Der größte gemeinsame Teiler von ").append(number1)
                 .append(" und ").append(number2)
                 .append(" ist: ").append(a);

            resultArea.setText(steps.toString());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Bitte geben Sie gültige Zahlen ein!",
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GGTGui().setVisible(true);
        });
    }
}
