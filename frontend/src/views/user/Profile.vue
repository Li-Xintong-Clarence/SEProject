<template>
  <div class="profile">
    <el-page-header title="返回" @back="goBack" />
    <h2 class="page-title">个人中心</h2>
    <p class="page-lead">预订、支付卡、反馈与统计 · CapyGlide</p>

    <el-row :gutter="16" class="stat-row">
      <el-col :xs="24" :sm="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-label">订单数</div>
          <div class="stat-value">{{ stats.totalBookings ?? '—' }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-label">累计消费 (¥)</div>
          <div class="stat-value">{{ formatMoney(stats.totalCost) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-label">累计租用时长 (小时)</div>
          <div class="stat-value">{{ stats.totalDuration ?? '—' }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="user-info-card">
      <template #header>
        <div class="card-header">
          <h3>用户信息 (ID1)</h3>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ userInfo.username || '—' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userInfo.email || '—' }}</el-descriptions-item>
        <el-descriptions-item label="手机">{{ userInfo.phone || '—' }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ userInfo.role || '—' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatTime(userInfo.createdAt || userInfo.registrationDate) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-tabs v-model="activeTab" class="profile-tabs">
      <el-tab-pane label="我的预订" name="bookings">
        <div v-if="bookingLoading" class="loading">
          <el-skeleton :rows="5" animated />
        </div>
        <el-table v-else :data="bookings" style="width: 100%">
          <el-table-column prop="confirmationCode" label="确认码" width="110" />
          <el-table-column prop="scooterName" label="滑板车" width="130" />
          <el-table-column label="开始" width="160">
            <template #default="{ row }">{{ formatTime(row.startTime) }}</template>
          </el-table-column>
          <el-table-column label="结束" width="160">
            <template #default="{ row }">{{ formatTime(row.endTime) }}</template>
          </el-table-column>
          <el-table-column prop="totalPrice" label="¥" width="80" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="280" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="viewConfirmation(row.id)">确认信息</el-button>
              <el-button
                type="primary"
                link
                size="small"
                :disabled="!canExtend(row)"
                @click="openExtend(row.id)"
              >
                延长
              </el-button>
              <el-button
                type="danger"
                link
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
        <el-empty v-if="!bookingLoading && bookings.length === 0" description="暂无预订" />
      </el-tab-pane>

      <el-tab-pane label="支付卡 (ID2)" name="cards">
        <el-alert type="warning" show-icon :closable="false" class="mb">
          演示环境：请勿使用真实卡号。CVV 仅用于演示，后端列表接口不返回 CVV (ID3)。
        </el-alert>
        <el-button type="primary" @click="showCardDialog = true">添加支付卡</el-button>
        <el-table :data="cards" style="margin-top: 16px">
          <el-table-column prop="cardHolder" label="持卡人" />
          <el-table-column label="卡号">
            <template #default="{ row }">****{{ String(row.cardNumber || '').slice(-4) }}</template>
          </el-table-column>
          <el-table-column prop="expiryDate" label="有效期" width="100" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="danger" link @click="removeCard(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="意见反馈 (ID13)" name="feedback">
        <el-button type="primary" @click="openFeedbackDialog">提交反馈</el-button>
        <el-table :data="feedbacks" style="margin-top: 16px">
          <el-table-column prop="description" label="内容" min-width="200" />
          <el-table-column prop="priority" label="优先级" width="100" />
          <el-table-column prop="status" label="状态" width="110" />
          <el-table-column prop="adminResponse" label="管理员回复" min-width="160" show-overflow-tooltip />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="车辆故障上报" name="issues">
        <el-button type="primary" @click="openIssueDialog">上报故障</el-button>
        <el-table :data="myIssues" style="margin-top: 16px">
          <el-table-column prop="scooterId" label="车辆ID" width="90" />
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="priority" label="优先级" width="100" />
          <el-table-column prop="status" label="状态" width="110" />
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="showCardDialog" title="添加支付卡" width="480px" @closed="resetCardForm">
      <el-form :model="cardForm" label-width="100px">
        <el-form-item label="卡号">
          <el-input v-model="cardForm.cardNumber" maxlength="19" placeholder="演示卡号" />
        </el-form-item>
        <el-form-item label="持卡人">
          <el-input v-model="cardForm.cardHolder" />
        </el-form-item>
        <el-form-item label="有效期">
          <el-input v-model="cardForm.expiryDate" placeholder="MM/YYYY" />
        </el-form-item>
        <el-form-item label="CVV">
          <el-input v-model="cardForm.cvv" maxlength="4" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCardDialog = false">取消</el-button>
        <el-button type="primary" :loading="cardSaving" @click="saveCard">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showFeedbackDialog" title="提交反馈" width="500px">
      <el-form :model="feedbackForm">
        <el-form-item label="内容">
          <el-input v-model="feedbackForm.description" type="textarea" rows="4" />
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

    <el-dialog v-model="showIssueDialog" title="故障上报" width="500px">
      <el-form :model="issueForm">
        <el-form-item label="车辆ID">
          <el-input-number v-model="issueForm.scooterId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="issueForm.description" type="textarea" rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showIssueDialog = false">取消</el-button>
        <el-button type="primary" @click="submitIssue">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="extendVisible" title="延长租期" width="480px">
      <el-radio-group v-model="extendHireOption">
        <el-radio-button value="1hr">1小时</el-radio-button>
        <el-radio-button value="4hr">4小时</el-radio-button>
        <el-radio-button value="1day">1天</el-radio-button>
        <el-radio-button value="1week">1周</el-radio-button>
      </el-radio-group>
      <template #footer>
        <el-button @click="extendVisible = false">取消</el-button>
        <el-button type="primary" :loading="extendLoading" @click="submitExtend">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="confirmVisible" title="预订确认信息 (ID8)" width="520px">
      <el-descriptions v-if="confirmation" :column="1" border>
        <el-descriptions-item label="确认码">{{ confirmation.confirmationCode }}</el-descriptions-item>
        <el-descriptions-item label="租期">{{ confirmation.hireOption }}</el-descriptions-item>
        <el-descriptions-item label="开始">{{ formatTime(confirmation.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束">{{ formatTime(confirmation.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="费用">¥{{ confirmation.totalCost }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusText(confirmation.status) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserBookings, cancelBooking, extendBooking, getBookingConfirmation } from '@/api/booking'
import { getCurrentUser, getUserStats } from '@/api/user'
import { getMyCards, addCard, deleteCard } from '@/api/card'
import { getMyFeedbacks, createFeedback } from '@/api/feedback'
import { createIssueReport, getMyIssueReports } from '@/api/issues'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const activeTab = ref('bookings')

const userInfo = ref({})
const stats = ref({})
const bookingLoading = ref(false)
const bookings = ref([])
const cancellingId = ref(null)

const cards = ref([])
const showCardDialog = ref(false)
const cardSaving = ref(false)
const cardForm = ref({ cardNumber: '', cardHolder: '', expiryDate: '', cvv: '' })

const feedbacks = ref([])
const showFeedbackDialog = ref(false)
const feedbackForm = ref({ description: '', priority: 'LOW' })

const myIssues = ref([])
const showIssueDialog = ref(false)
const issueForm = ref({ scooterId: null, description: '' })

const extendVisible = ref(false)
const extendBookingId = ref(null)
const extendHireOption = ref('1hr')
const extendLoading = ref(false)

const confirmVisible = ref(false)
const confirmation = ref(null)

const formatMoney = (v) => (v == null || v === '' ? '—' : Number(v).toFixed(2))

const formatTime = (time) => {
  if (!time) return '—'
  const date = new Date(time)
  if (isNaN(date.getTime())) return String(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
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

const goBack = () => router.back()

const loadMe = async () => {
  try {
    console.log('开始加载用户信息...')
    const res = await getCurrentUser()
    console.log('getCurrentUser 返回:', res)
    if (res) {
      userInfo.value = res
      localStorage.setItem('user', JSON.stringify({ ...JSON.parse(localStorage.getItem('user') || '{}'), ...res }))
      console.log('用户信息加载成功:', res)
    } else {
      console.warn('getCurrentUser 返回空数据')
      ElMessage.error('获取用户信息失败：user not found')
    }
  } catch (e) {
    console.error('获取用户信息异常:', e)
    ElMessage.error('获取用户信息失败，请重新登录')
  }
}

const loadStats = async () => {
  try {
    stats.value = (await getUserStats()) || {}
  } catch {
    stats.value = {}
  }
}

const loadBookings = async () => {
  bookingLoading.value = true
  try {
    const res = await getUserBookings()
    const list = Array.isArray(res) ? res : []
    bookings.value = list.map((b) => ({
      ...b,
      scooterName: b.scooterName || b.scooterNumber || `车 #${b.scooterId}`,
      totalPrice: b.totalPrice ?? b.totalCost ?? 0
    }))
  } catch {
    bookings.value = []
    ElMessage.error('获取预订失败')
  } finally {
    bookingLoading.value = false
  }
}

const loadCards = async () => {
  try {
    cards.value = (await getMyCards()) || []
  } catch {
    cards.value = []
  }
}

const loadFeedbacks = async () => {
  try {
    feedbacks.value = (await getMyFeedbacks()) || []
  } catch {
    feedbacks.value = []
  }
}

const loadIssues = async () => {
  try {
    myIssues.value = (await getMyIssueReports()) || []
  } catch {
    myIssues.value = []
  }
}

const handleCancel = (id) => {
  ElMessageBox.confirm('确定取消该预订？', '提示', { type: 'warning' }).then(async () => {
    cancellingId.value = id
    try {
      await cancelBooking(id)
      ElMessage.success('已取消')
      // 手动更新本地订单状态，避免依赖后端返回
      const booking = bookings.value.find(b => b.id === id)
      if (booking) {
        booking.status = 'CANCELLED'
      }
    } catch (e) {
      // 如果是取消成功的错误（后端已处理但返回格式不符合标准），仍显示成功
      if (e.message?.includes('成功') || e.message?.includes('cancel')) {
        ElMessage.success('已取消')
        const booking = bookings.value.find(b => b.id === id)
        if (booking) booking.status = 'CANCELLED'
      }
      // 否则让拦截器的错误提示显示
    } finally {
      cancellingId.value = null
    }
  })
}

const canExtend = (row) => {
  const u = (row?.status || '').toUpperCase()
  return u === 'ACTIVE' || u === 'PAID'
}

const openExtend = (id) => {
  extendBookingId.value = id
  extendHireOption.value = '1hr'
  extendVisible.value = true
}

const submitExtend = async () => {
  if (!extendBookingId.value) return
  extendLoading.value = true
  try {
    await extendBooking(extendBookingId.value, extendHireOption.value)
    ElMessage.success('已延长')
    extendVisible.value = false
    await loadBookings()
  } catch {
    /* */
  } finally {
    extendLoading.value = false
    extendBookingId.value = null
  }
}

const viewConfirmation = async (id) => {
  try {
    confirmation.value = await getBookingConfirmation(id)
    confirmVisible.value = true
  } catch {
    ElMessage.error('获取确认信息失败')
  }
}

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
    ElMessage.success('已保存')
    showCardDialog.value = false
    await loadCards()
  } catch {
    /* */
  } finally {
    cardSaving.value = false
  }
}

const removeCard = (id) => {
  ElMessageBox.confirm('删除该卡？', '提示', { type: 'warning' }).then(async () => {
    try {
      await deleteCard(id)
      ElMessage.success('已删除')
      await loadCards()
    } catch {
      /* */
    }
  })
}

const openFeedbackDialog = () => {
  feedbackForm.value = { description: '', priority: 'LOW' }
  showFeedbackDialog.value = true
}

const submitFeedback = async () => {
  if (!feedbackForm.value.description?.trim()) {
    ElMessage.warning('请输入内容')
    return
  }
  try {
    await createFeedback({
      description: feedbackForm.value.description,
      priority: feedbackForm.value.priority,
      status: 'OPEN'
    })
    ElMessage.success('已提交')
    showFeedbackDialog.value = false
    await loadFeedbacks()
  } catch {
    /* */
  }
}

const openIssueDialog = () => {
  issueForm.value = { scooterId: null, description: '' }
  showIssueDialog.value = true
}

const submitIssue = async () => {
  if (!issueForm.value.scooterId || !issueForm.value.description?.trim()) {
    ElMessage.warning('请填写车辆ID与描述')
    return
  }
  try {
    await createIssueReport({
      scooterId: issueForm.value.scooterId,
      description: issueForm.value.description
    })
    ElMessage.success('已上报')
    showIssueDialog.value = false
    await loadIssues()
  } catch {
    /* */
  }
}

onMounted(async () => {
  await loadMe()
  await loadStats()
  await loadBookings()
  await loadCards()
  await loadFeedbacks()
  await loadIssues()
})
</script>

<style scoped>
.profile {
  padding: 20px;
  max-width: 1100px;
  margin: 0 auto;
}
.page-title {
  margin: 0 0 6px;
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--cg-navy);
}
.page-lead {
  margin: 0 0 16px;
  font-size: 0.9rem;
  color: #6b7280;
}
.stat-row {
  margin-bottom: 16px;
}
.stat-card {
  border-radius: var(--cg-radius-md);
  margin-bottom: 12px;
}
.stat-label {
  font-size: 13px;
  color: #6b7280;
}
.stat-value {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--cg-navy);
  margin-top: 4px;
}
.user-info-card {
  margin-bottom: 20px;
}
.card-header h3 {
  margin: 0;
  font-size: 1rem;
}
.loading {
  padding: 40px;
}
.profile-tabs :deep(.el-tabs__content) {
  padding-top: 16px;
}
.mb {
  margin-bottom: 12px;
}
</style>
