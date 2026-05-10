<template>
  <div class="admin-login-page">
    <div class="login-card">
      <div class="brand-row">
        <img src="/brand-logo.png" alt="" width="56" height="56" class="logo" />
        <div>
          <h1>员工 / 管理后台</h1>
          <p class="sub">CapyGlide · 代客预订 · 报表 · 车辆与价格</p>
        </div>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="管理员用户名"
            size="large"
            prefix-icon="User"
          />
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
          <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click="submit">
            {{ loading ? '登录中...' : '登录后台' }}
          </el-button>
        </el-form-item>
      </el-form>
      <div class="links">
        <router-link to="/login">返回用户登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const loading = ref(false)
const form = ref({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const submit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await request({
        url: '/auth/admin/login',
        method: 'post',
        data: { username: form.value.username, password: form.value.password }
      })
      if (res?.token && res?.user) {
        if (res.user.role !== 'ADMIN') {
          ElMessage.error('该账号不是管理员')
          return
        }
        localStorage.setItem('token', res.token)
        localStorage.setItem('user', JSON.stringify(res.user))
        ElMessage.success('登录成功')
        const redirect = route.query.redirect || '/admin'
        router.push(typeof redirect === 'string' ? redirect : '/admin')
      } else {
        ElMessage.error('登录失败')
      }
    } catch (e) {
      console.error(e)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.admin-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: var(--cg-gradient-navy);
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: var(--cg-white);
  border-radius: var(--cg-radius-xl);
  padding: 40px 32px;
  box-shadow: var(--cg-shadow-lg);
}

.brand-row {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 32px;
}

.logo {
  border-radius: var(--cg-radius-lg);
  flex-shrink: 0;
}

.brand-row h1 {
  margin: 0;
  font-size: 1.4rem;
  color: var(--cg-navy);
  font-weight: 800;
}

.brand-row .sub {
  margin: 4px 0 0;
  font-size: 0.85rem;
  color: #6b7280;
}

.submit-btn {
  width: 100%;
  height: 48px;
  background: var(--cg-navy);
  border-color: var(--cg-navy);
  font-weight: 600;
}

.submit-btn:hover {
  background: var(--cg-navy-soft);
  border-color: var(--cg-navy-soft);
}

.links {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
}

.links a {
  color: var(--cg-navy-soft);
  font-weight: 600;
  text-decoration: none;
}

.links a:hover {
  color: var(--cg-accent);
}
</style>
