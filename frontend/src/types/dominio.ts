export type TipoTransacao = 'RECEITA' | 'DESPESA'

export type Categoria = {
  id: string
  nome: string
  tipo: TipoTransacao
  cor: string
}

export type DadosDaCategoria = Omit<Categoria, 'id'>

export type Transacao = {
  id: string
  descricao: string
  valor: number
  data: string
  tipo: TipoTransacao
  categoriaId: string
  categoriaNome: string
  observacoes: string | null
}

export type DadosDaTransacao = {
  descricao: string
  valor: number
  data: string
  tipo: TipoTransacao
  categoriaId: string
  observacoes: string | null
}