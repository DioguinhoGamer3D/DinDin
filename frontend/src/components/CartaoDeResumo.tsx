import type { ReactNode } from 'react'
import type { LucideIcon } from 'lucide-react'

type Variante = 'ouro' | 'verde' | 'vermelho' | 'roxo'

const estilos: Record<Variante, { icone: string; valor: string; borda: string }> = {
  ouro: {
    icone: 'bg-amber-400/10 text-amber-400',
    valor: 'text-white',
    borda: 'hover:border-amber-500/40',
  },
  verde: {
    icone: 'bg-emerald-500/10 text-emerald-400',
    valor: 'text-emerald-400',
    borda: 'hover:border-emerald-500/40',
  },
  vermelho: {
    icone: 'bg-red-500/10 text-red-400',
    valor: 'text-red-400',
    borda: 'hover:border-red-500/40',
  },
  roxo: {
    icone: 'bg-purple-500/10 text-purple-400',
    valor: 'text-purple-300',
    borda: 'hover:border-purple-500/40',
  },
}

type Props = {
  rotulo: string
  valor: string
  icone: LucideIcon
  variante: Variante
  children: ReactNode
}

export default function CartaoDeResumo({ rotulo, valor, icone: Icone, variante, children }: Props) {
  const estilo = estilos[variante]

  return (
    <div className={`bg-superficie border border-gray-800 p-5 rounded-2xl transition ${estilo.borda}`}>
      <div className="flex items-center justify-between">
        <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">{rotulo}</span>
        <div className={`p-2 rounded-xl ${estilo.icone}`}>
          <Icone className="w-5 h-5" />
        </div>
      </div>
      <div className="mt-3">
        <div className={`text-2xl font-extrabold tracking-tight ${estilo.valor}`}>{valor}</div>
        <div className="mt-2 text-xs">{children}</div>
      </div>
    </div>
  )
}