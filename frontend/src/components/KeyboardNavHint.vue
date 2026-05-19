<template>
  <Transition name="fade">
    <div v-if="visible" class="keyboard-nav-hint" role="tooltip" aria-label="键盘导航提示">
      <div class="hint-title">
        <span>键盘快捷键</span>
      </div>
      <div class="hint-keys">
        <div class="key-group">
          <span class="key">?</span>
          <span class="label">显示帮助</span>
        </div>
        <div class="key-group">
          <span class="key">↑↓←→</span>
          <span class="label">移动焦点</span>
        </div>
        <div class="key-group">
          <span class="key">Home</span>
          <span class="label">首个</span>
        </div>
        <div class="key-group">
          <span class="key">End</span>
          <span class="label">末尾</span>
        </div>
        <div class="key-group">
          <span class="key">Esc</span>
          <span class="label">关闭</span>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const visible = ref(false)
let hideTimer = null

const showHint = () => {
  visible.value = true
  if (hideTimer) clearTimeout(hideTimer)
  hideTimer = setTimeout(() => {
    visible.value = false
  }, 5000)
}

const hideHint = () => {
  visible.value = false
  if (hideTimer) clearTimeout(hideTimer)
}

const handleKeyDown = (e) => {
  if (e.key === '?' || (e.shiftKey && e.key === '/')) {
    showHint()
  } else if (e.key === 'Escape') {
    hideHint()
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleKeyDown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeyDown)
  if (hideTimer) clearTimeout(hideTimer)
})
</script>

<style scoped>
.keyboard-nav-hint {
  position: fixed;
  bottom: 16px;
  right: 16px;
  background: rgba(30, 41, 59, 0.95);
  color: white;
  padding: 10px 14px;
  border-radius: 8px;
  z-index: 9999;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  gap: 14px;
  font-size: 12px;
  backdrop-filter: blur(8px);
}

.hint-title {
  font-weight: 500;
  padding-right: 12px;
  border-right: 1px solid rgba(255, 255, 255, 0.2);
  color: #94a3b8;
}

.hint-keys {
  display: flex;
  gap: 12px;
}

.key-group {
  display: flex;
  align-items: center;
  gap: 4px;
}

.key {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 24px;
  height: 20px;
  padding: 0 6px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 4px;
  font-weight: 500;
  font-size: 10px;
  font-family: monospace;
}

.label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.6);
  margin-left: 2px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(8px);
}

@media (max-width: 768px) {
  .keyboard-nav-hint {
    bottom: 70px;
    right: 10px;
    left: 10px;
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }

  .hint-title {
    border-right: none;
    border-bottom: 1px solid rgba(255, 255, 255, 0.2);
    padding-right: 0;
    padding-bottom: 6px;
  }

  .hint-keys {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>
