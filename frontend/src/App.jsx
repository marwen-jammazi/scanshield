import { useState } from 'react'
import { scanMessage } from './api'
import ResultCard from './components/ResultCard'

const EXAMPLES = [
  { label: 'English', text: 'URGENT: your account suspended, confirm your identity at http://bit.ly/x1 and send your password' },
  { label: 'Français', text: 'Votre colis bloqué, payez sous 24h : http://poste-tn.xyz/pay' },
  { label: 'العربية', text: 'عاجل: حسابك موقوف ادخل رمز التحقق عبر http://192.168.4.7/login' },
]

export default function App() {
  const [message, setMessage] = useState('')
  const [result, setResult] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  async function handleScan() {
    setLoading(true)
    setError('')
    try {
      setResult(await scanMessage(message))
    } catch (e) {
      setResult(null)
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-slate-900 px-4 py-12 text-white">
      <main className="mx-auto max-w-2xl">
        <h1 className="text-4xl font-bold text-emerald-400">ScamShield</h1>
        <p className="mt-2 text-slate-400">
          Paste a suspicious message or link in Arabic, French or English and see why it might be a scam.
        </p>

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
      </main>
    </div>
  )
}