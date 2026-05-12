<template>
  <div class="map-view">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">找车</h2>
      <p class="page-sub">CapyGlide · 附近滑板车</p>
    </div>
    
    <div class="header-actions">
      <el-button type="primary" @click="$router.push('/scan')">
        <el-icon><Crop /></el-icon> 扫码租车
      </el-button>
    </div>

    <!-- 行程提示 -->
    <div v-if="hasActiveBooking" class="booking-banner">
      <div class="banner-content">
        <div class="banner-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="12" cy="12" r="10"/>
            <path d="M12 6v6l4 2"/>
          </svg>
        </div>
        <div class="banner-info">
          <span class="banner-title">您有正在进行的行程</span>
          <span class="banner-sub">点击前往管理您的滑行</span>
        </div>
      </div>
      <el-button type="primary" size="small" @click="$router.push('/trip')" class="banner-btn">
        <el-icon><Van /></el-icon>
        前往行程
      </el-button>
    </div>

    <div class="content-grid">
      <!-- 左侧地图 -->
      <div class="map-area">
        <div class="map-container">
          <div id="map-container"></div>
        </div>

        <!-- 定位栏 -->
        <div class="location-bar">
          <div class="location-info" :class="{ active: userLocated }">
            <div class="loc-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"/>
                <circle cx="12" cy="10" r="3"/>
              </svg>
            </div>
            <span v-if="locating">定位中...</span>
            <span v-else-if="userLocated">{{ locationText }}</span>
            <span v-else>点击定位获取位置</span>
          </div>
          <el-button type="primary" size="small" @click="refreshLocation" :loading="locating" class="loc-btn">
            <el-icon><Location /></el-icon>
            {{ userLocated ? '刷新' : '定位' }}
          </el-button>
        </div>

        <!-- 图例 -->
        <div class="legend-bar">
          <div class="legend-item"><span class="legend-dot user"></span> 我的位置</div>
          <div class="legend-item"><span class="legend-dot avail"></span> 可用</div>
          <div class="legend-item"><span class="legend-dot inuse"></span> 使用中</div>
          <div class="legend-item"><span class="legend-dot low"></span> 低电量</div>
        </div>
      </div>

      <!-- 右侧列表 -->
      <div class="list-area">
        <div class="list-header">
          <h3 class="list-title">附近滑板车</h3>
          <el-button size="small" @click="loadScooters" :loading="loadingScooters" class="refresh-btn">
            <el-icon><Refresh /></el-icon>
          </el-button>
        </div>

        <!-- 搜索 -->
        <div class="search-bar">
          <el-icon class="search-icon"><Search /></el-icon>
          <input v-model="searchKeyword" placeholder="搜索滑板车..." class="search-input" />
        </div>

        <div v-if="loadingScooters" class="loading-state">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>加载中...</span>
        </div>

        <div v-else-if="filteredScooters.length === 0" class="empty-state">
          <div class="empty-illustration">
            <svg viewBox="0 0 120 120" class="empty-svg">
              <circle cx="60" cy="60" r="50" fill="#f0f4f8"/>
              <circle cx="30" cy="85" r="12" stroke="#1e3a5f" stroke-width="3" fill="none"/>
              <circle cx="90" cy="85" r="12" stroke="#1e3a5f" stroke-width="3" fill="none"/>
              <path d="M30 85L45 50H75L90 85" stroke="#1e3a5f" stroke-width="3" stroke-linecap="round" fill="none"/>
              <path d="M45 50L55 35H65" stroke="#1e3a5f" stroke-width="3" stroke-linecap="round" fill="none"/>
              <rect x="52" y="32" width="10" height="5" rx="1" fill="#1e3a5f"/>
              <circle cx="60" cy="20" r="15" fill="#1e3a5f" opacity="0.1"/>
              <text x="60" y="25" text-anchor="middle" font-size="16" fill="#1e3a5f">?</text>
            </svg>
          </div>
          <p>暂无滑板车</p>
          <span class="empty-tip">稍后再来看看吧</span>
        </div>

        <div v-else class="scooter-list">
          <div
            v-for="scooter in filteredScooters"
            :key="scooter.id"
            class="scooter-card"
            :class="{
              'avail': scooter.status === 'AVAILABLE',
              'inuse': scooter.status === 'IN_USE',
              'low': scooter.batteryLevel < 30
            }"
            @click="showScooterDetail(scooter)"
          >
            <div class="card-icon">
              <svg viewBox="0 0 64 64" fill="none">
                <circle cx="14" cy="50" r="10" stroke="currentColor" stroke-width="2.5"/>
                <circle cx="50" cy="50" r="10" stroke="currentColor" stroke-width="2.5"/>
                <path d="M14 50L24 30H40L50 50" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
                <path d="M24 30L30 20H38" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
                <rect x="28" y="18" width="12" height="4" rx="1" fill="currentColor"/>
              </svg>
            </div>
            <div class="card-info">
              <span class="card-name">{{ scooter.scooterNumber }}</span>
              <span class="card-loc">{{ scooter.location || '位置未知' }}</span>
            </div>
            <div class="card-meta">
              <div class="battery" :class="getBatteryClass(scooter.batteryLevel)">
                <span class="battery-icon">⚡</span>
                <span>{{ Math.round(scooter.batteryLevel) }}%</span>
              </div>
              <div class="status" :class="scooter.status.toLowerCase().replace('_', '-')">
                {{ getStatusText(scooter.status) }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog v-model="showDetail" :title="null" width="420px" class="detail-dialog">
      <div v-if="selectedScooter" class="detail-content">
        <div class="detail-header">
          <div class="detail-icon">
            <svg viewBox="0 0 64 64" fill="none">
              <circle cx="14" cy="50" r="10" stroke="currentColor" stroke-width="2.5"/>
              <circle cx="50" cy="50" r="10" stroke="currentColor" stroke-width="2.5"/>
              <path d="M14 50L24 30H40L50 50" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
              <path d="M24 30L30 20H38" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
              <rect x="28" y="18" width="12" height="4" rx="1" fill="currentColor"/>
            </svg>
          </div>
          <div class="detail-info">
            <h4>{{ selectedScooter.scooterNumber }}</h4>
            <p>{{ selectedScooter.location || '位置未知' }}</p>
          </div>
          <div class="detail-battery" :class="getBatteryClass(selectedScooter.batteryLevel)">
            <span class="bt-icon">⚡</span>
            <span class="bt-val">{{ Math.round(selectedScooter.batteryLevel) }}%</span>
          </div>
        </div>

        <div class="detail-stats">
          <div class="stat-cell">
            <span class="stat-key">状态</span>
            <span class="stat-val" :class="selectedScooter.status.toLowerCase().replace('_', '-')">
              {{ getStatusText(selectedScooter.status) }}
            </span>
          </div>
          <div class="stat-cell" v-if="selectedScooter.lastMaintenanceDate">
            <span class="stat-key">上次维护</span>
            <span class="stat-val">{{ formatDate(selectedScooter.lastMaintenanceDate) }}</span>
          </div>
        </div>

        <div class="detail-progress">
          <div class="progress-head">
            <span>电量</span>
            <span>{{ Math.round(selectedScooter.batteryLevel) }}%</span>
          </div>
          <div class="progress-bar">
            <div class="progress-fill" :class="getBatteryClass(selectedScooter.batteryLevel)" :style="{ width: selectedScooter.batteryLevel + '%' }"></div>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="showDetail = false">关闭</el-button>
        <el-button
          v-if="selectedScooter && selectedScooter.status === 'AVAILABLE'"
          type="primary"
          size="large"
          @click="rentScooter"
          :disabled="hasActiveBooking"
          class="rent-btn"
        >
          立即租用
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Location, Van, Loading, Search, Refresh, Crop } from '@element-plus/icons-vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import { getScooters } from '@/api/scooter'
import { getMyActiveBookings } from '@/api/booking'
import { ElMessage } from 'element-plus'

