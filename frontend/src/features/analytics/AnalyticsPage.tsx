import { useQuery } from '@tanstack/react-query'
import { format, subDays } from './date'
import { api } from '../../lib/api/queries'
import { Card } from '../../components/ui/card'
import { Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis, CartesianGrid, Bar, BarChart } from 'recharts'

export function AnalyticsPage() {
  const to = format(new Date())
  const from = format(subDays(new Date(), 30))
  const { data } = useQuery({ queryKey: ['daily-analytics', from, to], queryFn: () => api.dailyAnalytics(from, to) })
  const points = data?.length ? data : [
    { date: '2026-05-23', nutritionScore: 72, consistencyScore: 68, hydrationScore: 90, calories: 2100, proteinG: 95, carbsG: 240, fatsG: 70, fiberG: 25, sugarG: 42, waterMl: 2200, macroBalanceScore: 80, dietQualityScore: 77, skippedBreakfast: false },
    { date: '2026-05-24', nutritionScore: 78, consistencyScore: 74, hydrationScore: 84, calories: 1980, proteinG: 104, carbsG: 210, fatsG: 66, fiberG: 28, sugarG: 36, waterMl: 2100, macroBalanceScore: 83, dietQualityScore: 80, skippedBreakfast: false },
    { date: '2026-05-25', nutritionScore: 86, consistencyScore: 82, hydrationScore: 92, calories: 2050, proteinG: 116, carbsG: 225, fatsG: 62, fiberG: 31, sugarG: 30, waterMl: 2500, macroBalanceScore: 88, dietQualityScore: 87, skippedBreakfast: false }
  ]
  return (
    <div className="grid gap-6">
      <Card>
        <h1 className="text-2xl font-bold">Nutrition Analytics</h1>
        <div className="mt-6 h-80">
          <ResponsiveContainer>
            <LineChart data={points}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" />
              <YAxis />
              <Tooltip />
              <Line dataKey="nutritionScore" stroke="hsl(var(--primary))" strokeWidth={3} />
              <Line dataKey="consistencyScore" stroke="hsl(var(--accent))" strokeWidth={3} />
              <Line dataKey="hydrationScore" stroke="hsl(var(--warning))" strokeWidth={3} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </Card>
      <Card>
        <h2 className="text-xl font-bold">Macro Intake</h2>
        <div className="mt-6 h-72">
          <ResponsiveContainer>
            <BarChart data={points}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="proteinG" fill="hsl(var(--primary))" />
              <Bar dataKey="carbsG" fill="hsl(var(--accent))" />
              <Bar dataKey="fatsG" fill="hsl(var(--warning))" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </Card>
    </div>
  )
}

