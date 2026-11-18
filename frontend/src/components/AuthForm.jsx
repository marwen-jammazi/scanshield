import { useState } from 'react'
import { authenticate } from '../api'

export default function AuthForm({ onAuthenticated }) {
  const [mode, setMode] = useState('login')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const isLogin = mode === 'login'

  async function handleSubmit(e) {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      onAuthenticated(await authenticate(mode, email, password))
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const inputClass =
    'w-full rounded-lg border border-slate-700 bg-slate-900 p-3 text-white placeholder-slate-500 focus:border-emerald-500 focus:outline-none'

  return (
    <form onSubmit={handleSubmit} className="mt-6 space-y-3 rounded-xl border border-slate-700 bg-slate-800 p-6">
      <h2 className="text-lg font-semibold">{isLogin ? 'Log in' : 'Create an account'}</h2>

      <input
        type="email"
        required
        autoComplete="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Email"
        className={inputClass}
      />
      <input
        type="password"
        required
        minLength={isLogin ? undefined : 8}
        autoComplete={isLogin ? 'current-password' : 'new-password'}
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder={isLogin ? 'Password' : 'Password (at least 8 characters)'}
        className={inputClass}
      />

      {error && <p className="text-sm text-red-400">{error}</p>}

      <button
        type="submit"
        disabled={loading}
        className="w-full rounded-lg bg-emerald-500 py-3 font-semibold text-slate-900 hover:bg-emerald-400 disabled:opacity-50"
      >
        {loading ? 'Please wait...' : isLogin ? 'Log in' : 'Sign up'}
      </button>

      <button
        type="button"
        onClick={() => {
          setMode(isLogin ? 'register' : 'login')
          setError('')
        }}
        className="w-full text-sm text-slate-400 hover:text-emerald-300"
      >
        {isLogin ? 'No account yet? Sign up' : 'Already have an account? Log in'}
      </button>
    </form>
  )
}