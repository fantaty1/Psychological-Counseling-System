<script setup>
import { ref, onMounted, computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import api, { getAvatarUrl, getUser, setUser, removeToken, removeUser } from '@/api'
import AvatarCropper from '@/components/AvatarCropper.vue'

const router = useRouter()

// 医生信息
const doctorInfo = ref({
  name: '',
  avatar: '',
  title: '',
  phone: '',
  email: '',
  location: '',
  description: '',
  tags: [],
  methods: [],
  certifications: [],
  years: 0
})

// 编辑状态
const isEditing = ref(false)
const editForm = ref({})

// 加载状态
const loading = ref(true)
const saving = ref(false)

// 头像上传相关
const avatarUploading = ref(false)
const avatarInput = ref(null)

// 头像裁剪相关
const showCropper = ref(false)
const cropperImgUrl = ref('')
const cropperRef = ref(null)

// 计算属性：获取正确的头像URL
const avatarUrl = computed(() => {
  return getAvatarUrl(doctorInfo.value.avatar) || 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?ixlib=rb-4.0.3&auto=format&fit=crop&w=200&q=80'
})

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

// 加载医生信息
const loadDoctorInfo = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/doctor/profile')
    if (response.data.success) {
      doctorInfo.value = response.data.body
      editForm.value = { ...response.data.body }
    }
  } catch (error) {
    console.error('加载医生信息失败:', error)
  } finally {
    loading.value = false
  }
}

// 触发头像选择
const triggerAvatarUpload = () => {
  avatarInput.value?.click()
}

// 选择文件后打开裁剪器
const handleFileSelect = (event) => {
  const file = event.target.files?.[0]
  if (!file) return
  
  // 验证文件类型
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    alert('只支持 JPG、PNG、GIF、WEBP 格式的图片')
    return
  }
  
  // 验证文件大小 (5MB)
  if (file.size > 5 * 1024 * 1024) {
    alert('图片大小不能超过5MB')
    return
  }
  
  // 读取文件并打开裁剪器
  const reader = new FileReader()
  reader.onload = (e) => {
    cropperImgUrl.value = e.target.result
    showCropper.value = true
  }
  reader.readAsDataURL(file)
  
  // 清空input以便重复选择同一文件
  event.target.value = ''
}

