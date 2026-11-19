const API_URL = 'http://localhost:8080'
const MAX_LENGTH = 5000

const $ = (id) => document.getElementById(id)

const LEVELS = {
  LOW: { label: 'Low risk', color: '#10b981' },
  MEDIUM: { label: 'Medium risk', color: '#f59e0b' },
  HIGH: { label: 'High risk', color: '#ef4444' },
}

async function getSelectedText() {
  try {
    const [tab] = await chrome.tabs.query({ active: true, currentWindow: true })
    const [res] = await chrome.scripting.executeScript({
      target: { tabId: tab.id },
      func: () => window.getSelection().toString(),
    })
    return (res?.result ?? '').trim()
  } catch {
    // Browser-internal pages do not allow script injection: fall back to manual paste
    return ''
  }
}

function render(result) {
  const level = LEVELS[result.level]
  $('badge').textContent = level.label
  $('badge').style.background = level.color
  $('score').textContent = `${result.score}/100`

  const list = $('reasons')
  list.replaceChildren()
  const reasons = result.reasons.length ? result.reasons : ['No suspicious signals found.']
  for (const reason of reasons) {
    const li = document.createElement('li')
    li.textContent = reason
    list.appendChild(li)
  }
  $('result').style.display = 'block'
}

async function scan() {
  const message = $('message').value.trim().slice(0, MAX_LENGTH)
  if (!message) return

  $('scan').disabled = true
  $('error').textContent = ''
  try {
    const res = await fetch(`${API_URL}/api/scan`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message }),
    })
    if (!res.ok) {
      throw new Error(res.status === 400 ? 'Enter a valid message.' : 'Server error, please try again.')
    }
    render(await res.json())
  } catch (e) {
    $('result').style.display = 'none'
    $('error').textContent =
      e instanceof TypeError ? 'Cannot reach the ScamShield server. Is it running?' : e.message
  } finally {
    $('scan').disabled = false
  }
}

async function init() {
  $('scan').addEventListener('click', scan)
  const selected = await getSelectedText()
  if (selected) {
    $('message').value = selected.slice(0, MAX_LENGTH)
    scan()
  }
}

init()