<template>
  <div class="contact-grid">
    <h-card>
      <template #header>
        <div class="card-header">
          <span>行内联系人信息维护</span>
          <div>
            <h-input
              v-model="keyword"
              placeholder="搜索联系人工号"
              style="width: 200px; margin-right: 10px"
              clearable
              @clear="loadContacts"
            >
              <template #prefix>
                <h-icon><Search /></h-icon>
              </template>
            </h-input>
            <h-button type="primary" @click="loadContacts">搜索</h-button>
          </div>
        </div>
      </template>

      <h-table :data="contactList" border stripe>
        <h-table-column prop="templateId" label="模板ID" width="120" />
        <h-table-column prop="smsTemplateContent" label="短信模板内容" min-width="300" />
        <h-table-column prop="workerNos" label="联系人工号" width="200" />
        <h-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <h-button type="primary" link @click="openEditDialog(row)">维护</h-button>
          </template>
        </h-table-column>
      </h-table>

      <h-pagination
        :current-page.sync="pageNum"
        :page-size.sync="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadContacts"
      />
    </h-card>

    <!-- Edit Dialog -->
    <h-dialog :visible.sync="dialogVisible" title="编辑联系人工号" width="500px">
      <h-form :model="contactForm" label-width="120px">
        <h-form-item label="联系人工号" required>
          <h-input
            v-model="contactForm.workerNos"
            type="textarea"
            :rows="4"
            placeholder="请输入工号，多个工号用英文逗号分隔（如：195853,123456）"
          />
        </h-form-item>
      </h-form>
      <template #footer>
        <h-button @click="dialogVisible = false">取消</h-button>
        <h-button type="primary" @click="handleSave">保存</h-button>
      </template>
    </h-dialog>
  </div>
</template>

<script>
export default {
  name: 'ContactGrid',
  data() {
    return {
      keyword: '',
      contactList: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      dialogVisible: false,
      editingId: null,
      contactForm: {
        workerNos: ''
      }
    }
  },
  methods: {
    async loadContacts() {
      try {
        const res = await this.$http.get('/api/ibps/contact/list', {
          params: {
            keyword: this.keyword,
            pageNum: this.pageNum,
            pageSize: this.pageSize
          }
        })
        this.contactList = res.data?.list || []
        this.total = res.data?.total || 0
      } catch (e) {
        console.error('Load contacts failed:', e)
      }
    },
    openEditDialog(row) {
      this.editingId = row.id
      this.contactForm = { workerNos: row.workerNos || '' }
      this.dialogVisible = true
    },
    async handleSave() {
      const nos = this.contactForm.workerNos.trim()
      if (!nos) {
        this.$hMessage.warning('请输入联系人工号')
        return
      }
      const noList = nos.split(',').map(s => s.trim()).filter(Boolean)
      const invalid = noList.filter(n => !/^\d{6}$/.test(n))
      if (invalid.length > 0) {
        this.$hMessage.warning(`工号必须为6位数字，无效工号：${invalid.join(', ')}`)
        return
      }
      try {
        await this.$http.put(`/api/ibps/contact/${this.editingId}`, { workerNos: noList.join(',') })
        this.$hMessage.success('更新成功')
        this.dialogVisible = false
        this.loadContacts()
      } catch (e) {
        this.$hMessage.error('操作失败')
      }
    }
  },
  mounted() {
    this.loadContacts()
  }
}
</script>

<style scoped>
.contact-grid {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
