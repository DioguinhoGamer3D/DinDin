import { Wallet } from 'lucide-react'

type Props = {
  tamanho?: 'sm' | 'md' | 'lg'
}

const tamanhos = {
  sm: { caixa: 'w-8 h-8 rounded-lg', icone: 'w-4 h-4', texto: 'text-lg' },
  md: { caixa: 'w-10 h-10 rounded-xl', icone: 'w-5 h-5', texto: 'text-xl' },
  lg: { caixa: 'w-16 h-16 rounded-2xl', icone: 'w-8 h-8', texto: 'text-3xl' },
} as const

export default function Logo({ tamanho = 'md' }: Props) {
  const t = tamanhos[tamanho]

  return (
    <div className="flex items-center gap-3">
      <div
        className={`${t.caixa} bg-linear-to-tr from-amber-500 to-yellow-300 flex items-center justify-center text-gray-950 shadow-md shadow-amber-500/20`}
      >
        <Wallet className={t.icone} />
      </div>
      <span className={`${t.texto} font-bold tracking-tight text-white`}>
        Din<span className="text-amber-400">Din</span>
      </span>
    </div>
  )
}