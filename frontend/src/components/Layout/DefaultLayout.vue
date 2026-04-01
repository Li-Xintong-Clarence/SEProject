<template>
  <el-container class="layout-container">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo" @click="goHome">
          <h1>电动滑板车租赁系统</h1>
        </div>

        <el-menu
          :default-active="activeIndex"
          mode="horizontal"
          class="menu"
          @select="handleSelect"
        >
          <el-menu-item index="scooters">找车</el-menu-item>
          <el-menu-item index="profile">个人中心</el-menu-item>
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

    <!-- 主要内容区 -->
    <el-main class="main-content">
      <router-view />
    </el-main>

    <el-footer class="footer">
      © 2026 电动滑板车租赁系统 | Sprint 1
    </el-footer>
  </el-container>
</template>

<script setup>
import { ref, onMounted, watchEffect } from 'vue'        // ← 确保 watchEffect 已导入
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'

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
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #409eff;
  color: white;
  padding: 0;
}

.header-content {
  display: flex;
  align-items: center;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo {
  cursor: pointer;
  margin-right: 40px;
}

.logo h1 {
  margin: 0;
  font-size: 22px;
  color: white;
  font-weight: bold;
}

.menu {
  flex: 1;
  background-color: transparent;
  border-bottom: none;
}

.menu :deep(.el-menu-item) {
  color: white !important;
}

.menu :deep(.el-menu-item.is-active) {
  color: white !important;
  border-bottom-color: white !important;
}

.user-info {
  color: white;
  font-size: 16px;
  cursor: pointer;
}

.main-content {
  background-color: #f5f7fa;
  padding: 24px;
  flex: 1;
  overflow: auto;
}

.footer {
  text-align: center;
  color: #999;
  font-size: 14px;
  background-color: #fff;
  border-top: 1px solid #eee;
  line-height: 60px;
}
</style>