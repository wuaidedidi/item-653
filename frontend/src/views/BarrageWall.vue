<template>
  <div class="barrage-wall">
    <div class="page-header">
      <h1 class="page-title">弹幕墙</h1>
    </div>

    <!-- 弹幕展示区 -->
    <div class="barrage-display">
      <div
        class="barrage-screen"
        ref="screenRef"
      ></div>

      <!-- 发送弹幕 -->
      <div class="barrage-input">
        <el-input
          v-model="barrageContent"
          placeholder="发一条弹幕评论..."
          maxlength="100"
          show-word-limit
          size="large"
          @keyup.enter="sendBarrage"
        >
          <template #prepend>
            <el-color-picker v-model="barrageColor" size="default" />
          </template>
          <template #append>
            <el-button type="primary" @click="sendBarrage" :loading="sending">
              发送
            </el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 弹幕列表 -->
    <div class="barrage-list">
      <h2>最新弹幕</h2>
      <div class="list-container">
        <div
          v-for="barrage in barrages"
          :key="barrage.id"
          class="list-item"
        >
          <span class="item-user">{{ barrage.nickname || barrage.username }}</span>
          <span class="item-content" :style="{ color: barrage.color }">
            {{ barrage.content }}
          </span>
          <span v-if="barrage.bookTitle" class="item-book">
            《{{ barrage.bookTitle }}》
          </span>
          <span class="item-time">{{ formatTime(barrage.createdAt) }}</span>
        </div>

        <el-empty v-if="barrages.length === 0" description="暂无弹幕" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getLatestBarrages, sendBarrage as sendBarrageApi } from '@/api/barrage'
import { ElMessage } from 'element-plus'

// 数据状态
const barrages = ref([])
const barrageContent = ref('')
const barrageColor = ref('#FF6B6B')
const sending = ref(false)

// 弹幕动画控制
const screenRef = ref(null)
const MAX_TRACKS = 10
const TRACK_HEIGHT = 40
let loopTimer = null
let currentIndex = 0

onMounted(async () => {
  await loadBarrages()
  // 等待页面 fadeIn 动画结束 + 布局稳定后再启动弹幕
  setTimeout(() => startLoop(), 600)
})

onUnmounted(() => {
  stopLoop()
})

async function loadBarrages() {
  try {
    const res = await getLatestBarrages(100)
    barrages.value = res.data
  } catch (error) {
    // 错误已在拦截器中处理
  }
}

function startLoop() {
  stopLoop()
  loopTimer = setInterval(() => {
    if (barrages.value.length === 0 || !screenRef.value) return
    const raw = barrages.value[currentIndex]
    currentIndex = (currentIndex + 1) % barrages.value.length
    shootBarrage(raw)
  }, 800)
}

function stopLoop() {
  if (loopTimer) {
    clearInterval(loopTimer)
    loopTimer = null
  }
}

/**
 * 直接操作 DOM 发射一条弹幕
 * 用 requestAnimationFrame 逐帧移动，不依赖 CSS transition/animation
 */
function shootBarrage(raw) {
  const screen = screenRef.value
  if (!screen) return

  const screenWidth = screen.offsetWidth

  // 创建弹幕 DOM
  const el = document.createElement('div')
  el.className = 'barrage-item'
  const color = raw.color || '#FF6B6B'
  const sender = raw.nickname || raw.username || '匿名'
  el.innerHTML = `<span class="barrage-text" style="color:${color}">${escapeHtml(raw.content)}</span><span class="barrage-sender">- ${escapeHtml(sender)}</span>`

  // 随机轨道
  const trackId = Math.floor(Math.random() * MAX_TRACKS)
  const top = 20 + trackId * TRACK_HEIGHT

  // 速度：每秒移动的像素数 (80~150 px/s)
  const speed = 80 + Math.random() * 70

  // 先隐藏放入 DOM 测量宽度
  el.style.position = 'absolute'
  el.style.top = `${top}px`
  el.style.visibility = 'hidden'
  screen.appendChild(el)
  const itemWidth = el.offsetWidth

  // 起点：容器右边缘  终点：弹幕完全飘出左边缘
  let currentX = screenWidth
  const endX = -itemWidth

  // 先设好 transform 再显示，避免闪烁
  el.style.transform = `translateX(${currentX}px)`
  el.style.visibility = ''

  let lastTime = null
  function tick(timestamp) {
    if (!el.parentNode) return
    if (lastTime === null) lastTime = timestamp
    const delta = (timestamp - lastTime) / 1000 // 秒
    lastTime = timestamp

    currentX -= speed * delta
    el.style.transform = `translateX(${currentX}px)`

    if (currentX <= endX) {
      el.remove()
      return
    }
    requestAnimationFrame(tick)
  }

  requestAnimationFrame(tick)
}

