<template>
  <div class="contact-grid">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>行内联系人信息维护</span>
          <div>
            <el-input
              v-model="keyword"
              placeholder="搜索联系人工号"
              style="width: 200px; margin-right: 10px"
              clearable
              @clear="loadContacts"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="loadContacts">搜索</el-button>
          </div>
        </div>
      </template>

      <el-table :data="contactList" border stripe>
        <el-table-column prop="templateId" label="模板ID" width="120" />
        <el-table-column prop="smsTemplateContent" label="短信模板内容" min-width="300" />
        <el-table-column prop="workerNos" label="联系人工号" width="200" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">维护</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadContacts"
      />
    </el-card>

    <!-- Edit Dialog -->
    <el-dialog v-model="dialogVisible" title="编辑联系人工号" width="500px">
      <el-form :model="contactForm" label-width="120px">
        <el-form-item label="联系人工号" required>
          <el-input
            v-model="contactForm.workerNos"
            type="textarea"
            :rows="4"
            placeholder="请输入工号，多个工号用英文逗号分隔（如：195853,123456）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getContactList, updateContact } from '@/api/ibps.js'

const keyword = ref('')
const contactList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const editingId = ref(null)

const contactForm = ref({
  workerNos: ''
})

const loadContacts = async () => {
  try {
    const res = await getContactList({
      keyword: keyword.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    contactList.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('Load contacts failed:', e)
  }
}

const openEditDialog = (row) => {
  editingId.value = row.id
  contactForm.value = { workerNos: row.workerNos || '' }
  dialogVisible.value = true
}

const handleSave = async () => {
  const nos = contactForm.value.workerNos.trim()
  if (!nos) {
    ElMessage.warning('请输入联系人工号')
    return
  }
  const noList = nos.split(',').map(s => s.trim()).filter(Boolean)
  const invalid = noList.filter(n => !/^\d{6}$/.test(n))
  if (invalid.length > 0) {
    ElMessage.warning(`工号必须为6位数字，无效工号：${invalid.join(', ')}`)
    return
  }
  try {
    await updateContact(editingId.value, { workerNos: noList.join(',') })
    ElMessage.success('更新成功')
    dialogVisible.value = false
    loadContacts()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadContacts()
})
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
