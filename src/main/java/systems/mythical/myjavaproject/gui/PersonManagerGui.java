package systems.mythical.myjavaproject.gui;

import systems.mythical.myjavaproject.Person;
import systems.mythical.myjavaproject.PersonManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PersonManagerGui extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(45, 45, 45);
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    private static final Color BUTTON_HOVER_COLOR = new Color(100, 149, 237);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final int MAX_PERSONS = 20;
    
    private final ArrayList<Person> persons;
    private final JPanel displayPanel;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField streetField;
    private JTextField houseNumberField;
    private JTextField postalCodeField;
    private JTextField cityField;
    private JTextField pointsField;
    
    public PersonManagerGui() {
        persons = new ArrayList<>();
        
        setTitle("Person Manager");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Main panel with gradient background
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Person Manager", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Center panel for input and display
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        
        // Input panel
        JPanel inputPanel = createInputPanel();
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        
        // Display panel
        displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        displayPanel.setBackground(new Color(60, 60, 60));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(displayPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setOpaque(false);
        
        // First Name
        JLabel firstNameLabel = new JLabel("Vorname:");
        firstNameLabel.setFont(LABEL_FONT);
        firstNameLabel.setForeground(TEXT_COLOR);
        firstNameField = new JTextField();
        
        // Last Name
        JLabel lastNameLabel = new JLabel("Nachname:");
        lastNameLabel.setFont(LABEL_FONT);
        lastNameLabel.setForeground(TEXT_COLOR);
        lastNameField = new JTextField();
        
        // Street
        JLabel streetLabel = new JLabel("Straße:");
        streetLabel.setFont(LABEL_FONT);
        streetLabel.setForeground(TEXT_COLOR);
        streetField = new JTextField();
        
        // House Number
        JLabel houseNumberLabel = new JLabel("Hausnummer:");
        houseNumberLabel.setFont(LABEL_FONT);
        houseNumberLabel.setForeground(TEXT_COLOR);
        houseNumberField = new JTextField();
        
        // Postal Code
        JLabel postalCodeLabel = new JLabel("PLZ:");
        postalCodeLabel.setFont(LABEL_FONT);
        postalCodeLabel.setForeground(TEXT_COLOR);
        postalCodeField = new JTextField();
        
        // City
        JLabel cityLabel = new JLabel("Ort:");
        cityLabel.setFont(LABEL_FONT);
        cityLabel.setForeground(TEXT_COLOR);
        cityField = new JTextField();
        
        // Points
        JLabel pointsLabel = new JLabel("Punkte (0-100):");
        pointsLabel.setFont(LABEL_FONT);
        pointsLabel.setForeground(TEXT_COLOR);
        pointsField = new JTextField();
        
        panel.add(firstNameLabel);
        panel.add(firstNameField);
        panel.add(lastNameLabel);
        panel.add(lastNameField);
        panel.add(streetLabel);
        panel.add(streetField);
        panel.add(houseNumberLabel);
        panel.add(houseNumberField);
        panel.add(postalCodeLabel);
        panel.add(postalCodeField);
        panel.add(cityLabel);
        panel.add(cityField);
        panel.add(pointsLabel);
        panel.add(pointsField);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setOpaque(false);
        
        CustomButton addButton = createCustomButton("Hinzufügen");
        CustomButton showStatsButton = createCustomButton("Statistik anzeigen");
        CustomButton backButton = createCustomButton("Zurück zum Hauptmenü");
        
        addButton.addActionListener(e -> addPerson());
        showStatsButton.addActionListener(e -> showStatistics());
        backButton.addActionListener(e -> {
            new GuiMenu();
            dispose();
        });
        
        panel.add(addButton);
        panel.add(showStatsButton);
        panel.add(backButton);
        
        return panel;
    }
    
    private void addPerson() {
        try {
            if (persons.size() >= MAX_PERSONS) {
                JOptionPane.showMessageDialog(this, 
                    "Maximale Anzahl an Personen (" + MAX_PERSONS + ") erreicht!", 
                    "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String street = streetField.getText().trim();
            String houseNumber = houseNumberField.getText().trim();
            String postalCode = postalCodeField.getText().trim();
            String city = cityField.getText().trim();
            int points = Integer.parseInt(pointsField.getText().trim());
            
            if (firstName.isEmpty() || lastName.isEmpty() || street.isEmpty() || 
                houseNumber.isEmpty() || postalCode.isEmpty() || city.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Bitte füllen Sie alle Felder aus.", 
                    "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (points < 0 || points > 100) {
                JOptionPane.showMessageDialog(this, 
                    "Punkte müssen zwischen 0 und 100 liegen.", 
                    "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Person person = new Person(firstName, lastName, street, houseNumber, 
                                     postalCode, city, points);
            persons.add(person);
            updateDisplay();
            
            // Clear fields
            firstNameField.setText("");
            lastNameField.setText("");
            streetField.setText("");
            houseNumberField.setText("");
            postalCodeField.setText("");
            cityField.setText("");
            pointsField.setText("");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Bitte geben Sie eine gültige Zahl für die Punkte ein.", 
                "Fehler", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), 
                "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateDisplay() {
        displayPanel.removeAll();
        
        if (persons.isEmpty()) {
            JLabel emptyLabel = new JLabel("Keine Personen vorhanden");
            emptyLabel.setFont(LABEL_FONT);
            emptyLabel.setForeground(TEXT_COLOR);
            displayPanel.add(emptyLabel);
        } else {
            for (Person person : persons) {
                JLabel personLabel = new JLabel("<html>" + person.toString().replace("\n", "<br>") + "</html>");
                personLabel.setFont(LABEL_FONT);
                personLabel.setForeground(TEXT_COLOR);
                displayPanel.add(personLabel);
                displayPanel.add(Box.createVerticalStrut(10));
            }
        }
        
        displayPanel.revalidate();
        displayPanel.repaint();
    }
    
    private void showStatistics() {
        if (persons.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Keine Personen vorhanden für die Statistik.", 
                "Statistik", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int min = persons.get(0).getPunktezahl();
        int max = min;
        int sum = min;
        
        for (int i = 1; i < persons.size(); i++) {
            int points = persons.get(i).getPunktezahl();
            min = Math.min(min, points);
            max = Math.max(max, points);
            sum += points;
        }
        
        double average = (double) sum / persons.size();
        
        String stats = String.format("Statistik:\nMinimum: %d\nMaximum: %d\nDurchschnitt: %.2f", 
            min, max, average);
        JOptionPane.showMessageDialog(this, stats, "Statistik", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private CustomButton createCustomButton(String text) {
        CustomButton button = new CustomButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBackground(BUTTON_COLOR);
        button.setHoverColor(BUTTON_HOVER_COLOR);
        button.setPreferredSize(new Dimension(200, 40));
        return button;
    }
    
    // Custom gradient panel
    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            int w = getWidth();
            int h = getHeight();
            
            Color color1 = new Color(45, 45, 45);
            Color color2 = new Color(70, 130, 180);
            
            GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    }
    
    // Custom button with hover effect and rounded corners
    private static class CustomButton extends JButton {
        private Color hoverColor;
        private boolean isHovered = false;
        
        public CustomButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }
        
        public void setHoverColor(Color color) {
            this.hoverColor = color;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (isHovered) {
                g2d.setColor(hoverColor);
            } else {
                g2d.setColor(getBackground());
            }
            
            g2d.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
            
            FontMetrics metrics = g2d.getFontMetrics();
            int x = (getWidth() - metrics.stringWidth(getText())) / 2;
            int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
            
            g2d.setColor(getForeground());
            g2d.drawString(getText(), x, y);
        }
    }
} 