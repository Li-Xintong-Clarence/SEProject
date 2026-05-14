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

    <!-- 频繁用户提示 -->
    <div v-if="stats.isFrequentUser" class="frequent-user-banner">
      <div class="frequent-user-icon">
        <el-icon><Star /></el-icon>
      </div>
      <div class="frequent-user-content">
        <h4>恭喜！您是频繁用户</h4>
        <p>本周已使用 <strong>{{ stats.weeklyHours }} 小时</strong>，已自动获得 <strong>20% off</strong> 折扣</p>
      </div>
    </div>
    <div v-else-if="stats.weeklyHours > 0" class="frequent-progress">
      <div class="progress-header">
        <span class="progress-label">距离频繁用户还差</span>
        <span class="progress-value">{{ 8 - stats.weeklyHours }} 小时</span>
      </div>
      <el-progress :percentage="Math.min(100, (stats.weeklyHours / 8) * 100)" :stroke-width="10" :show-text="false" color="#f59e0b" />
      <p class="progress-hint">本周使用满 8 小时即可获得 20% off 折扣</p>
    </div>

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
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <!-- 待支付状态：显示去支付按钮 -->
              <el-button
                v-if="(row.status || '').toUpperCase() === 'PENDING'"
                type="success"
                link
                size="small"
                @click="goToPay(row)"
              >
                <el-icon><Wallet /></el-icon> 去支付
              </el-button>
              <el-button type="primary" link size="small" @click="viewConfirmation(row.id)">确认信息</el-button>
              <el-button
                type="primary"
                link
                size="small"
                :disabled="!canExtend(row)"
                @click="openExtend(row.id)"
              >
                {{ getExtendText(row.status) }}
              </el-button>
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
        <el-alert type="info" :closable="false" class="mb">
          <template #title>
            <el-icon><InfoFilled /></el-icon>
            安全提示：我们仅存储卡号后4位，无法获取完整卡号信息
          </template>
        </el-alert>
        <el-button type="primary" @click="showCardDialog = true">
          <el-icon><Plus /></el-icon> 添加支付卡
        </el-button>
        <el-table :data="cards" stripe style="margin-top: 16px" v-if="cards.length > 0">
          <el-table-column label="卡号" min-width="200">
            <template #default="{ row }">
              <div class="card-display">
                <span class="card-type-badge">{{ row.cardType || 'UNKNOWN' }}</span>
                <span class="card-number">{{ row.lastFour ? '**** **** **** ' + row.lastFour : '****' }}</span>
                <el-tag v-if="row.isDefault" type="success" size="small">默认</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="cardHolder" label="持卡人" width="120" />
          <el-table-column prop="expiryDate" label="有效期" width="110" />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button
                v-if="!row.isDefault"
                type="primary"
                link
                size="small"
                @click="setDefaultCard(row.id)"
              >设为默认</el-button>
              <el-button type="danger" link size="small" @click="removeCard(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="暂无支付卡" />
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
          <el-input
            v-model="cardForm.cardNumber"
            placeholder="请输入卡号"
            maxlength="19"
            @input="formatCardNumber"
          />
          <div class="form-hint">我们只存储卡号后4位用于识别</div>
        </el-form-item>
        <el-form-item label="持卡人">
          <el-input v-model="cardForm.cardHolder" placeholder="请输入持卡人姓名" />
        </el-form-item>
        <el-form-item label="有效期">
          <el-input v-model="cardForm.expiryDate" placeholder="MM/YYYY" maxlength="7" />
        </el-form-item>
        <el-form-item label="CVV">
          <el-input
            v-model="cardForm.cvv"
            placeholder="卡背面3位安全码"
            maxlength="4"
            show-password
          />
          <div class="form-hint warning">CVV仅用于验证，不会被存储</div>
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
        <el-descriptions-item label="租期">{{ formatDuration(confirmation) }}</el-descriptions-item>
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
import { Ticket, Money, Clock, User, Star, Wallet } from '@element-plus/icons-vue'
import { getUserBookings, cancelBooking, extendBooking, getBookingConfirmation, returnScooter } from '@/api/booking'
import { getCurrentUser, getUserStats } from '@/api/user'
import { getMyCards, addCard, deleteCard } from '@/api/card'
import { getMyFeedbacks, createFeedback } from '@/api/feedback'
import { createIssueReport, getMyIssueReports } from '@/api/issues'
import request from '@/utils/request'

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

