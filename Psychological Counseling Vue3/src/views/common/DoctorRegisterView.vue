<script setup>
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import api from '@/api'

const router = useRouter()

const formData = ref({
  name: '',
  title: '',
  phone: '',
  password: '',
  confirmPassword: '',
  email: '',
  years: 0,
  description: '',
  tags: [],
  methods: [],
  certifications: []
})

const loading = ref(false)
const errorMessage = ref('')

// 可选的擅长领域
const availableTags = [
  '焦虑抑郁', '个人成长', '情绪管理', '人际关系', 
  '学业压力', '恋爱婚姻', '职业规划', '家庭关系',
  '睡眠问题', '自我认知', '压力管理', '创伤修复'
]

// 可选的咨询方法
const availableMethods = [
  '认知行为疗法', '人本主义', '精神分析', '家庭系统治疗',
  '正念疗法', '叙事疗法', '焦点解决', '沙盘治疗'
]

// 证书输入
const certInput = ref('')

// 表单验证
const validateForm = () => {
  if (!formData.value.name || formData.value.name.length < 2) {
    errorMessage.value = '请输入正确的姓名（至少2个字符）'
    return false
  }
  
  if (!formData.value.title) {
    errorMessage.value = '请输入职称'
    return false
  }
  
  if (!formData.value.phone || !/^1[3-9]\d{9}$/.test(formData.value.phone)) {
    errorMessage.value = '请输入正确的手机号码'
    return false
  }
  
  if (!formData.value.password || formData.value.password.length < 6) {
    errorMessage.value = '密码长度至少为6个字符'
    return false
  }
  
  if (formData.value.password !== formData.value.confirmPassword) {
    errorMessage.value = '两次输入的密码不一致'
    return false
  }
  
  if (!formData.value.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.value.email)) {
    errorMessage.value = '请输入正确的邮箱地址'
    return false
  }
  
  if (formData.value.years < 0 || formData.value.years > 50) {
    errorMessage.value = '从业年限应在0-50年之间'
    return false
  }
  
  return true
}

// 切换标签选择
const toggleTag = (tag) => {
  const index = formData.value.tags.indexOf(tag)
  if (index >= 0) {
    formData.value.tags.splice(index, 1)
  } else {
    formData.value.tags.push(tag)
  }
}

// 切换方法选择
const toggleMethod = (method) => {
  const index = formData.value.methods.indexOf(method)
  if (index >= 0) {
    formData.value.methods.splice(index, 1)
  } else {
    formData.value.methods.push(method)
  }
}

// 添加证书
const addCertification = () => {
  if (certInput.value.trim()) {
    formData.value.certifications.push(certInput.value.trim())
    certInput.value = ''
  }
}

// 删除证书
const removeCertification = (index) => {
  formData.value.certifications.splice(index, 1)
}

