<template>
    <div class="min-h-screen bg-gradient-to-br from-slate-50 via-teal-50 to-cyan-50">
        <!-- 顶部导航 -->
        <nav class="bg-white/80 backdrop-blur-md shadow-sm sticky top-0 z-50">
            <div class="max-w-3xl mx-auto px-4 py-4">
                <div class="flex items-center justify-between">
                    <div class="flex items-center gap-4">
                        <button @click="handleBack" class="text-gray-500 hover:text-teal-600 transition">
                            <i class="fa-solid fa-arrow-left text-lg"></i>
                        </button>
                        <h1 class="text-lg font-bold text-slate-800 truncate">{{ scale?.name || '心理测评' }}</h1>
                    </div>
                    <div class="text-sm text-gray-500">
                        {{ currentIndex + 1 }} / {{ questions.length }}
                    </div>
                </div>
            </div>
        </nav>

        <!-- 加载状态 -->
        <div v-if="loading" class="flex items-center justify-center min-h-[60vh]">
            <div class="text-center">
                <i class="fa-solid fa-spinner fa-spin text-4xl text-teal-500"></i>
                <p class="mt-4 text-gray-500">加载题目中...</p>
            </div>
        </div>

        <!-- 答题页面 -->
        <div v-else-if="!showResult" class="max-w-3xl mx-auto px-4 py-6">
            <!-- 进度条 -->
            <div class="mb-6">
                <div class="flex items-center justify-between text-sm text-gray-500 mb-2">
                    <span>进度</span>
                    <span>{{ currentIndex + 1 }} / {{ questions.length }}</span>
                </div>
                <div class="h-2 bg-gray-200 rounded-full overflow-hidden">
                    <div 
                        class="h-full bg-gradient-to-r from-teal-400 to-teal-600 transition-all duration-300"
                        :style="{ width: `${((currentIndex + 1) / questions.length) * 100}%` }"
                    ></div>
                </div>
            </div>

            <!-- 说明文字 -->
            <p class="text-sm text-gray-500 mb-6">
                请回答所有问题。您的回答将帮助我们提供更准确的评估。
            </p>

            <!-- 题目卡片 -->
            <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-8 mb-6">
                <div class="mb-8">
                    <span class="text-2xl font-bold text-slate-800">{{ currentIndex + 1 }}. </span>
                    <span class="text-xl text-slate-800">{{ currentQuestion?.content }}</span>
                </div>

                <!-- 选项列表 -->
                <div class="space-y-4">
                    <label 
                        v-for="(option, index) in currentOptions" 
                        :key="index"
                        class="flex items-center p-4 rounded-xl border-2 cursor-pointer transition-all"
                        :class="answers[currentQuestion?.id] === option.value 
                            ? 'border-teal-500 bg-teal-50' 
                            : 'border-gray-200 hover:border-teal-300 hover:bg-gray-50'"
                    >
                        <input 
                            type="radio" 
                            :name="`question-${currentQuestion?.id}`"
                            :value="option.value"
                            v-model="answers[currentQuestion?.id]"
                            @change="handleAnswerChange"
                            class="w-5 h-5 text-teal-600 border-gray-300 focus:ring-teal-500"
                        >
                        <span class="ml-4 text-lg" :class="answers[currentQuestion?.id] === option.value ? 'text-teal-700 font-medium' : 'text-gray-700'">
                            {{ option.label }}
                        </span>
                    </label>
                </div>
            </div>

            <!-- 导航按钮 -->
            <div class="flex items-center justify-between">
                <button 
                    @click="prevQuestion"
                    :disabled="currentIndex === 0"
                    class="px-6 py-3 rounded-xl font-medium transition"
                    :class="currentIndex === 0 
                        ? 'bg-gray-100 text-gray-400 cursor-not-allowed' 
                        : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
                >
                    <i class="fa-solid fa-chevron-left mr-2"></i>上一题
                </button>

                <button 
                    v-if="currentIndex < questions.length - 1"
                    @click="nextQuestion"
                    :disabled="answers[currentQuestion?.id] === undefined"
                    class="px-6 py-3 rounded-xl font-medium transition"
                    :class="answers[currentQuestion?.id] === undefined
                        ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                        : 'bg-teal-500 text-white hover:bg-teal-600'"
                >
                    下一题<i class="fa-solid fa-chevron-right ml-2"></i>
                </button>

                <button 
                    v-else
                    @click="submitAssessment"
                    :disabled="!canSubmit || submitting"
                    class="px-8 py-3 rounded-xl font-medium transition"
                    :class="!canSubmit || submitting
                        ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                        : 'bg-gradient-to-r from-teal-500 to-cyan-500 text-white hover:from-teal-600 hover:to-cyan-600'"
                >
                    <i v-if="submitting" class="fa-solid fa-spinner fa-spin mr-2"></i>
                    {{ submitting ? '提交中...' : '提交测评' }}
                </button>
            </div>

            <!-- 题目快速跳转 -->
            <div class="mt-8 bg-white rounded-xl p-4 border border-gray-100">
                <div class="text-sm text-gray-500 mb-3">题目导航</div>
                <div class="flex flex-wrap gap-2">
                    <button 
                        v-for="(q, index) in questions" 
                        :key="q.id"
                        @click="goToQuestion(index)"
                        class="w-10 h-10 rounded-lg text-sm font-medium transition"
                        :class="{
                            'bg-teal-500 text-white': index === currentIndex,
                            'bg-teal-100 text-teal-600': index !== currentIndex && answers[q.id] !== undefined,
                            'bg-gray-100 text-gray-500 hover:bg-gray-200': index !== currentIndex && answers[q.id] === undefined
                        }"
                    >
                        {{ index + 1 }}
                    </button>
                </div>
            </div>
        </div>

        <!-- 结果页面 -->
        <div v-else class="max-w-3xl mx-auto px-4 py-8">
            <div class="bg-white rounded-2xl shadow-lg border border-gray-100 p-8 text-center">
                <!-- 结果图标 -->
                <div 
                    class="w-24 h-24 mx-auto rounded-full flex items-center justify-center mb-6"
                    :class="getResultBgColor(result?.resultLevel)"
                >
                    <i class="fa-solid fa-check text-4xl text-white"></i>
                </div>

                <h2 class="text-2xl font-bold text-slate-800 mb-2">测评完成</h2>
                <p class="text-gray-500 mb-8">{{ scale?.name }}</p>

                <!-- 得分 -->
                <div class="bg-gray-50 rounded-2xl p-6 mb-8">
                    <div class="text-5xl font-bold mb-2" :class="getResultTextColor(result?.resultLevel)">
                        {{ result?.totalScore }}
                    </div>
                    <div class="text-lg font-medium" :class="getResultTextColor(result?.resultLevel)">
                        {{ result?.resultLevel }}
                    </div>
                </div>

                <!-- 结果说明 -->
                <div class="text-left bg-blue-50 rounded-xl p-6 mb-8">
                    <div class="flex items-start gap-3">
                        <i class="fa-solid fa-circle-info text-blue-500 mt-1"></i>
                        <div>
                            <div class="font-medium text-blue-800 mb-1">测评结果说明</div>
                            <p class="text-sm text-blue-700">{{ result?.resultSummary }}</p>
                        </div>
                    </div>
                </div>

                <!-- 维度分析 -->
                <div v-if="result?.dimensions && result.dimensions.length > 0" class="text-left mb-8">
                    <h3 class="font-bold text-slate-800 mb-4">维度分析</h3>
                    <div class="space-y-3">
                        <div 
                            v-for="dim in result.dimensions" 
                            :key="dim.dimension"
                            class="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
                        >
                            <span class="text-gray-700">{{ dim.dimension }}</span>
                            <div class="flex items-center gap-3">
                                <div class="w-32 h-2 bg-gray-200 rounded-full overflow-hidden">
                                    <div 
                                        class="h-full bg-teal-500"
                                        :style="{ width: `${Math.min(dim.average / 4 * 100, 100)}%` }"
                                    ></div>
                                </div>
                                <span class="text-sm font-medium text-gray-600 w-12 text-right">
                                    {{ dim.average?.toFixed(1) }}
                                </span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 操作按钮 -->
                <div class="flex gap-4">
                    <RouterLink 
                        to="/assessment" 
                        class="flex-1 py-3 bg-gray-100 text-gray-700 rounded-xl font-medium hover:bg-gray-200 transition"
                    >
                        返回列表
                    </RouterLink>
                    <RouterLink 
                        to="/doctors" 
                        class="flex-1 py-3 bg-teal-500 text-white rounded-xl font-medium hover:bg-teal-600 transition"
                    >
                        预约咨询
                    </RouterLink>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/api'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const submitting = ref(false)
