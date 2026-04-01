<template>
  <div class="profile">
    <el-page-header title="返回" @back="goBack" />
    <h2>个人中心</h2>

    <!-- 用户基本信息卡片 -->
    <el-card class="user-info-card">
      <template #header>
        <div class="card-header">
          <h3>用户信息</h3>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ userInfo.username || '未登录' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userInfo.email || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ userInfo.registerTime || '未知' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-tabs v-model="activeTab" class="profile-tabs">
      <!-- 我的预订 -->
      <el-tab-pane label="我的预订" name="bookings">
        <div v-if="bookingLoading" class="loading">
          <el-skeleton :rows="5" animated />
        </div>
        <el-table v-else :data="bookings" style="width: 100%">
          <el-table-column prop="scooterName" label="滑板车" width="150" />
          <el-table-column label="开始时间" width="180">
            <template #default="{ row }">
              {{ formatTime(row.startTime) }}
            </template>
          </el-table-column>
          <el-table-column label="结束时间" width="180">
            <template #default="{ row }">
              {{ formatTime(row.endTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="totalPrice" label="价格 (¥)" width="100" />
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button
                type="danger"
                size="small"
                :disabled="row.status === 'COMPLETED' || row.status === 'CANCELLED'"
                @click="handleCancel(row.id)"
                :loading="cancellingId === row.id"
              >
                取消
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!bookingLoading && bookings.length === 0" description="暂无预订记录" />
      </el-tab-pane>

      <!-- 故障反馈 -->
      <el-tab-pane label="故障反馈" name="issues">
        <el-button type="primary" @click="showIssueDialog = true">提交新反馈</el-button>
        <el-table :data="issues" style="margin-top: 20px">
          <el-table-column prop="description" label="问题描述" />
          <el-table-column prop="status" label="状态" width="120" />
          <el-table-column prop="submitTime" label="提交时间" width="180" />
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 提交故障对话框 -->
    <el-dialog v-model="showIssueDialog" title="提交故障反馈" width="500px">
      <el-form :model="issueForm">
        <el-form-item label="问题描述">
          <el-input 
            type="textarea" 
            v-model="issueForm.description" 
            rows="5" 
            placeholder="请详细描述遇到的问题（如刹车失灵、无法解锁等）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showIssueDialog = false">取消</el-button>
        <el-button type="primary" @click="submitIssue">提交反馈</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserBookings, cancelBooking } from '@/api/booking'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const activeTab = ref('bookings')

const userInfo = ref({
  username: '',
  email: '',
  registerTime: ''
})

const bookingLoading = ref(false)
const bookings = ref([])
const cancellingId = ref(null)

const issues = ref([])
const showIssueDialog = ref(false)
const issueForm = ref({ description: '' })

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  if (isNaN(date.getTime())) return time
  return date.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

// 状态映射（大写转中文）
const getStatusType = (status) => {
  const upperStatus = (status || '').toUpperCase()
  const map = { 'ACTIVE': 'warning', 'PAID': 'warning', 'COMPLETED': 'success', 'CANCELLED': 'info', 'PENDING': 'info' }
  return map[upperStatus] || 'info'
}

const getStatusText = (status) => {
  const upperStatus = (status || '').toUpperCase()
  const map = { 'ACTIVE': '进行中', 'PAID': '已支付', 'COMPLETED': '已完成', 'CANCELLED': '已取消', 'PENDING': '待支付' }
  return map[upperStatus] || status
}

const goBack = () => router.back()

// 加载用户基本信息（从 localStorage）
const loadUserInfo = () => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    const user = JSON.parse(userStr)
    userInfo.value = {
      username: user.username || user.name,
      email: user.email,
      registerTime: user.registerTime || '2025-01-01'
    }
  }
}

// 加载预订记录
const loadBookings = async () => {
  bookingLoading.value = true
  try {
    const res = await getUserBookings()
    if (Array.isArray(res)) {
      bookings.value = res.map(b => ({
        ...b,
        scooterName: b.scooterName || b.scooterNumber || `滑板车 #${b.scooterId}` || '未知',
        totalPrice: b.totalPrice || b.totalCost || 0
      }))
    } else if (res?.code === 200) {
      bookings.value = (res.data || []).map(b => ({
        ...b,
        scooterName: b.scooterName || b.scooterNumber || `滑板车 #${b.scooterId}` || '未知',
        totalPrice: b.totalPrice || b.totalCost || 0
      }))
    } else {
      bookings.value = []
    }
  } catch (error) {
    console.error('获取预订记录失败:', error)
    ElMessage.error('获取预订记录失败')
    bookings.value = []
  } finally {
    bookingLoading.value = false
  }
}

// 取消预订
const handleCancel = (id) => {
  ElMessageBox.confirm('确定要取消该预订吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    cancellingId.value = id
    try {
      const res = await cancelBooking(id)
      if (res?.code === 200 || res?.success || res === 'Cancel successful') {
        ElMessage.success('取消成功')
        await loadBookings()
      } else {
        ElMessage.error(res?.message || '取消失败')
      }
    } catch (error) {
      ElMessage.error('取消失败，请稍后重试')
    } finally {
      cancellingId.value = null
    }
  })
}

// 提交故障反馈（当前为前端模拟，后续可接真实接口）
const submitIssue = () => {
  if (!issueForm.value.description.trim()) {
    ElMessage.warning('请输入问题描述')
    return
  }

  issues.value.unshift({
    id: Date.now(),
    description: issueForm.value.description,
    status: '待处理',
    submitTime: new Date().toLocaleString('zh-CN')
  })

  ElMessage.success('反馈已提交，工作人员将尽快处理')
  showIssueDialog.value = false
  issueForm.value.description = ''
}

onMounted(() => {
  loadUserInfo()
  loadBookings()

  // 示例故障数据
  issues.value = [
    { id: 1, description: '滑板车刹车失灵', status: '已修复', submitTime: '2026-03-28 14:30' },
    { id: 2, description: '无法正常解锁', status: '处理中', submitTime: '2026-03-30 09:15' }
  ]
})
</script>

<style scoped>
.profile {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}
.user-info-card {
  margin-bottom: 30px;
}
.card-header {
  display: flex;
  align-items: center;
}
.loading {
  padding: 40px;
}
.profile-tabs :deep(.el-tabs__content) {
  padding-top: 20px;
}
</style>