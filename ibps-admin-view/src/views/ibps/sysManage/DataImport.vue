<template>
  <div class="data-import">
    <h-card>
      <template #header>
        <span>人行基础数据导入</span>
      </template>

      <h-select
        v-model="selectedFileType"
        placeholder="请选择文件类型"
        style="width: 100%; margin-bottom: 16px"
      >
        <h-option
          v-for="(label, code) in typeMap"
          :key="code"
          :label="`${code}(${label})`"
          :value="code"
        />
      </h-select>

      <h-upload
        class="upload-area"
        drag
        :auto-upload="false"
        :on-change="handleFileChange"
        :file-list="fileList"
        accept=".txt,.dat"
      >
        <h-icon class="el-icon--upload"><Upload /></h-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持文件: IBPSODT0301(公共参数), IBPSODT0305(金额上限), IBPSODT0401(行别信息),
            IBPSODT0402(参与机构), IBPSODT0601(证书档案), IBPSODT0991(节点数据)
          </div>
        </template>
      </h-upload>

      <div v-if="selectedFile" class="file-info">
        <h-descriptions :column="2" border>
          <h-descriptions-item label="文件名">{{ selectedFile.name }}</h-descriptions-item>
          <h-descriptions-item label="文件类型">
            <h-tag>{{ detectedType }}</h-tag>
          </h-descriptions-item>
        </h-descriptions>
        <h-button type="primary" :loading="importing" @click="startImport" style="margin-top: 16px">
          开始导入
        </h-button>
      </div>

      <div v-if="progress > 0" class="progress-area">
        <h-progress :percentage="progress" :status="progressStatus" />
        <p class="progress-text">{{ progressText }}</p>
      </div>

      <div v-if="importResult" class="result-area">
        <h-descriptions title="导入完成报告" :column="2" border>
          <h-descriptions-item label="总行数">{{ importResult.totalRows }}</h-descriptions-item>
          <h-descriptions-item label="成功行数">{{ importResult.successRows }}</h-descriptions-item>
          <h-descriptions-item label="失败行数">{{ importResult.failRows }}</h-descriptions-item>
          <h-descriptions-item label="状态">
            <h-tag :type="importResult.status === 'DONE' ? 'success' : 'danger'">
              {{ importResult.status }}
            </h-tag>
          </h-descriptions-item>
        </h-descriptions>
      </div>
    </h-card>
  </div>
</template>

<script>
import { connectImportProgress } from '@/scripts/api/ibps.js'

export default {
  name: 'DataImport',
  data() {
    return {
      fileList: [],
      selectedFile: null,
      detectedType: '',
      importing: false,
      progress: 0,
      progressText: '',
      progressStatus: '',
      importResult: null,
      selectedFileType: '',
      typeMap: {
        'IBPSODT0301': '公共参数',
        'IBPSODT0305': '金额上限',
        'IBPSODT0401': '行别信息',
        'IBPSODT0402': '参与机构',
        'IBPSODT0601': '证书档案',
        'IBPSODT0991': '节点数据'
      }
    }
  },
  methods: {
    detectFileType(filename) {
      const upper = filename.toUpperCase()
      for (const code of Object.keys(this.typeMap)) {
        if (upper.startsWith(code)) {
          return code
        }
      }
      return ''
    },
    handleFileChange(file) {
      this.selectedFile = file.raw
      if (this.selectedFileType) {
        this.detectedType = this.selectedFileType
      } else {
        this.detectedType = this.detectFileType(file.name)
        if (!this.detectedType) {
          this.$hMessage.warning('无法识别文件类型，请先选择文件类型')
        }
      }
    },
    async startImport() {
      if (!this.selectedFileType) {
        this.$hMessage.warning('请先选择文件类型')
        return
      }
      if (!this.detectedType) {
        this.$hMessage.error('无法识别文件类型')
        return
      }
      this.importing = true
      this.progress = 0
      this.importResult = null
      this.progressText = '正在上传...'

      try {
        const formData = new FormData()
        formData.append('file', this.selectedFile)
        formData.append('fileType', this.selectedFileType)
        const res = await this.$http.post('/api/ibps/import/upload', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        const importId = res.data?.importId

        if (importId) {
          connectImportProgress(importId, (msg) => {
            this.progress = msg.percent || 0
            this.progressText = msg.statusText || `进度 ${this.progress}%`
            if (msg.status === 'DONE' || msg.status === 'FAILED') {
              this.importResult = msg
              this.progressStatus = msg.status === 'DONE' ? 'success' : 'exception'
              this.importing = false
            }
          })
        }
      } catch (e) {
        this.$hMessage.error('导入失败')
        this.importing = false
      }
    }
  }
}
</script>

<style scoped>
.data-import {
  padding: 20px;
}

.upload-area {
  margin-bottom: 20px;
}

.file-info {
  margin-bottom: 20px;
}

.progress-area {
  margin: 20px 0;
}

.progress-text {
  margin-top: 8px;
  color: #909399;
  font-size: 14px;
}

.result-area {
  margin-top: 20px;
}
</style>
