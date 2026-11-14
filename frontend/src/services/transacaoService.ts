import { api } from './api'
import type { DadosDaTransacao, Transacao } from '../types/dominio'

export async function listarTransacoes(mes: string): Promise<Transacao[]> {
  const { data } = await api.get<Transacao[]>('/transacoes', { params: { mes } })
  return data
}

export async function criarTransacao(dados: DadosDaTransacao): Promise<Transacao> {
  const { data } = await api.post<Transacao>('/transacoes', dados)
  return data
}

export async function atualizarTransacao(id: string, dados: DadosDaTransacao): Promise<Transacao> {
  const { data } = await api.put<Transacao>(`/transacoes/${id}`, dados)
  return data
}

export async function excluirTransacao(id: string): Promise<void> {
  await api.delete(`/transacoes/${id}`)
}