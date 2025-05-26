package systems.mythical.myjavaproject.example;

public class Rechteck {
    protected int laenge;
    protected int breite;

    public Rechteck(int breite, int laenge) {
        this.breite = breite;
        this.laenge = laenge;
    }

    public void setBreite(int breite) { this.breite = breite; }
    public void setLaenge(int laenge) { this.laenge = laenge; }
    public int getBreite() { return breite; }
    public int getLaenge() { return laenge; }

    public int berechneFlaeche() {
        return breite * laenge;
    }

    public int berechneFlaeche(int a, int b) {
        return a * b;
    }

    public int berechneUmfang() {
        return 2 * (breite + laenge);
    }
}