<template>
  <div class="admin-dashboard">
    <header class="top-bar">
      <div class="left">
        <img src="/brand-logo.png" alt="" width="44" height="44" class="logo" />
        <div class="title-group">
          <h1>管理后台</h1>
          <span class="subtitle">CapyGlide</span>
        </div>
      </div>
      <div class="right">
        <span class="admin-name">{{ adminName }}</span>
        <el-button type="danger" plain @click="logout">退出登录</el-button>
      </div>
    </header>

    <el-tabs v-model="activeTab" type="border-card" class="main-tabs">
      <!-- 快速导航 -->
      <el-tab-pane label="概览" name="overview">
        <div class="overview-grid">
          <el-card class="quick-link-card" @click="activeTab = 'reports'">
            <el-icon :size="40" color="var(--cg-navy)"><DataLine /></el-icon>
            <h4>收入报表</h4>
            <p>查看收入统计</p>
          </el-card>
          <el-card class="quick-link-card" @click="$router.push('/admin/users')">
            <el-icon :size="40" color="var(--cg-navy)"><User /></el-icon>
            <h4>用户管理</h4>
            <p>管理所有用户</p>
          </el-card>
          <el-card class="quick-link-card" @click="$router.push('/admin/issues')">
            <el-icon :size="40" color="var(--cg-navy)"><Warning /></el-icon>
            <h4>问题报告</h4>
            <p>处理故障与反馈</p>
          </el-card>
        </div>
        <el-row :gutter="20" class="stats-row">
          <el-col :xs="24" :sm="8">
            <div class="stat-card">
              <div class="stat-label">总收入</div>
              <div class="stat-value primary">¥{{ totalIncome }}</div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="8">
            <div class="stat-card">
              <div class="stat-label">本周收入</div>
              <div class="stat-value">¥{{ weeklyIncome }}</div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="8">
            <div class="stat-card">
              <div class="stat-label">今日收入</div>
              <div class="stat-value">¥{{ todayIncome }}</div>
            </div>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 收入报表 -->
      <el-tab-pane label="收入报表" name="reports">
        <div class="pane-header">
          <h3>收入统计报表</h3>
          <el-button type="primary" @click="loadReports">刷新数据</el-button>
        </div>
        <el-row :gutter="20" class="stats-row">
          <el-col :xs="24" :sm="8">
            <div class="stat-card">
              <div class="stat-label">总收入</div>
              <div class="stat-value primary">¥{{ totalIncome }}</div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="8">
            <div class="stat-card">
              <div class="stat-label">本周收入</div>
              <div class="stat-value">¥{{ weeklyIncome }}</div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="8">
            <div class="stat-card">
              <div class="stat-label">今日收入</div>
              <div class="stat-value">¥{{ todayIncome }}</div>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :xs="24" :md="12">
            <div class="chart-card">
              <h4>各租期收入分布</h4>
              <div ref="chartByOptionRef" class="chart"></div>
            </div>
          </el-col>
          <el-col :xs="24" :md="12">
            <div class="chart-card">
              <h4>每日收入趋势</h4>
              <div ref="chartDailyRef" class="chart"></div>
            </div>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 代客预订 -->
      <el-tab-pane label="代客预订" name="staff">
        <div class="pane-header">
          <h3>员工代客预订</h3>
        </div>
        <el-card class="form-card">
          <el-form :model="staffForm" label-width="100px" class="staff-form">
            <el-form-item label="选择用户" required>
              <el-select v-model="staffForm.userId" filterable placeholder="搜索并选择用户" style="width: 100%">
                <el-option
                  v-for="u in users"
                  :key="u.id"
                  :label="`${u.username} (${u.email || '无邮箱'})`"
                  :value="u.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="选择车辆" required>
              <el-select v-model="staffForm.scooterId" filterable placeholder="选择可用车辆" style="width: 100%">
                <el-option
                  v-for="s in availableScooters"
                  :key="s.id"
                  :label="`${s.scooterNumber || s.name} - ${s.status}`"
                  :value="s.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="租用时长" required>
              <el-select v-model="staffForm.hireOption" style="width: 100%">
                <el-option label="1 小时" value="1hr" />
                <el-option label="4 小时" value="4hr" />
                <el-option label="1 天" value="1day" />
                <el-option label="1 周" value="1week" />
              </el-select>
            </el-form-item>
            <el-form-item label="开始时间" required>
              <el-date-picker
                v-model="staffForm.startTime"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" :loading="staffLoading" @click="submitStaffBooking">
                创建预订订单
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 车辆状态 -->
      <el-tab-pane label="车辆管理" name="scooters">
        <div class="pane-header">
          <h3>车辆状态管理</h3>
          <el-button @click="loadScooters">刷新列表</el-button>
        </div>
        <el-table :data="scooters" stripe size="small">
          <el-table-column prop="scooterNumber" label="编号" width="140" />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="batteryLevel" label="电量" width="80">
            <template #default="{ row }">{{ row.batteryLevel || '—' }}%</template>
          </el-table-column>
          <el-table-column prop="location" label="位置" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="setStatus(row.id, 'AVAILABLE')">可用</el-button>
              <el-button size="small" type="warning" @click="setStatus(row.id, 'IN_USE')">使用中</el-button>
              <el-button size="small" type="info" @click="setStatus(row.id, 'MAINTENANCE')">维护</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 价格配置 -->
      <el-tab-pane label="价格配置" name="pricing">
        <div class="pane-header">
          <h3>价格与租用选项</h3>
        </div>
        <el-card class="config-card">
          <h4>价格方案 (Pricing)</h4>
          <el-table :data="pricingList" stripe size="small">
            <el-table-column prop="hireOption" label="租期代码" width="120" />
            <el-table-column prop="description" label="说明" />
            <el-table-column prop="price" label="价格 (¥)" width="120">
              <template #default="{ row }">
                <el-input-number v-model="row.price" :min="0" :precision="2" :step="1" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="savePricing(row)">保存</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
        <el-card class="config-card">
          <h4>租用选项 (Hire Options)</h4>
          <el-table :data="hireOptions" stripe size="small">
            <el-table-column prop="code" label="代码" width="100" />
            <el-table-column prop="label" label="名称" />
            <el-table-column prop="durationMinutes" label="时长(分钟)" width="120" />
            <el-table-column prop="price" label="价格" width="120">
              <template #default="{ row }">
                <el-input-number v-model="row.price" :min="0" :precision="2" :step="1" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="saveHireOption(row)">保存</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 反馈处理 -->
      <el-tab-pane label="用户反馈" name="feedback">
        <div class="pane-header">
          <h3>反馈与高优先级问题</h3>
          <el-button type="primary" @click="loadFeedbacks">刷新</el-button>
        </div>
        <el-table :data="feedbacks" stripe size="small">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="description" label="内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="priority" label="优先级" width="100">
            <template #default="{ row }">
              <el-tag :type="getPriorityType(row.priority)">{{ row.priority }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120" />
          <el-table-column prop="adminResponse" label="回复" min-width="160" show-overflow-tooltip />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="openProcess(row)">处理</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 故障工单 -->
      <el-tab-pane label="故障工单" name="issues">
        <div class="pane-header">
          <h3>车辆故障报告</h3>
          <el-button @click="loadIssues">刷新</el-button>
        </div>
        <el-table :data="issueReports" stripe size="small">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="scooterId" label="车辆ID" width="100" />
          <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
          <el-table-column prop="priority" label="优先级" width="100">
            <template #default="{ row }">
              <el-tag :type="getPriorityType(row.priority)">{{ row.priority }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120" />
          <el-table-column prop="createdAt" label="时间" width="170" />
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 处理反馈弹窗 -->
    <el-dialog v-model="processVisible" title="处理反馈" width="520px">
      <el-form v-if="processRow" :model="processForm" label-width="100px">
        <el-form-item label="处理状态">
          <el-select v-model="processForm.status" style="width: 100%">
            <el-option label="待处理 (OPEN)" value="OPEN" />
            <el-option label="处理中 (IN_PROGRESS)" value="IN_PROGRESS" />
            <el-option label="已解决 (RESOLVED)" value="RESOLVED" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="processForm.priority" style="width: 100%">
            <el-option label="低 (LOW)" value="LOW" />
            <el-option label="中 (MEDIUM)" value="MEDIUM" />
            <el-option label="高 (HIGH)" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item label="管理员回复">
          <el-input v-model="processForm.adminResponse" type="textarea" :rows="4" placeholder="请输入回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processVisible = false">取消</el-button>
        <el-button type="primary" :loading="processLoading" @click="submitProcess">提交处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { DataLine, User, Warning } from '@element-plus/icons-vue'
import {
  adminCreateBooking,
  getWeeklyIncomeReport,
  getDailyIncomeReport,
  getAllFeedbacks,
  processFeedback,
  getAdminPricing,
  updateAdminPricing
} from '@/api/admin'
import { listUsers } from '@/api/user'
import { getScooters, updateScooterStatus } from '@/api/scooter'
import { listHireOptions, updateHireOption } from '@/api/hireOptions'
import request from '@/utils/request'

const router = useRouter()

// 状态
const activeTab = ref('overview')
const adminName = ref('管理员')

// 管理员信息
onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      adminName.value = user.username || '管理员'
    } catch (e) {}
  }
})

