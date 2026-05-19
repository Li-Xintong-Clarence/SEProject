<template>
  <div class="mobile-keyboard-nav" :class="{ visible: isVisible }">
    <!-- 顶部区域 -->
    <div class="nav-row">
      <div class="nav-placeholder"></div>
      <button class="nav-btn up" @click="navigate('up')" aria-label="向上移动">
        <el-icon><ArrowUp /></el-icon>
      </button>
      <div class="nav-placeholder"></div>
    </div>

    <!-- 中间区域 -->
    <div class="nav-row">
      <button class="nav-btn left" @click="navigate('left')" aria-label="向左移动">
        <el-icon><ArrowLeft /></el-icon>
      </button>
      <div class="nav-indicator">
        <div class="indicator-dot" :class="{ active: true }"></div>
      </div>
      <button class="nav-btn right" @click="navigate('right')" aria-label="向右移动">
        <el-icon><ArrowRight /></el-icon>
      </button>
    </div>

    <!-- 底部区域 -->
    <div class="nav-row">
      <div class="nav-placeholder"></div>
      <button class="nav-btn down" @click="navigate('down')" aria-label="向下移动">
        <el-icon><ArrowDown /></el-icon>
      </button>
      <div class="nav-placeholder"></div>
    </div>

    <!-- 显示/隐藏按钮 -->
    <button class="toggle-btn" @click="toggle" :aria-label="isVisible ? '隐藏导航按钮' : '显示导航按钮'">
      <el-icon>
        <Close v-if="isVisible" />
        <DArrowLeft v-else />
      </el-icon>
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ArrowUp, ArrowDown, ArrowLeft, ArrowRight, Close, DArrowLeft } from '@element-plus/icons-vue'

const props = defineProps({
  // 是否默认显示
  defaultVisible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['navigate'])

const isVisible = ref(props.defaultVisible)

// 切换显示状态
const toggle = () => {
  isVisible.value = !isVisible.value
}

// 导航到指定方向
const navigate = (direction) => {
  emit('navigate', direction)

  // 触发键盘事件，让 useKeyboardNavigation 处理
  const event = new KeyboardEvent('keydown', {
    key: 'Arrow' + direction.charAt(0).toUpperCase() + direction.slice(1),
    bubbles: true
  })
  document.dispatchEvent(event)
}

// 监听键盘快捷键 (Alt + N 切换显示)
const handleKeyDown = (e) => {
  if (e.altKey && e.key.toLowerCase() === 'n') {
    isVisible.value = !isVisible.value
  }
}

// 监听键盘事件来隐藏/显示
const handleFocus = (e) => {
  // 当用户使用 Tab 键时，隐藏移动端导航
  if (e.key === 'Tab') {
    // 可以在此处隐藏导航
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleKeyDown)
  document.addEventListener('keydown', handleFocus)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeyDown)
  document.removeEventListener('keydown', handleFocus)
})
</script>

<style scoped>
.mobile-keyboard-nav {
  position: fixed;
  bottom: 100px;
  right: 20px;
  z-index: 9999;
  display: none;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(30, 58, 95, 0.2);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(30, 58, 95, 0.1);
  transition: opacity 0.3s, transform 0.3s;

  /* 默认隐藏 */
  opacity: 0;
  transform: scale(0.8);
  pointer-events: none;
}

/* 移动端显示 */
@media (max-width: 768px) {
  .mobile-keyboard-nav {
    display: flex;
  }
}

.mobile-keyboard-nav.visible {
  opacity: 1;
  transform: scale(1);
  pointer-events: auto;
}

.nav-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.nav-btn {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  border: 2px solid #1e3a5f;
  background: white;
  color: #1e3a5f;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 20px;
}

.nav-btn:hover {
  background: #1e3a5f;
  color: white;
  transform: scale(1.05);
}

.nav-btn:active {
  transform: scale(0.95);
}

.nav-btn.up {
  order: 2;
}

.nav-btn.left {
  order: 1;
}

.nav-btn.right {
  order: 3;
}

.nav-btn.down {
  order: 4;
}

.nav-placeholder {
  width: 48px;
  height: 48px;
  order: 2;
}

.nav-indicator {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.indicator-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #1e3a5f;
  opacity: 0.3;
}

.indicator-dot.active {
  opacity: 1;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.7;
  }
}

.toggle-btn {
  position: absolute;
  top: -16px;
  right: -16px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 2px solid #1e3a5f;
  background: white;
  color: #1e3a5f;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 4px 12px rgba(30, 58, 95, 0.2);
}

.toggle-btn:hover {
  background: #1e3a5f;
  color: white;
}

/* 平板横屏 */
@media (min-width: 601px) and (max-width: 768px) {
  .mobile-keyboard-nav {
    bottom: 120px;
    right: 24px;
  }
}
</style>
