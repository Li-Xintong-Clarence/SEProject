/**
 * 键盘导航 Composable
 * 支持上下左右键在可聚焦元素间移动焦点
 * Tab 键保留原有功能
 */
import { ref, onMounted, onUnmounted } from 'vue'

// 全局键盘导航状态
const isKeyboardNavigationEnabled = ref(true)
const currentFocusIndex = ref(-1)
const focusableElements = ref([])

// 获取所有可聚焦元素的选择器
const FOCUSABLE_SELECTORS = [
  'a[href]',
  'button:not([disabled])',
  'input:not([disabled])',
  'select:not([disabled])',
  'textarea:not([disabled])',
  '[tabindex]:not([tabindex="-1"])',
  '.el-button:not([disabled])',
  '.el-menu-item:not([disabled])',
  '.el-tab-item',
  '.el-menu-item',
  '[role="button"]',
  '[role="menuitem"]'
].join(', ')

/**
 * 初始化键盘导航
 * @param {Object} options 配置选项
 * @param {string} options.selector - 可聚焦元素的选择器，默认为 FOCUSABLE_SELECTORS
 * @param {string} options.containerSelector - 容器选择器，默认为 'main, [role="main"], .main-content, #main-content, .content'
 * @param {Function} options.onNavigate - 导航回调函数
 */
export function useKeyboardNavigation(options = {}) {
  const containerSelector = options.containerSelector || 'main, [role="main"], .main-content, #main-content, .content'
  const customSelector = options.selector || FOCUSABLE_SELECTORS

  // 获取当前容器中的所有可聚焦元素
  const getFocusableElements = () => {
    const containers = document.querySelectorAll(containerSelector)
    const elements = []

    containers.forEach(container => {
      const focusable = container.querySelectorAll(customSelector)
      focusable.forEach(el => {
        // 排除隐藏的元素
        if (el.offsetParent !== null && getComputedStyle(el).display !== 'none') {
          elements.push(el)
        }
      })
    })

    return elements
  }

  // 更新可聚焦元素列表
  const updateFocusableElements = () => {
    focusableElements.value = getFocusableElements()
  }

  // 获取元素在页面中的网格位置
  const getElementGridPosition = (element) => {
    const rect = element.getBoundingClientRect()
    return {
      top: rect.top,
      bottom: rect.bottom,
      left: rect.left,
      right: rect.right,
      centerX: rect.left + rect.width / 2,
      centerY: rect.top + rect.height / 2,
      row: Math.round(rect.top / 50), // 按 50px 一行估算
      col: Math.round(rect.left / 200) // 按 200px 一列估算
    }
  }

  // 找到最接近的目标元素
  const findClosestElement = (currentEl, direction) => {
    if (!currentEl) return null

    const currentPos = getElementGridPosition(currentEl)
    const elements = focusableElements.value

    let bestCandidate = null
    let bestScore = Infinity

    elements.forEach(el => {
      if (el === currentEl) return

      const pos = getElementGridPosition(el)
      let dx = pos.centerX - currentPos.centerX
      let dy = pos.centerY - currentPos.centerY
      let isValid = false
      let score = Infinity

      switch (direction) {
        case 'up':
          if (dy < -10) { // 目标在上方
            isValid = true
            score = Math.abs(dx) + Math.abs(dy) * 2 // 优先上下移动
          }
          break
        case 'down':
          if (dy > 10) { // 目标在下方
            isValid = true
            score = Math.abs(dx) + Math.abs(dy) * 2
          }
          break
        case 'left':
          if (dx < -10) { // 目标在左方
            isValid = true
            score = Math.abs(dy) + Math.abs(dx) * 2 // 优先左右移动
          }
          break
        case 'right':
          if (dx > 10) { // 目标在右方
            isValid = true
            score = Math.abs(dy) + Math.abs(dx) * 2
          }
          break
      }

      if (isValid && score < bestScore) {
        bestScore = score
        bestCandidate = el
      }
    })

    return bestCandidate
  }

  // 键盘事件处理
  const handleKeyDown = (event) => {
    // 如果在输入框中，不拦截方向键
    const activeTag = document.activeElement?.tagName?.toLowerCase()
    if (['input', 'textarea', 'select'].includes(activeTag)) {
      // 但允许 Escape 键
      if (event.key !== 'Escape') {
        return
      }
    }

    // 启用/禁用键盘导航的快捷键 (Alt + N)
    if (event.altKey && event.key.toLowerCase() === 'n') {
      event.preventDefault()
      isKeyboardNavigationEnabled.value = !isKeyboardNavigationEnabled.value
      return
    }

    if (!isKeyboardNavigationEnabled.value) return

    let targetElement = null
    const currentElement = document.activeElement

    switch (event.key) {
      case 'ArrowUp':
        event.preventDefault()
        targetElement = findClosestElement(currentElement, 'up')
        break
      case 'ArrowDown':
        event.preventDefault()
        targetElement = findClosestElement(currentElement, 'down')
        break
      case 'ArrowLeft':
        event.preventDefault()
        targetElement = findClosestElement(currentElement, 'left')
        break
      case 'ArrowRight':
        event.preventDefault()
        targetElement = findClosestElement(currentElement, 'right')
        break
      case 'Home':
        event.preventDefault()
        updateFocusableElements()
        if (focusableElements.value.length > 0) {
          targetElement = focusableElements.value[0]
        }
        break
      case 'End':
        event.preventDefault()
        updateFocusableElements()
        if (focusableElements.value.length > 0) {
          targetElement = focusableElements.value[focusableElements.value.length - 1]
        }
        break
    }

    if (targetElement) {
      targetElement.focus()
      // 如果是链接，触发点击
      if (targetElement.tagName.toLowerCase() === 'a') {
        // 不自动点击，让用户自己决定
      }
    }
  }

  // 监听路由变化，更新可聚焦元素
  const handleRouteChange = () => {
    setTimeout(updateFocusableElements, 100)
  }

  // 监听 DOM 变化
  let observer = null
  const setupObserver = () => {
    observer = new MutationObserver(() => {
      updateFocusableElements()
    })
    observer.observe(document.body, {
      childList: true,
      subtree: true
    })
  }

  // 生命周期
  onMounted(() => {
    document.addEventListener('keydown', handleKeyDown)
    window.addEventListener('popstate', handleRouteChange)

    // 初始化可聚焦元素
    updateFocusableElements()

    // 设置 DOM 观察器
    setupObserver()

    // 监听 Vue 路由变化
    if (window.__vueRouter) {
      window.__vueRouter.afterEach(handleRouteChange)
    }
  })

  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeyDown)
    window.removeEventListener('popstate', handleRouteChange)
    if (observer) {
      observer.disconnect()
    }
  })

  return {
    isKeyboardNavigationEnabled,
    currentFocusIndex,
    focusableElements,
    updateFocusableElements,
    getFocusableElements
  }
}