// 数据
const users = ref([])
const scooters = ref([])
const pricingList = ref([])
const hireOptions = ref([])
const feedbacks = ref([])
const issueReports = ref([])

// 收入数据
const totalIncome = ref('0.00')
const weeklyIncome = ref('0.00')
const todayIncome = ref('0.00')

// 图表
const chartByOptionRef = ref(null)
const chartDailyRef = ref(null)
let chartOption = null
let chartDaily = null

// 代客预订
const staffForm = ref({
  userId: null,
  scooterId: null,
  hireOption: '1hr',
  startTime: new Date()
})
const staffLoading = ref(false)

// 处理反馈
const processVisible = ref(false)
const processRow = ref(null)
const processForm = ref({ status: 'OPEN', priority: 'LOW', adminResponse: '' })
const processLoading = ref(false)

// 计算可用车辆
const availableScooters = computed(() =>
  scooters.value.filter(s => s.status === 'AVAILABLE')
)

// 工具函数
const getStatusType = (status) => {
  const map = { AVAILABLE: 'success', IN_USE: 'warning', MAINTENANCE: 'info' }
  return map[status] || 'info'
}

const getPriorityType = (priority) => {
  const map = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info' }
  return map[priority] || 'info'
}

// 退出
const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.success('已退出登录')
  router.push('/admin/login')
}

