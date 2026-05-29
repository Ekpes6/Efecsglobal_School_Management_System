import { useQuery } from '@tanstack/react-query'
import { useAuth } from '../contexts/AuthContext'
import { studentApi } from '../services/api'
import { Link } from 'react-router-dom'
import { Users, Plus, Search } from 'lucide-react'
import { useState } from 'react'

interface Student {
  id: number; firstName: string; lastName: string; admissionNumber: string;
  academicLevel: string; status: string; classId: number; dateOfBirth: string
}

export default function StudentsPage() {
  const { user } = useAuth()
  const [searchQuery, setSearchQuery] = useState('')

  const { data, isLoading } = useQuery({
    queryKey: ['students', user?.schoolId, searchQuery],
    queryFn: () => searchQuery
      ? studentApi.search(user!.schoolId!, searchQuery).then(r => r.data)
      : studentApi.getBySchool(user!.schoolId!).then(r => r.data),
    enabled: !!user?.schoolId,
  })

  const students: Student[] = data?.content ?? []

  return (
    <div className="p-6 space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
            <Users className="w-6 h-6" /> Students
          </h1>
          <p className="text-sm text-gray-500 mt-1">Manage enrolled students</p>
        </div>
        <Link to="/students/new" className="btn-primary flex items-center gap-2">
          <Plus className="w-4 h-4" /> Enroll Student
        </Link>
      </div>

      <div className="card">
        <div className="flex items-center gap-3 mb-4">
          <div className="relative flex-1">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              className="input pl-9"
              placeholder="Search by name or admission number..."
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
            />
          </div>
        </div>

        {isLoading ? (
          <div className="flex justify-center py-8">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600" />
          </div>
        ) : students.length === 0 ? (
          <div className="text-center py-12 text-gray-400">
            <Users className="w-12 h-12 mx-auto mb-3 opacity-50" />
            <p>No students found</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-gray-500 border-b">
                  <th className="pb-3 pr-4">Name</th>
                  <th className="pb-3 pr-4">Admission No.</th>
                  <th className="pb-3 pr-4">Level</th>
                  <th className="pb-3 pr-4">Status</th>
                  <th className="pb-3">Actions</th>
                </tr>
              </thead>
              <tbody>
                {students.map(s => (
                  <tr key={s.id} className="border-b last:border-0 hover:bg-gray-50">
                    <td className="py-3 pr-4 font-medium">{s.firstName} {s.lastName}</td>
                    <td className="py-3 pr-4 font-mono text-xs">{s.admissionNumber}</td>
                    <td className="py-3 pr-4">
                      <span className="badge-blue">{s.academicLevel}</span>
                    </td>
                    <td className="py-3 pr-4">
                      <span className={s.status === 'ACTIVE' ? 'badge-green' : 'badge-red'}>
                        {s.status}
                      </span>
                    </td>
                    <td className="py-3">
                      <Link to={`/students/${s.id}`} className="text-blue-600 hover:underline text-xs mr-3">View</Link>
                      <Link to={`/results?studentId=${s.id}`} className="text-purple-600 hover:underline text-xs">Results</Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}
