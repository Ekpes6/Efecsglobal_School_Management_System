import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { schoolApi } from '../services/api'
import toast from 'react-hot-toast'

const NIGERIAN_STATES = [
  'Abia','Adamawa','Akwa Ibom','Anambra','Bauchi','Bayelsa','Benue','Borno',
  'Cross River','Delta','Ebonyi','Edo','Ekiti','Enugu','FCT','Gombe','Imo',
  'Jigawa','Kaduna','Kano','Katsina','Kebbi','Kogi','Kwara','Lagos',
  'Nasarawa','Niger','Ogun','Ondo','Osun','Oyo','Plateau','Rivers',
  'Sokoto','Taraba','Yobe','Zamfara'
]

export default function RegisterSchoolPage() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [form, setForm] = useState({
    name: '', registrationNumber: '', rcNumber: '', address: '',
    city: '', state: '', lga: '', phoneNumber: '', email: '',
    levels: [] as string[], subscriptionPlan: 'FREE',
    adminFirstName: '', adminLastName: '', adminEmail: '', adminPhone: '', adminPassword: ''
  })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleLevels = (level: string) => {
    setForm(prev => ({
      ...prev,
      levels: prev.levels.includes(level)
        ? prev.levels.filter(l => l !== level)
        : [...prev.levels, level]
    }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (form.levels.length === 0) { toast.error('Select at least one academic level'); return }
    setLoading(true)
    try {
      await schoolApi.register({
        name: form.name,
        registrationNumber: form.registrationNumber,
        rcNumber: form.rcNumber,
        address: form.address,
        city: form.city,
        state: form.state,
        lga: form.lga,
        phoneNumber: form.phoneNumber,
        email: form.email,
        levels: form.levels,
        subscriptionPlan: form.subscriptionPlan,
      })
      toast.success('School registered! Await admin approval.')
      navigate('/login')
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { detail?: string } } })?.response?.data?.detail
      toast.error(msg || 'Registration failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-blue-100 py-10 px-4">
      <div className="max-w-2xl mx-auto bg-white rounded-2xl shadow-lg p-8">
        <div className="text-center mb-8">
          <div className="text-2xl font-bold text-blue-700">EduManage NG</div>
          <h2 className="text-xl font-semibold text-gray-800 mt-2">Register Your School</h2>
          <p className="text-gray-500 text-sm">Join thousands of Nigerian schools on our platform</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">
          <h3 className="font-semibold text-gray-700 border-b pb-2">School Information</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">School Name *</label>
              <input name="name" value={form.name} onChange={handleChange} className="input" required placeholder="e.g. Sunshine Academy" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Registration Number *</label>
              <input name="registrationNumber" value={form.registrationNumber} onChange={handleChange} className="input" required placeholder="MoE registration number" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">RC Number</label>
              <input name="rcNumber" value={form.rcNumber} onChange={handleChange} className="input" placeholder="CAC RC Number (optional)" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Email *</label>
              <input type="email" name="email" value={form.email} onChange={handleChange} className="input" required />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Phone Number *</label>
              <input name="phoneNumber" value={form.phoneNumber} onChange={handleChange} className="input" required placeholder="08012345678" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">State *</label>
              <select name="state" value={form.state} onChange={handleChange} className="input" required>
                <option value="">Select State</option>
                {NIGERIAN_STATES.map(s => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">City *</label>
              <input name="city" value={form.city} onChange={handleChange} className="input" required />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">LGA *</label>
              <input name="lga" value={form.lga} onChange={handleChange} className="input" required placeholder="Local Government Area" />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Address *</label>
            <input name="address" value={form.address} onChange={handleChange} className="input" required placeholder="Full school address" />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Academic Levels *</label>
            <div className="flex flex-wrap gap-3">
              {['CRECHE','NURSERY','PRIMARY','SECONDARY'].map(level => (
                <label key={level} className="flex items-center gap-2 cursor-pointer">
                  <input type="checkbox" checked={form.levels.includes(level)}
                    onChange={() => handleLevels(level)} className="accent-blue-600" />
                  <span className="text-sm text-gray-700">{level}</span>
                </label>
              ))}
            </div>
          </div>

          <button type="submit" disabled={loading} className="btn-primary w-full py-3">
            {loading ? 'Registering...' : 'Register School'}
          </button>
        </form>

        <div className="mt-4 text-center text-sm text-gray-500">
          Already registered?{' '}
          <Link to="/login" className="text-blue-600 hover:underline">Sign in</Link>
        </div>
      </div>
    </div>
  )
}
