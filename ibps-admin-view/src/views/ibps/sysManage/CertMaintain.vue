<template>
  <div class="cert-maintain">
    <h-card>
      <template #header>
        <span>本行数字证书维护</span>
      </template>

      <h-form :model="certForm" label-width="120px" style="max-width: 500px">
        <h-form-item label="变更类型" required>
          <h-radio-group v-model="certForm.changeType">
            <h-radio label="CC00">CC00 - 新增</h-radio>
            <h-radio label="CC02">CC02 - 撤销</h-radio>
          </h-radio-group>
        </h-form-item>
        <h-form-item>
          <h-button type="primary" :loading="submitting" @click="handleSubmit">提交申请</h-button>
        </h-form-item>
      </h-form>
    </h-card>
  </div>
</template>

<script>
export default {
  name: 'CertMaintain',
  data() {
    return {
      certForm: {
        changeType: 'CC00'
      },
      submitting: false
    }
  },
  methods: {
    async handleSubmit() {
      try {
        await this.$hConfirm(
          `确定要提交 ${this.certForm.changeType} 证书变更申请吗？`,
          '二次确认',
          { type: 'warning' }
        )
        this.submitting = true
        await this.$http.post('/api/ibps/cert/change', this.certForm)
        this.$hMessage.success('申请已提交')
      } catch (e) {
        if (e !== 'cancel') {
          this.$hMessage.error('提交失败')
        }
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.cert-maintain {
  padding: 20px;
}
</style>
