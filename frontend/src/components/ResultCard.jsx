const STYLES = {
  LOW: { label: 'Low risk', badge: 'bg-[#c4f5df] text-[#0b4a35]', fill: '#3fd69a' },
  MEDIUM: { label: 'Medium risk', badge: 'bg-[#ffe3ad] text-[#6b4200]', fill: '#ffb84d' },
  HIGH: { label: 'High risk', badge: 'bg-[#ffc9c9] text-[#7a1111]', fill: '#ff6b6b' },
}

export default function ResultCard({ result }) {
  const style = STYLES[result.level]

  return (
    <div className="clay mt-6 p-6">
      <div className="flex items-center justify-between">
        <span className={`clay-pill rounded-full px-4 py-1.5 text-sm font-extrabold ${style.badge}`}>
          {style.label}
        </span>
        <span className="text-4xl font-extrabold">
          {result.score}
          <span className="text-lg text-ink-soft">/100</span>
        </span>
      </div>

      <div
        className="clay-inset mt-5 h-5 rounded-full p-1"
        role="progressbar"
        aria-label="Risk score"
        aria-valuemin={0}
        aria-valuemax={100}
        aria-valuenow={result.score}
      >
        <div
          className="clay-pill h-full rounded-full"
          style={{ width: `${result.score}%`, background: style.fill }}
        />
      </div>

      <h3 className="mt-6 text-sm font-extrabold uppercase tracking-wide text-ink-soft">Why</h3>
      {result.reasons.length === 0 ? (
        <p className="mt-2 font-semibold">No suspicious signals found. Stay careful anyway.</p>
      ) : (
        <ul className="mt-3 space-y-3">
          {result.reasons.map((reason) => (
            <li key={reason} className="flex items-start gap-3 font-semibold">
              <span
                className="clay-pill mt-1.5 h-3 w-3 shrink-0 rounded-full"
                style={{ background: style.fill }}
                aria-hidden="true"
              />
              <span>{reason}</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}