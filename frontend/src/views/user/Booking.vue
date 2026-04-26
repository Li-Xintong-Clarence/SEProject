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
        常客（累计租用 ≥8 小时）、学生、长者可在正式环境中享受折扣优惠。支持输入折扣码获取额外优惠！
      </el-alert>

      <el-alert v-if="discountHint" type="success" :closable="false" class="mb">
        {{ discountHint }}
      </el-alert>

      <!-- 优惠资格 -->
      <el-card class="discount-card" shadow="never">
        <div class="discount-label">优惠资格</div>
        <el-radio-group v-model="profileFlags.userType" @change="onUserTypeChange">
          <el-radio value="none">无优惠</el-radio>
          <el-radio value="student">学生</el-radio>
          <el-radio value="senior">长者</el-radio>
        </el-radio-group>
        <p v-if="profileFlags.userType && profileFlags.userType !== 'none'" class="discount-hint">
          已选择 {{ profileFlags.userType === 'student' ? '学生' : '长者' }} 优惠，费用将在结算时自动折扣。
        </p>

        <el-divider />

        <!-- 折扣码输入 -->
        <div class="discount-code-section">
          <div class="discount-label">折扣码（可选）</div>
          <div class="code-input-row">
            <el-input
              v-model="discountCodeInput"
              placeholder="输入折扣码"
              clearable
              :disabled="loadingDiscountCode"
              @keyup.enter="applyDiscountCode"
              style="max-width: 260px;"
            >
              <template #prefix>
                <el-icon><Tickets /></el-icon>
              </template>
            </el-input>
            <el-button
              type="primary"
              :loading="loadingDiscountCode"
              :disabled="!discountCodeInput.trim()"
              @click="applyDiscountCode"
            >
              验证折扣码
            </el-button>
          </div>

          <!-- 折扣码验证结果 -->
          <transition name="el-fade-in">
            <div v-if="discountCodeResult" class="code-result">
              <el-alert
                :type="discountCodeResult.valid ? 'success' : 'error'"
                :closable="false"
                class="code-alert"
              >
                <template #title>
                  <span v-if="discountCodeResult.valid">
                    折扣码有效！节省 {{ discountCodeResult.discountPercent }}%
                    <template v-if="discountCodeResult.description"> - {{ discountCodeResult.description }}</template>
                  </span>
                  <span v-else>{{ discountCodeResult.message || '折扣码无效或已过期' }}</span>
                </template>
              </el-alert>
            </div>
          </transition>

          <!-- 已应用的折扣码 -->
          <div v-if="appliedDiscountCode" class="applied-code">
            <el-tag type="success" size="large" effect="plain">
              <el-icon><Check /></el-icon>
              已应用折扣码: {{ appliedDiscountCode }}
            </el-tag>
            <el-button link type="danger" size="small" @click="removeDiscountCode">移除</el-button>
          </div>
        </div>
      </el-card>

      <!-- 滑板车信息 -->
      <el-card class="scooter-card" shadow="never">
        <template #header>
          <div class="scooter-header">
            <h3>{{ scooter.scooterNumber || scooter.name || '未知滑板车' }}</h3>
            <el-tag type="success">可用</el-tag>
          </div>
        </template>
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

          <!-- 价格明细 -->
          <el-form-item>
            <div class="price-breakdown">
              <div class="price-row original" v-if="hasDiscount">
                <span>原价</span>
                <span>¥{{ currentPrice.toFixed(2) }}</span>
              </div>
              <div class="price-row discount" v-if="hasDiscount">
                <span>折扣 (<span v-if="profileFlags.userType !== 'none'">{{ getUserTypeLabel() }}</span><span v-if="appliedDiscountCode"> + 折扣码</span>)</span>
                <span>-¥{{ discountAmount.toFixed(2) }}</span>
              </div>
              <div class="price-row total">
                <span>应付合计</span>
                <strong class="price">¥{{ totalPrice }}</strong>
              </div>
              <div class="savings-hint" v-if="hasDiscount">
                <el-icon><Discount /></el-icon> 节省 ¥{{ discountAmount.toFixed(2) }}
              </div>
            </div>
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
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Location, Tickets, Check, Discount } from '@element-plus/icons-vue'
import { getScooterById } from '@/api/scooter'
import { createBooking, payBooking } from '@/api/booking'
import { getPricingList, getPricePreview } from '@/api/pricing'
import { listHireOptions } from '@/api/hireOptions'
import { getUserStats } from '@/api/user'
import { addCard } from '@/api/card'
import { applyDiscountCode as apiApplyDiscountCode, validateDiscountCode } from '@/api/discount'

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
const profileFlags = ref({ userType: 'none' })
const discountPrice = ref(0)
const loadingPrice = ref(false)

