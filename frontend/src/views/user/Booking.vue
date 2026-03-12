<template>
  <div class="booking">
    <el-page-header title="返回" @back="goBack" />
    <h2>预订滑板车</h2>

    <el-card v-if="scooter">
      <h3>{{ scooter.name }}</h3>
      <p>位置：{{ scooter.location }}</p>
      <p>价格：¥{{ scooter.pricePerHour }}/小时</p>

      <el-form :model="form" label-width="120px">
        <el-form-item label="租用时长">
          <el-radio-group v-model="form.hours">
            <el-radio-button :label="1">1小时</el-radio-button>
            <el-radio-button :label="4">4小时</el-radio-button>
            <el-radio-button :label="24">1天</el-radio-button>
            <el-radio-button :label="168">1周</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="开始时间">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择开始时间"
            :disabled-date="disabledDate"
          />
        </el-form-item>

        <el-form-item label="支付方式">
          <el-radio-group v-model="form.paymentMethod">
            <el-radio label="credit">信用卡</el-radio>
            <el-radio label="debit">借记卡</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="卡号" v-if="form.paymentMethod">
          <el-input v-model="form.cardNumber" placeholder="卡号" maxlength="16" />
        </el-form-item>

        <el-form-item label="有效期" v-if="form.paymentMethod">
          <el-input v-model="form.expiry" placeholder="MM/YY" maxlength="5" />
        </el-form-item>

        <el-form-item label="CVV" v-if="form.paymentMethod">
          <el-input v-model="form.cvv" placeholder="CVV" maxlength="3" show-password />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确认支付 ¥{{ totalPrice }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getScooters } from '@/api/scooter'
import { createBooking } from '@/api/booking'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const scooter = ref(null)
const submitting = ref(false)

const form = ref({
  hours: 1,
  startTime: new Date(),
  paymentMethod: 'credit',
  cardNumber: '',
  expiry: '',
  cvv: ''
})

// 计算总价
const totalPrice = computed(() => {
  if (!scooter.value) return 0
  return scooter.value.pricePerHour * form.value.hours
})

// 禁用过去时间
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7 // 禁止选择今天之前
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
    // 模拟支付成功，然后创建预订
    await new Promise(resolve => setTimeout(resolve, 1000))
    const bookingData = {
      scooterId: scooter.value.id,
      scooterName: scooter.value.name,
      startTime: form.value.startTime,
      hours: form.value.hours,
      totalPrice: totalPrice.value,
    }
    const res = await createBooking(bookingData)
    ElMessage.success('预订成功！')
    // 跳转到个人中心或列表页
    router.push('/profile')
  } catch (error) {
    ElMessage.error('预订失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  const scooterId = route.query.scooterId
  if (!scooterId) {
    ElMessage.error('未指定滑板车')
    router.push('/scooters')
    return
  }
  try {
    const res = await getScooters()
    const found = res.data.find(s => s.id === parseInt(scooterId))
    if (found) {
      scooter.value = found
    } else {
      ElMessage.error('滑板车不存在')
      router.push('/scooters')
    }
  } catch (error) {
    ElMessage.error('获取滑板车信息失败')
  }
})
</script>

<style scoped>
.booking {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}
</style>