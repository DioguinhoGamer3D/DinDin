import type { ReactNode } from 'react'
import type { LucideIcon } from 'lucide-react'

type Props = {
  id: string
  rotulo: string
  icone: LucideIcon
  erro?: string
  children: ReactNode
}

export default function CampoFormulario({ id, rotulo, icone: Icone, erro, children }: Props) {
  return (
    <div>
      <label
        htmlFor={id}
        className="block text-xs font-medium text-gray-400 uppercase tracking-wider mb-1"
      >
        {rotulo}
      </label>
      <div className="relative">
        <Icone className="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-500" />
        {children}
      </div>
      {erro && <p className="mt-1 text-xs text-red-400">{erro}</p>}
    </div>
  )
}