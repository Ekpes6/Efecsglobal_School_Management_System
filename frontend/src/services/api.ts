import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1',
  headers: { 'Content-Type': 'application/json' },
})

// Attach JWT token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// Auto-refresh on 401
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true
      const refreshToken = localStorage.getItem('refreshToken')
      if (refreshToken) {
        try {
          const { data } = await axios.post('/api/v1/auth/refresh', { refreshToken })
          localStorage.setItem('accessToken', data.accessToken)
          original.headers.Authorization = `Bearer ${data.accessToken}`
          return api(original)
        } catch {
          localStorage.clear()
          window.location.href = '/login'
        }
      }
    }
    return Promise.reject(error)
  }
)

export default api

// Auth
export const authApi = {
  login: (email: string, password: string) => api.post('/auth/login', { email, password }),
  register: (data: object) => api.post('/auth/register', data),
  me: () => api.get('/auth/me'),
  logout: () => api.post('/auth/logout'),
}

// Schools
export const schoolApi = {
  register: (data: object) => api.post('/schools/register', data),
  get: (id: number) => api.get(`/schools/${id}`),
  list: (params?: object) => api.get('/schools', { params }),
  update: (id: number, data: object) => api.put(`/schools/${id}`, data),
  getSessions: (schoolId: number) => api.get(`/schools/${schoolId}/sessions`),
  createSession: (schoolId: number, data: object) => api.post(`/schools/${schoolId}/sessions`, data),
  getClasses: (schoolId: number) => api.get(`/schools/${schoolId}/classes`),
  createClass: (schoolId: number, data: object) => api.post(`/schools/${schoolId}/classes`, data),
}

// Students
export const studentApi = {
  enroll: (data: object) => api.post('/students', data),
  get: (id: number) => api.get(`/students/${id}`),
  getBySchool: (schoolId: number, params?: object) => api.get(`/students/school/${schoolId}`, { params }),
  search: (schoolId: number, query: string) => api.get(`/students/school/${schoolId}/search`, { params: { query } }),
  update: (id: number, data: object) => api.put(`/students/${id}`, data),
  count: (schoolId: number) => api.get(`/students/school/${schoolId}/count`),
}

// Academic
export const academicApi = {
  recordResult: (data: object) => api.post('/results', data),
  getStudentResults: (studentId: number) => api.get(`/results/student/${studentId}`),
  getPublishedResults: (studentId: number) => api.get(`/results/student/${studentId}/published`),
  getTermResults: (studentId: number, sessionId: number, term: string) =>
    api.get(`/results/student/${studentId}/session/${sessionId}/term/${term}`),
  publishResults: (data: object) => api.post('/results/publish', data),
  getSubjects: (schoolId: number) => api.get(`/subjects/school/${schoolId}`),
  getReportCard: (studentId: number, sessionId: number, term: string) =>
    api.get(`/academic/report-card/${studentId}/session/${sessionId}/term/${term}`),
}

// Guardian
export const guardianApi = {
  create: (data: object) => api.post('/guardians', data),
  get: (id: number) => api.get(`/guardians/${id}`),
  getByUserId: (userId: number) => api.get(`/guardians/user/${userId}`),
  getWards: (guardianId: number) => api.get(`/guardians/${guardianId}/wards`),
  linkWard: (guardianId: number, data: object) => api.post(`/guardians/${guardianId}/wards`, data),
}

// Financial
export const financialApi = {
  createFee: (data: object) => api.post('/fees', data),
  getFees: (schoolId: number) => api.get(`/fees/school/${schoolId}`),
  initiatePayment: (data: object) => api.post('/payments/initiate', data),
  recordCash: (data: object) => api.post('/payments/cash', data),
  getStudentPayments: (studentId: number) => api.get(`/payments/student/${studentId}`),
  getSchoolPayments: (schoolId: number) => api.get(`/payments/school/${schoolId}`),
}

// File
export const fileApi = {
  upload: (formData: FormData) => api.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  get: (id: number) => api.get(`/files/${id}`),
  delete: (id: number) => api.delete(`/files/${id}`),
}
