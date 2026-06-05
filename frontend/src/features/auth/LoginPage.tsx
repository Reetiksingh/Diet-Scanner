import { FormEvent, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Github, Mail } from 'lucide-react'
import { api } from '../../lib/api/queries'
import { setAccessToken } from '../../lib/api/client'
import { Button } from '../../components/ui/button'
import { Input } from '../../components/ui/input'
import { Card } from '../../components/ui/card'

export function LoginPage() {
  const [email, setEmail] = useState('developer@nutrilens.local')
  const [displayName, setDisplayName] = useState('NutriLens Developer')
  const navigate = useNavigate()

  async function submit(event: FormEvent) {
    event.preventDefault()
    const session = await api.devLogin(email, displayName)
    setAccessToken(session.accessToken)
    navigate('/dashboard')
  }

  return (
    <div className="min-h-screen bg-background p-6 text-foreground">
      <div className="mx-auto grid min-h-[calc(100vh-48px)] max-w-6xl items-center gap-8 lg:grid-cols-[1.1fr_0.9fr]">
        <motion.div initial={{ opacity: 0, y: 24 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.5 }}>
          <div className="mb-5 inline-flex rounded-md bg-primary px-3 py-1 text-sm font-semibold text-white">NutriLens</div>
          <h1 className="max-w-3xl text-5xl font-bold leading-tight md:text-6xl">A Distributed Nutrition Intelligence and Dietary Analytics Platform.</h1>
          <p className="mt-5 max-w-2xl text-lg text-muted">Score nutrition consistency, macro balance, hydration, behavioral trends, rankings, achievements, and reports from real analytics.</p>
        </motion.div>
        <Card>
          <form onSubmit={submit} className="space-y-4">
            <div>
              <h2 className="text-2xl font-bold">Sign in</h2>
              <p className="text-sm text-muted">OAuth-ready authentication with local development access.</p>
            </div>
            <Input value={email} onChange={(event) => setEmail(event.target.value)} />
            <Input value={displayName} onChange={(event) => setDisplayName(event.target.value)} />
            <Button className="w-full">
              <Mail size={18} />
              Continue
            </Button>
            <div className="grid grid-cols-2 gap-3">
              <a className="flex h-10 items-center justify-center gap-2 rounded-md border border-border text-sm" href="/api/v1/auth/oauth2/authorization/google">Google</a>
              <a className="flex h-10 items-center justify-center gap-2 rounded-md border border-border text-sm" href="/api/v1/auth/oauth2/authorization/github">
                <Github size={16} />
                GitHub
              </a>
            </div>
          </form>
        </Card>
      </div>
    </div>
  )
}