// 加载用户列表
const loadUsers = async () => {
  try {
    const res = await listUsers()
    users.value = Array.isArray(res) ? res.filter(u => u.role !== 'ADMIN') : []
  } catch (e) {
    console.error(e)
  }
}

// 加载车辆
const loadScooters = async () => {
  try {
    const res = await getScooters()
    scooters.value = Array.isArray(res) ? res : []
  } catch (e) {
    console.error(e)
  }
}

// 加载价格配置
const loadPricing = async () => {
  try {
    const res = await getAdminPricing()
    pricingList.value = Array.isArray(res) ? res.map(r => ({ ...r, price: Number(r.price) })) : []
  } catch (e) {
    console.error(e)
  }
}

const loadHireOptions = async () => {
  try {
    const res = await listHireOptions()
    hireOptions.value = Array.isArray(res) ? res.map(r => ({ ...r, price: Number(r.price) })) : []
  } catch (e) {
    console.error(e)
  }
}

// 加载反馈
const loadFeedbacks = async () => {
  try {
    const res = await getAllFeedbacks()
    feedbacks.value = Array.isArray(res) ? res : []
  } catch (e) {
    console.error(e)
  }
}

// 加载故障工单
const loadIssues = async () => {
  try {
    const res = await request({ url: '/api/issues', method: 'get' })
    issueReports.value = Array.isArray(res) ? res : []
  } catch (e) {
    console.error(e)
  }
}

