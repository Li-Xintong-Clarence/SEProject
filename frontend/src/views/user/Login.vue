<template>
  <main id="main-content" class="login-page" role="main">
    <!-- 左侧品牌区 -->
    <div class="login-hero" aria-hidden="true">
      <div class="hero-inner">
        <!-- 品牌文字 Logo -->
        <h1 class="hero-title">CapyGlide</h1>
        <p class="hero-tag">卡皮滑行 · 和卡皮巴拉一样从容</p>
        <p class="hero-desc">定位附近车辆，一键预订，随时出发。让出行成为享受。</p>

        <!-- 品牌特色 -->
        <div class="hero-features">
          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"/>
                <circle cx="12" cy="10" r="3"/>
              </svg>
            </div>
            <span>附近找车</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <rect x="3" y="4" width="18" height="18" rx="2"/>
                <path d="M16 2v4M8 2v4M3 10h18"/>
              </svg>
            </div>
            <span>随时租用</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
              </svg>
            </div>
            <span>安全出行</span>
          </div>
        </div>

        <!-- 装饰滑板车 -->
        <div class="hero-decor">
          <svg viewBox="0 0 200 80" class="scooter-decor">
            <circle cx="30" cy="65" r="12" stroke="rgba(255,255,255,0.3)" stroke-width="2" fill="none"/>
            <circle cx="170" cy="65" r="12" stroke="rgba(255,255,255,0.3)" stroke-width="2" fill="none"/>
            <path d="M30 65L50 35H140L170 65" stroke="rgba(255,255,255,0.3)" stroke-width="2" stroke-linecap="round" fill="none"/>
            <path d="M50 35L65 20H130" stroke="rgba(255,255,255,0.3)" stroke-width="2" stroke-linecap="round" fill="none"/>
            <rect x="60" y="17" width="15" height="6" rx="2" fill="rgba(255,255,255,0.3)"/>
          </svg>
        </div>

        <p class="hero-slogan">轻松租一辆，慢慢逛一城</p>
      </div>
    </div>

    <!-- 右侧登录面板 -->
    <section class="login-panel" aria-labelledby="login-title">
      <div class="panel-inner">
        <header class="panel-header">
          <h2 id="login-title" class="panel-title">欢迎回来</h2>
          <p class="panel-sub">登录后继续你的滑行之旅</p>
        </header>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="0"
          class="login-form"
          @submit.prevent="handleLogin"
          novalidate
        >
          <!-- 用户名输入 -->
          <el-form-item prop="username">
            <label for="username" class="sr-only">用户名</label>
            <el-input
              id="username"
              v-model="form.username"
              placeholder="用户名"
              size="large"
              class="custom-input"
              autocomplete="username"
              :aria-invalid="!!errors.username"
              :aria-describedby="errors.username ? 'username-error' : undefined"
              @keydown.enter="handleLogin"
            >
              <template #prefix>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="input-icon" aria-hidden="true">
                  <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
                </svg>
              </template>
            </el-input>
            <p v-if="errors.username" id="username-error" class="field-error" role="alert">
              {{ errors.username }}
            </p>
          </el-form-item>

          <!-- 密码输入 -->
          <el-form-item prop="password">
            <label for="password" class="sr-only">密码</label>
            <el-input
              id="password"
              v-model="form.password"
              type="password"
              placeholder="密码"
              size="large"
              show-password
              class="custom-input"
              autocomplete="current-password"
              :aria-invalid="!!errors.password"
              :aria-describedby="errors.password ? 'password-error' : undefined"
              @keydown.enter="handleLogin"
            >
              <template #prefix>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="input-icon" aria-hidden="true">
                  <rect x="3" y="11" width="18" height="11" rx="2"/>
                  <path d="M7 11V7a5 5 0 0110 0v4"/>
                </svg>
              </template>
            </el-input>
            <p v-if="errors.password" id="password-error" class="field-error" role="alert">
              {{ errors.password }}
            </p>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              @click="handleLogin"
              :loading="loading"
              class="login-btn"
              :aria-label="loading ? '登录中，请稍候' : '登录'"
            >
              <span v-if="loading" aria-live="polite">登录中...</span>
              <span v-else>登录</span>
            </el-button>
          </el-form-item>
        </el-form>

        <footer class="panel-footer">
          <p class="register-text">
            还没有账号？<router-link to="/register" class="register-link">立即注册</router-link>
          </p>
          <router-link to="/admin/login" class="admin-link">员工 / 管理后台登录</router-link>
        </footer>
      </div>
    </section>

    <!-- 免责声明弹窗 -->
    <DisclaimerDialog
      v-model="showDisclaimer"
      @accept="handleDisclaimerAccept"
      @decline="handleDisclaimerDecline"
    />
  </main>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { login } from '@/api/auth'
import DisclaimerDialog from '@/components/DisclaimerDialog.vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const showDisclaimer = ref(false)

// 错误状态 - 用于 ARIA 属性
const errors = reactive({
  username: '',
  password: ''
})

// 检查是否已同意过免责条款
const hasAgreedDisclaimer = localStorage.getItem('disclaimer_agreed') === 'true'

