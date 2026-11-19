import { useCallback, useEffect, useState } from 'react'
import { getHistory, scanMessage } from './api'
import { clearAuth, getAuth, saveAuth } from './auth'
import AuthForm from './components/AuthForm'
import HistoryList from './components/HistoryList'
import ResultCard from './components/ResultCard'

const EXAMPLES = [
  { label: 'English', text: 'URGENT: your account suspended, confirm your identity at http://bit.ly/x1 and send your password' },
  { label: 'Français', text: 'Votre colis bloqué, payez sous 24h : http://poste-tn.xyz/pay' },
  { label: 'العربية', text: 'عاجل: حسابك موقوف ادخل رمز التحقق عبر http://192.168.4.7/login' },
]

export default function App() {
  const [user, setUser] = useState(getAuth)
  const [showAuth, setShowAuth] = useState(false)
  const [message, setMessage] = useState('')
  const [result, setResult] = useState(null)
  const [history, setHistory] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const refreshHistory = useCallback(async () => {
    try {
      setHistory(await getHistory())
    } catch {
      // If the session expired, getHistory already cleared it: update the UI to match
      setUser(getAuth())
    }
  }, [])

  useEffect(() => {
    if (!user) return
    let cancelled = false
    getHistory()
      .then((items) => {
        if (!cancelled) setHistory(items)
      })
      .catch(() => {
        // An expired session was already cleared by getHistory: sync the UI with it
        if (!cancelled) setUser(getAuth())
      })
    return () => {
      cancelled = true
    }
  }, [user])

  function handleAuthenticated(auth) {
    saveAuth(auth)
    setUser(auth)
    setShowAuth(false)
  }

  function handleLogout() {
    clearAuth()
    setUser(null)
    setHistory([])
  }

  async function handleScan() {
    setLoading(true)
    setError('')
    try {
      setResult(await scanMessage(message))
      // scanMessage may have dropped an expired token, so re-read the login state
      const auth = getAuth()
      setUser(auth)
      if (auth) refreshHistory()
    } catch (e) {
      setResult(null)
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  function handleSelect(item) {
    setMessage(item.message)
    setResult({ score: item.score, level: item.level, reasons: item.reasons })
    setError('')
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  return (
    <div className="min-h-screen px-4 py-10 text-ink">
      <main className="mx-auto max-w-2xl">
        <header className="flex items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <div
              className="clay flex h-14 w-14 shrink-0 items-center justify-center rounded-[1.25rem] text-2xl"
              aria-hidden="true"
            >
              
            </div>
            <h1 className="text-3xl font-extrabold text-[#6c5ce7]">ScamShield</h1>
          </div>

          {user ? (
            <div className="text-end text-sm">
              <p className="font-bold">{user.email}</p>
              <button onClick={handleLogout} className="font-semibold text-ink-soft hover:text-ink">
                Log out
              </button>
            </div>
          ) : (
            <button
              onClick={() => setShowAuth((v) => !v)}
              className="clay-btn rounded-full bg-[#fbf9ff] px-5 py-2.5 text-sm font-bold"
            >
              Log in / Sign up
            </button>
          )}
        </header>

        <p className="mt-6 font-semibold text-ink-soft">
          Paste a suspicious message or link in Arabic, French, English or Arabizi and see why it might be a scam.
        </p>

        {!user && showAuth && <AuthForm onAuthenticated={handleAuthenticated} />}

        <section className="clay mt-8 p-6">
          <textarea
            dir="auto"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            rows={6}
            placeholder="Paste the message here..."
            className="clay-inset w-full resize-y p-4 font-semibold text-ink placeholder:text-ink-soft"
          />

          <div className="mt-4 flex flex-wrap items-center gap-3">
            <span className="text-sm font-bold text-ink-soft">Try an example:</span>
            {EXAMPLES.map((ex) => (
              <button
                key={ex.label}
                onClick={() => setMessage(ex.text)}
                className="clay-btn rounded-full bg-[#fbf9ff] px-4 py-1.5 text-sm font-bold"
              >
                {ex.label}
              </button>
            ))}
          </div>

          <button
            onClick={handleScan}
            disabled={loading || message.trim() === ''}
            className="clay-btn clay-mint mt-6 w-full py-4 text-lg font-extrabold"
          >
            {loading ? 'Scanning...' : 'Scan message'}
          </button>
        </section>

        {error && (
          <p role="alert" className="mt-5 font-bold text-[#b42323]">
            {error}
          </p>
        )}
        {result && <ResultCard result={result} />}

        {user ? (
          <HistoryList items={history} onSelect={handleSelect} />
        ) : (
          <p className="mt-12 text-sm font-semibold text-ink-soft">
            Guest scans are not stored. Log in to keep a private history of your scans.
          </p>
        )}
      </main>
    </div>
  )
}