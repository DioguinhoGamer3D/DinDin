import { createContext } from 'react'

export type Usuario = {
  nome: string
  email: string
}

export type AuthContextoValor = {
  usuario: Usuario | null
  autenticado: boolean
  login: (email: string, senha: string) => Promise<void>
  registrar: (nome: string, email: string, senha: string) => Promise<void>
  logout: () => void
}

export const AuthContext = createContext<AuthContextoValor | null>(null)