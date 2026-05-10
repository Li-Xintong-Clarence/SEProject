<template>
  <div class="profile">
    <el-page-header title="返回" @back="goBack" />
    <h2 class="page-title">个人中心</h2>
    <p class="page-sub">CapyGlide · 预订、支付卡、反馈与统计</p>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="24" :sm="8">
        <div class="stat-card">
          <div class="stat-icon"><el-icon><Ticket /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalBookings ?? '—' }}</div>
            <div class="stat-label">订单数</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="8">
        <div class="stat-card">
          <div class="stat-icon accent"><el-icon><Money /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">¥{{ formatMoney(stats.totalCost) }}</div>
            <div class="stat-label">累计消费</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="8">
        <div class="stat-card">
          <div class="stat-icon"><el-icon><Clock /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalDuration ?? '—' }}</div>
            <div class="stat-label">租用时长(小时)</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 用户信息卡片 -->
    <el-card class="user-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span><el-icon><User /></el-icon> 用户信息</span>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ userInfo.username || '—' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userInfo.email || '—' }}</el-descriptions-item>
        <el-descriptions-item label="手机">{{ userInfo.phone || '—' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatTime(userInfo.createdAt) }}</el-descriptions-item>
      </el-descriptions>

      <!-- 当前进行中的订单提示 -->
      <el-alert
        v-if="activeBooking"
        type="warning"
        :closable="false"
        class="mb active-booking-alert"
      >
        <template #title>
          您有正在进行的行程
          <el-button type="warning" size="small" @click="goToTrip" style="margin-left: 12px;">
            前往当前行程
          </el-button>
        </template>
      </el-alert>
    </el-card>

    <!-- 功能标签页 -->
    <el-tabs v-model="activeTab" class="profile-tabs">
      <!-- 我的预订 -->
      <el-tab-pane label="我的预订" name="bookings">
        <div v-if="bookingLoading" class="loading">
          <el-skeleton :rows="5" animated />
        </div>
        <el-table v-else :data="bookings" stripe>
          <el-table-column prop="confirmationCode" label="确认码" width="120" />
          <el-table-column prop="scooterName" label="滑板车" width="130" />
          <el-table-column label="开始时间" width="160">
            <template #default="{ row }">{{ formatTime(row.startTime) }}</template>
          </el-table-column>
          <el-table-column label="结束时间" width="160">
            <template #default="{ row }">{{ formatTime(row.endTime) }}</template>
          </el-table-column>
          <el-table-column prop="totalPrice" label="金额" width="90">
            <template #default="{ row }">¥{{ row.totalPrice ?? row.totalCost ?? 0 }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="viewConfirmation(row.id)">确认信息</el-button>
              <el-button
                type="primary"
                link
                size="small"
                :disabled="!canExtend(row)"
                @click="openExtend(row.id)"
              >延长</el-button>
              <el-button
                type="danger"
                link
                size="small"
                :disabled="row.status === 'COMPLETED' || row.status === 'CANCELLED'"
                @click="handleCancel(row.id)"
                :loading="cancellingId === row.id"
              >取消</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!bookingLoading && bookings.length === 0" description="暂无预订记录" />
      </el-tab-pane>

      <!-- 支付卡 -->
      <el-tab-pane label="支付卡" name="cards">
        <el-alert type="warning" :closable="false" class="mb">
          <template #title>演示环境：请勿使用真实卡号</template>
        </el-alert>
        <el-button type="primary" @click="showCardDialog = true">添加支付卡</el-button>
        <el-table :data="cards" stripe style="margin-top: 16px">
          <el-table-column prop="cardHolder" label="持卡人" />
          <el-table-column label="卡号">
            <template #default="{ row }">****{{ String(row.cardNumber || '').slice(-4) }}</template>
          </el-table-column>
          <el-table-column prop="expiryDate" label="有效期" width="110" />
          <el-table-column label="操作" width="80">
            <template #default="{ row }">
              <el-button type="danger" link @click="removeCard(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 意见反馈 -->
      <el-tab-pane label="意见反馈" name="feedback">
        <el-button type="primary" @click="openFeedbackDialog">提交反馈</el-button>
        <el-table :data="feedbacks" stripe style="margin-top: 16px">
          <el-table-column prop="description" label="内容" min-width="180" show-overflow-tooltip />
          <el-table-column prop="priority" label="优先级" width="90">
            <template #default="{ row }">
              <el-tag :type="getPriorityType(row.priority)" size="small">{{ row.priority }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" />
          <el-table-column prop="adminResponse" label="回复" min-width="150" show-overflow-tooltip />
        </el-table>
      </el-tab-pane>

      <!-- 故障上报 -->
      <el-tab-pane label="故障上报" name="issues">
        <el-button type="primary" @click="openIssueDialog">上报故障</el-button>
        <el-table :data="myIssues" stripe style="margin-top: 16px">
          <el-table-column prop="scooterId" label="车辆ID" width="90" />
          <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
          <el-table-column prop="priority" label="优先级" width="90">
            <template #default="{ row }">
              <el-tag :type="getPriorityType(row.priority)" size="small">{{ row.priority }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 添加支付卡弹窗 -->
    <el-dialog v-model="showCardDialog" title="添加支付卡" width="480px" @closed="resetCardForm">
      <el-form :model="cardForm" label-width="90px">
        <el-form-item label="卡号">
          <el-input v-model="cardForm.cardNumber" placeholder="卡号" maxlength="19" />
        </el-form-item>
        <el-form-item label="持卡人">
          <el-input v-model="cardForm.cardHolder" placeholder="持卡人姓名" />
        </el-form-item>
        <el-form-item label="有效期">
          <el-input v-model="cardForm.expiryDate" placeholder="MM/YYYY" />
        </el-form-item>
        <el-form-item label="CVV">
          <el-input v-model="cardForm.cvv" placeholder="CVV" maxlength="4" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCardDialog = false">取消</el-button>
        <el-button type="primary" :loading="cardSaving" @click="saveCard">保存</el-button>
      </template>
    </el-dialog>

    <!-- 提交反馈弹窗 -->
    <el-dialog v-model="showFeedbackDialog" title="提交反馈" width="500px">
      <el-form :model="feedbackForm" label-width="80px">
        <el-form-item label="内容">
          <el-input v-model="feedbackForm.description" type="textarea" :rows="4" placeholder="请输入反馈内容" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="feedbackForm.priority" style="width: 100%">
            <el-option label="低" value="LOW" />
            <el-option label="中" value="MEDIUM" />
            <el-option label="高" value="HIGH" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showFeedbackDialog = false">取消</el-button>
        <el-button type="primary" @click="submitFeedback">提交</el-button>
      </template>
    </el-dialog>

    <!-- 故障上报弹窗 -->
    <el-dialog v-model="showIssueDialog" title="故障上报" width="500px">
      <el-form :model="issueForm" label-width="90px">
        <el-form-item label="车辆ID">
          <el-input-number v-model="issueForm.scooterId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="问题描述">
          <el-input v-model="issueForm.description" type="textarea" :rows="4" placeholder="请描述故障情况" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showIssueDialog = false">取消</el-button>
        <el-button type="primary" @click="submitIssue">提交</el-button>
      </template>
    </el-dialog>

    <!-- 延长租期弹窗 -->
    <el-dialog v-model="extendVisible" title="延长租期" width="480px">
      <el-radio-group v-model="extendHireOption" style="display: flex; flex-direction: column; gap: 12px;">
        <el-radio-button value="1hr">1 小时</el-radio-button>
        <el-radio-button value="4hr">4 小时</el-radio-button>
        <el-radio-button value="1day">1 天</el-radio-button>
        <el-radio-button value="1week">1 周</el-radio-button>
      </el-radio-group>
      <template #footer>
        <el-button @click="extendVisible = false">取消</el-button>
        <el-button type="primary" :loading="extendLoading" @click="submitExtend">确认延长</el-button>
      </template>
    </el-dialog>

    <!-- 预订确认信息弹窗 -->
    <el-dialog v-model="confirmVisible" title="预订确认信息" width="520px">
      <el-descriptions v-if="confirmation" :column="1" border>
        <el-descriptions-item label="确认码">{{ confirmation.confirmationCode }}</el-descriptions-item>
        <el-descriptions-item label="租期">{{ confirmation.hireOption }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ formatTime(confirmation.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ formatTime(confirmation.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="总费用">¥{{ confirmation.totalCost }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusText(confirmation.status) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Ticket, Money, Clock, User } from '@element-plus/icons-vue'
import { getUserBookings, cancelBooking, extendBooking, getBookingConfirmation, returnScooter } from '@/api/booking'
import { getCurrentUser, getUserStats } from '@/api/user'
import { getMyCards, addCard, deleteCard } from '@/api/card'
import { getMyFeedbacks, createFeedback } from '@/api/feedback'
import { createIssueReport, getMyIssueReports } from '@/api/issues'

const router = useRouter()
const activeTab = ref('bookings')

// 用户信息
const userInfo = ref({})
const stats = ref({})

// 当前进行中的订单（用于提示）
const activeBooking = ref(null)

// 预订
const bookingLoading = ref(false)
const bookings = ref([])
const cancellingId = ref(null)

// 支付卡
const cards = ref([])
const showCardDialog = ref(false)
const cardSaving = ref(false)
const cardForm = ref({ cardNumber: '', cardHolder: '', expiryDate: '', cvv: '' })

// 反馈
const feedbacks = ref([])
const showFeedbackDialog = ref(false)
const feedbackForm = ref({ description: '', priority: 'LOW' })

// 故障
const myIssues = ref([])
const showIssueDialog = ref(false)
const issueForm = ref({ scooterId: null, description: '' })

// 延长
const extendVisible = ref(false)
const extendBookingId = ref(null)
const extendHireOption = ref('1hr')
const extendLoading = ref(false)

// 确认
const confirmVisible = ref(false)
const confirmation = ref(null)

// 工具函数
const formatMoney = (v) => (v == null || v === '' ? '0.00' : Number(v).toFixed(2))

const formatTime = (time) => {
  if (!time) return '—'
  const date = new Date(time)
  if (isNaN(date.getTime())) return String(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

const getStatusType = (status) => {
  const u = (status || '').toUpperCase()
  const map = { ACTIVE: 'warning', PAID: 'warning', COMPLETED: 'success', CANCELLED: 'info', PENDING: 'info' }
  return map[u] || 'info'
}

const getStatusText = (status) => {
  const u = (status || '').toUpperCase()
  const map = { ACTIVE: '进行中', PAID: '已支付', COMPLETED: '已完成', CANCELLED: '已取消', PENDING: '待支付' }
  return map[u] || status
}

const getPriorityType = (priority) => {
  const map = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info' }
  return map[priority] || 'info'
}

const goBack = () => router.back()

// 加载用户信息
const loadMe = async () => {
  try {
    const u = await getCurrentUser()
    if (u) {
      userInfo.value = u
      localStorage.setItem('user', JSON.stringify({ ...JSON.parse(localStorage.getItem('user') || '{}'), ...u }))
    }
  } catch (e) {
    // 静默失败
  }
}

// 加载统计
const loadStats = async () => {
  try {
    stats.value = (await getUserStats()) || {}
  } catch (e) {
    stats.value = {}
  }
}

// 加载预订
const loadBookings = async () => {
  bookingLoading.value = true
  try {
    const res = await getUserBookings()
    const list = Array.isArray(res) ? res : []
    bookings.value = list.map(b => ({
      ...b,
      scooterName: b.scooterName || b.scooterNumber || `车 #${b.scooterId}`,
      totalPrice: b.totalPrice ?? b.totalCost ?? 0
    }))

    // 查找当前进行中的订单
    activeBooking.value = bookings.value.find(b => {
      const s = (b.status || '').toUpperCase()
      return s === 'ACTIVE' || s === 'PAID'
    }) || null
  } catch (e) {
    bookings.value = []
    activeBooking.value = null
  } finally {
    bookingLoading.value = false
  }
}

// 前往当前行程
const goToTrip = () => {
  router.push('/trip')
}

// 加载支付卡
const loadCards = async () => {
  try {
    cards.value = (await getMyCards()) || []
  } catch (e) {
    cards.value = []
  }
}

// 加载反馈
const loadFeedbacks = async () => {
  try {
    feedbacks.value = (await getMyFeedbacks()) || []
  } catch (e) {
    feedbacks.value = []
  }
}

// 加载故障
const loadIssues = async () => {
  try {
    myIssues.value = (await getMyIssueReports()) || []
  } catch (e) {
    myIssues.value = []
  }
}

// 取消预订
const handleCancel = (id) => {
  ElMessageBox.confirm('确定取消该预订？', '提示', { type: 'warning' }).then(async () => {
    cancellingId.value = id
    try {
      await cancelBooking(id)
      ElMessage.success('已取消')
      // 乐观更新本地状态
      const booking = bookings.value.find(b => b.id === id)
      if (booking) booking.status = 'CANCELLED'
    } catch (e) {
      // 后端可能返回成功但格式不标准
      if (e.message?.includes('成功') || e.message?.includes('cancel')) {
        ElMessage.success('已取消')
        const booking = bookings.value.find(b => b.id === id)
        if (booking) booking.status = 'CANCELLED'
      }
    } finally {
      cancellingId.value = null
    }
  })
}

// 能否延长
const canExtend = (row) => {
  const u = (row?.status || '').toUpperCase()
  return u === 'ACTIVE' || u === 'PAID'
}

// 打开延长弹窗
const openExtend = (id) => {
  extendBookingId.value = id
  extendHireOption.value = '1hr'
  extendVisible.value = true
}

// 提交延长
const submitExtend = async () => {
  if (!extendBookingId.value) return
  extendLoading.value = true
  try {
    const res = await extendBooking(extendBookingId.value, extendHireOption.value)
    ElMessage.success('租期已延长')
    extendVisible.value = false
    await loadBookings()
  } catch (e) {
    // 显示真实错误信息
    const msg = e?.response?.data?.message || e?.message || '延长失败，请稍后重试'
    ElMessage.error(msg)
  } finally {
    extendLoading.value = false
    extendBookingId.value = null
  }
}

// 查看确认信息
const viewConfirmation = async (id) => {
  try {
    confirmation.value = await getBookingConfirmation(id)
    confirmVisible.value = true
  } catch (e) {
    ElMessage.error('获取确认信息失败')
  }
}

// 保存支付卡
const resetCardForm = () => {
  cardForm.value = { cardNumber: '', cardHolder: '', expiryDate: '', cvv: '' }
}

const saveCard = async () => {
  cardSaving.value = true
  try {
    await addCard({
      cardNumber: cardForm.value.cardNumber,
      cardHolder: cardForm.value.cardHolder,
      expiryDate: cardForm.value.expiryDate,
      cvv: cardForm.value.cvv,
      isDefault: true
    })
    ElMessage.success('支付卡已保存')
    showCardDialog.value = false
    await loadCards()
  } catch (e) {
    // 静默失败
  } finally {
    cardSaving.value = false
  }
}

// 删除支付卡
const removeCard = (id) => {
  ElMessageBox.confirm('确定删除该支付卡？', '提示', { type: 'warning' }).then(async () => {
    try {
      await deleteCard(id)
      ElMessage.success('已删除')
      await loadCards()
    } catch (e) {
      // 静默失败
    }
  })
}

// 打开反馈弹窗
const openFeedbackDialog = () => {
  feedbackForm.value = { description: '', priority: 'LOW' }
  showFeedbackDialog.value = true
}

// 提交反馈
const submitFeedback = async () => {
  if (!feedbackForm.value.description?.trim()) {
    ElMessage.warning('请输入反馈内容')
    return
  }
  try {
    await createFeedback({
      description: feedbackForm.value.description,
      priority: feedbackForm.value.priority,
      status: 'OPEN'
    })
    ElMessage.success('反馈已提交')
    showFeedbackDialog.value = false
    await loadFeedbacks()
  } catch (e) {
    // 静默失败
  }
}

// 打开故障弹窗
const openIssueDialog = () => {
  issueForm.value = { scooterId: null, description: '' }
  showIssueDialog.value = true
}

// 提交故障
const submitIssue = async () => {
  if (!issueForm.value.scooterId || !issueForm.value.description?.trim()) {
    ElMessage.warning('请填写车辆ID和问题描述')
    return
  }
  try {
    await createIssueReport({
      scooterId: issueForm.value.scooterId,
      description: issueForm.value.description
    })
    ElMessage.success('故障已上报')
    showIssueDialog.value = false
    await loadIssues()
  } catch (e) {
    // 静默失败
  }
}

// 初始化
onMounted(async () => {
  await Promise.all([
    loadMe(),
    loadStats(),
    loadBookings(),
    loadCards(),
    loadFeedbacks(),
    loadIssues()
  ])
})
</script>

<style scoped>
.profile {
  padding: 32px 24px;
  max-width: 1100px;
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

/* 统计卡片 */
.stat-row {
  margin-bottom: 24px;
}

.stat-card {
  background: var(--cg-white);
  border-radius: var(--cg-radius-lg);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: var(--cg-shadow);
  border: 1px solid var(--cg-border-light);
  transition: var(--cg-transition);
  margin-bottom: 16px;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--cg-shadow-md);
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: var(--cg-radius-md);
  background: var(--cg-accent-soft);
  color: var(--cg-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.accent {
  background: var(--cg-warning-bg);
  color: var(--cg-warning);
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--cg-text);
  letter-spacing: -0.02em;
}

.stat-label {
  font-size: 13px;
  color: var(--cg-text-light);
  font-weight: 500;
}

/* 用户卡片 */
.user-card {
  margin-bottom: 24px;
  border-radius: var(--cg-radius-lg);
}

.card-header {
  font-weight: 700;
  color: var(--cg-text);
  display: flex;
  align-items: center;
  gap: 6px;
}

.active-booking-alert {
  margin-top: 16px;
}

.active-booking-alert :deep(.el-alert__title) {
  color: var(--cg-text);
  display: flex;
  align-items: center;
  font-weight: 600;
}

/* 标签页 */
.profile-tabs {
  background: var(--cg-white);
  border-radius: var(--cg-radius-xl);
  padding: 24px;
  box-shadow: var(--cg-shadow-md);
  border: 1px solid var(--cg-border-light);
}

.loading {
  padding: 40px;
}

.mb {
  margin-bottom: 16px;
}
</style>