// 将分钟数转换为可读文本
const formatMinutesToText = (minutes) => {
  if (!minutes) return '未知'
  if (minutes < 60) return `${minutes}分钟`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  if (hours < 24) {
    return mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`
  }
  const days = Math.floor(hours / 24)
  const remainHours = hours % 24
  return remainHours > 0 ? `${days}天${remainHours}小时` : `${days}天`
}

// 根据开始和结束时间计算租期显示
const formatDuration = (confirmation) => {
  if (confirmation.startTime && confirmation.endTime) {
    const start = new Date(confirmation.startTime).getTime()
    const end = new Date(confirmation.endTime).getTime()
    const minutes = Math.round((end - start) / 60000)
    if (minutes > 0) {
      return formatMinutesToText(minutes)
    }
  }
  // 回退到套餐选项
  const map = { '1hr': '1小时', '4hr': '4小时', '1day': '1天', '1week': '1周' }
  return map[confirmation.hireOption] || confirmation.hireOption || '未知'
}

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
      return s === 'ACTIVE' || s === 'PAID' || s === 'PENDING'
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

// 前往支付页面
const goToPay = (booking) => {
  // 将订单信息存入 localStorage，跳转到预订页面进行支付
  localStorage.setItem('pendingPaymentBooking', JSON.stringify(booking))
  router.push('/booking')
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

      // 检查是否是当前进行中的订单，如果是则清理 localStorage
      const activeTripStr = localStorage.getItem('activeTrip')
      if (activeTripStr) {
        try {
          const activeTrip = JSON.parse(activeTripStr)
          if (activeTrip.id === id) {
            localStorage.removeItem('activeTrip')
          }
        } catch (e) {
          localStorage.removeItem('activeTrip')
        }
      }
    } catch (e) {
      // 后端可能返回成功但格式不标准
      if (e.message?.includes('成功') || e.message?.includes('cancel')) {
        ElMessage.success('已取消')
        const booking = bookings.value.find(b => b.id === id)
        if (booking) booking.status = 'CANCELLED'

        // 同样清理 localStorage
        const activeTripStr = localStorage.getItem('activeTrip')
        if (activeTripStr) {
          try {
            const activeTrip = JSON.parse(activeTripStr)
            if (activeTrip.id === id) {
              localStorage.removeItem('activeTrip')
            }
          } catch (err) {
            localStorage.removeItem('activeTrip')
          }
        }
      }
    } finally {
      cancellingId.value = null
    }
  })
}

// 能否延长（只要不是已取消或已完成，都可以延长）
const canExtend = (row) => {
  const u = (row?.status || '').toUpperCase()
  // PENDING(待支付)、ACTIVE(进行中)、PAID(已支付) 都可以延长
  return u === 'PENDING' || u === 'ACTIVE' || u === 'PAID'
}

// 获取延长按钮文字
const getExtendText = (status) => {
  const u = (status || '').toUpperCase()
  if (u === 'PENDING') return '去支付/延长'
  if (u === 'ACTIVE') return '延长租期'
  if (u === 'PAID') return '延长租期'
  return '延长'
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
    let confirmationData = await getBookingConfirmation(id)
    // 统一处理响应格式（兼容 { code: 200, data: {...} } 或直接返回对象）
    if (confirmationData && confirmationData.code === 200 && confirmationData.data) {
      confirmationData = confirmationData.data
    }
    confirmation.value = confirmationData
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
  if (!cardForm.value.cardNumber || !cardForm.value.cardHolder) {
    ElMessage.warning('请填写完整的卡信息')
    return
  }
  cardSaving.value = true
  try {
    // 只传递必要信息，后端会自动提取后4位和识别卡片类型
    await addCard({
      cardHolder: cardForm.value.cardHolder,
      expiryDate: cardForm.value.expiryDate,
      lastFour: cardForm.value.cardNumber.replace(/\s/g, '').slice(-4),  // 提取后4位
      cardType: detectCardType(cardForm.value.cardNumber),  // 识别卡片类型
      isDefault: true
    })
    ElMessage.success('支付卡已保存（仅存储后4位）')
    showCardDialog.value = false
    await loadCards()
  } catch (e) {
    // 静默失败
  } finally {
    cardSaving.value = false
  }
}

// 识别卡片类型
const detectCardType = (cardNumber) => {
  if (!cardNumber) return 'UNKNOWN'
  const clean = cardNumber.replace(/[\s-]/g, '')
  if (/^4/.test(clean)) return 'VISA'
  if (/^5[1-5]/.test(clean)) return 'Mastercard'
  if (/^3[47]/.test(clean)) return 'American Express'
  if (/^6(?:011|5)/.test(clean)) return 'Discover'
  if (/^62/.test(clean)) return 'UnionPay'
  return 'UNKNOWN'
}

// 格式化卡号（添加空格）
const formatCardNumber = (val) => {
  const v = val.replace(/[\s-]/g, '').replace(/\D/g, '')
  const parts = []
  for (let i = 0; i < v.length && i < 16; i += 4) {
    parts.push(v.slice(i, i + 4))
  }
  cardForm.value.cardNumber = parts.join(' ')
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

// 设为默认卡
const setDefaultCard = async (id) => {
  try {
    await request.put(`/api/users/me/cards/${id}/default`)
    ElMessage.success('已设为默认支付卡')
    await loadCards()
  } catch (e) {
    console.error('设置默认卡失败:', e)
  }
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

/* 频繁用户 Banner */
.frequent-user-banner {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border: 1px solid #f59e0b;
  border-radius: var(--cg-radius-lg);
  margin-bottom: 24px;
}

.frequent-user-icon {
  width: 52px;
  height: 52px;
  background: #f59e0b;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.frequent-user-icon .el-icon {
  font-size: 28px;
  color: white;
}

.frequent-user-content h4 {
  margin: 0 0 6px;
  font-size: 16px;
  font-weight: 700;
  color: #92400e;
}

.frequent-user-content p {
  margin: 0;
  font-size: 14px;
  color: #b45309;
}

.frequent-user-content strong {
  color: #d97706;
}

/* 频繁用户进度条 */
.frequent-progress {
  background: #f0f4f8;
  border-radius: var(--cg-radius-lg);
  padding: 16px 20px;
  margin-bottom: 24px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.progress-label {
  font-size: 14px;
  color: var(--cg-text-light);
}

.progress-value {
  font-size: 14px;
  font-weight: 700;
  color: #f59e0b;
}

.progress-hint {
  margin: 10px 0 0;
  font-size: 12px;
  color: var(--cg-text-light);
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

/* 支付卡相关样式 */
.card-display {
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-type-badge {
  background: var(--cg-navy);
  color: white;
  font-size: 11px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 4px;
  text-transform: uppercase;
}

.card-number {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  color: var(--cg-text);
  letter-spacing: 1px;
}

.form-hint {
  font-size: 12px;
  color: var(--cg-text-muted);
  margin-top: 4px;
}

.form-hint.warning {
  color: var(--cg-warning);
}

/* 响应式设计 */
@media (max-width: 900px) {
  .profile {
    padding: 24px 16px;
  }

  .page-title {
    font-size: 1.5rem;
  }

  .page-sub {
    font-size: 14px;
  }

  .stat-card {
    padding: 16px;
    margin-bottom: 12px;
  }

  .stat-icon {
    width: 44px;
    height: 44px;
    font-size: 20px;
  }

  .stat-value {
    font-size: 1.25rem;
  }

  .frequent-user-banner {
    padding: 16px;
    flex-direction: column;
    text-align: center;
  }

  .frequent-user-icon {
    width: 44px;
    height: 44px;
  }

  .frequent-user-icon .el-icon {
    font-size: 24px;
  }

  .profile-tabs {
    padding: 16px;
  }
}

@media (max-width: 768px) {
  .profile {
    padding: 16px 12px;
  }

  .page-title {
    font-size: 1.35rem;
  }

  .stat-row :deep(.el-col) {
    margin-bottom: 12px;
  }

  .user-card :deep(.el-descriptions) {
    font-size: 13px;
  }

  .active-booking-alert :deep(.el-alert__title) {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .active-booking-alert :deep(.el-alert__title) .el-button {
    margin-left: 0 !important;
  }
}

@media (max-width: 600px) {
  .profile {
    padding: 12px 8px;
  }

  .page-title {
    font-size: 1.2rem;
  }

  .page-sub {
    font-size: 13px;
    margin-bottom: 16px;
  }

  .stat-card {
    flex-direction: column;
    text-align: center;
    padding: 16px 12px;
  }

  .stat-info {
    margin-top: 8px;
  }

  .profile-tabs :deep(.el-tabs__header) {
    overflow-x: auto;
  }

  .profile-tabs :deep(.el-tabs__nav-wrap) {
    padding-bottom: 4px;
  }

  .profile-tabs :deep(.el-tabs__item) {
    padding: 0 12px;
    font-size: 13px;
  }

  /* 表格响应式 */
  .profile-tabs :deep(.el-table) {
    font-size: 12px;
  }

  .profile-tabs :deep(.el-table__header) {
    font-size: 12px;
  }

  .profile-tabs :deep(.el-table .el-table__cell) {
    padding: 8px 4px;
  }

  .profile-tabs :deep(.el-button) {
    padding: 4px 6px;
    font-size: 12px;
  }

  .profile-tabs :deep(.el-button .el-icon) {
    margin-right: 2px;
  }

  .profile-tabs :deep(.el-button--small) {
    padding: 3px 5px;
  }

  .profile-tabs :deep(.el-tag) {
    padding: 0 4px;
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .profile {
    padding: 8px 6px;
  }

  .page-title {
    font-size: 1.1rem;
  }

  .stat-row {
    margin-bottom: 16px;
  }

  .stat-row :deep(.el-col) {
    margin-bottom: 8px;
  }

  .frequent-user-content h4 {
    font-size: 14px;
  }

  .frequent-user-content p {
    font-size: 12px;
  }

  .user-card :deep(.el-descriptions__label) {
    width: 80px;
    font-size: 12px;
  }

  .user-card :deep(.el-descriptions__content) {
    font-size: 12px;
  }

  .profile-tabs {
    padding: 12px;
    border-radius: var(--cg-radius-lg);
  }

  /* 操作列按钮堆叠 */
  .profile-tabs :deep(.el-table__body) .el-table__row > td:last-child {
    padding: 8px 4px;
  }

  .profile-tabs :deep(.el-table__body) .el-table__row > td:last-child > div {
    display: flex;
    flex-direction: column;
    gap: 4px;
    align-items: stretch;
  }

  .profile-tabs :deep(.el-table__body) .el-table__row > td:last-child > div .el-button {
    width: 100%;
    margin: 0;
  }
}

@media (max-width: 380px) {
  .page-title {
    font-size: 1rem;
  }

  .stat-value {
    font-size: 1.1rem;
  }

  .stat-label {
    font-size: 12px;
  }

  .frequent-user-banner {
    padding: 12px;
  }

  .frequent-user-icon {
    width: 40px;
    height: 40px;
  }

  .profile-tabs :deep(.el-tabs__item) {
    padding: 0 8px;
    font-size: 12px;
  }
}
</style>
