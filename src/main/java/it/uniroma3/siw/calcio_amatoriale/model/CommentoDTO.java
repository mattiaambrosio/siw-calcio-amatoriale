package it.uniroma3.siw.calcio_amatoriale.model;

/**
 * DTO usato per serializzare i commenti in JSON verso React.
 * Evita di esporre dati sensibili come la password di Credentials.
 */
public class CommentoDTO {

    private Long id;
    private String testo;
    private String dataCreazione;
    private String autoreNome;
    private String autoreEmail;

    public CommentoDTO() {}

    public CommentoDTO(Commento c) {
        this.id = c.getId();
        this.testo = c.getTesto();
        this.dataCreazione = c.getDataCreazione() != null
                ? c.getDataCreazione().toString()
                : "";
        if (c.getAutore() != null) {
            this.autoreNome = c.getAutore().getUser() != null
                    ? c.getAutore().getUser().getNome() + " " + c.getAutore().getUser().getCognome()
                    : c.getAutore().getEmail();
            this.autoreEmail = c.getAutore().getEmail();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }

    public String getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(String dataCreazione) { this.dataCreazione = dataCreazione; }

    public String getAutoreNome() { return autoreNome; }
    public void setAutoreNome(String autoreNome) { this.autoreNome = autoreNome; }

    public String getAutoreEmail() { return autoreEmail; }
    public void setAutoreEmail(String autoreEmail) { this.autoreEmail = autoreEmail; }
}
