import type { Transacao } from '../types/dominio'

export type Totais = {
  receitas: number
  despesas: number
  saldo: number
  taxaDePoupanca: number | null
}

export type GastoPorCategoria = {
  categoriaId: string
  nome: string
  valor: number
  cor: string
}

export type PontoSemanal = {
  semana: string
  entradas: number
  saidas: number
}

const COR_PADRAO = '#9CA3AF'

// Soma em centavos inteiros para não acumular erro de ponto flutuante
function emCentavos(valor: number): number {
  return Math.round(valor * 100)
}

export function calcularTotais(transacoes: Transacao[]): Totais {
  let receitas = 0
  let despesas = 0

  for (const transacao of transacoes) {
    if (transacao.tipo === 'RECEITA') {
      receitas += emCentavos(transacao.valor)
    } else {
      despesas += emCentavos(transacao.valor)
    }
  }

  return {
    receitas: receitas / 100,
    despesas: despesas / 100,
    saldo: (receitas - despesas) / 100,
    taxaDePoupanca: receitas > 0 ? ((receitas - despesas) / receitas) * 100 : null,
  }
}

export function gastosPorCategoria(
  transacoes: Transacao[],
  corPorCategoria: Map<string, string>,
): GastoPorCategoria[] {
  const acumulado = new Map<string, { nome: string; centavos: number }>()

  for (const transacao of transacoes) {
    if (transacao.tipo !== 'DESPESA') continue

    const atual = acumulado.get(transacao.categoriaId)
    acumulado.set(transacao.categoriaId, {
      nome: transacao.categoriaNome,
      centavos: (atual?.centavos ?? 0) + emCentavos(transacao.valor),
    })
  }

  return Array.from(acumulado, ([categoriaId, { nome, centavos }]) => ({
    categoriaId,
    nome,
    valor: centavos / 100,
    cor: corPorCategoria.get(categoriaId) ?? COR_PADRAO,
  })).sort((a, b) => b.valor - a.valor)
}

// Semana 1: dias 1-7 | Semana 2: 8-14 | Semana 3: 15-21 | Semana 4: 22 até o fim do mês
export function fluxoPorSemana(transacoes: Transacao[]): PontoSemanal[] {
  const semanas = [
    { entradas: 0, saidas: 0 },
    { entradas: 0, saidas: 0 },
    { entradas: 0, saidas: 0 },
    { entradas: 0, saidas: 0 },
  ]

  for (const transacao of transacoes) {
    const dia = Number(transacao.data.slice(8, 10))
    const indice = Math.min(Math.floor((dia - 1) / 7), 3)

    if (transacao.tipo === 'RECEITA') {
      semanas[indice].entradas += emCentavos(transacao.valor)
    } else {
      semanas[indice].saidas += emCentavos(transacao.valor)
    }
  }

  return semanas.map((semana, indice) => ({
    semana: `Semana ${indice + 1}`,
    entradas: semana.entradas / 100,
    saidas: semana.saidas / 100,
  }))
}