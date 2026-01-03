<script setup>
import { ref, onMounted, computed } from 'vue'
import { RouterLink } from 'vue-router'
import api, { getAvatarUrl } from '@/api'

// 医生列表数据
const doctors = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const selectedTag = ref('')

// 获取医生列表
const fetchDoctors = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/user/doctors')
    if (response.data.success) {
      doctors.value = response.data.body
    }
  } catch (error) {
    console.error('获取医生列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索医生
const searchDoctors = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/user/doctors/search', {
      params: { keyword: searchKeyword.value }
    })
    if (response.data.success) {
      doctors.value = response.data.body
    }
  } catch (error) {
    console.error('搜索医生失败:', error)
  } finally {
    loading.value = false
  }
}

// 筛选后的医生列表
const filteredDoctors = computed(() => {
  if (!selectedTag.value) {
    return doctors.value
  }
  return doctors.value.filter(doctor => 
    doctor.tags && doctor.tags.includes(selectedTag.value)
  )
})

// 选择标签
const selectTag = (tag) => {
  selectedTag.value = selectedTag.value === tag ? '' : tag
}

// 格式化评分
const formatRating = (rating) => {
  return rating ? Number(rating).toFixed(1) : '5.0'
}

// 获取医生头像URL
const getDoctorAvatar = (avatar) => {
  return getAvatarUrl(avatar) || 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?ixlib=rb-4.0.3&auto=format&fit=crop&w=200&q=80'
}

