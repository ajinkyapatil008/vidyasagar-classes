import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { user, isLoggedIn, isTeacher, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <nav className="bg-white border-b border-gray-200 px-6 py-3 flex items-center justify-between sticky top-0 z-50">
      <Link to="/" className="text-xl font-bold text-blue-700">
        📚 VidyaSagar Classes
      </Link>

      <div className="flex items-center gap-4">
        <Link to="/" className="text-sm text-gray-600 hover:text-blue-600">
          Courses
        </Link>

        {isLoggedIn() ? (
          <>
            {isTeacher() && (
              <Link
                to="/teacher"
                className="text-sm text-gray-600 hover:text-blue-600"
              >
                My Dashboard
              </Link>
            )}
            {!isTeacher() && (
              <Link
                to="/my-courses"
                className="text-sm text-gray-600 hover:text-blue-600"
              >
                My Courses
              </Link>
            )}
            <span className="text-sm text-gray-500">
              Hi, {user?.name?.split(' ')[0]}
            </span>
            <button
              onClick={handleLogout}
              className="text-sm text-red-500 hover:text-red-700"
            >
              Logout
            </button>
          </>
        ) : (
          <>
            <Link
              to="/login"
              className="text-sm text-gray-600 hover:text-blue-600"
            >
              Login
            </Link>
            <Link
              to="/register"
              className="bg-blue-600 text-white text-sm px-4 py-2 rounded-lg hover:bg-blue-700"
            >
              Sign Up
            </Link>
          </>
        )}
      </div>
    </nav>
  )
}