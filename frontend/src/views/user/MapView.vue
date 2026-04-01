<template>
  <div class="map-view">
    <el-page-header title="返回" @back="goBack" />
    <h2>附近滑板车</h2>

    <div class="info-bar">
      <el-tag type="success" size="large">已定位到您的位置</el-tag>
      <span class="count-text">
        附近共有 <strong>{{ nearbyScooters.length }}</strong> 辆可用滑板车
      </span>
    </div>

    <div id="map-container" style="width: 100%; height: 680px; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.1);"></div>

    <div class="legend">
      <div class="legend-item">
        <span class="available-dot"></span> 可用车辆（点击即可预订）
      </div>
      <div class="legend-item">
        <span class="unavailable-dot"></span> 使用中 / 不可用
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import AMapLoader from '@amap/amap-jsapi-loader'
import { getScooters } from '@/api/scooter'
import { ElMessage } from 'element-plus'

const router = useRouter()
const nearbyScooters = ref([])

const goBack = () => router.back()
const availableCount = computed(() => nearbyScooters.value.length)

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
  // 1. 获取真实滑板车数据（不再使用 mock 生成）
  let scootersData = []
  try {
    const res = await getScooters()
    // 兼容不同的返回格式：直接数组 或 {code, data}
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

  // 2. 加载高德地图
  try {
    const AMap = await AMapLoader.load({
      key: '27ec2a64ff4acc99ccf61c8c897a69d3',     // 你的真实 Key
      version: '2.0'
    })

    const map = new AMap.Map('map-container', {
      zoom: 15,
      center: [103.9305, 30.7528], // 默认中心：成都西南交通大学犀浦校区
      resizeEnable: true
    })

    AMap.plugin('AMap.Geolocation', () => {
      const geolocation = new AMap.Geolocation({
        enableHighAccuracy: true,
        timeout: 10000,
        buttonPosition: 'RB',
        zoomToAccuracy: true
      })

      map.addControl(geolocation)

      geolocation.getCurrentPosition((status, result) => {
        if (status === 'complete') {
          const userLat = result.position.lat
          const userLng = result.position.lng

          // 3. 筛选有经纬度且状态为 AVAILABLE 的车辆（真实数据）
          const availableScooters = scootersData
            .map(scooter => {
              // 兼容字段名：latitude/longitude 或 lat/lng
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

          // 4. 添加标记
          availableScooters.forEach(scooter => {
            const marker = new AMap.Marker({
              position: [scooter.lng, scooter.lat],
              map: map,
              title: `${scooter.scooterNumber} • ${scooter.distance}km • 可用`,
              icon: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-red.png'
            })

            marker.on('click', () => {
              router.push({ path: '/booking', query: { scooterId: scooter.id } })
            })
          })

          map.setCenter([userLng, userLat])
          map.setZoom(16)

          if (availableScooters.length === 0) {
            ElMessage.warning('附近暂无可用滑板车')
          } else {
            ElMessage.success(`附近找到 ${availableScooters.length} 辆可用滑板车`)
          }

        } else {
          // 定位失败，仍显示所有可用车辆（基于默认中心）
          ElMessage.warning('定位失败，显示默认区域')
          const availableScooters = scootersData
            .map(scooter => {
              const lat = scooter.latitude ?? scooter.lat
              const lng = scooter.longitude ?? scooter.lng
              if (!lat || !lng) return null
              return { ...scooter, lat, lng }
            })
            .filter(Boolean)
            .filter(s => String(s.status).toUpperCase() === 'AVAILABLE')

          if (availableScooters.length === 0) {
            ElMessage.warning('暂无可用滑板车')
            return
          }

          // 将地图视野调整到第一个滑板车位置
          const first = availableScooters[0]
          map.setCenter([first.lng, first.lat])
          map.setZoom(14)

          availableScooters.forEach(scooter => {
            const marker = new AMap.Marker({
              position: [scooter.lng, scooter.lat],
              map: map,
              title: scooter.scooterNumber,
              icon: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-red.png'
            })
            marker.on('click', () => {
              router.push({ path: '/booking', query: { scooterId: scooter.id } })
            })
          })
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
.map-view { padding: 20px; max-width: 1280px; margin: 0 auto; }
.info-bar { margin: 15px 0 20px; font-size: 17px; display: flex; align-items: center; gap: 15px; }
.count-text { color: #555; }
.legend { margin-top: 20px; display: flex; gap: 30px; font-size: 15px; color: #666; }
.legend-item { display: flex; align-items: center; gap: 10px; }
.available-dot, .unavailable-dot {
  display: inline-block; width: 16px; height: 16px; border-radius: 50%;
}
.available-dot { background: #ff4d4f; }
.unavailable-dot { background: #999; }
</style>