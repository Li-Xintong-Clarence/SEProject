<template>
  <div class="admin-dash">
    <header class="top">
      <div class="left">
        <img src="/brand-logo.png" alt="" width="40" height="40" class="logo" />
        <h1>管理后台</h1>
      </div>
      <el-button @click="logout">退出</el-button>
    </header>

    <el-tabs v-model="tab" type="border-card" class="tabs">
      <el-tab-pane label="收入报表 (ID19/20/21)" name="reports">
        <p class="hint">按租期类型的累计收入（已支付/已完成订单）与按日汇总。</p>
        <el-row :gutter="16">
          <el-col :xs="24" :md="12">
            <div ref="chartWeeklyRef" class="chart" />
          </el-col>
          <el-col :xs="24" :md="12">
            <div ref="chartDailyRef" class="chart" />
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="代客预订 (ID9)" name="staff">
        <el-form :model="staffForm" label-width="100px" class="form-narrow">
          <el-form-item label="用户">
            <el-select v-model="staffForm.userId" filterable placeholder="选择注册用户" style="width: 100%">
              <el-option
                v-for="u in users"
                :key="u.id"
                :label="`${u.username} (#${u.id})`"
                :value="u.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="滑板车">
            <el-select v-model="staffForm.scooterId" filterable placeholder="选择车辆" style="width: 100%">
              <el-option
                v-for="s in scooters"
                :key="s.id"
                :label="`${s.scooterNumber} (${s.status})`"
                :value="s.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="租期">
            <el-select v-model="staffForm.hireOption" style="width: 100%">
              <el-option label="1 小时" value="1hr" />
              <el-option label="4 小时" value="4hr" />
              <el-option label="1 天" value="1day" />
              <el-option label="1 周" value="1week" />
            </el-select>
          </el-form-item>
          <el-form-item label="开始时间">
            <el-date-picker v-model="staffForm.startTime" type="datetime" style="width: 100%" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="staffLoading" @click="submitStaffBooking">创建订单</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="车辆状态 (ID10)" name="scooters">
        <el-table :data="scooters" size="small">
          <el-table-column prop="scooterNumber" label="编号" width="120" />
          <el-table-column prop="status" label="状态" width="120" />
          <el-table-column prop="location" label="位置描述" />
          <el-table-column label="操作" width="280">
            <template #default="{ row }">
              <el-button size="small" @click="setStatus(row.id, 'AVAILABLE')">可用</el-button>
              <el-button size="small" @click="setStatus(row.id, 'IN_USE')">使用中</el-button>
              <el-button size="small" @click="setStatus(row.id, 'MAINTENANCE')">维护</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="价格配置 (ID16)" name="pricing">
        <el-table :data="pricingList" size="small">
          <el-table-column prop="hireOption" label="租期代码" width="100" />
          <el-table-column prop="description" label="说明" />
          <el-table-column prop="price" label="价格 (¥)" width="120">
            <template #default="{ row }">
              <el-input-number v-model="row.price" :min="0" :step="1" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="保存" width="100">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="savePricing(row)">保存</el-button>
            </template>
          </el-table-column>
        </el-table>

        <h3 class="section-title">租用选项表（hire_option）</h3>
        <el-table :data="hireOptions" size="small">
          <el-table-column prop="code" label="代码" width="90" />
          <el-table-column prop="label" label="标签" />
          <el-table-column prop="durationMinutes" label="时长(分)" width="100" />
          <el-table-column prop="price" label="标价" width="120">
            <template #default="{ row }">
              <el-input-number v-model="row.price" :min="0" :step="1" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="保存" width="100">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="saveHireOption(row)">保存</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="反馈 / 高优先级 (ID14/15)" name="feedback">
        <el-button type="primary" class="mb" @click="loadFeedback">刷新列表</el-button>
        <el-table :data="feedbacks" size="small">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="description" label="内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="priority" label="优先级" width="100" />
          <el-table-column prop="status" label="状态" width="110" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" link @click="openProcess(row)">处理</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="故障工单 (ID13)" name="issues">
        <el-button type="primary" class="mb" @click="loadIssues">刷新</el-button>
        <el-table :data="issueReports" size="small">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="scooterId" label="车辆ID" width="90" />
          <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
          <el-table-column prop="priority" label="优先级" width="100" />
          <el-table-column prop="status" label="状态" width="110" />
          <el-table-column prop="createdAt" label="时间" width="170" />
        </el-table>
        <p class="hint">说明：工单优先级调整若需后台接口，可让后端增加 PUT /api/issues/{id}。</p>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="processVisible" title="处理反馈" width="520px">
      <el-form v-if="processRow" label-width="100px">
        <el-form-item label="状态">
          <el-select v-model="processForm.status" style="width: 100%">
            <el-option label="待处理 OPEN" value="OPEN" />
            <el-option label="处理中 IN_PROGRESS" value="IN_PROGRESS" />
            <el-option label="已解决 RESOLVED" value="RESOLVED" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="processForm.priority" style="width: 100%">
            <el-option label="低 LOW" value="LOW" />
            <el-option label="中 MEDIUM" value="MEDIUM" />
            <el-option label="高 HIGH" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item label="回复">
          <el-input v-model="processForm.adminResponse" type="textarea" rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processVisible = false">取消</el-button>
        <el-button type="primary" :loading="processLoading" @click="submitProcess">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
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
const tab = ref('reports')
const chartWeeklyRef = ref(null)
const chartDailyRef = ref(null)
let chartW = null
let chartD = null

const users = ref([])
const scooters = ref([])
const pricingList = ref([])
const hireOptions = ref([])
const feedbacks = ref([])
const issueReports = ref([])

