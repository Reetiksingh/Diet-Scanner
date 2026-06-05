export function subDays(date: Date, days: number) {
  const copy = new Date(date)
  copy.setDate(copy.getDate() - days)
  return copy
}

export function format(date: Date) {
  return date.toISOString().slice(0, 10)
}