const form = ref({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 清除错误
const clearErrors = () => {
  errors.username = ''
  errors.password = ''
}

// 清除单个字段错误
const clearFieldError = (field) => {
  errors[field] = ''
}

// 设置字段错误
const setFieldError = (field, message) => {
  errors[field] = message
}

const handleLogin = () => {
  // 清除之前的错误
  clearErrors()

  // 手动验证
  let isValid = true
  if (!form.value.username) {
    setFieldError('username', '请输入用户名')
    isValid = false
  }
  if (!form.value.password) {
    setFieldError('password', '请输入密码')
    isValid = false
  } else if (form.value.password.length < 6) {
    setFieldError('password', '密码长度不能少于6位')
    isValid = false
  }

  if (!isValid) return

  loading.value = true
  login({ username: form.value.username, password: form.value.password })
    .then(res => {
      if (res.token && res.user) {
        localStorage.setItem('token', res.token)
        localStorage.setItem('user', JSON.stringify(res.user))
        if (!hasAgreedDisclaimer) {
          showDisclaimer.value = true
        } else {
          ElMessage.success('登录成功')
          window.dispatchEvent(new Event('login-state-change'))
          const redirect = router.currentRoute.value.query.redirect || '/scooters'
          router.push(redirect)
        }
      } else {
        ElMessage.error('登录失败：返回数据格式不正确')
      }
    })
    .catch(error => {
      // 处理后端返回的验证错误
      if (error.response?.data?.message) {
        const msg = error.response.data.message
        if (msg.includes('用户') || msg.includes('username')) {
          setFieldError('username', msg)
        }
        if (msg.includes('密码') || msg.includes('password')) {
          setFieldError('password', msg)
        }
      }
    })
    .finally(() => {
      loading.value = false
    })
}

// 用户同意免责条款
const handleDisclaimerAccept = () => {
  localStorage.setItem('disclaimer_agreed', 'true')
  ElMessage.success('登录成功')
  // 触发登录状态变更事件
  window.dispatchEvent(new Event('login-state-change'))
  const redirect = router.currentRoute.value.query.redirect || '/scooters'
  router.push(redirect)
}

// 用户不同意免责条款
const handleDisclaimerDecline = () => {
  // 退出登录
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.warning('您需要同意用户协议才能使用服务')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1fr 1fr;
}

@media (max-width: 900px) {
  .login-page {
    grid-template-columns: 1fr;
  }
  .login-hero {
    min-height: 280px;
    padding: 40px 24px;
  }
}

/* 左侧品牌区 */
.login-hero {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  background: linear-gradient(135deg, #1e3a5f 0%, #2d4a6f 50%, #3b5998 100%);
  overflow: hidden;
}

.login-hero::before {
  content: '';
  position: absolute;
  inset: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.03'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  pointer-events: none;
}

.hero-inner {
  position: relative;
  z-index: 1;
  text-align: center;
  max-width: 400px;
}

.hero-title {
  margin: 0 0 12px;
  font-size: 42px;
  font-weight: 800;
  color: white;
  letter-spacing: -0.02em;
}

.hero-tag {
  margin: 0 0 16px;
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 600;
}

.hero-desc {
  margin: 0 0 40px;
  font-size: 15px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.75);
}

.hero-features {
  display: flex;
  justify-content: center;
  gap: 36px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.feature-icon {
  width: 52px;
  height: 52px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.feature-icon svg {
  width: 26px;
  height: 26px;
  color: white;
}

.feature-item span {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.85);
  font-weight: 500;
}

/* 装饰滑板车 */
.hero-decor {
  margin: 40px 0 20px;
}

.scooter-decor {
  width: 180px;
  height: 70px;
}

/* 品牌语 */
.hero-slogan {
  margin: 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

/* 右侧登录面板 */
.login-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 48px;
  background: #f5f7fa;
}

.panel-inner {
  width: 100%;
  max-width: 380px;
}

.panel-header {
  text-align: center;
  margin-bottom: 36px;
}

.panel-title {
  margin: 0 0 10px;
  font-size: 28px;
  font-weight: 800;
  color: #1e3a5f;
  letter-spacing: -0.02em;
}

.panel-sub {
  margin: 0;
  font-size: 15px;
  color: #6b7b8a;
}

/* 表单样式 */
.login-form {
  margin-bottom: 24px;
}

.custom-input :deep(.el-input__wrapper) {
  padding: 14px 16px;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  background: white;
}

.custom-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 8px rgba(30, 58, 95, 0.1);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(30, 58, 95, 0.15);
}

.input-icon {
  width: 18px;
  height: 18px;
  color: #94a3b8;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 50px;
  font-size: 15px;
  font-weight: 700;
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%) !important;
  border: none !important;
  border-radius: 10px;
  margin-top: 8px;
  transition: all 0.2s;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(30, 58, 95, 0.3);
}

/* 底部链接 */
.panel-footer {
  text-align: center;
  margin-top: 28px;
}

.register-text {
  margin: 0 0 16px;
  font-size: 14px;
  color: #6b7b8a;
}

.register-link {
  color: #1e3a5f;
  font-weight: 600;
  text-decoration: none;
  transition: color 0.2s;
}

.register-link:hover {
  color: #3b5998;
}

.admin-link {
  display: inline-block;
  font-size: 13px;
  color: #94a3b8;
  text-decoration: none;
  transition: color 0.2s;
}

.admin-link:hover {
  color: #1e3a5f;
}

/* 错误消息样式 - 可访问性 */
.field-error {
  color: var(--cg-danger);
  font-size: 12px;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 错误状态输入框 */
.custom-input.is-error :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 2px var(--cg-danger) !important;
}

/* 焦点样式 - 高可见性 */
.login-btn:focus-visible {
  outline: 3px solid var(--cg-accent);
  outline-offset: 2px;
}
</style>
