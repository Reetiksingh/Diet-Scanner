import { useQuery } from '@tanstack/react-query'
import { motion } from 'framer-motion'
import { Medal } from 'lucide-react'
import { api } from '../../lib/api/queries'
import { Card } from '../../components/ui/card'

const fallback = [
  { id: '1', code: 'HYDRATION_HERO', name: 'Hydration Hero', description: 'Hydration score reached 90 or higher.', unlocked: true },
  { id: '2', code: 'PROTEIN_WARRIOR', name: 'Protein Warrior', description: 'Protein target consistency detected.', unlocked: false },
  { id: '3', code: 'CONSISTENCY_MASTER', name: 'Consistency Master', description: 'Diet consistency score reached 85 or higher.', unlocked: true }
]

export function AchievementsPage() {
  const { data } = useQuery({ queryKey: ['achievements'], queryFn: api.achievements })
  const achievements = data?.achievements.length ? data.achievements : fallback
  return (
    <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
      {achievements.map((achievement, index) => (
        <motion.div key={achievement.id} initial={{ opacity: 0, scale: 0.96 }} animate={{ opacity: 1, scale: 1 }} transition={{ delay: index * 0.04 }}>
          <Card className={achievement.unlocked ? 'border-primary' : ''}>
            <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-background text-primary">
              <Medal />
            </div>
            <h2 className="text-lg font-bold">{achievement.name}</h2>
            <p className="mt-2 text-sm text-muted">{achievement.description}</p>
            <div className="mt-4 text-xs font-semibold uppercase">{achievement.unlocked ? 'Unlocked' : 'In Progress'}</div>
          </Card>
        </motion.div>
      ))}
    </div>
  )
}

