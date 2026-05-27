<template>
  <div class="sign-status">
    <el-card class="status-card" :class="statusClass">
      <div class="status-content">
        <div class="status-indicator" :class="statusClass">
          <el-icon :size="48">
            <SuccessFilled v-if="status === 'SIGNED_IN'" />
            <CircleCloseFilled v-else-if="status === 'ABNORMAL'" />
            <InfoFilled v-else />
          </el-icon>
        </div>
        <div class="status-info">
          <h2>{{ statusText }}</h2>
          <p v-if="lastSignTime">最后操作时间: {{ lastSignTime }}</p>
        </div>
      </div>
    </el-card>

    <div class="action-buttons">
      <el-button
        type="primary"
        size="large"
        :disabled="status === 'SIGNED_IN'"
        @click="handleSignIn"
      >
        <el-icon><Connection /></el-icon>
        系统签到
      </el-button>
      <el-button
        type="danger"
        size="large"
        :disabled="status !== 'SIGNED_IN'"
        @click="handleSignOut"
      >
        <el-icon><SwitchButton /></el-icon>
        系统签退
      </el-button>
      <el-button
        type="info"
        size="large"
        @click="refreshStatus"
      >
        <el-icon><Refresh /></el-icon>
        查询当前状态
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getConnectionStatus, signIn, signOut } from '@/api/ibps.js'

const status = ref('SIGNED_OUT')
const lastSignTime = ref('')

const statusText = computed(() => {
  const map = {
    SIGNED_IN: '已签到',
    SIGNED_OUT: '已签退',
    ABNORMAL: '异常'
  }
  return map[status.value] || '未知'
})

const statusClass = computed(() => {
  const map = {
    SIGNED_IN: 'status-success',
    SIGNED_OUT: 'status-gray',
    ABNORMAL: 'status-danger'
  }
  return map[status.value] || 'status-gray'
})

const refreshStatus = async () => {
  try {
    const res = await getConnectionStatus()
    status.value = res.data?.status || 'SIGNED_OUT'
    lastSignTime.value = res.data?.lastSignTime || ''
  } catch (e) {
    console.error('Failed to get status:', e)
  }
}

const handleSignIn = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要执行系统签到操作吗？',
      '二次确认',
      { type: 'warning', confirmButtonText: '确认签到', cancelButtonText: '取消' }
    )
    await signIn({ operator: 'admin', confirmed: true })
    ElMessage.success('签到成功')
    refreshStatus()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('签到失败: ' + (e.message || '未知错误'))
    }
  }
}

const handleSignOut = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要执行系统签退操作吗？',
      '二次确认',
      { type: 'warning', confirmButtonText: '确认签退', cancelButtonText: '取消' }
    )
    await signOut({ operator: 'admin', confirmed: true })
    ElMessage.success('签退成功')
    refreshStatus()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('签退失败: ' + (e.message || '未知错误'))
    }
  }
}

onMounted(() => {
  refreshStatus()
})
</script>

<style scoped>
.sign-status {
  padding: 20px;
}

.status-card {
  max-width: 600px;
  margin: 0 auto 30px;
  border-width: 3px;
}

.status-card.status-success {
  border-color: #67c23a;
}

.status-card.status-gray {
  border-color: #909399;
}

.status-card.status-danger {
  border-color: #f56c6c;
}

.status-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.status-indicator.status-success {
  color: #67c23a;
}

.status-indicator.status-gray {
  color: #909399;
}

.status-indicator.status-danger {
  color: #f56c6c;
}

.status-info h2 {
  font-size: 24px;
  margin-bottom: 8px;
}

.status-info p {
  color: #909399;
  font-size: 14px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
}
</style>
