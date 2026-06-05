<template>
  <div class="sign-status">
    <h-card class="status-card" :class="statusClass">
      <div class="status-content">
        <div class="status-indicator" :class="statusClass">
          <h-icon :size="48">
            <SuccessFilled v-if="status === 'SIGNED_IN'" />
            <CircleCloseFilled v-else-if="status === 'ABNORMAL'" />
            <InfoFilled v-else />
          </h-icon>
        </div>
        <div class="status-info">
          <h2>{{ statusText }}</h2>
          <p v-if="lastSignTime">最后操作时间: {{ lastSignTime }}</p>
        </div>
      </div>
    </h-card>

    <div class="action-buttons">
      <h-button
        type="primary"
        size="large"
        :disabled="status === 'SIGNED_IN'"
        @click="handleSignIn"
      >
        <h-icon><Connection /></h-icon>
        系统签到
      </h-button>
      <h-button
        type="danger"
        size="large"
        :disabled="status !== 'SIGNED_IN'"
        @click="handleSignOut"
      >
        <h-icon><SwitchButton /></h-icon>
        系统签退
      </h-button>
      <h-button
        type="info"
        size="large"
        @click="refreshStatus"
      >
        <h-icon><Refresh /></h-icon>
        查询当前状态
      </h-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SignStatus',
  data() {
    return {
      status: 'SIGNED_OUT',
      lastSignTime: ''
    }
  },
  computed: {
    statusText() {
      const map = {
        SIGNED_IN: '已签到',
        SIGNED_OUT: '已签退',
        ABNORMAL: '异常'
      }
      return map[this.status] || '未知'
    },
    statusClass() {
      const map = {
        SIGNED_IN: 'status-success',
        SIGNED_OUT: 'status-gray',
        ABNORMAL: 'status-danger'
      }
      return map[this.status] || 'status-gray'
    }
  },
  methods: {
    async refreshStatus() {
      try {
        const res = await this.$http.get('/api/ibps/connection/status')
        this.status = res.data?.status || 'SIGNED_OUT'
        this.lastSignTime = res.data?.lastSignTime || ''
      } catch (e) {
        console.error('Failed to get status:', e)
      }
    },
    async handleSignIn() {
      try {
        await this.$hConfirm(
          '确定要执行系统签到操作吗？',
          '二次确认',
          { type: 'warning', confirmButtonText: '确认签到', cancelButtonText: '取消' }
        )
        await this.$http.post('/api/ibps/connection/sign-in', { operator: 'admin', confirmed: true })
        this.$hMessage.success('签到成功')
        this.refreshStatus()
      } catch (e) {
        if (e !== 'cancel') {
          this.$hMessage.error('签到失败: ' + (e.message || '未知错误'))
        }
      }
    },
    async handleSignOut() {
      try {
        await this.$hConfirm(
          '确定要执行系统签退操作吗？',
          '二次确认',
          { type: 'warning', confirmButtonText: '确认签退', cancelButtonText: '取消' }
        )
        await this.$http.post('/api/ibps/connection/sign-out', { operator: 'admin', confirmed: true })
        this.$hMessage.success('签退成功')
        this.refreshStatus()
      } catch (e) {
        if (e !== 'cancel') {
          this.$hMessage.error('签退失败: ' + (e.message || '未知错误'))
        }
      }
    }
  },
  mounted() {
    this.refreshStatus()
  }
}
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
