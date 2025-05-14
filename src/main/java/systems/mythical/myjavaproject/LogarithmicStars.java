package systems.mythical.myjavaproject;

public class LogarithmicStars {
    static final int Faktor = 32;

    public static void main(String[] args) {
        // Basis 10 heißt, Wie oft muss man 10 mit sich selbst multiplizieren um die angegebene Zahl zu bekommen?
        for (int i = 80; i > 0; i--) {
            double dLog = Math.log10(i);
            int nStars = (int)(dLog * Faktor);
            for (int j = 0; j < nStars; j++) {
                System.out.print("*");
            }
            System.out.print("\n");
        }
    }
    public static void start() {
        main(null);
    }

    public static int getFaktor() {
        return Faktor;
    }
} 