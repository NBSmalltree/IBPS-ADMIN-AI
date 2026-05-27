import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layout/MainLayout.vue'

const routes = [
  {
    path: '/',
    redirect: '/ibps/sysManage/signStatus'
  },
  {
    path: '/ibps',
    component: MainLayout,
    redirect: '/ibps/sysManage/signStatus',
    children: [
      {
        path: 'sysManage/signStatus',
        name: 'SignStatus',
        component: () => import('../views/ibps/sysManage/SignStatus.vue'),
        meta: { title: '登录退出申请' }
      },
      {
        path: 'sysManage/freeMessage',
        name: 'FreeMessage',
        component: () => import('../views/ibps/sysManage/FreeMessage.vue'),
        meta: { title: '自由格式报文' }
      },
      {
        path: 'sysManage/dataImport',
        name: 'DataImport',
        component: () => import('../views/ibps/sysManage/DataImport.vue'),
        meta: { title: '基础数据导入' }
      },
      {
        path: 'sysManage/certMaintain',
        name: 'CertMaintain',
        component: () => import('../views/ibps/sysManage/CertMaintain.vue'),
        meta: { title: '证书维护' }
      },
      {
        path: 'config/contacts',
        name: 'ContactGrid',
        component: () => import('../views/ibps/config/ContactGrid.vue'),
        meta: { title: '联系人维护' }
      },
      {
        path: 'trace/dashboard',
        name: 'Dashboard',
        component: () => import('../views/ibps/trace/Dashboard.vue'),
        meta: { title: '监控大盘' }
      },
      {
        path: 'trace/graph/:busiSerial?',
        name: 'TraceGraph',
        component: () => import('../views/ibps/trace/TraceGraph.vue'),
        meta: { title: '轨迹图谱' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
