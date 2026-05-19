<template>
  <!-- 跳过导航链接 - 提高可访问性 -->
  <a href="#main-content" class="skip-link">跳转到主要内容</a>

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

  <!-- 键盘导航提示组件 -->
  <KeyboardNavHint v-model="showKeyboardHint" />

  <!-- 移动端键盘导航按钮 -->
  <MobileKeyboardNav :default-visible="false" @navigate="handleNavigate" />
</template>

<script setup>
import { ref, watch, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import DefaultLayout from '@/components/Layout/DefaultLayout.vue'
import KeyboardNavHint from '@/components/KeyboardNavHint.vue'
import MobileKeyboardNav from '@/components/MobileKeyboardNav.vue'
import { useKeyboardNavigation, injectKeyboardNavStyles } from '@/composables/useKeyboardNavigation'

const route = useRoute()
const isLoggedIn = ref(false)
const showKeyboardHint = ref(false)
let checkInterval = null

// 初始化键盘导航
const keyboardNav = useKeyboardNavigation({
  containerSelector: 'main, [role="main"], .main-content, #main-content, .content, .admin-container, .booking, .page-container',
  selector: 'button:not([disabled]), a, input:not([disabled]), select:not([disabled]), [tabindex]:not([tabindex="-1"]), .el-button, .el-menu-item, .el-tab-item, .el-table__row'
})

// 处理移动端导航按钮点击
const handleNavigate = (direction) => {
  const event = new KeyboardEvent('keydown', {
    key: 'Arrow' + direction.charAt(0).toUpperCase() + direction.slice(1),
    bubbles: true
  })
  document.dispatchEvent(event)
}

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
  // 路由变化时更新可聚焦元素
  setTimeout(() => keyboardNav.updateFocusableElements(), 100)
})

// 初始化时检查
onMounted(() => {
  checkLogin()

  // 注入键盘导航样式
  injectKeyboardNavStyles()

  // 监听 storage 变化事件
  window.addEventListener('storage', checkLogin)

  // 监听自定义事件（登录/退出登录）
  window.addEventListener('login-state-change', checkLogin)

  // 定期检查登录状态（保险措施）
  checkInterval = setInterval(checkLogin, 300)

  // 监听 DOM 变化，更新可聚焦元素
  const mutationObserver = new MutationObserver(() => {
    setTimeout(() => keyboardNav.updateFocusableElements(), 100)
  })
  mutationObserver.observe(document.body, {
    childList: true,
    subtree: true
  })

  // 键盘快捷键：按 H 显示/隐藏提示
  document.addEventListener('keydown', (e) => {
    const activeTag = document.activeElement?.tagName?.toLowerCase()
    if (['input', 'textarea', 'select'].includes(activeTag)) return

    if (e.key === 'h' || e.key === 'H') {
      showKeyboardHint.value = !showKeyboardHint.value
    }
  })
})

onUnmounted(() => {
  window.removeEventListener('storage', checkLogin)
  window.removeEventListener('login-state-change', checkLogin)
  if (checkInterval) {
    clearInterval(checkInterval)
  }
})
</script>

<style>
/* 移动端键盘导航 - 默认隐藏，只在移动端显示 */
@media (min-width: 769px) {
  .mobile-keyboard-nav {
    display: none !important;
  }
}

/* 全局键盘导航样式 */
.keyboard-nav-active {
  outline: 3px solid rgba(30, 58, 95, 0.5) !important;
  outline-offset: 2px !important;
  box-shadow: 0 0 0 4px rgba(30, 58, 95, 0.2) !important;
}

/* Element Plus 组件焦点优化 */
.el-button:focus-visible {
  outline: 3px solid rgba(30, 58, 95, 0.5) !important;
  outline-offset: 2px !important;
}

.el-menu-item:focus-visible {
  outline: 3px solid rgba(30, 58, 95, 0.5) !important;
  outline-offset: 2px !important;
}

.el-table__row:focus-visible {
  outline: 3px solid rgba(30, 58, 95, 0.5) !important;
  outline-offset: 2px !important;
  background: rgba(30, 58, 95, 0.05) !important;
}

/* 表格行悬停和焦点效果 */
.el-table__row:hover {
  cursor: pointer;
}

/* 跳过链接样式 */
.skip-link {
  position: absolute;
  top: -40px;
  left: 0;
  background: #1e3a5f;
  color: white;
  padding: 8px 16px;
  z-index: 10000;
  transition: top 0.3s;
}

.skip-link:focus {
  top: 0;
}
</style>
