<template>
  <div class="map-view">
    <div class="page-header">
      <h2 class="page-title">附近车辆</h2>
      <p class="page-sub">CapyGlide · 定位附近可用滑板车，点选标记即可预订</p>
    </div>

    <el-alert v-if="hasActiveBooking" type="warning" :closable="false" class="active-alert">
      <template #title>
        您有正在进行的行程
        <el-button type="warning" size="small" @click="$router.push('/trip')" style="margin-left: 12px;">
          前往当前行程
        </el-button>
      </template>
    </el-alert>

    <div class="info-bar">
      <el-tag type="success" size="large"><el-icon><Location /></el-icon> 已定位到您的位置</el-tag>
      <span class="count-text">附近共有 <strong>{{ nearbyScooters.length }}</strong> 辆可用滑板车</span>
    </div>

    <div id="map-container" class="map-shell"></div>

    <div class="legend">
      <div class="legend-item">
        <span class="available-dot"></span> 可用车辆（点击即可预订）
      </div>
      <div class="legend-item">
        <span class="depot-dot"></span> 服务点 (ID18)
      </div>
      <div class="legend-item">
        <span class="unavailable-dot"></span> 使用中 / 不可用
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Location } from '@element-plus/icons-vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import { getScooters } from '@/api/scooter'
import { getUserBookings } from '@/api/booking'
import { ElMessage } from 'element-plus'

const router = useRouter()
const nearbyScooters = ref([])
const hasActiveBooking = ref(false)

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

// 五个服务点 (ID18)
const SERVICE_DEPOTS = [
  { name: '服务点 A', lng: 103.922, lat: 30.746 },
  { name: '服务点 B', lng: 103.936, lat: 30.754 },
  { name: '服务点 C', lng: 103.915, lat: 30.758 },
  { name: '服务点 D', lng: 103.944, lat: 30.739 },
  { name: '服务点 E', lng: 103.928, lat: 30.765 }
]

// 添加服务点标记
const addDepotMarkers = (AMap, map) => {
  SERVICE_DEPOTS.forEach(d => {
    new AMap.Marker({
      position: [d.lng, d.lat],
      map,
      title: `${d.name}（停车/取车点）`,
      icon: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png'
    })
  })
}

// 计算两点间距离（单位：公里）
const getDistance = (lat1, lng1, lat2, lng2) => {
  const R = 6371
  const dLat = (lat2 - lat1) * Math.PI / 180
  const dLng = (lng2 - lng1) * Math.PI / 180
  const a = Math.sin(dLat/2)**2 + Math.cos(lat1*Math.PI/180)*Math.cos(lat2*Math.PI/180)*Math.sin(dLng/2)**2
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
  return (R * c).toFixed(2)
}

