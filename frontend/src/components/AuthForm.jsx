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

  const inputClass = 'clay-inset w-full p-4 font-semibold text-ink placeholder:text-ink-soft'

  return (
    <form onSubmit={handleSubmit} className="clay mt-8 space-y-4 p-6">
      <h2 className="text-xl font-extrabold">{isLogin ? 'Log in' : 'Create an account'}</h2>

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

      {error && (
        <p role="alert" className="font-semibold text-[#b42323]">
          {error}
        </p>
      )}

      <button type="submit" disabled={loading} className="clay-btn clay-mint w-full py-4 text-lg font-extrabold">
        {loading ? 'Please wait...' : isLogin ? 'Log in' : 'Sign up'}
      </button>

      <button
        type="button"
        onClick={() => {
          setMode(isLogin ? 'register' : 'login')
          setError('')
        }}
        className="w-full font-semibold text-ink-soft hover:text-ink"
      >
        {isLogin ? 'No account yet? Sign up' : 'Already have an account? Log in'}
      </button>
    </form>
  )
}