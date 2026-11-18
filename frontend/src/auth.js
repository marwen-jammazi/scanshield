const KEY = 'scamshield.auth'

export function getAuth() {
  try {
    return JSON.parse(localStorage.getItem(KEY))
  } catch {
    return null
  }
}

export function saveAuth(auth) {
  try {
    localStorage.setItem(KEY, JSON.stringify(auth))
  } catch {
    // Storage can be blocked (private mode): the user just has to log in again
  }
}

export function clearAuth() {
  try {
    localStorage.removeItem(KEY)
  } catch {
    // nothing to clean up
  }
}