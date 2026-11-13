import { useState } from 'react'
import type { UseFormRegisterReturn } from 'react-hook-form'
import { Eye, EyeOff, Lock } from 'lucide-react'
import CampoFormulario from './CampoFormulario'
import { classeInput } from './estilos'

type Props = {
  registro: UseFormRegisterReturn
  erro?: string
  autoComplete: 'current-password' | 'new-password'
}

export default function CampoSenha({ registro, erro, autoComplete }: Props) {
  const [visivel, setVisivel] = useState(false)

  return (
    <CampoFormulario id={registro.name} rotulo="Senha" icone={Lock} erro={erro}>
      <input
        id={registro.name}
        type={visivel ? 'text' : 'password'}
        placeholder="••••••••"
        autoComplete={autoComplete}
        className={`${classeInput} pr-10`}
        {...registro}
      />
      <button
        type="button"
        onClick={() => setVisivel(!visivel)}
        className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-300"
        aria-label={visivel ? 'Esconder senha' : 'Mostrar senha'}
      >
        {visivel ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
      </button>
    </CampoFormulario>
  )
}