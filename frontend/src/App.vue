<template>
  <DefaultLayout v-if="isLoggedIn" />
  <router-view v-else />
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import DefaultLayout from '@/components/Layout/DefaultLayout.vue'

const isLoggedIn = ref(false)

// 检查登录状态
const checkLogin = () => {
  const token = localStorage.getItem('token')
  const userStr = localStorage.getItem('user')
  isLoggedIn.value = !!(token && userStr)
}

// 初始化时检查
onMounted(() => {
  checkLogin()

  // 监听 storage 变化事件（其他标签页修改 localStorage 时触发）
  window.addEventListener('storage', checkLogin)

  // 监听自定义事件（当前窗口/标签内的变化）
  window.addEventListener('login-state-change', checkLogin)
})

onUnmounted(() => {
  window.removeEventListener('storage', checkLogin)
  window.removeEventListener('login-state-change', checkLogin)
})

// 暴露检查方法给子组件使用
defineExpose({ checkLogin })
</script>