const showResult = ref(false)
const scale = ref(null)
const questions = ref([])
const answers = ref({})
const currentIndex = ref(0)
const result = ref(null)
const startTime = ref(Date.now())

// 当前题目
const currentQuestion = computed(() => questions.value[currentIndex.value])

// 当前选项
const currentOptions = computed(() => {
    if (!currentQuestion.value?.options) return []
    try {
        return typeof currentQuestion.value.options === 'string' 
            ? JSON.parse(currentQuestion.value.options) 
            : currentQuestion.value.options
    } catch (e) {
        return []
    }
})

// 是否可以提交
const canSubmit = computed(() => {
    return questions.value.every(q => answers.value[q.id] !== undefined)
})

// 加载量表和题目
const loadData = async () => {
    const scaleId = route.params.scaleId
    loading.value = true
    
    try {
        const response = await api.get(`/api/assessment/scales/${scaleId}`)
        if (response.data.success) {
            scale.value = response.data.body.scale
            questions.value = response.data.body.questions || []
        } else {
            alert('加载失败：' + response.data.message)
            router.push('/assessment')
        }
    } catch (error) {
        console.error('加载失败:', error)
        alert('加载失败，请重试')
        router.push('/assessment')
    } finally {
        loading.value = false
    }
}

// 上一题
const prevQuestion = () => {
    if (currentIndex.value > 0) {
        currentIndex.value--
    }
}

