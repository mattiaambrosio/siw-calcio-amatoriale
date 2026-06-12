/* =====================================================
   COMPONENTE REACT: Sezione Commenti Partita
   Tecnologia: React 18 + Babel Standalone (CDN, no build)
   ===================================================== */

/* commento */

const { useState, useEffect, useCallback } = React;

// ─── Utility: legge il cookie CSRF di Spring Security ───
function getCsrfToken() {
    const meta = document.querySelector('meta[name="_csrf"]');
    return meta ? meta.getAttribute('content') : '';
}
function getCsrfHeader() {
    const meta = document.querySelector('meta[name="_csrf_header"]');
    return meta ? meta.getAttribute('content') : 'X-CSRF-TOKEN';
}

// ─── Componente singolo commento ─────────────────────────
function CommentoCard({ commento, utenteEmail, onDelete, onEdit }) {
    const [inModifica, setInModifica] = useState(false);
    const [testoModifica, setTestoModifica] = useState(commento.testo);
    const isOwner = utenteEmail && utenteEmail === commento.autoreEmail;

    function formattaData(dataStr) {
        if (!dataStr) return '';
        const d = new Date(dataStr);
        return d.toLocaleDateString('it-IT', {
            day: '2-digit', month: '2-digit', year: 'numeric',
            hour: '2-digit', minute: '2-digit'
        });
    }

    async function salvaModifica() {
        if (!testoModifica.trim()) return;
        await onEdit(commento.id, testoModifica.trim());
        setInModifica(false);
    }

    return (
        <div className="rc-comment-card" style={{
            background: 'rgba(255,255,255,0.03)',
            border: '1px solid rgba(255,255,255,0.07)',
            borderRadius: '14px',
            padding: '1.2rem 1.5rem',
            marginBottom: '1rem',
            transition: 'border-color 0.2s'
        }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.7rem' }}>
                <span style={{ fontWeight: 600, color: '#32D74B', fontSize: '0.9rem' }}>
                    {commento.autoreNome || commento.autoreEmail}
                </span>
                <span style={{ fontSize: '0.75rem', color: '#86868b' }}>
                    {formattaData(commento.dataCreazione)}
                </span>
            </div>

            {inModifica ? (
                <div>
                    <textarea
                        value={testoModifica}
                        onChange={e => setTestoModifica(e.target.value)}
                        maxLength={1000}
                        rows={3}
                        style={{
                            width: '100%', padding: '0.7rem', borderRadius: '8px',
                            background: 'rgba(0,0,0,0.3)', border: '1px solid rgba(50,215,75,0.4)',
                            color: '#fff', fontFamily: 'Inter, sans-serif', fontSize: '0.9rem',
                            resize: 'vertical', marginBottom: '0.7rem'
                        }}
                    />
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                        <button onClick={salvaModifica} style={btnStyle('#32D74B', '#000')}>
                            💾 Salva
                        </button>
                        <button onClick={() => { setInModifica(false); setTestoModifica(commento.testo); }}
                                style={btnStyle('rgba(255,255,255,0.1)', '#fff')}>
                            Annulla
                        </button>
                    </div>
                </div>
            ) : (
                <>
                    <p style={{ color: '#d2d2d7', lineHeight: 1.6, margin: '0 0 0.8rem 0', fontSize: '0.95rem' }}>
                        {commento.testo}
                    </p>
                    {isOwner && (
                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                            <button onClick={() => setInModifica(true)}
                                    style={btnStyle('rgba(255,255,255,0.08)', '#fff', '0.3rem 0.7rem', '0.78rem')}>
                                ✏️ Modifica
                            </button>
                            <button onClick={() => { if(window.confirm('Eliminare il commento?')) onDelete(commento.id); }}
                                    style={btnStyle('rgba(255,69,58,0.15)', '#FF453A', '0.3rem 0.7rem', '0.78rem')}>
                                🗑️ Elimina
                            </button>
                        </div>
                    )}
                </>
            )}
        </div>
    );
}

// ─── Componente principale: CommentiSection ───────────────
function CommentiSection({ partitaId }) {
    const [commenti, setCommenti] = useState([]);
    const [nuovoTesto, setNuovoTesto] = useState('');
    const [utenteEmail, setUtenteEmail] = useState(null);
    const [autenticato, setAutenticato] = useState(false);
    const [loading, setLoading] = useState(true);
    const [errore, setErrore] = useState(null);
    const [invioInCorso, setInvioInCorso] = useState(false);
    const [filtroRicerca, setFiltroRicerca] = useState('');

    // Carica chi è loggato
    useEffect(() => {
        fetch('/api/me')
            .then(r => r.json())
            .then(data => {
                setAutenticato(data.autenticato);
                if (data.autenticato) setUtenteEmail(data.email);
            })
            .catch(() => {});
    }, []);

    // Carica commenti
    const caricaCommenti = useCallback(() => {
        fetch(`/api/partita/${partitaId}/commenti`)
            .then(r => r.json())
            .then(data => { setCommenti(data); setLoading(false); })
            .catch(() => { setErrore('Errore nel caricamento dei commenti.'); setLoading(false); });
    }, [partitaId]);

    useEffect(() => { caricaCommenti(); }, [caricaCommenti]);

    // Invia nuovo commento
    async function inviaCommento(e) {
        e.preventDefault();
        if (!nuovoTesto.trim() || invioInCorso) return;
        setInvioInCorso(true);
        try {
            const res = await fetch(`/api/partita/${partitaId}/commento`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [getCsrfHeader()]: getCsrfToken()
                },
                body: JSON.stringify({ testo: nuovoTesto.trim() })
            });
            if (res.ok) {
                const nuovoCommento = await res.json();
                setCommenti(prev => [nuovoCommento, ...prev]);
                setNuovoTesto('');
            } else {
                setErrore('Errore nell\'invio del commento.');
            }
        } catch {
            setErrore('Errore di rete.');
        }
        setInvioInCorso(false);
    }

    // Modifica commento esistente
    async function modificaCommento(id, nuovoTestoMod) {
        try {
            const res = await fetch(`/api/commento/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    [getCsrfHeader()]: getCsrfToken()
                },
                body: JSON.stringify({ testo: nuovoTestoMod })
            });
            if (res.ok) {
                const aggiornato = await res.json();
                setCommenti(prev => prev.map(c => c.id === id ? aggiornato : c));
            }
        } catch {
            setErrore('Errore nella modifica.');
        }
    }

    // Elimina commento
    async function eliminaCommento(id) {
        try {
            const res = await fetch(`/api/commento/${id}`, {
                method: 'DELETE',
                headers: { [getCsrfHeader()]: getCsrfToken() }
            });
            if (res.ok) {
                setCommenti(prev => prev.filter(c => c.id !== id));
            }
        } catch {
            setErrore('Errore nell\'eliminazione.');
        }
    }

    return (
        <div style={{
            background: 'rgba(28,28,30,0.6)',
            backdropFilter: 'blur(20px)',
            border: '1px solid rgba(255,255,255,0.08)',
            borderRadius: '20px',
            padding: '2rem',
            marginTop: '1.5rem'
        }}>
            {/* Titolo sezione */}
            <h2 style={{
                fontSize: '1.1rem', fontWeight: 600, color: '#fff',
                marginBottom: '1.5rem', paddingBottom: '1rem',
                borderBottom: '1px solid rgba(255,255,255,0.06)'
            }}>
                💬 Commenti
                <span style={{
                    marginLeft: '0.75rem', fontSize: '0.7rem', fontWeight: 500,
                    background: 'rgba(50,215,75,0.15)', color: '#32D74B',
                    padding: '0.2rem 0.6rem', borderRadius: '20px'
                }}>React</span>
            </h2>

            {/* Errore */}
            {errore && (
                <div style={{
                    background: 'rgba(255,69,58,0.1)', border: '1px solid rgba(255,69,58,0.3)',
                    borderRadius: '10px', padding: '0.8rem 1rem', marginBottom: '1rem',
                    color: '#FF453A', fontSize: '0.9rem'
                }}>
                    ⚠️ {errore}
                    <button onClick={() => setErrore(null)} style={{ marginLeft: '1rem', background: 'none', border: 'none', color: '#FF453A', cursor: 'pointer' }}>✕</button>
                </div>
            )}

            {/* Form inserimento (solo utenti loggati) */}
            {autenticato ? (
                <form onSubmit={inviaCommento} style={{
                    background: 'rgba(255,255,255,0.03)', border: '1px solid rgba(255,255,255,0.08)',
                    borderRadius: '14px', padding: '1.2rem', marginBottom: '1.5rem'
                }}>
                    <label style={{ display: 'block', color: '#d2d2d7', marginBottom: '0.5rem', fontSize: '0.9rem', fontWeight: 500 }}>
                        Scrivi un commento:
                    </label>
                    <textarea
                        value={nuovoTesto}
                        onChange={e => setNuovoTesto(e.target.value)}
                        placeholder="Condividi il tuo pensiero sulla partita..."
                        maxLength={1000}
                        rows={3}
                        required
                        style={{
                            width: '100%', padding: '0.8rem', borderRadius: '10px',
                            background: 'rgba(0,0,0,0.25)', border: '1px solid rgba(255,255,255,0.1)',
                            color: '#fff', fontFamily: 'Inter, sans-serif', fontSize: '0.95rem',
                            resize: 'vertical', marginBottom: '0.8rem',
                            outline: 'none', transition: 'border-color 0.2s'
                        }}
                        onFocus={e => e.target.style.borderColor = '#32D74B'}
                        onBlur={e => e.target.style.borderColor = 'rgba(255,255,255,0.1)'}
                    />
                    <button type="submit" disabled={invioInCorso || !nuovoTesto.trim()}
                            style={btnStyle(invioInCorso ? '#555' : '#32D74B', '#000')}>
                        {invioInCorso ? '⏳ Pubblicazione...' : '💬 Pubblica Commento'}
                    </button>
                </form>
            ) : (
                <div style={{
                    textAlign: 'center', padding: '1.2rem',
                    background: 'rgba(10,132,255,0.05)', border: '1px solid rgba(10,132,255,0.15)',
                    borderRadius: '12px', marginBottom: '1.5rem', color: '#86868b'
                }}>
                    <a href="/login" style={{ color: '#0A84FF', fontWeight: 600 }}>Accedi</a>
                    {' '}o{' '}
                    <a href="/register" style={{ color: '#0A84FF', fontWeight: 600 }}>registrati</a>
                    {' '}per lasciare un commento.
                </div>
            )}

            {/* Lista commenti */}
            {loading ? (
                <div style={{ textAlign: 'center', padding: '2rem', color: '#86868b' }}>⏳ Caricamento commenti...</div>
            ) : commenti.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '2rem', color: '#86868b', fontStyle: 'italic' }}>
                    Ancora nessun commento. Sii il primo! 👋
                </div>
            ) : (
                <>
                    {/* Barra di ricerca */}
                    <div style={{
                        marginBottom: '1.5rem', display: 'flex', gap: '0.8rem', alignItems: 'center'
                    }}>
                        <span style={{ color: '#86868b', fontSize: '0.9rem' }}>🔍 Filtra:</span>
                        <input
                            type="text"
                            value={filtroRicerca}
                            onChange={e => setFiltroRicerca(e.target.value)}
                            placeholder="Cerca nei commenti..."
                            style={{
                                flex: 1, padding: '0.6rem 1rem', borderRadius: '10px',
                                background: 'rgba(0,0,0,0.25)', border: '1px solid rgba(255,255,255,0.1)',
                                color: '#fff', fontFamily: 'Inter, sans-serif', fontSize: '0.9rem',
                                outline: 'none', transition: 'border-color 0.2s'
                            }}
                            onFocus={e => e.target.style.borderColor = '#32D74B'}
                            onBlur={e => e.target.style.borderColor = 'rgba(255,255,255,0.1)'}
                        />
                        {filtroRicerca && (
                            <button 
                                onClick={() => setFiltroRicerca('')}
                                style={btnStyle('rgba(255,255,255,0.08)', '#fff', '0.3rem 0.7rem', '0.78rem')}
                            >
                                ✕ Cancella
                            </button>
                        )}
                    </div>

                    {/* Commenti filtrati */}
                    {commenti
                        .filter(c => 
                            c.testo.toLowerCase().includes(filtroRicerca.toLowerCase()) ||
                            (c.autoreNome && c.autoreNome.toLowerCase().includes(filtroRicerca.toLowerCase())) ||
                            c.autoreEmail.toLowerCase().includes(filtroRicerca.toLowerCase())
                        )
                        .length === 0 ? (
                        <div style={{ textAlign: 'center', padding: '2rem', color: '#86868b', fontStyle: 'italic' }}>
                            Nessun commento corrisponde al filtro. 🔍
                        </div>
                    ) : (
                        commenti
                            .filter(c => 
                                c.testo.toLowerCase().includes(filtroRicerca.toLowerCase()) ||
                                (c.autoreNome && c.autoreNome.toLowerCase().includes(filtroRicerca.toLowerCase())) ||
                                c.autoreEmail.toLowerCase().includes(filtroRicerca.toLowerCase())
                            )
                            .map(c => (
                                <CommentoCard
                                    key={c.id}
                                    commento={c}
                                    utenteEmail={utenteEmail}
                                    onDelete={eliminaCommento}
                                    onEdit={modificaCommento}
                                />
                            ))
                    )}
                </>
            )}
        </div>
    );
}

// ─── Helper stile bottoni inline ─────────────────────────
function btnStyle(bg, color, padding = '0.55rem 1.2rem', fontSize = '0.9rem') {
    return {
        background: bg, color, border: 'none', borderRadius: '980px',
        padding, fontSize, fontWeight: 600, cursor: 'pointer',
        fontFamily: 'Inter, sans-serif', transition: 'opacity 0.2s'
    };
}

// ─── Mount del componente ─────────────────────────────────
const rootEl = document.getElementById('react-commenti-root');
if (rootEl) {
    const partitaId = rootEl.getAttribute('data-partita-id');
    const root = ReactDOM.createRoot(rootEl);
    root.render(<CommentiSection partitaId={partitaId} />);
}
