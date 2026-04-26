<template>
  <div class="current-trip">
    <h2 class="page-title">当前行程</h2>
    <p class="page-sub">CapyGlide · 正在使用的滑板车</p>

    <el-skeleton v-if="loading" :rows="8" animated />

    <!-- 有正在使用的订单 -->
    <template v-else-if="activeBooking">
      <el-card class="trip-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span><el-icon>< Van /></el-icon> 正在租用</span>
            <el-tag type="warning">进行中</el-tag>
          </div>
        </template>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="滑板车">
            {{ activeBooking.scooterName || `车 #${activeBooking.scooterId}` }}
          </el-descriptions-item>
          <el-descriptions-item label="确认码">
            <strong>{{ activeBooking.confirmationCode }}</strong>
          </el-descriptions-item>
          <el-descriptions-item label="租用时长">
            {{ formatDuration(activeBooking.hireOption) }}
          </el-descriptions-item>
          <el-descriptions-item label="已用时长">
            <span :class="{ 'overdue': isOverdue }">
              {{ usedDuration }}
              <el-tag v-if="isOverdue" type="danger" size="small">已超时</el-tag>
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="开始时间">
            {{ formatTime(activeBooking.startTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="预计结束">
            {{ formatTime(activeBooking.endTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="应付金额">
            <strong>¥{{ activeBooking.totalCost ?? activeBooking.totalPrice ?? 0 }}</strong>
          </el-descriptions-item>
        </el-descriptions>

        <div class="action-buttons">
          <el-button type="primary" @click="viewConfirmation">
            <el-icon><View /></el-icon> 查看确认信息
          </el-button>
          <el-button type="warning" @click="openExtend">
            <el-icon><Clock /></el-icon> 延长租期
          </el-button>
          <el-button type="success" @click="handleReturn">
            <el-icon><Finished /></el-icon> 结束行程（还车）
          </el-button>
        </div>
      </el-card>
    </template>

    <!-- 没有正在使用的订单 -->
    <el-empty v-else description="当前没有正在进行的行程">
      <template #extra>
        <el-button type="primary" @click="goToScooters">去租车</el-button>
      </template>
    </el-empty>

    <!-- 延长租期弹窗 -->
    <el-dialog v-model="extendVisible" title="延长租期" width="480px">
      <p style="margin-bottom: 16px;">请选择延长的时长：</p>
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

    <!-- 确认信息弹窗 -->
    <el-dialog v-model="confirmVisible" title="预订确认信息" width="520px">
      <el-descriptions v-if="confirmation" :column="1" border>
        <el-descriptions-item label="确认码">{{ confirmation.confirmationCode }}</el-descriptions-item>
        <el-descriptions-item label="滑板车">{{ confirmation.scooterName || `车 #${confirmation.scooterId}` }}</el-descriptions-item>
        <el-descriptions-item label="租期">{{ formatDuration(confirmation.hireOption) }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ formatTime(confirmation.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ formatTime(confirmation.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="总费用">¥{{ confirmation.totalCost }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusText(confirmation.status) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Van, View, Clock, Finished } from '@element-plus/icons-vue'
import { getUserBookings, extendBooking, getBookingConfirmation, returnScooter } from '@/api/booking'

const router = useRouter()

const loading = ref(true)
const activeBooking = ref(null)
const bookings = ref([])

// 延长弹窗
const extendVisible = ref(false)
const extendHireOption = ref('1hr')
const extendLoading = ref(false)

// 确认信息弹窗
const confirmVisible = ref(false)
const confirmation = ref(null)

// 定时器用于更新已用时长
let timer = null

// 格式化时长
const formatDuration = (code) => {
  const m = { '1hr': '1小时', '4hr': '4小时', '1day': '1天', '1week': '1周' }
  return m[code] || code
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '—'
  const date = new Date(time)
  if (isNaN(date.getTime())) return String(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

// 状态文字
const getStatusText = (status) => {
  const u = (status || '').toUpperCase()
  const map = { ACTIVE: '进行中', PAID: '已支付', COMPLETED: '已完成', CANCELLED: '已取消', PENDING: '待支付' }
  return map[u] || status
}

// 计算已用时长
const usedDuration = computed(() => {
  if (!activeBooking.value?.startTime) return '—'
  const start = new Date(activeBooking.value.startTime)
  const now = new Date()
  const diff = Math.floor((now - start) / 1000 / 60) // 分钟
  const hours = Math.floor(diff / 60)
  const mins = diff % 60
  return hours > 0 ? `${hours}小时${mins}分钟` : `${mins}分钟`
})

// 是否已超时
const isOverdue = computed(() => {
  if (!activeBooking.value?.endTime) return false
  const end = new Date(activeBooking.value.endTime)
  return new Date() > end
})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserBookings()
    bookings.value = Array.isArray(res) ? res : []

    // 查找当前进行中的订单
    activeBooking.value = bookings.value.find(b => {
      const s = (b.status || '').toUpperCase()
      return s === 'ACTIVE' || s === 'PAID'
    }) || null

    if (activeBooking.value) {
      activeBooking.value.scooterName = activeBooking.value.scooterName ||
        activeBooking.value.scooterNumber ||
        `车 #${activeBooking.value.scooterId}`
    }
  } catch (e) {
    console.error('加载行程失败', e)
    activeBooking.value = null
  } finally {
    loading.value = false
  }
}

// 查看确认信息
const viewConfirmation = async () => {
  if (!activeBooking.value) return
  try {
    confirmation.value = await getBookingConfirmation(activeBooking.value.id)
    confirmVisible.value = true
  } catch (e) {
    ElMessage.error('获取确认信息失败')
  }
}

// 打开延长弹窗
const openExtend = () => {
  if (!activeBooking.value) return
  extendHireOption.value = '1hr'
  extendVisible.value = true
}

// 提交延长
const submitExtend = async () => {
  if (!activeBooking.value) return
  extendLoading.value = true
  try {
    await extendBooking(activeBooking.value.id, extendHireOption.value)
    ElMessage.success('租期已延长')
    extendVisible.value = false
    await loadData()
  } catch (e) {
    const msg = e?.response?.data?.message || e?.message || '延长失败'
    ElMessage.error(msg)
  } finally {
    extendLoading.value = false
  }
}

// 还车
const handleReturn = () => {
  if (!activeBooking.value) return
  ElMessageBox.confirm(
    '确定要结束行程并还车吗？还车后计时将停止。',
    '确认还车',
    { type: 'warning' }
  ).then(async () => {
    try {
      await returnScooter(activeBooking.value.id)
      ElMessage.success('还车成功！感谢使用 CapyGlide')
      activeBooking.value = null
      await loadData()
    } catch (e) {
      const msg = e?.response?.data?.message || e?.message || '还车失败'
      ElMessage.error(msg)
    }
  }).catch(() => {})
}

// 去租车
const goToScooters = () => {
  router.push('/scooters')
}

onMounted(() => {
  loadData()
  // 每分钟更新已用时长
  timer = setInterval(loadData, 60000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.current-trip {
  padding: 32px 24px;
  max-width: 900px;
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

.trip-card {
  border-radius: var(--cg-radius-xl);
  box-shadow: var(--cg-shadow-md);
  border: 1px solid var(--cg-border-light);
  overflow: hidden;
}

.trip-card :deep(.el-card__header) {
  background: var(--cg-gradient-navy);
  color: white;
  padding: 20px 24px;
  border: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  font-size: 16px;
  color: white;
}

.card-header :deep(.el-tag) {
  background: rgba(255, 255, 255, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: white;
}

.trip-card :deep(.el-card__body) {
  padding: 24px;
}

.action-buttons {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  flex-wrap: wrap;
}

.action-buttons :deep(.el-button) {
  border-radius: var(--cg-radius-md);
  font-weight: 600;
  padding: 12px 24px;
}

.overdue {
  color: var(--cg-danger);
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
