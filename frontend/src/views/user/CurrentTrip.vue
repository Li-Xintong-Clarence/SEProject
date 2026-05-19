<template>
  <main id="main-content" class="trip-page" role="main" aria-label="当前行程">
    <!-- 页面标题 -->
    <header class="page-header">
      <h1 class="page-title">当前行程</h1>
      <p class="page-sub" role="doc-subtitle">CapyGlide · 实时行程管理</p>
    </header>

    <template v-if="currentBooking">
      <div class="trip-layout">
        <!-- 左侧主卡片 -->
        <section class="main-content" aria-label="行程详情">
          <!-- 行程卡片 -->
          <article class="trip-card">
            <header class="card-header">
              <div class="status-section">
                <div class="status-badge" role="status" :aria-label="'状态：' + getStatusText(currentBooking.status)">
                  <span class="status-dot" aria-hidden="true"></span>
                  {{ getStatusText(currentBooking.status) }}
                </div>
                <div class="vehicle-section">
                  <div class="vehicle-icon" aria-hidden="true">
                    <svg viewBox="0 0 64 64" fill="none">
                      <circle cx="14" cy="50" r="10" stroke="currentColor" stroke-width="2.5"/>
                      <circle cx="50" cy="50" r="10" stroke="currentColor" stroke-width="2.5"/>
                      <path d="M14 50L24 30H40L50 50" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
                      <path d="M24 30L30 20H38" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
                      <rect x="28" y="18" width="12" height="4" rx="1" fill="currentColor"/>
                    </svg>
                  </div>
                  <div class="vehicle-text">
                    <span class="vehicle-name">{{ currentBooking.scooterNumber || '滑板车 #' + currentBooking.scooterId }}</span>
                    <span class="vehicle-code">订单号: {{ currentBooking.confirmationCode || currentBooking.id }}</span>
                  </div>
                </div>
              </div>
            </header>

            <!-- 计时区 -->
            <div class="timer-section" role="timer" aria-label="行程计时">
              <div class="timer-block elapsed">
                <span class="timer-label" id="elapsed-label">已使用时长</span>
                <span class="timer-value" aria-labelledby="elapsed-label">{{ elapsedTime }}</span>
              </div>
              <div class="timer-arrow" aria-hidden="true">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M5 12h14M12 5l7 7-7 7"/>
                </svg>
              </div>
              <div class="timer-block remaining">
                <span class="timer-label" id="remaining-label">预计剩余</span>
                <span class="timer-value" :class="{ warning: isLowTime }" aria-labelledby="remaining-label" :aria-label="'预计剩余时间：' + remainingTime">{{ remainingTime }}</span>
              </div>
            </div>

            <!-- 费用区 -->
            <div class="cost-section">
              <div class="cost-display">
                <span class="cost-label">当前费用</span>
                <span class="cost-value">¥{{ currentCost.toFixed(2) }}</span>
              </div>
              <div v-if="currentBooking.discountRate || currentBooking.discount" class="discount-tag">
                {{ ((currentBooking.discountRate || currentBooking.discount) * 100).toFixed(0) }}% OFF
              </div>
            </div>

            <!-- 订单信息 -->
            <div class="info-section">
              <div class="info-grid">
                <div class="info-item">
                  <div class="info-icon"><el-icon><Timer /></el-icon></div>
                  <div class="info-text">
                    <span class="info-key">租用时长</span>
                    <span class="info-val">{{ formatMinutesToText(durationMinutes) }}</span>
                  </div>
                </div>
                <div class="info-item">
                  <div class="info-icon"><el-icon><Clock /></el-icon></div>
                  <div class="info-text">
                    <span class="info-key">开始时间</span>
                    <span class="info-val">{{ formatDateTime(currentBooking.startTime) }}</span>
                  </div>
                </div>
                <div class="info-item">
                  <div class="info-icon"><el-icon><Clock /></el-icon></div>
                  <div class="info-text">
                    <span class="info-key">截止时间</span>
                    <span class="info-val">{{ formatDateTime(currentBooking.endTime) }}</span>
                  </div>
                </div>
                <div class="info-item full">
                  <div class="info-icon"><el-icon><Location /></el-icon></div>
                  <div class="info-text">
                    <span class="info-key">取车地点</span>
                    <span class="info-val">{{ currentBooking.startDepotName || currentBooking.depotName || '服务点' }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="action-section">
              <el-button type="primary" size="large" @click="showExtendModal" class="action-btn">
                <el-icon><Plus /></el-icon>
                延长租用
              </el-button>
              <el-button type="success" size="large" @click="endTrip" class="action-btn success">
                <el-icon><CircleCheck /></el-icon>
                结束行程
              </el-button>
              <el-button size="large" @click="reportIssue" class="action-btn outline">
                <el-icon><Warning /></el-icon>
                报告问题
              </el-button>
            </div>
          </article>

          <!-- 提示卡片 -->
          <div class="tips-card">
            <div class="tips-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <circle cx="12" cy="12" r="10"/>
                <path d="M12 16v-4M12 8h.01"/>
              </svg>
            </div>
            <div class="tips-text">
              <h4>使用提示</h4>
              <p>请在规定时间内将车辆归还至任意服务点，逾期将按超出时长计费。</p>
            </div>
          </div>
        </section>

        <!-- 右侧统计 -->
        <div class="stats-panel">
          <div class="stats-card">
            <h4 class="stats-title">行程统计</h4>
            <div class="stats-list">
              <div class="stat-item">
                <span class="stat-value">{{ elapsedTime }}</span>
                <span class="stat-label">已用时间</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">¥{{ currentCost.toFixed(2) }}</span>
                <span class="stat-label">当前费用</span>
              </div>
            </div>
          </div>

          <!-- 趣味数据卡片 -->
          <div class="fun-card">
            <div class="fun-header">
              <span class="fun-label">滑行进度</span>
            </div>
            <div class="progress-ring">
              <svg viewBox="0 0 100 100" class="ring-svg">
                <circle cx="50" cy="50" r="40" stroke="#e8eef5" stroke-width="8" fill="none"/>
                <circle cx="50" cy="50" r="40" stroke="#1e3a5f" stroke-width="8" fill="none" 
                  :stroke-dasharray="progressPercent + ' 251.2'" stroke-linecap="round"
                  transform="rotate(-90 50 50)"/>
              </svg>
              <div class="ring-text">
                <span class="ring-value">{{ Math.min(100, Math.round(progressPercent)) }}%</span>
                <span class="ring-label">完成</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <el-empty v-else description="暂无进行中的行程">
      <div class="empty-illustration">
        <svg viewBox="0 0 120 120" class="empty-svg">
          <circle cx="60" cy="60" r="50" fill="#f0f4f8"/>
          <circle cx="30" cy="85" r="12" stroke="#1e3a5f" stroke-width="3" fill="none"/>
          <circle cx="90" cy="85" r="12" stroke="#1e3a5f" stroke-width="3" fill="none"/>
          <path d="M30 85L45 50H75L90 85" stroke="#1e3a5f" stroke-width="3" stroke-linecap="round" fill="none"/>
          <path d="M45 50L55 35H65" stroke="#1e3a5f" stroke-width="3" stroke-linecap="round" fill="none"/>
          <rect x="52" y="32" width="10" height="5" rx="1" fill="#1e3a5f"/>
        </svg>
      </div>
      <p class="empty-text">暂无进行中的行程</p>
      <el-button type="primary" @click="$router.push('/scooters')">去租车</el-button>
    </el-empty>

    <!-- 延长租用弹窗 -->
    <el-dialog v-model="extendVisible" title="延长租用" width="400px" class="extend-dialog">
      <div class="extend-content">
        <p class="extend-tip">选择延长时间，费用将自动累加</p>
        <div class="extend-options">
          <div
            v-for="opt in extendOptions"
            :key="opt.value"
            class="extend-item"
            :class="{ active: selectedExtend === opt.value }"
            @click="selectedExtend = opt.value"
          >
            <span class="extend-label">{{ opt.label }}</span>
            <span class="extend-price">+¥{{ opt.price }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="extendVisible = false">取消</el-button>
        <el-button type="primary" :loading="extendLoading" @click="confirmExtend">确认延长</el-button>
      </template>
    </el-dialog>

    <!-- 报告问题弹窗 -->
    <el-dialog v-model="issueVisible" title="报告问题" width="480px" class="issue-dialog">
      <el-form :model="issueForm" label-width="100px">
        <el-form-item label="问题类型">
          <el-select v-model="issueForm.type" style="width: 100%">
            <el-option label="车辆故障" value="BREAKDOWN" />
            <el-option label="电量不足" value="LOW_BATTERY" />
            <el-option label="无法还车" value="RETURN_ISSUE" />
            <el-option label="其他问题" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="问题描述" required>
          <el-input v-model="issueForm.description" type="textarea" :rows="4" placeholder="请描述遇到的问题" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="issueVisible = false">取消</el-button>
        <el-button type="primary" :loading="issueLoading" @click="confirmReport">提交报告</el-button>
      </template>
    </el-dialog>

    <!-- 还车服务点弹窗 -->
    <el-dialog v-model="returnVisible" title="选择还车服务点" width="500px" class="return-dialog">
      <div class="return-content">
        <p class="return-tip">请将滑板车归还至以下服务点之一：</p>
        <div class="depot-list">
          <div
            v-for="depot in depots"
            :key="depot.id"
            class="depot-item"
            :class="{ selected: selectedDepot?.id === depot.id }"
            @click="selectedDepot = depot"
          >
            <div class="depot-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"/>
                <circle cx="12" cy="10" r="3"/>
              </svg>
            </div>
            <div class="depot-text">
              <span class="depot-name">{{ depot.name }}</span>
              <span class="depot-addr">{{ depot.address || depot.depotNumber }}</span>
            </div>
            <div class="depot-count" :class="{ available: depot.availableCount < (depot.capacity || 10) }">
              <span class="count-num">{{ depot.availableCount }}</span>
              <span class="count-label">/ {{ depot.capacity || 10 }}</span>
            </div>
            <div v-if="selectedDepot?.id === depot.id" class="selected-check">
              <el-icon><Check /></el-icon>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="returnVisible = false">取消</el-button>
        <el-button type="success" :loading="returnLoading" @click="confirmReturn" :disabled="!selectedDepot">确认还车</el-button>
      </template>
    </el-dialog>

    <!-- 还车成功弹窗 -->
    <el-dialog v-model="showCompleteModal" title="行程结束" width="440px" :close-on-click-modal="false" class="complete-dialog">
      <div class="complete-content">
        <div class="complete-icon">
          <svg viewBox="0 0 80 80" fill="none">
            <circle cx="40" cy="40" r="38" stroke="currentColor" stroke-width="2"/>
            <path d="M24 40L35 51L56 30" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h3 class="complete-title">行程已结束</h3>
        <p class="complete-sub">感谢您使用 CapyGlide</p>

        <div class="summary-block">
          <div class="summary-row">
            <span class="summary-key">使用时长</span>
            <span class="summary-val">{{ elapsedTime }}</span>
          </div>
          <div class="summary-row total">
            <span class="summary-key">应付金额</span>
            <span class="summary-price">¥{{ finalCost.toFixed(2) }}</span>
          </div>
        </div>

        <div class="rating-section">
          <p class="rating-prompt">为本次服务评分</p>
          <div class="rating-stars">
            <span v-for="i in 5" :key="i" class="star" :class="{ active: i <= rating }" @click="rating = i">★</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" size="large" @click="finishTrip" class="finish-btn">完成</el-button>
      </template>
    </el-dialog>
  </main>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Timer, Clock, Location, Plus, CircleCheck, Warning, Check } from '@element-plus/icons-vue'
import { getMyActiveBookings, endBooking, extendBooking, returnScooter } from '@/api/booking'
import { createIssueReport } from '@/api/issues'
import { getDepots } from '@/api/depot'

const router = useRouter()

const loading = ref(true)
const currentBooking = ref(null)
const extendVisible = ref(false)
const issueVisible = ref(false)
const returnVisible = ref(false)
const extendLoading = ref(false)
const issueLoading = ref(false)
const returnLoading = ref(false)
const showCompleteModal = ref(false)
const selectedExtend = ref('1hr')
const rating = ref(5)
const finalCost = ref(0)
const selectedDepot = ref(null)
const depots = ref([])
const isTripEnded = ref(false)
const lockedEndTime = ref(null)
const lockedDuration = ref(0)

const extendOptions = [
  { value: '1hr', label: '1 小时', price: 5 },
  { value: '4hr', label: '4 小时', price: 18 },
  { value: '1day', label: '1 天', price: 40 }
]

const issueForm = ref({ description: '', type: 'OTHER' })

let timer = null
const startTime = ref(null)
const durationMinutes = ref(60)
const now = ref(Date.now())

const progressPercent = computed(() => {
  if (!startTime.value || durationMinutes.value <= 0) return 0
  const elapsed = (now.value - startTime.value) / 60000
  return (elapsed / durationMinutes.value) * 100
})

const isLowTime = computed(() => {
  if (!startTime.value) return false
  const endTimeMs = startTime.value + durationMinutes.value * 60 * 1000
  return (endTimeMs - now.value) < 10 * 60 * 1000
})

const currentCost = computed(() => {
  if (!currentBooking.value) return 0
  if (isTripEnded.value) return finalCost.value

  const backendCost = currentBooking.value.totalCost
  if (backendCost !== undefined && backendCost !== null) {
    const cost = typeof backendCost === 'number' ? backendCost : parseFloat(backendCost)
    if (!isNaN(cost)) return cost
  }

  const priceMap = { '1hr': 5, '4hr': 18, '1day': 40, '1week': 200 }
  const basePrice = priceMap[currentBooking.value.hireOption] || 5
  const elapsedMinutes = Math.floor((now.value - (startTime.value || now.value)) / 60000)

  const usedRatio = Math.min(1, elapsedMinutes / Math.max(1, durationMinutes.value))
  let cost = basePrice * usedRatio

  const overtimeMinutes = Math.max(0, elapsedMinutes - durationMinutes.value)
  const overtimeCost = overtimeMinutes * 0.1
  cost = cost + overtimeCost

  const discount = currentBooking.value.discountRate || currentBooking.value.discount || 0
  if (discount > 0 && discount < 1) cost = cost * (1 - discount)

  return Math.max(0, cost)
})

const calculateFinalCost = () => {
  if (!currentBooking.value || !startTime.value) return 0

  const priceMap = { '1hr': 5, '4hr': 18, '1day': 40, '1week': 200 }
  const basePrice = priceMap[currentBooking.value.hireOption] || 5

  const elapsedMs = (lockedEndTime.value || now.value) - startTime.value
  const elapsedMinutes = Math.floor(elapsedMs / 60000)

  const usedRatio = Math.min(1, elapsedMinutes / Math.max(1, durationMinutes.value))
  let cost = basePrice * usedRatio

  const overtimeMinutes = Math.max(0, elapsedMinutes - durationMinutes.value)
  const overtimeCost = overtimeMinutes * 0.1
  cost = cost + overtimeCost

  const discount = currentBooking.value.discountRate || currentBooking.value.discount || 0
  if (discount > 0 && discount < 1) cost = cost * (1 - discount)

  return Math.max(0, cost)
}

const lockedElapsedTime = computed(() => {
  if (!isTripEnded.value || !startTime.value) return '00:00:00'
  const elapsed = Math.max(0, Math.floor((lockedEndTime.value - startTime.value) / 1000))
  const h = Math.floor(elapsed / 3600)
  const m = Math.floor((elapsed % 3600) / 60)
  const s = elapsed % 60
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
})

const getStatusText = (status) => {
  const map = { 'PENDING': '待支付', 'PAID': '租用中', 'ACTIVE': '租用中', 'COMPLETED': '已完成', 'CANCELLED': '已取消' }
  return map[status] || status || '未知'
}

const formatMinutesToText = (minutes) => {
  if (!minutes) return '未知'
  if (minutes < 60) return `${minutes}分钟`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  if (hours < 24) return mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`
  const days = Math.floor(hours / 24)
  const remainHours = hours % 24
  return remainHours > 0 ? `${days}天${remainHours}小时` : `${days}天`
}

const formatDateTime = (time) => {
  if (!time) return '—'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const durationToMinutes = (option) => {
  const map = { '1hr': 60, '4hr': 240, '1day': 1440, '1week': 10080 }
  return map[option] || 60
}

const elapsedTime = computed(() => {
  if (isTripEnded.value) return lockedElapsedTime.value
  if (!startTime.value || startTime.value <= 0) return '00:00:00'
  const elapsed = Math.max(0, Math.floor((now.value - startTime.value) / 1000))
  const h = Math.floor(elapsed / 3600)
  const m = Math.floor((elapsed % 3600) / 60)
  const s = elapsed % 60
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
})

const remainingTime = computed(() => {
  if (!startTime.value || startTime.value <= 0) return '00:00:00'
  const endTime = startTime.value + durationMinutes.value * 60 * 1000
  const remaining = Math.max(0, Math.floor((endTime - now.value) / 1000))
  const h = Math.floor(remaining / 3600)
  const m = Math.floor((remaining % 3600) / 60)
  const s = remaining % 60
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
})

const loadBooking = async () => {
  loading.value = true
  isTripEnded.value = false
  lockedEndTime.value = null
  lockedDuration.value = 0
  try {
    const res = await getMyActiveBookings()
    const booking = res?.data || res
    if (booking && booking.id) {
      currentBooking.value = booking
      localStorage.setItem('activeTrip', JSON.stringify(currentBooking.value))
      const timeValue = currentBooking.value.startTime || currentBooking.value.start_time
      if (timeValue) startTime.value = new Date(timeValue).getTime()
      const endTimeValue = currentBooking.value.endTime || currentBooking.value.end_time
      if (startTime.value && endTimeValue) {
        durationMinutes.value = Math.round((new Date(endTimeValue).getTime() - startTime.value) / 60000)
      } else {
        durationMinutes.value = durationToMinutes(currentBooking.value.hireOption)
      }
    } else {
      localStorage.removeItem('activeTrip')
      currentBooking.value = null
    }
  } catch {
    const localTrip = localStorage.getItem('activeTrip')
    if (localTrip) {
      try {
        const trip = JSON.parse(localTrip)
        currentBooking.value = trip
        const timeValue = trip.startTime || trip.start_time
        if (timeValue) startTime.value = new Date(timeValue).getTime()
        durationMinutes.value = durationToMinutes(trip.hireOption)
      } catch {
        localStorage.removeItem('activeTrip')
        currentBooking.value = null
      }
    }
  } finally {
    loading.value = false
  }
}

const showExtendModal = () => {
  extendVisible.value = true
  selectedExtend.value = '1hr'
}

const confirmExtend = async () => {
  extendLoading.value = true
  try {
    const res = await extendBooking(currentBooking.value.id, selectedExtend.value)
    ElMessage.success('租用已延长')
    extendVisible.value = false
    if (res?.data) {
      currentBooking.value = res.data
      localStorage.setItem('activeTrip', JSON.stringify(currentBooking.value))
      const startTimeValue = currentBooking.value.startTime || currentBooking.value.start_time
      const endTimeValue = currentBooking.value.endTime || currentBooking.value.end_time
      if (startTimeValue && endTimeValue) {
        startTime.value = new Date(startTimeValue).getTime()
        durationMinutes.value = Math.round((new Date(endTimeValue).getTime() - startTime.value) / 60000)
      }
    } else {
      await loadBooking()
    }
  } catch {
    ElMessage.error('延长失败')
  } finally {
    extendLoading.value = false
  }
}

const endTrip = async () => {
  returnVisible.value = true
  selectedDepot.value = null
  try {
    const res = await getDepots()
    depots.value = Array.isArray(res) ? res : (res?.data || [])
  } catch {
    ElMessage.error('获取服务点列表失败')
  }
}

const confirmReturn = async () => {
  if (!selectedDepot.value) {
    ElMessage.warning('请选择还车服务点')
    return
  }
  returnLoading.value = true
  try {
    lockedEndTime.value = Date.now()
    const timeValue = currentBooking.value.startTime || currentBooking.value.start_time
    if (timeValue) {
      lockedDuration.value = Math.floor((lockedEndTime.value - new Date(timeValue).getTime()) / 60000)
    }

    await returnScooter(currentBooking.value.id, selectedDepot.value.id)

    // 重新获取订单信息以获取实际费用（含超时费用）
    try {
      const bookings = await getMyActiveBookings()
      const completed = Array.isArray(bookings) ? bookings.filter(b => b.status === 'COMPLETED') : []
      if (completed.length > 0) {
        // 找到刚完成的订单
        const myBooking = completed.find(b => b.id === currentBooking.value.id)
        if (myBooking && myBooking.totalCost !== undefined) {
          finalCost.value = typeof myBooking.totalCost === 'number' ? myBooking.totalCost : parseFloat(myBooking.totalCost)
          currentBooking.value = { ...currentBooking.value, ...myBooking }
        }
      }
    } catch (e) {
      console.error('获取订单详情失败:', e)
      finalCost.value = calculateFinalCost()
    }

    localStorage.removeItem('activeTrip')
    returnVisible.value = false
    isTripEnded.value = true
    showCompleteModal.value = true
  } catch {
    ElMessage.error('还车失败')
  } finally {
    returnLoading.value = false
  }
}

const finishTrip = () => {
  showCompleteModal.value = false
  router.push('/scooters')
}

const reportIssue = () => {
  issueVisible.value = true
}

const confirmReport = async () => {
  if (!issueForm.value.description.trim()) {
    ElMessage.warning('请输入问题描述')
    return
  }
  issueLoading.value = true
  try {
    console.log('Sending issue report:', {
      scooterId: currentBooking.value.scooterId,
      description: issueForm.value.description,
      type: issueForm.value.type
    })
    const result = await createIssueReport({
      scooterId: currentBooking.value.scooterId,
      description: issueForm.value.description,
      type: issueForm.value.type
    })
    console.log('Issue report response:', result)
    ElMessage.success('问题已报告，我们会尽快处理')
    issueVisible.value = false
    issueForm.value = { description: '', type: 'OTHER' }
  } catch (error) {
    console.error('Report failed:', error)
    ElMessage.error('报告失败')
  } finally {
    issueLoading.value = false
  }
}

onMounted(() => {
  loadBooking()
  timer = setInterval(() => { now.value = Date.now() }, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.trip-page {
  padding: 32px 40px;
  max-width: 1200px;
  margin: 0 auto;
  background: linear-gradient(180deg, #e8eef5 0%, #d6e0eb 100%);
  min-height: calc(100vh - 64px);
}

.page-header {
  margin-bottom: 28px;
}

.page-title {
  margin: 0 0 6px;
  font-size: 26px;
  font-weight: 800;
  color: #1e3a5f;
}

.page-sub {
  margin: 0;
  font-size: 14px;
  color: #5a7a9a;
}

.trip-layout {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 28px;
}

.main-content {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.trip-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(30, 58, 95, 0.1);
}

.card-header {
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  padding: 24px 28px;
}

.status-section {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.15);
  padding: 6px 14px;
  border-radius: 20px;
  color: white;
  font-size: 12px;
  font-weight: 600;
  width: fit-content;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #4ade80;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.vehicle-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.vehicle-icon {
  width: 52px;
  height: 52px;
  background: rgba(255, 255, 255, 0.12);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.vehicle-icon svg {
  width: 30px;
  height: 30px;
  color: white;
}

.vehicle-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.vehicle-name {
  font-size: 20px;
  font-weight: 700;
  color: white;
}

.vehicle-code {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
}

.timer-section {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 36px;
  background: #f8fafc;
}

.timer-block {
  flex: 1;
  text-align: center;
}

.timer-label {
  display: block;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #7a8fa8;
  margin-bottom: 12px;
}

.timer-value {
  font-size: 34px;
  font-weight: 800;
  font-family: 'Plus Jakarta Sans', monospace;
}

.timer-block.elapsed .timer-value {
  color: #2d8a4e;
}

.timer-block.remaining .timer-value {
  color: #1e3a5f;
}

.timer-block.remaining .timer-value.warning {
  color: #c4880c;
}

.timer-arrow {
  width: 56px;
  height: 56px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 20px;
  box-shadow: 0 4px 16px rgba(30, 58, 95, 0.1);
}

.timer-arrow svg {
  width: 24px;
  height: 24px;
  color: #7a8fa8;
}

.cost-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 18px;
  padding: 24px;
  border-top: 1px solid #f0f4f8;
  border-bottom: 1px solid #f0f4f8;
}

.cost-display {
  text-align: center;
}

.cost-label {
  display: block;
  font-size: 12px;
  color: #7a8fa8;
  margin-bottom: 6px;
}

.cost-value {
  font-size: 48px;
  font-weight: 800;
  color: #1e3a5f;
  line-height: 1;
}

.discount-tag {
  padding: 10px 16px;
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  color: white;
  font-size: 13px;
  font-weight: 700;
  border-radius: 20px;
}

.info-section {
  padding: 22px 28px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.info-item.full {
  grid-column: span 2;
}

.info-icon {
  width: 40px;
  height: 40px;
  background: #f0f4f8;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #5a7a9a;
}

.info-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-key {
  font-size: 12px;
  color: #7a8fa8;
}

.info-val {
  font-size: 14px;
  font-weight: 600;
  color: #1e3a5f;
}

.action-section {
  display: flex;
  gap: 14px;
  padding: 22px 28px;
}

.action-btn {
  flex: 1;
  height: 48px;
  font-size: 14px;
  font-weight: 700;
  border-radius: 12px;
  border: none;
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  color: white;
}

.action-btn.success {
  background: #2d8a4e;
}

.action-btn.outline {
  background: #f0f4f8;
  color: #5a7a9a;
  border: 1px solid #d6e0eb;
}

.action-btn:hover {
  opacity: 0.9;
}

.tips-card {
  display: flex;
  gap: 14px;
  padding: 18px 20px;
  background: white;
  border-radius: 14px;
  box-shadow: 0 4px 16px rgba(30, 58, 95, 0.08);
}

.tips-icon {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #fef9c3 0%, #fef3c7 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.tips-icon svg {
  width: 22px;
  height: 22px;
  color: #c4880c;
}

.tips-text h4 {
  margin: 0 0 6px;
  font-size: 14px;
  font-weight: 700;
  color: #92400e;
}

.tips-text p {
  margin: 0;
  font-size: 13px;
  color: #b45309;
  line-height: 1.5;
}

.stats-panel {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.stats-card {
  background: white;
  border-radius: 16px;
  padding: 22px;
  box-shadow: 0 4px 20px rgba(30, 58, 95, 0.1);
}

.stats-title {
  margin: 0 0 18px;
  font-size: 16px;
  font-weight: 700;
  color: #1e3a5f;
}

.stats-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-item {
  text-align: center;
  padding: 16px 12px;
  background: #f8fafc;
  border-radius: 12px;
}

.stat-value {
  display: block;
  font-size: 18px;
  font-weight: 800;
  color: #1e3a5f;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #7a8fa8;
}

.fun-card {
  background: white;
  border-radius: 16px;
  padding: 22px;
  box-shadow: 0 4px 20px rgba(30, 58, 95, 0.1);
}

.fun-header {
  margin-bottom: 16px;
}

.fun-label {
  font-size: 14px;
  font-weight: 700;
  color: #1e3a5f;
}

.progress-ring {
  position: relative;
  width: 140px;
  height: 140px;
  margin: 0 auto;
}

.ring-svg {
  width: 100%;
  height: 100%;
  transform: rotate(-90deg);
}

.ring-text {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.ring-value {
  font-size: 28px;
  font-weight: 800;
  color: #1e3a5f;
}

.ring-label {
  font-size: 12px;
  color: #7a8fa8;
}

.extend-content {
  padding: 8px 0;
}

.extend-tip {
  color: #5a7a9a;
  font-size: 14px;
  margin: 0 0 20px;
}

.extend-options {
  display: flex;
  gap: 12px;
}

.extend-item {
  flex: 1;
  padding: 16px;
  background: #f0f4f8;
  border: 1px solid #d6e0eb;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.extend-item:hover {
  border-color: #1e3a5f;
}

.extend-item.active {
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  border-color: #1e3a5f;
}

.extend-label {
  display: block;
  font-size: 14px;
  font-weight: 700;
  color: #1e3a5f;
  margin-bottom: 4px;
}

.extend-item.active .extend-label {
  color: white;
}

.extend-price {
  font-size: 13px;
  color: #5a7a9a;
}

.extend-item.active .extend-price {
  color: rgba(255, 255, 255, 0.8);
}

.return-content {
  padding: 8px 0;
}

.return-tip {
  color: #5a7a9a;
  font-size: 14px;
  margin: 0 0 20px;
}

.depot-list {
  max-height: 380px;
  overflow-y: auto;
}

.depot-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  background: #f8fafc;
  border: 1px solid transparent;
  border-radius: 12px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.depot-item:hover {
  background: #f0f4f8;
}

.depot-item.selected {
  background: rgba(30, 58, 95, 0.06);
  border-color: #1e3a5f;
}

.depot-icon {
  width: 42px;
  height: 42px;
  background: white;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #5a7a9a;
}

.depot-icon svg {
  width: 20px;
  height: 20px;
}

.depot-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.depot-name {
  font-weight: 700;
  color: #1e3a5f;
  font-size: 14px;
}

.depot-addr {
  font-size: 12px;
  color: #7a8fa8;
}

.depot-count {
  padding: 8px 12px;
  background: #fde8e8;
  color: #d14545;
  border-radius: 10px;
  font-size: 12px;
  text-align: center;
}

.depot-count.available {
  background: #e6f4ea;
  color: #2d8a4e;
}

.count-num {
  display: block;
  font-size: 18px;
  font-weight: 800;
}

.count-label {
  font-size: 10px;
}

.selected-check {
  width: 26px;
  height: 26px;
  background: #1e3a5f;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.complete-content {
  text-align: center;
  padding: 16px 0;
}

.complete-icon {
  width: 70px;
  height: 70px;
  margin: 0 auto 18px;
  color: #2d8a4e;
}

.complete-icon svg {
  width: 100%;
  height: 100%;
}

.complete-title {
  margin: 0 0 6px;
  font-size: 24px;
  font-weight: 800;
  color: #1e3a5f;
}

.complete-sub {
  margin: 0 0 24px;
  color: #5a7a9a;
  font-size: 14px;
}

.summary-block {
  background: #f0f4f8;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.summary-key {
  color: #5a7a9a;
  font-size: 14px;
}

.summary-val {
  font-weight: 700;
  color: #1e3a5f;
}

.summary-row.total {
  border-top: 1px dashed #d6e0eb;
  margin-top: 10px;
  padding-top: 14px;
}

.summary-price {
  font-size: 24px;
  font-weight: 800;
  color: #1e3a5f;
}

.rating-section {
  margin-top: 18px;
}

.rating-prompt {
  color: #5a7a9a;
  font-size: 13px;
  margin: 0 0 14px;
}

.rating-stars {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.star {
  font-size: 40px;
  color: #d6e0eb;
  cursor: pointer;
  transition: all 0.2s;
}

.star:hover,
.star.active {
  color: #3b5998;
}

.finish-btn {
  width: 100%;
  height: 50px;
  font-size: 15px;
  font-weight: 700;
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%) !important;
  border: none !important;
  border-radius: 12px;
}

.empty-illustration {
  width: 100px;
  height: 100px;
  margin: 0 auto 16px;
}

.empty-svg {
  width: 100%;
  height: 100%;
}

.empty-text {
  color: #5a7a9a;
  margin-bottom: 20px;
}

/* ============================================
   响应式设计 - 移动端适配
   ============================================ */

/* 平板 (≤900px) */
@media (max-width: 900px) {
  .trip-page {
    padding: 24px 20px;
  }

  .page-header {
    text-align: center;
    margin-bottom: 20px;
  }

  .page-title {
    font-size: 22px;
  }

  .trip-layout {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .stats-panel {
    flex-direction: row;
    gap: 14px;
  }

  .stats-card, .fun-card {
    flex: 1;
  }

  .fun-card .progress-ring {
    width: 100px;
    height: 100px;
  }

  .ring-value {
    font-size: 22px;
  }
}

/* 手机 (≤600px) */
@media (max-width: 600px) {
  .trip-page {
    padding: 16px 12px;
  }

  .card-header {
    padding: 18px 16px;
  }

  .vehicle-name {
    font-size: 18px;
  }

  .timer-section {
    flex-direction: column;
    padding: 24px;
    gap: 16px;
  }

  .timer-block {
    width: 100%;
  }

  .timer-value {
    font-size: 28px;
  }

  .timer-arrow {
    width: 44px;
    height: 44px;
    margin: 0;
    transform: rotate(90deg);
  }

  .timer-arrow svg {
    width: 20px;
    height: 20px;
  }

  .cost-section {
    flex-direction: column;
    gap: 14px;
    padding: 18px;
  }

  .cost-value {
    font-size: 36px;
  }

  .info-section {
    padding: 18px 16px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .info-item.full {
    grid-column: span 1;
  }

  .action-section {
    flex-direction: column;
    padding: 18px 16px;
    gap: 10px;
  }

  .action-btn {
    width: 100%;
  }

  .tips-card {
    flex-direction: column;
    text-align: center;
  }

  .stats-panel {
    flex-direction: column;
  }

  .stats-card, .fun-card {
    width: 100%;
  }

  .fun-card .progress-ring {
    width: 120px;
    height: 120px;
  }

  .extend-dialog .extend-options {
    flex-direction: column;
  }

  .return-dialog .depot-list {
    max-height: 280px;
  }

  .complete-dialog {
    max-width: calc(100vw - 32px);
  }

  .summary-block {
    padding: 16px;
  }

  .summary-price {
    font-size: 20px;
  }

  .rating-stars {
    gap: 8px;
  }

  .star {
    font-size: 32px;
  }
}

/* 小屏手机 (≤380px) */
@media (max-width: 380px) {
  .page-title {
    font-size: 20px;
  }

  .vehicle-icon {
    width: 44px;
    height: 44px;
  }

  .vehicle-icon svg {
    width: 24px;
    height: 24px;
  }

  .timer-value {
    font-size: 24px;
  }

  .cost-value {
    font-size: 30px;
  }

  .discount-tag {
    font-size: 11px;
    padding: 8px 12px;
  }

  .finish-btn {
    height: 44px;
    font-size: 14px;
  }
}
</style>
