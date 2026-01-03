<script setup>
import { ref, onMounted, computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import api, { removeToken, removeUser } from '@/api'

const router = useRouter()

// 医生信息
const doctorInfo = ref({
  name: '',
  avatar: ''
})

// 预约列表
const appointments = ref([])

// 筛选条件
const filters = ref({
  status: 'all',
  date: '',
  keyword: ''
})

// 分页
const pagination = ref({
  page: 1,
  pageSize: 10,
  total: 0
})

// 加载状态
const loading = ref(false)

// 当前选中的预约（详情弹窗）
const selectedAppointment = ref(null)
const showDetailModal = ref(false)

// 状态选项
const statusOptions = [
  { value: 'all', label: '全部状态' },
  { value: 'pending', label: '待确认' },
  { value: 'confirmed', label: '已确认' },
  { value: 'completed', label: '已完成' },
  { value: 'cancelled', label: '已取消' }
]

// 筛选后的预约列表
const filteredAppointments = computed(() => {
  return appointments.value
})

// 加载医生信息
const loadDoctorInfo = async () => {
  try {
    const response = await api.get('/api/doctor/profile')
    if (response.data.success) {
      doctorInfo.value = response.data.body
    }
  } catch (error) {
    console.error('加载医生信息失败:', error)
  }
}

// 加载预约列表
const loadAppointments = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.value.page,
      pageSize: pagination.value.pageSize,
      status: filters.value.status !== 'all' ? filters.value.status : undefined,
      date: filters.value.date || undefined,
      keyword: filters.value.keyword || undefined
    }
    const response = await api.get('/api/doctor/appointments', { params })
    if (response.data.success) {
      appointments.value = response.data.body.list || []
      pagination.value.total = response.data.body.total || 0
    }
  } catch (error) {
    console.error('加载预约失败:', error)
  } finally {
    loading.value = false
  }
}

// 确认预约
const confirmAppointment = async (id) => {
  try {
    await api.put(`/api/doctor/appointments/${id}/confirm`)
    await loadAppointments()
  } catch (error) {
    console.error('确认预约失败:', error)
  }
}

// 拒绝预约
const rejectAppointment = async (id) => {
  const reason = prompt('请输入拒绝原因（可选）：')
  if (reason === null) return // 用户点击取消
  
  try {
    await api.put(`/api/doctor/appointments/${id}/reject`, {
      reason: reason || '医生拒绝了此预约'
    })
    alert('预约已拒绝')
    await loadAppointments()
  } catch (error) {
    console.error('拒绝预约失败:', error)
    alert('拒绝失败：' + (error.response?.data?.message || '请稍后重试'))
  }
}

// 完成预约
const completeAppointment = async (id) => {
  try {
    await api.put(`/api/doctor/appointments/${id}/complete`)
    await loadAppointments()
  } catch (error) {
    console.error('完成预约失败:', error)
  }
}

// 查看详情
const viewDetail = (appointment) => {
  selectedAppointment.value = appointment
  showDetailModal.value = true
}

// 开始咨询
const startChat = (appointment) => {
  router.push(`/doctor/chat/${appointment.sessionId || appointment.id}`)
}

// 退出登录
const handleLogout = () => {
  removeToken()
  removeUser()
  localStorage.removeItem('role')
  router.push('/login')
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', { month: 'long', day: 'numeric', weekday: 'short' })
}

// 获取状态样式
const getStatusStyle = (status) => {
  const styles = {
    pending: 'bg-amber-100 text-amber-700',
    confirmed: 'bg-green-100 text-green-700',
    completed: 'bg-gray-100 text-gray-600',
    cancelled: 'bg-rose-100 text-rose-700'
  }
  return styles[status] || 'bg-gray-100 text-gray-600'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    pending: '待确认',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return texts[status] || status
}

// 搜索
const handleSearch = () => {
  pagination.value.page = 1
  loadAppointments()
}

// 换页
const changePage = (page) => {
  pagination.value.page = page
  loadAppointments()
}

