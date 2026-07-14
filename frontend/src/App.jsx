import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import Navbar from './components/Navbar'
import ProtectedRoute from './components/ProtectedRoute'

// Pages (we'll create these Day 2 onwards)
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import CourseDetail from './pages/CourseDetail'
import Watch from './pages/Watch'
import TeacherDashboard from './pages/TeacherDashboard'
import MyCourses from './pages/MyCourses'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Navbar />
        <Routes>
          {/* Public routes */}
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/courses/:courseId" element={<CourseDetail />} />

          {/* Student protected routes */}
          <Route path="/watch/:lessonId" element={
            <ProtectedRoute>
              <Watch />
            </ProtectedRoute>
          } />
          <Route path="/my-courses" element={
            <ProtectedRoute>
              <MyCourses />
            </ProtectedRoute>
          } />

          {/* Teacher only routes */}
          <Route path="/teacher" element={
            <ProtectedRoute requireRole="TEACHER">
              <TeacherDashboard />
            </ProtectedRoute>
          } />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}