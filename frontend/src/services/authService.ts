import { api } from './api'

type RespostaDeAuth = { token: string }

export async function entrar(email: string, senha: string): Promise<string> {
  const { data } = await api.post<RespostaDeAuth>('/auth/login', { email, senha })
  return data.token
}

export async function cadastrar(nome: string, email: string, senha: string): Promise<string> {
  const { data } = await api.post<RespostaDeAuth>('/auth/register', { nome, email, senha })
  return data.token
}