<template>
  <div class="scooter-list">
    <el-page-header title="返回" @back="goBack" />
    <h2>可租滑板车</h2>
    <!-- 在 <h2>可租滑板车</h2> 下面添加 -->
<div class="view-toggle" style="margin-bottom: 20px;">
  <el-button-group>
    <el-button :type="viewMode === 'list' ? 'primary' : ''" @click="viewMode = 'list'">列表模式</el-button>
    <el-button :type="viewMode === 'map' ? 'primary' : ''" @click="viewMode = 'map'">地图模式</el-button>
  </el-button-group>
</div>

<!-- 条件渲染 -->
<div v-if="viewMode === 'list'">
  <!-- 原有 el-row 列表内容保持不变 -->
</div>
<div v-else>
  <MapView />
</div>

    <!-- 添加加载状态 -->
    <div v-if="loading" class="loading">
      <el-skeleton :rows="3" animated />
    </div>

    <el-row :gutter="20" v-else>
      <el-col :span="6" v-for="scooter in scooters" :key="scooter.id">
        <el-card class="scooter-card">
          <h3>{{ scooter.scooterNumber }}</h3>                 <!-- 改为 scooterNumber -->
          <p>位置：{{ scooter.latitude }}, {{ scooter.longitude }}</p>   <!-- 位置坐标 -->
          <p>电量：{{ scooter.batteryLevel }}%</p>             <!-- 电量 -->
          <p>价格：¥5/小时</p>                         <!-- 如果后端没有价格字段，暂时固定 -->
          <p>
            状态：
            <el-tag :type="scooter.status === 'AVAILABLE' ? 'success' : 'danger'">
              {{ scooter.status === 'AVAILABLE' ? '可用' : 
                 scooter.status === 'IN_USE' ? '使用中' : '维护中' }}
            </el-tag>
          </p>
          <el-button 
            type="primary" 
            :disabled="scooter.status !== 'AVAILABLE'"
            @click="handleBook(scooter.id)"
          >
            立即预订
          </el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getScooters } from '@/api/scooter'
import { ElMessage } from 'element-plus'
import MapView from './MapView.vue'  

const viewMode = ref('list')
const router = useRouter()
const scooters = ref([])
const loading = ref(false)  // 添加加载状态

const goBack = () => {
  router.back()
}

const handleBook = (scooterId) => {
  router.push({ path: '/booking', query: { scooterId } })
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getScooters()
    // 判断返回数据的类型，并适配
    if (Array.isArray(res)) {
      // 如果直接返回数组，直接赋值
      scooters.value = res
    } else if (res.code === 200) {
      // 如果返回 {code, data} 结构
      scooters.value = res.data
    } else {
      ElMessage.error(res.message || '获取滑板车列表失败')
      return
    }
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
}
.scooter-card {
  margin-bottom: 20px;
}
.loading {
  padding: 40px;
}
</style>