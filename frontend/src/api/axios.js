import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
})

// Automatically attach JWT token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('vidyasagar_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// If token expires (401), clear it and redirect to login
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('vidyasagar_token')
      localStorage.removeItem('vidyasagar_user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api