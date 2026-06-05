import { Card } from '../../components/ui/card'

const challenges = [
  ['Protein Challenge', 'protein-score', 68],
  ['Hydration Challenge', 'hydration-score', 84],
  ['Healthy Breakfast Challenge', 'consistency-score', 52],
  ['Consistency Challenge', 'nutrition-score', 76]
]

export function ChallengesPage() {
  return (
    <div className="grid gap-4 md:grid-cols-2">
      {challenges.map(([name, metric, progress]) => (
        <Card key={String(name)}>
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-xl font-bold">{name}</h2>
              <p className="text-sm text-muted">{metric}</p>
            </div>
            <div className="text-2xl font-bold">{progress}%</div>
          </div>
          <div className="mt-5 h-3 overflow-hidden rounded-full bg-background">
            <div className="h-full rounded-full bg-primary" style={{ width: `${progress}%` }} />
          </div>
        </Card>
      ))}
    </div>
  )
}

