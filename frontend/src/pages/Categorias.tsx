import { useEffect, useState } from 'react'
import { Pencil, Plus, Trash2 } from 'lucide-react'
import ModalCategoria from '../components/ModalCategoria'
import AlertaDeErro from '../components/AlertaDeErro'
import {
  atualizarCategoria,
  criarCategoria,
  excluirCategoria,
  listarCategorias,
} from '../services/categoriaService'
import { mensagemDeErro } from '../services/erros'
import { useToast } from '../contexts/useToast'
import { rotuloDoTipo } from '../utils/tipos'
import type { Categoria, DadosDaCategoria } from '../types/dominio'

function ordenar(lista: Categoria[]): Categoria[] {
  return [...lista].sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'))
}

export default function Categorias() {
  const { mostrarToast } = useToast()

  const [categorias, setCategorias] = useState<Categoria[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erroDeCarga, setErroDeCarga] = useState('')
  const [tentativa, setTentativa] = useState(0)

  const [modalAberto, setModalAberto] = useState(false)
  const [editando, setEditando] = useState<Categoria | null>(null)

  useEffect(() => {
    let ativo = true

    listarCategorias()
      .then((dados) => {
        if (ativo) setCategorias(ordenar(dados))
      })
      .catch((erro) => {
        if (ativo) setErroDeCarga(mensagemDeErro(erro))
      })
      .finally(() => {
        if (ativo) setCarregando(false)
      })

    return () => {
      ativo = false
    }
  }, [tentativa])

  function tentarNovamente() {
    setErroDeCarga('')
    setCarregando(true)
    setTentativa((t) => t + 1)
  }

  function abrirNova() {
    setEditando(null)
    setModalAberto(true)
  }

  function abrirEdicao(categoria: Categoria) {
    setEditando(categoria)
    setModalAberto(true)
  }

  async function salvar(dados: DadosDaCategoria) {
    if (editando) {
      const atualizada = await atualizarCategoria(editando.id, dados)
      setCategorias((atuais) =>
        ordenar(atuais.map((c) => (c.id === atualizada.id ? atualizada : c))),
      )
      mostrarToast('Categoria atualizada!')
    } else {
      const nova = await criarCategoria(dados)
      setCategorias((atuais) => ordenar([...atuais, nova]))
      mostrarToast('Nova categoria cadastrada!')
    }
    setModalAberto(false)
  }

  async function excluir(categoria: Categoria) {
    try {
      await excluirCategoria(categoria.id)
      setCategorias((atuais) => atuais.filter((c) => c.id !== categoria.id))
      mostrarToast('Categoria excluída!', 'info')
    } catch (erro) {
      mostrarToast(mensagemDeErro(erro), 'erro')
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-white">Categorias de Orçamento</h2>
          <p className="text-sm text-gray-400">
            Organize suas movimentações em grupos personalizados
          </p>
        </div>
        <button
          type="button"
          onClick={abrirNova}
          className="bg-amber-400 hover:bg-amber-300 text-gray-950 font-bold px-4 py-2.5 rounded-xl shadow-lg shadow-amber-400/10 flex items-center justify-center gap-2 text-sm transition"
        >
          <Plus className="w-4 h-4 stroke-3" />
          <span>Nova Categoria</span>
        </button>
      </div>

      {carregando && <p className="text-sm text-gray-400">Carregando categorias...</p>}

      {erroDeCarga && (
        <div>
          <AlertaDeErro mensagem={erroDeCarga} />
          <button
            type="button"
            onClick={tentarNovamente}
            className="text-sm text-amber-400 hover:underline"
          >
            Tentar novamente
          </button>
        </div>
      )}

      {!carregando && !erroDeCarga && categorias.length === 0 && (
        <div className="bg-superficie border border-gray-800 rounded-2xl p-8 text-center">
          <p className="text-gray-300 font-medium">Nenhuma categoria cadastrada ainda.</p>
          <p className="text-sm text-gray-500 mt-1">
            Crie a primeira para começar a registrar suas movimentações.
          </p>
        </div>
      )}

      {categorias.length > 0 && (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          {categorias.map((categoria) => (
            <div
              key={categoria.id}
              className="bg-superficie border border-gray-800 p-5 rounded-2xl flex flex-col justify-between hover:border-gray-700 transition group"
            >
              <div>
                <div className="flex items-center justify-between mb-3">
                  <span
                    className="w-4 h-4 rounded-full inline-block"
                    style={{ backgroundColor: categoria.cor }}
                  />
                  <span
                    className={`text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded ${
                      categoria.tipo === 'RECEITA'
                        ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                        : 'bg-red-500/10 text-red-400 border border-red-500/20'
                    }`}
                  >
                    {rotuloDoTipo[categoria.tipo]}
                  </span>
                </div>
                <h4 className="text-base font-bold text-white mb-1 break-words">
                  {categoria.nome}
                </h4>
              </div>

              <div className="pt-4 border-t border-gray-800/60 mt-4 flex justify-between items-center text-xs text-gray-500">
                <span className="font-mono">{categoria.cor}</span>
                <div className="flex items-center gap-3 opacity-80 group-hover:opacity-100">
                  <button
                    type="button"
                    onClick={() => abrirEdicao(categoria)}
                    className="text-gray-500 hover:text-amber-400 transition"
                    title="Editar Categoria"
                    aria-label={`Editar categoria ${categoria.nome}`}
                  >
                    <Pencil className="w-4 h-4" />
                  </button>
                  <button
                    type="button"
                    onClick={() => excluir(categoria)}
                    className="text-gray-500 hover:text-red-400 transition"
                    title="Excluir Categoria"
                    aria-label={`Excluir categoria ${categoria.nome}`}
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {modalAberto && (
        <ModalCategoria
          key={editando?.id ?? 'nova'}
          categoria={editando}
          aoFechar={() => setModalAberto(false)}
          aoSalvar={salvar}
        />
      )}
    </div>
  )
}