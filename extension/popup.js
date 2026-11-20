const API_URL = 'http://localhost:8080'
const MAX_LENGTH = 5000

const $ = (id) => document.getElementById(id)

const LEVELS = {
  LOW: { label: 'Low risk', bg: '#c4f5df', fg: '#0b4a35', fill: '#3fd69a' },
  MEDIUM: { label: 'Medium risk', bg: '#ffe3ad', fg: '#6b4200', fill: '#ffb84d' },
  HIGH: { label: 'High risk', bg: '#ffc9c9', fg: '#7a1111', fill: '#ff6b6b' },
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

function renderMl(probability) {
  const panel = $('ml')
  if (typeof probability !== 'number') {
    panel.style.display = 'none'
    return
  }
  const percent = Math.round(probability * 100)
  $('mlPct').textContent = `${percent}% likely a scam`
  $('mlBar').style.width = `${percent}%`
  $('mlBar').style.background =
    percent >= 60 ? LEVELS.HIGH.fill : percent >= 40 ? LEVELS.MEDIUM.fill : LEVELS.LOW.fill
  panel.style.display = 'block'
}

function render(result) {
  const level = LEVELS[result.level]

  const badge = $('badge')
  badge.textContent = level.label
  badge.style.background = level.bg
  badge.style.color = level.fg

  $('score').replaceChildren(String(result.score))
  const small = document.createElement('small')
  small.textContent = '/100'
  $('score').appendChild(small)

  $('track').setAttribute('aria-valuenow', String(result.score))
  $('bar').style.width = `${result.score}%`
  $('bar').style.background = level.fill

  renderMl(result.mlProbability)

  const list = $('reasons')
  list.replaceChildren()
  const reasons = result.reasons.length ? result.reasons : ['No suspicious signals found.']
  for (const reason of reasons) {
    const li = document.createElement('li')
    const dot = document.createElement('span')
    dot.className = 'dot clay-pill'
    dot.style.background = level.fill
    dot.setAttribute('aria-hidden', 'true')
    const text = document.createElement('span')
    text.textContent = reason
    li.append(dot, text)
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