import { useState } from 'react'
import { Controller, useForm, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import Modal from './Modal'
import Campo from './Campo'
import AlertaDeErro from './AlertaDeErro'
import { classeInputSimples } from './estilos'
import { mensagemDeErro } from '../services/erros'
import { hoje } from '../utils/formatacao'
import type { Categoria, DadosDaTransacao, TipoTransacao, Transacao } from '../types/dominio'

const schema = z.object({
  tipo: z.enum(['RECEITA', 'DESPESA']),
  descricao: z
    .string()
    .trim()
    .min(1, 'Descrição é obrigatória')
    .max(255, 'Descrição deve ter no máximo 255 caracteres'),
  valor: z
    .string()
    .min(1, 'Valor é obrigatório')
    .regex(/^\d{1,10}(\.\d{1,2})?$/, 'Informe um valor válido, com até 2 casas decimais')
    .refine((texto) => Number(texto) > 0, 'Valor deve ser maior que zero'),
  data: z.string().min(1, 'Data é obrigatória'),
  categoriaId: z.string().min(1, 'Selecione uma categoria'),
  observacoes: z.string().max(500, 'Observações deve ter no máximo 500 caracteres'),
})

type DadosDoFormulario = z.infer<typeof schema>

type Props = {
  transacao: Transacao | null
  categorias: Categoria[]
  aoFechar: () => void
  aoSalvar: (dados: DadosDaTransacao) => Promise<void>
}

export default function ModalTransacao({ transacao, categorias, aoFechar, aoSalvar }: Props) {
  const editando = transacao !== null
  const [erroDoServidor, setErroDoServidor] = useState('')

  const tipoInicial: TipoTransacao = transacao?.tipo ?? 'DESPESA'
  const primeiraCategoria = categorias.find((c) => c.tipo === tipoInicial)

  const {
    register,
    handleSubmit,
    control,
    setValue,
    formState: { errors, isSubmitting },
  } = useForm<DadosDoFormulario>({
    resolver: zodResolver(schema),
    defaultValues: {
      tipo: tipoInicial,
      descricao: transacao?.descricao ?? '',
      valor: transacao ? transacao.valor.toFixed(2) : '',
      data: transacao?.data ?? hoje(),
      categoriaId: transacao?.categoriaId ?? primeiraCategoria?.id ?? '',
      observacoes: transacao?.observacoes ?? '',
    },
  })

  const tipo = useWatch({ control, name: 'tipo' })
  const categoriasDoTipo = categorias.filter((c) => c.tipo === tipo)

  function trocarTipo(novoTipo: TipoTransacao) {
    if (novoTipo === tipo) return
    setValue('tipo', novoTipo)
    setValue('categoriaId', categorias.find((c) => c.tipo === novoTipo)?.id ?? '')
  }

  async function aoEnviar(dados: DadosDoFormulario) {
    setErroDoServidor('')
    const observacoes = dados.observacoes.trim()

    try {
      await aoSalvar({
        tipo: dados.tipo,
        descricao: dados.descricao,
        valor: Number(dados.valor),
        data: dados.data,
        categoriaId: dados.categoriaId,
        observacoes: observacoes === '' ? null : observacoes,
      })
    } catch (erro) {
      setErroDoServidor(mensagemDeErro(erro))
    }
  }

  return (
    <Modal titulo={editando ? 'Editar Transação' : 'Nova Transação'} aoFechar={aoFechar}>
      {erroDoServidor && <AlertaDeErro mensagem={erroDoServidor} />}

      <form onSubmit={handleSubmit(aoEnviar)} noValidate className="space-y-4">
        <div className="grid grid-cols-2 gap-3 p-1 bg-campo rounded-xl">
          <button
            type="button"
            onClick={() => trocarTipo('RECEITA')}
            className={`py-2 text-xs font-bold rounded-lg transition ${
              tipo === 'RECEITA' ? 'bg-emerald-500 text-white shadow' : 'text-gray-400 hover:text-white'
            }`}
          >
            ENTRADA (Receita)
          </button>
          <button
            type="button"
            onClick={() => trocarTipo('DESPESA')}
            className={`py-2 text-xs font-bold rounded-lg transition ${
              tipo === 'DESPESA' ? 'bg-red-500 text-white shadow' : 'text-gray-400 hover:text-white'
            }`}
          >
            SAÍDA (Despesa)
          </button>
        </div>

        <Campo id="descricao" rotulo="Descrição" erro={errors.descricao?.message}>
          <input
            id="descricao"
            type="text"
            placeholder="Ex: Supermercado, Salário, Freela..."
            className={classeInputSimples}
            {...register('descricao')}
          />
        </Campo>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <Campo id="valor" rotulo="Valor (R$)" erro={errors.valor?.message}>
            <input
              id="valor"
              type="number"
              step="0.01"
              inputMode="decimal"
              placeholder="0,00"
              className={classeInputSimples}
              {...register('valor')}
            />
          </Campo>

          <Campo id="data" rotulo="Data" erro={errors.data?.message}>
            <input id="data" type="date" className={classeInputSimples} {...register('data')} />
          </Campo>
        </div>

        <Campo id="categoriaId" rotulo="Categoria" erro={errors.categoriaId?.message}>
          {categoriasDoTipo.length === 0 ? (
            <p className="text-sm text-amber-400">
              Nenhuma categoria de {tipo === 'RECEITA' ? 'entrada' : 'saída'} cadastrada. Crie uma
              na tela de Categorias.
            </p>
          ) : (
            <Controller
              control={control}
              name="categoriaId"
              render={({ field }) => (
                <select id="categoriaId" className={classeInputSimples} {...field}>
                  {categoriasDoTipo.map((categoria) => (
                    <option key={categoria.id} value={categoria.id}>
                      {categoria.nome}
                    </option>
                  ))}
                </select>
              )}
            />
          )}
        </Campo>

        <Campo id="observacoes" rotulo="Observações (Opcional)" erro={errors.observacoes?.message}>
          <textarea
            id="observacoes"
            rows={2}
            placeholder="Anotações adicionais..."
            className={classeInputSimples}
            {...register('observacoes')}
          />
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
            disabled={isSubmitting || categoriasDoTipo.length === 0}
            className="flex-1 py-2.5 bg-amber-400 hover:bg-amber-300 text-gray-950 font-bold rounded-xl transition text-sm shadow-lg shadow-amber-400/10 disabled:opacity-50"
          >
            {editando ? 'Salvar Alterações' : 'Cadastrar'}
          </button>
        </div>
      </form>
    </Modal>
  )
}