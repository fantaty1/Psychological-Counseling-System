<script setup>
import { ref, onMounted, computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import api, { getAvatarUrl, getUser, setUser, removeToken, removeUser } from '@/api'
import AvatarCropper from '@/components/AvatarCropper.vue'

const router = useRouter()

// 当前激活的菜单
const activeMenu = ref('profile')

const userInfo = ref({
  name: '',
  avatar: '',
  college: '',
  majorClass: '',
  studentId: '',
  phone: '',
  email: '',
  stats: {
    counselingHours: 0,
    appointmentsCount: 0,
    checkinStreak: 0
  }
})

const loading = ref(true)

// 编辑模式相关
const isEditing = ref(false)
const editForm = ref({
  name: '',
  college: '',
  majorClass: '',
  phone: '',
  email: ''
})
const editLoading = ref(false)
const editError = ref('')

// 头像上传相关
const avatarUploading = ref(false)
const avatarInput = ref(null)
const showCropper = ref(false)
const cropperImgUrl = ref('')
const cropperRef = ref(null)

// 计算属性：获取正确的头像URL
const avatarUrl = computed(() => {
  const url = getAvatarUrl(userInfo.value.avatar)
  console.log('计算头像URL - 原始值:', userInfo.value.avatar, '转换后:', url)
  return url || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixlib=rb-4.0.3&auto=format&fit=crop&w=200&q=80'
})

// 修改密码表单
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const passwordLoading = ref(false)
const passwordError = ref('')
const passwordSuccess = ref('')

// 获取用户信息
const fetchUserProfile = async () => {
  try {
    const response = await api.get('/api/user/profile')
    console.log('获取用户信息响应:', response.data)
    if (response.data.success) {
      userInfo.value = response.data.body
      console.log('用户头像值:', response.data.body.avatar)
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    // 如果是未授权，会被拦截器处理跳转登录
  } finally {
    loading.value = false
  }
}

// 触发头像选择
const triggerAvatarUpload = () => {
  avatarInput.value?.click()
}

// 选择图片后打开裁剪器
const handleFileSelect = (event) => {
  const file = event.target.files?.[0]
  if (!file) return
  
  // 验证文件类型
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    alert('只支持 JPG、PNG、GIF、WEBP 格式的图片')
    return
  }
  
  // 验证文件大小 (10MB，裁剪前可以大一点)
  if (file.size > 10 * 1024 * 1024) {
    alert('图片大小不能超过10MB')
    return
  }
  
  // 读取文件为DataURL用于裁剪
  const reader = new FileReader()
  reader.onload = (e) => {
    cropperImgUrl.value = e.target.result
    showCropper.value = true
  }
  reader.readAsDataURL(file)
  
  // 清空input
  event.target.value = ''
}

// 裁剪确认后上传
const handleCropConfirm = async (croppedFile) => {
  try {
    avatarUploading.value = true
    cropperRef.value?.setUploading(true)
    
    const formData = new FormData()
    formData.append('file', croppedFile)
    
    console.log('开始上传头像，文件大小:', croppedFile.size)
    
    const response = await api.post('/api/file/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    
    console.log('头像上传响应:', response.data)
    
    if (response.data.success) {
      const avatarPath = response.data.body.url
      console.log('头像路径:', avatarPath)
      userInfo.value.avatar = avatarPath
      // 更新localStorage中的用户信息
      const storedUser = getUser() || {}
      storedUser.avatar = avatarPath
      setUser(storedUser)
      showCropper.value = false
      alert('头像上传成功！')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    alert(error.response?.data?.message || '头像上传失败，请稍后重试')
  } finally {
    avatarUploading.value = false
    cropperRef.value?.setUploading(false)
  }
}

// 修改密码
const changePassword = async () => {
  passwordError.value = ''
  passwordSuccess.value = ''
  
  // 验证
  if (!passwordForm.value.oldPassword) {
    passwordError.value = '请输入当前密码'
    return
  }
  if (!passwordForm.value.newPassword) {
    passwordError.value = '请输入新密码'
    return
  }
  if (passwordForm.value.newPassword.length < 6) {
    passwordError.value = '新密码长度至少6位'
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    passwordError.value = '两次输入的密码不一致'
    return
  }
  
  try {
    passwordLoading.value = true
    const response = await api.put('/api/user/password', {
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    
    if (response.data.success) {
      passwordSuccess.value = '密码修改成功！'
      passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    } else {
      passwordError.value = response.data.message || '修改失败'
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    passwordError.value = error.response?.data?.message || '修改密码失败，请稍后重试'
  } finally {
    passwordLoading.value = false
  }
}

// 切换菜单
const switchMenu = (menu) => {
  activeMenu.value = menu
}

// 开始编辑个人资料
const startEdit = () => {
  editForm.value = {
    name: userInfo.value.name || '',
    college: userInfo.value.college || '',
    majorClass: userInfo.value.majorClass || '',
    phone: userInfo.value.phone || '',
    email: userInfo.value.email || ''
  }
  editError.value = ''
  isEditing.value = true
}

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false
  editError.value = ''
}

// 保存编辑
const saveProfile = async () => {
  editError.value = ''
  
  // 验证邮箱格式
  if (editForm.value.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(editForm.value.email)) {
    editError.value = '邮箱格式不正确'
    return
  }
  
  // 验证手机号格式
  if (editForm.value.phone && !/^1[3-9]\d{9}$/.test(editForm.value.phone)) {
    editError.value = '手机号格式不正确'
    return
  }
  
  try {
    editLoading.value = true
    const response = await api.put('/api/user/profile', {
      name: editForm.value.name || null,
      college: editForm.value.college || null,
      majorClass: editForm.value.majorClass || null,
      phone: editForm.value.phone || null,
      email: editForm.value.email || null
    })
    
    if (response.data.success) {
      // 更新本地数据
      userInfo.value.name = editForm.value.name
      userInfo.value.college = editForm.value.college
      userInfo.value.majorClass = editForm.value.majorClass
      userInfo.value.phone = editForm.value.phone
      userInfo.value.email = editForm.value.email
      
      // 更新localStorage中的用户信息
      const storedUser = getUser() || {}
      storedUser.name = editForm.value.name
      storedUser.college = editForm.value.college
      setUser(storedUser)
      
      isEditing.value = false
      alert('个人资料更新成功！')
    } else {
      editError.value = response.data.message || '保存失败'
    }
  } catch (error) {
    console.error('保存个人资料失败:', error)
    editError.value = error.response?.data?.message || '保存失败，请稍后重试'
  } finally {
    editLoading.value = false
  }
}

// 跳转到预约页面
const goToAppointments = () => {
  router.push('/appointments')
}

// 跳转到历史会话页面
const goToHistory = () => {
  router.push('/chat-history')
}

// 跳转到测评记录页面
const goToAssessments = () => {
  router.push('/assessments')
}

const handleLogout = () => {
  // 清除token和用户信息
  removeToken()
  removeUser()
  localStorage.removeItem('role')
  
  router.push('/login')
}

onMounted(() => {
  fetchUserProfile()
})
</script>

<template>
  <div class="bg-gray-50 text-slate-800 min-h-screen flex flex-col">
    <!-- 顶部导航栏 (简化版) -->
    <nav class="bg-white shadow-sm border-b border-gray-100 sticky top-0 z-50">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div class="flex justify-between h-16">
                <div class="flex items-center gap-4">
                    <RouterLink to="/" class="text-gray-500 hover:text-teal-600 transition">
                        <i class="fa-solid fa-arrow-left mr-1"></i> 返回首页
                    </RouterLink>
                    <div class="h-6 w-px bg-gray-200"></div>
                    <h1 class="font-bold text-xl text-slate-800">个人中心</h1>
                </div>
            </div>
        </div>
    </nav>

    <main class="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
        
        <!-- 加载状态 -->
        <div v-if="loading" class="flex justify-center items-center py-20">
            <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-teal-500"></div>
        </div>

        <div v-else class="grid grid-cols-1 lg:grid-cols-4 gap-8">
            
            <!-- 左侧：侧边菜单 -->
            <div class="lg:col-span-1">
                <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                    <div class="p-6 border-b border-gray-50 flex flex-col items-center text-center">
                        <!-- 头像上传 -->
                        <div class="relative group cursor-pointer mb-4" @click="triggerAvatarUpload">
                            <div class="w-24 h-24 rounded-full p-1 bg-white border border-gray-100 shadow-sm overflow-hidden">
                                <img :src="avatarUrl" 
                                    :alt="userInfo.name" 
                                    class="w-full h-full rounded-full object-cover">
                            </div>
                            <!-- 上传遮罩 -->
                            <div class="absolute inset-0 bg-black bg-opacity-40 rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition">
                                <i v-if="avatarUploading" class="fa-solid fa-spinner fa-spin text-white text-xl"></i>
                                <i v-else class="fa-solid fa-camera text-white text-xl"></i>
                            </div>
                            <input ref="avatarInput" type="file" accept="image/*" class="hidden" @change="handleFileSelect">
                        </div>
                        <h2 class="font-bold text-lg text-slate-800">{{ userInfo.name || '未设置' }}</h2>
                        <p class="text-sm text-gray-500">{{ userInfo.college || '未设置' }} | {{ userInfo.majorClass || '未设置' }}</p>
                    </div>
                    <nav class="p-2">
                        <button @click="switchMenu('profile')" :class="[
                            'w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium transition text-left',
                            activeMenu === 'profile' ? 'bg-teal-50 text-teal-700' : 'text-gray-600 hover:bg-gray-50 hover:text-slate-800'
                        ]">
                            <i class="fa-regular fa-user w-5 text-center"></i> 个人资料
                        </button>
                        <button @click="goToAppointments" class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-gray-600 hover:bg-gray-50 hover:text-slate-800 transition text-left">
                            <i class="fa-regular fa-calendar-check w-5 text-center"></i> 我的预约
                        </button>
                        <button @click="goToHistory" class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-gray-600 hover:bg-gray-50 hover:text-slate-800 transition text-left">
                            <i class="fa-regular fa-comments w-5 text-center"></i> 历史会话
                        </button>
                        <button @click="goToAssessments" class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-gray-600 hover:bg-gray-50 hover:text-slate-800 transition text-left">
                            <i class="fa-regular fa-file-lines w-5 text-center"></i> 测评记录
                        </button>
                        <div class="my-2 border-t border-gray-50"></div>
                        <button @click="switchMenu('security')" :class="[
                            'w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium transition text-left',
                            activeMenu === 'security' ? 'bg-teal-50 text-teal-700' : 'text-gray-600 hover:bg-gray-50 hover:text-slate-800'
                        ]">
                            <i class="fa-solid fa-shield-halved w-5 text-center"></i> 账号与安全
                        </button>
                        <button @click="handleLogout" class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-rose-500 hover:bg-rose-50 transition text-left">
                            <i class="fa-solid fa-arrow-right-from-bracket w-5 text-center"></i> 退出登录
                        </button>
                    </nav>
                </div>
            </div>

            <!-- 右侧：内容区域 -->
            <div class="lg:col-span-3 space-y-6">
                
                <!-- 个人资料面板 -->
                <template v-if="activeMenu === 'profile'">
                  <!-- 数据概览 -->
                  <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                      <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center gap-4">
                          <div class="w-12 h-12 rounded-full bg-blue-50 text-blue-500 flex items-center justify-center text-xl">
                              <i class="fa-regular fa-clock"></i>
                          </div>
                          <div>
                              <div class="text-2xl font-bold text-slate-800">{{ userInfo.stats?.counselingHours || 0 }}</div>
                              <div class="text-sm text-gray-500">咨询时长 (小时)</div>
                          </div>
                      </div>
                      <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center gap-4">
                          <div class="w-12 h-12 rounded-full bg-teal-50 text-teal-500 flex items-center justify-center text-xl">
                              <i class="fa-regular fa-calendar-check"></i>
                          </div>
                          <div>
                              <div class="text-2xl font-bold text-slate-800">{{ userInfo.stats?.appointmentsCount || 0 }}</div>
                              <div class="text-sm text-gray-500">完成预约 (次)</div>
                          </div>
                      </div>
                      <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center gap-4">
                          <div class="w-12 h-12 rounded-full bg-orange-50 text-orange-500 flex items-center justify-center text-xl">
                              <i class="fa-solid fa-fire"></i>
                          </div>
                          <div>
                              <div class="text-2xl font-bold text-slate-800">{{ userInfo.stats?.checkinStreak || 0 }}</div>
                              <div class="text-sm text-gray-500">连续签到 (天)</div>
                          </div>
                      </div>
                  </div>

                  <!-- 基本信息表单 -->
                  <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
                      <div class="flex justify-between items-center mb-6">
                          <h3 class="font-bold text-lg text-slate-800">基本信息</h3>
                          <button v-if="!isEditing" @click="startEdit" 
                                  class="px-4 py-2 text-teal-600 text-sm font-medium hover:text-teal-700 border border-teal-500 rounded-lg hover:bg-teal-50 transition">
                            <i class="fa-solid fa-pen mr-1"></i> 编辑
                          </button>
                          <div v-else class="flex gap-2">
                            <button @click="cancelEdit" 
                                    class="px-4 py-2 text-gray-600 text-sm font-medium border border-gray-300 rounded-lg hover:bg-gray-50 transition">
                              取消
                            </button>
                            <button @click="saveProfile" :disabled="editLoading"
                                    class="px-4 py-2 bg-teal-500 text-white text-sm font-medium rounded-lg hover:bg-teal-600 transition disabled:opacity-50 disabled:cursor-not-allowed">
                              <i v-if="editLoading" class="fa-solid fa-spinner fa-spin mr-1"></i>
                              {{ editLoading ? '保存中...' : '保存' }}
                            </button>
                          </div>
                      </div>
                      
                      <!-- 错误提示 -->
                      <div v-if="editError" class="mb-6 p-4 bg-rose-50 border border-rose-200 rounded-xl text-rose-600 text-sm">
                          <i class="fa-solid fa-circle-exclamation mr-2"></i>{{ editError }}
                      </div>
                      
                      <!-- 编辑模式 -->
                      <div v-if="isEditing" class="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-6">
                          <div>
                              <label class="block text-sm font-medium text-gray-500 mb-2">姓名</label>
                              <input v-model="editForm.name" type="text"
                                     class="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"
                                     placeholder="请输入姓名">
                          </div>
                          <div>
                              <label class="block text-sm font-medium text-gray-500 mb-2">学号</label>
                              <div class="text-slate-800 font-medium py-3 text-gray-400">{{ userInfo.studentId || '未设置' }} <span class="text-xs">(不可修改)</span></div>
                          </div>
                          <div>
                              <label class="block text-sm font-medium text-gray-500 mb-2">学院</label>
                              <input v-model="editForm.college" type="text"
                                     class="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"
                                     placeholder="请输入学院">
                          </div>
                          <div>
                              <label class="block text-sm font-medium text-gray-500 mb-2">专业班级</label>
                              <input v-model="editForm.majorClass" type="text"
                                     class="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"
                                     placeholder="请输入专业班级">
                          </div>
                          <div>
                              <label class="block text-sm font-medium text-gray-500 mb-2">手机号码</label>
                              <input v-model="editForm.phone" type="tel"
                                     class="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"
                                     placeholder="请输入手机号码">
                          </div>
                          <div>
                              <label class="block text-sm font-medium text-gray-500 mb-2">电子邮箱</label>
                              <input v-model="editForm.email" type="email"
                                     class="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"
                                     placeholder="请输入电子邮箱">
                          </div>
                      </div>
                      
                      <!-- 查看模式 -->
                      <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-6">
                          <div>
                              <label class="block text-sm font-medium text-gray-500 mb-1">姓名</label>
                              <div class="text-slate-800 font-medium">{{ userInfo.name || '未设置' }}</div>
                          </div>
                          <div>
                              <label class="block text-sm font-medium text-gray-500 mb-1">学号</label>
                              <div class="text-slate-800 font-medium">{{ userInfo.studentId || '未设置' }}</div>
                          </div>
                          <div>
                              <label class="block text-sm font-medium text-gray-500 mb-1">学院</label>
                              <div class="text-slate-800 font-medium">{{ userInfo.college || '未设置' }}</div>
                          </div>
                          <div>
                              <label class="block text-sm font-medium text-gray-500 mb-1">专业班级</label>
                              <div class="text-slate-800 font-medium">{{ userInfo.majorClass || '未设置' }}</div>
                          </div>
                          <div>
                              <label class="block text-sm font-medium text-gray-500 mb-1">手机号码</label>
                              <div class="text-slate-800 font-medium">{{ userInfo.phone || '未设置' }}</div>
                          </div>
                          <div>
                              <label class="block text-sm font-medium text-gray-500 mb-1">电子邮箱</label>
                              <div class="text-slate-800 font-medium">{{ userInfo.email || '未设置' }}</div>
                          </div>
                      </div>
                  </div>

                  <!-- 隐私设置 -->
                  <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
                      <h3 class="font-bold text-lg text-slate-800 mb-6">隐私设置</h3>
                      
                      <div class="space-y-6">
                          <div class="flex items-center justify-between">
                              <div>
                                  <h4 class="font-medium text-slate-800">允许接收系统通知</h4>
                                  <p class="text-sm text-gray-500">包括预约提醒、消息通知等。</p>
                              </div>
                              <label class="relative inline-flex items-center cursor-pointer">
                                  <input type="checkbox" value="" class="sr-only peer" checked>
                                  <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-teal-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-teal-500"></div>
                              </label>
                          </div>
                      </div>
                  </div>
                </template>
                
                <!-- 账号与安全面板 -->
                <template v-else-if="activeMenu === 'security'">
                  <!-- 账号信息 -->
                  <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
                      <h3 class="font-bold text-lg text-slate-800 mb-6">账号信息</h3>
                      
                      <div class="space-y-4">
                          <div class="flex items-center justify-between py-4 border-b border-gray-100">
                              <div>
                                  <p class="font-medium text-slate-800">登录账号</p>
                                  <p class="text-sm text-gray-500">使用学号或邮箱登录</p>
                              </div>
                              <div class="text-right">
                                  <p class="font-medium text-slate-800">{{ userInfo.studentId || userInfo.email || '未设置' }}</p>
                              </div>
                          </div>
                          <div class="flex items-center justify-between py-4 border-b border-gray-100">
                              <div>
                                  <p class="font-medium text-slate-800">绑定邮箱</p>
                                  <p class="text-sm text-gray-500">用于找回密码和接收通知</p>
                              </div>
                              <div class="text-right">
                                  <p class="font-medium text-slate-800">{{ userInfo.email || '未绑定' }}</p>
                              </div>
                          </div>
                          <div class="flex items-center justify-between py-4">
                              <div>
                                  <p class="font-medium text-slate-800">绑定手机</p>
                                  <p class="text-sm text-gray-500">用于接收短信通知</p>
                              </div>
                              <div class="text-right">
                                  <p class="font-medium text-slate-800">{{ userInfo.phone || '未绑定' }}</p>
                              </div>
                          </div>
                      </div>
                  </div>
                  
                  <!-- 修改密码 -->
                  <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
                      <h3 class="font-bold text-lg text-slate-800 mb-6">修改密码</h3>
                      
                      <div class="max-w-md space-y-4">
                          <!-- 错误提示 -->
                          <div v-if="passwordError" class="p-4 bg-rose-50 border border-rose-200 rounded-xl text-rose-600 text-sm">
                              <i class="fa-solid fa-circle-exclamation mr-2"></i>{{ passwordError }}
                          </div>
                          
                          <!-- 成功提示 -->
                          <div v-if="passwordSuccess" class="p-4 bg-green-50 border border-green-200 rounded-xl text-green-600 text-sm">
                              <i class="fa-solid fa-check-circle mr-2"></i>{{ passwordSuccess }}
                          </div>
                          
                          <div>
                              <label class="block text-sm font-medium text-gray-700 mb-2">当前密码</label>
                              <input v-model="passwordForm.oldPassword" type="password" 
                                  class="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"
                                  placeholder="请输入当前密码">
                          </div>
                          <div>
                              <label class="block text-sm font-medium text-gray-700 mb-2">新密码</label>
                              <input v-model="passwordForm.newPassword" type="password" 
                                  class="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"
                                  placeholder="请输入新密码（至少6位）">
                          </div>
                          <div>
                              <label class="block text-sm font-medium text-gray-700 mb-2">确认新密码</label>
                              <input v-model="passwordForm.confirmPassword" type="password" 
                                  class="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent"
                                  placeholder="请再次输入新密码">
                          </div>
                          <div class="pt-4">
                              <button @click="changePassword" :disabled="passwordLoading"
                                  class="px-6 py-3 bg-teal-500 text-white font-medium rounded-xl hover:bg-teal-600 transition disabled:opacity-50 disabled:cursor-not-allowed">
                                  <i v-if="passwordLoading" class="fa-solid fa-spinner fa-spin mr-2"></i>
                                  {{ passwordLoading ? '保存中...' : '确认修改' }}
                              </button>
                          </div>
                      </div>
                  </div>
                </template>

            </div>
        </div>

    </main>

    <!-- 头像裁剪器 -->
    <AvatarCropper 
      ref="cropperRef"
      v-model:visible="showCropper" 
      :imgUrl="cropperImgUrl" 
      @confirm="handleCropConfirm" 
    />
  </div>
</template>
