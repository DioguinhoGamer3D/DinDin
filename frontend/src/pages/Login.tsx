import { Link } from 'react-router'

export default function Login() {
  return (
    <main className="min-h-screen flex flex-col items-center justify-center gap-4 bg-slate-100">
      <h1 className="text-3xl font-bold text-slate-800">Login</h1>
      <Link to="/registro" className="text-blue-600 hover:underline">
        Criar conta
      </Link>
    </main>
  )
}