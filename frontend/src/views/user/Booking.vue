<template>
  <div class="booking">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">预订滑板车</h2>
      <p class="page-sub">完成您的租赁预订</p>
    </div>

    <el-skeleton v-if="loading" :rows="8" animated />

    <template v-else-if="depot || selectedScooter">
      <div class="booking-layout">
        <!-- 左侧信息面板 -->
        <div class="info-panel">
          <!-- 车辆/服务点信息 -->
          <div v-if="selectedScooter" class="info-card">
            <div class="card-accent"></div>
            <div class="card-body">
              <div class="card-header-row">
                <div class="vehicle-icon">
                  <svg viewBox="0 0 64 64" fill="none">
                    <circle cx="14" cy="50" r="10" stroke="currentColor" stroke-width="2.5"/>
                    <circle cx="50" cy="50" r="10" stroke="currentColor" stroke-width="2.5"/>
                    <path d="M14 50L24 30H40L50 50" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
                    <path d="M24 30L30 20H38" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
                    <rect x="28" y="18" width="12" height="4" rx="1" fill="currentColor"/>
                  </svg>
                </div>
                <div class="vehicle-info">
                  <span class="name">{{ selectedScooter.scooterNumber }}</span>
                  <span class="loc">{{ selectedScooter.location || '未知位置' }}</span>
                </div>
                <div class="battery" :class="getBatteryClass(selectedScooter.batteryLevel)">
                  <span class="batt-icon">⚡</span>
                  <span class="batt-val">{{ Math.round(selectedScooter.batteryLevel) }}%</span>
                </div>
              </div>
              <div class="divider"></div>
              <div class="detail-list">
                <div class="detail-row">
                  <span class="key">服务点</span>
                  <span class="val">{{ depot?.name || '—' }}</span>
                </div>
                <div class="detail-row">
                  <span class="key">状态</span>
                  <el-tag type="success" size="small">可用</el-tag>
                </div>
              </div>
            </div>
          </div>

          <div v-else class="info-card">
            <div class="card-accent"></div>
            <div class="card-body">
              <div class="card-header-row">
                <div class="depot-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"/>
                    <circle cx="12" cy="10" r="3"/>
                  </svg>
                </div>
                <div class="depot-info">
                  <span class="name">{{ depot.name }}</span>
                  <span class="addr">{{ depot.address || depot.depotNumber }}</span>
                </div>
              </div>
              <div class="avail-display">
                <span class="avail-num">{{ depot.availableCount }}</span>
                <span class="avail-label">辆可用</span>
              </div>
            </div>
          </div>

          <!-- 趣味提示卡片 -->
          <div class="tips-card">
            <div class="tips-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"/>
              </svg>
            </div>
            <div class="tips-content">
              <h4>温馨提示</h4>
              <p>请在服务点工作人员处领取取车码，享受愉快的滑行体验！</p>
            </div>
          </div>
        </div>

        <!-- 右侧表单 -->
        <div class="form-panel">
          <el-card class="main-card">
            <template #header>
              <span class="card-title">完成预订</span>
            </template>

            <!-- 租赁时长 -->
            <div class="form-group">
              <label class="group-label">选择租赁时长</label>
              <div class="duration-grid">
                <div
                  v-for="opt in pricingOptions"
                  :key="opt.hireOption"
                  class="duration-item"
                  :class="{ active: form.hireOption === opt.hireOption }"
                  @click="form.hireOption = opt.hireOption"
                >
                  <span class="duration-time">{{ optionLabel(opt.hireOption) }}</span>
                  <span class="duration-price">¥{{ Number(opt.price).toFixed(2) }}</span>
                </div>
              </div>
            </div>

            <!-- 优惠资格 -->
            <div class="form-group">
              <label class="group-label">优惠资格</label>
              <el-radio-group v-model="profileFlags.userType" @change="onUserTypeChange" class="discount-group">
                <el-radio value="none">无优惠</el-radio>
                <el-radio value="student">学生 9折</el-radio>
                <el-radio value="senior">长者 8折</el-radio>
              </el-radio-group>
            </div>

            <!-- 折扣码 -->
            <div class="form-group">
              <label class="group-label">折扣码</label>
              <div class="coupon-row">
                <el-input
                  v-model="discountCodeInput"
                  placeholder="输入折扣码"
                  clearable
                  :disabled="loadingDiscountCode"
                  @keyup.enter="applyDiscountCode"
                >
                  <template #prefix><el-icon><Tickets /></el-icon></template>
                </el-input>
                <el-button
                  type="primary"
                  :loading="loadingDiscountCode"
                  :disabled="!discountCodeInput.trim()"
                  @click="applyDiscountCode"
                >
                  验证
                </el-button>
              </div>
              <transition name="el-fade-in">
                <div v-if="discountCodeResult" class="coupon-feedback" :class="discountCodeResult.valid ? 'valid' : 'invalid'">
                  <el-icon v-if="discountCodeResult.valid"><Check /></el-icon>
                  <el-icon v-else><Close /></el-icon>
                  {{ discountCodeResult.valid ? `有效！节省 ${discountCodeResult.discountPercent}%` : discountCodeResult.message }}
                </div>
              </transition>
              <div v-if="appliedDiscountCode" class="coupon-applied">
                <el-tag type="success" closable @close="removeDiscountCode">
                  已应用: {{ appliedDiscountCode }}
                </el-tag>
              </div>
            </div>

            <el-divider />

            <!-- 支付方式 -->
            <div class="form-group">
              <label class="group-label">支付方式</label>
              <div class="payment-grid">
                <div
                  class="payment-item"
                  :class="{ active: form.paymentMethod === 'credit' }"
                  @click="form.paymentMethod = 'credit'"
                >
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <rect x="2" y="5" width="20" height="14" rx="2"/>
                    <path d="M2 10h20"/>
                  </svg>
                  <span>信用卡</span>
                </div>
                <div
                  class="payment-item"
                  :class="{ active: form.paymentMethod === 'debit' }"
                  @click="form.paymentMethod = 'debit'"
                >
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <rect x="2" y="5" width="20" height="14" rx="2"/>
                    <path d="M2 10h20"/>
                  </svg>
                  <span>借记卡</span>
                </div>
              </div>
            </div>

            <!-- 银行卡 -->
            <div class="form-group">
              <label class="group-label">银行卡信息</label>
              <div class="card-inputs">
                <el-input v-model="form.cardNumber" placeholder="卡号" maxlength="19" />
                <div class="card-row">
                  <el-input v-model="form.expiry" placeholder="有效期" maxlength="5" />
                  <el-input v-model="form.cvv" placeholder="CVV" maxlength="4" show-password />
                </div>
              </div>
            </div>

            <!-- 价格 -->
            <div class="price-block">
              <div class="price-list">
                <div class="price-row" v-if="hasDiscount">
                  <span>原价</span>
                  <span class="original">¥{{ currentPrice.toFixed(2) }}</span>
                </div>
                <div class="price-row discount" v-if="hasDiscount">
                  <span>折扣</span>
                  <span>-¥{{ discountAmount.toFixed(2) }}</span>
                </div>
                <div class="price-row total">
                  <span>应付金额</span>
                  <span class="final">¥{{ totalPrice }}</span>
                </div>
              </div>
            </div>

            <!-- 提交 -->
            <el-button
              type="primary"
              size="large"
              :loading="submitting"
              class="submit-btn"
              @click="handleSubmit"
            >
              {{ submitting ? '处理中...' : `确认支付 ¥${totalPrice}` }}
            </el-button>

            <p class="terms">点击确认即表示您同意相关服务条款</p>
          </el-card>
        </div>
      </div>
    </template>

    <el-empty v-else description="未找到服务点信息">
      <el-button type="primary" @click="$router.push('/map')">返回地图</el-button>
    </el-empty>

    <!-- 成功弹窗 -->
    <el-dialog
      v-model="showSuccessModal"
      title="预订成功"
      width="460px"
      :close-on-click-modal="false"
      class="success-dialog"
    >
      <div class="success-content">
        <div class="success-icon">
          <svg viewBox="0 0 80 80" fill="none">
            <circle cx="40" cy="40" r="38" stroke="currentColor" stroke-width="2"/>
            <path d="M24 40L35 51L56 30" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h3 class="success-title">订单已确认</h3>
        <p class="success-sub">请在指定服务点凭取车码取车</p>

        <div class="order-block">
          <div class="order-header">
            <span class="key">订单号</span>
            <span class="val code">{{ confirmedBooking?.confirmationCode }}</span>
          </div>
          <div class="order-grid">
            <div class="cell">
              <span class="cell-key">车辆</span>
              <span class="cell-val">{{ confirmedBooking?.scooterNumber }}</span>
            </div>
            <div class="cell">
              <span class="cell-key">租期</span>
              <span class="cell-val">{{ confirmedBooking?.durationMinutes ? formatMinutesToText(confirmedBooking.durationMinutes) : formatDuration(confirmedBooking?.hireOption) }}</span>
            </div>
            <div class="cell full">
              <span class="cell-key">开始时间</span>
              <span class="cell-val">{{ confirmedBooking?.startTime ? formatDateTime(confirmedBooking.startTime) : '—' }}</span>
            </div>
            <div class="cell full">
              <span class="cell-key">结束时间</span>
              <span class="cell-val">{{ confirmedBooking?.endTime ? formatDateTime(confirmedBooking.endTime) : '—' }}</span>
            </div>
            <div class="cell full">
              <span class="cell-key">服务点</span>
              <span class="cell-val">{{ confirmedBooking?.depotName }}</span>
            </div>
          </div>
          <div class="order-footer">
            <span class="key">实付金额</span>
            <span class="price">¥{{ confirmedBooking?.totalCost }}</span>
          </div>
        </div>

        <div class="pickup-block">
          <span class="pickup-key">取车码</span>
          <div class="pickup-code">{{ confirmedBooking?.confirmationCode }}</div>
          <p class="pickup-tip">请将此码出示给服务点工作人员</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="goToMap">返回地图</el-button>
        <el-button type="primary" @click="goToTrip">查看行程</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Check, Close, Tickets } from '@element-plus/icons-vue'
