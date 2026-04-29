<template>
  <div class="user-management cg-page">
    <div class="page-header">
      <h2 class="cg-title">用户管理</h2>
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户名/邮箱"
        clearable
        style="width: 300px"
        @input="handleSearch"
      >
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>

    <el-skeleton v-if="loading" :rows="8" animated />

    <template v-else>
      <el-card class="stats-card" shadow="never">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ stats.activeUsers }}</div>
              <div class="stat-label">活跃用户</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ stats.newUsersToday }}</div>
              <div class="stat-label">今日新增</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ stats.totalBookings }}</div>
              <div class="stat-label">总订单数</div>
            </div>
          </el-col>
        </el-row>
      </el-card>

      <el-card shadow="never">
        <el-table :data="filteredUsers" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" min-width="120" />
          <el-table-column prop="email" label="邮箱" min-width="180" />
          <el-table-column prop="phone" label="手机号" min-width="120" />
          <el-table-column label="用户类型" width="120">
            <template #default="{ row }">
              <el-tag v-if="row.userType === 'STUDENT'" type="warning">学生</el-tag>
              <el-tag v-else-if="row.userType === 'SENIOR'" type="info">长者</el-tag>
              <el-tag v-else type="success">普通</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="totalDuration" label="累计时长(h)" width="120">
            <template #default="{ row }">
              {{ Number(row.totalDuration || 0).toFixed(1) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'">
                {{ row.status === 'ACTIVE' ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="注册时间" width="160">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="viewUserDetail(row)">详情</el-button>
              <el-button
                size="small"
                :type="row.status === 'ACTIVE' ? 'danger' : 'success'"
                @click="toggleUserStatus(row)"
              >
                {{ row.status === 'ACTIVE' ? '禁用' : '启用' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="totalUsers"
          layout="total, prev, pager, next"
          class="pagination"
        />
      </el-card>
    </template>

    <el-dialog v-model="detailVisible" title="用户详情" width="600px">
      <el-descriptions v-if="selectedUser" :column="2" border>
        <el-descriptions-item label="用户ID">{{ selectedUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ selectedUser.username }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ selectedUser.email }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ selectedUser.phone || '—' }}</el-descriptions-item>
        <el-descriptions-item label="用户类型">{{ getUserTypeLabel(selectedUser.userType) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ selectedUser.status === 'ACTIVE' ? '正常' : '禁用' }}</el-descriptions-item>
        <el-descriptions-item label="累计租用时长">{{ selectedUser.totalDuration || 0 }} 小时</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatDate(selectedUser.createdAt) }}</el-descriptions-item>
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
import { Search } from '@element-plus/icons-vue'
import { getAllUsers, updateUserStatus } from '@/api/user'

const loading = ref(true)
const users = ref([])
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalUsers = ref(0)
const detailVisible = ref(false)
const selectedUser = ref(null)

const stats = ref({
  totalUsers: 0,
  activeUsers: 0,
  newUsersToday: 0,
  totalBookings: 0
})

const filteredUsers = computed(() => {
  if (!searchKeyword.value) return users.value
  const keyword = searchKeyword.value.toLowerCase()
  return users.value.filter(u =>
    u.username?.toLowerCase().includes(keyword) ||
    u.email?.toLowerCase().includes(keyword)
  )
})

const formatDate = (date) => {
  if (!date) return '—'
  return new Date(date).toLocaleString('zh-CN')
}

const getUserTypeLabel = (type) => {
  const map = { STUDENT: '学生', SENIOR: '长者', null: '普通' }
  return map[type] || '普通'
}

const handleSearch = () => {
  currentPage.value = 1
}

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getAllUsers()
    users.value = Array.isArray(res) ? res : (res?.data || [])
    totalUsers.value = users.value.length
    stats.value.totalUsers = users.value.length
    stats.value.activeUsers = users.value.filter(u => u.status === 'ACTIVE').length
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const viewUserDetail = (user) => {
  selectedUser.value = user
  detailVisible.value = true
}

const toggleUserStatus = async (user) => {
  const newStatus = user.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
  try {
    await updateUserStatus(user.id, newStatus)
    user.status = newStatus
    ElMessage.success(`用户已${newStatus === 'ACTIVE' ? '启用' : '禁用'}`)
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.stats-card {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 16px;
}

.stat-value {
  font-size: 2rem;
  font-weight: 800;
  color: var(--cg-navy);
}

.stat-label {
  font-size: 14px;
  color: var(--cg-text-light);
  margin-top: 4px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