// 加载报表数据
const loadReports = async () => {
  try {
    const weekly = await getWeeklyIncomeReport()
    const daily = await getDailyIncomeReport()

    // 计算总收入
    const byOpt = weekly?.incomeByHireOption || {}
    const optValues = Object.values(byOpt)
    const total = optValues.reduce((sum, v) => sum + Number(v || 0), 0)
    totalIncome.value = total.toFixed(2)
    weeklyIncome.value = total.toFixed(2)

    // 今日收入
    const today = new Date().toISOString().split('T')[0]
    const dailyMap = daily?.dailyIncome || {}
    todayIncome.value = (Number(dailyMap[today]) || 0).toFixed(2)

    // 渲染图表
    await nextTick()
    renderCharts(byOpt, dailyMap)
  } catch (e) {
    console.error(e)
    ElMessage.error('加载报表失败')
  }
}

// 渲染图表
const renderCharts = (byOpt, dailyMap) => {
  // 按租期收入柱状图
  if (chartByOptionRef.value) {
    if (!chartOption) chartOption = echarts.init(chartByOptionRef.value)
    chartOption.setOption({
      color: ['#1e3a5f'],
      title: { text: '各租期收入', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: Object.keys(byOpt) },
      yAxis: { type: 'value', name: '金额 (¥)' },
      series: [{ type: 'bar', data: Object.values(byOpt), itemStyle: { borderRadius: [4, 4, 0, 0] } }]
    })
  }

  // 每日趋势折线图
  if (chartDailyRef.value) {
    if (!chartDaily) chartDaily = echarts.init(chartDailyRef.value)
    const days = Object.keys(dailyMap).sort()
    chartDaily.setOption({
      color: ['#6b9ac4'],
      title: { text: '每日收入趋势', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: days, axisLabel: { rotate: 40 } },
      yAxis: { type: 'value', name: '金额 (¥)' },
      series: [{
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.3 },
        data: days.map(d => dailyMap[d])
      }]
    })
  }
}

// 代客预订提交
const submitStaffBooking = async () => {
  if (!staffForm.value.userId || !staffForm.value.scooterId || !staffForm.value.startTime) {
    ElMessage.warning('请填写完整信息')
    return
  }
  staffLoading.value = true
  try {
    await adminCreateBooking({
      userId: staffForm.value.userId,
      scooterId: staffForm.value.scooterId,
      hireOption: staffForm.value.hireOption,
      startTime: staffForm.value.startTime.toISOString()
    })
    ElMessage.success('代客订单创建成功')
    staffForm.value = { userId: null, scooterId: null, hireOption: '1hr', startTime: new Date() }
  } catch (e) {
    console.error(e)
  } finally {
    staffLoading.value = false
  }
}

// 更新车辆状态
const setStatus = async (id, status) => {
  try {
    await updateScooterStatus(id, status)
    ElMessage.success('状态已更新')
    await loadScooters()
  } catch (e) {
    console.error(e)
  }
}

// 保存价格
const savePricing = async (row) => {
  try {
    await updateAdminPricing({
      id: row.id,
      hireOption: row.hireOption,
      price: row.price,
      description: row.description
    })
    ElMessage.success('价格已保存')
  } catch (e) {
    console.error(e)
  }
}

// 保存租用选项
const saveHireOption = async (row) => {
  try {
    await updateHireOption(row.id, {
      code: row.code,
      label: row.label,
      durationMinutes: row.durationMinutes,
      price: row.price
    })
    ElMessage.success('租用选项已保存')
    await loadHireOptions()
  } catch (e) {
    console.error(e)
  }
}

