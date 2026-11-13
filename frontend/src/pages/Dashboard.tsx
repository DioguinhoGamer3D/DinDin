import { useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router'
import {
  ArrowDownRight,
  ArrowRight,
  ArrowUpRight,
  ChartColumn,
  ChartPie,
  Plus,
  TrendingUp,
  Wallet,
} from 'lucide-react'
import { CartesianGrid, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import AlertaDeErro from '../components/AlertaDeErro'
import CartaoDeResumo from '../components/CartaoDeResumo'
import SeletorDeMes from '../components/SeletorDeMes'
import { useDadosDoMes } from '../hooks/useDadosDoMes'
import { calcularTotais, fluxoPorSemana, gastosPorCategoria } from '../utils/calculos'
import { formatarData, formatarMoeda, mesAtual, rotuloDoMes } from '../utils/formatacao'

export default function Dashboard() {
  const navigate = useNavigate()
  const [mes, setMes] = useState(mesAtual)
  const { categorias, transacoes, carregando, erro, recarregar } = useDadosDoMes(mes)

  const totais = useMemo(() => calcularTotais(transacoes), [transacoes])
  const fluxo = useMemo(() => fluxoPorSemana(transacoes), [transacoes])
  const corPorCategoria = useMemo(
    () => new Map(categorias.map((categoria) => [categoria.id, categoria.cor])),
    [categorias],
  )
  const gastos = useMemo(
    () => gastosPorCategoria(transacoes, corPorCategoria),
    [transacoes, corPorCategoria],
  )
  const recentes = transacoes.slice(0, 5)

  const saldoPositivo = totais.saldo >= 0

  return (
    <div className="space-y-6">
      {/* Faixa de boas-vindas */}
      <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4 bg-linear-to-r from-amber-500/10 via-superficie to-superficie border border-amber-500/20 p-6 rounded-2xl">
        <div className="space-y-1">
          <span className="inline-block px-2.5 py-0.5 rounded-full text-xs font-semibold bg-amber-400/20 text-amber-300 border border-amber-400/30">
            Painel Financeiro
          </span>
          <h2 className="text-2xl font-bold text-white">Resumo Patrimonial</h2>
          <p className="text-sm text-gray-400">
            Acompanhe seus rendimentos e mantenha suas contas no azul.
          </p>
        </div>
        <div className="flex flex-wrap items-center gap-3">
          <SeletorDeMes mes={mes} aoMudar={setMes} />
          <button
            type="button"
            onClick={() => navigate('/transacoes', { state: { abrirModal: true } })}
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
            onClick={recarregar}
            className="text-sm text-amber-400 hover:underline"
          >
            Tentar novamente
          </button>
        </div>
      )}

      {carregando && <p className="text-sm text-gray-400">Carregando painel...</p>}

      {!carregando && !erro && (
        <>
          {/* Cartões de resumo */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <CartaoDeResumo
              rotulo="Saldo do Mês"
              valor={formatarMoeda(totais.saldo)}
              icone={Wallet}
              variante="ouro"
            >
              <span
                className={`flex items-center font-medium ${
                  saldoPositivo ? 'text-emerald-400' : 'text-red-400'
                }`}
              >
                <TrendingUp className="w-3.5 h-3.5 mr-1" />
                {totais.taxaDePoupanca === null
                  ? 'Taxa de Poupança: sem entradas'
                  : `Taxa de Poupança: ${totais.taxaDePoupanca.toFixed(1)}%`}
              </span>
            </CartaoDeResumo>

            <CartaoDeResumo
              rotulo="Total Entradas"
              valor={formatarMoeda(totais.receitas)}
              icone={ArrowUpRight}
              variante="verde"
            >
              <span className="text-gray-400">Receitas brutas registradas</span>
            </CartaoDeResumo>

            <CartaoDeResumo
              rotulo="Total Saídas"
              valor={formatarMoeda(totais.despesas)}
              icone={ArrowDownRight}
              variante="vermelho"
            >
              <span className="text-gray-400">Despesas e pagamentos efetuados</span>
            </CartaoDeResumo>

            <CartaoDeResumo
              rotulo="Balanço do Mês"
              valor={saldoPositivo ? 'Positivo' : 'Atenção'}
              icone={ChartColumn}
              variante="roxo"
            >
              <span className="text-gray-400">
                {saldoPositivo ? 'Orçamento sob controle' : 'Despesas ultrapassaram receitas'}
              </span>
            </CartaoDeResumo>
          </div>

          {/* Gráficos */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2 bg-superficie border border-gray-800 p-6 rounded-2xl">
              <div className="flex flex-wrap items-center justify-between gap-2 mb-6">
                <div>
                  <h3 className="text-base font-bold text-white flex items-center gap-2">
                    <ChartColumn className="w-4 h-4 text-amber-400" />
                    <span>Fluxo Financeiro do Mês</span>
                  </h3>
                  <p className="text-xs text-gray-400">
                    Movimentações por semana em {rotuloDoMes(mes)}
                  </p>
                </div>
                <div className="flex items-center gap-3 text-xs">
                  <span className="flex items-center gap-1.5 text-emerald-400">
                    <span className="w-2.5 h-2.5 rounded-full bg-emerald-400 inline-block" />
                    <span>Entradas</span>
                  </span>
                  <span className="flex items-center gap-1.5 text-red-400">
                    <span className="w-2.5 h-2.5 rounded-full bg-red-400 inline-block" />
                    <span>Saídas</span>
                  </span>
                </div>
              </div>

              {transacoes.length === 0 ? (
                <p className="h-64 flex items-center justify-center text-sm text-gray-500">
                  Nenhuma movimentação neste mês.
                </p>
              ) : (
                <ResponsiveContainer width="100%" height={256}>
                  <LineChart data={fluxo} margin={{ top: 8, right: 16, bottom: 0, left: 16 }}>
                    <CartesianGrid stroke="#1F2937" strokeDasharray="4" vertical={false} />
                    <XAxis
                      dataKey="semana"
                      stroke="#6B7280"
                      tickLine={false}
                      axisLine={false}
                      fontSize={12}
                    />
                    <YAxis hide />
                    <Tooltip
                      contentStyle={{
                        backgroundColor: '#121824',
                        border: '1px solid #1F2937',
                        borderRadius: 12,
                      }}
                      labelStyle={{ color: '#9CA3AF' }}
                      formatter={(valor) => formatarMoeda(Number(valor))}
                    />
                    <Line
                      type="monotone"
                      dataKey="entradas"
                      name="Entradas"
                      stroke="#10B981"
                      strokeWidth={3}
                      dot={{ r: 4 }}
                    />
                    <Line
                      type="monotone"
                      dataKey="saidas"
                      name="Saídas"
                      stroke="#EF4444"
                      strokeWidth={3}
                      dot={{ r: 4 }}
                    />
                  </LineChart>
                </ResponsiveContainer>
              )}
            </div>

            <div className="bg-superficie border border-gray-800 p-6 rounded-2xl flex flex-col justify-between">
              <div>
                <h3 className="text-base font-bold text-white flex items-center gap-2">
                  <ChartPie className="w-4 h-4 text-amber-400" />
                  <span>Despesas por Categoria</span>
                </h3>
                <p className="text-xs text-gray-400">Para onde está indo o seu dinheiro</p>
              </div>

              <div className="space-y-4 my-4">
                {gastos.length === 0 ? (
                  <p className="text-sm text-gray-500 text-center py-8">
                    Nenhuma despesa registrada.
                  </p>
                ) : (
                  gastos.slice(0, 5).map((gasto) => {
                    const percentual =
                      totais.despesas > 0 ? Math.round((gasto.valor / totais.despesas) * 100) : 0

                    return (
                      <div key={gasto.categoriaId} className="space-y-1.5">
                        <div className="flex justify-between gap-2 text-xs">
                          <span className="text-gray-300 font-medium flex items-center gap-1.5 min-w-0">
                            <span
                              className="w-2.5 h-2.5 rounded-full inline-block shrink-0"
                              style={{ backgroundColor: gasto.cor }}
                            />
                            <span className="truncate">{gasto.nome}</span>
                          </span>
                          <span className="text-gray-400 whitespace-nowrap">
                            {formatarMoeda(gasto.valor)} ({percentual}%)
                          </span>
                        </div>
                        <div className="w-full h-2 bg-gray-800 rounded-full overflow-hidden">
                          <div
                            className="h-full rounded-full transition-all duration-500"
                            style={{ width: `${percentual}%`, backgroundColor: gasto.cor }}
                          />
                        </div>
                      </div>
                    )
                  })
                )}
              </div>

              <Link
                to="/categorias"
                className="w-full text-center py-2.5 text-xs text-amber-400 hover:text-amber-300 border border-gray-800 hover:border-amber-500/40 rounded-xl transition font-medium"
              >
                Gerenciar Categorias
              </Link>
            </div>
          </div>

          {/* Últimas movimentações */}
          <div className="bg-superficie border border-gray-800 rounded-2xl p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-base font-bold text-white">Últimas Movimentações</h3>
              <Link
                to="/transacoes"
                className="text-xs text-amber-400 hover:text-amber-300 font-semibold flex items-center gap-1"
              >
                <span>Ver Todas</span>
                <ArrowRight className="w-3.5 h-3.5" />
              </Link>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-gray-800 text-xs font-semibold text-gray-400 uppercase tracking-wider">
                    <th className="py-3 px-3">Descrição</th>
                    <th className="py-3 px-3">Categoria</th>
                    <th className="py-3 px-3">Data</th>
                    <th className="py-3 px-3 text-right">Valor</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-800/60 text-sm">
                  {recentes.length === 0 ? (
                    <tr>
                      <td colSpan={4} className="py-8 text-center text-gray-500 text-sm">
                        Nenhuma movimentação em {rotuloDoMes(mes)}.
                      </td>
                    </tr>
                  ) : (
                    recentes.map((transacao) => {
                      const ehReceita = transacao.tipo === 'RECEITA'
                      const cor = corPorCategoria.get(transacao.categoriaId) ?? '#6B7280'

                      return (
                        <tr key={transacao.id} className="hover:bg-gray-800/30 transition">
                          <td className="py-3 px-3">
                            <div className="font-medium text-white">{transacao.descricao}</div>
                            {transacao.observacoes && (
                              <div className="text-xs text-gray-500">{transacao.observacoes}</div>
                            )}
                          </td>
                          <td className="py-3 px-3">
                            <span
                              className="inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-medium"
                              style={{ backgroundColor: `${cor}20`, color: cor }}
                            >
                              {transacao.categoriaNome}
                            </span>
                          </td>
                          <td className="py-3 px-3 text-gray-400 text-xs">
                            {formatarData(transacao.data)}
                          </td>
                          <td className="py-3 px-3 text-right font-semibold whitespace-nowrap">
                            <span className={ehReceita ? 'text-emerald-400' : 'text-red-400'}>
                              {ehReceita ? '+' : '-'} {formatarMoeda(transacao.valor)}
                            </span>
                          </td>
                        </tr>
                      )
                    })
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </>
      )}
    </div>
  )
}