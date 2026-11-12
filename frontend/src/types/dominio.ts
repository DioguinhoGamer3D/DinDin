export type TipoTransacao = 'RECEITA' | 'DESPESA'

export type Categoria = {
  id: string
  nome: string
  tipo: TipoTransacao
  cor: string
}

export type DadosDaCategoria = Omit<Categoria, 'id'>