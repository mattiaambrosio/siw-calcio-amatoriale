package it.uniroma3.siw.calcio_amatoriale.model;

/**
 * POJO (non @Entity) che raccoglie i risultati di una singola
 * strategia di fetch durante l'analisi sperimentale JPA.
 */
public class StrategiaResult {

    private String nomeStrategia;
    private long tempoMs;           // tempo di esecuzione in millisecondi
    private int numTornei;          // numero di tornei caricati
    private int numTotaleSquadre;   // totale squadre caricate (verifica correttezza)
    private String descrizione;     // spiegazione della strategia
    private String queryGenerata;   // SQL rappresentativo generato

    public StrategiaResult() {}

    public StrategiaResult(String nomeStrategia, String descrizione, String queryGenerata) {
        this.nomeStrategia = nomeStrategia;
        this.descrizione = descrizione;
        this.queryGenerata = queryGenerata;
    }

    // Getter e Setter
    public String getNomeStrategia() { return nomeStrategia; }
    public void setNomeStrategia(String s) { this.nomeStrategia = s; }

    public long getTempoMs() { return tempoMs; }
    public void setTempoMs(long t) { this.tempoMs = t; }

    public int getNumTornei() { return numTornei; }
    public void setNumTornei(int n) { this.numTornei = n; }

    public int getNumTotaleSquadre() { return numTotaleSquadre; }
    public void setNumTotaleSquadre(int n) { this.numTotaleSquadre = n; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String d) { this.descrizione = d; }

    public String getQueryGenerata() { return queryGenerata; }
    public void setQueryGenerata(String q) { this.queryGenerata = q; }
}
