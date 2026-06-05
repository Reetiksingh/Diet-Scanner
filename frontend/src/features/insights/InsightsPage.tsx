import { useQuery } from '@tanstack/react-query'
import { motion } from 'framer-motion'
import { api } from '../../lib/api/queries'
import { Card } from '../../components/ui/card'

export function InsightsPage() {
  const { data } = useQuery({ queryKey: ['insights'], queryFn: api.insights })
  const insights = data?.length ? data : [
    { type: 'PROTEIN_TREND', title: 'Protein intake decreased', message: 'Protein intake decreased 15% over the last two weeks.', severity: 'MEDIUM', evidence: '{"changePercent":-15}' },
    { type: 'LATE_NIGHT_EATING', title: 'Late-night calories detected', message: 'Most calories are consumed after 9PM.', severity: 'LOW', evidence: '{"window":"21:00-23:59"}' },
    { type: 'BREAKFAST_CONSISTENCY', title: 'Breakfast consistency improved', message: 'Breakfast consistency improved by 28%.', severity: 'LOW', evidence: '{"changePercent":28}' }
  ]
  return (
    <div className="grid gap-4">
      {insights.map((insight, index) => (
        <motion.div key={`${insight.type}-${index}`} initial={{ opacity: 0, y: 14 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: index * 0.05 }}>
          <Card>
            <div className="flex items-start justify-between gap-4">
              <div>
                <div className="text-xs font-semibold uppercase text-primary">{insight.type}</div>
                <h2 className="mt-1 text-xl font-bold">{insight.title}</h2>
                <p className="mt-2 text-muted">{insight.message}</p>
              </div>
              <span className="rounded-md border border-border px-2 py-1 text-xs">{insight.severity}</span>
            </div>
          </Card>
        </motion.div>
      ))}
    </div>
  )
}

