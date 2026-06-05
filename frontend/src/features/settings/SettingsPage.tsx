import { useUiStore } from '../../lib/stores/ui-store'
import { Card } from '../../components/ui/card'

export function SettingsPage() {
  const { darkMode, setDarkMode } = useUiStore()
  return (
    <div className="grid gap-4">
      <Card>
        <h1 className="text-2xl font-bold">Settings</h1>
        <label className="mt-6 flex items-center justify-between rounded-md border border-border p-3">
          <span>Dark Mode</span>
          <input type="checkbox" checked={darkMode} onChange={(event) => setDarkMode(event.target.checked)} />
        </label>
      </Card>
      <Card>
        <h2 className="text-xl font-bold">Notification Preferences</h2>
        <div className="mt-4 grid gap-3">
          {['Achievement notifications', 'Streak reminders', 'Weekly reports', 'Challenge updates'].map((item) => (
            <label key={item} className="flex items-center justify-between rounded-md border border-border p-3">
              <span>{item}</span>
              <input type="checkbox" defaultChecked />
            </label>
          ))}
        </div>
      </Card>
    </div>
  )
}