// 下一题
const nextQuestion = () => {
    if (currentIndex.value < questions.value.length - 1) {
        currentIndex.value++
    }
}

// 跳转到指定题目
const goToQuestion = (index) => {
    currentIndex.value = index
}

// 处理答案变更，自动跳转到下一题
const handleAnswerChange = () => {
    // 延迟300ms后自动跳转，给用户一个视觉反馈
    setTimeout(() => {
        if (currentIndex.value < questions.value.length - 1) {
            nextQuestion()
        }
    }, 300)
}

// 提交测评
const submitAssessment = async () => {
    if (!canSubmit.value || submitting.value) return
    
    submitting.value = true
    const duration = Math.floor((Date.now() - startTime.value) / 1000)
    
    try {
        const answerList = Object.entries(answers.value).map(([questionId, answerValue]) => ({
            questionId: parseInt(questionId),
            answerValue
        }))
        
        const response = await api.post('/api/assessment/submit', {
            scaleId: scale.value.id,
            duration,
            answers: answerList
        })
        
        if (response.data.success) {
            result.value = response.data.body
            showResult.value = true
        } else {
            alert('提交失败：' + response.data.message)
        }
    } catch (error) {
        console.error('提交失败:', error)
        alert('提交失败，请重试')
    } finally {
        submitting.value = false
    }
}

// 返回处理
const handleBack = () => {
    if (showResult.value) {
        router.push('/assessment')
    } else if (Object.keys(answers.value).length > 0) {
        if (confirm('确定要退出吗？当前答题进度将不会保存。')) {
            router.push('/assessment')
        }
    } else {
        router.push('/assessment')
    }
}

// 获取结果背景色
const getResultBgColor = (level) => {
    const colors = {
        '正常': 'bg-green-500',
        '轻度': 'bg-yellow-500',
        '中度': 'bg-orange-500',
        '重度': 'bg-red-500'
    }
    return colors[level] || 'bg-teal-500'
}

// 获取结果文字颜色
const getResultTextColor = (level) => {
    const colors = {
        '正常': 'text-green-600',
        '轻度': 'text-yellow-600',
        '中度': 'text-orange-600',
        '重度': 'text-red-600'
    }
    return colors[level] || 'text-teal-600'
}

// 页面离开确认
const handleBeforeUnload = (e) => {
    if (!showResult.value && Object.keys(answers.value).length > 0) {
        e.preventDefault()
        e.returnValue = ''
    }
}

onMounted(() => {
    loadData()
    window.addEventListener('beforeunload', handleBeforeUnload)
})

onUnmounted(() => {
    window.removeEventListener('beforeunload', handleBeforeUnload)
})
</script>
