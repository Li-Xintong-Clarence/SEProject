<template>
  <!-- 管理员页面不使用 DefaultLayout -->
  <template v-if="isAdminRoute">
    <router-view />
  </template>
  <!-- 普通用户页面 -->
  <template v-else-if="isLoggedIn">
    <DefaultLayout />
  </template>
  <!-- 未登录 -->
  <template v-else>
    <router-view />
  </template>
</template>

<script setup>
import { ref, watch, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import DefaultLayout from '@/components/Layout/DefaultLayout.vue'

const route = useRoute()
const isLoggedIn = ref(false)
let checkInterval = null

// 检查是否为管理员路由
const isAdminRoute = computed(() => {
  return route.path.startsWith('/admin')
})

// 检查登录状态
const checkLogin = () => {
  const token = localStorage.getItem('token')
  const userStr = localStorage.getItem('user')
  isLoggedIn.value = !!(token && userStr)
}

// 监听路由变化，检查登录状态
watch(() => route.path, () => {
  checkLogin()
})

// 初始化时检查
onMounted(() => {
  checkLogin()

  // 监听 storage 变化事件
  window.addEventListener('storage', checkLogin)

  // 监听自定义事件（登录/退出登录）
  window.addEventListener('login-state-change', checkLogin)

  // 定期检查登录状态（保险措施）
  checkInterval = setInterval(checkLogin, 300)
})

onUnmounted(() => {
  window.removeEventListener('storage', checkLogin)
  window.removeEventListener('login-state-change', checkLogin)
  if (checkInterval) {
    clearInterval(checkInterval)
  }
})

// 暴露检查方法给子组件使用
defineExpose({ checkLogin })
</script>
