import { AlertCircle } from 'lucide-react'

export default function AlertaDeErro({ mensagem }: { mensagem: string }) {
  return (
    <div
      role="alert"
      className="mb-4 p-3 rounded-lg bg-red-500/10 border border-red-500/30 text-red-400 text-sm flex items-center gap-2"
    >
      <AlertCircle className="w-4 h-4 shrink-0" />
      <span>{mensagem}</span>
    </div>
  )
}