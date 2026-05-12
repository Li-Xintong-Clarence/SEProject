<template>
<<<<<<< Updated upstream
  <div class="riding-page">
    <!-- 顶部导航栏 -->
    <div class="riding-header">
      <div class="header-left">
        <span class="scooter-id">🛴 {{ currentBooking?.scooterNumber || '车辆' }}</span>
        <el-tag type="success" size="small">骑行中</el-tag>
      </div>
      <div class="header-right">
        <el-button type="primary" size="small" @click="showReturnDialog = true">
          <el-icon><Bottom /></el-icon> 还车
        </el-button>
      </div>
=======
  <div class="trip-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">当前行程</h2>
      <p class="page-sub">CapyGlide · 实时行程管理</p>
>>>>>>> Stashed changes
    </div>

    <!-- 地图容器 -->
    <div id="riding-map-container" class="riding-map"></div>

<<<<<<< Updated upstream
    <!-- 右下角计时卡片 -->
    <div class="timer-card">
      <div class="timer-main">
        <div class="timer-label">骑行时长</div>
        <div class="timer-value">{{ elapsedTime }}</div>
      </div>
      <div class="timer-divider"></div>
      <div class="timer-sub">
        <div class="sub-item">
          <span class="sub-label">费用</span>
          <span class="sub-value">¥{{ totalCost.toFixed(2) }}</span>
        </div>
        <div class="sub-item">
          <span class="sub-label">里程</span>
          <span class="sub-value">{{ currentMileage }} km</span>
=======
    <template v-else-if="currentBooking">
      <div class="trip-layout">
        <!-- 左侧主卡片 -->
        <div class="main-content">
          <!-- 行程卡片 -->
          <div class="trip-card">
            <div class="card-header">
              <div class="status-section">
                <div class="status-badge">
                  <span class="status-dot"></span>
                  {{ getStatusText(currentBooking.status) }}
                </div>
                <div class="vehicle-section">
                  <div class="vehicle-icon">
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
            </div>

            <!-- 计时区 -->
            <div class="timer-section">
              <div class="timer-block elapsed">
                <span class="timer-label">已使用时长</span>
                <span class="timer-value">{{ elapsedTime }}</span>
              </div>
              <div class="timer-arrow">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M5 12h14M12 5l7 7-7 7"/>
                </svg>
              </div>
              <div class="timer-block remaining">
                <span class="timer-label">预计剩余</span>
                <span class="timer-value" :class="{ warning: isLowTime }">{{ remainingTime }}</span>
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
          </div>

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
>>>>>>> Stashed changes
        </div>
      </div>
    </div>

<<<<<<< Updated upstream
    <!-- 左下角电量卡片 -->
    <div class="battery-card">
      <div class="battery-header">
        <span class="battery-icon">🔋</span>
        <span class="battery-value">{{ currentBattery }}%</span>
      </div>
      <el-progress
        :percentage="currentBattery"
        :color="batteryColor"
        :stroke-width="6"
        :show-text="false"
      />
      <div class="battery-info">
        <span>预计 {{ estimatedRange }} km</span>
      </div>
    </div>

    <!-- 底部信息栏 -->
    <div class="bottom-info-bar">
      <div class="info-item">
        <el-icon><Clock /></el-icon>
        <span>{{ formatTime(currentBooking?.startTime) }} 开始</span>
      </div>
      <div class="info-item warning" v-if="isOvertime">
        <el-icon><Warning /></el-icon>
        <span>超时 {{ overtimeMinutes }} 分钟</span>
      </div>
      <div class="info-item" v-if="!isOvertime">
        <el-icon><Timer /></el-icon>
        <span>剩余 {{ remainingTimeStr }}</span>
      </div>
    </div>

    <!-- 还车确认弹窗 -->
    <el-dialog
      v-model="showReturnDialog"
      title="确认还车"
      width="90%"
      :close-on-click-modal="false"
    >
      <div class="return-summary">
        <div class="summary-row">
          <span>骑行时长</span>
          <span>{{ elapsedTime }}</span>
        </div>
        <div class="summary-row">
          <span>行驶里程</span>
          <span>{{ currentMileage }} km</span>
        </div>
        <div class="summary-row">
          <span>基础费用</span>
          <span>¥{{ baseCost.toFixed(2) }}</span>
        </div>
        <div class="summary-row" v-if="totalElectricityCost > 0">
          <span>电费</span>
          <span>+ ¥{{ totalElectricityCost.toFixed(2) }}</span>
        </div>
        <div class="summary-row" v-if="overtimeCost > 0">
          <span>超时费</span>
          <span class="overtime">+ ¥{{ overtimeCost.toFixed(2) }}</span>
        </div>
        <el-divider />
        <div class="summary-row total">
          <span>应付金额</span>
          <span class="price">¥{{ totalCost.toFixed(2) }}</span>
        </div>
      </div>
      
      <div class="return-tips">
        <p>⚠️ 请将车辆停放在指定服务点（地图上蓝色标记）</p>
      </div>

      <template #footer>
        <el-button @click="showReturnDialog = false">继续骑行</el-button>
        <el-button type="primary" :loading="returning" @click="confirmReturn">
          确认还车 (¥{{ totalCost.toFixed(2) }})
        </el-button>
