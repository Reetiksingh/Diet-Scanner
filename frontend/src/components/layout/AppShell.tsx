import { PropsWithChildren } from 'react'
import { NavLink } from 'react-router-dom'
import { Activity, BarChart3, Bell, Gauge, Medal, Moon, ScanLine, Settings, Trophy, User, Zap } from 'lucide-react'
import { useUiStore } from '../../lib/stores/ui-store'

const nav = [
  { to: '/dashboard', label: 'Dashboard', icon: Gauge },
  { to: '/analytics', label: 'Analytics', icon: BarChart3 },
  { to: '/insights', label: 'Insights', icon: Zap },
  { to: '/nutrition', label: 'Nutrition', icon: ScanLine },
  { to: '/challenges', label: 'Challenges', icon: Activity },
  { to: '/leaderboards', label: 'Leaderboards', icon: Trophy },
  { to: '/achievements', label: 'Achievements', icon: Medal },
  { to: '/profile', label: 'Profile', icon: User },
  { to: '/settings', label: 'Settings', icon: Settings }
]

export function AppShell({ children }: PropsWithChildren) {
  const { darkMode, setDarkMode } = useUiStore()
  return (
    <div className={darkMode ? 'dark' : ''}>
      <div className="min-h-screen bg-background text-foreground">
        <aside className="fixed inset-y-0 left-0 hidden w-72 border-r border-border bg-card px-4 py-5 lg:block">
          <div className="mb-8 flex items-center gap-3 px-2">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary text-white">
              <ScanLine size={22} />
            </div>
            <div>
              <div className="text-lg font-bold">NutriLens</div>
              <div className="text-xs text-muted">Nutrition Intelligence</div>
            </div>
          </div>
          <nav className="space-y-1">
            {nav.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  `flex items-center gap-3 rounded-md px-3 py-2 text-sm transition ${isActive ? 'bg-primary text-white' : 'text-muted hover:bg-background hover:text-foreground'}`
                }
              >
                <item.icon size={18} />
                {item.label}
              </NavLink>
            ))}
          </nav>
        </aside>
        <main className="lg:pl-72">
          <header className="sticky top-0 z-10 flex h-16 items-center justify-between border-b border-border bg-background/90 px-4 backdrop-blur md:px-8">
            <div>
              <div className="text-sm text-muted">Distributed Nutrition Intelligence</div>
              <div className="font-semibold">Spotify Analytics meets Strava Analytics meets MyFitnessPal Intelligence</div>
            </div>
            <div className="flex items-center gap-2">
              <button className="rounded-md border border-border p-2" aria-label="Notifications">
                <Bell size={18} />
              </button>
              <button className="rounded-md border border-border p-2" aria-label="Toggle theme" onClick={() => setDarkMode(!darkMode)}>
                <Moon size={18} />
              </button>
            </div>
          </header>
          <section className="px-4 py-6 md:px-8">{children}</section>
        </main>
      </div>
    </div>
  )
}

