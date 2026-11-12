import { createContext } from 'react'

export type TipoDeToast = 'sucesso' | 'info' | 'erro'

export type ToastContextoValor = {
  mostrarToast: (mensagem: string, tipo?: TipoDeToast) => void
}

export const ToastContext = createContext<ToastContextoValor | null>(null)