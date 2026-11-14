import { ArrowRight, RefreshCw } from 'lucide-react'

type Props = {
  carregando: boolean
  texto: string
}

export default function BotaoEnviar({ carregando, texto }: Props) {
  return (
    <button
      type="submit"
      disabled={carregando}
      className="w-full mt-2 bg-linear-to-r from-amber-400 to-yellow-500 hover:from-amber-300 hover:to-yellow-400 text-gray-950 font-bold py-3 rounded-xl shadow-lg shadow-amber-500/10 flex items-center justify-center gap-2 transition disabled:opacity-50"
    >
      {carregando ? (
        <RefreshCw className="w-5 h-5 animate-spin" />
      ) : (
        <>
          <span>{texto}</span>
          <ArrowRight className="w-4 h-4" />
        </>
      )}
    </button>
  )
}