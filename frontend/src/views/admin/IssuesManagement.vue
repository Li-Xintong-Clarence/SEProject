<template>
  <div class="issues-management">
    <h2 class="page-title">问题报告管理</h2>
    <p class="page-sub">CapyGlide · 车辆故障与用户反馈</p>

    <el-tabs v-model="activeTab" type="border-card">
      <!-- 故障工单 -->
      <el-tab-pane label="故障工单" name="issues">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>车辆故障报告</span>
              <el-button type="primary" @click="loadIssues" :loading="loadingIssues">
                <el-icon><Refresh /></el-icon> 刷新
              </el-button>
            </div>
          </template>
          <el-table :data="issues" v-loading="loadingIssues" stripe>
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column prop="scooterId" label="车辆ID" width="100" />
            <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
            <el-table-column prop="priority" label="优先级" width="100">
              <template #default="{ row }">
                <el-tag :type="getPriorityType(row.priority)" size="small">{{ row.priority }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="时间" width="180">
              <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="openProcessIssue(row)">处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 用户反馈 -->
      <el-tab-pane label="用户反馈" name="feedbacks">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>用户反馈</span>
              <el-button type="primary" @click="loadFeedbacks" :loading="loadingFeedbacks">
                <el-icon><Refresh /></el-icon> 刷新
              </el-button>
            </div>
          </template>
          <el-table :data="feedbacks" v-loading="loadingFeedbacks" stripe>
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column prop="userId" label="用户ID" width="100" />
            <el-table-column prop="description" label="内容" min-width="200" show-overflow-tooltip />
            <el-table-column prop="priority" label="优先级" width="100">
              <template #default="{ row }">
                <el-tag :type="getPriorityType(row.priority)" size="small">{{ row.priority }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="adminResponse" label="回复" min-width="150" show-overflow-tooltip />
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="openProcessFeedback(row)">处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 处理故障弹窗 -->
    <el-dialog v-model="issueDialogVisible" title="处理故障工单" width="500px">
      <el-form :model="issueForm" label-width="100px">
        <el-form-item label="状态">
          <el-select v-model="issueForm.status" style="width: 100%">
            <el-option label="待处理 (OPEN)" value="OPEN" />
            <el-option label="处理中 (IN_PROGRESS)" value="IN_PROGRESS" />
            <el-option label="已解决 (RESOLVED)" value="RESOLVED" />
            <el-option label="已关闭 (CLOSED)" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="issueForm.priority" style="width: 100%">
            <el-option label="低 (LOW)" value="LOW" />
            <el-option label="中 (MEDIUM)" value="MEDIUM" />
            <el-option label="高 (HIGH)" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input v-model="issueForm.adminResponse" type="textarea" :rows="3" placeholder="输入处理备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="issueDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitIssue">提交</el-button>
      </template>
    </el-dialog>

    <!-- 处理反馈弹窗 -->
    <el-dialog v-model="feedbackDialogVisible" title="处理用户反馈" width="500px">
      <el-form :model="feedbackForm" label-width="100px">
        <el-form-item label="状态">
          <el-select v-model="feedbackForm.status" style="width: 100%">
            <el-option label="待处理 (OPEN)" value="OPEN" />
            <el-option label="处理中 (IN_PROGRESS)" value="IN_PROGRESS" />
            <el-option label="已解决 (RESOLVED)" value="RESOLVED" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="feedbackForm.priority" style="width: 100%">
            <el-option label="低 (LOW)" value="LOW" />
            <el-option label="中 (MEDIUM)" value="MEDIUM" />
            <el-option label="高 (HIGH)" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item label="回复内容">
          <el-input v-model="feedbackForm.adminResponse" type="textarea" :rows="4" placeholder="输入回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="feedbackDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitFeedback">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getAllFeedbacks, processFeedback } from '@/api/admin'
import { getAllIssueReports, updateIssueReport } from '@/api/issues'

const activeTab = ref('issues')

const issues = ref([])
const loadingIssues = ref(false)
const feedbacks = ref([])
const loadingFeedbacks = ref(false)

const issueDialogVisible = ref(false)
const feedbackDialogVisible = ref(false)
const submitting = ref(false)

const currentIssue = ref(null)
const issueForm = ref({ status: 'OPEN', priority: 'LOW', adminResponse: '' })

const currentFeedback = ref(null)
const feedbackForm = ref({ status: 'OPEN', priority: 'LOW', adminResponse: '' })

const formatTime = (time) => {
  if (!time) return '—'
  const date = new Date(time)
  if (isNaN(date.getTime())) return String(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

const getPriorityType = (priority) => {
  const map = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info' }
  return map[priority] || 'info'
}

const getStatusType = (status) => {
  const map = {
    OPEN: 'warning',
    IN_PROGRESS: 'primary',
    RESOLVED: 'success',
    CLOSED: 'info',
    PENDING: 'warning'
  }
  return map[status] || 'info'
}

const loadIssues = async () => {
  loadingIssues.value = true
  try {
    const res = await getAllIssueReports()
    issues.value = Array.isArray(res) ? res : []
  } catch (e) {
    console.error(e)
  } finally {
    loadingIssues.value = false
  }
}

const loadFeedbacks = async () => {
  loadingFeedbacks.value = true
  try {
    const res = await getAllFeedbacks()
    feedbacks.value = Array.isArray(res) ? res : []
  } catch (e) {
    console.error(e)
  } finally {
    loadingFeedbacks.value = false
  }
}

const openProcessIssue = (row) => {
  currentIssue.value = row
  issueForm.value = {
    status: row.status || 'OPEN',
    priority: row.priority || 'LOW',
    adminResponse: row.adminResponse || ''
  }
  issueDialogVisible.value = true
}

const submitIssue = async () => {
  if (!currentIssue.value) return
  submitting.value = true
  try {
    await updateIssueReport(currentIssue.value.id, issueForm.value)
    ElMessage.success('处理成功')
    issueDialogVisible.value = false
    await loadIssues()
  } catch (e) {
    ElMessage.error('处理失败')
  } finally {
    submitting.value = false
  }
}

const openProcessFeedback = (row) => {
  currentFeedback.value = row
  feedbackForm.value = {
    status: row.status || 'OPEN',
    priority: row.priority || 'LOW',
    adminResponse: row.adminResponse || ''
  }
  feedbackDialogVisible.value = true
}

const submitFeedback = async () => {
  if (!currentFeedback.value) return
  submitting.value = true
  try {
    await processFeedback(currentFeedback.value.id, feedbackForm.value)
    ElMessage.success('回复成功')
    feedbackDialogVisible.value = false
    await loadFeedbacks()
  } catch (e) {
    ElMessage.error('回复失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadIssues()
  loadFeedbacks()
})
</script>

<style scoped>
.issues-management {
  padding: 32px 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  margin: 0 0 4px;
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--cg-text);
  letter-spacing: -0.02em;
}

.page-sub {
  margin: 0 0 24px;
  font-size: 15px;
  color: var(--cg-text-light);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 700;
  color: var(--cg-text);
}
</style>
