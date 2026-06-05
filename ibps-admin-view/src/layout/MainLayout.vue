<template>
  <h-container class="layout-container">
    <h-aside width="220px" class="layout-aside">
      <div class="logo">
        <h2>IBPS 管理台</h2>
      </div>
      <h-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <h-sub-menu index="sysManage">
          <template #title>
            <h-icon><Setting /></h-icon>
            <span>系统管理</span>
          </template>
          <h-menu-item index="/ibps/sysManage/signStatus">
            <h-icon><Connection /></h-icon>
            <span>登录退出申请</span>
          </h-menu-item>
          <h-menu-item index="/ibps/sysManage/freeMessage">
            <h-icon><Document /></h-icon>
            <span>自由格式报文</span>
          </h-menu-item>
          <h-menu-item index="/ibps/sysManage/dataImport">
            <h-icon><Upload /></h-icon>
            <span>基础数据导入</span>
          </h-menu-item>
          <h-menu-item index="/ibps/sysManage/certMaintain">
            <h-icon><Key /></h-icon>
            <span>证书维护</span>
          </h-menu-item>
        </h-sub-menu>

        <h-sub-menu index="config">
          <template #title>
            <h-icon><User /></h-icon>
            <span>个性化配置</span>
          </template>
          <h-menu-item index="/ibps/config/contacts">
            <h-icon><Phone /></h-icon>
            <span>联系人维护</span>
          </h-menu-item>
        </h-sub-menu>

        <h-sub-menu index="trace">
          <template #title>
            <h-icon><DataLine /></h-icon>
            <span>交易轨迹</span>
          </template>
          <h-menu-item index="/ibps/trace/dashboard">
            <h-icon><Odometer /></h-icon>
            <span>监控大盘</span>
          </h-menu-item>
          <h-menu-item index="/ibps/trace/graph">
            <h-icon><Share /></h-icon>
            <span>轨迹图谱</span>
          </h-menu-item>
        </h-sub-menu>
      </h-menu>
    </h-aside>

    <h-container>
      <h-header class="layout-header">
        <span class="page-title">{{ currentTitle }}</span>
      </h-header>
      <h-main class="layout-main">
        <router-view />
      </h-main>
    </h-container>
  </h-container>
</template>

<script>
export default {
  name: 'MainLayout',
  computed: {
    activeMenu() {
      return this.$route.path
    },
    currentTitle() {
      return this.$route.meta.title || 'IBPS 管理控制台'
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout-aside {
  background-color: #304156;
  overflow-y: auto;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.logo h2 {
  font-size: 16px;
  font-weight: 600;
}

.layout-header {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #e6e6e6;
  background: #fff;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.layout-main {
  background-color: #f5f7fa;
  overflow-y: auto;
}
</style>
