import { useAuth } from '../contexts/AuthContext'
import { useQuery } from '@tanstack/react-query'
import { studentApi, financialApi } from '../services/api'
import { Users, BookOpen, DollarSign, Bell } from 'lucide-react'

function StatCard({ title, value, icon: Icon, color }: {
  title: string; value: string | number; icon: React.ElementType; color: string
}) {
  return (
    <div className="card flex items-center gap-4">
      <div className={`p-3 rounded-xl ${color}`}>
        <Icon className="w-6 h-6 text-white" />
      </div>
      <div>
        <p className="text-sm text-gray-500">{title}</p>
        <p className="text-2xl font-bold text-gray-800">{value}</p>
      </div>
    </div>
  )
}

export default function DashboardPage() {
  const { user } = useAuth()

  const { data: studentCount } = useQuery({
    queryKey: ['studentCount', user?.schoolId],
    queryFn: () => user?.schoolId ? studentApi.count(user.schoolId).then(r => r.data.activeStudents) : null,
    enabled: !!user?.schoolId,
  })

  const { data: payments } = useQuery({
    queryKey: ['schoolPayments', user?.schoolId],
    queryFn: () => user?.schoolId ? financialApi.getSchoolPayments(user.schoolId).then(r => r.data) : null,
    enabled: !!user?.schoolId,
  })

  const totalRevenue = payments?.content?.reduce(
    (sum: number, p: { status: string; amountPaid: number }) =>
      p.status === 'SUCCESS' ? sum + (p.amountPaid || 0) : sum, 0
  ) ?? 0

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">
          Welcome, {user?.firstName}!
        </h1>
        <p className="text-gray-500 text-sm mt-1">
          {new Date().toLocaleDateString('en-NG', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
        </p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Active Students" value={studentCount ?? '—'} icon={Users} color="bg-blue-500" />
        <StatCard title="Total Revenue (₦)" value={`₦${totalRevenue.toLocaleString()}`} icon={DollarSign} color="bg-green-500" />
        <StatCard title="Academic Results" value="—" icon={BookOpen} color="bg-purple-500" />
        <StatCard title="Notifications" value="—" icon={Bell} color="bg-orange-500" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <h2 className="text-lg font-semibold text-gray-800 mb-4">Recent Payments</h2>
          {payments?.content?.length ? (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-left text-gray-500 border-b">
                    <th className="pb-2">Reference</th>
                    <th className="pb-2">Amount</th>
                    <th className="pb-2">Status</th>
                    <th className="pb-2">Date</th>
                  </tr>
                </thead>
                <tbody>
                  {payments.content.slice(0, 5).map((p: {
                    id: number; referenceCode: string;
                    amount: number; status: string; paymentDate: string
                  }) => (
                    <tr key={p.id} className="border-b last:border-0 hover:bg-gray-50">
                      <td className="py-2 font-mono text-xs">{p.referenceCode}</td>
                      <td className="py-2">₦{p.amount?.toLocaleString()}</td>
                      <td className="py-2">
                        <span className={p.status === 'SUCCESS' ? 'badge-green' : 'badge-yellow'}>
                          {p.status}
                        </span>
                      </td>
                      <td className="py-2 text-gray-500">{p.paymentDate ?? '—'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <p className="text-gray-400 text-sm">No payments yet</p>
          )}
        </div>

        <div className="card">
          <h2 className="text-lg font-semibold text-gray-800 mb-4">Quick Actions</h2>
          <div className="space-y-2">
            {[
              { label: 'Enroll New Student', path: '/students/new', color: 'bg-blue-50 text-blue-700 hover:bg-blue-100' },
              { label: 'Record Result', path: '/results/new', color: 'bg-purple-50 text-purple-700 hover:bg-purple-100' },
              { label: 'Record Payment', path: '/payments/new', color: 'bg-green-50 text-green-700 hover:bg-green-100' },
              { label: 'Send Notification', path: '/notifications', color: 'bg-orange-50 text-orange-700 hover:bg-orange-100' },
            ].map(action => (
              <a key={action.path} href={action.path}
                className={`block px-4 py-3 rounded-lg text-sm font-medium transition-colors ${action.color}`}>
                {action.label} →
              </a>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
