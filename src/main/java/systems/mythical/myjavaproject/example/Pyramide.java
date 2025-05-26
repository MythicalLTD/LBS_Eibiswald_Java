package systems.mythical.myjavaproject.example;

public class Pyramide {
    private final int breite;
    private final int laenge;
    private final int hoehe;

    // Constructor for square base
    public Pyramide(int breite, int hoehe) {
        this.breite = breite;
        this.laenge = breite;
        this.hoehe = hoehe;
    }

    // Constructor for rectangular base
    public Pyramide(int breite, int laenge, int hoehe) {
        this.breite = breite;
        this.laenge = laenge;
        this.hoehe = hoehe;
    }

    public float berechneVolumen() {
        return (breite * laenge * hoehe) / 3.0f;
    }
} 