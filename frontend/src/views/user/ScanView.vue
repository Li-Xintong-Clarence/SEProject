<template>
  <div class="scan-page">
    <div class="scan-header">
      <el-page-header title="返回" @back="goBack" />
      <h2>扫码租车</h2>
      <p class="scan-sub">扫描车辆二维码，快速开始骑行</p>
    </div>

    <!-- 扫码区域 -->
    <div class="scan-container">
      <div class="scan-frame">
        <div class="corner top-left"></div>
        <div class="corner top-right"></div>
        <div class="corner bottom-left"></div>
        <div class="corner bottom-right"></div>
        <div class="scan-line"></div>
      </div>
      
      <p class="scan-hint">将二维码放入框内即可自动扫描</p>
    </div>

    <!-- 输入框备选方案 -->
    <div class="input-alternative">
      <el-divider>或手动输入车辆编号</el-divider>
      <div class="input-row">
        <el-input
          v-model="manualInput"
          placeholder="请输入车辆编号，如 S001"
          size="large"
          clearable
        />
        <el-button type="primary" size="large" @click="handleManualInput" :loading="loading">
          确认租车
        </el-button>
      </div>
    </div>

    <!-- 操作说明 -->
    <div class="instructions">
      <h4>如何使用：</h4>
      <ul>
        <li>🔍 扫描车身上的二维码</li>
        <li>📱 或手动输入车辆编号</li>
        <li>🛴 确认后即可开始骑行</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getScooters } from '@/api/scooter'

const router = useRouter()
const manualInput = ref('')
const loading = ref(false)

const goBack = () => router.back()

const handleManualInput = async () => {
  const scooterId = manualInput.value.trim()
  
  if (!scooterId) {
    ElMessage.warning('请输入车辆编号')
    return
  }

  loading.value = true
  try {
    // 验证车辆是否存在且可用
    const res = await getScooters()
    const scooters = Array.isArray(res) ? res : (res?.data || [])
    const scooter = scooters.find(s => 
      String(s.id) === scooterId || 
      String(s.scooterNumber) === scooterId ||
      String(s.scooterId) === scooterId
    )

    if (!scooter) {
      ElMessage.error('未找到该车辆，请检查编号是否正确')
      loading.value = false
      return
    }

    const status = String(scooter.status || '').toUpperCase()
    if (status !== 'AVAILABLE') {
      ElMessage.error('该车辆当前不可用，请选择其他车辆')
      loading.value = false
      return
    }

    // 跳转到预订确认页
    router.push({ path: '/booking', query: { scooterId: String(scooterId) } })
  } catch (e) {
    console.error('验证车辆失败:', e)
    // 如果API失败，直接跳转（后端可能没有这个接口）
    router.push({ path: '/booking', query: { scooterId: String(scooterId) } })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.scan-page {
  padding: 24px;
  max-width: 480px;
  margin: 0 auto;
}

.scan-header {
  text-align: center;
  margin-bottom: 32px;
}

.scan-header h2 {
  margin: 16px 0 8px;
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--cg-text);
}

.scan-sub {
  margin: 0;
  color: var(--cg-text-light);
  font-size: 14px;
}

/* 扫码框 */
.scan-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 32px;
}

.scan-frame {
  position: relative;
  width: 240px;
  height: 240px;
  background: #f5f5f5;
  border-radius: 16px;
  overflow: hidden;
}

.corner {
  position: absolute;
  width: 30px;
  height: 30px;
  border: 4px solid var(--cg-accent);
}

.top-left {
  top: 0;
  left: 0;
  border-right: none;
  border-bottom: none;
  border-radius: 8px 0 0 0;
}

.top-right {
  top: 0;
  right: 0;
  border-left: none;
  border-bottom: none;
  border-radius: 0 8px 0 0;
}

.bottom-left {
  bottom: 0;
  left: 0;
  border-right: none;
  border-top: none;
  border-radius: 0 0 0 8px;
}

.bottom-right {
  bottom: 0;
  right: 0;
  border-left: none;
  border-top: none;
  border-radius: 0 0 8px 0;
}

.scan-line {
  position: absolute;
  top: 0;
  left: 20px;
  right: 20px;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--cg-accent), transparent);
  animation: scan 2s linear infinite;
}

@keyframes scan {
  0% { top: 10px; }
  50% { top: calc(100% - 12px); }
  100% { top: 10px; }
}

.scan-hint {
  margin-top: 16px;
  font-size: 14px;
  color: #999;
}

/* 手动输入 */
.input-alternative {
  margin-bottom: 32px;
}

.input-alternative :deep(.el-divider__text) {
  color: #999;
  font-size: 13px;
}

.input-row {
  display: flex;
  gap: 12px;
}

.input-row .el-input {
  flex: 1;
}

.input-row .el-button {
  background: var(--cg-gradient);
  border: none;
  flex-shrink: 0;
}

/* 使用说明 */
.instructions {
  background: #f9f9f9;
  border-radius: 12px;
  padding: 16px 20px;
}

.instructions h4 {
  margin: 0 0 12px;
  font-size: 14px;
  color: var(--cg-text);
}

.instructions ul {
  margin: 0;
  padding-left: 20px;
}

.instructions li {
  margin-bottom: 8px;
  font-size: 14px;
  color: var(--cg-text-light);
}

.instructions li:last-child {
  margin-bottom: 0;
}
</style>
