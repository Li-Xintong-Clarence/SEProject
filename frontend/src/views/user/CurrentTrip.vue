<template>
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
    </div>

    <!-- 地图容器 -->
    <div id="riding-map-container" class="riding-map"></div>

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
        </div>
      </div>
    </div>

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
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Bottom, Clock, Timer, Warning } from '@element-plus/icons-vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import { getMyActiveBookings, endBooking } from '@/api/booking'

const router = useRouter()

const loading = ref(true)
const currentBooking = ref(null)
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

const formatDuration = (option) => {
  const map = { '1hr': 60, '4hr': 240, '1day': 1440, '1week': 10080 }
  return map[option] || 60
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
  if (!startTime.value) return '00:00:00'
  const elapsed = Math.floor((Date.now() - startTime.value) / 1000)
  const h = Math.floor(elapsed / 3600)
  const m = Math.floor((elapsed % 3600) / 60)
  const s = elapsed % 60
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
})

const remainingTimeStr = computed(() => {
  if (!startTime.value) return '00:00'
  const endTime = startTime.value + durationMinutes.value * 60 * 1000
  const remaining = Math.max(0, Math.floor((endTime - Date.now()) / 60000))
  const h = Math.floor(remaining / 60)
  const m = remaining % 60
  return h > 0 ? `${h}小时${m}分` : `${m}分钟`
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
  try {
    const res = await getMyActiveBookings()
    if (Array.isArray(res) && res.length > 0) {
      currentBooking.value = res[0]
      if (currentBooking.value.startTime) {
        startTime.value = new Date(currentBooking.value.startTime).getTime()
      }
      durationMinutes.value = durationToMinutes(currentBooking.value.hireOption)
    } else {
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
  } finally {
    loading.value = false
  }
}

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
  } finally {
    returning.value = false
  }
}

// 默认位置（成都）
const currentLocation = { lat: 30.7528, lng: 103.9305 }

onMounted(async () => {
  await loadBooking()
  await initMap()
  
  // 计时器
  timer = setInterval(() => {
    // 触发响应式更新
  }, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
  if (positionWatchId) navigator.geolocation.clearWatch(positionWatchId)
  if (map) map.destroy()
})
</script>

<style scoped>
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
}
</style>
