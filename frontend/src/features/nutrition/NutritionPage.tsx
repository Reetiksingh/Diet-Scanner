import { FormEvent, useState } from 'react'
import { useMutation, useQuery } from '@tanstack/react-query'
import { Barcode, Search, ScanText } from 'lucide-react'
import { api } from '../../lib/api/queries'
import { Card } from '../../components/ui/card'
import { Input } from '../../components/ui/input'
import { Button } from '../../components/ui/button'
import { Textarea } from '../../components/ui/textarea'

export function NutritionPage() {
  const [q, setQ] = useState('oats')
  const [barcode, setBarcode] = useState('8901777888999')
  const [rawText, setRawText] = useState('Energy 220 kcal Protein 18g Carbs 28g Total Fat 7g Fiber 8g Total Sugar 4g Sodium 210mg')
  const foods = useQuery({ queryKey: ['food-search', q], queryFn: () => api.foodSearch(q), enabled: q.length > 1 })
  const barcodeScan = useMutation({ mutationFn: api.scanBarcode })
  const labelScan = useMutation({ mutationFn: api.scanLabel })

  function submitBarcode(event: FormEvent) {
    event.preventDefault()
    barcodeScan.mutate(barcode)
  }

  function submitLabel(event: FormEvent) {
    event.preventDefault()
    labelScan.mutate(rawText)
  }

  return (
    <div className="grid gap-6 xl:grid-cols-[1fr_1fr]">
      <Card>
        <h1 className="mb-4 text-2xl font-bold">Food Search</h1>
        <div className="relative">
          <Search className="absolute left-3 top-2.5 text-muted" size={18} />
          <Input className="pl-10" value={q} onChange={(event) => setQ(event.target.value)} />
        </div>
        <div className="mt-4 space-y-3">
          {(foods.data ?? []).map((food) => (
            <div key={food.id} className="rounded-md border border-border p-3">
              <div className="font-semibold">{food.name}</div>
              <div className="text-sm text-muted">{food.brand} · {food.nutrition.calories} kcal · {food.nutrition.proteinG}g protein</div>
            </div>
          ))}
        </div>
      </Card>
      <div className="grid gap-6">
        <Card>
          <form onSubmit={submitBarcode} className="space-y-4">
            <h2 className="text-xl font-bold">Barcode Scan</h2>
            <Input value={barcode} onChange={(event) => setBarcode(event.target.value)} />
            <Button><Barcode size={18} /> Scan</Button>
            {barcodeScan.data && <Result decision={barcodeScan.data.decision} calories={barcodeScan.data.nutrition.calories} />}
          </form>
        </Card>
        <Card>
          <form onSubmit={submitLabel} className="space-y-4">
            <h2 className="text-xl font-bold">Label Scan</h2>
            <Textarea value={rawText} onChange={(event) => setRawText(event.target.value)} />
            <Button><ScanText size={18} /> Analyze</Button>
            {labelScan.data && <Result decision={labelScan.data.decision} calories={labelScan.data.nutrition.calories} />}
          </form>
        </Card>
      </div>
    </div>
  )
}

function Result({ decision, calories }: { decision: string; calories: number }) {
  return <div className="rounded-md border border-border bg-background p-3 text-sm">{decision} · {calories} kcal</div>
}

