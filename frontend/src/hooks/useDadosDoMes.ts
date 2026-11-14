import { useEffect, useState } from 'react'
import { listarCategorias } from '../services/categoriaService'
import { mensagemDeErro } from '../services/erros'
import { listarTransacoes } from '../services/transacaoService'
import type { Categoria, Transacao } from '../types/dominio'

type Resultado =
  | { mes: string; situacao: 'ok'; categorias: Categoria[]; transacoes: Transacao[] }
  | { mes: string; situacao: 'erro'; mensagem: string }

export function useDadosDoMes(mes: string) {
  const [resultado, setResultado] = useState<Resultado | null>(null)
  const [tentativa, setTentativa] = useState(0)

  useEffect(() => {
    let ativo = true

    Promise.all([listarCategorias(), listarTransacoes(mes)])
      .then(([categorias, transacoes]) => {
        if (ativo) setResultado({ mes, situacao: 'ok', categorias, transacoes })
      })
      .catch((falha) => {
        if (ativo) setResultado({ mes, situacao: 'erro', mensagem: mensagemDeErro(falha) })
      })

    return () => {
      ativo = false
    }
  }, [mes, tentativa])

  // O resultado só vale se for do mês que está na tela
  const atual = resultado?.mes === mes ? resultado : null

  return {
    carregando: atual === null,
    erro: atual?.situacao === 'erro' ? atual.mensagem : '',
    categorias: atual?.situacao === 'ok' ? atual.categorias : [],
    transacoes: atual?.situacao === 'ok' ? atual.transacoes : [],
    // Busca de novo sem piscar a tela (depois de salvar algo, por exemplo)
    atualizar: () => setTentativa((t) => t + 1),
    // Busca de novo mostrando "carregando" (botão "Tentar novamente")
    recarregar: () => {
      setResultado(null)
      setTentativa((t) => t + 1)
    },
  }
}