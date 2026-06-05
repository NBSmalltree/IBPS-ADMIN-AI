import axios from 'axios'

const request = axios.create({
  baseURL: '/ibps-admin',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

request.interceptors.request.use(
  config => {
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
    return response
  },
  error => {
    return Promise.reject(error)
  }
)

/**
 * 将 $http 安装到 Vue 应用实例上（开发环境用）
 * 行内环境已内置 $http，无需调用此函数
 */
export function installHttp(app) {
  app.config.globalProperties.$http = request
}

/**
 * WebSocket 工具函数 - 导入进度实时推送
 * 浏览器原生 API，兼容所有环境
 */
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