// 折扣码相关
const discountCodeInput = ref('')
const loadingDiscountCode = ref(false)
const discountCodeResult = ref(null)
const appliedDiscountCode = ref('')

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
  const opt = pricingOptions.value.find(p => p.hireOption === form.value.hireOption)
  return opt ? Number(opt.price) : 0
})

// 折扣金额
const discountAmount = computed(() => {
  if (currentPrice.value <= 0) return 0
  return Math.max(0, currentPrice.value - Number(totalPrice.value))
})

// 是否有折扣
const hasDiscount = computed(() => {
  return profileFlags.value.userType !== 'none' || appliedDiscountCode.value
})

const totalPrice = computed(() => {
  if ((profileFlags.value.userType !== 'none' || appliedDiscountCode.value) && discountPrice.value > 0) {
    return Number(discountPrice.value).toFixed(2)
  }
  return currentPrice.value.toFixed(2)
})

const eligibleDiscount = computed(() => {
  const hours = Number(userStats.value?.totalDuration || 0)
  return hours >= 8
})

const discountHint = computed(() => {
  const parts = []
  if (Number(userStats.value?.totalDuration || 0) >= 8) parts.push('累计租用 ≥8 小时（常客）')
  if (profileFlags.value.userType === 'student') parts.push('学生')
  if (profileFlags.value.userType === 'senior') parts.push('长者')
  if (!parts.length) return ''
  return `您符合优惠资格：${parts.join('、')}。`
})

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

const getUserTypeLabel = () => {
  const map = { student: '学生', senior: '长者' }
  return map[profileFlags.value.userType] || ''
}

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

const loadProfileFlags = () => {
  try {
    const raw = localStorage.getItem('capyglide_discount_profile')
    if (raw) {
      const parsed = JSON.parse(raw)
      profileFlags.value = { userType: parsed.userType || 'none' }
    }
  } catch {}
}

const persistFlags = () => {
  localStorage.setItem('capyglide_discount_profile', JSON.stringify({
    userType: profileFlags.value.userType || 'none'
  }))
  fetchDiscountPrice()
}

const onUserTypeChange = () => {
  persistFlags()
}

const fetchDiscountPrice = async () => {
  if (!form.value.hireOption) {
    discountPrice.value = 0
    return
  }
  // 如果没有折扣，不请求价格预览
  if (profileFlags.value.userType === 'none' && !appliedDiscountCode.value) {
    discountPrice.value = 0
    return
  }
  loadingPrice.value = true
  try {
    let res
    if (appliedDiscountCode.value) {
      res = await apiApplyDiscountCode(appliedDiscountCode.value, form.value.hireOption)
      discountPrice.value = res?.price ?? res?.discountedPrice ?? res?.data?.price ?? 0
    } else if (profileFlags.value.userType !== 'none') {
      res = await getPricePreview({
        hireOption: form.value.hireOption,
        discountType: profileFlags.value.userType
      })
      discountPrice.value = res?.price ?? res?.discountedPrice ?? res?.data?.price ?? 0
    } else {
      discountPrice.value = 0
    }
  } catch (e) {
    // 价格预览失败不影响预订，静默处理
    console.warn('获取折扣价格失败，使用原价', e.message)
    discountPrice.value = 0
  } finally {
    loadingPrice.value = false
  }
}

const applyDiscountCode = async () => {
  const code = discountCodeInput.value.trim()
  if (!code) return
  loadingDiscountCode.value = true
  discountCodeResult.value = null
  try {
    const res = await validateDiscountCode({ code })
    if (res?.valid || res?.code === 200 || res?.status === 200) {
      discountCodeResult.value = {
        valid: true,
        discountPercent: res?.discountPercent || res?.discount || 10,
        description: res?.description || res?.name || ''
      }
      appliedDiscountCode.value = code
      await fetchDiscountPrice()
      ElMessage.success(`折扣码有效！已节省 ${discountCodeResult.value.discountPercent}%`)
    } else {
      discountCodeResult.value = {
        valid: false,
        message: res?.message || '折扣码无效或已过期'
      }
      appliedDiscountCode.value = ''
    }
  } catch (e) {
    discountCodeResult.value = {
      valid: false,
      message: '折扣码验证失败，请检查后重试'
    }
    appliedDiscountCode.value = ''
  } finally {
    loadingDiscountCode.value = false
  }
}

const removeDiscountCode = () => {
  discountCodeInput.value = ''
  appliedDiscountCode.value = ''
  discountCodeResult.value = null
  fetchDiscountPrice()
}