const router = useRouter()
const scooters = ref([])
const searchKeyword = ref('')
const hasActiveBooking = ref(false)
const locating = ref(false)
const userLocated = ref(false)
const locationText = ref('')
const loadingScooters = ref(false)
const showDetail = ref(false)
const selectedScooter = ref(null)

let mapInstance = null
let AMapInstance = null
let userMarker = null
let scooterMarkers = []

const DEFAULT_LNG = 103.9305
const DEFAULT_LAT = 30.7528

const filteredScooters = computed(() => {
  if (!searchKeyword.value) return scooters.value
  const keyword = searchKeyword.value.toLowerCase()
  return scooters.value.filter(s => 
    (s.scooterNumber && s.scooterNumber.toLowerCase().includes(keyword)) ||
    (s.location && s.location.toLowerCase().includes(keyword))
  )
})

const checkActiveBooking = async () => {
  try {
    const res = await getMyActiveBookings()
    const booking = res?.data || res
    hasActiveBooking.value = !!(booking && booking.id)
  } catch {
    hasActiveBooking.value = false
  }
}

const getBrowserLocation = () => {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('不支持定位'))
      return
    }
    navigator.geolocation.getCurrentPosition(
      (position) => resolve({ lat: position.coords.latitude, lng: position.coords.longitude }),
      () => reject(new Error('定位失败')),
      { enableHighAccuracy: true, timeout: 15000 }
    )
  })
}