onMounted(async () => {
  await loadDoctorInfo()
  await loadAppointments()
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
              <RouterLink to="/doctor/home" class="text-gray-500 hover:text-indigo-600 hover:border-indigo-300 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">工作台</RouterLink>
              <RouterLink to="/doctor/appointments" class="text-indigo-600 border-b-2 border-indigo-600 px-1 pt-1 text-sm font-medium h-full flex items-center">预约管理</RouterLink>
              <RouterLink to="/doctor/chat" class="text-gray-500 hover:text-indigo-600 hover:border-indigo-300 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">咨询对话</RouterLink>
              <RouterLink to="/doctor/schedule" class="text-gray-500 hover:text-indigo-600 hover:border-indigo-300 border-b-2 border-transparent px-1 pt-1 text-sm font-medium h-full flex items-center transition">排班设置</RouterLink>
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
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主要内容 -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      
      <!-- 页面标题 -->
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-slate-800">预约管理</h1>
        <p class="text-gray-500 mt-1">管理学生的咨询预约申请</p>
      </div>

      <!-- 筛选栏 -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 mb-6">
        <div class="flex flex-wrap gap-4 items-center">
          <div class="flex-1 min-w-[200px]">
            <input 
              v-model="filters.keyword"
              type="text" 
              placeholder="搜索学生姓名..."
              class="w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400"
              @keyup.enter="handleSearch">
          </div>
          <div>
            <select 
              v-model="filters.status"
              @change="handleSearch"
              class="px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400">
              <option v-for="option in statusOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </div>
          <div>
            <input 
              v-model="filters.date"
              type="date"
              @change="handleSearch"
              class="px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-100 focus:border-indigo-400">
          </div>
          <button 
            @click="handleSearch"
            class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition">
            <i class="fa-solid fa-search mr-2"></i>搜索
          </button>
        </div>
      </div>

      <!-- 预约列表 -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <!-- 表头 -->
        <div class="hidden md:grid grid-cols-12 gap-4 px-6 py-4 bg-gray-50 border-b border-gray-100 text-sm font-medium text-gray-500">
          <div class="col-span-3">学生信息</div>
          <div class="col-span-2">预约时间</div>
          <div class="col-span-2">咨询方式</div>
          <div class="col-span-2">状态</div>
          <div class="col-span-3 text-right">操作</div>
        </div>

        <!-- 加载中 -->
        <div v-if="loading" class="p-12 text-center">
          <i class="fa-solid fa-spinner fa-spin text-3xl text-indigo-600"></i>
          <p class="text-gray-500 mt-3">加载中...</p>
        </div>

        <!-- 空状态 -->
        <div v-else-if="appointments.length === 0" class="p-12 text-center">
          <i class="fa-regular fa-calendar-xmark text-5xl text-gray-300 mb-4"></i>
          <p class="text-gray-400">暂无预约记录</p>
        </div>

        <!-- 预约列表 -->
        <div v-else class="divide-y divide-gray-50">
          <div v-for="appointment in appointments" :key="appointment.id" 
            class="grid grid-cols-1 md:grid-cols-12 gap-4 px-6 py-4 hover:bg-gray-50 transition items-center">
            
            <!-- 学生信息 -->
            <div class="md:col-span-3 flex items-center gap-3">
              <div class="w-10 h-10 rounded-full bg-gradient-to-br from-teal-400 to-cyan-500 flex items-center justify-center text-white font-bold">
                {{ appointment.studentName?.charAt(0) || 'S' }}
              </div>
              <div>
                <h3 class="font-medium text-slate-800">{{ appointment.studentName }}</h3>
                <p class="text-sm text-gray-500">{{ appointment.studentCollege }}</p>
              </div>
            </div>

            <!-- 预约时间 -->
            <div class="md:col-span-2">
              <p class="font-medium text-slate-800">{{ formatDate(appointment.date) }}</p>
              <p class="text-sm text-gray-500">{{ appointment.timeSlot }}</p>
            </div>

            <!-- 咨询方式 -->
            <div class="md:col-span-2">
              <span :class="[
                'inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium',
                appointment.type === 'online' ? 'bg-indigo-100 text-indigo-700' : 'bg-teal-100 text-teal-700'
              ]">
                <i :class="appointment.type === 'online' ? 'fa-solid fa-video' : 'fa-solid fa-user'"></i>
                {{ appointment.type === 'online' ? '线上咨询' : '线下面谈' }}
              </span>
            </div>

            <!-- 状态 -->
            <div class="md:col-span-2">
              <span :class="['px-3 py-1 rounded-full text-xs font-medium', getStatusStyle(appointment.status)]">
                {{ getStatusText(appointment.status) }}
              </span>
            </div>

            <!-- 操作 -->
            <div class="md:col-span-3 flex items-center justify-end gap-2">
              <button @click="viewDetail(appointment)" 
                class="p-2 text-gray-500 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition" title="查看详情">
                <i class="fa-solid fa-eye"></i>
              </button>
              
              <template v-if="appointment.status === 'pending'">
                <button @click="confirmAppointment(appointment.id)" 
                  class="px-3 py-1.5 bg-green-600 text-white text-sm rounded-lg hover:bg-green-700 transition">
                  确认
                </button>
                <button @click="rejectAppointment(appointment.id)" 
                  class="px-3 py-1.5 bg-rose-600 text-white text-sm rounded-lg hover:bg-rose-700 transition">
                  拒绝
                </button>
              </template>
              
              <template v-else-if="appointment.status === 'confirmed'">
                <!-- 只有在线咨询才显示开始咨询按钮 -->
                <button v-if="appointment.type === 'online'" @click="startChat(appointment)" 
                  class="px-3 py-1.5 bg-indigo-600 text-white text-sm rounded-lg hover:bg-indigo-700 transition">
                  <i class="fa-solid fa-comments mr-1"></i>开始咨询
                </button>
                <button @click="completeAppointment(appointment.id)" 
                  class="px-3 py-1.5 bg-gray-600 text-white text-sm rounded-lg hover:bg-gray-700 transition">
                  完成
                </button>
              </template>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="pagination.total > pagination.pageSize" class="px-6 py-4 border-t border-gray-100 flex items-center justify-between">
          <p class="text-sm text-gray-500">
            共 {{ pagination.total }} 条记录
          </p>
          <div class="flex gap-2">
            <button 
              :disabled="pagination.page === 1"
              @click="changePage(pagination.page - 1)"
              class="px-3 py-1 border border-gray-200 rounded-lg text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50">
              上一页
            </button>
            <button 
              :disabled="pagination.page * pagination.pageSize >= pagination.total"
              @click="changePage(pagination.page + 1)"
              class="px-3 py-1 border border-gray-200 rounded-lg text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50">
              下一页
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- 详情弹窗 -->
    <div v-if="showDetailModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4" @click.self="showDetailModal = false">
      <div class="bg-white rounded-2xl shadow-2xl max-w-lg w-full overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
          <h3 class="text-lg font-bold text-slate-800">预约详情</h3>
          <button @click="showDetailModal = false" class="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-lg transition">
            <i class="fa-solid fa-times"></i>
          </button>
        </div>
        
        <div v-if="selectedAppointment" class="p-6 space-y-4">
          <div class="flex items-center gap-4">
            <div class="w-16 h-16 rounded-full bg-gradient-to-br from-teal-400 to-cyan-500 flex items-center justify-center text-white font-bold text-xl">
              {{ selectedAppointment.studentName?.charAt(0) || 'S' }}
            </div>
            <div>
              <h4 class="text-xl font-bold text-slate-800">{{ selectedAppointment.studentName }}</h4>
              <p class="text-gray-500">{{ selectedAppointment.studentCollege }}</p>
            </div>
          </div>
          
          <div class="grid grid-cols-2 gap-4">
            <div class="bg-gray-50 rounded-xl p-4">
              <p class="text-sm text-gray-500 mb-1">预约日期</p>
              <p class="font-medium text-slate-800">{{ formatDate(selectedAppointment.date) }}</p>
            </div>
            <div class="bg-gray-50 rounded-xl p-4">
              <p class="text-sm text-gray-500 mb-1">时间段</p>
              <p class="font-medium text-slate-800">{{ selectedAppointment.timeSlot }}</p>
            </div>
            <div class="bg-gray-50 rounded-xl p-4">
              <p class="text-sm text-gray-500 mb-1">咨询方式</p>
              <p class="font-medium text-slate-800">{{ selectedAppointment.type === 'online' ? '线上咨询' : '线下面谈' }}</p>
            </div>
            <div class="bg-gray-50 rounded-xl p-4">
              <p class="text-sm text-gray-500 mb-1">状态</p>
              <span :class="['px-3 py-1 rounded-full text-xs font-medium', getStatusStyle(selectedAppointment.status)]">
                {{ getStatusText(selectedAppointment.status) }}
              </span>
            </div>
          </div>
          
          <div v-if="selectedAppointment.note" class="bg-amber-50 rounded-xl p-4">
            <p class="text-sm text-amber-700 font-medium mb-1">预约备注</p>
            <p class="text-amber-800">{{ selectedAppointment.note }}</p>
          </div>
        </div>
        
        <div class="px-6 py-4 border-t border-gray-100 flex justify-end gap-3">
          <button @click="showDetailModal = false" class="px-4 py-2 border border-gray-200 rounded-lg text-gray-600 hover:bg-gray-50 transition">
            关闭
          </button>
          <!-- 只有在线咨询才显示开始咨询按钮 -->
          <button v-if="selectedAppointment?.status === 'confirmed' && selectedAppointment?.type === 'online'" 
            @click="startChat(selectedAppointment); showDetailModal = false" 
            class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition">
            开始咨询
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
