<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'

const router = useRouter()

// 当前选中的菜单
const activeMenu = ref('students')

// 管理员信息
const adminInfo = ref({
  name: '管理员',
  username: ''
})

// 学生数据
const students = ref([])
const studentLoading = ref(false)
const studentSearchKeyword = ref('')

// 医生数据
const doctors = ref([])
const doctorLoading = ref(false)
const doctorSearchKeyword = ref('')

// 公告数据
const announcements = ref([])
const announcementLoading = ref(false)
const showAnnouncementModal = ref(false)
const editingAnnouncement = ref(null)
const announcementForm = ref({
  title: '',
  content: '',
  status: 1
})

// 设置axios请求头
const getAdminToken = () => {
  return localStorage.getItem('admin_token')
}

// 加载管理员信息
const loadAdminInfo = () => {
  const userStr = localStorage.getItem('admin_user')
  if (userStr) {
    adminInfo.value = JSON.parse(userStr)
  }
}

// 加载学生列表
const loadStudents = async () => {
  studentLoading.value = true
  try {
    const url = studentSearchKeyword.value 
      ? `/api/admin/students/search?keyword=${studentSearchKeyword.value}`
      : '/api/admin/students'
    const response = await api.get(url, {
      headers: { Authorization: `Bearer ${getAdminToken()}` }
    })
    if (response.data.success) {
      students.value = response.data.body
    }
  } catch (error) {
    console.error('加载学生列表失败:', error)
  } finally {
    studentLoading.value = false
  }
}

// 搜索学生
const searchStudents = () => {
  loadStudents()
}

// 更新学生状态
const updateStudentStatus = async (id, status) => {
  try {
    await api.put(`/api/admin/students/${id}/status`, { status }, {
      headers: { Authorization: `Bearer ${getAdminToken()}` }
    })
    loadStudents()
  } catch (error) {
    console.error('更新学生状态失败:', error)
    alert('操作失败：' + (error.response?.data?.message || '未知错误'))
  }
}

// 删除学生
const deleteStudent = async (id) => {
  if (!confirm('确定要删除该学生吗？此操作不可恢复。')) return
  try {
    await api.delete(`/api/admin/students/${id}`, {
      headers: { Authorization: `Bearer ${getAdminToken()}` }
    })
    loadStudents()
  } catch (error) {
    console.error('删除学生失败:', error)
    alert('删除失败：' + (error.response?.data?.message || '未知错误'))
  }
}

// 加载医生列表
const loadDoctors = async () => {
  doctorLoading.value = true
  try {
    const url = doctorSearchKeyword.value 
      ? `/api/admin/doctors/search?keyword=${doctorSearchKeyword.value}`
      : '/api/admin/doctors'
    const response = await api.get(url, {
      headers: { Authorization: `Bearer ${getAdminToken()}` }
    })
    if (response.data.success) {
      doctors.value = response.data.body
    }
  } catch (error) {
    console.error('加载医生列表失败:', error)
  } finally {
    doctorLoading.value = false
  }
}

// 搜索医生
const searchDoctors = () => {
  loadDoctors()
}

// 更新医生状态
const updateDoctorStatus = async (id, status) => {
  try {
    await api.put(`/api/admin/doctors/${id}/status`, { status }, {
      headers: { Authorization: `Bearer ${getAdminToken()}` }
    })
    loadDoctors()
  } catch (error) {
    console.error('更新医生状态失败:', error)
    alert('操作失败：' + (error.response?.data?.message || '未知错误'))
  }
}

// 删除医生
const deleteDoctor = async (id) => {
  if (!confirm('确定要删除该医生吗？此操作不可恢复。')) return
  try {
    await api.delete(`/api/admin/doctors/${id}`, {
      headers: { Authorization: `Bearer ${getAdminToken()}` }
    })
    loadDoctors()
  } catch (error) {
    console.error('删除医生失败:', error)
    alert('删除失败：' + (error.response?.data?.message || '未知错误'))
  }
}

// 加载公告列表
const loadAnnouncements = async () => {
  announcementLoading.value = true
  try {
    const response = await api.get('/api/admin/announcements', {
      headers: { Authorization: `Bearer ${getAdminToken()}` }
    })
    if (response.data.success) {
      announcements.value = response.data.body
    }
  } catch (error) {
    console.error('加载公告列表失败:', error)
  } finally {
    announcementLoading.value = false
  }
}

