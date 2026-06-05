import { PropsWithChildren } from 'react'
import { clsx } from 'clsx'

export function Card({ children, className = '' }: PropsWithChildren<{ className?: string }>) {
  return <div className={clsx('rounded-lg border border-border bg-card p-5 shadow-soft', className)}>{children}</div>
}