// 打开处理反馈
const openProcess = (row) => {
  processRow.value = row
  processForm.value = {
    status: row.status || 'OPEN',
    priority: row.priority || 'LOW',
    adminResponse: row.adminResponse || ''
  }
  processVisible.value = true
}

// 提交处理
const submitProcess = async () => {
  if (!processRow.value) return
  processLoading.value = true
  try {
    await processFeedback(processRow.value.id, { ...processForm.value })
    ElMessage.success('处理结果已保存')
    processVisible.value = false
    await loadFeedbacks()
  } catch (e) {
    console.error(e)
  } finally {
    processLoading.value = false
  }
}

// 窗口调整
const handleResize = () => {
  chartOption?.resize()
  chartDaily?.resize()
}

// Tab切换
watch(activeTab, async (v) => {
  if (v === 'reports') {
    await nextTick()
    loadReports()
  }
  if (v === 'feedback') loadFeedbacks()
  if (v === 'issues') loadIssues()
  if (v === 'staff') { loadUsers(); loadScooters() }
  if (v === 'scooters') loadScooters()
  if (v === 'pricing') { loadPricing(); loadHireOptions() }
})

// 初始化
onMounted(async () => {
  await Promise.all([
    loadUsers(),
    loadScooters(),
    loadPricing(),
    loadHireOptions(),
    loadFeedbacks(),
    loadIssues(),
    loadReports()
  ])

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartOption?.dispose()
  chartDaily?.dispose()
})
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  background: var(--cg-bg);
  padding: 0;
}

/* 顶部栏 */
.top-bar {
  background: var(--cg-navy);
  color: white;
  padding: 12px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.top-bar .left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.logo {
  border-radius: var(--cg-radius-md);
}

.title-group h1 {
  margin: 0;
  font-size: 1.3rem;
  font-weight: 700;
}

.title-group .subtitle {
  font-size: 0.8rem;
  opacity: 0.8;
}

.top-bar .right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.admin-name {
  font-weight: 500;
}

/* Tabs */
.main-tabs {
  margin: 20px;
  border-radius: var(--cg-radius-lg);
  overflow: hidden;
}

.pane-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.pane-header h3 {
  margin: 0;
  color: var(--cg-navy);
  font-size: 1.1rem;
}

/* 概览页面 */
.overview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.quick-link-card {
  cursor: pointer;
  text-align: center;
  transition: all 0.3s;
}

.quick-link-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--cg-shadow-lg);
}

.quick-link-card h4 {
  margin: 12px 0 4px;
  color: var(--cg-navy);
}

.quick-link-card p {
  margin: 0;
  font-size: 13px;
  color: var(--cg-text-light);
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  background: var(--cg-white);
  border-radius: var(--cg-radius-md);
  padding: 20px;
  box-shadow: var(--cg-shadow);
  text-align: center;
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 800;
  color: var(--cg-navy);
}

.stat-value.primary {
  color: var(--cg-accent);
}

/* 图表 */
.chart-card {
  background: var(--cg-white);
  border-radius: var(--cg-radius-md);
  padding: 20px;
  box-shadow: var(--cg-shadow);
  margin-bottom: 20px;
}

.chart-card h4 {
  margin: 0 0 12px;
  color: var(--cg-navy);
  font-size: 0.95rem;
}

.chart {
  height: 280px;
  width: 100%;
}

/* 表单卡片 */
.form-card, .config-card {
  background: var(--cg-white);
  border-radius: var(--cg-radius-md);
  box-shadow: var(--cg-shadow);
  margin-bottom: 16px;
}

.form-card :deep(.el-card__body),
.config-card :deep(.el-card__body) {
  padding: 24px;
}

.config-card h4 {
  margin: 0 0 16px;
  color: var(--cg-navy);
  font-size: 1rem;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5e7eb;
}

.staff-form {
  max-width: 480px;
}
</style>
