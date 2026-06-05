import { TextareaHTMLAttributes } from 'react'
import { clsx } from 'clsx'

export function Textarea({ className = '', ...props }: TextareaHTMLAttributes<HTMLTextAreaElement>) {
  return <textarea className={clsx('min-h-28 w-full rounded-md border border-border bg-background p-3 text-sm outline-none focus:ring-2 focus:ring-primary', className)} {...props} />
}

