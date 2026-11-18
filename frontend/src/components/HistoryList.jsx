const DOT = {
  LOW: 'bg-emerald-500',
  MEDIUM: 'bg-amber-500',
  HIGH: 'bg-red-500',
}

export default function HistoryList({ items, onSelect }) {
  if (items.length === 0) return null

  return (
    <section className="mt-10">
      <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-400">
        Recent scans
      </h2>
      <ul className="mt-3 space-y-2">
        {items.map((item) => (
          <li key={item.id}>
            <button
              onClick={() => onSelect(item)}
              title={new Date(item.createdAt).toLocaleString()}
              className="flex w-full items-center gap-3 rounded-lg border border-slate-700 bg-slate-800 px-4 py-3 hover:border-emerald-500"
            >
              <span className={`h-2.5 w-2.5 shrink-0 rounded-full ${DOT[item.level]}`} />
              <span dir="auto" className="flex-1 truncate text-start text-slate-200">
                {item.message}
              </span>
              <span className="text-sm text-slate-400">{item.score}</span>
            </button>
          </li>
        ))}
      </ul>
    </section>
  )
}