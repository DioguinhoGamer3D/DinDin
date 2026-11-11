import { useState } from 'react'
import { Link, NavLink, Outlet } from 'react-router'
import { CreditCard, LayoutDashboard, LogOut, Menu, Tag, X } from 'lucide-react'
import type { LucideIcon } from 'lucide-react'
import Logo from './Logo'

const itens: { para: string; rotulo: string; icone: LucideIcon }[] = [
  { para: '/dashboard', rotulo: 'Dashboard', icone: LayoutDashboard },
  { para: '/transacoes', rotulo: 'Transações', icone: CreditCard },
  { para: '/categorias', rotulo: 'Categorias', icone: Tag },
]

function classeDoLink({ isActive }: { isActive: boolean }) {
  return `w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-semibold border transition ${
    isActive
      ? 'bg-amber-400/10 text-amber-400 border-amber-400/20'
      : 'text-gray-400 hover:text-white hover:bg-gray-800/50 border-transparent'
  }`
}

export default function Layout() {
  const [menuAberto, setMenuAberto] = useState(false)

  return (
    <div className="min-h-screen flex flex-col md:flex-row">
      {/* Sidebar (desktop) */}
      <aside className="hidden md:flex flex-col justify-between w-64 bg-superficie border-r border-gray-800/80 p-5">
        <div className="space-y-6">
          <div className="px-2">
            <Logo tamanho="md" />
          </div>

          <nav className="space-y-1">
            {itens.map(({ para, rotulo, icone: Icone }) => (
              <NavLink key={para} to={para} className={classeDoLink}>
                <Icone className="w-5 h-5" />
                <span>{rotulo}</span>
              </NavLink>
            ))}
          </nav>
        </div>

        <div className="pt-4 border-t border-gray-800 space-y-3">
          <div className="flex items-center gap-3 px-2">
            <div className="w-9 h-9 rounded-full bg-amber-400/20 text-amber-300 border border-amber-400/30 flex items-center justify-center text-sm font-bold">
              U
            </div>
            <div className="overflow-hidden">
              <p className="text-sm font-bold text-white truncate">Usuário</p>
              <p className="text-xs text-gray-500 truncate">email@exemplo.com</p>
            </div>
          </div>

          <Link
            to="/login"
            className="w-full flex items-center justify-center gap-2 py-2 text-xs font-semibold text-gray-400 hover:text-red-400 hover:bg-red-400/10 rounded-xl transition"
          >
            <LogOut className="w-4 h-4" />
            <span>Encerrar sessão</span>
          </Link>
        </div>
      </aside>

      {/* Cabeçalho (celular) */}
      <header className="md:hidden flex items-center justify-between p-4 bg-superficie border-b border-gray-800">
        <Logo tamanho="sm" />
        <button
          type="button"
          onClick={() => setMenuAberto(!menuAberto)}
          className="p-2 text-gray-400 hover:text-white"
          aria-label={menuAberto ? 'Fechar menu' : 'Abrir menu'}
        >
          {menuAberto ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
        </button>
      </header>

      {/* Menu aberto (celular) */}
      {menuAberto && (
        <nav className="md:hidden bg-superficie border-b border-gray-800 p-4 space-y-1">
          {itens.map(({ para, rotulo, icone: Icone }) => (
            <NavLink
              key={para}
              to={para}
              className={classeDoLink}
              onClick={() => setMenuAberto(false)}
            >
              <Icone className="w-5 h-5" />
              <span>{rotulo}</span>
            </NavLink>
          ))}
          <Link
            to="/login"
            className="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-semibold text-red-400 hover:bg-red-500/10"
          >
            <LogOut className="w-5 h-5" />
            <span>Sair</span>
          </Link>
        </nav>
      )}

      {/* Conteúdo da página */}
      <main className="flex-1 p-4 md:p-8 max-w-7xl mx-auto w-full">
        <Outlet />
      </main>
    </div>
  )
}