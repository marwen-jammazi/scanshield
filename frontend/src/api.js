const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

export async function scanMessage(message) {
  const res = await fetch(`${API_URL}/api/scan`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ message }),
  })
  if (!res.ok) {
    throw new Error(res.status === 400 ? 'Please enter a valid message.' : 'Server error, please try again.')
  }
  return res.json()
}

export async function getHistory() {
  const res = await fetch(`${API_URL}/api/scans`)
  if (!res.ok) throw new Error('Could not load history.')
  return res.json()
}