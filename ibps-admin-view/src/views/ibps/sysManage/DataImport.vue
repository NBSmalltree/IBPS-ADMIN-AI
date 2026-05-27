<template>
  <div class="data-import">
    <el-card>
      <template #header>
        <span>人行基础数据导入</span>
      </template>

      <el-select
        v-model="selectedFileType"
        placeholder="请选择文件类型"
        style="width: 100%; margin-bottom: 16px"
      >
        <el-option
          v-for="(label, code) in typeMap"
          :key="code"
          :label="`${code}(${label})`"
          :value="code"
        />
      </el-select>

      <el-upload
        class="upload-area"
        drag
        :auto-upload="false"
        :on-change="handleFileChange"
        :file-list="fileList"
        accept=".txt,.dat"
      >
        <el-icon class="el-icon--upload"><Upload /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持文件: IBPSODT0301(公共参数), IBPSODT0305(金额上限), IBPSODT0401(行别信息),
            IBPSODT0402(参与机构), IBPSODT0601(证书档案), IBPSODT0991(节点数据)
          </div>
        </template>
      </el-upload>

      <div v-if="selectedFile" class="file-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="文件名">{{ selectedFile.name }}</el-descriptions-item>
          <el-descriptions-item label="文件类型">
            <el-tag>{{ detectedType }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
        <el-button type="primary" :loading="importing" @click="startImport" style="margin-top: 16px">
          开始导入
        </el-button>
      </div>

      <div v-if="progress > 0" class="progress-area">
        <el-progress :percentage="progress" :status="progressStatus" />
        <p class="progress-text">{{ progressText }}</p>
      </div>

      <div v-if="importResult" class="result-area">
        <el-descriptions title="导入完成报告" :column="2" border>
          <el-descriptions-item label="总行数">{{ importResult.totalRows }}</el-descriptions-item>
          <el-descriptions-item label="成功行数">{{ importResult.successRows }}</el-descriptions-item>
          <el-descriptions-item label="失败行数">{{ importResult.failRows }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="importResult.status === 'DONE' ? 'success' : 'danger'">
              {{ importResult.status }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { uploadFile, connectImportProgress } from '@/api/ibps.js'

const fileList = ref([])
const selectedFile = ref(null)
const detectedType = ref('')
const importing = ref(false)
const progress = ref(0)
const progressText = ref('')
const progressStatus = ref('')
const importResult = ref(null)
const selectedFileType = ref('')

const typeMap = {
  'IBPSODT0301': '公共参数',
  'IBPSODT0305': '金额上限',
  'IBPSODT0401': '行别信息',
  'IBPSODT0402': '参与机构',
  'IBPSODT0601': '证书档案',
  'IBPSODT0991': '节点数据'
}

const detectFileType = (filename) => {
  const upper = filename.toUpperCase()
  for (const code of Object.keys(typeMap)) {
    if (upper.startsWith(code)) {
      return code
    }
  }
  return ''
}

const handleFileChange = (file) => {
  selectedFile.value = file.raw
  if (selectedFileType.value) {
    detectedType.value = selectedFileType.value
  } else {
    detectedType.value = detectFileType(file.name)
    if (!detectedType.value) {
      ElMessage.warning('无法识别文件类型，请先选择文件类型')
    }
  }
}

const startImport = async () => {
  if (!selectedFileType.value) {
    ElMessage.warning('请先选择文件类型')
    return
  }
  if (!detectedType.value) {
    ElMessage.error('无法识别文件类型')
    return
  }
  importing.value = true
  progress.value = 0
  importResult.value = null
  progressText.value = '正在上传...'

  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    formData.append('fileType', selectedFileType.value)
    const res = await uploadFile(formData)
    const importId = res.data?.importId

    if (importId) {
      connectImportProgress(importId, (msg) => {
        progress.value = msg.percent || 0
        progressText.value = msg.statusText || `进度 ${progress.value}%`
        if (msg.status === 'DONE' || msg.status === 'FAILED') {
          importResult.value = msg
          progressStatus.value = msg.status === 'DONE' ? 'success' : 'exception'
          importing.value = false
        }
      })
    }
  } catch (e) {
    ElMessage.error('导入失败')
    importing.value = false
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
