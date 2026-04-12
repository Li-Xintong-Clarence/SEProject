<template>
  <div class="layout-container">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-content">
        <div class="logo" @click="goHome">
          <img src="/brand-logo.svg" alt="CapyGlide" width="36" height="36" />
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
import { Location, User, ArrowDown, SwitchButton } from '@element-plus/icons-vue'

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
  if (index === 'profile') router.push('/profile')
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

// 路由变化时自动更新菜单高亮
watchEffect(() => {
  const path = route.path
  if (path.includes('scooters') || path.includes('booking')) {
    activeIndex.value = 'scooters'
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
  background: var(--cg-mist);
}

/* 顶部导航 */
.header {
  background: var(--cg-white);
  border-bottom: 1px solid rgba(30, 58, 95, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.header-content {
  max-width: 1280px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  gap: 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  flex-shrink: 0;
}

.logo img {
  border-radius: var(--cg-radius-md);
}

.brand-name {
  font-size: 1.2rem;
  font-weight: 800;
  color: var(--cg-navy);
  letter-spacing: 0.02em;
}

/* 导航菜单 */
.nav-menu {
  flex: 1;
  border-bottom: none;
  background: transparent;
}

.nav-menu :deep(.el-menu-item) {
  color: var(--cg-charcoal);
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}

.nav-menu :deep(.el-menu-item:hover),
.nav-menu :deep(.el-menu-item.is-active) {
  color: var(--cg-navy);
  background: transparent;
  border-bottom: 2px solid var(--cg-navy);
}

.nav-menu :deep(.el-menu-item .el-icon) {
  margin-right: 2px;
}

/* 用户区域 */
.user-section {
  flex-shrink: 0;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: var(--cg-radius-md);
  transition: background 0.2s;
}

.user-trigger:hover {
  background: var(--cg-mist);
}

.user-avatar {
  background: var(--cg-navy);
  color: white;
  font-weight: 600;
}

.user-name {
  font-weight: 500;
  color: var(--cg-charcoal);
}

.arrow {
  color: #9ca3af;
}

/* 主内容 */
.main-content {
  flex: 1;
  padding: 0;
}

/* 页脚 */
.footer {
  background: var(--cg-navy);
  color: rgba(255, 255, 255, 0.8);
  text-align: center;
  padding: 16px 20px;
  font-size: 13px;
}

.footer span {
  opacity: 0.8;
}
</style>
