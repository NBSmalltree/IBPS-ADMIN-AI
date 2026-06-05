<template>
  <div class="dashboard">
    <h-card>
      <template #header>
        <div class="card-header">
          <span>异常监控大盘</span>
          <div>
            <h-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :picker-options="datePickerOptions"
              @change="loadDashboard"
            />
            <h-button type="primary" style="margin-left: 10px" @click="loadDashboard">
              <h-icon><Refresh /></h-icon>
              刷新
            </h-button>
          </div>
        </div>
      </template>

      <h-row :gutter="20">
        <h-col :span="8">
          <h-card shadow="hover" class="tile-card" @click="showDrillDown('ACCT')">
            <h3>记账异常交易</h3>
            <div class="tile-numbers">
              <div class="number-item">
                <span class="number fail">{{ dashboardData.acctFailCount || 0 }}</span>
                <span class="label">失败</span>
              </div>
              <div class="number-item">
                <span class="number timeout">{{ dashboardData.acctTimeoutCount || 0 }}</span>
                <span class="label">超时</span>
              </div>
            </div>
          </h-card>
        </h-col>
        <h-col :span="8">
          <h-card shadow="hover" class="tile-card" @click="showDrillDown('ROLLBACK')">
            <h3>冲正高危交易</h3>
            <div class="tile-numbers">
              <div class="number-item">
                <span class="number fail">{{ dashboardData.rollbackFailCount || 0 }}</span>
                <span class="label">失败</span>
              </div>
              <div class="number-item">
                <span class="number timeout">{{ dashboardData.rollbackTimeoutCount || 0 }}</span>
                <span class="label">超时</span>
              </div>
            </div>
          </h-card>
        </h-col>
        <h-col :span="8">
          <h-card shadow="hover" class="tile-card" @click="showDrillDown('SEND')">
            <h3>发送网络异常</h3>
            <div class="tile-numbers">
              <div class="number-item">
                <span class="number fail">{{ dashboardData.sendFailCount || 0 }}</span>
                <span class="label">失败</span>
              </div>
              <div class="number-item">
                <span class="number timeout">{{ dashboardData.sendTimeoutCount || 0 }}</span>
                <span class="label">超时</span>
              </div>
            </div>
          </h-card>
        </h-col>
      </h-row>
    </h-card>

    <!-- Drill Down Dialog -->
    <h-dialog :visible.sync="drillDownVisible" :title="drillDownTitle" width="800px">
      <h-table :data="drillDownList" border stripe>
        <h-table-column prop="busiSerial" label="业务流水号" width="200" />
        <h-table-column prop="transCode" label="交易代码" width="120" />
        <h-table-column prop="step" label="最后步点" width="150" />
        <h-table-column prop="eventDatetime" label="发生时间" width="180" />
        <h-table-column label="操作" width="120">
          <template #default="{ row }">
            <h-button type="primary" link @click="goToTraceGraph(row.busiSerial)">
              链路图谱
            </h-button>
          </template>
        </h-table-column>
      </h-table>
    </h-dialog>
  </div>
</template>

<script>
export default {
  name: 'Dashboard',
  data() {
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const todayEnd = new Date(today)
    todayEnd.setHours(23, 59, 59, 999)
    return {
      dateRange: [today, todayEnd],
      dashboardData: {},
      drillDownVisible: false,
      drillDownTitle: '',
      drillDownList: [],
      datePickerOptions: {
        disabledDate(date) {
          const now = new Date()
          return date.getTime() > now.getTime()
        }
      }
    }
  },
  methods: {
    async loadDashboard() {
      try {
        const params = {}
        if (this.dateRange && this.dateRange.length === 2) {
          params.dateFrom = this.dateRange[0]
          params.dateTo = this.dateRange[1]
        }
        const res = await this.$http.get('/api/ibps/trace/dashboard', { params })
        this.dashboardData = res.data || {}
      } catch (e) {
        console.error('Load dashboard failed:', e)
      }
    },
    async showDrillDown(category) {
      const titleMap = {
        ACCT: '记账异常交易明细',
        ROLLBACK: '冲正高危交易明细',
        SEND: '发送网络异常明细'
      }
      this.drillDownTitle = titleMap[category]
      try {
        const params = { category }
        if (this.dateRange && this.dateRange.length === 2) {
          params.dateFrom = this.dateRange[0]
          params.dateTo = this.dateRange[1]
        }
        const res = await this.$http.get('/api/ibps/trace/drill-down', { params })
        this.drillDownList = res.data || []
        this.drillDownVisible = true
      } catch (e) {
        console.error('Load drill-down failed:', e)
      }
    },
    goToTraceGraph(busiSerial) {
      this.drillDownVisible = false
      this.$router.push(`/ibps/trace/graph/${busiSerial}`)
    }
  },
  mounted() {
    this.loadDashboard()
  }
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tile-card {
  cursor: pointer;
  text-align: center;
  transition: transform 0.2s;
}

.tile-card:hover {
  transform: translateY(-4px);
}

.tile-card h3 {
  margin-bottom: 16px;
  color: #303133;
}

.tile-numbers {
  display: flex;
  justify-content: center;
  gap: 40px;
}

.number-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.number {
  font-size: 36px;
  font-weight: bold;
}

.number.fail {
  color: #f56c6c;
}

.number.timeout {
  color: #e6a23c;
}

.label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}
</style>
