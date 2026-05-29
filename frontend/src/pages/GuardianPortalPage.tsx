import { useAuth } from '../contexts/AuthContext'
import { useQuery } from '@tanstack/react-query'
import { guardianApi, academicApi } from '../services/api'
import { useState } from 'react'

interface Ward {
  id: number; studentId: number; studentName: string; admissionNumber: string;
  relationship: string; schoolId: number
}

interface Result {
  id: number; subjectName: string; totalScore: number; grade: string;
  gradeRemark: string; term: string; sessionName: string
}

export default function GuardianPortalPage() {
  const { user } = useAuth()
  const [selectedWard, setSelectedWard] = useState<Ward | null>(null)

  const { data: guardian } = useQuery({
    queryKey: ['guardianByUser', user?.id],
    queryFn: () => user ? guardianApi.getByUserId(user.id).then(r => r.data) : null,
    enabled: !!user && user.role === 'GUARDIAN',
  })

  const { data: wards } = useQuery({
    queryKey: ['wards', guardian?.id],
    queryFn: () => guardian ? guardianApi.getWards(guardian.id).then(r => r.data) : null,
    enabled: !!guardian,
  })

  const { data: results } = useQuery({
    queryKey: ['wardResults', selectedWard?.studentId],
    queryFn: () => selectedWard
      ? academicApi.getPublishedResults(selectedWard.studentId).then(r => r.data)
      : null,
    enabled: !!selectedWard,
  })

  const wardList: Ward[] = Array.isArray(wards) ? wards : []
  const resultList: Result[] = Array.isArray(results) ? results : []

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Guardian Portal</h1>
        <p className="text-sm text-gray-500 mt-1">View your ward's academic performance</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Ward List */}
        <div className="card">
          <h2 className="font-semibold text-gray-800 mb-4">My Wards</h2>
          {wardList.length === 0 ? (
            <p className="text-gray-400 text-sm">No wards linked to your account</p>
          ) : (
            <div className="space-y-2">
              {wardList.map(ward => (
                <button
                  key={ward.id}
                  onClick={() => setSelectedWard(ward)}
                  className={`w-full text-left p-3 rounded-lg border transition-colors ${
                    selectedWard?.id === ward.id
                      ? 'border-blue-500 bg-blue-50'
                      : 'border-gray-200 hover:bg-gray-50'
                  }`}
                >
                  <div className="font-medium text-sm">{ward.studentName}</div>
                  <div className="text-xs text-gray-500 mt-0.5">
                    {ward.admissionNumber} · {ward.relationship}
                  </div>
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Results */}
        <div className="lg:col-span-2">
          {!selectedWard ? (
            <div className="card h-full flex items-center justify-center text-gray-400">
              <div className="text-center">
                <p className="text-lg">Select a ward to view their results</p>
                <p className="text-sm mt-1">Only published results are visible</p>
              </div>
            </div>
          ) : (
            <div className="card">
              <h2 className="font-semibold text-gray-800 mb-4">
                Results for {selectedWard.studentName}
              </h2>
              {resultList.length === 0 ? (
                <p className="text-gray-400 text-sm">No published results yet</p>
              ) : (
                <div className="overflow-x-auto">
                  <table className="w-full text-sm">
                    <thead>
                      <tr className="text-left text-gray-500 border-b">
                        <th className="pb-2 pr-4">Subject</th>
                        <th className="pb-2 pr-4">Session</th>
                        <th className="pb-2 pr-4">Term</th>
                        <th className="pb-2 pr-4 text-center">Score</th>
                        <th className="pb-2 pr-4 text-center">Grade</th>
                        <th className="pb-2">Remark</th>
                      </tr>
                    </thead>
                    <tbody>
                      {resultList.map(r => (
                        <tr key={r.id} className="border-b last:border-0 hover:bg-gray-50">
                          <td className="py-2 pr-4 font-medium">{r.subjectName}</td>
                          <td className="py-2 pr-4 text-gray-500">{r.sessionName}</td>
                          <td className="py-2 pr-4 text-gray-500 text-xs">{r.term?.replace('_', ' ')}</td>
                          <td className="py-2 pr-4 text-center font-semibold">{r.totalScore}</td>
                          <td className="py-2 pr-4 text-center">
                            <span className={`font-bold ${['A1','B2','B3'].includes(r.grade)
                              ? 'text-green-600' : ['F9'].includes(r.grade)
                              ? 'text-red-600' : 'text-blue-600'}`}>
                              {r.grade}
                            </span>
                          </td>
                          <td className="py-2 text-gray-500">{r.gradeRemark}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
