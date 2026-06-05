<template>
  <div class="free-message">
    <h-card>
      <template #header>
        <div class="card-header">
          <span>报文查询</span>
          <h-button type="primary" @click="showSendDialog = true">
            <h-icon><EditPen /></h-icon>
            发送报文
          </h-button>
        </div>
      </template>

      <h-form :inline="true" :model="queryForm" class="query-form">
        <h-form-item label="往来标记">
          <h-select v-model="queryForm.direction" placeholder="全部" clearable>
            <h-option label="往账" value="OUT" />
            <h-option label="来账" value="IN" />
          </h-select>
        </h-form-item>
        <h-form-item label="时间范围">
          <h-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :picker-options="datePickerOptions"
          />
        </h-form-item>
        <h-form-item>
          <h-button type="primary" @click="handleQuery">查询</h-button>
        </h-form-item>
      </h-form>

      <h-table :data="messageList" border stripe>
        <h-table-column prop="busiSerial" label="业务流水号" width="180" show-overflow-tooltip />
        <h-table-column prop="msgDirection" label="方向" width="80">
          <template #default="{ row }">
            <h-tag :type="row.msgDirection === 'IN' ? 'success' : 'warning'">
              {{ row.msgDirection === 'IN' ? '来账' : '往账' }}
            </h-tag>
          </template>
        </h-table-column>
        <h-table-column prop="senderCode" label="发送方" width="120" />
        <h-table-column prop="receiverCode" label="接收方" width="120" />
        <h-table-column prop="msgContent" label="报文摘要" show-overflow-tooltip />
        <h-table-column prop="crossBorderType" label="跨境通" width="100" />
        <h-table-column prop="crossBorderOrigSerial" label="跨境通原业务流水号" width="180" show-overflow-tooltip />
        <h-table-column prop="sendStatus" label="状态" width="100">
          <template #default="{ row }">
            <h-tag :type="statusTagType(row.sendStatus)">
              {{ statusText(row.sendStatus) }}
            </h-tag>
          </template>
        </h-table-column>
        <h-table-column prop="createTime" label="时间" width="180" />
      </h-table>

      <h-pagination
        :current-page.sync="queryForm.pageNum"
        :page-size.sync="queryForm.pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handleQuery"
      />
    </h-card>

    <!-- Send Dialog -->
    <h-dialog :visible.sync="showSendDialog" title="发送自由格式报文" width="700px">
      <h-form :model="sendForm" label-width="140px">
        <h-form-item label="跨境通自由格式">
          <h-radio-group v-model="sendForm.crossBorder">
            <h-radio :label="true">是</h-radio>
            <h-radio :label="false">否</h-radio>
          </h-radio-group>
          <h-button type="primary" link style="margin-left: auto;" @click="handleSelfSend">自发自收</h-button>
        </h-form-item>
        <h-form-item label="接收行号" :required="!sendForm.crossBorder">
          <h-input
            v-model="sendForm.receiverBankCode"
            placeholder="请输入接收行号"
            maxlength="12"
            :disabled="sendForm.crossBorder"
          />
        </h-form-item>
        <h-form-item label="接收跨境机构号" :required="sendForm.crossBorder">
          <h-input
            v-model="sendForm.receiverCrossBorderCode"
            placeholder="请输入接收跨境机构号"
            maxlength="35"
            :disabled="!sendForm.crossBorder"
          />
        </h-form-item>
        <h-form-item label="跨境通业务流水号" :required="sendForm.crossBorder">
          <h-input
            v-model="sendForm.crossBorderSerial"
            placeholder="请输入跨境通业务流水号"
            maxlength="35"
            :disabled="!sendForm.crossBorder"
          />
        </h-form-item>
        <h-form-item label="报文内容" required>
          <h-input
            v-model="sendForm.msgContent"
            type="textarea"
            :rows="12"
            :placeholder="msgPlaceholder"
            style="font-family: monospace;"
          />
        </h-form-item>
      </h-form>
      <template #footer>
        <h-button @click="showSendDialog = false">取消</h-button>
        <h-button type="primary" :loading="sending" @click="handleSend">发送</h-button>
      </template>
    </h-dialog>
  </div>
</template>

<script>
export default {
  name: 'FreeMessage',
  data() {
    return {
      queryForm: {
        direction: '',
        dateRange: [],
        pageNum: 1,
        pageSize: 10
      },
      messageList: [],
      total: 0,
      showSendDialog: false,
      sending: false,
      sendForm: {
        crossBorder: false,
        receiverBankCode: '',
        receiverCrossBorderCode: '',
        crossBorderSerial: '',
        msgContent: ''
      },
      datePickerOptions: {
        disabledDate(date) {
          const now = new Date()
          const maxRange = 7 * 24 * 60 * 60 * 1000
          return date.getTime() > now.getTime() || (now.getTime() - date.getTime()) > maxRange
        }
      }
    }
  },
  computed: {
    msgPlaceholder() {
      return this.sendForm.crossBorder ? '请输入报文内容，必须为英文' : '请输入报文内容'
    }
  },
  methods: {
    statusTagType(status) {
      const map = { PENDING: 'info', SENT: 'success', FAILED: 'danger' }
      return map[status] || 'info'
    },
    statusText(status) {
      const map = { PENDING: '待发送', SENT: '已发送', FAILED: '发送失败' }
      return map[status] || status
    },
    async handleQuery() {
      try {
        const params = { ...this.queryForm }
        if (this.queryForm.dateRange && this.queryForm.dateRange.length === 2) {
          params.startDate = this.queryForm.dateRange[0]
          params.endDate = this.queryForm.dateRange[1]
        }
        delete params.dateRange
        const res = await this.$http.get('/api/ibps/message/list', { params })
        this.messageList = res.data?.list || []
        this.total = res.data?.total || 0
      } catch (e) {
        console.error('Query failed:', e)
      }
    },
    handleSelfSend() {
      this.sendForm.crossBorder = false
      this.sendForm.receiverBankCode = '313332082914'
      this.sendForm.msgContent = '宁波银行自由格式测试'
    },
    async handleSend() {
      const f = this.sendForm
      if (!f.msgContent) {
        this.$hMessage.warning('请填写完整信息')
        return
      }
      if (f.crossBorder) {
        if (!f.receiverCrossBorderCode || !f.crossBorderSerial) {
          this.$hMessage.warning('请填写完整信息')
          return
        }
      } else {
        if (!f.receiverBankCode) {
          this.$hMessage.warning('请填写完整信息')
          return
        }
      }
      this.sending = true
      try {
        const url = f.crossBorder
          ? '/api/ibps/message/cross-border/send'
          : '/api/ibps/message/send'
        await this.$http.post(url, f)
        this.$hMessage.success('报文已提交异步发送')
        this.showSendDialog = false
        this.sendForm = {
          crossBorder: false,
          receiverBankCode: '',
          receiverCrossBorderCode: '',
          crossBorderSerial: '',
          msgContent: ''
        }
        this.handleQuery()
      } catch (e) {
        this.$hMessage.error('发送失败')
      } finally {
        this.sending = false
      }
    }
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
