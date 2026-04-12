<template>
  <div class="booking">
    <el-page-header title="返回" @back="goBack" />
    <h2 class="page-title">预订滑板车</h2>
    <p class="page-sub">CapyGlide · 选择租期，开始行程</p>

    <el-skeleton v-if="loading" :rows="8" animated />

    <template v-else-if="scooter">
      <!-- 优惠说明 -->
      <el-alert type="info" :closable="false" class="mb alert-info">
        <template #title>优惠说明</template>
        常客（累计租用 ≥8 小时）、学生、长者可在正式环境中享受折扣优惠。
      </el-alert>

      <el-alert v-if="discountHint" type="success" :closable="false" class="mb">
        {{ discountHint }}
      </el-alert>

      <!-- 优惠资格（演示） -->
      <el-card class="discount-card" shadow="never">
        <div class="discount-label">优惠资格（演示）</div>
        <el-checkbox v-model="profileFlags.student" @change="persistFlags">学生</el-checkbox>
        <el-checkbox v-model="profileFlags.senior" @change="persistFlags">长者</el-checkbox>
      </el-card>

      <!-- 滑板车信息 -->
      <el-card class="scooter-card" shadow="never">
        <div class="scooter-header">
          <h3>{{ scooter.scooterNumber || scooter.name || '未知滑板车' }}</h3>
          <el-tag type="success" v-if="scooter.status === 'AVAILABLE'">可用</el-tag>
        </div>
        <div class="scooter-meta">
          <span><el-icon><Location /></el-icon> {{ scooter.location || `${scooter.latitude || scooter.lat}, ${scooter.longitude || scooter.lng}` }}</span>
          <span>电量：{{ scooter.batteryLevel ?? '—' }}%</span>
        </div>

        <el-form :model="form" label-width="110px" class="booking-form">
          <!-- 租用选项 -->
          <el-form-item label="租用时长" required>
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

          <!-- 开始时间 -->
          <el-form-item label="开始时间" required>
            <el-date-picker
              v-model="form.startTime"
              type="datetime"
              placeholder="选择开始时间"
              :disabled-date="disabledDate"
              format="YYYY-MM-DD HH:mm"
              style="width: 100%"
            />
          </el-form-item>

          <!-- 预计结束 -->
          <el-form-item label="预计结束">
            <el-tag type="info">{{ endTimeFormatted }}</el-tag>
          </el-form-item>

          <!-- 支付方式 -->
          <el-form-item label="支付方式" required>
            <el-radio-group v-model="form.paymentMethod">
              <el-radio value="credit">信用卡</el-radio>
              <el-radio value="debit">借记卡</el-radio>
            </el-radio-group>
          </el-form-item>

          <!-- 银行卡信息 -->
          <template v-if="form.paymentMethod">
            <el-form-item label="卡号">
              <el-input v-model="form.cardNumber" placeholder="卡号" maxlength="19" />
            </el-form-item>
            <el-form-item label="有效期">
              <el-input v-model="form.expiry" placeholder="MM/YY" maxlength="5" />
            </el-form-item>
            <el-form-item label="CVV">
              <el-input v-model="form.cvv" maxlength="4" show-password />
            </el-form-item>
          </template>

          <!-- 保存卡片 -->
          <el-form-item>
            <el-checkbox v-model="form.saveCardAfterPay">支付成功后保存此卡</el-checkbox>
          </el-form-item>

          <!-- 价格汇总 -->
          <el-form-item>
            <div class="price-summary">
              <span>应付合计</span>
              <strong class="price">¥{{ totalPrice }}</strong>
            </div>
            <p v-if="showDiscountLine" class="discount-note">
              参考折扣价 ¥{{ discountedPrice }}（正式折扣需后端支持）
            </p>
          </el-form-item>

          <!-- 提交按钮 -->
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="submitting"
              class="submit-btn"
              @click="handleSubmit"
            >
              {{ submitting ? '处理中...' : `确认支付 ¥${totalPrice}` }}
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
import { ElMessage } from 'element-plus'
import { Location } from '@element-plus/icons-vue'
import { getScooterById } from '@/api/scooter'
import { createBooking, payBooking } from '@/api/booking'
import { getPricingList } from '@/api/pricing'
import { listHireOptions } from '@/api/hireOptions'
import { getUserStats } from '@/api/user'
import { addCard } from '@/api/card'

const route = useRoute()
const router = useRouter()

const scooter = ref(null)
const loading = ref(true)
const submitting = ref(false)
const pricingOptions = ref([])

