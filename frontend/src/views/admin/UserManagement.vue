<template>
  <div class="user-management">
    <h2 class="page-title">用户管理</h2>
    <p class="page-sub">CapyGlide · 管理所有用户信息</p>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <el-button type="primary" @click="loadUsers" :loading="loading">
            <el-icon><Refresh /></el-icon> 刷新
          </el-button>
        </div>
      </template>

      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机" width="150" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'success'" size="small">
              {{ row.role === 'ADMIN' ? '管理员' : '用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewUserDetail(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 用户详情弹窗 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="600px">
      <el-descriptions v-if="selectedUser" :column="2" border>
        <el-descriptions-item label="ID">{{ selectedUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ selectedUser.username }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ selectedUser.email }}</el-descriptions-item>
        <el-descriptions-item label="手机">{{ selectedUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="selectedUser.role === 'ADMIN' ? 'danger' : 'success'" size="small">
            {{ selectedUser.role === 'ADMIN' ? '管理员' : '用户' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">
          {{ formatTime(selectedUser.createdAt) }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 用户统计 -->
      <el-divider>用户统计</el-divider>
      <el-row :gutter="12" v-if="userStats">
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-value">{{ userStats.totalBookings || 0 }}</div>
            <div class="stat-label">订单数</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-value">¥{{ formatMoney(userStats.totalCost) }}</div>
            <div class="stat-label">累计消费</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-value">{{ userStats.totalDuration || 0 }}h</div>
            <div class="stat-label">租用时长</div>
          </div>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { listUsers, getUserStats } from '@/api/user'

const users = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const selectedUser = ref(null)
const userStats = ref(null)

const formatTime = (time) => {
  if (!time) return '—'
  const date = new Date(time)
  if (isNaN(date.getTime())) return String(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

const formatMoney = (v) => (v == null || v === '' ? '0.00' : Number(v).toFixed(2))

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await listUsers()
    users.value = Array.isArray(res) ? res : []
  } catch (e) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const viewUserDetail = async (row) => {
  selectedUser.value = row
  detailVisible.value = true
  try {
    const res = await getUserStats(row.id)
    userStats.value = res || {}
  } catch {
    userStats.value = {}
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-management {
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

.stat-item {
  background: var(--cg-bg);
  border-radius: var(--cg-radius-md);
  padding: 16px;
  text-align: center;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--cg-navy);
}

.stat-label {
  font-size: 13px;
  color: var(--cg-text-light);
  margin-top: 4px;
}
</style>
