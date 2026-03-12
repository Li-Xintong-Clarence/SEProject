<template>
  <div class="scooter-list">
    <el-page-header title="返回" @back="goBack" />
    <h2>可租滑板车</h2>

    <el-row :gutter="20">
      <el-col :span="6" v-for="scooter in scooters" :key="scooter.id">
        <el-card class="scooter-card">
          <h3>{{ scooter.name }}</h3>
          <p>位置：{{ scooter.location }}</p>
          <p>价格：¥{{ scooter.pricePerHour }}/小时</p>
          <p>
            状态：
            <el-tag :type="scooter.status === 'available' ? 'success' : 'danger'">
              {{ scooter.status === 'available' ? '可用' : '不可用' }}
            </el-tag>
          </p>
          <el-button 
            type="primary" 
            :disabled="scooter.status !== 'available'"
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

const router = useRouter()
const scooters = ref([])

const goBack = () => {
  router.back()
}

const handleBook = (scooterId) => {
  // 跳转到预订页面，并传递滑板车ID
  router.push({ path: '/booking', query: { scooterId } })
}

onMounted(async () => {
  try {
    const res = await getScooters()
    scooters.value = res.data
  } catch (error) {
    ElMessage.error('获取滑板车列表失败')
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
</style>