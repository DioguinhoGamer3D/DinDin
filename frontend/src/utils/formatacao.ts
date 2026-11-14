const formatadorDeMoeda = new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL',
})

export function formatarMoeda(valor: number): string {
  return formatadorDeMoeda.format(valor)
}

// '2026-11-12' -> '12/11/2026'
export function formatarData(data: string): string {
  const [ano, mes, dia] = data.split('-')
  return `${dia}/${mes}/${ano}`
}

function doisDigitos(numero: number): string {
  return String(numero).padStart(2, '0')
}

// Data de hoje no fuso do usuário, no formato '2026-11-12'
export function hoje(): string {
  const agora = new Date()
  return `${agora.getFullYear()}-${doisDigitos(agora.getMonth() + 1)}-${doisDigitos(agora.getDate())}`
}

// Mês atual no formato '2026-11'
export function mesAtual(): string {
  return hoje().slice(0, 7)
}

// Soma ou subtrai meses: deslocarMes('2026-01', -1) -> '2025-12'
export function deslocarMes(mes: string, delta: number): string {
  const [ano, numero] = mes.split('-').map(Number)
  const data = new Date(ano, numero - 1 + delta, 1)
  return `${data.getFullYear()}-${doisDigitos(data.getMonth() + 1)}`
}

// '2026-11' -> 'Novembro de 2026'
export function rotuloDoMes(mes: string): string {
  const [ano, numero] = mes.split('-').map(Number)
  const texto = new Date(ano, numero - 1, 1).toLocaleDateString('pt-BR', {
    month: 'long',
    year: 'numeric',
  })
  return texto.charAt(0).toUpperCase() + texto.slice(1)
}