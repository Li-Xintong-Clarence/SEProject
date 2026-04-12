<template>
  <div class="scooter-list">
    <div class="page-header">
      <h2 class="page-title">可租滑板车</h2>
      <p class="page-sub">查看可用状态与坐标 · <router-link to="/scooters">打开地图模式</router-link></p>
    </div>

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
import { ElMessage } from 'element-plus'
import MapView from './MapView.vue'

const router = useRouter()
const viewMode = ref('list')
const scooters = ref([])
const loading = ref(false)

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
  router.push({ path: '/booking', query: { scooterId } })
}

onMounted(async () => {
  loading.value = true
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
  padding: 20px;
  max-width: 1280px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 16px;
}

.page-title {
  margin: 0 0 6px;
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--cg-navy);
}

.page-sub {
  margin: 0;
  font-size: 0.9rem;
  color: #6b7280;
}

.page-sub a {
  color: var(--cg-navy-soft);
  font-weight: 600;
}

.view-toggle {
  margin-bottom: 20px;
}

.loading {
  padding: 40px;
}

.scooter-card {
  margin-bottom: 20px;
  border-radius: var(--cg-radius-md);
  transition: all 0.2s;
}

.scooter-card:hover {
  transform: translateY(-2px);
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
  color: var(--cg-navy);
}

.card-meta p {
  margin: 6px 0;
  font-size: 14px;
  color: #555;
  display: flex;
  align-items: center;
  gap: 6px;
}

.book-btn {
  width: 100%;
  margin-top: 12px;
}
</style>
