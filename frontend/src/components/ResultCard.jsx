const STYLES = {
  LOW: { label: 'Low risk', badge: 'bg-emerald-500/20 text-emerald-300', bar: 'bg-emerald-500' },
  MEDIUM: { label: 'Medium risk', badge: 'bg-amber-500/20 text-amber-300', bar: 'bg-amber-500' },
  HIGH: { label: 'High risk', badge: 'bg-red-500/20 text-red-300', bar: 'bg-red-500' },
}

export default function ResultCard({ result }) {
  const style = STYLES[result.level]

  return (
    <div className="mt-6 rounded-xl border border-slate-700 bg-slate-800 p-6">
      <div className="flex items-center justify-between">
        <span className={`rounded-full px-3 py-1 text-sm font-semibold ${style.badge}`}>
          {style.label}
        </span>
        <span className="text-3xl font-bold">{result.score}<span className="text-lg text-slate-400">/100</span></span>
      </div>

      <div className="mt-4 h-2 w-full overflow-hidden rounded-full bg-slate-700">
        <div className={`h-full ${style.bar}`} style={{ width: `${result.score}%` }} />
      </div>

      <h3 className="mt-6 text-sm font-semibold uppercase tracking-wide text-slate-400">
        Why
      </h3>
      {result.reasons.length === 0 ? (
        <p className="mt-2 text-slate-300">No suspicious signals found. Stay careful anyway.</p>
      ) : (
        <ul className="mt-2 space-y-2">
          {result.reasons.map((reason) => (
            <li key={reason} className="flex gap-2 text-slate-200">
              <span aria-hidden></span>
              <span>{reason}</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}