function escapeHtml(str) {
  const div = document.createElement('div')
  div.textContent = str
  return div.innerHTML
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return Math.floor(diff / 86400000) + '天前'
}

async function sendBarrage() {
  if (!barrageContent.value.trim()) {
    ElMessage.warning('请输入弹幕内容')
    return
  }

  sending.value = true
  try {
    const newBarrage = {
      content: barrageContent.value,
      color: barrageColor.value
    }
    await sendBarrageApi(newBarrage)
    ElMessage.success('弹幕发送成功')
    barrageContent.value = ''

    // 立即发射这条新弹幕
    shootBarrage({
      ...newBarrage,
      nickname: '我',
      username: '我'
    })
    // 刷新列表
    await loadBarrages()
  } finally {
    sending.value = false
  }
}
</script>

<style scoped>
.barrage-wall {
  /* 不使用 fadeIn 动画，其 transform 会创建新的包含块，干扰弹幕 offsetWidth 测量 */
}

.barrage-display {
  background: white;
  border-radius: var(--radius-lg);
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: var(--shadow-sm);
}

.barrage-screen {
  position: relative;
  width: 100%;
  height: 450px;
  background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
  border-radius: var(--radius-md);
  margin-bottom: 20px;
  overflow: hidden;
}
</style>

<!-- 弹幕元素由 JS 直接创建，不受 scoped 限制，样式放在全局 -->
<style>
.barrage-screen .barrage-item {
  position: absolute;
  left: 0;
  top: 0;
  white-space: nowrap;
  user-select: none;
  cursor: default;
  font-family: 'Noto Sans SC', sans-serif;
  font-weight: 500;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.8);
  will-change: transform;
  display: flex;
  align-items: center;
  gap: 8px;
  pointer-events: none;
}

.barrage-screen .barrage-text {
  font-size: 20px;
}

.barrage-screen .barrage-sender {
  font-size: 12px;
  color: rgba(255,255,255,0.6);
}

.barrage-screen:hover .barrage-item {
  pointer-events: auto;
}
</style>

<style scoped>
.barrage-input :deep(.el-input-group__prepend) {
  padding: 0 12px;
}

/* 列表样式 */
.barrage-list {
  background: white;
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
}

.barrage-list h2 {
  font-size: 18px;
  margin-bottom: 16px;
  color: var(--text-primary);
}

.list-container {
  max-height: 400px;
  overflow-y: auto;
}

.list-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid var(--border-lighter);
  transition: background 0.2s ease;
}

.list-item:hover {
  background: var(--bg-light);
  padding-left: 8px;
  border-radius: var(--radius-sm);
}

.list-item:last-child {
  border-bottom: none;
}

.item-user {
  font-weight: 500;
  color: var(--primary-color);
  min-width: 80px;
}

.item-content {
  flex: 1;
  font-weight: 500;
}

.item-book {
  color: var(--text-secondary);
  font-size: 12px;
}

.item-time {
  color: var(--text-placeholder);
  font-size: 12px;
  min-width: 60px;
  text-align: right;
}
</style>