const reverseGeocode = (lng, lat) => {
  return new Promise((resolve) => {
    AMapInstance.plugin('AMap.Geocoder', () => {
      const geocoder = new AMap.Geocoder()
      geocoder.getAddress([lng, lat], (status, result) => {
        if (status === 'complete' && result.regeocode) {
          const address = result.regeocode.formattedAddress
          const shortAddress = address.replace(/^(四川省|成都市|.*?区|.*?县)/, '')
          resolve(shortAddress || '未知位置')
        } else {
          resolve('未知位置')
        }
      })
    })
  })
}

const updateUserMarker = (AMap, lng, lat) => {
  if (userMarker) mapInstance.remove(userMarker)
  const content = document.createElement('div')
  content.className = 'user-marker'
  content.innerHTML = `<div class="marker-ring"></div><div class="marker-dot"></div>`
  userMarker = new AMap.Marker({
    position: [lng, lat],
    content: content,
    offset: new AMap.Pixel(-16, -16)
  })
  mapInstance.add(userMarker)
}

const refreshLocation = async () => {
  if (!AMapInstance || !mapInstance) {
    ElMessage.warning('地图加载中...')
    return
  }
  locating.value = true
  try {
    const pos = await getBrowserLocation()
    if (pos.lng >= 73 && pos.lng <= 135 && pos.lat >= 15 && pos.lat <= 54) {
      mapInstance.setCenter([pos.lng, pos.lat])
      mapInstance.setZoom(16)
      updateUserMarker(AMapInstance, pos.lng, pos.lat)
      locationText.value = await reverseGeocode(pos.lng, pos.lat)
      userLocated.value = true
    }
  } catch {
    ElMessage.warning('定位失败')
  }
  locating.value = false
}

const loadScooters = async () => {
  loadingScooters.value = true
  try {
    const res = await getScooters()
    scooters.value = res?.data || res || []
    if (mapInstance && AMapInstance) updateScooterMarkers()
  } catch {
    ElMessage.error('获取失败')
  }
  loadingScooters.value = false
}

const createScooterMarkerContent = (scooter) => {
  const isAvail = scooter.status === 'AVAILABLE'
  const isLow = scooter.batteryLevel < 30
  let cls = 'scooter-marker'
  if (isAvail && !isLow) cls += ' avail'
  else if (isAvail && isLow) cls += ' low'
  else cls += ' inuse'
  return `<div class="${cls}"><svg viewBox="0 0 24 24" width="32" height="32"><circle cx="5" cy="19" r="3.5" stroke="currentColor" stroke-width="2" fill="none"/><circle cx="19" cy="19" r="3.5" stroke="currentColor" stroke-width="2" fill="none"/><path d="M5 19L9 10H17L19 19" stroke="currentColor" stroke-width="2" fill="none"/><path d="M9 10L12 5H14" stroke="currentColor" stroke-width="2" fill="none"/><rect x="10.5" y="4" width="5" height="2" rx="0.5" fill="currentColor"/></svg><div class="marker-battery">${Math.round(scooter.batteryLevel)}%</div></div>`
}

