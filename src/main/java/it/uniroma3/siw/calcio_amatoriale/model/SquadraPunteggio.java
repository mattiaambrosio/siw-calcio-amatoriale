package it.uniroma3.siw.calcio_amatoriale.model; // O .dto

public class SquadraPunteggio implements Comparable<SquadraPunteggio> {
    private Squadra squadra;
    private int punti = 0;
    private int giocate = 0;
    private int vittorie = 0;
    private int pareggi = 0;
    private int sconfitte = 0;
    private int golFatti = 0;
    private int golSubiti = 0;

    public SquadraPunteggio(Squadra squadra) {
        this.squadra = squadra;
    }

    // Metodo per aggiungere i dati di una partita giocata
    public void aggiungiPartita(int fatti, int subiti) {
        this.giocate++;
        this.golFatti += fatti;
        this.golSubiti += subiti;
        if (fatti > subiti) {
            this.punti += 3;
            this.vittorie++;
        } else if (fatti == subiti) {
            this.punti += 1;
            this.pareggi++;
        } else {
            this.sconfitte++;
        }
    }

    // Ordinamento: chi ha più punti sta sopra
    @Override
    public int compareTo(SquadraPunteggio altro) {
        return Integer.compare(altro.punti, this.punti);
    }

    // GETTER (Fondamentali per Thymeleaf)
    public Squadra getSquadra() { return squadra; }
    public int getPunti() { return punti; }
    public int getGiocate() { return giocate; }
    public int getVittorie() { return vittorie; }
    public int getPareggi() { return pareggi; }
    public int getSconfitte() { return sconfitte; }
    public int getGolFatti() { return golFatti; }
    public int getGolSubiti() { return golSubiti; }
}