import axios from 'axios'
import { lerToken, limparToken } from './token'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080',
})

api.interceptors.request.use((config) => {
  const token = lerToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (resposta) => resposta,
  (erro: unknown) => {
    if (
      axios.isAxiosError(erro) &&
      erro.response?.status === 401 &&
      !erro.config?.url?.startsWith('/auth/')
    ) {
      limparToken()
      window.location.href = '/login'
    }
    return Promise.reject(erro)
  },
)