watch(() => profileFlags.value.userType, () => {
  if (appliedDiscountCode.value) {
    appliedDiscountCode.value = ''
    discountCodeResult.value = null
    discountCodeInput.value = ''
  }
  fetchDiscountPrice()
})

watch(() => form.value.hireOption, () => {
  fetchDiscountPrice()
})

const handleSubmit = async () => {
  if (!form.value.cardNumber || !form.value.expiry || !form.value.cvv) {
    ElMessage.warning('请填写完整的支付信息')
    return
  }
  submitting.value = true
  try {
    let discountType = null
    if (profileFlags.value.userType !== 'none') {
      discountType = profileFlags.value.userType
    }
    const bookingData = {
      scooterId: scooter.value.id,
      hireOption: form.value.hireOption,
      startTime: form.value.startTime.toISOString()
    }
    if (discountType) {
      bookingData.discountType = discountType
    }
    if (appliedDiscountCode.value) {
      bookingData.discountCode = appliedDiscountCode.value
    }

    const bookingRes = await createBooking(bookingData)
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
        } catch {}
      }
      ElMessage.success('预订与支付成功！')
      router.push('/trip')
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
  padding: 32px 24px;
  max-width: 720px;
  margin: 0 auto;
}

.page-title {
  margin: 0 0 4px;
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--cg-text);
  letter-spacing: -0.02em;
}

.page-sub {
  margin: 0 0 24px;
  font-size: 15px;
  color: var(--cg-text-light);
}

.mb {
  margin-bottom: 16px;
}

.alert-info {
  border-radius: var(--cg-radius-md);
}

/* 优惠卡片 */
.discount-card {
  margin-bottom: 16px;
  border-radius: var(--cg-radius-lg);
  background: var(--cg-info-bg);
  border: 1px solid #bfdbfe;
}

.discount-label {
  font-size: 13px;
  color: #1e40af;
  font-weight: 700;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.discount-hint {
  margin: 12px 0 0;
  font-size: 13px;
  color: #1e40af;
  font-weight: 500;
}

/* 折扣码区域 */
.discount-code-section {
  margin-top: 12px;
}

.code-input-row {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-top: 8px;
}

.code-result {
  margin-top: 12px;
}

.code-alert {
  border-radius: var(--cg-radius-md);
}

.applied-code {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
  padding: 10px 14px;
  background: #f0fdf4;
  border-radius: var(--cg-radius-md);
  border: 1px solid #bbf7d0;
}

/* 滑板车卡片 */
.scooter-card {
  border-radius: var(--cg-radius-xl);
  box-shadow: var(--cg-shadow-md);
  border: 1px solid var(--cg-border-light);
  overflow: hidden;
}

.scooter-card :deep(.el-card__header) {
  background: var(--cg-gradient-navy);
  color: white;
  padding: 16px 20px;
  border: none;
}

.scooter-card :deep(.el-card__body) {
  padding: 20px;
}

.scooter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.scooter-header h3 {
  margin: 0;
  color: white;
  font-size: 1.25rem;
  font-weight: 700;
}

.scooter-header :deep(.el-tag) {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
}

.scooter-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: var(--cg-text-light);
  margin-top: 16px;
}

.scooter-meta span {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 表单 */
.booking-form {
  margin-top: 20px;
}

/* 价格明细 */
.price-breakdown {
  background: var(--cg-bg);
  border-radius: var(--cg-radius-md);
  padding: 16px;
  width: 100%;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  padding: 6px 0;
  font-size: 15px;
}

.price-row.original {
  color: #9ca3af;
  text-decoration: line-through;
}

.price-row.discount {
  color: var(--cg-success);
  font-weight: 500;
}

.price-row.total {
  border-top: 1px dashed #d1d5db;
  padding-top: 12px;
  margin-top: 6px;
  font-weight: 600;
}

.price {
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--cg-accent);
}

.savings-hint {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--cg-success);
  font-size: 13px;
  font-weight: 600;
  margin-top: 8px;
  padding: 6px 10px;
  background: rgba(16, 185, 129, 0.1);
  border-radius: var(--cg-radius-sm);
}

.submit-btn {
  width: 100%;
  height: 52px;
  font-size: 16px;
  font-weight: 700;
  background: var(--cg-gradient) !important;
  border: none !important;
  border-radius: var(--cg-radius-md);
  margin-top: 8px;
}

.submit-btn:hover {
  opacity: 0.9;
  transform: translateY(-2px);
  box-shadow: var(--cg-shadow-accent);
}
</style>