// 提交注册
const handleRegister = async () => {
  errorMessage.value = ''
  
  if (!validateForm()) {
    return
  }

  loading.value = true
  try {
    const response = await api.post('/api/auth/doctor/register', {
      name: formData.value.name,
      title: formData.value.title,
      phone: formData.value.phone,
      password: formData.value.password,
      email: formData.value.email,
      years: formData.value.years,
      description: formData.value.description,
      tags: formData.value.tags,
      methods: formData.value.methods,
      certifications: formData.value.certifications
    })
    
    if (response.data.success) {
      alert('注册成功，请登录')
      router.push('/login')
    } else {
      errorMessage.value = response.data.message || '注册失败'
    }
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '注册失败，请稀后重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-indigo-50 via-white to-purple-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-3xl w-full space-y-8">
      <!-- 头部 -->
      <div class="text-center">
        <div class="flex justify-center mb-4">
          <div class="w-16 h-16 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-2xl flex items-center justify-center text-white shadow-lg">
            <i class="fa-solid fa-user-doctor text-3xl"></i>
          </div>
        </div>
        <h2 class="text-4xl font-bold text-slate-800 tracking-tight">咨询师注册</h2>
        <p class="mt-2 text-gray-600">加入我们，帮助更多需要心理支持的学生</p>
      </div>

      <!-- 注册表单 -->
      <div class="bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
        <form @submit.prevent="handleRegister" class="space-y-6">
          
          <!-- 错误提示 -->
          <div v-if="errorMessage" class="bg-rose-50 border border-rose-200 text-rose-700 px-4 py-3 rounded-lg flex items-center gap-2">
            <i class="fa-solid fa-circle-exclamation"></i>
            <span>{{ errorMessage }}</span>
          </div>

          <!-- 基本信息 -->
          <div class="space-y-4">
            <h3 class="text-lg font-bold text-slate-800 flex items-center gap-2">
              <i class="fa-solid fa-user text-indigo-600"></i>
              基本信息
            </h3>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">姓名 *</label>
                <input v-model="formData.name" type="text" required
                  class="w-full px-4 py-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400 transition"
                  placeholder="请输入您的姓名">
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">职称 *</label>
                <input v-model="formData.title" type="text" required
                  class="w-full px-4 py-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400 transition"
                  placeholder="如：注册心理师">
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">手机号码 *</label>
                <input v-model="formData.phone" type="tel" required
                  class="w-full px-4 py-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400 transition"
                  placeholder="用于登录和联系">
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">从业年限 *</label>
                <input v-model.number="formData.years" type="number" min="0" max="50" required
                  class="w-full px-4 py-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400 transition"
                  placeholder="年">
              </div>
              
              <div class="md:col-span-2">
                <label class="block text-sm font-medium text-gray-700 mb-2">电子邮箱 *</label>
                <input v-model="formData.email" type="email" required
                  class="w-full px-4 py-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400 transition"
                  placeholder="example@email.com">
              </div>
            </div>
          </div>

          <!-- 密码设置 -->
          <div class="space-y-4">
            <h3 class="text-lg font-bold text-slate-800 flex items-center gap-2">
              <i class="fa-solid fa-lock text-indigo-600"></i>
              密码设置
            </h3>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">密码 *</label>
                <input v-model="formData.password" type="password" required
                  class="w-full px-4 py-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400 transition"
                  placeholder="至少6个字符">
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">确认密码 *</label>
                <input v-model="formData.confirmPassword" type="password" required
                  class="w-full px-4 py-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400 transition"
                  placeholder="再次输入密码">
              </div>
            </div>
          </div>

          <!-- 个人简介 -->
          <div class="space-y-4">
            <h3 class="text-lg font-bold text-slate-800 flex items-center gap-2">
              <i class="fa-solid fa-file-lines text-indigo-600"></i>
              个人简介
            </h3>
            <textarea v-model="formData.description" rows="4"
              class="w-full px-4 py-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400 transition resize-none"
              placeholder="请简要介绍您的专业背景和咨询风格（选填）"></textarea>
          </div>

          <!-- 擅长领域 -->
          <div class="space-y-4">
            <h3 class="text-lg font-bold text-slate-800 flex items-center gap-2">
              <i class="fa-solid fa-tags text-indigo-600"></i>
              擅长领域
            </h3>
            <div class="flex flex-wrap gap-2">
              <button v-for="tag in availableTags" :key="tag" type="button"
                @click="toggleTag(tag)"
                :class="[
                  'px-4 py-2 rounded-full text-sm font-medium transition',
                  formData.tags.includes(tag)
                    ? 'bg-indigo-600 text-white'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                ]">
                {{ tag }}
              </button>
            </div>
          </div>

          <!-- 咨询方法 -->
          <div class="space-y-4">
            <h3 class="text-lg font-bold text-slate-800 flex items-center gap-2">
              <i class="fa-solid fa-wand-magic-sparkles text-indigo-600"></i>
              咨询方法
            </h3>
            <div class="flex flex-wrap gap-2">
              <button v-for="method in availableMethods" :key="method" type="button"
                @click="toggleMethod(method)"
                :class="[
                  'px-4 py-2 rounded-full text-sm font-medium transition',
                  formData.methods.includes(method)
                    ? 'bg-purple-600 text-white'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                ]">
                {{ method }}
              </button>
            </div>
          </div>

          <!-- 资质认证 -->
          <div class="space-y-4">
            <h3 class="text-lg font-bold text-slate-800 flex items-center gap-2">
              <i class="fa-solid fa-certificate text-indigo-600"></i>
              资质认证
            </h3>
            <div class="flex gap-2">
              <input v-model="certInput" type="text" @keyup.enter="addCertification"
                class="flex-1 px-4 py-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400 transition"
                placeholder="输入资质证书后按回车添加">
              <button type="button" @click="addCertification"
                class="px-6 py-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition">
                <i class="fa-solid fa-plus mr-2"></i>添加
              </button>
            </div>
            <div v-if="formData.certifications.length > 0" class="space-y-2">
              <div v-for="(cert, index) in formData.certifications" :key="index"
                class="flex items-center justify-between bg-amber-50 border border-amber-200 px-4 py-2 rounded-lg">
                <span class="text-amber-700">{{ cert }}</span>
                <button type="button" @click="removeCertification(index)"
                  class="text-amber-700 hover:text-amber-900">
                  <i class="fa-solid fa-times"></i>
                </button>
              </div>
            </div>
          </div>

          <!-- 提交按钮 -->
          <button type="submit" :disabled="loading"
            class="w-full py-4 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-lg font-medium hover:from-indigo-700 hover:to-purple-700 transition disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 shadow-lg">
            <i v-if="loading" class="fa-solid fa-spinner fa-spin"></i>
            <span>{{ loading ? '注册中...' : '立即注册' }}</span>
          </button>

          <!-- 返回登录 -->
          <div class="text-center">
            <RouterLink to="/login" class="text-indigo-600 hover:text-indigo-700 text-sm font-medium">
              已有账号？返回登录
            </RouterLink>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
