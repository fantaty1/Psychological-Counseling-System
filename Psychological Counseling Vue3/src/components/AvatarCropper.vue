<template>
  <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
    <div class="bg-white rounded-2xl shadow-2xl max-w-lg w-full mx-4 overflow-hidden">
      <!-- 标题 -->
      <div class="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
        <h3 class="text-lg font-bold text-slate-800">裁剪头像</h3>
        <button @click="handleCancel" class="text-gray-400 hover:text-gray-600 transition">
          <i class="fa-solid fa-times text-xl"></i>
        </button>
      </div>
      
      <!-- 裁剪区域 -->
      <div class="p-6">
        <div class="relative w-full h-80 bg-gray-100 rounded-xl overflow-hidden">
          <VueCropper
            ref="cropperRef"
            :img="imgUrl"
            :autoCrop="true"
            :autoCropWidth="200"
            :autoCropHeight="200"
            :fixedBox="true"
            :fixed="true"
            :fixedNumber="[1, 1]"
            :centerBox="true"
            :canScale="true"
            :canMove="true"
            :canMoveBox="true"
            :original="false"
            outputType="jpeg"
            :high="true"
            mode="contain"
          />
        </div>
        
        <!-- 操作提示 -->
        <p class="text-sm text-gray-500 text-center mt-4">
          <i class="fa-solid fa-info-circle mr-1"></i>
          拖动图片调整位置，滚动鼠标缩放大小
        </p>
      </div>
      
      <!-- 按钮 -->
      <div class="px-6 py-4 border-t border-gray-100 flex justify-end gap-3">
        <button @click="handleCancel" 
          class="px-6 py-2 border border-gray-200 rounded-lg text-gray-600 hover:bg-gray-50 transition">
          取消
        </button>
        <button @click="handleConfirm" :disabled="uploading"
          class="px-6 py-2 bg-teal-500 text-white rounded-lg hover:bg-teal-600 transition disabled:opacity-50 disabled:cursor-not-allowed">
          <i v-if="uploading" class="fa-solid fa-spinner fa-spin mr-2"></i>
          {{ uploading ? '上传中...' : '确认上传' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import 'vue-cropper/dist/index.css'
import { VueCropper } from 'vue-cropper'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  imgUrl: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:visible', 'confirm', 'cancel'])

const cropperRef = ref(null)
const uploading = ref(false)

// 取消
const handleCancel = () => {
  emit('update:visible', false)
  emit('cancel')
}

// 确认裁剪
const handleConfirm = () => {
  uploading.value = true
  cropperRef.value.getCropBlob((blob) => {
    // 将blob转为File对象
    const file = new File([blob], 'avatar.jpg', { type: 'image/jpeg' })
    emit('confirm', file)
    uploading.value = false
  })
}

// 暴露方法供父组件调用
defineExpose({
  setUploading: (value) => {
    uploading.value = value
  }
})
</script>
