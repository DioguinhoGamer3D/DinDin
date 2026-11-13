import axios from 'axios'

export function mensagemDeErro(erro: unknown): string {
  if (axios.isAxiosError(erro)) {
    if (!erro.response) {
      return 'Não foi possível conectar ao servidor. Verifique sua conexão.'
    }
    const mensagem = erro.response.data?.mensagem
    if (typeof mensagem === 'string') {
      return mensagem
    }
  }
  return 'Ocorreu um erro inesperado. Tente novamente.'
}