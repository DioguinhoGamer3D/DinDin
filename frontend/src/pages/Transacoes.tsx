import { useEffect, useMemo, useState } from 'react'
import { ChevronDown, Pencil, Plus, Search, Trash2 } from 'lucide-react'
import { useLocation, useNavigate } from 'react-router'
import AlertaDeErro from '../components/AlertaDeErro'
import ModalConfirmacao from '../components/ModalConfirmacao'
import ModalTransacao from '../components/ModalTransacao'
import SeletorDeMes from '../components/SeletorDeMes'
import { classeInputSimples } from '../components/estilos'
import { useToast } from '../contexts/useToast'
import { listarCategorias } from '../services/categoriaService'
import { mensagemDeErro } from '../services/erros'
import {
  atualizarTransacao,
  criarTransacao,
  excluirTransacao,
  listarTransacoes,
} from '../services/transacaoService'
import { formatarData, formatarMoeda, mesAtual, rotuloDoMes } from '../utils/formatacao'
import { rotuloDoTipo } from '../utils/tipos'
import type { Categoria, DadosDaTransacao, TipoTransacao, Transacao } from '../types/dominio'

export default function Transacoes() {
  const { mostrarToast } = useToast()

  const location = useLocation()
  const navigate = useNavigate()

  const [mes, setMes] = useState(mesAtual)
  const [categorias, setCategorias] = useState<Categoria[]>([])
  const [transacoes, setTransacoes] = useState<Transacao[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [tentativa, setTentativa] = useState(0)

  const [busca, setBusca] = useState('')
  const [categoriaFiltro, setCategoriaFiltro] = useState('TODAS')
  const [tipoFiltro, setTipoFiltro] = useState<'TODOS' | TipoTransacao>('TODOS')

  const [modalAberto, setModalAberto] = useState(() => location.state?.abrirModal === true)
  const [editando, setEditando] = useState<Transacao | null>(null)
  const [excluindo, setExcluindo] = useState<Transacao | null>(null)

  useEffect(() => {
    if (location.state?.abrirModal) {
      navigate(location.pathname, { replace: true, state: null })
    }
  }, [location.pathname, location.state, navigate])

  useEffect(() => {
    let ativo = true

    Promise.all([listarCategorias(), listarTransacoes(mes)])
      .then(([categoriasRecebidas, transacoesRecebidas]) => {
        if (!ativo) return
        setCategorias(categoriasRecebidas)
        setTransacoes(transacoesRecebidas)
        setErro('')
      })
      .catch((falha) => {
        if (ativo) setErro(mensagemDeErro(falha))
      })
      .finally(() => {
        if (ativo) setCarregando(false)
      })

    return () => {
      ativo = false
    }
  }, [mes, tentativa])

  const corPorCategoria = useMemo(
    () => new Map(categorias.map((categoria) => [categoria.id, categoria.cor])),
    [categorias],
  )

  const filtradas = useMemo(() => {
    const termo = busca.trim().toLowerCase()

    return transacoes.filter((transacao) => {
      const combinaBusca =
        termo === '' ||
        transacao.descricao.toLowerCase().includes(termo) ||
        (transacao.observacoes?.toLowerCase().includes(termo) ?? false)
      const combinaCategoria =
        categoriaFiltro === 'TODAS' || transacao.categoriaId === categoriaFiltro
      const combinaTipo = tipoFiltro === 'TODOS' || transacao.tipo === tipoFiltro

      return combinaBusca && combinaCategoria && combinaTipo
    })
  }, [transacoes, busca, categoriaFiltro, tipoFiltro])

  function mudarMes(novoMes: string) {
    setCarregando(true)
    setMes(novoMes)
  }

  function tentarNovamente() {
    setErro('')
    setCarregando(true)
    setTentativa((t) => t + 1)
  }

  function abrirNova() {
    setEditando(null)
    setModalAberto(true)
  }

  function abrirEdicao(transacao: Transacao) {
    setEditando(transacao)
    setModalAberto(true)
  }

  async function salvar(dados: DadosDaTransacao) {
    if (editando) {
      await atualizarTransacao(editando.id, dados)
      mostrarToast('Transação atualizada com sucesso!')
    } else {
      await criarTransacao(dados)
      mostrarToast('Transação adicionada com sucesso!')
    }
    setModalAberto(false)

    // Se a data cai em outro mês, a tela vai para esse mês; senão, recarrega o atual
    const mesDaTransacao = dados.data.slice(0, 7)
    if (mesDaTransacao !== mes) {
      mudarMes(mesDaTransacao)
    } else {
      setTentativa((t) => t + 1)
    }
  }

  async function confirmarExclusao() {
    if (!excluindo) return

    try {
      await excluirTransacao(excluindo.id)
      mostrarToast('Transação removida com sucesso!', 'info')
      setTentativa((t) => t + 1)
    } catch (falha) {
      mostrarToast(mensagemDeErro(falha), 'erro')
    }
    setExcluindo(null)
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-white">Gestão de Transações</h2>
          <p className="text-sm text-gray-400">
            Cadastre, filtre e gerencie todas as suas movimentações
          </p>
        </div>
        <div className="flex flex-wrap items-center gap-3">
          <SeletorDeMes mes={mes} aoMudar={mudarMes} />
          <button
            type="button"
            onClick={abrirNova}
            className="bg-amber-400 hover:bg-amber-300 text-gray-950 font-bold px-4 py-2.5 rounded-xl shadow-lg shadow-amber-400/10 flex items-center gap-2 text-sm transition"
          >
            <Plus className="w-4 h-4 stroke-3" />
            <span>Nova Transação</span>
          </button>
        </div>
      </div>

      {erro && (
        <div>
          <AlertaDeErro mensagem={erro} />
          <button
            type="button"
            onClick={tentarNovamente}
            className="text-sm text-amber-400 hover:underline"
          >
            Tentar novamente
          </button>
        </div>
      )}

      <div className="bg-superficie border border-gray-800 p-4 rounded-2xl grid grid-cols-1 sm:grid-cols-3 gap-3">
        <div className="relative">
          <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-gray-500" />
          <input
            type="text"
            placeholder="Buscar por descrição..."
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
            className="w-full bg-campo border border-gray-700/60 rounded-xl pl-9 pr-4 py-2 text-sm text-white placeholder-gray-500 focus:outline-none focus:border-amber-400"
          />
        </div>

        <div className="relative">
          <select
            value={categoriaFiltro}
            onChange={(e) => setCategoriaFiltro(e.target.value)}
            className={`${classeInputSimples} appearance-none`}
          >
            <option value="TODAS">Todas as Categorias</option>
            {categorias.map((categoria) => (
              <option key={categoria.id} value={categoria.id}>
                {categoria.nome}
              </option>
            ))}
          </select>
          <ChevronDown className="w-4 h-4 absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 pointer-events-none" />
        </div>

        <div className="relative">
          <select
            value={tipoFiltro}
            onChange={(e) => setTipoFiltro(e.target.value as 'TODOS' | TipoTransacao)}
            className={`${classeInputSimples} appearance-none`}
          >
            <option value="TODOS">Todos os Tipos (Entradas/Saídas)</option>
            <option value="RECEITA">Somente Entradas (Receitas)</option>
            <option value="DESPESA">Somente Saídas (Despesas)</option>
          </select>
          <ChevronDown className="w-4 h-4 absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 pointer-events-none" />
        </div>
      </div>

      <div className="bg-superficie border border-gray-800 rounded-2xl overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-campo/50 border-b border-gray-800 text-xs font-semibold text-gray-400 uppercase tracking-wider">
                <th className="py-3.5 px-4">Descrição</th>
                <th className="py-3.5 px-4">Tipo</th>
                <th className="py-3.5 px-4">Categoria</th>
                <th className="py-3.5 px-4">Data</th>
                <th className="py-3.5 px-4 text-right">Valor</th>
                <th className="py-3.5 px-4 text-center">Ações</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-800/60 text-sm">
              {carregando ? (
                <tr>
                  <td colSpan={6} className="py-12 text-center text-gray-500 text-sm">
                    Carregando transações...
                  </td>
                </tr>
              ) : filtradas.length === 0 ? (
                <tr>
                  <td colSpan={6} className="py-12 text-center text-gray-500 text-sm">
                    {transacoes.length === 0
                      ? `Nenhuma transação registrada em ${rotuloDoMes(mes)}.`
                      : 'Nenhuma transação encontrada com os filtros selecionados.'}
                  </td>
                </tr>
              ) : (
                filtradas.map((transacao) => {
                  const ehReceita = transacao.tipo === 'RECEITA'
                  const cor = corPorCategoria.get(transacao.categoriaId) ?? '#6B7280'

                  return (
                    <tr key={transacao.id} className="hover:bg-gray-800/30 transition">
                      <td className="py-3.5 px-4">
                        <div className="font-semibold text-white">{transacao.descricao}</div>
                        {transacao.observacoes && (
                          <div className="text-xs text-gray-500">{transacao.observacoes}</div>
                        )}
                      </td>
                      <td className="py-3.5 px-4">
                        <span
                          className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-semibold ${
                            ehReceita
                              ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                              : 'bg-red-500/10 text-red-400 border border-red-500/20'
                          }`}
                        >
                          {rotuloDoTipo[transacao.tipo]}
                        </span>
                      </td>
                      <td className="py-3.5 px-4">
                        <span
                          className="inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-medium"
                          style={{ backgroundColor: `${cor}20`, color: cor }}
                        >
                          {transacao.categoriaNome}
                        </span>
                      </td>
                      <td className="py-3.5 px-4 text-gray-400 text-xs">
                        {formatarData(transacao.data)}
                      </td>
                      <td className="py-3.5 px-4 text-right font-bold whitespace-nowrap">
                        <span className={ehReceita ? 'text-emerald-400' : 'text-red-400'}>
                          {ehReceita ? '+' : '-'} {formatarMoeda(transacao.valor)}
                        </span>
                      </td>
                      <td className="py-3.5 px-4">
                        <div className="flex items-center justify-center gap-2">
                          <button
                            type="button"
                            onClick={() => abrirEdicao(transacao)}
                            className="p-1.5 text-gray-400 hover:text-amber-400 hover:bg-amber-400/10 rounded-lg transition"
                            title="Editar"
                            aria-label={`Editar ${transacao.descricao}`}
                          >
                            <Pencil className="w-4 h-4" />
                          </button>
                          <button
                            type="button"
                            onClick={() => setExcluindo(transacao)}
                            className="p-1.5 text-gray-400 hover:text-red-400 hover:bg-red-400/10 rounded-lg transition"
                            title="Excluir"
                            aria-label={`Excluir ${transacao.descricao}`}
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  )
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {modalAberto && (
        <ModalTransacao
          key={editando?.id ?? 'nova'}
          transacao={editando}
          categorias={categorias}
          aoFechar={() => setModalAberto(false)}
          aoSalvar={salvar}
        />
      )}

      {excluindo && (
        <ModalConfirmacao
          titulo="Excluir transação"
          mensagem={`Excluir "${excluindo.descricao}" (${formatarMoeda(excluindo.valor)})? Essa ação não pode ser desfeita.`}
          textoConfirmar="Excluir"
          aoConfirmar={confirmarExclusao}
          aoCancelar={() => setExcluindo(null)}
        />
      )}
    </div>
  )
}