import { createApp } from 'vue'
import * as ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import { installHttp } from './scripts/api/ibps'

const app = createApp(App)

app.use(router)

// 注册 Element Plus（样式、指令等）
app.use(ElementPlus, { locale: zhCn })

// 注册 Element Plus 图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 将 Element Plus 组件以 h-* 前缀全局注册，模拟行内环境的组件命名
// 例如 <h-table> -> ElTable, <h-button> -> ElButton
for (const [name, comp] of Object.entries(ElementPlus)) {
  if (name.startsWith('El') && typeof comp === 'object' && comp.name) {
    // ElSubMenu -> h-sub-menu, ElTableColumn -> h-table-column, ElButton -> h-button
    const hName = 'h-' + name
      .replace(/^El/, '')
      .replace(/([A-Z])/g, '-$1')
      .toLowerCase()
      .replace(/^-/, '')
    app.component(hName, comp)
  }
}

// 安装 $http（模拟行内的 this.$http.post/get 调用方式）
installHttp(app)

// 挂载行内环境的全局方法
app.config.globalProperties.$hMessage = ElementPlus.ElMessage
app.config.globalProperties.$hConfirm = ElementPlus.ElMessageBox.confirm
app.config.globalProperties.$hAlert = ElementPlus.ElMessageBox.alert

app.mount('#app')
