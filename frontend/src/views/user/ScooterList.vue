<template>
  <div class="scooter-list">
    <div class="page-header">
      <h2 class="page-title">可租滑板车</h2>
      <p class="page-sub">查看可用状态与坐标 · <router-link to="/scooters">打开地图模式</router-link></p>
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
          <el-card class="scooter-card" shadow="hover">
            <div class="card-header">
              <h3>{{ scooter.scooterNumber || scooter.name || '未知滑板车' }}</h3>
              <el-tag :type="statusType(scooter.status)" size="small">{{ statusText(scooter.status) }}</el-tag>
            </div>
            <div class="card-meta">
              <p><el-icon><Location /></el-icon> 位置：{{ scooter.location || `${scooter.latitude || scooter.lat}, ${scooter.longitude || scooter.lng}` }}</p>
              <p><span>电量：{{ scooter.batteryLevel ?? '—' }}%</span></p>
            </div>
            <el-button
              type="primary"
              class="book-btn"
              :disabled="String(scooter.status).toUpperCase() !== 'AVAILABLE'"
              @click="handleBook(scooter.id)"
            >
              立即预订
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
import { Location } from '@element-plus/icons-vue'
import { getScooters } from '@/api/scooter'
import { getUserBookings } from '@/api/booking'
import { ElMessage } from 'element-plus'
import MapView from './MapView.vue'

const router = useRouter()
const viewMode = ref('list')
const scooters = ref([])
const loading = ref(false)
const hasActiveBooking = ref(false)

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

// 检查是否有进行中的订单
const checkActiveBooking = async () => {
  try {
    const res = await getUserBookings()
    const list = Array.isArray(res) ? res : []
    hasActiveBooking.value = list.some(b => {
      const s = (b.status || '').toUpperCase()
      return s === 'ACTIVE' || s === 'PAID'
    })
  } catch {
    hasActiveBooking.value = false
  }
}

onMounted(async () => {
  loading.value = true
  await checkActiveBooking()
  try {
    const res = await getScooters()
    scooters.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (error) {
    console.error(error)
    ElMessage.error('获取滑板车列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
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
</style>