const staffForm = ref({
  userId: null,
  scooterId: null,
  hireOption: '1hr',
  startTime: new Date()
})
const staffLoading = ref(false)

const processVisible = ref(false)
const processRow = ref(null)
const processForm = ref({ status: 'OPEN', priority: 'LOW', adminResponse: '' })
const processLoading = ref(false)

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.push('/admin/login')
}

const loadBase = async () => {
  try {
    const [u, s, p, h] = await Promise.all([
      listUsers(),
      getScooters(),
      getAdminPricing(),
      listHireOptions()
    ])
    users.value = Array.isArray(u) ? u.filter((x) => x.role !== 'ADMIN') : []
    scooters.value = Array.isArray(s) ? s : []
    pricingList.value = Array.isArray(p) ? p.map((r) => ({ ...r, price: Number(r.price) })) : []
    hireOptions.value = Array.isArray(h) ? h.map((r) => ({ ...r, price: Number(r.price) })) : []
  } catch (e) {
    console.error(e)
  }
}

const renderCharts = async () => {
  await nextTick()
  await new Promise((r) => setTimeout(r, 120))
  try {
    const weekly = await getWeeklyIncomeReport()
    const daily = await getDailyIncomeReport()
    const byOpt = weekly?.incomeByHireOption || {}
    const dailyMap = daily?.dailyIncome || {}

    if (chartWeeklyRef.value) {
      if (!chartW) chartW = echarts.init(chartWeeklyRef.value)
      chartW.setOption({
        color: ['#1e3a5f'],
        title: { text: '各租期收入汇总', left: 'center', textStyle: { fontSize: 14 } },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: Object.keys(byOpt) },
        yAxis: { type: 'value', name: '¥' },
        series: [{ type: 'bar', data: Object.values(byOpt) }]
      })
    }

    if (chartDailyRef.value) {
      if (!chartD) chartD = echarts.init(chartDailyRef.value)
      const days = Object.keys(dailyMap).sort()
      chartD.setOption({
        color: ['#6b9ac4'],
        title: { text: '按日收入', left: 'center', textStyle: { fontSize: 14 } },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: days, axisLabel: { rotate: 40 } },
        yAxis: { type: 'value', name: '¥' },
        series: [{ type: 'line', smooth: true, areaStyle: {}, data: days.map((d) => dailyMap[d]) }]
      })
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('加载报表失败')
  }
}

const submitStaffBooking = async () => {
  if (!staffForm.value.userId || !staffForm.value.scooterId || !staffForm.value.startTime) {
    ElMessage.warning('请填写完整')
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
    ElMessage.success('代客订单已创建（待用户支付或后台流程）')
  } catch (e) {
    console.error(e)
  } finally {
    staffLoading.value = false
  }
}

const setStatus = async (id, status) => {
  try {
    await updateScooterStatus(id, status)
    ElMessage.success('状态已更新')
    await loadBase()
  } catch (e) {
    console.error(e)
  }
}

const savePricing = async (row) => {
  try {
    await updateAdminPricing({
      id: row.id,
      hireOption: row.hireOption,
      price: row.price,
      description: row.description
    })
    ElMessage.success('已保存')
  } catch (e) {
    console.error(e)
  }
}

const saveHireOption = async (row) => {
  try {
    await updateHireOption(row.id, {
      code: row.code,
      label: row.label,
      durationMinutes: row.durationMinutes,
      price: row.price,
      displayOrder: row.displayOrder,
      isActive: row.isActive
    })
    ElMessage.success('租用选项已更新')
    await loadBase()
  } catch (e) {
    console.error(e)
  }
}

const loadFeedback = async () => {
  try {
    feedbacks.value = (await getAllFeedbacks()) || []
  } catch (e) {
    console.error(e)
  }
}

const loadIssues = async () => {
  try {
    issueReports.value = (await request({ url: '/api/issues', method: 'get' })) || []
  } catch (e) {
    console.error(e)
  }
}

const openProcess = (row) => {
  processRow.value = row
  processForm.value = {
    status: row.status || 'OPEN',
    priority: row.priority || 'LOW',
    adminResponse: row.adminResponse || ''
  }
  processVisible.value = true
}

const submitProcess = async () => {
  if (!processRow.value) return
  processLoading.value = true
  try {
    await processFeedback(processRow.value.id, { ...processForm.value })
    ElMessage.success('已保存')
    processVisible.value = false
    await loadFeedback()
  } catch (e) {
    console.error(e)
  } finally {
    processLoading.value = false
  }
}

watch(tab, (v) => {
  if (v === 'reports') renderCharts()
  if (v === 'feedback') loadFeedback()
  if (v === 'issues') loadIssues()
})

onMounted(async () => {
  await loadBase()
  await renderCharts()
  window.addEventListener('resize', () => {
    chartW?.resize()
    chartD?.resize()
  })
})
</script>

<style scoped>
.admin-dash {
  min-height: 100vh;
  background: var(--cg-mist);
  padding: 16px 20px 40px;
}
.top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto 16px;
}
.left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.logo {
  border-radius: 10px;
}
h1 {
  margin: 0;
  font-size: 1.25rem;
  color: var(--cg-navy);
}
.tabs {
  max-width: 1200px;
  margin: 0 auto;
}
.chart {
  height: 320px;
  width: 100%;
  margin-bottom: 16px;
}
.hint {
  font-size: 13px;
  color: #6b7280;
  margin: 0 0 12px;
}
.form-narrow {
  max-width: 480px;
}
.section-title {
  margin: 24px 0 12px;
  font-size: 1rem;
  color: var(--cg-navy);
}
.mb {
  margin-bottom: 12px;
}
</style>
