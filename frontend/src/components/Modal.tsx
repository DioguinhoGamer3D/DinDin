import { useEffect } from 'react'
import type { ReactNode } from 'react'
import { X } from 'lucide-react'

type Props = {
  titulo: string
  aoFechar: () => void
  children: ReactNode
}

export default function Modal({ titulo, aoFechar, children }: Props) {
  useEffect(() => {
    function aoPressionarTecla(evento: KeyboardEvent) {
      if (evento.key === 'Escape') aoFechar()
    }
    document.addEventListener('keydown', aoPressionarTecla)
    return () => document.removeEventListener('keydown', aoPressionarTecla)
  }, [aoFechar])

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4">
      <div
        role="dialog"
        aria-modal="true"
        aria-label={titulo}
        className="bg-superficie border border-gray-800 rounded-2xl w-full max-w-md p-6 relative shadow-2xl max-h-[90vh] overflow-y-auto"
      >
        <button
          type="button"
          onClick={aoFechar}
          className="absolute top-4 right-4 text-gray-400 hover:text-white"
          aria-label="Fechar"
        >
          <X className="w-5 h-5" />
        </button>
        <h3 className="text-xl font-bold text-white mb-4">{titulo}</h3>
        {children}
      </div>
    </div>
  )
}