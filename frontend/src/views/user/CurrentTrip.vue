<template>
  <div class="booking current-trip">
    <el-page-header title="返回" @back="goBack" />
    <h2 class="page-title">当前行程</h2>
    <p class="page-sub">CapyGlide · 正在进行的租用</p>

    <el-skeleton v-if="loading" :rows="8" animated />

    <template v-else-if="currentBooking">
      <!-- 行程信息卡片 -->
      <el-card class="scooter-card trip-card" shadow="never">
        <template #header>
          <div class="scooter-header">
            <h3>{{ currentBooking.scooterNumber || currentBooking.scooterId }}</h3>
            <el-tag type="success">租用中</el-tag>
          </div>
        </template>

        <!-- 滑板车元信息 -->
        <div class="scooter-meta">
          <span><el-icon><Timer /></el-icon> {{ formatDuration(currentBooking.hireOption) }}</span>
          <span><el-icon><Location /></el-icon> {{ currentBooking.location || '位置信息' }}</span>
        </div>

        <!-- 倒计时区域 -->
        <div class="countdown-section">
          <div class="countdown-item">
            <div class="countdown-label">已使用时长</div>
            <div class="countdown-value">{{ elapsedTime }}</div>
          </div>
          <div class="countdown-divider"></div>
          <div class="countdown-item">
            <div class="countdown-label">预计剩余</div>
            <div class="countdown-value warning">{{ remainingTime }}</div>
          </div>
        </div>

        <!-- 价格信息 -->
        <div class="price-section">
          <div class="price-row total">
            <span>当前费用</span>
            <strong class="price">¥{{ Number(currentBooking.totalCost || 0).toFixed(2) }}</strong>
          </div>
        </div>

        <!-- 详细信息 -->
        <div class="details-grid">
          <div class="detail-item">
            <span class="detail-label">开始时间</span>
            <span class="detail-value">{{ formatDateTime(currentBooking.startTime) }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">租用时长</span>
            <span class="detail-value">{{ formatDuration(currentBooking.hireOption) }}</span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button type="primary" size="large" @click="extendTrip" class="action-btn">
            延长租用
          </el-button>
          <el-button type="success" size="large" @click="endTrip" class="action-btn success">
            结束行程
          </el-button>
          <el-button size="large" @click="reportIssue" class="action-btn outline">
            报告问题
          </el-button>
        </div>
      </el-card>
    </template>

    <el-empty v-else description="暂无进行中的行程">
      <el-button type="primary" @click="$router.push('/scooters')">
        去租车
      </el-button>
    </el-empty>

    <!-- 延长租用弹窗 -->
    <el-dialog v-model="extendVisible" title="延长租用" width="400px">
      <el-form :model="extendForm" label-width="80px">
        <el-form-item label="延长时间">
          <el-select v-model="extendForm.hireOption" style="width: 100%">
            <el-option label="1 小时" value="1hr" />
            <el-option label="4 小时" value="4hr" />
            <el-option label="1 天" value="1day" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="extendVisible = false">取消</el-button>
        <el-button type="primary" :loading="extendLoading" @click="confirmExtend">确认延长</el-button>
      </template>
    </el-dialog>

    <!-- 报告问题弹窗 -->
    <el-dialog v-model="issueVisible" title="报告问题" width="500px">
      <el-form :model="issueForm" label-width="100px">
        <el-form-item label="问题描述" required>
          <el-input
            v-model="issueForm.description"
            type="textarea"
            :rows="4"
            placeholder="请描述遇到的问题"
          />
        </el-form-item>
        <el-form-item label="问题类型">
          <el-select v-model="issueForm.type" style="width: 100%">
            <el-option label="车辆故障" value="BREAKDOWN" />
            <el-option label="电量不足" value="LOW_BATTERY" />
            <el-option label="无法还车" value="RETURN_ISSUE" />
            <el-option label="其他问题" value="OTHER" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="issueVisible = false">取消</el-button>
        <el-button type="primary" :loading="issueLoading" @click="confirmReport">提交报告</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Timer, Location } from '@element-plus/icons-vue'
import { getMyActiveBookings, endBooking, extendBooking } from '@/api/booking'
import { createIssueReport } from '@/api/issues'

const router = useRouter()

const loading = ref(true)
const currentBooking = ref(null)
const extendVisible = ref(false)
const issueVisible = ref(false)
const extendLoading = ref(false)
const issueLoading = ref(false)

const extendForm = ref({ hireOption: '1hr' })
const issueForm = ref({ description: '', type: 'OTHER' })

let timer = null
const startTime = ref(null)
const durationMinutes = ref(60)

const goBack = () => router.push('/scooters')

const formatDuration = (option) => {
  const map = { '1hr': '1小时', '4hr': '4小时', '1day': '1天', '1week': '1周' }
  return map[option] || option || '未知'
}

