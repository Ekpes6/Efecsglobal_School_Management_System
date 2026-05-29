import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './contexts/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import Layout from './components/Layout'
import LoginPage from './pages/LoginPage'
import RegisterSchoolPage from './pages/RegisterSchoolPage'
import DashboardPage from './pages/DashboardPage'
import StudentsPage from './pages/StudentsPage'
import ResultsPage from './pages/ResultsPage'
import GuardianPortalPage from './pages/GuardianPortalPage'

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Public */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register-school" element={<RegisterSchoolPage />} />
          <Route path="/" element={<Navigate to="/login" replace />} />

          {/* Protected */}
          <Route element={<ProtectedRoute />}>
            <Route path="/dashboard" element={<Layout><DashboardPage /></Layout>} />
            <Route path="/students" element={<Layout><StudentsPage /></Layout>} />
            <Route path="/results" element={<Layout><ResultsPage /></Layout>} />
            <Route path="/guardian-portal" element={<Layout><GuardianPortalPage /></Layout>} />
          </Route>

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
