import axios from 'axios'
import router from '@/router'

// API基础URL
const BASE_URL = 'http://localhost:8080'

// 创建axios实例
const api = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 获取完整的头像URL
export const getAvatarUrl = (avatar) => {
  if (!avatar) return null
  // 如果已经是完整URL，直接返回
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return avatar
  }
  // 如果是相对路径，拼接基础URL
  return `${BASE_URL}${avatar.startsWith('/') ? '' : '/'}${avatar}`
}

// 判断当前是否为医生端（管理页面）
// 注意：/doctor/:id 是学生查看医生详情，不是医生端
// 注意：/doctors 是学生查看医生列表，不是医生端
const isDoctorRoute = () => {
  const path = window.location.pathname
  // 医生端管理页面的路由格式是 /doctor/xxx，如 /doctor/home, /doctor/appointments
  // 学生端查看医生详情的路由格式是 /doctor/:id（纯数字）
  // 学生端查看医生列表的路由是 /doctors
  if (!path.startsWith('/doctor/')) {
    return false
  }
  // 提取 /doctor/ 后面的部分
  const subPath = path.slice('/doctor/'.length)
  // 如果是纯数字（医生ID），则是学生端查看医生详情
  if (/^\d+$/.test(subPath)) {
    return false
  }
  // 否则是医生端管理页面
  return true
}

// 获取token的key
const getTokenKey = () => {
  return isDoctorRoute() ? 'doctor_token' : 'student_token'
}

// 获取user的key
const getUserKey = () => {
  return isDoctorRoute() ? 'doctor_user' : 'student_user'
}

// 获取token
export const getToken = () => {
  return localStorage.getItem(getTokenKey())
}

// 设置token
export const setToken = (token) => {
  localStorage.setItem(getTokenKey(), token)
}

// 移除token
export const removeToken = () => {
  localStorage.removeItem(getTokenKey())
}

// 获取用户信息
export const getUser = () => {
  const userStr = localStorage.getItem(getUserKey())
  return userStr ? JSON.parse(userStr) : null
}

// 设置用户信息
export const setUser = (user) => {
  localStorage.setItem(getUserKey(), JSON.stringify(user))
}

// 移除用户信息
export const removeUser = () => {
  localStorage.removeItem(getUserKey())
}

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 根据当前路由获取对应的token
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    // 处理401未授权错误
    if (error.response?.status === 401) {
      // 获取当前路径
      const currentPath = window.location.pathname
      
      // 判断是否是管理员端
      const isAdminRoute = currentPath.startsWith('/admin')
      
      // 清除对应端的本地存储
      if (isAdminRoute) {
        localStorage.removeItem('admin_token')
        localStorage.removeItem('admin_user')
      } else {
        removeToken()
        removeUser()
      }
      
      // 确定登录页路径
      let loginPath = '/login'
      if (isAdminRoute) {
        loginPath = '/admin/login'
      } else if (isDoctorRoute()) {
        loginPath = '/doctor/login'
      }
      
      // 如果已经在登录页，不跳转，只显示提示
      if (currentPath === loginPath || currentPath === '/login' || currentPath === '/doctor/login' || currentPath === '/admin/login') {
        // 不跳转，让页面自行处理提示
      } else {
        // 跳转到登录页，并携带提示信息
        router.push({ path: loginPath, query: { expired: 'true' } })
      }
    }
    return Promise.reject(error)
  }
)

export default api