onMounted(async () => {
  await checkActiveBooking()

  let scootersData = []
  try {
    const res = await getScooters()
    scootersData = Array.isArray(res) ? res : (res?.data || [])
    if (!scootersData.length) {
      ElMessage.warning('暂无滑板车数据，请稍后再试')
      return
    }
  } catch (e) {
    console.error('获取滑板车数据失败:', e)
    ElMessage.error('获取滑板车数据失败，请检查网络')
    return
  }

  // 处理地图点击跳转
  const goToBooking = (scooterId) => {
    if (hasActiveBooking.value) {
      ElMessage.warning('您已有正在进行的行程，请先完成或取消当前行程')
      router.push('/trip')
      return
    }
    router.push({ path: '/booking', query: { scooterId } })
  }

  try {
    const AMap = await AMapLoader.load({
      key: '27ec2a64ff4acc99ccf61c8c897a69d3',
      version: '2.0'
    })

    const map = new AMap.Map('map-container', {
      zoom: 15,
      center: [103.9305, 30.7528],
      resizeEnable: true
    })

    addDepotMarkers(AMap, map)

    AMap.plugin('AMap.Geolocation', () => {
      const geolocation = new AMap.Geolocation({
        enableHighAccuracy: true,
        timeout: 10000,
        buttonPosition: 'RB',
        zoomToAccuracy: true,
        // 默认使用高精度模式
        GeoLocationFirst: true
      })

      map.addControl(geolocation)

      geolocation.getCurrentPosition((status, result) => {
        // 默认位置：成都
        const defaultLng = 103.9305
        const defaultLat = 30.7528

        let userLng = defaultLng
        let userLat = defaultLat

        if (status === 'complete' && result && result.position) {
          userLat = result.position.lat
          userLng = result.position.lng

          // 如果返回的位置在境外（经度不在中国范围内），使用默认位置
          if (userLng < 73 || userLng > 135 || userLat < 15 || userLat > 54) {
            console.warn('检测到境外定位，使用默认位置（成都）')
            userLng = defaultLng
            userLat = defaultLat
          }

          // 过滤可用滑板车并计算距离
          const availableScooters = scootersData
            .map(scooter => {
              const lat = scooter.latitude ?? scooter.lat
              const lng = scooter.longitude ?? scooter.lng
              if (!lat || !lng) return null
              const distance = parseFloat(getDistance(userLat, userLng, lat, lng))
              return { ...scooter, distance, lat, lng }
            })
            .filter(Boolean)
            .filter(s => String(s.status).toUpperCase() === 'AVAILABLE')
            .sort((a, b) => a.distance - b.distance)

          nearbyScooters.value = availableScooters

          availableScooters.forEach(scooter => {
            const marker = new AMap.Marker({
              position: [scooter.lng, scooter.lat],
              map: map,
              title: `${scooter.scooterNumber} • ${scooter.distance}km • 可用`,
              icon: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-red.png'
            })

            marker.on('click', () => goToBooking(scooter.id))
          })

          map.setCenter([userLng, userLat])
          map.setZoom(16)

          if (availableScooters.length === 0) {
            ElMessage.warning('附近暂无可用滑板车')
          } else {
            ElMessage.success(`附近找到 ${availableScooters.length} 辆可用滑板车`)
          }
        } else {
          ElMessage.warning('定位失败，使用默认区域（成都）')

          // 使用默认位置显示滑板车
          const availableScooters = scootersData
            .map(scooter => {
              const lat = scooter.latitude ?? scooter.lat
              const lng = scooter.longitude ?? scooter.lng
              if (!lat || !lng) return null
              const distance = parseFloat(getDistance(userLat, userLng, lat, lng))
              return { ...scooter, distance, lat, lng }
            })
            .filter(Boolean)
            .filter(s => String(s.status).toUpperCase() === 'AVAILABLE')
            .sort((a, b) => a.distance - b.distance)

          nearbyScooters.value = availableScooters

          availableScooters.forEach(scooter => {
            const marker = new AMap.Marker({
              position: [scooter.lng, scooter.lat],
              map: map,
              title: `${scooter.scooterNumber} • ${scooter.distance}km`,
              icon: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-red.png'
            })
            marker.on('click', () => goToBooking(scooter.id))
          })

          map.setCenter([userLng, userLat])
          map.setZoom(14)

          if (availableScooters.length === 0) {
            ElMessage.warning('暂无可用滑板车')
          }
        }
      })
    })
  } catch (err) {
    console.error('地图加载失败', err)
    ElMessage.error('地图加载失败')
  }
})
</script>

<style scoped>
.map-view {
  padding: 32px 24px;
  max-width: 1280px;
  margin: 0 auto;
  background: var(--cg-white);
  border-radius: var(--cg-radius-xl);
  box-shadow: var(--cg-shadow-md);
  border: 1px solid var(--cg-border-light);
}

.page-header {
  margin-bottom: 20px;
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

.active-alert {
  margin-bottom: 20px;
}

.active-alert :deep(.el-alert__title) {
  color: var(--cg-text);
  display: flex;
  align-items: center;
  font-weight: 600;
}

.info-bar {
  margin: 8px 0 20px;
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
  color: var(--cg-text);
}

.info-bar :deep(.el-tag) {
  background: var(--cg-gradient-navy);
  border: none;
  color: white;
  font-weight: 600;
}

.count-text {
  color: var(--cg-text-light);
}

.count-text strong {
  color: var(--cg-primary);
}

.map-shell {
  width: 100%;
  height: 580px;
  border-radius: var(--cg-radius-lg);
  overflow: hidden;
  border: 1px solid var(--cg-border);
}

.legend {
  margin-top: 20px;
  display: flex;
  gap: 30px;
  font-size: 14px;
  color: #5c6570;
  flex-wrap: wrap;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.available-dot, .unavailable-dot, .depot-dot {
  display: inline-block;
  width: 16px;
  height: 16px;
  border-radius: 50%;
}

.available-dot {
  background: var(--cg-accent);
  box-shadow: 0 0 0 3px rgba(224, 123, 57, 0.25);
}

.depot-dot {
  background: var(--cg-navy);
  box-shadow: 0 0 0 3px rgba(30, 58, 95, 0.2);
}

.unavailable-dot {
  background: #9ca3af;
}
</style>
