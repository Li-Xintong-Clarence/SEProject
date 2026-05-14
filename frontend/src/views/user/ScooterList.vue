<template>
  <div class="scooter-list">
    <div class="page-header">
      <div class="header-row">
        <div class="header-info">
          <h2 class="page-title">可租滑板车</h2>
          <p class="page-sub">查看可用状态与坐标 · <router-link to="/scooters">打开地图模式</router-link></p>
        </div>
        <div class="header-actions">
          <span v-if="lastUpdate" class="last-update">更新: {{ lastUpdate }}</span>
          <el-button type="primary" @click="handleRefresh" :loading="loading" circle title="刷新列表">
            <el-icon><Refresh /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <el-alert v-if="hasActiveBooking" type="warning" :closable="false" class="active-alert">
      <template #title>
        您有正在进行的行程
        <el-button type="warning" size="small" @click="$router.push('/trip')" style="margin-left: 12px;">
          前往当前行程
        </el-button>
      </template>
    </el-alert>

    <div class="view-toggle">
      <el-button-group>
        <el-button :type="viewMode === 'list' ? 'primary' : ''" @click="viewMode = 'list'">列表模式</el-button>
        <el-button :type="viewMode === 'map' ? 'primary' : ''" @click="viewMode = 'map'">地图模式</el-button>
      </el-button-group>
    </div>

    <div v-if="viewMode === 'map'">
      <MapView />
    </div>

    <div v-else>
      <div v-if="loading" class="loading">
        <el-skeleton :rows="4" animated />
      </div>

      <el-row v-else :gutter="20">
        <el-col v-for="scooter in scooters" :key="scooter.id" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card class="scooter-card" :class="{ 'unavailable': scooter.status !== 'AVAILABLE' }" shadow="hover">
            <div class="card-header">
              <h3>{{ scooter.scooterNumber || scooter.name || '未知滑板车' }}</h3>
              <el-tag :type="statusType(scooter.status)" size="small">{{ statusText(scooter.status) }}</el-tag>
            </div>
            <div class="card-meta">
              <p><el-icon><Location /></el-icon> 位置：{{ scooter.location || `${scooter.latitude || scooter.lat}, ${scooter.longitude || scooter.lng}` }}</p>
              <p>
                <span>电量：</span>
                <span :class="{ 'low-battery': scooter.batteryLevel < 20 }">{{ scooter.batteryLevel ?? '—' }}%</span>
                <span v-if="scooter.batteryLevel < 20" class="battery-warning">低</span>
              </p>
            </div>
            <el-button
              :type="scooter.status === 'AVAILABLE' ? 'primary' : 'info'"
              class="book-btn"
              :disabled="scooter.status !== 'AVAILABLE'"
              @click="handleBook(scooter.id)"
            >
              {{ scooter.status === 'AVAILABLE' ? '立即预订' : statusText(scooter.status) }}
            </el-button>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-if="!loading && scooters.length === 0" description="暂无车辆数据" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Location, Refresh } from '@element-plus/icons-vue'
import { getScooters } from '@/api/scooter'
import { getMyActiveBookings } from '@/api/booking'
import { ElMessage } from 'element-plus'
import MapView from './MapView.vue'

const router = useRouter()
const viewMode = ref('list')
const scooters = ref([])
const loading = ref(false)
const hasActiveBooking = ref(false)
const lastUpdate = ref('')
let refreshTimer = null

// 格式化更新时间
const formatLastUpdate = () => {
  const now = new Date()
  lastUpdate.value = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

const statusType = (s) => {
  const u = String(s || '').toUpperCase()
  if (u === 'AVAILABLE') return 'success'
  if (u === 'IN_USE') return 'warning'
  return 'info'
}

const statusText = (s) => {
  const u = String(s || '').toUpperCase()
  if (u === 'AVAILABLE') return '可用'
  if (u === 'IN_USE') return '使用中'
  if (u === 'MAINTENANCE') return '维护中'
  return s || '未知'
}

const handleBook = (scooterId) => {
  if (hasActiveBooking.value) {
    ElMessage.warning('您已有正在进行的行程，请先完成或取消当前行程')
    router.push('/trip')
    return
  }
  router.push({ path: '/booking', query: { scooterId } })
}

// 统一检查进行中订单的方法
const checkActiveBooking = async () => {
  try {
    const res = await getMyActiveBookings()
    // 兼容多种响应格式：{ code: 200, data: {...} } 或直接返回对象
    const booking = res?.data || res
    hasActiveBooking.value = !!(booking && booking.id)
  } catch {
    hasActiveBooking.value = false
  }
}

// 刷新车辆列表
const handleRefresh = async () => {
  loading.value = true
  await loadScooters()
  loading.value = false
}

// 刷新车辆列表
const loadScooters = async () => {
  try {
    const res = await getScooters()
    const newList = Array.isArray(res) ? res : (res?.data || [])
    // 记录状态变化
    newList.forEach(newScooter => {
      const oldScooter = scooters.value.find(s => s.id === newScooter.id)
      if (oldScooter && oldScooter.status !== newScooter.status) {
        // 状态变化时显示提示
        if (newScooter.status === 'IN_USE') {
          ElMessage.warning(`车辆 ${newScooter.scooterNumber || newScooter.name || newScooter.id} 已被租用`)
        }
      }
    })
    scooters.value = newList
    formatLastUpdate()
  } catch (error) {
    console.error(error)
    ElMessage.error('获取滑板车列表失败，请稍后重试')
  }
}

// 启动定时刷新
const startAutoRefresh = () => {
  // 每 30 秒刷新一次
  refreshTimer = setInterval(() => {
    loadScooters()
  }, 30000)
}

// 停止定时刷新
const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

onMounted(async () => {
  loading.value = true
  await checkActiveBooking()
  await loadScooters()
  loading.value = false
  startAutoRefresh()
})

// 组件卸载时清理
import { onUnmounted } from 'vue'
onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style scoped>
.scooter-list {
  padding: 32px 24px;
  max-width: 1280px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  margin: 0 0 6px;
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--cg-text);
  letter-spacing: -0.02em;
}

