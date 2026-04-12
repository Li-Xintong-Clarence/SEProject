<template>
  <el-container class="layout-container">
    <el-header class="header">
      <div class="header-content">
        <div class="brand" @click="goHome">
          <img class="brand-mark" src="/brand-logo.png" alt="" width="40" height="40" />
          <div class="brand-text">
            <span class="brand-en">CapyGlide</span>
            <span class="brand-zh">卡皮滑行</span>
          </div>
        </div>

        <el-menu
          :default-active="activeIndex"
          mode="horizontal"
          class="menu"
          background-color="transparent"
          text-color="rgba(255,255,255,0.88)"
          active-text-color="#ffffff"
          @select="handleSelect"
        >
          <el-menu-item index="scooter-map">地图找车</el-menu-item>
          <el-menu-item index="scooter-list">车辆列表</el-menu-item>
          <el-menu-item index="profile">个人中心</el-menu-item>
          <el-menu-item v-if="isAdmin" index="admin">管理后台</el-menu-item>
        </el-menu>

        <div class="user-info">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ username }} <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <el-main class="main-content">
      <router-view />
    </el-main>

    <el-footer class="footer">
      © 2026 CapyGlide 卡皮滑行 · 电动滑板车租赁
    </el-footer>
  </el-container>
</template>

<script setup>
import { ref, onMounted, watchEffect, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const activeIndex = ref('scooter-map')
const username = ref('用户')
const userRole = ref('')

const isAdmin = computed(() => userRole.value === 'ADMIN')

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      username.value = user.username || user.name || '用户'
      userRole.value = user.role || ''
    } catch (e) {
      console.error('解析用户信息失败')
    }
  }
})

const goHome = () => {
  router.push('/scooters')
}

const handleSelect = (index) => {
  if (index === 'scooter-map') router.push('/scooters')
  if (index === 'scooter-list') router.push('/scooters/list')
  if (index === 'profile') router.push('/profile')
  if (index === 'admin') router.push('/admin')
}

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    ElMessage.success('已成功退出登录')
    router.push('/login')
  }
}

watchEffect(() => {
  const path = route.path
  if (path === '/scooters/list') {
    activeIndex.value = 'scooter-list'
  } else if (path.startsWith('/scooters') || path.includes('booking')) {
    activeIndex.value = 'scooter-map'
  } else if (path.includes('profile')) {
    activeIndex.value = 'profile'
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  padding: 0;
  background: linear-gradient(115deg, var(--cg-navy-deep) 0%, var(--cg-navy) 42%, var(--cg-navy-soft) 100%);
  color: #fff;
  box-shadow: 0 8px 32px rgba(21, 42, 69, 0.25);
}

.header-content {
  display: flex;
  align-items: center;
  height: 100%;
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  margin-right: 36px;
}

.brand-mark {
  border-radius: 12px;
  object-fit: cover;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.2);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-en {
  font-size: 1.15rem;
  font-weight: 800;
  letter-spacing: 0.02em;
  color: #fff;
}

.brand-zh {
  font-size: 0.75rem;
  opacity: 0.82;
  color: var(--cg-sand-light);
}

.menu {
  flex: 1;
  border-bottom: none !important;
}

.menu :deep(.el-menu-item) {
  font-weight: 600;
  border-bottom: 2px solid transparent !important;
}

.menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.08) !important;
}

.menu :deep(.el-menu-item.is-active) {
  border-bottom-color: var(--cg-sand) !important;
  background: rgba(255, 255, 255, 0.06) !important;
}

.user-info {
  color: #fff;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
}

.el-dropdown-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.main-content {
  background: linear-gradient(180deg, var(--cg-mist) 0%, #ebe6dc 100%);
  padding: 24px;
  flex: 1;
  overflow: auto;
}

.footer {
  text-align: center;
  color: #7a8494;
  font-size: 13px;
  background: var(--cg-white);
  border-top: 1px solid rgba(30, 58, 95, 0.08);
  line-height: 52px;
}
</style>
