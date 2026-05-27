<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="layout-aside">
      <div class="logo">
        <h2>IBPS 管理台</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-sub-menu index="sysManage">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/ibps/sysManage/signStatus">
            <el-icon><Connection /></el-icon>
            <span>登录退出申请</span>
          </el-menu-item>
          <el-menu-item index="/ibps/sysManage/freeMessage">
            <el-icon><Document /></el-icon>
            <span>自由格式报文</span>
          </el-menu-item>
          <el-menu-item index="/ibps/sysManage/dataImport">
            <el-icon><Upload /></el-icon>
            <span>基础数据导入</span>
          </el-menu-item>
          <el-menu-item index="/ibps/sysManage/certMaintain">
            <el-icon><Key /></el-icon>
            <span>证书维护</span>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="config">
          <template #title>
            <el-icon><User /></el-icon>
            <span>个性化配置</span>
          </template>
          <el-menu-item index="/ibps/config/contacts">
            <el-icon><Phone /></el-icon>
            <span>联系人维护</span>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="trace">
          <template #title>
            <el-icon><DataLine /></el-icon>
            <span>交易轨迹</span>
          </template>
          <el-menu-item index="/ibps/trace/dashboard">
            <el-icon><Odometer /></el-icon>
            <span>监控大盘</span>
          </el-menu-item>
          <el-menu-item index="/ibps/trace/graph">
            <el-icon><Share /></el-icon>
            <span>轨迹图谱</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <span class="page-title">{{ currentTitle }}</span>
      </el-header>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || 'IBPS 管理控制台')
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