/**
 * 添加全局键盘导航样式
 * 在 App.vue 中调用此函数来注入样式
 */
export function injectKeyboardNavStyles() {
  const styleId = 'keyboard-nav-styles'
  if (document.getElementById(styleId)) return

  const style = document.createElement('style')
  style.id = styleId
  style.textContent = `
    /* 键盘导航焦点样式 */
    *:focus-visible {
      outline: 3px solid rgba(30, 58, 95, 0.5) !important;
      outline-offset: 2px !important;
    }

    /* 高亮当前焦点的元素 */
    .keyboard-nav-active {
      outline: 3px solid #1e3a5f !important;
      outline-offset: 2px !important;
      box-shadow: 0 0 0 4px rgba(30, 58, 95, 0.2) !important;
    }

    /* 导航提示 */
    .keyboard-nav-hint {
      position: fixed;
      bottom: 20px;
      left: 20px;
      background: rgba(30, 58, 95, 0.95);
      color: white;
      padding: 12px 16px;
      border-radius: 10px;
      font-size: 13px;
      z-index: 10000;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
      display: flex;
      gap: 12px;
      align-items: center;
    }

    .keyboard-nav-hint .key {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      min-width: 24px;
      height: 24px;
      padding: 0 6px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 4px;
      font-weight: 600;
      font-size: 12px;
    }

    .keyboard-nav-hint .label {
      color: rgba(255, 255, 255, 0.9);
    }

    /* Element Plus 组件焦点优化 */
    .el-button:focus-visible,
    .el-button:active:focus-visible {
      outline: 3px solid rgba(30, 58, 95, 0.5) !important;
      outline-offset: 2px !important;
    }

    .el-menu-item:focus-visible,
    .el-menu-item.is-active:focus-visible {
      outline: 3px solid rgba(30, 58, 95, 0.5) !important;
      outline-offset: 2px !important;
    }

    /* 卡片和表格项的焦点样式 */
    .el-card:focus-visible,
    .el-table__row:focus-visible,
    .el-table__row:hover:focus-visible {
      outline: 3px solid rgba(30, 58, 95, 0.5) !important;
      outline-offset: 2px !important;
    }
  `
  document.head.appendChild(style)
}

export default {
  useKeyboardNavigation,
  injectKeyboardNavStyles
}
