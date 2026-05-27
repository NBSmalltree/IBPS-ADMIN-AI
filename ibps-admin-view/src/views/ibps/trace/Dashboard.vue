<template>
  <div class="dashboard">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>自然日异常监控大盘</span>
          <div>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :picker-options="datePickerOptions"
              @change="loadDashboard"
            />
            <el-button type="primary" style="margin-left: 10px" @click="loadDashboard">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-card shadow="hover" class="tile-card" @click="showDrillDown('ACCT')">
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
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="tile-card" @click="showDrillDown('ROLLBACK')">
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
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="tile-card" @click="showDrillDown('SEND')">
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
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- Drill Down Dialog -->
    <el-dialog v-model="drillDownVisible" :title="drillDownTitle" width="800px">
      <el-table :data="drillDownList" border stripe>
        <el-table-column prop="busiSerial" label="业务流水号" width="200" />
        <el-table-column prop="transCode" label="交易代码" width="120" />
        <el-table-column prop="step" label="最后步点" width="150" />
        <el-table-column prop="eventDatetime" label="发生时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="goToTraceGraph(row.busiSerial)">
              链路图谱
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getDashboard, getDrillDownList } from '@/api/ibps.js'

const router = useRouter()
const dateRange = ref([])
const dashboardData = ref({})
const drillDownVisible = ref(false)
const drillDownTitle = ref('')
const drillDownList = ref([])

const datePickerOptions = {
  disabledDate(date) {
    const now = new Date()
    return date.getTime() > now.getTime()
  }
}

const loadDashboard = async () => {
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.dateFrom = dateRange.value[0]
      params.dateTo = dateRange.value[1]
    }
    const res = await getDashboard(params)
    dashboardData.value = res.data || {}
  } catch (e) {
    console.error('Load dashboard failed:', e)
  }
}

const showDrillDown = async (category) => {
  const titleMap = {
    ACCT: '记账异常交易明细',
    ROLLBACK: '冲正高危交易明细',
    SEND: '发送网络异常明细'
  }
  drillDownTitle.value = titleMap[category]
  try {
    const params = { category }
    if (dateRange.value && dateRange.value.length === 2) {
      params.dateFrom = dateRange.value[0]
      params.dateTo = dateRange.value[1]
    }
    const res = await getDrillDownList(params)
    drillDownList.value = res.data || []
    drillDownVisible.value = true
  } catch (e) {
    console.error('Load drill-down failed:', e)
  }
}

const goToTraceGraph = (busiSerial) => {
  drillDownVisible.value = false
  router.push(`/ibps/trace/graph/${busiSerial}`)
}

onMounted(() => {
  loadDashboard()
})
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
