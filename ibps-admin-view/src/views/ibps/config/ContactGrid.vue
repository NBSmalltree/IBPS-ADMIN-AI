<template>
  <div class="contact-grid">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>行内联系人信息维护</span>
          <div>
            <el-input
              v-model="keyword"
              placeholder="搜索姓名/部门"
              style="width: 200px; margin-right: 10px"
              clearable
              @clear="loadContacts"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="loadContacts">搜索</el-button>
            <el-button type="success" @click="openAddDialog">新增</el-button>
          </div>
        </div>
      </template>

      <el-table :data="contactList" border stripe>
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="dept" label="所属部门/岗位" width="150" />
        <el-table-column prop="officePhone" label="办公电话" width="130" />
        <el-table-column prop="mobile" label="手机号码" width="130">
          <template #default="{ row }">
            {{ desensitizeMobile(row.mobile) }}
          </template>
        </el-table-column>
        <el-table-column prop="pbocKeyCard" label="人行密钥卡号" width="130" />
        <el-table-column prop="bizTypes" label="负责业务类型" min-width="200">
          <template #default="{ row }">
            <el-tag v-for="type in (row.bizTypes || '').split(',')" :key="type" size="small" style="margin: 2px">
              {{ type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑联系人' : '新增联系人'" width="500px">
      <el-form :model="contactForm" label-width="120px">
        <el-form-item label="姓名" required>
          <el-input v-model="contactForm.name" />
        </el-form-item>
        <el-form-item label="所属部门/岗位">
          <el-input v-model="contactForm.dept" />
        </el-form-item>
        <el-form-item label="办公电话">
          <el-input v-model="contactForm.officePhone" />
        </el-form-item>
        <el-form-item label="手机号码">
          <el-input v-model="contactForm.mobile" />
        </el-form-item>
        <el-form-item label="人行密钥卡号">
          <el-input v-model="contactForm.pbocKeyCard" />
        </el-form-item>
        <el-form-item label="负责业务类型">
          <el-select v-model="contactForm.bizTypeList" multiple placeholder="请选择">
            <el-option label="实时贷记" value="实时贷记" />
            <el-option label="实时借记" value="实时借记" />
            <el-option label="第三方贷记" value="第三方贷记" />
            <el-option label="跨境贷记" value="跨境贷记" />
            <el-option label="自由格式" value="自由格式" />
          </el-select>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getContactList, createContact, updateContact, deleteContact } from '@/api/ibps.js'

const keyword = ref('')
const contactList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)

const contactForm = ref({
  name: '',
  dept: '',
  officePhone: '',
  mobile: '',
  pbocKeyCard: '',
  bizTypeList: []
})

const desensitizeMobile = (mobile) => {
  if (!mobile || mobile.length < 7) return mobile || ''
  return mobile.substring(0, 3) + '****' + mobile.substring(mobile.length - 4)
}

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

const openAddDialog = () => {
  isEdit.value = false
  editingId.value = null
  contactForm.value = { name: '', dept: '', officePhone: '', mobile: '', pbocKeyCard: '', bizTypeList: [] }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  editingId.value = row.id
  contactForm.value = {
    name: row.name,
    dept: row.dept,
    officePhone: row.officePhone,
    mobile: row.mobile,
    pbocKeyCard: row.pbocKeyCard,
    bizTypeList: row.bizTypes ? row.bizTypes.split(',') : []
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!contactForm.value.name) {
    ElMessage.warning('请输入姓名')
    return
  }
  const data = {
    ...contactForm.value,
    bizTypes: contactForm.value.bizTypeList.join(',')
  }
  try {
    if (isEdit.value) {
      await updateContact(editingId.value, data)
    } else {
      await createContact(data)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    loadContacts()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除联系人 "${row.name}" 吗？`, '确认删除', { type: 'warning' })
    await deleteContact(row.id)
    ElMessage.success('删除成功')
    loadContacts()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
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