import { getDepotById } from '@/api/depot'
import { getScooterById } from '@/api/scooter'
import { createBookingByDepot, createBooking, payBooking } from '@/api/booking'
import { getPricingList } from '@/api/pricing'
import { getUserStats } from '@/api/user'
import { validateDiscountCode } from '@/api/discount'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const submitting = ref(false)
const showSuccessModal = ref(false)
const confirmedBooking = ref(null)
const pricingOptions = ref([])

// 从租约选项计算实际分钟数
const hireOptionToMinutes = (option) => {
  const map = { '1hr': 60, '4hr': 240, '1day': 1440, '1week': 10080 }
  return map[option] || 60
}

// 将分钟数转换为可读文本
const formatMinutesToText = (minutes) => {
  if (!minutes) return '未知'
  if (minutes < 60) return `${minutes}分钟`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  if (hours < 24) {
    return mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`
  }
  const days = Math.floor(hours / 24)
  const remainHours = hours % 24
  return remainHours > 0 ? `${days}天${remainHours}小时` : `${days}天`
}

const depot = ref(null)
const selectedScooter = ref(null)
const userStats = ref({})
const profileFlags = ref({ userType: 'none' })
const discountPrice = ref(0)

const discountCodeInput = ref('')
const loadingDiscountCode = ref(false)
const discountCodeResult = ref(null)
const appliedDiscountCode = ref('')

const form = ref({
  hireOption: '1hr',
  paymentMethod: 'credit',
  cardNumber: '',
  expiry: '',
  cvv: ''
})

const getBatteryClass = (level) => {
  if (level >= 60) return 'high'
  if (level >= 30) return 'medium'
  return 'low'
}

const optionLabel = (code) => {
  const m = { '1hr': '1小时', '4hr': '4小时', '1day': '1天', '1week': '1周' }
  return m[code] || code
}

const formatDuration = (option) => {
  const map = { '1hr': '1小时', '4hr': '4小时', '1day': '1天', '1week': '1周' }
  return map[option] || option || ''
}

const formatDateTime = (time) => {
  if (!time) return '—'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const currentPrice = computed(() => {
  const opt = pricingOptions.value.find(p => p.hireOption === form.value.hireOption)
  return opt ? Number(opt.price) : 0
})

const discountAmount = computed(() => {
  if (currentPrice.value <= 0) return 0
  return Math.max(0, currentPrice.value - Number(totalPrice.value))
})

const hasDiscount = computed(() => {
  return profileFlags.value.userType !== 'none' || appliedDiscountCode.value
})

const totalPrice = computed(() => {
  if (appliedDiscountCode.value && discountPrice.value > 0) {
    return Number(discountPrice.value).toFixed(2)
  }
  if (profileFlags.value.userType !== 'none' && currentPrice.value > 0) {
    const rate = getDiscountRate(profileFlags.value.userType)
    return (currentPrice.value * rate).toFixed(2)
  }
  return currentPrice.value.toFixed(2)
})

const getDiscountRate = (userType) => {
  const rates = { student: 0.9, senior: 0.8 }
  return rates[userType] ?? 1
}

const loadPricing = async () => {
  try {
    const list = await getPricingList()
    const order = ['1hr', '4hr', '1day', '1week']
    pricingOptions.value = order.map(k => list.find(p => p.hireOption === k)).filter(Boolean)
    if (pricingOptions.value.length && !pricingOptions.value.find(p => p.hireOption === form.value.hireOption)) {
      form.value.hireOption = pricingOptions.value[0].hireOption
    }
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

const onUserTypeChange = () => {
  localStorage.setItem('capyglide_discount_profile', JSON.stringify({
    userType: profileFlags.value.userType || 'none'
  }))
  fetchDiscountPrice()
}

const fetchDiscountPrice = async () => {
  if (!form.value.hireOption || (profileFlags.value.userType === 'none' && !appliedDiscountCode.value)) {
    discountPrice.value = 0
    return
  }
  try {
    if (appliedDiscountCode.value) {
      const res = await validateDiscountCode({ code: appliedDiscountCode.value })
      discountPrice.value = res?.price ?? res?.discountedPrice ?? 0
    } else if (profileFlags.value.userType !== 'none') {
      discountPrice.value = currentPrice.value * getDiscountRate(profileFlags.value.userType)
    }
  } catch {
    discountPrice.value = 0
  }
}

const applyDiscountCode = async () => {
  const code = discountCodeInput.value.trim()
  if (!code) return
  loadingDiscountCode.value = true
  discountCodeResult.value = null
  try {
    const res = await validateDiscountCode({ code })
    if (res?.valid || res?.code === 200) {
      discountCodeResult.value = {
        valid: true,
        discountPercent: res?.discountPercent || res?.discount || 10
      }
      appliedDiscountCode.value = code
      await fetchDiscountPrice()
      ElMessage.success(`折扣码有效！已节省 ${discountCodeResult.value.discountPercent}%`)
    } else {
      discountCodeResult.value = { valid: false, message: res?.message || '折扣码无效或已过期' }
      appliedDiscountCode.value = ''
    }
  } catch {
    discountCodeResult.value = { valid: false, message: '折扣码验证失败' }
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

const handleSubmit = async () => {
  if (!form.value.cardNumber || !form.value.expiry || !form.value.cvv) {
    ElMessage.warning('请填写完整的支付信息')
    return
  }
  submitting.value = true
  try {
    let bookingRes
    if (selectedScooter.value) {
      bookingRes = await createBooking({
        scooterId: selectedScooter.value.id,
        hireOption: form.value.hireOption
      })
    } else {
      bookingRes = await createBookingByDepot(depot.value.id, form.value.hireOption)
    }

    let bookingId = bookingRes?.id
    if (!bookingId) throw new Error(bookingRes?.message || '预订失败')

    await payBooking(bookingId, {
      cardLast4: form.value.cardNumber.slice(-4),
      amount: Number(totalPrice.value),
      paymentMethod: form.value.paymentMethod
    })

    // 使用后端返回的完整数据更新 confirmedBooking
    if (bookingRes) {
      const backendBooking = bookingRes.data || bookingRes
      const startTime = backendBooking.startTime
      const endTime = backendBooking.endTime
      const durationMinutes = startTime && endTime
        ? Math.round((new Date(endTime).getTime() - new Date(startTime).getTime()) / 60000)
        : hireOptionToMinutes(backendBooking.hireOption || form.value.hireOption)

      confirmedBooking.value = {
        id: backendBooking.id || bookingId,
        scooterNumber: selectedScooter.value?.scooterNumber || backendBooking.scooterNumber,
        depotName: depot.value?.name || backendBooking.depotName,
        hireOption: backendBooking.hireOption || form.value.hireOption,
        totalCost: backendBooking.totalCost || totalPrice.value,
        confirmationCode: backendBooking.confirmationCode,
        startTime: startTime,
        endTime: endTime,
        durationMinutes: durationMinutes
      }
    } else {
      confirmedBooking.value = {
        id: bookingId,
        scooterNumber: selectedScooter.value?.scooterNumber,
        depotName: depot.value?.name,
        hireOption: form.value.hireOption,
        totalCost: totalPrice.value,
        confirmationCode: bookingRes?.confirmationCode,
        durationMinutes: hireOptionToMinutes(form.value.hireOption)
      }
    }
    showSuccessModal.value = true

  } catch (error) {
    ElMessage.error(error.message || '预订失败，请重试')
  } finally {
    submitting.value = false
  }
}

const goToMap = () => {
  showSuccessModal.value = false
  router.push('/map')
}

const goToTrip = () => {
  showSuccessModal.value = false
  router.push('/trip')
}

onMounted(async () => {
  loadProfileFlags()
  await loadPricing()
  try {
    userStats.value = (await getUserStats()) || {}
  } catch {}

  const scooterId = route.query.scooterId
  const depotId = route.query.depotId

  if (scooterId) {
    try {
      const scooterRes = await getScooterById(scooterId)
      selectedScooter.value = scooterRes?.data || scooterRes
      if (!selectedScooter.value) throw new Error()
      if (selectedScooter.value.depotId) {
        const depotRes = await getDepotById(selectedScooter.value.depotId)
        depot.value = depotRes?.data || depotRes?.depot || depotRes
      }
      loading.value = false
      return
    } catch {
      ElMessage.error('获取滑板车信息失败')
      router.push('/map')
      loading.value = false
      return
    }
  }

  if (!depotId) {
    ElMessage.error('未指定服务点或滑板车')
    router.push('/map')
    loading.value = false
    return
  }

  try {
    const res = await getDepotById(depotId)
    depot.value = res?.data || res?.depot || res
    if (!depot.value) throw new Error()
    if (depot.value.availableCount === 0) {
      ElMessage.warning('该服务点暂无可用车辆')
      router.push('/map')
    }
  } catch {
    ElMessage.error('获取服务点信息失败')
    router.push('/map')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.booking {
  padding: 32px 40px;
  max-width: 1200px;
  margin: 0 auto;
  background: linear-gradient(180deg, #e8eef5 0%, #d6e0eb 100%);
  min-height: calc(100vh - 64px);
}

.page-header {
  margin-bottom: 28px;
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

.booking-layout {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 28px;
}

.info-panel {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.info-card {
  background: white;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(30, 58, 95, 0.08);
}

.card-accent {
  height: 4px;
  background: linear-gradient(90deg, #1e3a5f 0%, #3b5998 100%);
}

.card-body {
  padding: 20px;
}

.card-header-row {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 16px;
}

.vehicle-icon {
  width: 52px;
  height: 52px;
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.vehicle-icon svg {
  width: 30px;
  height: 30px;
  color: white;
}

.vehicle-info, .depot-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.vehicle-info .name, .depot-info .name {
  font-size: 16px;
  font-weight: 700;
  color: #1e3a5f;
}

.vehicle-info .loc, .depot-info .addr {
  font-size: 12px;
  color: #7a8fa8;
}

.depot-icon {
  width: 52px;
  height: 52px;
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.depot-icon svg {
  width: 24px;
  height: 24px;
  color: white;
}

.battery {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 12px;
  background: #f0f4f8;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
}

.battery.high { background: #e6f4ea; }
.battery.high .batt-icon { color: #2d8a4e; }
.battery.medium { background: #fef7e6; }
.battery.medium .batt-icon { color: #c4880c; }
.battery.low { background: #fde8e8; }
.battery.low .batt-icon { color: #d14545; }

.batt-icon {
  font-size: 16px;
}

.divider {
  height: 1px;
  background: #e8eef5;
  margin: 16px 0;
}

.detail-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.key {
  font-size: 13px;
  color: #7a8fa8;
}

.val {
  font-size: 13px;
  font-weight: 600;
  color: #1e3a5f;
}

.avail-display {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.avail-num {
  font-size: 42px;
  font-weight: 800;
  color: #1e3a5f;
  line-height: 1;
}

.avail-label {
  font-size: 14px;
  color: #7a8fa8;
}

/* 趣味提示卡片 */
.tips-card {
  background: linear-gradient(135deg, #fef9c3 0%, #fef3c7 100%);
  border-radius: 14px;
  padding: 18px;
  display: flex;
  gap: 14px;
  border: 1px solid #fde68a;
}

.tips-icon {
  width: 44px;
  height: 44px;
  background: white;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.tips-icon svg {
  width: 22px;
  height: 22px;
  color: #c4880c;
}

.tips-content h4 {
  margin: 0 0 6px;
  font-size: 14px;
  font-weight: 700;
  color: #92400e;
}

.tips-content p {
  margin: 0;
  font-size: 13px;
  color: #b45309;
  line-height: 1.5;
}

.form-panel {
  display: flex;
  flex-direction: column;
}

.main-card {
  border-radius: 14px;
  border: none;
  box-shadow: 0 4px 20px rgba(30, 58, 95, 0.1);
}

.main-card :deep(.el-card__header) {
  padding: 18px 24px;
  border-bottom: 1px solid #e8eef5;
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e3a5f;
}

.main-card :deep(.el-card__body) {
  padding: 24px;
}

.form-group {
  margin-bottom: 22px;
}

.group-label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #5a7a9a;
  margin-bottom: 12px;
}

.duration-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.duration-item {
  background: #f0f4f8;
  border: 1px solid #d6e0eb;
  border-radius: 10px;
  padding: 14px 10px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.duration-item:hover {
  border-color: #1e3a5f;
  background: #e8eef5;
}

.duration-item.active {
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  border-color: #1e3a5f;
}

.duration-time {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #1e3a5f;
  margin-bottom: 6px;
}

.duration-price {
  display: block;
  font-size: 18px;
  font-weight: 800;
  color: #1e3a5f;
}

.duration-item.active .duration-time,
.duration-item.active .duration-price {
  color: white;
}

.discount-group {
  display: flex;
  gap: 16px;
}

.coupon-row {
  display: flex;
  gap: 10px;
}

.coupon-feedback {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
  padding: 10px 14px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
}

.coupon-feedback.valid {
  background: #e6f4ea;
  color: #2d8a4e;
}

.coupon-feedback.invalid {
  background: #fde8e8;
  color: #d14545;
}

.coupon-applied {
  margin-top: 10px;
}

.payment-grid {
  display: flex;
  gap: 12px;
}

.payment-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  background: #f0f4f8;
  border: 1px solid #d6e0eb;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 600;
  color: #5a7a9a;
}

.payment-item:hover {
  border-color: #1e3a5f;
}

.payment-item.active {
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  border-color: #1e3a5f;
  color: white;
}

.payment-item svg {
  width: 20px;
  height: 20px;
}

.card-inputs {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.card-row {
  display: flex;
  gap: 12px;
}

.price-block {
  margin-bottom: 20px;
}

.price-list {
  background: #f0f4f8;
  border-radius: 10px;
  padding: 16px;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  font-size: 14px;
  color: #5a7a9a;
}

.price-row.discount {
  color: #2d8a4e;
}

.price-row.total {
  border-top: 1px solid #d6e0eb;
  margin-top: 8px;
  padding-top: 14px;
  font-weight: 700;
  color: #1e3a5f;
}

.original {
  text-decoration: line-through;
}

.final {
  font-size: 24px;
  font-weight: 800;
  color: #1e3a5f;
}

.submit-btn {
  width: 100%;
  height: 50px;
  font-size: 15px;
  font-weight: 700;
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%) !important;
  border: none !important;
  border-radius: 10px;
  margin-bottom: 14px;
}

.submit-btn:hover {
  opacity: 0.9;
}

.terms {
  text-align: center;
  color: #7a8fa8;
  font-size: 12px;
  margin: 0;
}

/* 成功弹窗 */
.success-content {
  padding: 16px 0;
  text-align: center;
}

.success-icon {
  width: 70px;
  height: 70px;
  margin: 0 auto 18px;
  color: #2d8a4e;
}

.success-icon svg {
  width: 100%;
  height: 100%;
}

.success-title {
  margin: 0 0 6px;
  font-size: 22px;
  font-weight: 800;
  color: #1e3a5f;
}

.success-sub {
  margin: 0 0 22px;
  color: #5a7a9a;
  font-size: 14px;
}

.order-block {
  background: #f0f4f8;
  border-radius: 12px;
  padding: 18px;
  text-align: left;
  margin-bottom: 18px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px dashed #d6e0eb;
  margin-bottom: 14px;
}

.order-header .key {
  color: #7a8fa8;
  font-size: 13px;
}

.order-header .val {
  font-size: 16px;
  font-weight: 800;
  color: #1e3a5f;
  letter-spacing: 2px;
}

.order-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 14px;
}

.cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.cell.full {
  grid-column: span 2;
}

.cell-key {
  font-size: 11px;
  color: #7a8fa8;
}

.cell-val {
  font-size: 13px;
  font-weight: 600;
  color: #1e3a5f;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px dashed #d6e0eb;
}

.order-footer .key {
  color: #5a7a9a;
  font-size: 14px;
}

.price {
  font-size: 22px;
  font-weight: 800;
  color: #1e3a5f;
}

.pickup-block {
  background: white;
  border: 1px dashed #d6e0eb;
  border-radius: 12px;
  padding: 18px;
}

.pickup-key {
  display: block;
  font-size: 12px;
  color: #7a8fa8;
  margin-bottom: 10px;
}

.pickup-code {
  font-size: 30px;
  font-weight: 800;
  color: #1e3a5f;
  letter-spacing: 5px;
  margin-bottom: 10px;
}

.pickup-tip {
  margin: 0;
  font-size: 12px;
  color: #7a8fa8;
}
</style>
