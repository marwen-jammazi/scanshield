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
    <div className="min-h-screen bg-slate-900 px-4 py-12 text-white">
      <main className="mx-auto max-w-2xl">
        <div className="flex items-start justify-between gap-4">
          <h1 className="text-4xl font-bold text-emerald-400">ScamShield</h1>
          {user ? (
            <div className="text-end text-sm">
              <p className="text-slate-300">{user.email}</p>
              <button onClick={handleLogout} className="text-slate-400 hover:text-emerald-300">
                Log out
              </button>
            </div>
          ) : (
            <button
              onClick={() => setShowAuth((v) => !v)}
              className="rounded-full border border-slate-600 px-4 py-1.5 text-sm text-slate-300 hover:border-emerald-500 hover:text-emerald-300"
            >
              Log in / Sign up
            </button>
          )}
        </div>

        <p className="mt-2 text-slate-400">
          Paste a suspicious message or link in Arabic, French or English and see why it might be a scam.
        </p>

        {!user && showAuth && <AuthForm onAuthenticated={handleAuthenticated} />}

        <textarea
          dir="auto"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          rows={6}
          placeholder="Paste the message here..."
          className="mt-6 w-full rounded-xl border border-slate-700 bg-slate-800 p-4 text-white placeholder-slate-500 focus:border-emerald-500 focus:outline-none"
        />

        <div className="mt-3 flex flex-wrap items-center gap-2">
          <span className="text-sm text-slate-400">Try an example:</span>
          {EXAMPLES.map((ex) => (
            <button
              key={ex.label}
              onClick={() => setMessage(ex.text)}
              className="rounded-full border border-slate-600 px-3 py-1 text-sm text-slate-300 hover:border-emerald-500 hover:text-emerald-300"
            >
              {ex.label}
            </button>
          ))}
        </div>

        <button
          onClick={handleScan}
          disabled={loading || message.trim() === ''}
          className="mt-6 w-full rounded-xl bg-emerald-500 py-3 font-semibold text-slate-900 hover:bg-emerald-400 disabled:cursor-not-allowed disabled:opacity-50"
        >
          {loading ? 'Scanning...' : 'Scan message'}
        </button>

        {error && <p className="mt-4 text-red-400">{error}</p>}
        {result && <ResultCard result={result} />}

        {user ? (
          <HistoryList items={history} onSelect={handleSelect} />
        ) : (
          <p className="mt-10 text-sm text-slate-500">
            Guest scans are not stored. Log in to keep a private history of your scans.
          </p>
        )}
      </main>
    </div>
  )
}