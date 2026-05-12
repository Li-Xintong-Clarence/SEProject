<template>
  <div class="layout-container">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-content">
        <div class="logo" @click="goHome">
          <div class="logo-icon">
            <svg viewBox="0 0 64 64" fill="none">
              <circle cx="14" cy="50" r="10" stroke="currentColor" stroke-width="2.5"/>
              <circle cx="50" cy="50" r="10" stroke="currentColor" stroke-width="2.5"/>
              <path d="M14 50L24 30H40L50 50" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
              <path d="M24 30L30 20H38" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
              <rect x="28" y="18" width="12" height="4" rx="1" fill="currentColor"/>
            </svg>
          </div>
          <div class="logo-text">
            <span class="brand-name">CapyGlide</span>
            <span class="brand-tag">卡皮滑行</span>
          </div>
        </div>

        <el-menu
          :default-active="activeIndex"
          mode="horizontal"
          class="nav-menu"
          @select="handleSelect"
        >
          <el-menu-item index="scooters">
            <el-icon><Location /></el-icon>
            找车
          </el-menu-item>
          <el-menu-item index="trip">
            <el-icon><Van /></el-icon>
            当前行程
          </el-menu-item>
          <el-menu-item index="profile">
            <el-icon><User /></el-icon>
            个人中心
          </el-menu-item>
        </el-menu>

        <div class="user-section">
          <el-dropdown @command="handleCommand">
            <span class="user-trigger">
              <el-avatar :size="36" class="user-avatar">
                {{ username?.charAt(0)?.toUpperCase() || 'U' }}
              </el-avatar>
              <span class="user-name">{{ username }}</span>
              <el-icon class="arrow"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主要内容 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-brand">
          <span class="footer-name">CapyGlide</span>
          <span class="footer-divider">·</span>
          <span>卡皮滑行 · 电动滑板车租赁系统</span>
        </div>
        <p class="footer-copy">© 2026 CapyGlide. 和卡皮巴拉一样从容出行</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, watchEffect } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Location, Van, User, ArrowDown, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const activeIndex = ref('scooters')
const username = ref('用户')

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      username.value = user.username || user.name || '用户'
    } catch (e) {
      console.error('解析用户信息失败')
    }
  }
})

const goHome = () => {
  router.push('/scooters')
}

const handleSelect = (index) => {
  if (index === 'scooters') router.push('/scooters')
  if (index === 'trip') router.push('/trip')
  if (index === 'profile') router.push('/profile')
}

const handleCommand = async (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('capyglide_discount_profile')
    localStorage.removeItem('activeTrip')
    ElMessage.success('已成功退出登录')
    window.location.href = '/login'
  }
}

watchEffect(() => {
  const path = route.path
  if (path.includes('scooters') || path.includes('booking')) {
    activeIndex.value = 'scooters'
  } else if (path.includes('trip')) {
    activeIndex.value = 'trip'
  } else if (path.includes('profile')) {
    activeIndex.value = 'profile'
  }
})
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #e8eef5 0%, #d6e0eb 100%);
}

/* 顶部导航 */
.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid #d6e0eb;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 12px rgba(30, 58, 95, 0.06);
}

.header-content {
  max-width: 1280px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 68px;
  padding: 0 28px;
  gap: 32px;
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  flex-shrink: 0;
  transition: all 0.2s;
}

.logo:hover {
  opacity: 0.85;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon svg {
  width: 24px;
  height: 24px;
  color: white;
}

.logo-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-name {
  font-size: 17px;
  font-weight: 800;
  color: #1e3a5f;
  letter-spacing: -0.02em;
}

.brand-tag {
  font-size: 11px;
  color: #5a7a9a;
  font-weight: 500;
}

/* 导航菜单 */
.nav-menu {
  flex: 1;
  border-bottom: none;
  background: transparent;
}

.nav-menu :deep(.el-menu-item) {
  color: #5a7a9a;
  font-weight: 600;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 18px;
  height: 48px;
  border-radius: 10px;
  margin: 0 4px;
  transition: all 0.2s;
}

.nav-menu :deep(.el-menu-item:hover),
.nav-menu :deep(.el-menu-item.is-active) {
  color: #1e3a5f;
  background: #f0f4f8;
}

.nav-menu :deep(.el-menu-item .el-icon) {
  margin-right: 4px;
  font-size: 18px;
}

/* 用户区域 */
.user-section {
  flex-shrink: 0;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 14px;
  border-radius: 24px;
  transition: all 0.2s;
  background: #f0f4f8;
  border: 1px solid #d6e0eb;
}

.user-trigger:hover {
  background: #1e3a5f;
  border-color: #1e3a5f;
}

.user-trigger:hover .user-name,
.user-trigger:hover .arrow {
  color: white;
}

.user-avatar {
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  color: white;
  font-weight: 700;
  font-size: 14px;
}

.user-name {
  font-weight: 600;
  color: #1e3a5f;
  font-size: 14px;
}

.arrow {
  color: #5a7a9a;
  font-size: 12px;
  transition: all 0.2s;
}

/* 主内容 */
.main-content {
  flex: 1;
  padding: 0;
}

/* 页脚 */
.footer {
  background: #1e3a5f;
  padding: 32px 20px;
  margin-top: auto;
}

.footer-content {
  max-width: 1280px;
  margin: 0 auto;
  text-align: center;
}

.footer-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 12px;
  color: white;
  font-size: 15px;
}

.footer-name {
  font-weight: 800;
}

.footer-divider {
  opacity: 0.5;
}

.footer-copy {
  margin: 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}
</style>
