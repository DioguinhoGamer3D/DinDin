import type { ReactNode } from 'react'

type Props = {
  id: string
  rotulo: string
  erro?: string
  children: ReactNode
}

export default function Campo({ id, rotulo, erro, children }: Props) {
  return (
    <div>
      <label
        htmlFor={id}
        className="block text-xs font-medium text-gray-400 uppercase tracking-wider mb-1"
      >
        {rotulo}
      </label>
      {children}
      {erro && <p className="mt-1 text-xs text-red-400">{erro}</p>}
    </div>
  )
}