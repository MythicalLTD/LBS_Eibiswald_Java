package systems.mythical.myjavaproject.example;

public class Kreis {
    private final int radius;
    public static final double PI = 3.141592653589793;

    public Kreis(int radius) {
        this.radius = radius;
    }

    public double berechneFlaeche() {
        return PI * radius * radius;
    }

    public static void printPi() {
        System.out.println("PI = " + PI);
    }
} 