// 打开新建公告弹窗
const openNewAnnouncement = () => {
  editingAnnouncement.value = null
  announcementForm.value = {
    title: '',
    content: '',
    status: 1
  }
  showAnnouncementModal.value = true
}

// 打开编辑公告弹窗
const openEditAnnouncement = (announcement) => {
  editingAnnouncement.value = announcement
  announcementForm.value = {
    title: announcement.title,
    content: announcement.content || '',
    status: announcement.status
  }
  showAnnouncementModal.value = true
}

// 保存公告
const saveAnnouncement = async () => {
  if (!announcementForm.value.title.trim()) {
    alert('请输入公告标题')
    return
  }
  
  try {
    if (editingAnnouncement.value) {
      // 更新
      await api.put(`/api/admin/announcements/${editingAnnouncement.value.id}`, announcementForm.value, {
        headers: { Authorization: `Bearer ${getAdminToken()}` }
      })
    } else {
      // 新建
      await api.post('/api/admin/announcements', announcementForm.value, {
        headers: { Authorization: `Bearer ${getAdminToken()}` }
      })
    }
    showAnnouncementModal.value = false
    loadAnnouncements()
  } catch (error) {
    console.error('保存公告失败:', error)
    alert('保存失败：' + (error.response?.data?.message || '未知错误'))
  }
}

// 更新公告状态
const updateAnnouncementStatus = async (id, status) => {
  try {
    await api.put(`/api/admin/announcements/${id}/status`, { status }, {
      headers: { Authorization: `Bearer ${getAdminToken()}` }
    })
    loadAnnouncements()
  } catch (error) {
    console.error('更新公告状态失败:', error)
    alert('操作失败：' + (error.response?.data?.message || '未知错误'))
  }
}

// 删除公告
const deleteAnnouncement = async (id) => {
  if (!confirm('确定要删除该公告吗？')) return
  try {
    await api.delete(`/api/admin/announcements/${id}`, {
      headers: { Authorization: `Bearer ${getAdminToken()}` }
    })
    loadAnnouncements()
  } catch (error) {
    console.error('删除公告失败:', error)
    alert('删除失败：' + (error.response?.data?.message || '未知错误'))
  }
}

// 切换菜单
const switchMenu = (menu) => {
  activeMenu.value = menu
  if (menu === 'students') {
    loadStudents()
  } else if (menu === 'doctors') {
    loadDoctors()
  } else if (menu === 'announcements') {
    loadAnnouncements()
  }
}

// 退出登录
const logout = () => {
  localStorage.removeItem('admin_token')
  localStorage.removeItem('admin_user')
  localStorage.removeItem('role')
  router.push('/admin/login')
}

// 获取状态标签样式
const getStudentStatusClass = (status) => {
  return status === 1 
    ? 'bg-green-100 text-green-800' 
    : 'bg-red-100 text-red-800'
}

const getDoctorStatusClass = (status) => {
  switch(status) {
    case 0: return 'bg-yellow-100 text-yellow-800'  // 待审核
    case 1: return 'bg-green-100 text-green-800'    // 正常
    case 2: return 'bg-red-100 text-red-800'        // 下架
    default: return 'bg-gray-100 text-gray-800'
  }
}

const getDoctorStatusText = (status) => {
  switch(status) {
    case 0: return '待审核'
    case 1: return '正常'
    case 2: return '已下架'
    default: return '未知'
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  // 检查登录状态
  const token = getAdminToken()
  if (!token) {
    router.push('/admin/login')
    return
  }
  
  loadAdminInfo()
  loadStudents() // 默认加载学生列表
})
</script>

