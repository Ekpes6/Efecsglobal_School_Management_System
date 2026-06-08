import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useAuth } from '../contexts/AuthContext'
import { notificationApi } from '../services/api'
import { Bell, Mail, MessageSquare, Send, CheckCircle } from 'lucide-react'
import { useState } from 'react'

interface NotificationLog {
  id: number; type: string; notificationType: string;
  recipientEmail: string; recipientPhone: string;
  subject: string; message: string; status: string; sentAt: string
}

const NOTIF_TYPES = [
  { value: 'GENERAL', label: 'General Announcement' },
  { value: 'FEE_REMINDER', label: 'Fee Reminder' },
  { value: 'RESULT_PUBLISHED', label: 'Result Published' },
  { value: 'EVENT', label: 'School Event' },
]

export default function AnnouncementsPage() {
  const { user } = useAuth()
  const qc = useQueryClient()
  const [channel, setChannel] = useState<'email' | 'sms'>('email')
  const [form, setForm] = useState({
    email: '', phone: '', subject: '', message: '', type: 'GENERAL',
  })
  const [sent, setSent] = useState(false)

  const { data: logs } = useQuery({
    queryKey: ['notifications', user?.schoolId],
    queryFn: () => user?.schoolId ? notificationApi.getSchoolNotifications(user.schoolId).then(r => r.data) : null,
    enabled: !!user?.schoolId,
  })

  const sendEmail = useMutation({
    mutationFn: (data: object) => notificationApi.sendEmail(data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['notifications'] })
      setSent(true)
      setTimeout(() => setSent(false), 3000)
      setForm(f => ({ ...f, email: '', subject: '', message: '' }))
    },
  })

  const sendSms = useMutation({
    mutationFn: (data: object) => notificationApi.sendSms(data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['notifications'] })
      setSent(true)
      setTimeout(() => setSent(false), 3000)
      setForm(f => ({ ...f, phone: '', message: '' }))
    },
  })

  const logList: NotificationLog[] = logs?.content ?? []
  const isPending = sendEmail.isPending || sendSms.isPending

  const handleSend = () => {
    if (channel === 'email') {
      sendEmail.mutate({
        schoolId: String(user?.schoolId), email: form.email,
        subject: form.subject, message: form.message, type: form.type,
      })
    } else {
      sendSms.mutate({
        schoolId: String(user?.schoolId), phone: form.phone,
        message: form.message, type: form.type,
      })
    }
  }

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
          <Bell className="w-6 h-6" /> Announcements & Notifications
        </h1>
        <p className="text-sm text-gray-500 mt-1">Send messages to parents and guardians</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-5 gap-6">
        {/* Compose */}
        <div className="lg:col-span-2 card space-y-4">
          <h2 className="font-semibold text-gray-800">Compose Message</h2>

          {/* Channel toggle */}
          <div className="flex rounded-lg border border-gray-200 overflow-hidden">
            <button
              className={`flex-1 py-2 text-sm font-medium flex items-center justify-center gap-2 transition-colors
                ${channel === 'email' ? 'bg-blue-600 text-white' : 'text-gray-600 hover:bg-gray-50'}`}
              onClick={() => setChannel('email')}>
              <Mail className="w-4 h-4" /> Email
            </button>
            <button
              className={`flex-1 py-2 text-sm font-medium flex items-center justify-center gap-2 transition-colors
                ${channel === 'sms' ? 'bg-blue-600 text-white' : 'text-gray-600 hover:bg-gray-50'}`}
              onClick={() => setChannel('sms')}>
              <MessageSquare className="w-4 h-4" /> SMS
            </button>
          </div>

          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Notification Type</label>
            <select className="input" value={form.type}
              onChange={e => setForm(p => ({ ...p, type: e.target.value }))}>
              {NOTIF_TYPES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
            </select>
          </div>

          {channel === 'email' ? (
            <>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Recipient Email</label>
                <input className="input" type="email" placeholder="parent@example.com"
                  value={form.email} onChange={e => setForm(p => ({ ...p, email: e.target.value }))} />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Subject</label>
                <input className="input" placeholder="e.g. Term examination notice"
                  value={form.subject} onChange={e => setForm(p => ({ ...p, subject: e.target.value }))} />
              </div>
            </>
          ) : (
            <div>
              <label className="block text-xs font-medium text-gray-600 mb-1">Phone Number</label>
              <input className="input" type="tel" placeholder="+2348012345678"
                value={form.phone} onChange={e => setForm(p => ({ ...p, phone: e.target.value }))} />
            </div>
          )}

          <div>
            <label className="block text-xs font-medium text-gray-600 mb-1">Message</label>
            <textarea className="input min-h-[100px] resize-none" placeholder="Type your message here..."
              value={form.message} onChange={e => setForm(p => ({ ...p, message: e.target.value }))} />
          </div>

          {sent && (
            <div className="flex items-center gap-2 text-sm text-green-700 bg-green-50 rounded-lg px-3 py-2">
              <CheckCircle className="w-4 h-4" /> Message queued for delivery
            </div>
          )}

          <button className="btn-primary w-full flex items-center justify-center gap-2"
            disabled={isPending} onClick={handleSend}>
            <Send className="w-4 h-4" />
            {isPending ? 'Sending...' : `Send ${channel === 'email' ? 'Email' : 'SMS'}`}
          </button>
        </div>

        {/* Notification Log */}
        <div className="lg:col-span-3 card">
          <h2 className="font-semibold text-gray-800 mb-4">Notification History</h2>
          {logList.length === 0 ? (
            <div className="text-center py-12 text-gray-400">
              <Bell className="w-10 h-10 mx-auto mb-3 opacity-40" />
              <p>No notifications sent yet</p>
            </div>
          ) : (
            <div className="space-y-3 max-h-[520px] overflow-y-auto pr-1">
              {logList.map(log => (
                <div key={log.id} className="border border-gray-100 rounded-lg p-3 hover:bg-gray-50">
                  <div className="flex items-center justify-between mb-1">
                    <div className="flex items-center gap-2">
                      {log.type === 'EMAIL' ? (
                        <Mail className="w-3.5 h-3.5 text-blue-500" />
                      ) : (
                        <MessageSquare className="w-3.5 h-3.5 text-purple-500" />
                      )}
                      <span className="text-xs font-medium text-gray-700">
                        {log.subject || log.notificationType?.replace('_', ' ')}
                      </span>
                    </div>
                    <span className={`text-xs px-2 py-0.5 rounded-full font-medium
                      ${log.status === 'SENT' ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'}`}>
                      {log.status}
                    </span>
                  </div>
                  <p className="text-xs text-gray-500 line-clamp-2">{log.message}</p>
                  <p className="text-xs text-gray-400 mt-1">
                    To: {log.recipientEmail || log.recipientPhone} · {log.sentAt ? new Date(log.sentAt).toLocaleDateString() : '—'}
                  </p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