=======
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
      <el-button type="primary" @click="$router.push('/map')">去租车</el-button>
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
>>>>>>> Stashed changes
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
<<<<<<< Updated upstream
import { ElMessage, ElMessageBox } from 'element-plus'
import { Bottom, Clock, Timer, Warning } from '@element-plus/icons-vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import { getMyActiveBookings, endBooking } from '@/api/booking'
=======
import { ElMessage } from 'element-plus'
import { Timer, Clock, Location, Plus, CircleCheck, Warning, Check } from '@element-plus/icons-vue'
import { getMyActiveBookings, endBooking, extendBooking, returnScooter } from '@/api/booking'
import { createIssueReport } from '@/api/issues'
import { getDepots } from '@/api/depot'
>>>>>>> Stashed changes

const router = useRouter()

const loading = ref(true)
const currentBooking = ref(null)
<<<<<<< Updated upstream
const showReturnDialog = ref(false)
const returning = ref(false)

// 计时器相关
const startTime = ref(null)
const durationMinutes = ref(60)
let timer = null

// 实时数据
const currentBattery = ref(100)
const initialBattery = ref(100)
const currentMileage = ref(0)
const pricePerKm = 0.5
=======
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
// 订单是否已结束（锁定显示值）
const isTripEnded = ref(false)
// 锁定时的实际结束时间
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

  // 如果订单已结束，使用锁定的值
  if (isTripEnded.value) {
    return finalCost.value
  }

  // 优先使用后端返回的 totalCost
  const backendCost = currentBooking.value.totalCost
  if (backendCost !== undefined && backendCost !== null) {
    const cost = typeof backendCost === 'number' ? backendCost : parseFloat(backendCost)
    if (!isNaN(cost)) return cost
  }

  // 如果后端没有返回，使用本地计算
  const priceMap = { '1hr': 5, '4hr': 18, '1day': 40, '1week': 200 }
  const basePrice = priceMap[currentBooking.value.hireOption] || 5
  const elapsedMinutes = Math.floor((now.value - (startTime.value || now.value)) / 60000)

  // 计算基础费用（不超过套餐时长）
  const usedRatio = Math.min(1, elapsedMinutes / Math.max(1, durationMinutes.value))
  let cost = basePrice * usedRatio

  // 超时费用：每超1分钟加收0.1元
  const overtimeMinutes = Math.max(0, elapsedMinutes - durationMinutes.value)
  const overtimeCost = overtimeMinutes * 0.1
  cost = cost + overtimeCost

  // 应用折扣
  const discount = currentBooking.value.discountRate || currentBooking.value.discount || 0
  if (discount > 0 && discount < 1) cost = cost * (1 - discount)

  return Math.max(0, cost)
})

// 计算实际费用（用于最终结算）
const calculateFinalCost = () => {
  if (!currentBooking.value || !startTime.value) return 0

  const priceMap = { '1hr': 5, '4hr': 18, '1day': 40, '1week': 200 }
  const basePrice = priceMap[currentBooking.value.hireOption] || 5

  // 使用锁定的时间计算
  const elapsedMs = (lockedEndTime.value || now.value) - startTime.value
  const elapsedMinutes = Math.floor(elapsedMs / 60000)

  // 基础费用
  const usedRatio = Math.min(1, elapsedMinutes / Math.max(1, durationMinutes.value))
  let cost = basePrice * usedRatio

  // 超时费用
  const overtimeMinutes = Math.max(0, elapsedMinutes - durationMinutes.value)
  const overtimeCost = overtimeMinutes * 0.1
  cost = cost + overtimeCost

  // 应用折扣
  const discount = currentBooking.value.discountRate || currentBooking.value.discount || 0
  if (discount > 0 && discount < 1) cost = cost * (1 - discount)

  return Math.max(0, cost)
}

// 计算锁定的时间显示
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
>>>>>>> Stashed changes

