import type { ReactNode } from 'react'
import { NavLink } from 'react-router'
import { Wallet } from 'lucide-react'

function classeDaAba({ isActive }: { isActive: boolean }) {
  return `flex-1 py-3 text-sm font-semibold text-center transition-all border-b-2 ${
    isActive
      ? 'border-amber-400 text-amber-400'
      : 'border-transparent text-gray-400 hover:text-gray-200'
  }`
}

export default function AuthLayout({ children }: { children: ReactNode }) {
  return (
    <div className="min-h-screen flex items-center justify-center p-4 relative overflow-hidden">
      <div className="absolute -top-40 -left-40 w-96 h-96 bg-amber-500/10 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute -bottom-40 -right-40 w-96 h-96 bg-yellow-600/10 rounded-full blur-3xl pointer-events-none" />

      <div className="w-full max-w-md z-10">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-linear-to-tr from-amber-500 to-yellow-300 shadow-lg shadow-amber-500/20 mb-4">
            <Wallet className="w-8 h-8 text-gray-950" />
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight text-white">
            Din<span className="text-amber-400">Din</span>
          </h1>
          <p className="text-gray-400 text-sm mt-1">Sua gestão financeira em um novo patamar</p>
        </div>

        <div className="bg-superficie/90 backdrop-blur-xl border border-gray-800 rounded-2xl p-6 md:p-8 shadow-2xl">
          <div className="flex border-b border-gray-800 mb-6">
            <NavLink to="/login" className={classeDaAba}>
              Entrar
            </NavLink>
            <NavLink to="/registro" className={classeDaAba}>
              Criar Conta
            </NavLink>
          </div>

          {children}
        </div>

        <p className="text-center text-gray-500 text-xs mt-6">&copy; 2026 DinDin</p>
      </div>
    </div>
  )
}