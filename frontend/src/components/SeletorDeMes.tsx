import { ChevronLeft, ChevronRight } from 'lucide-react'
import { deslocarMes, rotuloDoMes } from '../utils/formatacao'

type Props = {
  mes: string
  aoMudar: (mes: string) => void
}

export default function SeletorDeMes({ mes, aoMudar }: Props) {
  return (
    <div className="inline-flex items-center gap-1 bg-superficie border border-gray-800 rounded-xl p-1">
      <button
        type="button"
        onClick={() => aoMudar(deslocarMes(mes, -1))}
        className="p-2 text-gray-400 hover:text-amber-400 rounded-lg transition"
        aria-label="Mês anterior"
      >
        <ChevronLeft className="w-4 h-4" />
      </button>
      <span className="min-w-40 text-center text-sm font-semibold text-white">
        {rotuloDoMes(mes)}
      </span>
      <button
        type="button"
        onClick={() => aoMudar(deslocarMes(mes, 1))}
        className="p-2 text-gray-400 hover:text-amber-400 rounded-lg transition"
        aria-label="Próximo mês"
      >
        <ChevronRight className="w-4 h-4" />
      </button>
    </div>
  )
}