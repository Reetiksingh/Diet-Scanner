import { useQuery } from '@tanstack/react-query'
import { api } from '../../lib/api/queries'
import { Card } from '../../components/ui/card'

export function ProfilePage() {
  const { data } = useQuery({ queryKey: ['profile'], queryFn: api.profile })
  return (
    <Card>
      <h1 className="text-2xl font-bold">Profile</h1>
      <dl className="mt-6 grid gap-4 md:grid-cols-2">
        {Object.entries(data ?? { displayName: 'NutriLens Developer', countryCode: 'IN', timezone: 'Asia/Kolkata', activityLevel: 'MODERATE', dietaryPreference: 'BALANCED' }).map(([key, value]) => (
          <div key={key} className="rounded-md border border-border p-3">
            <dt className="text-xs uppercase text-muted">{key}</dt>
            <dd className="mt-1 font-semibold">{String(value)}</dd>
          </div>
        ))}
      </dl>
    </Card>
  )
}

