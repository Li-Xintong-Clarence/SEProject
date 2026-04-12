<template>
  <div class="booking">
    <el-page-header title="返回" @back="goBack" />
    <h2 class="page-title">预订滑板车</h2>
    <p class="page-lead">租用选项与费用来自服务端 (ID4) · CapyGlide</p>

    <el-skeleton v-if="loading" :rows="8" animated />

    <template v-else-if="scooter">
      <el-alert type="info" show-icon :closable="false" class="mb">
        <template #title>优惠说明 (ID22)</template>
        常客（累计租用 ≥8 小时）、学生、长者可在正式环境中由后端计价规则自动折扣；当前演示仅展示资格提示，实际扣款以订单创建后服务端金额为准。
      </el-alert>
      <el-alert v-if="discountHint" type="success" show-icon :closable="false" class="mb">
        {{ discountHint }}
      </el-alert>

      <el-card class="flags-card" shadow="never">
        <span class="flags-label">自选优惠资格 (演示，用于 ID22 展示)</span>
        <el-checkbox v-model="profileFlags.student" @change="persistFlags">学生</el-checkbox>
        <el-checkbox v-model="profileFlags.senior" @change="persistFlags">长者</el-checkbox>
      </el-card>

      <el-card class="scooter-card">
        <h3>{{ scooter.scooterNumber || scooter.name || '未知滑板车' }}</h3>
        <p>位置：{{ scooter.location || `${scooter.latitude || scooter.lat}, ${scooter.longitude || scooter.lng}` }}</p>
        <p>电量：{{ scooter.batteryLevel ?? '—' }}%</p>

        <el-form :model="form" label-width="120px">
          <el-form-item label="租用选项">
            <el-radio-group v-model="form.hireOption" :disabled="!pricingOptions.length">
              <el-radio-button
                v-for="opt in pricingOptions"
                :key="opt.hireOption"
                :value="opt.hireOption"
              >
                {{ optionLabel(opt.hireOption) }} · ¥{{ Number(opt.price).toFixed(2) }}
              </el-radio-button>
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

          <el-form-item label="预计结束">
            <el-tag type="info">{{ endTimeFormatted }}</el-tag>
          </el-form-item>

          <el-form-item label="支付方式">
            <el-radio-group v-model="form.paymentMethod">
              <el-radio value="credit">信用卡</el-radio>
              <el-radio value="debit">借记卡</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="卡号" v-if="form.paymentMethod">
            <el-input v-model="form.cardNumber" placeholder="卡号" maxlength="19" />
          </el-form-item>
          <el-form-item label="有效期" v-if="form.paymentMethod">
            <el-input v-model="form.expiry" placeholder="MM/YY" maxlength="5" />
          </el-form-item>
          <el-form-item label="CVV" v-if="form.paymentMethod">
            <el-input v-model="form.cvv" maxlength="4" show-password />
          </el-form-item>

          <el-form-item>
            <el-checkbox v-model="form.saveCardAfterPay">支付成功后保存此卡 (ID2)</el-checkbox>
          </el-form-item>

          <el-form-item>
            <div class="price-line">
              <span>应付合计</span>
              <strong class="price">¥{{ totalPrice }}</strong>
            </div>
            <p v-if="showDiscountLine" class="discount-note">演示折扣参考价 ¥{{ discountedPrice }}（不修改服务端订单金额）</p>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" size="large" :loading="submitting" class="pay-btn" @click="handleSubmit">
              确认支付 ¥{{ totalPrice }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </template>

    <el-empty v-else description="未找到滑板车信息" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getScooterById } from '@/api/scooter'
import { createBooking, payBooking } from '@/api/booking'
import { getPricingList } from '@/api/pricing'
import { listHireOptions } from '@/api/hireOptions'
import { getUserStats } from '@/api/user'
import { addCard } from '@/api/card'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const scooter = ref(null)
const loading = ref(true)
const submitting = ref(false)
const pricingOptions = ref([])
const durationByHireOption = ref({
  '1hr': 60,
  '4hr': 240,
  '1day': 1440,
  '1week': 10080
})
const userStats = ref({})
const profileFlags = ref({ student: false, senior: false })

const form = ref({
  hireOption: '1hr',
  startTime: new Date(Date.now() + 10 * 60 * 1000),
  paymentMethod: 'credit',
  cardNumber: '',
  expiry: '',
  cvv: '',
  saveCardAfterPay: false
})

const optionLabel = (code) => {
  const m = { '1hr': '1小时', '4hr': '4小时', '1day': '1天', '1week': '1周' }
  return m[code] || code
}

const currentPrice = computed(() => {
  const opt = pricingOptions.value.find((p) => p.hireOption === form.value.hireOption)
  return opt ? Number(opt.price) : 0
})

const totalPrice = computed(() => currentPrice.value.toFixed(2))

const eligibleDiscount = computed(() => {
  const hours = Number(userStats.value?.totalDuration || 0)
  const frequent = hours >= 8
  return frequent || profileFlags.value.student || profileFlags.value.senior
})

const showDiscountLine = computed(() => eligibleDiscount.value && currentPrice.value > 0)

const discountedPrice = computed(() => (currentPrice.value * 0.85).toFixed(2))

