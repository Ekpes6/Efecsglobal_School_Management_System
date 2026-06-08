import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useAuth } from '../contexts/AuthContext'
import { attendanceApi, schoolApi } from '../services/api'
import { ClipboardList, Check, X, Clock, AlertCircle } from 'lucide-react'
import { useState } from 'react'

const TERMS = ['FIRST_TERM', 'SECOND_TERM', 'THIRD_TERM']
const STATUSES = [
  { value: 'PRESENT', label: 'Present', icon: Check, color: 'text-green-600 bg-green-50 border-green-200' },
  { value: 'ABSENT',  label: 'Absent',  icon: X,     color: 'text-red-600 bg-red-50 border-red-200' },
  { value: 'LATE',    label: 'Late',    icon: Clock,  color: 'text-yellow-600 bg-yellow-50 border-yellow-200' },
  { value: 'EXCUSED', label: 'Excused', icon: AlertCircle, color: 'text-blue-600 bg-blue-50 border-blue-200' },
]

interface AttendanceEntry {
  studentId: string; studentName: string; status: string
}

export default function AttendancePage() {
  const { user } = useAuth()
  const qc = useQueryClient()

  const today = new Date().toISOString().split('T')[0]
  const [classId, setClassId]     = useState('')
  const [sessionId, setSessionId] = useState('')
  const [term, setTerm]           = useState('FIRST_TERM')
  const [date, setDate]           = useState(today)
  const [loaded, setLoaded]       = useState(false)
  const [entries, setEntries]     = useState<AttendanceEntry[]>([])

  const { data: classes = [] } = useQuery<{ id: number; name: string }[]>({
    queryKey: ['classes', user?.schoolId],
    queryFn: () => user?.schoolId ? schoolApi.getClasses(user.schoolId).then(r => r.data) : [],
    enabled: !!user?.schoolId,
  })

  const { data: existingAttendance } = useQuery({
    queryKey: ['attendance', classId, date],
    queryFn: () => attendanceApi.getClassAttendance(Number(classId), date).then(r => r.data),
    enabled: loaded && !!classId,
  })

  const saveAttendance = useMutation({
    mutationFn: (records: object[]) => attendanceApi.markAttendance(records),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['attendance'] }),
  })

  const handleLoad = () => {
    if (!classId) return
    setLoaded(true)
    // Pre-fill from existing or empty; user adds student entries manually
    const existing: AttendanceEntry[] = (existingAttendance ?? []).map((a: {
      studentId: number; studentName: string; status: string
    }) => ({
      studentId: String(a.studentId),
      studentName: a.studentName,
      status: a.status,
    }))
    if (existing.length > 0) setEntries(existing)
    else setEntries([{ studentId: '', studentName: '', status: 'PRESENT' }])
  }

  const addRow = () => setEntries(e => [...e, { studentId: '', studentName: '', status: 'PRESENT' }])
  const removeRow = (i: number) => setEntries(e => e.filter((_, idx) => idx !== i))
  const updateEntry = (i: number, field: keyof AttendanceEntry, value: string) =>
    setEntries(e => e.map((row, idx) => idx === i ? { ...row, [field]: value } : row))

  const handleSave = () => {
    const records = entries
      .filter(e => e.studentId)
      .map(e => ({
        schoolId: user?.schoolId, classId, sessionId, term, attendanceDate: date,
        studentId: e.studentId, studentName: e.studentName, status: e.status,
        markedBy: user?.id,
      }))
    saveAttendance.mutate(records)
  }

  const presentCount = entries.filter(e => e.status === 'PRESENT').length
  const absentCount  = entries.filter(e => e.status === 'ABSENT').length

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
          <ClipboardList className="w-6 h-6" /> Attendance
        </h1>
        <p className="text-sm text-gray-500 mt-1">Mark and track daily class attendance</p>
      </div>

      {/* Filters */}
      <div className="card">
        <div className="flex flex-wrap gap-3 items-end">
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Class</label>
            <select className="input w-44" value={classId} onChange={e => setClassId(e.target.value)}>
              <option value="">Select class...</option>
              {classes.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Session ID</label>
            <input className="input w-28" type="number" placeholder="Session" value={sessionId}
              onChange={e => setSessionId(e.target.value)} />
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Term</label>
            <select className="input w-40" value={term} onChange={e => setTerm(e.target.value)}>
              {TERMS.map(t => <option key={t} value={t}>{t.replace('_', ' ')}</option>)}
            </select>
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Date</label>
            <input className="input w-40" type="date" value={date} onChange={e => setDate(e.target.value)} />
          </div>
          <button className="btn-primary" onClick={handleLoad} disabled={!classId}>
            Load Attendance
          </button>
        </div>
      </div>

      {loaded && (
        <>
          {/* Summary badges */}
          <div className="flex gap-3 flex-wrap">
            <span className="px-3 py-1.5 rounded-full text-sm bg-green-100 text-green-700 font-medium">
              ✓ Present: {presentCount}
            </span>
            <span className="px-3 py-1.5 rounded-full text-sm bg-red-100 text-red-700 font-medium">
              ✗ Absent: {absentCount}
            </span>
            <span className="px-3 py-1.5 rounded-full text-sm bg-gray-100 text-gray-600 font-medium">
              Total: {entries.filter(e => e.studentId).length}
            </span>
          </div>

          {/* Attendance Table */}
          <div className="card">
            <div className="flex justify-between items-center mb-4">
              <h2 className="font-semibold text-gray-800">Attendance Register — {date}</h2>
              <button className="btn-secondary text-xs" onClick={addRow}>+ Add Student</button>
            </div>

            <div className="space-y-2">
              {entries.map((entry, i) => (
                <div key={i} className="flex gap-3 items-center p-2 rounded-lg border border-gray-100 hover:bg-gray-50">
                  <span className="text-xs text-gray-400 w-6 text-center">{i + 1}</span>
                  <input className="input flex-1 max-w-[120px]" type="number" placeholder="Student ID"
                    value={entry.studentId} onChange={e => updateEntry(i, 'studentId', e.target.value)} />
                  <input className="input flex-1" placeholder="Student Name (optional)"
                    value={entry.studentName} onChange={e => updateEntry(i, 'studentName', e.target.value)} />
                  <div className="flex gap-1">
                    {STATUSES.map(s => (
                      <button key={s.value}
                        onClick={() => updateEntry(i, 'status', s.value)}
                        className={`px-2 py-1 rounded-md border text-xs font-medium transition-all
                          ${entry.status === s.value ? s.color : 'text-gray-400 border-gray-200 hover:bg-gray-50'}`}>
                        {s.label}
                      </button>
                    ))}
                  </div>
                  <button onClick={() => removeRow(i)} className="text-gray-300 hover:text-red-400 p-1">
                    <X className="w-4 h-4" />
                  </button>
                </div>
              ))}
            </div>

            {entries.length === 0 && (
              <div className="text-center py-8 text-gray-400">
                <ClipboardList className="w-10 h-10 mx-auto mb-2 opacity-40" />
                <p className="text-sm">No entries yet — click &ldquo;Add Student&rdquo; to begin</p>
              </div>
            )}

            <div className="mt-4 flex justify-end">
              <button className="btn-primary"
                disabled={saveAttendance.isPending || entries.every(e => !e.studentId)}
                onClick={handleSave}>
                {saveAttendance.isPending ? 'Saving...' : 'Save Attendance'}
              </button>
            </div>

            {saveAttendance.isSuccess && (
              <p className="text-sm text-green-600 mt-2 text-right">Attendance saved successfully!</p>
            )}
          </div>
        </>
      )}
    </div>
  )
}
