const DOT = {
  LOW: '#3fd69a',
  MEDIUM: '#ffb84d',
  HIGH: '#ff6b6b',
}

export default function HistoryList({ items, onSelect }) {
  if (items.length === 0) return null

  return (
    <section className="mt-12">
      <h2 className="text-sm font-extrabold uppercase tracking-wide text-ink-soft">Recent scans</h2>
      <ul className="mt-4 space-y-4">
        {items.map((item) => (
          <li key={item.id}>
            <button
              onClick={() => onSelect(item)}
              title={new Date(item.createdAt).toLocaleString()}
              className="clay-btn flex w-full items-center gap-3 bg-[#fbf9ff] px-5 py-4"
            >
              <span
                className="clay-pill h-3.5 w-3.5 shrink-0 rounded-full"
                style={{ background: DOT[item.level] }}
                aria-hidden="true"
              />
              <span dir="auto" className="flex-1 truncate text-start font-semibold">
                {item.message}
              </span>
              <span className="font-extrabold text-ink-soft">{item.score}</span>
            </button>
          </li>
        ))}
      </ul>
    </section>
  )
}