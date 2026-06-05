import { apiRequest, DashboardSummary, DailyAnalytics, Insight, Recommendation, RankingItem, Achievement, Food } from './client'

export const api = {
  devLogin: (email: string, displayName: string) =>
    apiRequest<{ accessToken: string; userId: string }>('/api/v1/auth/development/login', {
      method: 'POST',
      body: JSON.stringify({ email, displayName, countryCode: 'IN' })
    }),
  dashboard: () => apiRequest<DashboardSummary>('/api/v1/analytics/summary'),
  dailyAnalytics: (from: string, to: string) => apiRequest<DailyAnalytics[]>(`/api/v1/analytics/daily?from=${from}&to=${to}`),
  insights: () => apiRequest<Insight[]>('/api/v1/analytics/insights'),
  recommendations: () => apiRequest<{ recommendations: Recommendation[] }>('/api/v1/recommendations/current'),
  leaderboard: (metric = 'nutrition-score', period = 'weekly') =>
    apiRequest<{ metric: string; period: string; items: RankingItem[] }>(`/api/v1/leaderboards/global?metric=${metric}&period=${period}&limit=25`),
  achievements: () => apiRequest<{ achievements: Achievement[] }>('/api/v1/achievements/me'),
  foodSearch: (q: string) => apiRequest<Food[]>(`/api/v1/nutrition/foods/search?q=${encodeURIComponent(q)}`),
  scanBarcode: (barcode: string) =>
    apiRequest<{ scanId: string; status: string; nutrition: Food['nutrition']; decision: string }>('/api/v1/nutrition/scans/barcode', {
      method: 'POST',
      body: JSON.stringify({ barcode })
    }),
  scanLabel: (rawText: string) =>
    apiRequest<{ scanId: string; status: string; nutrition: Food['nutrition']; decision: string }>('/api/v1/nutrition/scans/label', {
      method: 'POST',
      body: JSON.stringify({ rawText })
    }),
  profile: () => apiRequest<Record<string, unknown>>('/api/v1/users/me'),
  notifications: () => apiRequest<{ notifications: Array<{ id: string; title: string; body: string; read: boolean; createdAt: string }> }>('/api/v1/notifications')
}

