export type DadosDoToken = {
  email: string
  nome: string
  userId: string
  expiraEm: number
}

export function decodificarToken(token: string): DadosDoToken | null {
  try {
    const parte = token.split('.')[1]
    if (!parte) return null

    const base64 = parte.replace(/-/g, '+').replace(/_/g, '/')
    const binario = atob(base64.padEnd(Math.ceil(base64.length / 4) * 4, '='))
    const bytes = Uint8Array.from(binario, (c) => c.charCodeAt(0))
    const payload = JSON.parse(new TextDecoder().decode(bytes))

    if (
      typeof payload.sub !== 'string' ||
      typeof payload.nome !== 'string' ||
      typeof payload.userId !== 'string' ||
      typeof payload.exp !== 'number'
    ) {
      return null
    }

    return {
      email: payload.sub,
      nome: payload.nome,
      userId: payload.userId,
      expiraEm: payload.exp * 1000,
    }
  } catch {
    return null
  }
}

export function tokenExpirado(dados: DadosDoToken): boolean {
  return dados.expiraEm <= Date.now()
}