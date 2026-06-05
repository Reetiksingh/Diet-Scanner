import type { Config } from 'tailwindcss'

export default {
  darkMode: ['class'],
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        background: 'hsl(var(--background))',
        foreground: 'hsl(var(--foreground))',
        card: 'hsl(var(--card))',
        border: 'hsl(var(--border))',
        muted: 'hsl(var(--muted))',
        primary: 'hsl(var(--primary))',
        accent: 'hsl(var(--accent))',
        success: 'hsl(var(--success))',
        warning: 'hsl(var(--warning))'
      },
      boxShadow: {
        soft: '0 16px 60px rgba(15, 23, 42, 0.08)'
      }
    }
  },
  plugins: []
} satisfies Config

