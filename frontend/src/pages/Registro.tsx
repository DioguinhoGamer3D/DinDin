import { Link } from 'react-router'
import Logo from '../components/Logo'

export default function Registro() {
  return (
    <main className="min-h-screen flex flex-col items-center justify-center gap-6 p-4">
      <Logo tamanho="lg" />
      <h1 className="text-xl font-semibold text-white">Registro</h1>
      <Link to="/login" className="text-amber-400 hover:underline">
        Já tenho conta
      </Link>
    </main>
  )
}