import { useQuery } from '@tanstack/react-query'
import { motion } from 'framer-motion'
import { Area, AreaChart, CartesianGrid, Cell, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { Droplets, Flame, Gauge, Trophy } from 'lucide-react'
import { api } from '../../lib/api/queries'
import { Card } from '../../components/ui/card'

const trend = [
  { day: 'Mon', score: 72, protein: 88 },
  { day: 'Tue', score: 78, protein: 94 },
  { day: 'Wed', score: 82, protein: 102 },
  { day: 'Thu', score: 79, protein: 91 },
  { day: 'Fri', score: 86, protein: 112 },
  { day: 'Sat', score: 81, protein: 97 },
  { day: 'Sun', score: 89, protein: 118 }
]

const scoreCards = [
  { label: 'Nutrition', valueKey: 'nutritionScore', icon: Gauge },
  { label: 'Consistency', valueKey: 'consistencyScore', icon: Flame },
  { label: 'Hydration', valueKey: 'hydrationScore', icon: Droplets },
  { label: 'Streak', valueKey: 'streakDays', icon: Trophy }
] as const

export function DashboardPage() {
  const { data } = useQuery({ queryKey: ['dashboard'], queryFn: api.dashboard })
  const summary = data ?? { nutritionScore: 86, consistencyScore: 79, hydrationScore: 92, macroBalance: { protein: 31, carbs: 44, fats: 25 }, streakDays: 18, topInsight: 'Protein intake increased 12% compared with the previous 14 days.' }
  const macro = Object.entries(summary.macroBalance).map(([name, value]) => ({ name, value }))

  return (
    <div className="space-y-6">
      <div className="grid gap-4 md:grid-cols-4">
        {scoreCards.map(({ label, valueKey, icon: Icon }, index) => (
          <motion.div key={label} initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: index * 0.05 }}>
            <Card className="min-h-32">
              <div className="flex items-center justify-between">
                <div className="text-sm text-muted">{label}</div>
                <Icon className="text-primary" size={20} />
              </div>
              <div className="mt-5 text-4xl font-bold">{Number(summary[valueKey])}</div>
            </Card>
          </motion.div>
        ))}
      </div>
      <div className="grid gap-6 lg:grid-cols-[1.4fr_0.8fr]">
        <Card>
          <div className="mb-4 flex items-center justify-between">
            <div>
              <h2 className="text-xl font-bold">Score Trend</h2>
              <p className="text-sm text-muted">Daily nutrition and protein movement</p>
            </div>
          </div>
          <div className="h-80">
            <ResponsiveContainer>
              <AreaChart data={trend}>
                <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
                <XAxis dataKey="day" />
                <YAxis />
                <Tooltip />
                <Area type="monotone" dataKey="score" stroke="hsl(var(--primary))" fill="hsl(var(--primary))" fillOpacity={0.22} />
                <Area type="monotone" dataKey="protein" stroke="hsl(var(--accent))" fill="hsl(var(--accent))" fillOpacity={0.14} />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </Card>
        <Card>
          <h2 className="text-xl font-bold">Macro Balance</h2>
          <div className="h-64">
            <ResponsiveContainer>
              <PieChart>
                <Pie data={macro} dataKey="value" nameKey="name" innerRadius={62} outerRadius={94}>
                  {macro.map((entry, index) => <Cell key={entry.name} fill={['hsl(var(--primary))', 'hsl(var(--accent))', 'hsl(var(--warning))'][index]} />)}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
          <p className="text-sm text-muted">{summary.topInsight}</p>
        </Card>
      </div>
      <Card>
        <h2 className="mb-4 text-xl font-bold">Weekly Consistency Heatmap</h2>
        <div className="grid grid-cols-7 gap-2">
          {trend.map((item) => (
            <div key={item.day} className="rounded-md border border-border p-3 text-center">
              <div className="text-sm text-muted">{item.day}</div>
              <div className="mt-2 h-16 rounded-md" style={{ background: `color-mix(in hsl, hsl(var(--primary)) ${item.score}%, transparent)` }} />
            </div>
          ))}
        </div>
      </Card>
    </div>
  )
}
