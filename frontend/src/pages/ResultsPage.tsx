import { useQuery } from '@tanstack/react-query'
import { academicApi } from '../services/api'
import { useState } from 'react'
import { BookOpen } from 'lucide-react'

const TERMS = ['FIRST_TERM', 'SECOND_TERM', 'THIRD_TERM']

interface Result {
  id: number; subjectName: string; ca1Score: number; ca2Score: number;
  ca3Score: number; examScore: number; totalScore: number; grade: string;
  gradeRemark: string; positionInClass: number; term: string
}

export default function ResultsPage() {
  const [studentId, setStudentId] = useState('')
  const [sessionId, setSessionId] = useState('')
  const [term, setTerm] = useState('FIRST_TERM')
  const [searched, setSearched] = useState(false)

  const { data: results, isLoading } = useQuery({
    queryKey: ['results', studentId, sessionId, term],
    queryFn: () => academicApi.getTermResults(Number(studentId), Number(sessionId), term).then(r => r.data),
    enabled: searched && !!studentId && !!sessionId,
  })

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    setSearched(true)
  }

  const getGradeColor = (grade: string) => {
    if (['A1','B2','B3'].includes(grade)) return 'text-green-600 font-bold'
    if (['C4','C5','C6'].includes(grade)) return 'text-blue-600 font-bold'
    if (['D7','E8'].includes(grade)) return 'text-yellow-600 font-bold'
    return 'text-red-600 font-bold'
  }

  const resultList: Result[] = Array.isArray(results) ? results : []
  const average = resultList.length
    ? (resultList.reduce((sum, r) => sum + r.totalScore, 0) / resultList.length).toFixed(1)
    : null

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
          <BookOpen className="w-6 h-6" /> Academic Results
        </h1>
      </div>

      <div className="card">
        <h2 className="font-semibold text-gray-700 mb-4">View Student Report Card</h2>
        <form onSubmit={handleSearch} className="flex flex-wrap gap-3 items-end">
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Student ID</label>
            <input className="input w-36" value={studentId} onChange={e => setStudentId(e.target.value)} placeholder="Student ID" required />
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Session ID</label>
            <input className="input w-32" value={sessionId} onChange={e => setSessionId(e.target.value)} placeholder="Session ID" required />
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Term</label>
            <select className="input w-40" value={term} onChange={e => setTerm(e.target.value)}>
              {TERMS.map(t => <option key={t} value={t}>{t.replace('_', ' ')}</option>)}
            </select>
          </div>
          <button type="submit" className="btn-primary">View Results</button>
        </form>
      </div>

      {isLoading && (
        <div className="flex justify-center py-8">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600" />
        </div>
      )}

      {searched && !isLoading && resultList.length > 0 && (
        <div className="card">
          <div className="flex justify-between items-center mb-4">
            <h2 className="font-semibold text-gray-800">
              Report Card — {term.replace('_', ' ')}
            </h2>
            {average && (
              <div className="text-sm text-gray-600">
                Average: <span className="font-bold text-blue-700">{average}%</span>
              </div>
            )}
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-gray-500 border-b bg-gray-50">
                  <th className="py-2 px-3">Subject</th>
                  <th className="py-2 px-3 text-center">CA1 (10)</th>
                  <th className="py-2 px-3 text-center">CA2 (10)</th>
                  <th className="py-2 px-3 text-center">CA3 (10)</th>
                  <th className="py-2 px-3 text-center">Exam (70)</th>
                  <th className="py-2 px-3 text-center">Total (100)</th>
                  <th className="py-2 px-3 text-center">Grade</th>
                  <th className="py-2 px-3">Remark</th>
                  <th className="py-2 px-3 text-center">Position</th>
                </tr>
              </thead>
              <tbody>
                {resultList.map(r => (
                  <tr key={r.id} className="border-b last:border-0 hover:bg-gray-50">
                    <td className="py-2 px-3 font-medium">{r.subjectName}</td>
                    <td className="py-2 px-3 text-center">{r.ca1Score ?? '—'}</td>
                    <td className="py-2 px-3 text-center">{r.ca2Score ?? '—'}</td>
                    <td className="py-2 px-3 text-center">{r.ca3Score ?? '—'}</td>
                    <td className="py-2 px-3 text-center">{r.examScore ?? '—'}</td>
                    <td className="py-2 px-3 text-center font-semibold">{r.totalScore}</td>
                    <td className={`py-2 px-3 text-center ${getGradeColor(r.grade)}`}>{r.grade}</td>
                    <td className="py-2 px-3 text-gray-500">{r.gradeRemark}</td>
                    <td className="py-2 px-3 text-center">{r.positionInClass ?? '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <div className="mt-4 text-xs text-gray-400">
            Grading: A1(75-100) B2(70-74) B3(65-69) C4(60-64) C5(55-59) C6(50-54) D7(45-49) E8(40-44) F9(&lt;40)
          </div>
        </div>
      )}

      {searched && !isLoading && resultList.length === 0 && (
        <div className="card text-center py-8 text-gray-400">
          No results found for the selected criteria
        </div>
      )}
    </div>
  )
}
