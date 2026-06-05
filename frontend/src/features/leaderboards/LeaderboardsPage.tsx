import { useQuery } from '@tanstack/react-query'
import { api } from '../../lib/api/queries'
import { Card } from '../../components/ui/card'

export function LeaderboardsPage() {
  const { data } = useQuery({ queryKey: ['leaderboard'], queryFn: () => api.leaderboard('nutrition-score', 'weekly') })
  const items = data?.items.length ? data.items : [
    { rank: 1, userId: '1', displayName: 'Aarav', score: 96.4, countryCode: 'IN' },
    { rank: 2, userId: '2', displayName: 'Mira', score: 94.1, countryCode: 'IN' },
    { rank: 3, userId: '3', displayName: 'Dev', score: 91.8, countryCode: 'US' }
  ]
  return (
    <Card>
      <h1 className="mb-5 text-2xl font-bold">Weekly Nutrition Rankings</h1>
      <div className="overflow-x-auto">
        <table className="w-full border-collapse text-left text-sm">
          <thead className="text-muted">
            <tr>
              <th className="border-b border-border p-3">Rank</th>
              <th className="border-b border-border p-3">User</th>
              <th className="border-b border-border p-3">Country</th>
              <th className="border-b border-border p-3">Score</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.userId}>
                <td className="border-b border-border p-3 font-bold">{item.rank}</td>
                <td className="border-b border-border p-3">{item.displayName}</td>
                <td className="border-b border-border p-3">{item.countryCode}</td>
                <td className="border-b border-border p-3">{item.score.toFixed(1)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </Card>
  )
}