// 裁剪确认后上传
const handleCropConfirm = async (croppedFile) => {
  try {
    avatarUploading.value = true
    showCropper.value = false
    
    const formData = new FormData()
    formData.append('file', croppedFile)
    
    const response = await api.post('/api/file/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    
    if (response.data.success) {
      doctorInfo.value.avatar = response.data.body.url
      // 更新localStorage中的用户信息
      const storedUser = getUser() || {}
      storedUser.avatar = response.data.body.url
      setUser(storedUser)
      alert('头像上传成功！')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    alert(error.response?.data?.message || '头像上传失败，请稍后重试')
  } finally {
    avatarUploading.value = false
  }
}

// 开始编辑
const startEdit = () => {
  editForm.value = { ...doctorInfo.value }
  isEditing.value = true
}

// 取消编辑
const cancelEdit = () => {
  editForm.value = { ...doctorInfo.value }
  isEditing.value = false
}

// 保存修改
const saveProfile = async () => {
  saving.value = true
  try {
    const response = await api.put('/api/doctor/profile', {
      name: editForm.value.name,
      title: editForm.value.title,
      phone: editForm.value.phone,
      email: editForm.value.email,
      location: editForm.value.location,
      description: editForm.value.description,
      tags: editForm.value.tags,
      methods: editForm.value.methods,
      years: editForm.value.years
    })
    if (response.data.success) {
      doctorInfo.value = { ...editForm.value }
      isEditing.value = false
    }
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 切换标签选择
const toggleTag = (tag) => {
  const index = editForm.value.tags?.indexOf(tag) ?? -1
  if (index >= 0) {
    editForm.value.tags.splice(index, 1)
  } else {
    if (!editForm.value.tags) editForm.value.tags = []
    editForm.value.tags.push(tag)
  }
}

// 切换方法选择
const toggleMethod = (method) => {
  const index = editForm.value.methods?.indexOf(method) ?? -1
  if (index >= 0) {
    editForm.value.methods.splice(index, 1)
  } else {
    if (!editForm.value.methods) editForm.value.methods = []
    editForm.value.methods.push(method)
  }
}

// 退出登录
const handleLogout = () => {
  removeToken()
  removeUser()
  localStorage.removeItem('role')
  router.push('/login')
}

onMounted(() => {
  loadDoctorInfo()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部导航 -->
    <nav class="bg-white shadow-sm border-b border-gray-100 sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center gap-8">
            <div class="flex-shrink-0 flex items-center gap-2">
              <div class="w-8 h-8 bg-indigo-500 rounded-lg flex items-center justify-center text-white">
                <i class="fa-solid fa-user-doctor"></i>
              </div>
              <span class="font-bold text-xl text-slate-800 tracking-tight">心语空间 · 咨询师端</span>
            </div>
            <div class="hidden md:flex space-x-6">
              <RouterLink to="/doctor/home" class="text-gray-500 hover:text-indigo-600 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">工作台</RouterLink>
              <RouterLink to="/doctor/appointments" class="text-gray-500 hover:text-indigo-600 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">预约管理</RouterLink>
              <RouterLink to="/doctor/chat" class="text-gray-500 hover:text-indigo-600 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">咨询对话</RouterLink>
              <RouterLink to="/doctor/schedule" class="text-gray-500 hover:text-indigo-600 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">排班设置</RouterLink>
            </div>
          </div>
          <div class="flex items-center gap-4">
            <div class="relative group">
              <div class="flex items-center gap-2 cursor-pointer hover:bg-gray-50 p-1 rounded-lg transition">
                <div class="h-8 w-8 rounded-full bg-gradient-to-br from-indigo-400 to-purple-500 flex items-center justify-center text-white font-bold text-sm">
                  {{ doctorInfo.name?.charAt(0) || 'D' }}
                </div>
                <span class="text-sm font-medium text-gray-700">{{ doctorInfo.name || '加载中...' }}</span>
              </div>
              <div class="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-lg border border-gray-100 py-2 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200">
                <button @click="handleLogout" class="w-full text-left px-4 py-2 text-sm text-rose-600 hover:bg-rose-50">
                  <i class="fa-solid fa-right-from-bracket mr-2"></i>退出登录
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主要内容 -->
    <main class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      
      <div v-if="loading" class="text-center py-12">
        <i class="fa-solid fa-spinner fa-spin text-3xl text-indigo-600"></i>
        <p class="text-gray-500 mt-3">加载中...</p>
      </div>

      <template v-else>
        <!-- 头部卡片 -->
        <div class="bg-gradient-to-r from-indigo-500 to-purple-600 rounded-2xl p-8 text-white mb-8 shadow-lg">
          <div class="flex items-center gap-6">
            <!-- 头像上传 -->
            <div class="relative group cursor-pointer" @click="triggerAvatarUpload">
              <div class="w-24 h-24 rounded-full bg-white/20 flex items-center justify-center text-4xl font-bold border-4 border-white/30 overflow-hidden">
                <img v-if="doctorInfo.avatar" :src="avatarUrl" :alt="doctorInfo.name" class="w-full h-full object-cover">
                <span v-else>{{ doctorInfo.name?.charAt(0) || 'D' }}</span>
              </div>
              <!-- 上传遮罩 -->
              <div class="absolute inset-0 bg-black bg-opacity-40 rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition">
                <i v-if="avatarUploading" class="fa-solid fa-spinner fa-spin text-white text-xl"></i>
                <i v-else class="fa-solid fa-camera text-white text-xl"></i>
              </div>
              <input ref="avatarInput" type="file" accept="image/*" class="hidden" @change="handleFileSelect">
            </div>
            <div>
              <h1 class="text-3xl font-bold">{{ doctorInfo.name }}</h1>
              <p class="text-white/80 mt-1">{{ doctorInfo.title }}</p>
              <div class="flex items-center gap-4 mt-3">
                <span class="flex items-center gap-1 text-white/70 text-sm">
                  <i class="fa-solid fa-briefcase"></i>
                  {{ doctorInfo.years }}年经验
                </span>
                <span class="flex items-center gap-1 text-white/70 text-sm">
                  <i class="fa-solid fa-star"></i>
                  {{ doctorInfo.rating || 5.0 }}分
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 基本信息 -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden mb-6">
          <div class="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
            <h2 class="text-lg font-bold text-slate-800">基本信息</h2>
            <button v-if="!isEditing" @click="startEdit" class="px-4 py-2 text-indigo-600 hover:bg-indigo-50 rounded-lg transition text-sm font-medium">
              <i class="fa-solid fa-pen mr-2"></i>编辑
            </button>
            <div v-else class="flex gap-2">
              <button @click="cancelEdit" class="px-4 py-2 text-gray-600 hover:bg-gray-50 rounded-lg transition text-sm">
                取消
              </button>
              <button @click="saveProfile" :disabled="saving" class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition text-sm disabled:opacity-50">
                <i v-if="saving" class="fa-solid fa-spinner fa-spin mr-2"></i>
                保存
              </button>
            </div>
          </div>
          
          <div class="p-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label class="block text-sm font-medium text-gray-500 mb-2">姓名</label>
                <input v-if="isEditing" v-model="editForm.name" type="text" 
                  class="w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400">
                <p v-else class="text-slate-800 font-medium">{{ doctorInfo.name }}</p>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-500 mb-2">职称</label>
                <input v-if="isEditing" v-model="editForm.title" type="text" 
                  class="w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400">
                <p v-else class="text-slate-800 font-medium">{{ doctorInfo.title }}</p>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-500 mb-2">从业年限</label>
                <input v-if="isEditing" v-model.number="editForm.years" type="number" min="0"
                  class="w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400">
                <p v-else class="text-slate-800 font-medium">{{ doctorInfo.years }}年</p>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-500 mb-2">联系电话</label>
                <input v-if="isEditing" v-model="editForm.phone" type="tel" 
                  class="w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400">
                <p v-else class="text-slate-800 font-medium">{{ doctorInfo.phone || '未设置' }}</p>
              </div>
              
              <div class="md:col-span-2">
                <label class="block text-sm font-medium text-gray-500 mb-2">邮箱</label>
                <input v-if="isEditing" v-model="editForm.email" type="email" 
                  class="w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400">
                <p v-else class="text-slate-800 font-medium">{{ doctorInfo.email || '未设置' }}</p>
              </div>
              
              <div class="md:col-span-2">
                <label class="block text-sm font-medium text-gray-500 mb-2">诊室位置</label>
                <input v-if="isEditing" v-model="editForm.location" type="text" 
                  placeholder="如：心理健康教育中心 203 室"
                  class="w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400">
                <p v-else class="text-slate-800 font-medium">{{ doctorInfo.location || '未设置' }}</p>
              </div>
              
              <div class="md:col-span-2">
                <label class="block text-sm font-medium text-gray-500 mb-2">个人简介</label>
                <textarea v-if="isEditing" v-model="editForm.description" rows="4"
                  class="w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400 resize-none"></textarea>
                <p v-else class="text-slate-800">{{ doctorInfo.description || '暂无简介' }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 擅长领域 -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden mb-6">
          <div class="px-6 py-4 border-b border-gray-100">
            <h2 class="text-lg font-bold text-slate-800">擅长领域</h2>
          </div>
          <div class="p-6">
            <div v-if="isEditing" class="flex flex-wrap gap-2">
              <button v-for="tag in availableTags" :key="tag"
                @click="toggleTag(tag)"
                :class="[
                  'px-4 py-2 rounded-full text-sm font-medium transition',
                  editForm.tags?.includes(tag)
                    ? 'bg-indigo-600 text-white'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                ]">
                {{ tag }}
              </button>
            </div>
            <div v-else class="flex flex-wrap gap-2">
              <span v-for="tag in doctorInfo.tags" :key="tag" 
                class="px-4 py-2 bg-indigo-100 text-indigo-700 rounded-full text-sm font-medium">
                {{ tag }}
              </span>
              <span v-if="!doctorInfo.tags?.length" class="text-gray-400">暂未设置</span>
            </div>
          </div>
        </div>

        <!-- 咨询方法 -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden mb-6">
          <div class="px-6 py-4 border-b border-gray-100">
            <h2 class="text-lg font-bold text-slate-800">咨询方法</h2>
          </div>
          <div class="p-6">
            <div v-if="isEditing" class="flex flex-wrap gap-2">
              <button v-for="method in availableMethods" :key="method"
                @click="toggleMethod(method)"
                :class="[
                  'px-4 py-2 rounded-full text-sm font-medium transition',
                  editForm.methods?.includes(method)
                    ? 'bg-purple-600 text-white'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                ]">
                {{ method }}
              </button>
            </div>
            <div v-else class="flex flex-wrap gap-2">
              <span v-for="method in doctorInfo.methods" :key="method" 
                class="px-4 py-2 bg-purple-100 text-purple-700 rounded-full text-sm font-medium">
                {{ method }}
              </span>
              <span v-if="!doctorInfo.methods?.length" class="text-gray-400">暂未设置</span>
            </div>
          </div>
        </div>

        <!-- 资质认证 -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          <div class="px-6 py-4 border-b border-gray-100">
            <h2 class="text-lg font-bold text-slate-800">资质认证</h2>
          </div>
          <div class="p-6">
            <ul v-if="doctorInfo.certifications?.length" class="space-y-3">
              <li v-for="cert in doctorInfo.certifications" :key="cert" 
                class="flex items-center gap-3 text-slate-700">
                <i class="fa-solid fa-certificate text-amber-500"></i>
                {{ cert }}
              </li>
            </ul>
            <p v-else class="text-gray-400">暂无认证信息</p>
          </div>
        </div>

      </template>
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
