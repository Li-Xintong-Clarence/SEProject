<template>
  <div class="login-page">
    <div class="login-hero">
      <div class="hero-inner">
        <img class="hero-logo" src="/brand-logo.png" alt="CapyGlide" width="120" height="120" />
        <h1 class="hero-title">CapyGlide</h1>
        <p class="hero-tag">卡皮滑行 · 轻松租一辆，慢慢逛一城</p>
        <p class="hero-desc">和卡皮巴拉一样从容：定位附近车辆，一键预订，随时出发。</p>
      </div>
    </div>

    <div class="login-panel">
      <el-card class="login-card" shadow="never">
        <h2 class="card-title">欢迎回来</h2>
        <p class="card-sub">登录后继续你的滑行之旅</p>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名" size="large" prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              size="large"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" @click="handleLogin" :loading="loading" class="login-btn">
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>
        <div class="register-link">
          还没有账号？<router-link to="/register">立即注册</router-link>
        </div>
        <div class="admin-entry">
          <router-link to="/admin/login">员工 / 管理后台登录</router-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { login } from '@/api/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

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

const handleLogin = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login({ username: form.value.username, password: form.value.password })
        if (res.token && res.user) {
          localStorage.setItem('token', res.token)
          localStorage.setItem('user', JSON.stringify(res.user))
          ElMessage.success('登录成功')
          // 触发登录状态变更事件
          window.dispatchEvent(new Event('login-state-change'))
          router.push('/scooters')
        } else {
          ElMessage.error('登录失败：返回数据格式不正确')
        }
      } catch (error) {
        console.error('登录出错:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1fr 480px;
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

.login-hero {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  background: var(--cg-gradient-navy);
  overflow: hidden;
}

.login-hero::before {
  content: '';
  position: absolute;
  inset: 0;
  background: url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M11 18c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm48 25c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm-43-7c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm63 31c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM34 90c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm56-76c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM12 86c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm28-65c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm23-11c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-6 60c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm29 22c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zM32 63c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm57-13c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-9-21c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM60 91c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM35 41c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM12 60c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2z' fill='%23ffffff' fill-opacity='0.05' fill-rule='evenodd'/%3E%3C/svg%3E");
  pointer-events: none;
}

.hero-inner {
  position: relative;
  text-align: center;
  max-width: 420px;
  z-index: 1;
}

.hero-logo {
  border-radius: 24px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
  margin-bottom: 24px;
}

.hero-title {
  margin: 0 0 12px;
  font-size: 2.5rem;
  font-weight: 800;
  color: white;
  letter-spacing: -0.02em;
}

.hero-tag {
  margin: 0 0 16px;
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 600;
}

.hero-desc {
  margin: 0;
  font-size: 1rem;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.8);
}

.login-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 32px;
  background: var(--cg-white);
}

.login-card {
  width: 100%;
  max-width: 380px;
}

.card-title {
  margin: 0 0 8px;
  text-align: center;
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--cg-navy);
  letter-spacing: -0.02em;
}

.card-sub {
  margin: 0 0 32px;
  text-align: center;
  font-size: 15px;
  color: var(--cg-text-light);
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 15px;
  font-weight: 700;
  background: var(--cg-gradient) !important;
  border: none !important;
  border-radius: var(--cg-radius-md);
  margin-top: 8px;
  transition: var(--cg-transition);
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--cg-shadow-accent);
}

.register-link {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: var(--cg-text-light);
}

.register-link a {
  color: var(--cg-accent);
  font-weight: 600;
  text-decoration: none;
  transition: var(--cg-transition);
}

.register-link a:hover {
  color: var(--cg-accent-dark);
}

.admin-entry {
  text-align: center;
  margin-top: 20px;
  font-size: 13px;
}

.admin-entry a {
  color: var(--cg-text-muted);
  text-decoration: none;
  transition: var(--cg-transition);
}

.admin-entry a:hover {
  color: var(--cg-navy);
}
</style>
