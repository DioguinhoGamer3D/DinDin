import { Navigate, Outlet } from 'react-router'
import { useAuth } from '../contexts/useAuth'

export default function RotaPublica() {
  const { autenticado } = useAuth()
  return autenticado ? <Navigate to="/dashboard" replace /> : <Outlet />
}