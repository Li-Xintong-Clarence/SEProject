<template>
  <div class="profile">
    <el-page-header title="返回" @back="goBack" />
    <h2>个人中心</h2>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="我的预订" name="bookings">
        <el-table :data="bookings" style="width: 100%">
          <el-table-column prop="scooterName" label="滑板车" width="120" />
          <el-table-column prop="startTime" label="开始时间" width="180" />
          <el-table-column prop="endTime" label="结束时间" width="180" />
          <el-table-column prop="totalPrice" label="价格" width="80" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'active' ? 'success' : 'info'">
                {{ row.status === 'active' ? '进行中' : '已完成/已取消' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="{ row }">
              <el-button 
                type="danger" 
                size="small" 
                :disabled="row.status !== 'active'"
                @click="handleCancel(row.id)"
              >
                取消
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="故障反馈" name="issues">
        <el-button type="primary" @click="showIssueDialog = true">提交反馈</el-button>
        <el-table :data="issues" style="margin-top: 20px">
          <el-table-column prop="description" label="问题描述" />
          <el-table-column prop="status" label="状态" />
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 提交故障对话框 -->
    <el-dialog v-model="showIssueDialog" title="提交故障反馈" width="500px">
      <el-form :model="issueForm">
        <el-form-item label="问题描述">
          <el-input type="textarea" v-model="issueForm.description" rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showIssueDialog = false">取消</el-button>
        <el-button type="primary" @click="submitIssue">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserBookings, cancelBooking } from '@/api/booking'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const activeTab = ref('bookings')
const bookings = ref([])
const issues = ref([]) // mock 数据，稍后可以扩展
const showIssueDialog = ref(false)
const issueForm = ref({ description: '' })

const goBack = () => {
  router.back()
}

const handleCancel = (id) => {
  ElMessageBox.confirm('确定要取消该预订吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cancelBooking(id)
      // 刷新列表
      const res = await getUserBookings()
      bookings.value = res.data
      ElMessage.success('已取消')
    } catch (error) {
      ElMessage.error('取消失败')
    }
  }).catch(() => {})
}

const submitIssue = () => {
  if (!issueForm.value.description) {
    ElMessage.warning('请输入问题描述')
    return
  }
  // mock 提交
  issues.value.push({
    id: Date.now(),
    description: issueForm.value.description,
    status: '待处理'
  })
  ElMessage.success('反馈已提交')
  showIssueDialog.value = false
  issueForm.value.description = ''
}

onMounted(async () => {
  try {
    const res = await getUserBookings()
    bookings.value = res.data
  } catch (error) {
    ElMessage.error('获取预订记录失败')
  }
  // mock 故障数据
  issues.value = [
    { id: 1, description: '滑板车刹车失灵', status: '已修复' },
    { id: 2, description: '无法解锁', status: '处理中' },
  ]
})
</script>

<style scoped>
.profile {
  padding: 20px;
}
</style>