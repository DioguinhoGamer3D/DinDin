import { useMemo, useState } from 'react'
import type { ReactNode } from 'react'
import { AuthContext } from './AuthContext'
import type { AuthContextoValor, Usuario } from './AuthContext'
import { cadastrar, entrar } from '../services/authService'
import { lerToken, limparToken, salvarToken } from '../services/token'
import { decodificarToken, tokenExpirado } from '../utils/jwt'

// Ao abrir o app: só aceita um token que exista, seja legível e não tenha expirado
function lerTokenValido(): string | null {
  const token = lerToken()
  if (!token) return null

  const dados = decodificarToken(token)
  if (!dados || tokenExpirado(dados)) {
    limparToken()
    return null
  }
  return token
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(lerTokenValido)

  const usuario = useMemo<Usuario | null>(() => {
    if (!token) return null
    const dados = decodificarToken(token)
    return dados ? { nome: dados.nome, email: dados.email } : null
  }, [token])

  const valor = useMemo<AuthContextoValor>(
    () => ({
      usuario,
      autenticado: token !== null,
      login: async (email, senha) => {
        const novoToken = await entrar(email, senha)
        salvarToken(novoToken)
        setToken(novoToken)
      },
      registrar: async (nome, email, senha) => {
        const novoToken = await cadastrar(nome, email, senha)
        salvarToken(novoToken)
        setToken(novoToken)
      },
      logout: () => {
        limparToken()
        setToken(null)
      },
    }),
    [token, usuario],
  )

  return <AuthContext.Provider value={valor}>{children}</AuthContext.Provider>
}