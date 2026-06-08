import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useAuth } from '../contexts/AuthContext'
import { financialApi } from '../services/api'
import { DollarSign, Plus, X } from 'lucide-react'
import { useState } from 'react'

interface FeeStructure {
  id: number; name: string; amount: number; feeType: string;
  term: string; sessionId: number; isActive: boolean
}
interface Payment {
  id: number; referenceCode: string; payerName: string;
  amount: number; amountPaid: number; status: string;
  paymentMethod: string; paymentDate: string; narration: string
}

export default function PaymentsPage() {
  const { user } = useAuth()
  const qc = useQueryClient()
  const [showFeeModal, setShowFeeModal] = useState(false)
  const [showPayModal, setShowPayModal] = useState(false)
  const [feeForm, setFeeForm] = useState({ name: '', amount: '', feeType: 'TUITION', term: 'FIRST_TERM', sessionId: '' })
  const [payForm, setPayForm] = useState({ studentId: '', feeStructureId: '', amount: '', payerName: '', narration: '' })

  const { data: feesData } = useQuery({
    queryKey: ['fees', user?.schoolId],
    queryFn: () => user?.schoolId ? financialApi.getFees(user.schoolId).then((r: { data: FeeStructure[] }) => r.data) : null,
    enabled: !!user?.schoolId,
  })
  const fees: FeeStructure[] = feesData ?? []

  const { data: payments } = useQuery({
    queryKey: ['payments', user?.schoolId],
    queryFn: () => user?.schoolId ? financialApi.getSchoolPayments(user.schoolId).then((r: { data: unknown }) => r.data) : null,
    enabled: !!user?.schoolId,
  })

  const createFee = useMutation({
    mutationFn: (data: object) => financialApi.createFee(data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['fees'] }); setShowFeeModal(false) },
  })

  const recordPayment = useMutation({
    mutationFn: (data: object) => financialApi.recordCash(data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['payments'] }); setShowPayModal(false) },
  })

  const paymentList: Payment[] = payments?.content ?? []
  const totalCollected = paymentList
    .filter(p => p.status === 'SUCCESS')
    .reduce((s, p) => s + (p.amountPaid || 0), 0)
  const pending = paymentList.filter(p => p.status === 'PENDING').length

  return (
    <div className="p-6 space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
            <DollarSign className="w-6 h-6" /> Fee Management
          </h1>
          <p className="text-sm text-gray-500 mt-1">Manage school fees and record payments</p>
        </div>
        <div className="flex gap-2">
          <button onClick={() => setShowFeeModal(true)} className="btn-secondary flex items-center gap-2">
            <Plus className="w-4 h-4" /> Add Fee
          </button>
          <button onClick={() => setShowPayModal(true)} className="btn-primary flex items-center gap-2">
            <Plus className="w-4 h-4" /> Record Payment
          </button>
        </div>
      </div>

      {/* Summary */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="card bg-green-50 border-green-100">
          <p className="text-xs text-green-700 font-medium">Total Collected</p>
          <p className="text-2xl font-bold text-green-800 mt-1">₦{totalCollected.toLocaleString()}</p>
        </div>
        <div className="card bg-yellow-50 border-yellow-100">
          <p className="text-xs text-yellow-700 font-medium">Pending Payments</p>
          <p className="text-2xl font-bold text-yellow-800 mt-1">{pending}</p>
        </div>
        <div className="card bg-blue-50 border-blue-100">
          <p className="text-xs text-blue-700 font-medium">Active Fee Types</p>
          <p className="text-2xl font-bold text-blue-800 mt-1">{fees.filter(f => f.isActive).length}</p>
        </div>
      </div>

      {/* Fee Structures */}
      <div className="card">
        <h2 className="font-semibold text-gray-800 mb-4">Fee Structures</h2>
        {fees.length === 0 ? (
          <p className="text-gray-400 text-sm">No fees configured yet</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-gray-500 border-b">
                  <th className="pb-2 pr-4">Name</th>
                  <th className="pb-2 pr-4">Amount</th>
                  <th className="pb-2 pr-4">Type</th>
                  <th className="pb-2 pr-4">Term</th>
                  <th className="pb-2">Status</th>
                </tr>
              </thead>
              <tbody>
                {fees.map(f => (
                  <tr key={f.id} className="border-b last:border-0 hover:bg-gray-50">
                    <td className="py-2 pr-4 font-medium">{f.name}</td>
                    <td className="py-2 pr-4">₦{Number(f.amount).toLocaleString()}</td>
                    <td className="py-2 pr-4 text-gray-500">{f.feeType}</td>
                    <td className="py-2 pr-4">{f.term?.replace('_', ' ')}</td>
                    <td className="py-2">
                      <span className={f.isActive ? 'badge-green' : 'badge-red'}>
                        {f.isActive ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Payment History */}
      <div className="card">
        <h2 className="font-semibold text-gray-800 mb-4">Payment History</h2>
        {paymentList.length === 0 ? (
          <p className="text-gray-400 text-sm">No payments recorded yet</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-gray-500 border-b">
                  <th className="pb-2 pr-4">Reference</th>
                  <th className="pb-2 pr-4">Payer</th>
                  <th className="pb-2 pr-4">Amount</th>
                  <th className="pb-2 pr-4">Method</th>
                  <th className="pb-2 pr-4">Status</th>
                  <th className="pb-2">Date</th>
                </tr>
              </thead>
              <tbody>
                {paymentList.map(p => (
                  <tr key={p.id} className="border-b last:border-0 hover:bg-gray-50">
                    <td className="py-2 pr-4 font-mono text-xs">{p.referenceCode}</td>
                    <td className="py-2 pr-4">{p.payerName}</td>
                    <td className="py-2 pr-4">₦{(p.amountPaid ?? p.amount)?.toLocaleString()}</td>
                    <td className="py-2 pr-4 text-gray-500">{p.paymentMethod}</td>
                    <td className="py-2 pr-4">
                      <span className={p.status === 'SUCCESS' ? 'badge-green' : 'badge-yellow'}>{p.status}</span>
                    </td>
                    <td className="py-2 text-gray-500">{p.paymentDate ?? '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Add Fee Modal */}
      {showFeeModal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl p-6 w-full max-w-md shadow-xl">
            <div className="flex justify-between items-center mb-4">
              <h3 className="font-semibold text-gray-800">Add Fee Structure</h3>
              <button onClick={() => setShowFeeModal(false)}><X className="w-5 h-5 text-gray-400" /></button>
            </div>
            <div className="space-y-3">
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Fee Name</label>
                <input className="input" placeholder="e.g. First Term Tuition" value={feeForm.name}
                  onChange={e => setFeeForm(p => ({ ...p, name: e.target.value }))} />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">Amount (₦)</label>
                  <input className="input" type="number" placeholder="50000" value={feeForm.amount}
                    onChange={e => setFeeForm(p => ({ ...p, amount: e.target.value }))} />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">Fee Type</label>
                  <select className="input" value={feeForm.feeType}
                    onChange={e => setFeeForm(p => ({ ...p, feeType: e.target.value }))}>
                    <option value="TUITION">Tuition</option>
                    <option value="DEVELOPMENT">Development</option>
                    <option value="EXAMINATION">Examination</option>
                    <option value="UNIFORM">Uniform</option>
                    <option value="SPORTS">Sports</option>
                    <option value="OTHER">Other</option>
                  </select>
                </div>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">Term</label>
                  <select className="input" value={feeForm.term}
                    onChange={e => setFeeForm(p => ({ ...p, term: e.target.value }))}>
                    <option value="FIRST_TERM">First Term</option>
                    <option value="SECOND_TERM">Second Term</option>
                    <option value="THIRD_TERM">Third Term</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">Session ID</label>
                  <input className="input" type="number" placeholder="Session ID" value={feeForm.sessionId}
                    onChange={e => setFeeForm(p => ({ ...p, sessionId: e.target.value }))} />
                </div>
              </div>
            </div>
            <div className="flex gap-2 mt-5">
              <button className="btn-secondary flex-1" onClick={() => setShowFeeModal(false)}>Cancel</button>
              <button className="btn-primary flex-1"
                disabled={createFee.isPending}
                onClick={() => createFee.mutate({
                  schoolId: user?.schoolId, name: feeForm.name,
                  amount: feeForm.amount, feeType: feeForm.feeType,
                  term: feeForm.term, sessionId: feeForm.sessionId,
                })}>
                {createFee.isPending ? 'Saving...' : 'Save Fee'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Record Payment Modal */}
      {showPayModal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl p-6 w-full max-w-md shadow-xl">
            <div className="flex justify-between items-center mb-4">
              <h3 className="font-semibold text-gray-800">Record Cash Payment</h3>
              <button onClick={() => setShowPayModal(false)}><X className="w-5 h-5 text-gray-400" /></button>
            </div>
            <div className="space-y-3">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">Student ID</label>
                  <input className="input" type="number" placeholder="Student ID" value={payForm.studentId}
                    onChange={e => setPayForm(p => ({ ...p, studentId: e.target.value }))} />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">Fee Structure</label>
                  <select className="input" value={payForm.feeStructureId}
                    onChange={e => setPayForm(p => ({ ...p, feeStructureId: e.target.value }))}>
                    <option value="">Select fee...</option>
                    {fees.filter(f => f.isActive).map(f => (
                      <option key={f.id} value={f.id}>{f.name} — ₦{Number(f.amount).toLocaleString()}</option>
                    ))}
                  </select>
                </div>
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Amount (₦)</label>
                <input className="input" type="number" placeholder="Amount paid" value={payForm.amount}
                  onChange={e => setPayForm(p => ({ ...p, amount: e.target.value }))} />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Payer Name</label>
                <input className="input" placeholder="Parent/Guardian name" value={payForm.payerName}
                  onChange={e => setPayForm(p => ({ ...p, payerName: e.target.value }))} />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Narration</label>
                <input className="input" placeholder="e.g. First Term school fees" value={payForm.narration}
                  onChange={e => setPayForm(p => ({ ...p, narration: e.target.value }))} />
              </div>
            </div>
            <div className="flex gap-2 mt-5">
              <button className="btn-secondary flex-1" onClick={() => setShowPayModal(false)}>Cancel</button>
              <button className="btn-primary flex-1"
                disabled={recordPayment.isPending}
                onClick={() => recordPayment.mutate({
                  schoolId: String(user?.schoolId), studentId: payForm.studentId,
                  feeStructureId: payForm.feeStructureId || null,
                  amount: payForm.amount, payerName: payForm.payerName,
                  narration: payForm.narration || 'Cash payment',
                })}>
                {recordPayment.isPending ? 'Recording...' : 'Record Payment'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
