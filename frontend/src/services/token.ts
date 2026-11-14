const CHAVE = 'dindin_token'

export function lerToken(): string | null {
  try {
    return localStorage.getItem(CHAVE)
  } catch {
    return null
  }
}

export function salvarToken(token: string): void {
  try {
    localStorage.setItem(CHAVE, token)
  } catch {
    // armazenamento indisponível: a sessão só dura até recarregar a página
  }
}

export function limparToken(): void {
  try {
    localStorage.removeItem(CHAVE)
  } catch {
    // nada a fazer
  }
}