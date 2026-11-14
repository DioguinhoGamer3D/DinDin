import { useState } from 'react'
import Modal from './Modal'

type Props = {
  titulo: string
  mensagem: string
  textoConfirmar: string
  aoConfirmar: () => Promise<void>
  aoCancelar: () => void
}

export default function ModalConfirmacao({
  titulo,
  mensagem,
  textoConfirmar,
  aoConfirmar,
  aoCancelar,
}: Props) {
  const [processando, setProcessando] = useState(false)

  async function confirmar() {
    setProcessando(true)
    try {
      await aoConfirmar()
    } finally {
      setProcessando(false)
    }
  }

  return (
    <Modal titulo={titulo} aoFechar={aoCancelar}>
      <p className="text-sm text-gray-300">{mensagem}</p>

      <div className="flex gap-3 pt-6">
        <button
          type="button"
          onClick={aoCancelar}
          className="flex-1 py-2.5 bg-gray-800 hover:bg-gray-700 text-gray-300 font-semibold rounded-xl transition text-sm"
        >
          Cancelar
        </button>
        <button
          type="button"
          onClick={confirmar}
          disabled={processando}
          className="flex-1 py-2.5 bg-red-500 hover:bg-red-400 text-white font-bold rounded-xl transition text-sm disabled:opacity-50"
        >
          {textoConfirmar}
        </button>
      </div>
    </Modal>
  )
}