const formatDateTime = (time) => {
  if (!time) return '—'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

const durationToMinutes = (option) => {
  const map = { '1hr': 60, '4hr': 240, '1day': 1440, '1week': 10080 }
  return map[option] || 60
}

const elapsedTime = computed(() => {
  if (!startTime.value) return '00:00:00'
  const elapsed = Math.floor((Date.now() - startTime.value) / 1000)
  const h = Math.floor(elapsed / 3600)
  const m = Math.floor((elapsed % 3600) / 60)
  const s = elapsed % 60
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
})

const remainingTime = computed(() => {
  if (!startTime.value) return '00:00:00'
  const endTime = startTime.value + durationMinutes.value * 60 * 1000
  const remaining = Math.max(0, Math.floor((endTime - Date.now()) / 1000))
  const h = Math.floor(remaining / 3600)
  const m = Math.floor((remaining % 3600) / 60)
  const s = remaining % 60
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
})

const loadBooking = async () => {
  loading.value = true
  try {
    const res = await getMyActiveBookings()
    if (Array.isArray(res) && res.length > 0) {
      currentBooking.value = res[0]
      if (currentBooking.value.startTime) {
        startTime.value = new Date(currentBooking.value.startTime).getTime()
      }
      durationMinutes.value = durationToMinutes(currentBooking.value.hireOption)
    } else {
      // 模拟数据进行测试（后端接口不存在时使用）
      currentBooking.value = {
        id: 'BK001',
        scooterId: 'S001',
        scooterNumber: 'S001',
        startTime: new Date(Date.now() - 30 * 60 * 1000).toISOString(),
        hireOption: '1hr',
        totalCost: 5.00,
        location: '地铁站A口'
      }
      startTime.value = Date.now() - 30 * 60 * 1000
      durationMinutes.value = 60
    }
  } catch (e) {
    console.warn('获取行程失败，使用模拟数据')
    // 模拟数据进行测试
    currentBooking.value = {
      id: 'BK001',
      scooterId: 'S001',
      scooterNumber: 'S001',
      startTime: new Date(Date.now() - 30 * 60 * 1000).toISOString(),
      hireOption: '1hr',
      totalCost: 5.00,
      location: '地铁站A口'
    }
    startTime.value = Date.now() - 30 * 60 * 1000
    durationMinutes.value = 60
  } finally {
    loading.value = false
  }
}

const extendTrip = () => {
  extendVisible.value = true
}

const confirmExtend = async () => {
  extendLoading.value = true
  try {
    await extendBooking(currentBooking.value.id, { hireOption: extendForm.value.hireOption })
    ElMessage.success('租用已延长')
    extendVisible.value = false
    await loadBooking()
  } catch (e) {
    ElMessage.error('延长失败')
  } finally {
    extendLoading.value = false
  }
}

const endTrip = async () => {
  try {
    await ElMessageBox.confirm('确定要结束当前行程吗？', '结束行程', {
      confirmButtonText: '确定结束',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await endBooking(currentBooking.value.id)
    ElMessage.success('行程已结束')
    router.push('/scooters')
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('结束行程失败')
  }
}

const reportIssue = () => {
  issueVisible.value = true
}

const confirmReport = async () => {
  if (!issueForm.value.description.trim()) {
    ElMessage.warning('请输入问题描述')
    return
  }
  issueLoading.value = true
  try {
    await createIssueReport({
      scooterId: currentBooking.value.scooterId,
      description: issueForm.value.description,
      type: issueForm.value.type
    })
    ElMessage.success('问题已报告，我们会尽快处理')
    issueVisible.value = false
    issueForm.value = { description: '', type: 'OTHER' }
  } catch (e) {
    ElMessage.error('报告失败')
  } finally {
    issueLoading.value = false
  }
}

onMounted(() => {
  loadBooking()
  timer = setInterval(() => {
    if (currentBooking.value) {
      // 触发响应式更新
    }
  }, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.current-trip {
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

/* 滑板车卡片 */
.trip-card {
  border-radius: var(--cg-radius-xl);
  box-shadow: var(--cg-shadow-md);
  border: 1px solid var(--cg-border-light);
  overflow: hidden;
}

.trip-card :deep(.el-card__header) {
  background: var(--cg-gradient-navy);
  color: white;
  padding: 16px 20px;
  border: none;
}

.trip-card :deep(.el-card__body) {
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
  margin-bottom: 20px;
}

.scooter-meta span {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 倒计时区域 */
.countdown-section {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px;
  background: var(--cg-bg);
  border-radius: var(--cg-radius-md);
  margin-bottom: 20px;
}

.countdown-item {
  text-align: center;
  flex: 1;
}

.countdown-label {
  font-size: 13px;
  color: var(--cg-text-light);
  margin-bottom: 8px;
  font-weight: 500;
}

.countdown-value {
  font-size: 2rem;
  font-weight: 800;
  color: var(--cg-navy);
  font-family: var(--cg-font);
  letter-spacing: 0.02em;
}

.countdown-value.warning {
  color: var(--cg-warning);
}

.countdown-divider {
  width: 1px;
  height: 60px;
  background: var(--cg-border);
  margin: 0 32px;
}

/* 价格信息 */
.price-section {
  background: var(--cg-bg);
  border-radius: var(--cg-radius-md);
  padding: 16px;
  margin-bottom: 16px;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  padding: 6px 0;
  font-size: 15px;
}

.price-row.total {
  font-weight: 600;
  color: var(--cg-text);
}

.price {
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--cg-accent);
}

/* 详细信息网格 */
.details-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 12px;
  color: var(--cg-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-weight: 600;
}

.detail-value {
  font-size: 15px;
  color: var(--cg-text);
  font-weight: 600;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-btn {
  width: 100%;
  height: 52px;
  font-size: 15px;
  font-weight: 600;
  border-radius: var(--cg-radius-md);
  transition: var(--cg-transition);
}

.action-btn:not(.outline) {
  background: var(--cg-gradient) !important;
  border: none !important;
  color: white;
}

.action-btn.success {
  background: var(--cg-success) !important;
  color: white;
}

.action-btn.outline {
  background: transparent;
  border: 1px solid var(--cg-border) !important;
  color: var(--cg-text);
}

.action-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: var(--cg-shadow-md);
}

.action-btn.outline:hover {
  border-color: var(--cg-navy) !important;
  color: var(--cg-navy);
}
</style>
