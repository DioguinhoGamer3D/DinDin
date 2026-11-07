import { Link } from 'react-router'

export default function Registro() {
  return (
    <main className="min-h-screen flex flex-col items-center justify-center gap-4 bg-slate-100">
      <h1 className="text-3xl font-bold text-slate-800">Registro</h1>
      <Link to="/login" className="text-blue-600 hover:underline">
        Já tenho conta
      </Link>
    </main>
  )
}