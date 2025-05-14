package systems.mythical.myjavaproject.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class GuiMenu extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(45, 45, 45);
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    private static final Color BUTTON_HOVER_COLOR = new Color(100, 149, 237);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 32);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 16);
    
    public GuiMenu() {
        setTitle("Hauptmenü");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Main panel with gradient background
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Willkommen", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Center panel for buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridLayout(4, 1, 20, 20));
        
        // Create custom buttons
        CustomButton speedButton = createCustomButton("Geschwindigkeitsüberschreitung berechnen");
        CustomButton calcButton = createCustomButton("Taschenrechner");
        CustomButton ggtButton = createCustomButton("ggT berechnen");
        CustomButton multiplicationButton = createCustomButton("Multiplikation und Division");
        CustomButton guesGame = createCustomButton("Guesing game");
        CustomButton personManagerButton = createCustomButton("Person Manager");
        CustomButton exitButton = createCustomButton("Beenden");

        // Add action listeners
        speedButton.addActionListener(e -> {
            new SpeedGui();
            dispose();
        });
        
        calcButton.addActionListener(e -> {
            new CalculatorGui();
            dispose();
        });

        ggtButton.addActionListener(e -> {
            new GGTGui();
            dispose();
        });

        multiplicationButton.addActionListener(e -> {
            new MultiplicationDivision();
            dispose();
        });

        guesGame.addActionListener(e -> {
            new NumberGuessingGameGui();
            dispose();
        });

        personManagerButton.addActionListener(e -> {
            new PersonManagerGui();
            dispose();
        });
        
        exitButton.addActionListener(e -> System.exit(0));
        
        centerPanel.add(speedButton);
        centerPanel.add(calcButton);
        centerPanel.add(ggtButton);
        centerPanel.add(multiplicationButton);
        centerPanel.add(guesGame);
        centerPanel.add(personManagerButton);
        centerPanel.add(exitButton);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private CustomButton createCustomButton(String text) {
        CustomButton button = new CustomButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setBackground(BUTTON_COLOR);
        button.setHoverColor(BUTTON_HOVER_COLOR);
        button.setPreferredSize(new Dimension(400, 60));
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
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
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
            
            g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
            
            FontMetrics metrics = g2d.getFontMetrics();
            int x = (getWidth() - metrics.stringWidth(getText())) / 2;
            int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
            
            g2d.setColor(getForeground());
            g2d.drawString(getText(), x, y);
        }
    }
} 