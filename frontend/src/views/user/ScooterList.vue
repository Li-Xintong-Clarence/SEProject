<template>
  <div class="scooter-list">
    <el-page-header title="返回" @back="goBack" />
    <h2 class="page-title">可租滑板车</h2>
    <p class="page-lead">
      查看可用状态与坐标 ·
      <router-link to="/scooters">打开地图模式</router-link>
    </p>

    <div v-if="loading" class="loading">
      <el-skeleton :rows="4" animated />
    </div>

    <el-row v-else :gutter="20">
      <el-col v-for="scooter in scooters" :key="scooter.id" :xs="24" :sm="12" :md="8" :lg="6">
        <el-card class="scooter-card" shadow="hover">
          <h3>{{ scooter.scooterNumber }}</h3>
          <p class="meta">
            位置：
            <template v-if="scooter.location">{{ scooter.location }}</template>
            <template v-else>{{ scooter.latitude }}, {{ scooter.longitude }}</template>
          </p>
          <p class="meta">电量：{{ scooter.batteryLevel }}%</p>
          <p>
            <el-tag :type="statusType(scooter.status)" size="small">
              {{ statusText(scooter.status) }}
            </el-tag>
          </p>
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getScooters } from '@/api/scooter'
import { ElMessage } from 'element-plus'

const router = useRouter()
const scooters = ref([])
const loading = ref(false)

const goBack = () => router.back()

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
    scooters.value = Array.isArray(res) ? res : []
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
.page-title {
  margin: 0 0 6px;
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--cg-navy);
}
.page-lead {
  margin: 0 0 20px;
  font-size: 0.92rem;
  color: #6b7280;
}
.page-lead a {
  color: var(--cg-navy-soft);
  font-weight: 600;
}
.loading {
  padding: 40px;
}
.scooter-card {
  margin-bottom: 20px;
  border-radius: var(--cg-radius-md);
}
.scooter-card h3 {
  margin-top: 0;
  color: var(--cg-navy);
}
.meta {
  font-size: 14px;
  color: #555;
  margin: 8px 0;
}
.book-btn {
  width: 100%;
  margin-top: 12px;
}
</style>
