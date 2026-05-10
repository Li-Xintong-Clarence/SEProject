<template>
  <div class="layout-container">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-content">
        <div class="logo" @click="goHome">
          <img src="/brand-logo.png" alt="CapyGlide" width="36" height="36" />
          <span class="brand-name">CapyGlide</span>
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
              <el-avatar :size="32" class="user-avatar">
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
      <span>© 2026 CapyGlide 卡皮滑行 · 电动滑板车租赁系统</span>
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

// 加载用户名
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

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    ElMessage.success('已成功退出登录')
    // 触发登录状态变更事件
    window.dispatchEvent(new Event('login-state-change'))
    router.push('/login')
  }
}

// 路由变化时自动更新菜单高亮
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
  background: var(--cg-bg);
}

/* 顶部导航 - 现代毛玻璃风格 */
.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--cg-border);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.header-content {
  max-width: 1280px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 64px;
  padding: 0 24px;
  gap: 32px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  flex-shrink: 0;
  transition: var(--cg-transition);
}

.logo:hover {
  opacity: 0.85;
}

.logo img {
  border-radius: var(--cg-radius-md);
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.brand-name {
  font-size: 1.25rem;
  font-weight: 800;
  color: var(--cg-navy);
  letter-spacing: -0.02em;
}

/* 导航菜单 - 现代简约风格 */
.nav-menu {
  flex: 1;
  border-bottom: none;
  background: transparent;
}

.nav-menu :deep(.el-menu-item) {
  color: var(--cg-text-light);
  font-weight: 600;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 16px;
  height: 48px;
  border-radius: var(--cg-radius-sm);
  margin: 0 4px;
  transition: var(--cg-transition);
}

.nav-menu :deep(.el-menu-item:hover),
.nav-menu :deep(.el-menu-item.is-active) {
  color: var(--cg-navy);
  background: var(--cg-accent-soft);
  border-bottom: none;
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
  padding: 6px 12px;
  border-radius: var(--cg-radius-full);
  transition: var(--cg-transition);
  background: var(--cg-bg-soft);
  border: 1px solid var(--cg-border);
}

.user-trigger:hover {
  background: var(--cg-white);
  border-color: var(--cg-navy);
  box-shadow: var(--cg-shadow);
}

.user-avatar {
  background: var(--cg-gradient-navy);
  color: white;
  font-weight: 700;
  font-size: 14px;
}

.user-name {
  font-weight: 600;
  color: var(--cg-text);
  font-size: 14px;
}

.arrow {
  color: var(--cg-text-muted);
  font-size: 12px;
}

/* 主内容 */
.main-content {
  flex: 1;
  padding: 0;
}

/* 页脚 - 品牌色风格 */
.footer {
  background: var(--cg-navy);
  color: rgba(255, 255, 255, 0.7);
  text-align: center;
  padding: 24px 20px;
  font-size: 14px;
  margin-top: auto;
}

.footer span {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
</style>
