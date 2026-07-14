import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function ProtectedRoute({ children, requireRole }) {
  const { isLoggedIn, user, loading } = useAuth()

  if (loading) return (
    <div className="flex justify-center items-center h-screen">
      <div className="text-gray-500">Loading...</div>
    </div>
  )

  if (!isLoggedIn()) return <Navigate to="/login" replace />

  if (requireRole && user?.role !== requireRole) {
    return <Navigate to="/" replace />
  }

  return children
}