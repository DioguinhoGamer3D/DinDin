import { useState } from 'react'
import { useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import Modal from './Modal'
import Campo from './Campo'
import AlertaDeErro from './AlertaDeErro'
import { classeInputSimples } from './estilos'
import { mensagemDeErro } from '../services/erros'
import { rotuloDoTipo } from '../utils/tipos'
import type { Categoria, DadosDaCategoria } from '../types/dominio'

const schema = z.object({
  nome: z
    .string()
    .trim()
    .min(1, 'Nome é obrigatório')
    .max(100, 'Nome deve ter no máximo 100 caracteres'),
  tipo: z.enum(['RECEITA', 'DESPESA']),
  cor: z.string().regex(/^#[0-9A-Fa-f]{6}$/, 'Cor inválida'),
})

type DadosDoFormulario = z.infer<typeof schema>

type Props = {
  categoria: Categoria | null
  aoFechar: () => void
  aoSalvar: (dados: DadosDaCategoria) => Promise<void>
}

export default function ModalCategoria({ categoria, aoFechar, aoSalvar }: Props) {
  const editando = categoria !== null
  const [erroDoServidor, setErroDoServidor] = useState('')

  const {
    register,
    handleSubmit,
    control,
    formState: { errors, isSubmitting },
  } = useForm<DadosDoFormulario>({
    resolver: zodResolver(schema),
    defaultValues: {
      nome: categoria?.nome ?? '',
      tipo: categoria?.tipo ?? 'DESPESA',
      cor: categoria?.cor ?? '#f59e0b',
    },
  })

  const cor = useWatch({ control, name: 'cor' })

  async function aoEnviar(dados: DadosDoFormulario) {
    setErroDoServidor('')
    try {
      await aoSalvar(dados)
    } catch (erro) {
      setErroDoServidor(mensagemDeErro(erro))
    }
  }

  return (
    <Modal titulo={editando ? 'Editar Categoria' : 'Nova Categoria'} aoFechar={aoFechar}>
      {erroDoServidor && <AlertaDeErro mensagem={erroDoServidor} />}

      <form onSubmit={handleSubmit(aoEnviar)} noValidate className="space-y-4">
        <Campo id="nome" rotulo="Nome da Categoria" erro={errors.nome?.message}>
          <input
            id="nome"
            type="text"
            placeholder="Ex: Assinaturas, Educação, Viagens..."
            className={classeInputSimples}
            {...register('nome')}
          />
        </Campo>

        {editando && categoria ? (
          <div>
            <span className="block text-xs font-medium text-gray-400 uppercase tracking-wider mb-1">
              Tipo de Fluxo
            </span>
            <p className="text-sm text-gray-300">{rotuloDoTipo[categoria.tipo]}</p>
            <p className="text-xs text-gray-500 mt-1">
              O tipo não pode ser alterado depois que a categoria é criada.
            </p>
          </div>
        ) : (
          <Campo id="tipo" rotulo="Tipo de Fluxo" erro={errors.tipo?.message}>
            <select id="tipo" className={classeInputSimples} {...register('tipo')}>
              <option value="DESPESA">SAÍDA (Despesas)</option>
              <option value="RECEITA">ENTRADA (Receitas)</option>
            </select>
          </Campo>
        )}

        <Campo id="cor" rotulo="Cor de Identificação" erro={errors.cor?.message}>
          <div className="flex items-center gap-3">
            <input
              id="cor"
              type="color"
              className="w-12 h-10 bg-transparent border-0 cursor-pointer rounded-lg"
              {...register('cor')}
            />
            <span className="text-sm font-mono text-gray-300">{cor}</span>
          </div>
        </Campo>

        <div className="flex gap-3 pt-3">
          <button
            type="button"
            onClick={aoFechar}
            className="flex-1 py-2.5 bg-gray-800 hover:bg-gray-700 text-gray-300 font-semibold rounded-xl transition text-sm"
          >
            Cancelar
          </button>
          <button
            type="submit"
            disabled={isSubmitting}
            className="flex-1 py-2.5 bg-amber-400 hover:bg-amber-300 text-gray-950 font-bold rounded-xl transition text-sm shadow-lg shadow-amber-400/10 disabled:opacity-50"
          >
            {editando ? 'Salvar' : 'Cadastrar'}
          </button>
        </div>
      </form>
    </Modal>
  )
}