<template>
  <div class="min-h-screen bg-gray-100 flex">
    <!-- 左侧边栏 -->
    <aside class="w-64 bg-gray-900 text-white flex flex-col">
      <!-- Logo -->
      <div class="h-16 flex items-center px-6 border-b border-gray-800">
        <div class="w-8 h-8 bg-purple-600 rounded-lg flex items-center justify-center mr-3">
          <i class="fa-solid fa-shield-halved"></i>
        </div>
        <span class="font-bold text-lg">管理后台</span>
      </div>
      
      <!-- 菜单 -->
      <nav class="flex-1 py-6 px-4">
        <ul class="space-y-2">
          <li>
            <button 
              @click="switchMenu('students')"
              :class="[
                'w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-colors',
                activeMenu === 'students' 
                  ? 'bg-purple-600 text-white' 
                  : 'text-gray-400 hover:bg-gray-800 hover:text-white'
              ]">
              <i class="fa-solid fa-user-graduate text-lg"></i>
              <span>学生管理</span>
            </button>
          </li>
          <li>
            <button 
              @click="switchMenu('doctors')"
              :class="[
                'w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-colors',
                activeMenu === 'doctors' 
                  ? 'bg-purple-600 text-white' 
                  : 'text-gray-400 hover:bg-gray-800 hover:text-white'
              ]">
              <i class="fa-solid fa-user-doctor text-lg"></i>
              <span>医生管理</span>
            </button>
          </li>
          <li>
            <button 
              @click="switchMenu('announcements')"
              :class="[
                'w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-colors',
                activeMenu === 'announcements' 
                  ? 'bg-purple-600 text-white' 
                  : 'text-gray-400 hover:bg-gray-800 hover:text-white'
              ]">
              <i class="fa-solid fa-bullhorn text-lg"></i>
              <span>公告管理</span>
            </button>
          </li>
        </ul>
      </nav>
      
      <!-- 底部用户信息 -->
      <div class="p-4 border-t border-gray-800">
        <div class="flex items-center gap-3 mb-3">
          <div class="w-10 h-10 rounded-full bg-purple-600 flex items-center justify-center">
            <i class="fa-solid fa-user"></i>
          </div>
          <div>
            <div class="text-sm font-medium">{{ adminInfo.name || adminInfo.username }}</div>
            <div class="text-xs text-gray-400">管理员</div>
          </div>
        </div>
        <button 
          @click="logout"
          class="w-full flex items-center justify-center gap-2 px-4 py-2 text-sm text-gray-400 hover:text-white hover:bg-gray-800 rounded-lg transition-colors">
          <i class="fa-solid fa-right-from-bracket"></i>
          退出登录
        </button>
      </div>
    </aside>

    <!-- 右侧主内容区 -->
    <main class="flex-1 overflow-auto">
      <!-- 顶部标题栏 -->
      <header class="h-16 bg-white border-b border-gray-200 flex items-center px-8">
        <h1 class="text-xl font-bold text-gray-800">
          <template v-if="activeMenu === 'students'">学生管理</template>
          <template v-else-if="activeMenu === 'doctors'">医生管理</template>
          <template v-else-if="activeMenu === 'announcements'">公告管理</template>
        </h1>
      </header>

      <!-- 内容区域 -->
      <div class="p-8">
        
        <!-- 学生管理 -->
        <div v-if="activeMenu === 'students'">
          <!-- 搜索栏 -->
          <div class="bg-white rounded-lg shadow-sm p-4 mb-6">
            <div class="flex gap-4">
              <input 
                v-model="studentSearchKeyword"
                type="text" 
                placeholder="搜索用户名、学号或邮箱..."
                class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                @keyup.enter="searchStudents">
              <button 
                @click="searchStudents"
                class="px-6 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition">
                <i class="fa-solid fa-search mr-2"></i>搜索
              </button>
            </div>
          </div>

          <!-- 学生列表 -->
          <div class="bg-white rounded-lg shadow-sm overflow-hidden">
            <table class="w-full">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">用户名</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">学号</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">邮箱</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">学院</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">状态</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">操作</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-200">
                <tr v-if="studentLoading">
                  <td colspan="7" class="px-6 py-8 text-center text-gray-500">
                    <i class="fa-solid fa-spinner fa-spin mr-2"></i>加载中...
                  </td>
                </tr>
                <tr v-else-if="students.length === 0">
                  <td colspan="7" class="px-6 py-8 text-center text-gray-500">暂无数据</td>
                </tr>
                <tr v-else v-for="student in students" :key="student.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 text-sm text-gray-900">{{ student.id }}</td>
                  <td class="px-6 py-4 text-sm text-gray-900">{{ student.name }}</td>
                  <td class="px-6 py-4 text-sm text-gray-500">{{ student.studentId }}</td>
                  <td class="px-6 py-4 text-sm text-gray-500">{{ student.email }}</td>
                  <td class="px-6 py-4 text-sm text-gray-500">{{ student.college || '-' }}</td>
                  <td class="px-6 py-4">
                    <span :class="['px-2 py-1 text-xs font-medium rounded-full', getStudentStatusClass(student.status)]">
                      {{ student.status === 1 ? '正常' : '禁用' }}
                    </span>
                  </td>
                  <td class="px-6 py-4 text-sm space-x-2">
                    <button 
                      v-if="student.status === 1"
                      @click="updateStudentStatus(student.id, 0)"
                      class="text-yellow-600 hover:text-yellow-800">
                      <i class="fa-solid fa-ban"></i> 禁用
                    </button>
                    <button 
                      v-else
                      @click="updateStudentStatus(student.id, 1)"
                      class="text-green-600 hover:text-green-800">
                      <i class="fa-solid fa-check"></i> 启用
                    </button>
                    <button 
                      @click="deleteStudent(student.id)"
                      class="text-red-600 hover:text-red-800 ml-2">
                      <i class="fa-solid fa-trash"></i> 删除
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 医生管理 -->
        <div v-if="activeMenu === 'doctors'">
          <!-- 搜索栏 -->
          <div class="bg-white rounded-lg shadow-sm p-4 mb-6">
            <div class="flex gap-4">
              <input 
                v-model="doctorSearchKeyword"
                type="text" 
                placeholder="搜索姓名、职称或专长..."
                class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                @keyup.enter="searchDoctors">
              <button 
                @click="searchDoctors"
                class="px-6 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition">
                <i class="fa-solid fa-search mr-2"></i>搜索
              </button>
            </div>
          </div>

          <!-- 医生列表 -->
          <div class="bg-white rounded-lg shadow-sm overflow-hidden">
            <table class="w-full">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">姓名</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">职称</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">手机号</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">从业年限</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">评分</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">状态</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">操作</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-200">
                <tr v-if="doctorLoading">
                  <td colspan="8" class="px-6 py-8 text-center text-gray-500">
                    <i class="fa-solid fa-spinner fa-spin mr-2"></i>加载中...
                  </td>
                </tr>
                <tr v-else-if="doctors.length === 0">
                  <td colspan="8" class="px-6 py-8 text-center text-gray-500">暂无数据</td>
                </tr>
                <tr v-else v-for="doctor in doctors" :key="doctor.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 text-sm text-gray-900">{{ doctor.id }}</td>
                  <td class="px-6 py-4 text-sm text-gray-900">{{ doctor.name }}</td>
                  <td class="px-6 py-4 text-sm text-gray-500">{{ doctor.title || '-' }}</td>
                  <td class="px-6 py-4 text-sm text-gray-500">{{ doctor.phone }}</td>
                  <td class="px-6 py-4 text-sm text-gray-500">{{ doctor.years }}年</td>
                  <td class="px-6 py-4 text-sm text-gray-500">
                    <span class="text-yellow-500">★</span> {{ doctor.rating }}
                  </td>
                  <td class="px-6 py-4">
                    <span :class="['px-2 py-1 text-xs font-medium rounded-full', getDoctorStatusClass(doctor.status)]">
                      {{ getDoctorStatusText(doctor.status) }}
                    </span>
                  </td>
                  <td class="px-6 py-4 text-sm space-x-2">
                    <button 
                      v-if="doctor.status === 0"
                      @click="updateDoctorStatus(doctor.id, 1)"
                      class="text-green-600 hover:text-green-800">
                      <i class="fa-solid fa-check"></i> 审核通过
                    </button>
                    <button 
                      v-if="doctor.status === 1"
                      @click="updateDoctorStatus(doctor.id, 2)"
                      class="text-yellow-600 hover:text-yellow-800">
                      <i class="fa-solid fa-ban"></i> 下架
                    </button>
                    <button 
                      v-if="doctor.status === 2"
                      @click="updateDoctorStatus(doctor.id, 1)"
                      class="text-green-600 hover:text-green-800">
                      <i class="fa-solid fa-arrow-up"></i> 上架
                    </button>
                    <button 
                      @click="deleteDoctor(doctor.id)"
                      class="text-red-600 hover:text-red-800 ml-2">
                      <i class="fa-solid fa-trash"></i> 删除
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 公告管理 -->
        <div v-if="activeMenu === 'announcements'">
          <!-- 操作栏 -->
          <div class="bg-white rounded-lg shadow-sm p-4 mb-6 flex justify-between items-center">
            <h3 class="text-lg font-medium text-gray-900">公告列表</h3>
            <button 
              @click="openNewAnnouncement"
              class="px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition">
              <i class="fa-solid fa-plus mr-2"></i>发布公告
            </button>
          </div>

          <!-- 公告列表 -->
          <div class="bg-white rounded-lg shadow-sm overflow-hidden">
            <table class="w-full">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">标题</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">发布日期</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">状态</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">操作</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-200">
                <tr v-if="announcementLoading">
                  <td colspan="5" class="px-6 py-8 text-center text-gray-500">
                    <i class="fa-solid fa-spinner fa-spin mr-2"></i>加载中...
                  </td>
                </tr>
                <tr v-else-if="announcements.length === 0">
                  <td colspan="5" class="px-6 py-8 text-center text-gray-500">暂无公告</td>
                </tr>
                <tr v-else v-for="announcement in announcements" :key="announcement.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 text-sm text-gray-900">{{ announcement.id }}</td>
                  <td class="px-6 py-4 text-sm text-gray-900">{{ announcement.title }}</td>
                  <td class="px-6 py-4 text-sm text-gray-500">{{ formatDate(announcement.date) }}</td>
                  <td class="px-6 py-4">
                    <span :class="[
                      'px-2 py-1 text-xs font-medium rounded-full',
                      announcement.status === 1 ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                    ]">
                      {{ announcement.status === 1 ? '已发布' : '已下架' }}
                    </span>
                  </td>
                  <td class="px-6 py-4 text-sm space-x-2">
                    <button 
                      @click="openEditAnnouncement(announcement)"
                      class="text-blue-600 hover:text-blue-800">
                      <i class="fa-solid fa-edit"></i> 编辑
                    </button>
                    <button 
                      v-if="announcement.status === 1"
                      @click="updateAnnouncementStatus(announcement.id, 0)"
                      class="text-yellow-600 hover:text-yellow-800">
                      <i class="fa-solid fa-eye-slash"></i> 下架
                    </button>
                    <button 
                      v-else
                      @click="updateAnnouncementStatus(announcement.id, 1)"
                      class="text-green-600 hover:text-green-800">
                      <i class="fa-solid fa-eye"></i> 发布
                    </button>
                    <button 
                      @click="deleteAnnouncement(announcement.id)"
                      class="text-red-600 hover:text-red-800">
                      <i class="fa-solid fa-trash"></i> 删除
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

      </div>
    </main>

    <!-- 公告编辑弹窗 -->
    <div v-if="showAnnouncementModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-xl w-full max-w-lg mx-4">
        <div class="flex items-center justify-between px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-medium text-gray-900">
            {{ editingAnnouncement ? '编辑公告' : '发布公告' }}
          </h3>
          <button @click="showAnnouncementModal = false" class="text-gray-400 hover:text-gray-500">
            <i class="fa-solid fa-times"></i>
          </button>
        </div>
        <div class="p-6 space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">公告标题</label>
            <input 
              v-model="announcementForm.title"
              type="text" 
              placeholder="请输入公告标题"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500">
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">公告内容</label>
            <textarea 
              v-model="announcementForm.content"
              rows="5"
              placeholder="请输入公告内容（可选）"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 resize-none"></textarea>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">状态</label>
            <select 
              v-model="announcementForm.status"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500">
              <option :value="1">立即发布</option>
              <option :value="0">保存为草稿</option>
            </select>
          </div>
        </div>
        <div class="flex justify-end gap-3 px-6 py-4 border-t border-gray-200 bg-gray-50 rounded-b-lg">
          <button 
            @click="showAnnouncementModal = false"
            class="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition">
            取消
          </button>
          <button 
            @click="saveAnnouncement"
            class="px-4 py-2 text-white bg-purple-600 rounded-lg hover:bg-purple-700 transition">
            {{ editingAnnouncement ? '保存修改' : '发布公告' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
