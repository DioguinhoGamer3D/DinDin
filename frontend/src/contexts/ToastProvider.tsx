import { useCallback, useMemo, useRef, useState } from 'react'
import type { ReactNode } from 'react'
import { AlertCircle, CheckCircle2 } from 'lucide-react'
import { ToastContext } from './ToastContext'
import type { TipoDeToast, ToastContextoValor } from './ToastContext'

type DadosDoToast = {
  mensagem: string
  tipo: TipoDeToast
}

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toast, setToast] = useState<DadosDoToast | null>(null)
  const temporizador = useRef<number | undefined>(undefined)

  const mostrarToast = useCallback((mensagem: string, tipo: TipoDeToast = 'sucesso') => {
    window.clearTimeout(temporizador.current)
    setToast({ mensagem, tipo })
    temporizador.current = window.setTimeout(() => setToast(null), 3500)
  }, [])

  const valor = useMemo<ToastContextoValor>(() => ({ mostrarToast }), [mostrarToast])

  return (
    <ToastContext.Provider value={valor}>
      {children}
      {toast && (
        <div
          role="status"
          className="fixed bottom-6 right-6 z-50 flex items-center gap-3 bg-gray-900 border border-amber-500/30 text-white px-4 py-3 rounded-xl shadow-2xl"
        >
          {toast.tipo === 'sucesso' ? (
            <CheckCircle2 className="w-5 h-5 text-amber-400" />
          ) : (
            <AlertCircle
              className={`w-5 h-5 ${toast.tipo === 'erro' ? 'text-red-400' : 'text-blue-400'}`}
            />
          )}
          <span className="text-sm font-medium">{toast.mensagem}</span>
        </div>
      )}
    </ToastContext.Provider>
  )
}