const updateScooterMarkers = () => {
  scooterMarkers.forEach(m => mapInstance.remove(m))
  scooterMarkers = []
  scooters.value.forEach(scooter => {
    if (!scooter.latitude || !scooter.longitude) return
    const content = document.createElement('div')
    content.innerHTML = createScooterMarkerContent(scooter)
    content.style.cursor = 'pointer'
    const marker = new AMap.Marker({
      position: [scooter.longitude, scooter.latitude],
      content: content,
      offset: new AMap.Pixel(-16, -32)
    })
    marker.on('click', () => showScooterDetail(scooter))
    mapInstance.add(marker)
    scooterMarkers.push(marker)
  })
}

const showScooterDetail = (scooter) => {
  selectedScooter.value = scooter
  showDetail.value = true
  if (mapInstance && scooter.longitude && scooter.latitude) {
    mapInstance.setCenter([scooter.longitude, scooter.latitude])
    mapInstance.setZoom(18)
  }
}

const rentScooter = () => {
  if (!selectedScooter.value) return
  showDetail.value = false
  router.push({ path: '/booking', query: { scooterId: selectedScooter.value.id } })
}

const getBatteryClass = (level) => {
  if (level >= 60) return 'high'
  if (level >= 30) return 'medium'
  return 'low'
}

const getStatusText = (status) => {
  const map = { 'AVAILABLE': '可租', 'IN_USE': '使用中', 'MAINTENANCE': '维护中', 'LOW_BATTERY': '低电量' }
  return map[status] || status
}

const formatDate = (date) => {
  if (!date) return '未知'
  return new Date(date).toLocaleDateString('zh-CN')
}

onMounted(async () => {
  await nextTick()
  const token = localStorage.getItem('token')
  if (token) await checkActiveBooking()

  try {
    await nextTick()
    await new Promise(resolve => setTimeout(resolve, 300))

    console.log('开始加载高德地图 API...')
    
    const AMap = await AMapLoader.load({
      key: '27ec2a64ff4acc99ccf61c8c897a69d3',
      version: '2.0',
      plugins: ['AMap.Geocoder']
    })
    
    console.log('高德地图 API 加载完成')

    const mapContainer = document.getElementById('map-container')
    if (!mapContainer) return

    console.log('创建地图实例...')
    const map = new AMap.Map('map-container', {
      zoom: 15,
      center: [DEFAULT_LNG, DEFAULT_LAT],
      resizeEnable: true
    })
    
    console.log('地图实例创建成功')

    mapInstance = map
    AMapInstance = AMap

    let userLng = DEFAULT_LNG
    let userLat = DEFAULT_LAT

    const userPos = await new Promise((resolve) => {
      AMap.plugin('AMap.Geolocation', () => {
        const geolocation = new AMap.Geolocation({
          enableHighAccuracy: true,
          timeout: 15000,
          showButton: false,
          convert: false
        })
        geolocation.getCurrentPosition((status, result) => {
          if (status === 'complete' && result?.position) {
            const lat = result.position.lat
            const lng = result.position.lng
            if (lng >= 73 && lng <= 135 && lat >= 15 && lat <= 54) {
              resolve({ lng, lat })
              return
            }
          }
          resolve(null)
        })
      })
    })

    if (userPos) {
      userLng = userPos.lng
      userLat = userPos.lat
    }

    map.setCenter([userLng, userLat])
    updateUserMarker(AMap, userLng, userLat)
    locationText.value = await reverseGeocode(userLng, userLat)
    userLocated.value = true

    await loadScooters()
    updateScooterMarkers()

  } catch (err) {
    console.error('地图加载失败', err)
  }
})
</script>

