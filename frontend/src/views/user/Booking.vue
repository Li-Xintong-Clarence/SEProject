<template>
  <div class="booking">
    <el-page-header title="返回" @back="goBack" />
    <h2>预订滑板车</h2>

    <el-skeleton v-if="loading" :rows="8" animated />

    <el-card v-else-if="scooter" class="scooter-card">
      <h3>{{ scooter.scooterNumber || scooter.name || '未知滑板车' }}</h3>
      <p>位置：{{ scooter.location || `${scooter.latitude || scooter.lat}, ${scooter.longitude || scooter.lng}` }}</p>
      <p>电量：{{ scooter.batteryLevel || '未知' }}%</p>
      <p>价格：¥{{ scooter.pricePerHour || 5 }}/小时</p>

      <el-form :model="form" label-width="120px">
        <el-form-item label="租用时长">
          <el-radio-group v-model="form.hours">
            <el-radio-button :value="1">1小时</el-radio-button>
            <el-radio-button :value="4">4小时</el-radio-button>
            <el-radio-button :value="24">1天</el-radio-button>
            <el-radio-button :value="168">1周</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="开始时间">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择开始时间"
            :disabled-date="disabledDate"
            format="YYYY-MM-DD HH:mm"
          />
        </el-form-item>

        <el-form-item label="预计结束时间">
          <el-tag type="info">{{ endTimeFormatted }}</el-tag>
        </el-form-item>

        <el-form-item label="支付方式">
          <el-radio-group v-model="form.paymentMethod">
            <el-radio value="credit">信用卡</el-radio>
            <el-radio value="debit">借记卡</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="卡号" v-if="form.paymentMethod">
          <el-input v-model="form.cardNumber" placeholder="请输入卡号" maxlength="16" />
        </el-form-item>

        <el-form-item label="有效期 (MM/YY)" v-if="form.paymentMethod">
          <el-input v-model="form.expiry" placeholder="MM/YY" maxlength="5" />
        </el-form-item>

        <el-form-item label="CVV" v-if="form.paymentMethod">
          <el-input v-model="form.cvv" placeholder="CVV" maxlength="3" show-password />
        </el-form-item>

        <el-form-item>
          <el-button 
            type="primary" 
            size="large" 
            @click="handleSubmit" 
            :loading="submitting"
            style="width: 100%;"
          >
            确认支付 ¥{{ totalPrice }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-empty v-else description="未找到滑板车信息" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getScooterById } from '@/api/scooter'
import { createBooking, payBooking } from '@/api/booking'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const scooter = ref(null)
const loading = ref(true)
const submitting = ref(false)

const form = ref({
  hours: 1,
  startTime: new Date(Date.now() + 10 * 60 * 1000), // 默认10分钟后
  paymentMethod: 'credit',
  cardNumber: '',
  expiry: '',
  cvv: ''
})

// 计算总价
const totalPrice = computed(() => {
  if (!scooter.value) return 0
  const price = scooter.value.pricePerHour || 5
  return (price * form.value.hours).toFixed(2)
})

// 计算预计结束时间
const endTimeFormatted = computed(() => {
  if (!form.value.startTime) return '请选择开始时间'
  const end = new Date(form.value.startTime.getTime() + form.value.hours * 60 * 60 * 1000)
  return end.toLocaleString('zh-CN', { 
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
})

// 禁用过去时间
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7 // 允许今天
}

const goBack = () => {
  router.back()
}

const handleSubmit = async () => {
  if (!form.value.cardNumber || !form.value.expiry || !form.value.cvv) {
    ElMessage.warning('请填写完整的支付信息')
    return
  }

  submitting.value = true
  try {
    // 1. 创建预订
    const bookingData = {
      scooterId: scooter.value.id,
      hireOption: `${form.value.hours}hr`,
      startTime: form.value.startTime.toISOString()
    }

    const bookingRes = await createBooking(bookingData)

    const bookingId = bookingRes?.id || bookingRes?.data?.id || (bookingRes.code === 200 ? bookingRes.data?.id : null)

    if (!bookingId) {
      throw new Error('创建预订失败：无法获取预订ID')
    }

    // 2. 支付
    const payRes = await payBooking(bookingId, {
      cardLast4: form.value.cardNumber.slice(-4),
      amount: parseFloat(totalPrice.value),
      paymentMethod: form.value.paymentMethod
    })

    if (payRes?.code === 200 || payRes === 'Payment successful' || payRes?.success) {
      ElMessage.success('预订与支付成功！')
      router.push('/profile')
    } else {
      throw new Error(payRes?.message || '支付失败')
    }
  } catch (error) {
    console.error('预订失败:', error)
    ElMessage.error(error.message || '预订失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  console.log('路由参数:', route.query)
  console.log('scooterId:', route.query.scooterId)
  const scooterId = route.query.scooterId
  if (!scooterId) {
    ElMessage.error('未指定滑板车ID')
    router.push('/scooters')
    return
  }

  try {
    const res = await getScooterById(scooterId)
    // 兼容不同返回格式
    scooter.value = res?.data || (Array.isArray(res) ? res[0] : res)
    
    if (!scooter.value) {
      throw new Error('滑板车不存在')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取滑板车信息失败')
    router.push('/scooters')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.booking {
  padding: 20px;
  max-width: 650px;
  margin: 0 auto;
}
.scooter-card {
  margin-bottom: 20px;
}
</style>