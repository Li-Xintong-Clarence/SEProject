<template>
  <div class="register-page">
    <div class="register-hero">
      <div class="hero-inner">
        <img class="hero-logo" src="/brand-logo.png" alt="CapyGlide" width="96" height="96" />
        <h1 class="hero-title">加入 CapyGlide</h1>
        <p class="hero-tag">卡皮滑行 · 注册即享从容出行</p>
      </div>
    </div>

    <div class="register-panel">
      <el-card class="register-card" shadow="never">
        <h2 class="card-title">创建账号</h2>
        <p class="card-sub">填写信息，开始你的第一次滑行</p>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名" size="large" prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="email">
            <el-input v-model="form.email" placeholder="电子邮箱" size="large" prefix-icon="Message" />
          </el-form-item>
          <el-form-item prop="phone">
            <el-input v-model="form.phone" placeholder="手机号（选填）" size="large" prefix-icon="Phone" />
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
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="确认密码"
              size="large"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" @click="handleRegister" :loading="loading" class="register-btn">
              {{ loading ? '注册中...' : '注册' }}
            </el-button>
          </el-form-item>
        </el-form>
        <div class="login-link">
          已有账号？<router-link to="/login">立即登录</router-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = ref({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.value.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const registerData = {
          username: form.value.username,
          email: form.value.email,
          phone: form.value.phone || null,
          password: form.value.password
        }
        await register(registerData)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        console.error('注册出错:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1fr 520px;
}

@media (max-width: 960px) {
  .register-page {
    grid-template-columns: 1fr;
  }
  .register-hero {
    min-height: 260px;
    padding: 40px 24px;
  }
}

.register-hero {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  background: var(--cg-gradient-navy);
  overflow: hidden;
}

.register-hero::before {
  content: '';
  position: absolute;
  inset: 0;
  background: url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M11 18c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm48 25c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm-43-7c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm63 31c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM34 90c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm56-76c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM12 86c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm28-65c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm23-11c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-6 60c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm29 22c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zM32 63c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm57-13c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-9-21c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM60 91c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM35 41c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM12 60c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2z' fill='%23ffffff' fill-opacity='0.05' fill-rule='evenodd'/%3E%3C/svg%3E");
  pointer-events: none;
}

.hero-inner {
  position: relative;
  text-align: center;
  max-width: 400px;
  z-index: 1;
}

.hero-logo {
  border-radius: 20px;
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.2);
  margin-bottom: 20px;
}

.hero-title {
  margin: 0 0 12px;
  font-size: 2rem;
  font-weight: 800;
  color: white;
  letter-spacing: -0.02em;
}

.hero-tag {
  margin: 0;
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.9);
}

.register-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 32px;
  background: var(--cg-white);
}

.register-card {
  width: 100%;
  max-width: 420px;
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

.register-btn {
  width: 100%;
  height: 48px;
  font-size: 15px;
  font-weight: 700;
  background: var(--cg-gradient) !important;
  border: none !important;
  border-radius: var(--cg-radius-md);
  margin-top: 8px;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--cg-shadow-accent);
}

.login-link {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: var(--cg-text-light);
}

.login-link a {
  color: var(--cg-accent);
  font-weight: 600;
  text-decoration: none;
  transition: var(--cg-transition);
}

.login-link a:hover {
  color: var(--cg-accent-dark);
}
</style>
