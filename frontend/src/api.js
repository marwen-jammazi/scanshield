import { clearAuth, getAuth } from './auth'

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

const AUTH_ERRORS = {
  400: 'Enter a valid email and a password of at least 8 characters.',
  401: 'Invalid email or password.',
  409: 'This email is already registered.',
}

function post(path, body, withToken) {
  const auth = withToken ? getAuth() : null
  return fetch(`${API_URL}${path}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(auth?.token ? { Authorization: `Bearer ${auth.token}` } : {}),
    },
    body: JSON.stringify(body),
  })
}

export async function scanMessage(message) {
  let res = await post('/api/scan', { message }, true)
  if (res.status === 401 && getAuth()) {
    // The saved token expired or is invalid: drop it and scan as a guest
    clearAuth()
    res = await post('/api/scan', { message }, false)
  }
  if (!res.ok) {
    throw new Error(res.status === 400 ? 'Please enter a valid message.' : 'Server error, please try again.')
  }
  return res.json()
}

export async function getHistory() {
  const auth = getAuth()
  const res = await fetch(`${API_URL}/api/scans`, {
    headers: auth?.token ? { Authorization: `Bearer ${auth.token}` } : {},
  })
  if (res.status === 401) {
    clearAuth()
    throw new Error('Session expired, please log in again.')
  }
  if (!res.ok) throw new Error('Could not load history.')
  return res.json()
}

export async function authenticate(mode, email, password) {
  const res = await post(`/api/auth/${mode}`, { email, password }, false)
  if (!res.ok) throw new Error(AUTH_ERRORS[res.status] ?? 'Server error, please try again.')
  return res.json()
}