// 地图相关
let map = null
let pathLine = null
let userMarker = null
let positionWatchId = null
const GPS_POINTS = []

// 电费计算
const pricePerKmValue = 0.5

const formatTime = (time) => {
  if (!time) return '--:--'
  return new Date(time).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

<<<<<<< Updated upstream
const formatDuration = (option) => {
  const map = { '1hr': 60, '4hr': 240, '1day': 1440, '1week': 10080 }
  return map[option] || 60
=======
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

const formatDateTime = (time) => {
  if (!time) return '—'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
>>>>>>> Stashed changes
}

const durationToMinutes = (option) => {
  const map = { '1hr': 60, '4hr': 240, '1day': 1440, '1week': 10080 }
  return map[option] || 60
}

// 费用计算
const baseCost = computed(() => {
  const durations = { '1hr': 5, '4hr': 15, '1day': 40, '1week': 200 }
  return durations[currentBooking.value?.hireOption] || 5
})

const overtimeCost = computed(() => {
  if (!currentBooking.value?.startTime) return 0
  const endTime = new Date(currentBooking.value.startTime).getTime() + durationMinutes.value * 60 * 1000
  const now = Date.now()
  if (now <= endTime) return 0
  const overtimeMinutes = Math.floor((now - endTime) / 60000)
  return overtimeMinutes * 0.5
})

const overtimeMinutes = computed(() => {
  if (!currentBooking.value?.startTime) return 0
  const endTime = new Date(currentBooking.value.startTime).getTime() + durationMinutes.value * 60 * 1000
  const now = Date.now()
  if (now <= endTime) return 0
  return Math.floor((now - endTime) / 60000)
})

const isOvertime = computed(() => overtimeMinutes.value > 0)

const totalElectricityCost = computed(() => {
  return parseFloat(currentMileage.value) * pricePerKmValue
})

const totalCost = computed(() => {
  return baseCost.value + overtimeCost.value + totalElectricityCost.value
})

const elapsedTime = computed(() => {
  // 订单结束后使用锁定的时间
  if (isTripEnded.value) {
    return lockedElapsedTime.value
  }
  if (!startTime.value || startTime.value <= 0) return '00:00:00'
  const elapsed = Math.max(0, Math.floor((now.value - startTime.value) / 1000))
  const h = Math.floor(elapsed / 3600)
  const m = Math.floor((elapsed % 3600) / 60)
  const s = elapsed % 60
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
})

<<<<<<< Updated upstream
const remainingTimeStr = computed(() => {
  if (!startTime.value) return '00:00'
  const endTime = startTime.value + durationMinutes.value * 60 * 1000
  const remaining = Math.max(0, Math.floor((endTime - Date.now()) / 60000))
  const h = Math.floor(remaining / 60)
  const m = remaining % 60
  return h > 0 ? `${h}小时${m}分` : `${m}分钟`
=======
const remainingTime = computed(() => {
  if (!startTime.value || startTime.value <= 0) return '00:00:00'
  const endTime = startTime.value + durationMinutes.value * 60 * 1000
  const remaining = Math.max(0, Math.floor((endTime - now.value) / 1000))
  const h = Math.floor(remaining / 3600)
  const m = Math.floor((remaining % 3600) / 60)
  const s = remaining % 60
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
>>>>>>> Stashed changes
})

// 电池相关
const batteryColor = computed(() => {
  const b = currentBattery.value
  if (b >= 60) return '#10b981'
  if (b >= 30) return '#f59e0b'
  return '#ef4444'
})

const estimatedRange = computed(() => {
  return Math.round(currentBattery.value * 0.6)
})

// 初始化地图
const initMap = async () => {
  try {
    await nextTick()
    await new Promise(resolve => setTimeout(resolve, 500))

    const AMap = await AMapLoader.load({
      key: '27ec2a64ff4acc99ccf61c8c897a69d3',
      version: '2.0'
    })

    const mapContainer = document.getElementById('riding-map-container')
    if (!mapContainer) return

    // 使用上次位置或默认位置
    const initLng = currentLocation.lng
    const initLat = currentLocation.lat

    map = new AMap.Map('riding-map-container', {
      zoom: 18,
      center: [initLng, initLat],
      resizeEnable: true,
      viewMode: '2D'
    })

    // 添加用户位置标记
    userMarker = new AMap.Marker({
      position: [initLng, initLat],
      icon: new AMap.Icon({
        size: new AMap.Size(40, 40),
        image: 'https://webapi.amap.com/theme/v1.3/markers/n/loc.png',
        imageSize: new AMap.Size(40, 40)
      }),
      offset: new AMap.Pixel(-20, -40)
    })
    map.add(userMarker)

    // 添加轨迹线
    pathLine = new AMap.Polyline({
      strokeColor: '#e07b39',
      strokeWeight: 6,
      strokeOpacity: 0.8,
      path: [[initLng, initLat]]
    })
    map.add(pathLine)

    // 添加服务点标记
    const serviceDepots = [
      { name: '服务点 A', lng: 103.922, lat: 30.746 },
      { name: '服务点 B', lng: 103.936, lat: 30.754 },
      { name: '服务点 C', lng: 103.915, lat: 30.758 },
      { name: '服务点 D', lng: 103.944, lat: 30.739 },
      { name: '服务点 E', lng: 103.928, lat: 30.765 }
    ]

    serviceDepots.forEach(d => {
      new AMap.Marker({
        position: [d.lng, d.lat],
        map: map,
        icon: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png',
        title: d.name
      })
    })

    // 启动 GPS 追踪
    startGPSWatch(AMap)
  } catch (err) {
    console.error('地图初始化失败:', err)
  }
}

// GPS 追踪
const startGPSWatch = (AMap) => {
  if ('geolocation' in navigator) {
    positionWatchId = navigator.geolocation.watchPosition(
      (position) => {
        const newLat = position.coords.latitude
        const newLng = position.coords.longitude
        
        // 检查是否在中国范围内
        if (newLng >= 73 && newLng <= 135 && newLat >= 15 && newLat <= 54) {
          updateLocation(newLat, newLng, AMap)
        }
      },
      (error) => {
        console.warn('GPS 定位失败:', error.message)
      },
      {
        enableHighAccuracy: true,
        maximumAge: 10000,
        timeout: 10000
      }
    )
  }
}

const updateLocation = (lat, lng, AMap) => {
  if (!map || !userMarker) return

  // 更新标记位置
  userMarker.setPosition([lng, lat])
  
  // 更新轨迹
  GPS_POINTS.push([lng, lat])
  if (pathLine) {
    pathLine.setPath(GPS_POINTS)
  }
  
  // 移动地图中心
  map.setCenter([lng, lat])
  
  // 计算里程（米转公里）
  if (GPS_POINTS.length > 1) {
    const lastPoint = GPS_POINTS[GPS_POINTS.length - 2]
    const distance = calculateDistance(lastPoint[1], lastPoint[0], lat, lng)
    currentMileage.value = (parseFloat(currentMileage.value) + distance).toFixed(3)
  }
  
  // 模拟电量消耗
  if (currentBattery.value > 5) {
    currentBattery.value = Math.max(5, currentBattery.value - 0.01)
  }
}

// Haversine 公式计算距离（公里）
const calculateDistance = (lat1, lng1, lat2, lng2) => {
  const R = 6371
  const dLat = (lat2 - lat1) * Math.PI / 180
  const dLng = (lng2 - lng1) * Math.PI / 180
  const a = Math.sin(dLat/2)**2 + Math.cos(lat1*Math.PI/180)*Math.cos(lat2*Math.PI/180)*Math.sin(dLng/2)**2
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
  return R * c
}

// 加载行程数据
const loadBooking = async () => {
  loading.value = true
  // 重置结束状态
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
<<<<<<< Updated upstream
      // 模拟测试数据
      currentBooking.value = {
        id: 'BK001',
        scooterId: 'S001',
        scooterNumber: 'S001',
        startTime: new Date(Date.now() - 15 * 60 * 1000).toISOString(),
        hireOption: '1hr'
      }
      startTime.value = Date.now() - 15 * 60 * 1000
      durationMinutes.value = 60
    }
  } catch (e) {
    console.warn('获取行程失败，使用模拟数据')
    currentBooking.value = {
      id: 'BK001',
      scooterId: 'S001',
      scooterNumber: 'S001',
      startTime: new Date(Date.now() - 15 * 60 * 1000).toISOString(),
      hireOption: '1hr'
    }
    startTime.value = Date.now() - 15 * 60 * 1000
    durationMinutes.value = 60
=======
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
>>>>>>> Stashed changes
  } finally {
    loading.value = false
  }
}

<<<<<<< Updated upstream
// 确认还车
const confirmReturn = async () => {
  returning.value = true
  try {
    await endBooking(currentBooking.value.id)
    ElMessage.success('还车成功！')
    showReturnDialog.value = false
    router.push('/scooters')
  } catch (e) {
    // 模拟成功
    ElMessage.success('还车成功！')
    showReturnDialog.value = false
    router.push('/scooters')
=======
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
    // 使用后端返回的最新订单数据更新本地状态
    if (res?.data) {
      currentBooking.value = res.data
      localStorage.setItem('activeTrip', JSON.stringify(currentBooking.value))
      // 更新 durationMinutes
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
    // 锁定结束时间和时长
    lockedEndTime.value = Date.now()
    const timeValue = currentBooking.value.startTime || currentBooking.value.start_time
    if (timeValue) {
      lockedDuration.value = Math.floor((lockedEndTime.value - new Date(timeValue).getTime()) / 60000)
    }

    await returnScooter(currentBooking.value.id, selectedDepot.value.id)
    finalCost.value = calculateFinalCost()
    localStorage.removeItem('activeTrip')
    returnVisible.value = false
    // 标记订单已结束，锁定显示
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
  router.push('/map')
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
>>>>>>> Stashed changes
  } finally {
    returning.value = false
  }
}

<<<<<<< Updated upstream
// 默认位置（成都）
const currentLocation = { lat: 30.7528, lng: 103.9305 }

onMounted(async () => {
  await loadBooking()
  await initMap()
  
  // 计时器
  timer = setInterval(() => {
    // 触发响应式更新
  }, 1000)
=======
onMounted(() => {
  loadBooking()
  timer = setInterval(() => { now.value = Date.now() }, 1000)
>>>>>>> Stashed changes
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
  if (positionWatchId) navigator.geolocation.clearWatch(positionWatchId)
  if (map) map.destroy()
})
</script>

<style scoped>
<<<<<<< Updated upstream
.riding-page {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #f5f5f5;
  overflow: hidden;
}

/* 顶部导航栏 */
.riding-header {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 56px;
  background: linear-gradient(180deg, rgba(0,0,0,0.6) 0%, rgba(0,0,0,0) 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.scooter-id {
  color: white;
  font-weight: 700;
  font-size: 16px;
  text-shadow: 0 1px 3px rgba(0,0,0,0.3);
}

.header-right :deep(.el-button) {
  background: var(--cg-accent);
  border: none;
  color: white;
  font-weight: 600;
}

/* 地图 */
.riding-map {
  width: 100%;
  height: 100%;
}

/* 右下角计时卡片 */
.timer-card {
  position: absolute;
  bottom: 80px;
  right: 16px;
  background: white;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  z-index: 100;
  min-width: 160px;
}

.timer-main {
  text-align: center;
  margin-bottom: 12px;
}

.timer-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.timer-value {
  font-size: 28px;
  font-weight: 800;
  color: var(--cg-navy);
  font-family: monospace;
}

.timer-divider {
  height: 1px;
  background: #eee;
  margin: 12px 0;
}

.timer-sub {
  display: flex;
  justify-content: space-around;
}

.sub-item {
  text-align: center;
}

.sub-label {
  display: block;
  font-size: 11px;
  color: #999;
  margin-bottom: 2px;
}

.sub-value {
  font-size: 14px;
  font-weight: 600;
  color: var(--cg-text);
}

/* 左下角电量卡片 */
.battery-card {
  position: absolute;
  bottom: 80px;
  left: 16px;
  background: white;
  border-radius: 12px;
  padding: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  z-index: 100;
  width: 100px;
}

.battery-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.battery-icon {
  font-size: 18px;
}

.battery-value {
  font-size: 18px;
  font-weight: 700;
  color: var(--cg-text);
}

.battery-info {
  margin-top: 6px;
  font-size: 11px;
  color: #999;
  text-align: center;
}

/* 底部信息栏 */
.bottom-info-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(10px);
  padding: 12px 20px;
  display: flex;
  justify-content: center;
  gap: 24px;
  z-index: 100;
  border-top: 1px solid #eee;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--cg-text);
}

.info-item.warning {
  color: #ef4444;
  font-weight: 600;
}

/* 还车弹窗 */
.return-summary {
  background: #f9f9f9;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 15px;
  color: var(--cg-text);
}

.summary-row.total {
  font-weight: 700;
  font-size: 18px;
}

.summary-row .overtime {
  color: #ef4444;
}

.summary-row .price {
  color: var(--cg-accent);
  font-size: 22px;
}

.return-tips {
  background: #fff7e6;
  border-radius: 8px;
  padding: 12px;
  margin-top: 12px;
}

.return-tips p {
  margin: 0;
  font-size: 13px;
  color: #b45309;
}

:deep(.el-divider) {
  margin: 8px 0;
=======
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

/* 趣味进度环 */
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

/* 延长租用弹窗 */
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

/* 还车列表 */
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

/* 完成弹窗 */
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

/* 空状态 */
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
>>>>>>> Stashed changes
}
</style>
