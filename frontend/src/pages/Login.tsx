import { Link } from 'react-router'
import Logo from '../components/Logo'

export default function Login() {
  return (
    <main className="min-h-screen flex flex-col items-center justify-center gap-6 p-4">
      <Logo tamanho="lg" />
      <h1 className="text-xl font-semibold text-white">Login</h1>
      <Link to="/registro" className="text-amber-400 hover:underline">
        Criar conta
      </Link>
    </main>
  )
}