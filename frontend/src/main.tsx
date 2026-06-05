import React from 'react'
import ReactDOM from 'react-dom/client'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import './styles/globals.css'
import { AppShell } from './components/layout/AppShell'
import { LoginPage } from './features/auth/LoginPage'
import { DashboardPage } from './features/dashboard/DashboardPage'
import { AnalyticsPage } from './features/analytics/AnalyticsPage'
import { InsightsPage } from './features/insights/InsightsPage'
import { NutritionPage } from './features/nutrition/NutritionPage'
import { ChallengesPage } from './features/challenges/ChallengesPage'
import { LeaderboardsPage } from './features/leaderboards/LeaderboardsPage'
import { AchievementsPage } from './features/achievements/AchievementsPage'
import { ProfilePage } from './features/profile/ProfilePage'
import { SettingsPage } from './features/settings/SettingsPage'

const queryClient = new QueryClient()

function ShellRoute({ children }: { children: React.ReactNode }) {
  return <AppShell>{children}</AppShell>
}

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/dashboard" element={<ShellRoute><DashboardPage /></ShellRoute>} />
          <Route path="/analytics" element={<ShellRoute><AnalyticsPage /></ShellRoute>} />
          <Route path="/insights" element={<ShellRoute><InsightsPage /></ShellRoute>} />
          <Route path="/nutrition" element={<ShellRoute><NutritionPage /></ShellRoute>} />
          <Route path="/challenges" element={<ShellRoute><ChallengesPage /></ShellRoute>} />
          <Route path="/leaderboards" element={<ShellRoute><LeaderboardsPage /></ShellRoute>} />
          <Route path="/achievements" element={<ShellRoute><AchievementsPage /></ShellRoute>} />
          <Route path="/profile" element={<ShellRoute><ProfilePage /></ShellRoute>} />
          <Route path="/settings" element={<ShellRoute><SettingsPage /></ShellRoute>} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  </React.StrictMode>
)

