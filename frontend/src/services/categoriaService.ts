import { api } from './api'
import type { Categoria, DadosDaCategoria } from '../types/dominio'

export async function listarCategorias(): Promise<Categoria[]> {
  const { data } = await api.get<Categoria[]>('/categorias')
  return data
}

export async function criarCategoria(dados: DadosDaCategoria): Promise<Categoria> {
  const { data } = await api.post<Categoria>('/categorias', dados)
  return data
}

export async function atualizarCategoria(id: string, dados: DadosDaCategoria): Promise<Categoria> {
  const { data } = await api.put<Categoria>(`/categorias/${id}`, dados)
  return data
}

export async function excluirCategoria(id: string): Promise<void> {
  await api.delete(`/categorias/${id}`)
}