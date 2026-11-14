import type { TipoTransacao } from '../types/dominio'

export const rotuloDoTipo: Record<TipoTransacao, string> = {
  RECEITA: 'ENTRADA',
  DESPESA: 'SAÍDA',
}