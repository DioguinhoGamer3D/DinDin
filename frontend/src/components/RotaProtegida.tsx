import { Navigate, Outlet } from 'react-router'
import { useAuth } from '../contexts/useAuth'

export default function RotaProtegida() {
  const { autenticado } = useAuth()
  return autenticado ? <Outlet /> : <Navigate to="/login" replace />
}