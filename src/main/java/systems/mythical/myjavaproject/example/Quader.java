package systems.mythical.myjavaproject.example;

public class Quader extends Rechteck {
    protected int hoehe;

    public Quader(int breite, int laenge, int hoehe) {
        super(breite, laenge);
        this.hoehe = hoehe;
    }

    public float berechneVolumen() {
        return breite * laenge * hoehe;
    }
} 