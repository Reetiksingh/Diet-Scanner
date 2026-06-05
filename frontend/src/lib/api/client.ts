export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''

let accessToken = ''

export function setAccessToken(token: string) {
  accessToken = token
}

export async function apiRequest<T>(path: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
      ...options.headers
    },
    credentials: 'include'
  })
  if (!response.ok) {
    const body = await response.text()
    throw new Error(body || `Request failed with ${response.status}`)
  }
  if (response.status === 204) {
    return undefined as T
  }
  return response.json() as Promise<T>
}

export type DashboardSummary = {
  date: string
  nutritionScore: number
  consistencyScore: number
  hydrationScore: number
  dietQualityScore?: number
  macroBalance: Record<string, number>
  streakDays: number
  topInsight: string
  weeklyRank?: number
}

export type DailyAnalytics = {
  date: string
  calories: number
  proteinG: number
  carbsG: number
  fatsG: number
  fiberG: number
  sugarG: number
  waterMl: number
  nutritionScore: number
  macroBalanceScore: number
  consistencyScore: number
  hydrationScore: number
  dietQualityScore: number
  skippedBreakfast: boolean
}

export type Insight = {
  type: string
  title: string
  message: string
  severity: string
  evidence: string
}

export type Recommendation = {
  id: string
  type: string
  title: string
  body: string
  evidence: string
  priority: number
}

export type RankingItem = {
  rank: number
  userId: string
  displayName: string
  score: number
  countryCode: string
}

export type Achievement = {
  id: string
  code: string
  name: string
  description: string
  unlocked: boolean
}

export type Food = {
  id: string
  barcode: string
  name: string
  brand: string
  source: string
  servingSize: number
  servingUnit: string
  nutrition: {
    calories: number
    proteinG: number
    carbsG: number
    fatsG: number
    fiberG: number
    sugarG: number
    sodiumMg: number
  }
}