const discountHint = computed(() => {
  const parts = []
  if (Number(userStats.value?.totalDuration || 0) >= 8) parts.push('累计租用已 ≥8 小时（常客）')
  if (profileFlags.value.student) parts.push('已勾选学生')
  if (profileFlags.value.senior) parts.push('已勾选长者')
  if (!parts.length) return ''
  return `您符合优惠资格参考：${parts.join('、')}。正式折扣需后端计价支持。`
})

const endTimeFormatted = computed(() => {
  if (!form.value.startTime) return '请选择开始时间'
  const minutes = durationByHireOption.value[form.value.hireOption] || 60
  const end = new Date(form.value.startTime.getTime() + minutes * 60 * 1000)
  return end.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
})

const disabledDate = (time) => time.getTime() < Date.now() - 8.64e7

const goBack = () => router.back()

const loadPricing = async () => {
  try {
    const [plist, hlist] = await Promise.all([getPricingList(), listHireOptions()])
    const list = Array.isArray(plist) ? plist : []
    const order = ['1hr', '4hr', '1day', '1week']
    pricingOptions.value = order
      .map((k) => list.find((p) => p.hireOption === k))
      .filter(Boolean)
    if (pricingOptions.value.length && !pricingOptions.value.find((p) => p.hireOption === form.value.hireOption)) {
      form.value.hireOption = pricingOptions.value[0].hireOption
    }
    const dm = { ...durationByHireOption.value }
    const codeToHire = { '1HR': '1hr', '4HR': '4hr', '1DAY': '1day', '1WEEK': '1week' }
    if (Array.isArray(hlist)) {
      hlist.forEach((h) => {
        const key = codeToHire[h.code] || String(h.code || '').toLowerCase()
        if (key && h.durationMinutes != null) dm[key] = h.durationMinutes
      })
    }
    durationByHireOption.value = dm
  } catch (e) {
    console.error(e)
    ElMessage.error('加载价格失败')
  }
}

const loadProfileFlags = () => {
  try {
    const raw = localStorage.getItem('capyglide_discount_profile')
    if (raw) profileFlags.value = { student: false, senior: false, ...JSON.parse(raw) }
  } catch {
    /* */
  }
}

const persistFlags = () => {
  localStorage.setItem(
    'capyglide_discount_profile',
    JSON.stringify({ student: !!profileFlags.value.student, senior: !!profileFlags.value.senior })
  )
}

const handleSubmit = async () => {
  if (!form.value.cardNumber || !form.value.expiry || !form.value.cvv) {
    ElMessage.warning('请填写完整支付信息')
    return
  }
  submitting.value = true
  try {
    const bookingRes = await createBooking({
      scooterId: scooter.value.id,
      hireOption: form.value.hireOption,
      startTime: form.value.startTime.toISOString()
    })
    const bookingId = bookingRes?.id
    if (!bookingId) throw new Error('无法获取预订ID')
    const amount = Number(bookingRes?.totalCost ?? totalPrice.value)
    const payRes = await payBooking(bookingId, {
      cardLast4: form.value.cardNumber.slice(-4),
      amount,
      paymentMethod: form.value.paymentMethod
    })
    if (payRes?.code === 200 || payRes === 'Payment successful' || payRes?.success) {
      if (form.value.saveCardAfterPay) {
        try {
          await addCard({
            cardNumber: form.value.cardNumber,
            cardHolder: 'Saved Card',
            expiryDate: form.value.expiry,
            cvv: form.value.cvv,
            isDefault: false
          })
        } catch {
          /* 非阻断 */
        }
      }
      ElMessage.success('预订与支付成功！')
      router.push('/profile')
    } else {
      throw new Error(payRes?.message || '支付失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error(error.message || '预订失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  loadProfileFlags()
  try {
    userStats.value = (await getUserStats()) || {}
  } catch {
    userStats.value = {}
  }
  await loadPricing()

  const scooterId = route.query.scooterId
  if (!scooterId) {
    ElMessage.error('未指定滑板车ID')
    router.push('/scooters')
    loading.value = false
    return
  }
  try {
    const res = await getScooterById(scooterId)
    scooter.value = res?.data || res
    if (!scooter.value) throw new Error('不存在')
  } catch (e) {
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
  max-width: 680px;
  margin: 0 auto;
}
.page-title {
  margin: 0 0 6px;
  font-size: 1.45rem;
  font-weight: 800;
  color: var(--cg-navy);
}
.page-lead {
  margin: 0 0 14px;
  font-size: 0.88rem;
  color: #6b7280;
}
.mb {
  margin-bottom: 12px;
}
.scooter-card {
  margin-bottom: 20px;
}
.scooter-card h3 {
  margin-top: 0;
  color: var(--cg-navy);
}
.price-line {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  font-size: 1.1rem;
}
.price {
  font-size: 1.5rem;
  color: var(--cg-navy);
}
.discount-note {
  margin: 8px 0 0;
  font-size: 13px;
  color: #6b7280;
}
.pay-btn {
  width: 100%;
}
.flags-card {
  margin-bottom: 16px;
  border-radius: var(--cg-radius-md);
}
.flags-label {
  display: block;
  margin-bottom: 8px;
  font-size: 13px;
  color: #6b7280;
}
</style>
