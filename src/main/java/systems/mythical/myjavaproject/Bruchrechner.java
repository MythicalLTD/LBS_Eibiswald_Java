package systems.mythical.myjavaproject;

import java.util.Scanner;

public class Bruchrechner {
    private final Scanner scanner;

    public Bruchrechner() {
        this.scanner = new Scanner(System.in);
    }
    
    public void bruchrechner() {
        boolean continueCalculation = true;
        
        while (continueCalculation) {
            try {
                System.out.println("""
                                   Bruchrechnen! Bitte geben Sie die gew\u00fcnschte Rechnung in folgendem Format ein: 
                                   a/b * x/y Gehen Sie sicher, dass Sie das Format genau befolgen!""");
                
                Fraction leftFraction = parseFraction(scanner.next());
                String operator = scanner.next();
                Fraction rightFraction = parseFraction(scanner.next());
                
                Fraction result = calculate(leftFraction, rightFraction, operator);
                Fraction simplifiedResult = result.simplify();
                
                System.out.println("Das Ergebnis dieser Rechnung ist " + result);
                System.out.println("Der gekürzte Bruch ist " + simplifiedResult);
                
                System.out.println("Wollen Sie wiederholen? j/n");
                continueCalculation = scanner.next().equalsIgnoreCase("j");
                
            } catch (IllegalArgumentException e) {
                System.out.println("Fehler: " + e.getMessage());
            }
        }
    }
    
    private Fraction parseFraction(String input) {
        String[] parts = input.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Ungültiges Bruchformat. Bitte verwenden Sie das Format a/b");
        }
        
        try {
            int numerator = Integer.parseInt(parts[0]);
            int denominator = Integer.parseInt(parts[1]);
            if (denominator == 0) {
                throw new IllegalArgumentException("Der Nenner darf nicht 0 sein");
            }
            return new Fraction(numerator, denominator);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ungültige Zahlen im Bruch");
        }
    }
    
    private Fraction calculate(Fraction left, Fraction right, String operator) {
        return switch (operator) {
            case "+" -> left.add(right);
            case "-" -> left.subtract(right);
            case "*" -> left.multiply(right);
            case "/" -> left.divide(right);
            default -> throw new IllegalArgumentException("Ungültiger Operator. Erlaubt sind: +, -, *, /");
        };
    }
    
    private static class Fraction {
        private final int numerator;
        private final int denominator;
        
        public Fraction(int numerator, int denominator) {
            this.numerator = numerator;
            this.denominator = denominator;
        }
        
        public Fraction add(Fraction other) {
            int newNumerator = this.numerator * other.denominator + this.denominator * other.numerator;
            int newDenominator = this.denominator * other.denominator;
            return new Fraction(newNumerator, newDenominator);
        }
        
        public Fraction subtract(Fraction other) {
            int newNumerator = this.numerator * other.denominator - this.denominator * other.numerator;
            int newDenominator = this.denominator * other.denominator;
            return new Fraction(newNumerator, newDenominator);
        }
        
        public Fraction multiply(Fraction other) {
            return new Fraction(this.numerator * other.numerator, this.denominator * other.denominator);
        }
        
        public Fraction divide(Fraction other) {
            if (other.numerator == 0) {
                throw new IllegalArgumentException("Division durch Null ist nicht erlaubt");
            }
            return new Fraction(this.numerator * other.denominator, this.denominator * other.numerator);
        }
        
        public Fraction simplify() {
            int gcd = calculateGCD(Math.abs(numerator), Math.abs(denominator));
            return new Fraction(numerator / gcd, denominator / gcd);
        }
        
        private int calculateGCD(int a, int b) {
            while (b != 0) {
                int temp = b;
                b = a % b;
                a = temp;
            }
            return a;
        }
        
        @Override
        public String toString() {
            return numerator + "/" + denominator;
        }
    }
}
