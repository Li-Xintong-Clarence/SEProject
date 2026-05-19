<template>
  <div class="issues-management cg-page">
    <div class="page-header">
      <h2 class="cg-title">问题管理</h2>
      <div class="header-actions">
        <el-select v-model="filterStatus" placeholder="筛选状态" clearable style="width: 150px">
          <el-option label="待处理" value="OPEN" />
          <el-option label="处理中" value="IN_PROGRESS" />
          <el-option label="已解决" value="RESOLVED" />
        </el-select>
      </div>
    </div>

    <!-- 优先级标签页 (ID15: 查看高优先级问题) -->
    <div class="priority-tabs">
      <el-radio-group v-model="viewMode" class="view-mode-group">
        <el-radio-button value="all">
          全部问题
          <el-badge :value="issues.length" :hidden="issues.length === 0" class="tab-badge" />
        </el-radio-button>
        <el-radio-button value="high">
          <span class="high-priority-tab">
            <el-icon class="high-icon"><WarningFilled /></el-icon>
            高优先级
            <el-badge :value="highPriorityCount" :hidden="highPriorityCount === 0" class="tab-badge high-badge" type="danger" />
          </span>
        </el-radio-button>
      </el-radio-group>
    </div>

    <el-skeleton v-if="loading" :rows="8" animated />

    <template v-else>
      <el-card shadow="never">
        <el-table :data="filteredIssues" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="优先级" width="100">
            <template #default="{ row }">
              <el-tag :type="getPriorityTagType(row.priority)" size="small">
                {{ getPriorityLabel(row.priority) }}
              </el-tag>
            </template>
          </el-table-column>
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
              <el-dropdown v-if="row.status === 'OPEN' || row.status === 'PENDING'" @command="(cmd) => handleAction(row, cmd)">
                <el-button size="small" type="success">
                  处理 <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="escalate">标记为高优先级</el-dropdown-item>
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

      <!-- 高优先级问题快速查看面板 (ID15) -->
      <el-card v-if="viewMode === 'high' && highPriorityIssues.length > 0" shadow="never" class="high-priority-panel">
        <template #header>
          <div class="panel-header">
            <el-icon class="warning-icon"><WarningFilled /></el-icon>
            <span>高优先级问题需要立即处理</span>
          </div>
        </template>
        <div class="high-priority-list">
          <div v-for="issue in highPriorityIssues.slice(0, 5)" :key="issue.id" class="high-priority-item">
            <div class="item-main">
              <span class="item-id">#{{ issue.id }}</span>
              <span class="item-desc">{{ issue.description }}</span>
            </div>
            <div class="item-meta">
              <el-tag :type="getStatusTagType(issue.status)" size="small">{{ getStatusLabel(issue.status) }}</el-tag>
              <span class="item-time">{{ formatDate(issue.createdAt) }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </template>

    <el-dialog v-model="detailVisible" title="问题详情" width="600px">
      <el-descriptions v-if="selectedIssue" :column="1" border>
        <el-descriptions-item label="问题ID">{{ selectedIssue.id }}</el-descriptions-item>
        <el-descriptions-item label="问题类型">{{ getTypeLabel(selectedIssue.type) }}</el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="getPriorityTagType(selectedIssue.priority)">{{ getPriorityLabel(selectedIssue.priority) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="滑板车">{{ selectedIssue.scooterId }}</el-descriptions-item>
        <el-descriptions-item label="报告人">{{ selectedIssue.reporterName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusLabel(selectedIssue.status) }}</el-descriptions-item>
        <el-descriptions-item label="问题描述">{{ selectedIssue.description }}</el-descriptions-item>
        <el-descriptions-item label="报告时间">{{ formatDate(selectedIssue.createdAt) }}</el-descriptions-item>
        <el-descriptions-item v-if="selectedIssue.resolvedAt" label="解决时间">
          {{ formatDate(selectedIssue.resolvedAt) }}
        </el-descriptions-item>
        <el-descriptions-item v-if="selectedIssue.adminFeedback" label="管理员反馈">
          {{ selectedIssue.adminFeedback }}
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
import { ArrowDown, WarningFilled } from '@element-plus/icons-vue'
import { getAllIssueReports, getHighPriorityIssueReports, updateIssueReport } from '@/api/issues'

const loading = ref(true)
const issues = ref([])
const highPriorityIssues = ref([])
const filterStatus = ref('')
const viewMode = ref('all')  // 'all' | 'high' - ID15
const detailVisible = ref(false)
const selectedIssue = ref(null)

// 高优先级问题数量
const highPriorityCount = computed(() => {
  return highPriorityIssues.value.filter(i =>
    i.priority === 'HIGH' && (i.status === 'OPEN' || i.status === 'PENDING' || i.status === 'IN_PROGRESS')
  ).length
})

// 根据视图模式过滤问题
const filteredIssues = computed(() => {
  let filtered = issues.value

  // 如果是高优先级视图，只显示高优先级问题
  if (viewMode.value === 'high') {
    filtered = filtered.filter(i => i.priority === 'HIGH')
  }

  // 按状态筛选
  if (filterStatus.value) {
    filtered = filtered.filter(i => i.status === filterStatus.value)
  }

  return filtered
})

const getPriorityLabel = (priority) => {
  const map = {
    HIGH: '高',
    MEDIUM: '中',
    LOW: '低',
    NORMAL: '普通'
  }
  return map[priority] || priority || '未知'
}

const getPriorityTagType = (priority) => {
  const map = {
    HIGH: 'danger',
    MEDIUM: 'warning',
    LOW: 'info',
    NORMAL: ''
  }
  return map[priority] || ''
}

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
    const [allRes, highRes] = await Promise.all([
      getAllIssueReports(),
      getHighPriorityIssueReports()
    ])
    issues.value = Array.isArray(allRes) ? allRes : (allRes?.data || [])
    highPriorityIssues.value = Array.isArray(highRes) ? highRes : (highRes?.data || [])
  } catch (e) {
    console.error(e)
    // 如果高优先级接口失败，只加载全部
    try {
      const allRes = await getAllIssueReports()
      issues.value = Array.isArray(allRes) ? allRes : (allRes?.data || [])
    } catch (e2) {
      console.error(e2)
    }
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
    if (action === 'escalate') {
      // ID15: 标记为高优先级
      updateData = { priority: 'HIGH' }
    } else if (action === 'accept') {
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
  flex-wrap: wrap;
  gap: 12px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* ID15: 优先级标签页样式 */
.priority-tabs {
  margin-bottom: 20px;
}

.view-mode-group {
  display: flex;
  gap: 8px;
}

.view-mode-group :deep(.el-radio-button__inner) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 8px;
}

.view-mode-group :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  border-color: #1e3a5f;
}

.high-priority-tab {
  display: flex;
  align-items: center;
  gap: 6px;
}

.high-icon {
  color: #f56c6c;
  font-size: 16px;
}

.tab-badge {
  margin-left: 4px;
}

/* 高优先级面板样式 */
.high-priority-panel {
  margin-top: 16px;
  border: 2px solid #f56c6c;
  background: #fef0f0;
}

.high-priority-panel :deep(.el-card__header) {
  background: #fde2e2;
  color: #c45656;
  font-weight: 700;
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.warning-icon {
  font-size: 20px;
  color: #f56c6c;
}

.high-priority-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.high-priority-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: white;
  border-radius: 8px;
  border-left: 4px solid #f56c6c;
}

.item-main {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.item-id {
  font-weight: 700;
  color: #1e3a5f;
  white-space: nowrap;
}

.item-desc {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #5a7a9a;
  font-size: 14px;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.item-time {
  font-size: 12px;
  color: #94a3b8;
}

/* ============================================
   响应式设计 - 移动端适配
   ============================================ */

/* 平板 (≤900px) */
@media (max-width: 900px) {
  .issues-management {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: stretch;
  }

  .header-actions {
    flex-direction: column;
    width: 100%;
  }

  .page-header .el-select {
    width: 100% !important;
  }

  /* 标签页适配 */
  .priority-tabs {
    overflow-x: auto;
    margin-bottom: 16px;
  }

  .view-mode-group {
    flex-wrap: nowrap;
  }

  /* 表格横向滚动 */
  :deep(.el-table) {
    overflow-x: auto;
    display: block;
    font-size: 13px;
  }

  :deep(.el-table__body-wrapper) {
    overflow-x: auto;
  }

  :deep(.el-table__header th) {
    font-size: 12px;
    padding: 8px 4px;
  }

  :deep(.el-table__body td) {
    padding: 8px 4px;
  }

  /* 高优先级面板适配 */
  .high-priority-panel {
    margin-top: 12px;
  }

  .high-priority-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .item-meta {
    width: 100%;
    justify-content: space-between;
  }
}

/* 手机 (≤600px) */
@media (max-width: 600px) {
  .issues-management {
    padding: 12px;
  }

  .page-header {
    flex-direction: column;
    align-items: stretch;
  }

  :deep(.el-table) {
    font-size: 12px;
  }

  :deep(.el-table__header th) {
    padding: 6px 4px;
    font-size: 11px;
  }

  :deep(.el-table__body td) {
    padding: 8px 4px;
  }

  :deep(.el-button) {
    padding: 6px 8px;
    font-size: 11px;
  }

  :deep(.el-button + .el-button) {
    margin-left: 4px;
  }

  /* 操作按钮下拉菜单适配 */
  :deep(.el-dropdown) {
    margin-top: 4px;
  }
}

/* 超小屏幕 (≤400px) */
@media (max-width: 400px) {
  .page-header h2 {
    font-size: 16px;
  }

  :deep(.el-tag) {
    font-size: 10px;
    padding: 2px 4px;
  }

  :deep(.el-button) {
    padding: 4px 6px;
    font-size: 10px;
  }
}

/* 详情弹窗响应式 */
@media (max-width: 600px) {
  :deep(.el-dialog) {
    max-width: calc(100vw - 32px) !important;
    width: calc(100vw - 32px) !important;
    margin: 16px auto;
  }

  :deep(.el-dialog__header) {
    padding: 16px;
  }

  :deep(.el-dialog__body) {
    padding: 16px;
  }

  :deep(.el-descriptions) {
    font-size: 13px;
  }

  :deep(.el-descriptions__label) {
    width: 80px;
  }

  :deep(.el-descriptions__cell) {
    padding: 8px 10px;
  }
}
</style>