<style scoped>
.map-view {
  padding: 32px 40px;
  max-width: 1400px;
  margin: 0 auto;
  background: linear-gradient(180deg, #e8eef5 0%, #d6e0eb 100%);
  min-height: calc(100vh - 64px);
}

.page-header {
  margin-bottom: 24px;
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

.header-actions {
  margin-bottom: 20px;
}

.header-actions :deep(.el-button) {
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  border: none;
  font-weight: 600;
}

/* 行程提示 */
.booking-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: white;
  border-radius: 14px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(30, 58, 95, 0.08);
  border: 1px solid #d6e0eb;
}

.banner-content {
  display: flex;
  align-items: center;
  gap: 14px;
}

.banner-icon {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-icon svg {
  width: 24px;
  height: 24px;
  color: white;
}

.banner-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.banner-title {
  font-size: 15px;
  font-weight: 700;
  color: #1e3a5f;
}

.banner-sub {
  font-size: 12px;
  color: #7a8fa8;
}

.banner-btn {
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  border: none;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 布局 */
.content-grid {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 24px;
}

/* 地图区 */
.map-area {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.map-container {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(30, 58, 95, 0.1);
}

#map-container {
  width: 100%;
  height: 500px;
}

/* 定位栏 */
.location-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(30, 58, 95, 0.06);
}

.location-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #7a8fa8;
}

.location-info.active {
  color: #1e3a5f;
}

.loc-icon {
  width: 36px;
  height: 36px;
  background: #f0f4f8;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loc-icon svg {
  width: 20px;
  height: 20px;
  color: #5a7a9a;
}

.loc-btn {
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  border: none;
}

/* 图例 */
.legend-bar {
  display: flex;
  gap: 24px;
  padding: 14px 18px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(30, 58, 95, 0.06);
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #5a7a9a;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.legend-dot.user { background: #1e3a5f; }
.legend-dot.avail { background: #2d8a4e; }
.legend-dot.inuse { background: #94a3b8; }
.legend-dot.low { background: #c4880c; }

/* 列表区 */
.list-area {
  background: white;
  border-radius: 16px;
  padding: 22px;
  box-shadow: 0 4px 20px rgba(30, 58, 95, 0.1);
  height: fit-content;
  max-height: calc(100vh - 220px);
  overflow-y: auto;
}

.list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.list-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e3a5f;
  margin: 0;
}

.refresh-btn {
  background: #f0f4f8;
  color: #5a7a9a;
  border: none;
}

/* 搜索 */
.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  background: #f0f4f8;
  border-radius: 10px;
  margin-bottom: 16px;
}

.search-icon {
  color: #7a8fa8;
  font-size: 16px;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
  font-family: inherit;
  color: #1e3a5f;
}

.search-input::placeholder {
  color: #7a8fa8;
}

.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  color: #7a8fa8;
  gap: 12px;
}

.empty-illustration {
  width: 100px;
  height: 100px;
}

.empty-svg {
  width: 100%;
  height: 100%;
}

.empty-tip {
  font-size: 13px;
  color: #94a3b8;
}

/* 滑板车列表 */
.scooter-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.scooter-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  background: #f8fafc;
  border: 1px solid transparent;
  border-left: 3px solid transparent;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.scooter-card:hover {
  border-color: #1e3a5f;
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(30, 58, 95, 0.1);
}

.scooter-card.avail {
  border-left-color: #2d8a4e;
}

.scooter-card.inuse {
  border-left-color: #5a7a9a;
  background: #f8fafc;
}

.scooter-card.low {
  border-left-color: #c4880c;
}

.card-icon {
  width: 44px;
  height: 44px;
  background: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(30, 58, 95, 0.08);
}

.card-icon svg {
  width: 26px;
  height: 26px;
  color: #5a7a9a;
}

.scooter-card.avail .card-icon svg {
  color: #2d8a4e;
}

.card-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.card-name {
  font-size: 14px;
  font-weight: 700;
  color: #1e3a5f;
}

.card-loc {
  font-size: 12px;
  color: #7a8fa8;
}

.card-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 5px;
}

.battery {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  font-weight: 600;
}

.battery.high { color: #2d8a4e; }
.battery.medium { color: #c4880c; }
.battery.low { color: #d14545; }

.status {
  font-size: 10px;
  font-weight: 600;
  padding: 3px 8px;
  border-radius: 6px;
}

.status.avail, .status.available {
  background: #e6f4ea;
  color: #2d8a4e;
}

.status.in-use, .status.in_use {
  background: #f0f4f8;
  color: #5a7a9a;
}

/* 详情弹窗 */
.detail-dialog :deep(.el-dialog__header) {
  display: none;
}

.detail-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.detail-dialog :deep(.el-dialog__footer) {
  padding: 16px 20px 20px;
}

.detail-content {
  padding: 6px 0;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 0 24px 20px;
}

.detail-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-icon svg {
  width: 34px;
  height: 34px;
  color: white;
}

.detail-info {
  flex: 1;
}

.detail-info h4 {
  margin: 0 0 4px;
  font-size: 20px;
  font-weight: 800;
  color: #1e3a5f;
}

.detail-info p {
  margin: 0;
  font-size: 13px;
  color: #7a8fa8;
}

.detail-battery {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 16px;
  background: #f0f4f8;
  border-radius: 12px;
}

.detail-battery.high { background: #e6f4ea; }
.detail-battery.medium { background: #fef7e6; }
.detail-battery.low { background: #fde8e8; }

.bt-icon {
  font-size: 18px;
}

.bt-val {
  font-size: 14px;
  font-weight: 700;
  color: #1e3a5f;
}

.detail-stats {
  display: flex;
  gap: 12px;
  padding: 0 24px 20px;
}

.stat-cell {
  flex: 1;
  padding: 14px;
  background: #f8fafc;
  border-radius: 12px;
}

.stat-key {
  display: block;
  font-size: 11px;
  color: #7a8fa8;
  margin-bottom: 6px;
}

.stat-val {
  font-size: 14px;
  font-weight: 700;
  color: #1e3a5f;
}

.stat-val.avail, .stat-val.available {
  color: #2d8a4e;
}

.detail-progress {
  padding: 0 24px 6px;
}

.progress-head {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #7a8fa8;
  margin-bottom: 8px;
}

.progress-bar {
  height: 8px;
  background: #d6e0eb;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s;
}

.progress-fill.high { background: #2d8a4e; }
.progress-fill.medium { background: #c4880c; }
.progress-fill.low { background: #d14545; }

.rent-btn {
  width: 100%;
  height: 50px;
  font-size: 15px;
  font-weight: 700;
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%) !important;
  border: none !important;
  border-radius: 12px;
}

/* 地图标记 */
:global(.user-marker) {
  position: relative;
  width: 32px;
  height: 32px;
}

:global(.marker-ring) {
  position: absolute;
  inset: 0;
  border: 3px solid #1e3a5f;
  border-radius: 50%;
  animation: ring-pulse 2s infinite;
}

:global(.marker-dot) {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 14px;
  height: 14px;
  background: #1e3a5f;
  border: 3px solid white;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(30, 58, 95, 0.4);
}

@keyframes ring-pulse {
  0% { transform: scale(1); opacity: 1; }
  100% { transform: scale(1.5); opacity: 0; }
}

:global(.scooter-marker) {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}

:global(.scooter-marker.avail) { color: #2d8a4e; }
:global(.scooter-marker.inuse) { color: #5a7a9a; }
:global(.scooter-marker.low) { color: #c4880c; }

:global(.scooter-marker.inuse .marker-battery) {
  background: #f0f4f8;
}

:global(.marker-battery) {
  margin-top: 4px;
  font-size: 10px;
  font-weight: 700;
  background: white;
  padding: 2px 6px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.15);
  color: #1e3a5f;
}

:deep(.scooter-marker) {
  cursor: pointer;
  transition: transform 0.2s;
}

:deep(.scooter-marker:hover) {
  transform: scale(1.1);
}

:deep(.marker-content) {
  display: flex;
  flex-direction: column;
  align-items: center;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.3));
}

:deep(.marker-icon) {
  font-size: 32px;
  animation: bounce 2s infinite;
}

:deep(.marker-label) {
  background: #e53e3e;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: bold;
  margin-top: -4px;
  white-space: nowrap;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-4px); }
}
</style>
