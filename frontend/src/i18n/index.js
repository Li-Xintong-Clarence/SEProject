// 国际化配置 - CapyGlide
import { ref, computed } from 'vue'

const messages = {
  zh: {
    // 通用
    app: {
      name: 'CapyGlide 滑板车租赁',
      tagline: '便捷出行，绿色生活'
    },
    nav: {
      scooters: '找车',
      trip: '行程',
      profile: '我的'
    },
    auth: {
      login: '登录',
      register: '注册',
      logout: '退出登录',
      username: '用户名',
      password: '密码',
      email: '邮箱',
      phone: '手机号',
      remember: '记住我'
    },
    scooter: {
      available: '可租',
      reserved: '已预订',
      inUse: '使用中',
      maintenance: '维护中',
      battery: '电量',
      location: '位置',
      bookNow: '立即预订'
    },
    booking: {
      start: '开始时间',
      end: '结束时间',
      duration: '时长',
      cost: '费用',
      status: '状态',
      pay: '支付',
      cancel: '取消'
    },
    common: {
      confirm: '确认',
      cancel: '取消',
      save: '保存',
      delete: '删除',
      edit: '编辑',
      add: '添加',
      refresh: '刷新',
      loading: '加载中...',
      noData: '暂无数据',
      success: '操作成功',
      error: '操作失败'
    }
  },
  en: {
    // General
    app: {
      name: 'CapyGlide Scooter Rental',
      tagline: 'Convenient Travel, Green Life'
    },
    nav: {
      scooters: 'Find Scooters',
      trip: 'Trip',
      profile: 'Profile'
    },
    auth: {
      login: 'Login',
      register: 'Register',
      logout: 'Logout',
      username: 'Username',
      password: 'Password',
      email: 'Email',
      phone: 'Phone',
      remember: 'Remember me'
    },
    scooter: {
      available: 'Available',
      reserved: 'Reserved',
      inUse: 'In Use',
      maintenance: 'Maintenance',
      battery: 'Battery',
      location: 'Location',
      bookNow: 'Book Now'
    },
    booking: {
      start: 'Start Time',
      end: 'End Time',
      duration: 'Duration',
      cost: 'Cost',
      status: 'Status',
      pay: 'Pay',
      cancel: 'Cancel'
    },
    common: {
      confirm: 'Confirm',
      cancel: 'Cancel',
      save: 'Save',
      delete: 'Delete',
      edit: 'Edit',
      add: 'Add',
      refresh: 'Refresh',
      loading: 'Loading...',
      noData: 'No Data',
      success: 'Success',
      error: 'Error'
    }
  }
}

// 当前语言
const currentLocale = ref('zh')

// 切换语言
const setLocale = (locale) => {
  if (messages[locale]) {
    currentLocale.value = locale
    localStorage.setItem('locale', locale)
    // 触发语言变更事件，让组件响应式更新
    window.dispatchEvent(new CustomEvent('locale-change', { detail: locale }))
  }
}

// 获取翻译文本
const t = (key) => {
  const keys = key.split('.')
  let value = messages[currentLocale.value]
  for (const k of keys) {
    if (value && typeof value === 'object') {
      value = value[k]
    } else {
      return key
    }
  }
  return value || key
}

// 是否为英文
const isEnglish = computed(() => currentLocale.value === 'en')

export { currentLocale, setLocale, t, isEnglish, messages }