const durationMap = ref({
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

// 租期标签
const optionLabel = (code) => {
  const m = { '1hr': '1小时', '4hr': '4小时', '1day': '1天', '1week': '1周' }
  return m[code] || code
}

// 当前价格
const currentPrice = computed(() => {
  const opt = pricingOptions.value.find(p => p.hireOption === form.value.hireOption)
  return opt ? Number(opt.price) : 0
})

const totalPrice = computed(() => currentPrice.value.toFixed(2))

// 折扣判断
const eligibleDiscount = computed(() => {
  const hours = Number(userStats.value?.totalDuration || 0)
  return hours >= 8 || profileFlags.value.student || profileFlags.value.senior
})

const showDiscountLine = computed(() => eligibleDiscount.value && currentPrice.value > 0)
const discountedPrice = computed(() => (currentPrice.value * 0.85).toFixed(2))

const discountHint = computed(() => {
  const parts = []
  if (Number(userStats.value?.totalDuration || 0) >= 8) parts.push('累计租用 ≥8 小时（常客）')
  if (profileFlags.value.student) parts.push('学生')
  if (profileFlags.value.senior) parts.push('长者')
  if (!parts.length) return ''
  return `您符合优惠资格：${parts.join('、')}。正式折扣需后端计价支持。`
})

// 结束时间
const endTimeFormatted = computed(() => {
  if (!form.value.startTime) return '请选择开始时间'
  const minutes = durationMap.value[form.value.hireOption] || 60
  const end = new Date(form.value.startTime.getTime() + minutes * 60 * 1000)
  return end.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
})

const disabledDate = (time) => time.getTime() < Date.now() - 8.64e7

const goBack = () => router.back()

// 加载价格
const loadPricing = async () => {
  try {
    const [plist, hlist] = await Promise.all([getPricingList(), listHireOptions()])
    const list = Array.isArray(plist) ? plist : []
    const order = ['1hr', '4hr', '1day', '1week']
    pricingOptions.value = order
      .map(k => list.find(p => p.hireOption === k))
      .filter(Boolean)
    if (pricingOptions.value.length && !pricingOptions.value.find(p => p.hireOption === form.value.hireOption)) {
      form.value.hireOption = pricingOptions.value[0].hireOption
    }
    // 更新时长映射
    const dm = { ...durationMap.value }
    const codeToKey = { '1HR': '1hr', '4HR': '4hr', '1DAY': '1day', '1WEEK': '1week' }
    if (Array.isArray(hlist)) {
      hlist.forEach(h => {
        const key = codeToKey[h.code] || String(h.code || '').toLowerCase()
        if (key && h.durationMinutes != null) dm[key] = h.durationMinutes
      })
    }
    durationMap.value = dm
  } catch (e) {
    console.error('加载价格失败', e)
  }
}

// 加载折扣配置
const loadProfileFlags = () => {
  try {
    const raw = localStorage.getItem('capyglide_discount_profile')
    if (raw) profileFlags.value = { student: false, senior: false, ...JSON.parse(raw) }
  } catch {}
}

const persistFlags = () => {
  localStorage.setItem('capyglide_discount_profile', JSON.stringify({
    student: !!profileFlags.value.student,
    senior: !!profileFlags.value.senior
  }))
}

// 提交预订
const handleSubmit = async () => {
  if (!form.value.cardNumber || !form.value.expiry || !form.value.cvv) {
    ElMessage.warning('请填写完整的支付信息')
    return
  }
  submitting.value = true
  try {
    // 1. 创建预订
    const bookingRes = await createBooking({
      scooterId: scooter.value.id,
      hireOption: form.value.hireOption,
      startTime: form.value.startTime.toISOString()
    })
    const bookingId = bookingRes?.id
    if (!bookingId) throw new Error('无法获取预订ID')

    // 2. 支付
    const amount = Number(bookingRes?.totalCost ?? totalPrice.value)
    const payRes = await payBooking(bookingId, {
      cardLast4: form.value.cardNumber.slice(-4),
      amount,
      paymentMethod: form.value.paymentMethod
    })

    if (payRes?.code === 200 || payRes === 'Payment successful' || payRes?.success) {
      // 保存卡片
      if (form.value.saveCardAfterPay) {
        try {
          await addCard({
            cardNumber: form.value.cardNumber,
            cardHolder: 'Saved Card',
            expiryDate: form.value.expiry,
            cvv: form.value.cvv,
            isDefault: false
          })
        } catch {}
      }
      ElMessage.success('预订与支付成功！')
      router.push('/profile')
    } else {
      throw new Error(payRes?.message || '支付失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '预订失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  loadProfileFlags()
  await loadPricing()

  // 加载用户统计
  try {
    userStats.value = (await getUserStats()) || {}
  } catch {
    userStats.value = {}
  }

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
  max-width: 700px;
  margin: 0 auto;
}

.page-title {
  margin: 0 0 4px;
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--cg-navy);
}

.page-sub {
  margin: 0 0 16px;
  font-size: 0.9rem;
  color: #6b7280;
}

.mb {
  margin-bottom: 16px;
}

.alert-info {
  background: #eff6ff;
  border-color: #bfdbfe;
}

.alert-info :deep(.el-alert__title) {
  color: #1e40af;
}

/* 优惠卡片 */
.discount-card {
  margin-bottom: 16px;
  border-radius: var(--cg-radius-md);
  background: #fef3c7;
  border-color: #fde68a;
}

.discount-label {
  font-size: 13px;
  color: #92400e;
  margin-bottom: 8px;
}

/* 滑板车卡片 */
.scooter-card {
  border-radius: var(--cg-radius-lg);
  box-shadow: var(--cg-shadow);
}

.scooter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.scooter-header h3 {
  margin: 0;
  color: var(--cg-navy);
  font-size: 1.2rem;
}

.scooter-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 24px;
}

.scooter-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 表单 */
.booking-form {
  margin-top: 20px;
}

/* 价格汇总 */
.price-summary {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  font-size: 1.1rem;
}

.price {
  font-size: 1.6rem;
  font-weight: 800;
  color: var(--cg-navy);
}

.discount-note {
  margin: 8px 0 0;
  font-size: 13px;
  color: #6b7280;
}

.submit-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  background: var(--cg-navy);
  border-color: var(--cg-navy);
}

.submit-btn:hover {
  background: var(--cg-navy-soft);
  border-color: var(--cg-navy-soft);
}
</style>
