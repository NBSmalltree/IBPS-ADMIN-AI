import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/ibps-admin',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

request.interceptors.request.use(
  config => {
    // Add token header if available
    const token = localStorage.getItem('ibps_token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code && res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    const message = error.response?.data?.message || error.message || '网络错误'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// Connection Management
export const getConnectionStatus = () => request.get('/api/ibps/connection/status')
export const signIn = (data) => request.post('/api/ibps/connection/sign-in', data)
export const signOut = (data) => request.post('/api/ibps/connection/sign-out', data)

// Free Message
export const queryMessages = (params) => request.get('/api/ibps/message/list', { params })
export const sendMessage = (data) => request.post('/api/ibps/message/send', data)

// Data Import
export const uploadFile = (formData) => request.post('/api/ibps/import/upload', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})
export const getImportRecords = (params) => request.get('/api/ibps/import/records', { params })

// Certificate
export const applyCertChange = (data) => request.post('/api/ibps/cert/change', data)
export const getCertHistory = (params) => request.get('/api/ibps/cert/history', { params })

// Contact
export const getContactList = (params) => request.get('/api/ibps/contact/list', { params })
export const getContact = (id) => request.get(`/api/ibps/contact/${id}`)
export const createContact = (data) => request.post('/api/ibps/contact', data)
export const updateContact = (id, data) => request.put(`/api/ibps/contact/${id}`, data)
export const deleteContact = (id) => request.delete(`/api/ibps/contact/${id}`)

// Trace
export const getTraceTimeline = (busiSerial) => request.get(`/api/ibps/trace/timeline/${busiSerial}`)
export const getDashboard = (params) => request.get('/api/ibps/trace/dashboard', { params })
export const getDrillDownList = (params) => request.get('/api/ibps/trace/drill-down', { params })

// WebSocket connection for import progress
export const connectImportProgress = (importId, onMessage) => {
  const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:'
  const ws = new WebSocket(`${protocol}//${location.host}/ws/import/${importId}`)
  ws.onmessage = (e) => {
    try {
      onMessage(JSON.parse(e.data))
    } catch (err) {
      console.error('WebSocket message parse error:', err)
    }
  }
  ws.onerror = (e) => console.error('WebSocket error:', e)
  return ws
}

export default request
