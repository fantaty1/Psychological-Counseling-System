<template>
    <div class="min-h-screen bg-gradient-to-br from-slate-50 via-teal-50 to-cyan-50">
        <!-- 顶部导航 -->
        <nav class="bg-white/80 backdrop-blur-md shadow-sm sticky top-0 z-50">
            <div class="max-w-4xl mx-auto px-4 py-4">
                <div class="flex items-center justify-between">
                    <div class="flex items-center gap-4">
                        <RouterLink to="/" class="text-gray-500 hover:text-teal-600 transition">
                            <i class="fa-solid fa-arrow-left text-lg"></i>
                        </RouterLink>
                        <h1 class="text-xl font-bold text-slate-800">心理测评</h1>
                    </div>
                    <RouterLink to="/profile" class="text-gray-500 hover:text-teal-600 transition">
                        <i class="fa-solid fa-user text-lg"></i>
                    </RouterLink>
                </div>
            </div>
        </nav>

        <div class="max-w-4xl mx-auto px-4 py-8">
            <!-- 统计卡片 -->
            <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-8">
                <div class="flex items-center justify-between">
                    <div>
                        <h2 class="text-lg font-bold text-slate-800 mb-1">我的测评</h2>
                        <p class="text-sm text-gray-500">定期进行心理测评，了解自己的心理健康状况</p>
                    </div>
                    <div class="text-right">
                        <div class="text-3xl font-bold text-teal-600">{{ stats.completedCount || 0 }}</div>
                        <div class="text-sm text-gray-500">已完成测评</div>
                    </div>
                </div>
            </div>

            <!-- 量表列表 -->
            <div class="mb-8">
                <h3 class="text-lg font-bold text-slate-800 mb-4">可用量表</h3>
                
                <div v-if="loading" class="text-center py-12">
                    <i class="fa-solid fa-spinner fa-spin text-3xl text-teal-500"></i>
                    <p class="mt-4 text-gray-500">加载中...</p>
                </div>

                <div v-else-if="scales.length === 0" class="text-center py-12 bg-white rounded-2xl border border-gray-100">
                    <i class="fa-solid fa-clipboard-list text-5xl text-gray-300 mb-4"></i>
                    <p class="text-gray-500">暂无可用量表</p>
                </div>

                <div v-else class="space-y-4">
                    <div 
                        v-for="scale in scales" 
                        :key="scale.id"
                        class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 hover:shadow-md transition cursor-pointer"
                        @click="startAssessment(scale)"
                    >
                        <div class="flex items-start justify-between">
                            <div class="flex-1">
                                <div class="flex items-center gap-3 mb-2">
                                    <h4 class="text-lg font-bold text-slate-800">{{ scale.name }}</h4>
                                    <span class="px-2 py-0.5 bg-teal-50 text-teal-600 text-xs rounded-full">
                                        {{ scale.category }}
                                    </span>
                                </div>
                                <p class="text-sm text-gray-500 mb-4 line-clamp-2">{{ scale.description }}</p>
                                <div class="flex items-center gap-6 text-sm text-gray-400">
                                    <span><i class="fa-solid fa-list-ol mr-1"></i>{{ scale.questionCount }}题</span>
                                    <span><i class="fa-regular fa-clock mr-1"></i>约{{ scale.estimatedTime }}分钟</span>
                                </div>
                            </div>
                            <div class="flex-shrink-0 ml-4">
                                <button class="px-4 py-2 bg-teal-500 text-white rounded-lg hover:bg-teal-600 transition text-sm font-medium">
                                    开始测评
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 测评记录 -->
            <div>
                <h3 class="text-lg font-bold text-slate-800 mb-4">测评记录</h3>
                
                <div v-if="records.length === 0" class="text-center py-8 bg-white rounded-2xl border border-gray-100">
                    <i class="fa-solid fa-file-lines text-4xl text-gray-300 mb-3"></i>
                    <p class="text-gray-500">暂无测评记录</p>
                </div>

                <div v-else class="space-y-3">
                    <div 
                        v-for="record in records" 
                        :key="record.id"
                        class="bg-white rounded-xl shadow-sm border border-gray-100 p-4 flex items-center justify-between"
                    >
                        <div>
                            <div class="font-medium text-slate-800">{{ getScaleName(record.scaleId) }}</div>
                            <div class="text-sm text-gray-400 mt-1">
                                {{ formatDate(record.completedAt || record.createdAt) }}
                            </div>
                        </div>
                        <div class="flex items-center gap-4">
                            <div class="text-right">
                                <div class="text-lg font-bold" :class="getLevelColor(record.resultLevel)">
                                    {{ record.totalScore }}分
                                </div>
                                <div class="text-xs" :class="getLevelColor(record.resultLevel)">
                                    {{ record.resultLevel }}
                                </div>
                            </div>
                            <span 
                                class="px-3 py-1 rounded-full text-xs font-medium"
                                :class="record.status === 'completed' ? 'bg-green-100 text-green-600' : 'bg-yellow-100 text-yellow-600'"
                            >
                                {{ record.status === 'completed' ? '已完成' : '进行中' }}
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'

const router = useRouter()
const loading = ref(true)
const scales = ref([])
const records = ref([])
const stats = ref({ completedCount: 0, totalScales: 0 })

// 加载数据
const loadData = async () => {
    loading.value = true
    try {
        const [scalesRes, recordsRes, statsRes] = await Promise.all([
            api.get('/api/assessment/scales'),
            api.get('/api/assessment/records'),
            api.get('/api/assessment/stats')
        ])
        
        if (scalesRes.data.success) {
            scales.value = scalesRes.data.body || []
        }
        if (recordsRes.data.success) {
            records.value = recordsRes.data.body || []
        }
        if (statsRes.data.success) {
            stats.value = statsRes.data.body || { completedCount: 0, totalScales: 0 }
        }
    } catch (error) {
        console.error('加载数据失败:', error)
    } finally {
        loading.value = false
    }
}

// 开始测评
const startAssessment = (scale) => {
    router.push(`/assessment/${scale.id}`)
}

// 获取量表名称
const getScaleName = (scaleId) => {
    const scale = scales.value.find(s => s.id === scaleId)
    return scale ? scale.name : '未知量表'
}

// 格式化日期
const formatDate = (dateStr) => {
    if (!dateStr) return '-'
    const date = new Date(dateStr)
    return date.toLocaleDateString('zh-CN', { 
        year: 'numeric', 
        month: '2-digit', 
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    })
}

// 获取等级颜色
const getLevelColor = (level) => {
    const colors = {
        '正常': 'text-green-600',
        '轻度': 'text-yellow-600',
        '中度': 'text-orange-600',
        '重度': 'text-red-600'
    }
    return colors[level] || 'text-gray-600'
}

onMounted(() => {
    loadData()
})
</script>
