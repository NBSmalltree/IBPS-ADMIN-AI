<template>
  <div class="free-message">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>报文查询</span>
          <el-button type="primary" @click="showSendDialog = true">
            <el-icon><EditPen /></el-icon>
            发送报文
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="往来标记">
          <el-select v-model="queryForm.direction" placeholder="全部" clearable>
            <el-option label="往账" value="OUT" />
            <el-option label="来账" value="IN" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :picker-options="datePickerOptions"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="messageList" border stripe>
        <el-table-column prop="busiSerial" label="业务流水号" width="180" show-overflow-tooltip />
        <el-table-column prop="msgDirection" label="方向" width="80">
          <template #default="{ row }">
            <el-tag :type="row.msgDirection === 'IN' ? 'success' : 'warning'">
              {{ row.msgDirection === 'IN' ? '来账' : '往账' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="senderCode" label="发送方" width="120" />
        <el-table-column prop="receiverCode" label="接收方" width="120" />
        <el-table-column prop="msgContent" label="报文摘要" show-overflow-tooltip />
        <el-table-column prop="crossBorderType" label="跨境通" width="100" />
        <el-table-column prop="crossBorderOrigSerial" label="跨境通原业务流水号" width="180" show-overflow-tooltip />
        <el-table-column prop="sendStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.sendStatus)">
              {{ statusText(row.sendStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handleQuery"
      />
    </el-card>

    <!-- Send Dialog -->
    <el-dialog v-model="showSendDialog" title="发送自由格式报文" width="700px">
      <el-form :model="sendForm" label-width="140px">
        <el-form-item label="跨境通自由格式">
          <el-radio-group v-model="sendForm.crossBorder">
            <el-radio :value="true">是</el-radio>
            <el-radio :value="false">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="接收行号" required>
          <el-input
            v-model="sendForm.receiverBankCode"
            placeholder="请输入接收行号"
            maxlength="12"
            :disabled="sendForm.crossBorder"
          />
        </el-form-item>
        <el-form-item label="接收跨境机构号">
          <el-input
            v-model="sendForm.receiverCrossBorderCode"
            placeholder="请输入接收跨境机构号"
            maxlength="35"
            :disabled="!sendForm.crossBorder"
          />
        </el-form-item>
        <el-form-item label="跨境通业务流水号">
          <el-input
            v-model="sendForm.crossBorderSerial"
            placeholder="请输入跨境通业务流水号"
            maxlength="35"
            :disabled="!sendForm.crossBorder"
          />
        </el-form-item>
        <el-form-item label="报文内容" required>
          <el-input
            v-model="sendForm.msgContent"
            type="textarea"
            :rows="12"
            :placeholder="msgPlaceholder"
            style="font-family: monospace;"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSendDialog = false">取消</el-button>
        <el-button type="primary" :loading="sending" @click="handleSend">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { queryMessages, sendMessage } from '@/api/ibps.js'

const queryForm = ref({
  direction: '',
  dateRange: [],
  pageNum: 1,
  pageSize: 10
})

const messageList = ref([])
const total = ref(0)
const showSendDialog = ref(false)
const sending = ref(false)

const sendForm = ref({
  crossBorder: false,
  receiverBankCode: '',
  receiverCrossBorderCode: '',
  crossBorderSerial: '',
  msgContent: ''
})

const msgPlaceholder = computed(() => {
  return sendForm.value.crossBorder ? '请输入报文内容，必须为英文' : '请输入报文内容'
})

const datePickerOptions = {
  disabledDate(date) {
    const now = new Date()
    const maxRange = 7 * 24 * 60 * 60 * 1000
    return date.getTime() > now.getTime() || (now.getTime() - date.getTime()) > maxRange
  }
}

const statusTagType = (status) => {
  const map = { PENDING: 'info', SENT: 'success', FAILED: 'danger' }
  return map[status] || 'info'
}

const statusText = (status) => {
  const map = { PENDING: '待发送', SENT: '已发送', FAILED: '发送失败' }
  return map[status] || status
}

const handleQuery = async () => {
  try {
    const params = { ...queryForm.value }
    if (queryForm.value.dateRange && queryForm.value.dateRange.length === 2) {
      params.startDate = queryForm.value.dateRange[0]
      params.endDate = queryForm.value.dateRange[1]
    }
    delete params.dateRange
    const res = await queryMessages(params)
    messageList.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('Query failed:', e)
  }
}

const handleSend = async () => {
  const f = sendForm.value
  if (!f.msgContent) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (f.crossBorder) {
    if (!f.receiverCrossBorderCode || !f.crossBorderSerial) {
      ElMessage.warning('请填写完整信息')
      return
    }
  } else {
    if (!f.receiverBankCode) {
      ElMessage.warning('请填写完整信息')
      return
    }
  }
  sending.value = true
  try {
    await sendMessage(f)
    ElMessage.success('报文已提交异步发送')
    showSendDialog.value = false
    sendForm.value = {
      crossBorder: false,
      receiverBankCode: '',
      receiverCrossBorderCode: '',
      crossBorderSerial: '',
      msgContent: ''
    }
    handleQuery()
  } catch (e) {
    ElMessage.error('发送失败')
  } finally {
    sending.value = false
  }
}
</script>

<style scoped>
.free-message {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.query-form {
  margin-bottom: 16px;
}

.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
