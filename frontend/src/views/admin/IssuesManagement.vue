<template>
  <div class="issues-management cg-page">
    <div class="page-header">
      <h2 class="cg-title">问题管理</h2>
      <el-select v-model="filterStatus" placeholder="筛选状态" clearable style="width: 150px">
        <el-option label="待处理" value="OPEN" />
        <el-option label="处理中" value="IN_PROGRESS" />
        <el-option label="已解决" value="RESOLVED" />
      </el-select>
    </div>

    <el-skeleton v-if="loading" :rows="8" animated />

    <template v-else>
      <el-card shadow="never">
        <el-table :data="filteredIssues" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="问题类型" width="120">
            <template #default="{ row }">
              <el-tag :type="getTypeTagType(row.type)">{{ getTypeLabel(row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="scooterId" label="滑板车" width="100" />
          <el-table-column prop="reporterName" label="报告人" width="120" />
          <el-table-column prop="description" label="问题描述" min-width="200" show-overflow-tooltip />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="报告时间" width="160">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="viewDetail(row)">详情</el-button>
              <el-dropdown v-if="row.status === 'OPEN'" @command="(cmd) => handleAction(row, cmd)">
                <el-button size="small" type="success">
                  处理 <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="accept">接受</el-dropdown-item>
                    <el-dropdown-item command="resolve">标记已解决</el-dropdown-item>
                    <el-dropdown-item command="reject">拒绝</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </template>

    <el-dialog v-model="detailVisible" title="问题详情" width="600px">
      <el-descriptions v-if="selectedIssue" :column="1" border>
        <el-descriptions-item label="问题ID">{{ selectedIssue.id }}</el-descriptions-item>
        <el-descriptions-item label="问题类型">{{ getTypeLabel(selectedIssue.type) }}</el-descriptions-item>
        <el-descriptions-item label="滑板车">{{ selectedIssue.scooterId }}</el-descriptions-item>
        <el-descriptions-item label="报告人">{{ selectedIssue.reporterName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusLabel(selectedIssue.status) }}</el-descriptions-item>
        <el-descriptions-item label="问题描述">{{ selectedIssue.description }}</el-descriptions-item>
        <el-descriptions-item label="报告时间">{{ formatDate(selectedIssue.createdAt) }}</el-descriptions-item>
        <el-descriptions-item v-if="selectedIssue.resolvedAt" label="解决时间">
          {{ formatDate(selectedIssue.resolvedAt) }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { getAllIssueReports, updateIssueReport } from '@/api/issues'

const loading = ref(true)
const issues = ref([])
const filterStatus = ref('')
const detailVisible = ref(false)
const selectedIssue = ref(null)

const filteredIssues = computed(() => {
  if (!filterStatus.value) return issues.value
  return issues.value.filter(i => i.status === filterStatus.value)
})

const getTypeLabel = (type) => {
  const map = {
    BREAKDOWN: '车辆故障',
    LOW_BATTERY: '电量不足',
    RETURN_ISSUE: '无法还车',
    OTHER: '其他问题'
  }
  return map[type] || type || '未知'
}

const getTypeTagType = (type) => {
  const map = {
    BREAKDOWN: 'danger',
    LOW_BATTERY: 'warning',
    RETURN_ISSUE: 'info',
    OTHER: ''
  }
  return map[type] || ''
}

const getStatusLabel = (status) => {
  const map = {
    OPEN: '待处理',
    IN_PROGRESS: '处理中',
    RESOLVED: '已解决',
    REJECTED: '已拒绝'
  }
  return map[status] || status || '未知'
}

const getStatusTagType = (status) => {
  const map = {
    OPEN: 'danger',
    IN_PROGRESS: 'warning',
    RESOLVED: 'success',
    REJECTED: 'info'
  }
  return map[status] || ''
}

const formatDate = (date) => {
  if (!date) return '—'
  return new Date(date).toLocaleString('zh-CN')
}

const loadIssues = async () => {
  loading.value = true
  try {
    const res = await getAllIssueReports()
    issues.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const viewDetail = (issue) => {
  selectedIssue.value = issue
  detailVisible.value = true
}

const handleAction = async (issue, action) => {
  try {
    let updateData = {}
    if (action === 'accept') {
      updateData = { status: 'IN_PROGRESS' }
    } else if (action === 'resolve') {
      updateData = { status: 'RESOLVED' }
    } else if (action === 'reject') {
      updateData = { status: 'REJECTED' }
    }
    await updateIssueReport(issue.id, updateData)
    ElMessage.success('操作成功')
    await loadIssues()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadIssues()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
</style>
