<template>
  <div class="trace-graph">
    <h-card>
      <template #header>
        <span>交易轨迹穿透图谱</span>
      </template>

      <h-form :inline="true" class="query-form">
        <h-form-item label="业务流水号">
          <h-input v-model="busiSerial" placeholder="请输入业务流水号" style="width: 300px" />
        </h-form-item>
        <h-form-item>
          <h-button type="primary" :loading="loading" @click="loadTrace">查询</h-button>
        </h-form-item>
      </h-form>

      <div v-if="traceSteps.length > 0" class="timeline">
        <div v-for="(step, index) in traceSteps" :key="index" class="timeline-item">
          <div class="timeline-node" :class="getNodeClass(step)">
            <h-icon :size="20">
              <SuccessFilled v-if="step.status === 'SUCCESS'" />
              <WarningFilled v-else-if="step.status === 'TIMEOUT'" />
              <CircleCloseFilled v-else />
            </h-icon>
          </div>
          <div class="timeline-content">
            <div class="step-name">{{ step.step }}</div>
            <div class="step-meta">
              <span class="trans-code">{{ step.transCode }}</span>
              <span class="event-time">{{ step.eventDatetime }}</span>
            </div>
            <div v-if="step.detail" class="step-detail">{{ step.detail }}</div>
          </div>
          <div
            v-if="index < traceSteps.length - 1"
            class="timeline-connector"
            :class="{ 'slow-link': step.isSlowLink }"
          >
            <span class="delta-label" :class="{ 'slow': step.isSlowLink }">
              {{ step.deltaMs }}ms
            </span>
          </div>
        </div>
      </div>

      <h-empty v-else-if="!loading && searched" description="未找到轨迹数据" />
    </h-card>
  </div>
</template>

<script>
export default {
  name: 'TraceGraph',
  data() {
    return {
      busiSerial: this.$route.params.busiSerial || '',
      traceSteps: [],
      loading: false,
      searched: false
    }
  },
  methods: {
    getNodeClass(step) {
      const map = {
        SUCCESS: 'node-success',
        TIMEOUT: 'node-timeout',
        FAIL: 'node-fail'
      }
      return map[step.status] || 'node-default'
    },
    async loadTrace() {
      if (!this.busiSerial) return
      this.loading = true
      this.searched = true
      try {
        const res = await this.$http.get(`/api/ibps/trace/timeline/${this.busiSerial}`)
        this.traceSteps = res.data || []
        if (this.traceSteps.length === 0) {
          this.$hMessage.warning('未查询到轨迹数据')
        }
      } catch (e) {
        console.error('Load trace failed:', e)
        this.traceSteps = []
      } finally {
        this.loading = false
      }
    }
  },
  mounted() {
    if (this.busiSerial) {
      this.loadTrace()
    }
  }
}
</script>

<style scoped>
.trace-graph {
  padding: 20px;
}

.query-form {
  margin-bottom: 20px;
}

.timeline {
  position: relative;
  padding-left: 40px;
}

.timeline-item {
  position: relative;
  padding-bottom: 8px;
}

.timeline-node {
  position: absolute;
  left: -40px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
}

.node-success {
  background: #f0f9eb;
  color: #67c23a;
  border: 2px solid #67c23a;
}

.node-timeout {
  background: #fdf6ec;
  color: #e6a23c;
  border: 2px solid #e6a23c;
}

.node-fail {
  background: #fef0f0;
  color: #f56c6c;
  border: 2px solid #f56c6c;
}

.node-default {
  background: #f4f4f5;
  color: #909399;
  border: 2px solid #909399;
}

.timeline-content {
  padding: 12px 16px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  margin-left: 12px;
}

.step-name {
  font-weight: 600;
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.step-meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  gap: 12px;
}

.step-detail {
  margin-top: 8px;
  font-size: 12px;
  color: #606266;
}

.timeline-connector {
  position: relative;
  width: 2px;
  height: 24px;
  background: #dcdfe6;
  margin-left: -25px;
}

.timeline-connector.slow-link {
  background: #f56c6c;
  width: 3px;
  margin-left: -26px;
}

.delta-label {
  position: absolute;
  left: 8px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.delta-label.slow {
  color: #f56c6c;
  font-weight: bold;
}
</style>