.page-sub {
  margin: 0;
  font-size: 15px;
  color: var(--cg-text-light);
}

.page-sub a {
  color: var(--cg-primary);
  font-weight: 600;
  text-decoration: none;
  transition: var(--cg-transition);
}

.page-sub a:hover {
  color: var(--cg-highlight);
}

.view-toggle {
  margin-bottom: 24px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.last-update {
  font-size: 12px;
  color: var(--cg-text-muted);
}

.active-alert {
  margin-bottom: 24px;
}

.active-alert :deep(.el-alert__title) {
  display: flex;
  align-items: center;
  font-weight: 600;
}

.loading {
  padding: 40px;
}

.scooter-card {
  margin-bottom: 20px;
  border-radius: var(--cg-radius-lg);
  border: 1px solid var(--cg-border-light);
  transition: var(--cg-transition);
  overflow: hidden;
}

.scooter-card.unavailable {
  opacity: 0.7;
  background: #f9fafb;
}

.scooter-card.unavailable .card-header h3 {
  color: var(--cg-text-muted);
}

.scooter-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--cg-shadow-lg);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.card-header h3 {
  margin: 0;
  font-size: 1rem;
  font-weight: 700;
  color: var(--cg-text);
}

.card-meta p {
  margin: 6px 0;
  font-size: 14px;
  color: var(--cg-text-light);
  display: flex;
  align-items: center;
  gap: 6px;
}

.low-battery {
  color: #ef4444;
  font-weight: 600;
}

.battery-warning {
  background: #fef2f2;
  color: #ef4444;
  font-size: 11px;
  font-weight: 600;
  padding: 1px 6px;
  border-radius: 4px;
  border: 1px solid #fecaca;
}

.book-btn {
  width: 100%;
  margin-top: 16px;
  border-radius: var(--cg-radius-md);
  font-weight: 600;
  background: var(--cg-gradient) !important;
  border: none !important;
}

.book-btn:hover {
  box-shadow: var(--cg-shadow-accent);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .scooter-list {
    padding: 24px 20px;
  }

  .page-title {
    font-size: 1.5rem;
  }
}

@media (max-width: 768px) {
  .header-row {
    flex-direction: column;
    gap: 12px;
  }

  .header-actions {
    align-self: flex-start;
  }
}

@media (max-width: 768px) {
  .scooter-list {
    padding: 16px 12px;
  }

  .page-header {
    margin-bottom: 16px;
  }

  .page-title {
    font-size: 1.35rem;
  }

  .page-sub {
    font-size: 13px;
  }

  .view-toggle {
    margin-bottom: 16px;
  }

  .active-alert {
    margin-bottom: 16px;
  }

  .scooter-card {
    margin-bottom: 16px;
  }

  .card-header h3 {
    font-size: 0.95rem;
  }

  .card-meta p {
    font-size: 13px;
  }
}

@media (max-width: 600px) {
  .scooter-list {
    padding: 12px 8px;
  }

  .page-title {
    font-size: 1.2rem;
  }

  .page-sub {
    font-size: 12px;
  }

  .scooter-card {
    margin-bottom: 12px;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .card-meta {
    margin-bottom: 12px;
  }

  .book-btn {
    margin-top: 8px;
  }

  .view-toggle :deep(.el-button-group) {
    width: 100%;
    display: flex;
  }

  .view-toggle :deep(.el-button) {
    flex: 1;
  }
}

@media (max-width: 480px) {
  .scooter-list {
    padding: 8px 6px;
  }

  .page-title {
    font-size: 1.1rem;
  }

  .scooter-list :deep(.el-row) {
    margin-left: 0 !important;
    margin-right: 0 !important;
  }

  .scooter-list :deep(.el-col) {
    padding-left: 0 !important;
    padding-right: 0 !important;
    margin-bottom: 12px;
  }

  .card-header h3 {
    font-size: 0.9rem;
  }

  .card-meta p {
    font-size: 12px;
  }
}

@media (max-width: 380px) {
  .page-title {
    font-size: 1rem;
  }

  .page-sub {
    font-size: 11px;
  }

  .active-alert :deep(.el-alert__title) {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .active-alert :deep(.el-alert__title) .el-button {
    margin-left: 0 !important;
  }
}
</style>
