<template>
  <div class="cert-maintain">
    <el-card>
      <template #header>
        <span>本行数字证书维护</span>
      </template>

      <el-form :model="certForm" label-width="120px" style="max-width: 500px">
        <el-form-item label="变更类型" required>
          <el-radio-group v-model="certForm.changeType">
            <el-radio value="CC00">CC00 - 新增</el-radio>
            <el-radio value="CC02">CC02 - 撤销</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交申请</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { applyCertChange } from '@/api/ibps.js'

const certForm = ref({
  changeType: 'CC00'
})

const submitting = ref(false)

const handleSubmit = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要提交 ${certForm.value.changeType} 证书变更申请吗？`,
      '二次确认',
      { type: 'warning' }
    )
    submitting.value = true
    await applyCertChange(certForm.value)
    ElMessage.success('申请已提交')
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('提交失败')
    }
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.cert-maintain {
  padding: 20px;
}
</style>