onMounted(() => {
  fetchDoctors()
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
                    <h1 class="font-bold text-xl text-slate-800">预约咨询师</h1>
                </div>
                <div class="flex items-center gap-4">
                    <RouterLink to="/appointments" class="text-sm font-medium text-teal-600 hover:text-teal-700">我的预约记录</RouterLink>
                </div>
            </div>
        </div>
    </nav>

    <main class="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
        
        <!-- 筛选与搜索 -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-8">
            <div class="flex flex-col md:flex-row gap-6 justify-between items-start md:items-center">
                <!-- 搜索框 -->
                <div class="relative w-full md:w-96">
                    <i class="fa-solid fa-magnifying-glass absolute left-4 top-1/2 -translate-y-1/2 text-gray-400"></i>
                    <input 
                      v-model="searchKeyword"
                      @keyup.enter="searchDoctors"
                      type="text" 
                      placeholder="搜索咨询师姓名、擅长领域..." 
                      class="w-full bg-gray-50 border border-gray-200 rounded-xl py-3 pl-11 pr-4 text-sm outline-none focus:border-teal-500 focus:ring-1 focus:ring-teal-500 transition">
                </div>

                <!-- 排序 -->
                <div class="flex items-center gap-2">
                    <span class="text-sm text-gray-500">排序：</span>
                    <select class="bg-gray-50 border border-gray-200 rounded-lg px-3 py-2 text-sm outline-none focus:border-teal-500">
                        <option>综合推荐</option>
                        <option>评分最高</option>
                        <option>最早可约</option>
                    </select>
                </div>
            </div>

            <!-- 标签筛选 -->
            <div class="mt-6 flex flex-wrap gap-2">
                <span class="text-sm text-gray-500 py-1.5 mr-2">擅长领域：</span>
                <button 
                  @click="selectTag('')"
                  :class="[
                    'px-4 py-1.5 text-sm font-medium rounded-full shadow-sm transition',
                    !selectedTag ? 'bg-teal-500 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                  ]">全部</button>
                <button 
                  v-for="tag in ['焦虑抑郁', '情绪压力', '人际关系', '学业发展', '恋爱婚姻', '危机干预']"
                  :key="tag"
                  @click="selectTag(tag)"
                  :class="[
                    'px-4 py-1.5 text-sm font-medium rounded-full transition',
                    selectedTag === tag ? 'bg-teal-500 text-white shadow-sm' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                  ]">{{ tag }}</button>
            </div>
        </div>

        <!-- 医生列表 Grid -->
        <div v-if="loading" class="text-center py-12">
            <i class="fa-solid fa-spinner fa-spin text-4xl text-teal-500"></i>
            <p class="text-gray-500 mt-4">加载中...</p>
        </div>

        <div v-else-if="filteredDoctors.length === 0" class="text-center py-12">
            <i class="fa-solid fa-user-doctor text-6xl text-gray-300 mb-4"></i>
            <p class="text-gray-500">暂无咨询师</p>
        </div>

        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <!-- 医生卡片 -->
            <div 
              v-for="doctor in filteredDoctors" 
              :key="doctor.id"
              class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition group flex flex-col">
                <div class="p-6 flex gap-4">
                    <div class="relative flex-shrink-0">
                        <img 
                          :src="getDoctorAvatar(doctor.avatar)" 
                          :alt="doctor.name" 
                          class="w-24 h-24 rounded-xl object-cover"
                          :class="{ 'grayscale': !doctor.available }">
                        <div class="absolute -bottom-3 left-1/2 -translate-x-1/2 bg-white px-2 py-1 rounded-full shadow-sm border border-gray-100 flex items-center gap-1 whitespace-nowrap">
                            <i class="fa-solid fa-star text-yellow-400 text-xs"></i>
                            <span class="text-xs font-bold">{{ formatRating(doctor.rating) }}</span>
                        </div>
                    </div>
                    <div class="flex-1 min-w-0">
                        <div class="flex justify-between items-start mb-1">
                            <h3 class="font-bold text-lg text-slate-800 truncate">{{ doctor.name }}</h3>
                            <span 
                              :class="[
                                'text-xs font-medium px-2 py-1 rounded-lg flex-shrink-0',
                                doctor.available ? 'text-teal-600 bg-teal-50' : 'text-gray-400 bg-gray-100'
                              ]">{{ doctor.available ? '可预约' : '已约满' }}</span>
                        </div>
                        <p class="text-xs text-gray-500 mb-3">{{ doctor.title || '心理咨询师' }} | 从业 {{ doctor.years || 0 }} 年</p>
                        <div class="flex flex-wrap gap-1.5">
                            <span 
                              v-for="(tag, index) in (doctor.tags || []).slice(0, 2)" 
                              :key="index"
                              class="text-[10px] text-gray-500 bg-gray-100 px-2 py-1 rounded">{{ tag }}</span>
                        </div>
                    </div>
                </div>
                <div class="px-6 pb-6 mt-auto">
                    <div class="flex items-center justify-between pt-4 border-t border-gray-50">
                        <span class="text-xs text-gray-400 flex items-center gap-1">
                            <i class="fa-regular fa-clock"></i> 预约咨询
                        </span>
                        <div class="flex gap-2">
                            <RouterLink 
                              :to="`/chat?doctorId=${doctor.id}`" 
                              class="border border-teal-500 text-teal-500 text-sm font-bold px-4 py-2 rounded-lg hover:bg-teal-50 transition">
                                聊天
                            </RouterLink>
                            <RouterLink 
                              v-if="doctor.available"
                              :to="`/doctor/${doctor.id}`" 
                              class="bg-teal-500 text-white text-sm font-bold px-4 py-2 rounded-lg shadow-sm hover:bg-teal-600 transition">
                                线下预约
                            </RouterLink>
                            <button 
                              v-else
                              class="bg-gray-100 text-gray-400 text-sm font-bold px-4 py-2 rounded-lg cursor-not-allowed">
                                满员
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 分页 -->
        <div class="flex justify-center mt-10">
            <nav class="flex gap-2">
                <button class="w-10 h-10 rounded-lg border border-gray-200 flex items-center justify-center text-gray-400 hover:bg-gray-50 transition">
                    <i class="fa-solid fa-chevron-left"></i>
                </button>
                <button class="w-10 h-10 rounded-lg bg-teal-500 text-white font-bold flex items-center justify-center shadow-sm">1</button>
                <button class="w-10 h-10 rounded-lg border border-gray-200 text-gray-600 font-medium flex items-center justify-center hover:bg-gray-50 transition">2</button>
                <button class="w-10 h-10 rounded-lg border border-gray-200 text-gray-600 font-medium flex items-center justify-center hover:bg-gray-50 transition">3</button>
                <button class="w-10 h-10 rounded-lg border border-gray-200 flex items-center justify-center text-gray-400 hover:bg-gray-50 transition">
                    <i class="fa-solid fa-chevron-right"></i>
                </button>
            </nav>
        </div>

    </main>
  </div>
</template>
