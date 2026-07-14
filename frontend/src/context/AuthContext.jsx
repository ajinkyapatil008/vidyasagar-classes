import { createContext, useContext, useState, useEffect } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [token, setToken] = useState(null)
  const [loading, setLoading] = useState(true)

  // Load from localStorage on app start
  useEffect(() => {
    const savedToken = localStorage.getItem('vidyasagar_token')
    const savedUser  = localStorage.getItem('vidyasagar_user')
    if (savedToken && savedUser) {
      setToken(savedToken)
      setUser(JSON.parse(savedUser))
    }
    setLoading(false)
  }, [])

  const login = (userData, jwtToken) => {
    setUser(userData)
    setToken(jwtToken)
    localStorage.setItem('vidyasagar_token', jwtToken)
    localStorage.setItem('vidyasagar_user', JSON.stringify(userData))
  }

  const logout = () => {
    setUser(null)
    setToken(null)
    localStorage.removeItem('vidyasagar_token')
    localStorage.removeItem('vidyasagar_user')
  }

  const isTeacher = () => user?.role === 'TEACHER'
  const isStudent = () => user?.role === 'STUDENT'
  const isLoggedIn = () => !!token

  return (
    <AuthContext.Provider value={{
      user, token, loading,
      login, logout,
      isTeacher, isStudent, isLoggedIn
    }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)